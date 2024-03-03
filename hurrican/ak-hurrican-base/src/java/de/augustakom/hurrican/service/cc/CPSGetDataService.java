/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.03.2012 15:31:41
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSFTTBData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsEndpointDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsNetworkDevice;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Service um CPS-relevante Daten zu ermitteln
 */
public interface CPSGetDataService extends ICCService {

    boolean isFttbAuftrag(Long auftragId) throws FindException;

    boolean isFtthAuftrag(Long auftragId) throws FindException;

    CPSFTTBData getFttbData4Wholesale(Long auftragId, String portId, Date when) throws FindException;

    /**
     * Provides FTTB data for given Hurrican auftrag and port equipment.
     * @param auftragId
     * @param portName
     * @param when Zeitpunkt der Provisionierung
     * @return
     * @throws FindException
     */
    CpsAccessDevice getFttbData(Long auftragId, String portName, Date when) throws FindException;

    /**
     * Ermittelt HVTGruppe und HVTStandort für die Endstelle des Auftrages.
     *
     * @param auftragId
     * @param esTyp     Endstelle.ENDSTELLEN_TYP_B | Endstelle.ENDSTELLEN_TYP_A
     * @return HVTGruppe und HVTStandort fuer die auftragId zum angegebenen Endstellen-Typ
     * @throws FindException
     */
    Pair<HVTGruppe, HVTStandort> findHvtGruppeAndStandort(Long auftragId, String esTyp) throws FindException;

    /**
     * Provides FTTH data for given Hurrican auftrag and port equipment.
     * @param auftragId
     * @param when Zeitpunkt der Provisionierung
     * @param deviceNecessary wird ein Radius Account provisioniert? (abhaengig davon werden up-/downstream gesetzt)
     * @param portName PortId aus VBZ und Kundenname
     * @return
     */
    @Nonnull
    CpsAccessDevice getFtthData(long auftragId, @Nonnull Date when, boolean deviceNecessary, @Nonnull String portName);

    /**
     *
     * @param oltGponPort the GPON Port of the endpoint device on the network rack
     * @param networkRack the network rack (typically OLT or GSLAM)
     * @return
     */
    CpsNetworkDevice getNetworkDevice(String oltGponPort, HWRack networkRack);

    /**
     * Create an endpoint device.
     * @param endpointRack the endpoint rack (currently ONT or MDU)
     * @return
     */
    CpsEndpointDevice getEndpointDevice(HWOltChild endpointRack);
}


