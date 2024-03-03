/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 15:31:19
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKExtServiceProviderView;
import de.augustakom.authentication.model.AKNiederlassungView;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;


/**
 * Interface definiert Methoden fuer DAO-Objekte zur Verwaltung von AKDepartment-Objekten.
 */
public interface AKDepartmentDAO extends ByExampleDAO {

    /**
     * Sucht nach allen Department-Eintraegen.
     *
     * @return Liste von AKDepartment-Objekten (never {@code null}).
     */
    public List<AKDepartment> findAll();

    /**
     * Liefert die Abteilung zu einer bestimmten Id
     *
     * @return Abteilungs-Objekt
     */
    public AKDepartment findById(Long id);

    /**
     * Sucht nach allen Niederlassungs-Eintraegen.
     *
     * @return Liste von Niederlassungs-Objekten (never {@code null}).
     */
    public List<AKNiederlassungView> findAllNiederlassungen();

    /**
     * Liefert die Niederlassung zu einer bestimmten Id
     *
     * @param id der gesuchten Niederlassung
     * @return Niederlassungs-Objekt
     */
    public AKNiederlassungView findNiederlassungById(Long id);

    /**
     * Sucht nach allen ExtServiceProvider-Eintraegen.
     *
     * @return Liste von ExtServiceProvider-Objekten (never {@code null}).
     */
    public List<AKExtServiceProviderView> findAllExtServiceProvider();

    /**
     * Liefert die ExtServiceProvider zu einer bestimmten Id
     *
     * @param Id des gesuchten ExtServiceProviders
     * @return ExtServiceProvider-Objekt
     */
    public AKExtServiceProviderView findExtServiceProviderById(Long id);

    /**
     * Erzeugt oder aktualisiert das AKDepartment-Objekt.
     */
    public void saveOrUpdate(AKDepartment department);
}
