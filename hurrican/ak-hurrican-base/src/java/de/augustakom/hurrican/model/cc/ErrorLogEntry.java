package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;

import de.augustakom.common.model.AbstractObservable;


/**
 * Entity for T_ERROR_LOG table
 *
 */
@Entity
@Table(name = "T_ERROR_LOG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_ERROR_LOG", allocationSize = 1)
public final class ErrorLogEntry extends AbstractObservable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private Long id;

    @Column(name = "ERROR_NAME")
    private String errorName;

    @Column(name = "ERROR_DESCRIPTION")
    private String errorDescription;

    @Column(name = "SERVICE")
    private String service;

    @Column(name = "STACKTRACE")
    private String stacktrace;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT")
    private Date createdAt;

    public ErrorLogEntry() {
    }

    public ErrorLogEntry(String errorName, String errorDescription, String service, String stacktrace) {
        this.errorName = errorName;
        this.errorDescription = errorDescription;
        this.service = service;
        setStacktrace(stacktrace);
        this.createdAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        // truncate to db field size
        this.stacktrace = (stacktrace != null && stacktrace.length() > 2000) ? stacktrace.substring(0, 2000) : stacktrace;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ErrorLogEntry logEntry = (ErrorLogEntry) o;

        if (id != null ? !id.equals(logEntry.id) : logEntry.id != null)
            return false;
        if (errorName != null ? !errorName.equals(logEntry.errorName) : logEntry.errorName != null)
            return false;
        if (errorDescription != null ? !errorDescription.equals(logEntry.errorDescription) : logEntry.errorDescription != null)
            return false;
        if (service != null ? !service.equals(logEntry.service) : logEntry.service != null)
            return false;
        if (stacktrace != null ? !stacktrace.equals(logEntry.stacktrace) : logEntry.stacktrace != null)
            return false;
        return !(createdAt != null ? !createdAt.equals(logEntry.createdAt) : logEntry.createdAt != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (errorName != null ? errorName.hashCode() : 0);
        result = 31 * result + (errorDescription != null ? errorDescription.hashCode() : 0);
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (stacktrace != null ? stacktrace.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ErrorLogEntry{" +
                "id=" + id +
                ", errorName='" + errorName + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                ", geraeteBez='" + service + '\'' +
                ", serialNumber='" + stacktrace + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
