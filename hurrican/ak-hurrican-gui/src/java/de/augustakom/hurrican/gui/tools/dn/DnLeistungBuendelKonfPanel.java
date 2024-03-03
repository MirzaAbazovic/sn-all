/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.02.2006 17:20:40
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.view.LeistungInLeistungsbuendelView;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Konfiguration der Leistungsbuendel
 *
 *
 */
public class DnLeistungBuendelKonfPanel extends AbstractDataPanel implements AKTableOwner, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(DnLeistungParameterPanel.class);
    private static final long serialVersionUID = -7693940931675972587L;

    private LeistungenSelectedTableModel tbMdlLeistungenSelected = null;
    private LeistungenTableModel tbMdlLeistungen = null;
    private OeTypTableModel tbMdlOeTyp = null;
    private AKJTable tblLeistungen = null;

    private AKJCheckBox chbStandard = null;
    private AKJTextField tfDefParamVal = null;
    private AKJLabel lblDefParamVal = null;

    private LeistungsbuendelTableModell tbMdlLeistungsbuendel = null;

    private AKJDateComponent dcVerwendenAb = null;
    private AKJDateComponent dcVErwendenBis = null;

    private LeistungInLeistungsbuendelView lbVObject = null;
    private Leistungsbuendel lbObject = null;
    private OE oeObject = null;

    /**
     * Default-Const.
     */
    public DnLeistungBuendelKonfPanel() {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistungBuendelKonfPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblSel = getSwingFactory().createLabel("lbl.sel");
        AKJLabel lblVerf = getSwingFactory().createLabel("lbl.verf");

        dcVerwendenAb = getSwingFactory().createDateComponent("v.von");
        dcVErwendenBis = getSwingFactory().createDateComponent("v.bis");

        AKJLabel lblVerwendenAb = getSwingFactory().createLabel("v.von");
        AKJLabel lblVerwendenBis = getSwingFactory().createLabel("v.bis");
        AKJLabel lblDnTyp = getSwingFactory().createLabel("dn.typ");
        AKJLabel lblStandard = getSwingFactory().createLabel("standard");

        tbMdlLeistungsbuendel = new LeistungsbuendelTableModell();
        AKJTable tblLeistungsbuendel = new AKJTable(tbMdlLeistungsbuendel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tblLeistungsbuendel.addMouseListener(new AKTableListener(this));
        tblLeistungsbuendel.fitTable(new int[] { 200, 200, 40 });
        AKJScrollPane spLBue = new AKJScrollPane(tblLeistungsbuendel);
        spLBue.setPreferredSize(new Dimension(450, 150));

        tbMdlLeistungen = new LeistungenTableModel();
        tblLeistungen = new AKJTable(tbMdlLeistungen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblLeistungen.addMouseListener(new AKTableListener(this));
        tblLeistungen.fitTable(new int[] { 200, 300, 25 });
        AKJScrollPane spLei = new AKJScrollPane(tblLeistungen);
        spLei.setPreferredSize(new Dimension(300, 350));

        CBItemListener cbItemListener = new CBItemListener();
        AKJButton btnAdd = getSwingFactory().createButton("add", getActionListener());
        AKJButton btnRemove = getSwingFactory().createButton("remove", getActionListener());
        chbStandard = getSwingFactory().createCheckBox("standard");
        chbStandard.addItemListener(cbItemListener);
        lblDefParamVal = getSwingFactory().createLabel("standard.parameter.wert");
        lblDefParamVal.setEnabled(false);
        tfDefParamVal = getSwingFactory().createTextField("standard.parameter.wert");
        tfDefParamVal.setEnabled(false);

        tbMdlLeistungenSelected = new LeistungenSelectedTableModel();
        AKJTable tblLeistungenSelected = new AKJTable(tbMdlLeistungenSelected, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblLeistungenSelected.attachSorter();
        tblLeistungenSelected.addMouseListener(new AKTableListener(this));
        tblLeistungenSelected.fitTable(new int[] { 150, 100, 40, 70, 70, 40, 25 });
        AKJScrollPane spLeiSel = new AKJScrollPane(tblLeistungenSelected);
        spLeiSel.setPreferredSize(new Dimension(460, 350));

        tbMdlOeTyp = new OeTypTableModel();
        AKJTable tblOeTyp = new AKJTable(tbMdlOeTyp, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tblOeTyp.addMouseListener(new AKTableListener(this));
        tblOeTyp.fitTable(new int[] { 30, 100 });
        AKJScrollPane spOeTyp = new AKJScrollPane(tblOeTyp);
        spOeTyp.setPreferredSize(new Dimension(150, 100));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder("Leistungsbuendel"));
        top.add(spLBue, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel bottom = new AKJPanel(new GridBagLayout());
        bottom.add(lblSel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 2, 1, GridBagConstraints.NONE));
        bottom.add(lblVerf, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(spLeiSel, GBCFactory.createGBC(100, 0, 0, 1, 1, 11, GridBagConstraints.HORIZONTAL));
        bottom.add(spLei, GBCFactory.createGBC(100, 0, 3, 1, 1, 11, GridBagConstraints.HORIZONTAL));
        bottom.add(btnAdd, GBCFactory.createGBC(0, 0, 1, 1, 2, 1, GridBagConstraints.NONE));
        bottom.add(btnRemove, GBCFactory.createGBC(0, 0, 1, 2, 2, 1, GridBagConstraints.NONE));
        bottom.add(lblVerwendenAb, GBCFactory.createGBC(0, 0, 1, 3, 2, 1, GridBagConstraints.NONE));
        bottom.add(dcVerwendenAb, GBCFactory.createGBC(0, 0, 1, 4, 2, 1, GridBagConstraints.NONE));
        bottom.add(lblVerwendenBis, GBCFactory.createGBC(0, 0, 1, 5, 2, 1, GridBagConstraints.NONE));
        bottom.add(dcVErwendenBis, GBCFactory.createGBC(0, 0, 1, 6, 2, 1, GridBagConstraints.NONE));
        bottom.add(lblDnTyp, GBCFactory.createGBC(0, 0, 1, 7, 2, 1, GridBagConstraints.NONE));
        bottom.add(spOeTyp, GBCFactory.createGBC(0, 0, 1, 8, 2, 1, GridBagConstraints.NONE));
        bottom.add(lblStandard, GBCFactory.createGBC(0, 0, 1, 9, 1, 1, GridBagConstraints.NONE));
        bottom.add(chbStandard, GBCFactory.createGBC(0, 0, 2, 9, 1, 1, GridBagConstraints.NONE));
        bottom.add(lblDefParamVal, GBCFactory.createGBC(0, 0, 1, 10, 2, 1, GridBagConstraints.NONE));
        bottom.add(tfDefParamVal, GBCFactory.createGBC(0, 0, 1, 11, 2, 1, GridBagConstraints.NONE));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 12, 2, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(top, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(bottom, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnAdd, btnRemove);
    }

    /**
     * @see AKDataLoaderComponent.loadData()
     */
    @Override
    public final void loadData() {
        try {
            CCRufnummernService service = getCCService(CCRufnummernService.class);
            List<Leistungsbuendel> leistungsbuendel = service.findLeistungsbuendel();
            tbMdlLeistungsbuendel.setData(leistungsbuendel);
            dcVerwendenAb.setDate(new Date());
            dcVErwendenBis.setDate(DateTools.getHurricanEndDate());
            OEService oeService = getBillingService(OEService.class.getName(), OEService.class);
            List<OE> oe = oeService.findOEByOeTyp(OE.OE_OETYP_RUFNUMMER, OEService.FIND_STRATEGY_ALL);
            if (oe != null) {
                tbMdlOeTyp.setData(oe);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        try {
            CCRufnummernService service = getCCService(CCRufnummernService.class);

            if ("add".equals(command)) {
                if (oeObject == null) {
                    MessageHelper.showInfoDialog(getMainFrame(), "Es wurde kein Rufnummerntyp gewählt!");
                }
                else {
                    addLb2Leistungen(service);
                }
            }
            else if ("remove".equals(command)) {
                if ((lbVObject != null) && (dcVErwendenBis.getDate(null) != null)) {
                    service.updateLb2Leistung(dcVErwendenBis.getDate(null), lbVObject.getLb2LeistungId());
                    refresh();
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(), "Es wurde keine Ende-Datum eingeben" +
                            " oder keine Zuordnung gewählt");
                }
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void addLb2Leistungen(CCRufnummernService service) {
        try {
            for (int aSelection : tblLeistungen.getSelectedRows()) {
                Object o = ((AKMutableTableModel) tblLeistungen.getModel()).getDataAtRow(aSelection);
                if (o instanceof Leistung4Dn) {
                    Leistung4Dn l4dView = (Leistung4Dn) o;
                    if (dcVerwendenAb.getDate(null) != null) {
                        Lb2Leistung lb2L = new Lb2Leistung();
                        lb2L.setGueltigVon(new Date());
                        lb2L.setGueltigBis(DateTools.getHurricanEndDate());
                        lb2L.setVerwendenVon(dcVerwendenAb.getDate(null));
                        lb2L.setLbId(lbObject.getId());
                        lb2L.setLeistungId(l4dView.getId());
                        lb2L.setOeNo(oeObject.getOeNoOrig());
                        lb2L.setStandard((chbStandard.isSelected()) ? Boolean.TRUE : false);
                        lb2L.setVerwendenBis(DateTools.getHurricanEndDate());
                        lb2L.setDefParamValue(tfDefParamVal.getText());
                        service.saveLb2Leistung(lb2L);
                    }
                }
            }
            refresh();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        // intentionally left blank
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        // intentionally left blank
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        // intentionally left blank
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

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        // intentionally left blank
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof Leistungsbuendel) {
            lbObject = (Leistungsbuendel) details;
            refresh();
        }
        if (details instanceof LeistungInLeistungsbuendelView) {
            lbVObject = (LeistungInLeistungsbuendelView) details;
        }
        if (details instanceof OE) {
            oeObject = (OE) details;
        }
    }

    /**
     * Refresh
     */
    @Override
    protected void refresh() {
        if (lbObject != null) {
            try {
                CCRufnummernService service = getCCService(CCRufnummernService.class);
                List<LeistungInLeistungsbuendelView> lb2L = service.findAllLb2Leistung(lbObject.getId());
                List<Long> verwendeteLB = new ArrayList<>();
                List<LeistungInLeistungsbuendelView> lbList = new ArrayList<>();
                if ((lb2L != null) && (!lb2L.isEmpty())) {

                    for (Iterator<LeistungInLeistungsbuendelView> iter = lb2L.iterator(); iter.hasNext(); ) {
                        LeistungInLeistungsbuendelView value = iter.next();
                        if (DateTools.isAfter(value.getVerwendenBis(), new Date())) {
                            verwendeteLB.add(value.getLeistungId());
                        }

                        LeistungInLeistungsbuendelView view = new LeistungInLeistungsbuendelView();
                        view.setLb2LeistungId(value.getLb2LeistungId());
                        view.setBeschreibung(value.getBeschreibung());
                        view.setGueltigBis(null);
                        view.setGueltigVon(null);
                        view.setLbId(value.getLbId());
                        view.setLeistung(value.getLeistung());
                        view.setLeistungId(value.getLeistungId());
                        view.setOeNo(value.getOeNo());
                        view.setExternSonstigesNo(null);
                        view.setExternLeistungNo(null);
                        view.setStandard(value.getStandard());
                        view.setVerwendenBis(value.getVerwendenBis());
                        view.setVerwendenVon(value.getVerwendenVon());
                        lbList.add(view);
                    }
                }

                List<Leistung4Dn> leistung = service.findLeistungen();
                List<Leistung4Dn> l4dList = new ArrayList<>();
                for (Iterator<Leistung4Dn> iter = leistung.iterator(); iter.hasNext(); ) {
                    Leistung4Dn val = iter.next();
                    Leistung4Dn viewl4Dn = new Leistung4Dn();
                    if ((verwendeteLB != null) && (!verwendeteLB.isEmpty())) {
                        if (!verwendeteLB.contains(val.getId())) {
                            viewl4Dn.setBeschreibung(val.getBeschreibung());
                            viewl4Dn.setLeistung(val.getLeistung());
                            viewl4Dn.setId(val.getId());
                            l4dList.add(viewl4Dn);
                        }
                    }
                    else {
                        viewl4Dn.setBeschreibung(val.getBeschreibung());
                        viewl4Dn.setLeistung(val.getLeistung());
                        viewl4Dn.setId(val.getId());
                        l4dList.add(viewl4Dn);
                    }
                }

                tbMdlLeistungen.setData(l4dList);
                tbMdlLeistungenSelected.setData(lbList);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * TableModel fuer die freien Leistungen .
     */
    static class LeistungenTableModel extends AKTableModel<Leistung4Dn> {
        static final int COL_LEISTUNG = 0;
        static final int COL_BESCHREIBUNG = 1;
        static final int COL_ID = 2;

        static final int COL_COUNT = 3;
        private static final long serialVersionUID = -574405468114137942L;

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
                case COL_ID:
                    return "ID";
                case COL_LEISTUNG:
                    return "Leistung";
                case COL_BESCHREIBUNG:
                    return "Beschreibung";
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
            if (o instanceof Leistung4Dn) {
                Leistung4Dn l4d = (Leistung4Dn) o;
                switch (column) {
                    case COL_ID:
                        return l4d.getId();
                    case COL_LEISTUNG:
                        return l4d.getLeistung();
                    case COL_BESCHREIBUNG:
                        return l4d.getBeschreibung();
                    default:
                        break;
                }
            }
            return null;
        }
    }

    /**
     * Tabellenmodell Beschreibt die moeglichen Leistungsbuendel
     */
    static class LeistungsbuendelTableModell extends AKTableModel<Leistungsbuendel> {
        static final int COL_NAME = 0;
        static final int COL_BESCHREIBUNG = 1;
        static final int COL_ID = 2;

        static final int COL_COUNT = 3;
        private static final long serialVersionUID = -7089818893094079899L;

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
                case COL_ID:
                    return "ID";
                case COL_NAME:
                    return "Name";
                case COL_BESCHREIBUNG:
                    return "Beschreibung";
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
                    case COL_ID:
                        return lb.getId();
                    case COL_NAME:
                        return lb.getName();
                    case COL_BESCHREIBUNG:
                        return lb.getBeschreibung();
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    /**
     * TableModel fuer die Darstellung der Leistungsbuendelzuordnungen.
     */
    static class LeistungenSelectedTableModel extends AKTableModel<LeistungInLeistungsbuendelView> {

        static final int COL_LEISTUNG = 0;
        static final int COL_BESCHREIBUNG = 1;
        static final int COL_OE_TYP = 2;
        static final int COL_V_VON = 3;
        static final int COL_V_BIS = 4;
        static final int COL_STANDARD = 5;
        static final int COL_ID = 6;

        static final int COL_COUNT = 7;
        private static final long serialVersionUID = -6339773223717846484L;

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
                case COL_ID:
                    return "ID";
                case COL_LEISTUNG:
                    return "Leistung";
                case COL_BESCHREIBUNG:
                    return "Beschreibung";
                case COL_OE_TYP:
                    return "DNTyp";
                case COL_V_BIS:
                    return "Verwenden bis";
                case COL_V_VON:
                    return "Verwenden von";
                case COL_STANDARD:
                    return "Def";
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
            if (o instanceof LeistungInLeistungsbuendelView) {
                LeistungInLeistungsbuendelView lbView = (LeistungInLeistungsbuendelView) o;
                switch (column) {
                    case COL_ID:
                        return lbView.getLb2LeistungId();
                    case COL_LEISTUNG:
                        return lbView.getLeistung();
                    case COL_BESCHREIBUNG:
                        return lbView.getBeschreibung();
                    case COL_OE_TYP:
                        return lbView.getOeNo();
                    case COL_V_BIS:
                        return lbView.getVerwendenBis();
                    case COL_V_VON:
                        return lbView.getVerwendenVon();
                    case COL_STANDARD:
                        return lbView.getStandard();
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int col) {
            switch (col) {
                case COL_V_VON:
                    return Date.class;
                case COL_V_BIS:
                    return Date.class;
                case COL_STANDARD:
                    return Boolean.class;
                default:
                    return String.class;
            }
        }
    }

    /**
     * Tabellenmodell zur Darstellung der Rufnummerntypen
     */
    static class OeTypTableModel extends AKTableModel<OE> {
        static final int COL_OE_NO = 0;
        static final int COL_BESCHREIBUNG = 1;

        static final int COL_COUNT = 2;
        private static final long serialVersionUID = -418556833378856543L;

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
                case COL_OE_NO:
                    return "OE__NO";
                case COL_BESCHREIBUNG:
                    return "Typ";
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
            if (o instanceof OE) {
                OE oeView = (OE) o;
                switch (column) {
                    case COL_BESCHREIBUNG:
                        return oeView.getProduktCode();
                    case COL_OE_NO:
                        return oeView.getOeNoOrig();
                    default:
                        break;
                }
            }
            return null;
        }
    }

    class CBItemListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if ((e.getStateChange() == ItemEvent.DESELECTED) && (e.getItem() == chbStandard)) {
                tfDefParamVal.setEnabled(false);
                tfDefParamVal.setText("");
                lblDefParamVal.setEnabled(false);
            }
            if ((e.getStateChange() == ItemEvent.SELECTED) && (e.getItem() == chbStandard)) {
                tfDefParamVal.setEnabled(true);
                lblDefParamVal.setEnabled(true);
            }
        }
    }
}


