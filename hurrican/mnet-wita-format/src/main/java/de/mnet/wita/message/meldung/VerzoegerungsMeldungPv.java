/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2016
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import javax.persistence.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.validators.VertragsnummerSet;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForMeldung;
import de.mnet.wita.validators.groups.Workflow;
import de.mnet.wita.validators.meldung.AbgebenderProviderSetForMeldung;
import de.mnet.wita.validators.meldung.AnlagenSet;
import de.mnet.wita.validators.meldung.AnschlussSet;
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet;
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet;
import de.mnet.wita.validators.meldung.KundenNummerSet;
import de.mnet.wita.validators.meldung.LeitungSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;
import de.mnet.wita.validators.meldung.VorabstimmungsIdSetMeldung;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("VZM-PV")
@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AnschlussSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet(groups = Workflow.class)
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSet(groups = Workflow.class)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class VerzoegerungsMeldungPv extends Meldung<MeldungsPosition> implements IncomingPvMeldung {

    private LocalDate verzoegerungstermin;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "VERZOEGERUNGSTERMIN")
    public LocalDate getVerzoegerungstermin() {
        return verzoegerungstermin;
    }

    public void setVerzoegerungstermin(LocalDate verzoegerungstermin) {
        this.verzoegerungstermin = verzoegerungstermin;
    }

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.VZM_PV;
    }

    @Override
    public String toString() {

        return "VerzoegerungsMeldungPv [verzoegerungstermin=" + verzoegerungstermin +
                ", toString()=" + super.toString() + "]";
    }

    @Transient
    @Override
    public String getBusinessKey() {
        return getExterneAuftragsnummer();
    }
}
