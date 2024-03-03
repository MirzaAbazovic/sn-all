/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2014 15:05
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.service.cc.HwDpoAdminService;

/**
 *
 */
public class HwDpoAdminPanel extends HwDeviceAdminPanel<HWDpo, HwDpoAdminService> {

    private static final long serialVersionUID = -9091571549775047677L;

    private AKJTextField tfChassisIdentifier;
    private AKJTextField tfChassisSlot;

    public HwDpoAdminPanel(HWDpo rack) {
        super(rack);
    }

    @Override
    protected Class<HwDpoAdminService> getServiceClass() {
        return HwDpoAdminService.class;
    }


    @Override
    protected String getRackDisplayName() {
        return "DPO";
    }

    @Override
    protected String getRackTypeLabelTxt() {
        return "DPO Typ:";
    }

    @Override
    protected String getHwRackTypeFromRack(HWDpo rack) {
        return rack.getDpoType();
    }

    @Override
    protected HWDpo setHwRackTypeForRack(HWDpo rack, String txt) {
        rack.setDpoType(txt);
        return rack;
    }

    @Override
    protected String getInitButtonIdentifier() {
        return "dpo.init";
    }

    @Override
    protected String getModifyButtonIdentifier() {
        return "dpo.modify";
    }

    @Override
    protected String getDeleteButtonIdentifier() {
        return "dpo.delete";
    }

    @Override
    protected List<CustomFormElement> getCustomFormElements() {
        AKJLabel lblChassisIdentifier = getSwingFactory().createLabel("dpo.chassis.identifier");
        if (tfChassisIdentifier == null) {
            tfChassisIdentifier = getSwingFactory().createTextField("dpo.chassis.identifier");
        }
        final Function<HWDpo, HWDpo> setValueToModelChassisIdentifier = input -> {
            input.setChassisIdentifier(tfChassisIdentifier.getText());
            return input;
        };
        final Function<HWDpo, Component> setValueToComponentChassisIdentifier = input -> {
            tfChassisIdentifier.setText(input.getChassisIdentifier());
            return tfChassisIdentifier;
        };

        AKJLabel lblChassisSlot = getSwingFactory().createLabel("dpo.chassis.slot");
        if (tfChassisSlot == null) {
            tfChassisSlot = getSwingFactory().createTextField("dpo.chassis.slot");
        }
        final Function<HWDpo, HWDpo> setValueToModelChassisSlot = input -> {
            input.setChassisSlot(tfChassisSlot.getText());
            return input;
        };
        final Function<HWDpo, Component> setValueToComponentChassisSlot = input -> {
            tfChassisSlot.setText(input.getChassisSlot());
            return tfChassisIdentifier;
        };

        return ImmutableList.of(
                new CustomFormElement(tfChassisIdentifier, lblChassisIdentifier, setValueToModelChassisIdentifier,
                        setValueToComponentChassisIdentifier),
                new CustomFormElement(tfChassisSlot, lblChassisSlot, setValueToModelChassisSlot,
                        setValueToComponentChassisSlot)
        );
    }

}
