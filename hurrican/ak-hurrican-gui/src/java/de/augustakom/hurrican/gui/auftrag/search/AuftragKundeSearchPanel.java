/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2004 07:46:21
 */
package de.augustakom.hurrican.gui.auftrag.search;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableEnterKeyListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.auftrag.AuftragUebersichtFrame;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragDatenTableModel;
import de.augustakom.hurrican.gui.auftrag.shared.KundeAdresseTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.gui.base.KNoMaskFormatter;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Panel fuer die Filter-Einstellungen und das Ergebnis der Kunden-Suche.
 *
 *
 */
public class AuftragKundeSearchPanel extends AbstractServicePanel implements AKObjectSelectionListener,
        AKSearchComponent, AKFilterTableModelListener {

    private static final Logger LOGGER = Logger.getLogger(AuftragKundeSearchPanel.class);

    public static final String RB_KUNDENDATEN = "kundendaten";
    public static final String RB_AUFTRAG = "auftrag";
    public static final String RB_RUFNUMMER = "rufnummer";
    public static final String RB_IN_RUFNUMMER = "in.nummer";
    public static final String RB_ENDSTELLE = "endstelle";
    public static final String RB_CARRIER = "carrier";
    public static final String RB_EQUIPMENT = "equipment";
    public static final String RB_INBETRIEBNAHME = "inbetriebnahme";
    public static final String RB_KOPPLUNG = "kopplung";
    public static final String RB_RECHNUNG = "rechnung";
    public static final String RB_INNENAUFTRAG = "innenauftrag";
    public static final String RB_HOUSING = "housingauftrag";
    public static final String RB_IP_ADDRESS = "ip.address";

    static final String CMD_SEARCH = "search";
    static final String CMD_CLEAR = "clear";

    private AKJRadioButton rbKundendaten = null;
    private AKJRadioButton rbAuftrag = null;
    private AKJRadioButton rbCarrier = null;
    private AKJRadioButton rbRechnung = null;
    private AKJRadioButton rbIpAddress = null;
    private AKJButton btnSearch = null;
    private AKJTable tbResult = null;
    private AKJLabel lblResultCount = null;
    private AKJPanel filterPanel = null;
    private KundendatenFilterPanel kundenPanel = null;
    private RufnummerFilterPanel rufnummerPanel = null;
    private INRufnummerFilterPanel inRNPanel = null;
    private AuftragDatenFilterPanel auftragPanel = null;
    private AuftragEndstelleFilterPanel endstellePanel = null;
    private AuftragCarrierFilterPanel carrierPanel = null;
    private AuftragHousingFilterPanel auftragHousingPanel = null;
    private EquipmentFilterPanel eqPanel = null;
    private InbetriebnahmeFilterPanel ibPanel = null;
    private RechnungFilterPanel rePanel = null;
    private InnenauftragFilterPanel iaPanel = null;
    private IpAddressFilterPanel ipAddressPanel = null;
    private AKSearchKeyListener searchKeyListener = null;
    private AKAbstractAction openSAPAction = null;
    private AKAbstractAction attachNBZAction = null;
    private AKAbstractAction openIoArchiveDialog = null;

    private boolean searching = false;
    private SearchWorker<Object, AKTableModel<?>> searchWorker;

    /**
     * Konstruktor fuer das Panel.
     */
    public AuftragKundeSearchPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragKundeSearchPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKTableEnterKeyListener enterKeyListener = new AKTableEnterKeyListener(this);
        enterKeyListener.addAKTableModel(new KundeAdresseTableModel());
        enterKeyListener.addAKTableModel(new AuftragDatenTableModel());

        searchKeyListener = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        ButtonGroup rbGroup = new ButtonGroup();
        rbKundendaten = getSwingFactory().createRadioButton(RB_KUNDENDATEN, getActionListener(), true, rbGroup);
        rbAuftrag = getSwingFactory().createRadioButton(RB_AUFTRAG, getActionListener(), false, rbGroup);
        AKJRadioButton rbRufnummer = getSwingFactory().createRadioButton(RB_RUFNUMMER, getActionListener(), false, rbGroup);
        AKJRadioButton rbINRufnummer = getSwingFactory().createRadioButton(RB_IN_RUFNUMMER, getActionListener(), false, rbGroup);
        AKJRadioButton rbEndstelle = getSwingFactory().createRadioButton(RB_ENDSTELLE, getActionListener(), false, rbGroup);
        rbCarrier = getSwingFactory().createRadioButton(RB_CARRIER, getActionListener(), false, rbGroup);
        AKJRadioButton rbEquipment = getSwingFactory().createRadioButton(RB_EQUIPMENT, getActionListener(), false, rbGroup);
        AKJRadioButton rbInbetriebnahme = getSwingFactory().createRadioButton(RB_INBETRIEBNAHME, getActionListener(), false, rbGroup);
        rbRechnung = getSwingFactory().createRadioButton(RB_RECHNUNG, getActionListener(), false, rbGroup);
        AKJRadioButton rbIA = getSwingFactory().createRadioButton(RB_INNENAUFTRAG, getActionListener(), false, rbGroup);
        AKJRadioButton rbAuftragHousing = getSwingFactory().createRadioButton(RB_HOUSING, getActionListener(), false, rbGroup);
        rbIpAddress = getSwingFactory().createRadioButton(RB_IP_ADDRESS, getActionListener(), false, rbGroup);

        filterPanel = new AKJPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.filter")));

        AKJPanel fillSouth = new AKJPanel(new Dimension(10, 1));
        AKJPanel searchType = new AKJPanel(new GridBagLayout());
        searchType.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.search.type")));
        searchType.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        searchType.add(rbKundendaten, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        searchType.add(rbCarrier, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(rbAuftrag, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(rbRufnummer, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(rbEndstelle, GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(rbINRufnummer, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(rbEquipment, GBCFactory.createGBC(100, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(rbInbetriebnahme, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        searchType.add(rbRechnung, GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        searchType.add(rbIA, GBCFactory.createGBC(100, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        searchType.add(rbAuftragHousing, GBCFactory.createGBC(100, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        searchType.add(rbIpAddress, GBCFactory.createGBC(100, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        searchType.add(fillSouth, GBCFactory.createGBC(0, 0, 5, 5, 1, 1, GridBagConstraints.NONE));

        btnSearch = getSwingFactory().createButton(CMD_SEARCH, getActionListener());
        AKJButton btnClearFilter = getSwingFactory().createButton(CMD_CLEAR, getActionListener());
        lblResultCount = new AKJLabel();

        AKJPanel btnPanel = new AKJPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnSearch);
        btnPanel.add(btnClearFilter);
        btnPanel.add(new AKJPanel());
        btnPanel.add(lblResultCount);

        openSAPAction = (AKAbstractAction) getSwingFactory().createAction("open.sap.daten");
        openSAPAction.setParentClass(this.getClass());
        attachNBZAction = (AKAbstractAction) getSwingFactory().createAction("attach.nbz");
        attachNBZAction.setParentClass(this.getClass());
        AKAbstractAction openCustomerAction = (AKAbstractAction) getSwingFactory().createAction("open.customer.overview");
        openCustomerAction.setParentClass(this.getClass());
        openIoArchiveDialog = (AKAbstractAction) getSwingFactory().createAction("open.io.archive.dialog");
        openIoArchiveDialog.setParentClass(this.getClass());

        tbResult = new AKJTable();
        tbResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbResult.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        tbResult.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbResult.addKeyListener(enterKeyListener);
        tbResult.addPopupAction(openSAPAction);
        tbResult.addPopupSeparator();
        tbResult.addPopupAction(openCustomerAction);
        tbResult.addPopupSeparator();
        tbResult.addPopupAction(attachNBZAction);
        tbResult.addPopupSeparator();
        tbResult.addPopupAction(openIoArchiveDialog);
        AKJScrollPane spTable = new AKJScrollPane(tbResult);

        this.setLayout(new GridBagLayout());
        this.add(searchType, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(filterPanel, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(btnPanel, GBCFactory.createGBC(100, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        this.add(spTable, GBCFactory.createGBC(100, 100, 0, 2, 3, 1, GridBagConstraints.BOTH));

        showKundendatenPanel();
        setPreferredSize(new Dimension(800, 450));

        manageGUI(openSAPAction);
    }

    /* Zeigt das Filter-Panel fuer die Suche nach IP-Adressen an. */
    private void showIpAddressPanel() {
        if (ipAddressPanel == null) {
            ipAddressPanel = new IpAddressFilterPanel(searchKeyListener);
        }
        showFilterPanel(ipAddressPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber Kundendaten an. */
    private void showKundendatenPanel() {
        if (kundenPanel == null) {
            kundenPanel = new KundendatenFilterPanel(this, searchKeyListener);
        }

        showFilterPanel(kundenPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber Rufnummern an. */
    private void showRufnummerPanel() {
        if (rufnummerPanel == null) {
            rufnummerPanel = new RufnummerFilterPanel(searchKeyListener);
        }

        showFilterPanel(rufnummerPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber IN-Rufnummern an. */
    private void showINRufnummerPanel() {
        if (inRNPanel == null) {
            inRNPanel = new INRufnummerFilterPanel(searchKeyListener);
        }

        showFilterPanel(inRNPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber Auftragsdaten an. */
    private void showAuftragPanel() {
        if (auftragPanel == null) {
            auftragPanel = new AuftragDatenFilterPanel(searchKeyListener);
        }

        showFilterPanel(auftragPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber Endstellendaten an. */
    private void showEndstellePanel() {
        if (endstellePanel == null) {
            endstellePanel = new AuftragEndstelleFilterPanel(searchKeyListener);
        }

        showFilterPanel(endstellePanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber Carrierdaten an. */
    private void showCarrierPanel() {
        if (carrierPanel == null) {
            carrierPanel = new AuftragCarrierFilterPanel(searchKeyListener);
        }

        showFilterPanel(carrierPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber Equipment-Daten an. */
    private void showEquipmentPanel() {
        if (eqPanel == null) {
            eqPanel = new EquipmentFilterPanel(searchKeyListener);
        }

        showFilterPanel(eqPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche ueber Equipment-Daten an. */
    private void showHousingPanel() {
        if (auftragHousingPanel == null) {
            auftragHousingPanel = new AuftragHousingFilterPanel(searchKeyListener);
        }

        showFilterPanel(auftragHousingPanel);
    }


    /* Zeigt das Filter-Panel fuer die Suche nach Inbetriebnahmedatum an. */
    private void showInbetriebnahmePanel() {
        if (ibPanel == null) {
            ibPanel = new InbetriebnahmeFilterPanel(searchKeyListener);
        }

        showFilterPanel(ibPanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche nach Rechnungsdaten an. */
    private void showRechnungPanel() {
        if (rePanel == null) {
            rePanel = new RechnungFilterPanel(searchKeyListener);
        }
        showFilterPanel(rePanel);
    }

    /* Zeigt das Filter-Panel fuer die Suche nach Innenauftragsdaten an. */
    private void showIAPanel() {
        if (iaPanel == null) {
            iaPanel = new InnenauftragFilterPanel(searchKeyListener);
        }
        showFilterPanel(iaPanel);
    }

    /*
     * Stellt das uebergebene Filter-Panel dar.
     * @param toShow
     *
     */
    private void showFilterPanel(IFilterOwner<?, ?> toShow) {
        toShow.clearFilter();
        filterPanel.removeAll();
        if (toShow instanceof Component) {
            filterPanel.add((Component) toShow, BorderLayout.CENTER);
        }
        filterPanel.revalidate();
        filterPanel.repaint();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (RB_KUNDENDATEN.equals(command)) {
            showKundendatenPanel();
        }
        else if (RB_AUFTRAG.equals(command)) {
            showAuftragPanel();
        }
        else if (RB_RUFNUMMER.equals(command)) {
            showRufnummerPanel();
        }
        else if (RB_ENDSTELLE.equals(command)) {
            showEndstellePanel();
        }
        else if (RB_CARRIER.equals(command)) {
            showCarrierPanel();
        }
        else if (RB_IN_RUFNUMMER.equals(command)) {
            showINRufnummerPanel();
        }
        else if (RB_EQUIPMENT.equals(command)) {
            showEquipmentPanel();
        }
        else if (RB_INBETRIEBNAHME.equals(command)) {
            showInbetriebnahmePanel();
        }
        else if (RB_RECHNUNG.equals(command)) {
            showRechnungPanel();
        }
        else if (RB_INNENAUFTRAG.equals(command)) {
            showIAPanel();
        }
        else if (RB_HOUSING.equals(command)) {
            showHousingPanel();
        }
        else if (RB_IP_ADDRESS.equals(command)) {
            showIpAddressPanel();
        }
        else if (CMD_SEARCH.equals(command)) {
            doSearch();
        }
        else if (CMD_CLEAR.equals(command)) {
            clearActiveFilter();
        }
    }

    /* Validiert die Actions des Popup-Menus */
    private void validatePopupActions() {
        boolean enableSapAction = (rbRechnung.isSelected() || rbKundendaten.isSelected() || rbAuftrag.isSelected());
        openSAPAction.setEnabled(enableSapAction);

        // History-Aufruf nur in der Carrier-Suche aktivieren
        openIoArchiveDialog.setEnabled(rbCarrier.isSelected());

        // 'NBZ hinterlegen' in der IP Suche deaktivieren
        attachNBZAction.setEnabled(!rbIpAddress.isSelected());
    }

    private class SearchWorker<S, T extends AKTableModel<?>> extends SwingWorker<T, Void> {

        final IFilterOwner<S, T> filterOwner;
        final S filterQuery;

        SearchWorker(IFilterOwner<S, T> filterOwner) throws HurricanGUIException {
            // Do this first, since if there is an exception, the GUI won't be changed!
            this.filterOwner = filterOwner;
            this.filterQuery = filterOwner.getFilter();

            // Now set the wait Cursors...
            setWaitCursor();
            showProgressBar("suchen...");
            btnSearch.setEnabled(false);
            tbResult.removeAll();
            tbResult.setWaitCursor();
            searching = true;

        }

        @Override
        protected T doInBackground() throws Exception {
            return filterOwner.doSearch(filterQuery);
        }

        @Override
        protected void done() {
            try {
                T tbMdl = get();

                // Search is done since get did not throw an exception!
                searching = false;
                filterOwner.updateGui(tbMdl, tbResult);
                if (tbMdl != null) {
                    tbMdl.addFilterTableModelListener(AuftragKundeSearchPanel.this);
                }
            }
            catch (CancellationException e) {
                LOGGER.debug("Worker thread has been cancelled: " + e.getMessage());
            }
            catch (Exception e) {
                searching = false;
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(AuftragKundeSearchPanel.this, e);
            }
            finally {
                if (!searching) {
                    setDefaultCursor();
                    stopProgressBar();
                    showResultCount(tbResult.getRowCount());
                    tbResult.setDefaultCursor();
                    btnSearch.setEnabled(true);
                    validatePopupActions();
                }
            }
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    // Needs unchecked conversion because of runtime type erasure
    public void doSearch() {
        if (searchWorker != null) {
            searchWorker.cancel(true);
        }
        try {
            for (int i = 0; i < filterPanel.getComponentCount(); i++) {
                Component comp = filterPanel.getComponent(i);
                if (comp instanceof IFilterOwner<?, ?>) {
                    searchWorker = new SearchWorker<Object, AKTableModel<?>>
                            ((IFilterOwner<Object, AKTableModel<?>>) comp);
                    searchWorker.execute();
                    break;
                }
            }
        }
        catch (HurricanGUIException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(AuftragKundeSearchPanel.this, e);
        }
    }

    /* Zeigt die Anzahl der gefundenen Datensaetze in einem Label an. */
    private void showResultCount(int count) {
        String text = getSwingFactory().getText("result.count");
        lblResultCount.setText(text + " " + count);
    }

    /* Setzt den aktuellen Filter zurueck. */
    private void clearActiveFilter() {
        for (int i = 0; i < filterPanel.getComponentCount(); i++) {
            Component comp = filterPanel.getComponent(i);
            if (comp instanceof IFilterOwner<?, ?>) {
                ((IFilterOwner<?, ?>) comp).clearFilter();
                break;
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof KundeAdresseView) {
            AuftragUebersichtFrame.showAuftragUebersicht((KundeAdresseView) selection);
        }
        else if (selection instanceof CCAuftragModel && ((CCAuftragModel) selection).getAuftragId() != null) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    @Override
    public void tableFiltered() {
        showResultCount(tbResult.getRowCount());
    }


    /**
     * Filter-Panel fuer die Kundensuche ueber Kundendaten.
     */
    class KundendatenFilterPanel extends AKJPanel implements IFilterOwner<KundeQuery, KundeAdresseTableModel>, FocusListener {
        private KeyListener searchKL = null;
        private AKSearchComponent searchComp = null;

        private AKJFormattedTextField tfNo = null;
        private AKJFormattedTextField tfHNo = null;
        private AKJTextField tfName = null;
        private AKJTextField tfVorname = null;
        private AKJTextField tfStrasse = null;
        private AKJTextField tfPLZ = null;
        private AKJTextField tfOrt = null;
        private AKJTextField tfOrtsteil = null;

        /* Konstruktor */
        KundendatenFilterPanel(AKSearchComponent searchComp, KeyListener searchKeyListener) {
            this.searchKL = searchKeyListener;
            this.searchComp = searchComp;
            createGUI();
        }

        /**
         * Erzeugt die GUI.
         */
        protected final void createGUI() {
            AKJLabel lblNo = getSwingFactory().createLabel("kundendaten.kunden.no");
            AKJLabel lblHNo = getSwingFactory().createLabel("kundendaten.haupt.no");
            AKJLabel lblName = getSwingFactory().createLabel("kundendaten.name");
            AKJLabel lblVorname = getSwingFactory().createLabel("kundendaten.vorname");
            AKJLabel lblStrasse = getSwingFactory().createLabel("kundendaten.strasse");
            AKJLabel lblPLZ = getSwingFactory().createLabel("kundendaten.plz");
            AKJLabel lblOrt = getSwingFactory().createLabel("kundendaten.ort");
            AKJLabel lblOrtsteil = getSwingFactory().createLabel("kundendaten.ortsteil");

            tfNo = getSwingFactory().createFormattedTextField("kundendaten.kunden.no", searchKL);
            tfNo.addFocusListener(this);
            tfNo.setFormatterFactory(new DefaultFormatterFactory(new KNoMaskFormatter()));
            tfHNo = getSwingFactory().createFormattedTextField("kundendaten.haupt.no", searchKL);
            tfHNo.setFormatterFactory(new DefaultFormatterFactory(new KNoMaskFormatter()));
            tfName = getSwingFactory().createTextField("kundendaten.name", true, true, searchKL);
            tfVorname = getSwingFactory().createTextField("kundendaten.vorname", true, true, searchKL);
            tfStrasse = getSwingFactory().createTextField("kundendaten.strasse", true, true, searchKL);
            tfPLZ = getSwingFactory().createTextField("kundendaten.plz", true, true, searchKL);
            tfOrt = getSwingFactory().createTextField("kundendaten.ort", true, true, searchKL);
            tfOrtsteil = getSwingFactory().createTextField("kundendaten.ortsteil", true, true, searchKL);

            AKJPanel left = new AKJPanel(new GridBagLayout());
            left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            left.add(lblNo, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
            left.add(tfNo, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(lblHNo, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(tfHNo, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(lblName, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(tfName, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(lblVorname, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            left.add(tfVorname, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));

            AKJPanel right = new AKJPanel(new GridBagLayout());
            right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            right.add(lblStrasse, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(tfStrasse, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(lblPLZ, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(tfPLZ, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(lblOrt, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(tfOrt, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(lblOrtsteil, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            right.add(tfOrtsteil, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));

            this.setLayout(new GridBagLayout());
            this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
            this.add(right, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
            this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 1, 1, 1, GridBagConstraints.BOTH));
        }

        @Override
        public KundeQuery getFilter() {
            KundeQuery query = new KundeQuery();

            try {
                if (tfNo.hasFocus()) {
                    tfNo.commitEdit(); // commitEdit ausfuehren, da ansonsten der Wert u.U. noch nicht gesetzt ist
                }
            }
            catch (ParseException e) { LOGGER.error(e.getMessage(), e); }

            Long kNo = tfNo.getValueAsLong(null);
            kNo = ((kNo == null) || (kNo.longValue() == 0)) ? null : kNo;

            Long kHNo = tfHNo.getValueAsLong(null);
            kHNo = ((kHNo == null) || (kHNo.longValue() == 0)) ? null : kHNo;

            query.setKundeNo(kNo);
            query.setHauptKundenNo(kHNo);
            query.setName(tfName.getText());
            query.setVorname(tfVorname.getText());
            query.setStrasse(tfStrasse.getText());
            query.setPlz(tfPLZ.getText());
            query.setOrt(tfOrt.getText());
            query.setOrtsteil(tfOrtsteil.getText());

            return query;
        }

        @Override
        public KundeAdresseTableModel doSearch(KundeQuery query) throws HurricanGUIException {
            try {
                KundenService service = getBillingService(KundenService.class);
                List<KundeAdresseView> result = service.findKundeAdresseViewsByQuery(query);

                KundeAdresseTableModel tbModel = new KundeAdresseTableModel();
                tbModel.setData(result);
                return tbModel;
            }
            catch (Exception e) {
                throw new HurricanGUIException(e.getMessage(), e);
            }
        }

        @Override
        public void updateGui(KundeAdresseTableModel tableModel, AKJTable resultTable) {
            resultTable.setModel(tableModel);
            resultTable.attachSorter();
            resultTable.fitTable(new int[] { 80, 80, 80, 120, 120, 100, 50, 50, 140, 80, 140 });
        }

        @Override
        public void clearFilter() {
            GuiTools.cleanFields(this);
        }

        @Override
        public void focusLost(FocusEvent e) {
            if ((e.getSource() == tfNo) && rbKundendaten.isSelected()) {
                try {
                    tfNo.commitEdit();
                }
                catch (ParseException ex) {
                    LOGGER.debug(ex.getLocalizedMessage(), ex);
                }

                Integer no = tfNo.getValueAsInt(null);
                if ((no != null) && (no.intValue() != 0)) {
                    this.clearFilter();
                    tfNo.setValue(no);
                    searchComp.doSearch();
                }
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
        }
    }
}
