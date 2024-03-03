/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2006 14:42:55
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.shared.RangierungEquipmentTableModel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog zur Zuordnung einer freien Physik (=Rangierung) zu einer Endstelle.
 *
 *
 */
public class PhysikZuordnungDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKObjectSelectionListener, PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(PhysikZuordnungDialog.class);

    private Endstelle endstelle = null;
    private boolean assignSelection = true;
    private boolean filterBandwidth;
    private Long hvtId = null;
    private GeoId2TechLocation geoId2TechLocation = null;

    private AKJTextField tfKVZNummer = null;
    private AKReferenceField rfHVT = null;
    private AKJComboBox cbHwBgTyp = null;
    private RangierungTable tbRangierungen = null;
    private RangierungEquipmentTableModel tbMdlRangierungen = null;

    private AvailabilityService availabilityService;
    private RangierungsService rangierungsService;
    private EndgeraeteService endgeraeteService;
    private PhysikService physikService;
    private CCAuftragService ccAuftragService;
    private ProduktService produktService;
    private HVTService hvtService;


    /**
     * Konstruktor mit Angabe der Endstelle, der eine Rangierung zugewiesen werden soll.
     *
     * @param endstelle
     * @param assignSelection
     * @param filterBandwidth
     */
    public PhysikZuordnungDialog(Endstelle endstelle, boolean assignSelection, boolean filterBandwidth) {
        this(endstelle, assignSelection, filterBandwidth, null);
        if (endstelle != null) {
            hvtId = endstelle.getHvtIdStandort();
            loadData();
        }
    }

    public PhysikZuordnungDialog(Endstelle endstelle, boolean assignSelection, boolean filterBandwidth, Long hvtId) {
        super("de/augustakom/hurrican/gui/auftrag/resources/PhysikZuordnungDialog.xml");
        if (endstelle == null) {
            throw new IllegalArgumentException("Es wurde kein Endstelle-Objekt angegeben.");
        }
        this.endstelle = endstelle;
        this.assignSelection = assignSelection;
        this.filterBandwidth = filterBandwidth;
        this.hvtId = hvtId;

        init();
        createGUI();
        loadData();
    }


    /**
     * Initialisiert das Panel.
     */
    private void init() {
        try {
            availabilityService = getCCService(AvailabilityService.class);
            rangierungsService = getCCService(RangierungsService.class);
            endgeraeteService = getCCService(EndgeraeteService.class);
            physikService = getCCService(PhysikService.class);
            ccAuftragService = getCCService(CCAuftragService.class);
            produktService = getCCService(ProduktService.class);
            hvtService = getCCService(HVTService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, getSwingFactory().getText("ok.text"),
                getSwingFactory().getText("ok.tooltip"), true, true);

        AKJLabel lblHVTStd = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblHwBgTyp = getSwingFactory().createLabel("hw.port");
        AKJLabel lblKVZNummer = getSwingFactory().createLabel("kvz.nummer");
        AKJLabel lblResult = getSwingFactory().createLabel("rangierungen", SwingConstants.LEFT, Font.BOLD);
        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        rfHVT.addPropertyChangeListener(AKReferenceField.PROPERTY_CHANGE_REFERENCE_ID, this);
        cbHwBgTyp = getSwingFactory().createComboBox("hw.port");
        tfKVZNummer = getSwingFactory().createTextField("kvz.nummer", false);
        AKJButton btnFilter = getSwingFactory().createButton("filter", getActionListener());

        tbMdlRangierungen = new RangierungEquipmentTableModel();
        tbRangierungen = new RangierungTable(tbMdlRangierungen,
                JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRangierungen.attachSorter();
        tbRangierungen.fitTable(new int[] { 75, 70, 45, 160, 70, 70, 85, 100, 70, 70, 85, 60, 40, 70, 85, 80, 50, 70, 110 });
        tbRangierungen.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTable = new AKJScrollPane(tbRangierungen, new Dimension(750, 270));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblHVTStd, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(rfHVT, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblHwBgTyp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 2)));
        top.add(cbHwBgTyp, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(btnFilter, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblKVZNummer,
                GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 2)));
        top.add(tfKVZNummer, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(lblResult, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 2, 2)));
        center.add(spTable, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(top, BorderLayout.NORTH);
        child.add(center, BorderLayout.CENTER);
    }


    @Override
    public final void loadData() {
        try {
            if (hvtId != null) {
                rfHVT.setReferenceList(hvtService.findHVTViews(new HVTQuery().withHvtIdStandort(hvtId)));
                rfHVT.setReferenceId(hvtId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Ermittelt die freien Rangierungen auf dem HVT-Standort mit der ID <code>hvtIdStd</code>. <br> Es werden nur die
     * Rangierungen ermittelt, deren Physiktypen dem aktuellen Produkt zugeordnet sein koennen.
     */
    private void loadRangierungen(final Long hvtIdStd, final String filterHwBgTyp) {
        if (hvtIdStd == null) {
            return;
        }
        tbMdlRangierungen.removeAll();

        final SwingWorker<List<RangierungsEquipmentView>, Void> worker = new SwingWorker<List<RangierungsEquipmentView>, Void>() {

            // Initialize required data from frame in the constructor
            // since this should be done in the EDT thread.
            final boolean localAssignSelection = assignSelection;
            final Long endstelleId = endstelle.getId();

            @Override
            public List<RangierungsEquipmentView> doInBackground() throws Exception {
                RangierungQuery query = new RangierungQuery();
                query.setHvtStandortId(hvtIdStd);
                query.setIncludeFreigabebereit(Boolean.TRUE);
                if (!localAssignSelection) {
                    query.setIncludeDefekt(Boolean.TRUE);
                }

                List<Rangierung> rangierungen = new ArrayList<Rangierung>();

                // Produktgruppe zu Auftrag ermitteln
                AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByEndstelle(endstelleId);
                Produkt produkt = produktService.findProdukt((auftragDaten != null) ? auftragDaten.getProdId() : null);

                Bandwidth bandwidth = null;
                if (filterBandwidth && (auftragDaten != null)) {
                    final TechLeistung downStreamTechnLeistung = getCCService(CCLeistungsService.class)
                            .findTechLeistung4Auftrag(auftragDaten.getAuftragId(), TechLeistung.TYP_DOWNSTREAM, true);
                    Integer downStream = (downStreamTechnLeistung != null) ? downStreamTechnLeistung.getIntegerValue() : null;
                    bandwidth = Bandwidth.create(downStream);
                }
                // Falls Produktgruppe NICHT gleich AK-Connect bzw. Connect-Intern beruecksichtige Physiktyp
                if ((produkt != null) && (auftragDaten != null) &&
                        !NumberTools.isIn(produkt.getProduktGruppeId(),
                                new Long[] { ProduktGruppe.AK_CONNECT, ProduktGruppe.AK_CONNECT_INTERN })) {
                    List<Produkt2PhysikTyp> p2pts = physikService.findP2PTs4Produkt(auftragDaten.getProdId(), null);
                    if (CollectionTools.isNotEmpty(p2pts)) {
                        for (Produkt2PhysikTyp p2pt : p2pts) {
                            query.setPhysikTypId(p2pt.getPhysikTypId());
                            List<Rangierung> resultForP2PT =
                                    rangierungsService.findFreieRangierungen(query, true,
                                            p2pt.getPhysikTypAdditionalId(), bandwidth);
                            rangierungen.addAll(resultForP2PT);
                        }
                    }
                }
                else {
                    // Ermittle freie Rangierungen
                    rangierungen = rangierungsService.findFreieRangierungen(query, true, null, bandwidth);
                }

                List<RangierungsEquipmentView> filteredRangierungen = rangierungsService
                        .createRangierungsEquipmentView(rangierungen,
                                (geoId2TechLocation != null) ? geoId2TechLocation.getKvzNumber() : null);
                return filteredRangierungen;
            }

            @Override
            protected void done() {
                try {
                    tbMdlRangierungen.setData(filterResult(get(), filterHwBgTyp));
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                enableGuiElements(true);
                setDefaultCursor();
            }
        };

        setWaitCursor();
        enableGuiElements(false);
        worker.execute();
    }


    /**
     * Filtert das Ergebnis der freien Rangierungen nach dem optional gesetzten Baugruppentyp Filter
     */
    private List<RangierungsEquipmentView> filterResult(List<RangierungsEquipmentView> unfiltered, String filterHwBgTyp) {
        List<String> hwBgTypen = new ArrayList<String>();

        if (CollectionTools.isNotEmpty(unfiltered)) {
            List<RangierungsEquipmentView> result = new ArrayList<RangierungsEquipmentView>();
            for (RangierungsEquipmentView view : unfiltered) {

                // Equipments filtern nach Baugruppentyp
                if (StringUtils.isBlank(filterHwBgTyp)
                        || StringUtils.equals(filterHwBgTyp, view.getHwBgTypEqIn())
                        || StringUtils.equals(filterHwBgTyp, view.getHwBgTypEqOut())) {
                    result.add(view);
                }

                // fuege HwBgTyp der Combobox hinzu
                if ((view.getHwBgTypEqIn() != null) && !hwBgTypen.contains(view.getHwBgTypEqIn())) {
                    hwBgTypen.add(view.getHwBgTypEqIn());
                }
                if ((view.getHwBgTypEqOut() != null) && !hwBgTypen.contains(view.getHwBgTypEqOut())) {
                    hwBgTypen.add(view.getHwBgTypEqOut());
                }
            }

            // Falls Filter nicht gesetzt, setze Inhalt der Combobox
            if (StringUtils.isBlank(filterHwBgTyp)) {
                cbHwBgTyp.removeAllItems();
                Collections.sort(hwBgTypen);
                cbHwBgTyp.addItems(hwBgTypen, true, String.class);
            }
            return result;
        }
        return unfiltered;
    }


    @Override
    public void objectSelected(Object selection) {
        // Berechtigung pruefen
        if (!getButton(CMD_SAVE).isEnabled()) { return; }

        try {
            RangierungsEquipmentView view = (RangierungsEquipmentView) selection;

            if (selection instanceof RangierungsEquipmentView) {
                if (assignSelection) {
                    String msg = StringTools.formatString(getSwingFactory().getText("save.question.msg"),
                            new Object[] { view.getHwEqnIn() }, null);

                    int option = MessageHelper.showYesNoQuestion(this, msg, getSwingFactory().getText("save.question.title"));
                    if (option != JOptionPane.YES_OPTION) {
                        return;
                    }

                    // Rangierungen ermitteln
                    Rangierung rangierung = rangierungsService.findRangierung(view.getRangierId());
                    Rangierung rangierungAdd = null;
                    if (view.getRangierIdAdd() != null) {
                        rangierungAdd = rangierungsService.findRangierung(view.getRangierIdAdd());
                    }

                    rangierungsService.attachRangierung2Endstelle(rangierung, rangierungAdd, endstelle);
                    endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);

                    prepare4Close();
                    setValue(OK_OPTION);
                }
                else {
                    prepare4Close();
                    setValue(selection);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    protected void doSave() {
        AKMutableTableModel mdl = (AKMutableTableModel) tbRangierungen.getModel();
        Object tmp = mdl.getDataAtRow(tbRangierungen.getSelectedRow());
        objectSelected(tmp);
    }

    @Override
    protected void execute(String command) {
        if (command.equals("filter")) {
            loadRangierungen();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (AKReferenceField.PROPERTY_CHANGE_REFERENCE_ID.equals(evt.getPropertyName())) {
            loadKVZNummer();
            loadRangierungen();
        }
    }

    private void enableGuiElements(boolean enable) {
        cbHwBgTyp.setEnabled(enable);
        rfHVT.setEnabled(enable);
        getButton(CMD_SAVE).setEnabled(enable);
    }

    /**
     * Falls der techn. Standort der Endstelle ein KVZ Standort ist und die Selektion genau diesen ausgewaehlt hat,
     * ermittelt die Methode die zugehoerige KVZ Nummer. Auf Basis dieser Nummer koennen dann die Rangierungen gefiltert
     * werden (Rangierungen eines Standortes koennen unterschiedlichen KVZs zugeordnet sein).
     */
    private void loadKVZNummer() {
        try {
            tfKVZNummer.setText((String) null);
            geoId2TechLocation = null;
            if ((endstelle != null) && (endstelle.getGeoId() != null) && (endstelle.getHvtIdStandort() != null)) {
                HVTGruppeStdView selectedView = rfHVT.getReferenceObjectAs(HVTGruppeStdView.class);
                Long hvtId = rfHVT.getReferenceIdAs(Long.class);
                if ((selectedView != null) && (hvtId != null) && NumberTools.equal(endstelle.getHvtIdStandort(), hvtId)
                        && NumberTools.equal(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ, selectedView.getStandortTypRefId())) {
                    geoId2TechLocation = availabilityService.findGeoId2TechLocation(endstelle.getGeoId(), hvtId);
                    tfKVZNummer.setText((geoId2TechLocation != null) ? geoId2TechLocation.getKvzNumber()
                            : null);
                }

            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Ermittelt die freien Rangierungen fuer den HVT
     */
    private void loadRangierungen() {
        String hwBgTyp = (String) cbHwBgTyp.getSelectedItem();
        Long hvtId = rfHVT.getReferenceIdAs(Long.class);
        loadRangierungen(hvtId, hwBgTyp);
    }


    static class RangierungTable extends AKJTable {
        private static final Color RANGIERUNG_NOT_ACTIVE_COLOR = new Color(200, 100, 255);

        public RangierungTable(TableModel dm, int autoResizeMode, int selectionMode) {
            super(dm, autoResizeMode, selectionMode);
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component comp = super.prepareRenderer(renderer, row, column);
            if (getModel() instanceof AKMutableTableModel<?>) {
                AKMutableTableModel<?> model = (AKMutableTableModel<?>) getModel();

                RangierungsEquipmentView rangierungEquipmentView = (RangierungsEquipmentView) model.getDataAtRow(row);
                if ((rangierungEquipmentView != null)
                        && NumberTools.equal(Rangierung.RANGIERUNG_NOT_ACTIVE, rangierungEquipmentView.getEsId())) {
                    comp.setBackground(RANGIERUNG_NOT_ACTIVE_COLOR);
                }
            }
            return comp;
        }
    }
}


