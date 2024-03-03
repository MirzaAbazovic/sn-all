/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 11:56:13
 */
package de.mnet.wita.model;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.base.Function;
import org.apache.commons.lang.NotImplementedException;

import de.augustakom.common.model.AbstractObservable;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.message.MeldungsType;

/**
 * DTO zur Anzeige der Abgebenden Leitungen
 */
public class AbgebendeLeitungenVorgang extends AbstractObservable implements Vorgang, CCAuftragModel, Serializable {

    private static final long serialVersionUID = 8159007898373203487L;

    private static final DateTimeFormatter EMPFANGEN_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
    private static final DateTimeFormatter KUE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static final Function<AbgebendeLeitungenVorgang, Boolean> GET_PRIO = new Function<AbgebendeLeitungenVorgang, Boolean>() {

        @Override
        public Boolean apply(AbgebendeLeitungenVorgang from) {
            return (from.getPrio() != null) ? from.getPrio() : Boolean.FALSE;
        }
    };

    public static final Function<AbgebendeLeitungenVorgang, LocalDate> GET_ANTWORT_FRIST = new Function<AbgebendeLeitungenVorgang, LocalDate>() {

        @Override
        public LocalDate apply(AbgebendeLeitungenVorgang from) {
            return from.getAntwortFrist();
        }
    };

    public static final Function<AbgebendeLeitungenVorgang, LocalDateTime> GET_EMPFANGEN = new Function<AbgebendeLeitungenVorgang, LocalDateTime>() {

        @Override
        public LocalDateTime apply(AbgebendeLeitungenVorgang from) {
            return from.getEmpfangen();
        }
    };

    private AbgebendeLeitungenUserTask abgebendeLeitungenUserTask;
    private String niederlassung;

    /**
     * Display Auftrag Daten werden in der Tasklist angezeigt.
     */
    private AuftragDaten displayAuftragDaten;
    private List<AuftragDaten> auftragDaten;
    private String originalBearbeiter;
    private String vbz;
    private Boolean klaerfall = Boolean.FALSE;
    private String klaerfallBemerkung;
    private MeldungsType lastMeldungsType;
    private Boolean zustimmungProviderWechsel;

    public AbgebendeLeitungenVorgang(AbgebendeLeitungenUserTask abgebendeLeitungenUserTask) {
        // required by HQL Query in TaskDaoImpl
        this.abgebendeLeitungenUserTask = abgebendeLeitungenUserTask;
    }

    public AbgebendeLeitungenVorgang() {
        // required by Hibernate
    }

    public LocalDate getKueDatum() {
        if (abgebendeLeitungenUserTask == null) {
            return null;
        }
        return abgebendeLeitungenUserTask.getKuendigungsDatum4Gui();
    }

    public String getKueDatumAsString() {
        if (getKueDatum() == null) {
            return null;
        }
        return getKueDatum().format(KUE_FORMATTER);
    }

    public LocalDateTime getEmpfangen() {
        if (abgebendeLeitungenUserTask == null) {
            return null;
        } else {
            return DateConverterUtils.asLocalDateTime(abgebendeLeitungenUserTask.getEmpfangsDatum());
        }
    }

    /**
     * Used by Hurrican GUI - with reflection call! So don´t delete even if it is shown as 'not used'
     * @return
     */
    public String getEmpfangenAsString() {
        if (getEmpfangen() == null) {
            return null;
        }
        return getEmpfangen().format(EMPFANGEN_FORMATTER);
    }

    @Override
    public boolean isImportant() {
        return getPrio() || ((getAntwortFrist() != null) && getAntwortFrist().isBefore((LocalDate.now()).plusDays(1)));
    }

    @Override
    public AbgebendeLeitungenUserTask getUserTask() {
        return abgebendeLeitungenUserTask;
    }

    public void setUserTask(AbgebendeLeitungenUserTask abgebendeLeitungenUserTask) {
        this.abgebendeLeitungenUserTask = abgebendeLeitungenUserTask;
    }

    @Override
    public Date getLetzteAenderung() {
        if (getUserTask() == null) {
            return null;
        }
        return getUserTask().getLetzteAenderung();
    }

    @Override
    public String getTaskBearbeiter() {
        if ((abgebendeLeitungenUserTask == null) || (abgebendeLeitungenUserTask.getBearbeiter() == null)) {
            return null;
        }
        return abgebendeLeitungenUserTask.getBearbeiter().getLoginName();
    }

    @Override
    public String getTaskBearbeiterTeam() {
        if ((abgebendeLeitungenUserTask == null) || (abgebendeLeitungenUserTask.getBearbeiter() == null)
                || (abgebendeLeitungenUserTask.getBearbeiter().getTeam() == null)) {
            return null;
        }
        return abgebendeLeitungenUserTask.getBearbeiter().getTeam().getName();
    }

    public Long getAuftragNoOrig() {
        return (getDisplayAuftragDaten() != null) ? getDisplayAuftragDaten().getAuftragNoOrig() : null;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public String getExterneAuftragsnummer() {
        return abgebendeLeitungenUserTask.getExterneAuftragsnummer();
    }

    public List<AuftragDaten> getAuftragDaten() {
        return auftragDaten;
    }

    public void setAuftragDaten(List<AuftragDaten> auftragDaten) {
        this.auftragDaten = auftragDaten;
    }

    public String getOriginalBearbeiter() {
        return originalBearbeiter;
    }

    public void setOriginalBearbeiter(String originalBearbeiter) {
        this.originalBearbeiter = originalBearbeiter;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public AuftragDaten getDisplayAuftragDaten() {
        return displayAuftragDaten;
    }

    public void setDisplayAuftragDaten(AuftragDaten displayAuftragDaten) {
        this.displayAuftragDaten = displayAuftragDaten;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        throw new NotImplementedException();
    }

    @Override
    public Long getAuftragId() {
        return (displayAuftragDaten != null) ? displayAuftragDaten.getAuftragId() : null;
    }

    public AbgebendeLeitungenUserTask getAbgebendeLeitungenUserTask() {
        return abgebendeLeitungenUserTask;
    }

    public void setLastMeldungType(MeldungsType lastMeldungsType) {
        this.lastMeldungsType = lastMeldungsType;
    }

    public MeldungsType getLastMeldungsType() {
        return lastMeldungsType;
    }

    public String getLastMeldungStatusAsString() {
        if (lastMeldungsType == null) {
            return null;
        }
        else if (lastMeldungsType == MeldungsType.RUEM_PV) {
            return lastMeldungsType.getValue() + " versendet";
        }
        else {
            return lastMeldungsType.getValue() + " erhalten";
        }
    }

    public void setZustimmungProviderWechsel(Boolean zustimmungProviderWechsel) {
        this.zustimmungProviderWechsel = zustimmungProviderWechsel;
    }

    public Boolean getZustimmungProviderWechsel() {
        return zustimmungProviderWechsel;
    }

    public String getLastRuemPvStatusAsString() {
        if (zustimmungProviderWechsel == null) {
            return null;
        }
        else if (zustimmungProviderWechsel) {
            return "Zustimmung";
        }
        else {
            return "Ablehnung";
        }
    }

    public String getAntwortFristAsString() {
        if (abgebendeLeitungenUserTask instanceof AkmPvUserTask) {
            return ((AkmPvUserTask) abgebendeLeitungenUserTask).getAntwortFristAsString();
        }
        return null;
    }

    public LocalDate getAntwortFrist() {
        if (abgebendeLeitungenUserTask instanceof AkmPvUserTask) {
            return ((AkmPvUserTask) abgebendeLeitungenUserTask).getAntwortFrist();
        }
        return null;
    }

    public Boolean getPrio() {
        return abgebendeLeitungenUserTask.getPrio();
    }

    @Override
    public String getAuftragBearbeiter() {
        if (getDisplayAuftragDaten() != null) {
            return getDisplayAuftragDaten().getBearbeiter();
        }
        return null;
    }

    @Override
    public String getAuftragBearbeiterTeam() {
        // AuftragDaten hat keinen AKUser ...
        return null;
    }

    @Override
    public Boolean isKlaerfall() {
        return klaerfall;
    }

    @Override
    public void setKlaerfall(Boolean klaerfall) {
        this.klaerfall = klaerfall;
    }

    @Override
    public String getKlaerfallBemerkung() {
        return klaerfallBemerkung;
    }

    @Override
    public void setKlaerfallBemerkung(String klaerfallBemerkung) {
        this.klaerfallBemerkung = klaerfallBemerkung;
    }

    @Override
    public boolean isKlaerfallSet() {
        return klaerfall != null && klaerfall;
    }

}
