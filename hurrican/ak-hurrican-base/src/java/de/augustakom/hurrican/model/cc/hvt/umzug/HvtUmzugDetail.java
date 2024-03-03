/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.model.cc.hvt.umzug;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell zur Detailverwaltung für den Umzug eines KVZ innerhalb eines HVT.
 * In dieser Tabelle werden je KVZ eines HVT die Aufträge verwaltet, die auf den neuen KVZ im neuen HVT umgezogen werden sollen.
 */

@Entity
@Table(name = "T_HVT_UMZUG_DETAIL")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HVT_UMZUG_DETAIL_0", allocationSize = 1)
public class HvtUmzugDetail extends AbstractCCIDModel {

    private static final long serialVersionUID = -6641613297802131177L;

    private Long auftragId = null;
    private Long auftragNoOrig = null;
    private String endstellenTyp = null;
    private Long produktId = null;
    private LocalDate witaBereitstellungAm = null;
    private String lbz = null;
    private String uevtStiftAlt = null;
    private String uevtIdStiftNeu = null;
    private Long rangierIdNeu = null;
    private Long rangierAddIdNeu = null;
    private Boolean additionalOrder = null;
    private Boolean rangNeuErzeugt = null;
    private Long cpsTxId = null;
    private Boolean manualCc = null;

    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "PRODAK_ORDER__NO")
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    @Column(name = "ENDSTELLEN_TYP")
    public String getEndstellenTyp() {
        return endstellenTyp;
    }

    public void setEndstellenTyp(String endstellenTyp) {
        this.endstellenTyp = endstellenTyp;
    }

    @Column(name = "PROD_ID")
    public Long getProduktId() {
        return produktId;
    }

    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    @Column(name = "BEREITSTELLUNG_AM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getWitaBereitstellungAm() {
        return witaBereitstellungAm;
    }

    public void setWitaBereitstellungAm(LocalDate witaBereitstellungAm) {
        this.witaBereitstellungAm = witaBereitstellungAm;
    }

    @Column(name = "LBZ")
    public String getLbz() {
        return lbz;
    }

    public void setLbz(String lbz) {
        this.lbz = lbz;
    }

    @Column(name = "UEVT_STIFT_ALT", nullable = false)
    @NotNull(message = "Die UEVT_STIFT_ALT muss gesetzt sein")
    public String getUevtStiftAlt() {
        return uevtStiftAlt;
    }

    public void setUevtStiftAlt(String uevtStiftAlt) {
        this.uevtStiftAlt = uevtStiftAlt;
    }

    @Column(name = "UEVT_STIFT_NEU")
    public String getUevtStiftNeu() {
        return uevtIdStiftNeu;
    }

    public void setUevtStiftNeu(String uevtIdStiftNeu) {
        this.uevtIdStiftNeu = uevtIdStiftNeu;
    }

    @Column(name = "RANGIER_ID_NEU")
    public Long getRangierIdNeu() {
        return rangierIdNeu;
    }

    public void setRangierIdNeu(Long rangierIdNeu) {
        this.rangierIdNeu = rangierIdNeu;
    }

    @Column(name = "RANGIER_ADD_ID_NEU")
    public Long getRangierAddIdNeu() {
        return rangierAddIdNeu;
    }

    public void setRangierAddIdNeu(Long rangierAddIdNeu) {
        this.rangierAddIdNeu = rangierAddIdNeu;
    }

    /**
     * @return true, wenn der ermittelte Auftrag der DTAG nicht bekannt war (d.h. nicht im importierten XLS enthalten war),
     * sondern zusaetzlich ermittelt wurde
     */
    @Column(name = "ADDITIONAL_ORDER")
    public Boolean getAdditionalOrder() {
        return additionalOrder != null ? additionalOrder : Boolean.FALSE;
    }

    public void setAdditionalOrder(Boolean additionalOrder) {
        this.additionalOrder = additionalOrder;
    }

    /**
     * @return true, wenn die geplante Rangierung neu erzeugt wurde.
     */
    @Column(name = "RANG_NEU_ERZEUGT")
    public Boolean getRangNeuErzeugt() {
        return rangNeuErzeugt != null ? rangNeuErzeugt : Boolean.FALSE;
    }

    public void setRangNeuErzeugt(Boolean rangNeuErzeugt) {
        this.rangNeuErzeugt = rangNeuErzeugt;
    }

    @Column(name = "CPS_TX_ID")
    public Long getCpsTxId() {
        return cpsTxId;
    }

    public void setCpsTxId(Long cpsTxId) {
        this.cpsTxId = cpsTxId;
    }

    /**
     * @return true = manuelle CCs auf altem Port existieren, false = default CCs, null = keine CCs notwendig
     */
    @Column(name = "MANUAL_CC")
    @CheckForNull
    public Boolean getManualCc() {
        return manualCc;
    }

    public void setManualCc(Boolean manualCc) {
        this.manualCc = manualCc;
    }

    @Transient
    public Set<Long> getRangierIdsNeu() {
        Set<Long> result = new HashSet<>();
        if (getRangierIdNeu() != null) {
            result.add(getRangierIdNeu());
        }
        if (getRangierAddIdNeu() != null) {
            result.add(getRangierAddIdNeu());
        }
        return result;
    }

}
