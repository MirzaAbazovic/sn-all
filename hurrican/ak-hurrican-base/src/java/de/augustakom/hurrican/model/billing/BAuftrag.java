/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 10:36:36
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Bildet einen Auftrag aus dem Billing-System ab.
 *
 *
 */
public class BAuftrag extends AbstractHistoryModel implements KundenModel {

    private static final long serialVersionUID = -6646795258647847787L;

    public static final String AUFTRAG_NO_ORIG = "auftragNoOrig";

    /**
     * Wert fuer 'astatus' markiert den Auftrag als 'eingegangen'
     */
    public static final Integer STATUS_EINGEGANGEN = Integer.valueOf(1);
    /**
     * Wert fuer 'astatus' markiert den Auftrag als 'erfasst'
     */
    public static final Integer STATUS_ERFASST = Integer.valueOf(2);
    /**
     * Wert fuer 'astatus' markiert den Auftrag als 'freigegeben'
     */
    public static final Integer STATUS_FREIGEGEBEN = Integer.valueOf(5);
    /**
     * Wert fuer 'astatus' markiert den Auftrag als 'storniert'
     */
    public static final Integer STATUS_STORNIERT = Integer.valueOf(7);
    /**
     * Wert fuer 'astatus' markiert den Auftrag als 'gekuendigt'
     */
    public static final Integer STATUS_GEKUENDIGT = Integer.valueOf(8);
    /**
     * Wert fuer 'astatus' markiert den Auftrag als 'freigabebereit'
     */
    public static final Integer STATUS_FREIGABEBEREIT = Integer.valueOf(9);

    private Long auftragNo = null;
    private Long auftragNoOrig = null;
    private String oldAuftragNoOrig = null;
    private String sapId = null;
    private String atyp = null;
    private Integer astatus = null;
    private Long kundeNo = null;
    private Long oeNoOrig = null;
    private Date wunschTermin = null;
    private Date vertragsdatum = null;
    private String user = null;
    private Integer bundleOrderNo = null;
    private Long rechInfoNoOrig = null;
    private Long apAddressNo = null;
    private Long haendlerNo = null;
    private Long vertragsLaufzeit = null;
    private Date eingangsdatum = null;
    private Date tatsaechlicherTermin = null;
    private Date ausfuehrZeit = null;
    private String bearbeiterKundeName;
    private String bearbeiterKundeRN = null;
    private String bearbeiterKundeFax;
    private String bearbeiterKundeEmail = null;
    private String bestellId = null;
    private Date kuendigungsdatum = null;
    private Date vertragsendedatum = null;
    private Long hauptAuftragNo = null;
    private Date aktuellesVertragsende = null;

    /**
     * Ueberprueft, ob es sich bei dem Auftrag um eine Kuendigung handelt. <br> Dies ist dann der Fall, wenn 'atyp' den
     * Wert 'KUEND' besitzt.
     *
     * @return
     *
     */
    public boolean isKuendigung() {
        return StringUtils.equals(getAtyp(), BillingConstants.ATYP_KUEND);
    }

    /**
     * @return Returns the aStatus.
     */
    public Integer getAstatus() {
        return astatus;
    }

    /**
     * @param status The aStatus to set.
     */
    public void setAstatus(Integer status) {
        astatus = status;
    }

    /**
     * @return Returns the aTyp.
     */
    public String getAtyp() {
        return atyp;
    }

    /**
     * @param typ The aTyp to set.
     */
    public void setAtyp(String typ) {
        atyp = typ;
    }

    /**
     * @return Returns the auftragNo.
     */
    public Long getAuftragNo() {
        return auftragNo;
    }

    /**
     * @param auftragNo The auftragNo to set.
     */
    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the oldAuftragNoOrig.
     */
    public String getOldAuftragNoOrig() {
        return this.oldAuftragNoOrig;
    }

    /**
     * @param oldAuftragNoOrig The oldAuftragNoOrig to set.
     */
    public void setOldAuftragNoOrig(String oldAuftragNoOrig) {
        this.oldAuftragNoOrig = oldAuftragNoOrig;
    }

    /**
     * @return Returns the kundeNo.
     */
    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the orderEntryNoOrig.
     */
    public Long getOeNoOrig() {
        return oeNoOrig;
    }

    /**
     * @param orderEntryNoOrig The orderEntryNoOrig to set.
     */
    public void setOeNoOrig(Long orderEntryNoOrig) {
        this.oeNoOrig = orderEntryNoOrig;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user The user to set.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return Returns the wunschTermin.
     */
    public Date getWunschTermin() {
        return wunschTermin;
    }

    /**
     * @param wunschTermin The wunschTermin to set.
     */
    public void setWunschTermin(Date wunschTermin) {
        this.wunschTermin = wunschTermin;
    }

    /**
     * @return Returns the kuendigungsdatum.
     */
    public Date getKuendigungsdatum() {
        return kuendigungsdatum;
    }

    /**
     * @param kuendigungsdatum The kuendigungsdatum to set.
     */
    public void setKuendigungsdatum(Date kuendigungsdatum) {
        this.kuendigungsdatum = kuendigungsdatum;
    }

    public Date getVertragsendedatum() {
        return vertragsendedatum;
    }

    public void setVertragsendedatum(Date vertragsendedatum) {
        this.vertragsendedatum = vertragsendedatum;
    }

    /**
     * @return Returns the bundleOrderNo.
     */
    public Integer getBundleOrderNo() {
        return bundleOrderNo;
    }

    /**
     * @param bundleOrderNo The bundleOrderNo to set.
     */
    public void setBundleOrderNo(Integer bundleOrderNo) {
        this.bundleOrderNo = bundleOrderNo;
    }

    /**
     * @return Returns the vertragsdatum.
     */
    public Date getVertragsdatum() {
        return vertragsdatum;
    }

    /**
     * @param vertragsdatum The vertragsdatum to set.
     */
    public void setVertragsdatum(Date vertragsdatum) {
        this.vertragsdatum = vertragsdatum;
    }

    /**
     * @return Returns the rechInfoNoOrig.
     */
    public Long getRechInfoNoOrig() {
        return rechInfoNoOrig;
    }

    /**
     * @param rechInfoNoOrig The rechInfoNoOrig to set.
     */
    public void setRechInfoNoOrig(Long rechInfoNoOrig) {
        this.rechInfoNoOrig = rechInfoNoOrig;
    }

    /**
     * @return Returns the sapId.
     */
    public String getSapId() {
        return this.sapId;
    }

    /**
     * @param sapId The sapId to set.
     */
    public void setSapId(String sapId) {
        this.sapId = sapId;
    }

    /**
     * @return apAddressNo
     */
    public Long getApAddressNo() {
        return apAddressNo;
    }

    /**
     * @param apAddressNo Festzulegender apAddressNo
     */
    public void setApAddressNo(Long apAddressNo) {
        this.apAddressNo = apAddressNo;
    }

    /**
     * @return haendlerNo
     */
    public Long getHaendlerNo() {
        return haendlerNo;
    }

    /**
     * @param haendlerNo Festzulegender haendlerNo
     */
    public void setHaendlerNo(Long haendlerNo) {
        this.haendlerNo = haendlerNo;
    }

    /**
     * @return vertragsLaufzeit
     */
    public Long getVertragsLaufzeit() {
        return vertragsLaufzeit;
    }

    /**
     * @param vertragsLaufzeit Festzulegender vertragsLaufzeit
     */
    public void setVertragsLaufzeit(Long vertragsLaufzeit) {
        this.vertragsLaufzeit = vertragsLaufzeit;
    }

    /**
     * @return the eingangsdatum
     */
    public Date getEingangsdatum() {
        return eingangsdatum;
    }

    /**
     * @param eingangsdatum the eingangsdatum to set
     */
    public void setEingangsdatum(Date eingangsdatum) {
        this.eingangsdatum = eingangsdatum;
    }

    /**
     * @return the tatsaechlicherTermin
     */
    public Date getTatsaechlicherTermin() {
        return tatsaechlicherTermin;
    }

    /**
     * @param tatsaechlicherTermin the tatsaechlicherTermin to set
     */
    public void setTatsaechlicherTermin(Date tatsaechlicherTermin) {
        this.tatsaechlicherTermin = tatsaechlicherTermin;
    }

    /**
     * @return the ausfuehrZeit
     */
    public Date getAusfuehrZeit() {
        return ausfuehrZeit;
    }

    /**
     * @param ausfuehrZeit the ausfuehrZeit to set
     */
    public void setAusfuehrZeit(Date ausfuehrZeit) {
        this.ausfuehrZeit = ausfuehrZeit;
    }

    public String getBearbeiterKundeName() {
        return bearbeiterKundeName;
    }

    public void setBearbeiterKundeName(String bearbeiterKundeName) {
        this.bearbeiterKundeName = bearbeiterKundeName;
    }

    /**
     * @return the bearbeiterKundeRN
     */
    public String getBearbeiterKundeRN() {
        return bearbeiterKundeRN;
    }


    /**
     * @param bearbeiterKundeRN the bearbeiterKundeRN to set
     */
    public void setBearbeiterKundeRN(String bearbeiterKundeRN) {
        this.bearbeiterKundeRN = bearbeiterKundeRN;
    }

    public String getBearbeiterKundeFax() {
        return bearbeiterKundeFax;
    }

    public void setBearbeiterKundeFax(String bearbeiterKundeFax) {
        this.bearbeiterKundeFax = bearbeiterKundeFax;
    }

    /**
     * @return the bearbeiterKundeEmail
     */
    public String getBearbeiterKundeEmail() {
        return bearbeiterKundeEmail;
    }


    /**
     * @param bearbeiterKundeEmail the bearbeiterKundeEmail to set
     */
    public void setBearbeiterKundeEmail(String bearbeiterKundeEmail) {
        this.bearbeiterKundeEmail = bearbeiterKundeEmail;
    }


    /**
     * @return the bestellId
     */
    public String getBestellId() {
        return bestellId;
    }


    /**
     * @param bestellId the bestellId to set
     */
    public void setBestellId(String bestellId) {
        this.bestellId = bestellId;
    }

    public Long getHauptAuftragNo() {
        return hauptAuftragNo;
    }

    public void setHauptAuftragNo(Long hauptAuftragNo) {
        this.hauptAuftragNo = hauptAuftragNo;
    }

    /**
     * @return aktuelles Vertragsende
     */
    public Date getAktuellesVertragsende() {
        return aktuellesVertragsende;
    }

    /**
     * @param aktuellesVertragsende aktuelles Vertragsende
     */
    public void setAktuellesVertragsende(Date aktuellesVertragsende) {
        this.aktuellesVertragsende = aktuellesVertragsende;
    }
}
