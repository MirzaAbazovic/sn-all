/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2005 13:17:26
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Panel fuer die Konfiguration der techn. Leistungen. <br> Die Konfiguration der techn. Leistungen wird fuer die
 * Bauauftrags-Steuerung verwendet.
 *
 *
 */
public class TechLeistungKonfigPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(TechLeistungKonfigPanel.class);

    private AKReflectionTableModel<TechLeistung> tbMdlLeistungen = null;
    private AKJFormattedTextField tfExtLeistNo = null;
    private AKJFormattedTextField tfExtMiscNo = null;
    private AKJComboBox cbTyp = null;
    private AKJTextField tfName = null;
    private AKJFormattedTextField tfLongValue = null;
    private AKJTextField tfStrValue = null;
    private AKJTextField tfParameter = null;
    private AKJTextField tfBaHinweis = null;
    private AKJTextArea taDescription = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;
    private AKJCheckBox chbSnapshotRel = null;
    private AKJCheckBox chbEwsd = null;
    private AKJCheckBox chbSdh = null;
    private AKJCheckBox chbSct = null;
    private AKJCheckBox chbIps = null;

    // Modelle
    private TechLeistung model = null;

    /**
     * Default-Konstruktor.
     */
    public TechLeistungKonfigPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/TechLeistungKonfigPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTyp = getSwingFactory().createLabel("typ");
        AKJLabel lblExtLeistNo = getSwingFactory().createLabel("extern.leistung.no");
        AKJLabel lblExtMiscNo = getSwingFactory().createLabel("extern.misc.no");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblLongValue = getSwingFactory().createLabel("long.value");
        AKJLabel lblStrValue = getSwingFactory().createLabel("str.value");
        AKJLabel lblParameter = getSwingFactory().createLabel("parameter");
        AKJLabel lblBaHinweis = getSwingFactory().createLabel("baHinweis");
        AKJLabel lblSnapshotRel = getSwingFactory().createLabel("snapshot.rel");
        AKJLabel lblDescription = getSwingFactory().createLabel("desc");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.bis");
        AKJLabel lblBA = getSwingFactory().createLabel("ba.steuerung");

        cbTyp = getSwingFactory().createComboBox("typ", TechLeistung.TYPEN);
        tfExtLeistNo = getSwingFactory().createFormattedTextField("extern.leistung.no");
        tfExtMiscNo = getSwingFactory().createFormattedTextField("extern.misc.no");
        tfName = getSwingFactory().createTextField("name");
        tfLongValue = getSwingFactory().createFormattedTextField("long.value");
        tfStrValue = getSwingFactory().createTextField("str.value");
        tfParameter = getSwingFactory().createTextField("parameter");
        tfBaHinweis = getSwingFactory().createTextField("baHinweis");
        taDescription = getSwingFactory().createTextArea("desc");
        AKJScrollPane spDescription = new AKJScrollPane(taDescription, new Dimension(150, 45));
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        chbSnapshotRel = getSwingFactory().createCheckBox("snapshot.rel");
        chbEwsd = getSwingFactory().createCheckBox("ewsd");
        chbSdh = getSwingFactory().createCheckBox("sdh");
        chbSct = getSwingFactory().createCheckBox("sct");
        chbIps = getSwingFactory().createCheckBox("ips");

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblTyp, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(cbTyp, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblExtLeistNo, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfExtLeistNo, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblExtMiscNo, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfExtMiscNo, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblName, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfName, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLongValue, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfLongValue, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblStrValue, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfStrValue, GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblParameter, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfParameter, GBCFactory.createGBC(0, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBaHinweis, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBaHinweis, GBCFactory.createGBC(0, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSnapshotRel, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbSnapshotRel, GBCFactory.createGBC(0, 0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblDescription, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(spDescription, GBCFactory.createGBC(0, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 0, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcGueltigVon, GBCFactory.createGBC(0, 0, 2, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 0, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcGueltigBis, GBCFactory.createGBC(0, 0, 2, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 12, 1, 1, GridBagConstraints.BOTH));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblBA, GBCFactory.createGBC(100, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbEwsd, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbSdh, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbSct, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbIps, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 5, 1, 1, GridBagConstraints.BOTH));

        tbMdlLeistungen = new AKReflectionTableModel<TechLeistung>(
                new String[] { "Typ", "Name", "Ext. Lst. No", "Ext. Misc No", "Long", "String",
                        "Gültig von", "Gültig bis", "EWSD", "SDH", "SCT", "IPS" },
                new String[] { "typ", "name", "externLeistungNo", "externMiscNo", "longValue", "strValue",
                        "gueltigVon", "gueltigBis", "ewsd", "sdh", "sct", "ips" },
                new Class[] { String.class, String.class, Long.class, Long.class, Long.class, String.class,
                        Date.class, Date.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class }
        );
        AKJTable tbLeistungen = new AKJTable(tbMdlLeistungen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLeistungen.attachSorter();
        tbLeistungen.fitTable(new int[] { 100, 200, 65, 65, 50, 50, 70, 70, 50, 50, 50, 50 });
        tbLeistungen.addMouseListener(getTableListener());
        tbLeistungen.addKeyListener(getTableListener());
        AKJScrollPane spLeistungen = new AKJScrollPane(tbLeistungen, new Dimension(650, 250));

        this.setLayout(new GridBagLayout());
        this.add(spLeistungen, GBCFactory.createGBC(100, 0, 0, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        this.add(left, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.VERTICAL, new Insets(2, 8, 2, 2)));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        this.add(right, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            CCLeistungsService ls = getCCService(CCLeistungsService.class);
            List<TechLeistung> leistungen = ls.findTechLeistungen(true);

            tbMdlLeistungen.setData(leistungen);
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
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        clear();
        if (details instanceof TechLeistung) {
            this.model = (TechLeistung) details;

            cbTyp.selectItem("toString", String.class, model.getTyp());
            tfExtLeistNo.setValue(model.getExternLeistungNo());
            tfExtMiscNo.setValue(model.getExternMiscNo());
            tfName.setText(model.getName());
            tfLongValue.setValue(model.getLongValue());
            tfStrValue.setText(model.getStrValue());
            tfParameter.setText(model.getParameter());
            tfBaHinweis.setText(model.getBaHinweis());
            chbSnapshotRel.setSelected(model.getSnapshotRel());
            taDescription.setText(model.getDescription());
            dcGueltigVon.setDate(model.getGueltigVon());
            dcGueltigBis.setDate(model.getGueltigBis());
            chbEwsd.setSelected(model.getEwsd());
            chbSdh.setSelected(model.getSdh());
            chbSct.setSelected(model.getSct());
            chbIps.setSelected(model.getIps());
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        clear();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            boolean isNew = false;
            if (model == null) {
                isNew = true;
                model = new TechLeistung();
            }

            model.setTyp(cbTyp.getSelectedItem().toString());
            model.setExternLeistungNo(tfExtLeistNo.getValueAsLong(null));
            model.setExternMiscNo(tfExtMiscNo.getValueAsLong(null));
            model.setName(tfName.getText());
            model.setLongValue(tfLongValue.getValueAsLong(null));
            model.setStrValue(tfStrValue.getText(null));
            model.setParameter(tfParameter.getText(null));
            model.setBaHinweis(tfBaHinweis.getText(null));
            model.setSnapshotRel(chbSnapshotRel.isSelectedBoolean());
            model.setDescription(taDescription.getText());
            model.setGueltigVon(dcGueltigVon.getDate(null));
            model.setGueltigBis(dcGueltigBis.getDate(null));
            model.setEwsd(chbEwsd.isSelectedBoolean());
            model.setSdh(chbSdh.isSelectedBoolean());
            model.setSct(chbSct.isSelectedBoolean());
            model.setIps(chbIps.isSelectedBoolean());

            CCLeistungsService ls = getCCService(CCLeistungsService.class);
            ls.saveLeistungKonfig(model);

            if (isNew) {
                tbMdlLeistungen.addObject(model);
            }
            tbMdlLeistungen.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            this.model = null;
        }
    }

    /* 'Loescht' alle Felder. */
    private void clear() {
        this.model = null;
        GuiTools.cleanFields(this);
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

}


