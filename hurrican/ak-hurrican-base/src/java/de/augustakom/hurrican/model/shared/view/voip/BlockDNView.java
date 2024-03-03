/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2011 09:11:52
 */
package de.augustakom.hurrican.model.shared.view.voip;

/**
 *
 */
public class BlockDNView {
    public static final BlockDNView NO_BLOCK = new BlockDNView();
    private String directDial = "", rangeFrom = "", rangeTo = "";

    BlockDNView() {
    }

    public BlockDNView(String directDial, String rangeFrom, String rangeTo) {
        this.directDial = directDial;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }

    public String getDirectDial() {
        return directDial;
    }

    public void setDirectDial(String directDial) {
        this.directDial = directDial;
    }

    public String getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(String rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public String getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(String rangeTo) {
        this.rangeTo = rangeTo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((directDial == null) ? 0 : directDial.hashCode());
        result = (prime * result) + ((rangeFrom == null) ? 0 : rangeFrom.hashCode());
        result = (prime * result) + ((rangeTo == null) ? 0 : rangeTo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BlockDNView other = (BlockDNView) obj;
        if (directDial == null) {
            if (other.directDial != null) {
                return false;
            }
        }
        else if (!directDial.equals(other.directDial)) {
            return false;
        }
        if (rangeFrom == null) {
            if (other.rangeFrom != null) {
                return false;
            }
        }
        else if (!rangeFrom.equals(other.rangeFrom)) {
            return false;
        }
        if (rangeTo == null) {
            if (other.rangeTo != null) {
                return false;
            }
        }
        else if (!rangeTo.equals(other.rangeTo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Block [directDial=" + directDial + ", rangeFrom=" + rangeFrom + ", rangeTo=" + rangeTo + "]";
    }
}
