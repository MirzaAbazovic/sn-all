/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 15:59:05
 */
package de.augustakom.hurrican.model.cc.query;

import java.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;

/**
 * Query für die Suche über MonitorService.findRsmRangCountBy().
 *
 *
 */
public class ResourcenMonitorQuery extends AbstractHurricanQuery {

    private Long niederlassungId;
    private Set<Long> standortTypen = new HashSet<Long>();
    private String cluster;

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void addStandortType(Long type) {
        standortTypen.add(type);
    }

    public void removeStandortType(Long type) {
        standortTypen.remove(type);
    }

    public Set<Long> getStandortTypen() {
        return ImmutableSet.copyOf(standortTypen);
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getCluster() {
        return cluster;
    }

    @Override
    public boolean isEmpty() {
        if (niederlassungId != null) {
            return false;
        }
        if (!standortTypen.isEmpty()) {
            return false;
        }
        if (!Strings.isNullOrEmpty(cluster)) {
            return false;
        }

        return true;
    }

}
