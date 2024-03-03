/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 14:32:32
 */
package de.augustakom.hurrican.model.exceptions;

import static com.google.common.base.Preconditions.*;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.annotations.Type;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.common.tools.system.SystemInformation;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell fuer die Eintraege im Exception-Log
 */
@Entity
@Table(name = "T_EXCEPTION_LOG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_EXCEPTION_LOG_0", allocationSize = 1)
public class ExceptionLogEntry extends AbstractCCIDModel {

    private static final long serialVersionUID = -8681763990536542831L;

    public static final String DATE_OCCURED_FIELD = "dateOccurred";
    public static final String BEARBEITER_FIELD = "bearbeiter";
    public static final String SOLUTION_FIELD = "solution";
    public static final String CONTEXT = "context";

    private String context;
    private String host;
    private String errorMessage;
    private String stacktrace;
    private Date dateOccurred;
    private String bearbeiter;
    private String solution;

    public ExceptionLogEntry() {
        // Required by Hibernate
    }

    public ExceptionLogEntry(ExceptionLogEntryContext context, String errorMessage) {
        this(context, errorMessage, null);
    }

    public ExceptionLogEntry(ExceptionLogEntryContext context, String errorMessage, Throwable throwable) {
        checkNotNull(errorMessage);
        setContext(context.identifier);
        setErrorMessage(errorMessage);
        if (throwable != null) {
            setStacktrace(ExceptionUtils.getStackTrace(throwable));
        }
        setHostToLocalHost();
    }

    public ExceptionLogEntry(ExceptionLogEntryContext context, Throwable throwable) {
        this(context, throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getName(), throwable);
        checkNotNull(throwable);
    }

    @Transient
    private void setHostToLocalHost() {
        this.host = SystemInformation.getLocalHostName();
    }

    @Column(name = "CONTEXT")
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @NotNull
    @Column(name = "HOST")
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @NotNull
    @Size(max = 2048)
    @Column(name = "ERROR_MESSAGE")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = StringUtils.abbreviate(errorMessage, 2048);
    }

    @Column(name = "STACKTRACE")
    @Lob
    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    @NotNull
    @Column(name = "DATE_OCCURED")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getDateOccurred() {
        return dateOccurred;
    }

    public void setDateOccurred(Date dateOccurred) {
        this.dateOccurred = dateOccurred;
    }

    @Column(name = "BEARBEITER")
    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    @Column(name = "SOLUTION")
    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

}
