/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2015
 */
package de.mnet.hurrican.acceptance.actions;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.common.tools.dao.FilterByProperty;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class VerifyCpsTxTestAction extends AbstractTestAction {

    private final CCAuftragDAO ccAuftragDAO;
    private final HardwareDAO hardwareDAO;
    private final Long serviceOrderTypeCreateSub;
    private final Long txState;
    private final boolean searchByOrderId;

    private Long auftragId = null;
    private String geraeteBezeichnung = null;

    public VerifyCpsTxTestAction(CCAuftragDAO ccAuftragDAO, HardwareDAO hardwareDAO,
            Long serviceOrderTypeCreateSub, Long txState, boolean searchByOrderId) {
        setName("WaitForCpsTxCreation");
        this.ccAuftragDAO = ccAuftragDAO;
        this.hardwareDAO = hardwareDAO;
        this.serviceOrderTypeCreateSub = serviceOrderTypeCreateSub;
        this.txState = txState;
        this.searchByOrderId = searchByOrderId;
    }

    @Override
    public void doExecute(TestContext context) {
        FilterByProperty filterByProperty;
        if (searchByOrderId) {
            if (auftragId == null) {
                auftragId = Long.valueOf(context.getVariable(VariableNames.AUFTRAG_ID));
            }
            filterByProperty = new FilterByProperty("auftragId", auftragId);
        }
        else {
            if (geraeteBezeichnung == null) {
                geraeteBezeichnung = context.getVariable(VariableNames.GERAETE_BEZEICHNUNG);
            }
            HWRack activeRackByBezeichnung = hardwareDAO.findActiveRackByBezeichnung(geraeteBezeichnung);
            assertNotNull(activeRackByBezeichnung);
            filterByProperty = new FilterByProperty("hwRackId", activeRackByBezeichnung.getId());
        }
        List<CPSTransaction> cpsTransactions = ccAuftragDAO.findFilteredAndOrdered(CPSTransaction.class,
                Arrays.asList(
                        filterByProperty,
                        new FilterByProperty("serviceOrderType", serviceOrderTypeCreateSub),
                        new FilterByProperty("txState", txState)
                ), null);
        if (cpsTransactions.isEmpty()) {
            throw new CitrusRuntimeException(String.format("No CpsTransaction found for %s '%s', "
                            + "serviceOrderType '%s' and txState '%s'.", filterByProperty.getPropertyName(),
                    filterByProperty.getFilterValue(), serviceOrderTypeCreateSub, txState));
        }
    }

    public VerifyCpsTxTestAction withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public VerifyCpsTxTestAction withGeraeteBezeichnung(String geraeteBezeichnung) {
        this.geraeteBezeichnung = geraeteBezeichnung;
        return this;
    }

}
