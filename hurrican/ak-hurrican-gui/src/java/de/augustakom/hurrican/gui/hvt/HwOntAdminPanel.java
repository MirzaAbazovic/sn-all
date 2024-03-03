/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2014 15:05
 */
package de.augustakom.hurrican.gui.hvt;

import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.cc.HwOntAdminService;

/**
 *
 */
public class HwOntAdminPanel extends HwDeviceAdminPanel<HWOnt, HwOntAdminService> {

    private static final long serialVersionUID = -362978028544918552L;

    public HwOntAdminPanel(HWOnt rack) {
        super(rack);
    }

    @Override
    protected String getRackDisplayName() {
        return "ONT";
    }

    @Override
    protected String getRackTypeLabelTxt() {
        return "ONT Typ:";
    }

    @Override
    protected String getHwRackTypeFromRack(HWOnt rack) {
        return rack.getOntType();
    }

    @Override
    protected HWOnt setHwRackTypeForRack(HWOnt rack, String txt) {
        rack.setOntType(txt);
        return rack;
    }

    @Override
    protected String getInitButtonIdentifier() {
        return "ont.init";
    }

    @Override
    protected String getModifyButtonIdentifier() {
        return "ont.modify";
    }

    @Override
    protected String getDeleteButtonIdentifier() {
        return "ont.delete";
    }

    @Override
    protected Class<HwOntAdminService> getServiceClass() {
        return HwOntAdminService.class;
    }
}
