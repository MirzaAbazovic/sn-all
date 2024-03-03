/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2004 12:07:50
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell bildet die Beziehung zwischen Produkten und HVTs ab. <br> Die Eintraege/Modelle werden nur dann benoetigt,
 * wenn ein Produkt 'virtuell' abgebildet werden muss.
 *
 *
 */
public class UEVT2Ziel extends AbstractCCIDModel {

    private Long produktId = null;
    private Long uevtId = null;
    private Long hvtStandortIdZiel = null;

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
}


