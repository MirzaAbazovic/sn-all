/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 09:30:06
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationType;
import de.augustakom.hurrican.model.cc.ProduktEQConfig;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.SdslNdraht;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Sub-Panel, um die Physiktyp-Zuordnung fuer ein Produkt zu konfigurieren.
 *
 */
public class ProduktPhysikPanel extends AbstractServicePanel implements AKModelOwner, AKNavigationBarListener,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ProduktPhysikPanel.class);
    public static final String SDSLNDRAHT = "sdslndraht";
    public static final String PHYSIKTYP = "physiktyp";
    public static final String PHYSIKTYP_ADDITIONAL = "physiktyp.additional";
    public static final String VIRTUELL = "virtuell";
    public static final String USEINRAGMATRIX = "use.in.rangmatrix";
    public static final String PARENT_PHYSIKTYP = "parent.physiktyp";
    public static final String PRIORITY_LIST = "priority.list";
    public static final String PRIORITY_CATALOG = "priority.catalog";
    public static final String NEW_P2PT = "new.p2pt";
    public static final String DELETE_P2PT = "delete.p2pt";
    public static final String NAV_P2PT = "nav.p2pt";
    public static final String ADD_PROD_EQ_CONFIG = "add.prod.eq.config";
    public static final String DEL_PROD_EQ_CONFIG = "del.prod.eq.config";
    public static final String ADD_PRIORITY = "add.priority";
    public static final String REMOVE_PRIORITY = "remove.priority";
    public static final String MOVE_PRIORITY_UP = "move.priority.up";
    public static final String MOVE_PRIORITY_DOWN = "move.priority.down";
    public static final String PHYSIKTYP_PRIORITY = "physiktyp.priority";

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Produkt model = null;

    // GUI-Elemente fuer die PhysikTyp-Zuordnung
    private AKJNavigationBar navBar = null;
    private AKJComboBox cbPhysikTyp = null;
    private AKJComboBox cbPhysikTypAdd = null;
    private AKJComboBox cbParentPT = null;
    private AKJCheckBox chbVirtuell = null;
    private AKJCheckBox chbUseInRangMatrix = null;
    private AKJFormattedTextField tfP2PtPriority= null;
    private AKJTable tbProdEqConfig = null;
    private AKReflectionTableModel<ProduktEQConfig> tbMdlProdEqConfig = null;

    private AKJList lsPriority = null;
    private DefaultListModel lsMdlPriority = null;
    private AKJList lsPrioCatalog = null;
    private DefaultListModel lsMdlPrioCatalog = null;

    // Sonstiges
    private Produkt2PhysikTyp actualP2PT = null;
    private int actualNavNumber = -1;
    private boolean guiCreated = false;

    // SDSL-n-Draht
    private AKJComboBox cbNdraht;

    /**
     * Konstruktor.
     */
    public ProduktPhysikPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktPhysikPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Erzeuge GUI-Komponenten und ordne diese auf dem Panel an
        AKJLabel lblPhysikTyp = getSwingFactory().createLabel(PHYSIKTYP);
        AKJLabel lblPhysikTypAdd = getSwingFactory().createLabel(PHYSIKTYP_ADDITIONAL);
        AKJLabel lblVirtuell = getSwingFactory().createLabel(VIRTUELL);
        AKJLabel lblUseInRangMatrix = getSwingFactory().createLabel(USEINRAGMATRIX);
        AKJLabel lblParentPT = getSwingFactory().createLabel(PARENT_PHYSIKTYP);
        AKJLabel lblP2PtPriority = getSwingFactory().createLabel(PHYSIKTYP_PRIORITY);
        AKJLabel lblPriorityList = getSwingFactory().createLabel(PRIORITY_LIST);
        AKJLabel lblPriorityCatalog = getSwingFactory().createLabel(PRIORITY_CATALOG);

        cbPhysikTyp = getSwingFactory().createComboBox(PHYSIKTYP);
        cbPhysikTyp.setRenderer(new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));
        cbPhysikTypAdd = getSwingFactory().createComboBox(PHYSIKTYP_ADDITIONAL);
        cbPhysikTypAdd.setRenderer(new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));
        chbVirtuell = getSwingFactory().createCheckBox(VIRTUELL);
        chbUseInRangMatrix = getSwingFactory().createCheckBox(USEINRAGMATRIX);
        cbParentPT = getSwingFactory().createComboBox(PARENT_PHYSIKTYP);
        cbParentPT.setRenderer(new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));
        tfP2PtPriority = getSwingFactory().createFormattedTextField(PHYSIKTYP_PRIORITY);
        AKJButton btnNewP2PT = getSwingFactory().createButton(NEW_P2PT, getActionListener(), null);
        AKJButton btnDeleteP2PT = getSwingFactory().createButton(DELETE_P2PT, getActionListener(), null);
        navBar = getSwingFactory().createNavigationBar(NAV_P2PT, this, true, false);

        AKJButton btnAddProdEqConfig = getSwingFactory().createButton(ADD_PROD_EQ_CONFIG, getActionListener(), null);
        AKJButton btnDelProdEqConfig = getSwingFactory().createButton(DEL_PROD_EQ_CONFIG, getActionListener(), null);

        AKJButton btnAddPriority = getSwingFactory().createButton(ADD_PRIORITY, getActionListener(), null);
        AKJButton btnRemovePriority = getSwingFactory().createButton(REMOVE_PRIORITY, getActionListener(), null);
        AKJButton btnMovePriorityUp = getSwingFactory().createButton(MOVE_PRIORITY_UP, getActionListener(), null);
        AKJButton btnMovePriorityDown = getSwingFactory().createButton(MOVE_PRIORITY_DOWN, getActionListener(), null);

        // @formatter:off
        tbMdlProdEqConfig = new AKReflectionTableModel<ProduktEQConfig>(
            new String[]{"Gruppe", "Typ", "Parameter", "Wert", "Rang. 1", "Rang. 2"},
            new String[]{"configGroup", "eqTyp", "eqParam", "eqValue", "rangierungsPartDefault", "rangierungsPartAdditional"},
                new Class[] { Long.class, String.class, String.class, String.class, Boolean.class, Boolean.class});
        tbProdEqConfig = new AKJTable(tbMdlProdEqConfig, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProdEqConfig.fitTable(new int[]{60,60,100,100,50,50});
        tbProdEqConfig.attachSorter();
        AKJScrollPane spProdEqConfig = new AKJScrollPane(tbProdEqConfig, new Dimension(470,100));

        AKJPanel leftTop = new AKJPanel(new GridBagLayout(), "automatische Zuordnung");
        leftTop.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        leftTop.add(btnNewP2PT        , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.NONE));
        leftTop.add(btnDeleteP2PT     , GBCFactory.createGBC(  0,  0, 4, 0, 1, 1, GridBagConstraints.NONE));
        leftTop.add(navBar            , GBCFactory.createGBC(  0,  0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(lblPhysikTyp      , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        leftTop.add(cbPhysikTyp       , GBCFactory.createGBC(100,  0, 3, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(lblPhysikTypAdd   , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(cbPhysikTypAdd    , GBCFactory.createGBC(100,  0, 3, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(lblVirtuell       , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(chbVirtuell       , GBCFactory.createGBC(100,  0, 3, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(lblParentPT       , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(cbParentPT        , GBCFactory.createGBC(100,  0, 3, 4, 3, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(lblP2PtPriority   , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(tfP2PtPriority    , GBCFactory.createGBC(100,  0, 3, 5, 3, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(lblUseInRangMatrix, GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(chbUseInRangMatrix, GBCFactory.createGBC(100,  0, 3, 6, 3, 1, GridBagConstraints.HORIZONTAL));
        leftTop.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 1, 7, 1, 1, GridBagConstraints.VERTICAL));

        lsMdlPriority = new DefaultListModel();
        lsPriority = new AKJList(lsMdlPriority);
        lsPriority.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsPriority.setCellRenderer(new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        AKJScrollPane spPriority = new AKJScrollPane(lsPriority, new Dimension(120, 75));

        lsMdlPrioCatalog = new DefaultListModel();
        lsPrioCatalog = new AKJList(lsMdlPrioCatalog);
        lsPrioCatalog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsPrioCatalog.setCellRenderer(new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        AKJScrollPane spPrioCatalog = new AKJScrollPane(lsPrioCatalog, new Dimension(120, 75));

        AKJPanel actionBarVertical = new AKJPanel(new GridBagLayout());
        actionBarVertical.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        actionBarVertical.add(btnMovePriorityUp   , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        actionBarVertical.add(btnMovePriorityDown , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.NONE));
        actionBarVertical.add(new AKJPanel()      , GBCFactory.createGBC(  0,100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel actionBarHorizontal = new AKJPanel(new GridBagLayout());
        actionBarHorizontal.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        actionBarHorizontal.add(btnAddPriority      , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        actionBarHorizontal.add(btnRemovePriority   , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.NONE));
        actionBarHorizontal.add(new AKJPanel()      , GBCFactory.createGBC(  0,100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel leftBottom = new AKJPanel(new GridBagLayout(), "Connection Typ Priorisierung");
        leftBottom.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(lblPriorityList     , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(lblPriorityCatalog  , GBCFactory.createGBC(  0,  0, 4, 0, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 5, 0, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(actionBarVertical   , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(spPriority          , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(actionBarHorizontal , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(spPrioCatalog       , GBCFactory.createGBC(  0,  0, 4, 1, 1, 1, GridBagConstraints.NONE));
        leftBottom.add(new AKJPanel()      , GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        final AKJPanel nDrahtPanel = createNdrahtPanel();

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(leftTop        , GBCFactory.createGBC(100,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(leftBottom     , GBCFactory.createGBC(100,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(nDrahtPanel    , GBCFactory.createGBC(100,100, 0, 3, 1, 1, GridBagConstraints.BOTH));

        AKJPanel right = new AKJPanel(new GridBagLayout(), "manueller Rangierungsaufbau");
        right.add(spProdEqConfig    , GBCFactory.createGBC(100,100, 0, 0, 1, 3, GridBagConstraints.BOTH));
        right.add(btnAddProdEqConfig, GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(btnDelProdEqConfig, GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        right.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 1, 2, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(left           , GBCFactory.createGBC(  0,100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(right          , GBCFactory.createGBC(  0,100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel() , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        enableP2PT(false);
        guiCreated = true;

        // Bitte alle GUI Komponenten auf Rechte prüfen, da diverse User nur auf wenige Komponenten rechte haben!
        manageGUI(cbPhysikTyp, cbPhysikTypAdd, chbVirtuell, chbUseInRangMatrix, cbParentPT,
                btnNewP2PT, btnDeleteP2PT, navBar, btnAddProdEqConfig,
                btnDelProdEqConfig, btnAddPriority, btnRemovePriority, btnMovePriorityUp,
                btnMovePriorityDown, tbProdEqConfig);
    }

    private AKJPanel createNdrahtPanel() {
        final AKJPanel nDrahtPanel = new AKJPanel(new GridBagLayout(), "SDSL-Portvergabe");
        final AKJLabel lblNdraht = getSwingFactory().createLabel(SDSLNDRAHT);
        cbNdraht = createCbNdraht();

        // @formatter:off
        nDrahtPanel.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        nDrahtPanel.add(lblNdraht,      GBCFactory.createGBC(  0,   0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        nDrahtPanel.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 2, 1, 1, 1, GridBagConstraints.NONE));
        nDrahtPanel.add(cbNdraht,       GBCFactory.createGBC(  0,   0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        nDrahtPanel.add(new AKJPanel(), GBCFactory.createGBC(100,   0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        nDrahtPanel.add(new AKJPanel(), GBCFactory.createGBC(  0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on
        return nDrahtPanel;
    }

    private AKJComboBox createCbNdraht() {
        final AKJComboBox cbNdraht = getSwingFactory().createComboBox(SDSLNDRAHT);
        cbNdraht.addItem("", null);
        for (final SdslNdraht sdslNdraht : SdslNdraht.values()) {
            cbNdraht.addItem(sdslNdraht.displayName, sdslNdraht);
        }
        return cbNdraht;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            // Lade alle Physiktypen
            loadPhysikTypen();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (NEW_P2PT.equals(command)) {
            createNewP2PT();
        }
        else if (DELETE_P2PT.equals(command)) {
            deleteP2PT();
        }
        else if (ADD_PROD_EQ_CONFIG.equals(command)) {
            addEQConfig();
        }
        else if (DEL_PROD_EQ_CONFIG.equals(command)) {
            deleteEQConfig();
        }
        else if (ADD_PRIORITY.equals(command)) {
            addPriority();
        }
        else if (REMOVE_PRIORITY.equals(command)) {
            removePriority();
        }
        else if (MOVE_PRIORITY_UP.equals(command)) {
            movePriorityUp();
        }
        else if (MOVE_PRIORITY_DOWN.equals(command)) {
            movePriorityDown();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof Produkt) ? (Produkt) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        // Lösche alle Einträge
        clearAll();

        // Lade Daten aus Model in GUI-Komponenten
        if (model != null) {
            try {
                // Physiktyp-Zuordnung laden
                PhysikService ps = getCCService(PhysikService.class);
                List<Produkt2PhysikTyp> produkt2PhysikTyp = ps.findP2PTs4Produkt(model.getId(), null);
                navBar.setData(produkt2PhysikTyp);

                // ProduktEQConfig Objekte zum Produkt laden
                RangierungAdminService ras = getCCService(RangierungAdminService.class);
                List<ProduktEQConfig> eqConfigs = ras.findProduktEQConfigs(model.getId());
                tbMdlProdEqConfig.setData(eqConfigs);

                // Connection Typen priorisieren
                loadPriorities();

                cbNdraht.selectItemWithValue(model.getSdslNdraht());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void loadPriorities() throws ServiceNotFoundException, FindException {
        if (model != null) {
            ReferenceService referenceService = getCCService(ReferenceService.class);
            ProduktService produktService = getCCService(ProduktService.class);

            List<Reference> connectionTypes = referenceService.findReferencesByType(Reference.REF_TYPE_STANDORT_TYP,
                    Boolean.TRUE);
            List<Produkt2TechLocationType> techLocationTypes = produktService.findProdukt2TechLocationTypes(model
                    .getId());

            lsMdlPrioCatalog.removeAllElements();
            lsMdlPriority.removeAllElements();

            for (Produkt2TechLocationType techLocationType : techLocationTypes) {
                Reference reference = techLocationType.findTechLocationTypeReference(connectionTypes);
                if (reference != null) {
                    lsMdlPriority.addElement(reference);
                    connectionTypes.remove(reference);
                }
            }
            for (Reference connectionType : connectionTypes) {
                lsMdlPrioCatalog.addElement(connectionType);
            }
        }
    }

    private void savePriorities() throws ServiceNotFoundException, StoreException {
        if (model != null) {
            List<Produkt2TechLocationType> techLocationTypes = new ArrayList<Produkt2TechLocationType>();
            ProduktService produktService = getCCService(ProduktService.class);
            for (int i = 0, x = lsMdlPriority.getSize(); i < x; i++) {
                Produkt2TechLocationType techLocationType = new Produkt2TechLocationType();
                Reference reference = (Reference) lsMdlPriority.get(i);
                techLocationType.setTechLocationTypeRefId(reference.getId());
                techLocationTypes.add(techLocationType);
            }
            produktService.saveProdukt2TechLocationTypes(model.getId(), techLocationTypes,
                    HurricanSystemRegistry.instance().getSessionId());
        }
    }

    private void addPriority() {
        Object value = lsPrioCatalog.getSelectedValue();
        if ((value != null) && (value instanceof Reference)) {
            Reference selection = (Reference) value;
            lsMdlPriority.addElement(selection);
            lsMdlPrioCatalog.removeElement(selection);
        }
    }

    private void removePriority() {
        Object value = lsPriority.getSelectedValue();
        if ((value != null) && (value instanceof Reference)) {
            Reference selection = (Reference) value;
            lsMdlPrioCatalog.addElement(selection);
            lsMdlPriority.removeElement(selection);
        }
    }

    /* Verschiebt das selektierte Command nach oben. */
    private void movePriorityUp() {
        int index = lsPriority.getSelectedIndex();
        lsPriority.switchItems(index, --index);
    }

    /* Verschiebt das selektierte Command nach unten. */
    private void movePriorityDown() {
        int index = lsPriority.getSelectedIndex();
        lsPriority.switchItems(index, ++index);
    }

    /* Laedt alle verfuegbaren PhysikTypen */
    private void loadPhysikTypen() throws ServiceNotFoundException, FindException {
        PhysikService ps = getCCService(PhysikService.class);
        List<PhysikTyp> pts = ps.findPhysikTypen();
        cbPhysikTyp.addItems(pts, true, PhysikTyp.class);
        cbPhysikTypAdd.addItems(pts, true, PhysikTyp.class);
        cbParentPT.addItems(pts, true, PhysikTyp.class);
    }

    /* Enabled/Disabled die GUI-Elemente fuer die PhysikTyp-Zuordnung. */
    private void enableP2PT(boolean enable) {
        cbPhysikTyp.setEnabled(enable);
        cbPhysikTypAdd.setEnabled(enable);
        cbParentPT.setEnabled(enable);
        chbVirtuell.setEnabled(enable);
    }

    /* Legt ein neues Mapping zwischen Produkt und PhysikTyp an. */
    private void createNewP2PT() {
        navBar.addNavigationObject(new Produkt2PhysikTyp());
    }

    /* Loescht eine Zuordnung zwischen Produkt und PhysikTyp. */
    private void deleteP2PT() {
        if (actualNavNumber > -1) {
            int choose = MessageHelper.showConfirmDialog(this,
                    "Soll die PhysikTyp-Zuordnung wirklich \ngelöscht werden?", "Zuordnung löschen",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choose == JOptionPane.YES_OPTION) {
                navBar.removeNavigationObject(actualNavNumber);
            }
        }
    }

    /* Oeffnet einen Dialog, um ein neues ProduktEQConfig-Objekt zu definieren. */
    private void addEQConfig() {
        try {
            ProduktEQConfigDialog dlg = new ProduktEQConfigDialog(model);
            Object config = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (config instanceof ProduktEQConfig) {
                ProduktEQConfig produktEQConfig = (ProduktEQConfig) config;
                tbMdlProdEqConfig.addObject(produktEQConfig);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Loescht die ausgewaehlte ProduktEQConfig. */
    private void deleteEQConfig() {
        try {
            @SuppressWarnings("unchecked")
            Object selection = ((AKMutableTableModel<ProduktEQConfig>) tbProdEqConfig.getModel())
                    .getDataAtRow(tbProdEqConfig.getSelectedRow());
            if (selection instanceof ProduktEQConfig) {
                int choose = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Selektierte EQ-Konfiguration löschen?", "Löschen?");
                if (choose == JOptionPane.YES_OPTION) {
                    RangierungAdminService ras = getCCService(RangierungAdminService.class);
                    ras.deleteProduktEQConfig((ProduktEQConfig) selection);

                    tbMdlProdEqConfig.removeObject(selection);
                }
            }
            else {
                throw new HurricanGUIException("Keine Produkt-Equipment Konfiguration gewaehlt.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        if (guiCreated) {
            setP2PTValues();
            if (obj instanceof Produkt2PhysikTyp) {
                clearAll();
                this.actualP2PT = (Produkt2PhysikTyp) obj;
                cbPhysikTyp.selectItem("getId", PhysikTyp.class, actualP2PT.getPhysikTypId());
                cbPhysikTypAdd.selectItem("getId", PhysikTyp.class, actualP2PT.getPhysikTypAdditionalId());
                chbVirtuell.setSelected(actualP2PT.getVirtuell());
                chbUseInRangMatrix.setSelected(actualP2PT.getUseInRangMatrix());
                cbParentPT.selectItem("getId", PhysikTyp.class, actualP2PT.getParentPhysikTypId());
                tfP2PtPriority.setValue(actualP2PT.getPriority());
                actualNavNumber = number;
            }
            else {
                clearAll();
                actualNavNumber = -1;
            }

            if (navBar.getNavCount() == 0) {
                enableP2PT(false);
            }
            else {
                enableP2PT(true);
            }
        }
    }

    /* Setzt die Daten der Produkt-PhysikTyp-Zuordnung in das aktuelle Mapping-Objekt. */
    private void setP2PTValues() {
        if (this.actualP2PT != null) {
            this.actualP2PT.setPhysikTypId((cbPhysikTyp.getSelectedItem() instanceof PhysikTyp)
                    ? ((PhysikTyp) cbPhysikTyp.getSelectedItem()).getId() : null);
            this.actualP2PT.setPhysikTypAdditionalId((cbPhysikTypAdd.getSelectedItem() instanceof PhysikTyp)
                    ? ((PhysikTyp) cbPhysikTypAdd.getSelectedItem()).getId() : null);
            this.actualP2PT.setVirtuell(chbVirtuell.isSelected());
            this.actualP2PT.setUseInRangMatrix(chbUseInRangMatrix.isSelected());
            this.actualP2PT.setParentPhysikTypId((cbParentPT.getSelectedItem() instanceof PhysikTyp)
                    ? ((PhysikTyp) cbParentPT.getSelectedItem()).getId() : null);
            this.actualP2PT.setPriority(tfP2PtPriority.getValueAsLong(null));
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            // PhysikTyp-Zuordnungen speichern
            PhysikService physikService = getCCService(PhysikService.class);
            setP2PTValues();
            @SuppressWarnings("unchecked")
            List<Produkt2PhysikTyp> p2pts = (navBar.getData() != null) ? (List<Produkt2PhysikTyp>) navBar.getData()
                    : new ArrayList<>();
            physikService.saveP2PTs4Produkt(model.getId(), p2pts);
            // Priorisierte Connection Typen speichern
            savePriorities();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if (model != null) {
            model.setSdslNdraht((SdslNdraht) cbNdraht.getSelectedItemValue());
        }
        return model;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* 'Loescht' alle Felder */
    private void clearAll() {
        GuiTools.cleanFields(this);
    }

}
