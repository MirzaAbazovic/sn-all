/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 12:28:58
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse fuer die Abbildung von Rufnummern-Leistungsmerkmalen zur CPS-Provisionierung. <br> <br> Das Modell
 * besitzt nicht fuer jede moegliche Rufnummernleistung einen Parameter, sondern ist allgemein gehalten. Das bedeutet,
 * dass die gewuenschte Rufnummernleistung ueber 'Name' u. 'Value' definiert wird. <br> <br> Ueber einen
 * XStream-Converter koennen die Eintraege dann in die notwendigen XML-Elemente konvertiert werden.
 *
 *
 */
@XStreamAlias("SERVICES")
public class CPSDNServiceData extends AbstractCPSServiceOrderDataModel {

    /**
     * zu verwendendes Trennzeichen, um mehrere Values fuer einen Service anzugeben
     */
    public static final String CPS_VALUE_DELIMITER = ";";

    /**
     * enum mit Angabe der Rufnummernleistungen, bei denen als Parameter ein Boolean-Wert (0|1) an den CPS geschickt
     * werden soll, auch wenn ein anderer Parameter-Text eingetragen ist.
     */
    public enum DnServicesMapParameterToBoolean {
        MCID;
    }

    private String serviceName = null;
    private String serviceValue = null;

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceValue
     */
    public String getServiceValue() {
        return serviceValue;
    }

    /**
     * @param serviceValue the serviceValue to set
     */
    public void setServiceValue(String serviceValue) {
        this.serviceValue = serviceValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CPSDNServiceData that = (CPSDNServiceData) o;

        if (!serviceName.equals(that.serviceName)) {
            return false;
        }
        if (serviceValue != null ? !serviceValue.equals(that.serviceValue) : that.serviceValue != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceName.hashCode();
        result = 31 * result + (serviceValue != null ? serviceValue.hashCode() : 0);
        return result;
    }
}


