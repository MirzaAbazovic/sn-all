/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 07.04.2016

 */

package de.augustakom.hurrican.model.wholesale;


import java.time.*;

/**
 * Request-Objekt fuer die releasePort-Methode des Wholesale-Services.
 */
public class WholesaleReleasePortRequest {

    private String lineId;

    private Long sessionId;

    private LocalDate releaseDate;

    private String orderId;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setReleaseDate(LocalDate localDate){ this.releaseDate = localDate;    }

    public LocalDate getReleaseDate(){
        return releaseDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        WholesaleReleasePortRequest that = (WholesaleReleasePortRequest) o;

        if (lineId != null ? !lineId.equals(that.lineId) : that.lineId != null)
            return false;
        return sessionId != null ? sessionId.equals(that.sessionId) : that.sessionId == null;

    }

    @Override
    public int hashCode() {
        int result = lineId != null ? lineId.hashCode() : 0;
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WholesaleReleasePortRequest{" +
                "lineId='" + lineId + '\'' +
                ", sessionId=" + sessionId +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
