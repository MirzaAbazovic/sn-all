/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 11:56:11
 */
package de.augustakom.hurrican.model.cc;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;


/**
 * Modell bildet eine Matrix fuer die Rangierungen ab.
 *
 *
 */
public class Rangierungsmatrix extends AbstractCCHistoryModel implements DebugModel {

    private Long produktId = null;
    private Long uevtId = null;
    private Long produkt2PhysikTypId = null;
    private Integer priority = null;
    private Long hvtStandortIdZiel = null;
    private Boolean projektierung = null;
    private String bearbeiter = null;

    /**
     * @return Returns the bearbeiter.
     */
    public String getBearbeiter() {
        return bearbeiter;
    }

    /**
     * @param bearbeiter The bearbeiter to set.
     */
    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    /**
     * @return Returns the hvtStandortIdZiel.
     */
    public Long getHvtStandortIdZiel() {
        return hvtStandortIdZiel;
    }

    /**
     * @param hvtStandortIdZiel The hvtStandortIdZiel to set.
     */
    public void setHvtStandortIdZiel(Long hvtStandortIdZiel) {
        this.hvtStandortIdZiel = hvtStandortIdZiel;
    }

    /**
     * @return Returns the priority.
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority The priority to set.
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return Returns the produkt2PhysikTypId.
     */
    public Long getProdukt2PhysikTypId() {
        return produkt2PhysikTypId;
    }

    /**
     * @param produkt2PhysikTypId The produkt2PhysikTypId to set.
     */
    public void setProdukt2PhysikTypId(Long produkt2PhysikTypId) {
        this.produkt2PhysikTypId = produkt2PhysikTypId;
    }

    /**
     * @return Returns the produktId.
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param produktId The produktId to set.
     */
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    /**
     * @return Returns the projektierung.
     */
    public Boolean getProjektierung() {
        return projektierung;
    }

    /**
     * @param projektierung The projektierung to set.
     */
    public void setProjektierung(Boolean projektierung) {
        this.projektierung = projektierung;
    }

    /**
     * @return Returns the uevtId.
     */
    public Long getUevtId() {
        return uevtId;
    }

    /**
     * @param uevtId The uevtId to set.
     */
    public void setUevtId(Long uevtId) {
        this.uevtId = uevtId;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + Rangierungsmatrix.class.getName());
            logger.debug("  ID            : " + getId());
            logger.debug("  PRODUKT_ID    : " + getProduktId());
            logger.debug("  UEVT_ID       : " + getUevtId());
            logger.debug("  PROD2PHYSIKTYP: " + getProdukt2PhysikTypId());
            logger.debug("  HVT_ID_ZIEL   : " + getHvtStandortIdZiel());
            logger.debug("  BEARBEITER    : " + getBearbeiter());
        }
    }
}


