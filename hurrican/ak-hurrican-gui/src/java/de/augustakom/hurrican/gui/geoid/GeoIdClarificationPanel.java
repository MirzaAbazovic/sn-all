/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2011 15:34:30
 */

package de.augustakom.hurrican.gui.geoid;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModelListener;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.model.AbstractObservable;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.query.GeoIdClarificationQuery;
import de.augustakom.hurrican.model.cc.view.GeoIdClarificationView;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel fuer die Administration der Klärungsliste.
 */
public class GeoIdClarificationPanel extends AbstractServicePanel implements AKDataLoaderComponent, AKTableOwner, AKTableModelListener {

    private static final Logger LOGGER = Logger.getLogger(GeoIdClarificationPanel.class);

    private static final String DATE_FILTER_TO = "date.filter.to";
    private static final String DATE_FILTER_FROM = "date.filter.from";
    private static final String LST_FILTER_TYPES = "lst.filter.types";
    private static final String LST_FILTER_STATES = "lst.filter.states";
    private static final String TF_FILTER_GEOID = "tf.filter.geoid";
    private static final String CB_CHANGE_STATES = "cb.change.states";
    private static final String CMD_CHANGE_STATE = "btn.change.state";
    private static final String CMD_FILTER_CLARIFICATIONS = "btn.filter.clarifications";
    private static final long serialVersionUID = 4667454751744570301L;

    private AKTableModelXML<GeoIdClarificationView> tbMdlClarification = null;
    private AKJTable tbClarification = null;
    private Publisher publisher = null;

    private AKJComboBox cbChangeStates = null;
    private AKJButton btnChangeState = null;
    private AKJTextField tfFilterGeoId = null;
    private AKJList lstFilterStates = null;
    private AKJList lstFilterTypes = null;
    private AKJDateComponent dateFilterFrom = null;
    private AKJDateComponent dateFilterTo = null;
    private AKJButton btnFilterClarifications = null;

    public GeoIdClarificationPanel() {
        super("de/augustakom/hurrican/gui/geoid/resources/GeoIdClarificationPanel.xml");
        init();
        createGUI();
        loadData();
    }

    private void init() {
        publisher = new Publisher();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblChangeStates = getSwingFactory().createLabel(CB_CHANGE_STATES);
        AKJLabel lblFilterGeoId = getSwingFactory().createLabel(TF_FILTER_GEOID);
        AKJLabel lblFilterStates = getSwingFactory().createLabel(LST_FILTER_STATES);
        AKJLabel lblFilterTypes = getSwingFactory().createLabel(LST_FILTER_TYPES);
        AKJLabel lblFilterFrom = getSwingFactory().createLabel(DATE_FILTER_FROM);
        AKJLabel lblFilterTo = getSwingFactory().createLabel(DATE_FILTER_TO);

        cbChangeStates = getSwingFactory().createComboBox(CB_CHANGE_STATES,
                new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        cbChangeStates.setPreferredSize(new Dimension(110, 22));
        btnChangeState = getSwingFactory().createButton(CMD_CHANGE_STATE, getActionListener());

        tbMdlClarification = new AKTableModelXML<>(
                "de/augustakom/hurrican/gui/geoid/resources/GeoIdClarificationPanelTableModel.xml");
        tbClarification = new AKJTable(tbMdlClarification, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbClarification.attachSorter(true);
        tbClarification.addTableListener(this);
        tbClarification.fitTable(tbMdlClarification.getFitList());
        AKJScrollPane spTable = new AKJScrollPane(tbClarification, new Dimension(540, 200));

        tfFilterGeoId = getSwingFactory().createTextField(TF_FILTER_GEOID, true, true);
        ClearListSelectionKeyListener clearListSelectionKL = new ClearListSelectionKeyListener();
        lstFilterStates = getSwingFactory().createList(LST_FILTER_STATES);
        lstFilterStates.addKeyListener(clearListSelectionKL);
        lstFilterStates.setCellRenderer(new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        lstFilterTypes = getSwingFactory().createList(LST_FILTER_TYPES);
        lstFilterTypes.addKeyListener(clearListSelectionKL);
        lstFilterTypes.setCellRenderer(new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        dateFilterFrom = getSwingFactory().createDateComponent(DATE_FILTER_FROM, true);
        dateFilterFrom.setEditable(false);
        dateFilterTo = getSwingFactory().createDateComponent(DATE_FILTER_TO, true);
        dateFilterTo.setEditable(false);
        btnFilterClarifications = getSwingFactory().createButton(CMD_FILTER_CLARIFICATIONS, getActionListener());

        Dimension lstSize = new Dimension(140, 54);
        AKJScrollPane spFilterStates = new AKJScrollPane(lstFilterStates);
        spFilterStates.setPreferredSize(lstSize);
        AKJScrollPane spFilterTypes = new AKJScrollPane(lstFilterTypes);
        spFilterTypes.setPreferredSize(lstSize);

        AKJPanel tablePanel = new AKJPanel(new GridBagLayout());
        tablePanel.add(spTable, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel changeStatusPanel = new AKJPanel(new GridBagLayout());
        changeStatusPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        changeStatusPanel.add(lblChangeStates, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        changeStatusPanel.add(cbChangeStates, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(btnChangeState, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 2, 1, 1, GridBagConstraints.VERTICAL));
        changeStatusPanel.add(lblFilterGeoId, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(tfFilterGeoId, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(lblFilterStates, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(spFilterStates, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(lblFilterTypes, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(spFilterTypes, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(lblFilterFrom, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(dateFilterFrom, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(lblFilterTo, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(dateFilterTo, GBCFactory.createGBC(100, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        changeStatusPanel.add(btnFilterClarifications, GBCFactory.createGBC(100, 0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.table")));
        this.add(tablePanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(changeStatusPanel, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));

        AKManageableComponent[] managedComponents = new AKManageableComponent[] { cbChangeStates, btnChangeState };
        manageGUI(managedComponents);
    }

    @Override
    protected void execute(String command) {
        if (CMD_CHANGE_STATE.equals(command)) {
            changeState();
        }
        else if (CMD_FILTER_CLARIFICATIONS.equals(command)) {
            doSearch();
        }
    }

    private void changeState() {
        try {
            if ((tbClarification.getSelectedRow() != -1) && (cbChangeStates.getSelectedItem() != null)) {
                int selectedRow = tbClarification.getSelectedRow();
                @SuppressWarnings("unchecked")
                AKMutableTableModel<GeoIdClarificationView> tbMdl = (AKMutableTableModel<GeoIdClarificationView>) tbClarification.getModel();
                GeoIdClarificationView toSaveView = tbMdl.getDataAtRow(selectedRow);
                Object selectedItem = cbChangeStates.getSelectedItem();
                if ((toSaveView != null) && (selectedItem instanceof Reference)) {
                    AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                    GeoIdClarification toSave = availabilityService.findGeoIdClarificationById(toSaveView.getId());
                    if (toSave != null) {
                        toSaveView.setStatus((Reference) selectedItem);
                        toSave.setStatus((Reference) selectedItem);
                        availabilityService.saveGeoIdClarification(toSave, HurricanSystemRegistry.instance().getSessionId());
                        updateChangeStateUI(toSave.getStatus());
                        tbClarification.repaint();
                        tbMdlClarification.fireTableRowsUpdated(selectedRow, selectedRow);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void updateChangeStateUI(Reference current) {
        try {
            List<Reference> referenceList = new ArrayList<>();
            if ((current != null) && (current.getOrderNo() != null)) {
                ReferenceService referenceService = getCCService(ReferenceService.class);
                String type = Reference.REF_TYPE_GEOID_CLARIFICATION_STATUS;
                List<Reference> allStates = referenceService.findReferencesByType(type, Boolean.TRUE);
                // Alle stati deren OrderNo > aktuelle OrderNo
                if (CollectionTools.isNotEmpty(allStates)) {
                    for (Reference reference : allStates) {
                        if (NumberTools.isGreater(reference.getOrderNo(), current.getOrderNo())) {
                            referenceList.add(reference);
                        }
                    }
                }
                if (!referenceList.isEmpty()) {
                    // Nach OrderNo sortieren
                    Collections.sort(referenceList, new Comparator<Reference>() {
                        @Override
                        public int compare(Reference ref1, Reference ref2) {
                            if (NumberTools.equal(ref1.getOrderNo(), ref2.getOrderNo())) {
                                return 0;
                            }
                            else if ((ref1.getOrderNo() == null) || NumberTools.isLess(ref1.getOrderNo(), ref2.getOrderNo())) {
                                return -1;
                            }
                            return 1;
                        }
                    });
                }
            }
            if (!referenceList.isEmpty()) {
                // Liste bekommt mindestens einen Eintrag -> UI aktivieren
                cbChangeStates.removeAllItems();
                cbChangeStates.addItems(referenceList);
                cbChangeStates.setEnabled(true);
                btnChangeState.setEnabled(true);
            }
            else {
                // Liste löschen -> UI deaktivieren
                cbChangeStates.removeAllItems();
                cbChangeStates.setEnabled(false);
                btnChangeState.setEnabled(false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

    }

    @Override
    public final void loadData() {
        try {
            if (btnChangeState.isComponentExecutable() &&
                    (lstFilterStates.getModel() == null || lstFilterStates.getModel().getSize() <= 0)) {
                ReferenceService referenceService = getCCService(ReferenceService.class);
                String type = Reference.REF_TYPE_GEOID_CLARIFICATION_STATUS;
                List<Reference> statusList = referenceService.findReferencesByType(type, Boolean.TRUE);
                lstFilterStates.addItems(statusList);
                type = Reference.REF_TYPE_GEOID_CLARIFICATION_TYPE;
                List<Reference> typeList = referenceService.findReferencesByType(type, Boolean.TRUE);
                lstFilterTypes.addItems(typeList);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void showDetails(Object details) {
        if (details instanceof GeoIdClarificationView) {
            GeoIdClarificationView row = (GeoIdClarificationView) details;
            updateChangeStateUI(row.getStatus());
            notifyObservers();
        }
        else {
            updateChangeStateUI(null);
            notifyObservers();
        }
    }

    private void doSearch() {
        final GeoIdClarificationQuery query = new GeoIdClarificationQuery();
        if (!StringUtils.isBlank(tfFilterGeoId.getText())) {
            query.setGeoId(tfFilterGeoId.getTextAsLong(null));
        }
        lstFilterStates.getSelectedValues(HVTStandort.class);
        query.setStatusList(lstFilterStates.getSelectedValues(Reference.class));
        query.setTypeList(lstFilterTypes.getSelectedValues(Reference.class));
        query.setFrom(dateFilterFrom.getDate(null));
        query.setTo(dateFilterTo.getDate(null));
        if ((query.getFrom() != null) && (query.getTo() == null)) {
            query.setTo(new Date());
        }

        setWaitCursor();
        showProgressBar("suchen...");
        btnFilterClarifications.setEnabled(false);

        final SwingWorker<List<GeoIdClarificationView>, Void> worker = doSearchInWorker(query);
        worker.execute();
    }

    private SwingWorker<List<GeoIdClarificationView>, Void> doSearchInWorker(final GeoIdClarificationQuery query) {
        return new SwingWorker<List<GeoIdClarificationView>, Void>() {
            final GeoIdClarificationQuery localQuery = query;

            @Override
            protected List<GeoIdClarificationView> doInBackground() throws Exception {
                AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                return availabilityService.findGeoIdClarificationViewsByQuery(localQuery);
            }

            @Override
            protected void done() {
                try {
                    List<GeoIdClarificationView> results = get();
                    tbMdlClarification.setData(results);
                    updateChangeStateUI(null);
                    notifyObservers();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    btnFilterClarifications.setEnabled(true);
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };
    }

    public void addObserver(Observer observer) {
        publisher.addObserver(observer);
    }

    public void notifyObservers() {
        if (tbClarification.getSelectedRow() != -1) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<GeoIdClarificationView> tbMdl = (AKMutableTableModel<GeoIdClarificationView>) tbClarification.getModel();
            GeoIdClarificationView row = tbMdl.getDataAtRow(tbClarification.getSelectedRow());
            publisher.notifyObservers(true, (row.getGeoId() != null) ? row.getGeoId() : null);
        }
        else {
            publisher.notifyObservers(true, null);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

    static class Publisher extends AbstractObservable {
        private static final long serialVersionUID = 7859306926483057301L;
    }

    /*
     * KeyListener, um die Selektion einer Liste aufzuheben.
     */
    static class ClearListSelectionKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && (e.getSource() instanceof AKJList)) {
                ((AKJList) e.getSource()).clearSelection();
            }
        }
    }
}
