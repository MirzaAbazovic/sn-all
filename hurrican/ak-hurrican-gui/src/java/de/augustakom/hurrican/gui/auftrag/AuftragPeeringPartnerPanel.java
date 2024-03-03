/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2015
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;

/**
 *
 */
public class AuftragPeeringPartnerPanel extends AbstractAuftragPanel implements AKDataLoaderComponent,
        AKTableOwner {

    private static final long serialVersionUID = 6748686027398693833L;

    public static final String ADD_PEERING_PARTNER = "add.peering.partner";
    public static final String DEACTIVATE_PEERING_PARTNER = "deactivate.peering.partner";

    private AKJTable tbPeeringPartner;
    private AKReferenceAwareTableModel<Auftrag2PeeringPartner> tbMdlPeeringPartner;
    private AKReflectionTableModel<IPAddress> tbMdlSbc;
    private AKJButton btnAddPeeringPartner;
    private AKJButton btnDeactivatePeeringPartner;

    private CCAuftragModel auftragModel = null;
    private boolean initialized = false;

    private SipPeeringPartnerService sipPeeringPartnerService;


    public AuftragPeeringPartnerPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragPeeringPartnerPanel.xml");
        createGUI();
        init();
    }

    @Override
    protected final void createGUI() {
        AKJPanel peeringPartnerPnael = createPeeringPartnerPanel();
        AKJPanel sbcPanel = createSbcPanel();

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(peeringPartnerPnael, GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(sbcPanel           , GBCFactory.createGBC(100,100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:off

        manageGUI(btnAddPeeringPartner, btnDeactivatePeeringPartner );
    }

    private AKJPanel createPeeringPartnerPanel() {
        btnAddPeeringPartner = getSwingFactory().createButton(ADD_PEERING_PARTNER, getActionListener(), null);
        btnDeactivatePeeringPartner = getSwingFactory().createButton(DEACTIVATE_PEERING_PARTNER, getActionListener(), null);

        tbMdlPeeringPartner = new AKReferenceAwareTableModel<>(
                new String[]{"Peering Partner", "von", "bis"},
                new String[]{"peeringPartnerId", "gueltigVon", "gueltigBis"},
                new Class[]{String.class, Date.class, Date.class});
        tbPeeringPartner = new AKJTable(tbMdlPeeringPartner, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        tbPeeringPartner.attachSorter();
        tbPeeringPartner.addTableListener(this);
        tbPeeringPartner.fitTable(new int[] { 200, 100, 100 });
        AKJScrollPane spTable = new AKJScrollPane(tbPeeringPartner, new Dimension(450, 250));

        // @formatter:off
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnAddPeeringPartner       , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnDeactivatePeeringPartner, GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel()             , GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        AKJPanel peeringPartnerPnael = new AKJPanel(new BorderLayout(), getSwingFactory().getText("peering.partner"));
        peeringPartnerPnael.add(spTable, BorderLayout.CENTER);
        peeringPartnerPnael.add(btnPanel, BorderLayout.WEST);
        return peeringPartnerPnael;
    }

    private AKJPanel createSbcPanel() {
        tbMdlSbc = new AKReferenceAwareTableModel<>(
                new String[] { "IP-Adresse", "Typ" },
                new String[] { "address", "ipType" },
                new Class[] { String.class, AddressTypeEnum.class });
        AKJTable tbSbc = new AKJTable(tbMdlSbc, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSbc.attachSorter();
        tbSbc.fitTable(new int[] { 250, 150 });
        AKJScrollPane spTable = new AKJScrollPane(tbSbc, new Dimension(350, 250));

        AKJPanel sbc = new AKJPanel(new BorderLayout(), getSwingFactory().getText("sbc"));
        sbc.add(spTable, BorderLayout.CENTER);
        return sbc;
    }

    private void initServices() throws Exception {
        sipPeeringPartnerService = getCCService(SipPeeringPartnerService.class);
    }

    /* Initialisiert das Panel (Reference-Felder erhalten Find-Parameter) */
    private void init() {
        try {
            if (!initialized) {
                initialized = true;
                initServices();

                List<SipPeeringPartner> peeringPartners = sipPeeringPartnerService.findAllPeeringPartner(null);
                tbMdlPeeringPartner.addReference(0,
                        CollectionMapConverter.convert2Map(peeringPartners, "getId", null),
                        "name");
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        GuiTools.cleanFields(this);
        tbMdlPeeringPartner.setData(null);

        if (auftragModel == null) {
            return;
        }

        try {
            tbMdlPeeringPartner.setData(sipPeeringPartnerService.findAuftragPeeringPartners(auftragModel.getAuftragId()));
            selectCurrentPeeringPartner();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void selectCurrentPeeringPartner() {
        int currentRow = -1;
        final Date now = new Date();
        final Collection<Auftrag2PeeringPartner> data = tbMdlPeeringPartner.getData();
        int skip = 1;
        for (Auftrag2PeeringPartner auftrag2PeeringPartner : data) {
            if (DateTools.isDateEqual(auftrag2PeeringPartner.getGueltigVon(), auftrag2PeeringPartner.getGueltigBis())) {
                skip += 1;
            }
            else if (DateTools.isDateBeforeOrEqual(auftrag2PeeringPartner.getGueltigVon(), now)) {
                currentRow += skip;
                skip = 1;
            }
        }
        if (currentRow > -1) {
            tbPeeringPartner.selectAndScrollToRow(currentRow);
        }
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        this.auftragModel = null;
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
            loadData();
        }
    }

    @Override
    protected void execute(String command) {
        if (ADD_PEERING_PARTNER.equals(command)) {
            addPeeringPartner();
        }
        else if (DEACTIVATE_PEERING_PARTNER.equals(command)) {
            deactivatePeeringPartner();
        }
    }


    /**
     * Oeffnet einen Dialog, um dem Auftrag einen neuen Peering-Partner zuzuordnen.
     */
    private void addPeeringPartner() {
        try {
            List<SipPeeringPartner> peeringPartners = sipPeeringPartnerService.findAllPeeringPartner(Boolean.TRUE);
            Object peeringPartner = MessageHelper.showInputDialog(getMainFrame(),
                    peeringPartners,
                    new AKCustomListCellRenderer<>(SipPeeringPartner.class, SipPeeringPartner::getName),
                    getSwingFactory().getText("add.title"),
                    getSwingFactory().getText("add.message"),
                    getSwingFactory().getText("add.peering.partner"));

            if (peeringPartner instanceof SipPeeringPartner) {
                AKDateSelectionDialog dlg = new AKDateSelectionDialog(
                        getSwingFactory().getText("valid.from.title"),
                        getSwingFactory().getText("valid.from.sub.title"),
                        getSwingFactory().getText("valid.from.label"));

                Object dateResult = DialogHelper.showDialog(this, dlg, true, true);
                if (dateResult instanceof Date) {
                    Date validFrom = (Date) dateResult;

                    sipPeeringPartnerService.addAuftrag2PeeringPartner(this.auftragModel.getAuftragId(),
                            ((SipPeeringPartner) peeringPartner).getId(),
                            validFrom);

                    loadData();
                }
            }
        }
        catch (Exception e ) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /**
     * Deaktiviert den aktuell selektierten Peering-Partner zu einem bestimmten Datum (muss vom Benutzer
     * per Dialog angegeben werden).
     */
    private void deactivatePeeringPartner() {
        try {
            int row = tbPeeringPartner.getSelectedRow();
            if (row < 0) {
                return;
            }

            Auftrag2PeeringPartner selection = (Auftrag2PeeringPartner)
                    ((AKMutableTableModel) tbPeeringPartner.getModel()).getDataAtRow(row);
            if (selection == null) {
                throw new HurricanGUIException("Bitte wählen Sie den zu deaktivierenden Peering-Partner");
            }

            AKDateSelectionDialog dlg = new AKDateSelectionDialog(
                    getSwingFactory().getText("deactivate.title"),
                    getSwingFactory().getText("deactivate.sub.title"),
                    getSwingFactory().getText("deactivate.label"));

            Object result = DialogHelper.showDialog(this, dlg, true, true);
            if (result instanceof Date) {
                Date deactivate = (Date) result;
                selection.setGueltigBis(deactivate);

                sipPeeringPartnerService.saveAuftrag2PeeringPartnerButCheckOverlapping(selection);
            }
        }
        catch (Exception e ) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            loadData();
        }
    }

    @Override
    public void showDetails(Object details) {
        tbMdlSbc.setData(null);
        if (details instanceof Auftrag2PeeringPartner) {
            Auftrag2PeeringPartner a2pp = (Auftrag2PeeringPartner) details;

            SipPeeringPartner peeringPartner = sipPeeringPartnerService.findPeeringPartnerById(a2pp.getPeeringPartnerId());
            if (peeringPartner != null) {
                Optional<SipSbcIpSet> sbcIpSet = peeringPartner.getCurrentSbcIpSetAt(new Date());
                if (sbcIpSet.isPresent()) {
                    tbMdlSbc.setData(sbcIpSet.get().getSbcIps());
                }
            }
        }
    }

    @Override
    public void readModel() throws AKGUIException {

    }

    @Override
    public void saveModel() throws AKGUIException {

    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
