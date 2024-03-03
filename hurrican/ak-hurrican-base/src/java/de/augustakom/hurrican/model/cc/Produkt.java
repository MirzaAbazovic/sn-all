/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 09:37:23
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.iface.CCProduktModel;

/**
 * Modell bildet ein technisches Produkt ab.
 *
 *
 */
public class Produkt extends AbstractCCHistoryModel implements CCProduktModel {

    /**
     * Prueft, ob die angegebene Produkt-Id eine SDSL n-Draht Option ist.
     * @param prodId
     * @return
     */
    public static boolean isSdslNDraht(Long prodId) {
        return NumberTools.isIn(prodId, new Number[]{PROD_ID_SDSL_N_DRAHT_OPTION, PROD_ID_SDSL_N_DRAHT_FTTC_OPTION});
    }

    /**
     * Produkt-ID fuer das Produkt 'AK-Connect'
     */
    public static final Long AK_CONNECT = 6L;
    /**
     * Produkt-ID fuer das Produkt 'AK-SDS 256'.
     */
    public static final Long PROD_ID_AKSDSL_256 = 11L;
    /**
     * Produkt-ID fuer das Produkt 'AK-SDSL 1024'.
     */
    public static final Long PROD_ID_AKSDSL_1024 = 16L;
    /**
     * Produkt-ID fuer das Produkt 'AK-SDSL 512'.
     */
    public static final Long PROD_ID_AKSDSL_512 = 17L;
    /**
     * Produkt-ID fuer das Produkt 'AK-SDSL 2300'.
     */
    public static final Long PROD_ID_AKSDSL_2300 = 18L;
    /**
     * Produkt-ID fuer das Produkt 'SDSL 1024'.
     */
    public static final Long PROD_ID_SDSL_1024 = 62L;
    /**
     * Produkt-ID fuer das Produkt 'SDSL 10000'.
     */
    public static final Long PROD_ID_SDSL_10000 = 67L;
    /**
     * Produkt-ID fuer das Produkt 'AK-phone appartement'.
     */
    public static final Long PROD_ID_APPARTEMENT = 101L;
    /**
     * Produkt-ID fuer das Produkt 'interne Arbeit'
     */
    public static final Long PROD_ID_INTERNE_ARBEIT = 110L;
    /**
     * Produkt-ID fuer das Produkt 'AK-email'
     */
    public static final Long PROD_ID_EMAIL = 310L;
    /**
     * Produkt-ID fuer das Produkt 'Maxi DSL Analog'
     */
    public static final Long PROD_ID_MAXI_DSL_ANALOG = 328L;
    /**
     * Produkt-ID fuer das Produkt 'ISDN MSN'
     */
    public static final Long PROD_ID_ISDN_MSN = 336L;
    /**
     * Produkt-ID fuer das Produkt 'ISDN TK'
     */
    public static final Long PROD_ID_ISDN_TK = 337L;
    /**
     * Produkt-ID fuer das Produkt 'ISDN PMX'
     */
    public static final Long PROD_ID_ISDN_PMX = 338L;
    /**
     * Produkt-ID fuer das Produkt 'Maxi DSL + IDSN'
     */
    public static final Long PROD_ID_MAXI_DSL_UND_ISDN = 420L;
    /**
     * Produkt-ID fuer das Produkt 'Premium DSL + analog'
     */
    public static final Long PROD_ID_PREMIUM_DSL_ANALOG = 431L;
    /**
     * Produkt-ID fuer das Produkt 'Premium DSL + ISDN'
     */
    public static final Long PROD_ID_PREMIUM_DSL_ISDN = 430L;
    /**
     * Produkt-ID fuer das Produkt 'M-Net Premium DSL + analog'
     */
    public static final Long PROD_ID_MNET_PREMIUM_DSL_ANALOG = 445L;
    /**
     * Produkt-ID fuer das Produkt 'M-Net Premium DSL + ISDN'
     */
    public static final Long PROD_ID_MNET_PREMIUM_DSL_ISDN = 446L;
    /**
     * Produkt-ID fuer das Produkt 'PMX Leitung'
     */
    public static final Long PROD_ID_PMX_LEITUNG = 443L;
    /**
     * Produkt-ID fuer das Produkt 'Maxi Komplett Deluxe'
     */
    public static final Long PROD_ID_MAXI_KOMPLETT_DELUXE = 503L;
    public static final Long PROD_ID_TV_SIGNALLIEFERUNG = 521L;
    public static final Long PROD_ID_TV_SIGNALLIEFERUNG_MV = 522L;
    /**
     * Produkt-ID fuer das Produkt Wholesale FTTX.
     */
    public static final Long PROD_ID_WHOLESALE_FTTX = 600L;
    /**
     * Produkt-ID fuer 'VPN IPSec Client-to-Site'
     */
    public static final Long PROD_ID_VPN_IPSEC_CLIENT_TO_SITE = 442L;
    /**
     * Produkt-ID fuer MVS Enterprise
     */
    public static final Long PROD_ID_MVS_ENTERPRISE = 535L;
    /**
     * Produkt-ID fuer MVS Site
     */
    public static final Long PROD_ID_MVS_SITE = 536L;
    public static final Long PROD_ID_SDSL_N_DRAHT_OPTION = 99L;
    public static final Long PROD_ID_SDSL_N_DRAHT_FTTC_OPTION = 98L;
    public static final Long PROD_ID_DSL_VOIP = 480L;
    public static final Long PROD_ID_MNET_KABEL_TV = 500L;
    public static final Long PROD_ID_FTTX_TELEFONIE = 511L;
    public static final Long PROD_ID_FTTX_DSL_FON = 513L;
    /**
     * Wert fuer EndstellenTyp zeigt an, dass das Produkt keine Endstellen benoetigt.
     */
    public static final Integer ES_TYP_KEINE_ENDSTELLEN = 0;
    /**
     * Wert fuer EndstellenTyp zeigt an, dass nur die Endstelle B benoetigt wird.
     */
    public static final Integer ES_TYP_NUR_B = 1;
    /**
     * Wert fuer EndstellenTyp zeigt an, dass die Endstellen A und B benoetigt werden.
     */
    public static final Integer ES_TYP_A_UND_B = 2;
    /**
     * Wert fuer Feld 'aktionsId' kennzeichnet, dass die Menge des Produkts zwischen dem Billing- und CC-System
     * unerheblich ist.
     */
    public static final Short AKTIONS_ID_COUNT_IRRELEVANT = (short) 2;
    /**
     * Pattern-Name fuer die Ermittlung der aktuellen Downstream-Leistung des Auftrags.
     */
    public static final String PROD_NAME_PATTERN_DOWNSTREAM = "DOWNSTREAM";
    /**
     * Pattern-Name fuer die Ermittlung der aktuellen Upstream-Leistung des Auftrags.
     */
    public static final String PROD_NAME_PATTERN_UPSTREAM = "UPSTREAM";
    /**
     * Pattern-Name fuer die Ermittlung der aktuellen Voip-Leistung des Auftrags.
     */
    public static final String PROD_NAME_PATTERN_VOIP = "VOIP";
    /**
     * Pattern-Name fuer die Ermittlung der aktuellen 'Real-Variante' des Auftrags.
     */
    public static final String PROD_NAME_PATTERN_REALVARIANTE = "REALVARIANTE";


    private static final long serialVersionUID = -3787054052022005273L;
    private Long produktGruppeId;
    private String produktNr;
    private String anschlussart;
    private String productNamePattern;
    private Long leitungsart;
    private Short aktionsId;
    private Integer maxDnCount;
    private Integer minDnCount;
    private Boolean dnBlock;
    private Long dnTyp;
    private Boolean auftragserstellung;
    /**
     * Buendel aus Billing-Buendellogik ermitteln.
     */
    private Boolean buendelProdukt;
    /**
     * Buendel aus Billing-Haupt-/Unterauftragslogik ermitteln.
     */
    private Boolean buendelBillingHauptauftrag;
    private Boolean leitungsNrAnlegen;
    private Boolean vbzUseFromMaster;
    private String vbzKindOfUseProduct;
    private String vbzKindOfUseType;
    private String vbzKindOfUseTypeVpn;
    private Boolean elVerlauf;
    private Integer endstellenTyp;
    private String beschreibung;
    private String accountVorsatz;
    private Integer liNr;
    private Boolean vpnPhysik;
    private Boolean projektierung;
    private Boolean isParent;
    private Boolean checkChild;
    private Boolean isCombiProdukt;
    private Boolean autoProductChange;
    private Boolean exportKdpM;
    private Boolean createKdpAccountReport;
    private Boolean exportAKProdukt;
    private Long verteilungDurch;
    private Boolean baRuecklaeufer;
    private Long projektierungChainId;
    private Long verlaufChainId;
    private Long verlaufCancelChainId;
    private Boolean createAPAddress;
    private Boolean baTerminVerschieben;
    private Boolean assignIad;
    private Boolean cpsProvisioning;
    private String cpsProductName;
    private String cpsAccountType;
    private Boolean cpsAutoCreation;
    private Boolean cpsDSLProduct;
    private Boolean cpsMultiDraht;
    private Boolean isVierDraht;
    private Boolean cpsIPDefault;
    private Long ipPool;
    private Boolean automationPossible;
    private GeoIdSource geoIdSource;
    private SdslNdraht sdslNdraht;
    private Boolean autoHvtZuordnung;
    private Boolean smsVersand;
    private Long erstellStatusId;
    private Long kuendigungStatusId;
    private Boolean sendStatusUpdates;

    /**
     * Der Verwendungszweck fuer eine IPv4 Adresse. Dieser ist in der Tabelle T_REFERENCE gespeichert und kann folgende
     * Werte einnehmen: <ul> <li>Transfernetz</li> <li>Kundennetz</li> </ul>
     */
    private Reference ipPurposeV4;

    /**
     * Soll das Feld {@link #ipPurposeV4} vom Benutzer editiert werden koennen?
     */
    private Boolean ipPurposeV4Editable;

    /**
     * Groesse der Netzmaske fuer IPv4 Adressen.
     */
    private Integer ipNetmaskSizeV4;

    /**
     * Groesse der Netzmaske fuer IPv6 Adressen.
     */
    private Integer ipNetmaskSizeV6;

    /**
     * Sollen die Felder {@link #ipNetmaskSizeV4} und / oder {@link #ipNetmaskSizeV6} vom Benutzer editiert werden
     * koennen?
     */
    private Boolean ipNetmaskSizeEditable;

    /**
     * Optionale Switch-Kennung.
     */
    private HWSwitch hwSwitch;

    /**
     * AFTR Adresse (FQDN)
     */
    private String aftrAddress;

    /**
     * PCP Wert für Daten (p-bit)
     */
    private Integer pbitDaten;

    /**
     * PCP Wert für Voip (p-bit)
     */
    private Integer pbitVoip;

    /**
     * Gibt eine Bezeichnung fuer das Produkt zurueck. Die Bezeichnung setzt sich aus 'Anschlussart', Produkt-ID und
     * 'ProduktNr' zusammen.
     *
     * @return
     */
    public String getBezeichnung() {
        if (getAnschlussart() == null) {
            return null;
        }

        return getAnschlussart() + " (" + getId() + ")" + " - " + getProduktNr();
    }



    /**
     * @see Produkt#getBezeichnung() - aber ohne Produkt-Nr
     */
    public String getBezeichnungShort() {
        if (getAnschlussart() == null) {
            return null;
        }

        return getAnschlussart() + " (" + getId() + ")";
    }

    @CheckForNull
    public SdslNdraht getSdslNdraht() {
        return sdslNdraht;
    }

    public void setSdslNdraht(@Nullable final SdslNdraht sdslNdraht) {
        this.sdslNdraht = sdslNdraht;
    }

    /**
     * Prueft, ob es sich bei dem Produkt um ein Wholesale-Produkt handelt.
     *
     * @return
     */
    public boolean isWholesale() {
        return NumberTools.equal(getProduktGruppeId(), ProduktGruppe.WHOLESALE);
    }

    /**
     * Prueft, ob es sich bei dem Produkt um ein Produkt der IPSec-Produktgruppe handelt.
     *
     * @return true wenn das Produkt ein IPSec Produkt ist.
     */
    public boolean isIPSecProduct() {
        return NumberTools.equal(getProduktGruppeId(), ProduktGruppe.IPSEC);
    }

    /**
     * Prueft, ob es sich bei dem Produkt um ein Produkt der MVS-Produktgruppen handelt.
     *
     * @return true wenn das Produkt ein MVS-Produkt ist.
     */
    public boolean isMVSProduct() {
        return (NumberTools.equal(getProduktGruppeId(), ProduktGruppe.MVS_ENTERPRISE) || NumberTools.equal(
                getProduktGruppeId(), ProduktGruppe.MVS_SITE));
    }

    /**
     * Prüft, ob es sich bei dem Produkt um ein virtuelles Produkt handelt.
     *
     * @return true wenn das Produkt ein IPSec- oder MVS-Produkt ist oder keine automatische HVt-Zuordnung für das
     * Produkt konfiguriert ist.
     */
    public boolean isVirtualProduct() {
        return isIPSecProduct() || isMVSProduct() || !getAutoHvtZuordnung();
    }

    public boolean isOnkzAsbNeededForCps() {
        return !isVirtualProduct() || GeoIdSource.GEO_ID.equals(getGeoIdSource());
    }

    /**
     * Gibt an, ob das Produkt Rufnummern besitzen kann. Dies ist der Fall, wenn entweder 'brauchtDN' oder 'dnMoeglich'
     * gesetzt ist.
     */
    public boolean isDnAllowed() {
        return (needsDn() || isDnPossible());
    }

    /**
     * @return true, falls für dieses Produkt eine Rufnummer (= DN) möglich ist
     */
    public boolean isDnPossible() {
        return NumberTools.isGreater(getMaxDnCount(), 0);
    }

    /**
     * @return true, falls dieses Produkt mindestens eine Rufnummer (= DN) braucht
     */
    public boolean needsDn() {
        return NumberTools.isGreater(getMinDnCount(), 0);
    }

    public GeoIdSource getGeoIdSource() {
        return geoIdSource;
    }

    public void setGeoIdSource(final GeoIdSource geoIdSource) {
        this.geoIdSource = geoIdSource;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCProduktModel#getProdId()
     */
    @Override
    public Long getProdId() {
        return getId();
    }

    /**
     * @return Returns the accountVorsatz.
     */
    public String getAccountVorsatz() {
        return accountVorsatz;
    }

    /**
     * @param accountVorsatz The accountVorsatz to set.
     */
    public void setAccountVorsatz(String accountVorsatz) {
        this.accountVorsatz = accountVorsatz;
    }

    /**
     * @return Returns the aktionsId.
     */
    public Short getAktionsId() {
        return aktionsId;
    }

    /**
     * @param aktionsId The aktionsId to set.
     */
    public void setAktionsId(Short aktionsId) {
        this.aktionsId = aktionsId;
    }

    /**
     * @return Returns the anschlussart.
     */
    public String getAnschlussart() {
        return anschlussart;
    }

    /**
     * @param anschlussArt The anschlussart to set.
     */
    public void setAnschlussart(String anschlussArt) {
        this.anschlussart = anschlussArt;
    }

    /**
     * Gibt ein Pattern fuer den zu generierenden Produkt-Namen zurueck. <br> Das Pattern kann aus Textteilen und
     * Platzhaltern bestehen. Platzhalter sind dabei in geschweiften Klammern ('{' und '}') einzuschliessen. <br> Die
     * moeglichen Platzhalter sind als Konstanten im Modell definiert (PROD_NAME_PATTERN_XXX). <br> Die Platzhalter
     * dienen dazu, Werte des Auftrags von anderen Tabellen zu ermitteln. Dadurch koennen bspw. variable Werte wie
     * Bandbreiten dynamisch ermittelt werden.
     *
     * @return Returns the productNamePattern.
     */
    public String getProductNamePattern() {
        return this.productNamePattern;
    }

    /**
     * @param productNamePattern The productNamePattern to set.
     */
    public void setProductNamePattern(String productNamePattern) {
        this.productNamePattern = productNamePattern;
    }

    /**
     * @return Returns the auftragserstellung.
     */
    public Boolean getAuftragserstellung() {
        return auftragserstellung;
    }

    /**
     * @param auftragserstellung The auftragserstellung to set.
     */
    public void setAuftragserstellung(Boolean auftragserstellung) {
        this.auftragserstellung = auftragserstellung;
    }

    /**
     * @return Returns the beschreibung.
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * @param beschreibung The beschreibung to set.
     */
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * @return Returns the {@link #buendelProdukt}.
     */
    public Boolean getBuendelProdukt() {
        return buendelProdukt;
    }

    /**
     * Sets {@link #buendelProdukt}
     *
     * @param buendelProdukt
     */
    public void setBuendelProdukt(Boolean buendelProdukt) {
        this.buendelProdukt = buendelProdukt;
    }

    /**
     * @return {@link #buendelBillingHauptauftrag}.
     */
    public Boolean getBuendelBillingHauptauftrag() {
        return buendelBillingHauptauftrag;
    }

    /**
     * Sets {@link #buendelBillingHauptauftrag}
     *
     * @param buendelBillingHauptauftrag
     */
    public void setBuendelBillingHauptauftrag(Boolean buendelBillingHauptauftrag) {
        this.buendelBillingHauptauftrag = buendelBillingHauptauftrag;
    }

    /**
     * @return Returns the dnBlock.
     */
    public Boolean getDnBlock() {
        return this.dnBlock;
    }

    /**
     * @param dnBlock The dnBlock to set.
     */
    public void setDnBlock(Boolean dnBlock) {
        this.dnBlock = dnBlock;
    }

    /**
     * @return Returns the dnTyp.
     */
    public Long getDnTyp() {
        return this.dnTyp;
    }

    /**
     * @param dnTyp The dnTyp to set.
     */
    public void setDnTyp(Long dnTyp) {
        this.dnTyp = dnTyp;
    }

    /**
     * @return die minimale Anzahl an Rufnummern (= DNs), niemals {@code null}
     */
    public Integer getMinDnCount() {
        return this.minDnCount;
    }

    public void setMinDnCount(Integer minDnCount) {
        this.minDnCount = minDnCount;
    }

    /**
     * @return die maximale Anzahl an Rufnummern (= DNs), niemals {@code null}
     */
    public Integer getMaxDnCount() {
        return this.maxDnCount;
    }

    public void setMaxDnCount(Integer maxDnCount) {
        this.maxDnCount = maxDnCount;
    }

    /**
     * @return Returns the elVerlauf.
     */
    public Boolean getElVerlauf() {
        return elVerlauf;
    }

    /**
     * @param elVerlauf The elVerlauf to set.
     */
    public void setElVerlauf(Boolean elVerlauf) {
        this.elVerlauf = elVerlauf;
    }

    /**
     * @return Returns the endstellenTyp.
     */
    public Integer getEndstellenTyp() {
        return endstellenTyp;
    }

    /**
     * @param endstellenTyp The endstellenTyp to set.
     */
    public void setEndstellenTyp(Integer endstellenTyp) {
        this.endstellenTyp = endstellenTyp;
    }

    /**
     * @return Returns the leitungsart.
     */
    public Long getLeitungsart() {
        return leitungsart;
    }

    /**
     * @param leitungsArt The leitungsart to set.
     */
    public void setLeitungsart(Long leitungsArt) {
        this.leitungsart = leitungsArt;
    }

    /**
     * @return Returns the leitungsNrAnlegen.
     */
    public Boolean getLeitungsNrAnlegen() {
        return leitungsNrAnlegen;
    }

    /**
     * @param leitungsNrAnlegen The leitungsNrAnlegen to set.
     */
    public void setLeitungsNrAnlegen(Boolean leitungsNrAnlegen) {
        this.leitungsNrAnlegen = leitungsNrAnlegen;
    }

    public String getVbzKindOfUseProduct() {
        return vbzKindOfUseProduct;
    }

    public void setVbzKindOfUseProduct(String vbzKindOfUseProduct) {
        this.vbzKindOfUseProduct = vbzKindOfUseProduct;
    }

    public String getVbzKindOfUseType() {
        return vbzKindOfUseType;
    }

    public void setVbzKindOfUseType(String vbzKindOfUseType) {
        this.vbzKindOfUseType = vbzKindOfUseType;
    }

    public String getVbzKindOfUseTypeVpn() {
        return vbzKindOfUseTypeVpn;
    }

    public void setVbzKindOfUseTypeVpn(String vbzKindOfUseTypeVpn) {
        this.vbzKindOfUseTypeVpn = vbzKindOfUseTypeVpn;
    }

    /**
     * liNr = Leitungs-Identifikations-Nr
     *
     * @return Returns the liNr.
     */
    public Integer getLiNr() {
        return liNr;
    }

    /**
     * liNr = Leitungs-Identifikations-Nr
     *
     * @param liNr The liNr to set.
     */
    public void setLiNr(Integer liNr) {
        this.liNr = liNr;
    }

    /**
     * @return Returns the produktGruppeId.
     */
    public Long getProduktGruppeId() {
        return produktGruppeId;
    }

    /**
     * @param produktGruppeId The produktGruppeId to set.
     */
    public void setProduktGruppeId(Long produktGruppeId) {
        this.produktGruppeId = produktGruppeId;
    }

    /**
     * @return Returns the produktNr.
     */
    public String getProduktNr() {
        return produktNr;
    }

    /**
     * @param produktNr The produktNr to set.
     */
    public void setProduktNr(String produktNr) {
        this.produktNr = produktNr;
    }

    /**
     * @return Returns the vpnPhysik.
     */
    public Boolean getVpnPhysik() {
        return vpnPhysik;
    }

    /**
     * @param vpnPhysik The vpnPhysik to set.
     */
    public void setVpnPhysik(Boolean vpnPhysik) {
        this.vpnPhysik = vpnPhysik;
    }

    /**
     * @return Returns the projektierung.
     */
    public Boolean getProjektierung() {
        return projektierung;
    }

    /**
     * @param projektierung The projektierung to set.
     */
    public void setProjektierung(Boolean projektierung) {
        this.projektierung = projektierung;
    }

    /**
     * @return Returns the isParent.
     */
    public Boolean getIsParent() {
        return isParent;
    }

    /**
     * @param isParent The isParent to set.
     */
    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }

    /**
     * @return Returns the checkChild.
     */
    public Boolean getCheckChild() {
        return checkChild;
    }

    /**
     * Flag, ob bei der Physikvergabe zuerst geprueft werden soll, welchen Physiktyp der 'Child'-Auftrag benoetigt.
     *
     * @param checkChild The checkChild to set.
     */
    public void setCheckChild(Boolean checkChild) {
        this.checkChild = checkChild;
    }

    /**
     * @return Returns the isCombiProdukt.
     */
    public Boolean getIsCombiProdukt() {
        return this.isCombiProdukt;
    }

    /**
     * @param isCombiProdukt The isCombiProdukt to set.
     */
    public void setIsCombiProdukt(Boolean isCombiProdukt) {
        this.isCombiProdukt = isCombiProdukt;
    }

    /**
     * @return Returns the exportKdpM.
     */
    public Boolean getExportKdpM() {
        return exportKdpM;
    }

    /**
     * Flag fuer Export Kundenportal Muenchen
     *
     * @param exportKdpM The exportKdpM to set.
     */
    public void setExportKdpM(Boolean exportKdpM) {
        this.exportKdpM = exportKdpM;
    }

    /**
     * @return Returns the createKdpAccountReport.
     */
    public Boolean getCreateKdpAccountReport() {
        return createKdpAccountReport;
    }

    /**
     * Ueber das Flag wird definiert, ob fuer das Produkt ein Account-Anschreiben mit den Zugangsdaten vom Kundenportal
     * generiert werden muss (true) oder nicht (false).
     *
     * @param createKdpAccountReport The createKdpAccountReport to set.
     */
    public void setCreateKdpAccountReport(Boolean createKdpAccountReport) {
        this.createKdpAccountReport = createKdpAccountReport;
    }

    /**
     * Flag für den Export von AK-Produkten ins Maxi-Portal
     *
     * @return exportAKProdukt
     */
    public Boolean getExportAKProdukt() {
        return exportAKProdukt;
    }

    public void setExportAKProdukt(Boolean exportAKProdukt) {
        this.exportAKProdukt = exportAKProdukt;
    }

    /**
     * Gibt die ID der Abteilung zurueck (Dispo oder NP), die fuer die Verteilung der Bauauftraege/Projektierung fuer
     * das Produkt verantwortlich ist. <br>
     *
     * @return ID der fuer die Verteilung verantwortlichen Abteilung oder <code>null</code>, falls Bauauftraege direkt
     * an eine Abteilung geschickt werden sollen (nur moeglich, wenn lediglich eine Abteilung den BA erhaelt).
     */
    public Long getVerteilungDurch() {
        return verteilungDurch;
    }

    public void setVerteilungDurch(Long verteilungDurch) {
        this.verteilungDurch = verteilungDurch;
    }

    /**
     * Gibt an, ob der Bauauftrag auf jeden Fall ueber die Ruecklaeufer gehen muss oder im Idealfall
     * (Realisierungstermin=Vorgabetermin) direkt abgeschlossen werden kann.
     *
     * @return Returns the baRuecklaeufer.
     */
    public Boolean getBaRuecklaeufer() {
        return this.baRuecklaeufer;
    }

    public void setBaRuecklaeufer(Boolean baRuecklaeufer) {
        this.baRuecklaeufer = baRuecklaeufer;
    }

    public Boolean getAutoProductChange() {
        return this.autoProductChange;
    }

    public void setAutoProductChange(Boolean autoProductChange) {
        this.autoProductChange = autoProductChange;
    }

    public Long getVerlaufChainId() {
        return verlaufChainId;
    }

    public void setVerlaufChainId(Long verlaufChainId) {
        this.verlaufChainId = verlaufChainId;
    }

    public Long getProjektierungChainId() {
        return projektierungChainId;
    }

    public void setProjektierungChainId(Long projektierungChainId) {
        this.projektierungChainId = projektierungChainId;
    }

    public Long getVerlaufCancelChainId() {
        return verlaufCancelChainId;
    }

    public void setVerlaufCancelChainId(Long verlaufCancelChainId) {
        this.verlaufCancelChainId = verlaufCancelChainId;
    }

    /**
     * Das Flag definiert, ob eine Standortadresse in Hurrican (TRUE) oder in Taifun (FALSE) verwaltet wird.
     *
     * @return createAPAddress
     */
    public Boolean getCreateAPAddress() {
        return createAPAddress;
    }

    public void setCreateAPAddress(Boolean createAPAddress) {
        this.createAPAddress = createAPAddress;
    }

    public Boolean getBaTerminVerschieben() {
        return baTerminVerschieben;
    }

    public void setBaTerminVerschieben(Boolean baTerminVerschieben) {
        this.baTerminVerschieben = baTerminVerschieben;
    }

    public Boolean getAssignIad() {
        return assignIad;
    }

    public void setAssignIad(Boolean assignIad) {
        this.assignIad = assignIad;
    }

    public Boolean getCpsProvisioning() {
        return cpsProvisioning;
    }

    public void setCpsProvisioning(Boolean cpsProvisioning) {
        this.cpsProvisioning = cpsProvisioning;
    }

    public String getCpsProductName() {
        return cpsProductName;
    }

    public void setCpsProductName(String cpsProductName) {
        this.cpsProductName = cpsProductName;
    }

    public String getCpsAccountType() {
        return cpsAccountType;
    }

    public void setCpsAccountType(String cpsAccountType) {
        this.cpsAccountType = cpsAccountType;
    }

    public Boolean getCpsAutoCreation() {
        return cpsAutoCreation;
    }

    public void setCpsAutoCreation(Boolean cpsAutoCreation) {
        this.cpsAutoCreation = cpsAutoCreation;
    }

    public Boolean getCpsDSLProduct() {
        return cpsDSLProduct;
    }

    public void setCpsDSLProduct(Boolean cpsDSLProduct) {
        this.cpsDSLProduct = cpsDSLProduct;
    }

    public Boolean getCpsMultiDraht() {
        return cpsMultiDraht;
    }

    public void setCpsMultiDraht(Boolean cpsMultiDraht) {
        this.cpsMultiDraht = cpsMultiDraht;
    }

    public Boolean getVbzUseFromMaster() {
        return vbzUseFromMaster;
    }

    public void setVbzUseFromMaster(Boolean vbzUseFromMaster) {
        this.vbzUseFromMaster = vbzUseFromMaster;
    }

    public Boolean getIsVierDraht() {
        return isVierDraht;
    }

    public void setIsVierDraht(Boolean isVierDraht) {
        this.isVierDraht = isVierDraht;
    }

    public Boolean getCpsIPDefault() {
        return cpsIPDefault;
    }

    public void setCpsIPDefault(Boolean cpsIPDefault) {
        this.cpsIPDefault = cpsIPDefault;
    }

    public Long getIpPool() {
        return ipPool;
    }

    public void setIpPool(Long ipPool) {
        this.ipPool = ipPool;
    }

    public Reference getIpPurposeV4() {
        return ipPurposeV4;
    }

    public void setIpPurposeV4(Reference ipPurposeV4) {
        this.ipPurposeV4 = ipPurposeV4;
    }

    public Boolean getIpPurposeV4Editable() {
        return ipPurposeV4Editable;
    }

    public void setIpPurposeV4Editable(Boolean ipPurposeV4Editable) {
        this.ipPurposeV4Editable = ipPurposeV4Editable;
    }

    public Integer getIpNetmaskSizeV4() {
        return ipNetmaskSizeV4;
    }

    public void setIpNetmaskSizeV4(Integer ipNetmaskSizeV4) {
        this.ipNetmaskSizeV4 = ipNetmaskSizeV4;
    }

    public Integer getIpNetmaskSizeV6() {
        return ipNetmaskSizeV6;
    }

    public void setIpNetmaskSizeV6(Integer ipNetmaskSizeV6) {
        this.ipNetmaskSizeV6 = ipNetmaskSizeV6;
    }

    public Boolean getIpNetmaskSizeEditable() {
        return ipNetmaskSizeEditable;
    }

    public void setIpNetmaskSizeEditable(Boolean ipNetmaskSizeEditable) {
        this.ipNetmaskSizeEditable = ipNetmaskSizeEditable;
    }

    public Boolean getAutomationPossible() {
        return automationPossible;
    }

    public void setAutomationPossible(Boolean automationPossible) {
        this.automationPossible = automationPossible;
    }

    public HWSwitch getHwSwitch() {
        return this.hwSwitch;
    }

    public void setHwSwitch(@Nullable HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    public Boolean getAutoHvtZuordnung() {
        return autoHvtZuordnung;
    }

    public void setAutoHvtZuordnung(Boolean autoHvtZuordnung) {
        this.autoHvtZuordnung = autoHvtZuordnung;
    }

    public Boolean getSmsVersand() {
        return smsVersand;
    }

    public void setSmsVersand(Boolean smsVersand) {
        this.smsVersand = smsVersand;
    }

    public Long getErstellStatusId() {
        return erstellStatusId;
    }

    public void setErstellStatusId(Long erstellStatusId) {
        this.erstellStatusId = erstellStatusId;
    }

    public Long getKuendigungStatusId() {
        return kuendigungStatusId;
    }

    public void setKuendigungStatusId(Long kuendigungStatusId) {
        this.kuendigungStatusId = kuendigungStatusId;
    }

    /**
     * @return ob nach Anlage ueber den Taifun-Webservice statusUpdates an Taifun gesendet werden sollen
     */
    public Boolean getSendStatusUpdates() {
        return sendStatusUpdates;
    }

    public void setSendStatusUpdates(Boolean sendStatusUpdates) {
        this.sendStatusUpdates = sendStatusUpdates;
    }

    public String getAftrAddress() {
        return aftrAddress;
    }

    public void setAftrAddress(String aftrAddress) {
        this.aftrAddress = aftrAddress;
    }

    public Integer getPbitDaten() {
        return pbitDaten;
    }

    public void setPbitDaten(Integer pbitDaten) {
        this.pbitDaten = pbitDaten;
    }

    public Integer getPbitVoip() {
        return pbitVoip;
    }

    public void setPbitVoip(Integer pbitVoip) {
        this.pbitVoip = pbitVoip;
    }

    public boolean isPremiumDslHVt() {
        return NumberTools.isIn(getProdId(), new Number[] {
                PROD_ID_PREMIUM_DSL_ANALOG,
                PROD_ID_PREMIUM_DSL_ISDN,
                PROD_ID_MNET_PREMIUM_DSL_ANALOG,
                PROD_ID_MNET_PREMIUM_DSL_ISDN
        });
    }

    @Override
    public int hashCode() {
        return 31
                * super.hashCode()
                + Objects.hash(produktGruppeId, produktNr, anschlussart, productNamePattern, leitungsart, aktionsId,
                maxDnCount, minDnCount, dnBlock, dnTyp, auftragserstellung, buendelProdukt, buendelBillingHauptauftrag,
                leitungsNrAnlegen, vbzUseFromMaster, vbzKindOfUseProduct, vbzKindOfUseType, vbzKindOfUseTypeVpn,
                elVerlauf, endstellenTyp, beschreibung, accountVorsatz, liNr, vpnPhysik, projektierung, isParent,
                checkChild, isCombiProdukt, autoProductChange, exportKdpM, createKdpAccountReport,
                exportAKProdukt, verteilungDurch, baRuecklaeufer, projektierungChainId, verlaufChainId,
                verlaufCancelChainId, createAPAddress, baTerminVerschieben, assignIad, cpsProvisioning,
                cpsProductName, cpsAccountType, cpsAutoCreation, cpsDSLProduct, cpsMultiDraht, isVierDraht,
                cpsIPDefault, ipPool, automationPossible, geoIdSource, sdslNdraht, autoHvtZuordnung,
                smsVersand, ipPurposeV4, ipPurposeV4Editable, ipNetmaskSizeV4, ipNetmaskSizeV6,
                ipNetmaskSizeEditable, hwSwitch, erstellStatusId, kuendigungStatusId, sendStatusUpdates,
                aftrAddress, pbitDaten, pbitVoip);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Produkt other = (Produkt) obj;
        return Objects.equals(this.produktGruppeId, other.produktGruppeId)
                && Objects.equals(this.produktNr, other.produktNr)
                && Objects.equals(this.anschlussart, other.anschlussart)
                && Objects.equals(this.productNamePattern, other.productNamePattern)
                && Objects.equals(this.leitungsart, other.leitungsart)
                && Objects.equals(this.aktionsId, other.aktionsId) && Objects.equals(this.maxDnCount, other.maxDnCount)
                && Objects.equals(this.minDnCount, other.minDnCount) && Objects.equals(this.dnBlock, other.dnBlock)
                && Objects.equals(this.dnTyp, other.dnTyp)
                && Objects.equals(this.auftragserstellung, other.auftragserstellung)
                && Objects.equals(this.buendelProdukt, other.buendelProdukt)
                && Objects.equals(this.buendelBillingHauptauftrag, other.buendelBillingHauptauftrag)
                && Objects.equals(this.leitungsNrAnlegen, other.leitungsNrAnlegen)
                && Objects.equals(this.vbzUseFromMaster, other.vbzUseFromMaster)
                && Objects.equals(this.vbzKindOfUseProduct, other.vbzKindOfUseProduct)
                && Objects.equals(this.vbzKindOfUseType, other.vbzKindOfUseType)
                && Objects.equals(this.vbzKindOfUseTypeVpn, other.vbzKindOfUseTypeVpn)
                && Objects.equals(this.elVerlauf, other.elVerlauf)
                && Objects.equals(this.endstellenTyp, other.endstellenTyp)
                && Objects.equals(this.beschreibung, other.beschreibung)
                && Objects.equals(this.accountVorsatz, other.accountVorsatz) && Objects.equals(this.liNr, other.liNr)
                && Objects.equals(this.vpnPhysik, other.vpnPhysik)
                && Objects.equals(this.projektierung, other.projektierung)
                && Objects.equals(this.isParent, other.isParent) && Objects.equals(this.checkChild, other.checkChild)
                && Objects.equals(this.isCombiProdukt, other.isCombiProdukt)
                && Objects.equals(this.autoProductChange, other.autoProductChange)
                && Objects.equals(this.exportKdpM, other.exportKdpM)
                && Objects.equals(this.createKdpAccountReport, other.createKdpAccountReport)
                && Objects.equals(this.exportAKProdukt, other.exportAKProdukt)
                && Objects.equals(this.verteilungDurch, other.verteilungDurch)
                && Objects.equals(this.baRuecklaeufer, other.baRuecklaeufer)
                && Objects.equals(this.projektierungChainId, other.projektierungChainId)
                && Objects.equals(this.verlaufChainId, other.verlaufChainId)
                && Objects.equals(this.verlaufCancelChainId, other.verlaufCancelChainId)
                && Objects.equals(this.createAPAddress, other.createAPAddress)
                && Objects.equals(this.baTerminVerschieben, other.baTerminVerschieben)
                && Objects.equals(this.assignIad, other.assignIad)
                && Objects.equals(this.cpsProvisioning, other.cpsProvisioning)
                && Objects.equals(this.cpsProductName, other.cpsProductName)
                && Objects.equals(this.cpsAccountType, other.cpsAccountType)
                && Objects.equals(this.cpsAutoCreation, other.cpsAutoCreation)
                && Objects.equals(this.cpsDSLProduct, other.cpsDSLProduct)
                && Objects.equals(this.cpsMultiDraht, other.cpsMultiDraht)
                && Objects.equals(this.isVierDraht, other.isVierDraht)
                && Objects.equals(this.cpsIPDefault, other.cpsIPDefault) && Objects.equals(this.ipPool, other.ipPool)
                && Objects.equals(this.automationPossible, other.automationPossible)
                && Objects.equals(this.geoIdSource, other.geoIdSource)
                && Objects.equals(this.sdslNdraht, other.sdslNdraht)
                && Objects.equals(this.autoHvtZuordnung, other.autoHvtZuordnung)
                && Objects.equals(this.smsVersand, other.smsVersand)
                && Objects.equals(this.ipPurposeV4, other.ipPurposeV4)
                && Objects.equals(this.ipPurposeV4Editable, other.ipPurposeV4Editable)
                && Objects.equals(this.ipNetmaskSizeV4, other.ipNetmaskSizeV4)
                && Objects.equals(this.ipNetmaskSizeV6, other.ipNetmaskSizeV6)
                && Objects.equals(this.ipNetmaskSizeEditable, other.ipNetmaskSizeEditable)
                && Objects.equals(this.hwSwitch, other.hwSwitch)
                && Objects.equals(this.erstellStatusId, other.erstellStatusId)
                && Objects.equals(this.kuendigungStatusId, other.kuendigungStatusId)
                && Objects.equals(this.sendStatusUpdates, other.sendStatusUpdates)
                && Objects.equals(this.aftrAddress, other.aftrAddress)
                && Objects.equals(this.pbitDaten, other.pbitDaten)
                && Objects.equals(this.pbitVoip, other.pbitVoip)
                ;
    }

    @Override
    public String toString() {
        return "Produkt{" +
                "produktGruppeId=" + produktGruppeId +
                ", produktNr='" + produktNr + '\'' +
                ", anschlussart='" + anschlussart + '\'' +
                ", productNamePattern='" + productNamePattern + '\'' +
                ", leitungsart=" + leitungsart +
                ", aktionsId=" + aktionsId +
                ", maxDnCount=" + maxDnCount +
                ", minDnCount=" + minDnCount +
                ", dnBlock=" + dnBlock +
                ", dnTyp=" + dnTyp +
                ", auftragserstellung=" + auftragserstellung +
                ", buendelProdukt=" + buendelProdukt +
                ", buendelBillingHauptauftrag=" + buendelBillingHauptauftrag +
                ", leitungsNrAnlegen=" + leitungsNrAnlegen +
                ", vbzUseFromMaster=" + vbzUseFromMaster +
                ", vbzKindOfUseProduct='" + vbzKindOfUseProduct + '\'' +
                ", vbzKindOfUseType='" + vbzKindOfUseType + '\'' +
                ", vbzKindOfUseTypeVpn='" + vbzKindOfUseTypeVpn + '\'' +
                ", elVerlauf=" + elVerlauf +
                ", endstellenTyp=" + endstellenTyp +
                ", beschreibung='" + beschreibung + '\'' +
                ", accountVorsatz='" + accountVorsatz + '\'' +
                ", liNr=" + liNr +
                ", vpnPhysik=" + vpnPhysik +
                ", projektierung=" + projektierung +
                ", isParent=" + isParent +
                ", checkChild=" + checkChild +
                ", isCombiProdukt=" + isCombiProdukt +
                ", autoProductChange=" + autoProductChange +
                ", exportKdpM=" + exportKdpM +
                ", createKdpAccountReport=" + createKdpAccountReport +
                ", exportAKProdukt=" + exportAKProdukt +
                ", verteilungDurch=" + verteilungDurch +
                ", baRuecklaeufer=" + baRuecklaeufer +
                ", projektierungChainId=" + projektierungChainId +
                ", verlaufChainId=" + verlaufChainId +
                ", verlaufCancelChainId=" + verlaufCancelChainId +
                ", createAPAddress=" + createAPAddress +
                ", baTerminVerschieben=" + baTerminVerschieben +
                ", assignIad=" + assignIad +
                ", cpsProvisioning=" + cpsProvisioning +
                ", cpsProductName='" + cpsProductName + '\'' +
                ", cpsAccountType='" + cpsAccountType + '\'' +
                ", cpsAutoCreation=" + cpsAutoCreation +
                ", cpsDSLProduct=" + cpsDSLProduct +
                ", cpsMultiDraht=" + cpsMultiDraht +
                ", isVierDraht=" + isVierDraht +
                ", cpsIPDefault=" + cpsIPDefault +
                ", ipPool=" + ipPool +
                ", automationPossible=" + automationPossible +
                ", geoIdSource=" + geoIdSource +
                ", sdslNdraht=" + sdslNdraht +
                ", autoHvtZuordnung=" + autoHvtZuordnung +
                ", smsVersand=" + smsVersand +
                ", ipPurposeV4=" + ipPurposeV4 +
                ", ipPurposeV4Editable=" + ipPurposeV4Editable +
                ", ipNetmaskSizeV4=" + ipNetmaskSizeV4 +
                ", ipNetmaskSizeV6=" + ipNetmaskSizeV6 +
                ", ipNetmaskSizeEditable=" + ipNetmaskSizeEditable +
                ", hwSwitch=" + hwSwitch +
                ", erstellStatusId=" + erstellStatusId +
                ", kuendigungStatusId=" + kuendigungStatusId +
                ", sendStatusUpdates=" + sendStatusUpdates +
                ", aftrAddress=" + aftrAddress +
                ", pbitDaten=" + pbitDaten +
                ", pbitVoip=" + pbitVoip +
                '}';
    }
}
