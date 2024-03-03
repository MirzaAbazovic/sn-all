/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Represents the matching between the GUI table which lists all {@link Rufnummernportierung} objects of a RUEM-VA
 * message.
 */
public class RufnummernportierungVO implements Serializable {

    private static final long serialVersionUID = -3196626610855426407L;

    public enum StatusInfo {
        /**
         * Rufnummer ist sowohl im WBCI-Request als auch in der RUEM-VA enthalten
         */
        BESTAETIGT("bestätigt"),
        /**
         * Rufnummer ist im WBCI-Request enthalten; noch keine RUEM-VA empfangen
         */
        ANGEFRAGT("angefragt"),
        /**
         * Rufnummer ist in der RUEM-VA, aber nicht im WBCI-Request enthalten
         */
        NEU_AUS_RUEM_VA("neu aus RUEM-VA"),
        /**
         * Rufnummer ist im WBCI-Request, aber nicht in der RUEM-VA enthalten
         */
        NICHT_IN_RUEM_VA("nicht in RUEM-VA");

        public String infoText;

        StatusInfo(String infoText) {
            this.infoText = infoText;
        }
    }

    public enum PkiAufMatch {
        /**
         * Zeigt an, dass die PkiAuf Kennung der AKM-TR mit dem FutureCarrier der Rufnummer uebereinstimmt.
         */
        MATCH,
        /**
         * Zeigt an, dass die PkiAuf Kennung der AKM-TR mit dem FutureCarrier der Rufnummer NICHT uebereinstimmt.
         */
        NO_MATCH
    }

    public static final Comparator<RufnummernportierungVO> doesExist = new Comparator<RufnummernportierungVO>() {
        @Override
        public int compare(RufnummernportierungVO o1, RufnummernportierungVO o2) {
            return ComparisonChain.start()
                    .compare(o1.getOnkz(), o2.getOnkz(), Ordering.natural().nullsFirst())
                    .compare(o1.getDnBase(), o2.getDnBase(), Ordering.natural().nullsFirst())
                    .compare(o1.getDirectDial(), o2.getDirectDial(), Ordering.natural().nullsFirst())
                    .compare(o1.getBlockFrom(), o2.getBlockFrom(), Ordering.natural().nullsFirst())
                    .compare(o1.getBlockTo(), o2.getBlockTo(), Ordering.natural().nullsFirst())
                    .result();
        }
    };

    private String onkz;
    private String dnBase;
    private String directDial;
    private String blockFrom;
    private String blockTo;
    private String pkiAbg;
    private String pkiAuf;
    private StatusInfo statusInfo;
    private PkiAufMatch pkiAufMatch;

    /**
     * Vergleicht den definierten {@code pkiAuf} mit der PkiAuf-Kennung aus der vorhandenen AKM-TR und setzt das
     * entsprechende {@link PkiAufMatch}.
     *
     * @param akmTr AKM-TR mit der PKIauf Kennung
     */
    public void checkPkiAufMatch(UebernahmeRessourceMeldung akmTr) {
        if (akmTr != null && akmTr.getPortierungskennungPKIauf() != null) {
            if (StringUtils.equalsIgnoreCase(pkiAuf, akmTr.getPortierungskennungPKIauf())) {
                pkiAufMatch = PkiAufMatch.MATCH;
            }
            else {
                pkiAufMatch = PkiAufMatch.NO_MATCH;
            }
        }
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public String getDnBase() {
        return dnBase;
    }

    public void setDnBase(String dnBase) {
        this.dnBase = dnBase;
    }

    public String getDirectDial() {
        return directDial;
    }

    public void setDirectDial(String directDial) {
        this.directDial = directDial;
    }

    public String getBlockFrom() {
        return blockFrom;
    }

    public void setBlockFrom(String blockFrom) {
        this.blockFrom = blockFrom;
    }

    public String getBlockTo() {
        return blockTo;
    }

    public void setBlockTo(String blockTo) {
        this.blockTo = blockTo;
    }

    public String getPkiAbg() {
        return pkiAbg;
    }

    public void setPkiAbg(String pkiAbg) {
        this.pkiAbg = pkiAbg;
    }

    public String getPkiAuf() {
        return pkiAuf;
    }

    public void setPkiAuf(String pkiAuf) {
        this.pkiAuf = pkiAuf;
    }

    public boolean isBlock() {
        return StringUtils.isNotBlank(getBlockFrom()) && StringUtils.isNotBlank(getBlockTo());
    }

    public StatusInfo getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
    }

    public PkiAufMatch getPkiAufMatch() {
        return pkiAufMatch;
    }

}
