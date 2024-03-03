/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 10:48:31
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.IPSecClient2SiteDAO;
import de.augustakom.hurrican.dao.cc.IPSecSite2SiteDAO;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.IPSecService;


/**
 * Service-Implementierung von {@link IPSecService}
 *
 *
 */
@CcTxRequired
public class IPSecServiceImpl extends DefaultCCService implements IPSecService {

    private static final Logger LOGGER = Logger.getLogger(IPSecServiceImpl.class);

    private AbstractValidator ipSecSite2SiteValidator;
    private IPSecClient2SiteDAO iPSecClient2SiteDAO;

    @Override
    public IPSecSite2Site findIPSecSiteToSite(Long auftragId) throws FindException {
        if (auftragId == null) { throw new FindException(FindException.EMPTY_FIND_PARAMETER); }
        try {
            return ((IPSecSite2SiteDAO) getDAO()).findByAuftragId(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveIPSecSiteToSite(IPSecSite2Site toSave) throws StoreException, ValidationException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        validateIPSecSiteToSite(toSave);
        try {
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void validateIPSecSiteToSite(IPSecSite2Site toValidate) throws ValidationException {
        ValidationException valEx = new ValidationException(toValidate, "IPSecSite2Site");
        getIpSecSite2SiteValidator().validate(toValidate, valEx);
        if (valEx.hasErrors()) {
            throw valEx;
        }
    }

    @Override
    public List<IPSecClient2SiteToken> findAllClient2SiteTokens() throws FindException {
        return iPSecClient2SiteDAO.findAllClient2SiteTokens();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.IPSecService#findAllClient2SiteTokens(java.lang.String)
     */
    @Override
    public List<IPSecClient2SiteToken> findAllClient2SiteTokens(String serialNumber) throws FindException {
        return iPSecClient2SiteDAO.findAllClient2SiteTokens(serialNumber);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.IPSecService#findFreeClient2SiteTokens()
     */
    @Override
    public List<IPSecClient2SiteToken> findFreeClient2SiteTokens() throws FindException {
        return iPSecClient2SiteDAO.findFreeClient2SiteTokens();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.IPSecService#findFreeClient2SiteTokens(java.lang.String)
     */
    @Override
    public List<IPSecClient2SiteToken> findFreeClient2SiteTokens(String serialNumber)
            throws FindException {
        return iPSecClient2SiteDAO.findFreeClient2SiteTokens(serialNumber);
    }

    @Override
    public List<IPSecClient2SiteToken> findClient2SiteTokens(Long auftragId) throws FindException {
        return iPSecClient2SiteDAO.findClient2SiteTokens(auftragId);
    }

    @Override
    public void saveClient2SiteToken(IPSecClient2SiteToken toSave) throws StoreException {
        iPSecClient2SiteDAO.store(toSave);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.IPSecService#saveClient2SiteTokens(java.util.List)
     */
    @Override
    public void saveClient2SiteTokens(List<IPSecClient2SiteToken> tokens) throws StoreException {
        for (IPSecClient2SiteToken token : tokens) {
            saveClient2SiteToken(token);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.IPSecService#deleteClient2SiteToken(de.augustakom.hurrican.model.cc.IPSecClient2SiteToken)
     */
    @Override
    public void deleteClient2SiteToken(IPSecClient2SiteToken token) throws StoreException {
        iPSecClient2SiteDAO.deleteClient2SiteToken(token);
    }

    /**
     * @throws StoreException
     * @see de.augustakom.hurrican.service.cc.IPSecService#importClient2SiteTokens(java.io.Reader)
     */
    @Override
    public List<IPSecClient2SiteToken> importClient2SiteTokens(Reader reader) throws StoreException {
        ICsvBeanReader beanReader = new CsvBeanReader(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
        // Abgesprochen: Seriennummer, SapId, Lieferdatum, Laufzeit in Monaten, Batterie-Ende, Batch
        // Header = serialNumber;sapOrderId;lieferdatum;laufzeitInMonaten;batterieEnde;batch
        final CellProcessor[] processors = new CellProcessor[] {
                new Unique(new StrMinMax(0, 50)),
                new StrMinMax(0, 50),
                new ParseDate("dd.MM.yyyy"),
                new Trim(new ParseInt()),
                new ParseDate("dd.MM.yyyy"),
                new StrMinMax(0, 75)
        };
        try {
            final String[] header = beanReader.getHeader(true);
            LOGGER.debug(header);
            List<IPSecClient2SiteToken> tokens = new ArrayList<>();
            IPSecClient2SiteToken token;
            while ((token = beanReader.read(IPSecClient2SiteToken.class, header, processors)) != null) {
                token.setStatusRefId(IPSecClient2SiteToken.REF_ID_TOKEN_STATUS_ACTIVE);
                tokens.add(token);
            }
            return tokens;
        }
        catch (IOException e) {
            LOGGER.error(e);
            throw new StoreException("Beim Importieren ist ein Fehler aufgetreten!", e);
        }
        catch (SuperCsvException e) {
            LOGGER.error(e);
            throw new StoreException("Beim Parsen der CSV-Datei ist ein Fehler aufgetreten!", e);
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                LOGGER.warn("importClient2SiteTokens() - exception closing reader", e);
            }
        }
    }

    /**
     * injected
     */
    public void setIpSecClient2SiteDAO(IPSecClient2SiteDAO iPSecClient2SiteDAO) {
        this.iPSecClient2SiteDAO = iPSecClient2SiteDAO;
    }

    public AbstractValidator getIpSecSite2SiteValidator() {
        return ipSecSite2SiteValidator;
    }

    /**
     * Injected
     */
    public void setIpSecSite2SiteValidator(AbstractValidator ipSecSite2SiteValidator) {
        this.ipSecSite2SiteValidator = ipSecSite2SiteValidator;
    }
}


