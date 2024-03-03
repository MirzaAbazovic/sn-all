/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 10:52:49
 */
package de.mnet.wita.model.validators;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.message.builder.common.portierung.RufnummernPortierungBuilder;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;

@Test(groups = UNIT)
public class RufnummerPortierungCheckTest extends BaseTest {

    private RufnummernPortierung bestelltePortierung;
    private RufnummernPortierung zurueckgemeldetePortierung;

    public void generateWarningsTextWithoutWarnings() {
        RufnummerPortierungCheck check = new RufnummerPortierungCheck(RufnummerPortierungCheck.IS_ABGEBEND);
        assertTrue(StringUtils.isEmpty(check.generateWarningsText()));
        assertFalse(check.hasWarnings());
    }

    public void bestelltePortierungUngleichZurueckgemeldetePortierung() {
        buildRufnmmer();
        RufnummerPortierungCheck check = new RufnummerPortierungCheck(RufnummerPortierungCheck.IS_ABGEBEND);

        check.portierungMnet = bestelltePortierung;
        check.portierungAndererCarrier = zurueckgemeldetePortierung;
        check.bestelltePortierungUngleichZurueckgemeldetePortierung = true;

        assertTrue(check.hasWarnings());
        String resultText = check.generateWarningsText();

        assertTrue(resultText.contains("65"));
        assertTrue(resultText.contains("66"));
    }

    public void checkPortierungenEqualAbgebend() {
        buildRufnmmer();
        RufnummerPortierungCheck checkAbgebend = new RufnummerPortierungCheck(RufnummerPortierungCheck.IS_ABGEBEND);

        checkAbgebend.checkPortierungenEqual(bestelltePortierung, bestelltePortierung);
        assertFalse(checkAbgebend.hasWarnings());

        checkAbgebend.checkPortierungenEqual(bestelltePortierung, zurueckgemeldetePortierung);
        assertTrue(checkAbgebend.hasWarnings());
        assertTrue(checkAbgebend.taifunPortierungUngleichAkmPvPortierung);

    }

    public void checkPortierungenEqualAufnehmend() {
        RufnummerPortierungCheck checkAufnehmend = new RufnummerPortierungCheck(RufnummerPortierungCheck.IS_AUFNEHMEND);
        checkAufnehmend.checkPortierungenEqual(bestelltePortierung, zurueckgemeldetePortierung);
        assertTrue(checkAufnehmend.bestelltePortierungUngleichZurueckgemeldetePortierung);

    }

    private void buildRufnmmer() {
        bestelltePortierung = new RufnummernPortierungBuilder().withOnkz("66")
                .buildMeldungPortierung(true);
        zurueckgemeldetePortierung = new RufnummernPortierungBuilder().withOnkz("65")
                .buildMeldungPortierung(true);

    }
}
