/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 10:34:33
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * WizardPanel, um die Auftraege anzuzeigen, die gekuendigt werden koennen.
 *
 *
 */
public class KuendigungAuswahlWizardPanel extends AbstractServiceWizardPanel implements AuftragWizardObjectNames,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(KuendigungAuswahlWizardPanel.class);

    private AuftragsMonitor auftragsMonitor = null;
    private List<AuftragsMonitor> auftragsMonitorList = null;
    private int selectionCount = 0;

    private AKJTable tbKuendigung = null;
    private KuendigungsTableModel tbMdlKuendigung = null;
    private AKJDateComponent dcKuendigung = null;

    private CCAuftragService auftragService;

    /**
     * Konstruktor
     *
     * @param wizardComponents
     */
    public KuendigungAuswahlWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/abgleich/KuendigungAuswahlWizardPanel.xml", wizardComponents);
        initServices();
        createGUI();
    }

    private void initServices() {
        try {
            auftragService = getCCService(CCAuftragService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    String.format("Services konnten nicht initialisiert werden!%n%s",
                            ExceptionUtils.getFullStackTrace(e))
            );
            cancel();
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblTitle = getSwingFactory().createLabel("title");
        AKJLabel lblKuendigung = getSwingFactory().createLabel("datum.kuendigung");
        dcKuendigung = getSwingFactory().createDateComponent("datum.kuendigung");
        AKJPanel datePanel = new AKJPanel(new GridBagLayout());
        datePanel.add(lblTitle, GBCFactory.createGBC(0, 0, 1, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        datePanel.add(lblKuendigung, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        datePanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        datePanel.add(dcKuendigung, GBCFactory.createGBC(10, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 10, 5)));
        datePanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlKuendigung = new KuendigungsTableModel();
        tbMdlKuendigung.setParent(this);
        tbKuendigung = new AKJTable(tbMdlKuendigung, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbKuendigung.fitTable(new int[] { 50, 80, 100, 100, 150 });
        AKJScrollPane spTable = new AKJScrollPane(tbKuendigung);
        spTable.setPreferredSize(new Dimension(505, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(datePanel, BorderLayout.NORTH);
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            selectionCount = 0;
            dcKuendigung.setDate(null);
            loadData();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadWizardObjects() throws HurricanGUIException {
        auftragsMonitor = (AuftragsMonitor) getWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR);
        auftragsMonitorList = (List<AuftragsMonitor>) getWizardObject(WIZARD_SELECTED_AUFTRAGS_MONITOR_LIST);

        if ((auftragsMonitor == null) && CollectionTools.isEmpty(auftragsMonitorList)) {
            throw new HurricanGUIException("Es ist kein Objekt fuer den Abgleich definiert!");
        }
        else if ((auftragsMonitor == null) && CollectionTools.isNotEmpty(auftragsMonitorList)) {
            auftragsMonitor = auftragsMonitorList.get(0);
        }
    }

    /**
     * Liest die Auftraege ein, die gekuendigt werden koennen.
     *
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            tbMdlKuendigung.removeAll();
            loadWizardObjects();

            AuftragDatenQuery query = new AuftragDatenQuery();
            List<AuftragDatenView> result = null;
            int diffCount = getAuftragsMonitorDiffCount();
            if (diffCount == 1) {
                query.setAuftragNoOrig(auftragsMonitor.getAuftragNoOrig());
                query.setProdId(auftragsMonitor.getCcProduktId());
                query.setAuftragStatusMin(AuftragStatus.IN_BETRIEB);
                query.setAuftragStatusMax(AuftragStatus.KUENDIGUNG);
            }
            else if (diffCount > 1) {
                query.setAuftragNoOrig(auftragsMonitor.getAuftragNoOrig());
                query.setAuftragStatusMax(AuftragStatus.KUENDIGUNG);
            }

            result = auftragService.findAuftragDatenViews(query, false);
            if (CollectionTools.isEmpty(result)) {
                result = null;
                String msg = getSwingFactory().getText("anzahl.nicht.ok");
                MessageHelper.showInfoDialog(this, msg, "Fehler", null, true);
            }

            defineTerminationDateFromBundle();

            if (result != null) {
                for (AuftragDatenView value : result) {
                    SelectionModel sm = new SelectionModel();
                    sm.model = value;
                    tbMdlKuendigung.addObject(sm);
                }
                selectOrdersToTerminate();
                setNextButtonEnabled(true);
            }
            else {
                setNextButtonEnabled(false);
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

    /* Kuendigungsdatum aus Buendel-Auftrag eintragen - falls vorhanden und in Zukunft */
    private void defineTerminationDateFromBundle() throws FindException {
        if ((auftragsMonitor.getBundleOrderNo() != null) && (auftragsMonitor.getBundleOrderNo().intValue() > 0)) {
            List<AuftragDaten> ads = auftragService.findAuftragDaten4Buendel(
                    auftragsMonitor.getBundleOrderNo(), auftragsMonitor.getBundleNoHerkunft());
            if (ads != null) {
                Date now = new Date();
                for (AuftragDaten ad : ads) {
                    if ((ad.getKuendigung() != null) && DateTools.isDateAfter(ad.getKuendigung(), now)) {
                        dcKuendigung.setDate(ad.getKuendigung());
                        MessageHelper.showMessageDialog(this,
                                "Kündigungsdatum wurde von Bündel-Auftrag übernommen.\nBitte kontrollieren.",
                                "Kündigungsdatum kontrollieren", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            }
        }
    }

    /*
     * Ermittelt die Anzahl der Differenz aus dem AuftragsMonitor.
     * Sofern eine Liste von AuftragsMonitor Objekten existiert wird die Summe der Differenz aus allen
     * AuftragsMonitor Objekten gebildet.
     * Sonderfall: <br>
     *  - Hurrican besitzt 2 ISDN TK Auftraege zu einer Taifun Auftragsnummer
     *  --> 2 AuftragsMonitor Objekte mit jeweils Anzahl 2
     * Dieser Fall wird extra geprueft und resultiert dann nicht in der Anzahl 4, sondern in Anzahl 2.
     */
    private int getAuftragsMonitorDiffCount() {
        if (CollectionTools.isNotEmpty(auftragsMonitorList)) {
            Set<Pair<Long, Long>> countedCombinationOfOrderNoAndProdId = new HashSet<Pair<Long, Long>>();

            int diffCount = 0;
            for (AuftragsMonitor am : auftragsMonitorList) {
                Pair<Long, Long> combination = Pair.create(am.getAuftragNoOrig(), am.getCcProduktId());
                if (!countedCombinationOfOrderNoAndProdId.contains(combination)) {
                    diffCount += am.getDifferenz();
                    countedCombinationOfOrderNoAndProdId.add(combination);
                }
            }
            return diffCount;
        }
        return auftragsMonitor.getDifferenz();
    }


    @Override
    protected boolean goNext() {
        Date datumKuendigung = dcKuendigung.getDate(null);
        Date now = new Date();
        if ((datumKuendigung == null) || DateTools.isDateBefore(datumKuendigung, now)) {
            String msg = getSwingFactory().getText("datum.ungueltig");
            MessageHelper.showInfoDialog(this, msg,
                    new Object[] { DateTools.formatDate(now, DateTools.PATTERN_DAY_MONTH_YEAR) }, true);
            return false;
        }

        List<Long> selection = new ArrayList<Long>();
        for (int i = 0; i < tbMdlKuendigung.getRowCount(); i++) {
            Object o = tbMdlKuendigung.getDataAtRow(i);
            if ((o instanceof SelectionModel) && ((SelectionModel) o).selected.booleanValue()) {
                SelectionModel sm = (SelectionModel) o;
                selection.add(sm.model.getAuftragId());
            }
        }

        int diffCount = getAuftragsMonitorDiffCount();
        if (selection.size() > diffCount) {
            String msg = getSwingFactory().getText("anzahl.ungueltig");
            MessageHelper.showInfoDialog(this, msg, new Object[] { Integer.valueOf(auftragsMonitor.getDifferenz()) }, true);
            return false;
        }
        else if (selection.isEmpty()) {
            String msg = getSwingFactory().getText("keine.selektion");
            MessageHelper.showInfoDialog(this, msg, new Object[] { Integer.valueOf(auftragsMonitor.getDifferenz()) }, true);
            return false;
        }

        // Selektion und Datum speichern
        addWizardObject(WIZARD_KUENDIGUNG_DATUM, datumKuendigung);
        addWizardObject(WIZARD_AUFTRAG_IDS_4_KUENDIGUNG, selection);
        return super.goNext();
    }


    /*
     * Selektiert alle dargestellten Auftraege zur Kuendigung.
     * Bedingung: die Summe der Diff-Counts entspricht der Anzahl der dargestellten Auftraege.
     */
    private void selectOrdersToTerminate() {
        if (getAuftragsMonitorDiffCount() == tbKuendigung.getRowCount()) {
            for (int i = 0; i < tbKuendigung.getRowCount(); i++) {
                tbMdlKuendigung.setValueAt(Boolean.TRUE, i, KuendigungsTableModel.COL_SELECTED);
            }
        }
    }


    /* Hilfsmodell fuer die Auswahl der zu kuendigenden Auftraege. */
    static class SelectionModel {
        Boolean selected = Boolean.FALSE;
        AuftragDatenView model = null;
    }

    /* TableModel fuer die Darstellung der Auftraege, die fuer die Kuendigung zur Auswahl stehen. */
    class KuendigungsTableModel extends AKTableModel<SelectionModel> {
        private static final int COL_SELECTED = 0;
        private static final int COL_AUFTRAG__NO = 1;
        private static final int COL_VBZ = 2;
        private static final int COL_INBETRIEBNAHME = 3;
        private static final int COL_ANSCHLUSSART = 4;

        private static final int COL_COUNT = 5;

        private Component parent = null;

        void setParent(Component parent) {
            this.parent = parent;
        }

        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_SELECTED:
                    return "Kündigen";
                case COL_AUFTRAG__NO:
                    return "Aufrag-Nr (CC)";
                case COL_VBZ:
                    return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
                case COL_INBETRIEBNAHME:
                    return "Inbetriebnahme";
                case COL_ANSCHLUSSART:
                    return "Anschlussart";
                default:
                    return null;
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof SelectionModel) {
                SelectionModel sm = (SelectionModel) o;
                switch (column) {
                    case COL_SELECTED:
                        return sm.selected;
                    case COL_AUFTRAG__NO:
                        return sm.model.getAuftragId();
                    case COL_VBZ:
                        return sm.model.getVbz();
                    case COL_INBETRIEBNAHME:
                        return sm.model.getInbetriebnahme();
                    case COL_ANSCHLUSSART:
                        return sm.model.getAnschlussart();
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof SelectionModel) {
                SelectionModel sm = (SelectionModel) o;
                if (column == COL_SELECTED) {
                    Boolean b = (aValue instanceof Boolean) ? (Boolean) aValue : Boolean.FALSE;
                    if (b.booleanValue()) {
                        int diffCount = getAuftragsMonitorDiffCount();
                        if (selectionCount < diffCount) {
                            selectionCount++;
                            sm.selected = b;
                        }
                        else {
                            String msg = getSwingFactory().getText("selektion.nicht.moeglich");
                            MessageHelper.showInfoDialog(parent, msg,
                                    new Object[] { Integer.valueOf(diffCount) }, true);
                        }
                    }
                    else {
                        selectionCount--;
                        sm.selected = b;
                    }
                }
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == COL_SELECTED) ? true : false;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_SELECTED:
                    return Boolean.class;
                case COL_AUFTRAG__NO:
                    return Long.class;
                case COL_INBETRIEBNAHME:
                    return Date.class;
                default:
                    return String.class;
            }
        }
    }
}


