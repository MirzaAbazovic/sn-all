/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.13
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallRrnpKftBuilder;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaRrnpAuf_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_RRNP;

    /**
     * Tests the RUEMVA sent by the Donating Carrier (Hurrican) with a different MeldungCode - AdresseAbweichend. The
     * AdresseAbweichend code should be stripped from the RUEM-VA, since this is not a valid code in the context of a
     * RRNP geschaeftsfall.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_02, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));
    }

    /**
     * Tests the RUEMVA (Anlagenanschluss) sent by the Donating Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_03, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior()
                        .isKlaerfall(true)
                        .withKlaerfallGrund(
                                "(?s).*Keine, der innerhalb der RUEM-VA angegebenen Rufnummern, ist in der Vorabstimmung enthalten.*")
        );

        hurrican().assertRufnummernblock(
                new RufnummernblockBuilder()
                        .withRnrBlockVon("00")
                        .withRnrBlockBis("29")
                        .build(),
                new RufnummernblockBuilder()
                        .withRnrBlockVon("50")
                        .withRnrBlockBis("99")
                        .build()
        );
    }

    /**
     * Tests the processing of a RUEM-VA which contains more then the expected Rufnummern and
     * 'alle Rufnummern portieren' is enabled.
     * This test verifies that the RUEMVA is processed successfully and the GF is NOT marked as Klaerfall.
     * <p/>
     * <pre>
     *     AtlasESB                          Hurrican (Receiving Carrier)
     *                           <---------  VA
     *                                       |- 'alle Rufnummern portieren' enabled
     *                                       |- Rufnummer 089 - 123456789
     *     RUEMVA   ---------->
     *     |- Rufnummer 089 - 123456789
     *     |- Rufnummer 089 - 1234566660
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_04, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(wbciCdmVersion, false)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior()
                        .isKlaerfall(false)
        );

        hurrican().assertRufnummerOnkzEinzel(
                new RufnummerOnkzBuilder()
                        .withOnkz("89")
                        .withRufnummer("123456789")
                        .build(),
                new RufnummerOnkzBuilder()
                        .withOnkz("89")
                        .withRufnummer("1234566660")
                        .build()
        );
    }

    /**
     * Tests the RUEMVA with MeldungsCode ADAPLZ (see WITA-1794). Normally ADA* should not be sent with a RUEM-VA for
     * the Geschaeftsfall RRNP, since the 'Leitungsübernahme' is <b>not</b> relevant in this case. However this test
     * verifies that the RUEMVA is processed successfully in any case.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA (RRNP)
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_05, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.ZWA));
    }

    /**
     * Tests the RUEMVA with MeldungsCode ZWA and NAT (see WITA-1794). Normally either one or the other can be sent, but
     * not both. However this test verifies that the RUEMVA is processed successfully in any case.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA (RRNP)
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_06, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior(MeldungsCode.NAT));
    }

    /**
     * Tests the RUEMVA with "Ressource" and "Technologie" set (see WITA-1794). Normally in a RUEMVA for a RRNP these
     * elements should not be sent, as they make no sense. However this test verifies that the RUEMVA is processed
     * successfully in any case.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA (RRNP)
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_07_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_07, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());
    }

    /**
     * Tests the RUEMVA (Rufnummerportierung is not set, but is mandatory -> GF should be marked as Klaerfall) sent by
     * the Donating Carrier (Hurrican):
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  VA
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_08_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_08, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior()
                        .isKlaerfall(true)
                        .withKlaerfallGrund("(?s).*Die Attribute .* sind Pflichtfelder und müssen gesetzt sein..*")
        );
    }

    /**
     * Tests the processing of a RUEM-VA which contains more then the expected Rufnummern and 'alle Rufnummern
     * portieren' is disabled. This test verifies that the RUEMVA is processed successfully and the GF IS marked as Klaerfall.
     * <p/>
     * <pre>
     *     AtlasESB                          Hurrican (Receiving Carrier)
     *                           <---------  VA
     *                                       |- 'alle Rufnummern portieren' DISABLED
     *                                       |- Rufnummer 089 - 123456789
     *     RUEMVA   ---------->
     *     |- Rufnummer 089 - 123456789
     *     |- Rufnummer 089 - 1234566660
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_09_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_09, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(wbciCdmVersion, false)
                        .withRufnummernportierung(
                                new RufnummernportierungEinzelnTestBuilder()
                                        .addRufnummer(new RufnummerOnkzTestBuilder()
                                                .withOnkz("89")
                                                .withRufnummer("123456789")
                                                .buildValid(wbciCdmVersion, geschaeftsfallTyp))
                                        .buildValid(wbciCdmVersion, geschaeftsfallTyp))));

        applyBehavior(new ReceiveRUEMVA_TestBehavior()
                .isKlaerfall(true)
                .withKlaerfallGrund("(?s).*Die angegebenen Rufnummern innerhalb der RUEM-VA " +
                        "\\[089/1234566660, 089/123456789\\]  stimmen nicht mit den angefragten Rufnummern der Vorabstimmung" +
                        " \\[089/123456789\\] .berein \\(\"Alle Nummern der Anschl.sse portieren\" ist nicht ausgew.hlt\\).*"));

        hurrican().assertRufnummerOnkzEinzel(
                new RufnummerOnkzBuilder()
                        .withOnkz("89")
                        .withRufnummer("123456789")
                        .build(),
                new RufnummerOnkzBuilder()
                        .withOnkz("89")
                        .withRufnummer("1234566660")
                        .build()
        );
    }


    /**
     * Tests the processing of a RUEM-VA which contains less then the expected Rufnummern and
     * 'alle Rufnummern protieren' is enabled.
     * This test verifies that the RUEMVA is processed successfully and the GF IS NOT marked as Klaerfall, since at
     * least one Rufnummer from the VA is present within the RUEMVA.
     * <p/>
     * <pre>
     *     AtlasESB                          Hurrican (Receiving Carrier)
     *                           <---------  VA
     *                                       |- 'alle Rufnummern protieren' ENABLED
     *                                       |- Rufnummer 089 - 123456789
     *                                       |- Rufnummer 089 - 1234566660
     *     RUEMVA   ---------->
     *     |- Rufnummer 089 - 123456789
     * </pre>
     */
    @CitrusTest
    public void VaRrnpAuf_10_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaRrnpAuf_10, wbciCdmVersion, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallRrnpKftBuilder(wbciCdmVersion, false)
                        .withRufnummernportierung(
                                new RufnummernportierungEinzelnTestBuilder()
                                        .addRufnummer(new RufnummerOnkzTestBuilder()
                                                .withOnkz("89")
                                                .withRufnummer("123456789")
                                                .buildValid(wbciCdmVersion, geschaeftsfallTyp))
                                        .addRufnummer(new RufnummerOnkzTestBuilder()
                                                .withOnkz("89")
                                                .withRufnummer("1234566660")
                                                .buildValid(wbciCdmVersion, geschaeftsfallTyp))
                                        .withAlleRufnummernPortieren(true)
                                        .buildValid(wbciCdmVersion, geschaeftsfallTyp))));

        applyBehavior(new ReceiveRUEMVA_TestBehavior()
                .isKlaerfall(false));

        hurrican().assertRufnummerOnkzEinzel(
                new RufnummerOnkzBuilder()
                        .withOnkz("89")
                        .withRufnummer("123456789")
                        .build()
        );
    }

}
