/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 16:20:58
 */
package de.mnet.wita.message;

import javax.persistence.*;

/**
 * Objekt fuer eine WITA Storno Meldung
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("Storno")
public class Storno extends MnetWitaRequest {

    private static final long serialVersionUID = -577334857746464837L;

    public Storno() {
        // required by Hibernate
    }

    /**
     * Erstellt ein Storno zu einem Auftrag
     */
    public Storno(Auftrag auftrag) {
        setCbVorgangId(auftrag.getCbVorgangId());
        setExterneAuftragsnummer(auftrag.getExterneAuftragsnummer());

        setGeschaeftsfall(auftrag.getGeschaeftsfall());
        setKunde(auftrag.getKunde());
        setBesteller(auftrag.getBesteller());
        setCdmVersion(auftrag.getCdmVersion());
    }

    @Override
    @Transient
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.STORNO;
    }

    @Override
    @Transient
    public boolean isStorno() {
        return true;
    }

    @Override
    public String toString() {
        return "Storno [toString()=" + super.toString() + "]";
    }
}
