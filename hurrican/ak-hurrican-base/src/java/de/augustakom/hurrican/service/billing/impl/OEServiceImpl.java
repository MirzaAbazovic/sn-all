/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 08:39:44
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.dao.billing.OeDAO;
import de.augustakom.hurrican.dao.billing.ProductTypeDAO;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.ProductType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.OEService;


/**
 * Service-Implementierung, um Objekte vom Typ <code>OE</code> zu verwalten.
 *
 *
 */
@BillingTx
public class OEServiceImpl extends DefaultBillingService implements OEService {

    private static final Logger LOGGER = Logger.getLogger(OEServiceImpl.class);
    private ProductTypeDAO productTypeDAO = null;

    @Override
    public OE findOE(Long oeNoOrig) throws FindException {
        if (oeNoOrig == null) {
            return null;
        }
        try {
            OeDAO dao = (OeDAO) getDAO();
            return dao.findByOeNoOrig(oeNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<Long, String> findProduktNamen4Auftraege(List<Long> auftragNoOrigs) throws FindException {
        if ((auftragNoOrigs == null) || (auftragNoOrigs.isEmpty())) { return null; }
        try {
            return ((OeDAO) getDAO()).findProduktNamen4Auftraege(auftragNoOrigs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String findProduktName4Auftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) { return null; }
        try {
            String name = ((OeDAO) getDAO()).findProduktName4Auftrag(auftragNoOrig);
            if (name != null) {
                name = StringUtils.trim(name);
            }
            return name;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Long findVaterOeNoOrig4Auftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) { return null; }
        try {
            return ((OeDAO) getDAO()).findVaterOeNoOrig4Auftrag(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<OE> findOEByOeTyp(String oeTyp, int strategy) throws FindException {
        if (oeTyp == null) { return null; }
        if (strategy == FIND_STRATEGY_ALL) {
            try {
                return ((OeDAO) getDAO()).findAllByOetyp(oeTyp);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new FindException(FindException._UNEXPECTED_ERROR, e);
            }
        }
        else if (strategy == FIND_STRATEGY_HAS_DN) {
            try {
                List<OE> oeResult = ((OeDAO) getDAO()).findAllByOetyp(oeTyp);
                List<ProductType> ptResult = getProductTypeDAO().findAll(ProductType.class);
                List<Long> productOeNo = new ArrayList<>();
                List<OE> result = new ArrayList<>();
                productOeNo.addAll(ptResult.stream().map(ProductType::getProductOeNo).collect(Collectors.toList()));

                if (!productOeNo.isEmpty()) {
                    for (OE view : oeResult) {
                        if (productOeNo.contains(view.getOeNoOrig())) {
                            result.add(view);
                        }
                    }
                    return result;
                }
                else {
                    return null;
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new FindException(FindException._UNEXPECTED_ERROR, e);
            }
        }
        return null;
    }

    /**
     * @return Returns the productTypeDAO.
     */
    public ProductTypeDAO getProductTypeDAO() {
        return productTypeDAO;
    }

    /**
     * @param ptDAO The productTypeDAO to set.
     */
    public void setProductTypeDAO(ProductTypeDAO ptDAO) {
        this.productTypeDAO = ptDAO;
    }

}


