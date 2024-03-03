/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2007 07:35:48
 */
package de.augustakom.hurrican.model.auskunft;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.model.IQuery;


/**
 * Query-Objekt fuer die Suche nach CDRs.
 *
 *
 */
public class CDRQuery extends CDR implements IQuery {

    private Date dateFrom = null;
    private Date dateTo = null;
    private String timeFrom = null;
    private String timeTo = null;


    /**
     * @return Returns the dateFrom.
     */
    public Date getDateFrom() {
        return this.dateFrom;
    }


    /**
     * @param dateFrom The dateFrom to set.
     */
    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }


    /**
     * @return Returns the dateTo.
     */
    public Date getDateTo() {
        return this.dateTo;
    }


    /**
     * @param dateTo The dateTo to set.
     */
    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }


    /**
     * @return Returns the timeFrom.
     */
    public String getTimeFrom() {
        return this.timeFrom;
    }


    /**
     * @param timeFrom The timeFrom to set.
     */
    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }


    /**
     * @return Returns the timeTo.
     */
    public String getTimeTo() {
        return this.timeTo;
    }


    /**
     * @param timeTo The timeTo to set.
     */
    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    /**
     * @see de.augustakom.common.model.IQuery#isEmpty()
     */
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getARN())) {
            return false;
        }
        if (StringUtils.isNotBlank(getBRN2())) {
            return false;
        }
        if (getDateFrom() != null) {
            return false;
        }
        if (getDateTo() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getTimeFrom())) {
            return false;
        }
        if (StringUtils.isNotBlank(getTimeTo())) {
            return false;
        }
        return true;
    }


}


