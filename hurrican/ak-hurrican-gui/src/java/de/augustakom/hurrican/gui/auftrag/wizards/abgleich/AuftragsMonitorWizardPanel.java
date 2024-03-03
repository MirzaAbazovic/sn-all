/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 09:37:49
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

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel fuer die Anzeige des AuftragMonitors (= Differenzen zwischen Hurrican und Taifun).
 *
 *
 */
public class AuftragsMonitorWizardPanel extends AbstractServiceWizardPanel implements AuftragWizardObjectNames,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(AuftragsMonitorWizardPanel.class);

    private AKJTable tbAuftragsMonitor = null;
    private AKTableModelXML<AuftragsMonitor> tbMdlAM = null;
    private List<AuftragsMonitor> amResult = null;
    private AuftragsMonitor amKuendigung4ProdChange = null;

    // Panels fuer Auftragskuendigung
    private KuendigungAuswahlWizardPanel kuendAuswahlWP = null;
    private KuendigungBestWizardPanel kuendBestWP = null;

    // Panels fuer Auftragsanlage
    private AuftragAnlegenWizardPanel anlegenWP = null;
    private ProductChangeWizardPanel prodChangeWP = null;

    // Panels fuer Leistungsabgleich
    private LeistungsSynchWizardPanel synchWP = null;

    private boolean p4kAdded = false;
    private boolean p4aAdded = false;
    private boolean p4syncAdded = false;

    private boolean multiOrderCreation = false;

    // Array mit den Physikaenderungstypen, die fuer einen automatischen
    // Produktwechsel freigegeben sind.
    private static final Long[] ALLOWED_AUTO_PRODUCT_CHANGES = new Long[] { PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME };

    /**
     * Default-Konstruktor
     *
     * @param wizardComponents
     */
    public AuftragsMonitorWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/abgleich/AuftragsMonitorWizardPanel.xml", wizardComponents);
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setNextButtonEnabled(false);
        tbMdlAM = new AKTableModelXML<AuftragsMonitor>(
                "de/augustakom/hurrican/gui/auftrag/wizards/abgleich/AuftragsMonitorTableModel.xml");
        tbAuftragsMonitor = new AKJTable(tbMdlAM, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftragsMonitor.attachSorter();
        tbAuftragsMonitor.fitTable(new int[] { 65, 65, 150, 110, 260, 60 });
        tbAuftragsMonitor.addMouseListener(new TableMouseListener());
        AKJScrollPane spTable = new AKJScrollPane(tbAuftragsMonitor);
        spTable.setPreferredSize(new Dimension(735, 230));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * Liest alle benoetigten Daten ein und uebergibt sie dem TableModel.
     *
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        setWaitCursor();
        final Long kNoOrig = (Long) getWizardObject(WIZARD_OBJECT_KUNDEN_NO);
        final Long taifunOrderNoOrig = (Long) getWizardObject(WIZARD_OBJECT_SELECTED_TAIFUN_ORDER);
        multiOrderCreation = (taifunOrderNoOrig != null) ? true : false;

        tbAuftragsMonitor.setSelectionMode(multiOrderCreation ? ListSelectionModel.SINGLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);

        final SwingWorker<List<AuftragsMonitor>, Void> worker = new SwingWorker<List<AuftragsMonitor>, Void>() {
            @Override
            protected List<AuftragsMonitor> doInBackground() throws Exception {
                CCAuftragService as = getCCService(CCAuftragService.class);
                return as.createAuftragMonitor(kNoOrig, taifunOrderNoOrig);
            }

            @Override
            protected void done() {
                try {
                    amResult = get();
                    tbMdlAM.setData(amResult);

                    if (CollectionTools.isEmpty(amResult)) {
                        MessageHelper.showMessageDialog(AuftragsMonitorWizardPanel.this,
                                "Keine Differenzen zwischen Taifun und Hurrican gefunden.",
                                "Differenzen", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(AuftragsMonitorWizardPanel.this, e);
                }
                finally {
                    if (multiOrderCreation) {
                        tbAuftragsMonitor.selectAll();
                        setNextButtonEnabled(true);
                    }

                    setDefaultCursor();
                }
            }


        };
        worker.execute();
    }

    @Override
    protected boolean goNext() {
        @SuppressWarnings("unchecked")
        AKMutableTableModel<AuftragsMonitor> tbMdl = (AKMutableTableModel<AuftragsMonitor>) tbAuftragsMonitor.getModel();
        int selectionCount = tbAuftragsMonitor.getSelectedRowCount();
        if (selectionCount == 1) {
            return addWizardPanel4SingleOrderSelection(tbMdl);
        }
        else if (selectionCount > 1) {
            return addWizardPanel4MultiOrderSelection(tbMdl);
        }
        else {
            String msg = getSwingFactory().getText("keine.selektion.msg");
            MessageHelper.showInfoDialog(this, msg, null, true);
        }

        return false;
    }

    private boolean addWizardPanel4SingleOrderSelection(AKMutableTableModel<AuftragsMonitor> tbMdl) {
        int selection = tbAuftragsMonitor.getSelectedRow();
        Object selObj = tbMdl.getDataAtRow(selection);
        if (selObj instanceof AuftragsMonitor) {
            AuftragsMonitor am = (AuftragsMonitor) selObj;
            addWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR, am);
            addWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR_LIST, null);

            switch (am.getAmAktion()) {
                case AuftragsMonitor.AM_AKTION_KUENDIGEN:
                    // WizardPanels fuer die Kuendigung zuordnen.
                    addPanels4Kuendigung();
                    return super.goNext();
                case AuftragsMonitor.AM_AKTION_ANLEGEN:
                    // WizardPanels fuer die Auftragsuebernahme zuordnen.
                    addPanels4Anlage(am);
                    return super.goNext();
                case AuftragsMonitor.AM_AKTION_LEISTUNGS_DIFF:
                    // WizardPanels fuer den Leistungsabgleich zuordnen.
                    addPanels4LeistungsDiff(am);
                    return super.goNext();
                default:
                    String msg = getSwingFactory().getText("aktion.unbekannt");
                    MessageHelper.showInfoDialog(this, msg, "Fehler", null, true);
            }
        }
        return false;
    }


    /*
     * Eine mehrfach-Auswahl bzw. Anlage von AuftragsMonitor Objekten darf nur erfolgen,
     * wenn folgende Bedingungen erfuellt sind: <br>
     * <ul>
     *  <li>ein spezieller Billing-Auftrag wird abgeglichen, nicht der ganze Kunde
     *  <li>alle AuftragsMonitor-Objekte besitzen die gleiche "Aktion" (z.B. ANLEGEN, KUENDIGEN)
     * </ul>
     */
    private boolean addWizardPanel4MultiOrderSelection(AKMutableTableModel<AuftragsMonitor> tbMdl) {
        try {
            List<AuftragsMonitor> selectedActions = new ArrayList<AuftragsMonitor>();
            int[] rows = tbAuftragsMonitor.getSelectedRows();
            for (int row : rows) {
                selectedActions.add(tbMdl.getDataAtRow(row));
            }

            checkAmAktionIsEqual(selectedActions);

            AuftragsMonitor amFirst = selectedActions.get(0);
            addWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR, null);
            addWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR_LIST, selectedActions);

            switch (amFirst.getAmAktion()) {
                case AuftragsMonitor.AM_AKTION_KUENDIGEN:
                    // WizardPanels fuer die Kuendigung zuordnen.
                    addPanels4Kuendigung();
                    return super.goNext();
                case AuftragsMonitor.AM_AKTION_ANLEGEN:
                    // WizardPanels fuer die Auftragsuebernahme zuordnen.
                    addPanels4Anlage(amFirst);
                    return super.goNext();
                default:
                    String msg = getSwingFactory().getText("aktion.unbekannt");
                    MessageHelper.showInfoDialog(this, msg, "Fehler", null, true);
            }
        }
        catch (HurricanGUIException e) {
            MessageHelper.showInfoDialog(this, e.getMessage(), "Fehler", null, true);
        }
        return false;
    }


    /*
     * Ueberprueft, ob alle selektierten AuftragsMonitor Objekte die gleiche Aktion besitzen.
     * Ist dies nicht der Fall wird eine Exception erzeugt.
     */
    private void checkAmAktionIsEqual(List<AuftragsMonitor> selectedActions) throws HurricanGUIException {
        Set<Integer> actions = new HashSet<Integer>();
        for (AuftragsMonitor am : selectedActions) {
            actions.add(am.getAmAktion());
        }

        if (actions.size() > 1) {
            throw new HurricanGUIException("Eine gleichzeitige Bearbeitung von unterschiedlichen Aktionen ist nicht möglich!");
        }
    }


    @Override
    public void update() {
        setFinishButtonEnabled(false);
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_BACKWARD) {
            removePanelsAfter(this);
        }
    }

    /* Fuegt die WizardPanels fuer den Kuendigungsablauf hinzu. */
    private void addPanels4Kuendigung() {
        if (!p4kAdded) {
            kuendAuswahlWP = new KuendigungAuswahlWizardPanel(getWizardComponents());
            kuendBestWP = new KuendigungBestWizardPanel(getWizardComponents());
            p4kAdded = true;
        }

        getWizardComponents().addWizardPanel(kuendAuswahlWP);
        getWizardComponents().addWizardPanel(kuendBestWP);
    }

    /* Fuegt die WizardPanels fuer die Auftrags-Neuanlage hinzu. */
    private void addPanels4Anlage(AuftragsMonitor auftragsMonitor) {
        if (!p4aAdded) {
            anlegenWP = new AuftragAnlegenWizardPanel(getWizardComponents());
            p4aAdded = true;
        }

        if (!multiOrderCreation) {  // Produktwechsel bei Abgleich mehrerer Auftraege nicht erlaubt!
            Produkt produkt = null;
            try {
                ProduktService ps = getCCService(ProduktService.class);
                produkt = ps.findProdukt(auftragsMonitor.getCcProduktId());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

            if ((produkt != null) && BooleanTools.nullToFalse(produkt.getAutoProductChange())) {
                // pruefen, ob es sich um einen Produktwechsel handelt (und ob dieser von Hurrican unterstuetzt wird)
                // wenn ja, entsprechendes WizardPanel erzeugen und hinzufuegen
                // Dieses Panel muss immer neu erzeugt werden, damit die richtigen Optionen dargestellt werden.
                prodChangeWP = null;
                List<Produkt2Produkt> p2ps = getPossibleProductChanges(auftragsMonitor);
                if (CollectionTools.isNotEmpty(p2ps)) {
                    getWizardComponents().addWizardObject(WIZARD_OBJECT_POSSIBLE_PRODUCTCHANGES, p2ps);
                    getWizardComponents().addWizardObject(
                            WIZARD_OBJECT_AM_KUENDIUNG_4_PROD_CHANGE, amKuendigung4ProdChange);
                    prodChangeWP = new ProductChangeWizardPanel(getWizardComponents());
                }
            }

            if (prodChangeWP != null) {
                getWizardComponents().addWizardPanel(prodChangeWP);
            }
        }

        getWizardComponents().addWizardPanel(anlegenWP);
    }

    /* Fuegt die WizardPanels fuer den Leistungsabgleich hinzu. */
    private void addPanels4LeistungsDiff(AuftragsMonitor auftragsMonitor) {
        if (!p4syncAdded) {
            synchWP = new LeistungsSynchWizardPanel(getWizardComponents());
            p4syncAdded = true;
        }

        getWizardComponents().addWizardPanel(synchWP);
    }

    /*
     * Ueberprueft, ob zu dem angegebenen Auftragsmonitor ein Produktwechsel
     * ueber den Wizard durchgefuehrt werden kann. <br>
     * Dies ist dann der Fall, wenn folgende Faelle erfuellt sind:
     * <ul>
     *  <li>AuftragsMonitor besitzt eine 'alte' Auftragsnummer
     *  <li>die Differenz der anzulegenden Auftraege ist '1'
     *  <li>zu der 'alten' Auftragsnummer ist eine Kuendigung in der aktuellen
     *      Liste der Auftragsdifferenzen vorhanden
     *  <li>es existiert eine Prod-2-Prod Konfiguration vom alten auf das neue Produkt
     * </ul>
     */
    private List<Produkt2Produkt> getPossibleProductChanges(AuftragsMonitor am) {
        amKuendigung4ProdChange = null;
        if ((am.getOldAuftragNoOrig() == null) || (am.getDifferenz() != 1)) {
            return null;
        }

        for (AuftragsMonitor amTmp : amResult) {
            try {
                if (amTmp.isKuendigung() && NumberTools.equal(amTmp.getAuftragNoOrig(), NumberTools.convertString2Long(am.getOldAuftragNoOrig()))) {
                    // pruefen, ob Prod-2-Prod fuer die Kombination eingetragen ist
                    ProduktService ps = getCCService(ProduktService.class);
                    List<Produkt2Produkt> p2ps = ps.findProdukt2Produkt(amTmp.getCcProduktId(), am.getCcProduktId());
                    amKuendigung4ProdChange = amTmp;

                    // Liste der Prod-2-Prod auf die fuer den Auto-Produktwechsel eingeschraenkten
                    // Typen filtern
                    CollectionUtils.filter(p2ps, new Predicate() {
                        @Override
                        public boolean evaluate(Object obj) {
                            Produkt2Produkt prod2prod = (Produkt2Produkt) obj;
                            for (Long allowed : ALLOWED_AUTO_PRODUCT_CHANGES) {
                                if (NumberTools.equal(prod2prod.getPhysikaenderungsTyp(), allowed)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    return p2ps;
                }
                break;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return null;
    }

    class TableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (tbAuftragsMonitor.getSelectedRow() >= 0) {
                setNextButtonEnabled(true);
            }
            else {
                setNextButtonEnabled(false);
            }
        }
    }

}


