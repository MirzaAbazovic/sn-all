/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2011 16:29:20
 */
package de.mnet.wita.model;

import static javax.persistence.CascadeType.*;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Type;

@Entity
public abstract class AbgebendeLeitungenUserTask extends UserTask {

    private static final long serialVersionUID = 941448294950233023L;

    public static final String EXTERNE_AUFTRAGSNUMMER_FIELD = "externeAuftragsnummer";

    private String vertragsNummer;
    private Date empfangsDatum;
    /**
     * Datum, zu der die Kuendigung der Leitung stattfindet
     */
    private LocalDate kuendigungsDatum;
    /**
     * Bei AkmPvUserTasks eine von uns erzeugte eindeutige Nummer
     */
    private String externeAuftragsnummer;
    private Set<UserTask2AuftragDaten> userTaskAuftragDaten = new HashSet<UserTask2AuftragDaten>();
    private Boolean terminverschiebung;

    /**
     * Benachrichtigung an den User, die von der GUI angezeigt wird nach abschliessen des UserTasks Nicht in der DB
     * speichern!
     */
    private String benachrichtigung;

    @Column(name = "EXTERNE_AUFTRAGSNUMMER")
    public String getExterneAuftragsnummer() {
        return externeAuftragsnummer;
    }

    public void setExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
    }

    @Column(name = "VERTRAGSNUMMER")
    public String getVertragsNummer() {
        return vertragsNummer;
    }

    public void setVertragsNummer(String vertragsNummer) {
        this.vertragsNummer = vertragsNummer;
    }

    @Column(name = "EMPFANGS_DATUM")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getEmpfangsDatum() {
        return empfangsDatum;
    }

    public void setEmpfangsDatum(Date empfangsDatum) {
        this.empfangsDatum = empfangsDatum;
    }

    @Column(name = "KUENDIGUNGS_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getKuendigungsDatum() {
        return kuendigungsDatum;
    }

    public void setKuendigungsDatum(LocalDate kuendigungsDatum) {
        this.kuendigungsDatum = kuendigungsDatum;
    }

    @OneToMany(cascade = ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_TASK_ID")
    public Set<UserTask2AuftragDaten> getUserTaskAuftragDaten() {
        return userTaskAuftragDaten;
    }

    @SuppressWarnings("unused")
    private void setUserTaskAuftragDaten(Set<UserTask2AuftragDaten> userTaskAuftragDaten) {
        this.userTaskAuftragDaten = userTaskAuftragDaten;
    }

    @Column(name = "TERMINVERSCHIEBUNG")
    public Boolean getTerminverschiebung() {
        return terminverschiebung;
    }

    public void setTerminverschiebung(Boolean terminverschiebung) {
        this.terminverschiebung = terminverschiebung;
    }

    @Transient
    public Set<Long> getCbIds() {
        return Sets.newHashSet(Iterables.transform(getUserTaskAuftragDaten(), UserTask2AuftragDaten.GET_CB_ID));
    }

    @Transient
    public Set<Long> getAuftragIds() {
        return Sets.newHashSet(Iterables.transform(getUserTaskAuftragDaten(), UserTask2AuftragDaten.GET_AUFTRAG_ID));
    }

    /**
     * Carrier, der die Kuendigung ausloest
     */
    @Transient
    public abstract String getAufnehmenderProvider();

    /**
     * Soll die Carrierbestellung beim abschliessen gekuendigt werden? Wenn nein, so heisst das, dass die
     * Kuendigungsdaten entfernt werden sollen.
     */
    @Transient
    public abstract boolean kuendigeCarrierbestellung();

    @Transient
    public abstract boolean getPrio();

    @Transient
    public String getBenachrichtigung() {
        return benachrichtigung;
    }

    public void setBenachrichtigung(String benachrichtigung) {
        this.benachrichtigung = benachrichtigung;
    }

    @Transient
    public LocalDate getKuendigungsDatum4Gui() {
        return kuendigungsDatum;
    }
}
