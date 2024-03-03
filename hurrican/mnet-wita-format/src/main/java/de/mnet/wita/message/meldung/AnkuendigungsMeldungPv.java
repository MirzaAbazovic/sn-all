/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2011 10:12:48
 */
package de.mnet.wita.message.meldung;

import javax.persistence.*;
import javax.validation.*;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.validators.AnlagenMeldungValid;
import de.mnet.wita.validators.VertragsnummerSet;
import de.mnet.wita.validators.groups.Workflow;
import de.mnet.wita.validators.groups.WorkflowV1;
import de.mnet.wita.validators.meldung.AbgebenderProviderSetForMeldung;
import de.mnet.wita.validators.meldung.AnlagenSet;
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet;
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet;
import de.mnet.wita.validators.meldung.KundenNummerBestellerSet;
import de.mnet.wita.validators.meldung.KundenNummerSet;
import de.mnet.wita.validators.meldung.LeitungAbschnittSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("AKM-PV")
@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class)
@ExterneAuftragsnummerSet(groups = Workflow.class)
@KundenNummerSet(groups = Workflow.class)
@KundenNummerBestellerSet(groups = Workflow.class, permitted = false)
@LeitungAbschnittSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@VertragsnummerSet
@AnlagenSet.List({
        @AnlagenSet(groups = WorkflowV1.class, mandatory = false, maxOccurrences = 99)
})
@AnlagenMeldungValid(groups = Workflow.class)
public class AnkuendigungsMeldungPv extends Meldung<MeldungsPosition> implements IncomingPvMeldung {

    private static final long serialVersionUID = -3461043728455120025L;

    private Kundenname endkunde;

    @Transient
    @Override
    public String getBusinessKey() {
        return getExterneAuftragsnummer();
    }

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.AKM_PV;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @Valid
    public Kundenname getEndkunde() {
        return endkunde;
    }

    public void setEndkunde(Kundenname endkunde) {
        this.endkunde = endkunde;
    }

    @Override
    public String toString() {
        return "AnkuendigungsMeldungPv [endkunde=" + endkunde + ", toString()=" + super.toString() + "]";
    }
}
