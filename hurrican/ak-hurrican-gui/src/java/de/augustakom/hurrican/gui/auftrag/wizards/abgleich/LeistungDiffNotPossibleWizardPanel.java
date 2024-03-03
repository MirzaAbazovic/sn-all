/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2006 09:04:04
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.AbstractKuendigungWizardPanel;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.model.cc.temp.LeistungsDiffCheck;


/**
 * Wizard-Panel, wenn eine Leistungsdifferenz auf einem Auftrag nicht abgeglichen werden kann (z.B. wenn die Physik fuer
 * die neue Leistung nicht geeignet ist). <br> Dieses Panel zeigt an, wieso die Leistungsdiff nicht abgeglichen werden
 * kann und fordert den Benutzer auf, ein Kuendigungsdatum fuer den Auftrag anzugeben. Dadurch kann der Auftrag
 * gekuendigt werden und spaeter ein neuer Auftrag mit den Leistungen angelegt werden.
 *
 *
 */
public class LeistungDiffNotPossibleWizardPanel extends AbstractKuendigungWizardPanel implements
        AKDataLoaderComponent, AuftragWizardObjectNames {

    private static final Logger LOGGER = Logger.getLogger(LeistungDiffNotPossibleWizardPanel.class);

    private static final String RESOURCE =
            "de/augustakom/hurrican/gui/auftrag/wizards/abgleich/LeistungDiffNotPossibleWizardPanel.xml";

    private AuftragsMonitor auftragsMonitor = null;

    private AKJTextArea taMessages = null;
    private AKJDateComponent dcKuendDate = null;

    /**
     * @param wizardComponents
     */
    public LeistungDiffNotPossibleWizardPanel(AKJWizardComponents wizardComponents) {
        super(RESOURCE, wizardComponents);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTitle = getSwingFactory().createLabel("title", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblKuendTitle = getSwingFactory().createLabel("kuend.title", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblKuendDate = getSwingFactory().createLabel("kuend.datum");

        taMessages = getSwingFactory().createTextArea("diff.messages", false, true, true);
        AKJScrollPane spMessages = new AKJScrollPane(taMessages, new Dimension(300, 200));
        dcKuendDate = getSwingFactory().createDateComponent("kuend.datum");

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblTitle, GBCFactory.createGBC(100, 0, 1, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        child.add(spMessages, GBCFactory.createGBC(100, 100, 1, 2, 4, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.NONE));
        child.add(lblKuendTitle, GBCFactory.createGBC(100, 0, 1, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblKuendDate, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.NONE));
        child.add(dcKuendDate, GBCFactory.createGBC(25, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 6, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            setWaitCursor();
            auftragsMonitor = (AuftragsMonitor) getWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR);
            Object tmp = getWizardObject(WIZARD_OBJECT_LEISTUNGS_DIFF_CHECK);
            if (tmp instanceof LeistungsDiffCheck) {
                LeistungsDiffCheck check = (LeistungsDiffCheck) tmp;
                taMessages.setText(check.getMessagesAsString());
                dcKuendDate.setDate(check.getLastChangeDate());
            }
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
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#finish()
     */
    @Override
    public void finish() throws AKJWizardFinishVetoException {
        try {
            setWaitCursor();
            Date kuendDate = dcKuendDate.getDate(null);
            if (kuendDate == null) {
                MessageHelper.showInfoDialog(this, "Bitte definieren Sie ein Kuendigungsdatum.", null, true);
                throw new AKJWizardFinishVetoException("kein Kuendigungsdatum!");
            }

            cancelOrderAndPhysic(auftragsMonitor.getAuftragId(), kuendDate);

            // Nachfrage, ob neuer Auftrag angelegt werden soll
            int selection = MessageHelper.showConfirmDialog(this, getSwingFactory().getText("create.new.auftrag"),
                    getSwingFactory().getText("create.new.auftrag.title"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (selection == JOptionPane.YES_OPTION) {
                getWizardComponents().addWizardObject(WIZARD_OBJECT_CC_AUFTRAG_TECHNIK, null);
                getWizardComponents().addWizardObject(WIZARD_OBJECT_CC_AUFTRAG_DATEN, null);

                getWizardComponents().addWizardPanel(new AuftragAnlegenWizardPanel(getWizardComponents()));
                super.goNext();

                throw new AKJWizardFinishVetoException("create new...");
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

}


