/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 10:56:11
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.AbstractKuendigungWizardPanel;


/**
 * WizardPanel fuer die Bestaetigung, dass die ausgewaehlten Auftraege zum angegebenen Termin gekuendigt werden sollen.
 *
 *
 */
public class KuendigungBestWizardPanel extends AbstractKuendigungWizardPanel implements AuftragWizardObjectNames {

    private static final Logger LOGGER = Logger.getLogger(KuendigungBestWizardPanel.class);

    private DefaultListModel<Long> lsMdlAuftraege = null;
    private AKJLabel lblTitle = null;

    private Date kuendigungsTermin = null;
    private List<Long> auftragIds = null;

    /**
     * Konstruktor.
     *
     * @param wizardComponents
     */
    public KuendigungBestWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/abgleich/KuendigungsBestWizardPanel.xml", wizardComponents);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setNextButtonEnabled(false);
        setFinishButtonEnabled(true);

        lblTitle = getSwingFactory().createLabel("title");

        lsMdlAuftraege = new DefaultListModel<Long>();
        AKJList lsAuftraege = getSwingFactory().createList("auftraege", lsMdlAuftraege);
        AKJScrollPane spList = new AKJScrollPane(lsAuftraege);
        spList.setPreferredSize(new Dimension(200, 150));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblTitle, GBCFactory.createGBC(100, 0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spList, GBCFactory.createGBC(50, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(50, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            lsMdlAuftraege.removeAllElements();
            lblTitle.setText("");

            if (getWizardObject(WIZARD_KUENDIGUNG_DATUM) instanceof Date) {
                kuendigungsTermin = (Date) getWizardObject(WIZARD_KUENDIGUNG_DATUM);
                lblTitle.setText(StringTools.formatString(getSwingFactory().getText("title.text"),
                        new Object[] { DateTools.formatDate(kuendigungsTermin, DateTools.PATTERN_DAY_MONTH_YEAR) }, null));
            }

            if (getWizardObject(WIZARD_AUFTRAG_IDS_4_KUENDIGUNG) instanceof List) {
                auftragIds = (List<Long>) getWizardObject(WIZARD_AUFTRAG_IDS_4_KUENDIGUNG);
                for (Long o : auftragIds) {
                    lsMdlAuftraege.addElement(o);
                }
            }
        }
    }

    @Override
    public void finish() {
        if (cancelPhysic(auftragIds)) {
            cancelOrdersAndPhysics();
        }
        else {
            Long auftragId = null;
            boolean open = false;

            try {
                setWaitCursor();
                setFinishButtonEnabled(false);
                open = (lsMdlAuftraege.getSize() == 1) ? true : false;

                for (int i = 0; i < lsMdlAuftraege.getSize(); i++) {
                    auftragId = lsMdlAuftraege.get(i);
                    cancelOrder(auftragId, kuendigungsTermin);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setFinishButtonEnabled(true);
                setDefaultCursor();
                if (open) {
                    openOrder(auftragId);
                }
            }
        }
    }

    /* Kuendigt die Physiken aller angezeigten Auftraege. */
    private void cancelOrdersAndPhysics() {
        Long auftragId = null;
        boolean open = false;
        try {
            setWaitCursor();
            open = (lsMdlAuftraege.getSize() == 1) ? true : false;
            for (int i = 0; i < lsMdlAuftraege.getSize(); i++) {
                auftragId = lsMdlAuftraege.get(i);
                cancelOrderAndPhysic(auftragId, kuendigungsTermin);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Exception ex = new Exception("Der Auftrag " + auftragId + " wurde nicht gekuendigt! Grund:\n" +
                    e.getMessage(), e);
            MessageHelper.showErrorDialog(this, ex);
        }
        finally {
            setDefaultCursor();
            if (open) {
                openOrder(auftragId);
            }
        }
    }

}
