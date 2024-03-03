/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 06:58:55
 */
package de.augustakom.hurrican.service.wholesale;

import java.util.*;

import de.augustakom.hurrican.model.wholesale.WholesaleAssignedVdslProfile;
import de.augustakom.hurrican.model.wholesale.WholesaleChangePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeReason;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeVdslProfileRequest;
import de.augustakom.hurrican.model.wholesale.WholesalePort;
import de.augustakom.hurrican.model.wholesale.WholesaleVdslProfile;
import de.augustakom.hurrican.service.cc.ICCService;

/**
 * Definiert Service-Methoden fuer Stoerungsbehebungen auf Wholesale-Auftraegen.
 */
public interface WholesaleFaultClearanceService extends ICCService {

    /**
     * enum ueber den entschieden wird, welche Art von Aenderungsgruenden ermittelt werden sollen.
     */
    public enum RequestedChangeReasonType {
        /**
         * Aenderungsgruende fuer einen Port-Wechsel
         */
        CHANGE_PORT,
        /**
         * Aenderungsgruende fuer einen VDSL-Profil Wechsel
         */
        CHANGE_VDSL_PROFILE;
    }

    /**
     * Ermittelt eine Liste von Aenderungsgruenden. <br>
     *
     * @param changeReasonType Angabe der Art der Aenderungsgruende (siehe {@link RequestedChangeReasonType}
     * @return
     */
    public List<WholesaleChangeReason> getChangeReasons(RequestedChangeReasonType changeReasonType);

    /**
     * Ermittelt alle freien / freigabebereiten Ports (bzw. Rangierungen), die fuer den Auftrag mit der LineId {@code
     * lineId} in Frage kommen. <br> Achtung: die maximale Bandbreite der Ports wird mit dem Auftrag NICHT abgeglichen!
     *
     * @param lineId
     * @return Liste der zur Verfuegung stehenden Ports / Rangierungen.
     */
    public List<WholesalePort> getAvailablePorts(String lineId);

    /**
     * Fuehrt einen Port-Wechsel auf dem im Request angegebenen Auftrag (LineId) durch.
     *
     * @param changePortRequest
     */
    public void changePort(WholesaleChangePortRequest changePortRequest);

    /**
     * Ermittelt alle VDSL Profile die einem Auftrag zugeordnet sind (Histories).
     *
     * @param lineId
     * @return
     */
    List<WholesaleAssignedVdslProfile> getAssignedVdslProfiles(String lineId);

    /**
     * Ermittelt eine Liste von VDSL Profilen die einem Auftrag zugeordnet werden können. Die Liste enthält nur Profile
     * deren Bandbreite kleiner oder gleich ist als die Bandbreite des Auftrages.
     *
     * @param lineId
     * @return
     */
    List<WholesaleVdslProfile> getPossibleVdslProfiles(String lineId);

    /**
     * Wechselt das VDSL Profil für den gegebenen Auftrag (LineId).
     *
     * @param request
     */
    void changeVdslProfile(WholesaleChangeVdslProfileRequest request);
}


