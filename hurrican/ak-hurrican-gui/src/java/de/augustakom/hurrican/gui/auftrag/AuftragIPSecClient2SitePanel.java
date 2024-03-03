/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2009 16:14:31
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.auftrag.shared.IPSecC2STokenTableModel;
import de.augustakom.hurrican.gui.auftrag.shared.IPSecClient2SiteTokenDetailPanel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.IPSecService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 *
 */
public class AuftragIPSecClient2SitePanel extends AbstractAuftragPanel implements AKTableOwner {
    private static final Logger LOGGER = Logger.getLogger(AuftragIPSecClient2SitePanel.class);

    private static final String RESOURCE_FILE = "de/augustakom/hurrican/gui/auftrag/resources/AuftragIPSecClient2SitePanel.xml";

    private static final String ASSIGN_TOKEN = "assign.token";
    private static final String REMOVE_TOKEN = "remove.token";

    private IPSecService ipSecService;

    private AKJTable tbClient2SiteToken;
    private IPSecC2STokenTableModel tbMdlClient2SiteToken;
    private AbstractDataPanel tokenDetailPanel;

    private CCAuftragModel auftragModel;

    private ReferenceService referenceService;


    public AuftragIPSecClient2SitePanel() {
        super(RESOURCE_FILE);
        init();
        createGUI();
    }

    private void init() {
        try {
            ipSecService = getCCService(IPSecService.class);
            referenceService = getCCService(ReferenceService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {

        tbMdlClient2SiteToken = new IPSecC2STokenTableModel();
        tbClient2SiteToken = new AKJTable(tbMdlClient2SiteToken, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbClient2SiteToken.attachSorter();
        tbClient2SiteToken.addTableListener(this);
        AKJScrollPane spTokens = new AKJScrollPane(tbClient2SiteToken, new Dimension(700, 150));
        AKJButton btnAssignToken = getSwingFactory().createButton(ASSIGN_TOKEN, getActionListener(), null);
        AKJButton btnRemoveToken = getSwingFactory().createButton(REMOVE_TOKEN, getActionListener(), null);
        AKJPanel tokens = new AKJPanel(new GridBagLayout(), "IPSecClient2Site Tokens");
        tokens.add(btnAssignToken, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        tokens.add(btnRemoveToken, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        tokens.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        tokens.add(spTokens, GBCFactory.createGBC(100, 100, 1, 0, 1, 3, GridBagConstraints.BOTH));

        tokenDetailPanel = new IPSecClient2SiteTokenDetailPanel();

        this.setLayout(new GridBagLayout());
        this.add(tokens, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(tokenDetailPanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnAssignToken, btnRemoveToken);
        try {
            tokenDetailPanel.setModel(null);
        }
        catch (AKGUIException e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (ASSIGN_TOKEN.equals(command)) {
            assignToken();
        }
        else if (REMOVE_TOKEN.equals(command)) {
            removeToken();
        }
    }

    private void assignToken() {
        if (auftragModel != null) {
            try {
                Client2SiteTokenDialog dlg = new Client2SiteTokenDialog(auftragModel);
                DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                showValues();
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void removeToken() {
        if (auftragModel != null) {
            try {
                IPSecClient2SiteToken selected = (IPSecClient2SiteToken)
                        ((AKMutableTableModel) tbClient2SiteToken.getModel())
                                .getDataAtRow(tbClient2SiteToken.getSelectedRow());

                if (selected == null) {
                    throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
                }
                int opt = JOptionPane.NO_OPTION;
                opt = MessageHelper.showYesNoQuestion(this,
                        "Soll der Token wirklich entfernt werden?", "Entfernen?");

                if (opt == JOptionPane.YES_OPTION) {
                    selected.setAuftragId(null);
                    ipSecService.saveClient2SiteToken(selected);
                    showValues();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        if (auftragModel != null) {
            try {
                setWaitCursor();
                showValues();
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

    /* Zeigt die IPSec Daten an */
    private void showValues() {
        GuiTools.cleanFields(this);
        try {
            tokenDetailPanel.setModel(null);
        }
        catch (AKGUIException e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        if (auftragModel != null) {
            loadTokens();
        }
    }

    private void loadTokens() {
        List<IPSecClient2SiteToken> freeTokens = null;
        try {
            freeTokens = ipSecService.findClient2SiteTokens(auftragModel.getAuftragId());
            tbMdlClient2SiteToken.setData(freeTokens);

            List<Reference> tokenStatusRefs = referenceService
                    .findReferencesByType(Reference.REF_TYPE_IPSEC_TOKEN_STATUS, Boolean.TRUE);
            tbMdlClient2SiteToken.setTokenStatusRefs(tokenStatusRefs);
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            tokenDetailPanel.saveModel();
            tbMdlClient2SiteToken.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return auftragModel;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        this.auftragModel = null;
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
        }
        readModel();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof IPSecClient2SiteToken) {
            try {
                tokenDetailPanel.setModel((IPSecClient2SiteToken) details);
            }
            catch (AKGUIException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

}
