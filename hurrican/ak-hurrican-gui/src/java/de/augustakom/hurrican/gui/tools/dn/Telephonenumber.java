/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.2014
 */
package de.augustakom.hurrican.gui.tools.dn;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;

/**
 * Telefonnummer ohne Verkehrsausscheidungsziffern.
 * Eine komplette zu waehlende internationale Telefonnummer besteht aus folgenden Teilen:
 * "international call prefix" oder "dial out code", z.B. in Deutschland "00"
 * "country calling code", z.B. "49" für Deutschland
 *
 * Quellen:
 * http://en.wikipedia.org/wiki/International_direct_dialing
 * http://en.wikipedia.org/wiki/List_of_international_call_prefixes
 *http://en.wikipedia.org/wiki/Telephone_numbering_plan
 */
public final class Telephonenumber {

    // laut ITU E.164 fuer oeffentliche Netze 15 Stellen (fuer teilnehmerspez. Rufnr und Dienste kann die Rufnr. laenger sein)
    public final static int PUBLIC_MAX_LENGTH = 15;

    /*
     * http://en.wikipedia.org/wiki/List_of_country_calling_codes
     */
    private Integer countryCode;
    private String areaCode;
    private Long subscriberNumber;

    @Override
    public String toString() {
        return Joiner.on(' ').skipNulls().join(countryCode, areaCode, subscriberNumber);
    }

    static Telephonenumber parse(String cc, String ac, String sn) {
        if (Strings.isNullOrEmpty(cc)) {
            return null;
        }
        Telephonenumber number = new Telephonenumber();
        number.countryCode = Integer.parseInt(cc.trim());
        if (Strings.isNullOrEmpty(ac)) {
            return number;
        }
        // Bei CC=39 (Italien) darf die fuehrende Null bei der LAC nicht entfernt werden, ansonsten schon (HUR-21987)
        number.areaCode = Integer.valueOf(39).equals(number.countryCode) ? ac : Long.toString(Long.parseLong(ac.trim()));
        if (Strings.isNullOrEmpty(sn)) {
            return number;
        }
        number.subscriberNumber = Long.parseLong(sn.trim());
        return number;
    }

    public boolean isPublicLenghtOk() {
        return StringUtils.remove(toString(), ' ').length() <= PUBLIC_MAX_LENGTH;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public Long getSubscriberNumber() {
        return subscriberNumber;
    }
}
