/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2006 08:32:22
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.ReferenceDAO;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Service-Implementierung von <code>ReferenceService</code>.
 *
 *
 */
@CcTxRequired
public class ReferenceServiceImpl extends DefaultCCService implements ReferenceService {

    private static final Logger LOGGER = Logger.getLogger(ReferenceServiceImpl.class);

    @Override
    public List<Reference> findReferences() throws FindException {
        try {
            return ((ReferenceDAO) getDAO()).findAll(Reference.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Reference> findReferencesByType(String type, Boolean onlyVisible) throws FindException {
        return findReferences(type, onlyVisible);
    }

    @Override
    @CcTxRequiresNew
    public List<Reference> findReferencesByTypeTxNew(String type, Boolean onlyVisible) throws FindException {
        return findReferences(type, onlyVisible);
    }

    private List<Reference> findReferences(String type, Boolean onlyVisible) throws FindException {
        if (StringUtils.isBlank(type)) { return null; }
        try {
            List<Reference> refs = ((ReferenceDAO) getDAO()).findReferences(type, onlyVisible);
            if (refs != null) {
                for (Reference ref : refs) {
                    createGuiText(ref);
                }
            }
            return refs;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Reference findReference(Long refId) throws FindException {
        if (refId == null) { return null; }
        try {
            Reference ref = ((ReferenceDAO) getDAO()).findById(refId, Reference.class);
            createGuiText(ref);
            return ref;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Reference findReference(String type, Integer intValue) throws FindException {
        if ((intValue == null) || StringUtils.isBlank(type)) { return null; }

        Reference example = new Reference();
        example.setType(type);
        example.setIntValue(intValue);
        return findByExample(example);
    }

    @Override
    public Reference findReference(String type, String strValue) throws FindException {
        if (StringUtils.isBlank(strValue) || StringUtils.isBlank(type)) { return null; }

        Reference example = new Reference();
        example.setType(type);
        example.setStrValue(strValue);
        return findByExample(example);
    }

    @Override
    public void saveReference(Reference toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }

        try {
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /* Sucht nach einem Reference-Objekt ueber ein Example-Objekt.
     * @param example Example-Objekt fuer die Suche.
     * @return das erste Objekt aus der Result-List.
     * @throws FindException
     *
     */
    private Reference findByExample(Reference example) throws FindException {
        try {
            List<Reference> result = ((ReferenceDAO) getDAO()).queryByExample(example, Reference.class);
            return ((result != null) && (!result.isEmpty())) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Ermittelt den darzustellenden Text fuer das Reference-Objekt
     * und uebergibt ihm dem Objekt <code>ref</code>.
     */
    private void createGuiText(Reference reference) {
        StringBuilder guiText = new StringBuilder();
        try {
            if (reference != null) {
                if (StringUtils.isNotBlank(reference.getStrValue())) {
                    guiText.append(reference.getStrValue());
                }
                else if (reference.getIntValue() != null) {
                    guiText.append(reference.getIntValue());
                }
                else if (reference.getFloatValue() != null) {
                    guiText.append(reference.getFloatValue());
                }

                if (reference.getUnitId() != null) {
                    Reference unitRef = findReference(reference.getUnitId());
                    if (unitRef != null) {
                        guiText.append(" ");
                        guiText.append(unitRef.getStrValue());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            if (reference != null) {
                reference.setGuiText(guiText.toString());
            }
        }
    }

}


