/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.05.2009 10:43:19
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CPSService;

/**
 * Admin-Panel fuer die Verwaltung von MDUs.
 *
 *
 */
public class HWMduAdminPanel extends HwOltChildAdminPanel<HWMdu> {
    private static final long serialVersionUID = 6347906347510517535L;
    private static final Logger LOGGER = Logger.getLogger(HWMduAdminPanel.class);

    private AKJCheckBox chbCaTv;
    private AKJLabel lblCaTv;

    /**
     * Konstruktor mit Angabe des uebergeordneten Racks.
     */
    public HWMduAdminPanel(HWMdu rack) {
        super(rack);
    }

    @Override
    protected String getRackDisplayName() {
        return "MDU";
    }

    @Override
    protected String getRackTypeLabelTxt() {
        return "MDU Typ";
    }

    @Override
    protected String getHwRackTypeFromRack(HWMdu rack) {
        return rack.getMduType();
    }

    @Override
    protected HWMdu setHwRackTypeForRack(HWMdu rack, String txt) {
        rack.setMduType(txt);
        return rack;
    }

    @Override
    protected AKJButton getButtonCpsInit() {
        final AKJButton btnMduInit = getSwingFactory().createButton("mdu.init");
        manageGUI(btnMduInit);
        btnMduInit.addActionListener(new ActionListener() {

            /** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    cpsService = getCCService(CPSService.class);
                    Long sessionId = HurricanSystemRegistry.instance().getSessionId();

                    CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction4MDUInit(rack.getId(), false,
                            sessionId, false);
                    if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
                        CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
                        cpsService.sendCPSTx2CPS(cpsTx, sessionId);

                        MessageHelper.showInfoDialog(getMainFrame(),
                                "CPS-Transaction erstellt und an den CPS gesendet.\nCPS-Transaction ID: {0}",
                                cpsTx.getId());
                    }
                    else {
                        String warnings = (cpsTxResult.getWarnings() != null)
                                ? cpsTxResult.getWarnings().getWarningsAsText() : null;
                        throw new HurricanServiceCommandException(warnings);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
                }
            }
        });
        return btnMduInit;
    }

    @Override
    protected AKJButton getButtonCpsModify() {
        return null;
    }

    @Override
    protected AKJButton getButtonCpsDelete() {
        return null;
    }

    @Override
    protected List<CustomFormElement> getCustomFormElements() {
        if (chbCaTv == null) {
            chbCaTv = super.getSwingFactory().createCheckBox("catv");
            lblCaTv = super.getSwingFactory().createLabel("catv");
        }
        final Function<HWMdu, HWMdu> setValueToModel = new Function<HWMdu, HWMdu>() {
            @Override
            public HWMdu apply(HWMdu input) {
                input.setCatvOnline(chbCaTv.isSelectedBoolean());
                return input;
            }
        };
        final Function<HWMdu, Component> setValueToComponent = new Function<HWMdu, Component>() {
            @Override
            public Component apply(HWMdu input) {
                chbCaTv.setSelected(input.getCatvOnline());
                return chbCaTv;
            }
        };
        return ImmutableList.of(new CustomFormElement(chbCaTv, lblCaTv, setValueToModel, setValueToComponent));
    }
}
