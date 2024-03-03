/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 11:32:50
 */
package de.augustakom.authentication.gui.basics;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.service.AKApplicationService;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.locator.ServiceLocator;


/**
 * Abstraktes Panel, das Methoden fuer den Zugriff auf Authentication-Services bietet.
 *
 *
 */
public abstract class AbstractAuthenticationPanel extends AKJAbstractPanel {

    private static final Logger LOGGER = Logger.getLogger(AbstractAuthenticationPanel.class);

    public AbstractAuthenticationPanel(String resource) {
        super(resource);
    }

    public AbstractAuthenticationPanel(String resource, LayoutManager layout) {
        super(resource, layout);
    }

    /**
     * Gibt einen Authentication-Service (z.B. AKUserService) zurueck.
     *
     * @param serviceName Name des gesuchten Services.
     * @param type        Typ des gesuchten Services.
     * @return gesucheter Service
     * @throws ServiceNotFoundException
     */
    protected <T> T getAuthenticationService(String serviceName, Class<T> type) throws ServiceNotFoundException {
        return ServiceLocator.instance().getService(serviceName, type);
    }

    /**
     * reads all applications and fills supplied combobox with retrieved values
     */
    protected void readApplications(AKJComboBox cobApplication) {
        DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
        try {
            AKApplicationService appService = getAuthenticationService(
                    AKAuthenticationServiceNames.APPLICATION_SERVICE, AKApplicationService.class);
            List<AKApplication> applications = appService.findAll();

            if (applications != null) {
                for (AKApplication application : applications) {
                    cbModel.addElement(application);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
        finally {
            cobApplication.setModel(cbModel);
        }
    }

    /**
     * selects the supplied application for the supplied combobox
     */
    protected void selectApplication(Long applicationId, AKJComboBox cobApplication) {
        for (int i = 0; i < cobApplication.getItemCount(); i++) {
            if (((AKApplication) cobApplication.getItemAt(i)).getId().equals(applicationId)) {
                cobApplication.setSelectedIndex(i);
                break;
            }
        }
    }
}
