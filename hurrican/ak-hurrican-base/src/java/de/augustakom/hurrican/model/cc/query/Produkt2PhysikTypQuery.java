/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2004 09:09:03
 */
package de.augustakom.hurrican.model.cc.query;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query fuer die Suche nach Produkt-PhysikTyp-Zuordnungen.
 *
 *
 */
public class Produkt2PhysikTypQuery extends AbstractHurricanQuery {

    private Long produktId = null;
    private Long physikTypId = null;
    private Long parentPhysikTypId = null;
    private Long hvtTechnikId = null;

    /**
     * @return Returns the hvtTechnikId.
     */
    public Long getHvtTechnikId() {
        return hvtTechnikId;
    }

    /**
     * @param hvtTechnikId The hvtTechnikId to set.
     */
    public void setHvtTechnikId(Long hvtTechnikId) {
        this.hvtTechnikId = hvtTechnikId;
    }

    /**
     * @return Returns the parentPhysikTypId.
     */
    public Long getParentPhysikTypId() {
        return parentPhysikTypId;
    }

    /**
     * @param parentPhysikTyp The parentPhysikTyp to set.
     */
    public void setParentPhysikTypId(Long parentPhysikTypId) {
        this.parentPhysikTypId = parentPhysikTypId;
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
    public void setProduktId(Long prodId) {
        this.produktId = prodId;
    }

    /**
     * @return Returns the physikTypId.
     */
    public Long getPhysikTypId() {
        return physikTypId;
    }

    /**
     * @param physikTypId The physikTypId to set.
     */
    public void setPhysikTypId(Long physikTypId) {
        this.physikTypId = physikTypId;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getProduktId() != null) {
            return false;
        }
        if (getPhysikTypId() != null) {
            return false;
        }
        if (getParentPhysikTypId() != null) {
            return false;
        }
        if (getHvtTechnikId() != null) {
            return false;
        }
        return true;
    }

}


