/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 11:32:03
 */
package de.mnet.wita.ticketing.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.MnetWitaRequest;

@Test(groups = BaseTest.UNIT)
public abstract class BaseRequestBsiProtokollConverterTest<T extends MnetWitaRequest, S extends BaseRequestBsiProtokollConverter<T>>
        extends BaseTest {

    @Mock
    protected CBVorgangDAO cbVorgangDAO;
    @Mock
    protected CarrierService carrierService;
    @InjectMocks
    protected S protokollConverter;

    protected CBVorgang setupCbVorgang(T auftrag) throws Exception {
        return setupCbVorgang(auftrag, Boolean.FALSE);
    }

    protected CBVorgang setupCbVorgang(T request, Boolean anbieterwechselTkg46) throws Exception {
        Long auftragId = 348754650L;
        CBVorgang cbVorgang = (new CBVorgangBuilder()).withAnbieterwechselTkg46(anbieterwechselTkg46)
                .withAuftragId(auftragId).build();
        AuftragDaten auftragDaten = (new AuftragDatenBuilder()).withAuftragId(auftragId).build();

        when(cbVorgangDAO.findCBVorgangByCarrierRefNr(request.getExterneAuftragsnummer())).thenReturn(cbVorgang);
        when(carrierService.findAuftragDaten4CB(cbVorgang.getCbId())).thenReturn(Arrays.asList(auftragDaten));
        return cbVorgang;
    }

    protected abstract T createRequest();

    public void externeAuftragsnummerShouldBeSet() throws Exception {
        T request = createRequest();
        setupCbVorgang(request);

        AddCommunication protokollEintrag = protokollConverter.apply(request);

        assertThat(protokollEintrag.getNotes(), containsString(request.getExterneAuftragsnummer()));
    }
}
