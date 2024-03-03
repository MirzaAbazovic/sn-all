/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2004
 */
package de.augustakom.common.service.base;

import de.augustakom.common.tools.validation.AbstractValidator;

/**
 * Standard-Implementierung fuer einen Service, der ein DAO-Objekt (Data Access Object) benoetigt.
 */
public class DefaultDAOService extends DefaultServiceObject {

    private Object dao;
    private AbstractValidator validator;

    public void setDAO(Object dao) {
        this.dao = dao;
    }

    protected Object getDAO() {
        return dao;
    }

    public void setValidator(AbstractValidator validator) {
        this.validator = validator;
    }

    protected AbstractValidator getValidator() {
        return validator;
    }
}
