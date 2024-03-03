/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 13:57:04
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.shared.HVTStandortListCellRenderer;
import de.augustakom.hurrican.gui.shared.Produkt2PhysikTypCellRenderer;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Admin-Panel fuer die Verwaltung der Rangierungsmatrix.
 *
 *
 */
public class RangierungsmatrixAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(RangierungsmatrixAdminPanel.class);

    // GUI-Elemente
    private AKJComboBox cbHvtFilter = null;
    private HVTStandortListCellRenderer cbHvtRenderer = null;
    private AKJComboBox cbUevtFilter = null;
    private AKJList lsProdukte = null;
    private DefaultListModel lsMdlProdukte = null;
    private RMatrixTableModel tbMdlMatrix = null;

    private AKJButton btnNew = null;
    private AKJButton btnSave = null;
    private AKJButton btnDelete = null;
    private AKJComboBox cbProdukt2PT = null;
    private AKJComboBox cbUevt = null;
    private AKJComboBox cbProdukt = null;
    private Produkt2PhysikTypCellRenderer cbP2PTRenderer = null;
    private AKJComboBox cbHvtZiel = null;
    private AKJFormattedTextField tfPriority = null;
    private AKJCheckBox chbProjektierung = null;
    private AKJTextField tfBearbeiter = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;
    private AKJButton btnCompleteMatrix = null;

    // Modelle
    private Rangierungsmatrix model = null;

    // Sonstiges
    private Map<Long, HVTGruppe> hvtGruppenMap = null;
    private Map<Long, UEVT> uevtMap = null;
    private Map<Long, Produkt> produktMap = null;
    private Map<Long, PhysikTyp> physikTypMap = null;

    private AKManageableComponent[] managedGuiComponents;

    /**
     * Konstruktor.
     */
    public RangierungsmatrixAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/RangierungsmatrixAdminPanel.xml");
        init();
        createGUI();
    }

    /* Initialisiert das Panel. */
    private void init() {
        produktMap = new HashMap<Long, Produkt>();
        physikTypMap = new HashMap<Long, PhysikTyp>();
        hvtGruppenMap = new HashMap<Long, HVTGruppe>();
        uevtMap = new HashMap<Long, UEVT>();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblHvtFilter = getSwingFactory().createLabel("hvt.filter");
        AKJLabel lblUevtFilter = getSwingFactory().createLabel("uevt.filter");
        AKJLabel lblProdukte = getSwingFactory().createLabel("produkte");
        AKJLabel lblUevt = getSwingFactory().createLabel("uevt");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblProdukt2PT = getSwingFactory().createLabel("produkt2PhysikTyp");
        AKJLabel lblHvtZiel = getSwingFactory().createLabel("hvt.ziel");
        AKJLabel lblPriority = getSwingFactory().createLabel("priority");
        AKJLabel lblProjektierung = getSwingFactory().createLabel("projektierung");
        AKJLabel lblBearbeiter = getSwingFactory().createLabel("bearbeiter");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.bis");

        AKJButton btnFilter = getSwingFactory().createButton("filter", getActionListener());
        btnNew = getSwingFactory().createButton("new", getActionListener(), null);
        btnSave = getSwingFactory().createButton("save", getActionListener(), null);
        btnDelete = getSwingFactory().createButton("delete", getActionListener(), null);

        cbHvtRenderer = new HVTStandortListCellRenderer();
        cbHvtFilter = getSwingFactory().createComboBox("hvt.filter", cbHvtRenderer);
        cbHvtFilter.addItemListener(new HVTItemListener());
        cbUevtFilter = getSwingFactory().createComboBox("uevt.filter", new AKCustomListCellRenderer<>(UEVT.class, UEVT::getUevt));
        lsMdlProdukte = new DefaultListModel();
        lsProdukte = getSwingFactory().createList("produkte", lsMdlProdukte);
        lsProdukte.setCellRenderer(new AKCustomListCellRenderer<>(Produkt.class, Produkt::getBezeichnung));
        AKJScrollPane spProdukte = new AKJScrollPane(lsProdukte);
        spProdukte.setPreferredSize(new Dimension(200, 130));
        tbMdlMatrix = new RMatrixTableModel();
        AKJTable tbMatrix = new AKJTable(tbMdlMatrix, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbMatrix.fitTable(new int[] { 70, 250, 200, 50 });
        tbMatrix.addMouseListener(getTableListener());
        tbMatrix.addKeyListener(getTableListener());
        AKJScrollPane spMatrix = new AKJScrollPane(tbMatrix);
        spMatrix.setPreferredSize(new Dimension(450, 180));

        cbUevt = getSwingFactory().createComboBox("uevt", new AKCustomListCellRenderer<>(UEVT.class, UEVT::getBezeichnung));
        cbUevt.setEnabled(false);
        cbProdukt = getSwingFactory().createComboBox("produkt", new AKCustomListCellRenderer<>(Produkt.class, Produkt::getBezeichnung));
        cbProdukt.setEnabled(false);
        cbProdukt.addItemListener(new ProduktItemListener());
        cbP2PTRenderer = new Produkt2PhysikTypCellRenderer();
        cbProdukt2PT = getSwingFactory().createComboBox("produkt2PhysikTyp", cbP2PTRenderer);
        cbProdukt2PT.setEnabled(false);
        cbProdukt2PT.addItemListener(new P2PTItemListener());
        cbHvtZiel = getSwingFactory().createComboBox("hvt.ziel", cbHvtRenderer);
        tfPriority = getSwingFactory().createFormattedTextField("priority");
        chbProjektierung = getSwingFactory().createCheckBox("projektierung");
        tfBearbeiter = getSwingFactory().createTextField("bearbeiter", false);
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");
        dcGueltigVon.setEnabled(false);
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        dcGueltigBis.setEnabled(false);
        btnCompleteMatrix = getSwingFactory().createButton("create.complete.matrix", getActionListener());

        // Filter-Panel
        AKJPanel filter = new AKJPanel(new GridBagLayout());
        filter.setBorder(BorderFactory.createTitledBorder("Filter"));
        filter.add(lblHvtFilter, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        filter.add(cbHvtFilter, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(lblUevtFilter, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(cbUevtFilter, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(lblProdukte, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(spProdukte, GBCFactory.createGBC(100, 100, 2, 2, 1, 4, GridBagConstraints.BOTH));
        filter.add(btnFilter, GBCFactory.createGBC(0, 0, 2, 7, 1, 1, GridBagConstraints.NONE));

        // Matrix-Panel
        AKJPanel matrix = new AKJPanel(new GridBagLayout());
        matrix.setBorder(BorderFactory.createTitledBorder("Rangierungsmatrix"));
        matrix.add(spMatrix, GBCFactory.createGBC(100, 100, 0, 0, 1, 0, GridBagConstraints.BOTH));

        // Detail-Panel
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(btnNew, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 2, 10)));
        left.add(btnSave, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 2, 10)));
        left.add(btnDelete, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblUevt, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        left.add(cbUevt, GBCFactory.createGBC(100, 0, 2, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProdukt, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbProdukt, GBCFactory.createGBC(100, 0, 2, 2, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProdukt2PT, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbProdukt2PT, GBCFactory.createGBC(100, 0, 2, 3, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblHvtZiel, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbHvtZiel, GBCFactory.createGBC(100, 0, 2, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPriority, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfPriority, GBCFactory.createGBC(100, 0, 2, 5, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProjektierung, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbProjektierung, GBCFactory.createGBC(100, 0, 2, 6, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBearbeiter, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBearbeiter, GBCFactory.createGBC(100, 0, 2, 7, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcGueltigVon, GBCFactory.createGBC(100, 0, 2, 8, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcGueltigBis, GBCFactory.createGBC(100, 0, 2, 9, 4, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        right.add(btnCompleteMatrix, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));

        AKJPanel detail = new AKJPanel(new GridBagLayout());
        detail.setBorder(BorderFactory.createTitledBorder("Details"));
        detail.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        detail.add(right, GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(filter, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(matrix, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.BOTH, new Insets(2, 10, 2, 2)));
        this.add(detail, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));

        managedGuiComponents = new AKManageableComponent[] { btnFilter, btnNew, btnSave, btnDelete, btnCompleteMatrix };
        manageGUI(managedGuiComponents);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        clearDetails();
        if (details instanceof Rangierungsmatrix) {
            try {
                this.model = (Rangierungsmatrix) details;
                cbUevt.setEnabled(false);
                cbProdukt.setEnabled(false);
                cbProdukt2PT.setEnabled(false);

                cbUevt.selectItem("getId", UEVT.class, model.getUevtId());
                cbProdukt.selectItem("getId", Produkt.class, model.getProduktId());
                cbProdukt2PT.selectItem("getId", Produkt2PhysikTyp.class, model.getProdukt2PhysikTypId());
                cbHvtZiel.selectItem("getId", HVTStandort.class, model.getHvtStandortIdZiel());
                tfPriority.setValue(model.getPriority());
                chbProjektierung.setSelected(model.getProjektierung());
                tfBearbeiter.setText(model.getBearbeiter());
                dcGueltigVon.setDate(model.getGueltigVon());
                dcGueltigBis.setDate(model.getGueltigBis());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
        else {
            this.model = null;
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            // HVT-Gruppen laden
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppe> hvtGruppen = hvts.findHVTGruppen();
            CollectionMapConverter.convert2Map(hvtGruppen, hvtGruppenMap, "getId", null);
            cbHvtRenderer.setHVTGruppen(hvtGruppenMap);

            // HVT-Standorte laden
            List<HVTStandort> hvtstds = hvts.findHVTStandorte();
            cbHvtFilter.addItems(hvtstds, true, HVTStandort.class);

            // UEVTs laden
            List<UEVT> uevts = hvts.findUEVTs();
            cbUevt.addItems(uevts, true, UEVT.class);
            CollectionMapConverter.convert2Map(uevts, uevtMap, "getId", null);

            // Produkte laden
            ProduktService ps = getCCService(ProduktService.class);
            List<Produkt> produkte = ps.findProdukte(false);
            lsMdlProdukte.addElement(new Produkt());
            lsProdukte.copyList2Model(produkte, lsMdlProdukte);
            cbProdukt.addItems(produkte, true, Produkt.class);
            CollectionMapConverter.convert2Map(produkte, produktMap, "getId", null);
            cbP2PTRenderer.setProdukte(produktMap);

            // PhysikTypen laden
            PhysikService phs = getCCService(PhysikService.class);
            List<PhysikTyp> ptypen = phs.findPhysikTypen();
            CollectionMapConverter.convert2Map(ptypen, physikTypMap, "getId", null);
            cbP2PTRenderer.setPhysikTypen(physikTypMap);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Laedt die Matrix fuer den eingestellten Filter. */
    private void loadMatrix() {
        try {
            clearDetails();
            if ((getUevtFilter() == null) && (getProduktFilter() == null)) {
                MessageHelper.showInfoDialog(this,
                        "Bitte wählen Sie zuerst einen UEVT und/oder ein Produkt aus.", null, true);
            }

            // Matrix laden
            setWaitCursor();
            RangierungsService rs = getCCService(RangierungsService.class);
            RangierungsmatrixQuery query = new RangierungsmatrixQuery();
            query.setUevtId(getUevtFilter());
            query.setProduktId(getProduktFilter());
            List<Rangierungsmatrix> matrix = rs.findMatrix(query);
            tbMdlMatrix.setData(matrix);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Laedt die Produkt-Physiktyp-Zuordnungen zu einem best. Produkt. */
    private List<Produkt2PhysikTyp> findP2PTs(Long produktId) {
        try {
            setWaitCursor();
            PhysikService ps = getCCService(PhysikService.class);
            return ps.findP2PTs4Produkt(produktId, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        clearDetails();
        this.model = new Rangierungsmatrix();
        cbUevt.selectItem("getId", UEVT.class, getUevtFilter());
        cbProdukt.selectItem("getId", Produkt.class, getProduktFilter());
        cbProdukt2PT.setEnabled(true);
        cbProdukt.setEnabled(true);
        cbUevt.setEnabled(true);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        Object uevt = cbUevt.getSelectedItem();
        Long uevtId = (uevt instanceof UEVT) ? ((UEVT) uevt).getId() : null;
        Object produkt = cbProdukt.getSelectedItem();
        Long produktId = (produkt instanceof Produkt) ? ((Produkt) produkt).getId() : null;

        boolean doSave = true;
        if ((model.getId() != null)
                && (!NumberTools.equal(model.getUevtId(), uevtId) ||
                !NumberTools.equal(model.getProduktId(), produktId))) {
            StringBuilder msg = new StringBuilder(getSwingFactory().getText("data.changed"));
            msg.append("\n");
            msg.append(getSwingFactory().getText("data.changed2"));
            int option = MessageHelper.showConfirmDialog(this,
                    msg.toString(), getSwingFactory().getText("data.changed.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            doSave = (option == JOptionPane.YES_OPTION) ? true : false;
        }

        if (doSave) {
            try {
                setWaitCursor();

                model.setUevtId(uevtId);
                model.setProduktId(produktId);
                model.setProdukt2PhysikTypId((cbProdukt2PT.getSelectedItem() instanceof Produkt2PhysikTyp)
                        ? ((Produkt2PhysikTyp) cbProdukt2PT.getSelectedItem()).getId() : null);
                model.setProjektierung(chbProjektierung.isSelectedBoolean());
                model.setPriority(tfPriority.getValueAsInt(null));
                model.setHvtStandortIdZiel((cbHvtZiel.getSelectedItem() instanceof HVTStandort)
                        ? ((HVTStandort) cbHvtZiel.getSelectedItem()).getId() : null);

                RangierungsService rs = getCCService(RangierungsService.class);
                this.model = rs.saveMatrix(model, HurricanSystemRegistry.instance().getSessionId(), true);
                loadMatrix();
                showDetails(this.model);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /* Loescht die aktuelle Matrix. */
    public void deleteMatrix() {
        if (this.model != null) {
            try {
                setWaitCursor();

                int opt = MessageHelper.showConfirmDialog(this,
                        getSwingFactory().getText("delete.matrix"), getSwingFactory().getText("delete.matrix.title"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) {
                    RangierungsService rs = getCCService(RangierungsService.class);
                    rs.deleteMatrix(this.model, HurricanSystemRegistry.instance().getSessionId());
                    this.model = null;
                    clearDetails();
                    loadMatrix();
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
    }

    /* 'Loescht' die Detail-Anzeige */
    private void clearDetails() {
        cbUevt.setSelectedIndex(-1);
        cbProdukt.setSelectedIndex(-1);
        cbProdukt2PT.setSelectedIndex(-1);
        cbHvtZiel.setSelectedIndex(-1);
        tfPriority.setText("");
        chbProjektierung.setSelected(false);
        tfBearbeiter.setText("");
        dcGueltigVon.setDate(null);
        dcGueltigBis.setDate(null);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("filter".equals(command)) {
            loadMatrix();
        }
        else if ("new".equals(command)) {
            createNew();
        }
        else if ("save".equals(command)) {
            saveData();
        }
        else if ("delete".equals(command)) {
            deleteMatrix();
        }
        else if ("create.complete.matrix".equals(command)) {
            createCompleteMatrix();
        }
    }

    /* Erstellt fuer alle Produkte und UEVTs eine Standard-Matrix. */
    private void createCompleteMatrix() {
        int opt = MessageHelper.showConfirmDialog(this, getSwingFactory().getText("create.complete.matrix.msg"),
                getSwingFactory().getText("create.complete.matrix.title"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {

            final SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {

                @Override
                protected Integer doInBackground() throws Exception {
                    ProduktService ps = getCCService(ProduktService.class);
                    List<Produkt> produkte = ps.findProdukte(true);
                    List<Long> prodIds = new ArrayList<Long>();
                    if (produkte != null) {
                        for (Produkt p : produkte) {
                            prodIds.add(p.getId());
                        }
                    }

                    HVTService hs = getCCService(HVTService.class);
                    List<UEVT> uevts = hs.findUEVTs();
                    List<Long> uevtIds = new ArrayList<Long>();
                    if (uevts != null) {
                        for (UEVT u : uevts) {
                            uevtIds.add(u.getId());
                        }
                    }

                    Long sessionId = HurricanSystemRegistry.instance().getSessionId();

                    RangierungsService rs = getCCService(RangierungsService.class);
                    List<Rangierungsmatrix> result = rs.createMatrix(sessionId, uevtIds, prodIds, null);
                    return (result != null) ? result.size() : 0;
                }

                @Override
                protected void done() {
                    try {
                        int anzahl = get();
                        MessageHelper.showMessageDialog(getMainFrame(),
                                getSwingFactory().getText("anzahl.matrizen", anzahl),
                                "Anzahl angelegter Matrizen", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        enableGuiButtons(true);
                        stopProgressBar();
                        setDefaultCursor();
                    }
                }

            };

            setWaitCursor();
            enableGuiButtons(false);
            showProgressBar("Matrix wird erstellt...");

            worker.execute();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Gibt die ID des im Filter ausgewaehlten UEVTs zurueck. */
    private Long getUevtFilter() {
        UEVT uevt = (cbUevtFilter.getSelectedItem() instanceof UEVT) ? (UEVT) cbUevtFilter.getSelectedItem() : null;
        return (uevt != null) ? uevt.getId() : null;
    }

    /* Gibt die ID des im Filter ausgewaehlten Produkts zurueck. */
    private Long getProduktFilter() {
        Produkt p = null;
        int pIndex = lsProdukte.getSelectedIndex();
        if (pIndex >= 0) {
            p = (lsMdlProdukte.get(pIndex) instanceof Produkt) ? (Produkt) lsMdlProdukte.get(pIndex) : null;
        }
        return (p != null) ? p.getId() : null;
    }

    private void enableGuiButtons(boolean enable) {
        btnCompleteMatrix.setEnabled(enable);
        btnNew.setEnabled(enable);
        btnSave.setEnabled(enable);
        btnDelete.setEnabled(enable);
        if (enable) {
            manageGUI(managedGuiComponents);
        }
    }

    /* ItemListener fuer die HVT-ComboBox des Filter-Panels. Laedt die zum ausgewaehlten HVT gehoerenden UEVTs. */
    class HVTItemListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object sel = cbHvtFilter.getSelectedItem();
                cbUevtFilter.removeAllItems();
                if (sel instanceof HVTStandort) {
                    try {
                        HVTService hvts = getCCService(HVTService.class);
                        List<UEVT> uevts = hvts.findUEVTs4HVTStandort(((HVTStandort) sel).getId());
                        cbUevtFilter.addItems(uevts, true, UEVT.class);
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }

    /* ItemListener fuer die Produkt-ComboBox. Laedt die Produkt-Physiktyp-Zuordnungen zu dem Produkt. */
    class ProduktItemListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object sel = cbProdukt.getSelectedItem();
                cbProdukt2PT.removeAllItems();
                if (sel instanceof Produkt) {
                    List<Produkt2PhysikTyp> p2pts = findP2PTs(((Produkt) sel).getId());
                    cbProdukt2PT.addItems(p2pts, true, Produkt2PhysikTyp.class);
                }
            }
        }
    }

    /* ItemListener fuer die ComboBox mit den Produkt-Physiktyp-Zuordnungen. */
    class P2PTItemListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object sel = cbProdukt2PT.getSelectedItem();
                cbHvtZiel.removeAllItems();
                if (sel instanceof Produkt2PhysikTyp) {
                    try {
                        Object uevt = cbUevt.getSelectedItem();
                        Long uevtId = null;
                        if (uevt instanceof UEVT) {
                            uevtId = ((UEVT) uevt).getId();
                        }

                        HVTService hs = getCCService(HVTService.class);
                        List<HVTStandort> hvtStds =
                                hs.findHVTStdZiele(uevtId, ((Produkt2PhysikTyp) sel).getId());
                        cbHvtZiel.addItems(hvtStds, true, HVTStandort.class);
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }

    /* TableModel fuer die Rangierungsmatrix. */
    class RMatrixTableModel extends AKTableModel {
        static final int COL_UEVT = 0;
        static final int COL_P2PT = 1;
        static final int COL_HVT_ZIEL = 2;
        static final int COL_PRIO = 3;

        static final int COL_COUNT = 4;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_UEVT:
                    return "UEVT";
                case COL_P2PT:
                    return "Produkt/Physiktyp";
                case COL_HVT_ZIEL:
                    return "Ziel-HVT";
                case COL_PRIO:
                    return "Priorität";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            try {
                Object o = getDataAtRow(row);
                if (o instanceof Rangierungsmatrix) {
                    Rangierungsmatrix rm = (Rangierungsmatrix) o;
                    switch (column) {
                        case COL_UEVT:
                            Object uevt = uevtMap.get(rm.getUevtId());
                            return (uevt instanceof UEVT) ? ((UEVT) uevt).getUevt() : null;
                        case COL_P2PT:
                            PhysikService ps = getCCService(PhysikService.class);
                            Produkt2PhysikTyp p2pt = ps.findP2PT(rm.getProdukt2PhysikTypId());
                            if (p2pt == null) {
                                return "";
                            }
                            StringBuilder text = new StringBuilder();
                            text.append(p2pt.getId());
                            text.append(" - ");
                            if (produktMap != null) {
                                Produkt p = produktMap.get(p2pt.getProduktId());
                                text.append((p != null) ? p.getBezeichnungShort() : "?");
                            }
                            if (physikTypMap != null) {
                                PhysikTyp pt = physikTypMap.get(p2pt.getPhysikTypId());
                                text.append(" / ");
                                text.append((pt != null) ? pt.getName() : "?");
                            }
                            if ((p2pt.getVirtuell() != null) && p2pt.getVirtuell().booleanValue()) {
                                text.append(" (virtuell)");
                            }
                            return text.toString();

                        case COL_HVT_ZIEL:
                            HVTGruppe g = null;
                            if (hvtGruppenMap != null) {
                                g = hvtGruppenMap.get(rm.getHvtStandortIdZiel());
                            }

                            if (g != null) {
                                StringBuilder hvtText = new StringBuilder();
                                hvtText.append(g.getOrtsteil());
                                hvtText.append(" - ");
                                hvtText.append(rm.getHvtStandortIdZiel());
                                return hvtText.toString();
                            }
                            else if (rm.getHvtStandortIdZiel() != null) {
                                return rm.getHvtStandortIdZiel().toString();
                            }
                            return "";
                        case COL_PRIO:
                            return rm.getPriority();
                        default:
                            break;
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }

            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int ci) {
            return ((ci == COL_UEVT) || (ci == COL_PRIO)) ? Integer.class : String.class;
        }
    }
}


