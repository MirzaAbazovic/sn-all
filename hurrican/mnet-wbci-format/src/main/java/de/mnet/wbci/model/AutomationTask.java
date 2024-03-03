/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.07.2014
 */
package de.mnet.wbci.model;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Type;

/**
 *
 */
@Entity
@Table(name = "T_WBCI_AUTOMATION_TASK")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_AUTOMATION_TASK_0", allocationSize = 1)
public class AutomationTask extends WbciEntity {

    private static final long serialVersionUID = 718474730197195011L;

    public static enum TaskName {
        TAIFUN_NACH_RUEMVA_AKTUALISIEREN(true, false),
        TAIFUN_NACH_AKMTR_AKTUALISIEREN(true, false),
        TAIFUN_NACH_RRNP_AKTUALISIEREN(true, false),
        TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN(true, true),

        WBCI_SEND_AKMTR(true, false),

        WITA_SEND_NEUBESTELLUNG(true, false),
        WITA_SEND_ANBIETERWECHSEL(true, false),
        WITA_SEND_KUENDIGUNG(true, false),
        WITA_SEND_TV(true, true),
        WITA_SEND_STORNO(true, false),

        RUFNUMMER_IN_TAIFUN_ANLEGEN(false, true),
        RUFNUMMER_IN_TAIFUN_ENTFERNEN(false, true),

        /**
         * Nach Absenden einer RUEM-VA alle zu dem Geschaeftsfall gehoerigen Taifun und Hurrican Auftraege kuendigen.
         */
        AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN(true, false),

        /**
         * M-net = abgebend
         * Wenn STR-AUF mit ERLM bestaetigt wird: Auftrags-Kuendigung rueckgaengig machen
         */
        UNDO_AUFTRAG_KUENDIGUNG(true, false),

        ;

        private boolean multipleTask;
        private boolean automatable;

        /**
         * 
         * @param automatable gibt an, ob der AutomationTask komplett automatisch vom System ausgefuehrt werden 
         *                    kann/darf, oder ob es sich eher um eine Art Komfortfunktion fuer die User handelt, die
         *                    sie ueber die GUI aufrufen koennen
         * @param multipleTask gibt an, ob dieser Task fuer einen WBIC GF mehrfach ausgefuehrt werden darf 
         */
        TaskName(boolean automatable, boolean multipleTask) {
            this.multipleTask = multipleTask;
            this.automatable = automatable;
        }

        public boolean isMultipleTask() {
            return multipleTask;
        }

        public boolean isAutomatable() {
            return automatable;
        }
    }

    public static enum AutomationStatus {
        COMPLETED,
        FEATURE_IS_NOT_ENABLED,
        ERROR
    }

    private TaskName name;
    private AutomationStatus status;
    private Date createdAt;
    private Date completedAt;
    private Long userId;
    private String userName;
    private String executionLog;
    private Boolean automatable;
    private Meldung meldung;

    @Enumerated(EnumType.STRING)
    @Column(name = "TASK_NAME")
    public TaskName getName() {
        return name;
    }

    public void setName(TaskName name) {
        this.name = name;
        setAutomatable(name.isAutomatable());
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    public AutomationStatus getStatus() {
        return status;
    }

    public void setStatus(AutomationStatus status) {
        this.status = status;
    }

    @Transient
    public boolean isDone() {
        return completedAt != null;
    }

    public void complete() {
        completedAt  = new Date();
    }

    @Column(name = "CREATED_AT")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "COMPLETED_AT")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "USER_NAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "EXECUTION_LOG", columnDefinition = "CLOB")
    @Lob
    public String getExecutionLog() {
        return executionLog;
    }

    public void setExecutionLog(String log) {
        this.executionLog = log;
    }

    /**
     * Flag gibt an, ob der Task manuell (=false) oder automatisch (=true) ausgefuehrt wurde. <br/>
     * <p/>
     * Eine manuelle Ausfuehrung ist durch den User ueber die GUI getriggert; eine automatische Ausfuehrung vom
     * System (z.B. ein Scheduler oder bei Erhalt einer Nachricht).
     * @return
     */
    @Column(name = "AUTOMATABLE")
    public Boolean getAutomatable() {
        return automatable;
    }

    private void setAutomatable(Boolean automatable) {
        this.automatable = automatable;
    }

    @ManyToOne(targetEntity = Meldung.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "MELDUNG_ID")
    public Meldung getMeldung() {
        return meldung;
    }

    public void setMeldung(Meldung meldung) {
        this.meldung = meldung;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AutomationTask)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        AutomationTask that = (AutomationTask) o;

        if (completedAt != null ? !completedAt.equals(that.completedAt) : that.completedAt != null) {
            return false;
        }
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) {
            return false;
        }
        if (executionLog != null ? !executionLog.equals(that.executionLog) : that.executionLog != null) {
            return false;
        }
        if (name != that.name) {
            return false;
        }
        if (status != that.status) {
            return false;
        }
        if (!automatable.equals(that.automatable)) {
            return false;
        }
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) {
            return false;
        }
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (completedAt != null ? completedAt.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (executionLog != null ? executionLog.hashCode() : 0);
        result = 31 * result + (automatable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AutomationTask{" +
                "name=" + name +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", executionLog='" + executionLog + '\'' +
                ", automatable=" + automatable +
                '}';
    }

}
