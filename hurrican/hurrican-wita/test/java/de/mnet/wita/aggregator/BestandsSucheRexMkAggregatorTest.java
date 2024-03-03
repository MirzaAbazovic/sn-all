/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2011 11:38:17
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public class BestandsSucheRexMkAggregatorTest extends BaseTest {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @InjectMocks
    private BestandsSucheRexMkAggregator cut = new BestandsSucheRexMkAggregator();

    @Mock
    private WitaDataService witaDataService;

    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG)
                .setPersist(false).build();
    }

    public void aggregateEinzelanschluss() throws Exception {
        VorabstimmungBuilder pvBuilder = new VorabstimmungBuilder().withBestandssucheEinzelanschluss();
        BestandsSuche achieved = aggregate(pvBuilder);
        assertNotNull(achieved.getOnkz());
    }

    public void aggregateAnlagenanschluss() throws Exception {
        VorabstimmungBuilder pvBuilder = new VorabstimmungBuilder().withBestandssucheAnlagenanschluss();
        BestandsSuche achieved = aggregate(pvBuilder);
        assertNotNull(achieved.getAnlagenOnkz());
    }

    public void aggregateAnlagenanschlussWithInvalidDN() throws Exception {
        // Only 8 numbers are allowed for durchwahlnummer in wita anlagenanschluss
        VorabstimmungBuilder pvBuilder = new VorabstimmungBuilder()
                .withBestandssucheAnlagenanschluss("1234567890");
        aggregate(pvBuilder, false);
    }

    private BestandsSuche aggregate(VorabstimmungBuilder pvBuilder) throws Exception {
        return aggregate(pvBuilder, true);
    }

    private BestandsSuche aggregate(VorabstimmungBuilder pvBuilder, boolean expected) throws Exception {
        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(pvBuilder.build());

        BestandsSuche achieved = cut.aggregate(cbVorgang);
        assertNotNull(achieved);

        Set<ConstraintViolation<BestandsSuche>> violations = validator.validate(achieved);
        if (expected) {
            assertTrue(violations.isEmpty(), "Found unexpected violations " + violations);
        }
        else {
            assertFalse(violations.isEmpty());
        }

        return achieved;
    }

}
