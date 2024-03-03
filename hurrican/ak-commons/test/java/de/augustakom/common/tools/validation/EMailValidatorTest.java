/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.2004 07:55:18
 */
package de.augustakom.common.tools.validation;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * TestNG-Test fuer <code>EMailValidator</code>.
 */
@Test(groups = { "unit" })
public class EMailValidatorTest {

    public void testIsValidForValid() {
        boolean ok = EMailValidator.getInstance().isValid("test@augustakom.de");
        assertTrue(ok, "EMail wurde als falsch erkannt");
    }

    public void testIsValidForNull() {
        boolean errNull = EMailValidator.getInstance().isValid(null);
        assertFalse(errNull, "<null>-Wert wurde als gueltige EMail-Adresse erkannt");
    }

    public void testIsValidForInvalidDomain() {
        boolean err = EMailValidator.getInstance().isValid("fj@test");
        assertFalse(err, "EMail wurde als korrekt erkannt");
    }

    public void testIsValidForMissingAt() {
        boolean err = EMailValidator.getInstance().isValid("fjdkslio.xy");
        assertFalse(err, "EMail wurde als korrekt erkannt");
    }

    public void testIsValidForInvalidCharacter1() {
        boolean err = EMailValidator.getInstance().isValid("fj\"dks@lio.xy");
        assertFalse(err, "EMail wurde als korrekt erkannt");
    }

    public void testIsValidForInvalidCharacter2() {
        boolean err = EMailValidator.getInstance().isValid("fjdks@li(o.xy");
        assertFalse(err, "EMail wurde als korrekt erkannt");
    }

    public void testIsValidForInvalidCharacter3() {
        boolean err = EMailValidator.getInstance().isValid("fjd\\ks@li(o.xy");
        assertFalse(err, "EMail wurde als korrekt erkannt");
    }
}
