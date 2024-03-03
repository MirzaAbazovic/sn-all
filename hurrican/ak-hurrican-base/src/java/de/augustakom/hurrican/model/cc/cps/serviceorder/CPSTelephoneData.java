/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 10:20:22
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.cc.cps.serviceorder.utils.CPSPhoneNumberModificator;


/**
 * Modell-Klasse fuer die Abbildung von Telephone-Daten (EWSD) fuer die CPS-Provisionierung.
 *
 *
 */
@XStreamAlias("TELEPHONE")
public class CPSTelephoneData extends AbstractCPSServiceOrderDataModel {

    /**
     * Konstante fuer 'type' definiert einen ISDN-Anschluss.
     */
    public static final String TELEPHONE_TYPE_ISDN = "ISDN";
    /**
     * Konstante fuer 'type' definiert einen PBX-Anschluss.
     */
    public static final String TELEPHONE_TYPE_PBX = "PBX";
    /**
     * Konstante fuer 'type' definiert einen Analog-Anschluss.
     */
    public static final String TELEPHONE_TYPE_ANALOG = "POTS";

    @XStreamAlias("TYPE")
    private String type = null;
    @XStreamAlias("ORIG1")
    private Integer orig1 = null;
    @XStreamAlias("PORTS")
    private List<CPSPortData> ports = null;
    @XStreamAlias("NUMBERS")
    private List<CPSTelephoneNumberData> telephoneNumbers = null;

    /**
     * Fuegt dem Modell ein weiteres Objekt vom Typ <code>CPSPortData</code> hinzu. <br> Das Modell wird der Liste
     * <code>ports</code> zugeordnet.
     *
     * @param toAdd
     */
    public void addPort(CPSPortData toAdd) {
        if (getPorts() == null) {
            setPorts(new ArrayList<CPSPortData>());
        }
        getPorts().add(toAdd);
    }

    /**
     * Fuegt dem Modell ein weiteres Objekt vom Typ <code>CPSTelephoneNumberData</code> hinzu. <br> Das Modell wird der
     * Liste <code>telephoneNumbers</code> zugeordnet.
     *
     * @param toAdd
     */
    @SuppressWarnings("unchecked")
    public void addCPSTelephoneNumber(CPSTelephoneNumberData toAdd) {
        if (getTelephoneNumbers() == null) {
            setTelephoneNumbers(new ArrayList<CPSTelephoneNumberData>());
        }

        // DIRECT_DIAL pruefen und ggf. modifizieren!
        CPSPhoneNumberModificator.modifyDirectDial((List) getTelephoneNumbers(), (AbstractCPSDNData) toAdd);

        getTelephoneNumbers().add(toAdd);
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the orig1
     */
    public Integer getOrig1() {
        return orig1;
    }

    /**
     * @param orig1 the orig1 to set
     */
    public void setOrig1(Integer orig1) {
        this.orig1 = orig1;
    }

    /**
     * @return the ports
     */
    public List<CPSPortData> getPorts() {
        return ports;
    }

    /**
     * @param ports the ports to set
     */
    public void setPorts(List<CPSPortData> ports) {
        this.ports = ports;
    }

    /**
     * @return the telephoneNumbers
     */
    public List<CPSTelephoneNumberData> getTelephoneNumbers() {
        return telephoneNumbers;
    }

    /**
     * @param telephoneNumbers the telephoneNumbers to set
     */
    public void setTelephoneNumbers(List<CPSTelephoneNumberData> telephoneNumbers) {
        this.telephoneNumbers = telephoneNumbers;
    }

}


