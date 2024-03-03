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
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForAuftrag;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenOrLbzSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet;
import de.mnet.wita.validators.geschaeftsfall.VorabstimmungsIdSetAuftrag;
import de.mnet.wita.validators.geschaeftsfall.VormieterSet;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet;
import de.mnet.wita.validators.groups.V1;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG_NAME)

@AktionsCodeSet
@SchaltangabenOrLbzSet
@StandortKollokationSet
@VertragsnummerSet
@ZeitfensterSet

@AbgebenderProviderSetForGeschaeftsfall(permitted = false)
@BestandsSucheSet(permitted = false)
@RufnummernPortierungSetForAuftrag(permitted = false)
@VormieterSet(permitted = false)
@AnlagenGeschaeftsfallValid.List({
        @AnlagenGeschaeftsfallValid(groups = V1.class, numberSonstigeAnlagen = 99),
})
@VorabstimmungsIdSetAuftrag(permitted = false)
public class GeschaeftsfallLae extends Geschaeftsfall {

    private static final long serialVersionUID = -5579181105000195783L;

    @Transient
    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.LEISTUNGS_AENDERUNG;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallLae [vertragsnummer=" + getVertragsNummer() + ", getGeschaeftsfalltyp()="
                + getGeschaeftsfallTyp() + ", getGfAnsprechpartner()=" + getGfAnsprechpartner()
                + ", getKundenwunschtermin()=" + getKundenwunschtermin() + ", getAuftragsPosition()="
                + getAuftragsPosition() + "]";
    }
}
