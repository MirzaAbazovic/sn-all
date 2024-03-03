/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;

import java.awt.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKActionGroup;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.wbci.helper.PreAgreementOverviewPanelHelper;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementReceivingEKPTableModel;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTable;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel;
import de.mnet.wbci.model.CarrierRole;

/**
 * Specific Panel for all received WBCI preagreements in the tab "Aufnehmend" in the view "VA-Übersicht".
 */
public class PreAgreementReceivingEKPOverviewPanel extends AbstractPreAgreementOverviewPanel {

    private static final long serialVersionUID = -6843560031485935488L;
    private static final Logger LOGGER = Logger.getLogger(PreAgreementReceivingEKPOverviewPanel.class);


    protected PreAgreementOverviewPanelHelper.SendWholesalePVAction sendWholesalePVAction;
    private PreAgreementOverviewPanelHelper preAgreementOverviewPanelHelper;

    @Override
    protected PreAgreementTableModel createPreAgreementTabelModel() {
        return new PreAgreementReceivingEKPTableModel();
    }


    @Override
    protected CarrierRole getMnetCarrierRole() {
        return CarrierRole.AUFNEHMEND;
    }


    /**
     * use this method to add additional wholesale context menu entries
     *
     * @param preAgreementTable
     */
    @Override
    protected void addWholesaleContextMenuEntries(PreAgreementTable preAgreementTable) {
        preAgreementOverviewPanelHelper = new PreAgreementOverviewPanelHelper();

        if (isFeatureEnabled(WHOLESALE_PV)) {
            addWholesalePVContextMenuEntry(preAgreementTable);
        }
    }

    private void addWholesalePVContextMenuEntry(PreAgreementTable preAgreementTable) {
        try {
            AKUser user = akUserService.findUserBySessionId(HurricanSystemRegistry.instance().getSessionId());
            sendWholesalePVAction = new PreAgreementOverviewPanelHelper.SendWholesalePVAction(preAgreementTable, this, user.getLoginName());
            preAgreementTable.addPopupAction(sendWholesalePVAction);
        }
        catch (AKAuthenticationException e) {
            LOGGER.warn("Unable to get user roles - super user operations might not be enabled in this session", e);
        }
    }


    @Override
    protected void validateButtonsAndActions() {
        //enable/disable context menu entry
        if (isFeatureEnabled(WHOLESALE_PV)) {
            preAgreementOverviewPanelHelper.enableSendWholesalePVActionAuf(preAgreementTable, sendWholesalePVAction);
        }
    }

    @Override
    protected void createCustomContextMenu(AKActionGroup actionGroup) {
        actionGroup.addAction(showAkmTrDialogAction);
        actionGroup.addActionSeparator();
        actionGroup.addAction(showTvDialogAction);
        actionGroup.addActionSeparator();
        actionGroup.addAction(showStornoDialogAction);
        actionGroup.addActionSeparator();
        actionGroup.addAction(showErlmDialogAction);
        actionGroup.addAction(showAbbmDialogAction);
    }

    @Override
    protected AKJPanel createCustomFunctionsPanel() {
        // @formatter:off
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAkmTr,             GBCFactory.createGBC(100,   0, 0,  0, 2, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnTv,                GBCFactory.createGBC(100,   0, 0,  1, 2, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnStorno,            GBCFactory.createGBC(100,   0, 0,  2, 2, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnSendErlm,          GBCFactory.createGBC(100,   0, 0,  3, 1, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnAbbm,              GBCFactory.createGBC(100,   0, 1,  3, 1, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        // @formatter:on
        return btnPnl;
    }
}
