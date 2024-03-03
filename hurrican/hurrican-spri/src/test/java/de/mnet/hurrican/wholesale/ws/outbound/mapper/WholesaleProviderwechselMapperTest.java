package de.mnet.hurrican.wholesale.ws.outbound.mapper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.StandortAType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.ProviderwechselTermineType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.ProviderwechselType;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.PersonTestdata;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;

/**
 * Created by wieran on 06.02.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WholesaleProviderwechselMapperTest {

    @Mock
    private WholesaleAnsprechpartnerMapper ansprechpartnerMapper;
    @Mock
    private WholesaleProviderwechselTermineMapper pvTermineMapper;
    @Mock
    private WholesaleStandortAMapper standortAMapper;

    @InjectMocks
    private WholesaleProviderwechselMapper pvMapper;


    @Test
    public void testCreateProviderwechsel() throws Exception {
        //given

        String expectedVorabstimmungsId = "vorabstimmungsId";
        Technologie expectedTechnologie = Technologie.FTTH;
        WbciGeschaeftsfall expectedWbciGeschaeftsfall = new WbciGeschaeftsfallKueMrn();
        expectedWbciGeschaeftsfall.setVorabstimmungsId(expectedVorabstimmungsId);
        expectedWbciGeschaeftsfall.setMnetTechnologie(expectedTechnologie);

        String expectedLineId = "lineId";
        Standort standort = new Standort();
        Person person = PersonTestdata.createPerson();

        AnsprechpartnerType expectedAnsprechpartnerType = new AnsprechpartnerType();
        when(ansprechpartnerMapper.createAnsprechpartner()).thenReturn(expectedAnsprechpartnerType);

        ProviderwechselTermineType expectedPvTermineType = new ProviderwechselTermineType();
        when(pvTermineMapper.createPvTermine(any())).thenReturn(expectedPvTermineType);

        StandortAType expectedStandortAType = new StandortAType();
        when(standortAMapper.createStandortA(any(), eq(person))).thenReturn(expectedStandortAType);

        //when
        ProviderwechselType providerwechsel = pvMapper.createProviderwechsel(expectedWbciGeschaeftsfall, expectedLineId, standort, person);

        //then
        assertThat(providerwechsel.getLineId(), is(expectedLineId));
        assertThat(providerwechsel.getVorabstimmungId(), is(expectedVorabstimmungsId));
        verify(ansprechpartnerMapper).createAnsprechpartner();
        verify(pvTermineMapper).createPvTermine(expectedWbciGeschaeftsfall);
        verify(standortAMapper).createStandortA(standort, person);
        assertThat(providerwechsel.getProdukt(), is("FttB BSA 100/40"));
        assertThat(providerwechsel.getAnsprechpartner(), is(expectedAnsprechpartnerType));
        assertThat(providerwechsel.getTermine(), is(expectedPvTermineType));
        assertThat(providerwechsel.getStandortA(), is(expectedStandortAType));
    }
}