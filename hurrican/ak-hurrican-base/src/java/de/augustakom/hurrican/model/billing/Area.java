/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.2006 13:24:19
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell repraesentiert eine Area (=Region). Wird bei M-net verwendet, um die einzelnen Niederlassungen zu definieren.
 * <br> Ein Kunde wird immer einer best. Area und dadurch Niederlassung zugeordnet.
 *
 *
 */
public class Area extends AbstractBillingModel {

    public static final Long NUERNBERG_AREA = Long.valueOf(3);

    /**
     * Array mit den Area_NOs, die von Hurrican NICHT unterstuetzt werden.
     */
    public static final Long[] AREA_NOS_NOT_SUPPORTED = new Long[] { NUERNBERG_AREA };

    private Long areaNo = null;
    private String description = null;

    /**
     * @return Returns the areaNo.
     */
    public Long getAreaNo() {
        return this.areaNo;
    }

    /**
     * @param areaNo The areaNo to set.
     */
    public void setAreaNo(Long areaNo) {
        this.areaNo = areaNo;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}


