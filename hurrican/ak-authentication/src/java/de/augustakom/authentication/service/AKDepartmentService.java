/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 15:27:07
 */
package de.augustakom.authentication.service;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKExtServiceProviderView;
import de.augustakom.authentication.model.AKNiederlassungView;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.reports.AKReportException;

/**
 * Interface fuer einen DepartmentService. <br> Ueber den Department-Service koennen AKDepartment-Objekte verwaltet
 * werden.
 *
 *
 */
public interface AKDepartmentService extends IAuthenticationService {

    /**
     * Sucht nach allen vorhandenen Department-Eintraegen und gibt diese zurueck.
     *
     * @return Liste mit AKDepartment-Objekten.
     * @throws AKAuthenticationException wenn bei der Abfrage der Departments ein Fehler auftritt.
     */
    public List<AKDepartment> findAll() throws AKAuthenticationException;

    /**
     * Erzeugt ein JasperPrint-Objekt mit einer Uebersicht ueber alle definierten Abteilungen und Benutzer.
     *
     * @return JasperPrint-Objekt
     * @throws AKReportException wenn beim Erzeugen des Reports ein Fehler auftritt.
     */
    public JasperPrint reportDepartmentUsers() throws AKReportException;

    /**
     * Liefert anhand der id den Abteilungsnamen zurueck
     *
     * @param id der Abteilung
     * @return Name der Abteilung
     *
     */
    public AKDepartment findDepartmentById(Long id) throws AKAuthenticationException;

    /**
     * Sucht nach allen vorhandenen Niederlassungs-Eintraegen und gibt diese zurueck.
     *
     * @return Liste mit AKNiederlassung-Objekten.
     * @throws AKAuthenticationException falls ein Fehler auftritt.
     */
    public List<AKNiederlassungView> findAllNiederlassungen() throws AKAuthenticationException;

    /**
     * Liefert anhand der id den Niederlassung zurueck
     *
     * @param id der Niederlassung
     * @return Niederlassung-Objekt
     *
     */
    public AKNiederlassungView findNiederlassungById(Long id) throws AKAuthenticationException;

    /**
     * Sucht nach allen vorhandenen ExtServiceProvider-Eintraegen und gibt diese zurueck.
     *
     * @return Liste mit ExtServiceProvider-Objekten.
     * @throws AKAuthenticationException falls ein Fehler auftritt.
     */
    public List<AKExtServiceProviderView> findAllExtServiceProvider() throws AKAuthenticationException;

    /**
     * Liefert anhand der id den ExtServiceProvider zurueck
     *
     * @param id des ExtServiceProviders
     * @return ExtServiceProvider-Objekt
     *
     */
    public AKExtServiceProviderView findExtServiceProviderById(Long id) throws AKAuthenticationException;

    /**
     * Erzeugt oder aktualisiert eine Abteilung.
     */
    public void save(AKDepartment department) throws AKAuthenticationException;
}
