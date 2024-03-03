/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2009 09:45:13
 */
package de.augustakom.hurrican.model.cc.hardware;

import java.io.*;
import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Subrack-Information.
 * <p/>
 * Im Moment eine optionale Stufe zwischen Baugruppe und Rack, benoetigt fuer die CrossConnection-Berechnung.
 *
 *
 */
public class HWSubrack extends AbstractCCIDModel {

    private HWSubrackTyp subrackTyp;
    public static final String SUBRACK_TYP = "subrackTyp";

    private Long rackId;
    public static final String RACK_ID = "rackId";

    private String modNumber;
    public static final String MOD_NUMBER = "modNumber";


    public static final HWSubrackComparator SUBRACK_COMPARATOR = new HWSubrackComparator();

    public static final class HWSubrackComparator implements Comparator<HWSubrack>, Serializable {
        @Override
        public int compare(HWSubrack o1, HWSubrack o2) {
            String[] s1 = o1.getModNumber().split("-");
            String[] s2 = o2.getModNumber().split("-");
            if ((s1.length != 2) || (s2.length != 2)) {
                return o1.getModNumber().compareTo(o2.getModNumber());
            }
            int x1 = Integer.parseInt(s1[0]);
            int x2 = Integer.parseInt(s2[0]);
            if (x1 != x2) {
                return (x1 < x2 ? -1 : 1);
            }
            x1 = Integer.parseInt(s1[1]);
            x2 = Integer.parseInt(s2[1]);
            if (x1 != x2) {
                return (x1 < x2 ? -1 : 1);
            }
            return 0;
        }
    }


    /**
     * Liefert modNumber und Subrack Typ als lesbaren String zurueck
     */
    public String getDisplay() {
        return modNumber + " (" + subrackTyp.getName() + ")";
    }


    public String getModNumber() {
        return modNumber;
    }

    public void setModNumber(String modNumber) {
        this.modNumber = modNumber;
    }

    public HWSubrackTyp getSubrackTyp() {
        return subrackTyp;
    }

    public void setSubrackTyp(HWSubrackTyp subrackTyp) {
        this.subrackTyp = subrackTyp;
    }

    public Long getRackId() {
        return rackId;
    }

    public void setRackId(Long rackId) {
        this.rackId = rackId;
    }
}
