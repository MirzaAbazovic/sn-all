/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 11:37:08
 */
package de.augustakom.hurrican.gui.lock;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog fuer die Anzeige der Lock-History zu einem Kunden oder Auftrag.
 *
 *
 */
public class LockHistoryDialog extends AbstractServiceOptionDialog implements AKTableOwner, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(LockHistoryDialog.class);

    private AKReferenceAwareTableModel<Lock> tbMdlLocks = null;
    private AKReferenceAwareTableModel<LockDetail> tbMdlLockDetails = null;
    private AKJTextArea taText = null;

    // Modelle
    private KundenModel kundenModel = null;
    private CCAuftragModel auftragModel = null;

    // sonstiges
    private boolean isInitialized = false;

    /**
     * Oeffnet den LockHistory-Dialog fuer einen Kunden.
     *
     * @param kundenModel
     */
    public LockHistoryDialog(KundenModel kundenModel) {
        super("de/augustakom/hurrican/gui/lock/resources/LockHistoryDialog.xml");
        this.kundenModel = kundenModel;
        createGUI();
        loadData();
    }

    /**
     * Oeffnet den LockHistory-Dialog fuer einen Auftrag.
     *
     * @param auftragModel
     */
    public LockHistoryDialog(CCAuftragModel auftragModel) {
        super("de/augustakom/hurrican/gui/lock/resources/LockHistoryDialog.xml");
        this.auftragModel = auftragModel;
        createGUI();
        loadData();
    }

    /* (non-Javadoc)
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIconURL("de/augustakom/hurrican/gui/images/locked.gif");

        tbMdlLocks = new AKReferenceAwareTableModel<Lock>(
                new String[] { "Kunde__NO", "Deb-ID", "Auftrag-ID", "Sperr-Art",
                        "Sperr-Grund", "angelegt am", "angelegt von", "Status" },
                new String[] { "kundeNo", "debId", "auftragId", "lockModeRefId",
                        "lockReasonRefId", "createdAt", "createdFrom", "lockStateRefId" },
                new Class[] { Long.class, String.class, Long.class, String.class,
                        String.class, Date.class, String.class, String.class }
        );
        AKJTable tbLocks = new AKJTable(tbMdlLocks, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLocks.fitTable(new int[] { 90, 100, 90, 120, 120, 100, 80, 80 });
        tbLocks.addTableListener(this);
        AKJScrollPane spLocks = new AKJScrollPane(tbLocks, new Dimension(650, 250));

        tbMdlLockDetails = new AKReferenceAwareTableModel<LockDetail>(
                new String[] { "Abteilung", "ausgeführt am", "ausgeführt von" },
                new String[] { "abteilungId", "executedAt", "executedFrom" },
                new Class[] { String.class, String.class, String.class });
        AKJTable tbLockDetails = new AKJTable(tbMdlLockDetails, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLockDetails.fitTable(new int[] { 90, 110, 100 });
        AKJScrollPane spLockDetails = new AKJScrollPane(tbLockDetails, new Dimension(320, 90));

        AKJLabel lblText = getSwingFactory().createLabel("lock.description");
        taText = getSwingFactory().createTextArea("lock.description", false);
        taText.setWrapStyleWord(true);
        AKJScrollPane spText = new AKJScrollPane(taText, new Dimension(250, 90));

        AKJPanel topPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title.locks"));
        topPnl.add(spLocks, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel downPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title.lock.details"));
        downPnl.add(spLockDetails, GBCFactory.createGBC(100, 100, 0, 0, 1, 2, GridBagConstraints.BOTH));
        downPnl.add(lblText, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        downPnl.add(spText, GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH, 10));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(topPnl, BorderLayout.CENTER);
        getChildPanel().add(downPnl, BorderLayout.SOUTH);
    }

    /* (non-Javadoc)
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            if (!isInitialized) {
                // Referenz-Daten laden und den TableModels uebergeben
                ReferenceService referenceService = getCCService(ReferenceService.class);
                List<Reference> lockModes = referenceService.findReferencesByType(Reference.REF_TYPE_LOCK_MODE, false);
                tbMdlLocks.addReference(3, CollectionMapConverter.convert2Map(lockModes, "getId", null), "strValue");

                List<Reference> lockReasons = referenceService.findReferencesByType(Reference.REF_TYPE_LOCK_REASON, false);
                tbMdlLocks.addReference(4, CollectionMapConverter.convert2Map(lockReasons, "getId", null), "strValue");

                List<Reference> lockStates = referenceService.findReferencesByType(Reference.REF_TYPE_LOCK_STATE, false);
                tbMdlLocks.addReference(7, CollectionMapConverter.convert2Map(lockStates, "getId", null), "strValue");

                NiederlassungService nlService = getCCService(NiederlassungService.class);
                List<Abteilung> abteilungen = nlService.findAbteilungen();
                tbMdlLockDetails.addReference(0, CollectionMapConverter.convert2Map(abteilungen, "getId", null), "name");

                isInitialized = true;
            }

            // Example-Objekt aufbauen
            Lock example = new Lock();
            if (kundenModel != null) {
                example.setKundeNo(kundenModel.getKundeNo());
            }
            else if (auftragModel != null) {
                example.setAuftragId(auftragModel.getAuftragId());
            }
            else {
                throw new HurricanGUIException("Weder Kunde noch Auftrag fuer Query angegeben!");
            }

            // Locks zum Example-Objekt laden
            LockService lockService = getCCService(LockService.class);
            List<Lock> locks = lockService.findLocksByExample(example);
            tbMdlLocks.setData(locks);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* (non-Javadoc)
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
    }

    /* (non-Javadoc)
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /* (non-Javadoc)
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        GuiTools.cleanFields(this);
        tbMdlLockDetails.removeAll();

        if (details instanceof Lock) {
            try {
                Lock selectedLock = (Lock) details;

                // Lock-Details laden / anzeigen
                LockService lockService = getCCService(LockService.class);
                List<LockDetail> lockDetails = lockService.findLockDetails(selectedLock.getId());
                tbMdlLockDetails.setData(lockDetails);

                taText.setText(selectedLock.getLockReasonText());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}
