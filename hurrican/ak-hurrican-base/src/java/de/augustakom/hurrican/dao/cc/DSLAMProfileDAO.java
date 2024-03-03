/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2007 17:17:43
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


/**
 * DAO-Interface fuer die Verwaltung von DSLAM-Profilen.
 *
 *
 */
public interface DSLAMProfileDAO extends FindDAO, ByExampleDAO, StoreDAO, FindAllDAO {

    /**
     * Ermittelt alle DSLAM-Profile, die dem angegebenen Produkt zugeordnet sind.
     *
     * @param prodId Produkt-ID
     * @return Liste mit Objekten des Typs <code>DSLAMProfile</code>
     *
     */
    public List<DSLAMProfile> findDSLAMProfiles4Produkt(Long prodId);

    /**
     * Ermittelt alle DSLAM-Profile, die dem Auftrag zugeordnet sind (Historie).
     *
     * @param auftragId
     * @return
     */
    List<DSLAMProfile> findDSLAMProfiles4Auftrag(Long auftragId);

    /**
     * Ermittelt alle DSLAM-Profile, die dem angegebenen Produkt und dem angegebenen Baugruppentyp zugeordnet sind.
     *
     * @param hwBaugruppenTyp ID des Baugruppentyps
     * @return Liste mit Objekten des Typs <code>DSLAMProfile</code>
     */
    public List<DSLAMProfile> findDSLAMProfiles4BaugruppenTyp(HWBaugruppenTyp hwBaugruppenTyp);

    /**
     * @param fromParams
     * @return
     */
    public List<DSLAMProfile> findByParams(DSLAMProfile fromParams);

    /**
     * Ermittelt alle zu den angegebenen Parametern passenden DSLAM-Profile. <br>Achtung</b> <br> Es werden nur
     * DSLAM-Profile ermittelt, deren Flag {@link DSLAMProfile#enabledForAutochange} auf {@code true} gesetzt sind!
     *
     * @param baugruppenTyp (optional) Ermittlung: (BG_TYP_ID=baugruppenTyp or BG_TYP_ID is null)
     * @param fastpath
     * @param uetvsAllowed  Liste an moeglichen uevts
     * @return
     */
    public List<DSLAMProfile> findDSLAMProfiles(Long baugruppenTyp, Boolean fastpath, Collection<String> uetvsAllowed);

    /**
     * Loescht das Auftrag2DSLAMProfile mit der angegeben id
     *
     * @param id
     */
    void deleteAuftrag2DSLAMProfileById(Long id);

}


