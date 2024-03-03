/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2004 07:53:34
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell bildet eine ProduktGruppe ab. <br> (In einer ProduktGruppe koennen mehrere Produkte zusammen gefasst werden.)
 *
 *
 */
public class ProduktGruppe extends AbstractCCIDModel {

    /**
     * ID fuer die Produkt-Gruppe 'Phone'.
     */
    public static final Long AK_PHONE = Long.valueOf(1);
    /**
     * ID fuer die Produkt-Gruppe 'Connect'.
     */
    public static final Long AK_CONNECT = Long.valueOf(2);
    /**
     * ID fuer die Produkt-Gruppe 'Connect-Intern'.
     */
    public static final Long AK_CONNECT_INTERN = Long.valueOf(9);
    /**
     * ID fuer die Produkt-Gruppe 'SDSL'.
     */
    public static final Long AK_SDSL = Long.valueOf(4);
    /**
     * ID fuer die Produkt-Gruppe 'Online'.
     */
    public static final Long AK_ONLINE = Long.valueOf(5);
    /**
     * ID fuer die Produkt-Gruppe 'ADSL'.
     */
    public static final Long AK_ADSL = Long.valueOf(3);
    /**
     * ID fuer die Produkt-Gruppe 'ADSLplus'.
     */
    public static final Long AK_DSLPLUS = Long.valueOf(16);
    public static final Long MAXI = Long.valueOf(17);
    /**
     * ID fuer die Produkt-Gruppe 'PremiumCall'
     */
    public static final Long PREMIUM_CALL = Long.valueOf(18);
    /**
     * ID fuer die Produkt-Gruppe 'TV'.
     */
    public static final Long TV = Long.valueOf(20);
    /**
     * ID fuer die Produkt-Gruppe 'Maxi Deluxe'.
     */
    public static final Long MAXI_DELUXE = Long.valueOf(21);
    /**
     * ID fuer die Produkt-Gruppe 'interne Arbeiten'.
     */
    public static final Long AK_INTERN_WORK = Long.valueOf(22);
    /**
     * ID fuer die Produkt-Gruppe 'IPSec'
     */
    public static final Long IPSEC = Long.valueOf(23);
    /**
     * ID fuer die Produkt-Gruppe 'Housing'
     */
    public static final Long HOUSING = Long.valueOf(24);
    /**
     * ID fuer die Produkt-Gruppe 'SIP InterTrunk'
     */
    public static final Long SIP_INTER_TRUNK = Long.valueOf(25);
    /**
     * ID fuer die Produkt-Gruppe 'SIP InterTrunk Endkunde'
     */
    public static final Long SIP_INTER_TRUNK_ENDKUNDE = Long.valueOf(26);
    /**
     * ID fuer die Produkt-Gruppe 'MVS Enterprise'
     */
    public static final Long MVS_ENTERPRISE = Long.valueOf(27);
    /**
     * ID fuer die Produkt-Gruppe 'MVS Site'
     */
    public static final Long MVS_SITE = Long.valueOf(28);
    /**
     * ID fuer die Produkt-Gruppe 'FTTX Phone'
     */
    public static final Long FTTX_PHONE = Long.valueOf(29);
    /**
     * ID fuer die Produkt-Gruppe 'Wholesale'
     */
    public static final Long WHOLESALE = Long.valueOf(32);
    /**
     * ID fuer die Produkt-Gruppe 'Glasfaser GK'
     */
    public static final Long GLASFASER_GK = Long.valueOf(33);
    /**
     * ID fuer die Produkt-Gruppe 'nicht zuordenbar'
     */
    public static final Long NICHT_ZUORDENBAR = Long.valueOf(15);


    public static final Long[] PRODUKTGRUPPEN_2_CREATE_RANG_MATRIX =
            new Long[]{MAXI_DELUXE, GLASFASER_GK, WHOLESALE, FTTX_PHONE};


    private String produktGruppe;
    private String realm;

    public String getProduktGruppe() {
        return produktGruppe;
    }

    public void setProduktGruppe(String produktGruppe) {
        this.produktGruppe = produktGruppe;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }
}


