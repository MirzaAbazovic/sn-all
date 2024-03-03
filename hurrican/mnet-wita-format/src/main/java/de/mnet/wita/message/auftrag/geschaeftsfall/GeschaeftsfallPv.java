/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:23
 */
package de.mnet.wita.message.auftrag.geschaeftsfall;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;

import javax.persistence.*;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.validators.AnlagenGeschaeftsfallValid;
import de.mnet.wita.validators.VertragsnummerSet;
import de.mnet.wita.validators.geschaeftsfall.AbgebenderProviderSetForGeschaeftsfall;
import de.mnet.wita.validators.geschaeftsfall.AktionsCodeSet;
import de.mnet.wita.validators.geschaeftsfall.BestandsSucheSet;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForAuftrag;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet;
import de.mnet.wita.validators.geschaeftsfall.VormieterSet;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet;
import de.mnet.wita.validators.groups.V1;


@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue(GeschaeftsfallTyp.PROVIDERWECHSEL_NAME)
@AbgebenderProviderSetForGeschaeftsfall(expectedCarrier = Carrier.OTHER)
@SchaltangabenSet
@StandortKollokationSet
@VertragsnummerSet
@ZeitfensterSet

@AktionsCodeSet(permitted = false)
@BestandsSucheSet(permitted = false)
@RufnummernPortierungSetForAuftrag(permitted = false)
@VormieterSet(permitted = false)
@AnlagenGeschaeftsfallValid.List({
        @AnlagenGeschaeftsfallValid(groups = V1.class, kuendigungAllowed = true, numberSonstigeAnlagen = 99),
})
public class GeschaeftsfallPv extends Geschaeftsfall {

    private static final long serialVersionUID = -3214331816411748652L;

    @Transient
    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return PROVIDERWECHSEL;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallPV [vertragsnummer=" + getVertragsNummer() + ", getGeschaeftsfalltyp()="
                + getGeschaeftsfallTyp() + ", getGfAnsprechpartner()=" + getGfAnsprechpartner()
                + ", getKundenwunschtermin()=" + getKundenwunschtermin() + ", getAuftragsPosition()="
                + getAuftragsPosition() + "]";
    }

}
