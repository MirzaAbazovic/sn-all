package de.mnet.wita.message.meldung;

import javax.persistence.*;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
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
@DiscriminatorValue("QEB")

@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AnschlussSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSet(groups = Workflow.class, permitted = false)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class QualifizierteEingangsBestaetigung extends Meldung<MeldungsPositionWithAnsprechpartner> implements
        IncomingTalOrderMeldung {

    private static final long serialVersionUID = -3707708637025376183L;

    public QualifizierteEingangsBestaetigung() {
        // required by Hibernate
    }

    public QualifizierteEingangsBestaetigung(String externeAuftragsnummer, String kundenNummer, String kundennummerBesteller) {
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundenNummer);
        setKundennummerBesteller(kundennummerBesteller);
    }

    @Override
    public String toString() {
        return "QualifizierteEingangsBestaetigung [toString()="
                + super.toString() + "]";
    }

    @Override
    @Transient
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.QEB;
    }

}
