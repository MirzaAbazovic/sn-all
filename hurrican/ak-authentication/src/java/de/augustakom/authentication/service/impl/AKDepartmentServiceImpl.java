/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 15:28:43
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import javax.annotation.*;
import javax.sql.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

import de.augustakom.authentication.dao.AKDepartmentDAO;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKExtServiceProviderView;
import de.augustakom.authentication.model.AKNiederlassungView;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;


/**
 * Implementierung eines Department-Services. <br>
 *
 *
 */
@AuthenticationTx
public class AKDepartmentServiceImpl implements AKDepartmentService {

    private static final Logger LOGGER = Logger.getLogger(AKDepartmentService.class);

    @Resource(name = "de.augustakom.authentication.dao.AKDepartmentDAO")
    private AKDepartmentDAO departmentDao;

    @Resource(name = AKAuthenticationServiceNames.DATA_SOURCE)
    private DataSource dataSource;

    @Override
    public List<AKDepartment> findAll() throws AKAuthenticationException {
        try {
            return departmentDao.findAll();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint reportDepartmentUsers() throws AKReportException {
        AKJasperReportContext ctx = new AKJasperReportContext();
        ctx.setReportName("de/augustakom/authentication/reports/AbteilungBenutzerMaster.jasper");
        ctx.setConnection(dataSource);

        AKJasperReportHelper jrh = new AKJasperReportHelper();
        return jrh.createReport(ctx);
    }

    @Override
    public AKDepartment findDepartmentById(Long id) throws AKAuthenticationException {
        try {
            return departmentDao.findById(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKNiederlassungView> findAllNiederlassungen() throws AKAuthenticationException {
        try {
            return departmentDao.findAllNiederlassungen();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AKNiederlassungView findNiederlassungById(Long id) throws AKAuthenticationException {
        try {
            return departmentDao.findNiederlassungById(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKExtServiceProviderView> findAllExtServiceProvider() throws AKAuthenticationException {
        try {
            return departmentDao.findAllExtServiceProvider();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AKExtServiceProviderView findExtServiceProviderById(Long id) throws AKAuthenticationException {
        try {
            return departmentDao.findExtServiceProviderById(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void save(AKDepartment department) throws AKAuthenticationException {
        try {
            departmentDao.saveOrUpdate(department);
            department.notifyObservers(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }
}
