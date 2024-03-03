/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2009 14:41:54
 */
package de.augustakom.hurrican.tools.predicate;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;


/**
 * Predicate, um UEVTCudaView-Objekte zu filtern.
 *
 *
 */
public class RSMEQCountPredicate implements Predicate {

    private String uevt = null;
    private Long hvtIdStandort = null;
    private String rangSchnittstelle = null;

    /**
     * Uebergibt dem Predicate die Werte, nach denen gefiltert werden soll.
     *
     * @param uevt
     * @param hvtId
     * @param cudaPhysik
     * @param carrier
     */
    public void setPredicateValues(String uevt, Long hvtId, String rangSchnittstelle) {
        this.uevt = uevt;
        this.hvtIdStandort = hvtId;
        this.rangSchnittstelle = rangSchnittstelle;
    }

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object obj) {
        if (obj instanceof UevtCuDAView) {
            UevtCuDAView view = (UevtCuDAView) obj;
            if (!StringUtils.equals(StringUtils.trimToNull(view.getUevt()), uevt)) {
                return false;
            }
            if (!NumberTools.equal(view.getHvtIdStandort(), hvtIdStandort)) {
                return false;
            }
            if (!StringUtils.equals(StringUtils.trimToNull(view.getCudaPhysik()), rangSchnittstelle)) {
                return false;
            }

            return true;
        }
        return false;
    }
}

