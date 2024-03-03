/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2006 13:05:49
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.model.cc.temp.LeistungsDiffCheck;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Wizard-Panel zur Anzeige der abzugleichenden Leistungen.
 *
 *
 */
public class LeistungsSynchWizardPanel extends AbstractServiceWizardPanel implements AuftragWizardObjectNames,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(LeistungsSynchWizardPanel.class);

    private AuftragsMonitor auftragsMonitor = null;

    private AKReflectionTableModel<LeistungsDiffView> tbMdlDiffs = null;

    /**
     * @param wizardComponents
     */
    public LeistungsSynchWizardPanel(AKJWizardComponents wizardComponents) {
        super(null, wizardComponents);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlDiffs = new AKReflectionTableModel<LeistungsDiffView>(
                new String[] { "ID", "techn. Leistung", "aktiv von", "aktiv bis", "Differenz", "Zugang", "Kündigung" },
                new String[] { "techLeistungId", "techLsName", "aktivVon", "aktivBis", "quantity", "zugang", "kuendigung" },
                new Class[] { Long.class, String.class, Date.class, Date.class, Long.class, Boolean.class,
                        Boolean.class }
        );
        AKJTable tbDiffs = new AKJTable(tbMdlDiffs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbDiffs.attachSorter();
        tbDiffs.fitTable(new int[] { 50, 150, 90, 90, 70, 70, 70 });
        AKJScrollPane spDiffs = new AKJScrollPane(tbDiffs, new Dimension(550, 150));

        AKJPanel title = new AKJPanel(new GridBagLayout());
        title.add(new AKJLabel("Folgende Leistungen werden abgeglichen:"),
                GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8)));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(title, BorderLayout.NORTH);
        getChildPanel().add(spDiffs, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            auftragsMonitor = (AuftragsMonitor) getWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR);
            tbMdlDiffs.setData(auftragsMonitor.getLeistungsDiffs());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#update()
     */
    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_BACKWARD) {
            removePanelsAfter(this);
            setFinishButtonEnabled(true);
            setNextButtonEnabled(false);
        }
    }

    /**
     * Prueft, ob die Leistungsdifferenzen auf dem Auftrag ausgefuehrt werden koennen. <br> Ist dies nicht der Fall,
     * wird der Benutzer 'gefragt', ob der Auftrag gekuendigt werden soll, damit ein Neu-Auftrag mit den notwendigen
     * Leistungen angelegt werden kann.
     *
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#finish()
     */
    @Override
    public void finish() throws AKJWizardFinishVetoException {
        if (CollectionTools.isEmpty(auftragsMonitor.getLeistungsDiffs())) {
            return;
        }

        // pruefen, ob aktiver Verlauf besteht - wenn ja, Exception
        checkVerlauf();

        try {
            setWaitCursor();
            CCLeistungsService ls = getCCService(CCLeistungsService.class);
            LeistungsDiffCheck check =
                    ls.checkLeistungsDiffs(auftragsMonitor.getLeistungsDiffs(), getSessionId());
            if ((check != null) && !check.isOk()) {
                // Leistungsdifferenz kann nicht abgeglichen werden
                //  --> Messages ueber ein weiteres Panel anzeigen und Auftrag evtl. kuendigen
                addWizardObject(WIZARD_OBJECT_LEISTUNGS_DIFF_CHECK, check);
                getWizardComponents().addWizardPanel(new LeistungDiffNotPossibleWizardPanel(getWizardComponents()));
                super.goNext();

                throw new AKJWizardFinishVetoException("Leistungs-Diff not possible.");
            }
            else {
                // Leistungen synchronisieren
                ls.synchTechLeistungen4Auftrag(auftragsMonitor.getAuftragId(), auftragsMonitor.getAuftragNoOrig(),
                        auftragsMonitor.getCcProduktId(), null, true, getSessionId());
                changeAuftragStatus();

                MessageHelper.showMessageDialog(this,
                        "Die Leistungsdifferenzen zwischen Taifun und\nHurrican wurden abgeglichen.\n" +
                                "Auftrag wird geöffnet.",
                        "Leistungsabgleich", JOptionPane.INFORMATION_MESSAGE
                );

                AuftragDataFrame.openFrame(auftragsMonitor);
            }
        }
        catch (AKJWizardFinishVetoException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /*
     * Ueberprueft, ob der Auftrag einen aktiven Verlauf besitzt. Ist dies
     * der Fall, wird eine Fehlermeldung angezeigt und eine Veto-Exception
     * erzeugt, damit der Abgleich nicht durchgefuehrt werden kann.
     * @throws AKJWizardFinishVetoException
     */
    private void checkVerlauf() throws AKJWizardFinishVetoException {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = bas.findActVerlauf4Auftrag(auftragsMonitor.getAuftragId(), false);
            if ((verlauf != null) && BooleanTools.nullToFalse(verlauf.getAkt())) {
                MessageHelper.showMessageDialog(this,
                        "Die Leistungen koennen nicht abgeglichen werden,\nda noch ein aktiver " +
                                "Bauauftrag fuer den Auftrag vorhanden ist!",
                        "Leistungsabgleich nicht moeglich", JOptionPane.ERROR_MESSAGE
                );

                throw new AKJWizardFinishVetoException("Aktiver Verlauf!");
            }
        }
        catch (AKJWizardFinishVetoException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            throw new AKJWizardFinishVetoException("Fehler bei Pruefung aktiver Verlauf!", e);
        }
    }

    /*
     * Versucht, den Auftragsstatus zu aendern.
     *   Status-Alt = 6000 --> 6100
     * Exceptions werden ignoriert!
     */
    private void changeAuftragStatus() {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragIdTx(auftragsMonitor.getAuftragId());
            if ((ad != null) && NumberTools.equal(ad.getStatusId(), AuftragStatus.IN_BETRIEB)) {
                ad.setStatusId(AuftragStatus.AENDERUNG);
                as.saveAuftragDaten(ad, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}


