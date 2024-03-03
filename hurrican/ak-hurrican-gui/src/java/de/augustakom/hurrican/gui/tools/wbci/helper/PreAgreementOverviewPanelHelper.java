/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 31.01.2017

 */

package de.augustakom.hurrican.gui.tools.wbci.helper;


import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.wbci.WholesaleDataChangeListener;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTable;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.hurrican.wholesale.ws.outbound.WholesaleOrderOutboundService;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;

/**
 * Helper methods used by {@link de.augustakom.hurrican.gui.tools.wbci.AbstractPreAgreementOverviewPanel} subclasses.
 * <p>
 * Created by wieran on 31.01.2017.
 */
public class PreAgreementOverviewPanelHelper {

    private static final Logger LOGGER = Logger.getLogger(PreAgreementOverviewPanelHelper.class);

    /**
     * Enables / disables context menu entry send Wholesale PV order, if its a wholesale preagreement
     *
     * @param preAgreementTable
     * @param sendWholesalePVAction
     */
    public void enableSendWholesalePVActionAuf(PreAgreementTable preAgreementTable, SendWholesalePVAction sendWholesalePVAction) {
        PreAgreementVO preAgreementVO = getSelectedPreAgreementVO(preAgreementTable);
        sendWholesalePVAction.setEnabled(preAgreementVO != null && PreAgreementType.WS.equals(preAgreementVO.getPreAgreementType()));
    }

    private static PreAgreementVO getSelectedPreAgreementVO(PreAgreementTable preAgreementTable) {
        int selectedRow = preAgreementTable.getSelectedRow();
        return (PreAgreementVO) ((AKTableSorter) preAgreementTable.getModel()).getDataAtRow(selectedRow);
    }

    /**
     * Action for contextmenu entry send Wholesale order
     */
    public static class SendWholesalePVAction extends AKAbstractAction {

        private final WholesaleDataChangeListener changeListener;
        private PreAgreementTable preAgreementTable;
        private String loginName;
        private WholesaleOrderOutboundService wholesaleOrderOutboundService;

        public SendWholesalePVAction(PreAgreementTable preAgreementTable, WholesaleDataChangeListener changeListener,
                String loginName) {
            super();
            this.loginName = loginName;

            this.preAgreementTable = preAgreementTable;
            this.changeListener = changeListener;
            setName("Wholesale PV senden");
            setActionCommand("send.wholesale.pv");
            try {
                wholesaleOrderOutboundService = CCServiceFinder.instance().getCCService(WholesaleOrderOutboundService.class);
            }
            catch (ServiceNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.debug("sendWholesalePVAction...");

            wholesaleOrderOutboundService.sendWholesaleCreateOrderPV(getSelectedPreAgreementVO(preAgreementTable), loginName);
            changeListener.updateWholesaleData();
        }
    }
}