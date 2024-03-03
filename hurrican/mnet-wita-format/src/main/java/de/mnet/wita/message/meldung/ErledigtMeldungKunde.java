/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2011 10:12:48
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import java.util.*;
import javax.persistence.*;

import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
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
@DiscriminatorValue("ERLM-K")
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
public class ErledigtMeldungKunde extends Meldung<MeldungsPosition> implements OutgoingMeldung, TalOrderMeldung {

    private static final long serialVersionUID = -3461043728455120025L;


    public ErledigtMeldungKunde() {
        // required by Hibernate
    }

    public ErledigtMeldungKunde(String vertragsNummer, String externeAuftragsnummer, String kundenNummer,
            String kundennummerBesteller, GeschaeftsfallTyp geschaeftsfallTyp,
            AenderungsKennzeichen aenderungsKennzeichen) {
        setExterneAuftragsnummer(externeAuftragsnummer);
        setKundenNummer(kundenNummer);
        setKundennummerBesteller(kundennummerBesteller);
        setVertragsNummer(vertragsNummer);
        setMeldungsPosition();
        setGeschaeftsfallTyp(geschaeftsfallTyp);
        setAenderungsKennzeichen(aenderungsKennzeichen);
        setVersandZeitstempel(new Date());
    }

    public ErledigtMeldungKunde(String vertragsNummer, Auftrag auftrag) {
        this(vertragsNummer, auftrag.getExterneAuftragsnummer(), auftrag.getKunde()
                        .getKundennummer(), null, auftrag.getGeschaeftsfall().getGeschaeftsfallTyp(),
                AenderungsKennzeichen.STANDARD
        );
        if (auftrag.getBesteller() != null) {
            this.setKundennummerBesteller(auftrag.getBesteller().getKundennummer());
        }
    }

    private void setMeldungsPosition() {
        getMeldungsPositionen().clear();
        MeldungsPosition meldungsPosition = new MeldungsPosition("0015",
                "Endkunde hat die Bereitstellung der Leistung bestätigt. Anschluss störungsfrei in Betrieb");
        addMeldungsPosition(meldungsPosition);
    }

    @Override
    public String toString() {
        return "ErledigtMeldungKunde [toString()=" + super.toString()
                + "]";
    }

    @Transient
    @Override
    public MeldungsType getMeldungsTyp() {
        return MeldungsType.ERLM_K;
    }

}
