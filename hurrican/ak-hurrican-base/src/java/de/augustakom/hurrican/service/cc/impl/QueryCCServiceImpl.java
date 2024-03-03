/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2006 10:22:17
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.jdbc.ResultSetHelper;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.base.iface.QueryDAO;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Service-Implementierung von QueryCCService.
 *
 *
 */
@CcTxRequired
public class QueryCCServiceImpl extends DefaultCCService implements QueryCCService {

    private static final Logger LOGGER = Logger.getLogger(QueryCCServiceImpl.class);

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
    @Override
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
    @Override
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
    @Override
    public <T> List<T> findByExample(Object example, Class<T> clazz) throws Exception {
        try {
            return ((ByExampleDAO) getFindDAO()).queryByExample(example, clazz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.QueryCCService#findByExample(java.lang.Object, java.lang.Class,
     * java.lang.String[], java.lang.String[])
     */
    @Override
    public <T> List<T> findByExample(Object example, Class<T> clazz, String[] orderAsc, String[] orderDesc) throws FindException {
        try {
            return ((ByExampleDAO) getFindDAO()).queryByExample(example, clazz, orderAsc, orderDesc);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.QueryCCService#findUniqueByExample(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> T findUniqueByExample(Object example, Class<T> clazz) throws FindException {
        try {
            List<T> result = findByExample(example, clazz);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() > 1) {
                    throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, result.size() });
                }
                return result.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
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


