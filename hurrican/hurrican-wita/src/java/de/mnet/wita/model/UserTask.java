/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 14:47:24
 */
package de.mnet.wita.model;

import static de.mnet.wita.model.UserTask.UserTaskStatus.*;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell zur Abbildung der TAM/KueDt/AKM-PV-Bearbeitung. Es kann nur einen Usertasks zu einem Vorgang geben.
 */
@Entity
@Table(name = "T_USER_TASK")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "VORGANG_TYP", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_USER_TASK_0", allocationSize = 1)
public abstract class UserTask extends AbstractCCIDModel {

    private static final long serialVersionUID = -2234405635264379958L;

    public enum UserTaskStatus {
        OFFEN("Offen"),
        GESCHLOSSEN("Geschlossen");

        private final String display;

        private UserTaskStatus(String display) {
            this.display = display;
        }

        /**
         * @return spezielle Bezeichnung zum Anzeigen des Enum
         */
        public String getDisplay() {
            return display;
        }
    }

    /**
     * OFFEN -> Task wird angezeigt, Geschlossen -> Task wird nicht angezeigt
     */
    private UserTaskStatus status = UserTaskStatus.OFFEN;
    private UserTaskStatus statusLast = null;
    private Long userId;
    // @Transient
    private AKUser bearbeiter;

    /**
     * Letzte Aenderung des Usertasks
     */
    private Date letzteAenderung;
    /**
     * Bemerkungen zur Bearbeitung (bisher nur bei TAM verwendet)
     */
    private String bemerkungen;

    /**
     * Wiedervorlage an dem genannten Datum
     */
    private Date wiedervorlageAm;

    @Transient
    public boolean isClosed() {
        return getStatus() == GESCHLOSSEN;
    }

    @Column(name = "LETZTE_AENDERUNG")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getLetzteAenderung() {
        return letzteAenderung;
    }

    public void setLetzteAenderung(Date letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    @Column(name = "STATUS")
    @NotNull
    @Enumerated(EnumType.STRING)
    public UserTaskStatus getStatus() {
        return status;
    }

    public void setStatus(UserTaskStatus status) {
        this.status = status;
    }

    @Column(name = "STATUS_LAST")
    @Enumerated(EnumType.STRING)
    public UserTaskStatus getStatusLast() {
        return statusLast;
    }

    public void setStatusLast(UserTaskStatus statusLast) {
        this.statusLast = statusLast;
    }

    @Column(name = "BEMERKUNGEN")
    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    /**
     * ID des (M-net) Users, der den Task bearbeitet.
     *
     * @return Returns the userId.
     */
    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    /**
     * Nur für Hibernate. Aus Anwendung heraus sollte {@link #setBearbeiter(AKUser)} verwendet werden.
     *
     * @param userId
     */
    private void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Der Bearbeiter des Tasks. Gespeichert wird nur die ID, siehe {@link #getUserId()}.<br> <b>ACHTUNG:</b> Muss
     * manuell im DAO nachgeladen werden.
     *
     * @return
     */
    @Transient
    public AKUser getBearbeiter() {
        return bearbeiter;
    }

    /**
     * Setzt den Bearbeiter (transient). Intern wird auch die (persistente) userId gesetzt.
     *
     * @param bearbeiter
     */
    public void setBearbeiter(AKUser bearbeiter) {
        this.bearbeiter = bearbeiter;
        setUserId(bearbeiter == null ? null : bearbeiter.getId());
    }

    @Column(name = "WIEDERVORLAGE_AM")
    //    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getWiedervorlageAm() {
        return wiedervorlageAm;
    }

    @Transient
    public LocalDateTime getWiedervorlageAmAsLocalDateTime() {
        return getWiedervorlageAm() != null ? Instant.ofEpochMilli(getWiedervorlageAm().getTime()).atZone((ZoneId.systemDefault())).toLocalDateTime() : null;
    }

    public void setWiedervorlageAm(Date wiedervorlageAm) {
        this.wiedervorlageAm = wiedervorlageAm;
    }

    public void setWiedervorlageAm(LocalDateTime wiedervorlageAmLD) {
        final Date dt = wiedervorlageAmLD != null ? Date.from(wiedervorlageAmLD.atZone(ZoneId.systemDefault()).toInstant()) : null;
        this.setWiedervorlageAm(dt);
    }
}
