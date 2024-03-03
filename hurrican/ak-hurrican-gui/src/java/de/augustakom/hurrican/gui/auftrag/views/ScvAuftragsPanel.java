/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2005 11:39:04
 */
package de.augustakom.hurrican.gui.auftrag.views;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJToggleButton;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ScvViewService;


/**
 * Panel fuer die Anzeige der offenen Auftraege fuer AM.
 */
public class ScvAuftragsPanel extends AbstractServicePanel implements AKDataLoaderComponent,
        AKObjectSelectionListener, AKFilterTableModelListener {

    private static final Logger LOGGER = Logger.getLogger(ScvAuftragsPanel.class);
    private static final String FILTER_NIEDERLASSUNG = "Niederlassung";
    private static final int COL_NIEDERLASSUNG = 17;

    private AKJTable tbAuftraege = null;
    private AKReflectionTableModel<IncompleteAuftragView> tbMdlAuftraege = null;
    private AKJButton btnRefresh = null;
    private AKJToggleButton btnAll = null;
    private AKJToggleButton btnOhneBA = null;
    private AKJToggleButton btnOhneBAVorgabe = null;
    private AKJToggleButton btnOhneLbz = null;
    private AKJToggleButton btnCudaBest = null;
    private AKJToggleButton btnCudaKuend = null;
    private AKJToggleButton btnInvisible = null;
    private AKJToggleButton btnDateFilter = null;
    private AKJComboBox cbNiederlassung = null;

    private short searchStrategy = ScvViewService.FIND_STRATEGY_ALL;
    private Object[] searchParams = null;

    private final AKJDateComponent dcGueltigVon = new AKJDateComponent(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

    /**
     * Default-Konstruktor.
     */
    public ScvAuftragsPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/ScvAuftragsPanel.xml");
        createGUI();
        loadData();
        init();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        ButtonGroup btnGroup = new ButtonGroup();
        btnRefresh = getSwingFactory().createButton("refresh", getActionListener(), null);
        btnAll = getSwingFactory().createToggleButton("alle.auftraege", getActionListener(), false, btnGroup);
        btnOhneBA = getSwingFactory().createToggleButton("ohne.ba", getActionListener(), false, btnGroup);
        btnOhneBAVorgabe = getSwingFactory().createToggleButton("ohne.ba.vorgabe", getActionListener(), false, btnGroup);
        btnOhneLbz = getSwingFactory().createToggleButton("ohne.lbz", getActionListener(), false, btnGroup);
        btnCudaBest = getSwingFactory().createToggleButton("cuda.bestellungen", getActionListener(), false, btnGroup);
        btnCudaKuend = getSwingFactory().createToggleButton("cuda.kuendigungen", getActionListener(), false, btnGroup);
        btnDateFilter = getSwingFactory().createToggleButton("date.filter", getActionListener(), false, btnGroup);
        btnInvisible = new AKJToggleButton();
        btnGroup.add(btnInvisible);
        AKJLabel lblNiederlassung = getSwingFactory().createLabel("niederlassung");
        cbNiederlassung = getSwingFactory().createComboBox("niederlassung");
        cbNiederlassung = new AKJComboBox(Niederlassung.class, "getName");
        cbNiederlassung.addItemListener(e -> {
            if ((e.getSource() == cbNiederlassung) && (e.getStateChange() == ItemEvent.SELECTED)) {
                filterNiederlassung(getSelectedNiederlassung());
            }
        });

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJLabel("gültig von: "), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(dcGueltigVon, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnAll, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnOhneBA, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnOhneBAVorgabe, GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnOhneLbz, GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCudaBest, GBCFactory.createGBC(0, 0, 8, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCudaKuend, GBCFactory.createGBC(0, 0, 9, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnDateFilter, GBCFactory.createGBC(0, 0, 10, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 11, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(lblNiederlassung, GBCFactory.createGBC(0, 0, 12, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 13, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(cbNiederlassung, GBCFactory.createGBC(0, 0, 14, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 15, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlAuftraege = new AKReflectionTableModel<>(
                new String[] { "Bearbeiter", "Vorgabe AM", "Real.-Termin", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Bestellt am", "Zurück am", "LBZ", "BA an Dispo",
                        "BA-Anlass", "Produktgruppe", "Produkt", "Name", "Vorname", "Kunde__No", "Auftrag-ID", "Order__No",
                        "Auftragsart", "Niederlassung", "Kündigung an", "Kündigung zurück" },
                new String[] { "bearbeiter", "vorgabeSCV", "baRealTermin", "vbz", "cbBestelltAm", "cbZurueckAm", "cbLbz", "baAnDispo",
                        "baAnlass", "produktGruppe", "produktName", "name", "vorname", "kundeNo", "auftragId", "auftragNoOrig",
                        "auftragsart", "niederlassung", "cbKuendigungAm", "cbKuendigungZurueck" },
                new Class[] { String.class, Date.class, Date.class, String.class, Date.class, Date.class, String.class, Date.class,
                        String.class, String.class, String.class, String.class, String.class, Long.class, Long.class,
                        Long.class,
                        String.class, String.class, Date.class, Date.class }
        );
        tbMdlAuftraege.addFilterTableModelListener(this);
        tbAuftraege = new AKJTable(tbMdlAuftraege, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftraege.attachSorter();
        tbAuftraege.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbAuftraege.fitTable(new int[] { 80, 60, 60, 80, 60, 60, 90, 60, 90, 100, 100, 120, 80, 70, 70, 70, 80, 90, 60, 60 });
        AKJScrollPane spTable = new AKJScrollPane(tbAuftraege);

        this.setLayout(new BorderLayout());
        this.add(btnPnl, BorderLayout.NORTH);
        this.add(spTable, BorderLayout.CENTER);
    }

    /**
     * Initialisiert die Daten des Panel.
     */
    protected void init() {
        try {
            // Lade Niederlassungen
            NiederlassungService ns = getCCService(NiederlassungService.class);
            List<Niederlassung> niederlassungen = ns.findNiederlassungen();
            cbNiederlassung.addItem("(alle)", null);
            cbNiederlassung.addItems(niederlassungen, Boolean.TRUE, Niederlassung.class);
            selectNiederlassung();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        setWaitCursor();
        showProgressBar("lade Aufträge...");
        enableButtons(false);
        tbMdlAuftraege.removeAll();
        final SwingWorker<List<IncompleteAuftragView>, Void> worker = new SwingWorker<List<IncompleteAuftragView>, Void>() {

            final short searchStrategyInput = searchStrategy;

            @Override
            protected List<IncompleteAuftragView> doInBackground() throws Exception {
                ScvViewService svs = getCCService(ScvViewService.class);
                return svs.findByParam(searchStrategyInput, searchParams, dcGueltigVon.getDate(null));
            }

            @Override
            protected void done() {
                try {
                    List<IncompleteAuftragView> views = get();
                    tbMdlAuftraege.setData(views);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    refreshCount();
                    stopProgressBar();
                    enableButtons(true);
                    setDefaultCursor();
                }
            }

        };
        worker.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        boolean doSearch = true;
        try {
            searchStrategy = (short) -1;
            searchParams = null;

            if ("refresh".equals(command)) {
                tbMdlAuftraege.removeAll();
                resetButtons();
                ScvViewService svs = getCCService(ScvViewService.class);
                svs.reInitialize();
                searchStrategy = ScvViewService.FIND_STRATEGY_ALL;
            }
            else {
                enableButtons(true);
                if ("alle.auftraege".equals(command)) {
                    searchStrategy = ScvViewService.FIND_STRATEGY_ALL;
                }
                else if ("ohne.ba".equals(command)) {
                    searchStrategy = ScvViewService.FIND_STRATEGY_WITHOUT_BA;
                }
                else if ("ohne.ba.vorgabe".equals(command)) {
                    searchStrategy = ScvViewService.FIND_STRATEGY_WITHOUT_BA_UEBERFAELLIG;
                }
                else if ("ohne.lbz".equals(command)) {
                    searchStrategy = ScvViewService.FIND_STRATEGY_WITHOUT_LBZ;
                }
                else if ("cuda.bestellungen".equals(command)) {
                    searchStrategy = ScvViewService.FIND_STRATEGY_CUDA_BESTELLUNG_OFFEN;
                }
                else if ("cuda.kuendigungen".equals(command)) {
                    searchStrategy = ScvViewService.FIND_STRATEGY_CUDA_KUENDIGUNG_OFFEN;
                }
                else if ("date.filter".equals(command)) {
                    searchStrategy = ScvViewService.FIND_STRATEGY_VORGABESCV_AND_REALDATE;
                    SelectDateDialog dlg = new SelectDateDialog(getSwingFactory());
                    Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    if (result instanceof Object[]) {
                        searchParams = (Object[]) result;
                    }
                    else {
                        doSearch = false;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            if (doSearch) {
                loadData();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Setzt die Buttons auf enabled/disabled */
    private void enableButtons(boolean enable) {
        btnRefresh.setEnabled(enable);
        btnAll.setEnabled(enable);
        btnOhneBA.setEnabled(enable);
        btnOhneBAVorgabe.setEnabled(enable);
        btnOhneLbz.setEnabled(enable);
        btnCudaBest.setEnabled(enable);
        btnCudaKuend.setEnabled(enable);
        btnDateFilter.setEnabled(enable);
    }

    /* Setzt alle Toggle-Buttons auf pressed=false */
    private void resetButtons() {
        btnInvisible.setSelected(true);
    }

    /* Zeigt die Anzahl der offenen Auftraege im Frame-Titel an. */
    private void refreshCount() {
        String newTitle = (getFrameTitle() == null) ? "" : getFrameTitle() + " - " +
                "Anzahl: " + tbAuftraege.getRowCount();
        setFrameTitle(newTitle);
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModelListener#tableFiltered()
     */
    @Override
    public void tableFiltered() {
        refreshCount();
    }

    /**
     * Liefert selektierte Niederlassung in der Combobox oder <code>null</code>
     */
    private Niederlassung getSelectedNiederlassung() {
        Object obj = (cbNiederlassung != null) ? cbNiederlassung.getSelectedItem() : null;
        return ((obj != null) && (obj instanceof Niederlassung)) ? (Niederlassung) obj : null;
    }

    /**
     * Funktion filtert nach einer Niederlassung
     *
     * @param niederlassung die gewünschte Niederlassung, nach der gefiltert werden soll
     */
    private void filterNiederlassung(Niederlassung niederlassung) {
        if (tbMdlAuftraege != null) {
            tbMdlAuftraege.removeFilter(FILTER_NIEDERLASSUNG);
            if (niederlassung != null) {
                // Daten nach Niederlassung filtern
                tbMdlAuftraege.addFilter(new FilterOperator(FILTER_NIEDERLASSUNG, FilterOperators.EQ,
                        niederlassung.getName(), COL_NIEDERLASSUNG));
            }
        }
    }

    /**
     * Selektiert die Niederlassung des Benutzers
     */
    private void selectNiederlassung() {
        try {
            // Ermittle User
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService us = (AKUserService) authSL.getService(AKAuthenticationServiceNames.USER_SERVICE, null);
            AKUser user = us.findUserBySessionId(sessionId);

            // Selektiere Niederlassung
            NiederlassungService nlService = getCCService(NiederlassungService.class);
            Niederlassung niederlassung = (user != null) ? nlService.findNiederlassung(user.getNiederlassungId()) : null;
            cbNiederlassung.selectItem((niederlassung != null) ? niederlassung.getName() : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Dialog zur Abfrage der Such-Parameter 'Vorgabe-AM' und 'Realisierungstermin'.
     */
    static class SelectDateDialog extends AbstractServiceOptionDialog {
        private SwingFactory swingFactory = null;
        private AKJDateComponent dcVorgabeScv = null;
        private AKJDateComponent dcRealDate = null;

        /**
         * Default-Konstruktor.
         */
        public SelectDateDialog(SwingFactory swingFactory) {
            super(null);
            this.swingFactory = swingFactory;
            createGUI();
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
         */
        @Override
        protected final void createGUI() {
            setTitle(swingFactory.getText("date.filter.title"));
            configureButton(CMD_SAVE, "Ok", null, true, true);

            AKJLabel lblVorgabeScv = swingFactory.createLabel("vorgabe.scv");
            AKJLabel lblRealDate = swingFactory.createLabel("realisierungsdatum");
            dcVorgabeScv = swingFactory.createDateComponent("vorgabe.scv");
            dcRealDate = swingFactory.createDateComponent("realisierungsdatum");

            AKJPanel child = getChildPanel();
            child.setLayout(new GridBagLayout());
            child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            child.add(lblVorgabeScv, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
            child.add(dcVorgabeScv, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(lblRealDate, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(dcRealDate, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));
        }

        /**
         * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
         */
        @Override
        protected void doSave() {
            prepare4Close();
            Date vorgabeSCV = dcVorgabeScv.getDate(null);
            Date realDate = dcRealDate.getDate(null);
            setValue(new Object[] { vorgabeSCV, realDate });
        }

        /**
         * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
         */
        @Override
        protected void validateSaveButton() {
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
         */
        @Override
        protected void execute(String command) {
        }

        /**
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        @Override
        public void update(Observable o, Object arg) {
        }
    }
}


