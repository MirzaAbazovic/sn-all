/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2004 17:01:35
 */
package de.augustakom.hurrican.model.base;

import java.io.*;
import javax.persistence.*;

import de.augustakom.common.model.AbstractObservable;


/**
 * Abstrakte Klasse fuer alle Modell-Klassen innerhalb des Hurrican-Projekts.
 *
 *
 */
@MappedSuperclass
public abstract class AbstractHurricanModel extends AbstractObservable implements Serializable {

    private Long version = null;

    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}


