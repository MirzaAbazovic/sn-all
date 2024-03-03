/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2011 14:00:47
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.NumberTools;

/**
 * IP-Pooltypen/Produkt-Gruppen
 */
public enum IPPoolType {

    unused(0L),
    XDSL(1L),
    DirectAccess(2L),
    C2S(3L);

    private IPPoolType(Long id) {
        this.id = id;
    }

    private Long id;

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    public static IPPoolType getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Keine Id fuer den IPPool angegeben!");
        }
        IPPoolType[] typeValues = IPPoolType.values();
        if ((typeValues != null) && (typeValues.length > 0)) {
            for (IPPoolType type : typeValues) {
                if (NumberTools.equal(id, type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }

} // end
