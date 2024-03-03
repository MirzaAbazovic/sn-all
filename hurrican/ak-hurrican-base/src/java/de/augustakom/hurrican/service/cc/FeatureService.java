/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 10:51:38
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Feature.FeatureName;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Service-Definition fuer die Verwaltung von Features.
 *
 *
 */
public interface FeatureService extends ICCService {

    /**
     * Mitteilt ob das Feature online ist oder nicht.
     *
     * @param name Name des gesuchten Features.
     * @return True falls das Feature online ist, sonst false.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Boolean isFeatureOnline(FeatureName name);

    /**
     * Mitteilt ob das Feature offline ist.
     *
     * @param name Name des gesuchten Features.
     * @return True wenn das Feature offline oder nicht vorhanden ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Boolean isFeatureOffline(FeatureName name);

    /**
     * Ermittelt alle Features, die online sind und liefert ein Set mit den Namen der Features zurueck.
     */
    public Set<String> getAllOnlineFeatures();

}
