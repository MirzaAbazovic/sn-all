package de.augustakom.hurrican.model.tools;

import java.util.*;

import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.HWRackBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * CpsTx4DeviceBuilder
 */
public class CpsTx4FtthDeviceBuilder implements HigherOrderBuilder<HWRackBuilder> {

    public class CpsTx4FtthDevice implements HigherOrderBuilder.Results {
        public CPSTransactionBuilder cpsTransactionBuilder;
    }

    private Date execTime = new Date();
    private Long serviceOrderType = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB;
    private Long txState = CPSTransaction.TX_STATE_SUCCESS;

    public CpsTx4FtthDeviceBuilder withExecTime(Date execTime) {
        this.execTime = execTime;
        return this;
    }

    public CpsTx4FtthDeviceBuilder withServiceOrderType(Long serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
        return this;
    }

    public CpsTx4FtthDeviceBuilder withTxState(Long txState) {
        this.txState = txState;
        return this;
    }

    @Override
    public CpsTx4FtthDevice prepare(AbstractHurricanBaseServiceTest test, HWRackBuilder hwRackBuilder) {
        CpsTx4FtthDevice cpsTx4Auftrag = new CpsTx4FtthDevice();
        cpsTx4Auftrag.cpsTransactionBuilder = test.getBuilder(CPSTransactionBuilder.class)
                .withHwRackBuilder(hwRackBuilder)
                .withEstimatedExecTime(execTime)
                .withServiceOrderType(serviceOrderType)
                .withTxState(txState);
        return cpsTx4Auftrag;
    }

    public void build(CpsTx4FtthDevice device) {
        device.cpsTransactionBuilder.build();
    }
}
