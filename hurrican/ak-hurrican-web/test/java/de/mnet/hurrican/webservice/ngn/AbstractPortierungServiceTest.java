/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2016
 */
package de.mnet.hurrican.webservice.ngn;

import static org.mockito.Matchers.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.mockito.Mockito;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.mnet.hurrican.base.AbstractHurricanWebServiceTest;
import de.mnet.hurrican.webservice.ngn.model.PortierungStatus;
import de.mnet.hurrican.webservice.ngn.model.PortierungWarning;
import de.mnet.hurrican.webservice.ngn.model.PortierungWarnings;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Basisklasse fuer die Service tests des PortierungServices
 */
public abstract class AbstractPortierungServiceTest extends AbstractHurricanWebServiceTest {
    private static final Logger LOGGER = Logger.getLogger(AbstractPortierungServiceTest.class);
    protected static final long WBCI_GF_ID = 123L;
    protected static final long WITA_CB_ID = 789L;
    protected static final String VORABSTIMMUNGS_ID = "DEU.MNET.VH12345678";

    protected GeneratedTaifunData taifunData;

    protected void prepareAuftrag(WbciCommonService wbciCommonService, WbciWitaServiceFacade wbciWitaServiceFacade,
            List<WbciGeschaeftsfall> wbciGeschaeftsfallList, List<WitaCBVorgang> witaCBVorgangList) {
        prepareTaifunAuftrag();
        prepareAuftragsDaten();
        Mockito.when(wbciCommonService.findActiveGfByTaifunId(anyLong(), anyBoolean())).thenReturn(wbciGeschaeftsfallList);
        Mockito.when(wbciWitaServiceFacade.findWitaCbVorgaengeByAuftrag(anyLong())).thenReturn(witaCBVorgangList);
        Mockito.when(wbciCommonService.findCompleteGfByTaifunId(anyLong())).thenReturn(wbciGeschaeftsfallList);
        Mockito.when(wbciWitaServiceFacade.findWitaCbVorgaenge(anyString())).thenReturn(witaCBVorgangList);
    }

    protected void prepareAuftragsDaten() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        auftragDatenBuilder.withGueltigVon(new Date(0L)).withGueltigBis(DateTools.getHurricanEndDate());
        auftragDatenBuilder.withAuftragNoOrig(taifunData.getBillingAuftrag().getAuftragNoOrig()).withStatusId(AuftragStatus.IN_BETRIEB);
        auftragDatenBuilder.build();
    }

    protected void prepareTaifunAuftrag() {
        taifunData = getTaifunDataFactory().surfAndFonWithDns(1);
        taifunData.persist();
    }

    protected void dumpEntry(long orderNumber, PortierungStatus entry) {
        StringBuilder sb = new StringBuilder();
        String EOL = System.getProperty("line.separator");
        sb.append("Auftrag = ").append(orderNumber).append(EOL)
                .append("---> message = ").append(entry.getMessage()).append(EOL)
                .append("---> status = ").append(entry.getPortierungStatusEnum()).append(EOL);

        if (entry.getPortierungWarnings().isPresent())
        {
            sb.append("---> warnings").append(EOL);
            PortierungWarnings portierungWarnings = entry.getPortierungWarnings().get();
            for (PortierungWarning warning : portierungWarnings.getWarnings()) {
                sb.append("-----> ").append(warning.getMessage()).append(EOL);
            }
        }

        LOGGER.debug(sb.toString());
    }

    protected Long getAuftragNoOrig() {
        return taifunData.getBillingAuftrag().getAuftragNoOrig();
    }



}


