/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2011 13:40:32
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
import de.mnet.wita.validators.meldung.AnschlussSet;
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet;
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet;
import de.mnet.wita.validators.meldung.KundenNummerSet;
import de.mnet.wita.validators.meldung.LeitungSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;
import de.mnet.wita.validators.meldung.VorabstimmungsIdSetMeldung;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")

@Entity
@DiscriminatorValue("TAM")

@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AnschlussSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@VertragsnummerSet(groups = Workflow.class)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class TerminAnforderungsMeldung extends Meldung<MeldungsPosition> implements IncomingTalOrderMeldung {

    private static final long serialVersionUID = -8687064546780235255L;
    public static final int TAM_FRIST_TAGE = 10;
    private boolean mahnTam = false;


    public TerminAnforderungsMeldung() {
        // required by Hibernate
    }

    public TerminAnforderungsMeldung(String externeAuftragsnummer, String kundenNummer,
            String kundennummerBesteller, String vertragsNummer) {
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundenNummer);
        setKundennummerBesteller(kundennummerBesteller);
        setVertragsNummer(vertragsNummer);
    }

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.TAM;
    }

    @Column(name = "MAHN_TAM")
    public boolean isMahnTam() {
        return mahnTam;
    }

    public void setMahnTam(boolean mahnTam) {
        this.mahnTam = mahnTam;
    }

    @Override
    public String toString() {
        return "TerminAnforderungsMeldung [mahnTam=" + mahnTam + "toString()=" + super.toString() + "]";
    }

}
