/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2014 15:05
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HwDpuAdminService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 *
 */
public class HwDpuAdminPanel extends HwDeviceAdminPanel<HWDpu, HwDpuAdminService> {

    private static final long serialVersionUID = -1407217440997449250L;

    private AKJCheckBox chbReversePower;
    private AKJLabel lblReversePower;

    public HwDpuAdminPanel(HWDpu rack) {
        super(rack);
    }

    @Override
    protected Class<HwDpuAdminService> getServiceClass() {
        return HwDpuAdminService.class;
    }


    @Override
    protected String getRackDisplayName() {
        return "DPU";
    }

    @Override
    protected String getRackTypeLabelTxt() {
        return "DPU Typ:";
    }

    @Override
    protected String getHwRackTypeFromRack(HWDpu rack) {
        return rack.getDpuType();
    }

    @Override
    protected HWDpu setHwRackTypeForRack(HWDpu rack, String txt) {
        rack.setDpuType(txt);
        return rack;
    }

    @Override
    protected String getInitButtonIdentifier() {
        return "dpu.init";
    }

    @Override
    protected String getModifyButtonIdentifier() {
        return "dpu.modify";
    }

    @Override
    protected String getDeleteButtonIdentifier() {
        return "dpu.delete";
    }

    @Override
    protected List<CustomFormElement> getCustomFormElements() {

        if (chbReversePower == null) {
            chbReversePower = getSwingFactory().createCheckBox("dpu.stromversorgung");
            chbReversePower.setEnabled(false);
            lblReversePower = getSwingFactory().createLabel("dpu.stromversorgung");
        }

        final Function<HWDpu, HWDpu> setValueToModel = input -> {
            input.setReversePower(chbReversePower.isSelectedBoolean());
            return input;
        };

        final Function<HWDpu, Component> setValueToComponent = input -> {
            chbReversePower.setSelected(input.getReversePower());
            return chbReversePower;
        };
        return ImmutableList.of(new CustomFormElement(chbReversePower, lblReversePower, setValueToModel, setValueToComponent));
    }

    @Override
    protected int addIpFields(AKJPanel left, int count) {
        int y = count;
        AKJLabel lblIP = getSwingFactory().createLabel("ip.address");
        left.add(lblIP, GBCFactory.createGBC(0, 0, 1, y, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfIP, GBCFactory.createGBC(100, 0, 3, y++, 1, 1, GridBagConstraints.HORIZONTAL));
        return y;
    }

    @Override
    protected int addTddProfileFields(AKJPanel left, Long rackId, int count) {
        final JComboBox<String> values = generateTDDValues(rackId);
        if (values.getModel().getSize() > 0) {
            tddLabel = getSwingFactory().createLabel("tdd.profil");
            tddCombobox = values;
            left.add(tddLabel,
                    GBCFactory.createGBC(100, 0, 1, count, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(tddCombobox,
                    GBCFactory.createGBC(100, 0, 3, count, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        return count;
    }

    private JComboBox<String> generateTDDValues(Long rackId) {
        try {
            ReferenceService referenceService = getCCService(ReferenceService.class);
            List<Reference> referencesByType = referenceService.findReferencesByType(Reference.REF_TYPE_HW_RACK_DPU_TDD_PROFIL, true);
            if (referencesByType.isEmpty()) {
                MessageHelper.showErrorDialog(getMainFrame(), new IllegalStateException("Keine Referenzwerte zu HW_RACK_DPU_TDD_PROFIL gefunden."));
            }

            List<String> tddProfileValues = new ArrayList<>();
            referencesByType.forEach(referenceByString -> tddProfileValues.add(referenceByString.getStrValue()));

            hwService = getCCService(HWService.class);
            HWRack rackById = hwService.findRackById(rackId);
            if(rackId == null || rackById == null) {
                return new JComboBox<>(tddProfileValues.toArray(new String[tddProfileValues.size()]));
            }

            String currentVal = ((HWDpu) rackById).getTddProfil();
            if (!Strings.isNullOrEmpty(currentVal) && !tddProfileValues.contains(currentVal)){
                tddProfileValues.add(currentVal);
            }

            JComboBox<String> comboBox = new JComboBox<>(tddProfileValues.toArray(new String[tddProfileValues.size()]));
            if (!Strings.isNullOrEmpty(currentVal)) {
                comboBox.setSelectedItem(currentVal);
            }
            return comboBox;
        }
        catch (Exception e) {
            LOGGER.error(e);
            return new JComboBox<>();
        }
    }

}
