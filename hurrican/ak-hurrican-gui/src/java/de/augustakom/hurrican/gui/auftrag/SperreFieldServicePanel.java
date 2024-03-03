/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2005 09:04:30
 */
package de.augustakom.hurrican.gui.auftrag;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel stellt die aktuellen Sperren fuer die Abteilung FieldService dar und bietet die Moeglichkeit, die Sperren zu
 * bearbeiten.
 *
 *
 */
public class SperreFieldServicePanel extends AbstractServicePanel implements AKDataLoaderComponent {

    // FIXME (HUR-15 / HUR-31) delete me - wenn manuell auszufuehrende Sperren zukuenftig ueber Bauauftraege laufen
    // (Bis zu der Entscheidung bzgl. Bauauftraege fuer Sperren bleibt die GUI so erhalten, um
    //  unnoetigen Arbeitsaufwand zu vermeiden.)

    private static final long serialVersionUID = -3681050535147457905L;

    private static final Logger LOGGER = Logger.getLogger(SperreFieldServicePanel.class);

    private AKJTable tbLocks = null;
    private AKReferenceAwareTableModel<Lock> tbMdlLocks = null;

    /**
     * Default-Konstruktor.
     */
    public SperreFieldServicePanel() {
        super(null);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlLocks = new AKReferenceAwareTableModel<Lock>(
                new String[] { "Kunde__NO", "Deb-ID", "Auftrag-ID", "Sperr-Art",
                        "Sperr-Grund", "angelegt am", "angelegt von" },
                new String[] { "kundeNo", "debId", "auftragId", "lockModeRefId",
                        "lockReasonRefId", "createdAt", "createdFrom" },
                new Class[] { Long.class, String.class, Long.class, String.class,
                        String.class, Date.class, String.class }
        );
        tbLocks = new AKJTable(tbMdlLocks, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLocks.attachSorter();
        tbLocks.addPopupAction(new FieldServiceSperreDoneAction());
        tbLocks.fitTable(new int[] { 90, 100, 90, 120, 120, 100, 80 });
        AKJScrollPane spTable = new AKJScrollPane(tbLocks, new Dimension(580, 250));

        this.setLayout(new BorderLayout());
        this.add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            boolean isInitialized = false;
            if (!isInitialized) {
                // Referenz-Daten laden und den TableModels uebergeben
                ReferenceService referenceService = getCCService(ReferenceService.class);
                List<Reference> lockModes = referenceService.findReferencesByType(Reference.REF_TYPE_LOCK_MODE, false);
                tbMdlLocks.addReference(3, CollectionMapConverter.convert2Map(lockModes, "getId", null), "strValue");

                List<Reference> lockReasons = referenceService.findReferencesByType(Reference.REF_TYPE_LOCK_REASON, false);
                tbMdlLocks.addReference(4, CollectionMapConverter.convert2Map(lockReasons, "getId", null), "strValue");
            }

            // Sperren fuer FieldService voruebergehen auf LockService umgestellt
            // (Anzeige der Sperren erfolgt zukuenftig wahrscheinlich ueber Bauauftraege!)
            QueryCCService queryCCService = getCCService(QueryCCService.class);
            LockService lockService = getCCService(LockService.class);

            LockDetail lockDetailExample = new LockDetail();
            lockDetailExample.setAbteilungId(Abteilung.FIELD_SERVICE);

            List<LockDetail> lockDetails = queryCCService.findByExample(lockDetailExample, LockDetail.class);
            if (CollectionTools.isNotEmpty(lockDetails)) {
                for (LockDetail lockDetail : lockDetails) {
                    if (lockDetail.getExecutedAt() == null) {
                        Lock lock = lockService.findLock(lockDetail.getLockId());
                        tbMdlLocks.addObject(lock);
                    }
                }
            }
            else {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Für den FieldService liegen keine offenen Sperren vor.");
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

    /*
     * Setzt die aktuell selektierte Sperre auf 'erledigt'.
     */
    private void finishSperre() {
        try {
            AKMutableTableModel mdl = (AKMutableTableModel) tbLocks.getModel();
            int selRow = tbLocks.getSelectedRow();

            Object tmp = mdl.getDataAtRow(selRow);
            if (tmp instanceof Lock) {
                Lock lock = (Lock) tmp;

                String title = "Sperre erledigen";
                String msg = "Bitte Erledigungsdatum der Sperre (Kunde: " + lock.getKundeNo() + ") eintragen:";
                String lblTitle = "erledigt am:";
                AKDateSelectionDialog dlg = new AKDateSelectionDialog(title, msg, lblTitle);
                dlg.showDate(new Date());
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof Date) {
                    Date realDate = (Date) result;
                    if (DateTools.isDateAfter(realDate, new Date())) {
                        throw new HurricanGUIException(
                                "Bitte tragen Sie ein gültiges Datum ein. \nDas Datum darf nicht in der Zukunft liegen.");
                    }

                    QueryCCService queryCCService = getCCService(QueryCCService.class);
                    LockDetail lockDetailExample = new LockDetail();
                    lockDetailExample.setAbteilungId(Abteilung.FIELD_SERVICE);
                    lockDetailExample.setLockId(lock.getId());

                    List<LockDetail> lockDetails = queryCCService.findByExample(lockDetailExample, LockDetail.class);
                    if (CollectionTools.isNotEmpty(lockDetails)) {
                        LockDetail lockDetailFieldService = lockDetails.get(0);

                        lockDetailFieldService.setExecutedAt(realDate);
                        lockDetailFieldService.setExecutedFrom(
                                HurricanSystemRegistry.instance().getCurrentUser().getNameAndFirstName());

                        LockService lockService = getCCService(LockService.class);
                        lockService.saveLockDetail(lockDetailFieldService);
                    }
                    else {
                        throw new HurricanGUIException(
                                "LockDetail Datensatz zur Sperre konnte nicht ermittelt werden!");
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
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
        tbLocks.repaint();
    }

    /*
     * Action, um die in der Tabelle selektierte Sperre zu erledigen!
     */
    class FieldServiceSperreDoneAction extends AKAbstractAction {

        /**
         * Default-Konstruktor.
         */
        public FieldServiceSperreDoneAction() {
            putValue(AKAbstractAction.NAME, "Sperre erledigen");
            putValue(AKAbstractAction.SHORT_DESCRIPTION, "Setzt die markierte Sperre auf erledigt");
            putValue(AKAbstractAction.ACTION_COMMAND_KEY, "fieldservice.sperre.erledigen");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            finishSperre();
        }

    }
}


