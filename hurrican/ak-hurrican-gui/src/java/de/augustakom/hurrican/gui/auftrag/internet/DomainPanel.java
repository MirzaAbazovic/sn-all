/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2004 08:51:12
 */
package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.auftrag.shared.IntDomainTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.billing.view.IntDomain;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.billing.DomainService;


/**
 * Panel fuer die Darstellung der Domains, die einem best. Auftrag zugeordnet sind.
 *
 *
 */
public class DomainPanel extends AbstractDataPanel implements AKModelOwner {

    private static final Logger LOGGER = Logger.getLogger(DomainPanel.class);

    private IntDomainTableModel tbMdlDomains = null;

    private CCAuftragModel model = null;

    /**
     * Konstruktor.
     */
    public DomainPanel() {
        super(null);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        tbMdlDomains = new IntDomainTableModel();
        AKJTable tbDomains = new AKJTable(tbMdlDomains, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbDomains.attachSorter();
        tbDomains.fitTable(new int[] { 150, 80, 80 });
        AKJScrollPane spDomains = new AKJScrollPane(tbDomains);
        spDomains.setPreferredSize(new Dimension(350, 100));

        this.setLayout(new BorderLayout());
        this.add(spDomains, BorderLayout.CENTER);
    }

    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof CCAuftragModel) ? (CCAuftragModel) model : null;
        readModel();
    }

    @Override
    public void readModel() {
        tbMdlDomains.setData(null);
        if (this.model != null) {
            try {
                setWaitCursor();
                DomainService ds = getBillingService(DomainService.class);
                List<IntDomain> domains = ds.findDomains4Auftrag(model.getAuftragId());
                tbMdlDomains.setData(domains);
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
    public void saveModel() throws AKGUIException {
        // Panel nur zur Anzeige!!!
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


