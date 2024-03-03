/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2004 15:42:27
 */
package de.augustakom.hurrican.model.cc;

import java.text.*;
import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;


/**
 * Modell bildet eine Carrier-Bestellung (eine bestellte Leitung) ab.
 */
@Entity
@Table(name = "T_CARRIERBESTELLUNG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_CARRIERBESTELLUNG_0", allocationSize = 1)
@AttributeOverride(name = "id", column = @Column(name = "CB_ID"))
public class Carrierbestellung extends AbstractCCIDModel {

    private static final long serialVersionUID = 8154406255368959252L;

    /**
     * Wert fuer <code>negativeRm</code>, wenn die Carrierbestellung wieder angezeigt werden soll.
     */
    public static final String NEGATIVE_RM_WIEDERVORLAGE = "Wiedervorlage";
    /**
     * Wert fuer <code>negativeRm</code>, wenn die Carrierbestellung noch zu klaeren ist.
     */
    public static final String NEGATIVE_RM_KLAERFALL = "Klärfall";
    /**
     * Wert fuer <code>negativeRm</code>, wenn eine NEUE Carrierbestellung erstellt wird.
     */
    public static final String NEGATIVE_RM_NEUBESTELLUNG = "neue CB";

    /**
     * Moegliche Werte fuer <code>negativeRm</code>
     */
    public static final String[] NEGATIVE_RMS = new String[] { NEGATIVE_RM_NEUBESTELLUNG,
            NEGATIVE_RM_WIEDERVORLAGE, NEGATIVE_RM_KLAERFALL };

    /**
     * Prefix fuer eine niederbitratige Kennung des Carriers DTAG.
     */
    public static final String DTAG_PREFIX_NIEDERBIT = "N";
    /**
     * Prefix fuer eine hochbitratige Kennung des Carriers DTAG.
     */
    public static final String DTAG_PREFIX_HOCHBIT = "H";

    private Long cb2EsId;
    private Long carrier;
    private Date vorgabedatum;
    private Date bestelltAm;
    private Date zurueckAm;
    private Date bereitstellungAm;
    private Boolean kundeVorOrt;
    private String lbz;
    private String vtrNr;
    private String aqs;
    private String ll;
    private String negativeRm;
    private Date wiedervorlage;
    private Date kuendigungAnCarrier;
    private Date kuendBestaetigungCarrier;
    private Long auftragId4TalNA;
    private Long talNATyp;
    private Long aiAddressId;
    private String maxBruttoBitrate;
    private Long eqOutId;
    private CarrierbestellungVormieter carrierbestellungVormieter;
    private TalRealisierungsZeitfenster talRealisierungsZeitfenster;

    /**
     * Prueft, ob auf der Carrierbestellung keine sinnvollen Daten vorhanden sind. Wenn keine sinnvollen Daten vorhanden
     * sind, ist das Loeschen der Carrierbestellung moeglich.
     */
    @Transient
    public boolean isLeereCarrierbestellung() {
        return allFieldsNull(vorgabedatum, bestelltAm, zurueckAm, bereitstellungAm, kundeVorOrt, lbz, vtrNr, aqs, ll,
                negativeRm, wiedervorlage, kuendigungAnCarrier, kuendBestaetigungCarrier, maxBruttoBitrate);
    }

    @Transient
    public boolean allFieldsNull(Object... fields) {
        for (Object field : fields) {
            if (field != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ueberprueft, ob es sich bei der Carrierbestellung um eine 96X Bestellung handelt.
     */
    @Transient
    public boolean is96X() {
        return StringUtils.startsWithIgnoreCase(getLbz(), "96X");
    }

    @Column(name = "CB_2_ES_ID")
    public Long getCb2EsId() {
        return cb2EsId;
    }

    public void setCb2EsId(Long cb2EsId) {
        this.cb2EsId = cb2EsId;
    }

    @Column(name = "AQS")
    public String getAqs() {
        return aqs;
    }

    public void setAqs(String aqs) {
        this.aqs = aqs;
    }

    @Column(name = "BEREITSTELLUNG_AM")
    public Date getBereitstellungAm() {
        return bereitstellungAm;
    }

    public void setBereitstellungAm(Date bereitstellungAm) {
        this.bereitstellungAm = bereitstellungAm;
    }

    @Column(name = "BESTELLT_AM")
    public Date getBestelltAm() {
        return bestelltAm;
    }

    public void setBestelltAm(Date bestelltAm) {
        this.bestelltAm = bestelltAm;
    }

    @Column(name = "CARRIER_ID")
    public Long getCarrier() {
        return carrier;
    }

    public void setCarrier(Long carrier) {
        this.carrier = carrier;
    }

    @Column(name = "KUENDBESTAETIGUNG_CARRIER")
    public Date getKuendBestaetigungCarrier() {
        return kuendBestaetigungCarrier;
    }

    public void setKuendBestaetigungCarrier(Date kuendBestaetigungDTAG) {
        this.kuendBestaetigungCarrier = kuendBestaetigungDTAG;
    }

    @Column(name = "KUENDIGUNG_AN_CARRIER")
    public Date getKuendigungAnCarrier() {
        return kuendigungAnCarrier;
    }

    public void setKuendigungAnCarrier(Date kuendigungAnCarrier) {
        this.kuendigungAnCarrier = kuendigungAnCarrier;
    }

    @Column(name = "LBZ")
    public String getLbz() {
        return lbz;
    }

    public void setLbz(String lbz) {
        this.lbz = lbz;
    }

    @Column(name = "LL")
    public String getLl() {
        return ll;
    }

    public void setLl(String ll) {
        this.ll = ll;
    }

    @Column(name = "NEGATIVERM")
    public String getNegativeRm() {
        return negativeRm;
    }

    public void setNegativeRm(String negativeRm) {
        this.negativeRm = negativeRm;
    }

    @Column(name = "VTRNR")
    public String getVtrNr() {
        return vtrNr;
    }

    public void setVtrNr(String vtrNr) {
        this.vtrNr = vtrNr;
    }

    @Column(name = "WIEDERVORLAGE")
    public Date getWiedervorlage() {
        return wiedervorlage;
    }

    public void setWiedervorlage(Date wiedervorlage) {
        this.wiedervorlage = wiedervorlage;
    }

    @Column(name = "ZURUECK_AM")
    public Date getZurueckAm() {
        return zurueckAm;
    }

    public void setZurueckAm(Date zurueckAm) {
        this.zurueckAm = zurueckAm;
    }

    /**
     * Gibt die Auftrags-ID des urspruenglichen Auftrags der Carrier-Bestellung zurueck.
     *
     * @return Returns the auftragId4TalNA.
     */
    @Column(name = "AUFTRAG_ID_4_TAL_NA")
    public Long getAuftragId4TalNA() {
        return auftragId4TalNA;
    }

    /**
     * Bei Durchfuehrung einer TAL-Nutzungsaenderung wird der 'neuen' Carrierbestellung die Auftrags-ID des
     * urspruenglichen Auftrags uebergeben, auf dem die TAL bestellt wurde.
     *
     * @param auftragId4TalNA The auftragId4TalNA to set.
     */
    public void setAuftragId4TalNA(Long auftragId4TalNA) {
        this.auftragId4TalNA = auftragId4TalNA;
    }

    /**
     * @return Returns the talNATyp.
     * @deprecated Ist/war fuer ESAA notwendig. Ist bei Umstellung auf WITA ueber eigenen UseCase abgedeckt.
     * TAL-Nutzungsaenderungen muessen in Hurrican entsprechend angepasst werden!
     */
    @Deprecated
    @Column(name = "TAL_NA_TYP")
    public Long getTalNATyp() {
        return talNATyp;
    }

    public void setTalNATyp(Long talNATyp) {
        this.talNATyp = talNATyp;
    }

    /**
     * Flag gibt an, ob der Kunde am Schaltungstermin vor Ort sein muss. <br> Die Vorgabe erfolgt ueber den Carrier, bei
     * dem die Bestellung ausgeloest wird.
     *
     * @return Returns the kundeVorOrt.
     */
    @Column(name = "KUNDE_VOR_ORT")
    public Boolean getKundeVorOrt() {
        return this.kundeVorOrt;
    }

    public void setKundeVorOrt(Boolean kundeVorOrt) {
        this.kundeVorOrt = kundeVorOrt;
    }

    /**
     * Gibt die ID der Anschlussinhaber-Adresse zurueck.
     *
     * @return Returns the aiAddressId.
     */
    @Column(name = "AI_ADDRESS_ID")
    public Long getAiAddressId() {
        return aiAddressId;
    }

    public void setAiAddressId(Long aiAddressId) {
        this.aiAddressId = aiAddressId;
    }

    @Column(name = "VORGABEDATUM")
    public Date getVorgabedatum() {
        return vorgabedatum;
    }

    public void setVorgabedatum(Date vorgabedatum) {
        this.vorgabedatum = vorgabedatum;
    }

    @Column(name = "MAX_BRUTTO_BITRATE")
    public String getMaxBruttoBitrate() {
        return maxBruttoBitrate;
    }

    public void setMaxBruttoBitrate(String maxBruttoBitrate) {
        this.maxBruttoBitrate = maxBruttoBitrate;
    }

    /**
     * Gibt die ID des EQ-OUT Ports an, auf dem die Carrierbestellung ausgefuehrt wird/wurde.
     *
     * @return Returns the eqOutId.
     */
    @Column(name = "EQ_OUT_ID")
    public Long getEqOutId() {
        return eqOutId;
    }

    /**
     * Angabe der ID des EQ-OUT Ports, der fuer die Carrierbestellung relevant ist.
     *
     * @param eqOutId The eqOutId to set.
     */
    public void setEqOutId(Long eqOutId) {
        this.eqOutId = eqOutId;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "CARRIERBESTELLUNG_VORMIETER_ID", nullable = true)
    public CarrierbestellungVormieter getCarrierbestellungVormieter() {
        return carrierbestellungVormieter;
    }

    public void setCarrierbestellungVormieter(CarrierbestellungVormieter carrierbestellungVormieter) {
        this.carrierbestellungVormieter = carrierbestellungVormieter;
    }

    @Column(name = "TAL_REAL_TIMESLOT")
    @Enumerated(EnumType.STRING)
    public TalRealisierungsZeitfenster getTalRealisierungsZeitfenster() {
        return talRealisierungsZeitfenster;
    }

    public void setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster talRealisierungsZeitfenster) {
        this.talRealisierungsZeitfenster = talRealisierungsZeitfenster;
    }

    @Transient
    public Integer calcLlSum() {
        if (getLl() == null) {
            return null;
        }
        int sumLl = 0;
        List<String> parts = Arrays.asList(getLl().split("m?[/\\s'*^&_(]+"));
        for (String num : parts) {
            if (StringUtils.isNotBlank(num)) {
                num = num.replace('.', ',');
                try {
                    Number parsed =
                            NumberFormat.getNumberInstance(Locale.GERMANY).parse(num.trim());
                    Double numDouble = parsed.doubleValue();
                    if (num.contains(",")) {
                        numDouble = numDouble * 1000;
                    }
                    sumLl += numDouble.intValue();
                }
                catch (ParseException e) {
                    continue;
                }
            }
        }
        return sumLl;
    }
}
