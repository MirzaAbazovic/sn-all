/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.13 11:48
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.io.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@XStreamAlias("RANGE")
@ObjectsAreNonnullByDefault
public class CpsVoipRangeData implements Serializable {

    private static final long serialVersionUID = 7060023002724752113L;

    @XStreamAlias("LAC")
    private String lac;
    @XStreamAlias("DN")
    private String dn;
    @XStreamAlias("RANGE_START")
    private String rangeStart;
    @XStreamAlias("RANGE_END")
    private String rangeEnd;
    @XStreamAlias("MAIN_OFFICE")
    private String mainOffice;

    public CpsVoipRangeData(final String lac, final String dn, final String rangeStart, final String rangeEnd,
            final String mainOffice) {
        this.lac = lac;
        this.dn = dn;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.mainOffice = mainOffice;
    }

    /*
     * Do not use. only for framework (Xstream)
     */
    protected CpsVoipRangeData() {
    }

    public String getLac() {
        return lac;
    }

    public String getDn() {
        return dn;
    }

    public String getRangeStart() {
        return rangeStart;
    }

    public String getRangeEnd() {
        return rangeEnd;
    }

    public String getMainOffice() {
        return mainOffice;
    }
}
