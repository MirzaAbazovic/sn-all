/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2009 14:00:17
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.text.*;
import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.StringUtils;


/**
 * Modell-Klasse, um den XML-String von ServiceResponse.SOData abzubilden.
 *
 *
 */
@XStreamAlias("SERVICE_ORDER_RESPONSE")
public class CPSServiceResponseSOData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("EXEC_DATE")
    private String execDate = null;
    @XStreamAlias("COMMENT")
    private String comment = null;
    @XStreamAlias("ERROR_MSG")
    private String errorMessage = null;

    /**
     * Gibt den Zeitpunkt an, zu dem die CPS-Tx erledigt wurde.
     *
     * @param execDate
     * @throws ParseException
     *
     */
    public void setExecDate(String execDate) throws ParseException {
        this.execDate = execDate;
    }

    /**
     * @return the execDateString
     */
    public String getExecDate() {
        return execDate;
    }

    /**
     * Gibt das 'execDate' als Date-Objekt zurueck.
     *
     * @return
     * @throws ParseException
     *
     */
    public Date getExecDateAsDate() throws ParseException {
        if (StringUtils.isNotBlank(getExecDate())) {
            SimpleDateFormat df = new SimpleDateFormat(DEFAULT_CPS_DATE_TIME_FORMAT_LONG);
            return df.parse(getExecDate());
        }
        return null;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}


