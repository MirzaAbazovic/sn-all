/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 09:30:06
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.SperreVerteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.SperreVerteilungService;


/**
 * Sub-Panel, um die Sperren fuer ein Produkt zu konfigurieren.
 *
 */
public class ProduktSperrePanel extends AbstractServicePanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ProduktSperrePanel.class);

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Produkt model = null;

    private Abteilung4SperreTableModel tbMdlSperren = null;

    /* Map mit allen Sperre-Verteilungen, die dem aktuellen Produkt zugeordnet sind.
     * Es wird lediglich der Key der Map verwendet (Key = ID der zugeordneten Abteilungen).
     */
    private Map<Long, SperreVerteilung> sperreVerteilungen = null;
    /* Map mit den Niederlassungen. Als Key wird die ID der Niederlassung eingetragen,
     * als Value der Name.
     */
    private Map<Long, String> niederlassungen = null;


    /**
     * Konstruktor.
     */
    public ProduktSperrePanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktSperrePanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Erzeuge GUI-Komponenten und ordne diese auf dem Panel an
        AKJLabel lblTitle = getSwingFactory().createLabel("sperre.title");

        tbMdlSperren = new Abteilung4SperreTableModel();
        AKJTable tbSperren = new AKJTable(tbMdlSperren, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSperren.fitTable(new int[] { 100, 100, 40 });
        AKJScrollPane spSperren = new AKJScrollPane(tbSperren);
        spSperren.setPreferredSize(new Dimension(265, 150));

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblTitle, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spSperren, GBCFactory.createGBC(0, 100, 1, 2, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        // Bitte alle GUI Komponenten auf Rechte prüfen, da diverse User nur auf wenige Komponenten rechte haben!
        manageGUI(tbSperren);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            // Niederlassungen laden
            loadNiederlassungen();
            // Abteilungen laden
            loadAbteilungen();
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
            // Sperre-Verteilungen laden
            try {
                SperreVerteilungService svService = getCCService(SperreVerteilungService.class);
                List<SperreVerteilung> svs = svService.findSperreVerteilungen4Produkt(model.getId());
                if (svs != null) {
                    for (SperreVerteilung sv : svs) {
                        sperreVerteilungen.put(sv.getAbteilungId(), sv);
                    }
                }
                tbMdlSperren.fireTableDataChanged();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            // Sperre-Verteilungen speichern
            SperreVerteilungService svService = getCCService(SperreVerteilungService.class);
            Collection<Long> abt4Sperren = (sperreVerteilungen != null) ? sperreVerteilungen.keySet()
                    : new HashSet<Long>();
            List<Long> abtIds = new ArrayList<Long>();
            Iterator<Long> iterator = abt4Sperren.iterator();
            while (iterator.hasNext()) {
                abtIds.add(iterator.next());
            }
            svService.createSperreVerteilungen(model.getId(), abtIds);
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
        return null;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Laedt alle Niederlassungen */
    private void loadNiederlassungen() throws ServiceNotFoundException, FindException {
        niederlassungen = new HashMap<Long, String>();
        NiederlassungService niederlassungsService = getCCService(NiederlassungService.class);
        List<Niederlassung> nls = niederlassungsService.findNiederlassungen();
        if (nls != null) {
            for (Niederlassung n : nls) {
                niederlassungen.put(n.getId(), n.getName());
            }
        }
    }

    /* Laedt alle Abteilungen */
    private void loadAbteilungen() throws ServiceNotFoundException, FindException {
        NiederlassungService niederlassungsService = getCCService(NiederlassungService.class);
        List<Abteilung> abteilungen = niederlassungsService.findAbteilungen();
        tbMdlSperren.setData(abteilungen);
    }

    /* 'Loescht' alle Felder */
    private void clearAll() {
        sperreVerteilungen = new HashMap<Long, SperreVerteilung>();
        tbMdlSperren.fireTableDataChanged();
    }

    /* TableModel fuer die Darstellung der Abteilungen und die Zuordnung zu Sperren. */
    class Abteilung4SperreTableModel extends AKTableModel {
        static final int COL_ABTEILUNG = 0;
        static final int COL_NIEDERLASSUNG = 1;
        static final int COL_INFO = 2;

        static final int COL_COUNT = 3;

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
                case COL_ABTEILUNG:
                    return "Abteilung";
                case COL_NIEDERLASSUNG:
                    return "Niederlassung";
                case COL_INFO:
                    return "informieren";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Abteilung) {
                Abteilung abt = (Abteilung) o;
                switch (column) {
                    case COL_ABTEILUNG:
                        return abt.getName();
                    case COL_NIEDERLASSUNG:
                        return
                                (((niederlassungen != null) && niederlassungen.containsKey(abt.getNiederlassungId())))
                                        ? niederlassungen.get(abt.getNiederlassungId())
                                        : "";
                    case COL_INFO:
                        return
                                (((sperreVerteilungen != null) && sperreVerteilungen.containsKey(abt.getId()))
                                        ? Boolean.TRUE
                                        : Boolean.FALSE);
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Abteilung) {
                Abteilung abt = (Abteilung) o;
                if (sperreVerteilungen == null) {
                    sperreVerteilungen = new HashMap<Long, SperreVerteilung>();
                }

                if (aValue instanceof Boolean) {
                    if (((Boolean) aValue).booleanValue()) {
                        sperreVerteilungen.put(abt.getId(), null);
                    }
                    else {
                        sperreVerteilungen.remove(abt.getId());
                    }
                }
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == COL_INFO) ? true : false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_INFO) ? Boolean.class : String.class;
        }
    }
}


