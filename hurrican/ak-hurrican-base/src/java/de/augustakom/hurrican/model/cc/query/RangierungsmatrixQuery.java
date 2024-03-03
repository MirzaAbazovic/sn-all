/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2004 14:47:02
 */
package de.augustakom.hurrican.model.cc.query;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche nach Rangierungsmatrix-Eintraegen.
 */
public class RangierungsmatrixQuery extends AbstractHurricanQuery implements DebugModel {

    private Long uevtId = null;
    private Long hvtIdStandort = null;
    private Long produktId = null;
    private List<Long> produkt2PhysikTypIds = null;

    /**
     * Fuegt dem Query eine weitere Produkt2PhysikTypId hinzu.
     */
    public void addProdukt2PhysikTypId(Long p2ptId) {
        if (produkt2PhysikTypIds == null) {
            produkt2PhysikTypIds = new ArrayList<>();
        }
        produkt2PhysikTypIds.add(p2ptId);
    }

    /**
     * Ruft die Methode <code>addProdukt2PhysikTypId</code> auf!
     *
     * @param produkt2PhysikTypId The produkt2PhysikTypId to set.
     */
    public void setProdukt2PhysikTypId(Long produkt2PhysikTypId) {
        this.addProdukt2PhysikTypId(produkt2PhysikTypId);
    }

    /**
     * @return Returns the List with Produkt2PhysikTypId´s
     */
    public List<Long> getProdukt2PhysikTypIds() {
        return produkt2PhysikTypIds;
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
     * @return Returns the hvtIdStandort.
     */
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    /**
     * ID des HVT-Standorts fuer den Rangierungsmatrizen gesucht werden. <br> WICHTIG: die ID ist kein Filter-Parameter
     * fuer die Spalte 'HVT_STANDORT_ID_ZIEL' der Rangierungsmatrix!!! <br><br> Ist der Parameter gesetzt, werden die
     * Matrizen zu allen UEVTs gesucht, die dem angegebenen HVT zugeordnet sind.
     *
     * @param hvtStandortId The hvtIdStandort to set.
     */
    public void setHvtIdStandort(Long hvtStandortId) {
        this.hvtIdStandort = hvtStandortId;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getUevtId() != null) {
            return false;
        }
        if (getProduktId() != null) {
            return false;
        }
        if (getHvtIdStandort() != null) {
            return false;
        }
        if ((getProdukt2PhysikTypIds() != null) && !getProdukt2PhysikTypIds().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + RangierungsmatrixQuery.class.getName());
            logger.debug("  UEVT-ID        : " + getUevtId());
            logger.debug("  Produkt-ID     : " + getProduktId());
            logger.debug("  HVT-Std-ID     : " + getHvtIdStandort());
            logger.debug("  Anzahl P2PT-IDs: " + ((getProdukt2PhysikTypIds() != null)
                    ? getProdukt2PhysikTypIds().size() : -1));
        }
    }

}


