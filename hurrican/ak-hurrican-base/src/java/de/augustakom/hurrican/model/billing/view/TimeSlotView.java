/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2009 11:00:36
 */

package de.augustakom.hurrican.model.billing.view;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.AbstractBillingModel;

/**
 * Stellt eine View fuer Realisierungszeitfenster dar.
 *
 *
 */
public class TimeSlotView extends AbstractBillingModel {

    private Date date = null;
    private Date daytimeFrom = null;
    private Date daytimeTo = null;
    private Long weekday = null;
    private Long areaNo = null;

    public String getTimeSlotAsStringOnlyTime() {
        String from = DateTools.formatDate(daytimeFrom, DateTools.PATTERN_TIME);
        String to = DateTools.formatDate(daytimeTo, DateTools.PATTERN_TIME);
        return StringTools.join(new String[] { from, to }, " - ", true);
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the daytimeFrom
     */
    public Date getDaytimeFrom() {
        return daytimeFrom;
    }

    /**
     * @param daytimeFrom the daytimeFrom to set
     */
    public void setDaytimeFrom(Date daytimeFrom) {
        this.daytimeFrom = daytimeFrom;
    }

    /**
     * @return the daytimeTo
     */
    public Date getDaytimeTo() {
        return daytimeTo;
    }

    /**
     * @param daytimeTo the daytimeTo to set
     */
    public void setDaytimeTo(Date daytimeTo) {
        this.daytimeTo = daytimeTo;
    }

    /**
     * @return the weekday
     */
    public Long getWeekday() {
        return weekday;
    }

    /**
     * @param weekday the weekday to set
     */
    public void setWeekday(Long weekday) {
        this.weekday = weekday;
    }

    /**
     * @return the areaNo
     */
    public Long getAreaNo() {
        return areaNo;
    }

    /**
     * @param areaNo the areaNo to set
     */
    public void setAreaNo(Long areaNo) {
        this.areaNo = areaNo;
    }

}


