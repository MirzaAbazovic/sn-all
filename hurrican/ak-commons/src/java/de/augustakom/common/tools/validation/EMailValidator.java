/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2004 15:55:06
 */
package de.augustakom.common.tools.validation;

import java.util.regex.*;

/**
 * Quellcode kopiert aus Apache Jakarta-Commons 'EmailValidator'!
 */
public class EMailValidator {

    private static final String SPECIAL_CHARS = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String ATOM = VALID_CHARS + '+';
    private static final String WORD = "(" + ATOM + "|" + QUOTED_USER + ")";

    private static final Pattern LEGAL_ASCII_PATTERN = Pattern.compile("^[\\000-\\0177]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");
    private static final Pattern IP_DOMAIN_PATTERN =
            Pattern.compile("^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$");

    private static final Pattern USER_PATTERN = Pattern.compile("^" + WORD + "(\\." + WORD + ")*$");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^" + ATOM + "(\\." + ATOM + ")*$");
    private static final Pattern ATOM_PATTERN = Pattern.compile("(" + ATOM + ")");

    /**
     * Singleton instance of this class.
     */
    private static final EMailValidator INSTANCE = new EMailValidator();

    /**
     * Returns the Singleton instance of this validator.
     */
    public static EMailValidator getInstance() {
        return INSTANCE;
    }

    /**
     * Protected constructor for subclasses to use.
     */
    protected EMailValidator() {
        super();
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A <code>null</code> value is considered invalid.
     */
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }

        Matcher legalAscii = LEGAL_ASCII_PATTERN.matcher(email);
        if (!legalAscii.matches()) {
            return false;
        }

        // Check the whole email address structure
        if (isEmailAddressStructureNotValid(email)) {
            return false;
        }

        return true;
    }

    private boolean isEmailAddressStructureNotValid(String email) {
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return true;
        }

        if (email.endsWith(".")) {
            return true;
        }

        if (!isValidUser(emailMatcher.group(1))) {
            return true;
        }

        if (!isValidDomain(emailMatcher.group(2))) {
            return true;
        }
        return false;
    }

    /**
     * Ueberprueft, ob alle eMails gueltig sind.
     */
    public boolean areValid(String[] emails) {
        if (emails != null) {
            for (String email : emails) {
                if (!isValid(email)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns true if the domain component of an email address is valid.
     *
     * @param domain being validated.
     */
    protected boolean isValidDomain(String domain) {
        boolean symbolic = false;
        Matcher ipAddressMatcher = IP_DOMAIN_PATTERN.matcher(domain);

        if (ipAddressMatcher.matches()) {
            if (!isValidIpAddress(ipAddressMatcher)) {
                return false;
            }
        }
        else {
            // Domain is symbolic name
            Matcher domainMatcher = DOMAIN_PATTERN.matcher(domain);
            symbolic = domainMatcher.matches();
        }

        if (symbolic) {
            if (!isValidSymbolicDomain(domain)) {
                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the user component of an email address is valid.
     *
     * @param user being validated
     */
    protected boolean isValidUser(String user) {
        Matcher userMatcher = USER_PATTERN.matcher(user);
        return userMatcher.matches();
    }

    /**
     * Validates an IP address. Returns true if valid.
     *
     * @param ipAddressMatcher Pattren matcher
     */
    protected boolean isValidIpAddress(Matcher ipAddressMatcher) {
        for (int i = 1; i <= 4; i++) {
            String ipSegment = ipAddressMatcher.group(i);
            if (ipSegment == null || ipSegment.length() <= 0) {
                return false;
            }

            int iIpSegment = 0;

            try {
                iIpSegment = Integer.parseInt(ipSegment);
            }
            catch (NumberFormatException e) {
                return false;
            }

            if (iIpSegment > 255) {
                return false;
            }

        }
        return true;
    }

    /**
     * Validates a symbolic domain name.  Returns true if it's valid.
     *
     * @param domain symbolic domain name
     */
    protected boolean isValidSymbolicDomain(String domain) {
        String[] domainSegment = new String[10];
        boolean match = true;
        int i = 0;

        while (match) {
            Matcher atomMatcher = ATOM_PATTERN.matcher(domain);
            match = atomMatcher.find();
            if (match) {
                domainSegment[i] = atomMatcher.group(1);
                int l = domainSegment[i].length() + 1;
                domain =
                        (l >= domain.length())
                                ? ""
                                : domain.substring(l);

                i++;
            }
        }

        int len = i;
        if (domainSegment[len - 1].length() < 2 || domainSegment[len - 1].length() > 4) {
            return false;
        }

        // Make sure there's a host name preceding the domain.
        if (len < 2) {
            return false;
        }

        return true;
    }

}



