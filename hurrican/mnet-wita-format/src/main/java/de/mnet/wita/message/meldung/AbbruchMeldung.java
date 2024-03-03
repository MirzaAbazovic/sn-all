/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.message.meldung;

import javax.persistence.*;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForMeldung;
import de.mnet.wita.validators.groups.Workflow;
import de.mnet.wita.validators.meldung.AnlagenSet;
import de.mnet.wita.validators.meldung.AnschlussSet;
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet;
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet;
import de.mnet.wita.validators.meldung.KundenNummerSet;
import de.mnet.wita.validators.meldung.LeitungSet;
import de.mnet.wita.validators.meldung.ProduktPositionenSet;
import de.mnet.wita.validators.meldung.VorabstimmungsIdSetMeldung;

/**
 * Meldungs-Objekt, das eine negative WITA Meldung (ABBM) darstellt.
 * <p/>
 * z.Z. nicht geunmarshalled: wiedervorlagetermin, ABBM spezifische Meldungspositionen
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ABBM")

@AnlagenSet(groups = Workflow.class, permitted = false)
@AnschlussSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class AbbruchMeldung extends Meldung<MeldungsPosition> implements IncomingTalOrderMeldung {

    private static final long serialVersionUID = -108129302207675155L;

    public AbbruchMeldung() {
        // for hibernate
    }

    public AbbruchMeldung(String externeAuftragsnummer, String kundennummer) {
        this(externeAuftragsnummer, kundennummer, null);
    }

    public AbbruchMeldung(String externeAuftragsnummer, String kundennummer, String kundennummerBesteller) {
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundennummer);
        setKundennummerBesteller(kundennummerBesteller);
    }

    @Override
    public String toString() {
        return "AbbruchMeldung [meldungsPositionen=" + getMeldungsPositionen() + ", toString()=" + super.toString()
                + "]";
    }

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ABBM;
    }

}
