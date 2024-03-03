/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015 13:17
 */
package de.augustakom.hurrican.model.cc.hvt.umzug;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.model.AbstractObservable;
import de.mnet.common.tools.DateConverterUtils;

/**
 * View-Klasse zur einfacheren GUI-Darstellung von HVT-Umzuegen.
 */
public class HvtUmzugMasterView extends AbstractObservable {
    private Long hvtUmzugId;
    private Date schalttag;
    private Long hvtStandortId;
    private String hvtStandortName;
    private Long hvtStandortDestinationId;
    private String hvtStandortDestinationName;
    private String kvzNr;
    private String bearbeiter;
    private String status;

    public HvtUmzugMasterView(@Nonnull final HvtUmzug hvtUmzug) {
        this.hvtUmzugId = hvtUmzug.getId();
        this.schalttag = hvtUmzug.getSchalttag() == null
                ? null : DateConverterUtils.asDate(hvtUmzug.getSchalttag());
        this.hvtStandortId = hvtUmzug.getHvtStandort();
        this.hvtStandortDestinationId = hvtUmzug.getHvtStandortDestination();
        this.kvzNr = hvtUmzug.getKvzNr();
        this.bearbeiter = hvtUmzug.getBearbeiter();
        this.status = hvtUmzug.getStatus() == null ? null : hvtUmzug.getStatus().getDisplayName();
    }

    public HvtUmzugMasterView(@Nonnull final HvtUmzug hvtUmzug, final String hvtStandortName, final String hvtStandortDestinationName) {
        this(hvtUmzug);
        this.hvtStandortName = hvtStandortName;
        this.hvtStandortDestinationName = hvtStandortDestinationName;
    }

    public Long getHvtUmzugId() {
        return hvtUmzugId;
    }

    public void setHvtUmzugId(Long hvtUmzugId) {
        this.hvtUmzugId = hvtUmzugId;
    }

    public Date getSchalttag() {
        return schalttag;
    }

    public void setSchalttag(Date schalttag) {
        this.schalttag = schalttag;
    }

    public Long getHvtStandortId() {
        return hvtStandortId;
    }

    public void setHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
    }

    public String getHvtStandortName() {
        return hvtStandortName;
    }

    public void setHvtStandortName(String hvtStandortName) {
        this.hvtStandortName = hvtStandortName;
    }

    public Long getHvtStandortDestinationId() {
        return hvtStandortDestinationId;
    }

    public void setHvtStandortDestinationId(Long hvtStandortDestinationId) {
        this.hvtStandortDestinationId = hvtStandortDestinationId;
    }

    public String getHvtStandortDestinationName() {
        return hvtStandortDestinationName;
    }

    public void setHvtStandortDestinationName(String hvtStandortDestinationName) {
        this.hvtStandortDestinationName = hvtStandortDestinationName;
    }

    public String getKvzNr() {
        return kvzNr;
    }

    public void setKvzNr(String kvzNr) {
        this.kvzNr = kvzNr;
    }

    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HvtUmzugMasterView that = (HvtUmzugMasterView) o;
        return Objects.equals(hvtUmzugId, that.hvtUmzugId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hvtUmzugId);
    }
}
