package de.mnet.hurrican.wholesale.ws.outbound.mapper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.StandortAType;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.FirmaTestdata;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.PersonTestdata;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.StandortTestdata;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.Standort;

/**
 * Created by wieran on 06.02.2017.
 */
public class WholesaleStandortAMapperTest {

    private WholesaleStandortAMapper standortAMapper = new WholesaleStandortAMapper();

    /**
     * Test that all mandatory fields with person are mapped correctly
     *
     * @throws Exception
     */
    @Test
    public void testShouldMapStandortAWithPerson() throws Exception {
        Standort expectedAdresse = StandortTestdata.createStandort();
        Person person = PersonTestdata.createPerson();

        StandortAType standortA = standortAMapper.createStandortA(expectedAdresse, person);

        assertThat(standortA.getOrt().getOrtsname(), is(expectedAdresse.getOrt()));
        assertThat(standortA.getPostleitzahl(), is(expectedAdresse.getPostleitzahl()));
        assertThat(standortA.getStrasse().getStrassenname(), is(expectedAdresse.getStrasse().getStrassenname()));
        assertThat(standortA.getStrasse().getHausnummer(), is(expectedAdresse.getStrasse().getHausnummer()));
        assertThat(standortA.getStrasse().getHausnummernZusatz(), is(expectedAdresse.getStrasse().getHausnummernZusatz()));

        assertThat(standortA.getFirma(), nullValue());
        assertThat(standortA.getPerson().getAnrede(), is("1"));
        assertThat(standortA.getPerson().getVorname(), is(person.getVorname()));
        assertThat(standortA.getPerson().getNachname(), is(person.getNachname()));
    }

    /**
     * Test that all mandatory fields with 'firma' are mapped correctly
     *
     * @throws Exception
     */
    @Test
    public void testShouldMapStandortAWithFirma() throws Exception {
        Standort expectedAdresse = StandortTestdata.createStandort();
        Firma firma = FirmaTestdata.createFirma();

        StandortAType standortA = standortAMapper.createStandortA(expectedAdresse, firma);

        assertThat(standortA.getOrt().getOrtsname(), is(expectedAdresse.getOrt()));
        assertThat(standortA.getPostleitzahl(), is(expectedAdresse.getPostleitzahl()));
        assertThat(standortA.getStrasse().getStrassenname(), is(expectedAdresse.getStrasse().getStrassenname()));
        assertThat(standortA.getStrasse().getHausnummer(), is(expectedAdresse.getStrasse().getHausnummer()));
        assertThat(standortA.getStrasse().getHausnummernZusatz(), is(expectedAdresse.getStrasse().getHausnummernZusatz()));

        assertThat(standortA.getFirma().getAnrede(), is("4"));
        assertThat(standortA.getFirma().getFirmenname(), is(firma.getFirmenname()));
        assertThat(standortA.getFirma().getFirmennameZweiterTeil(), is(firma.getFirmennamenZusatz()));

        assertThat(standortA.getPerson(), nullValue());
    }


    /**
     * Test that null address does not throw exception
     *
     * @throws Exception
     */
    @Test
    public void testShouldMapStandortAFromNullStandort() throws Exception {
        Person person = PersonTestdata.createPerson();
        StandortAType standortA = standortAMapper.createStandortA(null, person);

        assertThat(standortA, nullValue());
    }


    /**
     * Test that null person does not throw exception
     *
     * @throws Exception
     */
    @Test
    public void testShouldMapStandortAFromNullPersonOderFirma() throws Exception {
        Standort expectedAdresse = StandortTestdata.createStandort();
        StandortAType standortA = standortAMapper.createStandortA(expectedAdresse, null);

        assertThat(standortA, nullValue());
    }

}
