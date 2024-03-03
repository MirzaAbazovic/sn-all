/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2004 10:56:07
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.ClipboardTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.DNLeistungsViewTableModel;
import de.augustakom.hurrican.gui.shared.RufnummerTableModel;
import de.augustakom.hurrican.gui.tools.dn.DNLeistungUebernahmeDialog;
import de.augustakom.hurrican.gui.tools.dn.DnLeistung2AuftragDialog;
import de.augustakom.hurrican.gui.tools.dn.DnLeistung2AuftragKuendigenDialog;
import de.augustakom.hurrican.gui.utils.SimpleHelperModel;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Panel fuer die Darstellung der Rufnummern, die dem Auftrag zugeordnet sind. <br> Die Daten werden aus der Billing-DB
 * abgefragt.
 *
 *
 */
public class AuftragRufnummerPanel extends AbstractDataPanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(AuftragRufnummerPanel.class);

    private static final String WATCH_AUFTRAG_DATEN = "watch.auftrag.daten.telbuch";
    private static final long serialVersionUID = 5096088275999899927L;
    private static final Color NOT_DEFAULT_BG = new Color(0, 153, 153);
    private static final String ADD_DN = "add.dn";
    private static final String DEL_DN = "del.dn";

    // GUI
    private AKJComboBox cbTelbuch = null;
    private AKJComboBox cbInvers = null;
    private AKJTable tbRufnummer = null;
    private RufnummerTableModel tbMdlRufnummer = null;
    private List<Rufnummer> rufnummern = null;
    private AKJTable tbDNLeistungen = null;
    private DNLeistungsViewTableModel tbMdlDNLeistungen = null;
    private List<DNLeistungsView> leistungen = null;
    private AKJButton btnAddDN = null;
    private AKJButton btnDelDN = null;

    // Modelle
    private CCAuftragModel model = null;
    private AuftragDaten auftragDaten = null;
    private Leistung productLeistung = null;
    private HWSwitch hwSwitch = null;

    private boolean inLoad = false;

    private AKManageableComponent[] managedComponents;

    /**
     * Konstruktor
     */
    public AuftragRufnummerPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragRufnummerPanel.xml");
        createGUI();
        loadDefaultData();
    }

    HWSwitch getHwSwitch(Long auftragId) throws ServiceNotFoundException {
        if (auftragId == null) {
            throw new IllegalArgumentException(
                    "Fuer die Erkennung eines Switches ist eine korrekte AuftragId notwendig!");
        }
        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        return auftragService.getSwitchKennung4Auftrag(auftragId);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTelbuch = getSwingFactory().createLabel("telefonbuch");
        AKJLabel lblInvers = getSwingFactory().createLabel("inverssuche");
        Dimension cbDim = new Dimension(150, 20);
        cbTelbuch = getSwingFactory().createComboBox("telefonbuch",
                new AKCustomListCellRenderer<>(SimpleHelperModel.class, SimpleHelperModel::getText));
        cbTelbuch.setPreferredSize(cbDim);
        cbInvers = getSwingFactory().createComboBox("inverssuche",
                new AKCustomListCellRenderer<>(SimpleHelperModel.class, SimpleHelperModel::getText));
        cbInvers.setPreferredSize(cbDim);

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder("Telefonbuchdaten"));
        top.add(lblTelbuch,
                GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 15, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(cbTelbuch, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblInvers,
                GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 15, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        top.add(cbInvers, GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        CopyDNLeistungenAction copyDnAction = new CopyDNLeistungenAction();
        copyDnAction.setParentClass(this.getClass());

        tbMdlRufnummer = new RufnummerTableModel();
        tbRufnummer = new AKJTable(tbMdlRufnummer, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRufnummer.attachSorter();
        tbRufnummer.fitTable(new int[] { 70, 70, 50, 50, 60, 35, 60, 60, 100, 50, 70, 80, 50, 50, 50, 70, 70, 70, 50,
                50 });
        tbRufnummer.addMouseListener(new RNSelectionListener());
        tbRufnummer.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbRufnummer.addKeyListener(new TblKeyListener());
        tbRufnummer.addPopupAction(new CopyRufnummerAction());
        tbRufnummer.addPopupAction(new CopyAllRufnummernAction());
        tbRufnummer.addPopupAction(copyDnAction);
        tbRufnummer.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (tbRufnummer.getSelectedRow() < 0) {
                        btnAddDN.setEnabled(false);
                    }
                    else {
                        btnAddDN.setEnabled(true);
                    }
                    updateGUILeistungen();
                }
            }
        });
        AKJScrollPane rnTable = new AKJScrollPane(tbRufnummer);
        rnTable.setPreferredSize(new Dimension(500, 100));

        AKJPanel rnPanel = new AKJPanel(new BorderLayout());
        rnPanel.setBorder(BorderFactory.createTitledBorder("Rufnummern"));
        rnPanel.add(rnTable, BorderLayout.CENTER);

        // Single Selection, weil nicht für alle Leistungen der selbe 'Loeschen' Dialog verwendet wird (siehe
        // Sperrklasse).
        // Sollten also Leistungen mit unterschiedlichen Dialogen markiert sein, muesste eine 'Kreuzung' als Loesch
        // Dialog
        // dem Benutzer angeboten werden.
        tbMdlDNLeistungen = new DNLeistungsViewTableModel();
        tbDNLeistungen = new DNLeistungTable(tbMdlDNLeistungen, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        tbDNLeistungen.attachSorter();
        tbDNLeistungen.fitTable(new int[] { 30, 50, 150, 65, 65, 65, 65, 65, 65, 65, 65, 150, 50 });
        tbDNLeistungen.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbDNLeistungen.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (tbDNLeistungen.getSelectedRow() < 0) {
                        btnDelDN.setEnabled(false);
                    }
                    else {
                        btnDelDN.setEnabled(true);
                    }
                }
            }
        });

        AKJScrollPane spDNTable = new AKJScrollPane(tbDNLeistungen);
        spDNTable.setPreferredSize(new Dimension(500, 100));

        AKJPanel dnTablePanel = new AKJPanel(new BorderLayout());
        dnTablePanel.add(spDNTable, BorderLayout.CENTER);

        btnAddDN = getSwingFactory().createButton(ADD_DN, getActionListener(), null);
        btnDelDN = getSwingFactory().createButton(DEL_DN, getActionListener(), null);
        btnAddDN.setEnabled(false);
        btnDelDN.setEnabled(false);
        AKJPanel dnButtonPanel = new AKJPanel(new GridBagLayout());
        dnButtonPanel.add(btnAddDN, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dnButtonPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        dnButtonPanel.add(btnDelDN, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        dnButtonPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel dnPanel = new AKJPanel(new BorderLayout());
        dnPanel.setBorder(BorderFactory.createTitledBorder("Leistungen zu Rufnummern"));
        dnPanel.add(dnButtonPanel, BorderLayout.WEST);
        dnPanel.add(dnTablePanel, BorderLayout.CENTER);

        TableColumn col = tbRufnummer.getColumnModel().getColumn(RufnummerTableModel.COL_PORTIERUNG_AM);
        col.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        TableColumn col2 = tbRufnummer.getColumnModel().getColumn(RufnummerTableModel.COL_PORTIERUNG_VON);
        col2.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_TIME));
        TableColumn col3 = tbRufnummer.getColumnModel().getColumn(RufnummerTableModel.COL_PORTIERUNG_BIS);
        col3.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_TIME));

        this.setLayout(new GridBagLayout());
        this.add(top, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(rnPanel, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        this.add(dnPanel, GBCFactory.createGBC(100, 100, 0, 2, 1, 1, GridBagConstraints.BOTH));

        managedComponents = new AKManageableComponent[] { copyDnAction };
        manageGUI(managedComponents);
    }

    /* Laedt die Standard-Daten fuer das Panel. */
    private void loadDefaultData() {
        List<SimpleHelperModel> cbData = new ArrayList<>();
        cbData.add(new SimpleHelperModel(" "));
        cbData.add(new SimpleHelperModel(Integer.valueOf(AuftragDaten.EINTRAG_ERLEDIGT), "erledigt"));
        cbData.add(new SimpleHelperModel(Integer.valueOf(AuftragDaten.EINTRAG_NICHT_GEWUENSCHT), "nicht gewünscht"));
        cbTelbuch.addItems(cbData, true);

        List<SimpleHelperModel> cbDataInvers = new ArrayList<>();
        cbDataInvers.add(new SimpleHelperModel(" "));
        cbDataInvers.add(new SimpleHelperModel(Integer.valueOf(AuftragDaten.EINTRAG_WIDERSPROCHEN), "widersprochen"));
        cbDataInvers.add(new SimpleHelperModel(Integer.valueOf(AuftragDaten.EINTRAG_ERLEDIGT), "erledigt"));
        cbDataInvers.add(new SimpleHelperModel(Integer.valueOf(AuftragDaten.EINTRAG_NICHT_GEWUENSCHT),
                "nicht gewünscht"));
        cbInvers.addItems(cbDataInvers, true);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = null;
        if (model instanceof CCAuftragModel) {
            this.model = (CCAuftragModel) model;
        }

        readModel();
    }

    private static class WorkerResult {
        AuftragDaten auftragDaten;
        Produkt produkt;
        Leistung leistung;
        List<Rufnummer> rufnummern;

    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        rufnummern = null;
        tbMdlRufnummer.removeAll();
        tbMdlDNLeistungen.removeAll();
        this.auftragDaten = null;
        this.productLeistung = null;

        if (model != null) {
            enableGuiElements(false);
            inLoad = true;
            setWaitCursor();

            final SwingWorker<WorkerResult, Void> worker = new SwingWorker<WorkerResult, Void>() {

                final Long auftragId = model.getAuftragId();

                @Override
                protected WorkerResult doInBackground() throws Exception {
                    WorkerResult wResult = new WorkerResult();
                    ProduktService ps = getCCService(ProduktService.class);
                    wResult.produkt = ps.findProdukt4Auftrag(auftragId);

                    CCAuftragService ccAS = getCCService(CCAuftragService.class);
                    wResult.auftragDaten = ccAS.findAuftragDatenByAuftragId(auftragId);

                    if (wResult.auftragDaten != null) {
                        LeistungService ls = getBillingService(LeistungService.class);
                        wResult.leistung = ls.findProductLeistung4Auftrag(auftragId,
                                ProduktMapping.MAPPING_PART_TYPE_PHONE);

                        if (wResult.auftragDaten.getAuftragNoOrig() != null) {
                            RufnummerService rs = getBillingService(RufnummerService.class.getName(),
                                    RufnummerService.class);
                            wResult.rufnummern = rs.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                                    new Object[] { wResult.auftragDaten.getAuftragNoOrig(), Boolean.FALSE });
                        }
                    }
                    return wResult;
                }

                @Override
                public void done() {
                    try {
                        WorkerResult wResult = get();
                        enableGuiElements(true);
                        boolean needsDN = wResult.produkt.isDnAllowed();
                        cbTelbuch.setEnabled(needsDN);
                        cbInvers.setEnabled(needsDN);

                        auftragDaten = wResult.auftragDaten;
                        addObjectToWatch(WATCH_AUFTRAG_DATEN, auftragDaten);

                        if (auftragDaten != null) {
                            productLeistung = wResult.leistung;

                            cbTelbuch.selectItem("getId", SimpleHelperModel.class,
                                    (auftragDaten.getTelefonbuch() != null)
                                            ? Integer.valueOf(auftragDaten.getTelefonbuch()) : null
                            );
                            cbInvers.selectItem("getId", SimpleHelperModel.class,
                                    (auftragDaten.getInverssuche() != null)
                                            ? Integer.valueOf(auftragDaten.getInverssuche()) : null
                            );

                            if (auftragDaten.getAuftragNoOrig() != null) {
                                rufnummern = wResult.rufnummern;

                                tbMdlRufnummer.setData(rufnummern);
                                if (CollectionTools.isNotEmpty(rufnummern)) {
                                    loadLeistungen4RNs();
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        setDefaultCursor();
                        inLoad = false;
                    }
                }
            };
            worker.execute();
        }
    }

    private void enableGuiElements(boolean enable) {
        cbInvers.setEnabled(enable);
        cbTelbuch.setEnabled(enable);
        manageGUI(managedComponents);
    }

    /* Laedt die Leistungen, die den Rufnummern zugeordnet sind. */
    private void loadLeistungen4RNs() throws ServiceNotFoundException, FindException, HurricanGUIException {
        if (productLeistung == null) {
            throw new FindException("Die Produkt-Leistung aus dem Billing-System wurde nicht gefunden!");
        }

        if ((rufnummern != null) && !rufnummern.isEmpty()) {
            List<Long> dnNos = new ArrayList<>();
            Map<Long, Rufnummer> rnMap = new HashMap<>();
            for (Rufnummer rn : rufnummern) {
                dnNos.add(rn.getDnNo());
                rnMap.put(rn.getDnNo(), rn);
            }

            CCRufnummernService ccRS = getCCService(CCRufnummernService.class);
            Leistungsbuendel lb = ccRS.findLeistungsbuendel4Auftrag(auftragDaten.getAuftragId());
            if (lb == null) {
                throw new HurricanGUIException("Dem Produkt sind keine Rufnummernleistungen zugeordnet.");
            }
            leistungen = ccRS.findDNLeistungen(dnNos, lb.getId());
            List<LeistungParameter> parameter = ccRS.findAllParameter();
            Map<Long, LeistungParameter> pMap = new HashMap<>();
            for (LeistungParameter lp : parameter) {
                pMap.put(lp.getId(), lp);
            }

            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            BAuftrag ba = bas.findAuftrag(auftragDaten.getAuftragNoOrig());
            if (ba != null) {
                List<Lb2Leistung> defaultLeistungen = ccRS.findLeistungsbuendelKonfig(lb.getId(), true);
                Map<Long, Lb2Leistung> defLstMap = new HashMap<>();
                CollectionMapConverter.convert2Map(defaultLeistungen, defLstMap, "getLeistungId", null);
                tbMdlDNLeistungen.setDefaultLeistungsMap(defLstMap);
            }

            hwSwitch = getHwSwitch(auftragDaten.getAuftragId());

            tbMdlDNLeistungen.setRufnummerMap(rnMap);
            tbMdlDNLeistungen.setParameterMap(pMap);
            tbMdlDNLeistungen.setData(leistungen);
        }
    }

    /**
     * @return Returns the hwSwitch.
     */
    private HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
        try {
            if (!inLoad) {
                setWaitCursor();
                if (hasModelChanged()) {
                    Object[] models = (Object[]) getModel();
                    AuftragDaten toSave = (AuftragDaten) models[1];

                    CCAuftragService as = getCCService(CCAuftragService.class);
                    as.saveAuftragDaten(toSave, false);
                    addObjectToWatch(WATCH_AUFTRAG_DATEN, toSave);
                }
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

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if (auftragDaten != null) {
            SimpleHelperModel telbuch = (SimpleHelperModel) cbTelbuch.getSelectedItem();
            auftragDaten.setTelefonbuch(((telbuch != null) && (telbuch.getId() != null))
                    ? telbuch.getId().shortValue() : null);

            SimpleHelperModel invers = (SimpleHelperModel) cbInvers.getSelectedItem();
            auftragDaten.setInverssuche(((invers != null) && (invers.getId() != null))
                    ? invers.getId().shortValue() : null);
        }
        return new Object[] { model, auftragDaten };
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        if (!inLoad) {
            Object[] models = (Object[]) getModel();
            return hasChanged(WATCH_AUFTRAG_DATEN, models[1]);
        }
        return false;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (ADD_DN.equals(command) && tbRufnummer.getSelectedRow() >= 0) {
            AKMutableTableModel<?> tm = (AKMutableTableModel<?>) tbRufnummer.getModel();
            objectSelected(tm.getDataAtRow(tbRufnummer.getSelectedRow()));
        }
        else if (DEL_DN.equals(command) && tbDNLeistungen.getSelectedRow() >= 0) {
            AKMutableTableModel<?> tm = (AKMutableTableModel<?>) tbDNLeistungen.getModel();
            objectSelected(tm.getDataAtRow(tbDNLeistungen.getSelectedRow()));
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof Rufnummer) {
            try {
                ProduktService ps = getCCService(ProduktService.class);
                Produkt p = ps.findProdukt4Auftrag(model.getAuftragId());

                DnLeistung2AuftragDialog dlg = new DnLeistung2AuftragDialog((Rufnummer) selection, p, auftragDaten);
                DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                loadLeistungen4RNs();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            updateGUILeistungen();
        }

        if (selection instanceof DNLeistungsView) {
            final DNLeistungsView dnLeistungViewSelected = (DNLeistungsView) selection;
            onDnSelectionChanged(dnLeistungViewSelected);
        }
    }

    private void onDnSelectionChanged(final DNLeistungsView dnLeistungViewSelected) {
        if (getHwSwitch() == null) {
            MessageHelper.showInfoDialog(this, "Switchkenner nicht verfügbar, Leistung kann nicht gekündigt werden!");
            return;
        }
        DnLeistung2AuftragKuendigenDialog dlg = new DnLeistung2AuftragKuendigenDialog(dnLeistungViewSelected,
                getHwSwitch().getType());
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);

        if (dlg.getValue().equals(JOptionPane.OK_OPTION)) {
            final Rufnummer selectedRufnummer = getSelectedRufnummer();

            if ((selectedRufnummer == null) && !dlg.isLeistungFuerAlleDnKuendigen()) {
                MessageHelper.showWarningDialog(this, "Bitte eine Rufnummer auswählen!", true);
            }
            else {
                kuendigeRufnummerLeistungen(dnLeistungViewSelected, dlg);
            }
        }
        updateGUILeistungen();
    }

    private void kuendigeRufnummerLeistungen(final DNLeistungsView dnLeistungViewSelected,
            final DnLeistung2AuftragKuendigenDialog dlg) {
        final List<Rufnummer> rufnummernDenenDieLeistungGekuendigtWird = (dlg
                .isLeistungFuerAlleDnKuendigen())
                ? ImmutableList.copyOf(rufnummern)
                : ImmutableList.of(getSelectedRufnummer());
        final String username = HurricanSystemRegistry.instance().getCurrentUserName();
        final Date kuendigenAm = dlg.getKuendigenAm();

        try {
            final CCRufnummernService rufnummerService = getCCService(CCRufnummernService.class);
            List<Leistung2DN> rufnrLeistungenCancled = rufnummerService.kuendigeLeistung4Rufnummern(
                    dnLeistungViewSelected.getLeistungsId(), kuendigenAm, username,
                    rufnummernDenenDieLeistungGekuendigtWird);
            if (rufnummernDenenDieLeistungGekuendigtWird.size() > rufnrLeistungenCancled.size()) {
                if (rufnummernDenenDieLeistungGekuendigtWird.size() > 1) {
                    MessageHelper.showInfoDialog(this,
                            "Eine oder mehrere Rufnummern sind nicht auf das neue Kündigungsdatum "
                                    + "umgestellt, da bereits eine Kündigung technisch realisiert ist! Bitte prüfen!"
                    );
                }
                else {
                    MessageHelper.showInfoDialog(this,
                            "Die Rufnummer ist nicht auf das neue Kündigungsdatum umgestellt, "
                                    + "da bereits eine Kündigung technisch realisiert ist!"
                    );
                }
            }
            loadLeistungen4RNs();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
            LOGGER.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Rufnummer getSelectedRufnummer() {
        final int row = tbRufnummer.getSelectedRow();
        return ((AKTableSorter<Rufnummer>) tbRufnummer.getModel()).getDataAtRow(row);
    }

    private void updateGUILeistungen() {
        tbMdlDNLeistungen.setData(null);

        // DN-NOs der selektierten Rufnummern ermitteln
        int[] selectedRows = tbRufnummer.getSelectedRows();
        Long[] dnNos2Select = new Long[tbRufnummer.getSelectedRowCount()];
        for (int i = 0; i < selectedRows.length; i++) {
            Object tmp = ((AKMutableTableModel<?>) tbRufnummer.getModel()).getDataAtRow(selectedRows[i]);
            if (tmp instanceof Rufnummer) {
                dnNos2Select[i] = (((Rufnummer) tmp).getDnNo());
            }
        }

        if (dnNos2Select.length > 0) {
            // nur die Leistungen zu den selektierten Rufnummern anzeigen
            // CollectionUtils is not Generics-Save
            @SuppressWarnings("unchecked")
            Collection<DNLeistungsView> leistungen4SelectedDNs = CollectionUtils.select(leistungen,
                    new Leistung4DNPredicate(dnNos2Select));
            tbMdlDNLeistungen.setData(leistungen4SelectedDNs);
        }
        else {
            // wenn keine Rufnummer ausgewaehlt ist, die Leistungen zu allen Rufnummern
            // anzeigen; gleiches Verhalten wie beim frisch geoeffnet Dialog bzw. nach
            // druecken der 'Escape' Taste
            tbMdlDNLeistungen.setData(leistungen);
        }
    }

    /* MouseListener, um die Leistungen zu den selektierten Rufnummern zu markieren. */
    class RNSelectionListener extends MouseAdapter {
        /**
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if ((e.getSource() == tbRufnummer) && CollectionTools.isNotEmpty(leistungen)) {
                updateGUILeistungen();
            }
        }
    }

    /* Predicate, um die Leistungen zu den angegebenen Rufnummern aus der Leistungs-Liste zu ermitteln */
    static class Leistung4DNPredicate implements Predicate {
        private Long[] dnNos2Select = null;

        private Leistung4DNPredicate(Long[] dnNos2Select) {
            this.dnNos2Select = dnNos2Select;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof DNLeistungsView) {
                for (Long dnNo : dnNos2Select) {
                    if (NumberTools.equal(((DNLeistungsView) obj).getDnNo(), dnNo)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /* Action, um die gerade selektierte Rufnummer in die Zwischenablage zu kopieren. */
    class CopyRufnummerAction extends AKAbstractAction {
        private static final long serialVersionUID = -5360619608822015801L;

        /**
         * Default-Konstruktor.
         */
        public CopyRufnummerAction() {
            super();
            setName("Rufnummer kopieren");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            AKMutableTableModel<?> mdl = (AKMutableTableModel<?>) tbRufnummer.getModel();
            int selection = tbRufnummer.getSelectedRow();
            if (selection >= 0) {
                Object tmp = mdl.getDataAtRow(selection);
                if (tmp instanceof Rufnummer) {
                    Rufnummer r = (Rufnummer) tmp;
                    ClipboardTools.copy2Clipboard(r.getRufnummer());
                }
            }
        }
    }

    /* Action, um alle dargestellten Rufnummern in die Zwischenablage zu kopieren */
    class CopyAllRufnummernAction extends AKAbstractAction {
        private static final long serialVersionUID = -2479575236145768963L;

        /**
         * Default-Konstruktor.
         */
        public CopyAllRufnummernAction() {
            super();
            setName("alle Rufnummern kopieren");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder rufnummern = new StringBuilder();
            boolean empty = true;
            for (int i = 0, count = tbMdlRufnummer.getRowCount(); i < count; i++) {
                Rufnummer tmp = tbMdlRufnummer.getDataAtRow(i);
                if (tmp != null) {
                    if (!empty) {
                        rufnummern.append(", ");
                    }
                    rufnummern.append(tmp.getRufnummer());
                    empty = false;
                }
            }
            ClipboardTools.copy2Clipboard(rufnummern.toString());
        }
    }

    /* Action, um einen Dialog zu oeffnen, ueber den DN-Leistungen kopiert werden koennen */
    class CopyDNLeistungenAction extends AKAbstractAction {
        private static final long serialVersionUID = 4744934904966083955L;

        /**
         * Default-Const
         */
        public CopyDNLeistungenAction() {
            super();
            setName("DN-Leistungen kopieren");
            setActionCommand("copy.dn.leistungen");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            AKMutableTableModel<?> mdl = (AKMutableTableModel<?>) tbRufnummer.getModel();
            int selection = tbRufnummer.getSelectedRow();
            if (selection >= 0) {
                Object tmp = mdl.getDataAtRow(selection);
                if (tmp instanceof Rufnummer) {
                    DNLeistungUebernahmeDialog dlg =
                            new DNLeistungUebernahmeDialog((Rufnummer) tmp, auftragDaten.getAuftragId());
                    Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    if (result instanceof Integer) {
                        Integer res = (Integer) result;
                        if (JOptionPane.OK_OPTION == res) {
                            readModel();
                        }
                    }
                }
            }
        }
    }

    /* Table-Implementierung fuer die Darstellung der Rufnummernleistungen. */
    static class DNLeistungTable extends AKJTable {
        private static final long serialVersionUID = -2133620196337508741L;

        /**
         * @param dm
         * @param autoResizeMode
         * @param selectionMode
         */
        public DNLeistungTable(TableModel dm, int autoResizeMode, int selectionMode) {
            super(dm, autoResizeMode, selectionMode);
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
         */
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            AKMutableTableModel<?> tbMdl = (AKMutableTableModel<?>) getModel();
            Object tmp = tbMdl.getValueAt(row, DNLeistungsViewTableModel.COL_DEFAULT_LEISTUNG);
            if ((tmp instanceof Boolean) && !(Boolean) tmp) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(NOT_DEFAULT_BG);
                return c;
            }
            return super.prepareRenderer(renderer, row, column);
        }
    }

    /**
     * KeyListener fuer die Tabellenansicht <br> Durch die Betaetigung von 'ESCAPE' wird die Standardanzeige wieder
     * geladen. Es wird die Ursprungsdaten in das Tabellenmodell geladen
     */
    class TblKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            // intentionally left blank
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // intentionally left blank
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                tbRufnummer.clearSelection();
            }
        }
    }
}
