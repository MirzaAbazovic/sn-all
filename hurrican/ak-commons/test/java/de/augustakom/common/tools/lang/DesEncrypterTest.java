/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2004 11:19:34
 */
package de.augustakom.common.tools.lang;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 * Unit-Test fuer de.augustakom.common.tools.lang.DesEncrypter
 *
 *
 */
@Test(groups = { "unit" })
public class DesEncrypterTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(DesEncrypterTest.class);

    @DataProvider
    public Object[][] passwords() {
        return new Object[][] {
                { "Test", "cYpldbU0kYI=" },
                { "ARSTrsad", "uMa1unm6QqfAbxvooCewag==" },
                { "Trsatrs%45arst@456", "5tOY+aBgAr7HMjtOtnBXP2QbXXeO5eln" },
                { "{}$sr0ärast", "hCj/nQF/eP4sVFC9OJXW4A==" },
                { "öüRSAtsdars4235", "mHVX+VaW9RWZx58wQqtnKERq88tn6ONs" },
                { "$%&^(%*(!@%^", "2Q4dguSgHptGZWm2JWXfow==" },
        };
    }

    /**
     * Test fuer die Methode DesEncrypter.encrypt(String)
     */
    @Test(dataProvider = "passwords")
    public void testEncrypt(String decrypted, String encrypted) throws Exception {
        DesEncrypter encrypter = DesEncrypter.getInstance();
        String encryptedFromEncrypter = encrypter.encrypt(decrypted);
        LOGGER.debug("Kodiert: " + encryptedFromEncrypter);
        Assert.assertEquals(encryptedFromEncrypter, encrypted, "Encryption of string '" + decrypted + "' not valid!");
    }

    /**
     * Test fuer die Methode DesEncrypter.decrypt(String)
     */
    @Test(dataProvider = "passwords")
    public void testDecrypt(String decrypted, String encrypted) throws Exception {
        DesEncrypter decrypter = DesEncrypter.getInstance();
        String decryptedFromDecrypter = decrypter.decrypt(encrypted);
        LOGGER.debug("Dekodiert: " + decryptedFromDecrypter);
        Assert.assertEquals(decryptedFromDecrypter, decrypted, "Decryption of string '" + encrypted + "' not valid!");
    }

}
