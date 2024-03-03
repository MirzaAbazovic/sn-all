/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.mnet.wbci.model;

import javax.persistence.*;

/**
 *
 */
@MappedSuperclass
public abstract class StornoAbgAnfrage<GF extends WbciGeschaeftsfall> extends StornoMitEndkundeStandortAnfrage<GF> {

    private static final long serialVersionUID = -2486032244930890L;

    /**
     * Begruendung
     */
    private String stornoGrund;

    @Column(name = WbciRequest.COL_NAME_STORNO_GRUND)
    public String getStornoGrund() {
        return stornoGrund;
    }

    public void setStornoGrund(String stornoGrund) {
        this.stornoGrund = stornoGrund;
    }
}
