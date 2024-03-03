/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 09:39:14
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.model.cc.AK0800;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer die Verwaltung von IN-Rufnummern.
 *
 *
 */
public interface INRufnummerService extends ICCService {

    /**
     * Sucht nach den Details zu einem Service-Rufnummern Auftrag.
     *
     * @param auftragId
     * @return
     * @throws FindException
     *
     */
    public AK0800 findINDetails(Long auftragId) throws FindException;

    /**
     * Speichert das AK0800-Objekt ab. <br> Ueber das Flag <code>makeHistory</code> wird definiert, ob ein bereits
     * bestehender Datensatz historisiert werden soll.
     *
     * @param toSave      zu speicherndes Objekt
     * @param makeHistory Flag, ob ein bestehender DS historisiert werden soll
     * @return Abhaengig von makeHistory wird entweder <code>toSave</code> oder eine neue Instanz von
     * <code>AK0800</code> zurueck gegeben.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public AK0800 saveAK0800(AK0800 toSave, boolean makeHistory) throws StoreException;

}


