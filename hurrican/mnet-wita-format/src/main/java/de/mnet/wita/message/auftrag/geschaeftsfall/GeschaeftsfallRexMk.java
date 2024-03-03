/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:23
 */
package de.mnet.wita.message.auftrag.geschaeftsfall;

import javax.persistence.*;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.validators.AnlagenGeschaeftsfallValid;
import de.mnet.wita.validators.BestandsSucheValid;
import de.mnet.wita.validators.geschaeftsfall.AbgebenderProviderSetForGeschaeftsfall;
import de.mnet.wita.validators.geschaeftsfall.AktionsCodeSet;
import de.mnet.wita.validators.geschaeftsfall.BestandsSucheSet;
import de.mnet.wita.validators.geschaeftsfall.LeitungsBezeichnungSet;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForAuftrag;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKundeSet;
import de.mnet.wita.validators.geschaeftsfall.VorabstimmungsIdSetAuftrag;
import de.mnet.wita.validators.geschaeftsfall.VormieterSet;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet;
import de.mnet.wita.validators.groups.V1;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG_NAME)

@AbgebenderProviderSetForGeschaeftsfall(expectedCarrier = Carrier.DTAG)
@BestandsSucheSet
@RufnummernPortierungSetForAuftrag(message = "Für diesen Geschäftsfall ist eine Rufnummerportierung erforderlich.")
@StandortKundeSet
@ZeitfensterSet

@AktionsCodeSet(permitted = false)
@LeitungsBezeichnungSet(permitted = false)
@StandortKollokationSet(permitted = false)
@VormieterSet(permitted = false)

@BestandsSucheValid(erweitert = true)
// Montageleistung im Modell noch zulassen, da von V4 nach V7 migrierte REX-MKs die Montageleistung noch gesetzt haben!
// @MontageleistungSet(groups = V7.class, permitted = false)
@AnlagenGeschaeftsfallValid.List({
        @AnlagenGeschaeftsfallValid(groups = V1.class, numberSonstigeAnlagen = 99),
})
@VorabstimmungsIdSetAuftrag(permitted = false)
public class GeschaeftsfallRexMk extends Geschaeftsfall {

    private static final long serialVersionUID = -7891669965169014561L;

    @Transient
    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallRexMk [vertragsnummer=" + super.getVertragsNummer() + ", getGeschaeftsfalltyp()="
                + getGeschaeftsfallTyp() + ", getGfAnsprechpartner()=" + getGfAnsprechpartner()
                + ", getKundenwunschtermin()=" + getKundenwunschtermin() + ", getAuftragsPosition()="
                + getAuftragsPosition() + "]";
    }

}
