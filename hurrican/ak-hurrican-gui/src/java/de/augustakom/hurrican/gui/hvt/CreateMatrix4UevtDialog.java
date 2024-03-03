/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2004 08:40:25
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog fuer die Auswahl der Produkte fuer die eine Rangierungsmatrix fuer einen best. UEVT erstellt werden soll.
 *
 *
 */
public class CreateMatrix4UevtDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(CreateMatrix4UevtDialog.class);

    // GUI-Komponenten
    private AKJTextField tfUevt = null;
    private AKJCheckBox chbSelectAll = null;
    private ProduktSelectionTableModel tbMdlProdukte = null;

    // Modelle
    private UEVT uevt = null;
    private Map<Long, Leitungsart> leitungsartenMap = null;
    private List<Produkt> produkte = null;
    private Map<Long, Produkt> selectedProducts = null;

    /**
     * Konstruktor mit Angabe eines UEVTs.
     *
     * @param uevt UEVT fuer den eine Rangierungsmatrix erstellt werden soll.
     */
    public CreateMatrix4UevtDialog(UEVT uevt) {
        super("de/augustakom/hurrican/gui/hvt/resources/CreateMatrix4UevtDialog.xml");
        this.uevt = uevt;
        init();
        createGUI();
        load();
    }

    /* Initialisiert den Dialog. */
    private void init() {
        leitungsartenMap = new HashMap<Long, Leitungsart>();
        selectedProducts = new HashMap<Long, Produkt>();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Produkte für Matrix auswählen");
        configureButton(CMD_SAVE, getSwingFactory().getText("save.btn.text"),
                getSwingFactory().getText("save.btn.tooltip"), true, true);
        getButton(CMD_SAVE).getAccessibleContext().setAccessibleName("create.matrix.4.uevt");

        AKJLabel lblUevt = getSwingFactory().createLabel("uevt");
        AKJLabel lblProdukte = getSwingFactory().createLabel("produkte");
        tfUevt = getSwingFactory().createTextField("uevt", false);
        chbSelectAll = getSwingFactory().createCheckBox("select.all", getActionListener(), false);

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblUevt, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(tfUevt, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblProdukte, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbSelectAll, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlProdukte = new ProduktSelectionTableModel();
        AKJTable tbProdukte = new AKJTable(tbMdlProdukte, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProdukte.attachSorter();
        tbProdukte.fitTable(new int[] { 50, 45, 150, 80 });
        AKJScrollPane spProdukte = new AKJScrollPane(tbProdukte);
        spProdukte.setPreferredSize(new Dimension(360, 200));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(top, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spProdukte, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));

        manageGUI(new AKManageableComponent[] { getButton(CMD_SAVE) });
    }

    /* Selektiert alle UEVTs. */
    private void selectAll() {
        if (chbSelectAll.isSelected()) {
            CollectionMapConverter.convert2Map(produkte, selectedProducts, "getId", null);
        }
        else {
            selectedProducts.clear();
        }
        tbMdlProdukte.fireTableDataChanged();
    }

    /* Laedt die benoetigten Daten. */
    private void load() {
        try {
            setWaitCursor();

            // Leitungsarten laden
            ProduktService ps = getCCService(ProduktService.class);
            List<Leitungsart> larts = ps.findLeitungsarten();
            CollectionMapConverter.convert2Map(larts, leitungsartenMap, "getId", null);

            // Produkte laden
            produkte = ps.findProdukte(true);
            tbMdlProdukte.setData(produkte);

            if (uevt != null) {
                tfUevt.setText(uevt.getUevt());
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
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setWaitCursor();

            List<Long> uevtIds = new ArrayList<Long>();
            uevtIds.add(uevt.getId());

            List<Long> prodIds = new ArrayList<Long>();
            Set<Long> keys = selectedProducts.keySet();
            if (keys != null) {
                Iterator<Long> it = keys.iterator();
                while (it.hasNext()) {
                    prodIds.add(it.next());
                }
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            List<Rangierungsmatrix> createdMatrix =
                    rs.createMatrix(HurricanSystemRegistry.instance().getSessionId(), uevtIds, prodIds, null);

            prepare4Close();
            setValue(Integer.valueOf(OK_OPTION));

            // Anzahl angelegter Matrizen ausgeben
            String anzahl = (createdMatrix != null) ? "" + createdMatrix.size() : "";
            String msg = getSwingFactory().getText("anzahl.matrizen", anzahl);
            MessageHelper.showMessageDialog(getMainFrame(),
                    msg, "Anzahl angelegter Matrizen", JOptionPane.INFORMATION_MESSAGE);
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
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("select.all".equals(command)) {
            selectAll();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* TableModel fuer die Selektion der Produkte fuer die eine Matrix erstellt werden soll. */
    class ProduktSelectionTableModel extends AKTableModel {
        static final int COL_SELECTED = 0;
        static final int COL_ID = 1;
        static final int COL_ANSCHLUSSART = 2;
        static final int COL_LEITUNG = 3;

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
                case COL_SELECTED:
                    return "Auswahl";
                case COL_ID:
                    return "ID";
                case COL_ANSCHLUSSART:
                    return "Produkt";
                case COL_LEITUNG:
                    return "Leitungsart";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Produkt) {
                Produkt p = (Produkt) o;
                switch (column) {
                    case COL_SELECTED:
                        return (selectedProducts.containsKey(p.getId()))
                                ? Boolean.TRUE : Boolean.FALSE;
                    case COL_ID:
                        return p.getId();
                    case COL_ANSCHLUSSART:
                        return p.getAnschlussart();
                    case COL_LEITUNG:
                        Object lart = leitungsartenMap.get(p.getLeitungsart());
                        return (lart instanceof Leitungsart) ? ((Leitungsart) lart).getName() : null;
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
            if ((column == COL_SELECTED) && (aValue instanceof Boolean)) {
                Object o = getDataAtRow(row);
                if (o instanceof Produkt) {
                    Produkt p = (Produkt) o;
                    if (((Boolean) aValue).booleanValue()) {
                        selectedProducts.put(p.getId(), null);
                    }
                    else {
                        selectedProducts.remove(p.getId());
                    }
                }
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == COL_SELECTED) ? true : false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int ci) {
            switch (ci) {
                case COL_SELECTED:
                    return Boolean.class;
                case COL_ID:
                    return Long.class;
                default:
                    return String.class;
            }
        }
    }

}


