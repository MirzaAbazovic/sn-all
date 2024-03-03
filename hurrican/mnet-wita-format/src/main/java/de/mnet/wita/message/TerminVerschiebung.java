/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 16:20:58
 */
package de.mnet.wita.message;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * TerminVerschiebung-Anfrage
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS",
        justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("TV")
public class TerminVerschiebung extends MnetWitaRequest {

    private static final long serialVersionUID = -7332963219597083367L;

    private LocalDate termin;

    public TerminVerschiebung() {
        // required by Hibernate
    }

    /**
     * Erstellt eine Terminverschiebung zu einem Auftrag
     */
    public TerminVerschiebung(Auftrag auftrag, LocalDate termin) {
        setCbVorgangId(auftrag.getCbVorgangId());
        setExterneAuftragsnummer(auftrag.getExterneAuftragsnummer());
        setGeschaeftsfall(auftrag.getGeschaeftsfall());
        setKunde(auftrag.getKunde());
        setBesteller(auftrag.getBesteller());
        setCdmVersion(auftrag.getCdmVersion());
        this.termin = termin;
    }

    public void setTermin(LocalDate termin) {
        this.termin = termin;
    }

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getTermin() {
        return termin;
    }

    @Override
    @Transient
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.TV;
    }

    @Override
    @Transient
    public boolean isTv() {
        return true;
    }

    @Override
    public String toString() {
        return "TerminVerschiebung [termin=" + termin + " " + super.toString() + "]";
    }

}
