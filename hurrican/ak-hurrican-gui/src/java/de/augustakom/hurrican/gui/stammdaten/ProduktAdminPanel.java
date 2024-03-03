/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2004 14:51:05
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Admin-Panel fuer die Verwaltung der Produkte.
 *
 */
public class ProduktAdminPanel extends AbstractAdminPanel implements ChangeListener, AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(ProduktAdminPanel.class);
    private static final long serialVersionUID = -7284827692262820773L;

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Produkt model = null;

    // GUI-Komponenten
    private AKJTable produkteTable = null;
    private AKJTabbedPane tabbedPane = null;

    // Panel fuer die techn. Leistungen
    private Produkt2TechLsPanel p2techLsPnl = null;
    // Panel fuer die DN-Daten
    private ProduktDnPanel pDnPnl = null;
    // Panel fuer die PhysikTyp-Zuordnung
    private ProduktPhysikPanel pPtPnl = null;
    // Panel fuer die Sperren
    private ProduktSperrePanel pSperrePnl = null;
    // Panel fuer die technischen Daten
    private ProduktTechPanel pTechPnl = null;
    // Panel fuer die administrativen Daten
    private Produkt2AdminPanel pAdminPnl = null;
    // Panel fuer die CPS Daten
    private Produkt2CPSPanel cpsPnl = null;

    /**
     * Konstruktor
     */
    public ProduktAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktAdminPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Erzeuge Sub-Panels
        p2techLsPnl = new Produkt2TechLsPanel();
        pDnPnl = new ProduktDnPanel();
        pPtPnl = new ProduktPhysikPanel();
        pSperrePnl = new ProduktSperrePanel();
        pAdminPnl = new Produkt2AdminPanel();
        pTechPnl = new ProduktTechPanel();
        cpsPnl = new Produkt2CPSPanel();

        // Sub-Panels zu AKJTabbedPane hinzufügen
        tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab("technische Daten", pTechPnl);
        tabbedPane.addTab("administr. Daten", pAdminPnl);
        tabbedPane.addTab("Rufnummer", pDnPnl);
        tabbedPane.addTab("Physik-Zuordnung", pPtPnl);
        tabbedPane.addTab("Sperren", pSperrePnl);
        tabbedPane.addTab("techn. Leistungen", p2techLsPnl);
        tabbedPane.addTab("CPS", cpsPnl);

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof Produkt) {
            this.model = (Produkt) details;

            // ausgewaehltes Produkt den Sub-Panels uebergeben
            pDnPnl.setModel(model);
            p2techLsPnl.setModel(model);
            pPtPnl.setModel(model);
            pSperrePnl.setModel(model);
            pTechPnl.setModel(model);
            pAdminPnl.setModel(model);
            cpsPnl.setModel(model);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        // Erzeuge leeres Produkt und übergebe dies an die Sub-Panels zur Anzeige
        this.model = new Produkt();

        // Neues Produkt an die Sub-Panels uebergeben
        pDnPnl.setModel(model);
        p2techLsPnl.setModel(model);
        pPtPnl.setModel(model);
        pSperrePnl.setModel(model);
        pAdminPnl.setModel(model);
        pTechPnl.setModel(model);
        cpsPnl.setModel(model);

        // Setze Focus auf erstes Tab und oberstes Textfeld
        tabbedPane.setSelectedIndex(0);
        pTechPnl.setFocusTfId();
    }

    /**
     * Uebergibt dem Admin-Panel die Tabelle, die eine Uebersicht ueber die Produkte darstellt.
     */
    protected void setProdukteTable(AKJTable produkteTable) {
        this.produkteTable = produkteTable;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            setWaitCursor();

            if (model == null) { model = new Produkt(); }
            boolean wasNew = (model.getId() == null);

            // Achtung Seiteneffekte! getModel() auf den jeweiligen Sub-Panels
            // modifiziert model (welches sich alle Sub-Panels teilen).
            pTechPnl.getModel();
            pAdminPnl.getModel();
            pPtPnl.getModel();
            pDnPnl.getModel();
            cpsPnl.getModel();

            // Speicher Produkt
            ProduktService service = getCCService(ProduktService.class);
            service.saveProdukt(model);

            // Speicher Daten auf Zuordnungspanels
            pTechPnl.saveModel();
            pAdminPnl.saveModel();
            pPtPnl.saveModel();
            pSperrePnl.saveModel();
            cpsPnl.saveModel();

            // Lade Produkt-Tabelle neu, falls neues Produkt eingefügt wurde
            if (wasNew && (produkteTable != null)) {
                ((AKMutableTableModel) produkteTable.getModel()).addObject(model);
                produkteTable.requestFocus();
                produkteTable.scrollToRow(produkteTable.getRowCount());
                produkteTable.setRowSelectionInterval(produkteTable.getRowCount() - 1, produkteTable.getRowCount() - 1);
            }

            // Veranlasst, dass alle Daten des Produkts neu geladen werden.
            showDetails(model);
            if (produkteTable != null) {
                ((AbstractTableModel) produkteTable.getModel()).fireTableDataChanged();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
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

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
