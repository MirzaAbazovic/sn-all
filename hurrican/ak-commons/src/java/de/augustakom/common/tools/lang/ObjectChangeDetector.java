/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2004 11:53:26
 */
package de.augustakom.common.tools.lang;

import java.util.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;


/**
 * Hilfsklasse, um Aenderungen an Modellen zu erkennen.
 */
public class ObjectChangeDetector {

    private static final Logger LOGGER = Logger.getLogger(ObjectChangeDetector.class);

    private Map copies = null;

    /**
     * Fuegt der Klasse ein Objekt hinzu, das auf Aenderungen ueberwacht werden soll. <br> Von dem Objekt wird eine
     * Kopie (Deep-Copy) erstellt und in einer Map unter dem Namen <code>name</code> gespeichert.
     *
     * @param name    Name fuer das Objekt.
     * @param toWatch Objekt, das ueberwacht werden soll.
     */
    public void addObjectToWatch(String name, Object toWatch) {
        if (copies == null) {
            copies = new HashMap();
        }

        try {
            Object copy = ObjectTools.makeDeepCopy(toWatch);
            copies.put(name, copy);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Entfernt ein best. Objekt aus der Ueberwachung.
     */
    public void removeObjectFromWatch(String name) {
        if (copies != null) {
            copies.remove(name);
        }
    }

    /**
     * Entfernt alle Objekte aus der Ueberwachung.
     */
    public void removeObjectsFromWatch() {
        if (copies != null) {
            copies.clear();
        }
    }

    /**
     * Ueberprueft, ob das Modell <code>actualModel</code> mit der Kopie uebereinstimmt, die unter dem Namen
     * <code>name</code> gespeichert ist. <br>
     *
     * @param name        Name der Kopie
     * @param actualModel Modell, das mit der Kopie verglichen werden soll.
     * @return true die beiden Modelle sind unterschiedlich.
     */
    public boolean hasChanged(String name, Object actualModel) {
        LOGGER.debug("ObjectChangeDetector.hasChanged for " + name);
        if (copies != null) {
            Object copy = copies.get(name);
            if (copy != null) {
                try {
                    Map descOfCopy = BeanUtils.describe(copy);
                    Map descOfActual = BeanUtils.describe(actualModel);

                    LOGGER.debug("---> pruefe Aenderungen: " + name + " - class: " + actualModel.getClass().getName());
                    for (Iterator iter = descOfCopy.entrySet().iterator(); iter.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        LOGGER.debug("------> KEY (of copy): " + entry.getKey() + "  -  value: " + entry.getValue());
                    }

                    for (Iterator iter = descOfActual.entrySet().iterator(); iter.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        LOGGER.debug("------> KEY (of act.): " + entry.getKey() + "  -  value: " + entry.getValue());
                    }

                    return !descOfCopy.equals(descOfActual);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            else if (actualModel != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("---> copy == null;  actualModel != null");
                }
                return true;
            }
            else {
                return false;
            }
        }

        return false;
    }

}
