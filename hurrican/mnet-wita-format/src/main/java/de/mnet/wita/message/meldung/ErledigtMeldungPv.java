/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 15:06:08
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.validators.VertragsnummerSet;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForMeldung;
import de.mnet.wita.validators.groups.Workflow;
import de.mnet.wita.validators.meldung.AbgebenderProviderSetForMeldung;
import de.mnet.wita.validators.meldung.AnlagenSet;
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet;
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet;
import de.mnet.wita.validators.meldung.KundenNummerBestellerSet;
import de.mnet.wita.validators.meldung.KundenNummerSet;
import de.mnet.wita.validators.meldung.LeitungAbschnittSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;
import de.mnet.wita.validators.meldung.VorabstimmungsIdSetMeldung;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")

@Entity
@DiscriminatorValue("ERLM-PV")

@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet(groups = Workflow.class)
@KundenNummerSet(groups = Workflow.class)
@KundenNummerBestellerSet(groups = Workflow.class, permitted = false)
@LeitungAbschnittSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSet
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class ErledigtMeldungPv extends Meldung<MeldungsPosition> implements IncomingPvMeldung {

    private static final long serialVersionUID = 7072589164161211023L;

    private LocalDate erledigungstermin;

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ERLM_PV;
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull(message = "Erledigungstermin muss gesetzt sein.")
    public LocalDate getErledigungstermin() {
        return erledigungstermin;
    }

    public void setErledigungstermin(LocalDate erledigungstermin) {
        this.erledigungstermin = erledigungstermin;
    }

    @Transient
    @Override
    public String getBusinessKey() {
        return getExterneAuftragsnummer();
    }

    @Override
    public String toString() {
        return "ErledigtMeldungPv [erledigungstermin="
                + erledigungstermin + ", toString()=" + super.toString() + "]";
    }

}
