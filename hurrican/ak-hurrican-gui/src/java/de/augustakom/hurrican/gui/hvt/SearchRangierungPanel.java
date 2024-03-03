/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2006 16:10:45
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.RangierungEquipmentTableModel;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Panel fuer die Suche nach freien Rangierungen an einem bestimmten HVT.
 *
 *
 */
public class SearchRangierungPanel extends AbstractServicePanel implements AKSearchComponent, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(SearchRangierungPanel.class);

    private AKReferenceField rfHVT = null;
    private AKJComboBox cbPhysiktyp = null;
    private AKJComboBox cbChildTyp = null;
    private AKJLabel lblResultCount = null;
    private AKJTable tbRangierung = null;
    private RangierungEquipmentTableModel tbMdlRangierung = null;

    /**
     * Default-Const.
     */
    public SearchRangierungPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/SearchRangierungPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblResult = getSwingFactory().createLabel("search.result", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblPhysiktyp = getSwingFactory().createLabel("physiktyp");
        AKJLabel lblChildTyp = getSwingFactory().createLabel("child.physik");
        lblResultCount = getSwingFactory().createLabel("result.count", SwingConstants.LEFT, Font.BOLD);

        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        cbPhysiktyp = getSwingFactory().createComboBox("physiktyp",
                new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));
        cbPhysiktyp.addItemListener(new PhysiktypItemListener());
        cbChildTyp = getSwingFactory().createComboBox("child.physik",
                new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));
        AKJButton btnSearch = getSwingFactory().createButton("search", getActionListener());

        tbMdlRangierung = new RangierungEquipmentTableModel();
        tbRangierung = new AKJTable(tbMdlRangierung, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRangierung.attachSorter();
        tbRangierung.fitTable(new int[] { 75, 70, 45, 70, 85, 100, 70, 70, 85, 60, 40, 70, 85, 80, 50, 70, 110 });
        BreakRangierungAction breakRangierungAction = new BreakRangierungAction();
        tbRangierung.addPopupAction(breakRangierungAction);
        AKJScrollPane spResult = new AKJScrollPane(tbRangierung);

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("search.title")));
        top.add(lblHVT, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(rfHVT, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblPhysiktyp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(cbPhysiktyp, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblChildTyp, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(cbChildTyp, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.NONE));
        top.add(btnSearch, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.NONE));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel result = new AKJPanel(new GridBagLayout());
        result.add(lblResult, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        result.add(lblResultCount, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        result.add(spResult, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(result, BorderLayout.CENTER);

        manageGUI(breakRangierungAction);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHVT.setFindService(sfs);

            PhysikService ps = getCCService(PhysikService.class);
            List<PhysikTyp> physiktypen = ps.findPhysikTypen();
            cbPhysiktyp.addItems(physiktypen, true, PhysikTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSearchComponent#doSearch()
     */
    @Override
    public void doSearch() {
        try {
            tbMdlRangierung.setData(null);
            setWaitCursor();
            showProgressBar("suchen...");

            final RangierungQuery query = new RangierungQuery();
            query.setIncludeFreigabebereit(Boolean.TRUE);
            query.setHvtStandortId(rfHVT.getReferenceIdAs(Long.class));
            query.setPhysikTypId((Long) cbPhysiktyp.getSelectedItemValue("getId", Long.class));
            if ((query.getHvtStandortId() == null) || (query.getPhysikTypId() == null)) {
                throw new HurricanGUIException("Bitte waehlen Sie einen HVT und einen Physiktyp aus.");
            }
            final Long childPT = (Long) cbChildTyp.getSelectedItemValue("getId", Long.class);
            final SwingWorker<List<RangierungsEquipmentView>, Void> worker = new SwingWorker<List<RangierungsEquipmentView>, Void>() {

                @Override
                protected List<RangierungsEquipmentView> doInBackground() throws Exception {
                    RangierungsService rangierungsService = getCCService(RangierungsService.class);
                    List<Rangierung> rangierungen = rangierungsService
                            .findFreieRangierungen(query, true, childPT, null);
                    List<RangierungsEquipmentView> rangEqViews = rangierungsService.createRangierungsEquipmentView(
                            rangierungen, null);
                    return rangEqViews;
                }

                @Override
                protected void done() {
                    try {
                        List<RangierungsEquipmentView> rangEqViews = get();
                        lblResultCount.setText(String.format("%d", rangEqViews.size()));
                        tbMdlRangierung.setData(rangEqViews);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        stopProgressBar();
                        setDefaultCursor();
                    }
                }

            };
            worker.execute();

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            stopProgressBar();
            setDefaultCursor();
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("search".equals(command)) {
            doSearch();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /*
     * ItemListener fuer die Physiktyp-ComboBox. <br>
     * Je nach Auswahl von 'Physiktyp' werden die moeglichen Child-Physiktypen
     * geladen.
     */
    class PhysiktypItemListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if ((e.getSource() == cbPhysiktyp) && (e.getStateChange() == ItemEvent.SELECTED)) {
                cbChildTyp.removeAllItems();
                PhysikTyp pt = (PhysikTyp) cbPhysiktyp.getSelectedItem();
                if ((pt != null) && (pt.getId() != null)) {
                    try {
                        PhysikService ps = getCCService(PhysikService.class);
                        List<Long> ptIds = new ArrayList<Long>();
                        ptIds.add(pt.getId());
                        List<PhysikTyp> childPTs = ps.findPhysikTypen4ParentPhysik(ptIds);
                        cbChildTyp.addItems(childPTs, true, PhysikTyp.class);
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }

    /* Action, um die selektierte Rangierung aufzubrechen. */
    public class BreakRangierungAction extends AKAbstractAction {
        /**
         * Default-Const.
         */
        public BreakRangierungAction() {
            setName("Rangierung aufbrechen...");
            setActionCommand("break.rangierung");
            getAccessibleContext().setAccessibleName("break.rangierung");
            setTooltip("Entfernt alle Ports aus der Rangierung");
        }

        private boolean doBreak() {
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Soll die Rangierung wirklich aufgebrochen werden?",
                    "Rangierung aufbrechen?");
            return (option == JOptionPane.YES_OPTION) ? true : false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (doBreak()) {
                    AKMutableTableModel mdl = (AKMutableTableModel) tbRangierung.getModel();
                    Object tmp = mdl.getDataAtRow(tbRangierung.getSelectedRow());
                    if (tmp instanceof RangierungsEquipmentView) {
                        RangierungsEquipmentView rangierungEqView = (RangierungsEquipmentView) tmp;

                        if ((rangierungEqView.getEsId() != null) && (rangierungEqView.getEsId().intValue() > 0)) {
                            throw new HurricanGUIException("Die Rangierung ist einem Auftrag zugeordnet.\n" +
                                    "Rangierung darf nicht komplett aufgebrochen werden!");
                        }

                        RangierungsService rangierungsService = getCCService(RangierungsService.class);

                        List<Rangierung> rangierungenToBreak = new ArrayList<Rangierung>();
                        Rangierung rangierungDefault = rangierungsService.findRangierung(rangierungEqView.getRangierId());
                        rangierungenToBreak.add(rangierungDefault);

                        if (rangierungEqView.getRangierIdAdd() != null) {
                            Rangierung rangierungAdd = rangierungsService.findRangierung(rangierungEqView.getRangierIdAdd());
                            rangierungenToBreak.add(rangierungAdd);
                        }

                        rangierungsService.breakAndDeactivateRangierung(
                                rangierungenToBreak,
                                true,
                                HurricanSystemRegistry.instance().getSessionId());

                        doSearch();
                    }
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }

    }

}
