/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2010 16:11:33
 */

package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Definition von UMTS-Daten.
 *
 *
 */
@Entity
@Table(name = "T_AUFTRAG_UMTS")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_UMTS_0", allocationSize = 1)
public class AuftragUMTS extends AbstractCCHistoryUserModel implements CCAuftragModel {

    private Long auftragId;
    private String mobilfunkanbieter = null;
    private String simKartennummer = null;
    private String mobilfunkrufnummer = null;
    private String rahmenvertragsnummer = null;
    private String apn = null;

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

    @Column(name = "MOBILFUNKANBIETER")
    public String getMobilfunkanbieter() {
        return mobilfunkanbieter;
    }

    public void setMobilfunkanbieter(String mobilfunkanbieter) {
        this.mobilfunkanbieter = mobilfunkanbieter;
    }

    @Column(name = "SIM_KARTENNUMMER")
    public String getSimKartennummer() {
        return simKartennummer;
    }

    public void setSimKartennummer(String simKartennummer) {
        this.simKartennummer = simKartennummer;
    }

    @Column(name = "MOBILFUNKRUFNUMMER")
    public String getMobilfunkrufnummer() {
        return mobilfunkrufnummer;
    }

    public void setMobilfunkrufnummer(String mobilfunkrufnummer) {
        this.mobilfunkrufnummer = mobilfunkrufnummer;
    }

    @Column(name = "RAHMENVERTRAGSNUMMER")
    public String getRahmenvertragsnummer() {
        return rahmenvertragsnummer;
    }

    public void setRahmenvertragsnummer(String rahmenvertragsnummer) {
        this.rahmenvertragsnummer = rahmenvertragsnummer;
    }

    @Column(name = "APN")
    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

}
