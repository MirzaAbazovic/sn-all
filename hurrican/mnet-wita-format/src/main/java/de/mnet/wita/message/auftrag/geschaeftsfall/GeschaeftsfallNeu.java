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
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForAuftrag;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet;
import de.mnet.wita.validators.geschaeftsfall.StandortKundeSet;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet;
import de.mnet.wita.validators.groups.V1;


@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue(GeschaeftsfallTyp.BEREITSTELLUNG_NAME)

@SchaltangabenSet
@StandortKundeSet
@StandortKollokationSet
@ZeitfensterSet

@AbgebenderProviderSetForGeschaeftsfall(permitted = false)
@AktionsCodeSet(permitted = false)
@BestandsSucheSet(permitted = false)
@LeitungsBezeichnungSet(permitted = false)
@RufnummernPortierungSetForAuftrag(permitted = false)
@VertragsnummerSet(permitted = false)
// Kuendigungsschreiben muss fuer den Interimsprozess fuer Anbieterwechsel erlaubt sein
@AnlagenGeschaeftsfallValid.List({
        @AnlagenGeschaeftsfallValid(groups = V1.class, lageplanAllowed = true, kuendigungAllowed = true, numberSonstigeAnlagen = 99),
})
public class GeschaeftsfallNeu extends Geschaeftsfall {

    private static final long serialVersionUID = 1254355254771338406L;

    @Transient
    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.BEREITSTELLUNG;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallNeu [getGeschaeftsfalltyp()=" + getGeschaeftsfallTyp() + ", getGfAnsprechpartner()="
                + getGfAnsprechpartner() + ", getKundenwunschtermin()=" + getKundenwunschtermin()
                + ", getAuftragsPosition()=" + getAuftragsPosition() + "]";
    }
}
