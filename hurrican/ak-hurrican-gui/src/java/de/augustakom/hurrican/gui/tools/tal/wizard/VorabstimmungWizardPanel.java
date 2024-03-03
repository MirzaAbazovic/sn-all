/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2011 09:27:56
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.tools.tal.wita.VorabstimmungAufnehmendPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaVorabstimmungService;

public class VorabstimmungWizardPanel extends AbstractServiceWizardPanel implements AKDataLoaderComponent {

    private static final long serialVersionUID = 747471915328010706L;

    private static final Logger LOGGER = Logger.getLogger(VorabstimmungWizardPanel.class);
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/VorabstimmungWizardPanel.xml";

    private VorabstimmungAufnehmendPanel vorabstimmungPnl;
    private SelectAuftrag4KlammerWizardPanel selectAuftrag4KlammerWizardPanel;

    private WitaVorabstimmungService witaVorabstimmungService;
    private WitaConfigService witaConfigService;

    private AuftragDaten auftragDaten;
    private Endstelle endstelle;
    private final boolean isKlammerung;

    public VorabstimmungWizardPanel(AKJWizardComponents wizardComponents, boolean isKlammerung) {
        super(RESOURCE, wizardComponents);
        this.isKlammerung = isKlammerung;
        init();
    }

    private void initServices() throws ServiceNotFoundException {
        witaVorabstimmungService = getCCService(WitaVorabstimmungService.class);
        witaConfigService = getCCService(WitaConfigService.class);
    }

    private void init() {
        try {
            auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
            endstelle = (Endstelle) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_ENDSTELLE);

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
    public void update(Observable o, Object arg) {
        loadData();
    }

    @Override
    public final void loadData() {
        try {
            Vorabstimmung cbPv = witaVorabstimmungService.findVorabstimmung(endstelle, auftragDaten);
            vorabstimmungPnl.setModel(cbPv);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        vorabstimmungPnl = new VorabstimmungAufnehmendPanel(endstelle, auftragDaten, true, true);

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(vorabstimmungPnl   , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel()     , GBCFactory.createGBC(100,100, 2, 2, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    @Override
    public boolean next() {
        if (!vorabstimmungPnl.saveVorabstimmung()) {
            return false;
        }
        Object model = vorabstimmungPnl.getModel();
        if (model == null) {
            MessageHelper
                    .showErrorDialog(
                            SwingUtilities.getWindowAncestor(this),
                            new Exception("Es muss eine Vorabstimmung eingetragen werden.")
                    );
            return false;
        }

        if (model instanceof Vorabstimmung) {
            getWizardComponents().removeWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_WBCI_VORABSTIMMUNGSID);

            Vorabstimmung vorabstimmung = (Vorabstimmung) model;
            if (!vorabstimmung.isCarrierDtag()) {
                if (!isKlammerung) {
                    // für Klammerung wird DetailPanel schon durch WitaSelectCBVorgang hinzugefügt
                    selectAuftrag4KlammerWizardPanel = new SelectAuftrag4KlammerWizardPanel(getWizardComponents(), 0,
                            isKlammerung, false);
                    getWizardComponents().addWizardPanelAfter(this, selectAuftrag4KlammerWizardPanel);
                }
            }
            else if (selectAuftrag4KlammerWizardPanel != null) {
                getWizardComponents().removeWizardPanel(selectAuftrag4KlammerWizardPanel);
            }
            if (vorabstimmung.isCarrierDtag()) {
                // Wita 10: Vertragsnummer ist zwingend erforderlich, wenn DTAG abgebender Provider ist
                final boolean isWitaV2 = witaConfigService.getDefaultWitaVersion().isGreaterOrEqualThan(WitaCdmVersion.V2);
                if (isWitaV2 && StringUtils.isEmpty(vorabstimmung.getProviderVtrNr())) {
                    MessageHelper
                            .showErrorDialog(
                                    SwingUtilities.getWindowAncestor(this),
                                    new Exception("Es muss entweder eine Vorabstimmung oder eine Vertragsnummer eingetragen werden!")
                            );
                    return false;
                }
            }
        }
        else if (model instanceof String) {
            addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_WBCI_VORABSTIMMUNGSID, model);
        }
        else {
            MessageHelper
                    .showErrorDialog(
                            HurricanSystemRegistry.instance().getMainFrame(),
                            new Exception(
                                    "Bei der WITA-Verbundleistung muss entweder eine WBCI- oder eine Fax-Vorabstimmung ausgewählt werden!")
                    );
            return false;
        }
        return super.next();
    }

}
