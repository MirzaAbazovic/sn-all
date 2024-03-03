/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 15:03:12
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Definition von Connect-Daten.
 */
@Entity
@Table(name = "T_AUFTRAG_CONNECT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_CONNECT_0", allocationSize = 1)
public class AuftragConnect extends AbstractCCHistoryModel implements CCAuftragModel {

    private Long auftragId;

    // Legacy-Daten aus der KuP
    private String produktcode;
    private String produktspezifikation;
    private String projektleiter;
    private String projektleiterKuendigung;
    private String tts;
    private String verfuegbarkeit;

    public AuftragConnect() {
        setGueltigVon(new Date());
        setGueltigBis(DateTools.getHurricanEndDate());
    }

    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getProduktcode() {
        return produktcode;
    }

    public void setProduktcode(String produktcode) {
        this.produktcode = produktcode;
    }

    public String getProduktspezifikation() {
        return produktspezifikation;
    }

    public void setProduktspezifikation(String produktspezifikation) {
        this.produktspezifikation = produktspezifikation;
    }

    public String getProjektleiter() {
        return projektleiter;
    }

    public void setProjektleiter(String projektleiter) {
        this.projektleiter = projektleiter;
    }

    @Column(name = "PROJEKTLEITER_KUENDIGUNG")
    public String getProjektleiterKuendigung() {
        return projektleiterKuendigung;
    }

    public void setProjektleiterKuendigung(String projektleiterKuendigung) {
        this.projektleiterKuendigung = projektleiterKuendigung;
    }

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public String getVerfuegbarkeit() {
        return verfuegbarkeit;
    }

    public void setVerfuegbarkeit(String verfuegbarkeit) {
        this.verfuegbarkeit = verfuegbarkeit;
    }

}
