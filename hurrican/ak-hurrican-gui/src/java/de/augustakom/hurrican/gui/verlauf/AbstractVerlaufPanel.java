/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 09:42:59
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJOptionPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.reporting.TxtBausteinDialog;
import de.augustakom.hurrican.gui.shared.PortsDialog;
import de.augustakom.hurrican.gui.shared.SelectUserDialog;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.view.TimeSlotView;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;
import de.augustakom.hurrican.model.cc.view.TimeSlotAware;
import de.augustakom.hurrican.model.cc.view.TimeSlotHolder;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Abstraktes Panel fuer alle Verlauf-Panels (Bauauftraege sowie Projektierungen).
 *
 *
 */
public abstract class AbstractVerlaufPanel extends AbstractServicePanel implements AKDataLoaderComponent {
    protected static final String FILTER_NIEDERLASSUNG = "Niederlassung";
    protected static final String NOTHING_SELECTED = "Bitte wählen Sie zuerst einen Verlauf aus.";
    protected static final String PROGRESS_BAUAUFTRAEGE_TEXT = "lade Bauaufträge...";
    protected static final String PROGRESS_DETAILS_TEXT = "lade Details...";
    protected static final String BTN_SHOW_PORTS = "show.ports";
    private static final Logger LOGGER = Logger.getLogger(AbstractVerlaufPanel.class);
    private static final long serialVersionUID = -1758648720400111777L;

    protected Long abteilungId;


    /**
     * Default-Konstruktor
     */
    public AbstractVerlaufPanel(String resource, Long abteilungId) {
        super(resource);
        this.abteilungId = abteilungId;
    }

    /**
     * Gibt abhaengig von der Abteilung einen anderen Klassennamen zurueck, der als Parent-Name fuer GUI-Komponenten
     * verwendet werden kann.
     */
    public String getClassName() {
        return this.getClass().getName();
    }

    /**
     * Uebernimmt den Verlauf mit der ID <code>verlaufAbteilungId</code> fuer den aktuellen Benutzer.
     *
     * @param verlaufView Verlaufs-View mit den benoetigten Daten.
     * @return die uebernommene Verlauf-Abteilung.
     */
    protected VerlaufAbteilung verlaufUebernehmen(AbstractVerlaufView verlaufView) {
        try {
            if (verlaufView != null && verlaufView.getVerlaufAbtId() != null) {
                setWaitCursor();
                BAService bas = getCCService(BAService.class);

                return bas.verlaufUebernahme(
                        verlaufView.getVerlaufAbtId(), HurricanSystemRegistry.instance().getSessionId());
            }
            else {
                MessageHelper.showInfoDialog(getMainFrame(), NOTHING_SELECTED, null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
        return null;
    }


    /**
     * Weist den Verlauf einem bestimmten Mitarbeiter zu.
     *
     * @param verlaufView Verlaufs-View mit den benoetigten Daten.
     * @return aktualisierter VerlaufAbteilung Datensatz
     *
     */
    protected VerlaufAbteilung assignVerlauf(AbstractVerlaufView verlaufView, Long abtId) {
        try {
            if (verlaufView != null && verlaufView.getVerlaufAbtId() != null) {
                SelectUserDialog dlg = new SelectUserDialog(abtId);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof AKUser) {
                    setWaitCursor();
                    BAService bas = getCCService(BAService.class);

                    return bas.assignVerlauf(
                            verlaufView.getVerlaufAbtId(), (AKUser) result);
                }
            }
            else {
                MessageHelper.showInfoDialog(getMainFrame(), NOTHING_SELECTED, null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
        return null;
    }

    /**
     * Prueft, ob der Verlauf bei der 'zentralen Dispo' ist.
     */
    protected boolean isVerlaufAtZentraleDispo(Long verlaufId) {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = bas.findVerlauf(verlaufId);
            return VerlaufStatus.BEI_ZENTRALER_DISPO.equals(verlauf.getVerlaufStatusId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            return false;
        }
    }


    protected void anNetzplanungenVerteilen(AbstractVerlaufView verlaufView) {
        if (verteilenErlaubt(verlaufView)) {
            try {
                setWaitCursor();
                BAService bas = getCCService(BAService.class);

                Verlauf verlauf = bas.findVerlauf(verlaufView.getVerlaufId());
                CCAuftragService auftragService = getCCService(CCAuftragService.class);
                AuftragTechnik at = auftragService.findAuftragTechnikByAuftragId(verlauf.getAuftragId());
                if (at == null || at.getProjectResponsibleUserId() == null) {
                    MessageHelper.showInfoDialog(this, "Bitte waehlen Sie zuerst einen Projektverantwortlichen fuer diesen Auftrag aus.", null, true);
                    return;
                }
                if (at.getNiederlassungId() == null) {
                    MessageHelper.showInfoDialog(this, "Bitte definieren Sie die Hauptniederlassung fuer diesen Auftrag zuerst.", null, true);
                    return;
                }

                VerlaufNpAuswahlDialog dlg = new VerlaufNpAuswahlDialog(verlaufView.getVerlaufId());

                Object value = DialogHelper.showDialog(this, dlg, true, true);
                if (value instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Long> niederlassungsIds = (List<Long>) value;
                    if (niederlassungsIds.isEmpty()) {
                        return;
                    }

                    bas.anNetzplanungenVerteilen(verlaufView.getVerlaufId(), verlaufView.getVerlaufAbtId(),
                            niederlassungsIds, HurricanSystemRegistry.instance().getSessionId());

                    verlaufView.setVerlaufAbtStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                    verlaufView.notifyObservers(true);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * Ueberprueft, ob die Bauauftraege verteilt werden duerfen.
     */
    protected boolean verteilenErlaubt(AbstractVerlaufView verlaufView) {
        if (NumberTools.equal(verlaufView.getVerlaufAbtStatusId(), VerlaufStatus.STATUS_IN_BEARBEITUNG)) {
            MessageHelper.showInfoDialog(this, "Verlauf wurde bereits verteilt!", null, true);
            return false;
        }
        return true;
    }

    /**
     * Zeigt einen Dialog mit allen Bemerkungen zu dem Verlauf mit der ID <code>verlaufId</code> an.
     *
     * @param verlaufId
     */
    protected void showBemerkungen(Long verlaufId) {
        if (verlaufId != null) {
            try {
                VerlaufsBemerkungenDialog dlg = new VerlaufsBemerkungenDialog(verlaufId, this);
                DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Bitte wählen Sie zuerst einen Verlauf aus zu dem die Bemerkungen angezeigt werden sollen.", null, true);
        }
    }

    /**
     * Ermittelt den Produkt-Namen aus dem Billing-System zu dem zugehoerigen Auftrag.
     *
     * @param verlaufView
     * @return
     */
    protected String getOeName(AbstractVerlaufView verlaufView) {
        if (verlaufView.getAuftragNoOrig() == null) {
            return null;
        }

        try {
            OEService oeService = getBillingService(OEService.class);
            return oeService.findProduktName4Auftrag(verlaufView.getAuftragNoOrig());
        }
        catch (Exception e) {
            return "<failed>";
        }
    }


    /**
     * Gibt das Objekt der Tabelle an der Mouse-Position zurueck.
     */
    protected Object getTableSelection(MouseEvent me) {
        AKJTable table = (AKJTable) me.getSource();
        Point point = new Point(me.getX(), me.getY());
        int row = table.rowAtPoint(point);
        int column = table.columnAtPoint(point);
        table.changeSelection(row, column, false, false);

        return ((AKMutableTableModel) table.getModel()).getDataAtRow(row);
    }


    public Long getAbteilungId() {
        return abteilungId;
    }

    protected abstract AKJTable getTable();

    protected List<Pair<byte[], String>> getAttachmentsWithFilename(final String installationsauftrag, final long verlaufId, final long sessionId)
            throws ServiceNotFoundException, IOException, AKReportException {
        final List<Pair<byte[], String>> result = Lists.newArrayList();

        final Pair<byte[], String> baFile = getCCService(BAService.class).saveVerlaufPDF(verlaufId, sessionId, false, false);
        if (baFile.getFirst().length > 0) {
            final String filename = "Bauauftrag_" + verlaufId + ".pdf";
            result.add(Pair.create(baFile.getFirst(), filename));
            if (installationsauftrag != null) {
                final File instAuftrFile = FileUtils.getFile(installationsauftrag);
                final byte[] attachmentInstAuftr = FileUtils.readFileToByteArray(instAuftrFile);
                final String filenameInstAuftr = "Installationsauftrag_" + verlaufId + ".pdf";
                result.add(Pair.create(attachmentInstAuftr, filenameInstAuftr));
            }
        }

        return result;
    }

    /**
     * Prueft, ob bei der Nachverteilung ein externer Service-Provider eine Email bekommen soll
     *
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    public boolean emailToExtServiceProviderWillBeSend(List<SelectAbteilung4BAModel> abtIds) throws ServiceNotFoundException, FindException {
        for (SelectAbteilung4BAModel model : abtIds) {
            if (model != null
                    && !NumberTools.isIn(model.getAbteilungId(), new Number[] { Abteilung.AM, Abteilung.DISPO, Abteilung.NP })
                    && NumberTools.equal(model.getAbteilungId(), Abteilung.EXTERN)
                    && model.getExtServiceProviderId() != null) {

                ExtServiceProviderService ess = getCCService(ExtServiceProviderService.class);
                ExtServiceProvider ep = ess.findById(model.getExtServiceProviderId());
                if (ep != null && NumberTools.equal(ep.getContactType(), ExtServiceProvider.REF_ID_CONTACT_EMAIL)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String generateInstallationsauftrag(Verlauf verlauf) throws ServiceNotFoundException, FindException {
        return generateInstallationsauftrag(verlauf.getAuftragId());
    }

    public String generateInstallationsauftrag(Long auftragId) throws ServiceNotFoundException, FindException {
        ReportService reportService = getReportService(ReportService.class);
        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        BillingAuftragService billingAuftragService = getBillingService(BillingAuftragService.class);

        Report report = reportService.findReport(Report.REPORT_INSTALLATIONSAUFTRAG_ENDSTELLE_B);
        if (!reportService.isReportForAuftragAvailable(report.getId(), auftragId, HurricanSystemRegistry.instance().getSessionId())) {
            return null;
        }
        int option = MessageHelper.showConfirmDialog(this,
                "Soll der Email an den externen Service Provider\n" +
                        "ein Installationauftrag hinzugefügt werden?",
                "Installationauftrag hinzufügen?", JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
            BAuftrag bAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());
            if (bAuftrag != null) {
                return createReportRequest(report, bAuftrag.getKundeNo(), auftragDaten);
            }
        }
        return null;
    }

    public String createReportRequest(Report report, Long kundeNoOrig, AuftragDaten auftragDaten) {
        if (report == null || kundeNoOrig == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Es konnten nicht alle Daten für eine Report-Anforderung ermittelt werden. Es wird kein Report erzeugt.", null, true);
            return null;
        }

        try {
            setWaitCursor();
            ReportService reportService = getReportService(ReportService.class);

            // Starte Report-Erstellung
            Long requestId = null;
            if (auftragDaten != null) {
                requestId = reportService.createReportRequest(report.getId(), HurricanSystemRegistry.instance().getSessionId(),
                        kundeNoOrig, auftragDaten.getAuftragNoOrig(), auftragDaten.getAuftragId(), null, null, false);
            }

            // Bearbeite Text-Bausteine, falls es welche gibt
            if (requestId != null) {
                List<TxtBausteinGruppe> list = reportService.findAllTxtBausteinGruppen4Report(report.getId());
                ReportRequest request = reportService.findReportRequest(requestId);
                if (CollectionTools.isNotEmpty(list)) {
                    for (TxtBausteinGruppe gruppe : list) {
                        if (gruppe != null && gruppe.getId() != null) {
                            TxtBausteinDialog dlg = new TxtBausteinDialog(request, gruppe);
                            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                        }
                    }
                }

                // Schicke Nachricht an Report-Server zur Bearbeitung des Reports
                // -1 wird als Session-Id verwendet, damit der Report nicht angezeigt wird
                reportService.sendReportRequest(request, -1L);
                return activeWaitUntilReportFinished(request);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
        return null;
    }

    /**
     * Wartet aktiv
     */
    private String activeWaitUntilReportFinished(ReportRequest requestIn) throws Exception {
        ReportRequest request = requestIn;
        ReportService reportService = getReportService(ReportService.class);
        int count = 0;
        while (count < 30) {
            Thread.sleep(1000);
            request = reportService.findReportRequest(request.getId());
            if (request.getFilePathForCurrentPlatform() != null) {
                return reportService.downloadReport(request.getId(), System.getProperty("java.io.tmpdir"));
            }
            count++;
        }
        return null;
    }

    /**
     * Zeigt alle Ports zu einem Bauauftrag an.
     *
     * @param verlaufView Verlaufs-View mit den benoetigten Daten.
     */
    protected void showPorts(AbstractVerlaufView verlaufView) {
        if (verlaufView != null) {
            PortsDialog portsDialog = PortsDialog.createWithVerlaufId(verlaufView.getVerlaufId());
            DialogHelper.showDialog(getMainFrame(), portsDialog, true, true);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), NOTHING_SELECTED, null, true);
        }
    }

    /**
     * Funktion weist die Projektierung der Netzplanung zu.
     *
     * @param view View mit Projektierungsdaten
     * @throws HurricanGUIException
     *
     */
    protected void assignPR2NP(AbstractVerlaufView view) throws HurricanGUIException {
        if (view == null) {
            throw new HurricanGUIException("Kein Datensatz ausgewählt");
        }

        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("assign.to.np.msg"),
                getSwingFactory().getText("assign.to.np.title"));

        if (option == JOptionPane.YES_OPTION) {
            try {
                BAService baService = getCCService(BAService.class);

                VerlaufAbteilung va = baService.findVerlaufAbteilung(view.getVerlaufAbtId());
                va.setAbteilungId(Abteilung.NP);
                baService.saveVerlaufAbteilung(va);

                MessageHelper.showMessageDialog(getMainFrame(),
                        "Projektierung wurde der Netzplanung zugewiesen", "Abgeschlossen",
                        JOptionPane.INFORMATION_MESSAGE);

                view.setVerlaufAbtId(va.getAbteilungId());
                view.notifyObservers(true);
                view.setGuiFinished(true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
                throw new HurricanGUIException(e.getMessage(), e);
            }
        }
    }

    /**
     * Ermittelt den Installation-TimeSlot fuer den angegebenen Bauauftrag, sofern der Auftrag noch nicht 'in Betrieb'
     * ist.
     * @see TimeSlotHolder
     */
    protected String getTimeSlot(AuftragDaten auftragDaten, TimeSlotAware timeSlotAware) throws ServiceNotFoundException, FindException {
        if (timeSlotAware != null && auftragDaten != null
                && NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB)) {
            // timeSlot aus Carrierbestellung ermitteln
            String timeSlot = timeSlotAware.getTimeSlot().getTimeSlotToUseAsString();
            if (timeSlot != null) {
                return timeSlot;
            }
            else if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
                // wenn keine Carrierbestellung vorhanden ist, dann ueber BillingService versuchen
                return getTimeSlot(auftragDaten.getAuftragNoOrig(), auftragDaten.getStatusId());
            }
        }
        return null;
    }

    protected String getTimeSlot(Long auftragNoOrig, Long aufragStatusId) throws ServiceNotFoundException, FindException {
        if (NumberTools.isLess(aufragStatusId, AuftragStatus.IN_BETRIEB)) {
            BillingAuftragService billingAuftragService = getBillingService(BillingAuftragService.class);
            TimeSlotView timeSlot = billingAuftragService.findTimeSlotView4Auftrag(auftragNoOrig);
            if (timeSlot != null) {
                return timeSlot.getTimeSlotAsStringOnlyTime();
            }
        }
        return null;
    }
    /**
     * Action fuer die manuelle Verteilung eines Bauauftrags bzw. einer Projektierung. Die Action kann verwendet werden,
     * um einen bereits verteilten Verlauf noch nachtraeglich an andere Abteilungen zu verschicken.
     */
    class ManuellVerteilenAction extends AKAbstractAction {

        private static final long serialVersionUID = 3602271141379231781L;

        /**
         * @param name Text fuer die Action
         */
        public ManuellVerteilenAction(String name) {
            this(name, "ba.verteilen.manuell");
        }

        /**
         * @param name          Text fuer die Action
         * @param actionCommand Action-Command-Name für diese Action
         */
        public ManuellVerteilenAction(String name, String actionCommand) {
            super();
            setName(name);
            setActionCommand(actionCommand);
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Object actionSrc = getValue(ACTION_SOURCE);
            if (actionSrc instanceof MouseEvent && ((MouseEvent) actionSrc).getSource() instanceof AKJTable) {
                MouseEvent me = (MouseEvent) getValue(ACTION_SOURCE);
                Object value = getTableSelection(me);
                if (value instanceof AbstractVerlaufView) {
                    try {
                        BAService bas = getCCService(BAService.class);
                        AbstractVerlaufView verlaufView = (AbstractVerlaufView) value;
                        Verlauf verlauf = bas.findVerlauf(verlaufView.getVerlaufId());

                        boolean isProjektierung = BooleanTools.nullToFalse(verlauf.getProjektierung());
                        VerlaufAbtAuswahlDialog dlg = new VerlaufAbtAuswahlDialog(!isProjektierung, verlauf.getId());
                        dlg.loadData4Nachverteilung(verlauf.getId());
                        Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);

                        if (result instanceof List<?> && !((List<?>) result).isEmpty()) {
                            @SuppressWarnings("unchecked")
                            List<SelectAbteilung4BAModel> abtIds = (List<SelectAbteilung4BAModel>) result;

                            String installationsauftrag = null;
                            if (emailToExtServiceProviderWillBeSend(abtIds)) {
                                installationsauftrag = generateInstallationsauftrag(verlauf);
                            }

                            final Long sessionId = HurricanSystemRegistry.instance().getSessionId();

                            final List<Pair<byte[], String>> attachmentsWithFilename =
                                    getAttachmentsWithFilename(installationsauftrag, verlauf.getId(), sessionId);

                            bas.dispoVerteilenManuell(verlauf.getId(), verlaufView.getVerlaufAbtId(), abtIds,
                                    attachmentsWithFilename, sessionId);
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

    /**
     * Action-Klasse, um den Verlauf als 'beobachtet' zu markieren.
     */
    class ObserveVerlaufAction extends AKAbstractAction {
        private static final long serialVersionUID = -229057130249482262L;

        /**
         * Default-Konstruktor
         */
        public ObserveVerlaufAction() {
            super();
            setName("Verlauf beobachten");
            setActionCommand("observe.verlauf");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object actionSrc = getValue(ACTION_SOURCE);
                if (actionSrc instanceof MouseEvent && ((MouseEvent) actionSrc).getSource() instanceof AKJTable) {
                    Object value = getTableSelection((MouseEvent) getValue(ACTION_SOURCE));
                    if (value instanceof AbstractVerlaufView) {
                        BAService bas = getCCService(BAService.class);
                        bas.observeProcess(((AbstractVerlaufView) value).getVerlaufId());

                        ((AbstractVerlaufView) value).setObserveProcess(Boolean.TRUE);
                    }
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    /**
     * Action-Klasse, um einen Verlauf zu splitten.
     */
    class SplitVerlaufAction extends AKAbstractAction {
        private static final long serialVersionUID = -4367811887597798932L;

        public SplitVerlaufAction() {
            super();
            setName("Verlauf splitten");
            setActionCommand("split.verlauf");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object actionSrc = getValue(ACTION_SOURCE);
            if (actionSrc instanceof MouseEvent && ((MouseEvent) actionSrc).getSource() instanceof AKJTable) {
                Object value = getTableSelection((MouseEvent) getValue(ACTION_SOURCE));
                if (AbstractVerlaufView.class.isAssignableFrom(value.getClass())) {
                    try {
                        SplitVerlaufDialog splitDialog = new SplitVerlaufDialog(AbstractVerlaufView.class.cast(value).getVerlaufId());
                        Object result = DialogHelper.showDialog(getMainFrame(), splitDialog, true, true);
                        if (result instanceof Integer && result.equals(AKJOptionPane.OK_OPTION)) {
                            // Reload data
                            loadData();
                        }
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage());
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }

    /* Action-Klasse, um die Projektierung zur Netzplanung zu verschieben. */
    class AssignPRToNetzplanungAction extends AKAbstractAction {

        private static final long serialVersionUID = 673397232759731745L;

        /**
         * Default-Konstruktor
         */
        public AssignPRToNetzplanungAction() {
            super();
            setName("zur Netzplanung verschieben");
            setActionCommand("assign.np");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Object actionSrc = getValue(ACTION_SOURCE);
            if (actionSrc instanceof MouseEvent
                    && ((MouseEvent) actionSrc).getSource() instanceof AKJTable) {
                Object value = getTableSelection((MouseEvent) getValue(ACTION_SOURCE));
                if (AbstractVerlaufView.class.isAssignableFrom(value.getClass())) {
                    try {
                        assignPR2NP(AbstractVerlaufView.class.cast(value));
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage());
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }

}
