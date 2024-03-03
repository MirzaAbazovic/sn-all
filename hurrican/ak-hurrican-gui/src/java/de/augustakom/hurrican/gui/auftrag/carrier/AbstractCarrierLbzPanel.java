/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2005 13:05:36
 */
package de.augustakom.hurrican.gui.auftrag.carrier;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;


/**
 * Abstraktes Klasse fuer Panels, ueber die eine LBZ automatisiert eingegeben werden kann.
 *
 *
 */
public abstract class AbstractCarrierLbzPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private Long endstelleId = null;
    private Long carrierId = null;

    /**
     * @param resource
     */
    public AbstractCarrierLbzPanel(String resource) {
        super(resource);
    }

    /**
     * Ueber diese Methode kann die eingetragene LBZ abgefragt werden.
     *
     * @return
     */
    protected abstract String getLbz();

    /**
     * Uebergibt dem Panel die ID der Endstelle, fuer die die Carrierbestellung bestimmt ist.
     *
     * @param endstelleId
     */
    public void setEndstelleId(Long endstelleId) {
        this.endstelleId = endstelleId;
    }

    /**
     * Gibt die Endstellen-ID zurueck, die dem Panel uebergeben wurde.
     *
     * @return
     */
    public Long getEndstelleId() {
        return endstelleId;
    }

    /**
     * @return Returns the carrierId.
     */
    public Long getCarrierId() {
        return carrierId;
    }

    /**
     * @param carrierId The carrierId to set.
     */
    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

}


