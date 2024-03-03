/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 16:02:26
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;

@Test(groups = UNIT)
public class AbmPvBsiProtokollConverterTest extends
        AbstractMeldungPvBsiProtokollConverterTest<AuftragsBestaetigungsMeldungPv, AbmPvBsiProtokollConverter> {

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new AbmPvBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void uebernahmeDatumVerbindlichShouldBeSet() {
        AuftragsBestaetigungsMeldungPv abmPv = createMeldung();
        setupUserTask(abmPv);
        AddCommunication protokollEintrag = protokollConverter.apply(abmPv);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(),
                containsString(abmPv.getAufnehmenderProvider().getUebernahmeDatumVerbindlich().toString()));
    }

    public void providerAufnehmendMustBeSet() {
        AuftragsBestaetigungsMeldungPv abmPv = createMeldung();
        setupUserTask(abmPv);
        AddCommunication protokollEintrag = protokollConverter.apply(abmPv);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(),
                containsString(abmPv.getAufnehmenderProvider().getProvidernameAufnehmend()));
    }

    @Override
    protected AuftragsBestaetigungsMeldungPv createMeldung() {
        return new AuftragsBestaetigungsMeldungPvBuilder().build();
    }

}
