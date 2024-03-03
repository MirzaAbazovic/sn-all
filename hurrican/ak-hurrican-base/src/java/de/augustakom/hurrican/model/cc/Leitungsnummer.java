/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.2010
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;

/**
 * Modell bildet eine Leitungsnummer ab.
 */
@Entity
@Table(name = "T_LEITUNGSNUMMER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_LEITUNGSNUMMER_0", allocationSize = 1)
public class Leitungsnummer extends AbstractCCIDModel implements DebugModel {

    /**
     * Enumneration für die beiden möglichen Typen
     */
    public enum Typ {
        /**
         * Wert kennzeichnet eine Nürnberger Carrier-Leitungsnummer
         */
        NUE_LBZ("Nürnberger Leitungsnummer"),

        /**
         * DTAG-Leitungsnummer, zu der (hoffentlich) keine elektronische TAL-Bestellung existiert (sonst ist sie hier
         * falsch)
         */
        DTAG_ES_A("DTAG Leitungsnummer Endstelle A"),

        /**
         * DTAG-Leitungsnummer, zu der (hoffentlich) keine elektronische TAL-Bestellung existiert (sonst ist sie hier
         * falsch)
         */
        DTAG_ES_B("DTAG Leitungsnummer Endstelle B"),

        /**
         * Wert kennzeichnet eine sonstige Leitungsnummer
         */
        SONST("Sonstige Leitungsnummer");

        private String display;

        private Typ(String display) {
            this.display = display;
        }

        /**
         * @return gegebene Langbezeichnung
         */
        @Override
        public String toString() {
            return this.display;
        }
    }

    private Long auftragId = null;
    private Typ typ = null;
    private String leitungsnummer = null;

    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "LN_TYP")
    @NotNull
    public Typ getTyp() {
        return typ;
    }

    public void setTyp(Typ typ) {
        this.typ = typ;
    }

    @Column(name = "LEITUNGSNUMMER")
    @NotNull
    public String getLeitungsnummer() {
        return leitungsnummer;
    }

    public void setLeitungsnummer(String leitungsnummer) {
        this.leitungsnummer = leitungsnummer;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && (logger.isDebugEnabled())) {
            logger.debug("Eigenschaften von " + Leitungsnummer.class.getName());
            logger.debug("  Id            :" + getId());
            logger.debug("  AuftragId   :" + getAuftragId());
            logger.debug("  Typ           :" + getTyp());
            logger.debug("  Leitungsnummer:" + getLeitungsnummer());
        }
    }
}
