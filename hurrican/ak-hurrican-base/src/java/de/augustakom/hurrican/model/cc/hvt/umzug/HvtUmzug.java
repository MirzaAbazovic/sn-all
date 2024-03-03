/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.model.cc.hvt.umzug;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.KvzSperre;

/**
 * Modell um einen HVT-Umzug zu verwalten. <br> In dem Modell werden Informationen zu den jeweiligen KVZ's gehalten, die umgezogen werden sollen.
 * Die einzelnen Aufträge, die je KVZ umgezogen werden, werden in HvtUmzugDetail verwaltet.
 */

@Entity
@Table(name = "T_HVT_UMZUG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HVT_UMZUG_0", allocationSize = 1)
public class HvtUmzug extends AbstractCCIDModel {

    private static final long serialVersionUID = -3939429655409422595L;

    private Long hvtStandort = null;
    private Long hvtStandortDestination = null;
    private String kvzNr = null;
    private String bearbeiter = null;
    private HvtUmzugStatus status = HvtUmzugStatus.OFFEN;
    private LocalDate schalttag = null;
    private Date importAm = null;
    private byte[] excelBlob = null;
    private Set<HvtUmzugDetail> hvtUmzugDetails = new HashSet<>();
    private KvzSperre kvzSperre;

    @Column(name = "HVT_ID_STANDORT", nullable = false)
    @NotNull(message = "Die HVT_ID_STANDORT muss gesetzt sein")
    public Long getHvtStandort() {
        return hvtStandort;
    }

    public void setHvtStandort(Long hvtStandort) {
        this.hvtStandort = hvtStandort;
    }

    @Column(name = "HVT_ID_STANDORT_DEST", nullable = false)
    @NotNull(message = "Die Id des Ziel-HVTs (HVT_ID_STANDORT_DEST) muss gesetzt sein")
    public Long getHvtStandortDestination() {
        return hvtStandortDestination;
    }

    public void setHvtStandortDestination(Long hvtStandortDestination) {
        this.hvtStandortDestination = hvtStandortDestination;
    }


    @Column(name = "KVZ_NUMMER", nullable = false)
    @NotNull(message = "Die KVZ_NUMMER muss gesetzt sein")
    public String getKvzNr() {
        return kvzNr;
    }

    public void setKvzNr(String kvzNr) {
        this.kvzNr = kvzNr;
    }

    @Column(name = "BEARBEITER")
    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    @NotNull
    public HvtUmzugStatus getStatus() {
        return status;
    }

    public void setStatus(HvtUmzugStatus status) {
        this.status = status;
    }

    @Column(name = "SWITCH_AT")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getSchalttag() {
        return schalttag;
    }

    public void setSchalttag(LocalDate schalttag) {
        this.schalttag = schalttag;
    }

    @Column(name = "IMPORT_AT")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getImportAm() {
        return importAm;
    }

    public void setImportAm(Date importAm) {
        this.importAm = importAm;
    }

    @Lob
    @Column(name = "XLSDATA")
    public byte[] getExcelBlob() {
        return excelBlob;   // NOSONAR findbugs:EI_EXPOSE_REP ; ignore sonar warning
    }

    public void setExcelBlob(byte[] excelBlob) {
        this.excelBlob = excelBlob;  // NOSONAR findbugs:EI_EXPOSE_REP2 ; ignore sonar warning
    }


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "HVT_UMZUG_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    public Set<HvtUmzugDetail> getHvtUmzugDetails() {
        return hvtUmzugDetails;
    }

    public void setHvtUmzugDetails(Set<HvtUmzugDetail> hvtUmzugDetails) {
        this.hvtUmzugDetails = hvtUmzugDetails;
    }

    @Transient
    public void addHvtUmzugDetail(final HvtUmzugDetail hvtUmzugDetail) {
        hvtUmzugDetails.add(hvtUmzugDetail);
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "KVZ_SPERRE_ID")
    @Valid
    public KvzSperre getKvzSperre() {
        return kvzSperre;
    }

    public void setKvzSperre(KvzSperre kvzSperre) {
        this.kvzSperre = kvzSperre;
    }

    /**
     * @return {@code true} wenn fuer den HVT-Umzug ein CPS Ausfuehrung erlaubt ist (Planung ist auf Status
     * {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus#AUSGEFUEHRT}).
     */
    @Transient
    public boolean isCpsModifyAllowed() {
        return HvtUmzugStatus.AUSGEFUEHRT.equals(getStatus());
    }

    /**
     * @return {@code true} wenn für {@link de.augustakom.hurrican.service.cc.HvtUmzugService#closeHvtUmzug(Long id)}
     * ein valider Ausgangsstatus vorliegt, anderweitig {@code false}
     */
    @Transient
    public boolean isCloseHvtUmzugAllowed() {
        return HvtUmzugStatus.AUSGEFUEHRT.equals(getStatus());
    }

    @Transient
    public boolean isOffen() {
        return HvtUmzugStatus.OFFEN.equals(getStatus());
    }

    /**
     * @return {@code true} wenn für {@link de.augustakom.hurrican.service.cc.HvtUmzugService#executePlanning(HvtUmzug
     * hvtUmzug, Long sessionId)} ein valider Ausgangsstatus vorliegt, anderweitig {@code false}
     */
    @Transient
    public boolean isExecutePlanningAllowed() {
        return HvtUmzugStatus.PLANUNG_VOLLSTAENDIG.equals(getStatus());
    }

    /**
     * @return {@code true} wenn für {@link de.augustakom.hurrican.service.cc.HvtUmzugService#disableUmzug(Long id)}
     * ein valider Ausgangsstatus vorliegt, anderweitig {@code false}
     */
    @Transient
    public boolean isDisableAllowed() {
        return HvtUmzugStatus.OFFEN.equals(getStatus()) || HvtUmzugStatus.PLANUNG_VOLLSTAENDIG.equals(getStatus());
    }

    /**
     * @return {@code true} wenn für {@link de.augustakom.hurrican.service.cc.HvtUmzugService#automatischePortPlanung(Long
     * hvtUmzugId)} ein valider Ausgangsstatus vorliegt, anderweitig {@code false}
     */
    @Transient
    public boolean isAutomatischePortplanungAllowed() {
        return HvtUmzugStatus.OFFEN.equals(getStatus());
    }

}
