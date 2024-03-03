/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 14:08:18
 */
package de.augustakom.hurrican.model.cc.kubena;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell zur Abbildung eines KuBena-Headers (KuBena=Kunden-Benachrichtigung).
 *
 *
 */
public class Kubena extends AbstractCCIDModel {

    /**
     * Kriterium fuer die Kubena-Ermittlung ueber VerbindungsBezeichnung.
     */
    public static final Short KRITERIUM_VBZ = Short.valueOf((short) 1);
    /**
     * Kriterium fuer die Kubena-Ermittlung ueber HVT.
     */
    public static final Short KRITERIUM_HVT = Short.valueOf((short) 2);
    /**
     * Kriterium fuer die Kubena-Ermittlung ueber HVT und Produkt.
     */
    public static final Short KRITERIUM_HVT_PROD = Short.valueOf((short) 3);

    private Date datum = null;
    private Short kriterium = null;
    private String name = null;
    private Date schaltzeitVon = null;
    private Date schaltzeitBis = null;

    public static final String GET_DISPLAY_NAME = "getDisplayName";

    /**
     * Gibt alle moeglichen Kriterien fuer eine Kubena zurueck.
     *
     * @return
     */
    public static Short[] getKriterien() {
        return new Short[] { KRITERIUM_VBZ, KRITERIUM_HVT, KRITERIUM_HVT_PROD };
    }

    /**
     * Gibt den Namen zurueck, der fuer Anzeigen verwendet werden soll.
     *
     * @return
     */
    public String getDisplayName() {
        if (getDatum() != null) {
            StringBuilder sb = new StringBuilder(getName());
            sb.append(" (");
            sb.append(DateTools.formatDate(getDatum(), DateTools.PATTERN_DAY_MONTH_YEAR));
            sb.append(")");
            return sb.toString();
        }
        else {
            return getName();
        }
    }

    /**
     * @return Returns the datum.
     */
    public Date getDatum() {
        return datum;
    }

    /**
     * @param datum The datum to set.
     */
    public void setDatum(Date datum) {
        this.datum = datum;
    }

    /**
     * @return Returns the kriterium.
     */
    public Short getKriterium() {
        return kriterium;
    }

    /**
     * @param kriterium The kriterium to set.
     */
    public void setKriterium(Short kriterium) {
        this.kriterium = kriterium;
    }

    /**
     * @return Returns the schaltzeitBis.
     */
    public Date getSchaltzeitBis() {
        return schaltzeitBis;
    }

    /**
     * @param schaltzeitBis The schaltzeitBis to set.
     */
    public void setSchaltzeitBis(Date schaltzeitBis) {
        this.schaltzeitBis = schaltzeitBis;
    }

    /**
     * @return Returns the schaltzeitVon.
     */
    public Date getSchaltzeitVon() {
        return schaltzeitVon;
    }

    /**
     * @param schaltzeitVon The schaltzeitVon to set.
     */
    public void setSchaltzeitVon(Date schaltzeitVon) {
        this.schaltzeitVon = schaltzeitVon;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

}


