/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 10:48:10
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import javax.persistence.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
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

/**
 * Meldungsobjekt, das eine VZM Meldung darstellt.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("VZM")
@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AnschlussSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class VerzoegerungsMeldung extends Meldung<MeldungsPosition> implements IncomingTalOrderMeldung {

    private static final long serialVersionUID = -8595726019409158172L;

    private LocalDate verzoegerungstermin;

    /**
     * Default constructor
     */
    public VerzoegerungsMeldung() {
        // required by Hibernate
    }

    public VerzoegerungsMeldung(String externeAuftragsnummer, String kundenNummer, String kundennummerBesteller,
            String vertragsNummer, LocalDate verzoegerungstermin) {
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundenNummer);
        setKundennummerBesteller(kundennummerBesteller);
        setVertragsNummer(vertragsNummer);

        this.verzoegerungstermin = verzoegerungstermin;
    }

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
        return MeldungsType.VZM;
    }

    @Override
    public String toString() {
        return "VerzoegerungsMeldung [verzoegerungstermin=" + verzoegerungstermin + ", toString()=" + super.toString()
                + "]";
    }

}
