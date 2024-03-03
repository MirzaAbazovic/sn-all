/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2006 15:56:54
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Dialog zum Anlegen von Leistungen zu einer/aller dem Auftrag zugeordneten Rufnummer
 *
 *
 */
public class DnLeistung2AuftragDialog extends AbstractServiceOptionDialog implements
        AKTableOwner, ListSelectionListener, AKDataLoaderComponent {
    private static final Logger LOGGER = Logger.getLogger(DnLeistung2AuftragDialog.class);
    private static final long serialVersionUID = 1426697378632679269L;

    private Produkt produkt = null;
    private Rufnummer rufnummer = null;
    private AuftragDaten auftragDaten = null;
    private List<Rufnummer> rufnummern = null;
    private Leistung4Dn l4d = null;

    //GUI
    private AKJTextField tfRufnummer = null;
    private AKJTextField tfProdukt = null;
    private AKJTextField tfSwitchKennung = null;
    private LeistungTableModel tbMdlLeistung = null;
    private List<Leistung4Dn> leistungen = null;
    private AKJList lsParameter = null;
    private DefaultListModel lsMdlParameter = null;
    private AKJTextField tfParameter = null;
    private AKJDateComponent dcRealAm = null;
    private AKJCheckBox chbStandLeistung = null;

    private DnLeistungSperrklassePanel sperrklassePanel;

    private HWSwitch hwSwitch;

    private boolean defaultLeistungen = false;

    /**
     * Konstruktor.
     *
     * @param rufnummer
     * @param produkt
     * @param auftragDaten
     */
    public DnLeistung2AuftragDialog(Rufnummer rufnummer, Produkt produkt, AuftragDaten auftragDaten) {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistung2AuftragDialog.xml");
        this.rufnummer = rufnummer;
        this.produkt = produkt;
        this.auftragDaten = auftragDaten;
        try {
            this.hwSwitch = getHwSwitch(auftragDaten.getAuftragId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    @SuppressWarnings("squid:S1186")
    protected void doSave() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        configureButton(CMD_SAVE, null, null, false, false);
        configureButton(CMD_CANCEL, "schliessen", "Schliesst das Formular", true, true);
        String title = "Leistungen zu einer Rufnummer anlegen";
        setTitle(title);

        AKJLabel lblRufnummer = getSwingFactory().createLabel("rufnummer");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblSwitchKennung = getSwingFactory().createLabel("switchKennung");

        AKJLabel lblRealAm = getSwingFactory().createLabel("real.am");
        AKJLabel lblStandLeistung = getSwingFactory().createLabel("chb.standard");

        tfRufnummer = getSwingFactory().createTextField("rufnummer");
        tfRufnummer.setEditable(false);
        tfProdukt = getSwingFactory().createTextField("produkt");
        tfProdukt.setEditable(false);
        tfSwitchKennung = getSwingFactory().createTextField("switchKennung");
        tfSwitchKennung.setEditable(false);

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblRufnummer, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfRufnummer, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblProdukt, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfProdukt, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblSwitchKennung, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfSwitchKennung, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblLeistungen = new AKJLabel("Leistungen");
        AKJLabel lblParameter = new AKJLabel("Parameter");
        lsMdlParameter = new DefaultListModel();
        lsParameter = getSwingFactory().createList("ls.parameter", lsMdlParameter);
        lsParameter.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = -1166396160404953395L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LeistungParameter) {
                    label.setText(((LeistungParameter) value).getBezeichnung(getHwSwitchType()));
                }
                return label;
            }
        });
        lsParameter.addListSelectionListener(this);
        lsParameter.setEnabled(false);
        AKJScrollPane spParameter = new AKJScrollPane(lsParameter);
        spParameter.setPreferredSize(new Dimension(400, 130));
        tfParameter = getSwingFactory().createTextField("tf.parameter");
        tfParameter.setEnabled(false);

        tbMdlLeistung = new LeistungTableModel();
        AKJTable tbLeistungen = new AKJTable(tbMdlLeistung, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLeistungen.attachSorter();
        tbLeistungen.addTableListener(this);
        tbLeistungen.fitTable(new int[] { 20, 200, 320 });
        AKJScrollPane spLp = new AKJScrollPane(tbLeistungen);
        spLp.setPreferredSize(new Dimension(550, 400));

        chbStandLeistung = getSwingFactory().createCheckBox("chb.standard", getActionListener(), true);
        dcRealAm = getSwingFactory().createDateComponent("real.am");

        sperrklassePanel = new DnLeistungSperrklassePanel(null, getHwSwitchType(),
                sperrklasse -> {
                    if (sperrklasse != null) {
                        tfParameter.setText(sperrklasse.getSperrklasseByHwSwitchType(getHwSwitchType()));
                    }
                    else {
                        tfParameter.setText("");
                    }
                });
        AKJPanel center = new AKJPanel(new GridBagLayout());
        // @formatter:off
        center.add(lblLeistungen,    GBCFactory.createGBC(  0, 0, 0,  0, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(spLp,             GBCFactory.createGBC(  0, 0, 0,  1, 5, 8, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(),   GBCFactory.createGBC(  0, 0, 5,  0, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(lblParameter,     GBCFactory.createGBC(  0, 0, 6,  0, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(spParameter,      GBCFactory.createGBC(  0, 0, 6,  1, 5, 1, GridBagConstraints.HORIZONTAL));
        center.add(tfParameter,      GBCFactory.createGBC(  0, 0, 6,  2, 5, 1, GridBagConstraints.HORIZONTAL));
        center.add(sperrklassePanel, GBCFactory.createGBC(  0, 0, 6,  3, 5, 5, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(),   GBCFactory.createGBC(100, 0, 11, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(lblRealAm,        GBCFactory.createGBC(  0, 0, 6,  8, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(dcRealAm,         GBCFactory.createGBC(  0, 0, 7,  8, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(),   GBCFactory.createGBC( 40, 0, 8,  8, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(lblStandLeistung, GBCFactory.createGBC(  0, 0, 9,  8, 1, 1, GridBagConstraints.NONE));
        center.add(chbStandLeistung, GBCFactory.createGBC( 60, 0, 10, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(),   GBCFactory.createGBC(100, 0, 11, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        AKJButton btnAllDn = getSwingFactory().createButton("all.dn", getActionListener());
        AKJButton btnSingleDn = getSwingFactory().createButton("single.dn", getActionListener());

        AKJPanel bottom = new AKJPanel(new GridBagLayout());
        bottom.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(btnSingleDn, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(btnAllDn, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(top, BorderLayout.NORTH);
        getChildPanel().add(center, BorderLayout.CENTER);
        getChildPanel().add(bottom, BorderLayout.SOUTH);

        manageGUI(new AKManageableComponent[] { btnAllDn, btnSingleDn });
    }

    private HWSwitch getHwSwitch(Long auftragId) throws ServiceNotFoundException {
        if (auftragId == null) {
            throw new IllegalArgumentException(
                    "Fuer die Erkennung eines Switches ist eine korrekte AuftragId notwendig!");
        }
        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        return auftragService.getSwitchKennung4Auftrag(auftragId);
    }

    private HWSwitchType getHwSwitchType() {
        return hwSwitch != null ? hwSwitch.getType(): null;
    }

    private String createSwitchKennnungText() {
        String result = "";
        if (hwSwitch != null) {
            result = String.format("%s (%s)", hwSwitch.getName(), hwSwitch.getType());
        }
        return result;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        tfRufnummer.setText(rufnummer.getOnKz() + " " + rufnummer.getDnBase());
        tfProdukt.setText(produkt.getAnschlussart());
        try {
            if (auftragDaten.getAuftragNoOrig() != null) {
                tfSwitchKennung.setText(createSwitchKennnungText());
                CCRufnummernService dnS = getCCService(CCRufnummernService.class);
                Leistungsbuendel lb = dnS.findLeistungsbuendel4Auftrag(auftragDaten.getAuftragId());
                if (lb == null) {
                    throw new HurricanGUIException("Das DN-Leistungsbuendel konnte nicht ermittelt werden!");
                }

                RufnummerService rs = getBillingService(RufnummerService.class.getName(), RufnummerService.class);
                List<Rufnummer> rufnummern = rs.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                        new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.FALSE });
                if (rufnummern != null) {
                    defaultLeistungen = true;
                    chbStandLeistung.setSelected(defaultLeistungen);
                    for (Rufnummer rn : rufnummern) {
                        if (rn.isDefaultDN() && !BooleanTools.nullToFalse(dnS.hasLeistung(rn))) {
                            MessageHelper.showMessageDialog(getMainFrame(),
                                    getSwingFactory().getText("dn.leistungen.fehlen"), "Leistungen fehlen",
                                    JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(), "Keine Rufnummer gefunden");
                }

                leistungen = dnS.findDNLeistungen4Buendel(lb.getId());
            }
            else {
                throw new HurricanGUIException("Auftrag besitzt keine Billing-Auftragsnummer!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        if ("all.dn".equals(command)) {
            try {
                if (defaultLeistungen || (BooleanTools.nullToFalse(validate4Complete())
                        && (auftragDaten.getAuftragNoOrig() != null))) {
                    RufnummerService rS = getBillingService(RufnummerService.class);
                    if ((rufnummern != null) && !rufnummern.isEmpty()) {
                        rufnummern.clear();
                    }
                    rufnummern = rS.findRNs4Auftrag(auftragDaten.getAuftragNoOrig());
                    doSave(rufnummern);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        else if ("single.dn".equals(command)) {
            try {
                if ((rufnummern != null) && !rufnummern.isEmpty()) {
                    rufnummern.clear();
                }
                else {
                    rufnummern = new ArrayList<>();
                }

                if (defaultLeistungen || BooleanTools.nullToFalse(validate4Complete())) {
                    rufnummern.add(rufnummer);
                    doSave(rufnummern);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        else if ("chb.standard".equals(command)) {
            if (chbStandLeistung.isSelected()) {
                defaultLeistungen = true;
                tbMdlLeistung.setData(null);
            }
            else {
                defaultLeistungen = false;
                tbMdlLeistung.setData(leistungen);
            }
        }
    }

    /* Speichert die Leistungszuordnung zu den Rufnummern. */
    private void doSave(List<Rufnummer> rufnummern) {
        Long vLpId = null;
        Long lId = null;
        if ((lsParameter != null) && !lsParameter.isSelectionEmpty()) {
            LeistungParameter lp = (LeistungParameter) lsParameter.getSelectedValue();
            vLpId = lp.getId();
        }

        if (l4d != null) {
            lId = l4d.getId();
        }

        try {
            AKWarnings warnings = new AKWarnings();
            for (Rufnummer rn : rufnummern) {
                try {
                    CCRufnummernService ccS = getCCService(CCRufnummernService.class);
                    Leistungsbuendel lb = ccS.findLeistungsbuendel4Auftrag(auftragDaten.getAuftragId());

                    ccS.saveLeistung2DN(rn, lId, tfParameter.getText(null), defaultLeistungen,
                            lb.getId(), dcRealAm.getDate(null), vLpId,
                            HurricanSystemRegistry.instance().getSessionId());
                }
                catch (Exception e) {
                    warnings.addAKWarning(new AKWarning(null, String.format("%s: %s", rn.getRufnummer(), e.getMessage())));
                }
            }

            String swingTextId = (rufnummern != null && !rufnummern.isEmpty())? "dn.leistung.angelegt":
                    "keine.dn.leistung.angelegt";
            MessageHelper.showInfoDialog(getMainFrame(),
                    String.format("%s\n\nFolgende Warnungen sind aufgetreten:\n%s", getSwingFactory().getText(swingTextId),
                            (warnings.isNotEmpty())? warnings.getWarningsAsText(): ""),
                    "Leistung Anlage", JOptionPane.PLAIN_MESSAGE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    @SuppressWarnings("squid:S1186")
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        LOGGER.debug("showDetails");
        if (details instanceof Leistung4Dn) {
            defaultLeistungen = false;
            tfParameter.setText("");
            l4d = (Leistung4Dn) details;

            boolean enabled = false;
            if (NumberTools.equal(l4d.getId(), Leistung4Dn.SPERRKLASSE_ID)) {
                enabled = true;
            }
            sperrklassePanel.enableSperrklassen(enabled);

            try {
                CCRufnummernService rfs = getCCService(CCRufnummernService.class);
                List<LeistungParameter> par = rfs.findSignedParameter2Leistung(l4d.getId());
                lsMdlParameter.removeAllElements();
                if (!par.isEmpty()) {
                    tfParameter.setEnabled(false);
                    if (!NumberTools.equal(l4d.getId(), Leistung4Dn.SPERRKLASSE_ID)) {
                        lsParameter.setEnabled(true);
                    }
                    else {
                        lsParameter.setEnabled(false);
                    }
                    lsParameter.copyList2Model(par, lsMdlParameter);
                }
                else {
                    lsParameter.setEnabled(false);
                    tfParameter.setEnabled(false);
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        Object[] selection = lsParameter.getSelectedValues();
        if ((selection != null) && (selection.length == 1)) {
            LeistungParameter lp = (LeistungParameter) lsParameter.getSelectedValue();
            HWSwitchType hwSwitchType = getHwSwitchType();
            Integer leistungParameterMehrfach = lp.getLeistungParameterMehrfach(hwSwitchType);
            if ((leistungParameterMehrfach != null) && (leistungParameterMehrfach > 1)) {
                tfParameter.setEnabled(false);
                final AKJOptionDialog dlg;
                if (HWSwitchType.isImsOrNsp(hwSwitchType)
                        && lp.getLeistungParameterTyp().equals(LeistungParameter.PARAMETER_TYP_RUFNUMMER)) {
                    dlg = new DnRufnummern2LeistungDialog(leistungParameterMehrfach, tfParameter.getText());
                }
                else {
                    dlg = new DnParameter2LeistungDialog(lp.getLeistungParameterTyp(), leistungParameterMehrfach);
                }
                Object resultDlg = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if ((resultDlg instanceof String) && StringUtils.isNotBlank((String) resultDlg)) {
                    tfParameter.setText((String) resultDlg);
                }
                lsParameter.removeSelectionInterval(lsParameter.getSelectedIndex(), lsParameter.getSelectedIndex());
            }
            else {
                tfParameter.setEnabled(true);
            }
        }
    }

    /*
     * Ueberprüfung ob Leistungsposition vollstaendig und logisch richtig
     */
    private Boolean validate4Complete() {
        Boolean retVal = true;
        if (!defaultLeistungen && lsParameter.isEnabled() && StringUtils.equals(tfParameter.getText(null), "")) {
            MessageHelper.showInfoDialog(this, "Keine Standardleistung oder Parameter leer",
                    "Standardleistungen können nicht angelegt werden !!", null, true);
            retVal = false;
        }
        else if (!defaultLeistungen && l4d == null) {
            MessageHelper.showInfoDialog(this, "Es ist keine Leistung ausgewählt. Bitte eine Leistung auswählen.",
                    "Keine Leistung ausgewählt", null, true);
            retVal = false;
        }
        else if (!defaultLeistungen && NumberTools.equal(l4d.getId(), Leistung4Dn.SPERRKLASSE_ID) && StringUtils.isBlank(tfParameter.getText(null))) {
            MessageHelper.showInfoDialog(this, "Gewählte Sperrklasse ist nicht verfügbar.",
                    "Standardleistungen können nicht angelegt werden !!", null, true);
            retVal = false;
        }

        Date realDate = dcRealAm.getDate(null);
        if ((realDate == null) || DateTools.isDateBefore(realDate, new Date())) {
            MessageHelper.showInfoDialog(this, "Das Datum ist leer oder ungültig. Bitte beachten: Das Datum muss zu heute "
                            + "oder in der Zukunft gesetzt sein.", "Rufnummernleistungen können nicht angelegt werden !!",
                    null, true);
            retVal = false;
        }
        return retVal;
    }

    /* Modell fuer  Tabelle nach Leistung4DN */
    static class LeistungTableModel extends AKTableModel<Leistung4Dn> {
        static final int COL_ID = 0;
        static final int COL_LEISTUNG = 1;
        static final int COL_BESCHREIBUNG = 2;

        static final int COL_COUNT = 3;
        private static final long serialVersionUID = 830260380586963191L;

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
                    return " ";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Leistung4Dn l4d = getDataAtRow(row);
            if (l4d != null) {
                switch (column) {
                    case COL_ID:
                        return l4d.getId();
                    case COL_LEISTUNG:
                        return l4d.getLeistung();
                    case COL_BESCHREIBUNG:
                        return l4d.getBeschreibung();
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
    }

}
