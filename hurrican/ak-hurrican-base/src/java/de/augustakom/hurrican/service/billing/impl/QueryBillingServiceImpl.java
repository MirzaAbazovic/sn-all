/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 13:56:33
 */
package de.augustakom.hurrican.service.billing.impl;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.jdbc.ResultSetHelper;
import de.augustakom.hurrican.dao.base.iface.QueryDAO;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.QueryBillingService;


/**
 * Service-Implementierung von <code>QueryBillingService</code>.
 *
 *
 */
@BillingTx
public class QueryBillingServiceImpl extends DefaultBillingService implements QueryBillingService {

    private static final Logger LOGGER = Logger.getLogger(QueryBillingService.class);

    private Object findDAO = null;

    @Override
    public List<Map<String, Object>> query(String sql, String[] columnNames, Object[] params) throws FindException {
        if (StringUtils.isBlank(sql)) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            List<Object[]> result = ((QueryDAO) getDAO()).query(sql, params);
            return ResultSetHelper.getResultSet(columnNames, result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.common.service.iface.ISimpleFindService#findAll(java.lang.Class)
     */
    public <T> List<T> findAll(Class<T> clazz) throws Exception {
        try {
            return ((FindAllDAO) getFindDAO()).findAll(clazz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.service.iface.ISimpleFindService#findById(java.lang.Object, java.lang.Class)
     */
    public <T> T findById(Serializable id, Class<T> clazz) throws Exception {
        try {
            return ((FindDAO) getFindDAO()).findById(id, clazz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.service.iface.ISimpleFindService#findByExample(java.lang.Object, java.lang.Class)
     */
    public List findByExample(Object example, Class clazz) throws Exception {
        try {
            return ((ByExampleDAO) getFindDAO()).queryByExample(example, clazz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * @return Returns the findDAO.
     */
    public Object getFindDAO() {
        return findDAO;
    }

    /**
     * @param findDAO The findDAO to set.
     */
    public void setFindDAO(Object findDAO) {
        this.findDAO = findDAO;
    }

}


