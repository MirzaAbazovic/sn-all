/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2007 10:31:52
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDluView;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog, um den noch nicht definierten EWSD-Baugruppen einen Typ (HWBaugruppenTyp) zuzuweisen. <br> Die Auswahl und
 * somit die Zuordnung des Typs erfolgt durch einen Doppelklick auf die Zeile der zu definierenden Baugruppe. Es wird
 * dann ein Dialog aufgerufen, ueber den der Baugruppen-Typ ausgewaehlt werden kann.
 *
 *
 */
public class RangierungDefineEWSDBGDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(RangierungDefineEWSDBGDialog.class);

    private Long hvtIdStandort = null;

    // GUI-Objekte
    private AKJTable tbBGs = null;
    private AKReflectionTableModel<HWDluView> tbMdlBGs = null;

    /**
     * Konstruktor fuer den Dialog.
     *
     * @param hvtIdStandort
     */
    public RangierungDefineEWSDBGDialog(Long hvtIdStandort) {
        super("de/augustakom/hurrican/gui/hvt/resources/RangierungDefineEWSDBGDialog.xml");
        this.hvtIdStandort = hvtIdStandort;
        if (this.hvtIdStandort == null) {
            throw new IllegalArgumentException("Der HVT-Standort ist nicht definiert!");
        }
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Baugruppen definieren");
        configureButton(CMD_CANCEL, null, null, false, false);
        configureButton(CMD_SAVE, "OK", null, true, true);

        tbMdlBGs = new AKReflectionTableModel<HWDluView>(
                new String[] { "BG ID", "BG-Typ", "DLU", "MOD", "Verwendung" },
                new String[] { "baugruppenId", "bgTyp", "dluNumber", "modNumber", "hwSchnittstelle" },
                new Class[] { Long.class, String.class, String.class, String.class, String.class });
        tbBGs = new AKJTable(tbMdlBGs, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbBGs.fitTable(new int[] { 70, 110, 70, 70, 100 });
        tbBGs.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spBGs = new AKJScrollPane(tbBGs, new Dimension(450, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spBGs, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            showProgressBar("Baugruppen laden");

            HWService hws = getCCService(HWService.class);
            List<HWDluView> views = hws.findEWSDBaugruppen(hvtIdStandort, true);
            tbMdlBGs.setData(views);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
            stopProgressBar();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        try {
            setWaitCursor();
            if (selection instanceof HWDluView) {
                HWDluView dluView = (HWDluView) selection;

                HWService hws = getCCService(HWService.class);
                List<HWBaugruppenTyp> bgTypen = hws.findBaugruppenTypen(HWBaugruppenTyp.HW_PORT_EWSD_PREFIX, true);

                // Baugruppen-Typ abfragen
                Object value = MessageHelper.showInputDialog(getMainFrame(), bgTypen,
                        new AKCustomListCellRenderer<>(HWBaugruppenTyp.class, HWBaugruppenTyp::getDisplayName),
                        getSwingFactory().getText("sel.title"), getSwingFactory().getText("sel.msg"),
                        getSwingFactory().getText("sel.label"));

                if (value instanceof HWBaugruppenTyp) {
                    HWBaugruppenTyp bgTyp = (HWBaugruppenTyp) value;

                    // Baugruppe ermitteln und Typ setzen
                    HWBaugruppe baugruppe = hws.findBaugruppe(dluView.getBaugruppenId());
                    if (baugruppe != null) {
                        baugruppe.setHwBaugruppenTyp(bgTyp);
                        hws.saveHWBaugruppe(baugruppe);
                    }
                    else {
                        throw new HurricanGUIException("Die Baugruppe konnte nicht ermittelt werden!");
                    }

                    // alle Equipments der Baugruppe laden
                    RangierungsService rs = getCCService(RangierungsService.class);
                    List<Equipment> eqs = rs.findEquipments4HWBaugruppe(dluView.getBaugruppenId());
                    if (CollectionTools.isEmpty(eqs)) {
                        throw new HurricanGUIException(
                                "Es konnten keine Equipments zu der Baugruppe ermittelt werden!");
                    }

                    // HW_SCHNITTSTELLE setzen
                    for (Equipment eq : eqs) {
                        int port = eq.getHwEQNPartAsInt(Equipment.HWEQNPART_EWSD_PORT);
                        if (port < 0) {
                            throw new HurricanGUIException("Der EWSD-Port konnte nicht ermittelt werden!");
                        }

                        eq.setHwSchnittstelle(bgTyp.getHwSchnittstelleName());
                        eq.setUserW(HurricanSystemRegistry.instance().getCurrentLoginName());
                        eq.setDateW(new Date());

                        // es duerfen nur so viele Ports belegt werden, wie fuer
                        // den Baugruppen-Typ definiert sind.
                        if (port >= bgTyp.getPortCount().intValue()) {
                            eq.setStatus(null);
                        }

                        rs.saveEquipment(eq);
                    }

                    // gewaehlten BaugruppenTyp in der Tabelle darstellen (nur Darstellung!)
                    dluView.setBgTyp(bgTyp.getName());
                    dluView.setHwSchnittstelle(bgTyp.getHwSchnittstelleName());
                    tbMdlBGs.fireTableRowsUpdated(tbBGs.getSelectedRow(), tbBGs.getSelectedRow());

                    MessageHelper.showInfoDialog(this, "Baugruppe wurde definiert.", null, true);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        prepare4Close();
        setValue(null);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


