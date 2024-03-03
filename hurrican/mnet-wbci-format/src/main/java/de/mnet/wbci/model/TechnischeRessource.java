/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.constraints.CheckTechnischeRessourceMandatoryFields;
import de.mnet.wbci.validation.groups.V1Meldung;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@CheckTechnischeRessourceMandatoryFields(groups = V1Meldung.class)
@Table(name = "T_WBCI_TECHNISCHE_RESSOURCE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_TECH_RESSOURCE_0", allocationSize = 1)
public class TechnischeRessource extends WbciEntity {

    private static final long serialVersionUID = -5936165254213614085L;

    private String vertragsnummer;
    private String lineId;
    private String identifizierer;
    private String tnbKennungAbg;

    @Size(min = 1, max = 10)
    @Column(name = "VERTRAGSNUMMER")
    public String getVertragsnummer() {
        return vertragsnummer;
    }

    public void setVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
    }

    @Size(min = 7, max = 21)
    @Column(name = "LINE_ID")
    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    @Size(min = 1, max = 20)
    @Column(name = "IDENTIFIZIERER")
    public String getIdentifizierer() {
        return identifizierer;
    }

    /**
     * Definiert einen (optionalen) Identifizierer des abgebenden EKP fuer die techn. Ressource.
     *
     * @param identifizierer
     */
    public void setIdentifizierer(String identifizierer) {
        this.identifizierer = identifizierer;
    }

    @Size(min = 4, max = 4)
    @Column(name = "TNB_KENNUNG_ABG")
    public String getTnbKennungAbg() {
        return tnbKennungAbg;
    }

    /**
     * TNB-Kennung des abgebenden Carriers (Kennung gemaess Nummernplan Portierungskennungen der BNetzA). Muss gesetzt
     * werden, wenn das Vorleistungsprodukt ungleich 'NGA' ist.           n Beispiel: D052
     *
     * @param tnbKennungAbg
     */
    public void setTnbKennungAbg(String tnbKennungAbg) {
        this.tnbKennungAbg = tnbKennungAbg;
    }

    @Override
    public String toString() {
        return "TechnischeRessource [vertragsnummer=" + getVertragsnummer() + ", lineId=" + getLineId() +
                ", identifizierer=" + getIdentifizierer() + ", tnbKennungAbg=" + getTnbKennungAbg() + "]";
    }
}
