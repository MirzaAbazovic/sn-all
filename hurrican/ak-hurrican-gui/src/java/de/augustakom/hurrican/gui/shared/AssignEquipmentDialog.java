/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.2007 10:45:27
 */
package de.augustakom.hurrican.gui.shared;

import static de.augustakom.hurrican.model.cc.view.HardwareEquipmentView.*;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktEQConfig;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.cc.view.HardwareEquipmentView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Dialog, ueber den ein Equipment einer Rangierung zugeordnet werden kann. <br> Ueber verschiedene RadioButtons auf dem
 * Dialog kann definiert werden, welcher Rangierung ein Equipment auf welcher Seite (IN od OUT) zugeordnet werden soll.
 * <br> Besteht das Rangierungsobjekt noch nicht, so wird automatisch eines angelegt. <br> Die Ermittlung der zur
 * Verfuegung stehenden Equipments erfolgt ueber die Equipment-Konfiguration zu dem aktuellen Produkt.
 *
 *
 */
public class AssignEquipmentDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(AssignEquipmentDialog.class);

    /**
     * Konstante, um nur die Standard-Rangierung auswaehlen zu koennen.
     */
    public static final int ENABLE_RANG_STD = 1;
    /**
     * Konstante, um nur die Zusatz-Rangierung auswaehlen zu koennen.
     */
    public static final int ENABLE_RANG_ADD = 2;

    /**
     * Konstante, um nur die EQ-IN Seite auswaehlen zu koennen.
     */
    public static final int ENABLE_EQ_IN = 10;
    /**
     * Konstante, um nur die EQ-OUT Seite auswaehlen zu koennen.
     */
    public static final int ENABLE_EQ_OUT = 11;

    private static final String RANGIERUNG_DEFAULT = "rangierung.default";
    private static final String RANGIERUNG_ADDITIONAL = "rangierung.additional";

    private static final Map<String, Integer> RANGIERUNG_MAPPING = new HashMap<>();

    static {
        RANGIERUNG_MAPPING.put(RANGIERUNG_DEFAULT, ENABLE_RANG_STD);
        RANGIERUNG_MAPPING.put(RANGIERUNG_ADDITIONAL, ENABLE_RANG_ADD);
    }

    // Modelle
    private Endstelle endstelle;
    private Long hvtIdStandort;
    private Produkt produkt;
    private int enableRang;
    private int enableEQ;

    // GUI-Komponenten
    private AKReferenceField rfHVT;
    private AKJRadioButton rbRangStd;
    private AKJRadioButton rbRangAdd;
    private AKJPanel subPanel;

    private AssignRangStdPanel defaultPanel;

    private EndstellenService endstellenService;
    private EndgeraeteService endgeraeteService;
    private RangierungsService rangierungsService;

    /**
     * Konstruktor mit Angabe der Endstelle, zu der eine Rangierung aufgebaut werden soll.
     * Zusaetzlich wird noch der HVT angegeben, von dem die Equipments ermittelt werden sollen.
     *
     * @param endstelle
     * @param hvtIdStandort
     * @throws IllegalArgumentException wenn die Endstelle nicht angegeben ist.
     */
    public AssignEquipmentDialog(Endstelle endstelle, Long hvtIdStandort) {
        this(endstelle, hvtIdStandort, 0, 0);
    }

    public AssignEquipmentDialog(Endstelle endstelle) {
        this(endstelle, 0, 0);
    }

    public AssignEquipmentDialog(Endstelle endstelle, int enableRang, int enableEq) {
        this(endstelle, endstelle.getHvtIdStandort(), enableRang, enableEq);
    }

    /**
     * @param endstelle
     * @param enableRang Angabe, welche Rangierung definiert werden soll
     * @param enableEq   Angabe, welche EQ-Seite definiert werden soll
     * @see AssignEquipmentDialog(Endstelle)
     */
    public AssignEquipmentDialog(Endstelle endstelle, final Long hvtIdStandort, int enableRang, int enableEq) {
        super("de/augustakom/hurrican/gui/shared/resources/AssignEquipmentDialog.xml");

        if (endstelle == null) {
            throw new IllegalArgumentException("Endstelle muss angegeben werden!");
        }

        this.endstelle = endstelle;
        this.hvtIdStandort = hvtIdStandort;
        this.enableEQ = enableEq;
        this.enableRang = enableRang;

        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            endstellenService = getCCService(EndstellenService.class);
            endgeraeteService = getCCService(EndgeraeteService.class);
            rangierungsService = getCCService(RangierungsService.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblRangierung = getSwingFactory().createLabel("rangierung", SwingConstants.LEFT, Font.BOLD);

        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        rfHVT.addPropertyChangeListener(AKReferenceField.PROPERTY_CHANGE_REFERENCE_ID, this);
        ButtonGroup bgRangTyp = new ButtonGroup();
        rbRangStd = getSwingFactory().createRadioButton("rangierung.default", getActionListener(), true, bgRangTyp);
        rbRangAdd = getSwingFactory().createRadioButton("rangierung.additional", getActionListener(), false, bgRangTyp);

        subPanel = new AKJPanel(new CardLayout());

        defaultPanel = new AssignRangStdPanel(enableEQ);
        subPanel.add(defaultPanel, AssignEquipmentDialog.RANGIERUNG_DEFAULT);

        if (enableRang == ENABLE_RANG_STD) {
            rbRangStd.setSelected(true);
            rbRangAdd.setEnabled(false);
        }
        else if (enableRang == ENABLE_RANG_ADD) {
            rbRangAdd.setSelected(true);
            rbRangStd.setEnabled(false);
        }

        CardLayout cardLayout = (CardLayout) subPanel.getLayout();

        cardLayout.show(subPanel, AssignEquipmentDialog.RANGIERUNG_DEFAULT);

        AKJPanel top = new AKJPanel(new GridBagLayout());
        //@formatter:off
        top.add(lblHVT,         GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        top.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(rfHVT,          GBCFactory.createGBC(  0,   0, 2, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblRangierung,  GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        top.add(rbRangStd,      GBCFactory.createGBC(  0,   0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(rbRangAdd,      GBCFactory.createGBC(  0,   0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 2, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(top, BorderLayout.NORTH);
        getChildPanel().add(subPanel, BorderLayout.CENTER);
    }

    protected Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    @Override
    public final void loadData() {
        try {
            HVTService hvtService = getCCService(HVTService.class);
            rfHVT.setReferenceList(hvtService.findHVTViews(new HVTQuery().withHvtIdStandort(getHvtIdStandort())));

            if (endstelle != null) {
                if (getHvtIdStandort() != null) {
                    rfHVT.setReferenceId(getHvtIdStandort());
                }

                // Produkt zur Endstelle laden
                CCAuftragService as = getCCService(CCAuftragService.class);
                AuftragDaten auftragDaten = as.findAuftragDatenByEndstelle(endstelle.getId());
                if (auftragDaten != null) {
                    ProduktService ps = getCCService(ProduktService.class);
                    produkt = ps.findProdukt(auftragDaten.getProdId());
                }

                if (produkt == null) {
                    throw new IllegalArgumentException("Das Produkt konnte nicht ermittelt werden!");
                }
            }
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        HardwareEquipmentView view =
                (HardwareEquipmentView) ((AKMutableTableModel) defaultPanel.getTbEq().getModel())
                        .getDataAtRow(defaultPanel.getTbEq().getSelectedRow());
        defaultPanel.objectSelected(view);

        prepare4Close();
        setValue(1);
    }

    /**
     * Saves selected equipment to rangierung. In case no rangierung object is present yet creates a new rangierung
     * Based on GUI selection saves selected equipment as EQ_IN or EQ_OUT. Based on GUI selection saves rangierung as
     * default or additional rangierung.
     *
     * @param equipmentView
     * @param isEqIn
     */
    protected void saveEquipment(HardwareEquipmentView equipmentView, boolean isEqIn) {
        try {
            if (!rbRangStd.isSelected() && !rbRangAdd.isSelected()) {
                throw new HurricanGUIException("Bitte angeben, welche Rangierung erzeugt werden soll.");
            }

            validatePreconditions(rbRangStd.isSelected());

            Equipment equipment = equipmentView.getEquipment();
            Rangierung rangToUse;
            Rangierung rangParent = null;

            if (rbRangStd.isSelected()) {
                rangToUse = getRangierung();
            } else {
                rangToUse = getRangierungAdditional();
                rangParent = getRangierung();

                if (rangParent == null) {
                    throw new HurricanGUIException("Zusatz-Rangierung kann nur erzeugt werden, " +
                            "wenn bereits eine Haupt-Rangierung vorhanden ist.");
                }
            }

            if (rangToUse == null) {
                rangToUse = createRangierung();
                rangierungsService.saveRangierung(rangToUse, false);

                if ((rangParent != null) && (rangToUse.getLeitungGesamtId() == null)) {
                    // falls Standard- und Zusatz-Rangierung angegeben, diese buendeln
                    List<Rangierung> toBundle = new ArrayList<>();
                    toBundle.add(rangParent);
                    toBundle.add(rangToUse);
                    rangierungsService.bundleRangierungen(toBundle);
                }
            }

            assignRangierung(rangToUse, rbRangStd.isSelected());
            assignEquipment(rangToUse, equipment, isEqIn);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    protected void validatePreconditions(boolean isDefault) throws ServiceNotFoundException, ValidationException, FindException, HurricanGUIException {
        getCCService(HVTService.class).validateKvzSperre(endstelle);
    }

    /**
     * Rangierung der Endstelle zuordnen.
     * @param rangierung
     * @param isDefault
     */
    protected void assignRangierung(Rangierung rangierung, boolean isDefault) throws StoreException {
        if (isDefault && endstelle.getRangierId() == null) {
            endstelle.setRangierId(rangierung.getId());
            endstellenService.saveEndstelle(endstelle);
            endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);
        } else if (!isDefault && endstelle.getRangierIdAdditional() == null) {
            endstelle.setRangierIdAdditional(rangierung.getId());
            endstellenService.saveEndstelle(endstelle);
        }
    }

    /**
     * Equipment auf Rangierung als EQ_IN oder EQ_OUT zuordnen
     * @param rangierung
     * @param equipment
     * @param isEqIn
     * @throws StoreException
     */
    protected void assignEquipment(Rangierung rangierung, Equipment equipment, boolean isEqIn) throws StoreException {
        if (isEqIn) {
            rangierungsService.assignEquipment2Rangierung(rangierung, equipment, null, true, false, false);
        } else {
            rangierungsService.assignEquipment2Rangierung(rangierung, null, equipment, true, false, false);
        }
    }

    /**
     * Ermittelt die Rangierung (default) zur aktuell verwendeten Endstelle.
     * @return
     * @throws FindException
     */
    protected Rangierung getRangierung() throws FindException {
        return (endstelle.getRangierId() != null)
                ? rangierungsService.findRangierung(endstelle.getRangierId()) : null;
    }

    /**
     * Ermittelt die Rangierung (additional) zur aktuell verwendeten Endstelle.
     * @return
     * @throws FindException
     */
    protected Rangierung getRangierungAdditional() throws FindException {
        return (endstelle.getRangierIdAdditional() != null)
                ? rangierungsService.findRangierung(endstelle.getRangierIdAdditional()) : null;
    }

    /**
     * Neue Rangierung erzeugen und Default-Angaben setzen.
     * @return
     */
    protected Rangierung createRangierung() {
        Rangierung rangierung = new Rangierung();
        rangierung.setEsId(endstelle.getId());
        rangierung.setHvtIdStandort(endstelle.getHvtIdStandort());
        rangierung.setFreigegeben(Freigegeben.freigegeben);
        rangierung.setGueltigVon(new Date());
        rangierung.setGueltigBis(DateTools.getHurricanEndDate());
        rangierung.setUserW(HurricanSystemRegistry.instance().getCurrentLoginName());
        rangierung.setBemerkung("manueller Rangierungsaufbau");
        rangierung.setPhysikTypId(PhysikTyp.PHYSIKTYP_MANUELL);
        return rangierung;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (AKReferenceField.PROPERTY_CHANGE_REFERENCE_ID.equals(evt.getPropertyName())) {
            HVTGruppeStdView standortView = (HVTGruppeStdView) rfHVT.getReferenceObject();
            endstelle.setHvtIdStandort((standortView != null) ? standortView.getHvtIdStandort() : null);

            if ((subPanel != null) && (subPanel instanceof AssignRangStdPanel)) {
                // Equipments neu laden
                subPanel = new AssignRangStdPanel(enableEQ);
            }
        }
    }

    @Override
    protected void execute(String command) {
        String command1 = command;
        if (null != subPanel) {

            if (null != command1) {
                enableRang = RANGIERUNG_MAPPING.get(command1);

                if (command1.equalsIgnoreCase(AssignEquipmentDialog.RANGIERUNG_ADDITIONAL)) {
                    command1 = AssignEquipmentDialog.RANGIERUNG_DEFAULT;
                }
            }

            ((CardLayout) subPanel.getLayout()).show(subPanel, command1);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    /* Panel fuer einen Rangierungsaufbau fuer TAL basierte Produkte */
    class AssignRangStdPanel extends AbstractServicePanel implements AKObjectSelectionListener {
        private ButtonGroup bgEQSite;
        private AKJRadioButton rbEqIn;
        private AKJRadioButton rbEqOut;
        private AKReflectionTableModel<HardwareEquipmentView> tbMdlEq;
        private AKJTable tbEq;

        /**
         * Konstruktor
         */
        public AssignRangStdPanel(int enableEq) {
            super("de/augustakom/hurrican/gui/shared/resources/AssignRangStdPanel.xml");
            createGUI();
            loadData();

            if (enableEq == ENABLE_EQ_OUT) {
                rbEqOut.setSelected(true);
                rbEqIn.setEnabled(false);
                execute("eq.out");
            }
            else if (enableEq == ENABLE_EQ_IN) {
                rbEqIn.setSelected(true);
                rbEqOut.setEnabled(false);
                execute("eq.in");
            }
        }

        @Override
        protected final void createGUI() {
            AKJLabel lblPort = getSwingFactory().createLabel("port", SwingConstants.LEFT, Font.BOLD);

            bgEQSite = new ButtonGroup();
            rbEqIn = getSwingFactory().createRadioButton("eq.in", getActionListener(), false, bgEQSite);
            rbEqOut = getSwingFactory().createRadioButton("eq.out", getActionListener(), false, bgEQSite);

            tbMdlEq = new AKReflectionTableModel<>(
                    new String[] { "Gerätebez.", "BG-Typ", "BG-Bemerkung", "HW-EQN", "Status", "Bucht", "Leiste",
                            "Stift",
                            "Rang-SS-Typ", "R.-Schnittst.", "UETV", "KVZ Nr", "Verwendung" },
                    new String[] { RACK_GERAETEBEZ, BG_TYP, BG_BEMERKUNG, EQ_HW_EQN, EQ_STATUS, EQ_RANG_BUCHT,
                            EQ_RANG_LEISTE1, EQ_RANG_STIFT1,
                            EQ_RANG_SS_TYPE, EQ_RANG_SCHNITTSTELLE, EQ_UETV, EQ_KVZ_NR, EQ_VERWENDUNG },
                    new Class[] { String.class, String.class, String.class, String.class, String.class, String.class,
                            String.class, String.class,
                            String.class, String.class, String.class, String.class, Enum.class }
            );
            tbEq = new AKJTable(tbMdlEq, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
            tbEq.attachSorter();
            tbEq.addMouseListener(new AKTableDoubleClickMouseListener(this));
            tbEq.fitTable(new int[] { 100, 100, 100, 80, 60, 60, 60, 60, 90, 90, 60, 60, 90 });
            AKJScrollPane spEqOut = new AKJScrollPane(tbEq, new Dimension(890, 250));

            this.setLayout(new GridBagLayout());
            //@formatter:off
            this.add(lblPort,        GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
            this.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
            this.add(rbEqIn,         GBCFactory.createGBC(  0,   0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(rbEqOut,        GBCFactory.createGBC(  0,   0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(spEqOut,        GBCFactory.createGBC(100, 100, 0, 1, 4, 1, GridBagConstraints.BOTH));
            //@formatter:on
        }

        @Override
        public void update(Observable o, Object arg) {
            // intentionally left blank
        }

        @Override
        public void objectSelected(Object selection) {
            try {
                if (!(selection instanceof HardwareEquipmentView)
                        || (((HardwareEquipmentView) selection).getEquipment() == null)) {
                    throw new HurricanGUIException("Kein Equipment angegeben!");
                }

                if (!rbEqIn.isSelected() && !rbEqOut.isSelected()) {
                    throw new HurricanGUIException("Bitte angeben, welche Equipment-Seite definiert werden soll.");
                }

                saveEquipment((HardwareEquipmentView) selection, rbEqIn.isSelected());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }

        @Override
        protected final void execute(String command) {
            if ("eq.in".equals(command)) {
                loadEquipments(true);
            }
            else if ("eq.out".equals(command)) {
                loadEquipments(false);
            }
        }

        /*
         * Laedt die Equipments, die fuer die Vergabe in Frage kommen.
         *
         * @param eqIn Flag, ob EQ_IN (true) oder EQ_OUT (false) Seite zugeordnet werden soll.
         */
        private void loadEquipments(boolean eqIn) {
            try {
                tbMdlEq.setData(null);
                if (rfHVT.getReferenceId() == null) {
                    throw new HurricanGUIException("Es ist noch kein Standort angegeben!");
                }

                // Equipment-Konfiguration zum Produkt laden
                RangierungAdminService ras = getCCService(RangierungAdminService.class);
                String eqType = (eqIn) ? ProduktEQConfig.EQ_TYP_IN : ProduktEQConfig.EQ_TYP_OUT;
                boolean rangierungDefault = rbRangStd.isSelected();
                boolean rangierungAdditional = rbRangAdd.isSelected();

                List<Equipment> examples = ras.createExampleEquipmentsFromProdEqConfig(
                        produkt.getId(),
                        eqType,
                        rangierungDefault,
                        rangierungAdditional);

                if (CollectionTools.isEmpty(examples)) {
                    throw new HurricanGUIException("Zu dem Produkt wurde keine Equipment-Konfiguration gefunden!");
                }

                RangierungsService rangierungsService = getCCService(RangierungsService.class);

                // TreeMap, um Sortierung nach Equipment-ID zu erreichen
                Map<Long, HardwareEquipmentView> eqMap = new TreeMap<>();
                for (Equipment example : examples) {
                    example.setStatus(EqStatus.frei);
                    example.setHvtIdStandort(rfHVT.getReferenceIdAs(Long.class));

                    List<HardwareEquipmentView> result =
                            rangierungsService.findEquipmentViews(example, new String[] { "rangBucht", "rangLeiste1",
                                    "rangStift1" });

                    // Equipments einer Map zuordnen, mit ID als Key.
                    // Grund: je nach Konfiguration koennen mehrere Example-Objekte die identischen Equipments liefern
                    CollectionMapConverter.convert2Map(result, eqMap, "getId", null);
                }

                List<HardwareEquipmentView> equipments = new ArrayList<>();
                equipments.addAll(eqMap.values());

                tbMdlEq.setData(equipments);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }

        /**
         * @return the tbEq
         */
        public AKJTable getTbEq() {
            return tbEq;
        }
    }

    public RangierungsService getRangierungsService() {
        return rangierungsService;
    }
}
