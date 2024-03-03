/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.07.2013 07:49:04
 */
package de.augustakom.hurrican.gui.tools.wbci;

import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;
import static de.mnet.wbci.model.AutomationTask.*;
import static de.mnet.wbci.model.RufnummernportierungVO.StatusInfo.*;
import static de.mnet.wbci.model.WbciAction.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.apache.commons.lang.StringUtils.*;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.tree.*;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKActionGroup;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKJToggleButton;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.TextInputDialog;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.DateTimeTableCellRenderer;
import de.augustakom.common.gui.swing.table.FilterRelations;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeRenderer;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeWillExpandListener;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByExtOrderNoDialog;
import de.augustakom.hurrican.gui.tools.wbci.filter.PreAgreementTableFilter;
import de.augustakom.hurrican.gui.tools.wbci.filter.PreAgreementTableFilterRelation;
import de.augustakom.hurrican.gui.tools.wbci.helper.HurricanAuftragHelper;
import de.augustakom.hurrican.gui.tools.wbci.helper.UserHelper;
import de.augustakom.hurrican.gui.tools.wbci.tables.AutomationTaskTableModel;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTable;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel;
import de.augustakom.hurrican.gui.tools.wbci.tables.RufnummernPortierungTable;
import de.augustakom.hurrican.gui.tools.wbci.tables.RufnummernPortierungTableModel;
import de.augustakom.hurrican.gui.tools.wbci.tables.WholesaleAuditTableModel;
import de.augustakom.hurrican.gui.tools.wbci.tree.VaDetailViewer;
import de.augustakom.hurrican.gui.tools.wbci.tree.VaRootTreeNode;
import de.augustakom.hurrican.gui.tools.wbci.tree.VaTree;
import de.augustakom.hurrican.gui.tools.wbci.tree.VaTreeSelectionListener;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.augustakom.hurrican.service.elektra.builder.ElektraResponseDtoBuilder;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.hurrican.wholesale.ws.outbound.WholesaleOrderOutboundService;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungVO;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.RufnummernportierungVOBuilder;
import de.mnet.wbci.model.helper.WbciMeldungHelper;
import de.mnet.wbci.model.helper.WbciRequestHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciEscalationService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciKuendigungsService;

/**
 * Abstract panel for all open and not finished WBCI Vorabstimmungen.
 */
abstract class AbstractPreAgreementOverviewPanel extends AbstractServicePanel implements AKTableOwner,
        AKDataLoaderComponent, AKFilterTableModelListener, AKObjectSelectionListener, VaDetailViewer, WholesaleDataChangeListener {

    static final Insets BUTTON_INSETS = new Insets(1, 0, 1, 0);
    private static final long serialVersionUID = 5145603321897744943L;
    private static final Logger LOGGER = Logger.getLogger(AbstractPreAgreementOverviewPanel.class);
    private static final String ANFRAGE_KWT = "anfrage.kwt";
    private static final String ANFRAGE_KWT_DATE = "anfrage.kwt.date";
    private static final String KUNDE_TYP = "kunde.typ";
    private static final String KUNDE_NAME = "kunde.name";
    private static final String KUNDE_VORNAME = "kunde.vorname";
    private static final String KUNDE_FIRMA = "kunde.firma";
    private static final String KUNDE_FIRMAZUSATZ = "kunde.firmazusatz";
    private static final String STANDORT_STRASSE = "standort.strasse";
    private static final String STANDORT_PLZ_ORT = "standort.plz.ort";
    private static final String STANDORT_PLZ = "standort.plz";
    private static final String STANDORT_ORT = "standort.ort";
    private static final String TAIFUN_ORDER_IDS = "taifun.order.ids";
    private static final String AUTOMATABLE = "automatable";
    private static final String SONSTIGES = "sonstiges";
    private static final String ALLE_RUFNUMMERN = "alle.rufnummern";
    private static final String MNET_TECHNOLOGIE = "mnet.technologie";
    private static final String STR_TV_ID = "anfrage.stornotv.id";
    private static final String STR_TV_TA = "anfrage.stornotv.ta";
    private static final String RUFNUMMERN = "rufnummern";
    private static final String WECHSELTERMIN = "wechseltermin";
    private static final String TAIFUN_KUENDIGUNGSDATUM = "taifun.kuendigungstermin";
    private static final String TAIFUN_ORDER_CANCELLED = "taifun.order.cancelled";
    private static final String STR_AEN_GF_VORABSTIMMUNGSID = "str.aen.gf.voranstimmungsid";
    private static final String STR_AEN_GF_WECHSELTERMIN = "str.aen.gf.wechseltermin";
    private static final String BEARBEITUNGSSTATUS = "bearbeitungsstatus";

    private static final String BEMERKUNGEN_TEXTAREA = "textarea.bemerkungen";
    private static final String STORNO_VORGEHALTEN_TITLE = "storno.vorgehalten.title";
    private static final String STORNO_VORGEHALTEN_SUCCESS = "storno.vorgehalten.success";
    private static final String STORNO_VORGEHALTEN_INFO = "storno.vorgehalten.info";
    private static final String TASK_ASSIGN_TEXT = "assign.task";

    private static final String TASK_RELEASE_TEXT = "release.task";
    private static final String TASK_BEARBEITEN_BTN = "assign.release.task.btn";
    private static final String SAVE_BEMERKUNG_BTN = "save.bemerkung.btn";
    private static final String ASSIGN_TAIFUN_ORDER_BTN = "assign.taifun.order.btn";
    private static final String AKMTR_BTN = "akmtr.btn";
    private static final String ABBMTR_BTN = "abbmtr.btn";
    private static final String TV_BTN = "tv.btn";
    private static final String STORNO_BTN = "storno.btn";
    private static final String ABBM_SENDEN_BTN = "abbm.btn";
    private static final String SEND_ERLM_BTN = "send.erlm.btn";
    private static final String CLOSE_ISSUE_BTN = "issue.close.btn";
    private static final String FORCE_CLOSE_PREAGREEMENT_BTN = "preagreement.force.close.btn";
    private static final String ANSWER_VA_BTN = "answer.va.btn";
    private static final String ALLE_TASKS = "alle.tasks";
    private static final String TEAM_TASKS = "team.tasks";
    private static final String EIGENE_TASKS = "eigene.tasks";
    private static final String AUTO_RUEMVA_ACTION = "auto.ruemva.action";
    private static final String AUTO_RRNP_ACTION = "auto.rrnp.action";
    private static final String AUTO_AKMTR_ACTION = "auto.akmtr.action";
    private static final String AUTO_TVS_VA_ACTION = "auto.tvs.va.action";
    private static final String ADDITIONAL_FILTERS = "additional.filters";
    private static final String ADD_DIAL_NUMBER = "add.dial.number";
    private static final String DELETE_DIAL_NUMBER = "delete.dial.number";


    private static final String WHOLESALE_SHOW_XML = "wholesale.showxml";


    private static final String HISTORY_BTN = "history.btn";
    // contains the list of filters displayed within the additional-filters combo box
    // the first item in the list is selected as default => this normally should be the REMOVE_FILTER
    private static final String REMOVE_FILTER = "";
    private static final String REMOVE_ADDITIONAL_FILTER = "Zusätzlichen Filter entfernen";
    private static final String DUE_SOON_FILTER = "Bald fällig";
    private static final String KLAERFAELLE = "Klärfälle";
    private static final String POSITIVE_MELDUNGEN = "Positive Meldungen";
    private static final String NEGATIVE_MELDUNGEN = "Negative Meldungen";
    private static final String SENT_TODAY = "Heute verschickt";
    private static final String RECEIVED_TODAY = "Heute empfangen";
    private static final String WS = "WS - Alle";
    private static final String PK = "PK - Alle";
    private static final String PK_WITHOUT_TEAM = "PK - Ohne zug. Team";
    private static final String GK = "GK - Alle";
    private static final String GK_WITHOUT_TEAM = "GK - Ohne zug. Team";

    private static final String COMMENTS = "comments";
    private static final String AUTOMATION_TASKS_TITLE = "automationTasks.title";
    private static final String COMMONS = "commons";
    private static final String DETAILS = "details";
    private static final String COMMENT = "comment";
    private static final String AUTOMATION_TASKS = "automationTasks";
    private static final String WHOLESALE = "wholesale";
    private static final String KLAERFALL = "klaerfall";
    private static final String OPEN_TV_OR_STORNO = "open.tv.or.storno";
    private static final String OVERDUE_VA = "overdue.va";
    private static final String ZEITFENSTER = "zeitfenster";
    private static final String PKIAUF = "pkiauf";
    private static final String ERLM_NOT_ALLOWED = "Für den ausgewählten Vorgang ist eine Erledigtmeldung im Moment nicht zulässig!";
    private static final String CLOSE_VORGANG_QUESTION_LONG = "Der Vorgang kann abgeschlossen werden. Soll der Vorgang wirklich abgeschlossen werden?";
    private static final String CLOSE_VORGANG_QUESTION_SHORT = "Vorgang abschließen?";
    private static final String CLOSE_ISSUE_QUESTION_LONG = "Soll die Bearbeitung abgeschlossen werden?";
    private static final String CLOSE_ISSUE_QUESTION_SHORT = "Bearbeitung abschließen?";
    private static final String CLOSE_ISSUE_AUTOMATION_TASKS_QUESTION_LONG = "Nicht alle Automatisierungsschritte für den Vorgang sind erfolgreich abgeschlossen worden!\nWollen Sie wirklich die Bearbeitung trotz der fehlerhaften Automatisierungsschritte abschließen?";
    private static final String CLOSE_ISSUE_AUTOMATION_TASKS_QUESTION_SHORT = "Bearbeitung trotz fehlerhafter Automatisierung abschließen?";
    private static final String ABBMTR_ONLY_VALID_AFTER_AKM_TR_INFO = "Eine ABBM-TR ist nur bei einer vorher empfangenen AKM-TR gültig!";
    private static final String FORCE_CLOSE_VORGANG_QUESTION_SHORT = "Vorgang wirklich abschließen?";
    private static final String FORCE_CLOSE_VORGANG_QUESTION_LONG = "Soll der Vorgang wirklich abgeschlossen werden?\nBitte achten Sie darauf, dass nach dem Abschließen alle weiteren Aktionen auf dem Vorgang manuell durchgeführt werden müssen.";
    private static final String ABBM_NOT_ALLOWED_INFO = "Für den ausgewählten Vorgang ist eine Abbruchmeldung im Moment nicht zulässig!";
    private static final String NO_VA_SELECTED_INFO = "Es wurde keine Vorabstimmung ausgewählt!";
    private static final String ASSIGN_TAIFUN_ORDER_QUESTION_LONG = "Der Vorabstimmungsanfrage ist bereits ein Taifun Auftrag zugeordnet. Wollen Sie die Zuordnung überschreiben?";
    private static final String ASSIGN_TAIFUN_ORDER_QUESTION_SHORT = "Zuordnung Taifun Auftrag";
    private static final String ASSIGN_TAIFUN_ORDER_NOT_POSSIBLE_INFO = "Zuordnung eines Taifunauftrags ist nur für abgebende Vorabstimmungsabfragen möglich!";
    private static final String KLAERFALL_AUFLOESEN_LBL = "Klärfall auflösen";
    private static final String CLOSE_VORGANG_ADMIN_LABEL = "Abschließen Admin";
    private static final String CANCELLABLE_WITA_ORDERS_QUESTION_LONG = "Es wurde festgestellt, dass die WITA-Bestellung(en) mit Vetragsnummer(n) %s gekündigt werden können. Wollen Sie dies im Anschluß durchführen?";
    private static final String CANCELLABLE_WITA_ORDERS_QUESTION_SHORT = "Kündigung von WITA-Bestellungen";
    // Konfig-Map mit Angabe der Detail-Panels zu den VA-Typen
    private static final ImmutableMap<Class<? extends WbciEntity>, Class<? extends AbstractVaDetailPanel>> VA_DETAIL_PANEL_CONFIG =
            ImmutableMap.<Class<? extends WbciEntity>, Class<? extends AbstractVaDetailPanel>>builder()
                    .put(VorabstimmungsAnfrage.class, RequestDetailVaPanel.class)
                    .put(StornoAenderungAbgAnfrage.class, RequestDetailStornoAenderungAbgPanel.class)
                    .put(StornoAenderungAufAnfrage.class, RequestDetailStornoPanel.class)
                    .put(StornoAufhebungAbgAnfrage.class, RequestDetailStornoAufhebungAbgPanel.class)
                    .put(StornoAufhebungAufAnfrage.class, RequestDetailStornoPanel.class)
                    .put(TerminverschiebungsAnfrage.class, RequestDetailTvPanel.class)
                    .put(RueckmeldungVorabstimmung.class, MeldungDetailRuemVaPanel.class)
                    .put(Abbruchmeldung.class, MeldungDetailAbbmPanel.class)
                    .put(AbbruchmeldungTerminverschiebung.class, MeldungDetailAbbmPanel.class)
                    .put(AbbruchmeldungStornoAen.class, MeldungDetailAbbmPanel.class)
                    .put(AbbruchmeldungStornoAuf.class, MeldungDetailAbbmPanel.class)
                    .put(Erledigtmeldung.class, MeldungDetailErlmPanel.class)
                    .put(ErledigtmeldungTerminverschiebung.class, MeldungDetailErlmPanel.class)
                    .put(ErledigtmeldungStornoAen.class, MeldungDetailErlmPanel.class)
                    .put(ErledigtmeldungStornoAuf.class, MeldungDetailErlmPanel.class)
                    .put(AbbruchmeldungTechnRessource.class, MeldungDetailAbbmTrPanel.class)
                    .put(UebernahmeRessourceMeldung.class, MeldungDetailAkmTrPanel.class)
                    .build();
    // Actions
    ShowAkmtrDialogAction showAkmTrDialogAction;
    ShowTvDialogAction showTvDialogAction;
    ShowStornoDialogAction showStornoDialogAction;
    ShowDecisionDialogAction showDecisionDialogAction;
    ShowAbbmDialogAction showAbbmDialogAction;
    ShowErlmDialogAction showErlmDialogAction;
    ShowTaifunOrderSelectDialogAction showTaifunOrderSelectDialogAction;
    AKJButton btnAkmTr = null;
    AKJButton btnAbbmTr = null;
    AKJButton btnTv = null;
    AKJButton btnAbbm = null;
    AKJButton btnStorno = null;
    AKJButton btnAnswerVa = null;
    AKJButton btnSendErlm = null;
    AKJButton btnAssignTaifunOrder = null;
    // GUI-Komponenten
    protected PreAgreementTable preAgreementTable = null;
    private PreAgreementTableModel preAgreementTableModel = null;
    private AKJToggleButton btnEigeneTasks = null;
    private AKJToggleButton btnTeamTasks = null;
    private AKJToggleButton btnAlleTasks = null;
    private AKJComboBox cbAdditionalFilters = null;
    private AKJButton btnCloseIssue = null;
    private AKJButton btnForceClosePreagreement = null;
    private AKJButton btnTaskBearbeiten = null;
    private AKJButton btnSaveBemerkung = null;
    private AKJDateComponent dcAnfrageKWT = null;
    private AKJTextField tfKTyp = null;
    private AKJTextField tfKName = null;
    private AKJTextField tfKVorname = null;
    private AKJTextField tfKFrima = null;
    private AKJTextField tfKFirmazusatz = null;
    private AKJTextField tfStandortStrasse = null;
    private AKJTextField tfStandortPLZ = null;
    private AKJTextField tfStandortOrt = null;
    private AKJTextArea taTaifunOrderIds;
    private AKJLabel lblKName = null;
    private AKJLabel lblKVorname = null;
    private AKJLabel lblKFirma = null;
    private AKJLabel lblKFirmazusatz = null;
    private AKJCheckBox chbAutomatable = null;
    private AKJLabel lblKlaerfall = null;
    private AKJLabel lblTaifunKuendigungsInfo = null;
    private AKJLabel lblOffeneTvOrStorno = null;
    private AKJLabel lblOverdueVa = null;
    private AKJTextField tfZeitfenster = null;
    private AKJTextField tfPkiAuf = null;
    private AKJTextField tfMnetTechnologie = null;
    private AKJTextArea taStrTVIds;
    private AKJTextField tfStrAenGfVorabstimmung = null;
    private AKJDateComponent dcStrAenGfWechseltermin = null;
    private AKJCheckBox chbAlleRufnummern = null;
    private AKJDateComponent dcWechseltermin = null;
    private RufnummernPortierungTable tbRufnummern;
    private RufnummernPortierungTableModel tbMdlRufnummern;
    private AKJTextArea taBemerkungen;
    private VaTree vaTree;
    private DefaultTreeModel treeModel;
    private VaRootTreeNode rootNode;
    private AKJPanel selectedTreeNodeDetailPanel;
    private AutomationTaskTableModel tbMdlAutomationTasks;
    private WholesaleAuditTableModel tbMdlWholesaleAudits;
    private WholesaleAudit selectedAudit;
    private AKJTextArea taBearbeitungsstatus = null;
    // Super user flag for current user - evaluated once during panel initialization
    private boolean superUserRole = false;
    // service and control objects
    private WbciCommonService wbciCommonService;
    private WbciDeadlineService wbciDeadlineService;
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    private WbciEscalationService wbciEscalationService;
    private WbciKuendigungsService wbciKuendigungsService;
    private WbciElektraService wbciElektraService;
    private RufnummerService rufnummerService;
    private FeatureService featureService;
    protected AKUserService akUserService;
    private WholesaleOrderOutboundService wholesaleOrderOutboundService;
    private SendEscalationMailToCarrierAction sendEscalationMailToCarrierAction;
    private AssignPkOrGkAction assignPkAction;
    private AssignPkOrGkAction assignGkAction;
    private ShowCloseIssueDialogAction showCloseIssueDialogAction;
    private ShowForceClosePreagreementAction showForceClosePreagreementAction;
    private ToggleTaskAction toggleTaskAction;
    private AutomationAction automationRuemVaAction;
    private AutomationAction automationRrnpAction;
    private AutomationAction automationAkmTrAction;
    private AutomationAction automationTvsVaAction;
    private AutomationAction automationAddDialNumberAction;
    private AutomationAction automationDeleteDialNumberAction;
    private TasksFilterAction myTasksFilterAction;
    private TasksFilterAction teamTasksFilterAction;
    private TasksFilterAction allTasksFilterAction;
    private DropDownSelectionFilterAction noFilterAction;
    private DropDownSelectionFilterAction activeFilterAction;
    private DropDownSelectionFilterAction passiveFilterAction;
    private DropDownSelectionFilterAction newVaFilterAction;
    private DropDownSelectionFilterAction newVaExpiredFilterAction;

    // DATA Fields
    private List<WbciRequest> requestsForSelectedItem = null;
    private WbciRequest selectedWbciRequest = null;
    private PreAgreementVO selectedPreagreementVo = null;

    AbstractPreAgreementOverviewPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/PreAgreementOverviewPanel.xml");
        initServices();
        createGUI();
    }

    /**
     * @return an instance of the class {@link PreAgreementTable}
     */
    protected abstract PreAgreementTableModel createPreAgreementTabelModel();

    /**
     * @return the carrier role of M-Net{@link CarrierRole#AUFNEHMEND} or {@link CarrierRole#ABGEBEND} to filter the db
     * query
     */
    protected abstract CarrierRole getMnetCarrierRole();

    private void initServices() {
        try {
            wbciCommonService = getCCService(WbciCommonService.class);
            wbciDeadlineService = getCCService(WbciDeadlineService.class);
            wbciGeschaeftsfallService = getCCService(WbciGeschaeftsfallService.class);
            wbciEscalationService = getCCService(WbciEscalationService.class);
            wbciKuendigungsService = getCCService(WbciKuendigungsService.class);
            rufnummerService = getBillingService(RufnummerService.class);
            wbciElektraService = getCCService(WbciElektraService.class);
            featureService = getCCService(FeatureService.class);
            wholesaleOrderOutboundService = getCCService(WholesaleOrderOutboundService.class);
            akUserService = getAuthenticationService(AKUserService.class);

            checkAndSetSuperUserRole();

            checkAndSetSuperUserRole();
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void checkAndSetSuperUserRole() {
        try {
            superUserRole = UserHelper.isSuperWbciUser(akUserService);
        }
        catch (AKAuthenticationException e) {
            LOGGER.warn("Unable to get user roles - super user operations might not be enabled in this session", e);
        }
    }

    @Override
    protected final void createGUI() {
        /* split component for the bottom and top panels */
        AKJSplitPane split = new AKJSplitPane(JSplitPane.VERTICAL_SPLIT, true);

        /* top component */
        createOverviewTable();
        AKJScrollPane spPreAggreementTable = new AKJScrollPane(preAgreementTable, new Dimension(910, 290));
        split.setTopComponent(spPreAggreementTable);

        /* bottom component */

        AKJTabbedPane detailTab = new AKJTabbedPane();
        detailTab.add(getSwingFactory().getText(COMMONS), createBasicInformationPanel());
        detailTab.add(getSwingFactory().getText(DETAILS), createDetailsPanel());
        detailTab.add(getSwingFactory().getText(COMMENT), createCommentPanel());
        detailTab.add(getSwingFactory().getText(AUTOMATION_TASKS), createAutomationTaskPanel());
        if (isFeatureEnabled(WHOLESALE_PV)) {
            detailTab.add(getSwingFactory().getText(WHOLESALE), createWholesalePanel());
        }

        AKJPanel downPnl = new AKJPanel(new BorderLayout());
        downPnl.add(createButtonPanel(), BorderLayout.WEST);
        downPnl.add(detailTab, BorderLayout.CENTER);
        AKJScrollPane spDown = new AKJScrollPane(downPnl);
        spDown.setMinimumSize(new Dimension(1240, 400));
        split.setBottomComponent(spDown);
        split.setDividerSize(2);
        split.setResizeWeight(1d);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

        validateButtons();
    }

    private void createOverviewTable() {
        preAgreementTableModel = createPreAgreementTabelModel();
        preAgreementTableModel.addFilterTableModelListener(this);
        preAgreementTable = createPreAgreementTable(preAgreementTableModel);

        toggleTaskAction = new ToggleTaskAction();
        showAkmTrDialogAction = new ShowAkmtrDialogAction();
        showTvDialogAction = new ShowTvDialogAction();
        showStornoDialogAction = new ShowStornoDialogAction();
        showDecisionDialogAction = new ShowDecisionDialogAction();
        showAbbmDialogAction = new ShowAbbmDialogAction();
        showErlmDialogAction = new ShowErlmDialogAction();
        showTaifunOrderSelectDialogAction = new ShowTaifunOrderSelectDialogAction();
        showCloseIssueDialogAction = new ShowCloseIssueDialogAction();
        showForceClosePreagreementAction = new ShowForceClosePreagreementAction();
        ShowCarrierInfoDialogAction showCarrierInfoDialogAction = new ShowCarrierInfoDialogAction();
        assignGkAction = new AssignPkOrGkAction(KundenTyp.GK);
        assignPkAction = new AssignPkOrGkAction(KundenTyp.PK);
        sendEscalationMailToCarrierAction = new SendEscalationMailToCarrierAction();
        automationRuemVaAction = new AutomationAction("RUEM-VA Daten in Taifun aktualisieren", AUTO_RUEMVA_ACTION);
        automationRrnpAction = new AutomationAction("RRNP Daten in Taifun aktualisieren", AUTO_RRNP_ACTION);
        automationAkmTrAction = new AutomationAction("AKM-TR Daten in Taifun aktualisieren", AUTO_AKMTR_ACTION);
        automationTvsVaAction = new AutomationAction("TV ERLM nach Taifun übernehmen", AUTO_TVS_VA_ACTION);
        automationAddDialNumberAction = new AutomationAction("Rufnummer in Taifun anlegen", ADD_DIAL_NUMBER);
        automationDeleteDialNumberAction = new AutomationAction("Rufnummer aus Taifun löschen", DELETE_DIAL_NUMBER);
        myTasksFilterAction = new TasksFilterAction("Meine Tasks", EIGENE_TASKS);
        teamTasksFilterAction = new TasksFilterAction("Team-Tasks", TEAM_TASKS);
        allTasksFilterAction = new TasksFilterAction("Alle Tasks", ALLE_TASKS);
        noFilterAction = new DropDownSelectionFilterAction(REMOVE_ADDITIONAL_FILTER, REMOVE_FILTER);
        activeFilterAction = new DropDownSelectionFilterAction(ACTIVE.getDescription(), ACTIVE.getDescription());
        passiveFilterAction = new DropDownSelectionFilterAction(PASSIVE.getDescription(), PASSIVE.getDescription());
        newVaFilterAction = new DropDownSelectionFilterAction(NEW_VA.getDescription(), NEW_VA.getDescription());
        newVaExpiredFilterAction = new DropDownSelectionFilterAction(NEW_VA_EXPIRED.getDescription(), NEW_VA_EXPIRED.getDescription());

        AKActionGroup automationGroup = new AKActionGroup("Automatisierung");
        automationGroup.addAction(automationRuemVaAction);
        automationGroup.addAction(automationRrnpAction);
        automationGroup.addAction(automationAkmTrAction);
        automationGroup.addAction(automationTvsVaAction);

        preAgreementTable.addPopupGroup(automationGroup);
        preAgreementTable.addPopupSeparator();

        AKActionGroup filterGroup = new AKActionGroup("Filter");
        filterGroup.addAction(myTasksFilterAction);
        filterGroup.addAction(teamTasksFilterAction);
        filterGroup.addAction(allTasksFilterAction);
        filterGroup.addActionSeparator();
        filterGroup.addAction(noFilterAction);
        filterGroup.addAction(activeFilterAction);
        filterGroup.addAction(passiveFilterAction);
        filterGroup.addAction(newVaFilterAction);
        filterGroup.addAction(newVaExpiredFilterAction);

        preAgreementTable.addPopupGroup(filterGroup);
        preAgreementTable.addPopupSeparator();
        preAgreementTable.addPopupAction(assignGkAction);
        preAgreementTable.addPopupAction(assignPkAction);
        preAgreementTable.addPopupSeparator();
        preAgreementTable.addPopupAction(toggleTaskAction);
        preAgreementTable.addPopupSeparator();

        AKActionGroup functionGroup = new AKActionGroup("Funktionen");
        createCustomContextMenu(functionGroup);

        functionGroup.addActionSeparator();
        functionGroup.addAction(showCloseIssueDialogAction);

        if (superUserRole) {
            functionGroup.addActionSeparator();
            functionGroup.addAction(showForceClosePreagreementAction);
        }

        preAgreementTable.addPopupGroup(functionGroup);
        addWholesaleContextMenuEntries(preAgreementTable);

        preAgreementTable.addPopupSeparator();
        preAgreementTable.addPopupAction(showCarrierInfoDialogAction);

        preAgreementTable.addPopupSeparator();
        preAgreementTable.addPopupAction(sendEscalationMailToCarrierAction);
    }

    private PreAgreementTable createPreAgreementTable(PreAgreementTableModel preAgreementTableModel) {
        PreAgreementTable preAgreementTable = new PreAgreementTable(preAgreementTableModel, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        preAgreementTable.attachSorter();
        preAgreementTable.addTableListener(this);
        preAgreementTable.addPopupAction(new TableOpenOrderAction());
        preAgreementTable.addPopupAction(new ShowWbciHistoryAction());
        preAgreementTable.addMouseListener(new AKTableDoubleClickMouseListener(this));
        preAgreementTable.fitTable(preAgreementTableModel.getDataModelMetaData());
        preAgreementTable.addPopupSeparator();
        return preAgreementTable;
    }

    protected abstract void addWholesaleContextMenuEntries(PreAgreementTable preAgreementTable);

    protected abstract void createCustomContextMenu(AKActionGroup actionGroup);

    private AKJPanel createButtonPanel() {
        // buttons
        ButtonGroup btnGroup = new ButtonGroup();
        btnEigeneTasks = getSwingFactory().createToggleButton(EIGENE_TASKS, getActionListener(), false, btnGroup);
        btnTeamTasks = getSwingFactory().createToggleButton(TEAM_TASKS, getActionListener(), false, btnGroup);
        btnAlleTasks = getSwingFactory().createToggleButton(ALLE_TASKS, getActionListener(), true, btnGroup);

        cbAdditionalFilters = getSwingFactory().createComboBox(
                ADDITIONAL_FILTERS,
                new AKCustomListCellRenderer<>(PreAgreementTableModel.PreAgreementTableFilterEntry.class,
                        PreAgreementTableModel.PreAgreementTableFilterEntry::getDescription)
        );
        cbAdditionalFilters.addActionListener(getActionListener());

        btnAkmTr = getSwingFactory().createButton(AKMTR_BTN, getActionListener());
        btnAbbmTr = getSwingFactory().createButton(ABBMTR_BTN, getActionListener());
        btnTv = getSwingFactory().createButton(TV_BTN, getActionListener());
        btnAbbm = getSwingFactory().createButton(ABBM_SENDEN_BTN, getActionListener());
        btnStorno = getSwingFactory().createButton(STORNO_BTN, getActionListener());
        btnCloseIssue = getSwingFactory().createButton(CLOSE_ISSUE_BTN, getActionListener());
        btnForceClosePreagreement = getSwingFactory().createButton(FORCE_CLOSE_PREAGREEMENT_BTN, getActionListener());
        btnAnswerVa = getSwingFactory().createButton(ANSWER_VA_BTN, getActionListener());
        btnAssignTaifunOrder = getSwingFactory().createButton(ASSIGN_TAIFUN_ORDER_BTN, getActionListener());
        btnSendErlm = getSwingFactory().createButton(SEND_ERLM_BTN, getActionListener());
        btnTaskBearbeiten = getSwingFactory().createButton(TASK_BEARBEITEN_BTN, getActionListener());
        AKJButton btnHistory = getSwingFactory().createButton(HISTORY_BTN, getActionListener());

        // @formatter:off
        AKJPanel filtersPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("filters"));
        filtersPnl.add(btnEigeneTasks,       GBCFactory.createGBC(  0,  0, 0,  0, 1, 1, GridBagConstraints.NONE));
        filtersPnl.add(btnTeamTasks,         GBCFactory.createGBC(  0,  0, 1,  0, 1, 1, GridBagConstraints.NONE));
        filtersPnl.add(btnAlleTasks,         GBCFactory.createGBC(  0,  0, 2,  0, 1, 1, GridBagConstraints.NONE));
        filtersPnl.add(new AKJPanel(),       GBCFactory.createGBC(  0,  0, 3,  0, 1, 1, GridBagConstraints.HORIZONTAL));
        filtersPnl.add(cbAdditionalFilters, GBCFactory.createGBC(100,  0, 0,  1, 5, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel customFunctionsPnl = createCustomFunctionsPanel();
        AKJPanel functionsPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("functions"));
        functionsPnl.add(btnTaskBearbeiten,         GBCFactory.createGBC(  0,  0, 0,  0, 1, 1, GridBagConstraints.HORIZONTAL));
        functionsPnl.add(new AKJPanel(),            GBCFactory.createGBC(  0,  0, 0,  1, 1, 1, GridBagConstraints.NONE));
        functionsPnl.add(customFunctionsPnl,        GBCFactory.createGBC(100,  0, 0,  2, 1, 1, GridBagConstraints.HORIZONTAL));
        functionsPnl.add(new AKJPanel(),            GBCFactory.createGBC(  0,  0, 0,  3, 1, 1, GridBagConstraints.NONE));
        functionsPnl.add(btnCloseIssue,             GBCFactory.createGBC(  0,  0, 0,  4, 1, 1, GridBagConstraints.HORIZONTAL));
        functionsPnl.add(new AKJPanel(),            GBCFactory.createGBC(  0,  0, 0,  5, 1, 1, GridBagConstraints.NONE));
        functionsPnl.add(btnHistory,                GBCFactory.createGBC(  0,  0, 0,  6, 1, 1, GridBagConstraints.HORIZONTAL));
        functionsPnl.add(new AKJPanel(),            GBCFactory.createGBC(  0,  0, 0,  7, 1, 1, GridBagConstraints.NONE));
        functionsPnl.add(btnForceClosePreagreement, GBCFactory.createGBC(  0,  0, 0,  8, 1, 1, GridBagConstraints.HORIZONTAL));
        functionsPnl.add(new AKJPanel(),            GBCFactory.createGBC(  0,100, 0,  9, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(filtersPnl,      GBCFactory.createGBC(  0,  0, 0,  0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(functionsPnl,    GBCFactory.createGBC(  0,100, 0,  1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        return btnPnl;
    }

    protected abstract AKJPanel createCustomFunctionsPanel();

    private AKJPanel createBasicInformationPanel() {
        // labels
        lblKName = getSwingFactory().createLabel(KUNDE_NAME);
        lblKVorname = getSwingFactory().createLabel(KUNDE_VORNAME);
        lblKFirma = getSwingFactory().createLabel(KUNDE_FIRMA);
        lblKFirmazusatz = getSwingFactory().createLabel(KUNDE_FIRMAZUSATZ);
        AKJLabel lblKTyp = getSwingFactory().createLabel(KUNDE_TYP);
        AKJLabel lblStandortStrasse = getSwingFactory().createLabel(STANDORT_STRASSE);
        AKJLabel lblStandortPLZOrt = getSwingFactory().createLabel(STANDORT_PLZ_ORT);
        AKJLabel lblWechseltermin = getSwingFactory().createLabel(WECHSELTERMIN);
        AKJLabel lblAnfrageKWT = getSwingFactory().createLabel(ANFRAGE_KWT);
        AKJLabel lblMnetTech = getSwingFactory().createLabel(MNET_TECHNOLOGIE);
        AKJLabel lblStrTVIds = getSwingFactory().createLabel(STR_TV_ID);
        AKJLabel lblStrAenVorabstimmung = getSwingFactory().createLabel(STR_AEN_GF_VORABSTIMMUNGSID);
        AKJLabel lblStrAenWechseltermin = getSwingFactory().createLabel(STR_AEN_GF_WECHSELTERMIN);
        AKJLabel lblAlleRufnummern = getSwingFactory().createLabel(ALLE_RUFNUMMERN);
        AKJLabel lblZeitfenster = getSwingFactory().createLabel(ZEITFENSTER);
        AKJLabel lblPkiauf = getSwingFactory().createLabel(PKIAUF);
        AKJLabel lblOther = getSwingFactory().createLabel(SONSTIGES, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblBearbeitungsstatus = getSwingFactory().createLabel(BEARBEITUNGSSTATUS);

        lblTaifunKuendigungsInfo = getSwingFactory().createLabel(TAIFUN_KUENDIGUNGSDATUM);
        lblTaifunKuendigungsInfo.setVisible(false);
        lblKlaerfall = getSwingFactory().createLabel(KLAERFALL);
        lblKlaerfall.setVisible(false);
        lblOffeneTvOrStorno = getSwingFactory().createLabel(OPEN_TV_OR_STORNO);
        lblOffeneTvOrStorno.setVisible(false);
        lblOverdueVa = getSwingFactory().createLabel(OVERDUE_VA);
        lblOverdueVa.setVisible(false);
        lblOverdueVa.convertCurrentTextToMultiline(50);

        // fields
        tfKTyp = getSwingFactory().createTextField(KUNDE_TYP, false);
        tfKName = getSwingFactory().createTextField(KUNDE_NAME, false);
        tfKVorname = getSwingFactory().createTextField(KUNDE_VORNAME, false);
        tfKFrima = getSwingFactory().createTextField(KUNDE_FIRMA, false);
        tfKFirmazusatz = getSwingFactory().createTextField(KUNDE_FIRMAZUSATZ, false);
        makeEndkundeVisible(false);
        tfStandortStrasse = getSwingFactory().createTextField(STANDORT_STRASSE, false);
        tfStandortPLZ = getSwingFactory().createTextField(STANDORT_PLZ, false);
        tfStandortOrt = getSwingFactory().createTextField(STANDORT_ORT, false);
        AKJLabel lblTaifunOrderIds = getSwingFactory().createLabel(TAIFUN_ORDER_IDS);
        taTaifunOrderIds = getSwingFactory().createTextArea(TAIFUN_ORDER_IDS, false);
        AKJLabel lblAutomatable = getSwingFactory().createLabel(AUTOMATABLE);
        chbAutomatable = getSwingFactory().createCheckBox(AUTOMATABLE, false);
        chbAutomatable.addActionListener((ActionEvent actionEvent) -> {
            wbciGeschaeftsfallService.markGfAsAutomatable(selectedWbciRequest.getWbciGeschaeftsfall().getId(), chbAutomatable.isSelected());
            loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);
        });

        AKJScrollPane spTaifunOrderIds = new AKJScrollPane(taTaifunOrderIds);
        dcAnfrageKWT = getSwingFactory().createDateComponent(ANFRAGE_KWT_DATE, false);
        dcWechseltermin = getSwingFactory().createDateComponent(WECHSELTERMIN, false);
        tfMnetTechnologie = getSwingFactory().createTextField(MNET_TECHNOLOGIE, false);
        tfZeitfenster = getSwingFactory().createTextField(ZEITFENSTER, false);
        tfPkiAuf = getSwingFactory().createTextField(PKIAUF, false);
        chbAlleRufnummern = getSwingFactory().createCheckBox(ALLE_RUFNUMMERN, false);
        taBearbeitungsstatus = getSwingFactory().createTextArea(BEARBEITUNGSSTATUS, false);
        taBearbeitungsstatus.setMaxChars(255);
        taBearbeitungsstatus.addMouseListener(new AddStatusBemMouseListener());
        AKJScrollPane spBearbeitungsstatus = new AKJScrollPane(taBearbeitungsstatus);

        tfStrAenGfVorabstimmung = getSwingFactory().createTextField(STR_AEN_GF_VORABSTIMMUNGSID, false);
        dcStrAenGfWechseltermin = getSwingFactory().createDateComponent(STR_AEN_GF_WECHSELTERMIN, false);

        tbMdlRufnummern = new RufnummernPortierungTableModel();
        tbRufnummern = new RufnummernPortierungTable(
                tbMdlRufnummern, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRufnummern.fitTable(new int[] { 70, 40, 70, 40, 37, 37, 44, 44 });
        AKJScrollPane spRufnummern = new AKJScrollPane(tbRufnummern, new Dimension(395, 0));

        if (getMnetCarrierRole().equals(CarrierRole.AUFNEHMEND)) {
            AKActionGroup dnAutomationGroup = new AKActionGroup("DN-Automatisierung");
            dnAutomationGroup.addAction(automationAddDialNumberAction);
            dnAutomationGroup.addAction(automationDeleteDialNumberAction);

            tbRufnummern.addPopupGroup(dnAutomationGroup);
        }

        taStrTVIds = getSwingFactory().createTextArea(STR_TV_TA, false);
        AKJScrollPane spStrTVIds = new AKJScrollPane(taStrTVIds);

        // @formatter:off
        AKJPanel customerPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("customer"));
        int y = 0;
        customerPnl.add(lblKTyp             , GBCFactory.createGBC(  0,  0, 0,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 1,   y, 1, 1, GridBagConstraints.BOTH));
        customerPnl.add(tfKTyp              , GBCFactory.createGBC(  0,  0, 2,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblKName            , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(tfKName             , GBCFactory.createGBC(  0,  0, 2,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblKVorname         , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(tfKVorname          , GBCFactory.createGBC(  0,  0, 2,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblKFirma           , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(tfKFrima            , GBCFactory.createGBC(  0,  0, 2,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblKFirmazusatz     , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(tfKFirmazusatz      , GBCFactory.createGBC(  0,  0, 2,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblStandortStrasse  , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(tfStandortStrasse   , GBCFactory.createGBC(  0,  0, 2,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblStandortPLZOrt   , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(tfStandortPLZ       , GBCFactory.createGBC(  0,  0, 2,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(tfStandortOrt       , GBCFactory.createGBC(  0,  0, 3,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblTaifunOrderIds   , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(spTaifunOrderIds    , GBCFactory.createGBC(  0,  0, 2,   y, 2, 2, GridBagConstraints.HORIZONTAL));
        customerPnl.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.NONE));
        customerPnl.add(lblAutomatable      , GBCFactory.createGBC(  0,  0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(chbAutomatable      , GBCFactory.createGBC(  0,  0, 2,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblKlaerfall        , GBCFactory.createGBC(100,  0, 0, ++y, 5, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblOffeneTvOrStorno , GBCFactory.createGBC(100,  0, 0, ++y, 5, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(lblOverdueVa     , GBCFactory.createGBC(100,  0, 0, ++y, 5, 1, GridBagConstraints.HORIZONTAL));
        customerPnl.add(new AKJPanel()      , GBCFactory.createGBC(100,100, 4, ++y, 1, 1, GridBagConstraints.BOTH));

        AKJPanel datePnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("date"));
        y = 0;
        datePnl.add(lblAnfrageKWT           , GBCFactory.createGBC(  0,  0, 0, y   ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(new AKJPanel()          , GBCFactory.createGBC(  0,  0, 1, y   ,   1, 1, GridBagConstraints.NONE));
        datePnl.add(dcAnfrageKWT            , GBCFactory.createGBC(  0,  0, 2, y   ,   2, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(lblWechseltermin        , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(dcWechseltermin         , GBCFactory.createGBC(  0,  0, 2, y   ,   2, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(lblTaifunKuendigungsInfo, GBCFactory.createGBC(  0,  0, 0, ++y , 100, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(new AKJLabel(" ")       , GBCFactory.createGBC(  0,  0, 1, y   ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(new AKJPanel()          , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.NONE));
        datePnl.add(lblOther                , GBCFactory.createGBC(  0,  0, 0, ++y ,   3, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(lblBearbeitungsstatus   , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(spBearbeitungsstatus    , GBCFactory.createGBC(  0,100, 2, y   ,   2, 2, GridBagConstraints.BOTH));
        datePnl.add(new AKJPanel()          , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.NONE));
        datePnl.add(lblMnetTech             , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(tfMnetTechnologie       , GBCFactory.createGBC(  0,  0, 2, y   ,   2, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(lblStrTVIds             , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(spStrTVIds              , GBCFactory.createGBC(  0,100, 2, y   ,   2, 2, GridBagConstraints.BOTH));
        datePnl.add(new AKJPanel()          , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.NONE));
        datePnl.add(lblStrAenVorabstimmung  , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(tfStrAenGfVorabstimmung , GBCFactory.createGBC(  0,  0, 2, y   ,   2, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(lblStrAenWechseltermin  , GBCFactory.createGBC(  0,  0, 0, ++y ,   1, 1, GridBagConstraints.HORIZONTAL));
        datePnl.add(dcStrAenGfWechseltermin , GBCFactory.createGBC(  0,  0, 2, y   ,   2, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel rufnummernPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(RUFNUMMERN));
        rufnummernPnl.add(lblAlleRufnummern , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        rufnummernPnl.add(chbAlleRufnummern , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        rufnummernPnl.add(lblZeitfenster    , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        rufnummernPnl.add(tfZeitfenster     , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        rufnummernPnl.add(spRufnummern      , GBCFactory.createGBC(100,100, 0, 3, 2, 1, GridBagConstraints.BOTH));
        rufnummernPnl.add(lblPkiauf         , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        rufnummernPnl.add(tfPkiAuf          , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        rufnummernPnl.add(new AKJPanel()    , GBCFactory.createGBC(100, 50, 2, 5, 1, 1, GridBagConstraints.BOTH));

        AKJPanel baseInformationPnl = new AKJPanel(new GridBagLayout());
        baseInformationPnl.add(customerPnl      , GBCFactory.createGBC( 10,100, 0, 0, 1, 1, GridBagConstraints.BOTH, 10));
        baseInformationPnl.add(datePnl          , GBCFactory.createGBC( 10,100, 1, 0, 1, 1, GridBagConstraints.BOTH, 10));
        baseInformationPnl.add(rufnummernPnl    , GBCFactory.createGBC( 10,100, 2, 0, 1, 1, GridBagConstraints.BOTH, 10));
        baseInformationPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 3, 0, 1, 1, GridBagConstraints.BOTH));

        // @formatter:on
        return baseInformationPnl;
    }

    private AKJPanel createDetailsPanel() {
        vaTree = new VaTree();
        vaTree.addTreeWillExpandListener(new DynamicTreeWillExpandListener());
        vaTree.setShowsRootHandles(true);
        vaTree.setCellRenderer(new DynamicTreeRenderer());
        vaTree.addTreeSelectionListener(new VaTreeSelectionListener(this));
        vaTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(vaTree);

        rootNode = new VaRootTreeNode(vaTree, null);
        treeModel = new DefaultTreeModel(rootNode, false);
        vaTree.setModel(treeModel);
        AKJScrollPane spTree = new AKJScrollPane(vaTree, new Dimension(250, 150));

        selectedTreeNodeDetailPanel = new AKJPanel(new BorderLayout());

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout());
        detailPnl.add(spTree                        , GBCFactory.createGBC( 10,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        detailPnl.add(selectedTreeNodeDetailPanel   , GBCFactory.createGBC(100,100, 1, 0, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        return detailPnl;
    }

    private AKJPanel createCommentPanel() {
        taBemerkungen = getSwingFactory().createTextArea(BEMERKUNGEN_TEXTAREA, true, true, true);
        AKJScrollPane spBemerkungen = new AKJScrollPane(taBemerkungen);

        btnSaveBemerkung = getSwingFactory().createButton(SAVE_BEMERKUNG_BTN, getActionListener());

        // @formatter:off
        AKJPanel bemerkungenPnl = new AKJPanel(new GridBagLayout());
        bemerkungenPnl.add(spBemerkungen    , GBCFactory.createGBC(100,100, 0, 0, 2, 1, GridBagConstraints.BOTH));
        bemerkungenPnl.add(btnSaveBemerkung , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        bemerkungenPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 2, 2, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        AKJPanel panel = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(COMMENTS));
        panel.add(bemerkungenPnl, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        return panel;
    }

    private AKJPanel createAutomationTaskPanel() {
        tbMdlAutomationTasks = new AutomationTaskTableModel(false);
        AKJTable tbAutomationTasks = new AKJTable(tbMdlAutomationTasks, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAutomationTasks.getColumnModel().getColumn(AutomationTaskTableModel.COL_COMPLETED_AT).setCellRenderer(
                new DateTimeTableCellRenderer());
        tbAutomationTasks.getColumnModel().getColumn(AutomationTaskTableModel.COL_CREATED_AT).setCellRenderer(
                new DateTimeTableCellRenderer());
        tbAutomationTasks.fitTable(new int[] { 50, 225, 50, 110, 95, 110, 450 });
        tbAutomationTasks.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spAutomationTasks = new AKJScrollPane(tbAutomationTasks, new Dimension(500, 200));

        // @formatter:off
        AKJPanel automationTasksPnl = new AKJPanel(new GridBagLayout());
        automationTasksPnl.add(spAutomationTasks  , GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel panel = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(AUTOMATION_TASKS_TITLE));
        panel.add(automationTasksPnl   , GBCFactory.createGBC( 10, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        return panel;
    }

    private AKJPanel createWholesalePanel() {
        tbMdlWholesaleAudits = new WholesaleAuditTableModel();
        ShowWholesaleAuditXmlAction showWholesaleAuditXmlAction = new ShowWholesaleAuditXmlAction("XML anzeigen", WHOLESALE_SHOW_XML);

        AKJTable tbWholesaleAudits = new AKJTable(tbMdlWholesaleAudits, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbWholesaleAudits.setFilterEnabled(false);
        tbWholesaleAudits.addPopupAction(showWholesaleAuditXmlAction);
        tbWholesaleAudits.getColumnModel().getColumn(WholesaleAuditTableModel.COL_DATUM).setCellRenderer(
                new DateTimeTableCellRenderer());
        tbWholesaleAudits.addTableListener(details -> selectedAudit = tbMdlWholesaleAudits.getDataAtRow(tbWholesaleAudits.getSelectedRow()));
        AKJScrollPane spWholesaleAudits = new AKJScrollPane(tbWholesaleAudits, new Dimension(500, 200));

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(spWholesaleAudits, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        return panel;
    }

    /**
     * Handles the menu entry in the Wholesale panel that shows the request XML.
     */
    private final class ShowWholesaleAuditXmlAction extends AKAbstractAction {


        ShowWholesaleAuditXmlAction(String name, String actionCommand) {
            super();
            setName(name);
            setActionCommand(actionCommand);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            MessageHelper.showInfoDialogWithXml(AbstractPreAgreementOverviewPanel.this, new Dimension(800, 650), selectedAudit.getRequestXml(), "XML für Nachricht " + selectedAudit.getBeschreibung(), null, false);
        }
    }

    /**
     * load the initial data from the database
     */
    @Override
    public final void loadData() {
        loadDataAndFocus(null);
        final int lastSelectedIndex = cbAdditionalFilters.getSelectedIndex();
        if (cbAdditionalFilters.getItemCount() == 0) {
            cbAdditionalFilters.addItems(Arrays.asList(
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(REMOVE_FILTER),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(ACTIVE.getDescription(),
                            FilterRelations.OR, PreAgreementTableFilter.ACTIVE, PreAgreementTableFilter.NEW_VA),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(PASSIVE.getDescription(),
                            FilterRelations.AND, PreAgreementTableFilter.PASSIVE),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(NEW_VA.getDescription(),
                            FilterRelations.AND, PreAgreementTableFilter.NEW_VA),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(NEW_VA_EXPIRED.getDescription(),
                            FilterRelations.AND, PreAgreementTableFilter.NEW_VA_EXPIRED),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(DUE_SOON_FILTER,
                            FilterRelations.OR, PreAgreementTableFilter.DUE_SOON_MNET,
                            PreAgreementTableFilter.DUE_SOON_PARTNER),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(KLAERFAELLE,
                            FilterRelations.AND, PreAgreementTableFilter.KLAERFAELLE),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(POSITIVE_MELDUNGEN, FilterRelations.OR,
                            PreAgreementTableFilter.AKM_TR, PreAgreementTableFilter.RUEM_VA,
                            PreAgreementTableFilter.ERLM),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(NEGATIVE_MELDUNGEN,
                            FilterRelations.OR, PreAgreementTableFilter.ABBM,
                            PreAgreementTableFilter.ABBM_TR),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(SENT_TODAY,
                            FilterRelations.OR, PreAgreementTableFilterRelation.MELDUNG_OUTGOING_PROCESSED_TODAY,
                            PreAgreementTableFilterRelation.REQUEST_OUTGOING_PROCESSED_TODAY),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(RECEIVED_TODAY,
                            FilterRelations.OR, PreAgreementTableFilterRelation.MELDUNG_INCOMING_PROCESSED_TODAY,
                            PreAgreementTableFilterRelation.REQUEST_INCOMING_PROCESSED_TODAY),
                    // PK and GK Filters
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(WS,
                            FilterRelations.AND, PreAgreementTableFilter.WS),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(PK,
                            FilterRelations.AND, PreAgreementTableFilter.PK),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(PK_WITHOUT_TEAM,
                            FilterRelations.AND, PreAgreementTableFilter.PK, PreAgreementTableFilter.NO_TEAM_SET),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(GK,
                            FilterRelations.AND, PreAgreementTableFilter.GK),
                    new PreAgreementTableModel.PreAgreementTableFilterEntry(GK_WITHOUT_TEAM,
                            FilterRelations.AND, PreAgreementTableFilter.GK, PreAgreementTableFilter.NO_TEAM_SET)
            ));
        }
        cbAdditionalFilters.setSelectedIndex(lastSelectedIndex != -1 ? lastSelectedIndex : 1);
    }

    /**
     * refresh the current table and set the focus. If the row could not be found, no focus will be set.
     *
     * @param preAgreementID of the row, which should be focused. If the preAgreementID is NULL, no row will selected
     */
    private void refresh(String preAgreementID) {
        loadDataAndFocus(preAgreementID);
    }

    private void loadDataAndFocus(final String preAgreementID) {
        loadDataAndFocus(preAgreementID, true);
    }

    private void loadDataAndFocus(final String preAgreementID, boolean reloadDataFromBackend) {  //NOSONAR; cyclomatic complexity is high, but i do not see a way to avoid this
        // worker definition
        try {
            final SwingWorker<Collection<PreAgreementVO>, Void> worker = new SwingWorker<Collection<PreAgreementVO>, Void>() {
                @Override
                public Collection<PreAgreementVO> doInBackground() throws Exception {
                    if (preAgreementID != null && !reloadDataFromBackend && !preAgreementTableModel.getData().isEmpty()) {
                        // replace only one VA in the already loaded list
                        Collection<PreAgreementVO> loadedVOs = preAgreementTableModel.getData(false);  // false because it is filtered out separatelly
                        Collection<PreAgreementVO> currentVO = wbciCommonService.findPreAgreements(getMnetCarrierRole(), preAgreementID);

                        List<PreAgreementVO> filteredVOs = loadedVOs.stream()
                                .filter(vo -> !StringUtils.equals(vo.getVaid(), preAgreementID))
                                .collect(Collectors.toList());

                        if (CollectionUtils.isNotEmpty(currentVO)) {
                            int rowNumOfPreAgreementId = getCurrentRowNumOfPreAgreementId(preAgreementID);
                            int insertAt = (rowNumOfPreAgreementId >= 0) ? rowNumOfPreAgreementId : 0;

                            filteredVOs.addAll(insertAt, currentVO);
                        }
                        return filteredVOs;
                    }
                    else if (!reloadDataFromBackend
                            && preAgreementTableModel != null
                            && preAgreementTableModel.getData() != null
                            && !preAgreementTableModel.getData().isEmpty()) {
                        //data is loaded , data reload is not required => return already loaded list
                        return preAgreementTableModel.getData(true);
                    }
                    else {
                        return wbciCommonService.findPreAgreements(getMnetCarrierRole());
                    }
                }

                @Override
                protected void done() {
                    try {
                        preAgreementTableModel.setData(get());
                        if (preAgreementTableModel.isOnlyMyTasksFilterSet()) {
                            AbstractPreAgreementOverviewPanel.this.execute(EIGENE_TASKS);
                        }
                        else if (preAgreementTableModel.isOnlyTeamTasksFilterSet()) {
                            AbstractPreAgreementOverviewPanel.this.execute(TEAM_TASKS);
                        }
                        if (preAgreementTableModel.isDropdownSelectionFilterActive()) {
                            AbstractPreAgreementOverviewPanel.this.execute(ADDITIONAL_FILTERS);
                        }

                        // Select the preAgreement if the ID is set
                        if (isEmpty(preAgreementID)) {
                            if (selectedWbciRequest != null) {
                                selectAndFocusPreAgreement(selectedWbciRequest.getVorabstimmungsId());
                            }
                            else {
                                showDetails(null);
                            }
                        }
                        else {
                            selectAndFocusPreAgreement(preAgreementID);
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e, e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        setDefaultCursor();
                    }
                }

                private void selectAndFocusPreAgreement(String preAgreementId) {
                    int rowNumberOfFocus = getCurrentRowNumOfPreAgreementId(preAgreementId);
                    if (rowNumberOfFocus >= 0) {
                        preAgreementTable.selectAndScrollToRow(rowNumberOfFocus);
                        showDetails(preAgreementTableModel.getDataAtRow(rowNumberOfFocus));
                    }
                    else {
                        // the PreagreementVO could not be found (hidden by filter or perhaps the GF is closed) so
                        // reset the details panel.
                        showDetails(null);
                    }
                }

                private int getCurrentRowNumOfPreAgreementId(String preAgreementId) {
                    int rowNumberOfFocus = -1;
                    for (int i = 0; i < preAgreementTableModel.getRowCount() && rowNumberOfFocus == -1; i++) {
                        if (preAgreementTableModel.getDataAtRow(i).getVaid().equals(preAgreementId)) {
                            rowNumberOfFocus = i;
                        }
                    }
                    return rowNumberOfFocus;
                }
            };

            // execute data options
            setWaitCursor();
            // cleanup loaded data if needed only
            if (reloadDataFromBackend) {
                // HUR-22818 keep filter even by F5 reload
                preAgreementTableModel.removeAllButKeepFilter();
            }
            worker.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void showDetails(Object details) {
        this.selectedWbciRequest = null;
        this.selectedPreagreementVo = null;
        this.requestsForSelectedItem = null;
        RueckmeldungVorabstimmung ruemva;
        UebernahmeRessourceMeldung akmTr;

        // GUI zurueck setzen
        GuiTools.cleanFields(this, cbAdditionalFilters);
        tbMdlRufnummern.clearFilter();
        lblTaifunKuendigungsInfo.setVisible(false);
        chbAutomatable.setEnabled(false);
        // default show Name / Vorname fields
        makeEndkundeVisible(false);
        lblKlaerfall.setVisible(false);
        lblOffeneTvOrStorno.setVisible(false);
        lblOverdueVa.setVisible(false);
        tbMdlRufnummern.setData(null);
        tbMdlAutomationTasks.setData(null);
        if (isFeatureEnabled(WHOLESALE_PV)) {
            tbMdlWholesaleAudits.setData(null);
        }

        if (details instanceof PreAgreementVO) {
            try {
                this.selectedPreagreementVo = (PreAgreementVO) details;
                this.selectedWbciRequest = wbciCommonService.findWbciRequestById(selectedPreagreementVo
                        .getWbciRequestId());
                this.requestsForSelectedItem = wbciCommonService.findWbciRequestByType(
                        selectedWbciRequest.getVorabstimmungsId(), WbciRequest.class);
                akmTr = wbciCommonService.findLastForVaId(selectedWbciRequest.getVorabstimmungsId(),
                        UebernahmeRessourceMeldung.class);
                ruemva = wbciCommonService.findLastForVaId(selectedWbciRequest.getVorabstimmungsId(),
                        RueckmeldungVorabstimmung.class);
                if (StringUtils.isNotBlank(selectedWbciRequest.getWbciGeschaeftsfall().getStrAenVorabstimmungsId())) {
                    WbciGeschaeftsfall strAenGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(selectedWbciRequest.getWbciGeschaeftsfall()
                            .getStrAenVorabstimmungsId());
                    tfStrAenGfVorabstimmung.setText(strAenGeschaeftsfall.getVorabstimmungsId());
                    if (strAenGeschaeftsfall.getWechseltermin() != null) {
                        dcStrAenGfWechseltermin.setDate(DateConverterUtils.asDate(strAenGeschaeftsfall.getWechseltermin()));
                    }
                }
                tbMdlAutomationTasks.setData(selectedWbciRequest.getWbciGeschaeftsfall().getAutomationTasks());
                lblKlaerfall.setVisible(BooleanTools.nullToFalse(this.selectedWbciRequest.getWbciGeschaeftsfall()
                        .getKlaerfall()));
                lblOffeneTvOrStorno.setVisible(hasOpenStornoOrTv(selectedWbciRequest, requestsForSelectedItem));
                lblOverdueVa.setVisible(hasGeschaeftsfallStatus(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED));

                dcAnfrageKWT.setDate(selectedPreagreementVo.getVorgabeDatum());
                taBearbeitungsstatus.setText(selectedPreagreementVo.getInternalStatus());
                if (selectedPreagreementVo.getAutomatable() != null) {
                    chbAutomatable.setSelected(selectedPreagreementVo.getAutomatable());
                }
                chbAutomatable.setEnabled(isEditAutomatedEnabled());

                refreshTree(requestsForSelectedItem);

                // load Taifun Kuendigungsinfo on incomming VA-Requests
                loadTaifunKuendigungstermin(selectedWbciRequest);

                PersonOderFirma endkunde = selectedWbciRequest.getWbciGeschaeftsfall().getEndkunde();
                if (endkunde != null) {
                    if (endkunde.getKundenTyp() != null) {
                        tfKTyp.setText(endkunde.getKundenTyp().getBezeichnung());
                    }
                    if (endkunde instanceof Person) {
                        tfKName.setText(((Person) endkunde).getNachname());
                        tfKVorname.setText(((Person) endkunde).getVorname());
                        makeEndkundeVisible(false);

                    }
                    else if (endkunde instanceof Firma) {
                        tfKFrima.setText(((Firma) endkunde).getFirmenname());
                        tfKFirmazusatz.setText(((Firma) endkunde).getFirmennamenZusatz());
                        makeEndkundeVisible(true);
                    }
                }

                if (selectedWbciRequest.getWbciGeschaeftsfall() instanceof WbciGeschaeftsfallKue) {
                    Standort standort = ((WbciGeschaeftsfallKue) selectedWbciRequest.getWbciGeschaeftsfall())
                            .getStandort();
                    if (standort != null) {
                        tfStandortStrasse.setText(standort.getCombinedStreetData());
                        tfStandortPLZ.setText(standort.getPostleitzahl());
                        tfStandortOrt.setText(standort.getOrt());
                    }
                }

                taTaifunOrderIds.setText(WbciRequestHelper.extractTaifonOrderIds(selectedWbciRequest
                        .getWbciGeschaeftsfall()));

                if (selectedWbciRequest.getWbciGeschaeftsfall() instanceof RufnummernportierungAware) {
                    Rufnummernportierung portierung = ((RufnummernportierungAware) selectedWbciRequest
                            .getWbciGeschaeftsfall()).getRufnummernportierung();
                    if (portierung instanceof RufnummernportierungEinzeln) {
                        chbAlleRufnummern.setSelected(((RufnummernportierungEinzeln) portierung)
                                .getAlleRufnummernPortieren());
                    }

                    tfZeitfenster.setText(portierung.getPortierungszeitfenster().getDescription());
                    tfPkiAuf.setText(portierung.getPortierungskennungPKIauf());
                }

                if (akmTr != null) {
                    tfPkiAuf.setText(akmTr.getPortierungskennungPKIauf());
                }

                tfMnetTechnologie.setText(selectedPreagreementVo.getMnetTechnologie());
                taStrTVIds.setText(WbciRequestHelper.extractAenderungIds(requestsForSelectedItem));
                dcWechseltermin.setDate(selectedPreagreementVo.getWechseltermin());

                if (ruemva != null) {
                    tbMdlRufnummern.setData(
                            new RufnummernportierungVOBuilder()
                                    .withRufnummernportierungFromMeldung(ruemva.getRufnummernportierung())
                                    .withWbciRequest(selectedWbciRequest)
                                    .withRufnummern(getTaifunRufnummern())
                                    .withAkmTr(akmTr)
                                    .build()
                    );
                }
                else {
                    tbMdlRufnummern.setData(
                            new RufnummernportierungVOBuilder()
                                    .withWbciRequest(selectedWbciRequest)
                                    .withRufnummern(getTaifunRufnummern())
                                    .build()
                    );
                }

                taBemerkungen.setText(selectedWbciRequest.getWbciGeschaeftsfall().getBemerkungen());

                fillWholesaleTab();
            }
            catch (FindException | ServiceNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        validateButtons();
    }

    private void fillWholesaleTab() throws ServiceNotFoundException {
        if (selectedPreagreementVo.getPreAgreementType() == PreAgreementType.WS) {
            loadWholesaleData();
        }
    }

    @Override
    public void updateWholesaleData() {
        try {
            loadWholesaleData();
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error("Loading wholesaleData: ", e);
        }
    }

    private void loadWholesaleData() throws ServiceNotFoundException {
        if (isFeatureEnabled(WHOLESALE_PV)) {
            List<WholesaleAudit> audits = wholesaleOrderOutboundService.
                    loadWholesaleAuditsByVorabstimmungsId(selectedPreagreementVo.getVaid());
            tbMdlWholesaleAudits.setData(audits);
        }
    }

    private List<Rufnummer> getTaifunRufnummern() throws FindException {
        List<Rufnummer> rufnummern = new ArrayList<>();
        Long billingOrderNoOrig = selectedWbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig();
        if (billingOrderNoOrig != null) {
            List<Rufnummer> taiRufnummern = rufnummerService.findAllRNs4Auftrag(billingOrderNoOrig);
            if (taiRufnummern != null) {
                for (Rufnummer rufnummer : taiRufnummern) {
                    // histLast Rufnummer aus TAI ermitteln, um Information zu FutureCarrier zu erhalten
                    rufnummern.add(rufnummerService.findLastRN(rufnummer.getDnNoOrig()));
                }
            }
        }
        return rufnummern;
    }

    /**
     * makes ether 'Name / Vorname' or 'Firma / Firmenzusatz' visible
     */
    private void makeEndkundeVisible(boolean isCompany) {
        lblKName.setVisible(!isCompany);
        lblKVorname.setVisible(!isCompany);
        tfKName.setVisible(!isCompany);
        tfKVorname.setVisible(!isCompany);
        lblKFirma.setVisible(isCompany);
        lblKFirmazusatz.setVisible(isCompany);
        tfKFrima.setVisible(isCompany);
        tfKFirmazusatz.setVisible(isCompany);
    }

    /*
     * Prueft, ob ein evtl. vorhandener Storno/TV noch nicht beantwortet ist.
     */
    private boolean hasOpenStornoOrTv(WbciRequest currentWbciRequest, List<WbciRequest> wbciRequests) {
        List<Meldung> meldungen = wbciCommonService.findMeldungenForVaId(currentWbciRequest.getVorabstimmungsId());

        boolean hasOpenStornoOrTv = false;
        for (WbciRequest request : wbciRequests) {
            if (request instanceof StornoAnfrage) {
                List<Meldung> stornoMeldungen = wbciCommonService.filterMeldungenForStorno(meldungen,
                        ((StornoAnfrage) request).getAenderungsId());
                hasOpenStornoOrTv = CollectionTools.isEmpty(stornoMeldungen);
            }
            else if (request instanceof TerminverschiebungsAnfrage) {
                List<Meldung> tvMeldungen = wbciCommonService.filterMeldungenForTv(meldungen,
                        ((TerminverschiebungsAnfrage) request).getAenderungsId());
                hasOpenStornoOrTv = CollectionTools.isEmpty(tvMeldungen);
            }

            if (hasOpenStornoOrTv) {
                break;
            }
        }

        return hasOpenStornoOrTv;
    }

    /**
     * shows a warning if the Taifun order is already cancelled
     */
    private void loadTaifunKuendigungstermin(WbciRequest wbciRequest) {
        if (wbciRequest != null && WbciRequestStatus.VA_EMPFANGEN.equals(wbciRequest.getRequestStatus())
                && wbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig() != null) {

            LocalDateTime kuendigungstermin = wbciKuendigungsService.getTaifunKuendigungstermin(
                    wbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig());
            if (kuendigungstermin != null) {
                if (kuendigungstermin.isAfter(LocalDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.systemDefault()))) {
                    lblTaifunKuendigungsInfo.setText(String.format(getSwingFactory().getLabelText(TAIFUN_KUENDIGUNGSDATUM),
                            DateTools.formatDate(DateConverterUtils.asDate(kuendigungstermin), DateTools.PATTERN_DAY_MONTH_YEAR)));
                }
                else {
                    lblTaifunKuendigungsInfo.setText(getSwingFactory().getLabelText(TAIFUN_ORDER_CANCELLED));
                }
                lblTaifunKuendigungsInfo.setVisible(true);
                return;
            }
        }
        lblTaifunKuendigungsInfo.setVisible(false);
    }

    /* Aktualisiert den Tree mit der VA-Darstellung */
    private void refreshTree(List<WbciRequest> requestsForSelectedItem) {
        rootNode = new VaRootTreeNode(vaTree, requestsForSelectedItem);
        treeModel.setRoot(rootNode);

        try {
            vaTree.fireTreeWillExpand(new TreePath(rootNode.getPath()));
            vaTree.expandAll(new TreePath(rootNode.getPath()), true);

            vaTree.selectLatestWbciMessageInTree();
        }
        catch (ExpandVetoException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void showVaDetails(WbciEntity wbciEntity) {
        selectedTreeNodeDetailPanel.removeAll();

        Class<? extends AbstractVaDetailPanel> panelClazz = VA_DETAIL_PANEL_CONFIG.get(wbciEntity.getClass());
        try {
            if (panelClazz != null) {
                AbstractVaDetailPanel vaDetailPanel = panelClazz.newInstance();
                selectedTreeNodeDetailPanel.add(vaDetailPanel, BorderLayout.CENTER);

                vaDetailPanel.setVaModel(wbciEntity);
                vaDetailPanel.showVaDetails();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            selectedTreeNodeDetailPanel.revalidate();
            selectedTreeNodeDetailPanel.repaint();
        }
    }

    @Override
    protected void execute(String command) {
        switch (command) {
            case ADDITIONAL_FILTERS:
                applyDropDownSelectionFilter();
                break;
            case EIGENE_TASKS:
                preAgreementTableModel.showMyTasks();
                break;
            case TEAM_TASKS:
                preAgreementTableModel.showTeamTasks();
                break;
            case ALLE_TASKS:
                preAgreementTableModel.showAllTasks();
                break;
            case ANSWER_VA_BTN:
                showDecisionDialog();
                break;
            case AKMTR_BTN:
                showAkmTrDialog();
                break;
            case ABBMTR_BTN:
                showAbbmTrDialog();
                break;
            case TV_BTN:
                showTvDialog();
                break;
            case SEND_ERLM_BTN:
                showErlmDialog();
                break;
            case ABBM_SENDEN_BTN:
                showAbbmDialog();
                break;
            case STORNO_BTN:
                showStornoDialog();
                break;
            case CLOSE_ISSUE_BTN:
                showCloseIssueDialog();
                break;
            case FORCE_CLOSE_PREAGREEMENT_BTN:
                showForceClosePreagreementDialog();
                break;
            case ASSIGN_TAIFUN_ORDER_BTN:
                showTaifunOrderSelectDialog();
                break;
            case TASK_BEARBEITEN_BTN:
                toggleTask();
                break;
            case HISTORY_BTN:
                showHistoryDialog();
                break;
            case SAVE_BEMERKUNG_BTN:
                saveBemerkungen();
                break;
            default:
                break;
        }
        validateButtons();
    }

    private void applyDropDownSelectionFilter() {
        PreAgreementTableModel.PreAgreementTableFilterEntry filterEntry =
                (PreAgreementTableModel.PreAgreementTableFilterEntry) cbAdditionalFilters.getSelectedItem();
        // careful: We need to check and ignore nulls here... even though there are no NULL items within the combo!
        // The reason being that this method is also invoked before the GUI is fully initialized and before any items
        // have been added to the list.
        if (filterEntry != null) {
            if (REMOVE_FILTER.equals(filterEntry.getDescription())) {
                preAgreementTableModel.removeDropdownSelectionFilter();
            }
            else {
                preAgreementTableModel.filterByDropdownSelection(filterEntry);
            }
        }
    }

    private void showErlmDialog() {
        if (selectedWbciRequest instanceof TerminverschiebungsAnfrage) {
            ErlmTvDialog erlmDialog = new ErlmTvDialog((TerminverschiebungsAnfrage) selectedWbciRequest);
            Object o = DialogHelper.showDialog(getMainFrame(), erlmDialog, true, true);

            if (!DialogHelper.wasDialogCancelled(o)) {
                // HUR-22326 do not reload WHOLE data from BE
                loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);
            }
        }
        else if (selectedWbciRequest instanceof StornoAnfrage) {
            Antwortfrist antwortfrist = wbciDeadlineService.findAntwortfrist(selectedWbciRequest.getTyp(), WbciRequestStatus.STORNO_ERLM_VERSENDET);
            ErlmStornoDialog erlmDialog = new ErlmStornoDialog((StornoAnfrage) selectedWbciRequest,
                    antwortfrist);
            Object o = DialogHelper.showDialog(getMainFrame(), erlmDialog, true, true);

            if (!DialogHelper.wasDialogCancelled(o)) {
                loadData(); // load all data as wbci request was closed and disappeared from overview panel
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), ERLM_NOT_ALLOWED, null, true);
        }
    }

    private void showCarrierInfoDialog() {
        DialogHelper.showDialog(getMainFrame(), new CarrierInfoDialog(selectedWbciRequest), true, true);
    }

    private void saveBemerkungen() {
        String newCommentData = wbciCommonService.saveComment(selectedWbciRequest.getVorabstimmungsId(),
                taBemerkungen.getText(),
                HurricanSystemRegistry.instance().getCurrentUser());

        taBemerkungen.setText(newCommentData);
    }

    /**
     * Perform release task operation on common service.
     */
    private void releaseTask() {
        Long auftragId = selectedWbciRequest.getWbciGeschaeftsfall().getAuftragId();
        String vorabstimmungsId = selectedWbciRequest.getVorabstimmungsId();

        // check if a WITA order can be cancelled and ask if the user want to open the wita cancellation order.
        SortedSet<String> witaVertragsNrsForCancellation = getPossibleWitaVertragsNrsForCancellation(vorabstimmungsId);
        if (!witaVertragsNrsForCancellation.isEmpty() && shouldOpenWitaCancellationWizzard(witaVertragsNrsForCancellation)) {
            openWitaCancellationWizzard(vorabstimmungsId, witaVertragsNrsForCancellation);
        }

        if (isCloseGfEnabled()) {
            int closeGF = MessageHelper.showYesNoQuestion(getMainFrame(), CLOSE_VORGANG_QUESTION_LONG, CLOSE_VORGANG_QUESTION_SHORT);
            if (closeGF == JOptionPane.YES_OPTION) {
                wbciGeschaeftsfallService.closeGeschaeftsfall(selectedWbciRequest.getWbciGeschaeftsfall().getId());
            }
        }
        else if (isCloseProcessingEnabled()) {
            int closeProcessingGF = MessageHelper.showYesNoQuestion(getMainFrame(), CLOSE_ISSUE_QUESTION_LONG,
                    CLOSE_ISSUE_QUESTION_SHORT);
            if (closeProcessingGF == JOptionPane.YES_OPTION) {
                if (hasUncompletedAutomationTasks(selectedWbciRequest.getWbciGeschaeftsfall())) {
                    int uncompletedAutomationTasks = MessageHelper.showYesNoQuestion(getMainFrame(),
                            CLOSE_ISSUE_AUTOMATION_TASKS_QUESTION_LONG, CLOSE_ISSUE_AUTOMATION_TASKS_QUESTION_SHORT);
                    if (uncompletedAutomationTasks == JOptionPane.YES_OPTION) {
                        wbciCommonService.closeProcessing(selectedWbciRequest.getId());
                    }
                }
                else {
                    wbciCommonService.closeProcessing(selectedWbciRequest.getId());
                }
            }
        }

        wbciCommonService.releaseTask(vorabstimmungsId);
        loadDataAndFocus(vorabstimmungsId, false);  // do not reload data by task release

        if (isNewVorabstimmungPossible(selectedWbciRequest.getWbciGeschaeftsfall())) {
            HurricanAuftragHelper.openWbciElektronischerVorgangWizard(auftragId, vorabstimmungsId);
        }
    }

    /**
     * @return true, if the user want to open a wita cancellation dialog
     */
    private boolean shouldOpenWitaCancellationWizzard(Set<String> cancellableWitaVertNr) {
        String formattedVertrNr = StringUtils.join(cancellableWitaVertNr, ",");
        int cancelWitaOrder = MessageHelper.showYesNoQuestion(getMainFrame(),
                String.format(CANCELLABLE_WITA_ORDERS_QUESTION_LONG, formattedVertrNr),
                CANCELLABLE_WITA_ORDERS_QUESTION_SHORT);
        return cancelWitaOrder == JOptionPane.YES_OPTION;
    }

    /**
     * open the WITA cancellation wizard for every WITA VtrNr which should be cancelled
     *
     * @param vorabstimmungsId               VA-ID
     * @param witaVertragsNrsForCancellation WITA-Vertrags-Nrs which are cancelable
     */
    private void openWitaCancellationWizzard(String vorabstimmungsId, SortedSet<String> witaVertragsNrsForCancellation) {
        for (String witaVtrNr : witaVertragsNrsForCancellation) {
            Long orderIdForCancellation = getHurricanOrderIdForWitaVtrNrAndCurrentVA(witaVtrNr);

            if (orderIdForCancellation != null) {
                //open wita cancellation wizzard
                HurricanAuftragHelper.openWitaElektronischerVorgangWizard(
                        vorabstimmungsId, HurricanAuftragHelper.CBVorgangType.KUENDIGUNG);
            }
            else {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Es konnte kein Auftrag zu der WITA VtrNr {0} ermittelt werden.\n" +
                                "Bitte kuendigen Sie die Leitung nachträglich selbst.",
                        "Auftrag nicht gefunden",
                        new Object[] { witaVtrNr }, true);
            }
        }
    }


    /**
     * @return null or a valid hurrican order no for the assigned WITA-Vertrags-Nr.
     */
    private Long getHurricanOrderIdForWitaVtrNrAndCurrentVA(String witaVtrNr) {
        if (selectedWbciRequest == null) {
            return null;
        }

        return wbciCommonService.getHurricanOrderIdForWitaVtrNrAndCurrentVA(witaVtrNr,
                selectedWbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig(),
                selectedWbciRequest.getWbciGeschaeftsfall().getNonBillingRelevantOrderNoOrigs());
    }

    /**
     * @return all possible WITA-Vertragsnr, which can be cancelled, or an empty set.
     */
    private SortedSet<String> getPossibleWitaVertragsNrsForCancellation(String vorabstimmungsId) {
        if (isWitaVertragsNrCancellationPossible()) {
            return wbciKuendigungsService.getCancellableWitaVertragsnummern(vorabstimmungsId);
        }
        return new TreeSet<>();
    }


    /**
     * Checks if a new Vorabstimmung should be created. This is the case when the current Vorabstimmungn has been
     * cancelled via STR-AEN, M-Net is the receiving carrier and the gerschaeftsfall is not marked as a klaerfall.
     */
    private boolean isNewVorabstimmungPossible(WbciGeschaeftsfall gf) {
        boolean isReceivingCarrier = CarrierRole.AUFNEHMEND.equals(CarrierRole.lookupMNetCarrierRoleByCarrierCode(
                gf.getAufnehmenderEKP(), gf.getAbgebenderEKP()));
        boolean isNotKlaerfall = !Boolean.TRUE.equals(gf.getKlaerfall());
        boolean inStateNewVa = WbciGeschaeftsfallStatus.NEW_VA.equals(gf.getStatus());
        return isReceivingCarrier && inStateNewVa && isNotKlaerfall;
    }

    private boolean hasGeschaeftsfallStatus(WbciGeschaeftsfallStatus status) {
        return status.equals(this.selectedWbciRequest.getWbciGeschaeftsfall().getStatus());
    }

    /**
     * Gets the request status of the corresponding VA request. In case selected request is a TV or STORNO the VA is
     * queried by service call first.
     */
    private boolean hasVaRequestStatus(WbciRequestStatus status) {
        if (selectedWbciRequest instanceof TerminverschiebungsAnfrage || selectedWbciRequest instanceof StornoAnfrage) {
            return getAllGeschaeftsfallRequestStatuses().contains(status);
        }
        else {
            return this.selectedWbciRequest.getRequestStatus().equals(status);
        }
    }

    /**
     * Checks whether there are some uncompleted automation tasks assigned to the provided WbciGeschaeftsfall.
     */
    private boolean hasUncompletedAutomationTasks(WbciGeschaeftsfall wbciGeschaeftsfall) {
        List<AutomationTask> automationTasks = wbciGeschaeftsfall.getAutomationTasks();
        if (CollectionUtils.isEmpty(automationTasks)) {
            return false;
        }

        boolean isOpen = false;
        for (AutomationTask automationTask : automationTasks) {
            isOpen = isOpen || !automationTask.isDone();
        }
        return isOpen;
    }

    private void toggleTask() {
        try {
            if (isAssigned()) {
                releaseTask();
            }
            else {
                assignTask();
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Perform assign task operation on common service.
     */
    private void assignTask() {
        wbciCommonService.assignTask(selectedWbciRequest.getVorabstimmungsId(), HurricanSystemRegistry.instance()
                .getCurrentUser());
        loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);  // do not reload data by task assign
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den AKM-TR Dialog an. Voraussetzung: letzte Meldung ist vom Typ
     * RUEM-VA
     */
    private void showAkmTrDialog() {
        boolean isWholesaleRequest = selectedPreagreementVo.getPreAgreementType() == PreAgreementType.WS;
        AkmTrDialog akmTrDialog = new AkmTrDialog(selectedWbciRequest, isWholesaleRequest);
        Object retValue = DialogHelper.showDialog(getMainFrame(), akmTrDialog, true, true);
        if (!DialogHelper.wasDialogCancelled(retValue)) {
            loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);  // do not reload whole screen, one VA only
        }
    }

    /**
     * Oeffnet fuer den aktuellen Vorgang den Dialog für das Versenden der
     * {@link de.mnet.wbci.model.AbbruchmeldungTechnRessource}.
     */
    private void showAbbmTrDialog() {
        if (selectedWbciRequest instanceof VorabstimmungsAnfrage) {
            Object retValue = DialogHelper.showDialog(getMainFrame(), new AbbmTrDialog(
                            (VorabstimmungsAnfrage) selectedWbciRequest),
                    true, true);
            if (!DialogHelper.wasDialogCancelled(retValue)) {
                loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);  // do not reload whole screen, one VA only
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), ABBMTR_ONLY_VALID_AFTER_AKM_TR_INFO);
        }
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den TV Dialog an. Voraussetzung: letzte VA-Meldung ist vom Typ
     * RUEM-VA oder AKM-TR
     */
    private void showTvDialog() {
        TvDialog tvDialog = new TvDialog(selectedWbciRequest);
        Object retValue = DialogHelper.showDialog(getMainFrame(), tvDialog, true, true);
        if (!DialogHelper.wasDialogCancelled(retValue)) {
            loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);  // do not reload whole screen, one VA only
        }
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den Storno Dialog. Vorraussetzung fuer STR-Aufhebung ist immer
     * gegeben, Voraussetzung fuer STR-Aenderung: letzte VA-Meldung ist vom Typ RUEM-VA. Ist die VA noch vorgehalten
     * wird nach Bestaetigung die komplette VA mit Geschaeftsfall geloescht.
     */
    private void showStornoDialog() {
        if (selectedWbciRequest.getRequestStatus().isVorgehalten()) {
            try {
                int result = MessageHelper.showYesNoQuestion(getMainFrame(),
                        String.format(getSwingFactory().getText(STORNO_VORGEHALTEN_INFO), selectedWbciRequest.getTyp()
                                .name()),
                        String.format(getSwingFactory().getText(STORNO_VORGEHALTEN_TITLE), selectedWbciRequest.getTyp()
                                .name())
                );

                if (result == JOptionPane.YES_OPTION) {
                    if (hasVaRequestStatus(WbciRequestStatus.VA_VORGEHALTEN)) {
                        wbciGeschaeftsfallService.deleteGeschaeftsfall(selectedWbciRequest.getWbciGeschaeftsfall()
                                .getId());
                    }
                    else {
                        wbciGeschaeftsfallService.deleteStornoOrTvRequest(selectedWbciRequest.getId());
                    }
                    loadData();

                    MessageHelper.showInfoDialog(getMainFrame(),
                            String.format(getSwingFactory().getText(STORNO_VORGEHALTEN_SUCCESS), selectedWbciRequest
                                    .getTyp().name())
                    );
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        else {
            StornoDialog stornoDialog = new StornoDialog(selectedWbciRequest, requestsForSelectedItem);
            Object retValue = DialogHelper.showDialog(getMainFrame(), stornoDialog, true, true);
            if (!DialogHelper.wasDialogCancelled(retValue)) {
                loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);  // do not reload whole screen, one VA only
            }
        }
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den Close Issue Dialog.
     */
    private void showCloseIssueDialog() {
        IssueDialog issueDialog = new IssueDialog(selectedWbciRequest);
        Object o = DialogHelper.showDialog(getMainFrame(), issueDialog, true, true);
        if (!DialogHelper.wasDialogCancelled(o)) {
            // HUR-22326 do not reload WHOLE data from BE
            loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);
        }
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den Force Close Preagreement Dialog.
     */
    private void showForceClosePreagreementDialog() {
        Long gfId = selectedWbciRequest.getWbciGeschaeftsfall().getId();
        int result = MessageHelper
                .showYesNoQuestion(
                        this,
                        FORCE_CLOSE_VORGANG_QUESTION_LONG,
                        FORCE_CLOSE_VORGANG_QUESTION_SHORT);
        if (result == JOptionPane.YES_OPTION) {
            wbciGeschaeftsfallService.closeGeschaeftsfall(gfId);
            loadDataAndFocus(null);
        }
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den Manual Tasks Dialog.
     */
    private void showAutomationTasksDialog(AutomationTask automationTasks) {
        AutomationTaskDialog automationTaskDialog = new AutomationTaskDialog(automationTasks);
        Object result = DialogHelper.showDialog(getMainFrame(), automationTaskDialog, true, true);
        if (!DialogHelper.wasDialogCancelled(result)) {
            // HUR-22326 do not reload WHOLE data from BE
            loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);
        }
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den Dialog zur Beantwortung der Vorabstimmung an. Voraussetzung:
     * letzte Meldung ist noch nicht gesetzt (ABBM, RUEMVA wurde noch nicht verschickt).
     */
    private void showDecisionDialog() {
        DecisionDialog decisionDialog = new DecisionDialog(selectedWbciRequest);
        Object retValue = DialogHelper.showDialog(getMainFrame(), decisionDialog, true, true);
        if (!DialogHelper.wasDialogCancelled(retValue)) {
            // HUR-22326 do not reload WHOLE data from BE
            loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);
        }
    }

    /*
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den Dialog zur Senden einer ABBM zur TV. Voraussetzung:
     */
    private void showAbbmDialog() {
        AbstractAbbmDialog abbmDialog = null;
        if (selectedWbciRequest instanceof TerminverschiebungsAnfrage) {
            abbmDialog = new AbbmTvDialog((TerminverschiebungsAnfrage) selectedWbciRequest);
        }
        else if (selectedWbciRequest instanceof StornoAnfrage) {
            abbmDialog = new AbbmStornoDialog((StornoAnfrage) selectedWbciRequest);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), ABBM_NOT_ALLOWED_INFO, null, true);
        }

        if (abbmDialog != null) {
            Object o = DialogHelper.showDialog(getMainFrame(), abbmDialog, true, true);
            if (!DialogHelper.wasDialogCancelled(o)) {
                // HUR-22326 do not reload WHOLE data from BE
                loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);
            }
        }
    }

    /**
     * Öffnet für den ausgewählten Vorgang den History-Dialog
     */
    private void showHistoryDialog() {
        if (selectedWbciRequest != null && !isEmpty(selectedWbciRequest.getVorabstimmungsId())) {
            Set<String> vaIds = wbciCommonService.getPreAgreementIdsByOrderNoOrig(
                    selectedWbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig());

            HistoryByExtOrderNoDialog historyDialog;
            if (vaIds == null || vaIds.isEmpty()) {
                historyDialog = new HistoryByExtOrderNoDialog(selectedWbciRequest.getVorabstimmungsId());
            }
            else {
                historyDialog = new HistoryByExtOrderNoDialog(vaIds.toArray(new String[vaIds.size()]));
            }
            DialogHelper.showDialog(getMainFrame(), historyDialog, false, true);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), NO_VA_SELECTED_INFO, null, true);
        }
    }

    /**
     * Validiert die Buttons je nach Status der Vorabstimmung.
     */
    private void validateButtons() {
        validateButtonsAndActions();
        btnEigeneTasks.setEnabled(!preAgreementTableModel.isOnlyMyTasksFilterSet());
        myTasksFilterAction.setEnabled(!preAgreementTableModel.isOnlyMyTasksFilterSet());
        btnAlleTasks.setEnabled(preAgreementTableModel.isOnlyMyTasksFilterSet()
                || preAgreementTableModel.isOnlyTeamTasksFilterSet());
        allTasksFilterAction.setEnabled(preAgreementTableModel.isOnlyMyTasksFilterSet()
                || preAgreementTableModel.isOnlyTeamTasksFilterSet());
        btnTeamTasks.setEnabled(!preAgreementTableModel.isOnlyTeamTasksFilterSet());
        teamTasksFilterAction.setEnabled(!preAgreementTableModel.isOnlyTeamTasksFilterSet());

        // pre-select the drop-down filter
        final String selectionFilterValue = preAgreementTableModel.getDropdownSelectionFilterValue();
        final String filterValue = selectionFilterValue != null ? selectionFilterValue : "";
        cbAdditionalFilters.setSelectedItem(filterValue);
        activeFilterAction.setEnabled(!activeFilterAction.getActionCommand().equals(selectionFilterValue));
        passiveFilterAction.setEnabled(!passiveFilterAction.getActionCommand().equals(selectionFilterValue));
        newVaFilterAction.setEnabled(!newVaFilterAction.getActionCommand().equals(selectionFilterValue));
        newVaExpiredFilterAction.setEnabled(!newVaExpiredFilterAction.getActionCommand().equals(selectionFilterValue));
        noFilterAction.setEnabled(!noFilterAction.getActionCommand().equals(filterValue));

        // enable/disable assign Taifun order number button and context menu entry
        btnAssignTaifunOrder.setEnabled(isAssignTaifunOrderEnabled());
        showTaifunOrderSelectDialogAction.setEnabled(isAssignTaifunOrderEnabled());

        assignGkAction.setEnabled(isAssignGkOrPkEnabled(KundenTyp.GK));
        assignPkAction.setEnabled(isAssignGkOrPkEnabled(KundenTyp.PK));

        // enable/disable Automation context
        automationRuemVaAction.setEnabled(isAutomationRuemVaEnabled());
        automationRrnpAction.setEnabled(isAutomationRrnpEnabled());
        automationAkmTrAction.setEnabled(isAutomationAkmTrEnabled());
        automationTvsVaAction.setEnabled(isAutomationTvsVaEnabled());

        // enable/disable AKM-TR button and context menu entry
        btnAnswerVa.setEnabled(isAnswerVaEnabled());
        showDecisionDialogAction.setEnabled(isAnswerVaEnabled());

        btnAkmTr.setEnabled(isAkmTrEnabled());
        showAkmTrDialogAction.setEnabled(isAkmTrEnabled());
        btnAbbmTr.setEnabled(isAbbmTrEnabled());

        btnTv.setEnabled(isTvEnabled());
        showTvDialogAction.setEnabled(isTvEnabled());

        btnStorno.setEnabled(isStornoEnabled());
        showStornoDialogAction.setEnabled(isStornoEnabled());

        btnAbbm.setEnabled(isAbbmEnabled());
        showAbbmDialogAction.setEnabled(isAbbmEnabled());

        btnSendErlm.setEnabled(isSendErlmEnabled());
        showErlmDialogAction.setEnabled(isSendErlmEnabled());

        btnCloseIssue.setEnabled(isCloseIssueEnabled());
        showCloseIssueDialogAction.setEnabled(isCloseIssueEnabled());

        if (superUserRole) {
            btnForceClosePreagreement.setEnabled(isForceClosePreagreementEnabled());
            showForceClosePreagreementAction.setEnabled(isForceClosePreagreementEnabled());
        }
        else {
            btnForceClosePreagreement.setVisible(false);
            showForceClosePreagreementAction.setEnabled(false);
        }

        toggleBearbeitenFreigeben();

        btnSaveBemerkung.setEnabled(isAssignedToCurrentUser());
        taBemerkungen.setEnabled(isAssignedToCurrentUser());

        sendEscalationMailToCarrierAction.setEnabled(isSendEscalationMailEnabled());
    }

    /**
     * implement this method in derived classes, if distinct functionality in derived classes is needed
     */
    @SuppressWarnings("pmd:EmptyMethodInAbstractClassShouldBeAbstract")
    protected void validateButtonsAndActions() {
        //could be implemented in derieved class
    }

    private void toggleBearbeitenFreigeben() {
        if (selectedWbciRequest != null) {
            btnTaskBearbeiten.setEnabled(true);
            toggleTaskAction.setEnabled(true);

            final String taskText = isAssigned() ? getSwingFactory().getText(TASK_RELEASE_TEXT)
                    : getSwingFactory().getText(TASK_ASSIGN_TEXT);
            btnTaskBearbeiten.setText(taskText);
            toggleTaskAction.setName(taskText);
        }
        else {
            btnTaskBearbeiten.setEnabled(false);
            toggleTaskAction.setEnabled(false);
        }
    }

    private boolean isCloseIssueEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && Boolean.TRUE.equals(selectedWbciRequest.getWbciGeschaeftsfall().getKlaerfall());
    }

    private boolean isForceClosePreagreementEnabled() {
        return isAssignedAndSelectedWbciRequestValid() && superUserRole;
    }

    private boolean isSendEscalationMailEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && selectedPreagreementVo != null
                && selectedPreagreementVo.getDaysUntilDeadlinePartner() != null
                && selectedPreagreementVo.getDaysUntilDeadlinePartner() < 0;
    }

    /**
     * checks if current task is assigned to person logged in and an {@link WbciRequest} is correctly selected
     *
     * @return true if all is ok, else false
     */
    private boolean isAssignedAndSelectedWbciRequestValid() {
        return isAssignedToCurrentUser() && selectedWbciRequest != null && requestsForSelectedItem != null;
    }

    /**
     * AKM-TR button is enabled/disabled based on multiple factors that are checked in this method.
     *
     * @return boolean flag marking enabled/disabled state of button.
     */
    private boolean isAkmTrEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && !(selectedWbciRequest.getWbciGeschaeftsfall() instanceof WbciGeschaeftsfallRrnp)
                && CREATE_AKM_TR.isActionPermitted(getMNetCarrierRole(), getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(), null, null);
    }

    /**
     * Checks if the ABBM-TR button is enabled/disabled.
     *
     * @return boolean flag marking enabled/disabled state of button.
     */
    private boolean isAbbmTrEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && !(selectedWbciRequest.getWbciGeschaeftsfall() instanceof WbciGeschaeftsfallRrnp)
                && wbciCommonService.isResourceUebernahmeRequested(selectedWbciRequest.getWbciGeschaeftsfall()
                .getVorabstimmungsId())
                && CREATE_ABBM_TR.isActionPermitted(getMNetCarrierRole(), getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(), null, null);
    }

    /**
     * TV button is enabled/disabled based on multiple factors that are checked in this method:
     * <ul>
     * <li>Task is assigned to current user</li>
     * <li>M-Net is the receiving carrier</li>
     * <li>The VA Request must have one of the following statuses - RUEM_VA_EMPFANGEN or AKM_TR_VERSENDET</li>
     * <li>If any Storno Requests exist then only the following statuses are permitted - STORNO_ABBM_EMPFANGEN,
     * STORNO_ABBM_VERSENDET</li>
     * <li>If any TV Requests exist then only the following statuses are permitted - TV_ERLM_EMPFANGEN,
     * TV_ABBM_EMPFANGEN</li>
     * </ul>
     *
     * @return boolean flag marking enabled/disabled state of button.
     */
    private boolean isTvEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && CREATE_TV.isActionPermitted(getMNetCarrierRole(), getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(), null, null);
    }

    /**
     * @return true if if a TV_ABBM is permitted:
     */
    private boolean isAbbmEnabled() {
        if (isAssignedAndSelectedWbciRequestValid()) {
            WbciGeschaeftsfallStatus gfStatus = selectedWbciRequest.getWbciGeschaeftsfall().getStatus();
            selectedWbciRequest.getWbciGeschaeftsfall().getTyp();

            boolean isTvErlmPermitted = CREATE_TV_ABBM.isActionPermitted(getMNetCarrierRole(),
                    getAllGeschaeftsfallRequestStatuses(), gfStatus, null, null);

            boolean isStornoErlmPermitted = CREATE_STORNO_ABBM.isActionPermitted(getMNetCarrierRole(),
                    getAllGeschaeftsfallRequestStatuses(), gfStatus, null, null);

            if (selectedWbciRequest instanceof TerminverschiebungsAnfrage && isTvErlmPermitted) {
                return true;
            }
            if (selectedWbciRequest instanceof StornoAnfrage && isStornoErlmPermitted) {
                return true;
            }
        }

        return false;
    }

    /**
     * Storno button is enabled/disabled based on multiple factors that are checked in this method: - Task is assigned
     * to current user - If any Storno Requests exist then only the following statuses are permitted -
     * STORNO_ABBM_EMPFANGEN, STORNO_ABBM_VERSENDET,
     *
     * @return boolean flag marking enabled/disabled state of button.
     */
    private boolean isStornoEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && CREATE_STORNO.isActionPermitted(
                getMNetCarrierRole(),
                getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(),
                null, null);
    }

    private boolean isCloseGfEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && CLOSE_GF.isActionPermitted(getMNetCarrierRole(), getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(), null, null)
                && !hasUncompletedAutomationTasks(selectedWbciRequest.getWbciGeschaeftsfall());
    }

    /**
     * @return true if the GF-Status can be changed to {@link WbciGeschaeftsfallStatus#PASSIVE}.
     */
    private boolean isCloseProcessingEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && ACTIVE.equals(selectedWbciRequest.getWbciGeschaeftsfall().getStatus())
                && !selectedWbciRequest.getRequestStatus().isInitialStatus();
    }

    /**
     * @return true if the VA has the state {@link WbciRequestStatus#AKM_TR_EMPFANGEN} and no active TV or Strono has
     * been found.
     */
    private boolean isWitaVertragsNrCancellationPossible() {
        return isAssignedAndSelectedWbciRequestValid()
                && getAllGeschaeftsfallRequestStatuses().contains(AKM_TR_EMPFANGEN)
                && !WbciRequestHelper.isActiveStornoOrTvRequestStatusIncluded(getAllGeschaeftsfallRequestStatuses());
    }

    private boolean isAnswerVaEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && selectedWbciRequest.getWbciGeschaeftsfall() != null
                && selectedWbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig() != null
                && CREATE_RUEM_VA.isActionPermitted(getMNetCarrierRole(), getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(), null, null);
    }

    private List<WbciRequestStatus> getAllGeschaeftsfallRequestStatuses() {
        return WbciRequestHelper.getAllGeschaeftsfallRequestStatuses(requestsForSelectedItem);
    }

    /**
     * Checks whether a RUEM-VA notification for the selected Preagreement has been received
     */
    private boolean isRuemVaReceived() {
        for (WbciRequestStatus wbciRequestStatus : getAllGeschaeftsfallRequestStatuses()) {
            if (wbciRequestStatus.isVaRequestStatus()) {
                return ABBM_TR_EMPFANGEN.equals(wbciRequestStatus) || AKM_TR_VERSENDET.equals(wbciRequestStatus)
                        || RUEM_VA_EMPFANGEN.equals(wbciRequestStatus);
            }
        }
        return false;
    }

    /**
     * Checks whether a RUEM-VA notification for the selected Preagreement has been sent
     */
    private boolean isRuemVaSent() {
        for (WbciRequestStatus wbciRequestStatus : getAllGeschaeftsfallRequestStatuses()) {
            if (wbciRequestStatus.isVaRequestStatus()) {
                return RUEM_VA_VERSENDET.equals(wbciRequestStatus);
            }
        }
        return false;
    }

    private CarrierRole getMNetCarrierRole() {
        CarrierCode abgebenderEKP = selectedWbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP();
        CarrierCode aufnehmenderEKP = selectedWbciRequest.getWbciGeschaeftsfall().getAufnehmenderEKP();
        return CarrierRole.lookupMNetCarrierRoleByCarrierCode(aufnehmenderEKP, abgebenderEKP);
    }

    private boolean isSendErlmEnabled() {
        if (isAssignedAndSelectedWbciRequestValid()) {
            List<WbciRequestStatus> geschaeftsfallRequestStatuses = getAllGeschaeftsfallRequestStatuses();
            WbciGeschaeftsfallStatus gfStatus = selectedWbciRequest.getWbciGeschaeftsfall().getStatus();
            List<MeldungTyp> meldungTypen = WbciMeldungHelper.mapMeldungen2MeldungTyp(wbciCommonService
                    .filterMeldungenForVa(wbciCommonService
                            .findMeldungenForVaId(selectedWbciRequest.getVorabstimmungsId())));

            boolean isTvErlmPermitted = CREATE_TV_ERLM.isActionPermitted(getMNetCarrierRole(),
                    geschaeftsfallRequestStatuses, gfStatus, null, null);

            boolean isStornoErlmPermitted = CREATE_STORNO_ERLM.isActionPermitted(getMNetCarrierRole(),
                    geschaeftsfallRequestStatuses, gfStatus, meldungTypen, selectedWbciRequest);

            if (selectedWbciRequest instanceof TerminverschiebungsAnfrage && isTvErlmPermitted) {
                return true;
            }
            if (selectedWbciRequest instanceof StornoAnfrage && isStornoErlmPermitted) {
                return true;
            }
        }
        return false;
    }

    private boolean isAutomationRuemVaEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && isRuemVaReceived()
                && isFeatureEnabled(WBCI_RUEMVA_AUTO_PROCESSING)
                && selectedWbciRequest.getWbciGeschaeftsfall().isTaskProcessingPossible(TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN);
    }

    private boolean isAutomationRrnpEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && isRuemVaSent()
                && (selectedWbciRequest.getWbciGeschaeftsfall() instanceof WbciGeschaeftsfallRrnp)
                && isFeatureEnabled(WBCI_RRNP_AUTO_PROCESSING)
                && selectedWbciRequest.getWbciGeschaeftsfall().isTaskProcessingPossible(TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN);
    }

    protected boolean isFeatureEnabled(Feature.FeatureName featureName) {
        return featureService.isFeatureOnline(featureName);
    }

    private boolean isAutomationAkmTrEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && isFeatureEnabled(WBCI_AKMTR_AUTO_PROCESSING)
                && !(selectedWbciRequest.getWbciGeschaeftsfall() instanceof WbciGeschaeftsfallRrnp)
                && !(selectedWbciRequest.getWbciGeschaeftsfall() instanceof WbciGeschaeftsfallKueOrn)
                && CREATE_ABBM_TR.isActionPermitted(getMNetCarrierRole(), getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(), null, null)
                && selectedWbciRequest.getWbciGeschaeftsfall().isTaskProcessingPossible(TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN);
    }

    private boolean isAutomationTvsVaEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && isFeatureEnabled(WBCI_TVS_VA_AUTO_PROCESSING)
                && existsTvWithErlm()
                && selectedWbciRequest.getWbciGeschaeftsfall().isTaskProcessingPossible(TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN);
    }

    private boolean existsTvWithErlm() {
        for (WbciRequest wbciRequest : requestsForSelectedItem) {
            if (WbciRequestStatus.TV_ERLM_EMPFANGEN.equals(wbciRequest.getRequestStatus())) {
                return true;
            }
            if (WbciRequestStatus.TV_ERLM_VERSENDET.equals(wbciRequest.getRequestStatus())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEditAutomatedEnabled() {
        return isAssignedAndSelectedWbciRequestValid()
                && EDIT_AUTOMATED_FLAG.isActionPermitted(getMNetCarrierRole(), getAllGeschaeftsfallRequestStatuses(),
                selectedWbciRequest.getWbciGeschaeftsfall().getStatus(), null, null);
    }


    /**
     * Checks if task is assigned to some user.
     *
     * @return boolean flag marking state of assignment.
     */
    private boolean isAssigned() {
        if (selectedWbciRequest == null
                || selectedWbciRequest.getWbciGeschaeftsfall() == null
                || selectedWbciRequest.getWbciGeschaeftsfall().getCurrentUserId() == null) {
            // task is not assigned to person logged in
            return false;
        }

        // all checks are ok - task is assigned to current user
        return true;
    }

    /**
     * Checks if task is assigned to current user logged in.
     *
     * @return boolean flag marking state of assignment.
     */
    private boolean isAssignedToCurrentUser() {
        return isAssigned()
                && selectedWbciRequest.getWbciGeschaeftsfall().getCurrentUserId()
                .equals(HurricanSystemRegistry.instance().getCurrentUser().getId());
    }

    /**
     * "Taifun-Auftrag zuordnen" button is enabled/disabled based on multiple factors that are checked in this method.
     * Assignment is disabled if - no Taifun order is selected within the overview table - the Taifung order is not
     * assigned to the user - the donating carrier is NOT MNet - only in Status VA_EMPFANGEN
     *
     * @return boolean flag marking enabled/disabled state of button.
     */
    private boolean isAssignTaifunOrderEnabled() {
        return isAssignedToCurrentUser()
                && CarrierCode.MNET.equals(selectedWbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP())
                && hasVaRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
    }

    private boolean isAssignGkOrPkEnabled(KundenTyp kundenTyp) {
        return selectedWbciRequest != null
                && kundenTyp != selectedWbciRequest.getWbciGeschaeftsfall().getEndkunde().getKundenTyp();
    }

    @Override
    @SuppressWarnings("pmd:EmptyMethodInAbstractClassShouldBeAbstract")
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    @SuppressWarnings("pmd:EmptyMethodInAbstractClassShouldBeAbstract")
    public void tableFiltered() {
        // not used
    }

    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel && ((CCAuftragModel) selection).getAuftragId() != null) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
        else if (selection instanceof AutomationTask) {
            showAutomationTasksDialog((AutomationTask) selection);
        }
    }

    @Override
    public void setWaitCursor() {
        preAgreementTable.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        super.setWaitCursor();
    }

    @Override
    public void setDefaultCursor() {
        preAgreementTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        super.setDefaultCursor();
    }

    /**
     * Oeffnet fuer den aktuell ausgewaehlten Vorgang den Dialog zur Auswahl eines Taifunauftrags.
     */
    private void showTaifunOrderSelectDialog() {
        if (CarrierCode.MNET == selectedWbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP()) {
            if (selectedWbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig() != null) {
                int result = MessageHelper.showYesNoQuestion(getMainFrame(), ASSIGN_TAIFUN_ORDER_QUESTION_LONG, ASSIGN_TAIFUN_ORDER_QUESTION_SHORT);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            TaifunOrderIdSelectDialog taifunOrderIdSelectDialog = new TaifunOrderIdSelectDialog(
                    selectedWbciRequest.getWbciGeschaeftsfall());
            Object retValue = DialogHelper.showDialog(getMainFrame(), taifunOrderIdSelectDialog, true, true);
            if (!DialogHelper.wasDialogCancelled(retValue)) {
                loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);  // do not reload whole screen, one VA only
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), ASSIGN_TAIFUN_ORDER_NOT_POSSIBLE_INFO);
        }
    }


    private RufnummernportierungVO getSelectedRufnummernportierungVO() {
        return (RufnummernportierungVO)
                ((AKMutableTableModel) tbRufnummern.getModel()).getDataAtRow(tbRufnummern.getSelectedRow());
    }


    private ElektraResponseDto addOrDeleteDialNumber(boolean addDialNumber) {
        RufnummernportierungVO rufnummernportierungVO = getSelectedRufnummernportierungVO();
        if (rufnummernportierungVO != null) {
            if (addDialNumber && !rufnummernportierungVO.getStatusInfo().equals(NEU_AUS_RUEM_VA)) {
                return new ElektraResponseDtoBuilder()
                        .withStatus(ElektraResponseDto.ResponseStatus.ERROR)
                        .withModifications("Nur Rufnummern, die in der RUEM-VA zusätzlich zurück gemeldet " +
                                "wurden, können in Taifun angelegt werden!")
                        .build();
            }
            else if (!addDialNumber
                    && !rufnummernportierungVO.getStatusInfo().equals(NICHT_IN_RUEM_VA)
                    && !rufnummernportierungVO.getStatusInfo().equals(ANGEFRAGT)) {
                return new ElektraResponseDtoBuilder()
                        .withStatus(ElektraResponseDto.ResponseStatus.ERROR)
                        .withModifications(String.format("Nur Rufnummern mit Status %s bzw. %s dürfen in Taifun " +
                                "gelöscht werden!", NICHT_IN_RUEM_VA, ANGEFRAGT))
                        .build();
            }
        }

        String question = String.format("Soll die selektierte Rufnummer wirklich %s",
                (addDialNumber)
                        ? "dem Taifun Auftrag zugeordnet werden?"
                        : "vom Taifun Auftrag entfernt werden?");

        int option = MessageHelper.showYesNoQuestion(getMainFrame(), question, "Rufnummer abgleichen?");
        if (option == JOptionPane.YES_OPTION) {
            if (addDialNumber) {
                return wbciElektraService.addDialNumber(selectedWbciRequest.getVorabstimmungsId(),
                        getSelectedRufnummernportierungVO(), HurricanSystemRegistry.instance().getCurrentUser());
            }
            else {
                return wbciElektraService.deleteDialNumber(selectedWbciRequest.getVorabstimmungsId(),
                        getSelectedRufnummernportierungVO(), HurricanSystemRegistry.instance().getCurrentUser());
            }
        }
        return null;
    }

    /**
     * Action, um eine PK- oder GK-Zuweisung durchzuführen
     */
    private final class AssignPkOrGkAction extends AKAbstractAction {

        private static final long serialVersionUID = -8890163906986100915L;
        private final KundenTyp kundenTyp;

        private AssignPkOrGkAction(KundenTyp kundenTyp) {
            super();
            setName(String.format("VA %s zuweisen", kundenTyp.name()));
            setActionCommand("assign.to.pk.or.gk");
            this.kundenTyp = kundenTyp;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            wbciGeschaeftsfallService.assignCustomerTypeToEndCustomer(
                    selectedWbciRequest.getWbciGeschaeftsfall().getId(), kundenTyp);
            refresh(selectedWbciRequest.getVorabstimmungsId());
        }
    }

    /**
     * Action, um den Dialog für die Zuordnung des TaifunAuftrags anzuzeigen
     */
    private final class ShowTaifunOrderSelectDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = 193556977642928136L;

        private ShowTaifunOrderSelectDialogAction() {
            super();
            setName("Taifunauftrag zuordnen");
            setActionCommand("show.taifun.order.select.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showTaifunOrderSelectDialog();
        }
    }

    /* Action, um den AKM-TR Dialog anzuzeigen */
    private final class ShowAkmtrDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = 193556977642928136L;

        private ShowAkmtrDialogAction() {
            super();
            setName("AKM-TR erfassen");
            setActionCommand("show.akmtr.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showAkmTrDialog();
        }
    }

    /* Action, um den Klaerfall Dialog anzuzeigen */
    private final class ShowCloseIssueDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = 193556937642928136L;

        private ShowCloseIssueDialogAction() {
            super();
            setName(KLAERFALL_AUFLOESEN_LBL);
            setActionCommand("show.closeIssue.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showCloseIssueDialog();
        }
    }

    /* Action, um den TV Dialog anzuzeigen */
    private final class ShowTvDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = 193556977642923136L;

        private ShowTvDialogAction() {
            super();
            setName("TV erfassen");
            setActionCommand("show.tv.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showTvDialog();
        }
    }

    /* Action, um den TV Dialog anzuzeigen */
    private final class ShowStornoDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = -6742167753550464177L;

        private ShowStornoDialogAction() {
            super();
            setName("Storno erfassen");
            setActionCommand("show.storno.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showStornoDialog();
        }
    }

    /* Action, um den VA Antwort Dialog anzuzeigen */
    private final class ShowDecisionDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = 5391898824623921543L;

        private ShowDecisionDialogAction() {
            super();
            setName("Antwort auf VA erfassen");
            setActionCommand("show.answer.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showDecisionDialog();
        }
    }

    /* Action, um die History anzuzeigen */
    private final class ShowWbciHistoryAction extends AKAbstractAction {
        private static final long serialVersionUID = 808167729709120648L;

        private ShowWbciHistoryAction() {
            super();
            setName("History...");
            setActionCommand("show.details");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showHistoryDialog();
        }
    }

    private final class ShowAbbmDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = 3646043111098012320L;

        private ShowAbbmDialogAction() {
            super();
            setName("ABBM versenden");
            setActionCommand("show.abbm.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showAbbmDialog();
        }
    }

    private final class ShowErlmDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = -2042243355032557402L;

        private ShowErlmDialogAction() {
            super();
            setName("ERLM versenden");
            setActionCommand("show.erlm.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showErlmDialog();
        }
    }

    private final class ToggleTaskAction extends AKAbstractAction {
        private static final long serialVersionUID = -2996552439988098558L;

        private ToggleTaskAction() {
            super();
            setName("Task bearbeiten/freigeben");
            setActionCommand("toggle.task");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            toggleTask();
        }
    }

    private final class ShowForceClosePreagreementAction extends AKAbstractAction {
        private static final long serialVersionUID = -2996552439988098558L;

        private ShowForceClosePreagreementAction() {
            super();
            setName(CLOSE_VORGANG_ADMIN_LABEL);
            setActionCommand("forse.close.preagreement");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showForceClosePreagreementDialog();
        }
    }

    private final class ShowCarrierInfoDialogAction extends AKAbstractAction {
        private static final long serialVersionUID = 1326753983599732134L;

        private ShowCarrierInfoDialogAction() {
            super();
            setName("EKP Kontakinformation");
            setActionCommand("show.carrierinfo.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showCarrierInfoDialog();
        }
    }

    /**
     * Action, um eine Eskalationsmail an dem Partnercarrier zu schicken
     */
    private final class SendEscalationMailToCarrierAction extends AKAbstractAction {

        private static final long serialVersionUID = -2283325061277199727L;

        private SendEscalationMailToCarrierAction() {
            super();
            setName("Eskalationsmail verschicken");
            setActionCommand("send.escalation.mail.to.carrier");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                int result = MessageHelper
                        .showYesNoQuestion(
                                getMainFrame(),
                                String.format(
                                        "Wollen Sie wirklich eine Eskalationsmail an den Partner-Carrier '%s' verschicken?",
                                        selectedWbciRequest.getEKPPartner()),
                                "Eskalationsmail versenden");
                if (result == JOptionPane.YES_OPTION) {
                    wbciEscalationService.sendVaEscalationMailToCarrier(selectedWbciRequest.getVorabstimmungsId(),
                            HurricanSystemRegistry.instance().getCurrentUser());
                    refresh(selectedWbciRequest.getVorabstimmungsId());
                }
            }
            catch (Exception exc) {
                LOGGER.error(exc.getMessage(), exc);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), exc);
            }
        }
    }

    private final class TasksFilterAction extends AKAbstractAction {
        private static final long serialVersionUID = 8752754968706048305L;

        private TasksFilterAction(String name, String actionCommand) {
            super();
            setName(name);
            setActionCommand(actionCommand);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (EIGENE_TASKS.equals(e.getActionCommand())) {
                preAgreementTableModel.showMyTasks();
            }
            else if (TEAM_TASKS.equals(e.getActionCommand())) {
                preAgreementTableModel.showTeamTasks();
            }
            else if (ALLE_TASKS.equals(e.getActionCommand())) {
                preAgreementTableModel.showAllTasks();
            }
            validateButtons();
        }
    }

    private final class AutomationAction extends AKAbstractAction {
        private static final long serialVersionUID = 8752754963706048305L;

        private AutomationAction(String name, String actionCommand) {
            super();
            setName(name);
            setActionCommand(actionCommand);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ElektraResponseDto elektraResult = null;
                switch (e.getActionCommand()) {
                    case AUTO_RUEMVA_ACTION:
                        elektraResult = wbciElektraService.processRuemVa(selectedWbciRequest.getVorabstimmungsId(),
                                HurricanSystemRegistry.instance().getCurrentUser());
                        break;
                    case AUTO_RRNP_ACTION:
                        elektraResult = wbciElektraService.processRrnp(selectedWbciRequest.getVorabstimmungsId(),
                                HurricanSystemRegistry.instance().getCurrentUser());
                        break;
                    case AUTO_AKMTR_ACTION:
                        elektraResult = wbciElektraService.processAkmTr(selectedWbciRequest.getVorabstimmungsId(),
                                HurricanSystemRegistry.instance().getCurrentUser());
                        break;
                    case AUTO_TVS_VA_ACTION:
                        elektraResult = wbciElektraService.processTvErlm(selectedWbciRequest.getVorabstimmungsId(),
                                HurricanSystemRegistry.instance().getCurrentUser());
                        break;
                    case ADD_DIAL_NUMBER:
                        elektraResult = addOrDeleteDialNumber(true);
                        break;
                    case DELETE_DIAL_NUMBER:
                        elektraResult = addOrDeleteDialNumber(false);
                        break;
                    default:
                        break;
                }

                if (elektraResult != null) {
                    if (ElektraResponseDto.ResponseStatus.OK == elektraResult.getStatus()) {
                        MessageHelper.showInfoDialog(getMainFrame(), elektraResult.getModifications());
                    }
                    else {
                        MessageHelper.showErrorDialog(getMainFrame(), new RuntimeException(elektraResult.getModifications()));
                    }
                    loadDataAndFocus(selectedWbciRequest.getVorabstimmungsId(), false);  // do not reload whole screen, one VA only
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
            }

            validateButtons();
        }
    }


    private final class DropDownSelectionFilterAction extends AKAbstractAction {
        private static final long serialVersionUID = -1584365491098503800L;

        private DropDownSelectionFilterAction(String name, String actionCommand) {
            super();
            setName(name);
            setActionCommand(actionCommand);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cbAdditionalFilters.setSelectedItem(e.getActionCommand());
            applyDropDownSelectionFilter();
            validateButtons();
        }
    }


    /**
     * MouseListener, um einen Dialog zu oeffnen, uber den eine Status-Bemerkung zur aktuellen Vorabstimmung eingegeben
     * werden kann.
     */
    private final class AddStatusBemMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedWbciRequest != null && e.getClickCount() >= 2) {
                try {
                    TextInputDialog dlg = new TextInputDialog("Status-Bemerkung",
                            "Status-Bemerkung zur aktuellen Vorabstimmung", "Bemerkung:", 255);
                    final WbciGeschaeftsfall wbciGeschaeftsfall = selectedWbciRequest.getWbciGeschaeftsfall();
                    dlg.showText(wbciGeschaeftsfall.getInternalStatus());
                    Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    if (result instanceof String) {
                        wbciGeschaeftsfallService.updateInternalStatus(wbciGeschaeftsfall.getId(), (String) result);
                        refresh(wbciGeschaeftsfall.getVorabstimmungsId());
                    }
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(getMainFrame(), ex);
                }
            }
        }
    }

}
