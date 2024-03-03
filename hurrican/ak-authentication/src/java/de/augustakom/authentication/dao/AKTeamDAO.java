/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.01.2015
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKTeam;

/**
 * Interface zur Definition von Methoden zum Arbeiten mit Team-Objekten.
 */
public interface AKTeamDAO {
    /**
     * @return a list with all available {@link AKTeam}s.
     */
    List<AKTeam> findAll();

}
