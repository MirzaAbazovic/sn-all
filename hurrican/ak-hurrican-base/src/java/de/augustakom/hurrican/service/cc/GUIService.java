/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 10:07:15
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.gui.GUIDefinition;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.LoadException;


/**
 * Definition des GUI-Services. <br> Ueber den GUI-Service koennen Oberflaechenelemente abhaengig von bestimmten Daten
 * erzeugt werden.
 *
 *
 */
public interface GUIService extends ICCService {

    /**
     * Sucht nach einer GUI-Definition ueber den Klassennamen.
     *
     * @param className Name der Klasse, deren GUI-Definition gesucht wird.
     * @return Objekt vom Typ <code>GUIDefinition</code> oder <code>null</code>
     */
    public GUIDefinition findGUIDefinitionByClass(String className) throws FindException;

    /**
     * Sucht nach den GUI-Definitionen, die von dem Typ <code>guiType</code> sind und einer best. Referenz zugeordnet
     * sind.
     *
     * @param referenceId ID der Referenz
     * @param refHerkunft Klassenname der Referenz
     * @param guiType     Typ der gesuchten GUI-Definitionen (Konstante aus GUIDefinition).
     * @return Liste mit Objekten des Typs <code>GUIDefinition</code>
     * @throws LoadException wenn beim Laden ein Fehler auftritt.
     */
    public List<GUIDefinition> getGUIDefinitions4Reference(Long referenceId, String refHerkunft, String guiType)
            throws LoadException;

}


