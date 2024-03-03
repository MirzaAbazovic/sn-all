/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2011 13:38:13
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;

import javax.validation.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.DNTNBBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.RufnummerPortierungService;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public class RufnummernPortierungAggregatorTest extends BaseTest {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @InjectMocks
    private RufnummernPortierungAggregator cut = new RufnummernPortierungAggregator();

    @Mock
    private CarrierService carrierService;
    @Mock
    private WitaDataService witaDataService;

    private WitaCBVorgang cbVorgang;

    private RufnummerPortierungService rufnummerPortierungService;

    private Set<Long> rufnummerIds = new HashSet<>();


    @BeforeClass
    public void setUpCbVorgang() throws Exception {

        rufnummerIds.add(12345L);
        rufnummerIds.add(1234567L);

        cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG)
                .withRufnummerIds(rufnummerIds)
                .setPersist(false).build();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        rufnummerPortierungService = new RufnummerPortierungService();
        rufnummerPortierungService.carrierService = carrierService;
        cut.rufnummerPortierungService = rufnummerPortierungService;

        Carrier mnetCarrier = new Carrier();
        mnetCarrier.setElTalEmpfId("D052-089");
        when(carrierService.findCarrier(Carrier.ID_AKOM)).thenReturn(mnetCarrier);
        when(carrierService.findCarrier(Carrier.ID_MNET)).thenReturn(mnetCarrier);
        when(carrierService.findCarrier(Carrier.ID_NEFKOM)).thenReturn(mnetCarrier);

    }

    public void aggregateWithoutRufnummern() throws Exception {
        assertNull(aggregate());
    }

    public void aggregateEinzelanschluss() throws Exception {
        RufnummernPortierung portierung = aggregate(validEinzelAnschluss());
        assertTrue(portierung instanceof RufnummernPortierungEinzelanschluss);
        Set<ConstraintViolation<RufnummernPortierung>> violations = validator.validate(portierung);
        assertTrue(violations.isEmpty(), "Found unexpected violations " + violations);
    }

    public void aggregateEinzelanschlussWithMultipleRufnummer() throws Exception {
        RufnummernPortierung portierung = aggregate(validEinzelAnschluss(), validEinzelAnschluss());
        assertTrue(portierung instanceof RufnummernPortierungEinzelanschluss);
    }

    public void aggregateAnlagenanschluss() throws Exception {
        RufnummernPortierung portierung = aggregate(validAnlagenAnschluss());
        assertTrue(portierung instanceof RufnummernPortierungAnlagenanschluss);
        Set<ConstraintViolation<RufnummernPortierung>> violations = validator.validate(portierung);
        assertTrue(violations.isEmpty(), "Found unexpected violations " + violations);
    }

    public void aggregateAnlagenanschlussWithMultipleRufnummer() throws Exception {
        RufnummernPortierung portierung = aggregate(validAnlagenAnschluss(), validAnlagenAnschluss());
        assertTrue(portierung instanceof RufnummernPortierungAnlagenanschluss);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void aggregateWithAmbigiousAnschluss() throws Exception {
        aggregate(validEinzelAnschluss(), validAnlagenAnschluss());
    }

    private Rufnummer validEinzelAnschluss() {
        Rufnummer rufnummer = new Rufnummer();
        rufnummer.setDnNoOrig(12345L);
        rufnummer.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rufnummer.setRealDate(cbVorgang.getVorgabeMnet());
        rufnummer.setOnKz("089");
        rufnummer.setDnBase("123456");
        rufnummer.setActCarrierDnTnb(new DNTNBBuilder().withTnb(TNB.MNET.carrierNameUC).setPersist(false).build());
        return rufnummer;
    }

    private Rufnummer validAnlagenAnschluss() {
        Rufnummer rufnummer = new Rufnummer();
        rufnummer.setDnNoOrig(1234567L);
        rufnummer.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rufnummer.setRealDate(cbVorgang.getVorgabeMnet());
        rufnummer.setOnKz("089");
        rufnummer.setDnBase("13456");
        rufnummer.setDirectDial("1234");
        rufnummer.setRangeFrom("100");
        rufnummer.setRangeTo("299");
        rufnummer.setActCarrierDnTnb(new DNTNBBuilder().withTnb(TNB.NEFKOM.carrierNameUC).setPersist(false).build());
        return rufnummer;
    }

    private RufnummernPortierung aggregate(Rufnummer... rufnummer) throws Exception {
        ImmutableList<Rufnummer> rufnummernResult = ImmutableList.copyOf(rufnummer);
        when(witaDataService.loadRufnummern(cbVorgang)).thenReturn(rufnummernResult);

        RufnummernPortierung portierung = cut.aggregate(cbVorgang);

        return portierung;
    }

}
