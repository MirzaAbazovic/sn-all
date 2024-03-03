/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.14
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.RufnummernblockTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;

/**
 *
 */
public class CheckRuemVaRufnummerTest extends AbstractValidatorTest<CheckRuemVaRufnummer.RufnummernValidator> {

    @Override
    protected CheckRuemVaRufnummer.RufnummernValidator createTestling() {
        return new CheckRuemVaRufnummer.RufnummernValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s";
    }

    @DataProvider
    public Object[][] checkRufnummerDP() {
        final RufnummerOnkz rn1 = new RufnummerOnkzTestBuilder()
                .withRufnummer("123456787")
                .withOnkz("089")
                .withPortierungKennungPKIabg("D001")
                .build();
        final RufnummerOnkz rn2 = new RufnummerOnkzTestBuilder()
                .withRufnummer("123456788")
                .withOnkz("089")
                .withPortierungKennungPKIabg("D001")
                .build();
        final RufnummerOnkz rn3 = new RufnummerOnkzTestBuilder()
                .withRufnummer("123456789")
                .withOnkz("089")
                .withPortierungKennungPKIabg("D001")
                .build();
        final RufnummerOnkz rn4 = new RufnummerOnkzTestBuilder()
                .withRufnummer("123456789")
                .withOnkz("088")
                .withPortierungKennungPKIabg("D001")
                .build();
        final Rufnummernblock rnb1 = new RufnummernblockTestBuilder()
                .withRnrBlockVon("00")
                .withRnrBlockVon("49")
                .withPkiAbg("D001")
                .build();
        final Rufnummernblock rnb2 = new RufnummernblockTestBuilder()
                .withRnrBlockVon("50")
                .withRnrBlockVon("99")
                .withPkiAbg("D001")
                .build();
        final Rufnummernblock rnb3 = new RufnummernblockTestBuilder()
                .withRnrBlockVon("100")
                .withRnrBlockVon("200")
                .withPkiAbg("D001")
                .build();

        return new Object[][] {
                { getGeschaeftsfallWithoutRnp(), getRuemVaRnpEinzeln(rn3, rn4), true },
                { getGeschaeftsfallRnpEinzeln(rn1, rn2), getRuemVaWithoutRnp(), true },
                { getGeschaeftsfallWithoutRnp(), getRuemVaWithoutRnp(), true },
                { getGeschaeftsfallRnpEinzeln(rn1, rn2), getRuemVaRnpEinzeln(rn3, rn4), false },
                { getGeschaeftsfallRnpEinzeln(rn1, rn2), getRuemVaRnpEinzeln(rn1, rn4), false },
                { getGeschaeftsfallRnpEinzeln(rn1, rn2), getRuemVaRnpEinzeln(rn1, rn2, rn4), false },
                { getGeschaeftsfallRnpEinzelnAlle(rn1, rn2), getRuemVaRnpEinzeln(rn1, rn2, rn4), true },
                { getGeschaeftsfallRnpEinzelnAlle(rn1, rn2), getRuemVaRnpEinzeln(rn1, rn3, rn4), true },
                { getGeschaeftsfallRnpAnlage("123", "089", rnb1), getRuemVaRnpEinzeln(rn1, rn4), false },
                { getGeschaeftsfallRnpEinzeln(rn1, rn2), getRuemVaRnpAnlage("123", "089", rnb1), false },
                { getGeschaeftsfallRnpAnlage("123", "089", rnb1, rnb2), getRuemVaRnpAnlage("123", "089", rnb1, rnb2), true },
                { getGeschaeftsfallRnpAnlage("123", "089", rnb1, rnb2), getRuemVaRnpAnlage("123", "089", rnb1, rnb3), false },
                { getGeschaeftsfallRnpAnlage("123", "089", rnb1, rnb2), getRuemVaRnpAnlage("123", "089", rnb3), false },
                { getGeschaeftsfallRnpAnlage("124", "089", rnb1, rnb2), getRuemVaRnpAnlage("123", "089", rnb1, rnb2), false },
                { getGeschaeftsfallRnpAnlage("123", "0891", rnb1, rnb2), getRuemVaRnpAnlage("123", "089", rnb1, rnb2), false },
                { null, getRuemVaRnpAnlage("123", "089", rnb1, rnb2), true },
                { new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(V1, VA_KUE_ORN), getRuemVaRnpAnlage("123", "089", rnb1, rnb2), true },
        };
    }

    @Test(dataProvider = "checkRufnummerDP")
    public void testCheckRufnummer(WbciGeschaeftsfall wbciGeschaeftsfalls, RueckmeldungVorabstimmungBuilder ruemVaBuilder, boolean valid) throws Exception {
        ruemVaBuilder.withWbciGeschaeftsfall(wbciGeschaeftsfalls);
        Assert.assertEquals(testling.isValid(ruemVaBuilder.build(), contextMock), valid);
        assertErrorMessageSet(valid);
    }

    private WbciGeschaeftsfallKueMrn getGeschaeftsfallRnpEinzeln(RufnummerOnkz... rufnummerOnkzs) {
        return new WbciGeschaeftsfallKueMrnTestBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungEinzelnTestBuilder()
                                .withRufnummerOnkzs(rufnummerOnkzs != null ? Arrays.asList(rufnummerOnkzs) : null)
                                .buildValid(V1, VA_KUE_MRN)
                )
                .buildValid(V1, VA_KUE_MRN);
    }

    private WbciGeschaeftsfallKueMrn getGeschaeftsfallRnpEinzelnAlle(RufnummerOnkz... rufnummerOnkzs) {
        return new WbciGeschaeftsfallKueMrnTestBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungEinzelnTestBuilder()
                                .withRufnummerOnkzs(rufnummerOnkzs != null ? Arrays.asList(rufnummerOnkzs) : null)
                                .withAlleRufnummernPortieren(true)
                                .buildValid(V1, VA_KUE_MRN)
                )
                .buildValid(V1, VA_KUE_MRN);
    }

    private WbciGeschaeftsfallRrnp getGeschaeftsfallRnpAnlage(String durchwahlnummer, String onkz, Rufnummernblock... rufnummernblocks) {
        return new WbciGeschaeftsfallRrnpTestBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungAnlageTestBuilder()
                                .withDurchwahlnummer(durchwahlnummer)
                                .withOnkz(onkz)
                                .withRufnummernbloecke(rufnummernblocks != null ? Arrays.asList(rufnummernblocks) : null)
                                .buildValid(V1, VA_RRNP)
                )
                .buildValid(V1, VA_RRNP);
    }

    private WbciGeschaeftsfallRrnp getGeschaeftsfallWithoutRnp() {
        WbciGeschaeftsfallRrnp wbciGeschaeftsfallRrnp = new WbciGeschaeftsfallRrnpTestBuilder().buildValid(V1, VA_RRNP);
        wbciGeschaeftsfallRrnp.setRufnummernportierung(null);
        return wbciGeschaeftsfallRrnp;
    }

    private RueckmeldungVorabstimmungTestBuilder getRuemVaRnpEinzeln(RufnummerOnkz... rufnummerOnkzs) {
        return new RueckmeldungVorabstimmungTestBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungEinzelnTestBuilder()
                                .withRufnummerOnkzs(rufnummerOnkzs != null ? Arrays.asList(rufnummerOnkzs) : null)
                                .buildValid(V1, VA_KUE_MRN)
                );
    }

    private RueckmeldungVorabstimmungTestBuilder getRuemVaWithoutRnp() {
        return new RueckmeldungVorabstimmungTestBuilder()
                .withoutRufnummernportierung();
    }

    private RueckmeldungVorabstimmungTestBuilder getRuemVaRnpAnlage(String durchwahlnummer, String onkz, Rufnummernblock... rufnummernblocks) {
        return new RueckmeldungVorabstimmungTestBuilder()
                .withRufnummernportierung(
                        new RufnummernportierungAnlageTestBuilder()
                                .withDurchwahlnummer(durchwahlnummer)
                                .withOnkz(onkz)
                                .withRufnummernbloecke(rufnummernblocks != null ? Arrays.asList(rufnummernblocks) : null)
                                .buildValid(V1, VA_RRNP)
                );
    }

}
