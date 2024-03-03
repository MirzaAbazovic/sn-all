/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 11:07:45
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * Abbildung eines Kunden aus dem Billing-System.
 */
public class Kunde extends AbstractBillingModel implements KundenModel, DebugModel {

    private static final long serialVersionUID = 3115015532761040001L;

    /**
     * Wert fuer <code>kundenTyp</code> kennzeichnet einen Geschaeftskunden.
     */
    public static final String CUSTOMER_TYPE_BUSINESS = "GEWERBLICH";

    /**
     * Wert fuer <code>brancheNo</code> kennzeichnet die M-net.
     */
    public static final Integer BRANCHE_NO_MNET = 99;

    /**
     * Reseller-Kundennummern, die mit diesem Wert beginnen, zeigen an, dass der Kunde ueber die NL Kempten betreut
     * wird.
     */
    public static final String RESELLER_START_4_KEMPTEN = "4";

    /**
     * Kundennummer fuer den Reseller AugustaKom.
     */
    public static final Long RESELLER_KUNDE_NO_AKOM = 100000009L;
    /**
     * Kundennummer fuer den Reseller AllgaeuKom.
     */
    public static final Long RESELLER_KUNDE_NO_ALLGAEUKOM = 400000001L;
    /**
     * Kundennummer fuer den IC-Reseller AugustaKom.
     */
    public static final Long RESELLER_KUNDE_NO_IC_AGB = 500101538L;
    /**
     * Kundennummer fuer den Reseller M-net.
     */
    public static final Long RESELLER_KUNDE_NO_MNET = 100000081L;
    /**
     * Kundennummer fuer den Reseller Nefkom.
     */
    public static final Long RESELLER_KUNDE_NO_NEFKOM = 500000010L;
    /**
     * Kundennummer fuer den Reseller Herzo-Media.
     */
    public static final Long RESELLER_KUNDE_NO_HZM = 500000020L;

    private Long kundeNo = null;
    private Long resellerKundeNo = null;
    private Long areaNo = null;
    private Integer brancheNo = null;
    private String name = null;
    private String vorname = null;
    private String hauptRufnummer = null;
    private String rnGeschaeft = null;
    private String rnPrivat = null;
    private String rnMobile = null;
    private String rnVoip = null;
    private String rnFax = null;
    private String email = null;
    private String kundenTyp = null;
    private Long hauptKundenNo = null;
    private Long kundenbetreuerNo = null;
    private String vip = null;
    private Boolean hauptkunde = null;
    private Boolean kunde = null;
    private Boolean fernkatastrophe = null;
    private Long postalAddrNo = null;
    private Date validFrom = null;
    private Date createdAt = null;
    private Date lockDate = null;

    /**
     * Gibt an, ob der Kunde nach §95 TKG gesperrt ist. Es handelt sich hier NICHT um eine technische Sperrung der
     * Auftraege! Bei aktivierter Sperre duerfen die Daten des Kunden (und dessen Auftraege) im System nicht mehr mit
     * angezeigt werden!
     *
     * @return {@code true} bei einer aktiven Sperre des Kunden
     */
    public boolean isLocked() {
        return getLockDate() != null;
    }

    /**
     * Erstellt einen String aus Name u. Vorname des Kunden.
     */
    public String getNameVorname() {
        return StringTools.join(new String[] { getName(), getVorname() }, " ", true);
    }

    /**
     * Ueberprueft, ob es sich bei dem Reseller um die M-net handelt.
     */
    public static boolean isResellerMnet(Long resellerKundeNo) {
        return RESELLER_KUNDE_NO_MNET.equals(resellerKundeNo);
    }

    public boolean isResellerMnet() {
        return isResellerMnet(getResellerKundeNo());
    }

    /**
     * Ueberprueft, ob es sich bei dem Reseller des Kunden um die Allgäukom handelt.
     */
    public boolean isResellerAllgaeukom() {
        return RESELLER_KUNDE_NO_ALLGAEUKOM.equals(getResellerKundeNo());
    }

    /**
     * Ueberprueft, ob es sich bei dem Reseller des Kunden um die Augustakom handelt.
     */
    public boolean isResellerAkom() {
        return RESELLER_KUNDE_NO_AKOM.equals(getResellerKundeNo());
    }

    /**
     * Ueberprueft, ob es sich bei dem Reseller des Kunden um die NEFKom handelt.
     */
    public boolean isResellerNef() {
        return RESELLER_KUNDE_NO_NEFKOM.equals(getResellerKundeNo());
    }

    /**
     * Ueberprueft, ob es sich bei dem Reseller des Kunden um Herzo-Media handelt.
     */
    public boolean isResellerHzm() {
        return RESELLER_KUNDE_NO_HZM.equals(getResellerKundeNo());
    }

    /**
     * Ueberprueft, ob es sich bei dem Kunden um einen Geschaefts- od. Privatkunden handelt.
     *
     * @return true wenn es ein Geschaeftskunde ist.
     */
    public boolean isBusinessCustomer() {
        return StringUtils.equals(StringUtils.trimToEmpty(getKundenTyp()), CUSTOMER_TYPE_BUSINESS);
    }

    public Long getAreaNo() {
        return this.areaNo;
    }

    public void setAreaNo(Long areaNo) {
        this.areaNo = areaNo;
    }

    public Integer getBrancheNo() {
        return brancheNo;
    }

    public void setBrancheNo(Integer brancheNo) {
        this.brancheNo = brancheNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getHauptkunde() {
        return hauptkunde;
    }

    public boolean isHauptkunde() {
        return (hauptkunde != null) && hauptkunde;
    }

    public void setHauptkunde(Boolean hauptkunde) {
        this.hauptkunde = hauptkunde;
    }

    public Long getHauptKundenNo() {
        return hauptKundenNo;
    }

    public void setHauptKundenNo(Long hauptKundenNo) {
        this.hauptKundenNo = hauptKundenNo;
    }

    public String getHauptRufnummer() {
        return hauptRufnummer;
    }

    public void setHauptRufnummer(String hauptRufnummer) {
        this.hauptRufnummer = hauptRufnummer;
    }

    public Boolean getKunde() {
        return kunde;
    }

    public void setKunde(Boolean kunde) {
        this.kunde = kunde;
    }

    public Long getKundenbetreuerNo() {
        return kundenbetreuerNo;
    }

    public void setKundenbetreuerNo(Long kundenbetreuerNo) {
        this.kundenbetreuerNo = kundenbetreuerNo;
    }

    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public String getKundenTyp() {
        return kundenTyp;
    }

    public void setKundenTyp(String kundenTyp) {
        this.kundenTyp = kundenTyp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getResellerKundeNo() {
        return resellerKundeNo;
    }

    public void setResellerKundeNo(Long resellerKundeNo) {
        this.resellerKundeNo = resellerKundeNo;
    }

    public String getRnFax() {
        return rnFax;
    }

    public void setRnFax(String rnFax) {
        this.rnFax = rnFax;
    }

    public String getRnGeschaeft() {
        return rnGeschaeft;
    }

    public void setRnGeschaeft(String rnGeschaeft) {
        this.rnGeschaeft = rnGeschaeft;
    }

    public String getRnMobile() {
        return rnMobile;
    }

    public void setRnMobile(String rnMobile) {
        this.rnMobile = rnMobile;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public Boolean getFernkatastrophe() {
        return fernkatastrophe;
    }

    public boolean isFernkatastrophe() {
        return (fernkatastrophe != null) && fernkatastrophe;
    }

    public void setFernkatastrophe(Boolean fernkatastrophe) {
        this.fernkatastrophe = fernkatastrophe;
    }

    public Long getPostalAddrNo() {
        return postalAddrNo;
    }

    public void setPostalAddrNo(Long postalAddrNo) {
        this.postalAddrNo = postalAddrNo;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLockDate() {
        return lockDate;
    }

    public void setLockDate(Date lockDate) {
        this.lockDate = lockDate;
    }

    public String getRnPrivat() {
        return rnPrivat;
    }

    public void setRnPrivat(String rnPrivat) {
        this.rnPrivat = rnPrivat;
    }

    public String getRnVoip() {
        return rnVoip;
    }

    public void setRnVoip(String rnVoip) {
        this.rnVoip = rnVoip;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Properties zu " + Kunde.class.getName());
            logger.debug("  No         : " + getKundeNo());
            logger.debug("  Haupt-No   : " + getHauptKundenNo());
            logger.debug("  Name       : " + getName());
            logger.debug("  Vorname    : " + getVorname());
            logger.debug("  Valid-From : " + getValidFrom());
        }
    }

}
