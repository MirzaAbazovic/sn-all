/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2010 10:16:20
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.common.model.EntityBuilder;


/**
 * EntityBuilder fuer {@link HWBaugruppenChangePort2PortView} Objekte.
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangePort2PortViewBuilder extends EntityBuilder<HWBaugruppenChangePort2PortViewBuilder, HWBaugruppenChangePort2PortView> {

    private Long port2PortId;
    private Long hwBgChangeId;
    private Long equipmentIdOld;
    private String hwEqnOld;
    private Long equipmentIdNew;
    private String hwEqnNew;
    private String equipmentStatusOld;
    private Long auftragId;
    private String auftragStatus;
    private Long orderNoOrig;
    private String produkt;
    private Long vpnNr;
    private Long lastSuccessfulCpsTx;
    private Boolean eqOldManualConfiguration;
    private Boolean eqNewManualConfiguration;

    @Override
    public boolean getPersist() {
        return false;
    }

    public HWBaugruppenChangePort2PortViewBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public HWBaugruppenChangePort2PortViewBuilder withLastSuccessfulCpsTx(Long lastSuccessfulCpsTx) {
        this.lastSuccessfulCpsTx = lastSuccessfulCpsTx;
        return this;
    }

}


