/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2007 13:23:45
 */
package de.augustakom.hurrican.gui.tools.tal;

import static com.google.common.collect.Collections2.*;
import static com.google.common.collect.Lists.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.math.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import javax.swing.*;
import javax.swing.table.*;
import com.google.common.base.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SpringLayoutUtilities;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.tal.CBUsecase;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.exmodules.tal.TALService;
import de.augustakom.hurrican.service.exmodules.tal.utils.TALServiceFinder;
import de.mnet.wita.model.WitaCBVorgang;


/**
 * Dialog zur Darstellung aller el. CB-Vorgaenge zu einer bestimmten Carrierbestellung. <br> Die Darstellung der
 * Vorgaenge erfolgt in absteigender Reihenfolge. Das bedeutet, der aktuellste Vorgang wird zuerst dargestellt.
 *
 *
 */
public class CBVorgangHistoryDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKNavigationBarListener {

    private static final long serialVersionUID = 4822784700660917661L;
    private static final Logger LOGGER = Logger.getLogger(CBVorgangHistoryDialog.class);
    private static final String TAL_SEG_PROPERTIES = "de/augustakom/hurrican/gui/tools/tal/resources/segment.properties";
    private static final Properties talSegmentProperties = SystemPropertyTools.instance().getProperties(CBVorgangHistoryDialog.TAL_SEG_PROPERTIES);

    private Long carrierbestellungId = null;

    // Map für die Segmente der Carrierbestellung
    private final SortedMap<Number, SortedMap<Number, SortedMap<String, List<TALSegment>>>> allSegments = new TreeMap<>();

    // GUI-Komponenten
    private AKJNavigationBar navBar = null;

    private AKJFormattedTextField tfAuftragId = null;
    private AKJDateComponent dcSubmittedAt = null;
    private AKJTextField tfUser = null;
    private AKJDateComponent dcAnsweredAt = null;
    private AKReferenceField rfCarrier = null;
    private AKReferenceField rfTyp = null;
    private AKReferenceField rfDTAGUsecase = null;
    private AKReferenceField rfStatus = null;
    private AKJTextField tfBezMnet = null;
    private AKJTextField tfDTAGRefNr = null;
    private AKJDateComponent dcVorgabeMnet = null;
    private AKJDateComponent dcRetRealDate = null;
    private AKJComboBox cbRetOk = null;
    private AKReferenceField rfRetTyp = null;
    private AKJTextField tfRetLbz = null;
    private AKJTextField tfRetVtrnr = null;
    private AKJTextField tfRetLL = null;
    private AKJTextField tfRetAQS = null;
    private AKJComboBox cbRetKVorOrt = null;
    private AKJTextArea taMontagehinweis = null;
    private AKJTextArea taRetBemerkung = null;
    private AKJTabbedPane tabPane = null;
    private AKJTabbedPane segmentTabbedPane = null;

    /**
     * Konstruktor mit Angabe der Carrierbestellungs-ID, deren elektronische Vorgaenge angezeigt werden sollen.
     */
    public CBVorgangHistoryDialog(Long carrierbestellungId) {
        super("de/augustakom/hurrican/gui/tools/tal/resources/CBVorgangHistoryDialog.xml");
        this.carrierbestellungId = carrierbestellungId;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, null, null, false, false);
        configureButton(CMD_CANCEL, getSwingFactory().getText("ok"), null, true, true);

        navBar = getSwingFactory().createNavigationBar("vorgaenge", this, true, true);

        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblSubmittedAt = getSwingFactory().createLabel("submitted.at");
        AKJLabel lblUser = getSwingFactory().createLabel("bearbeiter.mnet");
        AKJLabel lblAnsweredAt = getSwingFactory().createLabel("answered.at");
        AKJLabel lblCarrier = getSwingFactory().createLabel("carrier");
        AKJLabel lblTyp = getSwingFactory().createLabel("typ");
        AKJLabel lblDTAGUsecase = getSwingFactory().createLabel("dtag.usecase");
        AKJLabel lblStatus = getSwingFactory().createLabel("status");
        AKJLabel lblBezMnet = getSwingFactory().createLabel("bezeichnung.mnet");
        AKJLabel lblDTAGRefNr = getSwingFactory().createLabel("dtag.ref.nr");
        AKJLabel lblVorgabeMnet = getSwingFactory().createLabel("vorgabe.mnet");
        AKJLabel lblRetRealDate = getSwingFactory().createLabel("ret.real.date");
        AKJLabel lblRetOk = getSwingFactory().createLabel("ret.ok");
        AKJLabel lblRetTyp = getSwingFactory().createLabel("ret.typ");
        AKJLabel lblRetLbz = getSwingFactory().createLabel("ret.lbz");
        AKJLabel lblRetVtrnr = getSwingFactory().createLabel("ret.vtrnr");
        AKJLabel lblRetLL = getSwingFactory().createLabel("ret.laenge");
        AKJLabel lblRetAQS = getSwingFactory().createLabel("ret.aqs");
        AKJLabel lblRetKVorOrt = getSwingFactory().createLabel("ret.kunde.vor.ort");

        AKJLabel lblMontagehinweis;
        lblMontagehinweis = getSwingFactory().createLabel("montagehinweis");

        AKJLabel lblRetBemerkung = getSwingFactory().createLabel("ret.bemerkung");

        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        dcSubmittedAt = getSwingFactory().createDateComponent("submitted.at", false);
        tfUser = getSwingFactory().createTextField("bearbeiter.mnet", false);
        dcAnsweredAt = getSwingFactory().createDateComponent("answered.at", false);
        rfCarrier = getSwingFactory().createReferenceField("carrier");
        rfCarrier.setEnabled(false);
        rfTyp = getSwingFactory().createReferenceField("typ");
        rfTyp.setEnabled(false);
        rfDTAGUsecase = getSwingFactory().createReferenceField("dtag.usecase");
        rfDTAGUsecase.setEnabled(false);
        rfStatus = getSwingFactory().createReferenceField("status");
        rfStatus.setEnabled(false);
        tfBezMnet = getSwingFactory().createTextField("bezeichnung.mnet", false);
        tfDTAGRefNr = getSwingFactory().createTextField("dtag.ref.nr", false);
        dcVorgabeMnet = getSwingFactory().createDateComponent("vorgabe.mnet", false);
        dcRetRealDate = getSwingFactory().createDateComponent("ret.real.date", false);
        cbRetOk = getSwingFactory().createComboBox("ret.ok", false);
        rfRetTyp = getSwingFactory().createReferenceField("ret.typ");
        rfRetTyp.setEnabled(false);
        tfRetLbz = getSwingFactory().createTextField("ret.lbz", false);
        tfRetVtrnr = getSwingFactory().createTextField("ret.vtrnr", false);
        tfRetLL = getSwingFactory().createTextField("ret.laenge", false);
        tfRetAQS = getSwingFactory().createTextField("ret.aqs", false);
        cbRetKVorOrt = getSwingFactory().createComboBox("ret.kunde.vor.ort", false);
        taMontagehinweis = getSwingFactory().createTextArea("montagehinweis", false);
        taRetBemerkung = getSwingFactory().createTextArea("ret.bemerkung", false);

        AKJPanel details = new AKJPanel(new SpringLayout());
        Component[] components = new Component[] { lblAuftragId, tfAuftragId, lblSubmittedAt, dcSubmittedAt,
                lblUser, tfUser, lblAnsweredAt, dcAnsweredAt, lblCarrier, rfCarrier,
                lblTyp, rfTyp, lblDTAGUsecase, rfDTAGUsecase, lblStatus, rfStatus,
                lblBezMnet, tfBezMnet, lblDTAGRefNr, tfDTAGRefNr,
                lblVorgabeMnet, dcVorgabeMnet, lblRetRealDate, dcRetRealDate,
                lblRetOk, cbRetOk, lblRetTyp, rfRetTyp, lblRetLbz, tfRetLbz, lblRetVtrnr, tfRetVtrnr,
                lblRetLL, tfRetLL, lblRetAQS, tfRetAQS, lblRetKVorOrt, cbRetKVorOrt,
                lblRetBemerkung, new AKJScrollPane(taRetBemerkung, new Dimension(150, 30)),
                lblMontagehinweis, new AKJScrollPane(taMontagehinweis, new Dimension(150, 30)) };
        SpringLayoutUtilities.makeGrid(details, components, true,
                components.length / 2, 2, 5, 5, 5, 5);

        Dimension scrollDim = new Dimension(340, 590);

        tabPane = new AKJTabbedPane();
        tabPane.addTab("Vorgang", new AKJScrollPane(details, scrollDim));

        AKJPanel navBarPanel = new AKJPanel();
        navBarPanel.setLayout(new BorderLayout());
        navBarPanel.add(navBar, BorderLayout.NORTH);
        navBarPanel.add(tabPane, BorderLayout.CENTER);

        getChildPanel().add(navBarPanel);
    }

    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfCarrier.setFindService(sfs);
            rfTyp.setFindService(sfs);
            rfStatus.setFindService(sfs);

            ISimpleFindService exSFS = (ISimpleFindService) TALServiceFinder.instance().getTALService(TALService.class);
            rfDTAGUsecase.setFindService(exSFS);
            rfRetTyp.setFindService(exSFS);

            CarrierElTALService talService = getCCService(CarrierElTALService.class);
            List<CBVorgang> vorgaenge = talService.findCBVorgaenge4CB(carrierbestellungId);
            // WitaCBVorgaenge nicht anzeigen
            vorgaenge = newArrayList(filter(vorgaenge, new Predicate<CBVorgang>() {
                @Override
                public boolean apply(CBVorgang input) {
                    return !(input instanceof WitaCBVorgang);
                }
            }));
            navBar.setData(vorgaenge);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        if (obj instanceof CBVorgang) {
            final CBVorgang cbv = (CBVorgang) obj;
            tfAuftragId.setValue(cbv.getAuftragId());
            dcSubmittedAt.setDate(cbv.getSubmittedAt());
            tfUser.setText(cbv.getBearbeiter() != null ? cbv.getBearbeiter().getLoginName() : null);
            dcAnsweredAt.setDate(cbv.getAnsweredAt());
            rfCarrier.setReferenceId(cbv.getCarrierId());
            rfTyp.setReferenceId(cbv.getTyp());
            rfStatus.setReferenceId(cbv.getStatus());
            tfBezMnet.setText(cbv.getBezeichnungMnet());
            tfDTAGRefNr.setText(cbv.getCarrierRefNr());
            dcVorgabeMnet.setDate(cbv.getVorgabeMnet());
            dcRetRealDate.setDate(cbv.getReturnRealDate());
            cbRetOk.selectItemWithValue(cbv.getReturnOk());
            rfRetTyp.setReferenceId(cbv.getExmRetFehlertyp());
            tfRetLbz.setText(cbv.getReturnLBZ());
            tfRetVtrnr.setText(cbv.getReturnVTRNR());
            tfRetLL.setText(cbv.getReturnLL());
            tfRetAQS.setText(cbv.getReturnAQS());
            cbRetKVorOrt.selectItemWithValue(cbv.getReturnKundeVorOrt());
            taMontagehinweis.setText(cbv.getMontagehinweis());
            taRetBemerkung.setText(cbv.getReturnBemerkung());
            if (cbv.getUsecaseId() != null) {
                try {
                    CarrierElTALService elTalS = getCCService(CarrierElTALService.class);
                    CBUsecase usecase = elTalS.findCBUsecase(cbv.getUsecaseId());
                    if (usecase != null) {
                        rfDTAGUsecase.setReferenceId(usecase.getExmTbvId());
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            if (null != cbv.getExmId()) {
                final SwingWorker<SortedMap<Number, SortedMap<String, List<TALSegment>>>, Void> worker
                        = new SwingWorker<SortedMap<Number, SortedMap<String, List<TALSegment>>>, Void>() {

                    // Initialize required data from dialog in the constructor since this should be done in the EDT thread.
                    final Long exmId = cbv.getExmId();

                    @Override
                    protected SortedMap<Number, SortedMap<String, List<TALSegment>>> doInBackground()
                            throws Exception {
                        TALService exmTals = (TALService) TALServiceFinder.instance().getTALService(TALService.class);
                        return exmTals.findAllSegmentsForTBSFirstId(exmId);
                    }

                    @Override
                    protected void done() {
                        try {
                            allSegments.put(exmId, get());
                            segmentTabbedPane = createTabbedPaneForSegments(getSegmentsForView(allSegments.get(exmId)));
                            tabPane.add("Segmente", segmentTabbedPane);
                        }
                        catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        finally {
                            stopProgressBar();
                            setDefaultCursor();
                        }
                    }
                };

                allSegments.clear();
                if ((tabPane.getComponentCount() > 1) && (getComponent(1) != null)) {
                    tabPane.remove(1);
                }
                setWaitCursor();
                showProgressBar("Lade Segmente...");
                worker.execute();
            }
        }
    }


    @Override
    protected void doSave() {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        // not used
    }

    /**
     * Erzeugt die TabbedPabe der Segmente
     *
     * @return TabbedPabe der Segmente
     */
    private AKJTabbedPane createTabbedPaneForSegments(Map<String, List<TALSegment>> segments) {
        Iterator<Entry<String, List<TALSegment>>> entrySetIterator = segments.entrySet().iterator();

        AKJTabbedPane segmentTabbedPane = new AKJTabbedPane();
        Dimension scrollDim = new Dimension(340, 590);
        int i = 0;

        while (entrySetIterator.hasNext()) {
            Entry<String, List<TALSegment>> entry = entrySetIterator.next();
            List<TALSegment> talSegments = entry.getValue();
            String segmentName = entry.getKey();

            TALSegmentTableModel talSegmentTableModel = new TALSegmentTableModel(
                    segmentName,
                    talSegments.get(0).getValues().size(),
                    talSegments,
                    allSegments.keySet().iterator().next());
            TALSegmentTable talSegmentTable = new TALSegmentTable(talSegmentTableModel);

            AKJScrollPane scrollPane = new AKJScrollPane(talSegmentTable, scrollDim);
            String segmentText = (String) talSegmentProperties.get(segmentName);
            segmentTabbedPane.add(segmentText, new AKJScrollPane(scrollPane, scrollDim));
            segmentTabbedPane.setToolTipTextAt(i, segmentName);
            i++;
        }
        return segmentTabbedPane;
    }

    /**
     * Ermittelt alle Segmente für die Anzeige
     */
    private Map<String, List<TALSegment>> getSegmentsForView(SortedMap<Number, SortedMap<String, List<TALSegment>>> map) {
        Iterator<Entry<Number, SortedMap<String, List<TALSegment>>>> entrySetIt = map.entrySet().iterator();
        Map<String, List<TALSegment>> segments = new TreeMap<>();

        while (entrySetIt.hasNext()) {
            Entry<Number, SortedMap<String, List<TALSegment>>> entry = entrySetIt.next();
            SortedMap<String, List<TALSegment>> talSegmentsMap = entry.getValue();

            for (Entry<String, List<TALSegment>> segmentEntry : talSegmentsMap.entrySet()) {
                List<TALSegment> value = segmentEntry.getValue();

                if ((null != value) && (!value.isEmpty())) {
                    List<TALSegment> talSegments = !segments.containsKey(
                            segmentEntry.getKey()) ? new Vector<>() : segments.get(segmentEntry.getKey());
                    talSegments.addAll(value);
                    segments.put(segmentEntry.getKey(), talSegments);
                }
            }
        }
        return segments;
    }

    /**
     * Tabellenmodell für TAL-Segment
     */
    static class TALSegmentTableModel extends AKTableModel<TALSegment> {

        private static final long serialVersionUID = 5644541120016342619L;
        private String identifier = null;
        private Number exmId = null;

        public TALSegmentTableModel(String identifier, int columnCount, Collection<TALSegment> data, Number exmId) {
            setData(data);
            setColumnCount(columnCount);
            this.identifier = identifier;
            this.exmId = exmId;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return getFieldName(column);
        }

        @Override
        public Object getValueAt(int row, int column) {
            TALSegment talSegment = getDataAtRow(row);
            if (talSegment != null) {
                Object columnValue = talSegment.getValues().get(column);

                if (column == 0) {
                    columnValue = ((BigDecimal) columnValue).longValue() == exmId.longValue() ? "Bestellung" : "Antwort";
                }
                return columnValue;
            }
            return null;
        }

        /**
         * Ermittelt in Abhängigkeit der Position der Spalte die Beschriftung aus den Properties
         */
        private String getFieldName(int column) {
            String fieldName = "NOT SET!!!";

            if (identifier != null) {
                fieldName = (String) talSegmentProperties.get(identifier + "_" + (column + 1));
            }
            return fieldName;
        }
    }

    /**
     * Tabelle für die TAL-Segmente
     */
    static class TALSegmentTable extends AKJTable {

        private static final long serialVersionUID = -8232175085003129687L;

        public TALSegmentTable(TableModel dm) {
            super(dm);
        }

        @Override
        protected JTableHeader createDefaultTableHeader() {
            return new JTableHeader(columnModel) {

                private static final long serialVersionUID = 3115262913236784273L;

                @Override
                public String getToolTipText(MouseEvent e) {
                    java.awt.Point p = e.getPoint();
                    int index = columnModel.getColumnIndexAtX(p.x);
                    int realIndex = columnModel.getColumn(index).getModelIndex();
                    TableColumn tableColumn = columnModel.getColumn(realIndex);
                    return (String) tableColumn.getHeaderValue();
                }
            };
        }
    }
}
