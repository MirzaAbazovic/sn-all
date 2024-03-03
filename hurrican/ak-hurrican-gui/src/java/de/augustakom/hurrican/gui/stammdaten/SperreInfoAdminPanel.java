/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2004 11:51:29
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.SperreInfo;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.SperreVerteilungService;


/**
 * Panel fuer die Administration der Sperr-Infos.
 *
 *
 */
public class SperreInfoAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(SperreInfoAdminPanel.class);

    private SperreInfoTableModel tbMdlSperreInfos = null;

    private AKJTextField tfId = null;
    private AKJTextField tfEMail = null;
    private AKJComboBox cbAbteilungen = null;
    private AKJCheckBox chbActive = null;

    private SperreInfo detail = null;

    /* Die Map mit allen verfuegbaren Abteilungen. Key: ID der Abteilung; Value: Objekt vom Typ 'Abteilung'; */
    private Map<Long, Abteilung> abteilungen = null;
    /* Die Map mit allen Niederlassungen: Key: ID der NL; Value: Objekt vom Typ 'Niederlassung'; */
    private Map<Long, Niederlassung> niederlassungen = null;

    /**
     * Konstruktor.
     */
    public SperreInfoAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/SperreInfoAdminPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlSperreInfos = new SperreInfoTableModel();
        AKJTable tbSperreInfos = new AKJTable(tbMdlSperreInfos, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSperreInfos.fitTable(new int[] { 50, 150, 100, 40 });
        tbSperreInfos.addMouseListener(getTableListener());
        tbSperreInfos.addKeyListener(getTableListener());
        AKJScrollPane spTable = new AKJScrollPane(tbSperreInfos);
        spTable.setPreferredSize(new Dimension(400, 150));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.add(spTable, BorderLayout.CENTER);

        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblEMail = getSwingFactory().createLabel("email");
        AKJLabel lblAbteilung = getSwingFactory().createLabel("abteilung");
        AKJLabel lblActive = getSwingFactory().createLabel("active");

        tfId = getSwingFactory().createTextField("id", false);
        tfEMail = getSwingFactory().createTextField("email");
        cbAbteilungen = getSwingFactory().createComboBox("abteilung");
        cbAbteilungen.setRenderer(new AbtNLComboBoxRenderer());
        chbActive = getSwingFactory().createCheckBox("active");

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfId, GBCFactory.createGBC(20, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblEMail, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfEMail, GBCFactory.createGBC(20, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblAbteilung, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbAbteilungen, GBCFactory.createGBC(20, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblActive, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(chbActive, GBCFactory.createGBC(20, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(80, 0, 4, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tablePanel);
        split.setBottomComponent(dataPanel);
        split.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            niederlassungen = new HashMap<Long, Niederlassung>();
            abteilungen = new HashMap<Long, Abteilung>();

            SperreVerteilungService svService = getCCService(SperreVerteilungService.class);
            List<SperreInfo> sis = svService.findSperreInfos(null, null);
            tbMdlSperreInfos.setData(sis);

            // Niederlassungen laden
            NiederlassungService niederlassungService = getCCService(NiederlassungService.class);
            List<Niederlassung> nls = niederlassungService.findNiederlassungen();
            if (nls != null) {
                for (Niederlassung n : nls) {
                    niederlassungen.put(n.getId(), n);
                }
            }

            // Abteilungen laden
            List<Abteilung> abts = niederlassungService.findAbteilungen();
            cbAbteilungen.addItems(abts, true, Abteilung.class);
            if (abts != null) {
                for (Abteilung abt : abts) {
                    abteilungen.put(abt.getId(), abt);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
            stopProgressBar();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof SperreInfo) {
            this.detail = (SperreInfo) details;
            tfId.setText(detail.getId());
            tfEMail.setText(detail.getEmail());
            cbAbteilungen.selectItem("getId", Abteilung.class, detail.getAbteilungId());
            chbActive.setSelected(detail.getActive());
        }
        else {
            this.detail = null;
            clear();
        }
    }

    /* 'Loescht' alle Felder. */
    private void clear() {
        tfId.setText("");
        tfEMail.setText("");
        cbAbteilungen.setSelectedIndex(0);
        chbActive.setSelected(false);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        this.detail = null;
        clear();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            boolean isNew = false;
            if (detail == null) {
                isNew = true;
                detail = new SperreInfo();
            }

            detail.setEmail(tfEMail.getText());
            detail.setAbteilungId((cbAbteilungen.getSelectedItem() instanceof Abteilung)
                    ? ((Abteilung) cbAbteilungen.getSelectedItem()).getId()
                    : null);
            detail.setActive(chbActive.isSelected());

            SperreVerteilungService svService = getCCService(SperreVerteilungService.class);
            svService.saveSperreInfo(detail);

            if (isNew) {
                tbMdlSperreInfos.addObject(detail);
            }
            tbMdlSperreInfos.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
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

    /* TableModel fuer die Sperr-Infos. */
    class SperreInfoTableModel extends AKTableModel {
        static final int COL_ID = 0;
        static final int COL_EMAIL = 1;
        static final int COL_ABTEILUNG = 2;
        static final int COL_ACTIVE = 3;

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
                case COL_ID:
                    return "ID";
                case COL_EMAIL:
                    return "EMail";
                case COL_ABTEILUNG:
                    return "Abteilung";
                case COL_ACTIVE:
                    return "aktiv";
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
            if (o instanceof SperreInfo) {
                SperreInfo si = (SperreInfo) o;
                switch (column) {
                    case COL_ID:
                        return si.getId();
                    case COL_EMAIL:
                        return si.getEmail();
                    case COL_ABTEILUNG:
                        StringBuilder abt = new StringBuilder();
                        Abteilung abteilung = null;
                        if ((abteilungen != null) && abteilungen.containsKey(si.getAbteilungId())) {
                            abteilung = abteilungen.get(si.getAbteilungId());
                            abt.append(abteilung.getName());
                        }

                        if ((abteilung != null) && (niederlassungen != null) &&
                                niederlassungen.containsKey(abteilung.getNiederlassungId())) {
                            Niederlassung nl = niederlassungen.get(abteilung.getNiederlassungId());
                            abt.append(" (");
                            abt.append(nl.getName());
                            abt.append(")");
                        }
                        return abt.toString();
                    case COL_ACTIVE:
                        return si.getActive();
                    default:
                        return null;
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

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_ID:
                    return Long.class;
                case COL_ACTIVE:
                    return Boolean.class;
                default:
                    return String.class;
            }
        }
    }

    /* Renderer fuer die Abteilungs-ComboBox. */
    class AbtNLComboBoxRenderer extends DefaultListCellRenderer {
        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
         * boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if ((comp instanceof JLabel) && (value instanceof Abteilung)) {
                Abteilung abt = (Abteilung) value;
                StringBuilder text = new StringBuilder();
                if (abt.getName() != null) {
                    text.append(abt.getName());

                    if (niederlassungen.containsKey(abt.getNiederlassungId())) {
                        Niederlassung nl = niederlassungen.get(abt.getNiederlassungId());
                        text.append(" (");
                        text.append(nl.getName());
                        text.append(")");
                    }
                }
                else {
                    text.append(" ");
                }
                ((JLabel) comp).setText(text.toString());
            }

            return comp;
        }
    }
}


