/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 12:14:48
 */
package de.augustakom.hurrican.gui.kubena;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.kubena.Kubena;
import de.augustakom.hurrican.model.cc.kubena.KubenaHVT;
import de.augustakom.hurrican.model.cc.kubena.KubenaProdukt;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.KubenaService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel zur Auswahl der HVTs und Produkte, die in die Kundenbenachrichtigung mit einbezogen werden sollen.
 *
 *
 */
public class KubenaHVTSelectionPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(KubenaHVTSelectionPanel.class);

    private Kubena kubena = null;
    private boolean showProdukte = false;

    private AKJTable tbHVTs = null;
    private AKReflectionTableModel<HVTGruppeStdView> tbMdlHVTs = null;
    private AKJTable tbHVTsSelected = null;
    private AKReflectionTableModel<HVTGruppeStdView> tbMdlHVTsSelected = null;
    private AKJTable tbProdukte = null;
    private AKReflectionTableModel<Produkt> tbMdlProdukte = null;
    private AKJTable tbProdukteSelected = null;
    private AKReflectionTableModel<Produkt> tbMdlProdukteSelected = null;

    /**
     * Konstruktor mit Angabe der Kubena, deren HVT-Details angezeigt werden sollen.
     *
     * @param kubena
     */
    public KubenaHVTSelectionPanel(Kubena kubena) {
        super("de/augustakom/hurrican/gui/kubena/resources/KubenaHVTSelectionPanel.xml");
        this.kubena = kubena;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJButton btnAddHVT = getSwingFactory().createButton("add.hvt", getActionListener());
        AKJButton btnRemoveHVT = getSwingFactory().createButton("remove.hvt", getActionListener());
        AKJLabel lblHvt = getSwingFactory().createLabel("hvt");
        Dimension hvtTableDim = new Dimension(235, 200);

        String[] hvtHeader = new String[] { "ONKZ", "ASB", "Ortsteil" };
        String[] hvtProps = new String[] { "onkz", "asb", "ortsteil" };
        Class[] hvtClasses = new Class[] { String.class, Integer.class, String.class };
        int[] hvtColWidth = new int[] { 35, 30, 150 };

        tbMdlHVTs = new AKReflectionTableModel<HVTGruppeStdView>(hvtHeader, hvtProps, hvtClasses);
        tbHVTs = new AKJTable(tbMdlHVTs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbHVTs.attachSorter();
        tbHVTs.fitTable(hvtColWidth);
        AKJScrollPane spHVT = new AKJScrollPane(tbHVTs, hvtTableDim);

        tbMdlHVTsSelected = new AKReflectionTableModel<HVTGruppeStdView>(hvtHeader, hvtProps, hvtClasses);
        tbHVTsSelected = new AKJTable(tbMdlHVTsSelected, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbHVTsSelected.attachSorter();
        tbHVTsSelected.fitTable(hvtColWidth);
        AKJScrollPane spHVTSelected = new AKJScrollPane(tbHVTsSelected, hvtTableDim);

        AKJPanel btnPnlHvt = new AKJPanel(new GridBagLayout());
        btnPnlHvt.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnlHvt.add(btnAddHVT, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnlHvt.add(btnRemoveHVT, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        btnPnlHvt.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblHvt, GBCFactory.createGBC(0, 0, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(spHVT, GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        left.add(btnPnlHvt, GBCFactory.createGBC(0, 100, 1, 1, 1, 1, GridBagConstraints.VERTICAL));
        left.add(spHVTSelected, GBCFactory.createGBC(0, 100, 2, 1, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));

        if (NumberTools.equal(kubena.getKriterium(), Kubena.KRITERIUM_HVT_PROD)) {
            showProdukte = true;

            AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
            AKJButton btnAddProdukt = getSwingFactory().createButton("add.produkt", getActionListener());
            AKJButton btnRemoveProdukt = getSwingFactory().createButton("remove.produkt", getActionListener());

            String[] prodHeader = new String[] { "ID", "Produkt" };
            String[] prodProps = new String[] { "id", "anschlussart" };
            Class[] prodClasses = new Class[] { Long.class, String.class };
            int[] prodColWidth = new int[] { 35, 150 };
            Dimension prodTableDim = new Dimension(205, 200);

            tbMdlProdukte = new AKReflectionTableModel<Produkt>(prodHeader, prodProps, prodClasses);
            tbProdukte = new AKJTable(tbMdlProdukte, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tbProdukte.attachSorter();
            tbProdukte.fitTable(prodColWidth);
            AKJScrollPane spProdukt = new AKJScrollPane(tbProdukte, prodTableDim);

            tbMdlProdukteSelected = new AKReflectionTableModel<Produkt>(prodHeader, prodProps, prodClasses);
            tbProdukteSelected = new AKJTable(tbMdlProdukteSelected, AKJTable.AUTO_RESIZE_OFF,
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tbProdukteSelected.attachSorter();
            tbProdukteSelected.fitTable(prodColWidth);
            AKJScrollPane spProdSel = new AKJScrollPane(tbProdukteSelected, prodTableDim);

            AKJPanel btnPnlProd = new AKJPanel(new GridBagLayout());
            btnPnlProd.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            btnPnlProd.add(btnAddProdukt, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
            btnPnlProd.add(btnRemoveProdukt, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
            btnPnlProd.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

            AKJPanel right = new AKJPanel(new GridBagLayout());
            right.add(lblProdukt, GBCFactory.createGBC(0, 0, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
            right.add(spProdukt, GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
            right.add(btnPnlProd, GBCFactory.createGBC(0, 100, 1, 1, 1, 1, GridBagConstraints.VERTICAL));
            right.add(spProdSel, GBCFactory.createGBC(0, 100, 2, 1, 1, 1, GridBagConstraints.VERTICAL));

            this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
            this.add(right, GBCFactory.createGBC(0, 100, 2, 0, 1, 1, GridBagConstraints.VERTICAL));
            this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        else {
            this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();
            tbMdlHVTs.setData(hvtViews);

            Map<Long, HVTGruppeStdView> hvtViewMap = Maps.newHashMap();
            CollectionMapConverter.convert2Map(hvtViews, hvtViewMap, "getHvtIdStandort", null);

            // ermitteln, welche HVTs bereits in der Kubena aufgenommen sind
            KubenaService ks = getCCService(KubenaService.class);
            List<KubenaHVT> kubenaHVTs = ks.findKubenaHVTs(kubena.getId());
            if (kubenaHVTs != null) {
                for (KubenaHVT k : kubenaHVTs) {
                    tbMdlHVTsSelected.addObject(hvtViewMap.get(k.getHvtIdStandort()));
                }
            }

            if (showProdukte) {
                ProduktService ps = getCCService(ProduktService.class);
                List<Produkt> produkte = ps.findProdukte(false);
                tbMdlProdukte.setData(produkte);

                Map<Long, Produkt> produktMap = Maps.newHashMap();
                CollectionMapConverter.convert2Map(produkte, produktMap, "getId", null);

                // ermitteln, welche Produkte bereits in der Kubena aufgenommen sind
                List<KubenaProdukt> kubenaProdukte = ks.findKubenaProdukt(kubena.getId());
                if (kubenaProdukte != null) {
                    for (KubenaProdukt p : kubenaProdukte) {
                        tbMdlProdukteSelected.addObject(produktMap.get(p.getProdId()));
                    }
                }
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
        if ("add.hvt".equals(command)) {
            addSelection(tbHVTs, tbHVTsSelected, "getHvtIdStandort");
        }
        else if ("remove.hvt".equals(command)) {
            removeSelection(tbHVTsSelected);
        }
        else if ("add.produkt".equals(command)) {
            addSelection(tbProdukte, tbProdukteSelected, "getId");
        }
        else if ("remove.produkt".equals(command)) {
            removeSelection(tbProdukteSelected);
        }
    }

    /*
     * Fuegt die selektierten Objekte aus <code>tbSource</code> in die Tabelle
     * <code>tbDest</code> ein - sofern noch nicht vorhanden.
     * @param tbSource Quell-Tabelle
     * @param tbDest Ziel-Tabelle
     * @param idMethod Name der Methode, ueber die die eindeutige ID ermittelt werden kann.
     */
    private void addSelection(AKJTable tbSource, AKJTable tbDest, String idMethod) {
        AKMutableTableModel mdlSource = (AKMutableTableModel) tbSource.getModel();
        AKMutableTableModel mdlDest = (AKMutableTableModel) tbDest.getModel();

        Map destMap = new HashMap();
        Collection destObjects = (mdlDest.getData() != null) ? mdlDest.getData() : new ArrayList();
        CollectionMapConverter.convert2Map(destObjects, destMap, idMethod, null);

        List<KubenaHVT> hvts2Add = new ArrayList<>();
        List<KubenaProdukt> produkte2Add = new ArrayList<>();

        try {
            setWaitCursor();

            int[] selection = tbSource.getSelectedRows();
            for (int i = 0; i < selection.length; i++) {
                Object value = mdlSource.getDataAtRow(selection[i]);
                Object id = MethodUtils.invokeMethod(value, idMethod, null);
                if (!destMap.containsKey(id)) {
                    mdlDest.addObject(value);

                    // speichern
                    if (value instanceof HVTGruppeStdView) {
                        KubenaHVT khvt = new KubenaHVT();
                        khvt.setKubenaId(kubena.getId());
                        khvt.setHvtIdStandort(((HVTGruppeStdView) value).getHvtIdStandort());
                        hvts2Add.add(khvt);
                    }
                    else if (value instanceof Produkt) {
                        KubenaProdukt kp = new KubenaProdukt();
                        kp.setKubenaId(kubena.getId());
                        kp.setProdId(((Produkt) value).getId());
                        produkte2Add.add(kp);
                    }
                }
            }

            KubenaService ks = getCCService(KubenaService.class);
            ks.saveKubenaHVTs(hvts2Add);
            ks.saveKubenaProdukte(produkte2Add);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /*
     * Entfernt die selektierten Eintraege aus der Tabelle <code>table</code>
     * @param table
     */
    private void removeSelection(AKJTable table) {
        AKMutableTableModel mdl = (AKMutableTableModel) table.getModel();
        int[] selection = table.getSelectedRows();

        if (selection.length > 0) {
            List toRemove = new ArrayList();
            for (int i = 0; i < selection.length; i++) {
                toRemove.add(mdl.getDataAtRow(selection[i]));
            }

            List<Long> hvtStdIds = new ArrayList<>();
            List<Long> prodIds = new ArrayList<>();

            for (Iterator iter = toRemove.iterator(); iter.hasNext(); ) {
                Object next = iter.next();
                mdl.removeObject(next);

                if (next instanceof HVTGruppeStdView) {
                    hvtStdIds.add(((HVTGruppeStdView) next).getHvtIdStandort());
                }
                else if (next instanceof Produkt) {
                    prodIds.add(((Produkt) next).getId());
                }
            }

            try {
                setWaitCursor();

                KubenaService ks = getCCService(KubenaService.class);
                ks.deleteKubenaHVTs(kubena.getId(), hvtStdIds);
                ks.deleteKubenaProdukte(kubena.getId(), prodIds);
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

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


