/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 14:26:56
 */
package de.augustakom.hurrican.model.cc.kubena;


/**
 * Modell zur Abbildung der Produkt-IDs, die einer Kubena zugeordnet sind.
 *
 *
 */
public class KubenaProdukt extends AbstractKubenaRefModel {

    private Long prodId = null;

    /**
     * @return Returns the prodId.
     */
    public Long getProdId() {
        return prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

}


