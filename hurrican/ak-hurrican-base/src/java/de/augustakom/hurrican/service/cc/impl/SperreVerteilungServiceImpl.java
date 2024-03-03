/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2009 11:14:43
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.SperreInfoDAO;
import de.augustakom.hurrican.dao.cc.SperreVerteilungDAO;
import de.augustakom.hurrican.model.cc.SperreInfo;
import de.augustakom.hurrican.model.cc.SperreVerteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.SperreVerteilungService;
import de.augustakom.hurrican.validation.cc.SperreInfoValidator;


/**
 * Service-Implementierung fuer die Verwaltung der Sperr-Konfiguration.
 *
 *
 */
@CcTxRequired
public class SperreVerteilungServiceImpl extends DefaultCCService implements SperreVerteilungService {
    private static final Logger LOGGER = Logger.getLogger(SperreVerteilungServiceImpl.class);

    // DAOs
    private SperreVerteilungDAO sperreVerteilungDAO = null;
    private SperreInfoDAO sperreInfoDAO = null;

    // Validators
    private SperreInfoValidator sperreInfoValidator = null;

    @Override
    public List<SperreVerteilung> findSperreVerteilungen4Produkt(Long produktId) throws FindException {
        if (produktId == null) { return null; }
        try {
            return getSperreVerteilungDAO().findByProduktId(produktId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void createSperreVerteilungen(Long prodId, List<Long> abteilungIds) throws StoreException {
        if (prodId == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            SperreVerteilungDAO dao = getSperreVerteilungDAO();
            dao.deleteVerteilungen4Produkt(prodId);

            if (abteilungIds != null) {
                List<SperreVerteilung> toSave = new ArrayList<SperreVerteilung>();
                for (Long abtId : abteilungIds) {
                    SperreVerteilung sv = new SperreVerteilung();
                    sv.setProduktId(prodId);
                    sv.setAbteilungId(abtId);
                    toSave.add(sv);
                }
                dao.saveSperreVerteilungen(toSave);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_SPERREVERTEILUNG, e);
        }
    }

    @Override
    public List<SperreInfo> findSperreInfos(Boolean onlyActive, Long abteilungsId) throws FindException {
        try {
            if ((onlyActive != null) || (abteilungsId != null)) {
                SperreInfo example = new SperreInfo();
                example.setActive(onlyActive);
                if (abteilungsId != null) {
                    example.setAbteilungId(abteilungsId);
                }
                return getSperreInfoDAO().queryByExample(example, SperreInfo.class);
            }
            else {
                return getSperreInfoDAO().findAll(SperreInfo.class);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveSperreInfo(SperreInfo toSave) throws StoreException, ValidationException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            // Validierung
            AbstractValidator val = getValidator();
            ValidationException ve = new ValidationException(toSave, "SperreInfo");
            val.validate(toSave, ve);
            if (ve.hasErrors()) {
                throw ve;
            }

            getSperreInfoDAO().store(toSave);
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @return the sperreVerteilungDAO
     */
    public SperreVerteilungDAO getSperreVerteilungDAO() {
        return sperreVerteilungDAO;
    }

    /**
     * @param sperreVerteilungDAO the sperreVerteilungDAO to set
     */
    public void setSperreVerteilungDAO(SperreVerteilungDAO sperreVerteilungDAO) {
        this.sperreVerteilungDAO = sperreVerteilungDAO;
    }

    /**
     * @return the sperreInfoDAO
     */
    public SperreInfoDAO getSperreInfoDAO() {
        return sperreInfoDAO;
    }

    /**
     * @param sperreInfoDAO the sperreInfoDAO to set
     */
    public void setSperreInfoDAO(SperreInfoDAO sperreInfoDAO) {
        this.sperreInfoDAO = sperreInfoDAO;
    }

    /**
     * @return Returns the sperreInfoValidator.
     */
    public SperreInfoValidator getSperreInfoValidator() {
        return sperreInfoValidator;
    }

    /**
     * @param sperreInfoValidator The sperreInfoValidator to set.
     */
    public void setSperreInfoValidator(SperreInfoValidator sperreInfoValidator) {
        this.sperreInfoValidator = sperreInfoValidator;
    }


}
