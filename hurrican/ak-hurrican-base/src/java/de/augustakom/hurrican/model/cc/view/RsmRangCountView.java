/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2010 11:27:47
 */

package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.RsmRangCount;


/**
 * Erweitert Modell fuer ein Objekt vom Typ RSM_RANG_COUNT
 *
 *
 */
public class RsmRangCountView extends RsmRangCount {

    private String niederlassung = null;
    private String cluster = null;

    /**
     * Copy Constructor fuer die Superklasse
     */
    public RsmRangCountView(RsmRangCount rsmRangCount) {
        super(rsmRangCount);
    }


    /**
     * Default Constructor fuer die Superklasse
     */
    public RsmRangCountView() {
    }


    /**
     * @return the niederlassungId
     */
    public String getNiederlassung() {
        return niederlassung;
    }


    /**
     * @param niederlassung the niederlassung to set
     */
    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
