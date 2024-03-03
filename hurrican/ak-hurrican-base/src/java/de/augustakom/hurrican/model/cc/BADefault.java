/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2004 16:12:45
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.BooleanTools;


/**
 * Basisklasse fuer Modelle, die Bauauftragsverlaeufe definieren.
 *
 *
 */
public abstract class BADefault extends AbstractCCIDModel implements DebugModel {

    private Boolean ewsd = null;
    private Boolean ips = null;
    private Boolean sdh = null;
    private Boolean sct = null;

    /**
     * Gibt die Anzahl der Abteilungen zurueck, die in den Verlauf integriert sind.
     *
     * @return Anzahl der Abteilungen, die in den Verlauf integriert sind.
     */
    public int getAbteilungCount() {
        int count = 0;
        if (BooleanTools.nullToFalse(getEwsd())) {
            count++;
        }
        if (BooleanTools.nullToFalse(getIps())) {
            count++;
        }
        if (BooleanTools.nullToFalse(getSct())) {
            count++;
        }
        if (BooleanTools.nullToFalse(getSdh())) {
            count++;
        }

        return count;
    }

    /**
     * Gibt eine Liste mit den IDs der Abteilungen zurueck, die in den Verlauf integriert sind. <br>
     *
     * @return Liste mit den IDs der Abteilungen, die in den Verlauf integriert sind.
     */
    public List<Long> getAbteilungIds() {
        List<Long> abtIds = new ArrayList<Long>();

        if (BooleanTools.nullToFalse(getEwsd())) {
            abtIds.add(Abteilung.ST_VOICE);
        }
        if (BooleanTools.nullToFalse(getIps())) {
            abtIds.add(Abteilung.ST_ONLINE);
        }
        if (BooleanTools.nullToFalse(getSct())) {
            abtIds.add(Abteilung.FIELD_SERVICE);
        }
        if (BooleanTools.nullToFalse(getSdh())) {
            abtIds.add(Abteilung.ST_CONNECT);
        }

        return abtIds;
    }

    /**
     * Gibt eine Map zurueck, die als Key die ID jeder Abteilung besitzt, die auf <code>true</code> gesetzt ist.
     *
     * @return
     */
    public Set<Long> getAbteilungIdSet() {
        Set<Long> result = new HashSet<Long>();
        if (BooleanTools.nullToFalse(getEwsd())) {
            result.add(Abteilung.ST_VOICE);
        }
        if (BooleanTools.nullToFalse(getIps())) {
            result.add(Abteilung.ST_ONLINE);
        }
        if (BooleanTools.nullToFalse(getSct())) {
            result.add(Abteilung.FIELD_SERVICE);
        }
        if (BooleanTools.nullToFalse(getSdh())) {
            result.add(Abteilung.ST_CONNECT);
        }
        return result;
    }

    /**
     * Ueberprueft, ob die Abteilung mit der ID <code>abtId</code> fuer den Bauauftrag konfiguriert ist.
     *
     * @param abtId
     * @return
     */
    public boolean containsAbteilung(Long abtId) {
        return getAbteilungIdSet().contains(abtId);
    }

    /**
     * Ueberprueft, ob eine Abteilung mit einer der IDs <code>abtIds</code> fuer den Bauauftrag konfiguriert ist.
     *
     * @param abtIds
     * @return
     */
    public boolean containsAbteilungen(Long... abtIds) {
        boolean result = false;
        for (Long abtId : abtIds) {
            result |= getAbteilungIdSet().contains(abtId);
        }
        return result;
    }

    /**
     * @return Returns the ewsd.
     */
    public Boolean getEwsd() {
        return ewsd;
    }

    /**
     * @param ewsd The ewsd to set.
     */
    public void setEwsd(Boolean ewsd) {
        this.ewsd = ewsd;
    }

    /**
     * @return Returns the ips.
     */
    public Boolean getIps() {
        return ips;
    }

    /**
     * @param ips The ips to set.
     */
    public void setIps(Boolean ips) {
        this.ips = ips;
    }

    /**
     * @return Returns the sct.
     */
    public Boolean getSct() {
        return sct;
    }

    /**
     * @param sct The sct to set.
     */
    public void setSct(Boolean sct) {
        this.sct = sct;
    }

    /**
     * @return Returns the sdh.
     */
    public Boolean getSdh() {
        return sdh;
    }

    /**
     * @param sdh The sdh to set.
     */
    public void setSdh(Boolean sdh) {
        this.sdh = sdh;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("  ID    : " + getId());
            logger.debug("  EWSD  : " + getEwsd());
            logger.debug("  IPS   : " + getIps());
            logger.debug("  SCT   : " + getSct());
            logger.debug("  SDH   : " + getSdh());
        }
    }
}


