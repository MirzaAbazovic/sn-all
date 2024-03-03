/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 16:31:40
 */
package de.augustakom.hurrican.gui.auftrag.wizards.shared;

import java.awt.*;
import java.beans.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Wizard-Panel, um einige Details fuer den neuen Hurrican-Auftrag anzugeben.
 *
 *
 */
public class AuftragDetailsPanel extends AbstractServiceWizardPanel implements AKModelOwner,
        PropertyChangeListener, AuftragWizardObjectNames, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(AuftragDetailsPanel.class);

    private AKJDateComponent dcAngebotAm = null;
    private AKJDateComponent dcAuftragAm = null;
    private AKJDateComponent dcVorgabeKunde = null;
    private AKJDateComponent dcVorgabeAm = null;
    private AKJTextField tfBestellNr = null;

    private AuftragDaten auftragDaten = null;

    /**
     * Konstruktor mit Angabe der Wizard-Komponente.
     */
    public AuftragDetailsPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/shared/AuftragDetailsPanel.xml", wizardComponents);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblDetails = getSwingFactory().createLabel("details");
        AKJLabel lblVorgabeKunde = getSwingFactory().createLabel("vorgabe.kunde");
        AKJLabel lblVorgabeAm = getSwingFactory().createLabel("vorgabe.am");
        AKJLabel lblAngebotAm = getSwingFactory().createLabel("angebot.am");
        AKJLabel lblAuftragAm = getSwingFactory().createLabel("auftrag.am");
        AKJLabel lblBestellNr = getSwingFactory().createLabel("bestell.nr");

        Component mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        dcVorgabeKunde = getSwingFactory().createDateComponent("vorgabe.kunde");
        dcVorgabeKunde.setParent4Dialog(mainFrame);
        dcVorgabeKunde.addPropertyChangeListener(this);
        dcVorgabeAm = getSwingFactory().createDateComponent("vorgabe.am");
        dcVorgabeAm.setParent4Dialog(mainFrame);
        dcAngebotAm = getSwingFactory().createDateComponent("angebot.am");
        dcAngebotAm.setParent4Dialog(mainFrame);
        dcAuftragAm = getSwingFactory().createDateComponent("auftrag.am");
        dcAuftragAm.setParent4Dialog(mainFrame);
        tfBestellNr = getSwingFactory().createTextField("bestell.nr");

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblDetails, GBCFactory.createGBC(0, 0, 1, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        this.add(lblVorgabeKunde, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.NONE));
        this.add(dcVorgabeKunde, GBCFactory.createGBC(0, 0, 4, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 3, 1, 1, GridBagConstraints.NONE));
        this.add(lblAngebotAm, GBCFactory.createGBC(0, 0, 7, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 8, 3, 1, 1, GridBagConstraints.NONE));
        this.add(dcAngebotAm, GBCFactory.createGBC(0, 0, 9, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblVorgabeAm, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dcVorgabeAm, GBCFactory.createGBC(50, 0, 4, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblAuftragAm, GBCFactory.createGBC(0, 0, 7, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dcAuftragAm, GBCFactory.createGBC(50, 0, 9, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.NONE));
        this.add(lblBestellNr, GBCFactory.createGBC(0, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfBestellNr, GBCFactory.createGBC(50, 0, 4, 6, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 10, 7, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    public void setModel(Observable model) {
        if (model instanceof AuftragDaten) {
            auftragDaten = (AuftragDaten) model;
            loadData();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData() Bei Buendel-Auftraegen werden die Daten des
     * zugehoerigen Buendel-Auftrags ermittelt und vorbelegt.
     */
    @Override
    public final void loadData() {
        try {
            if (auftragDaten != null) {
                // Billing-Auftrag ermitteln
                if (auftragDaten.getAuftragNoOrig() != null) {
                    BillingAuftragService bas = getBillingService(BillingAuftragService.class);
                    BAuftrag bAuftrag = bas.findAuftrag(auftragDaten.getAuftragNoOrig());
                    if (bAuftrag != null) {
                        dcVorgabeAm.setDate(bAuftrag.getGueltigVon());
                        dcAuftragAm.setDate(bAuftrag.getVertragsdatum());
                        dcVorgabeKunde.setDate(bAuftrag.getWunschTermin());
                        tfBestellNr.setText(bAuftrag.getBestellId());
                    }
                }

                Integer buendelNr = auftragDaten.getBuendelNr();
                String buendelNrHerkunft = auftragDaten.getBuendelNrHerkunft();
                if ((buendelNr == null) || (buendelNr.intValue() == 0) || StringUtils.isBlank(buendelNrHerkunft)) {
                    return;
                }

                // zugehoerige Buendel-Auftraege ermitteln
                CCAuftragService as = getCCService(CCAuftragService.class);
                List<AuftragDaten> ads = as.findAuftragDaten4Buendel(buendelNr, buendelNrHerkunft);
                for (AuftragDaten ad : ads) {
                    if (NumberTools.notEqual(ad.getAuftragId(), auftragDaten.getAuftragId())) {
                        tfBestellNr.setText(ad.getBestellNr());

                        // AuftragTechnik - Auftragsart setzen
                        AuftragTechnik auftragTechnik = (AuftragTechnik) getWizardObject(WIZARD_OBJECT_CC_AUFTRAG_TECHNIK);
                        AuftragTechnik atBuendel = as.findAuftragTechnikByAuftragId(ad.getAuftragId());
                        if ((atBuendel != null) && (ads.size() == 1)) {
                            // Auftragsart nur setzen, wenn genau 1 zug. Buendel-Auftrag gefunden
                            // wurde - ansonsten koennte es z.B. ein Up/Downgrade sein!
                            auftragTechnik.setAuftragsart(atBuendel.getAuftragsart());
                        }
                        break;
                    }
                }

                MessageHelper.showMessageDialog(getParent(), getSwingFactory().getText("daten.uebernahme"),
                        "Kontrolle", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void saveModel() {
        auftragDaten.setVorgabeKunde(dcVorgabeKunde.getDate(null));
        auftragDaten.setVorgabeSCV(dcVorgabeAm.getDate(null));
        auftragDaten.setAngebotDatum(dcAngebotAm.getDate(null));
        auftragDaten.setAuftragDatum(dcAuftragAm.getDate(null));
        auftragDaten.setBestellNr(tfBestellNr.getText(null));
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void readModel() {
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ((evt.getSource() == dcVorgabeKunde)
                && AKJDateComponent.PROPERTY_CHANGE_DATE.equals(evt.getPropertyName())
                && (dcVorgabeAm.getDate(null) == null)) {
            dcVorgabeAm.setDate(dcVorgabeKunde.getDate(null));
        }
    }
}


