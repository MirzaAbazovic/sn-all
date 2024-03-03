/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2004 14:16:26
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
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
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog fuer die Auswahl der UEVTs fuer die eine Rangierungsmatrix fuer ein best. Produkt erstellt werden soll.
 *
 *
 */
public class CreateMatrix4ProduktDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(CreateMatrix4ProduktDialog.class);

    // GUI-Elemente
    private AKJTextField tfProdukt = null;
    private AKJCheckBox chbSelectAll = null;
    private UEVTSelectionTableModel tbMdlUEVTs = null;

    // Modelle
    private Produkt produkt = null;
    private Map<Long, HVTStandort> hvtStdMap = null;
    private Map<Long, HVTGruppe> hvtGrMap = null;
    private Map<Long, UEVT> selectedUEVTs = null;

    /**
     * Konstruktor.
     */
    public CreateMatrix4ProduktDialog(Produkt produkt) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/Matrix4ProduktDialog.xml");
        this.produkt = produkt;
        init();
        createGUI();
        load();
    }

    /* Initialisiert den Dialog. */
    private void init() {
        hvtStdMap = new HashMap<Long, HVTStandort>();
        hvtGrMap = new HashMap<Long, HVTGruppe>();
        selectedUEVTs = new HashMap<Long, UEVT>();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("UEVTs für Matrix auswählen");
        configureButton(CMD_SAVE, getSwingFactory().getText("save.btn.text"),
                getSwingFactory().getText("save.btn.tooltip"), true, true);
        getButton(CMD_SAVE).getAccessibleContext().setAccessibleName("create.matrix.4.produkt");

        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblUevt = getSwingFactory().createLabel("uevt");
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        chbSelectAll = getSwingFactory().createCheckBox("select.all", getActionListener(), false);

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblProdukt, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(tfProdukt, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblUevt, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbSelectAll, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlUEVTs = new UEVTSelectionTableModel();
        AKJTable tbUEVTs = new AKJTable(tbMdlUEVTs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbUEVTs.attachSorter();
        tbUEVTs.fitTable(new int[] { 50, 150, 80 });
        AKJScrollPane spUEVTs = new AKJScrollPane(tbUEVTs);
        spUEVTs.setPreferredSize(new Dimension(305, 200));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(top, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spUEVTs, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));

        manageGUI(new AKManageableComponent[] { getButton(CMD_SAVE) });
    }

    /* Setzt den Save-Button auf enabled/disabled. */
    private void enableSaveBtn(boolean enable) {
        AKJButton btn = getButton(CMD_SAVE);
        if (btn != null) {
            btn.setEnabled(enable);
        }
    }

    public static class WorkerResult {
        List<HVTStandort> hvtStandorte;
        List<HVTGruppe> hvtGruppen;
        List<UEVT> uevts;
    }

    /* Laedt alle benoetigten Daten. */
    private void load() {
        final SwingWorker<WorkerResult, Void> worker = new SwingWorker<WorkerResult, Void>() {

            @Override
            protected WorkerResult doInBackground() throws Exception {
                WorkerResult workerResult = new WorkerResult();

                // HVT-Gruppen und -Standorte laden
                HVTService service = getCCService(HVTService.class);
                workerResult.hvtGruppen = service.findHVTGruppen();
                workerResult.hvtStandorte = service.findHVTStandorte();

                // UEVTs laden
                workerResult.uevts = service.findUEVTs();

                return workerResult;
            }

            @Override
            protected void done() {
                try {
                    WorkerResult result = get();

                    CollectionMapConverter.convert2Map(result.hvtGruppen, hvtGrMap, "getId", null);
                    CollectionMapConverter.convert2Map(result.hvtStandorte, hvtStdMap, "getId", null);
                    tbMdlUEVTs.setData(result.uevts);

                    if (produkt != null) {
                        tfProdukt.setText(produkt.getAnschlussart());
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableSaveBtn(true);
                    setDefaultCursor();
                }
            }
        };
        setWaitCursor();
        enableSaveBtn(false);
        worker.execute();
    }

    /* Selektiert alle UEVTs. */
    private void selectAll() {
        if (chbSelectAll.isSelected()) {
            CollectionMapConverter.convert2Map(tbMdlUEVTs.getData(), selectedUEVTs, "getId", null);
        }
        else {
            selectedUEVTs.clear();
        }
        tbMdlUEVTs.fireTableDataChanged();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setWaitCursor();
            enableSaveBtn(false);

            List<Long> prodIds = new ArrayList<Long>();
            prodIds.add(produkt.getId());

            List<Long> uevtIds = new ArrayList<Long>();
            Set<Long> keys = selectedUEVTs.keySet();
            if (keys != null) {
                for (Long next : keys) {
                    uevtIds.add(next);
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
            enableSaveBtn(true);
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

    /* TableModel fuer die Auswahl der UEVTs fuer die eine Matrix erstellt werden soll. */
    class UEVTSelectionTableModel extends AKTableModel<UEVT> {
        static final int COL_SELECTED = 0;
        static final int COL_HVT = 1;
        static final int COL_UEVT = 2;

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
                case COL_SELECTED:
                    return "Auswahl";
                case COL_HVT:
                    return "HVT";
                case COL_UEVT:
                    return "UEVT";
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
            if (o instanceof UEVT) {
                UEVT uevt = (UEVT) o;
                switch (column) {
                    case COL_SELECTED:
                        return (selectedUEVTs.containsKey(uevt.getId())) ? Boolean.TRUE : Boolean.FALSE;
                    case COL_HVT:
                        StringBuilder hvt = new StringBuilder();
                        HVTStandort hvtStd = hvtStdMap.get(uevt.getHvtIdStandort());
                        HVTGruppe hvtGr = hvtGrMap.get((hvtStd).getHvtGruppeId());
                        hvt.append(hvtGr.getOrtsteil());
                        hvt.append(" - ");
                        hvt.append((hvtStd).getId());
                        return hvt.toString();
                    case COL_UEVT:
                        return uevt.getUevt();
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
                if (o instanceof UEVT) {
                    UEVT uevt = (UEVT) o;
                    if (((Boolean) aValue).booleanValue()) {
                        selectedUEVTs.put(uevt.getId(), null);
                    }
                    else {
                        selectedUEVTs.remove(uevt.getId());
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
                case COL_UEVT:
                    return Integer.class;
                default:
                    return String.class;
            }
        }
    }
}


