/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 10:45:57
 */
package de.augustakom.hurrican.gui.auftrag.wizards.anlage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.CreateAccountAction;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.CreateAuftragInternAction;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.CreateEndstellenAction;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardOptionDialog;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Wizard-Dialog, um einen neuen Hurrican-Auftrag anzulegen.
 *
 *
 */
public class CreateHurricanAuftragWizard extends AbstractServiceWizardOptionDialog implements AuftragWizardObjectNames {

    private static final Logger LOGGER = Logger.getLogger(CreateHurricanAuftragWizard.class);

    /**
     * Konstruktor fuer den Wizard-Dialog. <br>
     *
     * @param kundeNoOrig (original) Kundennummer des Kunden, fuer den ein neuer Hurrican-Auftrag angelegt werden
     *                    soll.+
     * @param kundeNoOrig (original) Kundennummer des Kunden, fuer den ein Auftrag angelegt werden soll.
     */
    public CreateHurricanAuftragWizard(Long kundeNoOrig) {
        super(null);
        init(kundeNoOrig);
    }

    /* Initialisiert den Wizard */
    private void init(Long kundeNoOrig) {
        getWizardComponents().addWizardObject(
                AuftragWizardObjectNames.WIZARD_OBJECT_KUNDEN_NO, kundeNoOrig);
        getWizardComponents().addWizardObject(
                AuftragWizardObjectNames.WIZARD_OBJECT_CC_AUFTRAG_DATEN, new AuftragDaten());
        getWizardComponents().addWizardObject(
                AuftragWizardObjectNames.WIZARD_OBJECT_CC_AUFTRAG_TECHNIK, new AuftragTechnik());

        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle("Hurrican-Auftrag anlegen");
        setIconURL("de/augustakom/hurrican/gui/images/auftrag.gif");

        SelectProduktWizardPanel selProdWP = new SelectProduktWizardPanel(getWizardComponents());
        getWizardComponents().addWizardPanel(selProdWP);

        getWizardComponents().getFinishButton().setText("Fertigstellen");
        getWizardComponents().updateComponents();
    }

    @Override
    public void finishWizard() {
        try {
            setWaitCursor();
            getWizardComponents().setFinishButtonEnabled(false);
            CCAuftragService service = getCCService(CCAuftragService.class);

            Object tmp = getWizardComponents().getWizardObject(WIZARD_OBJECT_CC_AUFTRAGSART);
            String hvtSchaltung = (tmp instanceof String) ? (String) tmp : null;

            AuftragDaten ad = (AuftragDaten) getWizardComponents().getWizardObject(WIZARD_OBJECT_CC_AUFTRAG_DATEN);
            ad.setStatusId(AuftragStatus.ERFASSUNG_SCV);

            AuftragTechnik auftragTechnik = (AuftragTechnik) getWizardComponents().getWizardObject(WIZARD_OBJECT_CC_AUFTRAG_TECHNIK);
            if (StringUtils.equals(hvtSchaltung, AUFTRAGSART_UEBERNAHME)) {
                auftragTechnik.setAuftragsart(BAVerlaufAnlass.UEBERNAHME);
            }
            else {
                auftragTechnik.setAuftragsart(BAVerlaufAnlass.NEUSCHALTUNG);
            }

            Auftrag auftrag = service.createAuftrag(
                    (Long) getWizardComponents().getWizardObject(WIZARD_OBJECT_KUNDEN_NO), ad, auftragTechnik,
                    HurricanSystemRegistry.instance().getSessionId(), null);

            // Endstellen anlegen (wenn notwendig)
            CreateEndstellenAction esAction = new CreateEndstellenAction(auftrag, this);
            esAction.actionPerformed(null);

            // Accounts anlegen (wenn notwendig)
            CreateAccountAction accAction = new CreateAccountAction(auftrag, this);
            accAction.actionPerformed(null);

            // AuftragIntern anlegen, falls vorhanden
            Object tmpAI = getWizardComponents().getWizardObject(WIZARD_OBJECT_CC_AUFTRAG_INTERN);
            if ((tmpAI != null) && (tmpAI instanceof AuftragIntern)) {
                AuftragIntern ai = (AuftragIntern) tmpAI;
                CreateAuftragInternAction aiAction = new CreateAuftragInternAction(auftrag, ai);
                aiAction.actionPerformed(null);
            }

            AuftragDataFrame.openFrame(auftrag);
            getWizardComponents().setFinishButtonEnabled(true);
            super.finishWizard();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

}


