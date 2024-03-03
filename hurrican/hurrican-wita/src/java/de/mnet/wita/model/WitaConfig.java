/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2011 07:37:03
 */
package de.mnet.wita.model;

import javax.persistence.*;

import de.mnet.wita.message.GeschaeftsfallTyp;

/**
 * Tabelle fuer die Konfiguration der WITA- und WBCI-Schnittstelle
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WITA_CONFIG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WITA_CONFIG_0", allocationSize = 1)
public class WitaConfig extends AbstractWitaModel {

    private static final long serialVersionUID = 644643830096352818L;

    // Keys
    public static final String DAYS_BEFORE_SENT = "wita.count.of.days.before.sent";
    public static final String MINUTES_WHILE_REQUESTS_ON_HOLD = "wita.count.of.minutes.request.onhold";
    public static final String DEFAULT_WITA_VERSION = "default.wita.version";
    public static final String DEFAULT_WBCI_CDM_VERSION = "default.wbci.cdm.version.%s";
    public static final String WBCI_MINUTES_WHILE_REQUESTS_ON_HOLD = "wbci.count.of.minutes.request.onhold";
    public static final String WBCI_RUEMVA_PORTING_DATE_DIFFERENCE = "wbci.ruemva.porting.date.difference";
    public static final String WBCI_TV_FRIST_EINGEHEND = "wbci.tv.frist.eingehend";
    public static final String WBCI_STORNO_FRIST_EINGEHEND = "wbci.storno.frist.eingehend";
    public static final String WBCI_WITA_KUENDIGUNG_OFFSET = "wbci.wita.kuendigung.offset";
    public static final String WBCI_REQUESTED_AND_CONFIRMED_DATE_OFFSET = "wbci.requested.and.confirmed.date.offset";

    // Values
    public static final String ACTIVE = "active";
    public static final String PASSIVE = "passive";

    // Other
    private static final String SEPARATOR = ".";

    public static String daysBeforeSentFor(GeschaeftsfallTyp geschaeftsfallTyp) {
        return DAYS_BEFORE_SENT + SEPARATOR + geschaeftsfallTyp.name();
    }

    private String key;
    private String value;

    public WitaConfig() {
        // just for hibernate
    }

    public WitaConfig(String key, String value) {
        setKey(key);
        setValue(value);
    }

    @Column(name = "CONFIG_KEY")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Column(name = "CONFIG_VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Transient
    public boolean isActive() {
        return WitaConfig.ACTIVE.equals(value);
    }

    @Transient
    public boolean isPassive() {
        return WitaConfig.PASSIVE.equals(value);
    }
}
