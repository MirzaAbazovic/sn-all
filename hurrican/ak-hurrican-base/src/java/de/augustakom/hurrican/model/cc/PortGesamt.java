/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2005 16:15:20
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;

/**
 * Modell bildet einen EWSD-Datensatz ab. Dient zur freigabe der Rangierungen
 *
 * @author Hanke
 */
public class PortGesamt extends AbstractCCIDModel implements DebugModel {

    private static final Logger LOGGER = Logger.getLogger(PortGesamt.class);

    /* Konstanten zur Umsetzung der Importschnittstelle aus der EWSD */
    public static final String BEZEICHNUG_EWSD_1_AUGBURG_INTERN = "AUG01";
    public static final String BEZEICHNUG_EWSD_1_AUGBURG_EXTERN = "VE_01";
    public static final String BEZEICHNUG_EWSD_2_AUGBURG_INTERN = "AUG02";
    public static final String BEZEICHNUG_EWSD_2_AUGBURG_EXTERN = "VE_02";
    public static final String BEZEICHNUG_EWSD_1_MUENCHEN_INTERN = "MUC01";
    public static final String BEZEICHNUG_EWSD_1_MUENCHEN_EXTERN = "VE_03";
    public static final String BEZEICHNUG_EWSD_2_MUENCHEN_INTERN = "MUC02";
    public static final String BEZEICHNUG_EWSD_2_MUENCHEN_EXTERN = "VE_04";
    public static final String BEZEICHNUG_EWSD_4_MUENCHEN_INTERN = "MUC04";
    public static final String BEZEICHNUG_EWSD_5_MUENCHEN_INTERN = "MUC05";
    public static final String BEZEICHNUG_EWSD_1_NUERNBERG_INTERN = "NBG01";
    public static final String BEZEICHNUG_EWSD_1_NUERNBERG_EXTERN = "VE_05";

    private String pg_switch = null;
    private String port = null;
    private String feld3 = null;
    private String status = null;
    private String vorwahl = null;
    private String drn = null;
    private Date datum = null;

    /**
     * @param values
     * @return
     */
    public static PortGesamt createPortGesamt(String values) {
        StringTokenizer tokenizer = new StringTokenizer(values, " ");
        PortGesamt pg = new PortGesamt();
        pg.setDatum(new Date());
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = (token != null) ? token.trim() : null;
            LOGGER.debug("Feld: " + i + " token: " + token);
            switch (i) {
                case 0:
                    if (token.equals(BEZEICHNUG_EWSD_1_AUGBURG_EXTERN) ||
                            token.equals(BEZEICHNUG_EWSD_1_AUGBURG_INTERN)) {
                        pg.setPg_switch(BEZEICHNUG_EWSD_1_AUGBURG_INTERN);
                    }
                    else if (token.equals(BEZEICHNUG_EWSD_2_AUGBURG_EXTERN) ||
                            token.equals(BEZEICHNUG_EWSD_2_AUGBURG_INTERN)) {
                        pg.setPg_switch(BEZEICHNUG_EWSD_2_AUGBURG_INTERN);
                    }
                    else if (token.equals(BEZEICHNUG_EWSD_1_MUENCHEN_EXTERN) ||
                            token.equals(BEZEICHNUG_EWSD_1_MUENCHEN_INTERN)) {
                        pg.setPg_switch(BEZEICHNUG_EWSD_1_MUENCHEN_INTERN);
                    }
                    else if (token.equals(BEZEICHNUG_EWSD_2_MUENCHEN_EXTERN) ||
                            token.equals(BEZEICHNUG_EWSD_2_MUENCHEN_INTERN)) {
                        pg.setPg_switch(BEZEICHNUG_EWSD_2_MUENCHEN_INTERN);
                    }
                    else if (token.equals(BEZEICHNUG_EWSD_1_NUERNBERG_EXTERN) ||
                            token.equals(BEZEICHNUG_EWSD_1_NUERNBERG_INTERN)) {
                        pg.setPg_switch(BEZEICHNUG_EWSD_1_NUERNBERG_INTERN);
                    }
                    else if (token.equals(BEZEICHNUG_EWSD_4_MUENCHEN_INTERN)) {
                        pg.setPg_switch(BEZEICHNUG_EWSD_4_MUENCHEN_INTERN);
                    }
                    else if (token.equals(BEZEICHNUG_EWSD_5_MUENCHEN_INTERN)) {
                        pg.setPg_switch(BEZEICHNUG_EWSD_5_MUENCHEN_INTERN);
                    }
                    else {
                        pg.setPg_switch("nA");
                    }
                    break;
                case 1:
                    pg.setPort(token);
                    break;
                case 2:
                    pg.setFeld3(token);
                    break;
                case 3:
                    pg.setStatus(token);
                    break;
                case 4:
                    pg.setVorwahl(token);
                    break;
                case 5:
                    pg.setDrn(token);
                    break;
                default:
                    break;
            }
            i++;
        }
        return pg;
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
     * @return Returns the drn.
     */
    public String getDrn() {
        return drn;
    }

    /**
     * @param drn The drn to set.
     */
    public void setDrn(String drn) {
        this.drn = drn;
    }

    /**
     * @return Returns the feld3.
     */
    public String getFeld3() {
        return feld3;
    }

    /**
     * @param feld3 The feld3 to set.
     */
    public void setFeld3(String feld3) {
        this.feld3 = feld3;
    }

    /**
     * @return Returns the pg_switch.
     */
    public String getPg_switch() {
        return pg_switch;
    }

    /**
     * @param pg_switch The pg_switch to set.
     */
    public void setPg_switch(String pg_switch) {
        this.pg_switch = pg_switch;
    }

    /**
     * @return Returns the port.
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port The port to set.
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return Returns the vorwahl.
     */
    public String getVorwahl() {
        return vorwahl;
    }

    /**
     * @param vorwahl The vorwahl to set.
     */
    public void setVorwahl(String vorwahl) {
        this.vorwahl = vorwahl;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + PortGesamt.class.getName());
            logger.debug("  pg_switch        : " + getPg_switch());
            logger.debug("  port      : " + getPort());
            logger.debug("  feld3     : " + getFeld3());
            logger.debug("  status    : " + getStatus());
            logger.debug("  vorwahl   : " + getVorwahl());
            logger.debug("  drn       : " + getDrn());
            logger.debug("  datum     : " + getDatum());
        }
    }
}