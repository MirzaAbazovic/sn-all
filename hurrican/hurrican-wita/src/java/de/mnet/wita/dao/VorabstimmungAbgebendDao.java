/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2012 09:30:32
 */
package de.mnet.wita.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.wita.model.VorabstimmungAbgebend;

public interface VorabstimmungAbgebendDao extends StoreDAO {

    /**
     * Gibt die eindeutige Vorabstimmung fuer endstellenTyp und auftragsId zurueck - null, falls keine gefunden wurde.
     */
    VorabstimmungAbgebend findVorabstimmung(String endstelleTyp, Long auftragId);

    /**
     * Gibt die Liste der Vorabstimmungen fuer auftragsId zurueck - leer, falls keine gefunden wurde.
     */
    List<VorabstimmungAbgebend> findVorabstimmungen(Long auftragId);

    /**
     * Loescht die uebergebene {@link VorabstimmungAbgebend}
     */
    void deleteVorabstimmungAbgebend(VorabstimmungAbgebend vorabstimmungAbgebend);
}
