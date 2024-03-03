/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 08:15:05
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;


/**
 * Dialog zur Darstellung aller vorhandenen Stift-Bestellungen zu einem UEVT.
 *
 *
 */
public class HVTBestellungen4UevtDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(HVTBestellungen4UevtDialog.class);

    private Long uevtId = null;

    private AKReflectionTableModel<HVTBestellung> tbMdlBestellungen = null;

    /**
     * Konstruktor mit Angabe der ID des UEVTs, dessen Stift-Bestellungen angezeigt werden sollen.
     *
     * @param uevtId
     */
    public HVTBestellungen4UevtDialog(Long uevtId) {
        super(null, true, true);
        this.uevtId = uevtId;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Bestellungen zu UEVT ");
        configureButton(CMD_CANCEL, null, null, false, false);
        configureButton(CMD_SAVE, "OK", "Schliesst den Dialog", true, true);

        tbMdlBestellungen = new AKReflectionTableModel<HVTBestellung>(
                new String[] { "Angebot Datum", "Physiktyp", "Anzahl CuDA", "Bestelldatum", "Bestell-Nr AKom",
                        "Bestell-Nr DTAG", "Bereitgestellt", "Verwendung" },
                new String[] { "angebotDatum", "physiktyp", "anzahlCuDA", "bestelldatum", "bestellNrAKom",
                        "bestellNrDTAG", "bereitgestellt", "eqVerwendung" },
                new Class[] { Date.class, String.class, String.class, Date.class, String.class,
                        String.class, Date.class, String.class, String.class }
        );
        AKJTable tbBestellungen = new AKJTable(tbMdlBestellungen, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbBestellungen.attachSorter();
        tbBestellungen.fitTable(new int[] { 70, 40, 50, 70, 100, 100, 70, 70 });
        AKJScrollPane spTable = new AKJScrollPane(tbBestellungen);
        spTable.setPreferredSize(new Dimension(600, 300));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // Save-Button nicht auf Berechtigungen pruefen
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public void loadData() {
        try {
            setWaitCursor();
            HVTService hvts = getCCService(HVTService.class);
            UEVT uevt = hvts.findUEVT(uevtId);
            if (uevt == null) {
                throw new IllegalArgumentException("Kein UEVT mit der ID " + uevtId + " gefunden!");
            }

            setTitle(getTitle() + uevt.getUevt());

            HVTToolService hts = getCCService(HVTToolService.class);
            List<HVTBestellung> bestellungen = hts.findHVTBestellungen(uevtId);
            tbMdlBestellungen.setData(bestellungen);
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


