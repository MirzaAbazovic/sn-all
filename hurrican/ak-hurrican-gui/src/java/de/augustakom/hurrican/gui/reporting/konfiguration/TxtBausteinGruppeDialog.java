/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 11:40:19
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Dialog, um die Text-Baustein-Gruppen denen ein bestimmter Text-Baustein zugeordnet ist.
 *
 *
 */
public class TxtBausteinGruppeDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(TxtBausteinGruppeDialog.class);

    private AKReflectionTableModel<TxtBausteinGruppe> tbModelGruppen = null;

    private TxtBaustein baustein = null;

    /**
     * Konstruktor mit Angabe der Command-Klasse Die restlichen Daten werden von dem Dialog selbst geladen.
     */
    public TxtBausteinGruppeDialog(TxtBaustein baustein) {
        super(null);
        this.baustein = baustein;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Text-Bausteingruppen für Text-Baustein " + baustein.getName());
        setIconURL("de/augustakom/hurrican/gui/images/printer.gif");

        configureButton(CMD_SAVE, "Ok", null, true, true);
        configureButton(CMD_CANCEL, "Abbrechen", null, false, true);

        // Table für Bausteingruppen
        tbModelGruppen = new AKReflectionTableModel<TxtBausteinGruppe>(
                new String[] { "ID", "Name", "Beschreibung", "Mandatory" },
                new String[] { "id", "name", "description", "mandatory" },
                new Class[] { Long.class, String.class, String.class, Boolean.class });

        AKJTable tbGruppen = new AKJTable(tbModelGruppen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbGruppen.attachSorter();
        tbGruppen.fitTable(new int[] { 50, 200, 300, 50 });
        AKJScrollPane tableSP = new AKJScrollPane(tbGruppen, new Dimension(900, 300));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tableSP, BorderLayout.CENTER);
    }

    /* Laedt die Liste der verfügbaren Reports*/
    private void read() {
        if (baustein != null) {
            try {
                setWaitCursor();

                ReportConfigService rs = getReportService(ReportConfigService.class);
                List<TxtBausteinGruppe> gruppen = rs.findTxtBausteinGruppen4TxtBaustein(baustein.getIdOrig());

                if (CollectionTools.isNotEmpty(gruppen)) {
                    tbModelGruppen.setData(gruppen);
                    tbModelGruppen.fireTableDataChanged();
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Dieser Text-Baustein ist keiner Gruppe zugeordnet.", null, true);
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
        // Schließe Dialog
        prepare4Close();
        setValue(AKJOptionDialog.OK_OPTION);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        doSave();
    }


}


