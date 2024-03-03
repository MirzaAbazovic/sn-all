/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2011 09:05:43
 */

package de.augustakom.hurrican.model.cc.query;

import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.cc.Reference;


/**
 * Query fuer die Suche nach GeoID Klärungen ueber div. Parameter.
 */
public class GeoIdClarificationQuery extends AbstractHurricanQuery {

    private Long geoId;
    private List<Reference> statusList;
    private List<Reference> typeList;
    private Date from;
    private Date to;

    @Override
    public boolean isEmpty() {
        if (geoId != null) {
            return false;
        }
        if (CollectionTools.isNotEmpty(statusList)) {
            return false;
        }
        if (CollectionTools.isNotEmpty(typeList)) {
            return false;
        }
        if (from != null) {
            return false;
        }
        if (to != null) {
            return false;
        }
        return true;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setStatusList(List<Reference> statusList) {
        this.statusList = statusList;
    }

    public List<Reference> getStatusList() {
        return statusList;
    }

    public void setTypeList(List<Reference> typeList) {
        this.typeList = typeList;
    }

    public List<Reference> getTypeList() {
        return typeList;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getFrom() {
        return from;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getTo() {
        return to;
    }

}
