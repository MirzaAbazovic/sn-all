/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2010 16:41:06
 */

package de.augustakom.hurrican.service.cc;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.hurrican.model.cc.AuftragUMTS;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 *
 */
public interface UMTSService extends ICCService {

    /**
     * Speichert das angegebene AuftragUMTS-Objekt.
     */
    AuftragUMTS saveAuftragUMTS(AuftragUMTS toSave, Long sessionId) throws StoreException;

    /**
     * Ermittelt das aktuelle AuftragUMTS-Objekt zu dem angegebenen Auftrag.
     */
    AuftragUMTS findAuftragUMTS(Long auftragId) throws FindException, IncorrectResultSizeDataAccessException;

}
