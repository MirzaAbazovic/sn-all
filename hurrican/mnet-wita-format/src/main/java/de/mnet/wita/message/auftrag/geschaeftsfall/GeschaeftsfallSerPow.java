/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:23
 */
package de.mnet.wita.message.auftrag.geschaeftsfall;

import javax.persistence.*;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.validators.AnlagenGeschaeftsfallValid;
import de.mnet.wita.validators.VertragsnummerSet;
import de.mnet.wita.validators.geschaeftsfall.BestandsSucheSet;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForAuftrag;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKundeSet;
import de.mnet.wita.validators.geschaeftsfall.VorabstimmungsIdSetAuftrag;
import de.mnet.wita.validators.geschaeftsfall.VormieterSet;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet;
import de.mnet.wita.validators.groups.V1;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue(GeschaeftsfallTyp.PORTWECHSEL_NAME)

@SchaltangabenSet
@StandortKollokationSet
@VertragsnummerSet

@BestandsSucheSet(permitted = false)
@RufnummernPortierungSetForAuftrag(permitted = false)
@StandortKundeSet(permitted = false)
@VormieterSet(permitted = false)
@ZeitfensterSet.List({
        @ZeitfensterSet(groups = V1.class)
})
@AnlagenGeschaeftsfallValid.List({
        @AnlagenGeschaeftsfallValid(groups = V1.class, lageplanAllowed = true, numberSonstigeAnlagen = 99),
})
@VorabstimmungsIdSetAuftrag(permitted = false)
public class GeschaeftsfallSerPow extends Geschaeftsfall {

    private static final long serialVersionUID = -7891669965169014561L;

    @Transient
    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.PORTWECHSEL;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallSerPow [vertragsnummer=" + super.getVertragsNummer() + ", getGeschaeftsfalltyp()="
                + getGeschaeftsfallTyp() + ", getGfAnsprechpartner()=" + getGfAnsprechpartner()
                + ", getKundenwunschtermin()=" + getKundenwunschtermin() + ", getAuftragsPosition()="
                + getAuftragsPosition() + "]";
    }

}
