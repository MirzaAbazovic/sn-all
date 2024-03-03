/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 14:15:40
 */
package de.mnet.wita.message.meldung.position;

import javax.persistence.*;
import javax.validation.*;

import de.mnet.wita.AbmMeldungsCode;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS",
        justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("WITH-AP")
public class MeldungsPositionWithAnsprechpartner extends MeldungsPosition {

    private static final long serialVersionUID = 3868051765128093039L;
    private AnsprechpartnerTelekom ansprechpartnerTelekom;

    public MeldungsPositionWithAnsprechpartner() {
        // required by Hibernate
    }

    public MeldungsPositionWithAnsprechpartner(String code, String text, AnsprechpartnerTelekom ansprechpartnerTelekom) {
        super(code, text);
        this.setAnsprechpartnerTelekom(ansprechpartnerTelekom);
    }

    public MeldungsPositionWithAnsprechpartner(AbmMeldungsCode abmMeldung, AnsprechpartnerTelekom ansprechpartnerTelekom) {
        super(abmMeldung.meldungsCode, abmMeldung.meldungsText);
        this.setAnsprechpartnerTelekom(ansprechpartnerTelekom);
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public AnsprechpartnerTelekom getAnsprechpartnerTelekom() {
        return ansprechpartnerTelekom;
    }

    public void setAnsprechpartnerTelekom(AnsprechpartnerTelekom ansprechpartnerTelekom) {
        this.ansprechpartnerTelekom = ansprechpartnerTelekom;
    }

}
