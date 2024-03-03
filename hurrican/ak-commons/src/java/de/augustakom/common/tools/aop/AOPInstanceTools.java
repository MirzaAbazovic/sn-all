/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.03.2007 08:45:21
 */
package de.augustakom.common.tools.aop;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.Advised;


/**
 * Hilfsklasse, um mit AOP-Instanzen zu arbeiten.
 *
 *
 */
public class AOPInstanceTools {

    private static final Logger LOGGER = Logger.getLogger(AOPInstanceTools.class);

    /**
     * Ueberprueft, ob das Objekt <code>obj</code> vom Typ <code>type</code> ist. Ist dies nicht der Fall, wird
     * geprueft, ob das Objekt vom Typ <code>org.springframework.aop.framework.Advised</code> ist und dessen Target eine
     * Instanz von <code>type</code> ist.
     *
     * @param obj  zu pruefendes Objekt
     * @param type zu pruefender Typ
     * @return true wenn <code>obj</code> oder dessen Target vom angegebenen Typ ist.
     *
     */
    public static <T> boolean isInstanceof(Object obj, Class<T> type) {
        if (obj != null && type != null) {
            if (type.isInstance(obj)) {
                return true;
            }
            else if (obj instanceof Advised) {
                try {
                    Object target = ((Advised) obj).getTargetSource().getTarget();
                    boolean isInstance = type.isInstance(target);
                    return isInstance;
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /**
     * Ist das Objekt <code>obj</code> ueber ein Proxy gekapselt, wird das eigentliche Target-Objekt zurueck geliefert
     * ansonsten 'obj'.
     *
     * @param obj zu pruefendes Objekt
     * @return das eigentliche Target oder 'obj'
     *
     */
    public static Object getRealImplementation(Object obj) {
        if (obj instanceof Advised) {
            try {
                return ((Advised) obj).getTargetSource().getTarget();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return obj;
    }

}


