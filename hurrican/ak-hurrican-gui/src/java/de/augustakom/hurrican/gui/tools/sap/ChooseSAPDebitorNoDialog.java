/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2007 10:16:19
 */
package de.augustakom.hurrican.gui.tools.sap;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Dialog für die Auswahl einer SAP-Debitoren-Nummer, falls für einen Kunden mehrere RInfos vorhanden sind
 *
 *
 */
public class ChooseSAPDebitorNoDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(ChooseSAPDebitorNoDialog.class);

    private AKJTable tbSAPId = null;
    private AKReflectionTableModel<RInfoAdresseView> tbModelSAPId = null;

    private Long kundeNoOrig = null;

    /**
     * Konstruktor mit Angabe der Kundennummer. Die restlichen Daten werden von dem Dialog selbst geladen.
     */
    public ChooseSAPDebitorNoDialog(Long kundeNoOrig) {
        super(null);
        this.kundeNoOrig = kundeNoOrig;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Mehrere SAP-Debitorennummern vorhanden");
        setIconURL("de/augustakom/hurrican/gui/images/calculator.gif");

        configureButton(CMD_SAVE, "Ok", null, true, true);
        configureButton(CMD_CANCEL, "Abbrechen", null, true, true);

        // Table für Reports
        tbModelSAPId = new AKReflectionTableModel<RInfoAdresseView>(
                new String[] { "SAP-Debitorennummer", "Name", "Vorname", "Strasse", "Nummer", "Plz", "Ort" },
                new String[] { "extDebitorNo", "name", "vorname", "strasse", "nummer", "plz", "ort" },
                new Class[] { String.class, String.class, String.class, String.class,
                        String.class, String.class, String.class }
        );

        tbSAPId = new AKJTable(tbModelSAPId, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSAPId.attachSorter();
        tbSAPId.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbSAPId.fitTable(new int[] { 100, 150, 100, 100, 50, 70, 150 });
        AKJScrollPane tableSP = new AKJScrollPane(tbSAPId, new Dimension(750, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tableSP, BorderLayout.CENTER);
    }

    /* Laedt die Liste der verfügbaren Debitorennummern*/
    private void read() {
        try {
            setWaitCursor();

            if (kundeNoOrig != null) {
                // Ermittle R-Infos zu Kunden
                RechnungsService reService = getBillingService(RechnungsService.class);
                RInfoQuery query = new RInfoQuery();
                query.setKundeNo(kundeNoOrig);
                List<RInfoAdresseView> list = reService.findKundeByRInfoQuery(query);
                if (CollectionTools.isEmpty(list)) {
                    MessageHelper.showMessageDialog(HurricanSystemRegistry.instance().getMainFrame(),
                            "Konnte keine R-Info ermitteln!", "Abbruch", JOptionPane.WARNING_MESSAGE);
                }

                // Falls nur eine R-Info, zeige direkt SAP-Daten
                else if (list.size() == 1) {
                    String debNo = (list.get(0) != null) ? list.get(0).getExtDebitorNo() : null;
                    if (StringUtils.isNotBlank(debNo)) {
                        SAPDatenFrame.showSAPDaten(debNo);
                    }
                    closeCurrentDialog(JOptionPane.OK_OPTION);
                }
                else {
                    tbModelSAPId.setData(list);
                    tbModelSAPId.fireTableDataChanged();
                }
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
        Integer selection = tbSAPId.getSelectedRow();
        RInfoAdresseView view = null;

        if ((selection != null) && (selection != -1)) {
            AKMutableTableModel tbMdlRep = (AKMutableTableModel) tbSAPId.getModel();
            view = (RInfoAdresseView) tbMdlRep.getDataAtRow(selection);

            // Öffne Frame mit SAP-Daten
            if ((view != null) && StringUtils.isNotBlank(view.getExtDebitorNo())) {
                SAPDatenFrame.showSAPDaten(view.getExtDebitorNo());
            }
            // Schließe Dialog
            closeCurrentDialog(AKJOptionDialog.OK_OPTION);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Nichts ausgewählt!", null, true);
        }
    }

    /* Funktion beendet Frame */
    private void closeCurrentDialog(int status) {
        prepare4Close();
        setValue(Integer.valueOf(status));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (getButton(CMD_SAVE).isEnabled()) {
            doSave();
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Sie haben keine Berechtigung auf SAP-Daten zuzugreifen", null, true);
        }
    }


}


