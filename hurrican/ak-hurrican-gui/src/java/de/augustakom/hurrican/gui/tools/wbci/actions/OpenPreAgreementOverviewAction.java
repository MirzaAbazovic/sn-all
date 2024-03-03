/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2007 15:48:09
 */
package de.augustakom.hurrican.gui.tools.wbci.actions;

import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.wbci.PreAgreementOverviewFrame;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Action, um das Frame fuer alle WBCI-Vorabstimmungen zu oeffnen.
 *
 *
 */
public class OpenPreAgreementOverviewAction extends AKAbstractOpenFrameAction {

    private static final Logger LOGGER = Logger.getLogger(OpenPreAgreementOverviewAction.class);
    private static final long serialVersionUID = -634772408078559619L;

    private FeatureService featureService;
    private String uniqueName = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen() <br/> The frame will only get up
     * if the flag 'WBCI_ENABLED' in table ' T_FEATURE' is set to 1. Else the the user will get an {@link
     * AKGUIException} with the message "Die Übersichtsseite der WBCI-Vorabstimmung ist aktuell noch nicht nutzbar!".
     */
    protected AKJInternalFrame getFrameToOpen() {
        PreAgreementOverviewFrame frame = generateWbciFrame();
        if (frame != null) {
            uniqueName = frame.getUniqueName();
        }
        return frame;
    }

    private PreAgreementOverviewFrame generateWbciFrame() {
        try {
            initServices();
            if (!featureService.isFeatureOnline(Feature.FeatureName.WBCI_ENABLED)) {
                // check if the flag 'WBCI_ENABLED' in table ' T_FEATURE' is set to 1!
                throw new AKGUIException("Die Übersichtsseite der WBCI-Vorabstimmung ist aktuell noch nicht nutzbar!");
            }
            // if wbci is enabled, then create the GUI
            return new PreAgreementOverviewFrame();
        }
        catch (AKGUIException | ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            return null;
        }
    }

    private void initServices() throws ServiceNotFoundException {
        featureService = CCServiceFinder.instance().getCCService(FeatureService.class);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getMainFrame()
     */
    @Override
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getUniqueName()
     */
    @Override
    protected String getUniqueName() {
        return uniqueName;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#maximizeFrame()
     */
    @Override
    protected boolean maximizeFrame() {
        return true;
    }

}
