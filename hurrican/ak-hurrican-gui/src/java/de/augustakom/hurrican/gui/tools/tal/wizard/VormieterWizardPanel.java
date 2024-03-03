/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 11:43:14
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.tools.tal.wita.VormieterPanel;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.service.cc.CarrierService;

/**
 * Wizard-Panel fuer die Erfassung von Vormieter-Daten.
 */
public class VormieterWizardPanel extends AbstractServiceWizardPanel implements AKDataLoaderComponent {

    private static final long serialVersionUID = -2562748349098812038L;

    private static final Logger LOGGER = Logger.getLogger(VorabstimmungWizardPanel.class);
    private static final String RESOURCE =
            "de/augustakom/hurrican/gui/tools/tal/resources/VormieterWizardPanel.xml";

    private VormieterPanel vormieterPnl = null;

    private CarrierService carrierService;
    private final Long carrierbestellungId;

    public VormieterWizardPanel(AKJWizardComponents wizardComponents) {
        super(RESOURCE, wizardComponents);
        carrierbestellungId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID);
        init();
    }

    private void initServices() throws ServiceNotFoundException {
        carrierService = getCCService(CarrierService.class);
    }

    private void init() {
        try {
            createGUI();
            initServices();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        vormieterPnl = new VormieterPanel(carrierbestellungId);

        AKJPanel child = getChildPanel();
        // @formatter:off
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(vormieterPnl  , GBCFactory.createGBC(100,100, 1, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    @Override
    public void update(Observable o, Object arg) {
        packWizard();
        loadData();
    }

    @Override
    public final void loadData() {
        try {
            Carrierbestellung carrierbestellung = carrierService.findCB(carrierbestellungId);
            vormieterPnl.setModel(carrierbestellung.getCarrierbestellungVormieter());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected boolean goNext() {
        if (!vormieterPnl.saveVormieter()) {
            return false;
        }
        return super.goNext();
    }
}
