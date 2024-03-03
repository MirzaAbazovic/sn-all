/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2004 11:09:16
 */
package de.augustakom.common.tools.lang;

import java.io.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Hilfsklasse zum Ver- und Entschluesseln von Strings.
 */
public final class DesEncrypter {

    private static final Logger LOGGER = Logger.getLogger(DesEncrypter.class);

    private static DesEncrypter instance = null;

    private Cipher ecipher;
    private Cipher dcipher;

    // 8-byte Salt
    private byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

    int iterationCount = 19;
    private static final String PASS_PHRASE = "0x832*3s#ak#s";
    private static final String ALGORITHM = "PBEWithMD5AndDES";

    /*
     * Konstruktor.
     */
    private DesEncrypter() {
        init();
    }

    /**
     * Gibt eine Singleton-Instanz des Encryters zurueck.
     */
    public static DesEncrypter getInstance() {
        if (instance == null) {
            instance = new DesEncrypter();
        }
        return instance;
    }

    /* Initialisiert den Encrypter */
    private void init() {
        try {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(PASS_PHRASE.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        }
        catch (java.security.InvalidAlgorithmParameterException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (java.security.spec.InvalidKeySpecException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (javax.crypto.NoSuchPaddingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (java.security.NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (java.security.InvalidKeyException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Verschluesselt den String <code>toEncrypt</code> und gibt den verschluesselten String zurueck.
     *
     * @param toEncrypt String der verschluesselt werden soll.
     * @return Verschluesselter String.
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encrypt(String toEncrypt) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        // Encode the string into bytes using utf-8
        byte[] utf8 = toEncrypt.getBytes("UTF8");

        // Encrypt
        byte[] enc = ecipher.doFinal(utf8);

        // Encode bytes to base64 to get a string
        return Base64.encodeBase64String(enc);
    }

    /**
     * @see #encrypt(String)
     */
    public String encrypt(String toEncrypt, boolean ignoreNull) {
        try {
            if (ignoreNull && StringUtils.isBlank(toEncrypt)) {
                return null;
            }
            return encrypt(toEncrypt);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Entschluesselt den angegebenen String.
     *
     * @param toDecrypt String der entschluesselt werden soll.
     * @return Entschluesselter String.
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(String toDecrypt) throws IOException, IllegalBlockSizeException, BadPaddingException {
        if (StringUtils.isBlank(toDecrypt)) {
            return null;
        }

        // Decode base64 to get bytes
        byte[] dec = Base64.decodeBase64(toDecrypt);

        // Decrypt
        byte[] utf8 = dcipher.doFinal(dec);

        // Decode using utf-8
        return new String(utf8, "UTF8");
    }

    /**
     * @see #decrypt(String)
     */
    public String decryptSilent(String toDecrypt) {
        try {
            return decrypt(toDecrypt);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}

