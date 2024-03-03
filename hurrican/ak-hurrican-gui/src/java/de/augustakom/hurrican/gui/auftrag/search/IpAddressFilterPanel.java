/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2012 13:55:21
 */
package de.augustakom.hurrican.gui.auftrag.search;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.net.IPValidationTool;
import de.augustakom.hurrican.gui.auftrag.shared.IPAddressSearchResultTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchQuery;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchView;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Filter-Panel fuer die Kundensuche ueber IP-Adressen.
 *
 *
 */
public class IpAddressFilterPanel extends AbstractServicePanel implements
        IFilterOwner<AbstractHurricanQuery, IPAddressSearchResultTableModel> {

    private static final String IP_ADDRESS = "ip.address";
    private static final String IP_ONLY_ACTIVE = "ip.onlyactive";

    private static final int[] RESULT_TABLE_COLUMN_SIZES = new int[] { 80, 80, 240, 160, 80, 120, 80, 80, 120 };

    private static final int RESULT_LIMIT = 500;

    private static final Logger LOGGER = Logger.getLogger(IpAddressFilterPanel.class);

    private final KeyListener searchKL;
    private AKJTextField tfIpAddress;
    private AKJCheckBox cbIncludeHistories;

    public IpAddressFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/IpAddressFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblIpAddress = getSwingFactory().createLabel(IP_ADDRESS);
        AKJLabel lblOnlyActive = getSwingFactory().createLabel(IP_ONLY_ACTIVE);
        tfIpAddress = getSwingFactory().createTextField(IP_ADDRESS, true, true, searchKL);
        cbIncludeHistories = getSwingFactory().createCheckBox(IP_ONLY_ACTIVE);

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel()         , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblIpAddress           , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()         , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.NONE));
        this.add(tfIpAddress            , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()         , GBCFactory.createGBC(  0,  0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblOnlyActive          , GBCFactory.createGBC(  0,  0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()         , GBCFactory.createGBC(  0,  0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(cbIncludeHistories     , GBCFactory.createGBC(  0,  0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()         , GBCFactory.createGBC(100,100, 8, 2, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    private boolean onlyActive() {
        return !cbIncludeHistories.isSelected();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public AbstractHurricanQuery getFilter() throws HurricanGUIException {
        String address = StringUtils.trim(tfIpAddress.getText());
        boolean v4search = IPValidationTool.validateIPV4(address).isValid(), v6search = IPValidationTool.validateIPV6(
                address).isValid();

        if ((!v4search && !v6search) || (v4search && v6search)) {
            return IPAddressSearchQuery.createEmptyIPAddressSearchQuery();
        }

        return new IPAddressSearchQuery(address, v4search, onlyActive(), RESULT_LIMIT);
    }

    @Override
    public IPAddressSearchResultTableModel doSearch(AbstractHurricanQuery filter) throws HurricanGUIException {
        final IPAddressSearchResultTableModel tbModel = new IPAddressSearchResultTableModel();
        tbModel.setData(Collections.<IPAddressSearchView>emptyList());
        if (filter.isEmpty()) {
            MessageHelper.showWarningDialog(getMainFrame(),
                    String.format("Es wurde kein oder kein gültiger Suchparameter angegeben!%n"
                            + "Die Eingabe muss eine gültige IPv4/IPv6 mit oder ohne Präfix sein!", (Object[]) null),
                    true
            );
        }
        else {
            try {
                if (filter instanceof IPAddressSearchQuery) {
                    IPAddressSearchQuery tmp = (IPAddressSearchQuery) filter;
                    List<IPAddressSearchView> view = getCCService(IPAddressService.class)
                            .filterIPsByBinaryRepresentation(tmp);
                    if (view.size() >= RESULT_LIMIT) {
                        MessageHelper
                                .showWarningDialog(
                                        getMainFrame(),
                                        String.format(
                                                "Es wurden mindestens %d Suchergebnisse gefunden, bitte schränken Sie die Suche weiter ein!%n"
                                                        + "(Die Anzeige ist auf %d Datensätze beschränkt.)",
                                                RESULT_LIMIT, RESULT_LIMIT
                                        ), true
                                );
                    }
                    tbModel.setData(view);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }

        return tbModel;
    }

    @Override
    public void updateGui(IPAddressSearchResultTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(RESULT_TABLE_COLUMN_SIZES);
    }

    @Override
    public void clearFilter() {
        GuiTools.cleanFields(this);
    }

}
