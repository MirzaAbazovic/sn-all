/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2009 08:46:43
 */
package de.augustakom.hurrican.model.cc.cps;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell-Klasse fuer die Zuordnung einer ServiceChain fuer die Kombination aus Produkt u. ServiceOrder-Type. <br> <br>
 * In diesem Modell koennen spaeter evtl. noch weitere Konfig-Daten fuer die Service-Chain aufgenommen werden.
 *
 *
 */
public class CPSDataChainConfig extends AbstractCCIDModel {

    private Long prodId = null;
    private Long serviceOrderTypeRefId = null;
    private Long serviceChainId = null;

    /**
     * @return the prodId
     */
    public Long getProdId() {
        return prodId;
    }

    /**
     * @param prodId the prodId to set
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * @return the serviceOrderTypeRefId
     */
    public Long getServiceOrderTypeRefId() {
        return serviceOrderTypeRefId;
    }

    /**
     * @param serviceOrderTypeRefId the serviceOrderTypeRefId to set
     */
    public void setServiceOrderTypeRefId(Long serviceOrderTypeRefId) {
        this.serviceOrderTypeRefId = serviceOrderTypeRefId;
    }

    /**
     * @return the serviceChainId
     */
    public Long getServiceChainId() {
        return serviceChainId;
    }

    /**
     * @param serviceChainId the serviceChainId to set
     */
    public void setServiceChainId(Long serviceChainId) {
        this.serviceChainId = serviceChainId;
    }

}


