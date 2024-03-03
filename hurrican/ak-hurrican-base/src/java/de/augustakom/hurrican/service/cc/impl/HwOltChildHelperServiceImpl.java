package de.augustakom.hurrican.service.cc.impl;

import javax.annotation.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.HwOltChildHelperService;

/**
 * HwOltChildHelperServiceImpl
 */
public class HwOltChildHelperServiceImpl implements HwOltChildHelperService {

    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;

    @Override
    public <T extends HWOltChild> CPSTransaction createAndSendCpsTx(@Nonnull final T oltChild, final Long serviceOrderType,
            final Long sessionId) {
        final CPSTransactionResult cpsTxResult;
        try {
            cpsTxResult = cpsService.createCPSTransaction4OltChild(oltChild.getId(), serviceOrderType, sessionId);
            if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
                final CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
                cpsService.sendCpsTx2CPSAsyncWithoutNewTx(cpsTx, sessionId);
                return cpsTx;
            }
        }
        catch (StoreException e) {
            throw new RuntimeException(e);
        }

        if (CollectionTools.isEmpty(cpsTxResult.getCpsTransactions())) {
            final AKWarnings warnings = cpsTxResult.getWarnings();
            final String msg = (warnings != null) ? warnings.getWarningsAsText() : null;
            throw new RuntimeException(msg);
        }
        return null;
    }

    @Override
    public <T extends HWOltChild> boolean isOltChildDeleted(@Nonnull final T oltChild) {
        return oltChild.getGueltigBis() == null || oltChild.getGueltigBis().before(DateTools.plusWorkDays(1));
    }
}
