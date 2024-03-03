/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.13 08:23
 */
package de.augustakom.hurrican.gui.auftrag.voip;

import java.awt.*;
import java.beans.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableSingleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.wholesale.EditDialog;
import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.VoipDnPlanValidationService;

/**
 *
 */
public class DnPlanPanel extends AKJPanel implements AKObjectSelectionListener {
    private static final long serialVersionUID = -5351980768586217477L;

    private static final String DD_MM_YYYY = "dd.MM.yyyy";
    private final AKJNavigationBar navBar;
    private final AKReflectionTableModel<VoipDn2DnBlockView> tbmDnBlock;
    private final AKJTextField tfDnPlanGueltigAb;
    private final AKJButton btnNewDnPlan;
    private final AKJButton btnDelDnPlan;
    private AuftragVoipDNView selectedAuftragVoipDnView;
    private VoipDnPlanView selectedPlan;
    private VoipLoginPanel voipLoginPnl;

    public DnPlanPanel(final SwingFactory swingFactory, final VoipDnPlanValidationService voipDnPlanValidationService,
            final AuftragVoIPPanel.DoSaveCallback doSaveCallback) {
        super(new GridBagLayout());
        navBar = new AKJNavigationBar(null, true, false);
        navBar.addNavigationListener(new AKNavigationBarListener() {
            @Override
            public void showNavigationObject(final Object obj, final int number) throws PropertyVetoException {
                if (obj instanceof VoipDnPlanView) {
                    selectedPlan = (VoipDnPlanView) obj;
                }
                if (selectedPlan != null) {
                    tfDnPlanGueltigAb.setText(Instant.ofEpochMilli(selectedPlan.getGueltigAb().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime()
                            .format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
                    tbmDnBlock.setData(selectedPlan.getVoipDn2DnBlockViews());
                    btnDelDnPlan.setEnabled(DateTools.isDateAfterOrEqual(selectedPlan.getGueltigAb(), new Date()));
                    voipLoginPnl.objectSelected(selectedPlan);
                }
            }
        });
        tbmDnBlock = new TableModelDnBlock();
        final AKJTable dnPlanTable = new AKJTable(tbmDnBlock, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        dnPlanTable.attachSorter();
        final AKJScrollPane dnPlanTblScrollPane = new AKJScrollPane(dnPlanTable, new Dimension(500, 140));

        final AKJLabel lblDnPlanGueltigAb = swingFactory.createLabel("dnplan.gueltig.ab");
        tfDnPlanGueltigAb = swingFactory.createTextField("dnplan.gueltig.ab");
        tfDnPlanGueltigAb.setEditable(false);

        btnNewDnPlan = swingFactory.createButton("new.dn.plan", e -> {
            if (selectedAuftragVoipDnView.getSipDomain() == null) {
                MessageHelper.showInfoDialog(GUISystemRegistry.instance().getMainFrame(), "Da die SIP-Domäne nicht "
                        + "konfiguriert ist, können SIP-Login sowie SIP-Hauptrufnummer nicht generiert werden. Der "
                        + "Vorgang kann nicht fortgesetzt werden!", null, true);
                return; // Hier geht's nicht weiter
            }
            final Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    new NewDnPlanDialog(selectedAuftragVoipDnView, voipDnPlanValidationService, doSaveCallback, voipLoginPnl), true, true);
            if (result instanceof VoipDnPlanView) {
                objectSelected(selectedAuftragVoipDnView);
                navBar.navigateToFirst();
            }
        });
        btnNewDnPlan.setEnabled(false);

        btnDelDnPlan = swingFactory.createButton("del.dn.plan", e -> {
            final int result = MessageHelper.showConfirmDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    "Soll der Rufnummernplan wirklich gelöscht werden?");
            if (result == JOptionPane.YES_OPTION) {
                try {
                    selectedAuftragVoipDnView.removeVoipDnPlanView(selectedPlan);
                    doSaveCallback.execute(selectedAuftragVoipDnView);
                    objectSelected(selectedAuftragVoipDnView);
                }
                catch (Exception exc) {
                    MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), exc);
                }
            }
        });
        btnDelDnPlan.setEnabled(false);

        voipLoginPnl = new VoipLoginPanel(swingFactory);

        final AKJPanel navigationPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        navigationPanel.add(btnNewDnPlan        , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        navigationPanel.add(btnDelDnPlan        , GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        navigationPanel.add(navBar              , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        navigationPanel.add(lblDnPlanGueltigAb  , GBCFactory.createGBC(  0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        navigationPanel.add(tfDnPlanGueltigAb   , GBCFactory.createGBC(  0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        navigationPanel.add(new AKJPanel()      , GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setBorder(BorderFactory.createTitledBorder("VoIP-Rufnummernplan"));
        this.add(navigationPanel    , GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()     , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(voipLoginPnl       , GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()     , GBCFactory.createGBC(  0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(dnPlanTblScrollPane, GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()     , GBCFactory.createGBC(  0, 0, 1, 3, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
    }

    public void reset() {
        GuiTools.cleanFields(this);
        btnNewDnPlan.setEnabled(false);
        navBar.setData(null);
        tbmDnBlock.setData(null);
    }

    @Override
    public void objectSelected(final Object selection) {
        tfDnPlanGueltigAb.setText("");
        if (selection instanceof AuftragVoipDNView && ((AuftragVoipDNView) selection).isBlock()) {
            final AuftragVoipDNView selectedRow = (AuftragVoipDNView) selection;
            navBar.setData(selectedRow.getVoipDnPlanViewsDesc());
            navBar.navigateToFirst();
            selectedAuftragVoipDnView = selectedRow;
            btnNewDnPlan.setEnabled(true);
        }
        else {
            tbmDnBlock.setData(Collections.<VoipDn2DnBlockView>emptySet());
            selectedAuftragVoipDnView = null;
            btnNewDnPlan.setEnabled(false);
            btnDelDnPlan.setEnabled(false);
        }

        voipLoginPnl.objectSelected(selection);
    }

    private static class TableModelDnBlock extends AKReflectionTableModel<VoipDn2DnBlockView> {
        private static final long serialVersionUID = 1L;

        public TableModelDnBlock() {
            super(
                    new String[] { "ONKZ", "DN-Base", "Start", "Ende", "Zentrale" },
                    new String[] { "onkz", "dnBase", "anfang", "ende", "zentrale" },
                    new Class[] { String.class, String.class, String.class, String.class, Boolean.class });
        }
    }

    private static class NewDnPlanDialog extends AbstractServiceOptionDialog {
        private static final long serialVersionUID = 1L;

        private final AKJButton btnDelBlock;
        private final AKJButton btnNewBlock;
        private final AKReflectionTableModel<VoipDn2DnBlockView> tbmDnBlock;
        private final VoipDnPlanView newVoipDnPlanView;
        private final AKJDateComponent dpDnPlanGueltigAb;
        private final Date todayMidnight = getTodayMidnight();
        private final AuftragVoIPPanel.DoSaveCallback doSaveCallback;
        private final AuftragVoipDNView selectedAuftragVoipDnView;
        private final VoipLoginPanel voipLoginPnl;
        private final VoipDnPlanValidationService voipDnPlanValidationService;
        private VoipDn2DnBlockView selectedVoipDn2DnBlockView;

        public NewDnPlanDialog(final AuftragVoipDNView selectedAuftragVoipDnView,
                final VoipDnPlanValidationService voipDnPlanValidationService,
                final AuftragVoIPPanel.DoSaveCallback doSaveCallback, VoipLoginPanel voipLoginPnl) {
            super("de/augustakom/hurrican/gui/auftrag/voip/resources/AuftragVoIPPanel.xml");
            this.selectedAuftragVoipDnView = selectedAuftragVoipDnView;
            this.newVoipDnPlanView = new VoipDnPlanView(selectedAuftragVoipDnView.getOnKz(),
                    selectedAuftragVoipDnView.getDnBase(), new VoipDnPlan());
            this.voipDnPlanValidationService = voipDnPlanValidationService;
            this.doSaveCallback = doSaveCallback;
            this.voipLoginPnl = voipLoginPnl;

            tbmDnBlock = new TableModelDnBlock();
            final AKJTable dnPlanTable = new AKJTable(tbmDnBlock, AKJTable.AUTO_RESIZE_OFF,
                    ListSelectionModel.SINGLE_SELECTION);
            dnPlanTable.attachSorter();
            dnPlanTable.addMouseListener(new AKTableSingleClickMouseListener(new AKObjectSelectionListener() {
                @Override
                public void objectSelected(final Object selection) {
                    if (selection == null) {
                        deselectVoipDn2DnBlockView();
                    }
                    if (selection instanceof VoipDn2DnBlockView) {
                        btnDelBlock.setEnabled(true);
                        selectedVoipDn2DnBlockView = (VoipDn2DnBlockView) selection;
                    }
                }
            }));
            final AKJScrollPane dnPlanTblScrollPane = new AKJScrollPane(dnPlanTable, new Dimension(500, 180));

            final AKJLabel lblDnPlanGueltigAb = getSwingFactory().createLabel("dnplan.gueltig.ab");
            dpDnPlanGueltigAb = getSwingFactory().createDateComponent("dnplan.gueltig.ab");
            dpDnPlanGueltigAb.setDate(todayMidnight);
            dpDnPlanGueltigAb.setDateFormat(new SimpleDateFormat(DD_MM_YYYY));

            btnNewBlock = getSwingFactory().createButton("new.dn.block", e -> {
                final VoipDn2DnBlockView toCreate =
                        new VoipDn2DnBlockView(selectedAuftragVoipDnView.getOnKz(), selectedAuftragVoipDnView
                                .getDnBase());

                final EditDialog editDialog =
                        new EditDialog("de/augustakom/hurrican/gui/auftrag/voip/resources/EditRnPlanDialog.xml",
                                toCreate, ImmutableList.of("anfang", "ende", "zentrale"));
                final Object result =
                        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), editDialog, true,
                                true);
                if (result instanceof VoipDn2DnBlockView) {
                    final VoipDn2DnBlockView newBlock = (VoipDn2DnBlockView) result;
                    if (StringUtils.isEmpty(newBlock.getAnfang())) {
                        newBlock.setAnfang(null);
                    }
                    if (StringUtils.isEmpty(newBlock.getEnde())) {
                        newBlock.setEnde(null);
                    }
                    newVoipDnPlanView.addVoipDn2BlockView(newBlock);
                    tbmDnBlock.setData(newVoipDnPlanView.getVoipDn2DnBlockViews()); //refresh table

                }
            }, null);
            btnDelBlock = getSwingFactory().createButton("delete.dn.block", e -> {
                newVoipDnPlanView.removeVoipDn2BlockView(selectedVoipDn2DnBlockView);
                tbmDnBlock.setData(newVoipDnPlanView.getVoipDn2DnBlockViews());
                deselectVoipDn2DnBlockView();
            }, null);

            btnDelBlock.setEnabled(false);

            final AKJPanel dnPlanButtonPanel = new AKJPanel(new GridBagLayout());
            dnPlanButtonPanel.add(btnNewBlock, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            dnPlanButtonPanel.add(btnDelBlock, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
            dnPlanButtonPanel.add(new AKJPanel(),
                    GBCFactory.createGBC(100, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

            final AKJPanel dnPlanGueltigAbPanel = new AKJPanel(new GridBagLayout());
            dnPlanGueltigAbPanel.add(lblDnPlanGueltigAb,
                    GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            dnPlanGueltigAbPanel
                    .add(dpDnPlanGueltigAb, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
            dnPlanGueltigAbPanel.add(new AKJPanel(),
                    GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.NONE));

            getButton(CMD_SAVE).setComponentExecutable(true);

            getChildPanel().setLayout(new GridBagLayout());

            getChildPanel().add(dnPlanGueltigAbPanel,
                    GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.BOTH));
            getChildPanel().add(new AKJPanel(),
                    GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

            getChildPanel()
                    .add(dnPlanButtonPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.BOTH));
            getChildPanel().add(new AKJPanel(),
                    GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

            getChildPanel().add(dnPlanTblScrollPane,
                    GBCFactory.createGBC(0, 100, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        private Date getTodayMidnight() {
            return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        private void deselectVoipDn2DnBlockView() {
            btnDelBlock.setEnabled(false);
            selectedVoipDn2DnBlockView = null;
        }

        @Override
        protected final void createGUI() {
        }

        @Override
        protected void execute(final String command) {
        }

        @Override
        public void update(final Observable o, final Object arg) {
        }

        @Override
        protected void doSave() {
            if (dpDnPlanGueltigAb.getDate(todayMidnight).getTime() < todayMidnight.getTime()) {
                MessageHelper.showWarningDialog(getMainFrame(),
                        "Das Gültigkeitsdatum darf nicht in der Vergangenheit liegen!", true);
            }
            else {
                newVoipDnPlanView.setGueltigAb(dpDnPlanGueltigAb.getDate(todayMidnight));
                final List<String> validationResult = voipDnPlanValidationService
                        .allErrorMessages(newVoipDnPlanView, selectedAuftragVoipDnView);
                if (validationResult.isEmpty()) {
                    try {
                        selectedAuftragVoipDnView.addVoipDnPlanView(newVoipDnPlanView);
                        generateVoIPLoginDaten(newVoipDnPlanView);

                        doSaveCallback.execute(selectedAuftragVoipDnView);
                        voipLoginPnl.objectSelected(selectedAuftragVoipDnView);
                        prepare4Close();
                        setValue(newVoipDnPlanView);
                    }
                    catch (final Exception e1) {
                        MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e1, true);
                    }
                }
                else {
                    final StringBuilder validationMsg = new StringBuilder();
                    for (String msg : validationResult) {
                        validationMsg.append(" - ").append(msg).append("\n");
                    }
                    MessageHelper.showWarningDialog(HurricanSystemRegistry.instance().getMainFrame(),
                            validationMsg.toString(), true);
                }
            }
        }

        private void generateVoIPLoginDaten(VoipDnPlanView voipDnPlanView){
            try {
                VoipDnPlan plan = voipDnPlanView.toEntity();
                VoipDnPlan activePlan = null;
                final Date when = Date.from(Instant.ofEpochMilli(voipDnPlanView.getGueltigAb().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                VoipDnPlanView activeVoipDnPlanView = selectedAuftragVoipDnView.getActiveVoipDnPlanView(when).orElse(null);
                if (activeVoipDnPlanView != null) {
                    activePlan = activeVoipDnPlanView.toEntity();
                }

                getVoipService().createVoIPLoginDaten(plan, activePlan, selectedAuftragVoipDnView.getOnKz(), selectedAuftragVoipDnView.getDnBase(),
                        selectedAuftragVoipDnView.getRangeFrom(), selectedAuftragVoipDnView.getSipDomain().getStrValue());

                voipDnPlanView.setSipLogin(plan.getSipLogin());
                voipDnPlanView.setSipHauptrufnummer(plan.getSipHauptrufnummer());
            }
            catch (StoreException | ServiceNotFoundException e) {
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e, true);
            }
        }

        protected VoIPService getVoipService() throws ServiceNotFoundException {
            return getCCService(VoIPService.class);
        }
    }
}
