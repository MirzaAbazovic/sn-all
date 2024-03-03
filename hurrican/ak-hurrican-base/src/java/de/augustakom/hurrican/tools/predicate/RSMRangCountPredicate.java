/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2009 14:41:54
 */
package de.augustakom.hurrican.tools.predicate;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.RsmRangCount;


/**
 * Predicate, um RSMRangCount-Objekte zu filtern.
 *
 *
 */
public class RSMRangCountPredicate implements Predicate {

    private Long hvtIdStandort = null;
    private String kvzNummer = null;
    private Long physiktyp = null;
    private Long physiktypAdd = null;

    /**
     * Uebergibt dem Predicate die Werte, nach denen gefiltert werden soll.
     *
     * @param hvtId
     * @param kvzNummer
     * @param physiktyp
     * @param physiktypAdd
     */
    public void setPredicateValues(Long hvtId, String kvzNummer, Long physiktyp, Long physiktypAdd) {
        this.hvtIdStandort = hvtId;
        this.kvzNummer = kvzNummer;
        this.physiktyp = physiktyp;
        this.physiktypAdd = physiktypAdd;
    }

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    @Override
    public boolean evaluate(Object obj) {
        if (obj instanceof RsmRangCount) {
            RsmRangCount view = (RsmRangCount) obj;
            if (!NumberTools.equal(view.getHvtStandortId(), hvtIdStandort)) {
                return false;
            }
            if (!StringUtils.equals(view.getKvzNummer(), kvzNummer)) {
                return false;
            }
            if (!NumberTools.equal(view.getPhysiktyp(), physiktyp)) {
                return false;
            }
            if (!NumberTools.equal(view.getPhysiktypAdd(), physiktypAdd)) {
                return false;
            }

            return true;
        }
        return false;
    }
}

