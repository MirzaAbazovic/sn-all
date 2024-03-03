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
import de.mnet.wita.validators.geschaeftsfall.AbgebenderProviderSetForGeschaeftsfall;
import de.mnet.wita.validators.geschaeftsfall.AktionsCodeSet;
import de.mnet.wita.validators.geschaeftsfall.BestandsSucheSet;
import de.mnet.wita.validators.geschaeftsfall.LeitungsBezeichnungSet;
import de.mnet.wita.validators.geschaeftsfall.MontageleistungSet;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForAuftrag;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKundeSet;
import de.mnet.wita.validators.geschaeftsfall.VormieterSet;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet;
import de.mnet.wita.validators.groups.V1;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue(GeschaeftsfallTyp.KUENDIGUNG_KUNDE_NAME)

@LeitungsBezeichnungSet
@VertragsnummerSet

@AbgebenderProviderSetForGeschaeftsfall(permitted = false)
@AktionsCodeSet(permitted = false)
@BestandsSucheSet(permitted = false)
@MontageleistungSet(permitted = false)
@RufnummernPortierungSetForAuftrag(permitted = false)
@SchaltangabenSet(permitted = false)
@StandortKundeSet(permitted = false)
@StandortKollokationSet(permitted = false)
@ZeitfensterSet.List({
        @ZeitfensterSet(groups = V1.class)
})
@VormieterSet(permitted = false)

@AnlagenGeschaeftsfallValid.List({
        @AnlagenGeschaeftsfallValid(groups = V1.class, kuendigungAllowed = true, numberSonstigeAnlagen = 99),
})
public class GeschaeftsfallKueKd extends Geschaeftsfall {

    private static final long serialVersionUID = -5579181105000195783L;

    @Transient
    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.KUENDIGUNG_KUNDE;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallKueKd [vertragsnummer=" + super.getVertragsNummer() + ", getGeschaeftsfalltyp()="
                + getGeschaeftsfallTyp() + ", getGfAnsprechpartner()=" + getGfAnsprechpartner()
                + ", getKundenwunschtermin()=" + getKundenwunschtermin() + ", getAuftragsPosition()="
                + getAuftragsPosition() + "]";
    }

}
