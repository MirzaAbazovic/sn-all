/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 10:47:10
 */

package de.augustakom.hurrican.model.cc;

import java.io.*;
import java.util.*;

/**
 * Entitaet zur Abbildung einer statischen Route eines Endgeraets.
 *
 *
 */
public class Routing extends AbstractCCIDModel {

    public static final RoutingComparator ROUTING_COMPARATOR = new RoutingComparator();

    public static final class RoutingComparator implements Comparator<Routing>, Serializable {
        @Override
        public int compare(Routing o1, Routing o2) {
            if (!o1.getDestinationAdressRef().getAddress().equals(o2.getDestinationAdressRef().getAddress())) {
                return o1.getDestinationAdressRef().getAddress().compareTo(o2.getDestinationAdressRef().getAddress());
            }
            if (!o1.getNextHop().equals(o2.getNextHop())) {
                return o1.getNextHop().compareTo(o2.getNextHop());
            }
            return 0;
        }
    }

    /**
     * Freitext evtl. die IP-Adresse des naechsten Routers.
     */
    private String nextHop;

    /**
     * Bemerkung zu der Route.
     */
    private String bemerkung;

    /**
     * Ziel-IP-Adresse der Route (IPAddress Reference).
     */
    private IPAddress destinationAdressRef;


    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public IPAddress getDestinationAdressRef() {
        return destinationAdressRef;
    }

    public void setDestinationAdressRef(IPAddress destinationAdressRef) {
        this.destinationAdressRef = destinationAdressRef;
    }
}
