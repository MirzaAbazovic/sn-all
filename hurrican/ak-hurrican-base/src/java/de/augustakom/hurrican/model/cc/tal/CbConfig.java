/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2007 17:09:39
 */
package de.augustakom.hurrican.model.cc.tal;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell bildet die Konfiguration der el. Talbestllung ab
 *
 *
 */
public class CbConfig extends AbstractCCIDModel {

    private Long usecaseId = null;
    private String bezeichnung = null;
    private Integer position = null;
    private Long commandId = null;
    private Integer min = null;
    private Integer max = null;

    /**
     * @return Returns the bezeichnung.
     */
    public String getBezeichnung() {
        return bezeichnung;
    }

    /**
     * @param bezeichnung The bezeichnung to set.
     */
    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    /**
     * @return Returns the commandId.
     */
    public Long getCommandId() {
        return commandId;
    }

    /**
     * @param commandId The commandId to set.
     */
    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    /**
     * @return Returns the max.
     */
    public Integer getMax() {
        return max;
    }

    /**
     * @param max The max to set.
     */
    public void setMax(Integer max) {
        this.max = max;
    }

    /**
     * @return Returns the min.
     */
    public Integer getMin() {
        return min;
    }

    /**
     * @param min The min to set.
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * @return Returns the position.
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position The position to set.
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * @return Returns the usecaseId.
     */
    public Long getUsecaseId() {
        return usecaseId;
    }

    /**
     * @param usecaseId The usecaseId to set.
     */
    public void setUsecaseId(Long usecaseId) {
        this.usecaseId = usecaseId;
    }
}


