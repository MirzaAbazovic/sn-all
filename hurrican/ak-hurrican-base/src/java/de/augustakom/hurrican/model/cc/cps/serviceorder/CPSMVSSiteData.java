/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2012 08:42:48
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.io.*;
import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import de.augustakom.hurrican.model.billing.Rufnummer;

/**
 * Modellklasse zur Abbildung von MVS-Site-Daten fuer die CPS-Provisionierung.
 */
@XStreamAlias("SITE")
public class CPSMVSSiteData extends CPSMVSBaseData {

    private static final long serialVersionUID = 530184211102297076L;
    @XStreamAlias("ENTERPRISE_ID")
    private Long enterpriseId;

    @XStreamAlias("LOCATION")
    private String location;

    @XStreamAlias("SUBDOMAIN")
    private String subdomain;

    @XStreamAlias("NUMBERS")
    private Numbers numbers;

    @XStreamAlias("CHANNELS")
    private Long channels;

    public CPSMVSSiteData(Long resellerId, Long enterpriseId, String username, String password, String subdomain,
            List<Number> numbers, String location, Long channels) {
        this.resellerId = resellerId;
        this.enterpriseId = enterpriseId;
        this.adminAccount = new CPSMVSAdminAccount(username, password);
        this.subdomain = subdomain;
        this.numbers = new Numbers(numbers);
        this.location = location;
        this.channels = channels;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public String getLocation() {
        return location;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public Numbers getNumbers() {
        return numbers;
    }

    public Long getChannels() {
        return channels;
    }

    @XStreamAlias("NUMBERS")
    public static class Numbers implements Serializable {
        private static final long serialVersionUID = -3762085537162706660L;
        @XStreamImplicit(itemFieldName = "NUMBER")
        private List<Number> numbers;

        public Numbers(List<Number> numbers) {
            this.numbers = numbers;
        }

        public List<Number> getNumbers() {
            return Collections.unmodifiableList(numbers);
        }
    }

    @XStreamAlias("NUMBER")
    public static class Number extends AbstractCPSDNData {

        private static final long serialVersionUID = -738214074823357099L;
        @XStreamAlias("COUNTRYCODE")
        private final static String COUNTRY_CODE = AbstractCPSDNData.COUNTRY_CODE_GERMANY;

        public String getCountryCode() {
            return COUNTRY_CODE;
        }

        @Override
        public void transferDNData(Rufnummer dn) {
            transferDNDataWithoutAdjustments(dn);
        }
    }
}
