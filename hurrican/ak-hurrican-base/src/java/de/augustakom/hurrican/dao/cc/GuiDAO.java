/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 10:34:48
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.gui.GUIDefinition;


/**
 * DAO-Interface fuer die Verwaltung der GUI-Objekte (GUIDefinition und GUIMapping).
 *
 *
 */
public interface GuiDAO {

    /**
     * Sucht nach einer GUI-Definition ueber den Klassennamen. <br> Voraussetzung ist, dass die GUI-Definition als
     * 'aktiv' markiert ist.
     *
     * @param className Name der Klasse
     * @return GUIDefinition oder <code>null</code>
     */
    public GUIDefinition findGUIDefByClass(String className);

    /**
     * Sucht nach allen GUI-Definitionen, die vom Typ <code>guiType</code> sind und einer best. Referenz zugeordnet
     * sind. <br> Voraussetzung ist, dass die GUI-Definitionen als 'aktiv' markiert sind.
     *
     * @param referenceId ID der Referenz
     * @param refHerkunft Klassenname der Referenz
     * @param guiType     Typ der GUI-Komponente.
     * @return Liste mit Objekten des Typs <code>GUIDefinition</code>
     */
    public List<GUIDefinition> findGUIDefinitionen(Long referenceId, String refHerkunft, String guiType);

}


