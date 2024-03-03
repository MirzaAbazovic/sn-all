/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2010 07:37:08
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKReferenceFieldEvent;
import de.augustakom.common.gui.swing.AKReferenceFieldObserver;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgType;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.view.HWBaugruppeView;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Planung / Ausfuehrung von Baugruppen-Schwenks des Typs 'Baugruppentyp aendern'.
 */
public class HWBaugruppenChangeBgTypPanel extends AbstractHWBaugruppenChangeDefinitionPanel
        implements AKDataLoaderComponent, AKReferenceFieldObserver {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeBgTypPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/hvt/hardware/resources/HWBaugruppenChangeBgTypPanel.xml";
    private static final String HW_BAUGRUPPE_TYP_NEW = "hw.baugruppe.typ.new";
    private static final String LOAD_PORTS = "load.ports";

    // GUI
    private AKReferenceField rfHwBgTypNew;
    private final HWBaugruppenChangeDetailPanel parentPanel;

    /**
     * Konstruktor mit Angabe der Planung.
     *
     * @param hwBaugruppenChange
     * @param parent
     */
    public HWBaugruppenChangeBgTypPanel(HWBaugruppenChange hwBaugruppenChange, HWBaugruppenChangeDetailPanel parent) {
        super(RESOURCE, hwBaugruppenChange);
        this.hwBaugruppenChange = hwBaugruppenChange;
        this.parentPanel = parent;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        this.setLayout(new GridBagLayout());
        addPanelWithSourceData();
        addPanelWithDestinationData();
    }

    @Override
    protected void addPanelWithDestinationData() {
        AKJLabel lblHwBgTypNew = getSwingFactory().createLabel(HW_BAUGRUPPE_TYP_NEW, AKJLabel.LEFT, Font.BOLD);
        rfHwBgTypNew = getSwingFactory().createReferenceField(HW_BAUGRUPPE_TYP_NEW);
        AKJButton btnLoadPorts = getSwingFactory().createButton(LOAD_PORTS, getActionListener());

        AKJPanel destPanel = new AKJPanel(new GridBagLayout());
        destPanel.add(lblHwBgTypNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        destPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        destPanel.add(rfHwBgTypNew, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        destPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        destPanel.add(btnLoadPorts, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        this.add(destPanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    protected void clearAll() {
        tbMdlBaugruppenSrc.removeAll();
        rfHwBgTypNew.clearReference();
    }

    @Override
    public final void loadData() {
        try {
            clearAll();

            ISimpleFindService simpleFindService = getCCService(QueryCCService.class);
            rfHwBgTypNew.setFindService(simpleFindService);

            // Details laden
            Set<HWBaugruppenChangeBgType> changeBgTypeSet = hwBaugruppenChange.getHwBgChangeBgType();
            if ((changeBgTypeSet != null) && !changeBgTypeSet.isEmpty()) {
                List<HWBaugruppeView> hwBgViews = hwService.findHWBaugruppenViews(hwBaugruppenChange.getHvtStandort().getId());
                Iterator<HWBaugruppenChangeBgType> iterator = changeBgTypeSet.iterator();
                while (iterator.hasNext()) {
                    HWBaugruppenChangeBgType changeBgTyp = iterator.next();

                    if (changeBgTyp != null) {
                        // alte/abzuloesende Baugruppen ermitteln u. anzeigen
                        for (HWBaugruppeView hwBgView : hwBgViews) {
                            if (NumberTools.equal(changeBgTyp.getHwBaugruppe().getId(), hwBgView.getBaugruppenId())) {
                                tbMdlBaugruppenSrc.addObject(hwBgView);
                            }
                        }

                        // Ziel-Baugruppentyp ermitteln u. anzeigen
                        if (changeBgTyp.getHwBaugruppenTypNew() != null) {
                            rfHwBgTypNew.setReferenceObject(changeBgTyp.getHwBaugruppenTypNew());
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            rfHwBgTypNew.addObserver(this);
        }
        validateByStatus();
    }

    @Override
    protected void execute(String command) {
        super.execute(command);
        if (LOAD_PORTS.equals(command)) {
            loadPorts();
        }
    }

    @Override
    protected void addSrcBg(boolean acceptMultipleSelection) {
        super.addSrcBg(hwBaugruppenChange.getChangeTypeValue().isMehrfachauswahlQuellbaugruppe());
        validateByStatus();
    }

    @Override
    protected void removeSrcBg() {
        super.removeSrcBg();
        validateByStatus();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void update(AKReferenceFieldEvent akReferenceFieldEvent) throws Exception {
        if (akReferenceFieldEvent == rfHwBgTypNew) {
            try {
                HWBaugruppenChangeBgType hwBgChangeBgTyp = null;
                Set<HWBaugruppenChangeBgType> changeBgTypSet = hwBaugruppenChange.getHwBgChangeBgType();
                if (changeBgTypSet.isEmpty()) {
                    throw new HurricanGUIException("Bitte geben Sie zuerst die zu aendernde Baugruppe an.");
                }
                else {
                    Iterator<HWBaugruppenChangeBgType> iterator = changeBgTypSet.iterator();
                    while (iterator.hasNext()) {
                        hwBgChangeBgTyp = iterator.next();
                        hwBgChangeBgTyp.setHwBaugruppenTypNew(rfHwBgTypNew.getReferenceObjectAs(HWBaugruppenTyp.class));
                        hwBaugruppenChangeService.saveHWBaugruppenChange(hwBaugruppenChange);
                    }
                }
            }
            catch (HurricanGUIException e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /* Ermittelt die Ports, die von der Typ-Aenderung betroffen sind. */
    private void loadPorts() {
        try {
            hwBaugruppenChangeService.createPort2Port4ChangeBgType(hwBaugruppenChange);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            // Parent Panel dazu veranlassen, die Ports/Auftraege darzustellen
            parentPanel.loadData();
        }
    }

    @Override
    protected void validateByStatus() {
        if ((hwBaugruppenChange != null) &&
                NumberTools.isGreater(hwBaugruppenChange.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING.refId())) {
            GuiTools.disableComponents(new Component[] { btnAddSrcBg, btnRemoveSrcBg, rfHwBgTypNew });
        }
        else {
            boolean enableAddBtn = (tbBaugruppenSrc.getRowCount()) > 0 ? false : true;
            GuiTools.enableComponents(new Component[] { btnAddSrcBg }, enableAddBtn, true);
        }
    }

}


