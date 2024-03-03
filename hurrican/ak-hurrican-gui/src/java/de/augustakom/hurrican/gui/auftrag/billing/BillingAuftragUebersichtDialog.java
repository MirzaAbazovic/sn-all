/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.08.2004 13:15:49
 */
package de.augustakom.hurrican.gui.auftrag.billing;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragLeistungTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.service.billing.BillingAuftragService;


/**
 * Dialog, um eine Uebersicht der Billing-Auftraege zu einem Kunden zu erhalten.
 *
 *
 */
public class BillingAuftragUebersichtDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(BillingAuftragUebersichtDialog.class);

    private KundeAdresseView model = null;

    private AuftragLeistungTableModel tbMdlAuftragLeistung = null;

    /**
     * Konstruktor mit Angabe des Kunden, dessen Billing-Auftraege dargestellt werden sollen.
     *
     * @param kaView
     */
    public BillingAuftragUebersichtDialog(KundeAdresseView kaView) {
        super(null);
        this.model = kaView;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        StringBuilder sb = new StringBuilder();
        sb.append("Auftragsübersicht Billing ");
        if (model != null) {
            sb.append(" für Kunde ");
            sb.append(this.model.getName());
            sb.append(" (");
            sb.append(this.model.getKundeNo());
            sb.append(")");
        }
        setTitle(sb.toString());
        setIconURL("de/augustakom/hurrican/gui/images/auftraege.gif");

        configureButton(CMD_SAVE, null, null, false, false);
        configureButton(CMD_CANCEL, "Ok", null, true, true);

        tbMdlAuftragLeistung = new AuftragLeistungTableModel();
        AKJTable tbAuftraege = new AKJTable(tbMdlAuftragLeistung, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftraege.attachSorter();
        tbAuftraege.fitTable(new int[] { 70, 50, 70, 70, 70, 25, 70, 125, 200, 50, 50, 50, 50 });
        AKJScrollPane spTable = new AKJScrollPane(tbAuftraege);
        spTable.setPreferredSize(new Dimension(980, 250));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /* Liest alle benoetigten Daten. */
    private void read() {
        tbMdlAuftragLeistung.removeAll();
        if (this.model != null) {
            setWaitCursor();
            final SwingWorker<List<BAuftragLeistungView>, Void> worker = new SwingWorker<List<BAuftragLeistungView>, Void>() {

                @Override
                protected List<BAuftragLeistungView> doInBackground() throws Exception {
                    BillingAuftragService bas = getBillingService(BillingAuftragService.class.getName(), BillingAuftragService.class);
                    List<BAuftragLeistungView> result = bas.findAuftragLeistungViews(model.getKundeNo(), false);
                    return result;
                }

                @Override
                protected void done() {
                    try {
                        List<BAuftragLeistungView> result = get();
                        tbMdlAuftragLeistung.setData(result);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        setDefaultCursor();
                    }
                }

            };
            worker.execute();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
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
    public void update(Observable o, Object arg) {
    }

}


