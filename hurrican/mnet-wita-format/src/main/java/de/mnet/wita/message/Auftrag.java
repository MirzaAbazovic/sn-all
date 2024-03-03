/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 16:20:58
 */
package de.mnet.wita.message;

import javax.persistence.*;

/**
 * Basis-Objekt fuer eine WITA TAL-Bestellung.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS",
        justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("Auftrag")
public class Auftrag extends MnetWitaRequest {

    // <?xml version="1.0" encoding="UTF-8"?>
    // <order:auftrag xmlns:order="http://wholesale.telekom.de/oss/v4/order"
    // xmlns:complex="http://wholesale.telekom.de/oss/v4/complex"
    // xmlns:tal="http://wholesale.telekom.de/oss/v4/tal"
    // xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    // xsi:schemaLocation="http://wholesale.telekom.de/oss/v4/order ../../../main/xsd/oss-order.xsd http://wholesale.telekom.de/oss/v4/tal ../../../main/xsd/oss-product-TAL.xsd">
    // <externeAuftragsnummer>TAL.001.NEU</externeAuftragsnummer>
    // ... Kunde ...
    // ... Geschaeftsfall ...
    // </order:auftrag>

    private static final long serialVersionUID = -2699251972629287115L;

    @Override
    @Transient
    public boolean isAuftrag() {
        return true;
    }

    @Override
    public String toString() {
        return "Auftrag [toString()=" + super.toString() + "]";
    }
}
