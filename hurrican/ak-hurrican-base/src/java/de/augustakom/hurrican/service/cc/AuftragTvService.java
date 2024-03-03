/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2012 15:48:23
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.shared.view.TvFeedView;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Service-Interface, um TV-Signallieferungs Auftraege zu verwalten.
 */
public interface AuftragTvService extends ICCService {

    /**
     * Sucht alle {@link TvFeedView} fuer die gegebene List an Geo IDs
     */
    Map<Long, List<TvFeedView>> findTvFeed4GeoIdViews(List<Long> geoIds) throws FindException;

    /**
     * Sucht alle versorgende TV-Auftraege anhand der angegebenen geoId. Wenn der angegebenen geoId
     * nur ein mitversorgter Auftrag zugewiesen ist und dieser mit einem versogendem Auftrag gebuendelt ist, liefert
     * diese Methode nur den versorgenden Auftrag zurueck.
     */
    @Nonnull
    List<AuftragDaten> findVersorgendeAuftraege(Long geoId) throws FindException;

    /**
     * Sucht alle versorgende und die mitversorgten TV-Auftraege fuer das angegebene geoId. Wenn der angegebenen geoId
     * nur ein mitversorgter Auftrag zugewiesen ist und dieser mit einem versogendem Auftrag gebuendelt ist, liefert
     * diese Methode beide Auftraege zurueck.
     */
    @Nonnull
    List<AuftragDaten> findTvAuftraege(Long geoId) throws FindException;

    /**
     * Sucht alle {@link TvFeedView} fuer die gegebene List an Technikkuerzel (Ortsteil)
     */
    Map<String, List<TvFeedView>> findTvFeed4TechLocationNameViews(List<String> techLocationNames)
            throws FindException;

}
