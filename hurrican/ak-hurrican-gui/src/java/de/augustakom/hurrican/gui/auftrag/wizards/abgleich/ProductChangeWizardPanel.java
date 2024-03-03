/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.2006 13:31:02
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * WizardPanel, um einen Produktwechsel zu unterstuetzen. <br> Der Benutzer erhaelt eine Auswahl, ob er einen
 * Produktwechsel durchfuehren will und wenn ja, welche Art des Produkt- bzw. Physikwechsels (z.B. Anschlussuebernahme)
 * verwendet werden soll.
 *
 *
 */
public class ProductChangeWizardPanel extends AbstractServiceWizardPanel implements AKDataLoaderComponent,
        AuftragWizardObjectNames, IServiceCallback {

    private static final Logger LOGGER = Logger.getLogger(ProductChangeWizardPanel.class);

    // GUI-Elemente
    private AKJRadioButton rbYes = null;
    private AKJRadioButton rbNo = null;
    private AKJDateComponent dcVorgabeAm = null;
    private List<AKJRadioButton> rbActions = null;

    private AuftragsMonitor auftragsMonitor = null;
    private AuftragsMonitor amKuendigung = null;
    private BAuftrag bAuftrag = null;
    private Collection<PhysikaenderungsTyp> possibleActions = null;

    /**
     * @param resource
     * @param wizardComponents
     */
    public ProductChangeWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/abgleich/ProductChangeWizardPanel.xml", wizardComponents);
        loadData();
        createGUI();
    }

    @Override
    protected final void createGUI() {
        RBActionListener rbListener = new RBActionListener();
        ButtonGroup bgYesNo = new ButtonGroup();
        rbYes = getSwingFactory().createRadioButton("yes", rbListener, true, bgYesNo);
        rbNo = getSwingFactory().createRadioButton("no", rbListener, false, bgYesNo);

        AKJLabel lblTitle = getSwingFactory().createLabel("title.question", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblActions = getSwingFactory().createLabel("possible.actions",
                new Object[] { "" + auftragsMonitor.getOldAuftragNoOrig() });
        AKJLabel lblVorgabeAm = getSwingFactory().createLabel("vorgabe.am");
        dcVorgabeAm = getSwingFactory().createDateComponent("vorgabe.am");
        dcVorgabeAm.setDate((bAuftrag != null) ? bAuftrag.getGueltigVon() : null);

        // @formatter:off
        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblTitle        , GBCFactory.createGBC(100,  0, 1, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        top.add(rbYes           , GBCFactory.createGBC(  0,  0, 1, 2, 4, 1, GridBagConstraints.HORIZONTAL, 25));
        top.add(rbNo            , GBCFactory.createGBC(  0,  0, 1, 3, 4, 1, GridBagConstraints.HORIZONTAL, 25));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.NONE));
        top.add(lblVorgabeAm    , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 2, 5, 1, 1, GridBagConstraints.NONE));
        top.add(dcVorgabeAm     , GBCFactory.createGBC(  0,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(100,  0, 4, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        rbActions = new ArrayList<AKJRadioButton>();
        ButtonGroup bgAction = new ButtonGroup();
        int y = 0;
        AKJPanel actPnl = new AKJPanel(new GridBagLayout());
        actPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, y++, 1, 1, GridBagConstraints.NONE));
        actPnl.add(lblActions       , GBCFactory.createGBC(100,  0, 1, y++, 1, 1, GridBagConstraints.HORIZONTAL));
        for (PhysikaenderungsTyp pt : possibleActions) {
            AKJRadioButton rbAction = new AKJRadioButton(pt.getName());
            rbAction.getAccessibleContext().setAccessibleName(""+pt.getId());
            bgAction.add(rbAction);
            rbActions.add(rbAction);

            actPnl.add(rbAction     , GBCFactory.createGBC(  0,  0, 1, y++, 1, 1, GridBagConstraints.HORIZONTAL, 25));
        }
        actPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 2, y++, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(top, BorderLayout.NORTH);
        getChildPanel().add(actPnl, BorderLayout.CENTER);
    }

    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            setFinishButtonEnabled(true);
            setNextButtonEnabled(false);
        }
    }

    @Override
    public final void loadData() {
        try {
            auftragsMonitor = (AuftragsMonitor) getWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR);
            amKuendigung = (AuftragsMonitor) getWizardObject(WIZARD_OBJECT_AM_KUENDIUNG_4_PROD_CHANGE);
            if ((auftragsMonitor == null) || (amKuendigung == null)) {
                throw new HurricanGUIException("AuftragsMonitor-Objekte nicht korrekt definiert!");
            }

            // versuchen, das Realisierungsdatum des neuen Auftrags zu ermitteln
            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            bAuftrag = bas.findAuftrag(auftragsMonitor.getAuftragNoOrig());

            Object p2psTmp = getWizardObject(WIZARD_OBJECT_POSSIBLE_PRODUCTCHANGES);
            if (p2psTmp instanceof Collection) {
                Collection<Produkt2Produkt> prod2prod = (Collection<Produkt2Produkt>) p2psTmp;

                PhysikService ps = getCCService(PhysikService.class);
                List<PhysikaenderungsTyp> typen = ps.findPhysikaenderungsTypen();

                possibleActions = new ArrayList<PhysikaenderungsTyp>();
                for (Produkt2Produkt p2p : prod2prod) {
                    final Long id2check = p2p.getPhysikaenderungsTyp();
                    Collection selection = CollectionUtils.select(typen, new Predicate() {
                        @Override
                        public boolean evaluate(Object obj) {
                            if ((obj instanceof PhysikaenderungsTyp) &&
                                    NumberTools.equal(id2check, ((PhysikaenderungsTyp) obj).getId())) {
                                return true;
                            }
                            return false;
                        }
                    });

                    if (CollectionTools.isNotEmpty(selection)) {
                        possibleActions.addAll(selection);
                    }
                }

                if (CollectionTools.isEmpty(possibleActions)) {
                    throw new HurricanGUIException("Es wurden keine Actions fuer den Produktwechsel gefunden!");
                }

                setFinishButtonEnabled(true);
            }
        }
        catch (Exception e) {
            setFinishButtonEnabled(false);
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    public void finish() throws AKJWizardFinishVetoException {
        try {
            Date vorgabeAm = dcVorgabeAm.getDate(null);
            if (vorgabeAm == null) {
                MessageHelper.showInfoDialog(this, "Bitte definieren Sie ein Vorgabe-Datum.", null, true);
                throw new AKJWizardFinishVetoException("kein Vorgabe-Datum!");
            }

            // ausgewaehlten Physikaenderungstyp ermitteln
            Long physikaendTyp = null;
            for (AKJRadioButton rb : rbActions) {
                if (rb.isSelected()) {
                    physikaendTyp = Long.valueOf(rb.getAccessibleContext().getAccessibleName());
                    break;
                }
            }

            if (physikaendTyp == null) {
                MessageHelper.showInfoDialog(this, "Bitte wählen Sie die durchzuführende Aktion aus.", null, true);
                throw new AKJWizardFinishVetoException("kein Physikaenderungstyp gewaehlt");
            }

            doProductChange(physikaendTyp, vorgabeAm);
        }
        catch (AKJWizardFinishVetoException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /*
     * Fuehrt den Produktwechsel durch. Steps:
     *  1. Basis-Auftrag kuendigen (Zugriff auf Hurrican Auftrags-ID notwendig)
     *  2. neuen Auftrag anlegen
     *  3. Auftragsdetails kopieren
     *  4. Physikwechsel durchfuehren
     * @param changeTyp
     * @param vorgabeAM
     *
     */
    private void doProductChange(Long changeTyp, Date vorgabeAM) {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            Auftrag auftrag = as.changeProduct(amKuendigung.getAuftragId(), changeTyp,
                    auftragsMonitor.getCcProduktId(), auftragsMonitor.getAuftragNoOrig(),
                    vorgabeAM, getSessionId(), this);

            AuftragDataFrame.openFrame(auftrag);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
        if (callbackAction == RangierungsService.CALLBACK_ASK_4_ACCOUNT_UEBERNAHME) {
            return Boolean.TRUE;
        }
        else {
            LOGGER.error("ServiceCallback-Action not implemented - Source: " + source + "  -  Action: " + callbackAction);
        }
        return null;
    }

    /* ActionListener fuer die RadioButtons. */
    class RBActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == rbNo) {
                setFinishButtonEnabled(false);
                setNextButtonEnabled(true);
            }
            else if (e.getSource() == rbYes) {
                setFinishButtonEnabled(true);
                setNextButtonEnabled(false);
            }
        }
    }

}


