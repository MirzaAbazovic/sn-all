/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 07:09:58
 */
package de.augustakom.hurrican.model.wholesale;

import java.util.*;

/**
 * DTO zur Uebermittlung von verfuegbaren Ports sowie der moeglichen "ChangeReasons" fuer einen Port-Wechsel.
 */
public class WholesaleGetAvailablePortsResponse {

    private List<WholesalePort> ports;
    private List<WholesaleChangeReason> possibleChangeReasons;

    public List<WholesalePort> getPorts() {
        return ports;
    }

    public void setPorts(List<WholesalePort> ports) {
        this.ports = ports;
    }

    public List<WholesaleChangeReason> getPossibleChangeReasons() {
        return possibleChangeReasons;
    }

    public void setPossibleChangeReasons(List<WholesaleChangeReason> possibleChangeReasons) {
        this.possibleChangeReasons = possibleChangeReasons;
    }

}


