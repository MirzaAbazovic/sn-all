/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2011 16:50:24
 */
package de.mnet.wita.model;

import static de.mnet.wita.model.AkmPvUserTask.AkmPvStatus.*;
import static de.mnet.wita.model.UserTask.UserTaskStatus.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;

@Entity
@DiscriminatorValue("AkmPv")
public class AkmPvUserTask extends AbgebendeLeitungenUserTask {

    private static final long serialVersionUID = -7266596382801277502L;

    private static final DateTimeFormatter ANTWORT_FRIST_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private AkmPvStatus akmPvStatus = AKM_PV_EMPFANGEN;
    private String aufnehmenderProvider;
    private LocalDate geplantesKuendigungsDatum;
    private LocalDate antwortFrist;
    private String leitungsBezeichnung;

    /**
     * Achtung, bei Umbennenen von Konstanten die Daten updaten, sowohl die Felder als auch das Constraint
     * T_USER_TASK_AKM_PV_STATUS
     */
    public enum AkmPvStatus {
        AKM_PV_EMPFANGEN,
        RUEM_PV_GESENDET,
        ABM_PV_EMPFANGEN,
        ABBM_PV_EMPFANGEN;

        private static Set<AkmPvStatus> beantwortetStatusSet = ImmutableSet.of(ABM_PV_EMPFANGEN, ABBM_PV_EMPFANGEN);

        public boolean isBeantwortet() {
            return beantwortetStatusSet.contains(this);
        }
    }

    /**
     * Gibt an, ob die AKM-PV abgeholt/abgeschlossen werden kann.
     */
    @Transient
    public boolean isAbschliessbar() {
        return akmPvStatus.isBeantwortet() && (UserTaskStatus.OFFEN == getStatus());
    }

    /**
     * Gibt an, ob gerade eine RueckMeldungPv gesendet werden kann.
     */
    @Transient
    public boolean isRuemPvSendbar() {
        return akmPvStatus == AKM_PV_EMPFANGEN;
    }

    @Column(name = "AKM_PV_STATUS")
    @NotNull
    @Enumerated(EnumType.STRING)
    public AkmPvStatus getAkmPvStatus() {
        return akmPvStatus;
    }

    private void setAkmPvStatus(AkmPvStatus akmPvStatus) {
        this.akmPvStatus = akmPvStatus;
    }

    /**
     * Cannot be done in setter, since Hibernate uses Setter!
     */
    @Transient
    public void changeAkmPvStatus(AkmPvStatus akmPvStatus) {
        setAkmPvStatus(akmPvStatus);
        if (akmPvStatus.isBeantwortet()) {
            setStatus(OFFEN);
            setBearbeiter(null);
        }
        if (akmPvStatus == RUEM_PV_GESENDET) {
            setStatus(GESCHLOSSEN);
        }
    }

    @Override
    @Column(name = "AUFNEHMENDER_PROVIDER")
    public String getAufnehmenderProvider() {
        return aufnehmenderProvider;
    }

    public void setAufnehmenderProvider(String aufnehmenderProvider) {
        this.aufnehmenderProvider = aufnehmenderProvider;
    }

    /**
     * Kuendigungsdatum von der AkmPv bzw. der RuemPv, zu der die Kuendigung stattfinden soll
     */
    @Column(name = "GEPLANTES_KUENDIGUNGS_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getGeplantesKuendigungsDatum() {
        return geplantesKuendigungsDatum;
    }

    public void setGeplantesKuendigungsDatum(LocalDate abmPvKuendigungsDatum) {
        this.geplantesKuendigungsDatum = abmPvKuendigungsDatum;
    }

    @Override
    @Transient
    public boolean kuendigeCarrierbestellung() {
        return akmPvStatus == ABM_PV_EMPFANGEN;
    }

    @Column(name = "ANTWORT_FRIST")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getAntwortFrist() {
        return antwortFrist;
    }

    @Transient
    public String getAntwortFristAsString() {
        if (antwortFrist != null) {
            return antwortFrist.format(ANTWORT_FRIST_FORMATTER);
        }
        return null;
    }

    public void setAntwortFrist(LocalDate antwortFrist) {
        this.antwortFrist = antwortFrist;
    }

    @Column(name = "LEITUNGSBEZEICHNUNG")
    public String getLeitungsBezeichnung() {
        return leitungsBezeichnung;
    }

    public void setLeitungsBezeichnung(String leitungsBezeichnung) {
        this.leitungsBezeichnung = leitungsBezeichnung;
    }

    @Transient
    public <T extends Meldung<?>> void setLeitungsBezeichnungFromAkmPv(T meldung) {
        Preconditions.checkNotNull(meldung);
        Leitung leitung = meldung.getLeitung();
        if (leitung != null) {
            setLeitungsBezeichnung(leitung.getLeitungsBezeichnung().getLeitungsbezeichnungString());
        }
    }

    @Transient
    public <T extends Meldung<?>> void setProviderAndDateFieldsFromAkmPv(T meldung) {
        Preconditions.checkNotNull(meldung);
        AufnehmenderProvider aufnehmenderProvider = meldung.getAufnehmenderProvider();
        if (aufnehmenderProvider != null) {
            setAufnehmenderProvider(aufnehmenderProvider.getProvidernameAufnehmend());
            setGeplantesKuendigungsDatum(aufnehmenderProvider.getUebernahmeDatumGeplant());
            setAntwortFrist(aufnehmenderProvider.getAntwortFrist());
        }
    }

    @Override
    @Transient
    public boolean getPrio() {
        return getAbbmOnAbm();
    }

    @Transient
    public boolean getAbbmOnAbm() {
        // Das Kuendigungsdatum wird nur von der ABM-PV gesetzt
        return (getAkmPvStatus() == ABBM_PV_EMPFANGEN) && (getKuendigungsDatum() != null);
    }

    @Transient
    @Override
    public LocalDate getKuendigungsDatum4Gui() {
        return super.getKuendigungsDatum4Gui() == null ? getGeplantesKuendigungsDatum() : super
                .getKuendigungsDatum4Gui();
    }

    @Override
    public String toString() {
        return "AkmPvUserTask{" +
                "akmPvStatus=" + akmPvStatus +
                ", aufnehmenderProvider='" + aufnehmenderProvider + '\'' +
                ", geplantesKuendigungsDatum=" + geplantesKuendigungsDatum +
                ", antwortFrist=" + antwortFrist +
                ", leitungsBezeichnung='" + leitungsBezeichnung + '\'' +
                '}';
    }
}
