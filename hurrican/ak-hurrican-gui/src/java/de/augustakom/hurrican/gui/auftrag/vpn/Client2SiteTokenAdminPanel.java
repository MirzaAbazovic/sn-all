/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2009 09:29:36
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.auftrag.shared.IPSecC2STokenTableModel;
import de.augustakom.hurrican.gui.auftrag.shared.IPSecClient2SiteTokenDetailPanel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.IPSecService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 *
 */
public class Client2SiteTokenAdminPanel extends AbstractServicePanel implements
        AKSearchComponent, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(Client2SiteTokenAdminPanel.class);

    private static final String SERIAL_NUMBER_SEARCH = "serial.number.search";
    private static final String CSV_IMPORT = "import.csv";

    private static final String SAVE_TOKEN_DETAIL = "save.token.detail";
    private static final String NEW_TOKEN = "new.token";
    private static final String DEL_TOKEN = "del.token";

    private IPSecService ipSecService;
    private ReferenceService referenceService;

    private List<IPSecClient2SiteToken> tokens;

    private IPSecClient2SiteTokenDetailPanel tokenDetailPanel;
    private AKJTable tbClient2SiteToken;
    private IPSecC2STokenTableModel tbMdlClient2SiteToken;
    private AKJTextField tfSerialNumberSearch;

    /**
     * @param resource
     */
    public Client2SiteTokenAdminPanel() {
        super("de/augustakom/hurrican/gui/auftrag/vpn/resources/Client2SiteTokenAdminPanel.xml");
        try {
            ipSecService = getCCService(IPSecService.class);
            referenceService = getCCService(ReferenceService.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        createGUI();
        loadData();
    }

    private void loadData() {
        try {
            String searchString = tfSerialNumberSearch.getText(null);
            if (searchString == null) {
                tokens = ipSecService.findAllClient2SiteTokens();
            }
            else {
                tokens = ipSecService.findAllClient2SiteTokens(searchString);
            }
            tbMdlClient2SiteToken.setData(tokens);
            tokenDetailPanel.setModel(null);

            List<Reference> tokenStatusRefs = referenceService
                    .findReferencesByType(Reference.REF_TYPE_IPSEC_TOKEN_STATUS, Boolean.TRUE);

            tbMdlClient2SiteToken.setTokenStatusRefs(tokenStatusRefs);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKSearchKeyListener searchKeyListener = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        tfSerialNumberSearch = getSwingFactory().createTextField(SERIAL_NUMBER_SEARCH, true, true, searchKeyListener);
        AKJLabel lbSerialNumberSearch = getSwingFactory().createLabel(SERIAL_NUMBER_SEARCH);
        AKJPanel filter = new AKJPanel(new GridBagLayout(), "Filter");
        filter.add(lbSerialNumberSearch, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(tfSerialNumberSearch, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJButton btnSaveTokenDetail = getSwingFactory().createButton(SAVE_TOKEN_DETAIL, getActionListener());
        AKJButton btnNewToken = getSwingFactory().createButton(NEW_TOKEN, getActionListener(), null);
        AKJButton btnDelToken = getSwingFactory().createButton(DEL_TOKEN, getActionListener(), null);
        AKJButton btnCsvImport = getSwingFactory().createButton(CSV_IMPORT, getActionListener(), null);
        tbMdlClient2SiteToken = new IPSecC2STokenTableModel();
        tbClient2SiteToken = new AKJTable(tbMdlClient2SiteToken,
                AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbClient2SiteToken.attachSorter();
        tbClient2SiteToken.addTableListener(this);
        AKJScrollPane spTokens = new AKJScrollPane(tbClient2SiteToken, new Dimension(700, 150));
        AKJPanel tokenPanel = new AKJPanel(new GridBagLayout(), "Client2Site Tokens");
        tokenPanel.add(btnNewToken, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        tokenPanel.add(btnDelToken, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        tokenPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        tokenPanel.add(btnCsvImport, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        tokenPanel.add(spTokens, GBCFactory.createGBC(100, 100, 1, 0, 1, 4, GridBagConstraints.BOTH));

        tokenDetailPanel = new IPSecClient2SiteTokenDetailPanel();
        tokenDetailPanel.setAdminPanel(true);

        this.setLayout(new GridBagLayout());
        this.add(filter, GBCFactory.createGBC(100, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(tokenPanel, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));
        this.add(tokenDetailPanel, GBCFactory.createGBC(100, 0, 0, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnSaveTokenDetail, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (CSV_IMPORT.equals(command)) {
            csvImport();
        }
        else if (NEW_TOKEN.equals(command)) {
            newToken();
        }
        else if (DEL_TOKEN.equals(command)) {
            delToken();
        }
        if (SAVE_TOKEN_DETAIL.equals(command)) {
            saveToken();
        }
    }

    private void saveToken() {
        try {
            IPSecClient2SiteToken token = (IPSecClient2SiteToken) tokenDetailPanel.getModel();
            boolean tokenIsNew = (token != null) && (token.getId() == null);
            tokenDetailPanel.saveModel();
            if (tokenIsNew) {
                loadData();
            }
            tbMdlClient2SiteToken.fireTableDataChanged();
        }
        catch (AKGUIException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void delToken() {
        try {
            IPSecClient2SiteToken selected = (IPSecClient2SiteToken)
                    ((AKMutableTableModel) tbClient2SiteToken.getModel())
                            .getDataAtRow(tbClient2SiteToken.getSelectedRow());

            if (selected == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
            }
            int opt = JOptionPane.NO_OPTION;
            opt = MessageHelper.showYesNoQuestion(this,
                    "Soll der Token wirklich gelöscht werden?", "Löschen?");

            if (opt == JOptionPane.YES_OPTION) {
                ipSecService.deleteClient2SiteToken(selected);
            }
            loadData();
            tokenDetailPanel.setModel(null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void newToken() {
        IPSecClient2SiteToken newToken = new IPSecClient2SiteToken();
        showDetails(newToken);
    }

    private void csvImport() {
        Reader reader = null;
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(getMainFrame());
            File file = chooser.getSelectedFile();
            if (file != null) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringTools.CC_DEFAULT_CHARSET));
                List<IPSecClient2SiteToken> importedTokens = ipSecService.importClient2SiteTokens(reader);
                ipSecService.saveClient2SiteTokens(importedTokens);

                int count = importedTokens.size();

                MessageHelper.showInfoDialog(getMainFrame(), "Anzahl importierter Tokens: " + count);
                loadData();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            FileTools.closeStreamSilent(reader);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSearchComponent#doSearch()
     */
    @Override
    public void doSearch() {
        try {
            tokens = ipSecService.findFreeClient2SiteTokens(tfSerialNumberSearch.getText());
            tbMdlClient2SiteToken.setData(tokens);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
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
