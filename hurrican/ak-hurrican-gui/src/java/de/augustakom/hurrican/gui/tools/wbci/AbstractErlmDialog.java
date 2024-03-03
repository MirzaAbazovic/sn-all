/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciWitaServiceFacade;

/**
 * Abstract Erlm dialog provides basic dialog layout and basic labels.
 *
 *
 */
public abstract class AbstractErlmDialog<T extends WbciRequest> extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final long serialVersionUID = -3049402685679226597L;
    protected final Logger LOGGER = Logger.getLogger(getClass()); // NOSONAR squid:S1312

    private static final String DIALOG_TITLE = "dialog.title";
    private static final String INFO_LBL = "dialog.info.label";
    private static final String INFO_TXT = "dialog.info.text";
    private static final String ADDITIONAL_INFO_TXT = "dialog.additional.info.text";

    protected final T wbciRequest;
    private WbciMeldungService wbciMeldungService;
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    private AKJLabel lblAdditionalInfoText;

    public AbstractErlmDialog(String resource, T wbciRequest) {
        super(resource);
        this.wbciRequest = wbciRequest;

        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected final void initServices() throws ServiceNotFoundException {
        wbciMeldungService = getCCService(WbciMeldungService.class);
        wbciWitaServiceFacade = getCCService(WbciWitaServiceFacade.class);
    }

    @Override
    protected final void createGUI() {
        setSize(new Dimension(200, 100));
        setTitle(String.format(getSwingFactory().getText(DIALOG_TITLE),
                wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId()));

        AKJLabel lblInfo = getSwingFactory().createLabel(INFO_LBL, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblInfoText = getSwingFactory().createLabel(INFO_TXT, AKJLabel.LEFT);
        lblAdditionalInfoText = getSwingFactory().createLabel(ADDITIONAL_INFO_TXT, AKJLabel.LEFT);
        lblAdditionalInfoText.setVisible(false);

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblInfo              , GBCFactory.createGBC(  0,  0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblInfoText          , GBCFactory.createGBC(  0,  0, 1, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblAdditionalInfoText, GBCFactory.createGBC(  0,  0, 1, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0, 20, 1, 5, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(createDetailPanel()  , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(100,100, 1, 7, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        configureButton(CMD_SAVE, getSwingFactory().getText("send.txt"), getSwingFactory().getText("send.tooltip"), true, true);
        configureButton(CMD_CANCEL, getSwingFactory().getText("cancel.txt"), getSwingFactory().getText("cancel.tooltip"), true, true);
    }

    @Override
    public void loadData() {
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void cancel() {
        prepare4Close();
        setValue(null);
    }

    @Override
    protected void doSave() {
        try {
            onSave();
            prepare4Close();
            setValue(wbciRequest);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Abstract dialog save callback method. Subclasses must add custom logic here for dialog save action.
     */
    protected abstract void onSave();

    /**
     * Give subclasses the opportunity to add custom dialog details by overwriting this method.
     *
     * @return
     */
    protected AKJPanel createDetailPanel() {
        return new AKJPanel();
    }

    protected WbciMeldungService getWbciMeldungService() {
        return wbciMeldungService;
    }

    protected WbciWitaServiceFacade getWbciWitaServiceFacade() {
        return wbciWitaServiceFacade;
    }

    protected void setAdditionalInfoTxt(String additionalInfoTxt) {
        setAdditionalInfoTxt(additionalInfoTxt, false);
    }

    protected void setAdditionalInfoTxt(String additionalInfoTxt, boolean highlight) {
        lblAdditionalInfoText.setText(additionalInfoTxt);
        lblAdditionalInfoText.setVisible(StringUtils.isNotEmpty(additionalInfoTxt));
        if (highlight) {
            lblAdditionalInfoText.setForeground(Color.red);
        }
    }

}
