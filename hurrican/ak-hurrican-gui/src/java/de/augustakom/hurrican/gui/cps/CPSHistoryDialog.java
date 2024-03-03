/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2005 09:16:37
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.beans.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.CPSService;

/**
 * Dialog zur Anzeige der CPS-History zu einem best. Auftrag.
 *
 *
 */
public class CPSHistoryDialog extends AbstractServiceOptionDialog implements AKNavigationBarListener {


    private static final Logger LOGGER = Logger.getLogger(CPSHistoryDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTransactionPanel.xml";
    private static final String NAVBAR = "nav.bar";
    private static final String REFRESH = "button.refresh";
    private static final String INVALID_ORDER = "invalidOrder";
    private static final String CLOSE_DIALOG = "closeDialog";
    private static final String OK = "ok";
    private static final String PROGRESS_TEXT = "progressText";
    private static final String FOUND = "found";
    private static final String CPS_TX_NOT_FOUND = "cpsTxNotFound";
    private static final String TITLE_CPS = "titleCPS";

    private AKJNavigationBar navBar = null;
    private CPSTxTabbedPane tabbedPane = null;

    private CCAuftragModel model = null;
    private CPSHistoryLoader loader = null;

    /**
     * Konstruktor mit Angabe des Auftrags, fuer den die CPS-History ermittelt/angezeigt werden soll.
     */
    public CPSHistoryDialog(CCAuftragModel auftrag) {
        super(RESOURCE);
        this.model = auftrag;

        if ((this.model == null) || (null == this.model.getAuftragId())) {
            throw new IllegalArgumentException(getSwingFactory().getText(INVALID_ORDER));
        }

        createGUI();
        if ((model != null) && (model.getAuftragId() != null)) {
            loader = new CPSHistoryLoader(model);
            loader.execute();
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(new StringBuilder(getSwingFactory().getText(TITLE_CPS)).append(model.getAuftragId()).toString());
        configureButton(CMD_CANCEL, null, null, Boolean.FALSE, Boolean.FALSE);
        configureButton(CMD_SAVE, getSwingFactory().getText(OK), getSwingFactory().getText(CLOSE_DIALOG), Boolean.TRUE, Boolean.TRUE);
        navBar = getSwingFactory().createNavigationBar(NAVBAR, this, Boolean.TRUE, Boolean.TRUE);
        AKJButton btnRefresh = getSwingFactory().createButton(REFRESH, getActionListener(), null);
        tabbedPane = new CPSTxTabbedPane();

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(navBar, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        top.add(tabbedPane, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));
        getChildPanel().add(top);
    }

    @Override
    protected void validateSaveButton() {
    }

    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        CPSTransactionExt cpsTransaction = null;
        if (obj instanceof CPSTransactionExt) {
            try {
                setWaitCursor();
                cpsTransaction = (CPSTransactionExt) obj;

                CPSTxController.writeDataFromModelToView(cpsTransaction
                        , tabbedPane.getFieldPanel()
                        , tabbedPane.getRequestPanel()
                        , tabbedPane.getResponsePanel()
                        , tabbedPane.getLogPanel()
                        , tabbedPane.getOrderPanel());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    @Override
    protected void execute(String command) {
        if (REFRESH.equals(command)) {
            loader = new CPSHistoryLoader(model);
            loader.execute();
        }
    }

    @Override
    protected void doSave() {
        loader.cancel(true);
        setDefaultCursor();
        stopProgressBar();
        prepare4Close();
        setValue(Integer.valueOf(OK_OPTION));
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * Load data in a thread
     */
    class CPSHistoryLoader extends SwingWorker<List<CPSTransactionExt>, Void> {
        final CCAuftragModel modelInput;

        CPSHistoryLoader(final CCAuftragModel model) {
            setWaitCursor();
            showProgressBar(getSwingFactory().getText(PROGRESS_TEXT));

            modelInput = new CCAuftragModel() {

                final Long auftragId = model.getAuftragId();

                @Override
                public void setAuftragId(Long auftragId) {
                    throw new UnsupportedOperationException("Cannot change auftragId of this model!");
                }

                @Override
                public Long getAuftragId() {
                    return auftragId;
                }
            };
        }

        @Override
        protected List<CPSTransactionExt> doInBackground() throws Exception {
            CPSService cpsService = getCCService(CPSService.class);
            List<CPSTransactionExt> cpsTransactionList = cpsService.findCPSTransactionsForTechOrder(modelInput);
            return cpsTransactionList;
        }

        @Override
        protected void done() {
            try {
                List<CPSTransactionExt> cpsTransactionList = get();

                if (CollectionTools.isNotEmpty(cpsTransactionList)) {
                    showProgressBar(new StringBuilder(cpsTransactionList.size()).append(getSwingFactory().getText(FOUND)).toString());

                    if (navBar != null) {
                        navBar.setData(cpsTransactionList);
                    }
                }
                if (CollectionTools.isEmpty(cpsTransactionList)) {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            getSwingFactory().getText(CPS_TX_NOT_FOUND, model.getAuftragId()),
                            null,
                            Boolean.TRUE);
                }
            }
            catch (CancellationException e) {
                LOGGER.debug("CPSHistoryLoader canceled", e);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
                stopProgressBar();
            }

        }
    }

}
