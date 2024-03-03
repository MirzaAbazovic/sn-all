/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.augustakom.hurrican.model.cc.view;

/**
 *
 */
public class FTTBDpuPortImportView extends OltChildPortImportView {

    private String parent = null;
    private String leiste = null;
    private String stift = null;
    private String resourceSpecId;

    public String getLeiste() {
        return leiste;
    }

    public void setLeiste(String leiste) {
        this.leiste = leiste;
    }

    public String getStift() {
        return stift;
    }

    public void setStift(String stift) {
        this.stift = stift;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getResourceSpecId() {
        return resourceSpecId;
    }

    public void setResourceSpecId(String resourceSpecId) {
        this.resourceSpecId = resourceSpecId;
    }
}
