/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2012 11:07:26
 */
package de.mnet.wita.model;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.mnet.common.tools.PropertyTools;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;
import de.mnet.wita.message.common.LeitungsBezeichnung;

public enum KollokationsTyp {

    HVT,
    FTTC_KVZ;

    /**
     * Ermittelt ueber die Schaltangaben aus dem Request, ob es sich um einen FTTC_KVZ Standort handelt. Ist dies der
     * Fall, so wird {@code FTTC_KVZ} zurueck geliefert; in allen anderen Faellen wird {@code HVT} zurueck geliefert.
     *
     * @param request
     * @return
     */
    public static KollokationsTyp getKollokationsTypForRequest(MnetWitaRequest request) {
        Object schaltungKvz = PropertyTools.getNestedPropertyIgnoreNestedNulls(request, SchaltungKvzTal.SCHALTUNG_KVZ_PROPERTY_PATH);
        if (schaltungKvz != null && schaltungKvz instanceof Collection<?> && CollectionUtils.isNotEmpty((Collection<?>) schaltungKvz)) {
            return FTTC_KVZ;
        }
        else {
            // notwendig fuer KUE-KD, um KVZ/HVT zu unterscheiden (bei KUE-KD gibt es keine Schaltangaben, sondern nur die LBZ)
            // (ist nur fuer die korrektue Ueberpruefung u. Protokollierung des Sende-Limits notwendig)
            String leitungsSchluesselZahl = (String) PropertyTools.getNestedPropertyIgnoreNestedNulls(request, LeitungsBezeichnung.LEITUNGS_SCHLUESSEL_ZAHL_PROPERTY_PATH);
            ProduktBezeichner produktBezeichner = ProduktBezeichner.getByLeitungsSchluesselZahl(leitungsSchluesselZahl);
            if (produktBezeichner != null && produktBezeichner.isKvz()) {
                return FTTC_KVZ;
            }
        }

        return HVT;
    }

}


