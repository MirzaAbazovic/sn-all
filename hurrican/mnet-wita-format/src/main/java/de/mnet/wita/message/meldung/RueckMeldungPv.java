/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2011 10:12:48
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
import de.mnet.wita.validators.meldung.LeitungSchleifenwiderstandSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("RUEM-PV")
@AbgebenderProviderSetForMeldung(groups = Workflow.class)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet(groups = Workflow.class)
@KundenNummerSet(groups = Workflow.class)
@KundenNummerBestellerSet(groups = Workflow.class, mandatory = false)
@LeitungAbschnittSet(groups = Workflow.class, permitted = false)
@LeitungSchleifenwiderstandSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSet(groups = Workflow.class)
public class RueckMeldungPv extends Meldung<MeldungsPosition> implements OutgoingMeldung {

    public static final String MELDUNGSCODE = "0021";
    public static final String MELDUNGSTEXT = "Antwort des abgebenden Providers";

    private static final long serialVersionUID = -3461043728455120025L;


    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.RUEM_PV;
    }

    @Override
    public String toString() {
        return "RueckMeldungPv [toString()=" + super.toString() + "]";
    }

}
