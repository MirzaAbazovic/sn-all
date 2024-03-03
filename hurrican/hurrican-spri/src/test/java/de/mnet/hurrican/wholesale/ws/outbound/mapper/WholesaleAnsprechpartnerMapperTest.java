package de.mnet.hurrican.wholesale.ws.outbound.mapper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Test;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AnsprechpartnerType;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Created by wieran on 06.02.2017.
 */
public class WholesaleAnsprechpartnerMapperTest {

    private WholesaleAnsprechpartnerMapper ansprechpartnerMapper = new WholesaleAnsprechpartnerMapper();

    @Test
    public void testCreateAnsprechpartner() {
        WbciGeschaeftsfall wbciGeschaeftsfall = null;

        AnsprechpartnerType ansprechpartner = ansprechpartnerMapper.createAnsprechpartner();

        de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.AnsprechpartnerType ansprechpartnerAuftragsmanagement = ansprechpartner.getAuftragsmanagement();

        assertThat(ansprechpartnerAuftragsmanagement.getAnrede(), is("9"));
        assertThat(ansprechpartnerAuftragsmanagement.getNachname(), is("MNET"));
        assertThat(ansprechpartnerAuftragsmanagement.getTelefonnummer(), is("089 452000"));

        assertThat(ansprechpartnerAuftragsmanagement, Matchers.instanceOf(de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.AnsprechpartnerType.class));
    }
}