/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.12.2011 13:41:37
 */
package de.mnet.wita.bpm.variables;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.exceptions.WitaBaseException;

public class ActivitiVariableUtils {

    public static <T> T extractVariable(DelegateExecution execution, WitaTaskVariables key, Class<T> clazz) {
        Object variable = execution.getVariable(key.id);
        if (variable == null) {
            throw new WitaBaseException("Keine Variable vorhanden: " + key.id);
        }
        if (!variable.getClass().isAssignableFrom(clazz)) {
            throw new WitaBaseException("Unerwarteter Variablentyp: " + key.id);
        }
        @SuppressWarnings("unchecked")
        T result = (T) variable;
        return result;
    }

    public static <T> T extractVariableSilent(DelegateExecution execution, WitaTaskVariables key, Class<T> clazz,
            T defaultValue) {
        try {
            return extractVariable(execution, key, clazz);
        }
        catch (Exception e) {
            // do nothing!
            return defaultValue;
        }
    }

}


