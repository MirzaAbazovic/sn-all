/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 14:05:24
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.EqVerwendung;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.HVTBestellungView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;


/**
 * Panel zur Darstellung/Verwaltung von HVT-Bestellungen.
 */
public class HVTBestellungenPanel extends AbstractDataPanel implements AKDataLoaderComponent, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(HVTBestellungenPanel.class);

    private AKJButton btnEdit = null;
    private AKJTable tbView = null;
    private AKReflectionTableModel<HVTBestellungView> tbMdlView = null;

    /**
     * Default-Konstruktor.
     */
    public HVTBestellungenPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTBestellungenPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        btnEdit = getSwingFactory().createButton("edit", getActionListener());
        AKJButton btnHistory = getSwingFactory().createButton("history", getActionListener());
        AKJButton btnRefresh = getSwingFactory().createButton("refresh", getActionListener());

        tbMdlView = new AKReflectionTableModel<HVTBestellungView>(
                new String[] { "ASB (VT)", "Ortsteil", "UEVT", "Schwellwert", "Angebot Datum", "Physik",
                        "Anzahl CuDA", "Real. DTAG bis", "Bestell-Nr AKom", "Bestell-Nr DTAG", "Verwendung" },
                new String[] { "hvtASB", "hvtOrtsteil", "uevt", "uevtSchwellwert", "angebotDatum", "physiktyp",
                        "anzahlCuDA", "realDTAGBis", "bestellNrAKom", "bestellNrDTAG", "eqVerwendung" },
                new Class[] { Integer.class, String.class, String.class, Integer.class, Date.class, String.class,
                        String.class, Date.class, String.class, String.class, EqVerwendung.class }
        );
        tbView = new AKJTable(tbMdlView, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbView.attachSorter();
        tbView.fitTable(980, new double[] { 6, 17, 5, 5, 10, 10, 10, 11, 10, 10 });
        tbView.addKeyListener(getRefreshKeyListener());
        tbView.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbView.addPopupAction(new EditSchwellwertAction());
        AKJScrollPane spTable = new AKJScrollPane(tbView);
        spTable.setPreferredSize(new Dimension(980, 500));

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnEdit, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnHistory, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnRefresh, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));

        this.setLayout(new BorderLayout());
        this.add(spTable, BorderLayout.CENTER);
        this.add(btnPanel, BorderLayout.SOUTH);

        manageGUI(btnEdit, btnHistory, btnRefresh);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractDataPanel#refresh()
     */
    @Override
    protected void refresh() {
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            tbMdlView.removeAll();
            HVTToolService hts = getCCService(HVTToolService.class);
            List<HVTBestellungView> views = hts.findHVTBestellungViews();
            tbMdlView.setData(views);
            ObserverHelper.addObserver2Objects(this, views.toArray(new Observable[views.size()]));
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
        if ("edit".equals(command)) {
            editBestellung();
        }
        else if ("history".equals(command)) {
            showBestellungen4Uevt();
        }
        else if ("refresh".equals(command)) {
            refresh();
        }
    }

    /* Oeffnet einen Dialog, um die ausgewaehlte HVT-Bestellung zu editieren. */
    private void editBestellung() {
        if (btnEdit.isEnabled()) {
            try {
                setWaitCursor();
                HVTBestellungEditFrame.openFrame(getSelectedView());
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

    /* Oeffnet einen Dialog mit Ansicht aller HVT-Bestellungen zu dem selektierten UEVT. */
    private void showBestellungen4Uevt() {
        try {
            HVTBestellungView view = getSelectedView();
            if (view.getUevtId() == null) {
                MessageHelper.showInfoDialog(this, "Der selektierte HVT-Standort besitzt keinen UEVT!", null, true);
                return;
            }

            HVTBestellungen4UevtDialog dlg = new HVTBestellungen4UevtDialog(view.getUevtId());
            DialogHelper.showDialog(this, dlg, true, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Gibt die selektierte HVT-Bestellung zurueck oder wirft eine Exception,
     * wenn kein Datensatz selektiert ist.
     * @return
     * @throws HurricanGUIException
     */
    private HVTBestellungView getSelectedView() throws HurricanGUIException {
        AKMutableTableModel model = (AKMutableTableModel) tbView.getModel();
        Object selection = model.getDataAtRow(tbView.getSelectedRow());
        if (selection instanceof HVTBestellungView) {
            return (HVTBestellungView) selection;
        }

        throw new HurricanGUIException("Bitte selektieren Sie zuerst eine HVT-Bestellung.");
    }

    /*
     * Editiert den UEVT-Schwellwert des aktuell selektieren UEVTs.
     */
    private void editUevtSchwellwert() {
        try {
            HVTBestellungView view = getSelectedView();
            if (view == null) {
                return;
            }

            HVTService hvts = getCCService(HVTService.class);
            UEVT uevt = hvts.findUEVT(view.getUevtId());
            if (uevt == null) {
                throw new HurricanGUIException(
                        "Der Schwellwert kann nicht geändert werden, da der UEVT nicht ermittelt werden konnte.");
            }

            String value = MessageHelper.showInputDialog(this,
                    "Bitte definieren Sie den neuen Schwellwert\nfür den UEVT " + uevt.getUevt() + ".\n\n" +
                            "Es sind nur ganzzahlige Werte zulässig!",
                    "Schwellwert definieren", JOptionPane.PLAIN_MESSAGE
            );

            if (StringUtils.isBlank(value)) {
                return;
            }

            try {
                Integer intValue = Integer.valueOf(value);
                if ((intValue >= 0) && (intValue <= 100)) {
                    uevt.setSchwellwert(intValue);
                    hvts.saveUEVT(uevt);

                    view.setUevtSchwellwert(intValue);
                    uevt.notifyObservers(true);
                }
                else {
                    throw new HurricanGUIException("Der Schwellwert muss zwischen 0 und 100 liegen!");
                }
            }
            catch (NumberFormatException e) {
                throw new HurricanGUIException("Ungültige Eingabe für den Schwellwert!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        editBestellung();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        tbView.repaint();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /*
     * Action, um die Schwellwerte von UEVTs zu bestimmen.
     */
    class EditSchwellwertAction extends AKAbstractAction {
        /**
         * Default-Konstruktor.
         */
        public EditSchwellwertAction() {
            putValue(AKAbstractAction.NAME, "Schwellwert ändern");
            putValue(AKAbstractAction.SHORT_DESCRIPTION, "Ermöglicht die Eingabe eines neuen Schwellwerts");
            putValue(AKAbstractAction.ACTION_COMMAND_KEY, "edit.schwellwert");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            editUevtSchwellwert();
        }

    }
}


