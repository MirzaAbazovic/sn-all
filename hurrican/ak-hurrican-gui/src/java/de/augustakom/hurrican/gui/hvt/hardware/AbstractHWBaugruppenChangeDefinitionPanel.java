/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.04.2010 10:09:48
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import static de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange.ChangeType.*;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgType;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCard;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.view.HWBaugruppeView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * Basis-Klasse fuer alle Panels, die Details fuer HWBaugruppenChanges darstellen.
 */
public abstract class AbstractHWBaugruppenChangeDefinitionPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(AbstractHWBaugruppenChangeDefinitionPanel.class);

    private static final String REMOVE_SRC_BG = "remove.source.bg";
    private static final String ADD_SRC_BG = "add.source.bg";
    private static final String SRC_BGS = "source.bgs";

    // GUI
    protected AKJTable tbBaugruppenSrc;
    protected AKReflectionTableModel<HWBaugruppeView> tbMdlBaugruppenSrc;
    protected AKJButton btnAddSrcBg;
    protected AKJButton btnRemoveSrcBg;

    // Modelle
    protected HWBaugruppenChange hwBaugruppenChange;

    // Services
    protected HWService hwService;
    protected HWBaugruppenChangeService hwBaugruppenChangeService;

    public AbstractHWBaugruppenChangeDefinitionPanel(String resource, HWBaugruppenChange hwBaugruppenChange) {
        super(resource);
        this.hwBaugruppenChange = hwBaugruppenChange;
        init();
    }

    private void init() {
        try {
            hwService = getCCService(HWService.class);
            hwBaugruppenChangeService = getCCService(HWBaugruppenChangeService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Generiert ein Panel mit Table + Buttons, um die Ursprungs-Baugruppe(n) auszuwaehlen bzw. darzustellen.
     */
    protected void addPanelWithSourceData() {
        tbMdlBaugruppenSrc = new AKReflectionTableModel<HWBaugruppeView>(
                HWBaugruppeView.TABLE_COLUMN_NAMES, HWBaugruppeView.TABLE_PROPERTY_NAMES, HWBaugruppeView.TABLE_CLASS_TYPES);
        tbBaugruppenSrc = new AKJTable(tbMdlBaugruppenSrc, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbBaugruppenSrc.fitTable(HWBaugruppeView.TABLE_FIT);
        AKJScrollPane spBaugruppenOld = new AKJScrollPane(tbBaugruppenSrc, new Dimension(400, 100));

        btnAddSrcBg = getSwingFactory().createButton(ADD_SRC_BG, getActionListener(), null);
        btnRemoveSrcBg = getSwingFactory().createButton(REMOVE_SRC_BG, getActionListener(), null);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddSrcBg, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnRemoveSrcBg, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJLabel lblBGsSrc = getSwingFactory().createLabel(SRC_BGS, AKJLabel.LEFT, Font.BOLD);
        AKJPanel sourcePanel = new AKJPanel(new BorderLayout());
        sourcePanel.add(lblBGsSrc, BorderLayout.NORTH);
        sourcePanel.add(spBaugruppenOld, BorderLayout.CENTER);
        sourcePanel.add(btnPnl, BorderLayout.EAST);

        this.add(sourcePanel, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    protected abstract void clearAll();

    protected abstract void addPanelWithDestinationData();

    protected abstract void validateByStatus();

    @Override
    protected void execute(String command) {
        if (ADD_SRC_BG.equals(command)) {
            addSrcBg(hwBaugruppenChange.getChangeTypeValue().isMehrfachauswahlQuellbaugruppe());
        }
        else if (REMOVE_SRC_BG.equals(command)) {
            removeSrcBg();
        }
    }

    /* Oeffnet einen Dialog, um eine weitere Baugruppe auszuwaehlen, die ersetzt werden soll. */
    protected void addSrcBg(boolean mehrfachauswahlQuellbaugruppe) {
        try {
            if (!mehrfachauswahlQuellbaugruppe && tbBaugruppenSrc.getRowCount() > 0) {
                throw new HurricanGUIException("Es ist bereits eine Quell-Baugruppe ausgewählt!");
            }
            SelectHWBaugruppenDialog selectHwBgDialog = new SelectHWBaugruppenDialog(hwBaugruppenChange.getHvtStandort().getId(), mehrfachauswahlQuellbaugruppe);
            Object selection = DialogHelper.showDialog(this, selectHwBgDialog, true, true);
            if (selection instanceof List<?>) {
                for (Object toAdd : (List<?>) selection) {
                    if (toAdd instanceof HWBaugruppeView) {
                        HWBaugruppeView baugruppeToAdd = (HWBaugruppeView) toAdd;
                        HWBaugruppe hwBaugruppe = hwService.findBaugruppe(baugruppeToAdd.getBaugruppenId());
                        switch (hwBaugruppenChange.getChangeTypeValue()) {
                            case CHANGE_CARD:
                            case MERGE_CARDS:
                            case PORT_CONCENTRATION:
                                addSrcBg4ChangeCard(hwBaugruppe);
                                break;
                            case CHANGE_BG_TYPE:
                                addSrcBg4ChangeBgTyp(hwBaugruppe);
                                break;
                            default:
                                break;
                        }
                        tbMdlBaugruppenSrc.addObject(baugruppeToAdd);
                    }
                }

                hwBaugruppenChangeService.saveHWBaugruppenChange(hwBaugruppenChange);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /**
     * Fuegt die Baugruppe dem der Planung hinzu. Wird fuer Planungen vom Typ 'einfacher Kartenwechsel',
     * 'Karten zusammenlegen' und 'Portkonzentration' verwendet!
     */
    private void addSrcBg4ChangeCard(HWBaugruppe baugruppeToAdd) throws StoreException {
        HWBaugruppenChangeCard hwBgChangeCard = null;
        if (CollectionTools.isEmpty(hwBaugruppenChange.getHwBgChangeCard())) {
            hwBgChangeCard = new HWBaugruppenChangeCard();
            hwBaugruppenChange.addHWBaugruppenChangeCard(hwBgChangeCard);
        }
        else {
            hwBgChangeCard = hwBaugruppenChange.getHwBgChangeCard().iterator().next();
        }

        hwBaugruppenChangeService.checkAndAddHwBaugruppe4Source(hwBgChangeCard, baugruppeToAdd);
    }


    /**
     *  Fuegt die Baugruppe dem der Planung hinzu. Wird fuer Planungen vom Typ 'Baugruppentyp aendern' verwendet!
     */
    private void addSrcBg4ChangeBgTyp(HWBaugruppe baugruppeToAdd) {
        HWBaugruppenChangeBgType hwBgChangeBgTyp = new HWBaugruppenChangeBgType();
        hwBgChangeBgTyp.setHwBaugruppe(baugruppeToAdd);
        hwBaugruppenChange.addHwBgChangeBgTyp(hwBgChangeBgTyp);
    }


    /* Entfernt eine selektierte Baugruppe aus der Quell-Tabelle. */
    protected void removeSrcBg() {
        try {
            List<HWBaugruppeView> selectionToRemove = tbBaugruppenSrc.getTableSelectionAsList(HWBaugruppeView.class);
            if (selectionToRemove != null) {
                for (HWBaugruppeView toRemove : selectionToRemove) {
                    if (hwBaugruppenChange.isChangeType(CHANGE_CARD)
                            || hwBaugruppenChange.isChangeType(MERGE_CARDS)
                            || hwBaugruppenChange.isChangeType(PORT_CONCENTRATION)) {
                        Set<HWBaugruppenChangeCard> changeCards = hwBaugruppenChange.getHwBgChangeCard();
                        if ((changeCards != null) && !changeCards.isEmpty()) {
                            HWBaugruppenChangeCard hwBgChangeCard = changeCards.iterator().next();

                            HWBaugruppe hwBaugruppe = hwService.findBaugruppe(toRemove.getBaugruppenId());
                            hwBgChangeCard.removeHwBaugruppeSource(hwBaugruppe);
                            hwBaugruppenChangeService.saveHWBaugruppenChange(hwBaugruppenChange);

                        }
                    }
                    else if (hwBaugruppenChange.isChangeType(CHANGE_BG_TYPE)) {
                        HWBaugruppenChangeBgType removedObject = hwBaugruppenChange.removeHWBaugruppenChangeBgType(toRemove.getBaugruppenId());
                        if (removedObject != null) {
                            hwBaugruppenChangeService.deleteHWBaugruppenChangeBgType(removedObject);
                        }
                    }

                    tbMdlBaugruppenSrc.removeObject(toRemove);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}


