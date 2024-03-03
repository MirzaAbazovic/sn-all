/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 11:10:43
 */
package de.mnet.wita.bpm.variables;

import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;

import de.mnet.wita.RuemPvAntwortCode;

/**
 * Wird von einem Activiti-Workflow verwendet.
 */
public class RuemPvAntwortCodeVariableType implements VariableType {

    @Override
    public String getTypeName() {
        return RuemPvAntwortCode.class.getName();
    }

    @Override
    public boolean isCachable() {
        return false;
    }

    @Override
    public boolean isAbleToStore(Object value) {
        return (value != null) && (value instanceof RuemPvAntwortCode);
    }

    @Override
    public void setValue(Object value, ValueFields valueFields) {
        RuemPvAntwortCode antwortCode = (RuemPvAntwortCode) value;
        valueFields.setTextValue(antwortCode.name());
    }

    @Override
    public Object getValue(ValueFields valueFields) {
        return RuemPvAntwortCode.valueOf(valueFields.getTextValue());
    }

}


