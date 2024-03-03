/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.06.2007 09:43:51
 */
package de.augustakom.hurrican.gui.auftrag.voip;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJComboBoxCellEditor;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableSingleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AbstractAuftragPanel;
import de.augustakom.hurrican.gui.shared.VoipDNAuftragViewTableModel;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.VoipDnPlanValidationService;

/**
 * Panel zur Darstellung und zum Editieren der VoIP-Daten eines Auftrags.
 */
public class AuftragVoIPPanel extends AbstractAuftragPanel implements AKModelOwner {

    public static final String ASSIGN_PORTS_AUTOMATICALLY = "assignPortsAutomatically";
    public static final String ADD_DN = "add.dn";
    public static final String DEL_DN = "del.dn";
    private static final long serialVersionUID = -4165748476189118695L;
    private static final Logger LOGGER = Logger.getLogger(AuftragVoIPPanel.class);
    /* Watch-Konstante fuer die VoIP-Daten. */
    private static final String WATCH_VOIP = "auftrag.voip";
    // GUI-Konstanten
    private static final String DN_ABGLEICH = "rufnrAbgleich";
    // Observer
    private final AuftragVoipDnViewObserver viewObserver = new AuftragVoipDnViewObserver();
    // GUI-Elemente
    private AKJComboBox cbSipRegistrars = null;
    private DnPlanPanel dnPlanPnl;
    // Modelle
    private CCAuftragModel auftragModel = null;
    private AuftragVoIP auftragVoIP = null;
    // sonstiges
    private boolean initialized = false;
    // für Passworte:
    private AKJVoIDn2PortTable tbPortzuordnung = null;
    private VoipDNAuftragViewTableModel tbMdlPortzuordnung = null;

    private VoipDnTableModel tbMdlVoipDn;

    private Collection<AuftragVoipDNView> vdnVList;

    /**
     * Default-Const.
     */
    public AuftragVoIPPanel() {
        super("de/augustakom/hurrican/gui/auftrag/voip/resources/AuftragVoIPPanel.xml");
        createGUI();
        init();
    }

    private static Object[] arrayOfRangeWithNullElement(int from, int to) {
        final Integer[] integers = new Integer[(to - from) + 2];
        integers[0] = null;
        for (int i = from, n = 1; i <= to; i++, n++) {
            integers[n] = i;
        }
        return integers;
    }

    @Override
    protected final void createGUI() {
        final AKJPanel portzuordnungPnl = createPortzuordnungPanel();

        try {
            dnPlanPnl = new DnPlanPanel(getSwingFactory(),
                    getCCService(VoipDnPlanValidationService.class),
                    auftragVoipDNView -> {
                        saveOrUpdateViews();
                        readModel();
                    }
            );

            final AKJPanel voipDnPanel = createVoipDnPanel(dnPlanPnl);
            final AKJPanel voipPanel = createVoipPanel(voipDnPanel, portzuordnungPnl, dnPlanPnl);
            this.setLayout(new BorderLayout());
            this.add(voipPanel, BorderLayout.CENTER);

        }
        catch (ServiceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private AKJPanel createVoipDnPanel(final AKObjectSelectionListener selectionListenerDnPlan) {
        cbSipRegistrars = new AKJComboBox(Reference.class, "getStrValue");
        AKJComboBox cbBlockLength = new AKJComboBox(arrayOfRangeWithNullElement(1, 10));
        AKJButton btnRefreshRufnummern = getSwingFactory().createButton(DN_ABGLEICH, getActionListener());
        tbMdlVoipDn = new VoipDnTableModel();
        tbMdlVoipDn.addTableModelListener(e -> dnPlanPnl.reset());
        AKJTable voipDnTable = new AKJTable(tbMdlVoipDn, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        voipDnTable.setDefaultEditor(Reference.class, new AKJComboBoxCellEditor(cbSipRegistrars));
        voipDnTable.setDefaultRenderer(Reference.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : ((Reference) value).getStrValue());
            }
        });
        voipDnTable.setDefaultEditor(Integer.class, new AKJComboBoxCellEditor(cbBlockLength));
        voipDnTable.addMouseListener(new AKTableSingleClickMouseListener(selectionListenerDnPlan));
        voipDnTable.attachSorter();

        // @formatter:off
        final AKJPanel btnPnlRufnr = new AKJPanel(new GridBagLayout());
        btnPnlRufnr.add(btnRefreshRufnummern, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        btnPnlRufnr.add(new AKJPanel()      , GBCFactory.createGBC(1, 0, 3, 0, 1, 1, GridBagConstraints.VERTICAL));

        final AKJScrollPane voipDnScrollPane = new AKJScrollPane(voipDnTable, new Dimension(500, 140));
        final AKJPanel voipDnPanel = new AKJPanel();
        voipDnPanel.setLayout(new GridBagLayout());
        voipDnPanel.setBorder(new TitledBorder("VoIP-Rufnummern"));
        voipDnPanel.add(voipDnScrollPane, GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        voipDnPanel.add(btnPnlRufnr     , GBCFactory.createGBC(  0,  0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        return voipDnPanel;
    }

    private AKJPanel createPortzuordnungPanel() {
        tbMdlPortzuordnung = new VoipDNAuftragViewTableModel();
        // Auto-Resize, da nach fireTableStructureChanged (braucht man für das Zählen im Header) die fitTable(...)
        // verworfen wird
        tbPortzuordnung = new AKJVoIDn2PortTable(tbMdlPortzuordnung, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        tbPortzuordnung.attachSorter();

        AKJButton btnAdd = getSwingFactory().createButton(ADD_DN, getActionListener(), null);
        AKJButton btnDel = getSwingFactory().createButton(DEL_DN, getActionListener(), null);

        final AKJPanel dnButtonPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dnButtonPanel.add(btnAdd        , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dnButtonPanel.add(btnDel        , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.NONE));
        dnButtonPanel.add(new AKJPanel(), GBCFactory.createGBC(100,100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        final AKJScrollPane spPortzuordnung = new AKJScrollPane(tbPortzuordnung);
        spPortzuordnung.setPreferredSize(new Dimension(500, 125));

        AKJButton btnAutoPortAssignment = getSwingFactory().createButton(ASSIGN_PORTS_AUTOMATICALLY, getActionListener());
        final AKJPanel portAssignBtnPanel = new AKJPanel(new GridBagLayout());
        portAssignBtnPanel.add(btnAutoPortAssignment,
                GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        portAssignBtnPanel.add(new AKJPanel(), GBCFactory.createGBC(1, 0, 3, 0, 1, 1, GridBagConstraints.VERTICAL));

        // @formatter:off
        final AKJPanel portzuordnungPnl = new AKJPanel(new GridBagLayout());
        portzuordnungPnl.setBorder(BorderFactory.createTitledBorder("Portzuordnungen"));
        portzuordnungPnl.add(dnButtonPanel      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        portzuordnungPnl.add(spPortzuordnung    , GBCFactory.createGBC(100,100, 1, 0, 1, 1, GridBagConstraints.BOTH));
        portzuordnungPnl.add(portAssignBtnPanel , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        return portzuordnungPnl;
    }

    private AKJPanel createVoipPanel(final AKJPanel voipDnPanel, final AKJPanel portzuordnungPnl, final AKJPanel dnPlanPnl) {
        // @formatter:off
        AKJPanel top = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("voip.data"));
        top.add(voipDnPanel     , GBCFactory.createGBC(  0,  0, 0, 3, 4, 2, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.NONE));
        top.add(dnPlanPnl       , GBCFactory.createGBC(  0,  0, 0, 5, 6, 2, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.NONE));
        top.add(portzuordnungPnl, GBCFactory.createGBC(  0,100, 0, 8, 4, 2, GridBagConstraints.BOTH));
        top.add(new AKJPanel()  , GBCFactory.createGBC(100,  0, 0, 9, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
        return top;
    }

    /* Initialisiert das Panel (Reference-Felder erhalten Find-Parameter) */
    private void init() {
        try {
            if (!initialized) {
                initialized = true;

                Reference egModeEx = new Reference();
                egModeEx.setType(Reference.REF_TYPE_VOIP_EG_MODE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        auftragVoIP = null;
        GuiTools.cleanFields(this);
        if (auftragModel != null) {
            setWaitCursor();

            try {
                addSipDomainItems();
                auftragVoIP = getVoIPService().findVoIP4Auftrag(auftragModel.getAuftragId());
                vdnVList = getVoIPService().findVoIPDNView(auftragModel.getAuftragId());
                addObserver2ViewList(vdnVList);

                final List<AuftragVoIpDnTableView> auftragVoIpDnTableViews = Lists.newLinkedList();
                for (final AuftragVoipDNView voipDNView : vdnVList) {
                    for (final SelectedPortsView selectedPorts : voipDNView.getSelectedPorts()) {
                        auftragVoIpDnTableViews.add(new AuftragVoIpDnTableView(voipDNView, selectedPorts));
                    }
                }

                tbMdlPortzuordnung.setData(auftragVoIpDnTableViews);
                tbMdlPortzuordnung.fireTableStructureChanged();
                fitTbRufnummern();

                tbMdlVoipDn.setData(vdnVList);
                selectActiveAuftragVoipDNView(vdnVList);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * Select the currently active {@link AuftragVoipDNView} in the GUI.
     */
    private void selectActiveAuftragVoipDNView(Collection<AuftragVoipDNView> vdnVList) {
        Optional<AuftragVoipDNView> currentView = vdnVList.stream()
                .filter(vdn -> DateTools.isDateBetween(new Date(), vdn.getGueltigVon(), vdn.getGueltigBis()))
                .findFirst();
        if (currentView.isPresent()) {
            dnPlanPnl.objectSelected(currentView.get());
        }
    }

    private void addObserver2ViewList(Collection<AuftragVoipDNView> vdnVList) {
        if (CollectionTools.isNotEmpty(vdnVList)) {
            for (AuftragVoipDNView view : vdnVList) {
                view.addObserver(viewObserver);
            }
        }
    }

    /**
     * da die Spaltenanzahl abhängig von der Anzahl an belegbaren Engeräte-Ports ist, wird hier das Array der
     * Größenangaben dynamisch gebaut.
     */
    private void fitTbRufnummern() {
        if ((tbMdlPortzuordnung != null) && !CollectionUtils.isEmpty(tbMdlPortzuordnung.getData())) {
            if (tbPortzuordnung.isTableFitted()) { // um sicherzustellen, dass nach Tab-Wechsel vom user angepasste
                // Spaltengrößen erhalten bleiben
                fitTbRufnummernWithUserModSize();
            }
            else { // die default spaltengrößen festlegen
                fitTbRufnummernWithDefaultSize();
            }
        }
    }

    private void fitTbRufnummernWithUserModSize() {
        tbPortzuordnung.fitTableWithLastFit();
    }

    private void fitTbRufnummernWithDefaultSize() {
        final int portColumnCount = getPortColumnCount();
        final int[] staticColumnSizes = new int[] { 70, 70, 70, 50, 70, 70, 70, 60, 60, 100, 60, 40 };
        final int portColumnSize = 40;
        final int[] allColumnSizes = new int[staticColumnSizes.length + portColumnCount];

        System.arraycopy(staticColumnSizes, 0, allColumnSizes, 0, staticColumnSizes.length);
        for (int i = staticColumnSizes.length; i < allColumnSizes.length; i++) {
            allColumnSizes[i] = portColumnSize;
        }
        tbPortzuordnung.fitTable(allColumnSizes);
    }

    private int getPortColumnCount() {
        Iterator<AuftragVoIpDnTableView> iter = tbMdlPortzuordnung.getData().iterator();
        int portColumnCount = (iter.hasNext()) ? iter.next().getNumberOfPorts() : 0;
        if (portColumnCount != 0) {
            portColumnCount++; // existieren Ports füge +1 für Spalte 'Alle' hinzu
        }
        return portColumnCount;
    }

    @SuppressWarnings("unchecked")
    private void addSipDomainItems() throws ServiceNotFoundException, FindException {
        List<Reference> sipRegistrars = getSIPDomainService().findPossibleSIPDomains4Auftrag(auftragModel.getAuftragId(),
                false);
        cbSipRegistrars.removeAllItems();
        if (sipRegistrars.isEmpty()) {
            cbSipRegistrars.addItem(makeObj("Kein Mapping konfiguriert"));
        }
        else {
            for (Reference sipRegistrar : sipRegistrars) {
                cbSipRegistrars.addItem(sipRegistrar);
            }
        }
    }

    private Object makeObj(final String item) {
        return new Object() {
            @Override
            public String toString() {
                return item;
            }
        };
    }

    @Override
    protected void execute(String command) {
        boolean readModel = false;
        try {
            if (StringUtils.equals(DN_ABGLEICH, command)) {
                rufnummernAbgleich();
            }
            else if (StringUtils.equals(ADD_DN, command)) {
                final Object selectedVoipDn = DialogHelper.showDialog(getAuftragDataFrame(), new PortzuordnungDialog(
                        vdnVList), true, true);
                if (selectedVoipDn instanceof AuftragVoipDNView) {
                    readModel = true;
                    deleteViewObserver();
                    viewObserver.viewChanged = true;
                    saveOrUpdateViews();
                }
            }
            else if (StringUtils.equals(DEL_DN, command)) {
                int selectedRow = tbPortzuordnung.getSelectedRow();
                if (selectedRow > -1) {
                    final Object selection = DialogHelper.showDialog(getMainFrame(),
                            new AKDateSelectionDialog("Ende Datum wählen", "", "Aktiv bis", false), true, true);
                    if (selection instanceof Date) {
                        final AuftragVoIpDnTableView tableView = tbMdlPortzuordnung.getDataAtRow(selectedRow);
                        tableView.selectedPortsView.setValidTo((Date) selection);
                        readModel = true;
                        deleteViewObserver();
                        viewObserver.viewChanged = true;
                        saveOrUpdateViews();
                    }
                }
            }
            else if (StringUtils.equals(ASSIGN_PORTS_AUTOMATICALLY, command)) {
                deleteViewObserver();
                getVoIPService().assignVoIPDNs2EGPorts(vdnVList, auftragModel.getAuftragId());
                viewObserver.viewChanged = true;
                readModel = true;
                saveModel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            if (readModel) {
                deleteViewObserver();
                try {
                    readModel();
                }
                catch (AKGUIException e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(this, e);
                    // aufgeben...
                }
            }
        }
    }

    private void deleteViewObserver() {
        if (vdnVList != null) {
            for (final AuftragVoipDNView view : vdnVList) {
                view.deleteObserver(viewObserver);
            }
        }
    }

    private void rufnummernAbgleich() {
        if ((auftragModel != null) && (auftragModel.getAuftragId() != null)) {
            try {
                Long auftragId = auftragModel.getAuftragId();

                getVoIPService().createVoIP4Auftrag(auftragId, HurricanSystemRegistry.instance().getSessionId());
                AuftragDaten ad = getCCAuftragService().findAuftragDatenByAuftragIdTx(auftragId);
                Long auftragNoOrig = ad.getAuftragNoOrig();

                if (auftragNoOrig == null) {
                    MessageHelper.showWarningDialog(this, "Billing Auftragsnummer fehlt!", true);
                }
                else {
                    // Liste der Rufnummern ermitteln:
                    RufnummerService serviceRn = getBillingService(RufnummerService.class);
                    List<Rufnummer> rufnummern = serviceRn.findRNs4Auftrag(auftragNoOrig);

                    if (CollectionTools.isEmpty(rufnummern)) {
                        MessageHelper.showWarningDialog(this, "Keine Rufnummern für Auftrag: " + auftragId
                                + ", AuftragNoOrig: " + auftragNoOrig + "!", true);
                    }
                    else {
                        for (Rufnummer rufnr : rufnummern) {
                            if (rufnr != null) {
                                // Falls kein AuftragVoipDn-Objekt existiert,
                                // muss dies angelegt werden, falls schon eines
                                // existiert, muss die SIP Domäne zugeordnet
                                // werden (falls verfuegbar)
                                getVoIPService().createVoIPDN4Auftrag(auftragId, rufnr.getDnNoOrig());
                            }
                        }
                        setModel((Observable) auftragModel); // refresh
                        MessageHelper.showInfoDialog(this, "Rufnummernabgleich erfolgreich durchgeführt!", true);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        boolean modelChanged = false;
        boolean guiCreated = false;
        if (guiCreated && (auftragVoIP != null)) {
            modelChanged = hasChanged(WATCH_VOIP, auftragVoIP);
        }
        if (!modelChanged) {
            modelChanged = viewObserver.viewChanged;
        }
        return modelChanged;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            boolean saveable = true;
            if (!saveable) {
                return;
            }
            setWaitCursor();

            auftragVoIP = getVoIPService().saveAuftragVoIP(auftragVoIP, true, HurricanSystemRegistry.instance()
                    .getSessionId());

            saveOrUpdateViews();
            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            readModel();
        }
        finally {
            setDefaultCursor();
        }
    }

    private void saveOrUpdateViews() throws Exception {
        if (viewObserver.isChanged()) {
            validate(vdnVList);
            getVoIPService().saveAuftragVoIPDNs(vdnVList);
            viewObserver.reset();
            MessageHelper.showInfoDialog(this, "Änderung der VOIP-Daten wurden gespeichert! Gegebenenfalls CPS-Provisionierung auslösen!");
        }
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        try {
            if (model instanceof CCAuftragModel) {
                this.auftragModel = (CCAuftragModel) model;
            }
            else {
                this.auftragModel = null;
            }

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void update(Observable observable, Object obj) {
    }

    public void validate(Collection<AuftragVoipDNView> auftragVoipDNViews) throws ServiceNotFoundException {
        AKWarnings warnings = getVoIPService().validatePortAssignment(auftragVoipDNViews);
        if (warnings.isNotEmpty()) {
            MessageHelper.showWarningDialog(this, warnings.getWarningsAsText(), true);
        }
    }

    protected SIPDomainService getSIPDomainService() throws ServiceNotFoundException {
        return getCCService(SIPDomainService.class);
    }

    protected VoIPService getVoIPService() throws ServiceNotFoundException {
        return getCCService(VoIPService.class);
    }

    protected CCAuftragService getCCAuftragService() throws ServiceNotFoundException {
        return getCCService(CCAuftragService.class);
    }


    public static interface DoSaveCallback {
        void execute(AuftragVoipDNView auftragVoipDNView) throws Exception;
    }

    private static class AKJVoIDn2PortTable extends AKJTable {
        private static final long serialVersionUID = -2997176481127569347L;
        private boolean tableFitted = false;
        private int[] lastCSFit = null;

        public AKJVoIDn2PortTable(VoipDNAuftragViewTableModel dm, int autoResizeMode, int selectionMode) {
            super(dm, autoResizeMode, selectionMode);
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            int[] cSizes = new int[0];
            if (tableFitted) {
                cSizes = getColumnSizes();
            }
            super.tableChanged(e);
            if (tableFitted) {
                fitTable(cSizes);
            }
        }

        private int[] getColumnSizes() {
            int[] cSizes = new int[getColumnCount()];
            for (int i = 0; i < cSizes.length; i++) {
                cSizes[i] = getColumnModel().getColumn(i).getWidth();
            }
            return cSizes;
        }

        public void fitTableWithLastFit() {
            if (lastCSFit != null) {
                fitTable(lastCSFit);
            }
        }

        @Override
        public void fitTable(int[] columnWidth) {
            super.fitTable(columnWidth);
            lastCSFit = columnWidth;
            tableFitted = true;
        }

        @Override
        public void fitTable(int tableWidth, double[] columnWidthPercent) {
            super.fitTable(tableWidth, columnWidthPercent);
            tableFitted = true;
        }

        public boolean isTableFitted() {
            return tableFitted;
        }
    }

    class AuftragVoipDnViewObserver implements Observer {
        private boolean viewChanged = false;

        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof AuftragVoipDNView) {
                final AuftragVoipDNView changed = (AuftragVoipDNView) o;
                final Map<AuftragVoipDNView, AuftragVoipDNView> toReplaceBy = Maps.newHashMap();
                for (final AuftragVoipDNView auftragVoipDNView : vdnVList) {
                    if (changed.getDnNoOrig().equals(auftragVoipDNView.getDnNoOrig())
                            && changed.getGueltigVon().equals(auftragVoipDNView.getGueltigVon())
                            && changed.getGueltigBis().equals(auftragVoipDNView.getGueltigBis())
                            && Strings.nullToEmpty(changed.getRangeFrom()).equals(Strings.nullToEmpty(changed.getRangeFrom()))
                            && Strings.nullToEmpty(changed.getRangeTo()).equals(Strings.nullToEmpty(changed.getRangeTo()))
                            ) {
                        toReplaceBy.put(auftragVoipDNView, changed);
                    }
                }
                for (Map.Entry<AuftragVoipDNView, AuftragVoipDNView> entry : toReplaceBy.entrySet()) {
                    vdnVList.remove(entry.getKey());
                    vdnVList.add(entry.getValue());
                }

                viewChanged = true;
            }
        }

        public boolean isChanged() {
            return viewChanged;
        }

        public void reset() {
            viewChanged = false;
        }
    }

}
