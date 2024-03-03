/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2007 11:54:07
 */
package de.augustakom.hurrican.model.cc.tal;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell fuer das Mapping von CB-Usecases zu den CarrierKennungen.
 *
 *
 */
public class CBUsecase2CarrierKennung extends AbstractCCIDModel {

    private Long usecaseId = null;
    private Long carrierKennungId = null;

    /**
     * @return Returns the usecaseId.
     */
    public Long getUsecaseId() {
        return usecaseId;
    }

    /**
     * @param usecaseId The usecaseId to set.
     */
    public void setUsecaseId(Long usecaseId) {
        this.usecaseId = usecaseId;
    }

    /**
     * @return Returns the carrierKennungId.
     */
    public Long getCarrierKennungId() {
        return carrierKennungId;
    }

    /**
     * @param carrierKennungId The carrierKennungId to set.
     */
    public void setCarrierKennungId(Long carrierKennungId) {
        this.carrierKennungId = carrierKennungId;
    }

}


