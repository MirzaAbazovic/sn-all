/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 16:53:05
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.util.*;
import javax.swing.*;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.TalQueryService;
import de.mnet.wita.service.WitaTalOrderService;

public abstract class BaseAenderungsKennzeichenDialog extends AbstractServiceOptionDialog {

    private static final long serialVersionUID = -5743147550954407043L;
    private static final Logger LOGGER = Logger.getLogger(BaseAenderungsKennzeichenDialog.class);

    protected abstract class SubOrderWorker extends SwingWorker<List<TalSubOrder>, Void> {

        private final AKJButton sendButton;

        protected SubOrderWorker(AKJButton sendButton) {
            this.sendButton = sendButton;
        }

        @Override
        protected abstract List<TalSubOrder> doInBackground() throws Exception;

        @Override
        protected void done() {
            try {
                List<TalSubOrder> subOrdersForSelection = get();
                subOrderPanel.setData(subOrdersForSelection, getSelectedSubOrders());
                manageGUI(new AKManageableComponent[] { sendButton });
            }
            catch (Exception e) {
                LOGGER.error(e, e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    protected TalSubOrderPanel subOrderPanel;

    protected AuftragDaten auftragDaten;

    // Services
    protected CCAuftragService auftragService;
    protected CarrierElTALService carrierElTalService;
    protected TalQueryService talQueryService;
    protected WitaTalOrderService witaTalOrderService;
    protected MwfEntityService mwfEntityService;

    protected BaseAenderungsKennzeichenDialog(String resource, AuftragDaten auftragDaten) {
        this(resource);
        this.auftragDaten = auftragDaten;
    }

    protected BaseAenderungsKennzeichenDialog(String resource) {
        super(resource);
        try {
            initServices();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    protected void initGui(Long auftragId) {
        try {
            createGUI();
            loadData(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        auftragService = getCCService(CCAuftragService.class);
        carrierElTalService = getCCService(CarrierElTALService.class);
        witaTalOrderService = getCCService(WitaTalOrderService.class);
        talQueryService = getCCService(TalQueryService.class);
        mwfEntityService = getCCService(MwfEntityService.class);
    }

    private void loadData(Long auftragId) throws FindException {
        if (auftragId != null) {
            auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
        }
        checkIfAuftragCanBeChanged(auftragDaten);

        final AKJButton sendButton = getButton(CMD_SAVE);
        SwingWorker<List<TalSubOrder>, Void> loadSubOrders = getSubOrderWorker(auftragDaten, sendButton);
        setWaitCursor();
        sendButton.setEnabled(false);
        loadSubOrders.execute();
    }


    /**
     * Hook-Methode um noch extra checks auf den Auftragdaten zu machen
     *
     * @param auftragDaten Die Auftragdaten, die ueberprueft werden sollen
     */
    protected void checkIfAuftragCanBeChanged(@SuppressWarnings("unused") AuftragDaten auftragDaten) {
        // to be overridden; check nothing by default
    }

    /**
     * Hier den SwingWorker zurueckgeben, der die subOrders laedt.
     */
    protected abstract SubOrderWorker getSubOrderWorker(final AuftragDaten auftragDaten, final AKJButton sendButton);

    protected abstract List<WitaCBVorgang> getSelectedSubOrders();

    protected void closeDialog() {
        prepare4Close();
        setValue(null);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    protected List<Long> getAuftragIdsOfChangedNotSentRequests(Collection<WitaCBVorgang> changedCbVorgaenge) {
        List<Long> result = Lists.newArrayList();
        for (WitaCBVorgang witaCbVorgang : changedCbVorgaenge) {
            if (witaCbVorgang.isRequestOnUnsentRequest()) {
                result.add(witaCbVorgang.getAuftragId());
            }
        }
        return result;
    }
}
