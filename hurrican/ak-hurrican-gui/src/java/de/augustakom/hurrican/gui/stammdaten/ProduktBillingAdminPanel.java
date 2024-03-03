/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2006 15:07:41
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.tools.dn.DnLeistungsbuendelAnlegenDialog;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;


/**
 * Admin-Panel fuer die Verwaltung der Produkte aus dem Billing-System.
 *
 *
 */
public class ProduktBillingAdminPanel extends AbstractAdminPanel implements AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(ProduktBillingAdminPanel.class);

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private OE actProduct = null;
    private Leistung actLeistung = null;

    /* Map mit allen Leistungsbuendeln
     * Es wird lediglich der Kay der Map verwendet (Key = OE__NO der OE).
     */
    private Set<Long> leistungsbuendel = null;
    private AKJTable produkteTable = null;

    private AKReflectionTableModel<Leistung> tbMdlOELeistungen = null;

    private LeistungsbuendelTableModel tbMdlLeistungsbuendel = null;

    /**
     * Default-Const.
     */
    public ProduktBillingAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktBillingAdminPanel.xml");
        init();
        createGUI();
    }

    /* Initialisiert das Panel. */
    private void init() {
        leistungsbuendel = new HashSet<Long>();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        AKJButton btnNew = getSwingFactory().createButton("btn.new", getActionListener());

        tbMdlOELeistungen = new AKReflectionTableModel<Leistung>(
                new String[] { "L__NO", "Name" },
                new String[] { "leistungNoOrig", "name" },
                new Class[] { Long.class, String.class });
        AKJTable tbOELeistungen = new AKJTable(tbMdlOELeistungen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbOELeistungen.fitTable(new int[] { 70, 220 });
        tbOELeistungen.addTableListener(this);
        AKJScrollPane spOELeistungen = new AKJScrollPane(tbOELeistungen, new Dimension(305, 150));

        AKJPanel oelPnl = new AKJPanel(new GridBagLayout(), "Prod.-Leistung");
        oelPnl.add(spOELeistungen, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        tbMdlLeistungsbuendel = new LeistungsbuendelTableModel();
        AKJTable tbLeistungsbuendel = new AKJTable(tbMdlLeistungsbuendel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLeistungsbuendel.fitTable(new int[] { 40, 150, 220, 40 });
        AKJScrollPane spDnOe = new AKJScrollPane(tbLeistungsbuendel);
        spDnOe.setPreferredSize(new Dimension(450, 150));

        AKJPanel dnPanel = new AKJPanel(new GridBagLayout(), "Leistungsbündel");
        dnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dnPanel.add(btnNew, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        dnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        dnPanel.add(spDnOe, GBCFactory.createGBC(100, 100, 0, 1, 3, 1, GridBagConstraints.BOTH));
        dnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel dnLeistungPanel = new AKJPanel(new GridBagLayout());
        dnLeistungPanel.add(oelPnl, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        dnLeistungPanel.add(dnPanel, GBCFactory.createGBC(100, 100, 1, 0, 1, 1, GridBagConstraints.BOTH));

        tabbedPane.addTab("Rufnummer-Leistungen", dnLeistungPanel);

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            // DN-Oetypen laden
            loadLeistungsbuendel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Laedt alle verfügbaren Leistungsbuendel. */
    private void loadLeistungsbuendel() throws ServiceNotFoundException, FindException {
        CCRufnummernService service = getCCService(CCRufnummernService.class);
        List<Leistungsbuendel> Leistungsbuendel = service.findLeistungsbuendel();
        tbMdlLeistungsbuendel.setData(Leistungsbuendel);
    }

    /**
     * Uebergibt dem Admin-Panel die Tabelle, die eine Uebersicht ueber die Produkte darstellt.
     *
     * @param tableModel
     */
    protected void setProdukteTable(AKJTable produkteTable) {
        this.produkteTable = produkteTable;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        clearAll();
        if (details instanceof OE) {
            this.actProduct = (OE) details;
            // Leistungen des Produkts laden
            try {
                LeistungService ls = getBillingService(LeistungService.class);
                List<Leistung> leistungen = ls.findLeistungen4OE(this.actProduct.getOeNoOrig(), true);
                tbMdlOELeistungen.setData(leistungen);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        else if (details instanceof Leistung) {
            this.actLeistung = (Leistung) details;
            //zugeordnete Leistungsbuendel laden
            try {
                CCRufnummernService serviceDn = getCCService(CCRufnummernService.class);
                List<Leistungsbuendel2Produkt> lb2ps =
                        serviceDn.findLeistungsbuendel2Produkt(actLeistung.getLeistungNoOrig());
                if (lb2ps != null) {
                    for (Leistungsbuendel2Produkt lb2p : lb2ps) {
                        leistungsbuendel.add(lb2p.getLbId());
                    }
                }
                tbMdlLeistungsbuendel.fireTableDataChanged();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            setWaitCursor();

            if (actProduct == null) { actProduct = new OE(); }
            boolean wasNew = (actProduct.getOeNoOrig() == null) ? true : false;

            // Leistungsbuendel2Produkt-Mappings speichern
            CCRufnummernService serviceDn = getCCService(CCRufnummernService.class);
            Collection<Long> lb2p = (leistungsbuendel != null) ? leistungsbuendel : new HashSet<Long>();
            serviceDn.saveLeistungsbuendel2Produkt(
                    actProduct.getOeNoOrig(), actLeistung.getLeistungNoOrig(), lb2p);

            if (wasNew && (produkteTable != null)) {
                ((AKMutableTableModel) produkteTable.getModel()).addObject(actProduct);
                produkteTable.requestFocus();
                produkteTable.scrollToRow(produkteTable.getRowCount());
                produkteTable.setRowSelectionInterval(produkteTable.getRowCount() - 1, produkteTable.getRowCount() - 1);
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
        if ("btn.new".equals(command)) {
            DnLeistungsbuendelAnlegenDialog dlg = new DnLeistungsbuendelAnlegenDialog();
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            loadData();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /* 'Loescht' alle Felder */
    private void clearAll() {
        leistungsbuendel = new HashSet<Long>();
        tbMdlLeistungsbuendel.fireTableDataChanged();
    }

    /**
     * TableModel fuer die Darstellung der Leistungsbuendelzuordnungen.
     */
    class LeistungsbuendelTableModel extends AKTableModel<Leistungsbuendel> {
        static final int COL_LB_ID = 0;
        static final int COL_NAME = 1;
        static final int COL_BESCHREIBUNG = 2;
        static final int COL_USE = 3;

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
                case COL_LB_ID:
                    return "LB_ID";
                case COL_NAME:
                    return "Name";
                case COL_BESCHREIBUNG:
                    return "Beschreibung";
                case COL_USE:
                    return "verwenden";
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
            if (o instanceof Leistungsbuendel) {
                Leistungsbuendel lb = (Leistungsbuendel) o;
                switch (column) {
                    case COL_LB_ID:
                        return lb.getId();
                    case COL_NAME:
                        return lb.getName();
                    case COL_BESCHREIBUNG:
                        return lb.getBeschreibung();
                    case COL_USE:
                        return
                                (((leistungsbuendel != null) && leistungsbuendel.contains(lb.getId()))
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
            if (o instanceof Leistungsbuendel) {
                Leistungsbuendel lb = (Leistungsbuendel) o;
                if (leistungsbuendel == null) {
                    leistungsbuendel = new HashSet<Long>();
                }

                if (aValue instanceof Boolean) {
                    if (((Boolean) aValue).booleanValue()) {
                        leistungsbuendel.add(lb.getId());
                    }
                    else {
                        leistungsbuendel.remove(lb.getId());
                    }
                }
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == COL_USE) ? true : false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_USE) ? Boolean.class : super.getColumnClass(columnIndex);
        }
    }

}


