/*
/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015 09:09
 */
package de.augustakom.hurrican.gui.hvt.umzug;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.validation.constraints.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.AKTableSingleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.auftrag.PhysikZuordnungDialog;
import de.augustakom.hurrican.gui.auftrag.actions.OpenAuftragFrameAction;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.shared.AssignEquipmentPlanDialog;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetailView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugMasterView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HvtUmzugService;

// @formatter:off
/**
 * Panel fuer den HVT-Umzug. <br/>
 * Grober Ablauf: <br/>
 * <ul>
 *     <li>Excel-File von DTAG importieren und dabei HVT und KVZ-Nr angeben</li>
 *     <li>Auftrags-Mapping durchführen</li>                     P
 *     <li>neue Rangierungen zuordnen (automatisch und/oder manuell) und Planung ausführen</li>
 *     <li>Planung ausfuehren (=CPS) und abschliessen</li>
 * </ul>
 */
// @formatter:on
public class HvtUmzugPanel extends AbstractServicePanel implements AKTableOwner, AKDataLoaderComponent {

    private static final long serialVersionUID = -6456443006817401252L;

    private static final Logger LOGGER = Logger.getLogger(HvtUmzugPanel.class);

    private static final String BTN_IMPORT_XLS = "btn.import.xls";
    private static final String BTN_EXPORT_XLS = "btn.export.xls";
    private static final String BTN_PLANUNG_VOLLSTAENDIG = "btn.planung.vollstaendig";
    private static final String BTN_PLANUNG_AUSFUEHREN = "btn.planung.ausfuehren";
    private static final String BTN_PLANUNG_DEAKTIVIEREN = "btn.planung.deaktivieren";
    private static final String BTN_AUTO_PORT_PLANUNG = "auto.port.planung";
    private static final String BTN_MAN_PORT_PLANUNG = "man.port.planung";
    private static final String BTN_CPS_SIMULATE = "cps.simulate";
    private static final String BTN_CPS_MODIFY = "cps.modify";
    private static final String BTN_CLOSE_UMZUG = "close.umzug";
    private static final String BTN_FIND_AUFTRAG = "find.auftrag";
    private static final String BTN_DELETE_DETAIL = "delete.detail";

    private final AKTableModel<HvtUmzugMasterView> tbmMaster = new AKReflectionTableModel<>(
            new String[] { "Umzugs-Datum", "HVT", "Ziel-HVT", "KVZ-Nr", "Bearbeiter", "Status" },
            new String[] { "schalttag", "hvtStandortName", "hvtStandortDestinationName", "kvzNr", "bearbeiter", "status" },
            new Class<?>[] { Date.class, String.class, String.class, String.class, String.class, String.class }
    );

    private final AKTableModel<HvtUmzugDetailView> tbmDetail = new AKReflectionTableModel<HvtUmzugDetailView>(
            new String[] { "Techn. AuftragNr.", "Billing AuftragNr.", "Verbindungsbezeichnung (VBZ)", "Produkt", "Status", "Bereitstellung am",
                    "LBZ", "ÜVT", "ÜVT neu", "ES Typ", "CPS erlaubt", "CPS Status", "man. CCs" },
            new String[] { "auftragId", "auftragNoOrig", "vbz", "produkt", "auftragStatus", "witaBereitstellungAm",
                    "lbz", "uevt", "uevtNeu", "endstellenTyp", "cpsAllowed", "cpsStatus", "manualCc" },
            new Class<?>[] { Long.class, Long.class, String.class, String.class, String.class, Date.class,
                    String.class, String.class, String.class, String.class, Boolean.class, String.class, Boolean.class }
    )

    {
        private static final long serialVersionUID = 3269798745314511357L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == 0);
        }

    };

    private AKJTable tbMaster;
    private AKJTable tbDetail;
    private AKJButton btnClose;
    private AKJButton btnPlanungVollstaendig;
    private AKJButton btnPlanungAusfuehren;
    private AKJButton btnPlanungDeaktivieren;
    private AKJButton btnAutoPortPlanung;
    private AKJButton btnManPortPlanung;
    private AKJButton btnCpsModify;
    private AKJButton btnCpsSimulate;
    private AKJButton btnFindAuftrag;
    private AKJButton btnDeleteDetail;

    public HvtUmzugPanel() {
        super("de/augustakom/hurrican/gui/hvt/umzug/resources/HvtUmzugPanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        final AKJPanel masterPanel = new AKJPanel(new GridBagLayout());
        //@formatter:off
        masterPanel.add(createTableInScrollPaneForMasterView(), GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        masterPanel.add(createButtonsPanelForMasterView(),      GBCFactory.createGBC(  0,100, 1, 0, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on

        final AKJPanel detailPanel = new AKJPanel(new GridBagLayout());
        //@formatter:off
        detailPanel.add(createTableInScrollPaneForDetailView(), GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        detailPanel.add(createButtonsPanelForDetailView(),      GBCFactory.createGBC(  0,100, 1, 0, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on

        this.setLayout(new GridBagLayout());
        this.add(masterPanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(detailPanel, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
    }

    private AKJPanel createButtonsPanelForMasterView() {
        final AKJPanel buttonsPanel = new AKJPanel(new GridBagLayout());
        final AKJButton btnXlsImport = getSwingFactory().createButton(BTN_IMPORT_XLS, getActionListener());
        final AKJButton btnXlsExport = getSwingFactory().createButton(BTN_EXPORT_XLS, getActionListener());
        btnPlanungVollstaendig = getSwingFactory().createButton(BTN_PLANUNG_VOLLSTAENDIG, getActionListener());
        btnPlanungAusfuehren = getSwingFactory().createButton(BTN_PLANUNG_AUSFUEHREN, getActionListener());
        btnPlanungDeaktivieren = getSwingFactory().createButton(BTN_PLANUNG_DEAKTIVIEREN, getActionListener());
        btnClose = getSwingFactory().createButton(BTN_CLOSE_UMZUG, getActionListener());
        //@formatter:off
        buttonsPanel.add(btnXlsImport,              GBCFactory.createGBC(100,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(btnXlsExport,              GBCFactory.createGBC(100,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(new AKJPanel(),            GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPanel.add(btnPlanungVollstaendig,    GBCFactory.createGBC(100,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(btnPlanungAusfuehren,      GBCFactory.createGBC(100,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(btnPlanungDeaktivieren,    GBCFactory.createGBC(100,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(new AKJPanel(),            GBCFactory.createGBC(  0,100, 0, 6, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPanel.add(btnClose,                  GBCFactory.createGBC(100,  0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on
        final AKJPanel borderedPanel = new AKJPanel(new BorderLayout());
        borderedPanel.add(buttonsPanel);
        borderedPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        return borderedPanel;
    }

    private AKJPanel createButtonsPanelForDetailView() {
        final AKJPanel buttonsPanel = new AKJPanel(new GridBagLayout());
        btnAutoPortPlanung = getSwingFactory().createButton(BTN_AUTO_PORT_PLANUNG, getActionListener());
        btnManPortPlanung = getSwingFactory().createButton(BTN_MAN_PORT_PLANUNG, getActionListener());
        btnCpsSimulate = getSwingFactory().createButton(BTN_CPS_SIMULATE, getActionListener());
        btnCpsModify = getSwingFactory().createButton(BTN_CPS_MODIFY, getActionListener());
        btnDeleteDetail = getSwingFactory().createButton(BTN_DELETE_DETAIL, getActionListener());
        btnFindAuftrag =  getSwingFactory().createButton(BTN_FIND_AUFTRAG, getActionListener());
        //@formatter:off
        buttonsPanel.add(btnAutoPortPlanung,    GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        buttonsPanel.add(btnManPortPlanung,     GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.BOTH));
        buttonsPanel.add(new AKJPanel(),        GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPanel.add(btnCpsSimulate,        GBCFactory.createGBC(100, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(btnCpsModify,          GBCFactory.createGBC(100, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(new AKJPanel(),        GBCFactory.createGBC(0, 100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPanel.add(btnFindAuftrag,        GBCFactory.createGBC(100, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPanel.add(btnDeleteDetail,       GBCFactory.createGBC(100, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on
        final AKJPanel borderedPanel = new AKJPanel(new BorderLayout());
        borderedPanel.add(buttonsPanel);
        borderedPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        return borderedPanel;
    }

    private AKJScrollPane createTableInScrollPaneForDetailView() {
        tbDetail = new HvtUmzugTable(tbmDetail, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbDetail.attachSorter(true);
        tbDetail.addMouseListener(new AKTableSingleClickMouseListener(this::onDetailSelected));
        tbDetail.addMouseListener(new AKTableDoubleClickMouseListener(this::showAuftragFrame));
        tbDetail.addPopupAction(new OpenAuftragFrameAction());
        return new AKJScrollPane(tbDetail, new Dimension(850, 400));
    }

    private void showAuftragFrame(Object selection) {
        if (selection != null && selection instanceof CCAuftragModel &&
                ((CCAuftragModel) selection).getAuftragId() != null) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    private AKJScrollPane createTableInScrollPaneForMasterView() {
        tbMaster = new AKJTable(tbmMaster, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbMaster.attachSorter(true);
        tbMaster.addTableListener(this);
        tbMaster.fitTable(new int[] { 120, 170, 170, 100, 150, 150 });
        tbMaster.addMouseListener(new AKTableSingleClickMouseListener(selection -> {
            if (selection instanceof HvtUmzugMasterView) {
                showDetails(selection);
            }
        }));
        return new AKJScrollPane(tbMaster, new Dimension(850, 150));
    }

    @Override
    public final void loadData() {
        tbmMaster.setData(null);
        tbmDetail.setData(null);
        disableHvtUmzugStateDependentButtons();

        hvtUmzugService().ifPresent(service ->
                tbmMaster.setData(service.loadHvtMasterData(HvtUmzugStatus.OFFEN,
                        HvtUmzugStatus.PLANUNG_VOLLSTAENDIG, HvtUmzugStatus.AUSGEFUEHRT)));
    }

    private void disableHvtUmzugStateDependentButtons() {
        btnClose.setEnabled(false);
        btnPlanungVollstaendig.setEnabled(false);
        btnPlanungAusfuehren.setEnabled(false);
        btnPlanungDeaktivieren.setEnabled(false);
        btnAutoPortPlanung.setEnabled(false);
        btnManPortPlanung.setEnabled(false);
        btnCpsModify.setEnabled(false);
        btnCpsSimulate.setEnabled(false);
        btnFindAuftrag.setEnabled(false);
        btnDeleteDetail.setEnabled(false);
    }

    private void enableValidHvtUmzugStateDependentButtons(@NotNull final HvtUmzug hvtUmzug) {
        hvtUmzugService().ifPresent(service -> {
            btnClose.setEnabled(hvtUmzug.isCloseHvtUmzugAllowed());
            btnPlanungVollstaendig.setEnabled(hvtUmzug.isOffen());
            btnPlanungAusfuehren.setEnabled(hvtUmzug.isExecutePlanningAllowed());
            btnPlanungDeaktivieren.setEnabled(hvtUmzug.isDisableAllowed());
            btnCpsModify.setEnabled(hvtUmzug.isCpsModifyAllowed());
            btnCpsSimulate.setEnabled(btnCpsModify.isEnabled() || btnPlanungAusfuehren.isEnabled());
            btnAutoPortPlanung.setEnabled(hvtUmzug.isAutomatischePortplanungAllowed());
            btnFindAuftrag.setEnabled(hvtUmzug.isOffen());
            btnDeleteDetail.setEnabled(hvtUmzug.isOffen());
        });
    }

    private void enableValidHvtUmzugDetailDependentButtons(@NotNull final Long hvtUmzugId, @NotNull Long hvtUmzugDetailId) {
        hvtUmzugService().ifPresent(service -> btnManPortPlanung.setEnabled(
                service.manuellePortplanungAllowed(hvtUmzugId, hvtUmzugDetailId)));
    }

    @Override
    public void showDetails(Object master) {
        try {
            disableHvtUmzugStateDependentButtons();
            if (master != null && master instanceof HvtUmzugMasterView && hvtUmzugService().isPresent()) {
                final Long hvtUmzugId = ((HvtUmzugMasterView) master).getHvtUmzugId();
                tbmDetail.setData(hvtUmzugService().get().loadHvtUmzugDetailData(hvtUmzugId));
                enableValidHvtUmzugStateDependentButtons(getSelectedHvtUmzug().get());
            }
            else {
                tbmDetail.setData(null);
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private void onDetailSelected(final Object detail) {
        disableHvtUmzugStateDependentButtons();
        getSelectedMasterView().ifPresent(mv -> {
            final HvtUmzug hvtUmzug = getSelectedHvtUmzug().get();
            enableValidHvtUmzugStateDependentButtons(hvtUmzug);
            if (detail instanceof HvtUmzugDetailView) {
                enableValidHvtUmzugDetailDependentButtons(hvtUmzug.getId(), ((HvtUmzugDetailView) detail).getId());
            }
        });
    }

    private HvtUmzug findHvtUmzug(Long hvtUmzugId) {
        return hvtUmzugService().map(s -> s.findById(hvtUmzugId)).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Optional<HvtUmzugMasterView> getSelectedMasterView() {
        final int selectedRow = tbMaster.getSelectedRow();
        return (selectedRow == -1)
                ? Optional.empty()
                : Optional.of(((AKTableSorter<HvtUmzugMasterView>) tbMaster.getModel()).getDataAtRow(selectedRow));
    }

    @SuppressWarnings("unchecked")
    private Optional<HvtUmzugDetailView> getSelectedDetailView() {
        final int selectedRow = tbDetail.getSelectedRow();
        return (selectedRow == -1)
                ? Optional.empty()
                : Optional.of(((AKTableSorter<HvtUmzugDetailView>) tbDetail.getModel()).getDataAtRow(selectedRow));
    }

    private Optional<HvtUmzug> getSelectedHvtUmzug() {
        return getSelectedMasterView()
                .map(HvtUmzugMasterView::getHvtUmzugId)
                .map(this::findHvtUmzug)
                .filter(Objects::nonNull);
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        switch (command) {
            case BTN_IMPORT_XLS:
                importFromXLS();
                break;
            case BTN_EXPORT_XLS:
                exportToXLS();
                break;
            case BTN_PLANUNG_VOLLSTAENDIG:
                markHvtUmzugAsDefined();
                loadData();
                break;
            case BTN_PLANUNG_AUSFUEHREN:
                executePlanning();
                loadData();
                break;
            case BTN_PLANUNG_DEAKTIVIEREN:
                deaktviereAusgewaehltePlanung();
                loadData();
                break;
            case BTN_AUTO_PORT_PLANUNG:
                automatischePortPlanung();
                break;
            case BTN_MAN_PORT_PLANUNG:
                manuellePortPlanung();
                break;
            case BTN_CPS_SIMULATE:
                sendCpsModifies(true);
                loadData();
                break;
            case BTN_CPS_MODIFY:
                sendCpsModifies(false);
                loadData();
                break;
            case BTN_CLOSE_UMZUG:
                closeUmzug();
                loadData();
                break;
            case BTN_FIND_AUFTRAG:
                matchHurricanOrders4HvtUmzug();
                break;
            case BTN_DELETE_DETAIL:
                deleteHvtUmzugDetail();
                break;
            default:
                LOGGER.warn("unhandled command with name " + command);
        }
    }

    private void matchHurricanOrders4HvtUmzug() {
        final Optional<HvtUmzug> selected = getSelectedHvtUmzug();
        if (!selected.isPresent()) {
            MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
        }
        else {
            hvtUmzugService().ifPresent(service -> {
                try {
                    service.matchHurricanOrders4HvtUmzug(selected.get());
                }
                catch (StoreException e) {
                    MessageHelper.showErrorDialog(this, e);
                }
            });
            getSelectedMasterView().ifPresent(this::showDetails);
        }
    }

    private void importFromXLS() {
        final HvtUmzugDialog hvtUmzugDialog = new HvtUmzugDialog();
        final Object result = DialogHelper.showDialog(getMainFrame(), hvtUmzugDialog, true, false);
        if (result instanceof HvtUmzug) {
            loadData();
            findRowIndex(((HvtUmzug) result).getId()).ifPresent(i -> {
                tbMaster.selectAndScrollToRow(i);
                showDetails(tbmMaster.getDataAtRow(i));
            });
        }
    }

    private void exportToXLS() {
        hvtUmzugService().ifPresent(service -> {
            final Optional<HvtUmzug> selected = getSelectedHvtUmzug();
            if (!selected.isPresent()) {
                MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
            }
            else {
                FileOutputStream stream = null;
                try {
                    byte[] portsForHvtUmzug = service.exportPortsForHvtUmzug(selected.get().getId());
                    String fileName = String.format("DTAG_Uevt_Export_%s_%s.xlsx",
                            selected.get().getKvzNr(),
                            DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14));

                    File xlsFile = new File(System.getProperty("user.home"), fileName);
                    stream = new FileOutputStream(xlsFile);
                    stream.write(portsForHvtUmzug);
                    MessageHelper.showInfoDialog(this,
                            String.format("Export erfolgreich durchgelaufen!\nExportdatei: %s", xlsFile.getCanonicalPath()));
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (IOException e) {
                            // ignore errors
                        }
                    }
                }
            }
        });
    }

    private void manuellePortPlanung() {
        final Optional<HvtUmzugDetailView> selected = getSelectedDetailView();
        if (!selected.isPresent()) {
            MessageHelper.showInfoDialog(this, "Es ist kein Auftrag selektiert!");
        }
        else {
            HvtUmzugDetailView hvtUmzugDetailView = selected.get();

            if (hvtUmzugDetailView.getUevtNeu() != null) {
                int remove = MessageHelper.showYesNoQuestion(this,
                        "Soll die bereits zugeordnete Rangierung wirklich übersteuert werden?",
                        "Rangierung übersteuern?");

                if (remove == JOptionPane.YES_OPTION) {
                    HvtUmzugDetail hvtUmzugDetail = hvtUmzugService().get().findDetailById(hvtUmzugDetailView.getId());
                    hvtUmzugService().get().unlockRangierung(hvtUmzugDetail, true);
                }
                else {
                    return;
                }
            }

            int selection = MessageHelper.showOptionDialog(this, "Rangierung erzeugen oder zuordnen?",
                    "Manuelle Port-Planung",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[] { "Rangierung erzeugen", "Rangierung zuordnen", "Abbrechen" }, null);
            switch (selection) {
                case JOptionPane.YES_OPTION:
                    createRangierung(hvtUmzugDetailView);
                    break;
                case JOptionPane.NO_OPTION:
                    assignRangierung(hvtUmzugDetailView);
                    break;
                default:
                    break;
            }
        }
    }


    private void sendCpsModifies(boolean simulate) {
        final Optional<HvtUmzug> selected = getSelectedHvtUmzug();
        if (!selected.isPresent()) {
            MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
        }
        else {
            try {
                String question = (simulate)
                        ? "Soll die Simulierung von CPS 'modifySubscriber' wirklich durchgeführt werden?"
                        : "Sollen für die Aufträge wirklich CPS 'modifySubscriber' erstellt werden?";

                final int decision = MessageHelper.showYesNoQuestion(this, question, "CPS");
                if (decision != JOptionPane.YES_OPTION) {
                    return;
                }

                setWaitCursor();

                AKWarnings warnings = hvtUmzugService().get().sendCpsModifies(
                        selected.get(), simulate, HurricanSystemRegistry.instance().getSessionId());
                if (warnings.isNotEmpty()) {
                    String message = (simulate)
                            ? "Folgende Fehler/Warnungen wurden bei der CPS Simulation erkannt:%n%s"
                            : "Bei der CPS-Ausführung (modifySubscriber) sind Fehler/Warnungen aufgetreten:%n%s";

                    MessageHelper.showInfoDialog(this, String.format(message, warnings.getWarningsAsText()));
                }
                else {
                    String message = (simulate)
                            ? "Die CPS Simulation hat keine Fehler festgestellt."
                            : "CPS modifySubscriber wurden erzeugt und an den CPS gesendet.";
                    MessageHelper.showInfoDialog(this, message);
                }
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }


    private void closeUmzug() {
        final Optional<HvtUmzug> selected = getSelectedHvtUmzug();
        if (!selected.isPresent()) {
            MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
        }
        else {
            final Optional<Either<AKWarnings, HvtUmzug>> result =
                    hvtUmzugService().flatMap(service -> selected.map(umzug -> service.closeHvtUmzug(umzug.getId())));
            result.ifPresent(either -> {
                if (either.isLeft()) {
                    MessageHelper.showWarningDialog(this, either.getLeft().getWarningsAsText(), true);
                }
            });
        }
    }


    private void deleteHvtUmzugDetail() {
        final Optional<HvtUmzugDetailView> selected = getSelectedDetailView();
        if (!selected.isPresent()) {
            MessageHelper.showInfoDialog(this, "Es ist kein Auftrag/Port selektiert!");
        }
        else {
            HvtUmzugDetailView hvtUmzugDetailView = selected.get();

            int remove = MessageHelper.showYesNoQuestion(this,
                    "Soll der selektierte Auftrag / Port wirklich aus der Planung gelöscht werden?",
                    "Auswahl löschen?");

            if (remove == JOptionPane.YES_OPTION) {
                HvtUmzugDetail hvtUmzugDetail = hvtUmzugService().get().findDetailById(hvtUmzugDetailView.getId());
                hvtUmzugService().get().deleteHvtUmzugDetail(hvtUmzugDetail);

                ((AKMutableTableModel) tbDetail.getModel()).removeObject(hvtUmzugDetailView);
            }
        }
    }


    private void markHvtUmzugAsDefined() {
        final Optional<HvtUmzug> selected = getSelectedHvtUmzug();
        if (!selected.isPresent()) {
            MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
        }
        else {
            final Optional<Either<AKWarnings, HvtUmzug>> result =
                    hvtUmzugService().flatMap(service -> selected.map(umzug -> service.markHvtUmzugAsDefined(umzug.getId())));
            result.ifPresent(either -> {
                if (either.isLeft()) {
                    MessageHelper.showWarningDialog(this, either.getLeft().getWarningsAsText(), true);
                }
            });
        }
    }


    private void automatischePortPlanung() {
        try {
            setWaitCursor();

            Optional<HvtUmzugMasterView> masterView = getSelectedMasterView();
            if (!masterView.isPresent()) {
                MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
                return;
            }

            AKWarnings warnings = hvtUmzugService().map(s -> s.automatischePortPlanung(masterView.get().getHvtUmzugId()))
                    .orElse(null);

            showDetails(masterView.get());
            if (warnings != null && warnings.isNotEmpty()) {
                MessageHelper.showInfoDialog(this, String.format("Folgende Fehler sind während der automatischen "
                        + "Port-Planung aufgetreten:\n%s", warnings.getWarningsAsText()));
            }
            else {
                MessageHelper.showInfoDialog(this, "Die automatische Port-Planung ist erfolgreich durchgeführt!");
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Oeffnet einen Dialog, ueber den eine Rangierung manuell aufgebaut werden kann.
     */
    private void createRangierung(HvtUmzugDetailView hvtUmzugDetail) {
        try {
            Optional<HvtUmzugMasterView> masterView = getSelectedMasterView();
            if (!masterView.isPresent()) {
                MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
                return;
            }

            setWaitCursor();
            Endstelle endstelle = findEndstelle4Detail(hvtUmzugDetail);
            AssignEquipmentPlanDialog dlg = new AssignEquipmentPlanDialog(endstelle,
                    masterView.get().getHvtStandortDestinationId(),
                    hvtUmzugDetail.getRangierIdNeu(), hvtUmzugDetail.getRangierAddIdNeu());

            setDefaultCursor();
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);

            if (dlg.getRangierungPlan() != null) {
                AKWarnings warnings = hvtUmzugService().map(s -> s.manuellePortPlanung(masterView.get().getHvtUmzugId(),
                        hvtUmzugDetail.getId(), dlg.getRangierungPlan().getId(), dlg.isDefault(), true))
                        .orElse(null);

                showWarningsOfManuellePlanung(warnings);
                getSelectedMasterView().ifPresent(this::showDetails);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Oeffnet einen Dialog, ueber den eine Rangierung zugeordnet werden kann.
     */
    private void assignRangierung(HvtUmzugDetailView hvtUmzugDetailView) {
        try {
            setWaitCursor();
            final Endstelle endstelle = findEndstelle4Detail(hvtUmzugDetailView);
            final Optional<HvtUmzug> hvtUmzug = getSelectedHvtUmzug();
            if (!hvtUmzug.isPresent()) {
                return;
            }

            final Long hvtDestId = hvtUmzug.get().getHvtStandortDestination();
            final PhysikZuordnungDialog dialog = new PhysikZuordnungDialog(endstelle, false, true, hvtDestId);
            setDefaultCursor();

            final Object result = DialogHelper.showDialog(getMainFrame(), dialog, true, true);
            if (!(result instanceof RangierungsEquipmentView)) {
                return;
            }

            final RangierungsEquipmentView view = (RangierungsEquipmentView) result;
            hvtUmzugService().ifPresent(service -> {
                AKWarnings warnings = service.manuellePortPlanung(hvtUmzug.get().getId(),
                        hvtUmzugDetailView.getId(),
                        view.getRangierId(), true, false);

                if (view.getRangierIdAdd() != null && warnings.isEmpty()) {
                    warnings = service.manuellePortPlanung(hvtUmzug.get().getId(),
                            hvtUmzugDetailView.getId(),
                            view.getRangierIdAdd(), false, false);
                }

                showWarningsOfManuellePlanung(warnings);
                getSelectedMasterView().ifPresent(this::showDetails);
            });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void deaktviereAusgewaehltePlanung() {
        try {
            setWaitCursor();

            final int decision = MessageHelper.showYesNoQuestion(this,
                    "Soll die ausgewählte Planung wirklich deaktiviert werden?", "Planung deaktivieren?");
            if (decision == 0) {
                getSelectedMasterView().ifPresent(view -> hvtUmzugService()
                        .ifPresent(service -> service.disableUmzug(view.getHvtUmzugId())));
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }


    private void executePlanning() {
        try {
            Optional<HvtUmzugMasterView> masterView = getSelectedMasterView();
            if (!masterView.isPresent()) {
                MessageHelper.showInfoDialog(this, "Es ist kein Umzug selektiert!");
                return;
            }

            setWaitCursor();

            final int decision = MessageHelper.showYesNoQuestion(this,
                    "Soll die ausgewählte Planung wirklich ausgeführt werden?", "Planung ausführen?");
            if (decision == 0 && hvtUmzugService().isPresent()) {
                HvtUmzugService hvtUmzugService = hvtUmzugService().get();
                HvtUmzug hvtUmzug = hvtUmzugService.findById(masterView.get().getHvtUmzugId());
                AKWarnings warnings = hvtUmzugService.executePlanning(hvtUmzug,
                        HurricanSystemRegistry.instance().getSessionId());

                if (warnings.isNotEmpty()) {
                    MessageHelper.showInfoDialog(this, String.format(
                            "Bei der Ausführung der Planung sind folgende Warnungen / Fehler aufgetreten:%n%s",
                            warnings.getWarningsAsText()), null, true);
                }
                else {
                    MessageHelper.showInfoDialog(this, "Planung wurde erfolgreich ausgeführt.", null, true);
                }
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }


    private Optional<Integer> findRowIndex(Long hvtUmzugId) {
        if (hvtUmzugId != null) {
            for (int i = 0; i < tbMaster.getRowCount(); i++) {
                @SuppressWarnings("unchecked")
                final HvtUmzugMasterView row = ((AKTableSorter<HvtUmzugMasterView>) tbMaster.getModel()).getDataAtRow(i);
                if (hvtUmzugId.equals(row.getHvtUmzugId())) {
                    return Optional.of(i);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<HvtUmzugService> hvtUmzugService() {
        try {
            return Optional.of(getCCService(HvtUmzugService.class));
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(this, e);
            return Optional.empty();
        }
    }

    private Optional<EndstellenService> endstellenService() {
        try {
            return Optional.of(getCCService(EndstellenService.class));
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(this, e);
            return Optional.empty();
        }
    }

    private Endstelle findEndstelle4Detail(HvtUmzugDetailView hvtUmzugDetail) {
        return endstellenService().map(s -> {
            try {
                return s.findEndstelle4Auftrag(hvtUmzugDetail.getAuftragId(), hvtUmzugDetail.getEndstellenTyp());
            }
            catch (FindException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        }).orElse(null);
    }

    private void showWarningsOfManuellePlanung(AKWarnings warnings) {
        if (warnings != null && warnings.isNotEmpty()) {
            MessageHelper.showInfoDialog(this, String.format("Folgende Fehler sind während der manuellen "
                    + "Port-Planung aufgetreten:\n%s", warnings.getWarningsAsText()));
        }
        else {
            MessageHelper.showInfoDialog(this, "Die manuelle Port-Planung ist erfolgreich durchgeführt!");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed
    }

}
