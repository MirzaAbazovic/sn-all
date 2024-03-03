/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.2012 11:13:43
 */
package de.augustakom.hurrican.gui.auftrag.mvs;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceDialog;

/**
 * Dialog zur Anzeige von Domains. Eine Tabelle mit einer Spalte.
 *
 *
 * @since Release 11
 */
class AuftragMVSDomainsDialog extends AbstractServiceDialog {

    private static final long serialVersionUID = -6168487508028978757L;
    private final AuftragMVSDomainTableModel tableModel;

    /**
     * @param resource
     * @throws ServiceNotFoundException
     */
    AuftragMVSDomainsDialog(String tableColumnName, Collection<String> domains) {
        super("de/augustakom/hurrican/gui/auftrag/mvs/resources/AuftragMVSDomainsDialog.xml");
        tableModel = new AuftragMVSDomainTableModel(tableColumnName);
        createGUI();
        setData(domains);
    }

    protected void setData(Collection<String> domains) {
        tableModel.setData(domains);
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJTable table = new AKJTable(tableModel);
        table.attachSorter();

        AKJScrollPane spTable = new AKJScrollPane(table);
        spTable.setPreferredSize(new Dimension(200, 400));

        setLayout(new BorderLayout());
        add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

} // end
