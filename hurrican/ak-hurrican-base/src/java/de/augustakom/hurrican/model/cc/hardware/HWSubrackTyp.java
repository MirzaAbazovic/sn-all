/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2009 09:45:13
 */
package de.augustakom.hurrican.model.cc.hardware;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Subrack-Typ.
 * <p/>
 * Haelt Informationen fuer Subracks.
 *
 *
 */
public class HWSubrackTyp extends AbstractCCIDModel {

    private String name;
    public static final String NAME = "name";

    private String description;
    public static final String DESCRIPTION = "description";

    private String rackTyp;
    public static final String RACK_TYP = "rackTyp";

    private Integer bgCount;
    public static final String BG_COUNT = "bgCount";

    private Integer portCount;
    public static final String PORT_COUNT = "portCount";

    private String hwTypeName;
    public static final String HW_TYPE_NAME = "hwTypeName";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRackTyp() {
        return rackTyp;
    }

    public void setRackTyp(String rackTyp) {
        this.rackTyp = rackTyp;
    }

    public Integer getBgCount() {
        return bgCount;
    }

    public void setBgCount(Integer bgCount) {
        this.bgCount = bgCount;
    }

    public Integer getPortCount() {
        return portCount;
    }

    public void setPortCount(Integer portCount) {
        this.portCount = portCount;
    }

    public String getHwTypeName() {
        return hwTypeName;
    }

    public void setHwTypeName(String hwTypeName) {
        this.hwTypeName = hwTypeName;
    }
}
