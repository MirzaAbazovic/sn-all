/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2015 09:32
 */
package de.augustakom.authentication.service;

import java.util.*;
import javax.annotation.*;

import de.augustakom.authentication.dao.AKBereichDAO;
import de.augustakom.authentication.model.AKBereich;

/**
 *
 */
@AuthenticationTx
public class AKBereichService implements IAuthenticationService {

    @Resource(name = "de.augustakom.authentication.dao.AKBereichDAO")
    private AKBereichDAO akBereichDAO;

    public List<AKBereich> findAll()    {
        return akBereichDAO.findAll(AKBereich.class);
    }

    public AKBereich findById(final Long id)    {
        return akBereichDAO.findById(id, AKBereich.class);
    }

    public AKBereich findByName(final String name)  {
        //name is unique constrained
        final List<AKBereich> result = akBereichDAO.findByProperty(AKBereich.class, "name", name);
        return result.isEmpty() ? null : result.get(0);
    }
}
