/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;

/**
 * Selects all hurrican orders for the given billing order no and changes the state of {@link AuftragDaten}
 * to {@link AuftragStatus#STORNO}
 */
public class CancelHurricanOrdersTestAction extends AbstractWbciTestAction {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyGfStatusAction.class);

    private AuftragDatenDAO auftragDatenDAO;
    private Long billingOrderNoOrig;

    public CancelHurricanOrdersTestAction(AuftragDatenDAO auftragDatenDAO, Long billingOrderNoOrig) {
        super("cancelHurricanOrdersTestAction");
        this.auftragDatenDAO = auftragDatenDAO;
        this.billingOrderNoOrig = billingOrderNoOrig;
    }

    @Override
    public void doExecute(TestContext context) {
        List<AuftragDaten> auftragDaten4OrderNoOrig = auftragDatenDAO.findByOrderNoOrig(billingOrderNoOrig, true);
        if (CollectionUtils.isNotEmpty(auftragDaten4OrderNoOrig)) {
            for (AuftragDaten auftragDaten : auftragDaten4OrderNoOrig) {
                auftragDaten.setStatusId(AuftragStatus.STORNO);
                auftragDatenDAO.store(auftragDaten);
                LOGGER.info(String.format("Hurrican order %s cancelled", auftragDaten.getAuftragId()));
            }
        }
    }
    
}
