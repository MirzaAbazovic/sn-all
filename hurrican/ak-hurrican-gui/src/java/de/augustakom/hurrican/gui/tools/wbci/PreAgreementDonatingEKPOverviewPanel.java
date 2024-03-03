/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKActionGroup;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementDonatingEKPTableModel;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTable;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel;
import de.mnet.wbci.model.CarrierRole;

/**
 * Specific Panel for all donating WBCI preagreements in the tab "Abgebend" in the view "VA-Übersicht".
 *
 *
 */
public class PreAgreementDonatingEKPOverviewPanel extends AbstractPreAgreementOverviewPanel {

    private static final long serialVersionUID = 6667665447100263301L;

    @Override
    protected PreAgreementTableModel createPreAgreementTabelModel() {
        return new PreAgreementDonatingEKPTableModel();
    }

    @Override
    protected CarrierRole getMnetCarrierRole() {
        return CarrierRole.ABGEBEND;
    }

    @Override
    protected void addWholesaleContextMenuEntries(PreAgreementTable preAgreementTable) {
        //not used in Donating view
    }

    @Override
    protected void createCustomContextMenu(AKActionGroup actionGroup) {
        actionGroup.addAction(showTaifunOrderSelectDialogAction);
        actionGroup.addAction(showDecisionDialogAction);
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
        btnPnl.add(btnAssignTaifunOrder, GBCFactory.createGBC(100,   0, 0,  0, 2, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnAnswerVa,          GBCFactory.createGBC(100,   0, 0,  1, 2, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnStorno,            GBCFactory.createGBC(100,   0, 0,  2, 2, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnSendErlm,          GBCFactory.createGBC(100,   0, 0,  3, 1, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnAbbm,              GBCFactory.createGBC(100,   0, 1,  3, 1, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        btnPnl.add(btnAbbmTr,            GBCFactory.createGBC(100,   0, 0,  4, 2, 1, GridBagConstraints.HORIZONTAL, BUTTON_INSETS));
        // @formatter:on
        return btnPnl;
    }

}
