/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2011 15:50:59
 */
package de.mnet.wita.message;

import static de.mnet.wita.TestGroups.*;

import java.lang.reflect.*;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.VerzoegerungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.tools.TestHelper;
import de.mnet.wita.validators.AbstractValidationTest;
import de.mnet.wita.validators.groups.V1;
import de.mnet.wita.validators.groups.WorkflowV1;

@Test(groups = UNIT)
public class MeldungValidationTest<POS extends MeldungsPosition> extends AbstractValidationTest<Meldung<POS>> {

    private static final Logger LOGGER = Logger.getLogger(MeldungValidationTest.class);

    @DataProvider
    public Object[][] dataProvider(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        return new Object[][] {
                { new AbbruchMeldungBuilder().build(), true },
                { buildWithVorabstimmungsId(new AbbruchMeldungBuilder().build()), false },
                { new AbbruchMeldungBuilder().addMeldungspositionen(100).build(), false },
                { new AbbruchMeldungBuilder().withWitaVersion(witaCdmVersion).addMeldungspositionen(99).build(), true },

                { new AbbruchMeldungPvBuilder().build(), true },
                { buildWithVorabstimmungsId(new AbbruchMeldungPvBuilder().build()), false },

                { new AnkuendigungsMeldungPvBuilder().build(), true },
                { new AnkuendigungsMeldungPvBuilder().addAnlagen(100).build(), false },
                { new AnkuendigungsMeldungPvBuilder().addAnlagen(99).build(), true },
                { new AnkuendigungsMeldungPvBuilder().addMeldungspositionen(100).build(), false },
                { new AnkuendigungsMeldungPvBuilder().withWitaVersion(witaCdmVersion).addMeldungspositionen(99).build(), true },
                { buildWithVorabstimmungsId(new AnkuendigungsMeldungPvBuilder().build()), true },

                { new AuftragsBestaetigungsMeldungBuilder().build(), true },
                { new AuftragsBestaetigungsMeldungBuilder().withLeitung(new LeitungBuilder(witaCdmVersion)
                        .withLeitungsabschnitte(100).buildValid()).build(), false },
                { new AuftragsBestaetigungsMeldungBuilder().addProduktPositionen(100).build(), false },
                { buildWithVorabstimmungsId(new AuftragsBestaetigungsMeldungBuilder().build()), false },

                { new AuftragsBestaetigungsMeldungPvBuilder().build(), true },
                { buildWithVorabstimmungsId(new AuftragsBestaetigungsMeldungPvBuilder().build()), false },

                { new EntgeltMeldungBuilder().build(), true },
                { new EntgeltMeldungBuilder().addAnlagen(100).build(), false },
                { new EntgeltMeldungBuilder().addAnlagen(99).build(), true },
                { new EntgeltMeldungBuilder().addProduktPositionen(100).build(), false },
                { buildWithVorabstimmungsId(new EntgeltMeldungBuilder().build()), false },

                { new EntgeltMeldungPvBuilder().build(), true },
                { buildWithVorabstimmungsId(new EntgeltMeldungPvBuilder().build()), false },

                { new ErledigtMeldungBuilder().build(), true },
                { new ErledigtMeldungBuilder().addProduktPositionen(100).build(), false },
                { buildWithVorabstimmungsId(new ErledigtMeldungBuilder().build()), false },

                { new ErledigtMeldungPvBuilder().build(), true },
                { buildWithVorabstimmungsId(new ErledigtMeldungPvBuilder().build()), false },

                { new QualifizierteEingangsBestaetigungBuilder().build(), true },
                { buildWithVorabstimmungsId(new QualifizierteEingangsBestaetigungBuilder().build()), false },

                { new RueckMeldungPvBuilder().build(), true },
                { buildWithVorabstimmungsId(new RueckMeldungPvBuilder().build()), true },

                { new TerminAnforderungsMeldungBuilder().build(), true },
                { buildWithVorabstimmungsId(new TerminAnforderungsMeldungBuilder().build()), false },

                { new VerzoegerungsMeldungBuilder().build(), true },
                { buildWithVorabstimmungsId(new VerzoegerungsMeldungBuilder().build()), false },
        };
    }

    @Test(dataProvider = "dataProvider")
    public void testValidForWorkflowV1(Meldung<POS> meldung, boolean valid) {
        checkValidation(meldung, valid, V1.class, WorkflowV1.class);
    }

    private Meldung<?> buildWithVorabstimmungsId(Meldung<?> meldung) {
        meldung.setVorabstimmungsId("1234567890123456");
        return meldung;
    }

}
