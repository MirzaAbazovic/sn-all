/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2009 14:32:27
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.auftrag.shared.IPSecC2STokenTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.IPSecService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 *
 */
public class Client2SiteTokenDialog extends AbstractServiceOptionDialog
        implements AKDataLoaderComponent, AKObjectSelectionListener, AKSearchComponent {
    private static final Logger LOGGER = Logger.getLogger(Client2SiteTokenDialog.class);

    private IPSecService ipSecService;
    private ReferenceService referenceService;

    private CCAuftragModel iPSecClient2Site;
    private List<IPSecClient2SiteToken> freeTokens;
    private IPSecClient2SiteToken selectedToken;

    private AKJTable tbClient2SiteToken;
    private IPSecC2STokenTableModel tbMdlClient2SiteToken;
    private AKJTextField tfSerialNumber;

    /**
     * Konstruktor mit Angabe des zu editierenden IPSecClient2Site Objekts.
     *
     * @param endgeraetIp
     * @throws ServiceNotFoundException
     */
    public Client2SiteTokenDialog(CCAuftragModel iPSecClient2Site) {
        super("de/augustakom/hurrican/gui/auftrag/resources/Client2SiteTokenDialog.xml");
        if (iPSecClient2Site == null) {
            throw new IllegalArgumentException("Es wurde kein IPSecClient2Site-Objekt angegeben.");
        }

        try {
            ipSecService = getCCService(IPSecService.class);
            referenceService = getCCService(ReferenceService.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.iPSecClient2Site = iPSecClient2Site;

        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setValues();
            if (selectedToken != null) {
                ipSecService.saveClient2SiteToken(selectedToken);
            }

            prepare4Close();
            setValue(selectedToken);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private void setValues() {
        selectedToken = (IPSecClient2SiteToken)
                ((AKMutableTableModel) tbClient2SiteToken.getModel())
                        .getDataAtRow(tbClient2SiteToken.getSelectedRow());
        if (selectedToken != null) {
            selectedToken.setAuftragId(iPSecClient2Site.getAuftragId());
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJPanel child = getChildPanel();

        AKSearchKeyListener searchKeyListener = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        tfSerialNumber = getSwingFactory().createTextField("serial.number", true, true, searchKeyListener);
        AKJLabel lbSerialNumber = getSwingFactory().createLabel("serial.number");
        AKJPanel filter = new AKJPanel(new GridBagLayout(), "Filter");
        filter.add(lbSerialNumber, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(tfSerialNumber, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        child.setLayout(new GridBagLayout());
        tbMdlClient2SiteToken = new IPSecC2STokenTableModel();
        tbClient2SiteToken = new AKJTable(tbMdlClient2SiteToken,
                AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbClient2SiteToken.attachSorter();
        tbClient2SiteToken.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spAcls = new AKJScrollPane(tbClient2SiteToken, new Dimension(700, 150));

        child.add(filter, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spAcls, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        // Default-Daten fuer den Dialog laden
        try {
            freeTokens = ipSecService.findFreeClient2SiteTokens();
            tbMdlClient2SiteToken.setData(freeTokens);

            List<Reference> tokenStatusRefs = referenceService
                    .findReferencesByType(Reference.REF_TYPE_IPSEC_TOKEN_STATUS, Boolean.TRUE);
            tbMdlClient2SiteToken.setTokenStatusRefs(tokenStatusRefs);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof IPSecClient2SiteToken) {
            doSave();
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
            freeTokens = ipSecService.findFreeClient2SiteTokens(tfSerialNumber.getText());
            tbMdlClient2SiteToken.setData(freeTokens);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
