/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 13:31:12
 */
package de.mnet.wita.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.wita.model.Vorabstimmung;

public interface VorabstimmungDao extends StoreDAO {

    /**
     * Gibt die eindeutige Vorabstimmung fuer endstellenTyp und auftragsId zurueck - null, falls keine gefunden wurde.
     */
    Vorabstimmung findVorabstimmung(String endstelleTyp, Long auftragId);

    /**
     * Gibt die Liste der Vorabstimmungen fuer auftragsId zurueck - leer, falls keine gefunden wurde.
     */
    List<Vorabstimmung> findVorabstimmungen(Long auftragId);

    /**
     * Loescht die uebergebene {@link Vorabstimmung}
     */
    void deleteVorabstimmung(Vorabstimmung vorabstimmungAufnehmend);
}
