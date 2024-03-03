/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 15:39:09
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster.*;
import static de.mnet.wita.message.common.Anlagentyp.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallVbl;
import de.mnet.wita.message.builder.TestAnlage;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.tools.TestHelper;
import de.mnet.wita.validators.groups.V1;

@Test(groups = UNIT)
public class GeschaeftsfallValidationTest extends AbstractValidationTest<Geschaeftsfall> {

    private static final GeschaeftsfallTyp[] GESCHAEFTSFAELLE = { BEREITSTELLUNG, KUENDIGUNG_KUNDE,
            LEISTUNGS_AENDERUNG, LEISTUNGSMERKMAL_AENDERUNG, PORTWECHSEL, PROVIDERWECHSEL,
            RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, VERBUNDLEISTUNG };

    @DataProvider
    public Object[][] geschaeftsfallProvider() {
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            data.add(new Object[] { typ });
        }
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "geschaeftsfallProvider")
    public void testValidGeschaeftsfall(GeschaeftsfallTyp geschaeftsfallTyp) throws Exception {
        Geschaeftsfall gf = new GeschaeftsfallBuilder(geschaeftsfallTyp, WitaCdmVersion.V1).buildValid();
        violations = validator.validate(gf, V1.class);
        assertEquals(violations.size(), 0, "Found validations " + violations);
    }

    @DataProvider
    public Object[][] dataProviderIsZeitfensterValid(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        return new Object[][] {
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_3).buildValid(), true },
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },

                { new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },
                { new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_2).buildValid(), true },

                { new GeschaeftsfallBuilder(LEISTUNGS_AENDERUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_3).buildValid(), true },
                { new GeschaeftsfallBuilder(LEISTUNGS_AENDERUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },

                { new GeschaeftsfallBuilder(LEISTUNGSMERKMAL_AENDERUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },
                { new GeschaeftsfallBuilder(LEISTUNGSMERKMAL_AENDERUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_4).buildValid(), true },

                { new GeschaeftsfallBuilder(PROVIDERWECHSEL, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_7).buildValid(), true },
                { new GeschaeftsfallBuilder(PROVIDERWECHSEL, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },

                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_6).buildValid(), true },
                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },

                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_3).buildValid(), true },
                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },

                { new GeschaeftsfallBuilder(PORTWECHSEL, witaCdmVersion).withKundenwunschtermin(LocalDate.now()).buildValid(), false },
                { new GeschaeftsfallBuilder(PORTWECHSEL, witaCdmVersion).withKundenwunschtermin(LocalDate.now(), SLOT_4).buildValid(), true },
        };
    }

    @Test(dataProvider = "dataProviderIsZeitfensterValid")
    public void testIsZeitfensterValidV1(Geschaeftsfall geschaeftsfall, boolean valid) {
        checkValidation(geschaeftsfall, valid, V1.class);
    }

    public void testValidVblWithoutRnPortierung() throws Exception {
        GeschaeftsfallVbl gf = (GeschaeftsfallVbl) new GeschaeftsfallBuilder(VERBUNDLEISTUNG, WitaCdmVersion.V1).buildValid();
        gf.getAuftragsPosition().getGeschaeftsfallProdukt().setRufnummernPortierung(null);
        violations = validator.validate((Geschaeftsfall) gf, V1.class);
        assertEquals(violations.size(), 0, "Found validations " + violations);
    }

    @DataProvider
    public Object[][] dataProviderBestandsSucheValid(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        BestandsSuche bestandsSuche = new BestandsSuche("89", "1234567", null, null, null);
        BestandsSuche bestandsSucheErweitert = new BestandsSuche(null, null, "89", "1234", "0");
        BestandsSuche bestandsSucheInvalid = new BestandsSuche("89", "1234567", "89", "1234", "0");

        // @formatter:off
        return new Object[][] {
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).buildValid(), true },

                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).withBestandsSuche(bestandsSuche).buildValid(), true },

                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).withBestandsSuche(bestandsSucheErweitert).buildValid(), false },
                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).withBestandsSuche(bestandsSucheInvalid).buildValid(), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderBestandsSucheValid")
    public void testBestandsSucheValidV1(Geschaeftsfall geschaeftsfall, boolean valid) {
        checkValidation(geschaeftsfall, valid, V1.class);
    }

    @DataProvider
    public Object[][] dataProviderVormieterSetAndValid(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        Vormieter personVormieter = new Vormieter("Max", "Weber", null, null, null);
        Vormieter anschlussVormieter = new Vormieter(null, null, "89", "1234567890", null);
        Vormieter ufaVormieter = new Vormieter(null, null, null, null, "1234C123456");
        Vormieter invalidVormieter = new Vormieter("Max", null, null, "1234567890", null);

        // @formatter:off
        return new Object[][] {
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withVormieter(invalidVormieter).buildValid(), false },

                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withVormieter(personVormieter).buildValid(), true },
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withVormieter(anschlussVormieter).buildValid(), true},
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withVormieter(ufaVormieter).buildValid(), true },

                { new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE, witaCdmVersion).withVormieter(personVormieter).buildValid(), false },

                { new GeschaeftsfallBuilder(LEISTUNGS_AENDERUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(LEISTUNGS_AENDERUNG, witaCdmVersion).withVormieter(personVormieter).buildValid(), false },

                { new GeschaeftsfallBuilder(LEISTUNGSMERKMAL_AENDERUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(LEISTUNGSMERKMAL_AENDERUNG, witaCdmVersion).withVormieter(personVormieter).buildValid(), false },

                { new GeschaeftsfallBuilder(PROVIDERWECHSEL, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(PROVIDERWECHSEL, witaCdmVersion).withVormieter(personVormieter).buildValid(), false },

                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).withVormieter(personVormieter).buildValid(), false },

                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).withVormieter(ufaVormieter).buildValid(), false },

                { new GeschaeftsfallBuilder(PORTWECHSEL, witaCdmVersion).buildValid(), true },
                { new GeschaeftsfallBuilder(PORTWECHSEL, witaCdmVersion).withVormieter(anschlussVormieter).buildValid(), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderVormieterSetAndValid")
    public void testVormieterSetAndValidV1(Geschaeftsfall geschaeftsfall, boolean valid) {
        violations = validator.validate(geschaeftsfall, V1.class);
        assertEquals(violations.isEmpty(), valid, "Found validations " + violations);
    }

    @DataProvider
    public Object[][] dataProviderVorabstimmungsIdSetAndValid(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        String vorabstimmungsId = "DEU.MNET.V123456789";
        return new Object[][] {
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), witaCdmVersion == WitaCdmVersion.V1 },

                { new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(KUENDIGUNG_KUNDE, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), witaCdmVersion == WitaCdmVersion.V1 },

                { new GeschaeftsfallBuilder(LEISTUNGS_AENDERUNG, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(LEISTUNGS_AENDERUNG, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), false },

                { new GeschaeftsfallBuilder(LEISTUNGSMERKMAL_AENDERUNG, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(LEISTUNGSMERKMAL_AENDERUNG, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), false },

                { new GeschaeftsfallBuilder(PROVIDERWECHSEL, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(PROVIDERWECHSEL, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), witaCdmVersion == WitaCdmVersion.V1 },

                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(VERBUNDLEISTUNG, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), witaCdmVersion == WitaCdmVersion.V1 },

                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), false },

                { new GeschaeftsfallBuilder(PORTWECHSEL, witaCdmVersion).withVorabstimmungsId(null).buildValid(), true },
                { new GeschaeftsfallBuilder(PORTWECHSEL, witaCdmVersion).withVorabstimmungsId(vorabstimmungsId).buildValid(), false },
        };
    }

    @Test(dataProvider = "dataProviderVorabstimmungsIdSetAndValid")
    public void testVorabstimmungsIdSetAndValidV1(Geschaeftsfall geschaeftsfall, boolean valid) {
        checkValidation(geschaeftsfall, valid, V1.class);
    }

    @DataProvider
    public Object[][] dataProviderAnlageValid(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            // Add valid
            data.add(new Object[] {
                    new GeschaeftsfallBuilder(typ, witaCdmVersion).addTestAnlage(TestAnlage.SIMPLE, SONSTIGE).buildValid(), true });

            // Add without anhang
            Geschaeftsfall gf = new GeschaeftsfallBuilder(typ, witaCdmVersion).buildValid();
            data.add(new Object[] { gf, true });

            // Add with lageplan
            gf = new GeschaeftsfallBuilder(typ, witaCdmVersion).addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.LAGEPLAN).buildValid();
            switch (typ) {
                case BEREITSTELLUNG:
                case PORTWECHSEL:
                    data.add(new Object[] { gf, true });
                    break;
                default:
                    data.add(new Object[] { gf, false });
            }

            // Add second lageplan
            gf = new GeschaeftsfallBuilder(typ, witaCdmVersion).addTestAnlage(TestAnlage.SIMPLE, LAGEPLAN)
                    .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.LAGEPLAN).buildValid();
            if (BEREITSTELLUNG.equals(typ)) {
                data.add(new Object[] { gf, false });
            }

            // Add with kuendigng
            gf = new GeschaeftsfallBuilder(typ, witaCdmVersion).addTestAnlage(TestAnlage.LARGE, KUENDIGUNGSSCHREIBEN).buildValid();
            switch (typ) {
                case BEREITSTELLUNG:
                case KUENDIGUNG_KUNDE:
                case PROVIDERWECHSEL:
                case VERBUNDLEISTUNG:
                    data.add(new Object[] { gf, true });
                    break;
                default:
                    data.add(new Object[] { gf, false });
            }

            // Add second kuendigung
            gf = new GeschaeftsfallBuilder(typ, witaCdmVersion).addTestAnlage(TestAnlage.SIMPLE, KUENDIGUNGSSCHREIBEN)
                    .addTestAnlage(TestAnlage.SIMPLE, KUENDIGUNGSSCHREIBEN).buildValid();
            switch (typ) {
                case KUENDIGUNG_KUNDE:
                case PROVIDERWECHSEL:
                case VERBUNDLEISTUNG:
                    data.add(new Object[] { gf, false });
                    break;
                default:
            }
        }
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "dataProviderAnlageValid")
    public void testAnlageValidV1(Geschaeftsfall geschaeftsfall, boolean valid) {
        checkValidation(geschaeftsfall, valid, V1.class);
    }

    @DataProvider
    public Object[][] dataProviderSonstigeAnlageValid(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            // Add 99 sonstige anlagen - 99 is the maximum allowed in the interface
            data.add(new Object[] {
                    new GeschaeftsfallBuilder(typ, witaCdmVersion).addTestAnlagen(TestAnlage.SIMPLE, SONSTIGE, 99).buildValid(), true });
            // Add 100 sonstige anlagen - 100 exceeds the maximum (of 99)
            data.add(new Object[] {
                    new GeschaeftsfallBuilder(typ, witaCdmVersion).addTestAnlagen(TestAnlage.SIMPLE, SONSTIGE, 100).buildValid(), false });
        }
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "dataProviderSonstigeAnlageValid")
    public void testSonstigeAnlageValidV1(Geschaeftsfall geschaeftsfall, boolean valid) {
        checkValidation(geschaeftsfall, valid, V1.class);
    }

    public void testVertragsnummerOderBestandssuche() {
        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(VERBUNDLEISTUNG, WitaCdmVersion.V1).buildValid();
        violations = validator.validate(geschaeftsfall);
        assertEquals(violations.isEmpty(), true, "Found validations " + violations);
        geschaeftsfall.setVertragsNummer("1234567890");
        violations = validator.validate(geschaeftsfall);
        assertEquals(violations.isEmpty(), true, "Found validations " + violations);
    }

    @DataProvider
    public Object[][] dataProviderAnlagenTooLarge(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            data.add(new Object[] {
                    new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).addTestAnlage(TestAnlage.TOO_LARGE, SONSTIGE).buildValid(), false });
            // Add it five times to exceed four MB
            data.add(new Object[] {
                    new GeschaeftsfallBuilder(BEREITSTELLUNG, witaCdmVersion).addTestAnlagen(TestAnlage.TOO_LARGE, SONSTIGE, 5).buildValid(), false });
        }
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "dataProviderAnlagenTooLarge")
    public void testTooLargeAnlagenV1(Geschaeftsfall geschaeftsfall, boolean valid) {
        checkValidation(geschaeftsfall, valid, V1.class);
    }

}
