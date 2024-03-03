/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2007 08:47:41
 */
package de.augustakom.hurrican.model.exmodules.tal;

import de.augustakom.hurrican.model.exmodules.AbstractExModuleModel;
import de.augustakom.hurrican.model.shared.iface.LongIdModel;


/**
 * Abstraktes Modell fuer alle TAL-Modelle. <br> Als "TAL-Modelle" werden die Abbildungen bezeichnet, die fuer die
 * elektronische TAL-Bestellung zu DTAG verwendet werden.
 *
 *
 */
public abstract class AbstractTALModel extends AbstractExModuleModel implements LongIdModel {

    private Long id = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.LongIdModel#getId()
     */
    public Long getId() {
        return id;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.LongIdModel#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

}


