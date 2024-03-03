/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.2009 11:29:03
 */
package de.augustakom.hurrican.gui.lock;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionPane;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.LockView;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel fuer die Darstellung u. Verwaltung von Sperren.
 *
 *
 */
public class LockOverviewPanel extends AbstractServicePanel implements AKTableOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(LockOverviewPanel.class);

    private static final int COL_LOCK_MODE = 5;
    private static final int COL_LOCK_REASON = 6;

    // GUI-Elements
    private AKJTable tbLocks = null;
    private AKReferenceAwareTableModel<LockView> tbMdlLocks = null;
    private AKReferenceAwareTableModel<LockDetail> tbMdlLockDetails = null;
    private AKJTextArea taLockReason = null;

    // Modelle
    private Lock actLock = null;

    // sonstiges
    private boolean isInitialized = false;

    /**
     * Default-Const.
     */
    public LockOverviewPanel() {
        super("de/augustakom/hurrican/gui/lock/resources/LockOverviewPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlLocks = new AKReferenceAwareTableModel<LockView>(
                new String[] { "Kunde__NO", "Name", "Deb-ID", "Auftrag-ID", "Order__No", "Sperr-Art",
                        "Sperr-Grund", "angelegt am", "angelegt von", "Auftrag-Status" },
                new String[] { "kundeNo", "kundenName", "debId", "auftragId", "auftragNoOrig",
                        "lockModeRefId", "lockReasonRefId", "createdAt", "createdFrom", "auftragStatus" },
                new Class[] { Long.class, String.class, String.class, Long.class, Long.class,
                        String.class, String.class, Date.class, String.class, String.class }
        );
        tbLocks = new AKJTable(tbMdlLocks, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLocks.attachSorter();
        tbLocks.fitTable(new int[] { 90, 120, 100, 90, 90, 120, 140, 100, 80, 120 });
        tbLocks.addTableListener(this);
        AKJScrollPane spLocks = new AKJScrollPane(tbLocks, new Dimension(500, 400));

        tbMdlLockDetails = new AKReferenceAwareTableModel<LockDetail>(
                new String[] { "Abteilung", "ausgeführt am", "ausgeführt von" },
                new String[] { "abteilungId", "executedAt", "executedFrom" },
                new Class[] { String.class, String.class, String.class });
        AKJTable tbLockDetails = new AKJTable(tbMdlLockDetails, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLockDetails.fitTable(new int[] { 90, 110, 100 });
        AKJScrollPane spLockDetails = new AKJScrollPane(tbLockDetails, new Dimension(320, 70));

        AKJButton btnNewLock = getSwingFactory().createButton("create.new.lock", getActionListener());
        AKJButton btnChangeToFull = getSwingFactory().createButton("change.to.full", getActionListener());
        AKJButton btnRemoveLock = getSwingFactory().createButton("remove.lock", getActionListener());
        AKJButton btnDemontage = getSwingFactory().createButton("create.demontage", getActionListener());
        AKJButton btnSave = getSwingFactory().createButton("save.lock", getActionListener());

        AKJLabel lblLockDetails = getSwingFactory().createLabel("lock.details", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblLockReason = getSwingFactory().createLabel("lock.reason.text", AKJLabel.LEFT, Font.BOLD);
        taLockReason = getSwingFactory().createTextArea("lock.reason.text", true);
        AKJScrollPane spLockReason = new AKJScrollPane(taLockReason, new Dimension(150, 70));

        AKJPanel fctPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("functions"));
        fctPnl.add(btnNewLock, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnChangeToFull, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnRemoveLock, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnDemontage, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 4, 1, 1, GridBagConstraints.VERTICAL));
        fctPnl.add(btnSave, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("details"));
        detPnl.add(lblLockDetails, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(lblLockReason, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(spLockDetails, GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        detPnl.add(spLockReason, GBCFactory.createGBC(0, 100, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel downPnl = new AKJPanel(new BorderLayout());
        downPnl.add(fctPnl, BorderLayout.WEST);
        downPnl.add(detPnl, BorderLayout.CENTER);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT, true);
        split.setTopComponent(spLocks);
        split.setBottomComponent(new AKJScrollPane(downPnl));
        split.setDividerSize(2);
        split.setResizeWeight(1d);  // Top-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

        manageGUI(btnNewLock, btnChangeToFull, btnRemoveLock, btnDemontage);
    }

    @Override
    public final void loadData() {
        try {
            GuiTools.cleanFields(this);
            tbMdlLocks.removeAll();
            tbMdlLockDetails.removeAll();

            if (!isInitialized) {
                // Referenz-Daten laden und den TableModels uebergeben
                ReferenceService referenceService = getCCService(ReferenceService.class);
                List<Reference> lockModes = referenceService.findReferencesByType(Reference.REF_TYPE_LOCK_MODE, false);
                tbMdlLocks.addReference(COL_LOCK_MODE, CollectionMapConverter.convert2Map(lockModes, "getId", null), "strValue");

                List<Reference> lockReasons = referenceService.findReferencesByType(Reference.REF_TYPE_LOCK_REASON, false);
                tbMdlLocks.addReference(COL_LOCK_REASON, CollectionMapConverter.convert2Map(lockReasons, "getId", null), "strValue");

                NiederlassungService nlService = getCCService(NiederlassungService.class);
                List<Abteilung> abteilungen = nlService.findAbteilungen();
                tbMdlLockDetails.addReference(0, CollectionMapConverter.convert2Map(abteilungen, "getId", null), "name");

                isInitialized = true;
            }

            // aktive Sperren laden
            KundenService kundenService = getBillingService(KundenService.class);
            LockService lockService = getCCService(LockService.class);
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            List<Lock> activeLocks = lockService.findActiveLocks();
            if (CollectionTools.isNotEmpty(activeLocks)) {
                List<LockView> lockViews = new ArrayList<LockView>();
                for (Lock activeLock : activeLocks) {
                    LockView lockView = new LockView();
                    lockView.setLock(activeLock);

                    final Kunde kunde = kundenService.findKunde(activeLock.getKundeNo());
                    final AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(lockView.getAuftragId());
                    final AuftragStatus auftragStatus = auftragService.findAuftragStatus(auftragDaten.getStatusId());
                    lockView.setKundenName((kunde != null) ? kunde.getNameVorname() : null);
                    lockView.setAuftragStatus(auftragStatus.getStatusText());
                    lockViews.add(lockView);
                }

                tbMdlLocks.setData(lockViews);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if ("create.new.lock".equals(command)) {
            createNewLock();
        }
        else if ("change.to.full".equals(command)) {
            changeLockToFull();
        }
        else if ("remove.lock".equals(command)) {
            removeLock();
        }
        else if ("create.demontage".equals(command)) {
            createDemontage();
        }
        else if ("save.lock".equals(command)) {
            doSave();
        }
    }

    @Override
    public void showDetails(Object details) {
        GuiTools.cleanFields(this);
        tbMdlLockDetails.removeAll();
        actLock = null;

        if (details instanceof LockView) {
            actLock = ((LockView) details).getLock();
            try {
                // Lock-Details laden / anzeigen
                LockService lockService = getCCService(LockService.class);
                List<LockDetail> lockDetails = lockService.findLockDetails(actLock.getId());
                tbMdlLockDetails.setData(lockDetails);

                taLockReason.setText(actLock.getLockReasonText());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /* Oeffnet einen Dialog, um neue Locks zu generieren. */
    private void createNewLock() {
        try {
            CreateNewLockDialog dlg = new CreateNewLockDialog();
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof List<?>) {
                KundenService kundenService = getBillingService(KundenService.class);
                List<Lock> locks = (List<Lock>) result;
                for (Lock lock : locks) {
                    // neu generierte Locks der Tabelle hinzufuegen
                    LockView view = new LockView();
                    view.setLock(lock);

                    Kunde kunde = kundenService.findKunde(lock.getKundeNo());
                    view.setKundenName((kunde != null) ? kunde.getNameVorname() : null);

                    ((AKMutableTableModel<LockView>) tbLocks.getModel()).addObject(view);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Aendert die aktuelle Sperre in eine 'Vollsperre'. */
    private void changeLockToFull() {
        createLockSuccessor(Lock.REF_ID_LOCK_MODE_CHANGE_TO_FULL);
    }

    /* Schaltet den Auftrag wieder frei. */
    private void removeLock() {
        createLockSuccessor(Lock.REF_ID_LOCK_MODE_UNLOCK);
    }

    /* Veranlasst eine Demontage fuer den Auftrag. */
    private void createDemontage() {
        createLockSuccessor(Lock.REF_ID_LOCK_MODE_DEMONTAGE);
    }

    /* Erstellt fuer den aktuell selektierten Lock-Eintrag einen Nachfolger. */
    private void createLockSuccessor(Long newLockMode) {
        try {
            int selection = MessageHelper.showConfirmDialog(getMainFrame(),
                    StringTools.formatString(getSwingFactory().getText("change.message"), new String[] { getActLock().getDebId() }),
                    getSwingFactory().getText("change.title"), AKJOptionPane.YES_NO_OPTION, AKJOptionPane.QUESTION_MESSAGE);

            if (selection == AKJOptionPane.OK_OPTION) {
                Lock lock = createCopyOfLock(getActLock());
                lock.setLockModeRefId(newLockMode);
                lock.setLockStateRefId(Lock.REF_ID_LOCK_STATE_ACTIVE);

                if (NumberTools.equal(newLockMode, Lock.REF_ID_LOCK_MODE_UNLOCK)) {
                    // bei UNLOCK wird die Lock-Reason auf NULL gesetzt
                    lock.setLockReasonRefId(null);
                    lock.setLockReasonText(null);
                }

                LockService lockService = getCCService(LockService.class);
                lockService.createLock(lock, HurricanSystemRegistry.instance().getSessionId());

                loadData();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Speichert eine Aenderung (Lock-Reason) fuer die aktuelle Sperre. */
    private void doSave() {
        try {
            Lock lock = getActLock();
            lock.setLockReasonText(taLockReason.getText(null));

            LockService lockService = getCCService(LockService.class);
            lockService.saveLock(lock);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Erstellt ein neues Lock-Objekt und uebernimmt die Basis-Daten
     * aus <code>toCopy</code>.
     * Als Basisdaten werden z.B. Kunden- u. Auftragsnummern angesehen.
     * Parameter wie LockMode, LockState etc. werden ignoriert.
     * <br><br>
     * Das neue Lock-Objekt erhaelt als Parent-ID die ID des Lock-Objekts
     * <code>toCopy</code>.
     */
    private Lock createCopyOfLock(Lock toCopy) {
        Lock newLock = new Lock();
        newLock.setAuftragId(toCopy.getAuftragId());
        newLock.setAuftragNoOrig(toCopy.getAuftragNoOrig());
        newLock.setDebId(toCopy.getDebId());
        newLock.setKundeNo(toCopy.getKundeNo());
        newLock.setLockReasonRefId(toCopy.getLockReasonRefId());
        newLock.setLockReasonText(toCopy.getLockReasonText());
        newLock.setParentLockId(toCopy.getId());

        return newLock;
    }

    /*
     * Gibt den aktuell ausgewaehlten Lock-Eintrag zurueck.
     * @throws HurricanGUIException wenn kein Lock-Eintrag markiert ist
     */
    private Lock getActLock() throws HurricanGUIException {
        if (actLock == null) {
            throw new HurricanGUIException(getSwingFactory().getText("no.selection"));
        }
        return actLock;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}
