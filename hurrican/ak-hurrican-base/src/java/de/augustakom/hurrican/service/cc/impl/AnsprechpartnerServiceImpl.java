/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 11:20:39
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.AnsprechpartnerDAO;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.validation.cc.AnsprechpartnerValidator;


/**
 * Service-Implementierung von AnsprechpartnerService.
 *
 *
 */
@CcTxRequiredReadOnly
public class AnsprechpartnerServiceImpl implements AnsprechpartnerService {
    private static final Logger LOGGER = Logger.getLogger(AnsprechpartnerServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.AnsprechpartnerDAO")
    private AnsprechpartnerDAO ansprechpartnerDao;
    @Resource(name = "de.augustakom.hurrican.validation.cc.AnsprechpartnerValidator")
    private AnsprechpartnerValidator ansprechpartnerValidator;

    @Override
    @CcTxRequired
    public void saveAnsprechpartner(Ansprechpartner ansprechpartner) throws StoreException, ValidationException {
        if (ansprechpartner == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            validateAnsprechpartner(ansprechpartner);
            ansprechpartnerDao.store(ansprechpartner);
        }
        catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    @CcTxRequired
    public void deleteAnsprechpartner(Ansprechpartner ansprechpartner) throws DeleteException {
        try {
            ansprechpartnerDao.deleteAnsprechpartner(ansprechpartner);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<Ansprechpartner> findAnsprechpartner(Typ type, Long auftragId) throws FindException {
        if (auftragId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            List<Ansprechpartner> result = ansprechpartnerDao.findAnsprechpartner(type, auftragId, false);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public Ansprechpartner findPreferredAnsprechpartner(Typ type, Long auftragId) throws FindException {
        if ((type == null) || (auftragId == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            List<Ansprechpartner> result = ansprechpartnerDao.findAnsprechpartner(type, auftragId, true);
            if (result.size() > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { Integer.valueOf(1), result.size() });
            }
            if (result.size() == 1) {
                return result.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    @CcTxRequired
    public void copyAnsprechpartner(Typ type, Long auftragIdSrc, Long auftragIdDest) throws StoreException {
        if ((type == null) || (auftragIdSrc == null) || (auftragIdDest == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            Ansprechpartner anspFromAuftragSrc = findPreferredAnsprechpartner(type, auftragIdSrc);
            if (anspFromAuftragSrc != null) {
                Ansprechpartner anspFromAuftragDest = findPreferredAnsprechpartner(type, auftragIdDest);
                if (anspFromAuftragDest == null) {
                    // Ansprechpartner kopieren
                    Ansprechpartner copy = Ansprechpartner.createCopy(anspFromAuftragSrc, auftragIdDest);
                    saveAnsprechpartner(copy);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    private void validateAnsprechpartner(Ansprechpartner toValidate) throws ValidationException {
        ValidationException valEx = new ValidationException(toValidate, Ansprechpartner.class.getSimpleName());
        ansprechpartnerValidator.validate(toValidate, valEx);
        if (valEx.hasErrors()) {
            throw valEx;
        }
    }

    @Override
    public Ansprechpartner findAnsprechpartner(Long id) throws FindException {
        if (id == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            Ansprechpartner result = ansprechpartnerDao.findById(id, Ansprechpartner.class);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    @CcTxRequired
    public Ansprechpartner copyAnsprechpartner(Ansprechpartner source,
            Ansprechpartner.Typ ansprechpartnerTyp, Long addressType, Boolean preferred) throws StoreException {
        try {
            Ansprechpartner newAnsprechpartner = new Ansprechpartner();
            PropertyUtils.copyProperties(newAnsprechpartner, source);
            newAnsprechpartner.setId(null);
            newAnsprechpartner.setAddress(null);
            newAnsprechpartner.setVersion(null);
            newAnsprechpartner.setTypeRefId(ansprechpartnerTyp.refId());
            if (preferred == null) {
                newAnsprechpartner.setPreferred(source.getPreferred());
                if (source.getPreferred()) {
                    // Prüfen ob es bereis einen preferred Ansprechpartner des Typs gibt
                    List<Ansprechpartner> existing = findAnsprechpartner(ansprechpartnerTyp, source.getAuftragId());
                    for (Ansprechpartner ap : existing) {
                        if (ap.getPreferred()) {
                            newAnsprechpartner.setPreferred(Boolean.FALSE);
                        }
                    }
                }
            }
            else {
                newAnsprechpartner.setPreferred(preferred);
            }

            CCAddress sourceAddress = source.getAddress();
            if (sourceAddress != null) {
                CCAddress newAddresss = new CCAddress();
                PropertyUtils.copyProperties(newAddresss, sourceAddress);
                newAddresss.setVersion(null);
                newAddresss.setId(null);
                newAddresss.setAddressType(addressType);
                newAnsprechpartner.setAddress(newAddresss);
            }

            validateAnsprechpartner(newAnsprechpartner);

            ansprechpartnerDao.store(newAnsprechpartner);
            return newAnsprechpartner;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequired
    public Ansprechpartner copyAnsprechpartner(Long ansprechpartnerId, Long auftragId) throws StoreException {
        if (ansprechpartnerId == null || auftragId == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            Ansprechpartner toCopy = findAnsprechpartner(ansprechpartnerId);
            Ansprechpartner copy = Ansprechpartner.createCopy(toCopy, auftragId);
            List<Ansprechpartner> existing = findAnsprechpartner(Ansprechpartner.Typ.forRefId(toCopy.getTypeRefId()), auftragId);
            for (Ansprechpartner ap : existing) {
                if (ap.getPreferred()) {
                    copy.setPreferred(Boolean.FALSE);
                    break;
                }
            }
            validateAnsprechpartner(copy);
            return ansprechpartnerDao.store(copy);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Ansprechpartner createPreferredAnsprechpartner(CCAddress ccaddress, Long auftragId) throws FindException {
        if (ccaddress == null || auftragId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        Ansprechpartner ansprechpartner = new Ansprechpartner();
        ansprechpartner.setAddress(ccaddress);
        ansprechpartner.setAuftragId(auftragId);
        ansprechpartner.setTypeRefId(Ansprechpartner.Typ.ENDSTELLE_B.refId());
        ansprechpartner.setPreferred(true);
        ansprechpartner.setPrio(1);

        return ansprechpartner;
    }

    @Override
    public List<CCAnsprechpartnerView> findAnsprechpartnerByKundeNoAndBuendelNo(Long kundeNo, Integer buendelNr) throws FindException {
        if (kundeNo == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        return ansprechpartnerDao.findAnsprechpartnerByKundeNoAndBuendelNo(kundeNo, buendelNr);
    }
}
