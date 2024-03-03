/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2011 13:40:32
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.activiti.CanOpenActivitiWorkflow;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.validators.VertragsnummerSetForErlm;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForMeldung;
import de.mnet.wita.validators.groups.Workflow;
import de.mnet.wita.validators.groups.WorkflowV1;
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
@DiscriminatorValue("ERLM")
@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AnschlussSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSetForErlm(groups = Workflow.class)
@ProduktPositionenSet(groups = WorkflowV1.class, maxOccurrences = 99, mandatory = false)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class ErledigtMeldung extends Meldung<MeldungsPosition> implements IncomingTalOrderMeldung,
        CanOpenActivitiWorkflow {

    private static final long serialVersionUID = -8687064546780235255L;

    private LocalDate erledigungstermin;

    public ErledigtMeldung() {
        // required by Hibernate
    }

    public ErledigtMeldung(String externeAuftragsnummer, String kundenNummer, String kundennummerBesteller) {
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundenNummer);
        setKundennummerBesteller(kundennummerBesteller);
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull(message = "Erledigungstermin muss gesetzt sein.")
    public LocalDate getErledigungstermin() {
        return erledigungstermin;
    }

    public void setErledigungstermin(LocalDate erledigungstermin) {
        this.erledigungstermin = erledigungstermin;
    }

    @Override
    public String toString() {
        return "ErledigtMeldung [erledigungstermin=" + erledigungstermin
                + ", toString()=" + super.toString() + "]";
    }

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ERLM;
    }

    @Transient
    @Override
    public String getBusinessKey() {
        return getExterneAuftragsnummer();
    }
}
