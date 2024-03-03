/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 09:43:19
 */
package de.augustakom.hurrican.gui.auftrag.wizards.anlage;

import java.awt.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Wizard-Panel fuer die Definition von Daten fuer interne Arbeitsauftraege.
 *
 *
 */
public class AuftragInternWizardPanel extends AbstractServiceWizardPanel implements AuftragWizardObjectNames,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(AuftragInternWizardPanel.class);

    // GUI-Elements
    private AKReferenceField rfHVT = null;
    private AKReferenceField rfWorkType = null;

    private boolean isLoaded = false;

    /**
     * @param wizardComponents
     */
    public AuftragInternWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/anlage/AuftragInternWizardPanel.xml", wizardComponents);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTitle = getSwingFactory().createLabel("title");
        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblWorkType = getSwingFactory().createLabel("work.type");

        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        rfWorkType = getSwingFactory().createReferenceField("work.type");

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblTitle, GBCFactory.createGBC(0, 0, 1, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        this.add(lblHVT, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.NONE));
        this.add(rfHVT, GBCFactory.createGBC(50, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblWorkType, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(rfWorkType, GBCFactory.createGBC(50, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 5, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            if (!isLoaded) {
                ISimpleFindService sfs = getCCService(QueryCCService.class);
                rfHVT.setFindService(sfs);

                Reference workTypeRefEx = new Reference();
                workTypeRefEx.setType(Reference.REF_TYPE_WORKING_TYPE);
                rfWorkType.setReferenceFindExample(workTypeRefEx);
                rfWorkType.setFindService(sfs);

                isLoaded = true;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#update()
     */
    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            setFinishButtonEnabled(true);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#finish()
     */
    @Override
    public void finish() {
        try {
            // hard-coded: Auftragsart auf 'interne Arbeit' setzen
            AuftragTechnik at = (AuftragTechnik) getWizardComponents().getWizardObject(WIZARD_OBJECT_CC_AUFTRAG_TECHNIK);
            if (at != null) {
                at.setAuftragsart(BAVerlaufAnlass.INTERN_WORK);
            }

            // Objekte fuer die interne Arbeit anlegen
            AuftragIntern ai = new AuftragIntern();
            ai.setHvtStandortId(rfHVT.getReferenceIdAs(Long.class));
            ai.setWorkingTypeRefId(rfWorkType.getReferenceIdAs(Long.class));
            getWizardComponents().addWizardObject(WIZARD_OBJECT_CC_AUFTRAG_INTERN, ai);

            LOGGER.debug("finish on AuftragInternWP");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}


