/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2005 14:41:20
 */
package de.augustakom.hurrican.service.base.impl;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;


/**
 * Abstrakte Jasper-DataSource Implementierung.
 *
 *
 */
public abstract class AbstractHurricanJasperDS implements JRDataSource {

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    public Object getFieldValue(JRField jrField) throws JRException {
        return getFieldValue(jrField.getName());
    }

    /**
     * @param field
     * @return
     * @throws JRException
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    protected Object getFieldValue(String field) throws JRException {
        return null;
    }

}


