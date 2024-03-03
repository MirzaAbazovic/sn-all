/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2004 07:45:09
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKCompBehavior;
import de.augustakom.authentication.model.view.AKCompBehaviorView;


/**
 * DAO-Definition fuer Klassen des Typs <code>AKCompBehavior</code>.
 */
public interface AKCompBehaviorDAO {

    /**
     * Speichert oder aktualisiert das Objekt in der DB.
     */
    void save(AKCompBehavior toSave);

    /**
     * Loescht das angegebene Objekt aus der DB.
     */
    void delete(AKCompBehavior toDelete);

    /**
     * Sucht nach der Verhaltensweise einer Komponente fuer eine best. Rolle.
     *
     * @param compId ID der Komponente
     * @param roleId ID der Rolle
     * @return AKCompBehavior oder <code>null</code>
     */
    AKCompBehavior find(Long compId, Long roleId);

    /**
     * Sucht nach den Eigenschaften einer Komponente fuer bestimmte Rollen.
     *
     * @param compId  ID der Komponente
     * @param roleIds Rollen-IDs.
     * @return Liste mit Objekten des Typs <code>AKCompBehavior</code> (never {@code null})
     */
    List<AKCompBehavior> findBehaviors(Long compId, Long[] roleIds);

    /**
     * @return (never {@code null})
     */
    List<AKCompBehaviorView> findBehaviorViews(List<String> compNames, List<String> parentNames,
            Long applicationId, Long[] roleIds);
}
