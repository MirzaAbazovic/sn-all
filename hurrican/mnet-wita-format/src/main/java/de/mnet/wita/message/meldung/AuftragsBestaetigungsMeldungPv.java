/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 14:14:01
 */
package de.mnet.wita.message.meldung;

import javax.persistence.*;

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

/**
 * Meldungsobjekt, das eine Bestätigung des Providerwechsels an den abgebenden Provider darstellt.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ABM-PV")
@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class)
@ExterneAuftragsnummerSet(groups = Workflow.class)
@KundenNummerSet(groups = Workflow.class, permitted = false)
@KundenNummerBestellerSet(groups = Workflow.class, permitted = false)
@LeitungAbschnittSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSet
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class AuftragsBestaetigungsMeldungPv extends Meldung<MeldungsPosition> implements IncomingPvMeldung {

    private static final long serialVersionUID = 2683514427365191213L;

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ABM_PV;
    }

    @Transient
    @Override
    public String getBusinessKey() {
        return getExterneAuftragsnummer();
    }

    @Override
    public String toString() {
        return "AuftragsBestaetigungsMeldungPv [toString()=" + super.toString() + "]";
    }
}
