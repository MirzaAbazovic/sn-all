/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2004 09:09:29
 */
package de.augustakom.hurrican.model.cc.view;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche nach IntAccounts.
 *
 *
 */
public class AuftragIntAccountQuery extends AbstractCCModel implements DebugModel, KundenModel {

    private Long kundeNo = null;
    private Long produktGruppeId = null;
    private Integer intAccountTyp = null;

    /**
     * @return Returns the intAccountTyp.
     */
    public Integer getIntAccountTyp() {
        return intAccountTyp;
    }

    /**
     * @param intAccountTyp The intAccountTyp to set.
     */
    public void setIntAccountTyp(Integer intAccountTyp) {
        this.intAccountTyp = intAccountTyp;
    }

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the produktGruppeId.
     */
    public Long getProduktGruppeId() {
        return produktGruppeId;
    }

    /**
     * @param produktGruppeId The produktGruppeId to set.
     */
    public void setProduktGruppeId(Long produktGruppeId) {
        this.produktGruppeId = produktGruppeId;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("QUERY: " + AuftragIntAccountQuery.class.getName());
            logger.debug("  KundeNoOrig: " + getKundeNo());
            logger.debug("  Pr.Gruppe  : " + getProduktGruppeId());
            logger.debug("  Account-Typ: " + getIntAccountTyp());
        }
    }
}


