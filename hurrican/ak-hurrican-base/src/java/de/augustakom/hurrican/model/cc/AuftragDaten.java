/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2004 08:39:26
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragStatusModel;
import de.augustakom.hurrican.model.shared.iface.CCProduktModel;

/**
 * In diesem Modell sind allgemeine Daten zu einem Auftrag enthalten.
 */
public class AuftragDaten extends AbstractCCHistoryModel implements CCAuftragModel, CCAuftragStatusModel,
        CCProduktModel {

    public static final Function<List<AuftragDaten>, List<AuftragDaten>> RETURN_ACTIVE_AUFTRAG_DATEN = in -> {
        List<AuftragDaten> out = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(in)) {
            for (AuftragDaten auftrag : in) {
                if (auftrag.isAuftragActiveAndInBetrieb()) {
                    out.add(auftrag);
                }
            }
        }
        return out;
    };

    private static final long serialVersionUID = 7830208122009189214L;

    /**
     * Konstante fuer {@link BAuftrag#bundleOrderNo}. Gibt an, dass die Buendel-No aus dem Billing-System vorgegeben
     * wird.
     */
    public static final String BUENDEL_HERKUNFT_BILLING_PRODUKT = "prodak";
    /**
     * Konstante fuer {@link BAuftrag#bundleOrderNo} Gibt an, dass ein Bündel aus der Hauptauftragsnummer aus dem
     * Billing-System vorgegeben wurde.
     */
    public static final String BUENDEL_HERKUNFT_BILLING_HAUPTAUFTRAG = "billing_main_order";

    /**
     * Konstante fuer <code>buendelNoHerkunft</code>. Gibt an, dass die Buendel-No aus dem Hurrican-System vergeben
     * wird.
     */
    public static final String BUENDEL_HERKUNFT_HURRICAN = "hurrican";

    /**
     * Wert fuer 'telefonbuch' bzw. 'inverssuche', wenn der Eintrag nicht vorhanden ist.
     */
    public static final Short EINTRAG_NICHT_VORHANDEN = (short) 0;
    /**
     * Wert fuer 'telefonbuch' bzw. 'inverssuche', wenn der Eintrag erledigt wurde.
     */
    public static final Short EINTRAG_ERLEDIGT = (short) 1;
    /**
     * Wert fuer 'telefonbuch' bzw. 'inverssuche', wenn der Kunde keinen Eintrag wuenscht.
     */
    public static final Short EINTRAG_NICHT_GEWUENSCHT = (short) 2;
    /**
     * Wert fuer 'inverssuche', wenn der Kunde der Inverssuche widerspricht.
     */
    public static final Short EINTRAG_WIDERSPROCHEN = (short) 3;

    public static final Function<AuftragDaten, Long> AUFTRAG_NO_ORIG = new Function<AuftragDaten, Long>() {
        @Override
        @Nullable
        public Long apply(@Nullable AuftragDaten input) {
            return input == null ? null : input.getAuftragNoOrig();
        }
    };

    public static final Function<AuftragDaten, Long> STATUS_ID = new Function<AuftragDaten, Long>() {
        @Nullable
        @Override
        public Long apply(@Nullable final AuftragDaten input) {
            return input == null ? null : input.getAuftragStatusId();
        }
    };

    private Long auftragId = null;
    private Long auftragNoOrig = null;
    private Long prodId = null;
    private Long statusId = null;
    private Long mmzId = null;
    private String bemerkungen = null;
    private Date angebotDatum = null;
    private Date auftragDatum = null;
    private Date inbetriebnahme = null;
    private Date kuendigung = null;
    private Date vorgabeSCV = null;
    private Date vorgabeKunde = null;
    private String bestellNr = null;
    private String lbzKunde = null;
    private Integer buendelNr = null;
    private String buendelNrHerkunft = null;
    private String bearbeiter = null;
    private Short telefonbuch = null;
    private Short inverssuche = null;
    private Boolean statusmeldungen;
    private boolean autoSmsAndMailVersand;
    private String wholesaleAuftragsId = null;


    public static final Function<List<AuftragDaten>, List<Long>> AUFTRAG_ID_EXTRACTOR_4_AUFTRAG_DATEN = in -> {
        List<Long> out = new ArrayList<>(in.size());
        if (CollectionUtils.isNotEmpty(in)) {
            for (AuftragDaten auftrag : in) {
                out.add(auftrag.getAuftragId());
            }
        }
        return out;
    };

    public static final Function<List<AuftragDaten>, List<Integer>> BUENDEL_NR_EXTRACTOR_4_AUFTRAG_DATEN = in -> {
        final List<Integer> out = new ArrayList<>(in.size());
        if (CollectionUtils.isNotEmpty(in)) {
            for (AuftragDaten auftrag : in) {
                if (auftrag.getBuendelNr() != null) {
                    out.add(auftrag.getBuendelNr());
                }
            }
        }
        return out;
    };

    /**
     * Prueft, ob der aktuelle Auftrag zum angegebenen Datum 'aktiv' ist.
     */
    public boolean isActiveAt(Date activeAt) {
        Date auftragValidFrom = (inbetriebnahme != null) ? inbetriebnahme : vorgabeSCV;

        if (auftragValidFrom != null) {
            if (DateTools.isDateBeforeOrEqual(auftragValidFrom, activeAt)
                    && (kuendigung == null || DateTools.isDateAfter(kuendigung, activeAt))) {
                return true;
            }
        }
        else {
            return isInBetriebOrAenderung();
        }

        return false;
    }

    /**
     * Ueberprueft, ob sich der Auftrag in einem aktiven Status befindet. Dies ist dann der Fall, wenn er nicht
     * storniert ist, auf Absage oder sich in einem Kuendigungsstatus befindet.
     *
     * @return true, wenn sich der Auftrag in einem aktiven Status befindet.
     */
    public boolean isAuftragActive() {
        if (NumberTools.isIn(getStatusId(),
                new Number[] { AuftragStatus.ABSAGE, AuftragStatus.STORNO })
                || NumberTools.isGreaterOrEqual(getStatusId(), AuftragStatus.KUENDIGUNG)) {
            return false;
        }
        return true;
    }

    /**
     * Ueberprueft, ob sich der Auftrag aktiv ist und noch nicht gekündigt
     *
     * @return true, wenn sich der Auftrag in einem aktiven Status befindet.
     */
    public boolean isAuftragActiveAndInBetrieb() {
        if (NumberTools.isIn(getStatusId(),
                new Number[] { AuftragStatus.ABSAGE, AuftragStatus.STORNO })
                || NumberTools.isGreaterOrEqual(getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT)) {
            return false;
        }
        return true;
    }

    /**
     * Prüft den Auftragsstatus, ob eine Terminverschiebung möglich ist.
     */
    public boolean isTerminverschiebungPossible() {
        return !NumberTools.isIn(getStatusId(), new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE });
    }

    /**
     * Ueberprueft, ob sich der Auftrag in Erfassung befindet.
     */
    public boolean isInErfassung() {
        return NumberTools.isIn(getStatusId(), new Number[] { AuftragStatus.ERFASSUNG, AuftragStatus.ERFASSUNG_SCV });
    }

    /**
     * Ueberprueft, ob sich der Auftrag in einer Realisierung befindet. Dies ist dann der Fall, wenn der Status
     * <=TECHNISCHE_REALISIERUNG (ausser Absage/Storno) oder =AENDERUNG bzw. =AENDERUNG_IM_UMLAUF ist
     */
    public boolean isInRealisierung() {
        if (NumberTools.isLessOrEqual(getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG) &&
                !NumberTools.isIn(getStatusId(), new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE })) {
            return true;
        }
        else if (NumberTools.isIn(getStatusId(), new Number[] { AuftragStatus.AENDERUNG, AuftragStatus.AENDERUNG_IM_UMLAUF })) {
            return true;
        }
        return false;
    }

    /**
     * Prueft, ob sich der aktuelle Auftrag im Status 'in Betrieb' bzw. 'in Aenderung' befindet.
     */
    public boolean isInBetriebOrAenderung() {
        return NumberTools.isGreaterOrEqual(getStatusId(), AuftragStatus.IN_BETRIEB) &&
                NumberTools.isLessOrEqual(getStatusId(), AuftragStatus.AENDERUNG_IM_UMLAUF);
    }

    /**
     * Prueft, ob sich der aktuelle Auftrag im Status 'in Betrieb' befindet.
     */
    public boolean isInBetrieb() {
        return NumberTools.equal(getStatusId(), AuftragStatus.IN_BETRIEB);
    }

    /**
     * Prueft, ob sich der aktuelle Auftrag im Status 'Aenderung' oder 'Aenderung im Umlauf' befindet.
     */
    public boolean isInAenderung() {
        return NumberTools.isGreaterOrEqual(getStatusId(), AuftragStatus.AENDERUNG) &&
                NumberTools.isLessOrEqual(getStatusId(), AuftragStatus.AENDERUNG_IM_UMLAUF);
    }

    public boolean isAenderung() {
        return NumberTools.equal(getStatusId(), AuftragStatus.AENDERUNG);
    }

    public boolean isAenderungImUmlauf() {
        return NumberTools.equal(getStatusId(), AuftragStatus.AENDERUNG_IM_UMLAUF);
    }

    /**
     * Prueft, ob sich der Auftrag im Status 'Kuendigung Erfassung' oder spaeter befindet.
     */
    public boolean isInKuendigung() {
        return NumberTools.isGreaterOrEqual(getStatusId(), AuftragStatus.KUENDIGUNG_ERFASSEN)
                && NumberTools.isLess(getStatusId(), AuftragStatus.KONSOLIDIERT);
    }

    /**
     * Prueft, ob der Auftrag 'in Kuendigung' ist (>= Kuendigung && < Gekuendigt)). Bereits gekuendigte Auftraege werden
     * nicht betrachtet!
     */
    public boolean isInKuendigungEx() {
        return NumberTools.isGreaterOrEqual(getStatusId(), AuftragStatus.KUENDIGUNG)
                && NumberTools.isLess(getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT);
    }

    /**
     * Prueft, ob sich der Auftrag im Status 'Projektierung' befindet.
     */
    public boolean isInProjektierung() {
        return NumberTools.equal(getStatusId(), AuftragStatus.PROJEKTIERUNG);
    }

    /**
     * Prueft, ob der Auftrag bereits (vollstaendig) gekuendigt wurde. Dies ist dann der Fall, wenn der Status auf
     * {@code AUFTRAG_GEKUENDIGT} steht.
     */
    public boolean isCancelled() {
        return NumberTools.equal(getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT);
    }

    /**
     * Prueft, ob der Auftrag geschlossen ist. Dies ist dann der Fall, wenn der Auftrag storniert, abgesagt, gekuendigt
     * oder konsolidiert ist.
     */
    public boolean isAuftragClosed() {
        return NumberTools.isIn(getStatusId(), new Number[] {
                AuftragStatus.ABSAGE,
                AuftragStatus.STORNO,
                AuftragStatus.AUFTRAG_GEKUENDIGT,
                AuftragStatus.KONSOLIDIERT });
    }

    /**
     * Prueft, ob der Auftrag abweichend vom normalen Prozess geschlossen ist.
     */
    public boolean isAuftragAborted() {
        return NumberTools.isIn(getStatusId(), new Number[] {
                AuftragStatus.ABSAGE,
                AuftragStatus.STORNO,
                AuftragStatus.KONSOLIDIERT });
    }

    public boolean isCancellatedWithNonRenewal() {
        return NumberTools.isIn(getStatusId(), new Number[] {
                AuftragStatus.STORNO,
                AuftragStatus.AUFTRAG_GEKUENDIGT });
    }

    /**
     * Prueft, ob der Status vom Hauptauftrag mit dem Status vom Unterauftrag zusammenpaßt.
     * <p/>
     * <ul> <li>Hauptauftrag < Kündigung --> nur Aufträge < Kündigung passend</li> <li>Hauptauftrag >= Kündigung --> nur
     * Aufträge >= Kündigung passend</li> </ul>
     *
     * @param subOrder Unterauftrag
     */
    public boolean matchSubOrderStatus(AuftragDaten subOrder) {
        if (isInKuendigung()) {
            return subOrder.isInKuendigung();
        }
        else if (isInProjektierung()) {
            return NumberTools.isLess(subOrder.getStatusId(), AuftragStatus.PROJEKTIERUNG);
        }
        return !subOrder.isInKuendigung();
    }

    /**
     * @return Returns the angebotDatum.
     */
    public Date getAngebotDatum() {
        return angebotDatum;
    }

    /**
     * @param angebotDatum The angebotDatum to set.
     */
    public void setAngebotDatum(Date angebotDatum) {
        this.angebotDatum = angebotDatum;
    }

    /**
     * @return Returns the auftragDatum.
     */
    public Date getAuftragDatum() {
        return auftragDatum;
    }

    /**
     * @param auftragDatum The auftragDatum to set.
     */
    public void setAuftragDatum(Date auftragDatum) {
        this.auftragDatum = auftragDatum;
    }

    /**
     * @return Returns the auftragId.
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the bemerkungen.
     */
    public String getBemerkungen() {
        return bemerkungen;
    }

    /**
     * @param bemerkungen The bemerkungen to set.
     */
    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    /**
     * @return Returns the bestellNr.
     */
    public String getBestellNr() {
        return bestellNr;
    }

    /**
     * @param bestellNr The bestellNr to set.
     */
    public void setBestellNr(String bestellNr) {
        this.bestellNr = bestellNr;
    }

    /**
     * @return Returns the buendelNr.
     */
    public Integer getBuendelNr() {
        return buendelNr;
    }

    /**
     * @param buendelNr The buendelNr to set.
     */
    public void setBuendelNr(Integer buendelNr) {
        this.buendelNr = buendelNr;
    }

    /**
     * @return Returns the buendelNrHerkunft.
     */
    public String getBuendelNrHerkunft() {
        return buendelNrHerkunft;
    }

    /**
     * @param buendelNrHerkunft The buendelNrHerkunft to set.
     */
    public void setBuendelNrHerkunft(String buendelNrHerkunft) {
        this.buendelNrHerkunft = buendelNrHerkunft;
    }

    /**
     * @return Returns the inbetriebnahme.
     */
    public Date getInbetriebnahme() {
        return inbetriebnahme;
    }

    /**
     * @param inbetriebnahme The inbetriebnahme to set.
     */
    public void setInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

    /**
     * @return Returns the inverssuche.
     */
    public Short getInverssuche() {
        return inverssuche;
    }

    /**
     * @param inverssuche The inverssuche to set.
     */
    public void setInverssuche(Short inverssuche) {
        this.inverssuche = inverssuche;
    }

    /**
     * @return Returns the kuendigung.
     */
    public Date getKuendigung() {
        return kuendigung;
    }

    /**
     * @param kuendigung The kuendigung to set.
     */
    public void setKuendigung(Date kuendigung) {
        this.kuendigung = kuendigung;
    }

    /**
     * @return Returns the mmz.
     */
    public Long getMmzId() {
        return mmzId;
    }

    /**
     * @param mmzId The mmz to set.
     */
    public void setMmzId(Long mmzId) {
        this.mmzId = mmzId;
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
     * @return Returns the prodId.
     */
    @Override
    public Long getProdId() {
        return prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * @return Returns the statusId.
     */
    public Long getStatusId() {
        return statusId;
    }

    /**
     * @param statusId The statusId to set.
     */
    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    /**
     * @return Returns the telefonbuch.
     */
    public Short getTelefonbuch() {
        return telefonbuch;
    }

    /**
     * @param telefonbuch The telefonbuch to set.
     */
    public void setTelefonbuch(Short telefonbuch) {
        this.telefonbuch = telefonbuch;
    }

    /**
     * @return Returns the vorgabeKunde.
     */
    public Date getVorgabeKunde() {
        return vorgabeKunde;
    }

    /**
     * @param vorgabeKunde The vorgabeKunde to set.
     */
    public void setVorgabeKunde(Date vorgabeKunde) {
        this.vorgabeKunde = vorgabeKunde;
    }

    /**
     * @return Returns the vorgabeSCV.
     */
    public Date getVorgabeSCV() {
        return vorgabeSCV;
    }

    /**
     * @param vorgabeSCV The vorgabeSCV to set.
     */
    public void setVorgabeSCV(Date vorgabeSCV) {
        this.vorgabeSCV = vorgabeSCV;
    }

    /**
     * @return Returns the bearbeiter.
     */
    public String getBearbeiter() {
        return bearbeiter;
    }

    /**
     * @param bearbeiter The bearbeiter to set.
     */
    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    @Override
    public Long getAuftragStatusId() {
        return getStatusId();
    }

    @Override
    public void setAuftragStatusId(Long statusId) {
        setStatusId(statusId);
    }

    /**
     * @return Returns the lbzKunde.
     */
    public String getLbzKunde() {
        return lbzKunde;
    }

    /**
     * @param lbzKunde The lbzKunde to set.
     */
    public void setLbzKunde(String lbzKunde) {
        this.lbzKunde = lbzKunde;
    }

    /**
     * @return statusmeldungen - flag das angibt ob statusmeldungen fuer den Auftrag an den Taifun-Workflow gesendet
     * werden sollen
     */
    public Boolean getStatusmeldungen() {
        return statusmeldungen;
    }

    public void setStatusmeldungen(Boolean statusmeldungen) {
        this.statusmeldungen = statusmeldungen;
    }

    public boolean isAutoSmsAndMailVersand() {
        return autoSmsAndMailVersand;
    }

    public void setAutoSmsAndMailVersand(final boolean autoSmsAndMailVersand) {
        this.autoSmsAndMailVersand = autoSmsAndMailVersand;
    }

    /**
     * Feld fuer Speicherung des Wholesale-AuftragsId.
     *
     * @return den Wert des Wholesale-AuftragsId's.
     */
    public String getWholesaleAuftragsId() {
        return wholesaleAuftragsId;
    }

    /**
     * Feld fuer Speicherung des Wholesale-AuftragsId.
     *
     * @param wholesaleAuftragsId
     */
    public void setWholesaleAuftragsId(String wholesaleAuftragsId) {
        this.wholesaleAuftragsId = wholesaleAuftragsId;
    }

    /**
     * Flag das angibt ob es hier um einen Wholesale-Auftrag handelt.
     *
     * @return true wenn das Feld 'wholesaleAuftragsId' belegt ist, anderfalls ist false.
     */
    public boolean isWholesaleAuftrag() {
        return StringUtils.isNotEmpty(this.wholesaleAuftragsId);
    }

    public boolean isStartOperationInPast() {
        return DateTools.isDateBefore(this.getInbetriebnahme(), new Date());
    }
}
