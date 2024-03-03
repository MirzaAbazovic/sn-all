/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 08:57:06
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Modelklasse zur Speicherung von Wholesale-Auftraeg bezogene Informationen.
 */
@Entity
@Table(name = "T_AUFTRAG_WHOLESALE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_WHOLESALE_0", allocationSize = 1)
public class AuftragWholesale extends AbstractCCIDModel implements CCAuftragModel {

    private static final long serialVersionUID = 6087372071685235614L;

    private Long auftragId;

    /**
     * Id des Wholesale-Auftrags (HER_12345678). Hermes kann über diesem Id Hurrican Responses intern zuordnen.
     *
     * @return den Wert des Wholesale-AuftragsId's.
     */
    private String wholesaleAuftragsId = null;

    /**
     * Ausfuehrungstermin (Datum) des Wholesale-Auftrags.
     */
    private LocalDate executionDate;

    /**
     * Ausfuehrungstermin (Startzeit) des Wholesale-Auftrags.
     */
    private LocalTime executionTimeBegin;

    /**
     * Ausfuehrungstermin (Endzeit) des Wholesale-Auftrags.
     */
    private LocalTime executionTimeEnd;

    @Column(name = "AUFTRAG_ID")
    @NotNull
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Nullable
    @Column(name = "WHOLESALE_AUFTRAG_ID")
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
    @Transient
    public boolean isWholesaleAuftrag() {
        return StringUtils.isNotEmpty(this.wholesaleAuftragsId);
    }

    @Column(name = "EXECUTION_TIME_END")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalTimeAsString")
    public LocalTime getExecutionTimeEnd() {
        return executionTimeEnd;
    }

    public void setExecutionTimeEnd(LocalTime executionTimeEnd) {
        this.executionTimeEnd = executionTimeEnd;
    }

    @Column(name = "EXECUTION_TIME_BEGIN")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalTimeAsString")
    public LocalTime getExecutionTimeBegin() {
        return executionTimeBegin;
    }

    public void setExecutionTimeBegin(LocalTime executionTimeBegin) {
        this.executionTimeBegin = executionTimeBegin;
    }

    @Column(name = "EXECUTION_DATE")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getExecutionDate() { return executionDate;}

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

}
