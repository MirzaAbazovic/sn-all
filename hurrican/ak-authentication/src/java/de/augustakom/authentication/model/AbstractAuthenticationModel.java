/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2004 08:48:55
 */
package de.augustakom.authentication.model;

import java.io.*;
import javax.persistence.*;

import de.augustakom.common.model.AbstractObservable;


/**
 * Basisklasse fuer alle Modell-Klassen im AK-Authentication Projekt.
 *
 *
 */
@MappedSuperclass
public class AbstractAuthenticationModel extends AbstractObservable implements Serializable {

    private Long version = null;

    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}


