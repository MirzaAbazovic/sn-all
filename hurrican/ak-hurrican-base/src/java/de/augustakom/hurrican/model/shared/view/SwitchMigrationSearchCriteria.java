/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2015
 */
package de.augustakom.hurrican.model.shared.view;

import java.time.*;
import java.util.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

/**
 *
 */
public class SwitchMigrationSearchCriteria {

    private List<Integer> billingAuftragIdList = null;
    private Long prodId = null;
    private Date baRealisierungVon = null;
    private Date baRealisierungBis = null;
    private Date inbetriebnahmeVon = null;
    private Date inbetriebnahmeBis = null;
    private HWSwitch hwSwitch = null;
    private Long limit;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public Date getBaRealisierungVon() {
        return baRealisierungVon;
    }

    public void setBaRealisierungVon(Date baRealisierungVon) {
        if (baRealisierungVon != null) {
            this.baRealisierungVon = Date.from(DateTools.stripTimeFromDate( Instant.ofEpochMilli(baRealisierungVon.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() ).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public Date getInbetriebnahmeVon() {
        return inbetriebnahmeVon;
    }

    public void setInbetriebnahmeVon(Date inbetriebnahmeVon) {
        if (inbetriebnahmeVon != null) {
            this.inbetriebnahmeVon = Date.from(DateTools.stripTimeFromDate( Instant.ofEpochMilli(inbetriebnahmeVon.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() ).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    public boolean isEmpty() {
        return hwSwitch == null && billingAuftragIdList == null && prodId == null && baRealisierungVon == null && inbetriebnahmeVon == null;
    }

    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public Date getBaRealisierungBis() {
        return baRealisierungBis;
    }

    public void setBaRealisierungBis(Date baRealisierungBis) {
        if (baRealisierungBis != null) {
            this.baRealisierungBis = Date.from(DateTools.stripTimeFromDate( Instant.ofEpochMilli(baRealisierungBis.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() ).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public Date getInbetriebnahmeBis() {
        return inbetriebnahmeBis;
    }

    public void setInbetriebnahmeBis(Date inbetriebnahmeBis) {
        if (inbetriebnahmeBis != null) {
            this.inbetriebnahmeBis = Date.from(DateTools.stripTimeFromDate( Instant.ofEpochMilli(inbetriebnahmeBis.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() ).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public List<Integer> getBillingAuftragIdList() {
        return billingAuftragIdList;
    }

    public void setBillingAuftragIdList(List<Integer> billingAuftragIdList) {
        this.billingAuftragIdList = billingAuftragIdList;
    }
}
