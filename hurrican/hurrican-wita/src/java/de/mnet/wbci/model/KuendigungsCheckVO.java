/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.mnet.wbci.model;

import java.io.*;
import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.KuendigungFrist;
import de.augustakom.hurrican.model.cc.Reference;
import de.mnet.wbci.service.WbciKuendigungsService;

/**
 * Value-Object zur Darstellung von Daten, die fuer einen Kuendigungs-Check herangezogen wurden.
 */
public class KuendigungsCheckVO implements Serializable {

    private static final long serialVersionUID = 4253147539438078111L;

    private LocalDateTime mindestVertragslaufzeitTaifun;
    private LocalDateTime mindestVertragslaufzeitCalculated;
    private List<BAuftragLeistungView> ueberlassungsleistungen;
    private KuendigungFrist kuendigungsfrist;
    private String vertragsverlaengerung;
    private LocalDateTime calculatedEarliestCancelDate;
    private Kuendigungsstatus kuendigungsstatus;

    public LocalDateTime getMindestVertragslaufzeitTaifun() {
        return mindestVertragslaufzeitTaifun;
    }

    public void setMindestVertragslaufzeitTaifun(LocalDateTime mindestVertragslaufzeitTaifun) {
        this.mindestVertragslaufzeitTaifun = mindestVertragslaufzeitTaifun;
    }

    public LocalDateTime getMindestVertragslaufzeitCalculated() {
        return mindestVertragslaufzeitCalculated;
    }

    public void setMindestVertragslaufzeitCalculated(LocalDateTime mindestVertragslaufzeitCalculated) {
        this.mindestVertragslaufzeitCalculated = mindestVertragslaufzeitCalculated;
    }

    public List<BAuftragLeistungView> getUeberlassungsleistungen() {
        return ueberlassungsleistungen;
    }

    public void setUeberlassungsleistungen(List<BAuftragLeistungView> ueberlassungsleistungen) {
        this.ueberlassungsleistungen = ueberlassungsleistungen;
    }

    public KuendigungFrist getKuendigungsfrist() {
        return kuendigungsfrist;
    }

    public void setKuendigungsfrist(KuendigungFrist kuendigungsfrist) {
        this.kuendigungsfrist = kuendigungsfrist;
    }

    public String getVertragsverlaengerung() {
        return vertragsverlaengerung;
    }

    public void setVertragsverlaengerung(String vertragsverlaengerung) {
        this.vertragsverlaengerung = vertragsverlaengerung;
    }

    public LocalDateTime getCalculatedEarliestCancelDate() {
        return calculatedEarliestCancelDate;
    }

    public void setCalculatedEarliestCancelDate(LocalDateTime calculatedEarliestCancelDate) {
        this.calculatedEarliestCancelDate = calculatedEarliestCancelDate;
    }

    public Kuendigungsstatus getKuendigungsstatus() {
        return kuendigungsstatus;
    }

    public void setKuendigungsstatus(Kuendigungsstatus kuendigungsstatus) {
        this.kuendigungsstatus = kuendigungsstatus;
    }

    /**
     * Repräsentiert die verschiedenen Kündigungsstati die ein Taifun-Auftrag haben kann.
     */
    public static enum Kuendigungsstatus {

        /**
         * Der Anschluss ist: <ul> <li>gekündigt</li> <li>noch nicht abgeschaltet</li> <li>Vorlauffrist ist
         * eingehalten</li> </ul>
         */
        GEKUENDIGT_AKTIV_ABW_OK(
                Arrays.asList(GeschaeftsfallTyp.VA_KUE_MRN, GeschaeftsfallTyp.VA_KUE_ORN, GeschaeftsfallTyp.VA_RRNP),
                false,
                "Der Auftrag ist bereits vom Kunden gekündigt.",
                "Ein Anbieterwechsel kann regulär durchgeführt werden.",
                Kuendigungsstatus.RRNP_POSSIBLE,
                null,
                null),

        /**
         * Der Anschluss ist: <ul> <li>gekündigt</li> <li>noch nicht abgeschaltet</li> <li>Vorlauffrist ist knapp
         * unterschritten</li> </ul>
         */
        GEKUENDIGT_AKTIV_ABW_WARNING(
                Arrays.asList(GeschaeftsfallTyp.VA_KUE_MRN, GeschaeftsfallTyp.VA_KUE_ORN, GeschaeftsfallTyp.VA_RRNP),
                false,
                "Der Auftrag ist bereits vom Kunden gekündigt. Die erweiterte Vorlauffrist ( "
                        + (WbciKuendigungsService.ALLOWED_WORKING_DAYS_BEFORE_WECHSELTERMIN_ERROR + 1)
                        + " - "
                        + WbciKuendigungsService.ALLOWED_WORKING_DAYS_BEFORE_WECHSELTERMIN_WARNING
                        + " Tage) für den Anbieterwechsel ist unterschritten.",
                "Es ist manuell zu prüfen, ob ein Anbieterwechsel tatsächlich durchgeführt werden kann!",
                Kuendigungsstatus.RRNP_POSSIBLE,
                "Vorlauffrist unterschritten",
                null
        ),

        /**
         * Der Anschluss ist: <ul> <li>gekündigt</li> <li>noch nicht abgeschaltet</li> <li>Vorlauffrist ist nicht
         * eingehalten</li> </ul>
         */
        GEKUENDIGT_AKTIV_ABW_NICHT_OK(
                Arrays.asList(GeschaeftsfallTyp.VA_RRNP),
                false,
                "Der Auftrag ist bereits vom Kunden gekündigt und die Vorlauffrist ("
                        + WbciKuendigungsService.ALLOWED_WORKING_DAYS_BEFORE_WECHSELTERMIN_ERROR
                        + " Tage) für den Anbieterwechsel ist unterschritten.",
                Kuendigungsstatus.ABW_NOT_POSSIBLE,
                Kuendigungsstatus.RRNP_POSSIBLE,
                "Vorlauffrist unterschritten",
                null
        ),

        /**
         * Der Anschluss ist: <ul> <li>gekündigt</li> <li>abgeschaltet</li> <li>Karenzzeit  ist eingehalten</li> </ul>
         */
        GEKUENDIGT_INAKTIV_PORTIERUNG_OK(
                Arrays.asList(GeschaeftsfallTyp.VA_RRNP),
                false,
                "Anschluss ist gekündigt und abgeschaltet. Ein Anbieterwechsel kann nicht mehr durchgeführt werden.",
                Kuendigungsstatus.ABW_NOT_POSSIBLE,
                Kuendigungsstatus.RRNP_POSSIBLE,
                "Bitte Geschäftsfall VA-RRNP nutzen",
                null),

        /**
         * Der Anschluss ist: <ul> <li>gekündigt</li> <li>abgeschaltet</li> <li>Karenzzeit ist überschritten</li> </ul>
         */
        GEKUENDIGT_INAKTIV_PORTIERUNG_NICHT_OK(
                new ArrayList<GeschaeftsfallTyp>(),
                false,
                "Der Anschluss des Kunden ist bereits gekündigt, abgeschaltet und die Karenzzeit ("
                        + WbciKuendigungsService.ALLOWED_DAYS_AFTER_CANCELLATION
                        + " Tage) ist überschritten. ",
                "Ein Anbieterwechsel ist nicht mehr möglich.",
                "Ein eine Rufnummernportierung ist nicht mehr möglich.",
                "Anschluss gekündigt und abgeschaltet. Nachträgliche Rufnummernportierung ist nicht mehr möglich",
                Reference.REF_ID_ABBM_REASON_TYPE_KARENZZEIT
        ),

        /**
         * Der Anschluss ist PMXer oder eine TK_ANLAGE => Vetrieb
         */
        MANUELL_PMX_OR_TK(
                Arrays.asList(GeschaeftsfallTyp.VA_KUE_MRN, GeschaeftsfallTyp.VA_KUE_ORN, GeschaeftsfallTyp.VA_RRNP),
                true,
                "Der Anschluss des Kunden ist ein PMX- oder TK-Auftrag. Der Kündigungstermin ist über den Vertrieb zu erfragen!",
                null,
                null,
                null,
                null),

        /**
         * Der Kündigunstermin ist über den Vetrieb zu erfragen.
         */
        MANUELL_CONTACT_SALES(
                Arrays.asList(GeschaeftsfallTyp.VA_KUE_MRN, GeschaeftsfallTyp.VA_KUE_ORN, GeschaeftsfallTyp.VA_RRNP),
                true,
                "Der Kündigungstermin konnte nicht ermittelt werden, bitte Kündigungstermin über den Vertrieb erfragen!",
                null,
                null,
                null,
                null),;
        protected static final String RRNP_POSSIBLE = "Die reine Rufnummernportierung ist möglich.";
        protected static final String ABW_NOT_POSSIBLE = "Bitte ggf. eine Vorabstimmung zur reinen Rufnummernportierung (VA-RRNP) senden.";

        private final List<GeschaeftsfallTyp> allowedGFTyps;
        private final boolean askSales;
        private final String infoText;
        private final String additionalTextVaKue;
        private final String additionalTextVaRrnp;
        private final String abbmReason;
        private final Long referenceId;

        private Kuendigungsstatus(List<GeschaeftsfallTyp> allowedGFTyps, boolean askSales, String infoText, String additionalTextVaKue, String additionalTextVaRrnp,
                String abbmReason, Long referenceId) {
            this.allowedGFTyps = allowedGFTyps;
            this.askSales = askSales;
            this.infoText = infoText;
            this.additionalTextVaKue = additionalTextVaKue;
            this.additionalTextVaRrnp = additionalTextVaRrnp;
            this.abbmReason = abbmReason;
            this.referenceId = referenceId;
        }

        public boolean isGeschaeftsfallAllowed(GeschaeftsfallTyp geschaeftsfallTyp) {
            return allowedGFTyps.contains(geschaeftsfallTyp);
        }

        public boolean isAskSales() {
            return askSales;
        }

        public String getInfoText() {
            return getInfoText(null);
        }

        public Long getReferenceId() {
            return referenceId;
        }

        public String getInfoText(GeschaeftsfallTyp gfTyp) {
            // special Handling for different Geschäftsfällle
            if (gfTyp != null) {
                if (GeschaeftsfallTyp.VA_RRNP.equals(gfTyp)) {
                    return infoText + " " + additionalTextVaRrnp;
                }
                else {
                    return infoText + " " + additionalTextVaKue;
                }
            }
            return infoText;
        }

        public String getAbbmReason() {
            return abbmReason;
        }
    }
}
