/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2005 07:40:41
 */
package de.augustakom.hurrican.model.cc.consistence;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Model bildet Datensaetze ab, die nicht richtig historisiert sind.
 *
 *
 */
public class HistoryConsistence extends AbstractCCModel {

    private Object id = null;
    private String idType = null;
    private String table = null;
    private int anzahl = 0;
    private String hinweis = null;

    /**
     * @return Returns the anzahl.
     */
    public int getAnzahl() {
        return anzahl;
    }

    /**
     * @param anzahl The anzahl to set.
     */
    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    /**
     * @return Returns the id.
     */
    public Object getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * @return Returns the idType.
     */
    public String getIdType() {
        return idType;
    }

    /**
     * @param idType The idType to set.
     */
    public void setIdType(String idType) {
        this.idType = idType;
    }

    /**
     * @return Returns the table.
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table The table to set.
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return Returns the hinweis.
     */
    public String getHinweis() {
        return hinweis;
    }

    /**
     * @param hinweis The hinweis to set.
     */
    public void setHinweis(String hinweis) {
        this.hinweis = hinweis;
    }

}


