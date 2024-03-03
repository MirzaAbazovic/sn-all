/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015
 */
package de.augustakom.hurrican.gui.sip.peering;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.annotation.*;
import javax.swing.*;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.wholesale.EditDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;
import de.mnet.common.tools.DateConverterUtils;

class PeeringPartnerAdminPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(PeeringPartnerAdminPanel.class);

    private static final long serialVersionUID = 8387623892941634670L;

    public static final String NEW_PEERING_PARTNER = "new.peering.partner";
    public static final String EDIT_PEERING_PARTNER = "edit.peering.partner";
    public static final String PEERING_PARTNER_ADMIN_PANEL =
            "de/augustakom/hurrican/gui/sip/peering/resources/PeeringPartnerAdminPanel.xml";
    public static final String PEERING_PARTNER_EDIT_DIALOG =
            "de/augustakom/hurrican/gui/sip/peering/resources/PeeringPartnerEditDialog.xml";

    private SipPeeringPartnerService peeringPartnerService;

    private AKJButton btnNewPeeringPartner;
    private AKJButton btnEditPeeringPartner;

    private PeeringPartnerTableModel ppTableModel;
    private AKJTable ppTable;

    private AKJButton btnNewIpSet;
    private AKJButton btnDelIpSet;
    private AKJButton btnCpsInfo;
    private AKJButton btnCps;
    private AKJNavigationBar navigationBar;
    private AKJTextField tfIpSetGueltigAb;

    private IpSetTableModel ipSetTableModel;

    public PeeringPartnerAdminPanel() {
        super(PEERING_PARTNER_ADMIN_PANEL);
        try {
            peeringPartnerService = getCCService(SipPeeringPartnerService.class);
        }
        catch (ServiceNotFoundException e) {
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            return;
        }

        createGUI();
        createLogic();
    }

    // GUI layout:

    @Override
    protected final void createGUI() {
        setLayout(new GridBagLayout());

        final AKJPanel peeringPartnerPanel = createPeeringPartnerPanel();
        final AKJPanel ipSetPanel = createIpSetPanel();

        // @formatter:off
        add(peeringPartnerPanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        add(ipSetPanel,          GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    private AKJPanel createPeeringPartnerPanel() {
        final AKJPanel ppPanel = new AKJPanel();
        ppPanel.setLayout(new GridBagLayout());
        ppPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("peering.partners.title")));

        final AKJPanel ppEditPanel = createPeeringPartnerEditPanel();
        final AKJScrollPane ppScrollPane = createPeeringPartnerTableScrollPane();

        // @formatter:off
        ppPanel.add(ppEditPanel,  GBCFactory.createGBC(100,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ppPanel.add(ppScrollPane, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        return ppPanel;
    }

    private AKJPanel createPeeringPartnerEditPanel() {
        btnNewPeeringPartner = getSwingFactory().createButton(NEW_PEERING_PARTNER);
        btnEditPeeringPartner = getSwingFactory().createButton(EDIT_PEERING_PARTNER);
        final AKJPanel editPanel = new AKJPanel(new GridBagLayout());

        // @formatter:off
        editPanel.add(btnNewPeeringPartner,  GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(btnEditPeeringPartner, GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(new AKJPanel(),        GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        return editPanel;
    }

    private AKJScrollPane createPeeringPartnerTableScrollPane() {
        ppTableModel = new PeeringPartnerTableModel();
        ppTable = new AKJTable(ppTableModel, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        ppTable.attachSorter();
        return new AKJScrollPane(ppTable, new Dimension(520, 150));
    }

    private AKJPanel createIpSetPanel() {
        final AKJPanel ipSetPanel = new AKJPanel();
        ipSetPanel.setLayout(new GridBagLayout());
        ipSetPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("ip.sets.title")));

        final AKJPanel ipSetEditPanel = createIpSetEditPanel();
        final AKJScrollPane ipSetTable = createIpSetTableScrollPane();

        // @formatter:off
        ipSetPanel.add(ipSetEditPanel, GBCFactory.createGBC(100,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ipSetPanel.add(ipSetTable,     GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        return ipSetPanel;
    }

    private AKJPanel createIpSetEditPanel() {
        btnNewIpSet = getSwingFactory().createButton("new.ip.set");
        btnDelIpSet = getSwingFactory().createButton("del.ip.set");
        btnCpsInfo = getSwingFactory().createButton("cps.info");
        btnCps = getSwingFactory().createButton("cps");

        final AKJLabel lblIpSetGueltigAb = getSwingFactory().createLabel("ip.set.gueltig.ab");
        tfIpSetGueltigAb = getSwingFactory().createTextField("ip.set.gueltig.ab");
        tfIpSetGueltigAb.setEditable(false);

        navigationBar = new AKJNavigationBar(null, true, false);
        final AKJPanel editPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        editPanel.add(btnNewIpSet,       GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(btnDelIpSet,       GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(navigationBar,     GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(lblIpSetGueltigAb, GBCFactory.createGBC(  0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(tfIpSetGueltigAb,  GBCFactory.createGBC(  0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(new AKJPanel(),    GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(btnCpsInfo,        GBCFactory.createGBC(  0, 0, 7, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(btnCps,            GBCFactory.createGBC(  0, 0, 8, 0, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
        return editPanel;
    }

    private AKJScrollPane createIpSetTableScrollPane() {
        ipSetTableModel = new IpSetTableModel();
        final AKJTable ipSetTable = new AKJTable(ipSetTableModel, AKJTable.AUTO_RESIZE_ALL_COLUMNS,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        return new AKJScrollPane(ipSetTable, new Dimension(520, 190));
    }

    // GUI logic:

    private void createLogic() {
        btnNewPeeringPartner.addActionListener(this::handleBtnNewPeeringPartnerAction);
        btnEditPeeringPartner.addActionListener(this::handleBtnEditPeeringPartnerAction);
        ppTable.addTableListener(o -> resetIpSetTable());

        btnNewIpSet.addActionListener(this::handleBtnNewIpSetAction);
        btnDelIpSet.addActionListener(this::handleBtnDelIpSetAction);
        btnCpsInfo.addActionListener(this::cpsInfoSubscriber);
        btnCps.addActionListener(this::cpsModifySubscriber);
        navigationBar.addNavigationListener((object, number) -> {
            if (object instanceof SipSbcIpSet)
                showIpSet((SipSbcIpSet) object);
        });

        resetPeeringPartnerTable();
        resetIpSetTable();
    }

    private static class PeeringPartnerTableModel extends AKReflectionTableModel<SipPeeringPartner> {
        private static final long serialVersionUID = 8472222142554591658L;

        public PeeringPartnerTableModel() {
            super(
                    new String[] { "Name", "Aktiv" },
                    new String[] { "name", "isActive" },
                    new Class[] { String.class, Boolean.class });
        }
    }

    private Optional<SipPeeringPartner> getSelectedPeeringPartner() {
        final int selectedRow = ppTable.getSelectedRow();
        return (selectedRow >= 0)
                ? Optional.of((SipPeeringPartner) ((AKMutableTableModel) ppTable.getModel()).getDataAtRow(selectedRow))
                : Optional.empty();
    }

    // NOSONAR squid:S1172
    private void handleBtnNewPeeringPartnerAction(ActionEvent event) {
        final SipPeeringPartner newPeeringPartner = new SipPeeringPartner();
        newPeeringPartner.setIsActive(true);
        final EditDialog editDialog =
                new EditDialog("de/augustakom/hurrican/gui/sip/peering/resources/PeeringPartnerNewDialog.xml",
                        newPeeringPartner, ImmutableList.of("name", "isActive"));
        final AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        final Object result = DialogHelper.showDialog(mainFrame, editDialog, true, true);
        if (!(result instanceof SipPeeringPartner)) {
            return;
        }

        final SipPeeringPartner added = (SipPeeringPartner) result;
        final Optional<SipPeeringPartner> otherWithEqualName = ppTableModel.getData().stream()
                .filter(pp -> pp.getName().equals(added.getName()))
                .findFirst();

        if (!otherWithEqualName.isPresent()) {
            peeringPartnerService.savePeeringPartner(added);
        }
        else {
            showWarningPeeringPartnerWithNameAlreadyExists(added);
        }
        resetPeeringPartnerTable();
    }

    // NOSONAR squid:S1172
    private void handleBtnEditPeeringPartnerAction(ActionEvent event) {
        final Optional<SipPeeringPartner> selectedOptional = getSelectedPeeringPartner();
        if (!selectedOptional.isPresent()) {
            return;
        }

        final SipPeeringPartner selected = selectedOptional.get();
        final EditDialog editDialog =
                new EditDialog(PEERING_PARTNER_EDIT_DIALOG,
                        selected, ImmutableList.of("name", "isActive"));
        final AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        final Object result = DialogHelper.showDialog(mainFrame, editDialog, true, true);
        if (!(result instanceof SipPeeringPartner)) {
            return;
        }

        final SipPeeringPartner edited = (SipPeeringPartner) result;
        final Optional<SipPeeringPartner> otherWithEqualName = ppTableModel.getData().stream()
                .filter(pp -> !pp.getId().equals(selected.getId()))
                .filter(pp -> pp.getName().equals(edited.getName()))
                .findFirst();

        if (!otherWithEqualName.isPresent()) {
            peeringPartnerService.savePeeringPartner(edited);
        }
        else {
            showWarningPeeringPartnerWithNameAlreadyExists(edited);
        }
        resetPeeringPartnerTablePreserveSelection();
    }

    private void showWarningPeeringPartnerWithNameAlreadyExists(@Nonnull SipPeeringPartner peeringPartner) {
        final String text = getSwingFactory().getText("warning.peering.partner.already.exists");
        MessageHelper.showWarningDialog(getMainFrame(), String.format(text, peeringPartner.getName()), true);
    }

    private void resetPeeringPartnerTable() {
        ppTableModel.setData(peeringPartnerService.findAllPeeringPartner(null));
    }

    private void resetPeeringPartnerTablePreserveSelection() {
        final Optional<String> nameOptional = getSelectedPeeringPartner().map(SipPeeringPartner::getName);
        resetPeeringPartnerTable();

        nameOptional.ifPresent(name -> {
            final int row = ppTableModel.findFirstRowBy(pp -> pp.getName().equals(name));
            ppTable.setRowSelectionInterval(row, row);
        });
    }

    private void resetIpSetTable() {
        final Optional<SipPeeringPartner> selectedOptional = getSelectedPeeringPartner();
        if (!selectedOptional.isPresent()) {
            clearIpSet();
            return;
        }

        final SipPeeringPartner selected = selectedOptional.get();
        final Optional<SipSbcIpSet> currentIpSetOptional = findCurrentIpSet(selected);
        if (!currentIpSetOptional.isPresent()) {
            clearIpSet();
            return;
        }

        final SipSbcIpSet currentIpSet = currentIpSetOptional.get();
        navigateToIpSetOf(currentIpSet, selected);
    }

    private void navigateToIpSetOf(@Nonnull SipSbcIpSet ipSet, @Nonnull SipPeeringPartner peeringPartner) {
        final List<SipSbcIpSet> ipSets = peeringPartner.getSbcIpSets();
        ipSets.sort((s1, s2) -> s1.getGueltigAb().compareTo(s2.getGueltigAb()));

        navigationBar.setData(ipSets);
        navigationBar.navigateTo(ipSets.indexOf(ipSet));
    }

    private static Optional<SipSbcIpSet> findCurrentIpSet(@Nonnull SipPeeringPartner peeringPartner) {
        final List<SipSbcIpSet> ipSets = peeringPartner.getSbcIpSets();
        ipSets.sort((s1, s2) -> s1.getGueltigAb().compareTo(s2.getGueltigAb()));

        SipSbcIpSet latest = null;
        final Date now = new Date();
        for (final SipSbcIpSet ipSet : ipSets) {
            if (latest != null && DateTools.isDateAfter(ipSet.getGueltigAb(), now)) {
                break;
            }
            else {
                latest = ipSet;
            }
        }
        return Optional.ofNullable(latest);
    }

    private void clearIpSet() {
        btnDelIpSet.setEnabled(false);
        navigationBar.setData(null);
        tfIpSetGueltigAb.setText("");
        ipSetTableModel.setData(Collections.emptyList());
    }

    private void showIpSet(@Nonnull SipSbcIpSet ipSet) {
        btnDelIpSet.setEnabled(DateTools.isDateAfter(ipSet.getGueltigAb(), new Date()));
        btnCps.setEnabled(DateTools.isDateAfterOrEqual(new Date(), ipSet.getGueltigAb()));
        final LocalDateTime ipSetGueltigAb = DateConverterUtils.asLocalDateTime(ipSet.getGueltigAb());
        tfIpSetGueltigAb.setText(ipSetGueltigAb.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        ipSetTableModel.setData(ipSet.getSbcIps());
    }

    // NOSONAR squid:S1172
    private void handleBtnNewIpSetAction(ActionEvent event) {
        final Optional<SipPeeringPartner> selectedOptional = getSelectedPeeringPartner();
        if (!selectedOptional.isPresent()) {
            return;
        }

        final Object result = DialogHelper.showDialog(getMainFrame(), new NewIpSetDialog(), true, true);
        if (!(result instanceof SipSbcIpSet)) {
            return;
        }

        final SipSbcIpSet newIpSet = (SipSbcIpSet) result;
        if (newIpSet.getSbcIps().isEmpty()) {
            return;
        }

        final SipPeeringPartner selected = selectedOptional.get();
        selected.addSbcIpSet(newIpSet);
        try {
            peeringPartnerService.savePeeringPartnerWithValidation(selected);
        }
        catch (StoreException e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        resetPeeringPartnerTablePreserveSelection();
        resetIpSetTable();
    }

    // NOSONAR squid:S1172
    private void handleBtnDelIpSetAction(ActionEvent event) {
        final int result = MessageHelper.showConfirmDialog(HurricanSystemRegistry.instance().getMainFrame(),
                getSwingFactory().getText("confirm.del.ip.set"));
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        final Optional<SipPeeringPartner> selectedOptional = getSelectedPeeringPartner();
        if (!selectedOptional.isPresent()) {
            return;
        }

        final Object objectAt = navigationBar.getNavigationObjectAt(navigationBar.getNavPosition());
        if (!(objectAt instanceof SipSbcIpSet)) {
            return;
        }

        final SipPeeringPartner selected = selectedOptional.get();
        final SipSbcIpSet ipSet = (SipSbcIpSet) objectAt;

        selected.getSbcIpSets().remove(ipSet);
        try {
            peeringPartnerService.savePeeringPartnerWithValidation(selected);
        }
        catch (StoreException e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        resetPeeringPartnerTablePreserveSelection();
        resetIpSetTable();
    }

    private Optional<Pair<List<AuftragDaten>, Long>> auftragDatenToModify(Date now) throws FindException {
        Optional<Pair<List<AuftragDaten>, Long>> result = Optional.empty();
        final Optional<SipPeeringPartner> selectedPeeringPartner = getSelectedPeeringPartner();
        if (!selectedPeeringPartner.isPresent()) {
            return result;
        }

        final Object objectAt = navigationBar.getNavigationObjectAt(navigationBar.getNavPosition());
        if (!(objectAt instanceof SipSbcIpSet)) {
            return result;
        }

        Long peeringPartnerId = selectedPeeringPartner.get().getId();

        List<AuftragDaten> toModify = peeringPartnerService.findActiveOrdersAssignedToPeeringPartner(peeringPartnerId,
                now);
        if (toModify == null || toModify.isEmpty()) {
            return result;
        }
        return Optional.of(new Pair<>(toModify, peeringPartnerId));
    }

    // NOSONAR squid:S1172
    private void cpsInfoSubscriber(ActionEvent event) {
        try {
            Date now = new Date();
            Optional<Pair<List<AuftragDaten>, Long>> toModify = auftragDatenToModify(now);
            if (!toModify.isPresent()) {
                MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("cps.no.orders"), null, true);
                return;
            }

            String message = toModify.get().getFirst()
                    .stream()
                    .map(AuftragDaten::getAuftragId)
                    .map(Object::toString)
                    .reduce("", (String a, String b) -> {
                        if (a.isEmpty())
                            return b;
                        else
                            return a + ", " + b;
                    });
            MessageHelper.showInfoDialog(getMainFrame(),
                    getSwingFactory().getText("cps.info.result.message", message),
                    getSwingFactory().getText("cps.info.result.title"), null, true);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    // NOSONAR squid:S1172
    private void cpsModifySubscriber(ActionEvent event) {
        try {
            Date now = new Date();
            Optional<Pair<List<AuftragDaten>, Long>> toModify = auftragDatenToModify(now);
            if (!toModify.isPresent()) {
                MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("cps.no.orders"), null, true);
                return;
            }

            // Nachfrage, ob CPS modifySub wirklich durchgefuehrt werden soll
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    getSwingFactory().getText("confirm.cps.modify", toModify.get().getFirst().size()),
                    getSwingFactory().getText("confirm.cps.modify.title"));

            if (option == JOptionPane.YES_OPTION) {

                final SwingWorker<AKWarnings, Void> cpsWorker = new SwingWorker<AKWarnings, Void>() {
                    @Override
                    protected AKWarnings doInBackground() throws Exception {
                        setWaitCursor();
                        showProgressBar(getSwingFactory().getText("cps.running"));

                        // modifySub durchfuehren
                        return peeringPartnerService.createAndSendCpsTxForAssignedOrders(
                                toModify.get().getSecond(), now, HurricanSystemRegistry.instance().getSessionId());
                    }

                    @Override
                    protected void done() {
                        try {
                            stopProgressBar();
                            MessageHelper.showInfoDialog(getMainFrame(),
                                    getSwingFactory().getText("cps.result.message", get().getWarningsAsText()),
                                    getSwingFactory().getText("cps.result.title"), null, true);
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
                };
                cpsWorker.execute();
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    //

    @Override
    protected void execute(String command) {
        // intentionally left blank
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }
}
