/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2013 13:34:27
 */
package de.augustakom.hurrican.model.cc.tal;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.annotations.Type;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell zur Abbildung eines Fehler, der bei der automatischen Verarbeitung des {@link CBVorgang}s auftrat.
 */
@Entity
@Table(name = "T_CB_VORGANG_AUTOMATION_ERROR")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_CBV_AUTOMATION_ERROR_0", allocationSize = 1)
public class CBVorgangAutomationError extends AbstractCCIDModel {

    private static final int MAX_ERROR_SIZE = 2047;

    private Date dateOccured;
    private String errorMessage;
    private String stacktrace;

    /**
     * just for hibernate
     */
    public CBVorgangAutomationError() {
    }

    /**
     * @param throwable
     */
    public CBVorgangAutomationError(Throwable throwable) {
        setDateOccured(new Date());
        setErrorMessage(StringUtils.substring(throwable.getMessage(), 0, MAX_ERROR_SIZE));
        setStacktrace(ExceptionUtils.getStackTrace(throwable));
    }

    @NotNull
    @Column(name = "DATE_OCCURED")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getDateOccured() {
        return dateOccured;
    }

    public void setDateOccured(Date dateOccured) {
        this.dateOccured = dateOccured;
    }

    @Size(max = 2048)
    @Column(name = "ERROR_MESSAGE")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Column(name = "STACKTRACE")
    @Lob
    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

}


