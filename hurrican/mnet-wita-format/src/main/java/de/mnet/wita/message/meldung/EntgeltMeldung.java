/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 11:48:10
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.validators.AnlagenMeldungValid;
import de.mnet.wita.validators.VertragsnummerSet;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForMeldung;
import de.mnet.wita.validators.groups.Workflow;
import de.mnet.wita.validators.groups.WorkflowV1;
import de.mnet.wita.validators.meldung.AbgebenderProviderSetForMeldung;
import de.mnet.wita.validators.meldung.AnlagenSet;
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet;
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet;
import de.mnet.wita.validators.meldung.KundenNummerSet;
import de.mnet.wita.validators.meldung.LeitungSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;
import de.mnet.wita.validators.meldung.VorabstimmungsIdSetMeldung;

/**
 * Meldungsobjekt, das eine ENTM Meldung darstellt.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ENTM")
@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet.List({
        @ProduktPositionenSet(groups = WorkflowV1.class, maxOccurrences = 99)
})
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSet(groups = Workflow.class)
@AnlagenSet.List({
        @AnlagenSet(groups = WorkflowV1.class, mandatory = false, maxOccurrences = 99)
})
@AnlagenMeldungValid(groups = Workflow.class)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class EntgeltMeldung extends Meldung<MeldungsPosition> implements IncomingTalOrderMeldung {

    private static final long serialVersionUID = 752073861179727123L;

    private LocalDate entgelttermin;

    public EntgeltMeldung() {
        // required by Hibernate
    }

    public EntgeltMeldung(String externeAuftragsnummer, String kundenNummer, LocalDate entgelttermin,
            String vertragsNummer) {
        this.entgelttermin = entgelttermin;
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundenNummer);
        setVertragsNummer(vertragsNummer);
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull(message = "Entgelttermin muss gesetzt sein.")
    public LocalDate getEntgelttermin() {
        return entgelttermin;
    }

    public void setEntgelttermin(LocalDate entgelttermin) {
        this.entgelttermin = entgelttermin;
    }

    @Override
    public String toString() {
        return "EntgeltMeldung [entgelttermin=" + entgelttermin + ", toString()="
                + super.toString() + "]";
    }

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ENTM;
    }

}
