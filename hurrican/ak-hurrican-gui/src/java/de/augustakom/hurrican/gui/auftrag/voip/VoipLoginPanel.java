/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.13 08:23
 */
package de.augustakom.hurrican.gui.auftrag.voip;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 *
 */
public class VoipLoginPanel extends AbstractServicePanel implements AKObjectSelectionListener {
    private static final long serialVersionUID = -5323930557865440777L;

    private FocusListener focusListener = null;

    private AKJTextField tfSipUri;
    private AKJTextField tfSipPassword;
    private AKJTextField tfSipBenutzername;
    private AKJTextField tfSipRegistrar;

    private AuftragVoipDNView selectedAuftragVoipDnView;
    private VoipDnPlanView selectedVoipDnPlanView;
    private final SwingFactory swingFactory;

    public VoipLoginPanel(final SwingFactory swingFactory) {
        super(null, new GridBagLayout());
        this.swingFactory = swingFactory;
        this.focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setVoipLoginDataToModel();
            }
        };
        createGUI();
    }

    @Override
    public void objectSelected(final Object selection) {
        if (selection instanceof AuftragVoipDNView) {
            selectedAuftragVoipDnView = (AuftragVoipDNView) selection;

            tfSipUri.setEditable(selectedAuftragVoipDnView.isBlock());
            tfSipPassword.setEditable(selectedAuftragVoipDnView.isBlock());
            tfSipBenutzername.setEditable(selectedAuftragVoipDnView.isBlock());
            tfSipRegistrar.setEditable(false);

            selectedVoipDnPlanView = selectedAuftragVoipDnView.getLatestVoipDnPlanView();
        } else if (selection instanceof VoipDnPlanView) {
            selectedVoipDnPlanView = (VoipDnPlanView) selection;
        } else {
            selectedAuftragVoipDnView = null;
            selectedVoipDnPlanView = null;
        }
        refresh();
    }

    private void refresh(){
        if (selectedAuftragVoipDnView != null) {
            tfSipPassword.setText(selectedAuftragVoipDnView.getSipPassword() != null ? selectedAuftragVoipDnView.getSipPassword() : "");
            if (selectedAuftragVoipDnView.getSipDomain() != null)
                tfSipRegistrar.setText(selectedAuftragVoipDnView.getSipDomain().getStrValue() != null ? selectedAuftragVoipDnView.getSipDomain().getStrValue() : "");
            else
                tfSipRegistrar.setText("");
        } else {
            tfSipPassword.setText("");
            tfSipRegistrar.setText("");
        }

        if (selectedVoipDnPlanView != null) {
            tfSipUri.setText(selectedVoipDnPlanView.getSipLogin() != null ? selectedVoipDnPlanView.getSipLogin() : "");
            tfSipBenutzername.setText(selectedVoipDnPlanView.getSipHauptrufnummer() != null ? selectedVoipDnPlanView.getSipHauptrufnummer() : "");
        } else {
            tfSipUri.setText("");
            tfSipBenutzername.setText("");
        }
    }

    public void setVoipLoginDataToModel() {
        if (selectedAuftragVoipDnView != null &&
                !StringUtils.trimToEmpty(selectedAuftragVoipDnView.getSipPassword()).equals(StringUtils.trimToEmpty(tfSipPassword.getText()))) {
            selectedAuftragVoipDnView.setSipPassword(StringUtils.trimToNull(tfSipPassword.getText()));
        }

        if (selectedVoipDnPlanView != null) {
            if (!StringUtils.trimToEmpty(selectedVoipDnPlanView.getSipLogin()).equals(StringUtils.trimToEmpty(tfSipUri.getText()))) {
                selectedVoipDnPlanView.setSipLogin(StringUtils.trimToNull(tfSipUri.getText()));
            }

            if (!StringUtils.trimToEmpty(selectedVoipDnPlanView.getSipHauptrufnummer()).equals(StringUtils.trimToEmpty(tfSipBenutzername.getText()))) {
                selectedVoipDnPlanView.setSipHauptrufnummer(StringUtils.trimToNull(tfSipBenutzername.getText()));
            }
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSipUri = swingFactory.createLabel("sipuri");
        AKJLabel lblSipPassword = swingFactory.createLabel("sippassword");
        AKJLabel lblSipBenutzername= swingFactory.createLabel("sipbenutzername");
        AKJLabel lblSipRegistrar = swingFactory.createLabel("sipregistrar");

        tfSipUri = swingFactory.createTextField("sipuri", false);
        tfSipUri.addFocusListener(focusListener);
        tfSipPassword = swingFactory.createTextField("sippassword", false);
        tfSipPassword.addFocusListener(focusListener);
        AKJButton btnCreatePassword = swingFactory.createButton("create.sippassword", this::btnCreatePasswordActionPerformed);
        tfSipBenutzername = swingFactory.createTextField("sipbenutzername", false);
        tfSipBenutzername.addFocusListener(focusListener);
        tfSipRegistrar = swingFactory.createTextField("sipregistrar", false);
        tfSipRegistrar.addFocusListener(focusListener);

        // @formatter:off
        add(lblSipBenutzername,   GBCFactory.createGBC(0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(),       GBCFactory.createGBC(0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
        add(tfSipBenutzername,    GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(),       GBCFactory.createGBC(0,   0, 3, 0, 1, 1, GridBagConstraints.NONE));

        add(lblSipPassword,       GBCFactory.createGBC(0,   0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(),       GBCFactory.createGBC(0,   0, 5, 0, 1, 1, GridBagConstraints.NONE));
        add(tfSipPassword,        GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(btnCreatePassword,    GBCFactory.createGBC(0,   0, 7, 0, 1, 1, GridBagConstraints.NONE));
        add(new AKJPanel(),       GBCFactory.createGBC(0,   0, 8, 0, 1, 1, GridBagConstraints.NONE));

        add(lblSipRegistrar,      GBCFactory.createGBC(0,   0, 9, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(),       GBCFactory.createGBC(0,   0, 10, 0, 1, 1, GridBagConstraints.NONE));
        add(tfSipRegistrar,       GBCFactory.createGBC(100, 0, 11, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        add(lblSipUri,            GBCFactory.createGBC(0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(),       GBCFactory.createGBC(0,   0, 1, 1, 1, 1, GridBagConstraints.NONE));
        add(tfSipUri,             GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        add(new AKJPanel(),       GBCFactory.createGBC(0,   0, 3, 1, 1, 1, GridBagConstraints.NONE));

    }

    private void btnCreatePasswordActionPerformed(ActionEvent e) {
        try {
            tfSipPassword.setText(getVoIPService().generateSipPassword());
            setVoipLoginDataToModel();
        }
        catch (ServiceNotFoundException e1) {
            MessageHelper.showErrorDialog(getMainFrame(), e1);
        }
    }


    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    protected VoIPService getVoIPService() throws ServiceNotFoundException {
        return getCCService(VoIPService.class);
    }
}
