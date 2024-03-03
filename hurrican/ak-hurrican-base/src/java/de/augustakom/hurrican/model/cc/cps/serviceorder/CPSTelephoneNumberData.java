/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 10:27:39
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSDNServiceConverter;


/**
 * Modell-Klasse zur Abbildung einer Rufnummer (EWSD) fuer die CPS-Provisionierung.
 *
 *
 */
@XStreamAlias("NUMBER")
public class CPSTelephoneNumberData extends AbstractCpsDnWithCarrierIdData implements ICPSDNServiceAwareModel {

    @XStreamAlias("MAIN_DN")
    private String mainDN = null;
    @XStreamAlias("SERVICES")
    @XStreamConverter(CPSDNServiceConverter.class)
    private List<CPSDNServiceData> dnServices = null;

    /**
     * Fuegt dem Modell ein weiteres Objekt vom Typ <code>CPSDNServiceData</code> hinzu. <br> Das Modell wird der Liste
     * <code>dnServices</code> zugeordnet.
     *
     * @param toAdd
     */
    public void addDNService(CPSDNServiceData toAdd) {
        if (getDnServices() == null) {
            setDnServices(new ArrayList<CPSDNServiceData>());
        }
        getDnServices().add(toAdd);
    }

    /**
     * @return the mainDN
     */
    public String getMainDN() {
        return mainDN;
    }

    /**
     * @param mainDN the mainDN to set
     */
    public void setMainDN(String mainDN) {
        this.mainDN = mainDN;
    }

    /**
     * @return the dnServices
     */
    public List<CPSDNServiceData> getDnServices() {
        return dnServices;
    }

    /**
     * @param dnServices the dnServices to set
     */
    public void setDnServices(List<CPSDNServiceData> dnServices) {
        this.dnServices = dnServices;
    }

}


