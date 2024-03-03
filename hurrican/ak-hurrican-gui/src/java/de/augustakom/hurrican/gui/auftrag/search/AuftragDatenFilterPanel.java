/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2004 09:44:00
 */
package de.augustakom.hurrican.gui.auftrag.search;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragDatenTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Filter-Panel fuer die Suche ueber Auftragsdaten.
 */
class AuftragDatenFilterPanel extends AbstractServicePanel implements IFilterOwner<Either<AuftragDatenQuery, AuftragSAPQuery>, AuftragDatenTableModel>, AKDataLoaderComponent {

    public static final String TF_CC_AUFTRAG_ID = "cc.auftrag.id";

    private static final Logger LOGGER = Logger.getLogger(AuftragDatenFilterPanel.class);

    private KeyListener searchKL = null;

    private AKJTextField tfAuftragId = null;
    private AKJTextField tfAuftragNoOrig = null;
    private AKJTextField tfBestelltNr = null;
    private AKJTextField tfLbzKunde = null;
    private AKJTextField tfIntAccount = null;
    private AKJTextField tfSapId = null;
    private AKJTextField tfSapDebId = null;
    private AKJComboBox cbAuftragStatus = null;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/AuftragDatenFilterPanel.xml";

    public AuftragDatenFilterPanel(KeyListener searchKL) {
        super(RESOURCE);
        this.searchKL = searchKL;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblAuftragId = getSwingFactory().createLabel(TF_CC_AUFTRAG_ID);
        AKJLabel lblAuftragNoOrig = getSwingFactory().createLabel("billing.auftrag.no");
        AKJLabel lblBestellNr = getSwingFactory().createLabel("bestell.nr");
        AKJLabel lblLbzKunde = getSwingFactory().createLabel("lbz.kunde");
        AKJLabel lblIntAccount = getSwingFactory().createLabel("int.account");
        AKJLabel lblSapId = getSwingFactory().createLabel("sap.id");
        AKJLabel lblSapDebId = getSwingFactory().createLabel("sap.debitor.id");
        AKJLabel lblAuftragStatus = getSwingFactory().createLabel("cc.auftrag.status");

        tfAuftragId = getSwingFactory().createTextField(TF_CC_AUFTRAG_ID, true, true, searchKL);
        tfAuftragNoOrig = getSwingFactory().createTextField("billing.auftrag.no", true, true, searchKL);
        tfBestelltNr = getSwingFactory().createTextField("bestell.nr", true, true, searchKL);
        tfLbzKunde = getSwingFactory().createTextField("lbz.kunde", true, true, searchKL);
        tfIntAccount = getSwingFactory().createTextField("int.account", true, true, searchKL);
        tfSapId = getSwingFactory().createTextField("sap.id", true, true, searchKL);
        tfSapDebId = getSwingFactory().createTextField("sap.deb.id", true, true, searchKL);
        cbAuftragStatus = getSwingFactory().createComboBox("cc.auftrag.status");
        cbAuftragStatus.setMaximumSize(new Dimension(10, 10));
        cbAuftragStatus.setRenderer(new AKCustomListCellRenderer<>(AuftragStatus.class, AuftragStatus::getStatusText));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblAuftragId, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfAuftragId, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAuftragNoOrig, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfAuftragNoOrig, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBestellNr, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBestelltNr, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAuftragStatus, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbAuftragStatus, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL, -50));
        left.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        right.add(lblLbzKunde, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfLbzKunde, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblIntAccount, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfIntAccount, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblSapId, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfSapId, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblSapDebId, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfSapDebId, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(right, GBCFactory.createGBC(0, 100, 2, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    public final void loadData() {
        try {
            CCAuftragService svc = getCCService(CCAuftragService.class);
            List<AuftragStatus> statusList = svc.findAuftragStati();
            cbAuftragStatus.addItems(statusList, true, AuftragStatus.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    public Either<AuftragDatenQuery, AuftragSAPQuery> getFilter() throws HurricanGUIException {
        AuftragDatenQuery auftragDatenQuery = new AuftragDatenQuery();
        auftragDatenQuery.setAuftragId(tfAuftragId.getTextAsLong(null));
        auftragDatenQuery.setAuftragNoOrig(tfAuftragNoOrig.getTextAsLong(null));
        auftragDatenQuery.setBestellNr(tfBestelltNr.getText());
        auftragDatenQuery.setLbzKunde(tfLbzKunde.getText());
        auftragDatenQuery.setIntAccount(tfIntAccount.getText());
        auftragDatenQuery.setAuftragStatusId((cbAuftragStatus.getSelectedItem() instanceof AuftragStatus)
                ? ((AuftragStatus) cbAuftragStatus.getSelectedItem()).getId() : null);

        if (WildcardTools.containsWildcard(auftragDatenQuery.getIntAccount())) {
            throw new HurricanGUIException("Die Suche über Wildcards (z.B. *, ?) ist im Feld Account nicht zulässig!");
        }

        AuftragSAPQuery sapQuery = new AuftragSAPQuery();
        sapQuery.setNurAktuelle(true);
        sapQuery.setSapId(tfSapId.getText(null));
        sapQuery.setSapDebitorId(tfSapDebId.getText(null));

        // Fehlermeldung, falls beide Query-Bereiche definiert sind
        if (!auftragDatenQuery.isEmpty() && !sapQuery.isEmpty()) {
            throw new HurricanGUIException("Falls nach einer SAP-Id gesucht wird, dürfen die restlichen Suchfelder nicht gefüllt sein.");
        }

        if (!auftragDatenQuery.isEmpty()) {
            return Either.left(auftragDatenQuery);
        }
        else {
            return Either.right(sapQuery);
        }

    }

    @Override
    public AuftragDatenTableModel doSearch(Either<AuftragDatenQuery, AuftragSAPQuery> query) throws HurricanGUIException {
        try {
            CCAuftragService service = getCCService(CCAuftragService.class);
            List<AuftragDatenView> result = null;
            if (query.isRight()) {
                result = service.findAuftragDatenViews(query.getRight());
            }
            else {
                result = service.findAuftragDatenViews(query.getLeft(), true);
            }

            AuftragDatenTableModel tbModel = new AuftragDatenTableModel();
            tbModel.setData(result);
            return tbModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AuftragDatenTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 70, 70, 70, 110, 110, 120, 120, 80, 70, 35, 25, 70, 70 });

    }

    @Override
    public void clearFilter() {
        GuiTools.cleanFields(this);
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}

