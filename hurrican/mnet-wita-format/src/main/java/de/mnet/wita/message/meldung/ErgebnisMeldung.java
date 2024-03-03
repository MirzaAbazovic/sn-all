/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 17:42:37
 */
package de.mnet.wita.message.meldung;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.validators.VertragsnummerSet;
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
//@Entity // must not be persisted because Bestandsuebersicht has no workflow where Meldung must be processed

@AbgebenderProviderSetForMeldung(groups = Workflow.class, permitted = false)
@AnlagenSet(groups = Workflow.class, permitted = false)
@AnschlussSet(groups = Workflow.class, permitted = false)
@AufnehmenderProviderSet(groups = Workflow.class, permitted = false)
@ExterneAuftragsnummerSet
@KundenNummerSet(groups = Workflow.class)
@LeitungSet(groups = Workflow.class, permitted = false)
@ProduktPositionenSet(groups = Workflow.class, permitted = false)
@RufnummernPortierungSetForMeldung(groups = Workflow.class, permitted = false)
@VertragsnummerSet(groups = Workflow.class)
@VorabstimmungsIdSetMeldung(groups = Workflow.class, permitted = false)
public class ErgebnisMeldung extends Meldung<MeldungsPosition> implements IncomingTalOrderMeldung {

    private static final long serialVersionUID = 2256641067944197154L;

    private String ergebnislink;

    public ErgebnisMeldung() {
    }

    public ErgebnisMeldung(String externeAuftragsnummer, String kundenNummer, String kundennummerBesteller,
            String vertragsNummer, String ergebnislink) {
        this.setExterneAuftragsnummer(externeAuftragsnummer);
        this.setKundenNummer(kundenNummer);
        this.setKundennummerBesteller(kundennummerBesteller);
        this.setVertragsNummer(vertragsNummer);
        this.ergebnislink = ergebnislink;
    }

    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ERGM;
    }

    @NotNull(message = "Ergebnislink muss gesetzt sein.")
    @Size(groups = WorkflowV1.class, min = 1, max = 512, message = "Ergebnislink muss zwischen {min} und {max} Zeichen haben.")
    @Transient
    public String getErgebnislink() {
        return ergebnislink;
    }

    public void setErgebnislink(String ergebnislink) {
        this.ergebnislink = ergebnislink;
    }

    @Override
    public String toString() {
        return "ErgebnisMeldung [ergebnislink=" + ergebnislink + ", getGeschaeftsfallTyp()="
                + getGeschaeftsfallTyp() + ", getAenderungsKennzeichen()=" + getAenderungsKennzeichen()
                + ", getKundenNummer()=" + getKundenNummer() + ", getKundenNummerBesteller()="
                + getKundennummerBesteller() + ", getVertragsNummer()=" + getVertragsNummer()
                + ", getExterneAuftragsnummer()=" + getExterneAuftragsnummer() + "]";
    }

}
