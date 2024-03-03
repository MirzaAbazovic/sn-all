/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.04.2006 10:34:33
 */
package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.predicates.ValidAuftragPredicate;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Dialog, um fuer einen IntAccount eine Rufnummer zu vergeben. <br> Die moeglichen Rufnummern werden ueber den Kunden
 * ermittelt und in einer Tabelle zur Auswahl gestellt.
 *
 *
 */
public class ChangeDN4AccountDialog extends AbstractServiceOptionDialog implements
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ChangeDN4AccountDialog.class);

    // GUI-Komponenten
    private AKJTable tbDN = null;
    private AKReflectionTableModel<AuftragDNView> tbMdlDN = null;

    // Modelle
    private Long kundeNo = null;
    private Long auftragId = null;
    private IntAccount intAccount = null;

    /**
     * Default-Const.
     *
     * @param kundeNoOrig (original) Kundennummer
     * @param auftragId   (CC-)Auftrags-ID des Auftrags, auf dem der Account z.Z. zugeordnet ist
     * @param intAccount  Account, dem eine andere Rufnummer zugeordnet werden soll.
     */
    public ChangeDN4AccountDialog(Long kundeNoOrig, Long auftragId, IntAccount intAccount) {
        super(null);
        this.kundeNo = kundeNoOrig;
        if (this.kundeNo == null) {
            throw new IllegalArgumentException("Kundennummer muss angegeben werden!");
        }

        this.auftragId = auftragId;
        if (this.auftragId == null) {
            throw new IllegalArgumentException("Auftrags-ID muss angegeben werden!");
        }

        this.intAccount = intAccount;
        if (this.intAccount == null) {
            throw new IllegalArgumentException("Account muss angegeben werden!");
        }
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Rufnummer für Account");
        configureButton(CMD_SAVE, "Auswählen", "Ordnet die ausgewählte Rufnummer dem Account zu", true, true);

        tbMdlDN = new AKReflectionTableModel<AuftragDNView>(
                new String[] { "ONKZ", "DN-Base", "Hist-Status", "Verb.bez." },
                new String[] { "onKz", "dnBase", "histStatus", "vbz" },
                new Class[] { String.class, String.class, String.class, String.class });
        tbDN = new AKJTable(tbMdlDN, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbDN.attachSorter();
        tbDN.fitTable(new int[] { 80, 100, 80, 110 });
        AKJScrollPane spTable = new AKJScrollPane(tbDN, new Dimension(400, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            // Rufnummer darf nur geaendert werden, wenn der Auftrag noch nicht
            // realisiert ist bzw. wenn eine Aenderung angelegt wird.
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragId(this.auftragId);
            if ((ad == null) || !ad.isInRealisierung()) {
                getButton(CMD_SAVE).setEnabled(false);
                throw new HurricanGUIException("Um die Account-Rufnummer zu ändern, muss sich der Auftrag " +
                        "entweder in der Erfassung oder in einer Änderung befinden!");
            }

            RufnummerService rs = getBillingService(RufnummerService.class);
            RufnummerQuery query = new RufnummerQuery();
            query.setKundeNo(this.kundeNo);
            query.setOnlyValidToFuture(true);

            List<AuftragDNView> result = rs.findAuftragDNViews(query);
            CollectionUtils.filter(result, new ValidAuftragPredicate());
            tbMdlDN.setData(result);
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
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        AKMutableTableModel mdl = (AKMutableTableModel) tbDN.getModel();
        Object selection = mdl.getDataAtRow(tbDN.getSelectedRow());
        if (selection instanceof AuftragDNView) {
            AuftragDNView dnView = (AuftragDNView) selection;
            // 0 von ONKZ abschneiden!!!
            intAccount.setRufnummer(dnView.getOnKz().substring(1) + dnView.getDnBase());

            try {
                setWaitCursor();
                AccountService accs = getCCService(AccountService.class);
                accs.saveIntAccount(intAccount, false);

                prepare4Close();
                setValue(intAccount);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
        else {
            MessageHelper.showErrorDialog(getMainFrame(),
                    new HurricanGUIException("Bitte wählen Sie eine Rufnummer aus oder schliessen Sie den " +
                            "Dialog über <Abbrechen>.")
            );
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

}
