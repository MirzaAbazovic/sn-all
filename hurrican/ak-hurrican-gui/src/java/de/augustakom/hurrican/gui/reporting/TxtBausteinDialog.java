/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2007 08:40:19
 */
package de.augustakom.hurrican.gui.reporting;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.reporting.ReportData;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Dialog, um Text-Bausteine für einen Report auszuwählen.
 *
 *
 */
public class TxtBausteinDialog extends AbstractServiceOptionDialog implements AKTableOwner, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(TxtBausteinDialog.class);
    private static final long serialVersionUID = 1957262169695119769L;

    private AKJTable tbBausteine = null;
    private AKReflectionTableModel<TxtBaustein> tbModelBausteine = null;

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextArea taText = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;

    private TxtBaustein detail = null;

    private ReportRequest request = null;
    private TxtBausteinGruppe txtGruppe = null;
    private StringBuilder mandatoryString = null;

    /**
     * Konstruktor mit Angabe des Report-Requests und der aktuellen Baustein-Gruppe. Die restlichen Daten werden von dem
     * Dialog selbst geladen.
     */
    public TxtBausteinDialog(ReportRequest request, TxtBausteinGruppe txtGruppe) {
        super("de/augustakom/hurrican/gui/reporting/resources/TxtBausteinDialog.xml");
        this.txtGruppe = txtGruppe;
        this.request = request;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIconURL("de/augustakom/hurrican/gui/images/printer.gif");

        configureButton(CMD_SAVE, "Ok", null, true, true);
        configureButton(CMD_CANCEL, "Abbrechen", null, false, true);

        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblText = getSwingFactory().createLabel("text");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.bis");

        tfId = getSwingFactory().createFormattedTextField("id");
        tfName = getSwingFactory().createTextField("name");
        taText = getSwingFactory().createTextArea("text");
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        AKJScrollPane spText = new AKJScrollPane(taText, new Dimension(100, 100));

        tbModelBausteine = new AKReflectionTableModel<>(
                new String[] { "ID", "Name", "Text", "Gültig von", "Gültig bis", "Editierbar" },
                new String[] { "id", "name", "text", "gueltigVon", "gueltigBis", "editable" },
                new Class[] { Long.class, String.class, String.class, Date.class, Date.class, Boolean.class });
        tbBausteine = new AKJTable(tbModelBausteine, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbBausteine.attachSorter();
        tbBausteine.addTableListener(this);
        tbBausteine.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbBausteine.fitTable(new int[] { 50, 250, 300, 70, 70, 70 });
        AKJScrollPane spBausteine = new AKJScrollPane(tbBausteine, new Dimension(800, 100));

        AKJPanel table = new AKJPanel(new GridBagLayout());
        table.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assigned.templates")));
        table.add(spBausteine, GBCFactory.createGBC(100, 0, 0, 1, 1, 3, GridBagConstraints.HORIZONTAL));
        table.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfId, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblName, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfName, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(dcGueltigVon, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(dcGueltigBis, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblText, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(spText, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 50, 6, 7, 1, 1, GridBagConstraints.BOTH));

        AKJPanel tempPanel = new AKJPanel(new GridBagLayout());
        tempPanel.add(table, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        tempPanel.add(dataPanel, GBCFactory.createGBC(100, 100, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        setGUIEnable(false);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tempPanel, BorderLayout.CENTER);
    }

    /*
     * Setzt GUI-Elemente editierbar
     */
    private void setGUIEnable(boolean bool) {
        tfId.setEditable(bool);
        tfName.setEnabled(bool);
        taText.setEnabled(bool);
        dcGueltigVon.setEnabled(bool);
        dcGueltigBis.setEnabled(bool);
    }

    /*
     * Funktion lädt TxtBausteine
     */
    private void read() {
        try {
            setWaitCursor();

            List<TxtBaustein> list = new ArrayList<>();
            mandatoryString = new StringBuilder();
            ReportService service = getReportService(ReportService.class);

            // Ermittle TxtBausteine für Report und Key
            List<TxtBaustein> allTxts = service.findTxtBausteine4Gruppe(txtGruppe.getId());

            // Text-Bausteine mit dem Flag mandatory werden nicht angezeigt, sondern zwischengespeichert
            if (CollectionTools.isNotEmpty(allTxts)) {
                for (TxtBaustein baustein : allTxts) {
                    if (baustein.getMandatory()) {
                        mandatoryString.append(baustein.getText()).append("\n");
                    }
                    else {
                        list.add(baustein);
                    }
                }
            }

            tbModelBausteine.setData(list);
            tbModelBausteine.fireTableDataChanged();

            // Falls keine TxtBausteine vorhanden sind, rufe Funktion doSave() auf und beende Dialog
            if (list.isEmpty()) {
                doSave();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
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
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        // Ermittle Selektion
        int[] selection = tbBausteine.getSelectedRows();

        // Falls kein Text-Baustein ausgewählt wurde, aber die Gruppe als Mandatory
        // gekennzeichnet ist, gebe Meldung aus
        if (txtGruppe.getMandatory() && (selection.length == 0)
                && CollectionTools.isNotEmpty(tbModelBausteine.getData())) {
            MessageHelper.showInfoDialog(this, "Für diese Gruppe von Text-Bausteinen " +
                    "muss mindestens ein Text-Baustein ausgewählt werden.", null, true);
        }
        else {
            // Falls Text-Baustein editierbar, lese Text-Area aus
            if ((detail != null) && detail.getEditable() && !StringUtils.equals(detail.getText(), taText.getText())) {
                detail.setText(taText.getText());
            }

            StringBuilder str = new StringBuilder();
            // Falls Mandatory-Text-Bausteine vorhanden sind, füge diese hinzu
            if ((mandatoryString != null) && StringUtils.isNotBlank(mandatoryString.toString())) {
                str.append(mandatoryString.toString());
            }

            // Für jeden ausgewählten Text-Baustein, füge Text zusammen.
            AKMutableTableModel tbMdlTxt = (AKMutableTableModel) tbBausteine.getModel();
            for (int sel : selection) {
                if (sel != -1) {
                    TxtBaustein txt = (TxtBaustein) tbMdlTxt.getDataAtRow(sel);
                    if ((txt != null) && StringUtils.isNotBlank(txt.getText())) {
                        str.append(txt.getText()).append("\n");
                    }
                }
            }

            // TextBausteine werden in Tabelle Report_Data gespeichert.
            try {
                setWaitCursor();

                ReportService service = getReportService(ReportService.class);

                ReportData data = new ReportData();
                data.setRequestId(request.getId());
                data.setKeyName(txtGruppe.getName());
                data.setKeyValue(str.toString().trim());

                service.saveReportData(data);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }

            // Schließe Dialog
            closeCurrentDialog(AKJOptionDialog.OK_OPTION);
        }
    }

    /* Funktion beendet Frame */
    private void closeCurrentDialog(int status) {
        prepare4Close();
        setValue(status);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        doSave();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof TxtBaustein) {
            // Schreibe vorheriges Objekt in TableModel zurück
            if ((detail != null) && detail.getEditable() && !StringUtils.equals(detail.getText(), taText.getText())) {
                int[] selection = tbBausteine.getSelectedRows();
                detail.setText(taText.getText());
                tbModelBausteine.fireTableDataChanged();
                for (int i : selection) {
                    tbBausteine.addRowSelectionInterval(i, i);
                }
            }

            // Zeige aktuelle Selektion an
            this.detail = (TxtBaustein) details;

            if (detail != null) {
                tfId.setValue(detail.getId());
                tfName.setText(detail.getName());
                taText.setText(detail.getText());
                dcGueltigVon.setDate(detail.getGueltigVon());
                dcGueltigBis.setDate(detail.getGueltigBis());

                // TextArea aktivieren
                taText.setEnabled(detail.getEditable());
            }
        }
    }

}


