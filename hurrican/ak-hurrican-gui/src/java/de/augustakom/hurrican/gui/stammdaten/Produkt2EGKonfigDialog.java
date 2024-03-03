/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2006 09:40:25
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2EG;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 * Dialog, um die zu einem Produkt moeglichen Endgeraete zu konfigurieren.
 *
 *
 */
public class Produkt2EGKonfigDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(Produkt2EGKonfigDialog.class);

    private Produkt produkt = null;

    private AKJList lsEGs = null;
    private AKJTable tbProd2EGs = null;
    private Produkt2EGTableModel tbMdlProd2EGs = null;
    private Map<Long, EG> egMap = null;

    /**
     * Konstruktor mit Angabe des Produkts, zu dem die Endgeraete konfiguriert werden sollen.
     *
     * @param prod
     */
    public Produkt2EGKonfigDialog(Produkt prod) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/Produkt2EGKonfigDialog.xml", true);
        this.produkt = prod;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblEGs = getSwingFactory().createLabel("eg", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblAssigned = getSwingFactory().createLabel("assigned.egs", AKJLabel.LEFT, Font.BOLD);
        AKJButton btnAddEG = getSwingFactory().createButton("add.eg", getActionListener(), null);
        AKJButton btnRemEG = getSwingFactory().createButton("rem.eg", getActionListener(), null);

        lsEGs = getSwingFactory().createList("eg");
        lsEGs.setCellRenderer(new AKCustomListCellRenderer<>(EG.class, EG::getEgName));
        lsEGs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AKJScrollPane spEGs = new AKJScrollPane(lsEGs, new Dimension(150, 120));

        tbMdlProd2EGs = new Produkt2EGTableModel();
        tbProd2EGs = new AKJTable(tbMdlProd2EGs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProd2EGs.attachSorter();
        tbProd2EGs.fitTable(new int[] { 120, 50, 50 });
        AKJScrollPane spProd2EGs = new AKJScrollPane(tbProd2EGs, new Dimension(330, 120));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddEG, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(15, 15, 15, 15)));
        btnPnl.add(btnRemEG, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblEGs, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(btnPnl, GBCFactory.createGBC(0, 0, 2, 1, 1, 2, GridBagConstraints.VERTICAL));
        child.add(lblAssigned, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spEGs, GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));
        child.add(spProd2EGs, GBCFactory.createGBC(100, 100, 3, 2, 1, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            EndgeraeteService egService = getCCService(EndgeraeteService.class);
            List<EG> egs = egService.findEGs(EG.TYP_ENDGERAET_4_PRODUKT);
            List<EG> packets = egService.findEGs(EG.TYP_DEFAULT_PAKET);
            if (CollectionTools.isNotEmpty(packets)) {
                egs.addAll(packets);
            }
            lsEGs.addItems(egs, true);

            egMap = new HashMap<Long, EG>();
            CollectionMapConverter.convert2Map(egs, egMap, "getId", null);

            List<Produkt2EG> prod2EGs = egService.findProdukt2EGs(produkt.getProdId());
            tbMdlProd2EGs.setData(prod2EGs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            EndgeraeteService egService = getCCService(EndgeraeteService.class);
            List<Produkt2EG> prod2egs = (List<Produkt2EG>) tbMdlProd2EGs.getData();

            egService.deleteEGs2Produkt(produkt.getProdId());
            egService.assignEGs2Produkt(produkt.getProdId(), prod2egs);

            prepare4Close();
            setValue(Integer.valueOf(OK_OPTION));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if ("add.eg".equals(command)) {
            // Endgeraet hinzufuegen
            Object selection = lsEGs.getSelectedValue();
            if (selection instanceof EG) {
                EG eg = (EG) selection;

                Produkt2EG toAdd = new Produkt2EG();
                toAdd.setProdId(produkt.getProdId());
                toAdd.setEndgeraetId(eg.getId());

                AKMutableTableModel<Produkt2EG> mdl = (AKMutableTableModel<Produkt2EG>) tbProd2EGs.getModel();
                mdl.addObject(toAdd);
            }
        }
        else if ("rem.eg".equals(command)) {
            // Endgeraete-Zuordnung entfernen
            AKMutableTableModel mdl = (AKMutableTableModel) tbProd2EGs.getModel();
            mdl.removeObject(mdl.getDataAtRow(tbProd2EGs.getSelectedRow()));
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /* TableModel fuer die Produkt2EG Zuordnung. */
    class Produkt2EGTableModel extends AKTableModel<Produkt2EG> {

        private static final int COL_EG = 0;
        private static final int COL_DEFAULT = 1;
        private static final int COL_ACTIVE = 2;

        private static final int COL_COUNT = 3;

        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_EG:
                    return "EG";
                case COL_DEFAULT:
                    return "default";
                case COL_ACTIVE:
                    return "aktiv";
                default:
                    return null;
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object tmp = getDataAtRow(row);
            if (tmp instanceof Produkt2EG) {
                Produkt2EG p2eg = (Produkt2EG) tmp;
                EG eg = egMap.get(p2eg.getEndgeraetId());
                switch (column) {
                    case COL_EG:
                        return (eg != null) ? eg.getEgName() : null;
                    case COL_DEFAULT:
                        return p2eg.getIsDefault();
                    case COL_ACTIVE:
                        return p2eg.getIsActive();
                    default:
                        return null;
                }
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == COL_EG) ? false : true;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (aValue instanceof Boolean) {
                Object tmp = getDataAtRow(row);
                if (tmp instanceof Produkt2EG) {
                    Produkt2EG p2eg = (Produkt2EG) tmp;
                    switch (column) {
                        case COL_DEFAULT:
                            p2eg.setIsDefault((Boolean) aValue);
                            return;
                        case COL_ACTIVE:
                            p2eg.setIsActive((Boolean) aValue);
                            return;
                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_EG) ? String.class : Boolean.class;
        }

    }
}


