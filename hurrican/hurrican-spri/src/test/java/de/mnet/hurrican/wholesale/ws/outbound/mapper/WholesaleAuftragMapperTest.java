package de.mnet.hurrican.wholesale.ws.outbound.mapper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.AuftraggeberType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.GeschaeftsfallArtType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AuftragType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.ProviderwechselType;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.PersonTestdata;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;

/**
 * Created by wieran on 06.02.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WholesaleAuftragMapperTest {

    @Mock
    private WholesaleProviderwechselMapper pvMapper;

    @InjectMocks
    private WholesaleAuftragMapper auftragMapper;

    @Test
    public void testCreatePVAuftrag() throws Exception {
        //given
        Person person = PersonTestdata.createPerson();

        ProviderwechselType expectedPvT = new ProviderwechselType();
        when(pvMapper.createProviderwechsel(Mockito.any(WbciGeschaeftsfall.class), anyString(), Mockito.any(Standort.class), eq(person))).thenReturn(expectedPvT);
        WbciGeschaeftsfall expectedWbciGeschaeftsfall = new WbciGeschaeftsfallKueMrn();
        String expectedExterneAuftragsnummer = "ExterneAuftragsNummer";
        String expectedLineId = "lineId";
        Standort expectedStandot = new Standort();

        //when
        AuftragType auftrag = auftragMapper.createPVAuftrag(expectedWbciGeschaeftsfall, expectedExterneAuftragsnummer, expectedLineId, expectedStandot, person);

        //then
        Mockito.verify(pvMapper).createProviderwechsel(expectedWbciGeschaeftsfall, expectedLineId, expectedStandot, person);
        assertThat(auftrag.getExterneAuftragsnummer(), is(expectedExterneAuftragsnummer));
        assertAuftraggeber(auftrag.getAuftraggeber());
        assertThat(auftrag.getGeschaeftsfall().getPV(), is(expectedPvT));
        assertThat(auftrag.getGeschaeftsfallArt(), is(GeschaeftsfallArtType.ENDKUNDENANBIETERWECHSEL));
    }

    private void assertAuftraggeber(AuftraggeberType auftraggeber) {
        assertThat(auftraggeber.getAuftraggebernummer(), is("MNETH"));
        assertThat(auftraggeber.getLeistungsnummer(), is("MNETH-001"));
    }
}