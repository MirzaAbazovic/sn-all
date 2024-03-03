/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.04.2010 09:48:38
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCard;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.view.HWBaugruppeView;


/**
 * Panel fuer die Planung / Ausfuehrung von Baugruppen-Schwenks der Typen "einfacher Kartenwechsel",
 * "Karten zusammenlegen" und "Portkonzentration".
 */
public class HWBaugruppenChangeCardPanel extends AbstractHWBaugruppenChangeDefinitionPanel implements AKDataLoaderComponent {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/hvt/hardware/resources/HWBaugruppenChangeCardPanel.xml";
    private static final String MAP_PORTS = "map.ports";
    private static final String DESTINATION_BG = "destination.bg";
    private static final String ADD_DEST_BG = "add.dest.bg";
    private static final String REMOVE_DEST_BG = "remove.dest.bg";

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeCardPanel.class);

    // GUI
    private AKJTable tbBaugruppenDest;
    private AKReflectionTableModel<HWBaugruppeView> tbMdlBaugruppenDest;
    private AKJButton btnAddDestBg;
    private AKJButton btnRemoveDestBg;
    private AKJButton btnMapPorts;
    private final HWBaugruppenChangeDetailPanel parentPanel;

    public HWBaugruppenChangeCardPanel(HWBaugruppenChange hwBaugruppenChange, HWBaugruppenChangeDetailPanel parent) {
        super(RESOURCE, hwBaugruppenChange);
        this.parentPanel = parent;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        this.setLayout(new GridBagLayout());
        addPanelWithSourceData();
        addPanelWithDestinationData();
        validateByStatus();
    }

    @Override
    protected void addPanelWithDestinationData() {
        tbMdlBaugruppenDest = new AKReflectionTableModel<HWBaugruppeView>(
                HWBaugruppeView.TABLE_COLUMN_NAMES, HWBaugruppeView.TABLE_PROPERTY_NAMES, HWBaugruppeView.TABLE_CLASS_TYPES);
        tbBaugruppenDest = new AKJTable(tbMdlBaugruppenDest, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbBaugruppenDest.fitTable(HWBaugruppeView.TABLE_FIT);
        AKJScrollPane spBaugruppenNew = new AKJScrollPane(tbBaugruppenDest, new Dimension(400, 100));

        btnAddDestBg = getSwingFactory().createButton(ADD_DEST_BG, getActionListener(), null);
        btnRemoveDestBg = getSwingFactory().createButton(REMOVE_DEST_BG, getActionListener(), null);
        btnMapPorts = getSwingFactory().createButton(MAP_PORTS, getActionListener());

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddDestBg, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnRemoveDestBg, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel btnPnlExec = new AKJPanel(new GridBagLayout());
        btnPnlExec.add(btnMapPorts, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnlExec.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblBGsDest = getSwingFactory().createLabel(DESTINATION_BG, AKJLabel.LEFT, Font.BOLD);
        AKJPanel destPanel = new AKJPanel(new BorderLayout());
        destPanel.add(lblBGsDest, BorderLayout.NORTH);
        destPanel.add(spBaugruppenNew, BorderLayout.CENTER);
        destPanel.add(btnPnl, BorderLayout.EAST);
        destPanel.add(btnPnlExec, BorderLayout.SOUTH);

        this.add(destPanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    protected void clearAll() {
        tbMdlBaugruppenSrc.removeAll();
        tbMdlBaugruppenDest.removeAll();
    }

    @Override
    public final void loadData() {
        try {
            clearAll();

            Set<HWBaugruppenChangeCard> hwBgChangeCards = hwBaugruppenChange.getHwBgChangeCard();
            if ((hwBgChangeCards != null) && CollectionTools.isNotEmpty(hwBgChangeCards)) {
                List<HWBaugruppeView> hwBgViews = hwService.findHWBaugruppenViews(hwBaugruppenChange.getHvtStandort().getId());
                HWBaugruppenChangeCard changeCard = hwBgChangeCards.iterator().next();

                identifyOldBaugruppen(hwBgViews, changeCard);
                identifyZielBaugruppen(hwBgViews, changeCard);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    // Ziel-Baugruppen ermitteln u. anzeigen
    private void identifyZielBaugruppen(List<HWBaugruppeView> hwBgViews, HWBaugruppenChangeCard changeCard) {
        for (HWBaugruppe zielBaugruppe : hwBaugruppenChangeService.sortBaugruppenByGerateBezAndModNr(changeCard.getHwBaugruppenNew())) {
            for (HWBaugruppeView hwBgView : hwBgViews) {
                if (NumberTools.equal(zielBaugruppe.getId(), hwBgView.getBaugruppenId())) {
                    tbMdlBaugruppenDest.addObject(hwBgView);
                    break;
                }
            }
        }
    }

    // alte/abzuloesende Baugruppen ermitteln u. anzeigen
    private void identifyOldBaugruppen(List<HWBaugruppeView> hwBgViews, HWBaugruppenChangeCard changeCard) {
        if ((changeCard.getHwBaugruppenSource() != null)) {
            for (HWBaugruppe bg : hwBaugruppenChangeService.sortBaugruppenByGerateBezAndModNr(changeCard.getHwBaugruppenSource())) {
                for (HWBaugruppeView hwBgView : hwBgViews) {
                    if (NumberTools.equal(bg.getId(), hwBgView.getBaugruppenId())) {
                        tbMdlBaugruppenSrc.addObject(hwBgView);
                    }
                }
            }
        }
    }


    @Override
    protected void execute(String command) {
        super.execute(command);
        if (ADD_DEST_BG.equals(command)) {
            addDestBg(hwBaugruppenChange.getChangeTypeValue().isMehrfachauswahlZielbaugruppe());
        }
        else if (REMOVE_DEST_BG.equals(command)) {
            removeDestBg();
        }
        else if (MAP_PORTS.equals(command)) {
            mapPorts();
        }
    }

    /* Oeffnet einen Dialog, um die Ziel-Baugruppe auszuwaehlen. */
    private void addDestBg(boolean mehrfachauswahlZielbaugruppe) {
        try {
            if (!mehrfachauswahlZielbaugruppe && tbBaugruppenDest.getRowCount() > 0) {
                throw new HurricanGUIException("Es ist bereits eine Ziel-Baugruppe ausgewählt!");
            }

            SelectHWBaugruppenDialog selectHwBgDialog = new SelectHWBaugruppenDialog(hwBaugruppenChange.getHvtStandort().getId(), mehrfachauswahlZielbaugruppe);
            Object selection = DialogHelper.showDialog(this, selectHwBgDialog, true, true);
            if (selection instanceof List<?>) {
                List<?> result = (List<?>) selection;
                if (!mehrfachauswahlZielbaugruppe && result.size() > 1) {
                    throw new HurricanGUIException("Es muss genau 1 Ziel-Baugruppe ausgewaehlt werden!");
                }

                for (Object toAdd : (List<?>) selection) {

                    HWBaugruppeView bgViewDest = (HWBaugruppeView) toAdd;

                    HWBaugruppe hwBaugruppe = hwService.findBaugruppe(bgViewDest.getBaugruppenId());
                    HWBaugruppenChangeCard hwBgChangeCard;
                    Set<HWBaugruppenChangeCard> cardChangeSet = hwBaugruppenChange.getHwBgChangeCard();
                    if (cardChangeSet.isEmpty()) {
                        hwBgChangeCard = new HWBaugruppenChangeCard();
                        hwBaugruppenChange.addHWBaugruppenChangeCard(hwBgChangeCard);
                    }
                    else {
                        hwBgChangeCard = cardChangeSet.iterator().next();
                    }

                    hwBaugruppenChangeService.checkAndAddHwBaugruppe4New(hwBgChangeCard, hwBaugruppe);
                    tbMdlBaugruppenDest.addObject(bgViewDest);
                }
                hwBaugruppenChangeService.saveHWBaugruppenChange(hwBaugruppenChange);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Entfernt die selektierte Ziel-Baugruppe */
    private void removeDestBg() {
        try {
            List<HWBaugruppeView> selectionToRemove = tbBaugruppenDest.getTableSelectionAsList(HWBaugruppeView.class);
            if (selectionToRemove != null) {
                for (HWBaugruppeView toRemove : selectionToRemove) {
                    Set<HWBaugruppenChangeCard> changeCards = hwBaugruppenChange.getHwBgChangeCard();
                    if ((changeCards != null) && !changeCards.isEmpty()) {
                        HWBaugruppenChangeCard changeCard = changeCards.iterator().next();

                        HWBaugruppe hwBaugruppe = hwService.findBaugruppe(toRemove.getBaugruppenId());
                        changeCard.removeHwBaugruppeNew(hwBaugruppe);

                        hwBaugruppenChangeService.saveHWBaugruppenChange(hwBaugruppenChange);
                    }
                    tbMdlBaugruppenDest.removeObject(toRemove);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Ermittelt die betroffenen Ports (Ursprungs- u. Ziel-Ports) und mappt diese. */
    private void mapPorts() {
        try {
            switch (this.hwBaugruppenChange.getChangeTypeValue()) {
                case CHANGE_CARD:
                case MERGE_CARDS:
                    hwBaugruppenChangeService.createPort2Port4ChangeCard(hwBaugruppenChange);
                    break;
                case PORT_CONCENTRATION:
                    hwBaugruppenChangeService.createPort2Port4PortConcentration(hwBaugruppenChange);
                    break;
                default:
                    MessageHelper.showInfoDialog(getMainFrame(),
                            String.format("Für den Baugruppen-Schwenk vom Typ '%s' wurde kein Port-Mapping erstellt. "
                                            + "Entschuldigung, das hätte nicht passieren dürfen. "
                                            + "Bitte informiere die Entwickler.",
                                    hwBaugruppenChange.getChangeTypeValue().name()));
                    break;
            }
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
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    @Override
    protected void validateByStatus() {
        if ((hwBaugruppenChange != null) &&
                NumberTools.isGreater(hwBaugruppenChange.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING.refId())) {
            GuiTools.disableComponents(new Component[] { btnAddSrcBg, btnRemoveSrcBg, btnAddDestBg, btnRemoveDestBg, btnMapPorts });
        }
    }

}


