/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2005 15:11:18
 */
package de.augustakom.hurrican.gui.tools.consistency;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.consistence.HistoryConsistence;
import de.augustakom.hurrican.service.cc.ConsistenceCheckService;


/**
 * Panel fuer den History-KonsistenzCheck (prueft, ob fuer Auftraege/Rangierungen jeweils nur ein aktiver Datensatz
 * existiert).
 *
 *
 */
public class HistoryConsistenceCheckPanel extends AbstractConsistenceCheckPanel<List<HistoryConsistence>, Void> {

    private static final Logger LOGGER = Logger.getLogger(HistoryConsistenceCheckPanel.class);

    private AKReflectionTableModel<HistoryConsistence> tbMdlResult = null;

    private AKJCheckBox chbAuftragDaten = null;
    private AKJCheckBox chbAuftragTechnik = null;
    private AKJCheckBox chbRangierung = null;
    private AKJCheckBox chbIntAccount = null;
    private AKJCheckBox chbEsLtgDaten = null;
    private AKJCheckBox chbEsAnsp = null;

    public HistoryConsistenceCheckPanel() {
        super("de/augustakom/hurrican/gui/tools/consistency/resources/HistoryConsistenceCheckPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        chbAuftragDaten = getSwingFactory().createCheckBox("auftrag.daten", null, true);
        chbAuftragTechnik = getSwingFactory().createCheckBox("auftrag.technik", null, true);
        chbRangierung = getSwingFactory().createCheckBox("rangierung", null, true);
        chbIntAccount = getSwingFactory().createCheckBox("int.account", null, true);
        chbEsLtgDaten = getSwingFactory().createCheckBox("es.ltg.daten", null, true);
        chbEsAnsp = getSwingFactory().createCheckBox("es.ansprechpartner", null, true);

        AKJPanel north = new AKJPanel(new GridBagLayout());
        north.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.title")));
        north.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        north.add(chbAuftragDaten, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        north.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        north.add(chbIntAccount, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        north.add(chbEsAnsp, GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(chbAuftragTechnik, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(chbRangierung, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(chbEsLtgDaten, GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 8, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlResult = new AKReflectionTableModel<HistoryConsistence>(
                new String[] { "ID", "ID-Name", "Tabelle", "Anzahl", "Hinweis" },
                new String[] { "id", "idType", "table", "anzahl", "hinweis" },
                new Class[] { String.class, String.class, String.class, String.class, String.class });
        AKJTable tbResult = new AKJTable(tbMdlResult, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbResult.attachSorter();
        tbResult.fitTable(new int[] { 90, 120, 150, 80, 200 });

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(north, BorderLayout.NORTH);
        getChildPanel().add(new AKJScrollPane(tbResult), BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.tools.consistency.AbstractConsistenceCheckPanel#executeCheckConsistence()
     */
    @Override
    public void executeCheckConsistence() {
        swingWorker = new SwingWorker<List<HistoryConsistence>, Void>() {

            // Initialize required data from panel in the constructor since this should be done in the EDT thread.
            final boolean checkAuftragDaten = chbAuftragDaten.isSelected();
            final boolean checkAuftragTechnik = chbAuftragTechnik.isSelected();
            final boolean checkIntAccount = chbIntAccount.isSelected();
            final boolean checkRangierung = chbRangierung.isSelected();
            final boolean checkEsLtgDaten = chbEsLtgDaten.isSelected();
            final boolean checkEsAnsp = chbEsAnsp.isSelected();

            @Override
            protected List<HistoryConsistence> doInBackground() throws Exception {
                ConsistenceCheckService ccs = getCCService(ConsistenceCheckService.class);

                List<HistoryConsistence> result = new LinkedList<HistoryConsistence>();
                if (checkAuftragDaten) {
                    result.addAll(ccs.checkHistoryConsistence(AuftragDaten.class));
                }
                if (checkAuftragTechnik) {
                    result.addAll(ccs.checkHistoryConsistence(AuftragTechnik.class));
                }
                if (checkIntAccount) {
                    result.addAll(ccs.checkHistoryConsistence(IntAccount.class));
                }
                if (checkRangierung) {
                    result.addAll(ccs.checkHistoryConsistence(Rangierung.class));
                }
                if (checkEsLtgDaten) {
                    result.addAll(ccs.checkHistoryConsistence(EndstelleLtgDaten.class));
                }
                if (checkEsAnsp) {
                    result.addAll(ccs.checkHistoryConsistence(EndstelleAnsprechpartner.class));
                }
                return result;
            }

            @Override
            protected void done() {
                try {
                    tbMdlResult.setData(get());
                }
                catch (CancellationException e) {
                    LOGGER.info(e.getMessage(), e);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableGuiButtonsForSwingWorkerInProgress(false);
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };

        setWaitCursor();
        showProgressBar("History-Check...");
        enableGuiButtonsForSwingWorkerInProgress(true);
        tbMdlResult.setData(null);
        swingWorker.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


