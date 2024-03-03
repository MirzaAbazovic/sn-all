/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 12:58:35
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.common.tools.matcher.RegularExpressionFinder.*;
import static de.augustakom.hurrican.model.cc.AuftragStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = UNIT)
public class AbmBsiProtokollConverterTest extends
        BaseMeldungBsiProtokollConverterTest<AuftragsBestaetigungsMeldung, AbmBsiProtokollConverter> {

    private static final Logger LOGGER = Logger.getLogger(AbmBsiProtokollConverterTest.class);

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new AbmBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void aenderungsKennzeichenStandardShouldChangeText() throws Exception {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().withAenderungsKennzeichen(
                AenderungsKennzeichen.STANDARD).build();
        setupCbVorgang(abm);

        AddCommunication protokollEintrag = protokollConverter.apply(abm);
        assert protokollEintrag != null;
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString("Lieferung erfolgt am"));
    }

    @DataProvider
    public Object[][] dataProviderFindHurricanAuftragIdViaCbVorgang() {
        Long auftragId1 = 1000000101L;
        Long auftragId2 = 1000000102L;
        // @formatter:off
        return new Object[][] {
                { auftragId1, IN_BETRIEB,               auftragId2, KUENDIGUNG,                 auftragId1 },
                { auftragId1, KUENDIGUNG,               auftragId2, ERFASSUNG_SCV,              auftragId2 },
                { auftragId1, ABSAGE,                   auftragId2, AUS_TAIFUN_UEBERNOMMEN,     auftragId2 },
                { auftragId1, KUENDIGUNG_TECHN_REAL,    auftragId2, STORNO,                     auftragId1 },
                { auftragId1, STORNO,                   auftragId2, KUENDIGUNG_ERFASSEN,        auftragId2 },

                { auftragId1, IN_BETRIEB,               auftragId2, IN_BETRIEB,                 auftragId1 },
                { auftragId1, ERFASSUNG,                auftragId2, KUENDIGUNG_ERFASSEN,        auftragId1 },

                { auftragId1, KUENDIGUNG,               auftragId2, ABSAGE,                     auftragId1 },
                { auftragId1, KONSOLIDIERT,             auftragId2, STORNO,                     auftragId1 },
                { auftragId1, AUFTRAG_GEKUENDIGT,       auftragId2, KUENDIGUNG,                 auftragId1 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFindHurricanAuftragIdViaCbVorgang")
    public void testFindHurricanAuftragIdViaCbVorgang(Long auftragId1, Long statusId1, Long auftragId2,
            Long statusId2, Long expectedAuftragId) throws Exception {
        String extAuftragsnr = "727027";

        CBVorgang cbVorgang = (new CBVorgangBuilder()).withAuftragId(1231234512L).build();
        when(cbVorgangDao.findCBVorgangByCarrierRefNr(extAuftragsnr)).thenReturn(cbVorgang);

        AuftragDaten ad1 = (new AuftragDatenBuilder()).withAuftragId(auftragId1).withStatusId(statusId1).build();
        AuftragDaten ad2 = (new AuftragDatenBuilder()).withAuftragId(auftragId2).withStatusId(statusId2).build();
        when(carrierService.findAuftragDaten4CB(cbVorgang.getCbId())).thenReturn(Lists.newArrayList(ad1, ad2));

        try {
            Long result = protokollConverter.findHurricanAuftragIdViaCbVorgang(extAuftragsnr);
            assertEquals(result, expectedAuftragId);
        }
        catch (RuntimeException e) {
            assertNull(expectedAuftragId);
            assertThat(e.getMessage(), findsPattern("^Keinen aktiven Auftrag zur externen Auftragsnummer"));
        }
    }

    public void testMultipleAuftraegeForCarrierbestellungFoundButNoActive() throws Exception {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().withAenderungsKennzeichen(
                AenderungsKennzeichen.STANDARD).build();
        setupCbVorgang(abm);

        AddCommunication protokollEintrag = protokollConverter.apply(abm);
        assert protokollEintrag != null;
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString("Lieferung erfolgt am"));
    }

    public void testMultipleAuftraegeForCarrierbestellungFoundButMultipleActive() throws Exception {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().withAenderungsKennzeichen(
                AenderungsKennzeichen.STANDARD).build();
        setupCbVorgang(abm);

        AddCommunication protokollEintrag = protokollConverter.apply(abm);
        assert protokollEintrag != null;
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString("Lieferung erfolgt am"));
    }

    public void anbieterwechsel() throws Exception {
        AuftragsBestaetigungsMeldung abm = createMeldung();
        setupCbVorgang(abm, Boolean.TRUE);

        AddCommunication protokollEintrag = protokollConverter.apply(abm);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(), containsString(WitaCBVorgang.ANBIETERWECHSEL_46TKG));
    }

    @Override
    protected AuftragsBestaetigungsMeldung createMeldung() {
        return new AuftragsBestaetigungsMeldungBuilder().build();
    }
}
