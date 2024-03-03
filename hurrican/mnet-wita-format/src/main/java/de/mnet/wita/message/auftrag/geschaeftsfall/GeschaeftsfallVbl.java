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
import de.mnet.wita.validators.BestandsSucheValid;
import de.mnet.wita.validators.geschaeftsfall.AbgebenderProviderSetForGeschaeftsfall;
import de.mnet.wita.validators.geschaeftsfall.AktionsCodeSet;
import de.mnet.wita.validators.geschaeftsfall.LeitungsBezeichnungSet;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet;
import de.mnet.wita.validators.geschaeftsfall.VertragsnummerOrBestandsSucheSet;
import de.mnet.wita.validators.geschaeftsfall.VormieterSet;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet;
import de.mnet.wita.validators.groups.V1;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue(GeschaeftsfallTyp.VERBUNDLEISTUNG_NAME)
@SchaltangabenSet
@StandortKollokationSet
@VertragsnummerOrBestandsSucheSet
@ZeitfensterSet

@AbgebenderProviderSetForGeschaeftsfall(checkCarrier = false)
@AktionsCodeSet(permitted = false)
@LeitungsBezeichnungSet(permitted = false)
@VormieterSet(permitted = false)

@BestandsSucheValid
@AnlagenGeschaeftsfallValid.List({
        @AnlagenGeschaeftsfallValid(groups = V1.class, kuendigungAllowed = true, numberSonstigeAnlagen = 99),
})
public class GeschaeftsfallVbl extends Geschaeftsfall {

    private static final long serialVersionUID = -7891669965169014561L;

    @Transient
    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VERBUNDLEISTUNG;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallVbl [vertragsnummer=" + super.getVertragsNummer() + ", getGeschaeftsfalltyp()="
                + getGeschaeftsfallTyp() + ", getGfAnsprechpartner()=" + getGfAnsprechpartner()
                + ", getKundenwunschtermin()=" + getKundenwunschtermin() + ", getAuftragsPosition()="
                + getAuftragsPosition() + "]";
    }

}
