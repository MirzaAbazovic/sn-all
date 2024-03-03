/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.2009 14:01:21
 */
package de.augustakom.hurrican.gui.lock;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog, um eine neue Sperre fuer n Auftraege anzulegen.
 *
 *
 */
public class CreateNewLockDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(CreateNewLockDialog.class);

    private AKJTextField tfDebitorId = null;
    private AKReferenceField rfLockMode = null;
    private AKReferenceField rfLockReason = null;
    private AKJTextArea taLockReason = null;
    private AKJTable tbOrders = null;
    private AKReflectionTableModel<AuftragDatenView> tbMdlOrders = null;

    /**
     * Default-Const.
     */
    public CreateNewLockDialog() {
        super("de/augustakom/hurrican/gui/lock/resources/CreateNewLockDialog.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblDebitorId = getSwingFactory().createLabel("debitor.id");
        AKJLabel lblLockMode = getSwingFactory().createLabel("lock.mode");
        AKJLabel lblLockReason = getSwingFactory().createLabel("lock.reason");
        AKJLabel lblOrders = getSwingFactory().createLabel("orders", AKJLabel.LEFT, Font.BOLD);

        tfDebitorId = getSwingFactory().createTextField("debitor.id");
        rfLockMode = getSwingFactory().createReferenceField("lock.mode");
        rfLockReason = getSwingFactory().createReferenceField("lock.reason");
        taLockReason = getSwingFactory().createTextArea("lock.reason");
        AKJScrollPane spLockReason = new AKJScrollPane(taLockReason, new Dimension(100, 30));
        AKJButton btnSearch = getSwingFactory().createButton("search", getActionListener());

        tbMdlOrders = new AKReflectionTableModel<AuftragDatenView>(
                new String[] { "Kunde__NO", "Name", "Taifun", "Auftrag-ID", "Status", "Produkt" },
                new String[] { "kundeNo", "name", "auftragNoOrig", "auftragId", "auftragStatusText", "anschlussart" },
                new Class[] { Long.class, String.class, Long.class, Long.class, String.class, String.class });
        tbOrders = new AKJTable(tbMdlOrders, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbOrders.fitTable(new int[] { 90, 120, 80, 80, 120, 120 });
        AKJScrollPane spOrders = new AKJScrollPane(tbOrders, new Dimension(700, 200));

        AKJPanel searchPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title.search"));
        searchPnl.add(lblDebitorId, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        searchPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        searchPnl.add(tfDebitorId, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        searchPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        searchPnl.add(btnSearch, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel lockPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title.lock"));
        lockPnl.add(lblOrders, GBCFactory.createGBC(0, 0, 0, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        lockPnl.add(spOrders, GBCFactory.createGBC(100, 100, 0, 1, 4, 1, GridBagConstraints.BOTH));
        lockPnl.add(lblLockMode, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        lockPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        lockPnl.add(rfLockMode, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        lockPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        lockPnl.add(lblLockReason, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        lockPnl.add(rfLockReason, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        lockPnl.add(spLockReason, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(searchPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(lockPnl, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    public final void loadData() {
        try {
            QueryCCService queryService = getCCService(QueryCCService.class);

            Reference refLockModeExample = new Reference();
            refLockModeExample.setType(Reference.REF_TYPE_LOCK_MODE);
            refLockModeExample.setGuiVisible(Boolean.TRUE);
            rfLockMode.setReferenceFindExample(refLockModeExample);
            rfLockMode.setFindService(queryService);

            Reference refLockReasonExample = new Reference();
            refLockReasonExample.setType(Reference.REF_TYPE_LOCK_REASON);
            refLockReasonExample.setGuiVisible(Boolean.TRUE);
            rfLockReason.setReferenceFindExample(refLockReasonExample);
            rfLockReason.setFindService(queryService);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if ("search".equals(command)) {
            doSearch();
        }
    }

    /* Sucht nach allen Auftraegen zu den angegebenen Suchparametern. */
    private void doSearch() {
        try {
            tbMdlOrders.removeAll();

            AuftragSAPQuery querySAP = new AuftragSAPQuery();
            querySAP.setSapDebitorId(tfDebitorId.getText(null));
            querySAP.setNurAktuelle(true);

            final LockService lockService = getCCService(LockService.class);
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            List<AuftragDatenView> auftragDaten = auftragService.findAuftragDatenViews(querySAP);

            CollectionUtils.filter(auftragDaten, new Predicate() {
                // auf aktive Auftraege filtern
                @Override
                public boolean evaluate(Object obj) {
                    AuftragDatenView view = (AuftragDatenView) obj;
                    AuftragDaten ad = new AuftragDaten();
                    ad.setStatusId(view.getAuftragStatusId());
                    ad.setKuendigung(view.getKuendigung());
                    if (ad.isAuftragActive() || (ad.isInKuendigung() && !ad.isCancelled())) {
                        try {
                            // nur Auftraege anzeigen, die keine aktive Sperre haben!
                            return !lockService.hasActiveLock(view.getAuftragId());
                        }
                        catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            return true;
                        }
                    }
                    return false;
                }
            });
            tbMdlOrders.setData(auftragDaten);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            int[] selectedRows = tbOrders.getSelectedRows();
            if ((selectedRows == null) || (selectedRows.length <= 0)) {
                throw new HurricanGUIException(getSwingFactory().getText("error.no.selection"));
            }

            List<Lock> createdLocks = new ArrayList<Lock>();
            @SuppressWarnings("unchecked")
            AKMutableTableModel<AuftragDatenView> tbMdl = (AKMutableTableModel<AuftragDatenView>) tbOrders.getModel();
            for (int selectedRow : selectedRows) {
                AuftragDatenView auftrag = tbMdl.getDataAtRow(selectedRow);

                Lock lock = new Lock();
                lock.setAuftragId(auftrag.getAuftragId());
                lock.setAuftragNoOrig(auftrag.getAuftragNoOrig());
                lock.setKundeNo(auftrag.getKundeNo());
                lock.setDebId(tfDebitorId.getText());
                lock.setLockModeRefId(rfLockMode.getReferenceIdAs(Long.class));
                lock.setLockReasonRefId(rfLockReason.getReferenceIdAs(Long.class));
                lock.setLockReasonText(taLockReason.getText(null));
                lock.setLockStateRefId(Lock.REF_ID_LOCK_STATE_ACTIVE);

                if (lock.getLockModeRefId() == null) {
                    throw new HurricanGUIException(getSwingFactory().getText("error.no.lock.mode"));
                }

                LockService lockService = getCCService(LockService.class);
                lockService.createLock(lock, HurricanSystemRegistry.instance().getSessionId());

                createdLocks.add(lock);
            }

            prepare4Close();
            setValue(createdLocks);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

}
