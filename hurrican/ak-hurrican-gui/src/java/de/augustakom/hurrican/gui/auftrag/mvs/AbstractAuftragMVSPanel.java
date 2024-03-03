/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 11:10:03
 */
package de.augustakom.hurrican.gui.auftrag.mvs;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.MVSService;

/**
 * Gemeinsame Oberklasse fuer alle MVS Panel.
 *
 *
 * @since Release 11
 */
abstract class AbstractAuftragMVSPanel<T extends AuftragMVS> extends AbstractDataPanel {

    private static final long serialVersionUID = 3759254125242861562L;

    private static final Logger LOGGER = Logger.getLogger(AbstractAuftragMVSPanel.class);

    protected static final String NEW = "new";
    protected static final String DOMAINS = "domains";
    private static final String DOMAIN = "domain";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    protected AKJTextField tfUsername;
    protected AKJTextField tfPassword;
    protected AKJTextField tfDomain;

    private AKJPanel mvsDataPanel;

    private T mvsModel = null;
    private CCAuftragModel auftragModel = null;

    private AKJButton btnCreateNew;

    protected void enableAllComponents(boolean enable) {
        for (Component component : getComponents()) {
            component.setEnabled(enable);
        }
    }

    /**
     * @return Returns the auftragModel.
     */
    protected CCAuftragModel getAuftragModel() {
        return auftragModel;
    }

    /**
     * @return Returns the mvsModel.
     */
    protected T getMvsModel() {
        return mvsModel;
    }

    /**
     * @param mvsModel The mvsModel to set.
     */
    protected void setMvsModel(T mvsModel) {
        this.mvsModel = mvsModel;
    }

    protected boolean isMvsModelEmpty() {
        return getMvsModel() == null;
    }

    protected void enableOrDisableNewButtonAndFields() {
        if (getMvsModel() != null) {
            btnCreateNew.setEnabled(false);
            GuiTools.enableComponents(getComponentsToEnable(), true, true);
        }
        else {
            btnCreateNew.setEnabled(true);
        }
    }

    protected MVSService getMVSService() {
        MVSService service = null;
        try {
            service = getCCService(MVSService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        return service;
    }

    protected AKJPanel getMvsDataPanel() {
        return mvsDataPanel;
    }

    /**
     * @param resource
     */
    public AbstractAuftragMVSPanel(String resource) {
        super(resource);
    }

    /**
     * Liefert ein generiertes Passwort mit der gegebenen Laenge.
     *
     * @param length
     * @return
     */
    protected String generateRandomPasswordForLength(int length) {
        String result = RandomTools.createPassword(length);
        return result;
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        try {
            auftragModel = null;
            if (model instanceof CCAuftragModel) {
                auftragModel = (CCAuftragModel) model;
            }
            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException("Fehler beim Laden von MVS Daten: " + e.getMessage(), e);
        }
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    protected void createNewEntry() {
        GuiTools.enableComponents(getComponentsToEnable(), true, true);
        tfUsername.setText("admin");
        tfPassword.setText(generateRandomPasswordForLength(8));
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblUsername = getSwingFactory().createLabel(USERNAME);
        tfUsername = getSwingFactory().createTextField(USERNAME, false);

        AKJLabel lblPassword = getSwingFactory().createLabel(PASSWORD);
        tfPassword = getSwingFactory().createTextField(PASSWORD, false);

        AKJLabel lblDomain = getSwingFactory().createLabel(DOMAIN);
        tfDomain = getSwingFactory().createTextField(DOMAIN);

        btnCreateNew = getSwingFactory().createButton(NEW, getActionListener(), null);
        AKJButton btnOpenDomainDialog = getSwingFactory().createButton(DOMAINS, getActionListener(), null);
        btnOpenDomainDialog.setPreferredSize(new Dimension(20, 20));
        btnOpenDomainDialog.setText("...");

        mvsDataPanel = new AKJPanel();
        mvsDataPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.title")));
        mvsDataPanel.setLayout(new GridBagLayout());

        int yCounter = 0;
        // @formatter:off
        mvsDataPanel.add(new AKJPanel()     , GBCFactory.createGBC(   0,   0, 0, yCounter  , 1, 1, GridBagConstraints.NONE));
        mvsDataPanel.add(btnCreateNew       , GBCFactory.createGBC(   0,   0, 1, yCounter  , 1, 1, GridBagConstraints.NONE));
        mvsDataPanel.add(lblUsername        , GBCFactory.createGBC(   0,   0, 1, ++yCounter, 1, 1, GridBagConstraints.HORIZONTAL));
        mvsDataPanel.add(new AKJPanel()     , GBCFactory.createGBC(   0,   0, 2, yCounter  , 1, 1, GridBagConstraints.NONE));
        mvsDataPanel.add(tfUsername         , GBCFactory.createGBC(   0,   0, 3, yCounter  , 1, 1, GridBagConstraints.HORIZONTAL));
        mvsDataPanel.add(lblPassword        , GBCFactory.createGBC(   0,   0, 1, ++yCounter, 1, 1, GridBagConstraints.HORIZONTAL));
        mvsDataPanel.add(tfPassword         , GBCFactory.createGBC(   0,   0, 3, yCounter  , 1, 1, GridBagConstraints.HORIZONTAL));
        mvsDataPanel.add(lblDomain          , GBCFactory.createGBC(   0,   0, 1, ++yCounter, 1, 1, GridBagConstraints.HORIZONTAL));
        mvsDataPanel.add(tfDomain           , GBCFactory.createGBC(   0,   0, 3, yCounter  , 1, 1, GridBagConstraints.HORIZONTAL));
        mvsDataPanel.add(btnOpenDomainDialog, GBCFactory.createGBC(   0,   0, 4, yCounter  , 1, 1, GridBagConstraints.HORIZONTAL));
        addSpecificGUIElements(++yCounter);
        mvsDataPanel.add(new AKJPanel()     , GBCFactory.createGBC(   0, 100, 0, ++yCounter, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        this.setLayout(new GridBagLayout());
        this.add(mvsDataPanel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnCreateNew, tfUsername, tfPassword, tfDomain, btnOpenDomainDialog);
    }

    protected Component[] getComponentsToEnable() {
        return new Component[] { tfDomain };
    }

    protected void addSpecificGUIElements(int yCounter) {
        // to be implemented by subclasses if required
    }

}
