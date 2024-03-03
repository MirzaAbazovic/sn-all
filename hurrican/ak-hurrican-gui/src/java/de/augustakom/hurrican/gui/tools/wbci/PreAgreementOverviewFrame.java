/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.06.2007 07:47:02
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJTabbedPane;

/**
 *
 *         <p/>
 *         The GUI for the WBCI-Vorabstimmungs-GFs will only starts if the flag 'WBCI_ENABLED' in table ' T_FEATURE' is
 *         set to 1.
 * @see de.augustakom.hurrican.gui.tools.wbci.actions.OpenPreAgreementOverviewAction#generateWbciFrame().
 */
public class PreAgreementOverviewFrame extends AKJAbstractInternalFrame implements ChangeListener {

    private static final String TITEL_PREAGREEMENT_OVERVIEW = "Vorabstimmungsübersicht";
    private static final String TITEL_RECEIVING_PREAGREEMENTS = "Aufnehmend";
    private static final String TITEL_DONATING_PREAGREEMENTS = "Abgebend";
    private static final long serialVersionUID = 7988280525689920304L;

    private AKJTabbedPane tabbedPane;
    private final Set<Integer> alreadyLoaded = new HashSet();

    public PreAgreementOverviewFrame() {
        super(null);
        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle(TITEL_PREAGREEMENT_OVERVIEW);

        getContentPane().setLayout(new BorderLayout());

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(this);
        tabbedPane.add(TITEL_RECEIVING_PREAGREEMENTS, new PreAgreementReceivingEKPOverviewPanel());
        tabbedPane.add(TITEL_DONATING_PREAGREEMENTS, new PreAgreementDonatingEKPOverviewPanel());
        getContentPane().add(tabbedPane);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (!alreadyLoaded.contains(selectedIndex)
                && (tabbedPane.getSelectedComponent() instanceof AKDataLoaderComponent)) {
            ((AKDataLoaderComponent) tabbedPane.getSelectedComponent()).loadData();
            alreadyLoaded.add(selectedIndex);
        }
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
