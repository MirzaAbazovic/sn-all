/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.06.2007 07:47:02
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.util.*;
import javax.swing.event.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.temp.AKCompBehaviorSummary;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKGUIService;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.service.WitaConfigService;

/**
 * Frame fuer die Darstellung der noch nicht abgeschlossenen el. TAL-Bestellungen.
 */
public class UnfinishedCarrierBestellungFrame extends AKJAbstractInternalFrame implements ChangeListener {

    private static final long serialVersionUID = -4412759761875577522L;
    private static final Logger LOGGER = Logger.getLogger(UnfinishedCarrierBestellungFrame.class);

    private static final String TITEL_CARRIERBESTELLUNG = "TAL-Bestellungen";
    private static final String TITEL_UNFINISHED_CARRIERBESTELLUNG = "Nicht Abgeschlossene TAL-Bestellungen";
    private static final String TITEL_OPEN_TAMS = "Offene Terminanforderungsmeldungen (TAMs)";
    private static final String TITEL_OPEN_TKG_TAMS = "Offene TKG-Terminanforderungsmeldungen (TKG-TAMs)";
    private static final String TITEL_KLAERFAELLE = "Workflow-Klärfälle";
    private static final String TITEL_ABGEBENE_LEITUNG = "Abgebende Leitungen";

    private AKJTabbedPane tabbedPane;
    private final Set<Integer> alreadyLoaded = new HashSet<>();

    public UnfinishedCarrierBestellungFrame() {
        super(null);
        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle(TITEL_CARRIERBESTELLUNG);

        getContentPane().setLayout(new BorderLayout());

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(this);
        tabbedPane.add(TITEL_UNFINISHED_CARRIERBESTELLUNG, new UnfinishedCarrierBestellungPanel());
        tabbedPane.add(TITEL_OPEN_TAMS, new OffeneTamsPanel());

        if (isWitaV2orGreater())
        {
            tabbedPane.add(TITEL_OPEN_TKG_TAMS, new OffeneTKGTamsPanel());
        }
        tabbedPane.add(TITEL_ABGEBENE_LEITUNG, new AbgebendeLeitungenPanel());
        try {
            AKCompBehaviorSummary compBehaviour = getCompBehaviour(this.getClass().getName(),
                    WitaKlaerfaellePanel.class.getSimpleName());
            if (compBehaviour.isComponentExecutable()) {
                tabbedPane.add(TITEL_KLAERFAELLE, new WitaKlaerfaellePanel());
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
        getContentPane().add(tabbedPane);
    }

    private boolean isWitaV2orGreater()
    {
        try {
            WitaConfigService witaConfigService = CCServiceFinder.instance().getCCService(WitaConfigService.class);
            final WitaCdmVersion witaVersion = witaConfigService.getDefaultWitaVersion();
            return witaVersion.isGreaterOrEqualThan(WitaCdmVersion.V2);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            return false;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (!alreadyLoaded.contains(selectedIndex) && (tabbedPane.getSelectedComponent() instanceof AKDataLoaderComponent)) {
            ((AKDataLoaderComponent) tabbedPane.getSelectedComponent()).loadData();
            alreadyLoaded.add(selectedIndex);
        }
    }

    private AKCompBehaviorSummary getCompBehaviour(String parent, String componentName) throws ServiceNotFoundException {
        IServiceLocator serviceLocator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        AKGUIService guiService = serviceLocator.getService(AKAuthenticationServiceNames.GUI_SERVICE,
                AKGUIService.class, null);

        AKCompBehaviorSummary summary = new AKCompBehaviorSummary();
        summary.setComponentName(componentName);
        summary.setParentClass(parent);

        AKCompBehaviorSummary[] behaviours = guiService.evaluateRights(
                HurricanSystemRegistry.instance().getSessionId(), new AKCompBehaviorSummary[] { summary });
        return behaviours[0];
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        // not used
    }
}
