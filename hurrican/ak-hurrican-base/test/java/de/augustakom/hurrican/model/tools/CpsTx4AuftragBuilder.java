package de.augustakom.hurrican.model.tools;

import java.util.*;

import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * CpsTx4AuftragBuilder
 */
public class CpsTx4AuftragBuilder implements HigherOrderBuilder<HigherOrderBuilder.Auftrag> {

    public class CpsTx4Auftrag implements HigherOrderBuilder.Results {
        public CPSTransactionBuilder cpsTransactionBuilder;
    }

    private Date execTime = new Date();
    private Long serviceOrderType = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB;
    private Long txState = CPSTransaction.TX_STATE_SUCCESS;

    public CpsTx4AuftragBuilder withExecTime(Date execTime) {
        this.execTime = execTime;
        return this;
    }

    public CpsTx4AuftragBuilder withServiceOrderType(Long serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
        return this;
    }

    public CpsTx4AuftragBuilder withTxState(Long txState) {
        this.txState = txState;
        return this;
    }

    @Override
    public CpsTx4Auftrag prepare(AbstractHurricanBaseServiceTest test, Auftrag auftrag) {
        CpsTx4Auftrag cpsTx4Auftrag = new CpsTx4Auftrag();
        cpsTx4Auftrag.cpsTransactionBuilder = test.getBuilder(CPSTransactionBuilder.class)
                .withOrderNoOrig(auftrag.auftragDatenBuilder.getAuftragNoOrig())
                .withAuftragBuilder(auftrag.auftragBuilder)
                .withEstimatedExecTime(execTime)
                .withServiceOrderType(serviceOrderType)
                .withTxState(txState);
        return cpsTx4Auftrag;
    }

    public void build(CpsTx4Auftrag auftrag) {
        auftrag.cpsTransactionBuilder.build();
    }
}
