/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 10:48:10
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.AbmSchaltangaben;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.validators.groups.Workflow;
import de.mnet.wita.validators.groups.WorkflowV1;
import de.mnet.wita.validators.meldung.AnlagenSet;
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet;
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet;
import de.mnet.wita.validators.meldung.KundenNummerSet;
import de.mnet.wita.validators.meldung.LeitungAbschnittSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;
import de.mnet.wita.validators.meldung.VorabstimmungsIdSetMeldung;

/**
 * Meldungsobjekt, das eine positive WITA Meldung (ABM) darstellt.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ABM")
@AnlagenSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungAbschnittSet(groups = WorkflowV1.class, mandatory = false, maxOccurrences = 99)
@ProduktPositionenSet.List({
        @ProduktPositionenSet(groups = WorkflowV1.class, maxOccurrences = 99)
})
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class AuftragsBestaetigungsMeldung extends Meldung<MeldungsPositionWithAnsprechpartner> implements
        IncomingTalOrderMeldung {

    private static final long serialVersionUID = 1905684356072852637L;

    private LocalDate verbindlicherLiefertermin;
    private AbmSchaltangaben schaltangaben;

    /**
     * Default constructor
     */
    public AuftragsBestaetigungsMeldung() {
        // used by Hibernate
    }

    public AuftragsBestaetigungsMeldung(String externeAuftragsnummer, String kundenNummer,
            LocalDate verbindlicherLiefertermin) {
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundenNummer);
        this.verbindlicherLiefertermin = verbindlicherLiefertermin;
    }

    @Valid
    @Transient
    // AbmSchaltangaben are used nowhere, therefore transient!
    public AbmSchaltangaben getSchaltangaben() {
        return schaltangaben;
    }

    public void setSchaltangaben(AbmSchaltangaben schaltangaben) {
        this.schaltangaben = schaltangaben;
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "VERBINDLICHER_LIEFERTERMIN")
    @NotNull(groups = Workflow.class, message = "Verbindlicher Liefertermin muss gesetzt sein.")
    public LocalDate getVerbindlicherLiefertermin() {
        return verbindlicherLiefertermin;
    }

    public void setVerbindlicherLiefertermin(LocalDate verbindlicherLiefertermin) {
        this.verbindlicherLiefertermin = verbindlicherLiefertermin;
    }


    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ABM;
    }

    @Override
    public String toString() {
        return "AuftragsBestaetigungsMeldung [verbindlicherLiefertermin=" + verbindlicherLiefertermin
                + ", schaltangaben=" + schaltangaben + ", toString()=" + super.toString() + "]";
    }

}
