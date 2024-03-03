/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 10:16:10
 */
package de.augustakom.hurrican.gui.tools.sap;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.BLZ;
import de.augustakom.hurrican.model.exmodules.sap.SAPBankverbindung;
import de.augustakom.hurrican.model.exmodules.sap.SAPBuchungssatz;
import de.augustakom.hurrican.model.exmodules.sap.SAPSaldo;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.exmodules.sap.SAPService;
import de.augustakom.hurrican.service.exmodules.sap.utils.SAPServiceFinder;


/**
 * Panel zur Anzeige von SAP-Daten <br>
 *
 *
 */
public class SAPDatenPanel extends AbstractSAPPanel implements AKTableOwner, AKSearchComponent {

    private static final Logger LOGGER = Logger.getLogger(SAPDatenPanel.class);

    private static final String CMD_SEARCH = "search";

    // GUI-Komponenten
    private AKJTextField tfKontoNo = null;
    private AKJTextField tfBLZ = null;
    private AKJTextField tfBankName = null;
    private AKJTextField tfBankland = null;
    private AKJTextField tfSaldo = null;
    private AKJTextField tfWaehrung = null;
    private AKJTextField tfDebitor = null;
    private AKJTextField tfMahnstufe = null;
    private AKJTextField tfName = null;
    private AKJTextField tfStrasse = null;
    private AKJTextField tfPlz = null;
    private AKJTextField tfOrt = null;
    private AKJCheckBox cbLastschrift = null;
    private AKJButton btnSearch = null;

    private SAPBuchungssatzPanel sapOffenePostenPnl = null;
    private SAPBuchungssatzPanel sapAusgeglPostenPnl = null;
    private SAPBuchungssatzPanel sapAllePostenPnl = null;

    private boolean guiCreated = false;

    private String debNo = null;

    /**
     * Default-Const.
     */
    public SAPDatenPanel() {
        super("de/augustakom/hurrican/gui/tools/sap/resources/SAPDatenPanel.xml");
        createGUI();
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        if (guiCreated) { return; }

        AKSearchKeyListener searchKeyListener = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });

        //Labels
        AKJLabel lblKontoNo = getSwingFactory().createLabel("konto.no");
        AKJLabel lblBLZ = getSwingFactory().createLabel("blz");
        AKJLabel lblBankland = getSwingFactory().createLabel("bankland");
        AKJLabel lblSaldo = getSwingFactory().createLabel("saldo");
        AKJLabel lblWaehrung = getSwingFactory().createLabel("waehrung");
        AKJLabel lblDebitor = getSwingFactory().createLabel("debitor");
        AKJLabel lblMahnstufe = getSwingFactory().createLabel("mahnstufe");
        AKJLabel lblBankName = getSwingFactory().createLabel("bankname");
        AKJLabel lblLastschrift = getSwingFactory().createLabel("lastschrift");

        tfKontoNo = getSwingFactory().createTextField("konto.no", false);
        tfBLZ = getSwingFactory().createTextField("blz", false);
        tfBankName = getSwingFactory().createTextField("bankname", false);
        tfBankland = getSwingFactory().createTextField("bankland", false);
        tfSaldo = getSwingFactory().createTextField("saldo", false);
        tfWaehrung = getSwingFactory().createTextField("waehrung", false);
        tfDebitor = getSwingFactory().createTextField("debitor", false, false, searchKeyListener);
        tfMahnstufe = getSwingFactory().createTextField("mahnstufe", false);
        tfName = getSwingFactory().createTextField("name", false);
        tfStrasse = getSwingFactory().createTextField("strasse", false);
        tfPlz = getSwingFactory().createTextField("plz", false);
        tfOrt = getSwingFactory().createTextField("ort", false);
        cbLastschrift = getSwingFactory().createCheckBox("lastschrift", false);
        btnSearch = getSwingFactory().createButton(CMD_SEARCH, getActionListener());

        AKJPanel debitorPnl = new AKJPanel(new GridBagLayout(), "Debitor");
        debitorPnl.add(lblDebitor, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        debitorPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        debitorPnl.add(tfDebitor, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        debitorPnl.add(btnSearch, GBCFactory.createGBC(0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        debitorPnl.add(tfName, GBCFactory.createGBC(100, 0, 0, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        debitorPnl.add(tfStrasse, GBCFactory.createGBC(100, 0, 0, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        debitorPnl.add(tfPlz, GBCFactory.createGBC(100, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        debitorPnl.add(tfOrt, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        debitorPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 5, 3, 1, GridBagConstraints.VERTICAL));

        AKJPanel bankPnl = new AKJPanel(new GridBagLayout(), "Bankdaten");
        bankPnl.add(lblKontoNo, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        bankPnl.add(tfKontoNo, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(lblBLZ, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(tfBLZ, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(lblBankName, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(tfBankName, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(lblLastschrift, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(cbLastschrift, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(lblBankland, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(tfBankland, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        bankPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 4, 3, 1, GridBagConstraints.VERTICAL));

        AKJPanel saldoPnl = new AKJPanel(new GridBagLayout(), "Saldo");
        saldoPnl.add(lblMahnstufe, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        saldoPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        saldoPnl.add(tfMahnstufe, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        saldoPnl.add(lblSaldo, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        saldoPnl.add(tfSaldo, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        saldoPnl.add(lblWaehrung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        saldoPnl.add(tfWaehrung, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        saldoPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 4, 3, 1, GridBagConstraints.VERTICAL));

        AKJPanel infoPnl = new AKJPanel(new GridBagLayout(), "SAP-Daten");
        infoPnl.add(debitorPnl, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        infoPnl.add(bankPnl, GBCFactory.createGBC(100, 100, 1, 0, 1, 1, GridBagConstraints.BOTH));
        infoPnl.add(saldoPnl, GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));

        sapAllePostenPnl = new SAPBuchungssatzPanel();
        sapAusgeglPostenPnl = new SAPBuchungssatzPanel();
        sapOffenePostenPnl = new SAPBuchungssatzPanel();

        AKJTabbedPane tbBuchungssaetze = new AKJTabbedPane();
        tbBuchungssaetze.addTab(getSwingFactory().getText("offene.posten"), sapOffenePostenPnl);
        tbBuchungssaetze.addTab(getSwingFactory().getText("ausgegl.posten"), sapAusgeglPostenPnl);
        tbBuchungssaetze.addTab(getSwingFactory().getText("alle.posten"), sapAllePostenPnl);

        btnSearch.setVisible(false);

        this.setLayout(new BorderLayout());
        this.add(infoPnl, BorderLayout.NORTH);
        this.add(tbBuchungssaetze, BorderLayout.CENTER);
        guiCreated = true;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (CMD_SEARCH.equals(command)) {
            String debitor = tfDebitor.getText(null);
            if (StringUtils.isNotBlank(debitor)) {
                showDetails(debitor);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() {
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    public void showDetails(Object details) {
        if (details == null) {
            // Leere Debitorennummer, d.h. gebe Suchfeld frei
            debNo = null;
            tfDebitor.setEditable(true);
            btnSearch.setVisible(true);

            GuiTools.cleanFields(this);
            sapAllePostenPnl.setTableData(null);
            sapOffenePostenPnl.setTableData(null);
            sapAusgeglPostenPnl.setTableData(null);
        }
        else if (details instanceof String) {
            debNo = (String) details;
            if (StringUtils.isNotBlank(debNo)) {
                // Führender Buchstabe muss Grossbuchstabe sein
                debNo = debNo.toUpperCase();

                // TODO new Swing-Worker not tested yet
                final SwingWorker<List<RInfoAdresseView>, Void> worker = new SwingWorker<List<RInfoAdresseView>, Void>() {
                    final String localdebNo = debNo;

                    @Override
                    public List<RInfoAdresseView> doInBackground() throws Exception {
                        RechnungsService reService = getBillingService(RechnungsService.class);
                        RInfoQuery query = new RInfoQuery();
                        query.setSapDebitorNo(localdebNo);
                        return reService.findKundeByRInfoQuery(query);
                    }

                    @Override
                    protected void done() {
                        try {
                            List<RInfoAdresseView> views = get();
                            RInfoAdresseView view = ((views != null) && (views.size() == 1)) ? views.get(0) : null;
                            tfName.setText((view != null) ? StringTools.join(new String[] { view.getName(), view.getVorname() }, " ", true) : null);
                            tfStrasse.setText((view != null) ? StringTools.join(new String[] { view.getStrasse(), view.getNummer() }, " ", true) : null);
                            tfPlz.setText((view != null) ? view.getPlz() : null);
                            tfOrt.setText((view != null) ? view.getOrt() : null);

                            SAPService saps = (SAPService) SAPServiceFinder.instance().getSAPService(SAPService.class);

                            Integer mahnstufe = saps.findMahnstufe(localdebNo);
                            SAPSaldo saldo = saps.findAktSaldo(localdebNo);
                            SAPBankverbindung bank = saps.findBankverbindung(localdebNo);
                            List<SAPBuchungssatz> alle = saps.findBuchungssaetze(localdebNo);
                            List<SAPBuchungssatz> offene = saps.findOffenePosten(localdebNo);
                            List<SAPBuchungssatz> ausgegl = saps.findAusgeglichenePosten(localdebNo);

                            // Setze Daten in Textfelder
                            tfKontoNo.setText(((bank != null) && (bank.getAccount() != null)) ? bank.getAccount().toString() : null);
                            tfBLZ.setText(((bank != null) && (bank.getBlz() != null)) ? bank.getBlz().toString() : null);
                            tfBankName.setText((bank != null) ? findBankName(bank.getBlz()) : null);
                            cbLastschrift.setSelected((bank != null) ? StringUtils.equals(bank.getCollAuth(), "X") : false);
                            tfBankland.setText((bank != null) ? bank.getBankLand() : null);

                            tfSaldo.setText(((saldo != null) && (saldo.getSaldo() != null)) ? (new DecimalFormat("0.00")).format(saldo.getSaldo()) : null);
                            tfWaehrung.setText((saldo != null) ? saldo.getWaehrung() : null);

                            tfDebitor.setText(localdebNo);
                            tfMahnstufe.setText((mahnstufe != null) ? mahnstufe.toString() : null);

                            // Übergebe Daten an Subpanels
                            sapAllePostenPnl.setTableData(alle);
                            sapOffenePostenPnl.setTableData(offene);
                            sapAusgeglPostenPnl.setTableData(ausgegl);
                        }
                        catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            MessageHelper.showErrorDialog(getMainFrame(), e);
                        }
                        finally {
                            GuiTools.unlockComponents(new Component[] { btnSearch });
                            stopProgressBar();
                            setDefaultCursor();
                        }
                    }
                };

                setWaitCursor();
                showProgressBar("suchen...");
                GuiTools.lockComponents(new Component[] { btnSearch });
                worker.execute();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSearchComponent#doSearch()
     */
    public void doSearch() {
        execute(CMD_SEARCH);
    }

    /* Ermittle Bankname aus Taifun-Tabelle */
    private String findBankName(Long blz) {
        if (blz == null) {
            return null;
        }
        try {
            RechnungsService reService = getBillingService(RechnungsService.class);
            BLZ bank = reService.findBLZ(blz);
            if (bank != null) {
                return StringUtils.trimToNull(bank.getName());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
