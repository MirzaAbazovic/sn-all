/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.2014
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.service.cc.HwOltChildAdminService;

/**
 * Abstraktes Admin Panel um Devices mit CreateDevice, ModifyDevice und DeleteDevice Schaltflaechen auszustatten.
 */
public abstract class HwDeviceAdminPanel<T extends HWOltChild, S extends HwOltChildAdminService<T>> extends HwOltChildAdminPanel<T> {

    private static final long serialVersionUID = -3925123871300603799L;
    protected final Logger LOGGER = Logger.getLogger(this.getClass()); // NOSONAR squid:S1312
    protected S adminService;

    public HwDeviceAdminPanel(T rack) {
        super(rack);
        try {
            adminService = getCCService(getServiceClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected int addIpFields(AKJPanel left, int count) {
        //empty to avoid the creation of IP address fields
        return count;
    }

    protected void showCpsTxId(Long cpsTxId) {
        MessageHelper.showInfoDialog(getMainFrame(),
                "CPS-Transaction erstellt und an den CPS gesendet.\nCPS-Transaction ID: {0}", cpsTxId);
    }

    protected abstract Class<S> getServiceClass();

    @Override
    protected AKJButton getButtonCpsInit() {
        final AKJButton btnInit = getSwingFactory().createButton(getInitButtonIdentifier());
        manageGUI(btnInit);
        btnInit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final Long sessionId = HurricanSystemRegistry.instance().getSessionId();
                try {
                    final Long cpsTxId = adminService.createAndSendCreateDeviceCpsTransaction(rack, sessionId);
                    showCpsTxId(cpsTxId);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
                }
            }
        });
        return btnInit;
    }

    protected abstract String getInitButtonIdentifier();

    @Override
    protected AKJButton getButtonCpsModify() {
        final AKJButton btnOntModify = getSwingFactory().createButton(getModifyButtonIdentifier());
        manageGUI(btnOntModify);
        btnOntModify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final Long sessionId = HurricanSystemRegistry.instance().getSessionId();
                try {
                    final Long cpsTxId = adminService.createAndSendModifyDeviceCpsTransaction(rack, sessionId);
                    showCpsTxId(cpsTxId);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
                }
            }
        });
        return btnOntModify;
    }

    protected abstract String getModifyButtonIdentifier();

    @Override
    protected AKJButton getButtonCpsDelete() {
        final AKJButton btnOntDelete = getSwingFactory().createButton(getDeleteButtonIdentifier());
        manageGUI(btnOntDelete);
        btnOntDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final Long sessionId = HurricanSystemRegistry.instance().getSessionId();
                try {
                    final Long cpsTxId = adminService.createAndSendDeleteDeviceCpsTransaction(rack, sessionId);
                    showCpsTxId(cpsTxId);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
                }
            }
        });
        return btnOntDelete;
    }

    protected abstract String getDeleteButtonIdentifier();
}
