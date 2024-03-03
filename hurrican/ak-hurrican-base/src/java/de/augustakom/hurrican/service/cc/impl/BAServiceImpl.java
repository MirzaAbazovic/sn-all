/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH All rights reserved. -------------------------------------------------------
 * File created: 02.07.2004 08:30:27
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.common.tools.lang.BooleanTools.*;
import static de.augustakom.hurrican.service.wholesale.ReservePortRequestMapper.*;
import static de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.dao.cc.PhysikUebernahmeDAO;
import de.augustakom.hurrican.dao.cc.VerlaufAbteilungDAO;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.dao.cc.VerlaufViewDAO;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.exceptions.HurricanConcurrencyException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufZusatz;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungStatus;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.cc.view.SimpleVerlaufView;
import de.augustakom.hurrican.model.cc.view.TimeSlotHolder;
import de.augustakom.hurrican.model.cc.view.VerlaufAbteilungView;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.BillingWorkflowService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.cc.impl.command.BaseCreateProjektierungCommand;
import de.augustakom.hurrican.service.cc.impl.command.CreateProjektierungCommand;
import de.augustakom.hurrican.service.cc.impl.command.CreateProjektierungForZentraleDispoCommand;
import de.augustakom.hurrican.service.cc.impl.command.CreateVerlaufCommand;
import de.augustakom.hurrican.service.cc.impl.command.FindVerlaufViews4AbtCommand;
import de.augustakom.hurrican.service.cc.impl.command.FinishVerlauf4AbteilungCommand;
import de.augustakom.hurrican.service.cc.impl.command.SplitVerlaufCommand;
import de.augustakom.hurrican.service.cc.impl.command.StornoVerlaufCommand;
import de.augustakom.hurrican.service.cc.impl.reportdata.BauauftragEmptyJasperDS;
import de.augustakom.hurrican.service.cc.impl.reportdata.BauauftragJasperDS;
import de.augustakom.hurrican.service.cc.impl.reportdata.VerlaufDetailJasperDS;
import de.augustakom.hurrican.service.wholesale.ReservePortRequestMapper;
import de.augustakom.hurrican.service.wholesale.WholesaleServiceException;
import de.augustakom.hurrican.service.wholesale.WholesaleTechnicalException;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungPredicate;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ResourceOrderManagementNotificationService;

/**
 * Implementierung von <code>BAService</code>
 */
@CcTxRequired
public class BAServiceImpl extends DefaultCCService implements BAService {

    private static final Logger LOGGER = Logger.getLogger(BAServiceImpl.class);

    // DAOs
    @Autowired
    private VerlaufDAO verlaufDAO = null;
    @Autowired
    private VerlaufAbteilungDAO verlaufAbteilungDAO = null;
    @Autowired
    private PhysikUebernahmeDAO physikUebernahmeDAO = null;
    @Autowired
    private VerlaufViewDAO verlaufViewDAO = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAConfigService")
    private BAConfigService baConfigService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.NiederlassungService")
    private NiederlassungService niederlassungService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.ExtServiceProviderService")
    private ExtServiceProviderService extServiceProviderService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.InnenauftragService")
    private InnenauftragService innenauftragService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.LockService")
    private LockService lockService = null;
    @Resource(name = "mailSender")
    private JavaMailSender mailSender;
    @Resource(name = "de.augustakom.hurrican.service.cc.ffm.FFMService")
    private FFMService ffmService;

    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;

    @Autowired
    private ResourceOrderManagementNotificationService romNotificationService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Autowired
    private PhysikService physikService;

    @Autowired
    private HardwareDAO hardwareDAO;

    @Autowired
    private EkpFrameContractService ekpFrameContractService;

    @Autowired
    private VlanService vlanService;

    private static ResourceReader resourceReader = new ResourceReader(
            "de.augustakom.hurrican.service.cc.resources.BAVerlauf");

    protected <T> IServiceCommand getServiceCommand(Class<T> clazz) {
        return serviceLocator.getCmdBean(clazz);
    }

    @Nonnull
    @Override
    public TimeSlotHolder getTimeSlotHolder(Long auftragId) throws FindException {
        TimeSlotHolder timeSlot = new TimeSlotHolder();
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if (endstelle != null) {
            Carrierbestellung lastCB4Endstelle = carrierService.findLastCB4Endstelle(endstelle.getId());
            if (lastCB4Endstelle != null) {
                timeSlot.setTalRealisierungsDay(lastCB4Endstelle.getBereitstellungAm());
                timeSlot.setTalRealisierungsZeitfenster(lastCB4Endstelle.getTalRealisierungsZeitfenster());
            }
        }
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
        if (auftragDaten != null && auftragDaten.getAuftragNoOrig() != null) {
            timeSlot.setTimeSlotView(billingAuftragService.findTimeSlotView4Auftrag(auftragDaten.getAuftragNoOrig()));
        }
        return timeSlot;
    }

    @Override
    @CcTxRequiresNew
    public Pair<Verlauf, AKWarnings> createVerlaufNewTx(CreateVerlaufParameter parameterObject) throws StoreException, FindException {
        return createVerlauf(parameterObject);
    }

    @Override
    public Pair<Verlauf, AKWarnings> createVerlauf(CreateVerlaufParameter parameterObject)
            throws StoreException, FindException {
        try {
            IServiceCommand cmd = getServiceCommand(CreateVerlaufCommand.class);
            cmd.prepare(CreateVerlaufCommand.AUFTRAG_ID, parameterObject.auftragId);
            cmd.prepare(CreateVerlaufCommand.REALISIERUNGSTERMIN, parameterObject.realisierungsTermin);
            cmd.prepare(CreateVerlaufCommand.ANLASS_ID, parameterObject.anlass);
            cmd.prepare(CreateVerlaufCommand.INSTALL_TYPE_ID, parameterObject.installType);
            cmd.prepare(CreateVerlaufCommand.SESSION_ID, parameterObject.sessionId);
            cmd.prepare(CreateVerlaufCommand.ZENTRALE_DISPO, parameterObject.anZentraleDispo);
            cmd.prepare(CreateVerlaufCommand.AUFTRAG_SUBORDERS, parameterObject.subAuftragsIds);
            Object result = cmd.execute();

            Verlauf verlauf = (result instanceof Verlauf) ? (Verlauf) result : null;
            AKWarnings warnings = ((IWarningAware) cmd).getWarnings();

            return Pair.create(verlauf, warnings);
        }
        catch (StoreException | FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Anlage des Bauauftrags ist ein unerwarteter Fehler aufgetreten. " +
                    "BA wurde nicht erstellt!", e);
        }
    }

    @Override
    public AKWarnings verlaufStornieren(Long verlaufId, boolean sendMail, Long sessionId) throws StoreException,
            FindException {
        try {
            IServiceCommand cmd = getServiceCommand(StornoVerlaufCommand.class);
            cmd.prepare(StornoVerlaufCommand.VERLAUF_ID, verlaufId);
            cmd.prepare(StornoVerlaufCommand.SEND_MAIL, sendMail);
            cmd.prepare(StornoVerlaufCommand.VA_STORNO, Boolean.FALSE);
            cmd.prepare(StornoVerlaufCommand.SESSION_ID, sessionId);
            Object result = cmd.execute();

            return (result instanceof AKWarnings) ? (AKWarnings) result : null;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Stornieren des Bauauftrags ist ein unerwarteter Fehler aufgetreten.", e);
        }
    }

    @Override
    public List<Verlauf> splitVerlauf(Verlauf toSplit, Set<Long> orderIdsToRemove, Long sessionId)
            throws StoreException {
        try {
            IServiceCommand cmd = getServiceCommand(SplitVerlaufCommand.class);
            cmd.prepare(SplitVerlaufCommand.VERLAUF, toSplit);
            cmd.prepare(SplitVerlaufCommand.ORDER_IDS_TO_REMOVE, orderIdsToRemove);
            cmd.prepare(SplitVerlaufCommand.SESSION_ID, sessionId);
            @SuppressWarnings("unchecked")
            List<Verlauf> verlaeufe = (List<Verlauf>) cmd.execute();
            return verlaeufe;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Herausloesen von Auftraegen ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    @Override
    public void observeProcess(Long verlaufId) throws StoreException {
        if (verlaufId == null) { throw new StoreException("Keine Verlaufs-ID angegeben!"); }
        try {
            Verlauf v = findVerlauf(verlaufId);
            if (v != null) {
                if (nullToFalse(v.getAkt())) {
                    v.setObserveProcess(Boolean.TRUE);
                    saveVerlauf(v);
                }
                else {
                    throw new StoreException("Verlauf ist nicht aktiv und kann deshalb nicht beobachtet werden!");
                }
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void infoDispoChangeRealDate(Long auftragId, Date newRealDate, Long sessionId) throws FindException {
        if ((auftragId == null) || (newRealDate == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        else if (DateTools.isBefore(newRealDate, new Date())) {
            throw new FindException("Realisierungstermin von Bauauftrag darf nicht in der Vergangenheit liegen!");
        }

        try {
            Verlauf actVerl = findActVerlauf4Auftrag(auftragId, false);
            if (actVerl == null) {
                throw new FindException("Es konnte kein aktiver Verlauf zu dem Auftrag gefunden werden!\n" +
                        "Realisierungstermin kann nicht geaendert werden!");
            }

            if (actVerl.getVerlaufStatusId() >= VerlaufStatus.RUECKLAEUFER_DISPO) {
                throw new FindException("Der Realisierungstermin darf im aktuellen Bauauftrags-Status " +
                        "nicht mehr geaendert werden!");
            }

            if (DateTools.isDateBefore(newRealDate, actVerl.getRealisierungstermin())) {
                throw new FindException("Der Realisierungstermin darf nur nach hinten verschoben werden!");
            }

            if (!DateTools.isWorkDay(newRealDate)) {
                throw new FindException("Der Realisierungstermin darf ausschliesslich auf einen Arbeitstag fallen!");
            }

            // Lade VerlaufAbteilung der Dispo
            VerlaufAbteilung va = findVAOfVerteilungsAbt(actVerl.getId());
            if ((va == null) || (va.getNiederlassungId() == null)) {
                throw new FindException("Zustaendige Dispo kann nicht ermittelt werden!");
            }

            // Sende Email
            Niederlassung nl = niederlassungService.findNiederlassung(va.getNiederlassungId());
            AKUser user = getUser(sessionId);
            String emailDef = (nl != null) ? nl.getDispoTeampostfach() : null;
            String[] emails = StringUtils.split(emailDef, HurricanConstants.EMAIL_SEPARATOR);
            if ((emails != null) && (emails.length > 0)) {
                StringBuilder txt = new StringBuilder(resourceReader.getValue("email.termin.verschieben",
                        new Object[] { actVerl.getAuftragId().toString(),
                                DateTools.formatDate(actVerl.getRealisierungstermin(), DateTools.PATTERN_DAY_MONTH_YEAR),
                                DateTools.formatDate(newRealDate, DateTools.PATTERN_DAY_MONTH_YEAR),
                                (user != null) ? user.getNameAndFirstName() : "unbekannt" }
                ));

                // Mail senden
                SimpleMailMessage mailMsg = new SimpleMailMessage();
                mailMsg.setFrom(((user != null) && StringUtils.isNotBlank(user.getEmail())) ? user.getEmail() : "Hurrican");
                mailMsg.setTo(emails);
                mailMsg.setSubject("Terminverschiebung");
                mailMsg.setText(txt.toString());

                mailSender.send(mailMsg);
            }
            else {
                throw new FindException("Email-Adresse der zustaendigen Dispo kann nicht ermittelt werden!");
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e);
        }
    }

    @Override
    public void createProjektierung(Long auftragId, Long auftragIdAlt, Long sessionId,
            Set<Long> subAuftragsIds) throws StoreException, FindException {
        try {
            IServiceCommand cmd = getServiceCommand(CreateProjektierungCommand.class);
            cmd.prepare(BaseCreateProjektierungCommand.AUFTRAG_ID, auftragId);
            cmd.prepare(BaseCreateProjektierungCommand.AUFTRAG_ID_ALT, auftragIdAlt);
            cmd.prepare(BaseCreateProjektierungCommand.SESSION_ID, sessionId);
            cmd.prepare(BaseCreateProjektierungCommand.AUFTRAG_SUBORDERS, subAuftragsIds);
            cmd.execute();
        }
        catch (StoreException | FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Anlage der Projektierung ist ein unerwarteter Fehler aufgetreten. " +
                    "Projektierung wurde nicht erstellt!", e);
        }
    }

    @Override
    public void createProjektierungForZentraleDispo(Long auftragId, Long auftragIdAlt, Long sessionId,
            Set<Long> subAuftragsIds) throws StoreException, FindException {
        try {
            IServiceCommand cmd = getServiceCommand(CreateProjektierungForZentraleDispoCommand.class);
            cmd.prepare(BaseCreateProjektierungCommand.AUFTRAG_ID, auftragId);
            cmd.prepare(BaseCreateProjektierungCommand.AUFTRAG_ID_ALT, auftragIdAlt);
            cmd.prepare(BaseCreateProjektierungCommand.SESSION_ID, sessionId);
            cmd.prepare(BaseCreateProjektierungCommand.AUFTRAG_SUBORDERS, subAuftragsIds);
            cmd.execute();
        }
        catch (StoreException e) {
            throw e;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Anlage der Projektierung fuer die zentrale Dispo ist ein unerwarteter Fehler aufgetreten. " +
                    "Projektierung wurde nicht erstellt!", e);
        }
    }

    @Override
    public List<VerlaufAbteilung> baErstellen(Long verlaufId, List<Long> abtIds, Date realDate, Long sessionId)
            throws StoreException {
        checkParameterNullAndThrowExceptionIf(verlaufId);
        try {
            AKUser user = getUser(sessionId);
            Niederlassung nl = null;
            Verlauf verlauf = findVerlauf(verlaufId);
            if (verlauf != null) {
                nl = niederlassungService.findNiederlassung4Auftrag(verlauf.getAuftragId());
            }

            if (nl == null) {
                throw new FindException("Kann Niederlassung nicht ermitteln.");
            }

            List<VerlaufAbteilung> retVal = createVerlauf4Abteilungen(verlaufId, abtIds, realDate, user, nl);

            notifyWholesale(verlauf);

            return retVal;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_VERTEILEN, e);
        }
    }

    // Verlauf fuer die Abteilungen anlegen
    private List<VerlaufAbteilung> createVerlauf4Abteilungen(Long verlaufId, List<Long> abtIds, Date realDate,
            AKUser user, Niederlassung nl) throws FindException {
        List<VerlaufAbteilung> verlaufAbteilungen = new ArrayList<>();
        if (abtIds == null) {
            return verlaufAbteilungen;
        }
        Date now = new Date();
        for (Long abtId : abtIds) {
            VerlaufAbteilung va = new VerlaufAbteilung();
            va.setVerlaufId(verlaufId);
            va.setAbteilungId(abtId);
            va.setDatumAn(now);
            va.setRealisierungsdatum(realDate);
            va.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
            // Niederlassung ueber Abt/NL Mapping
            Niederlassung nlAbt = baConfigService.findNL4Verteilung(abtId, nl.getId());
            if (nlAbt == null) {
                throw new FindException("Kann Niederlassung fuer Abteilung " + abtId + " nicht finden.");
            }
            va.setNiederlassungId(nlAbt.getId());

            // Fuer Abteilung AM wird Niederlassung ueber User ermittelt.
            if (NumberTools.equal(abtId, Abteilung.AM)) {
                va.setBearbeiter(user.getLoginName());
                if (!user.isHurricanWsUser()) {
                    va.setNiederlassungId(user.getNiederlassungId());
                }
            }

            getVerlaufAbteilungDAO().store(va);
            verlaufAbteilungen.add(va);
        }
        return verlaufAbteilungen;
    }

    private void notifyWholesale(final Verlauf verlauf) throws StoreException, FindException {
        if (verlauf == null) {
            return;
        }
        Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            AuftragDaten auftragDaten = findAuftragDatenAndThrowExceptionIfNotFound(orderId);
            if (auftragDaten.isWholesaleAuftrag() && isAnschlussartFtth(auftragDaten.getAuftragId()) && auftragDaten.isInErfassung()) {
                final WholesaleReservePortResponse whsResponse = reservePortResponse(auftragDaten, verlauf.getRealisierungstermin());
                NotifyPortOrderUpdate response = toReservePortResponseNotification(whsResponse, auftragDaten.getWholesaleAuftragsId());
                romNotificationService.notifyPortOrderUpdate(response);
            }
        }
    }

    private boolean isAnschlussartFtth(final Long auftragId) throws FindException {
        final List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
        if (endstellen == null || endstellen.isEmpty()) {
            throw new FindException(String.format("Keine Endstelle fuer auftragId '%s' gefunden.", auftragId));
        }
        for (Endstelle endstelle : endstellen) {
            if (endstelle.isEndstelleB()) {
                Long anschlussartId = hvtService.findAnschlussart4HVTStandort(endstelle.getHvtIdStandort());
                if (Anschlussart.ANSCHLUSSART_FTTH.equals(anschlussartId)) {
                    return true;
                }
            }
        }

        return false;
    }

    private WholesaleReservePortResponse reservePortResponse(final AuftragDaten auftragDaten, Date execDate) throws FindException,
            StoreException {
        final Long auftragId = auftragDaten.getAuftragId();
        final VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungByAuftragId(auftragId);
        final String lineId = vbz.getVbz();
        final WholesaleReservePortResponse response = new WholesaleReservePortResponse();
        final LocalDate execLocalDate = DateConverterUtils.asLocalDate(execDate);
        response.setExecutionDate(execLocalDate);
        response.setHurricanAuftragId(auftragId.toString());
        response.setLineId(lineId);

        final Auftrag2EkpFrameContract frameContract = ekpFrameContractService.findAuftrag2EkpFrameContract(auftragId, execLocalDate);

        EkpFrameContract ekpFrameContract = (frameContract != null) ? frameContract.getEkpFrameContract() : null;
        if (ekpFrameContract != null) {
            addA10nspAndDpoInfoToResponse(auftragId, ekpFrameContract, response);
        }

        List<EqVlan> vlans = calculateAndSaveEqVlan(ekpFrameContract, lineId, auftragId, auftragDaten.getProdId(), execLocalDate);
        response.setSvlanEkp(String.valueOf(vlans.get(0).getSvlanEkp()));

        return response;
    }

    private void addA10nspAndDpoInfoToResponse(final Long auftragId, final EkpFrameContract ekpFrameContract,
            final WholesaleReservePortResponse response) throws FindException {
        final HWRack rack = auftragService.findHwRackByAuftragId(auftragId);
        final HWOlt olt = hardwareDAO.findHwOltForRack(rack);
        final A10NspPort a10NspPort = ekpFrameContractService.findA10NspPort(ekpFrameContract, olt.getId());
        response.setDpoNochNichtVerbaut((rack instanceof HWDpo) && ((HWDpo) rack).getSerialNo() == null);
        if (a10NspPort != null) {
            response.setA10nsp(a10NspPort.getA10Nsp().getName());
            response.setA10nspPort(a10NspPort.getVbz().getVbz());
        }
    }

    private List<EqVlan> calculateAndSaveEqVlan(final EkpFrameContract ekpFrameContract, final String lineId,
            final Long auftragId, final Long produktId, final LocalDate desiredExecutionDate) throws StoreException {
        try {
            List<EqVlan> vlans = vlanService.assignEqVlans(ekpFrameContract, auftragId, produktId, desiredExecutionDate, null);
            if (vlans.isEmpty()) {
                throw new WholesaleServiceException(VLANS_NOT_EXIST, "VLANs could not be calculated or found!", lineId, null);
            }
            return vlans;
        }
        catch (HurricanConcurrencyException e) {
            final String msg = String.format("Error calculating/assigning VLANs: %s Please try again!", e.getMessage());
            throw new WholesaleTechnicalException(msg, e);
        }
        catch (Exception e) {
            final String msg = String.format("Error calculating VLANs: %s", e.getMessage());
            throw new WholesaleServiceException(VLANS_NOT_EXIST, msg, null, e);
        }
    }

    @Override
    public List<VerlaufAbteilung> createVerlaufAbteilungForZentraleDispo(Long verlaufId,
            Long sessionId) throws StoreException {
        checkParameterNullAndThrowExceptionIf(verlaufId);
        try {
            AKUser user = getUser(sessionId);
            List<VerlaufAbteilung> retVal = new ArrayList<>();
            Date now = new Date();

            VerlaufAbteilung vaAm = createSingleVerlaufAbteilungForZentraleDispo(verlaufId, baConfigService, now, Abteilung.AM);
            vaAm.setBearbeiter(user.getLoginName());
            vaAm.setNiederlassungId((user.getNiederlassungId() == null) ? Niederlassung.ID_ZENTRAL : user.getNiederlassungId());
            getVerlaufAbteilungDAO().store(vaAm);
            retVal.add(vaAm);

            VerlaufAbteilung vaDispo = createSingleVerlaufAbteilungForZentraleDispo(verlaufId, baConfigService, now, Abteilung.DISPO);
            vaDispo.setParentVerlaufAbteilungId(vaAm.getId());
            getVerlaufAbteilungDAO().store(vaDispo);
            retVal.add(vaDispo);

            return retVal;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_VERTEILEN, e);
        }
    }

    private VerlaufAbteilung createSingleVerlaufAbteilungForZentraleDispo(Long verlaufId, BAConfigService bac, Date now,
            Long abtId) throws FindException {
        VerlaufAbteilung va = new VerlaufAbteilung();
        va.setVerlaufId(verlaufId);
        va.setAbteilungId(abtId);
        va.setDatumAn(now);
        va.setRealisierungsdatum(null);
        va.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
        Niederlassung nlAbt = bac.findNL4Verteilung(abtId, Niederlassung.ID_ZENTRAL);
        if (nlAbt == null) {
            throw new FindException("Kann Niederlassung fuer Abteilung " + abtId + " nicht finden.");
        }
        va.setNiederlassungId(nlAbt.getId());
        return va;
    }

    @Override
    public List<VerlaufAbteilung> dispoBAVerteilenAuto(Long verlaufId, Long sessionId) throws StoreException {
        try {
            Verlauf verlauf = getVerlaufDAO().findById(verlaufId, Verlauf.class);
            if (verlauf == null) {
                throw new StoreException("Verlauf mit der ID " + verlaufId + " wurde nicht gefunden!");
            }

            Set<Long> abtIds;
            if (NumberTools.isIn(verlauf.getAnlass(),
                    new Number[] { BAVerlaufAnlass.NEUSCHALTUNG, BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG, BAVerlaufAnlass.UEBERNAHME })) {
                abtIds = readAbtIdsNeu4Auftrag(verlauf, verlauf.getAuftragId());
            }
            else {
                abtIds = readAbtIdsAend4Anlass(verlauf);
            }

            if (NumberTools.notEqual(verlauf.getAnlass(), BAVerlaufAnlass.KUENDIGUNG)) {
                addAbtIds4PhysikUebernahme(verlauf, abtIds);
            }

            addAbtIds4LeistungsKonfig(verlauf.getId(), abtIds);

            // Erzeuge SelectAbteilung4BAModelle
            Niederlassung nl = niederlassungService.findNiederlassung4Auftrag(verlauf.getAuftragId());
            if (nl == null) {
                throw new StoreException("Kann Niederlassung des Auftrags für die Verteilung nicht ermitteln.");
            }

            if (isAutomaticallyDispatchable(verlauf)) {
                if (abtIds.contains(Abteilung.MQUEUE)) {
                    // falls automatisch verteilbar und an M-Queue: STOnline/Voice entfernen
                    abtIds.remove(Abteilung.ST_ONLINE);
                    abtIds.remove(Abteilung.ST_VOICE);
                }
            }
            else {
                if (abtIds.contains(Abteilung.ST_ONLINE) || abtIds.contains(Abteilung.ST_VOICE)) {
                    // nicht automatisch verteilbar und an STOnline/Voice verteilt: M-Queue entfernen
                    abtIds.remove(Abteilung.MQUEUE);
                }
            }

            if (abtIds.isEmpty()) {
                throw new StoreException("Dieser Verlauf lässt sich nicht automatisch verteilen");
            }

            List<SelectAbteilung4BAModel> abteilungen = new ArrayList<>();
            for (Long abtId : abtIds) {
                SelectAbteilung4BAModel model = new SelectAbteilung4BAModel();
                model.setAbteilungId(abtId);
                model.setRealDate(verlauf.getRealisierungstermin());
                Niederlassung nl4Abt = baConfigService.findNL4Verteilung(abtId, nl.getId());
                if (nl4Abt == null) {
                    throw new StoreException("Kann zuständige Niederlassung für Abteilung " + abtId
                            + " nicht ermitteln.");
                }
                model.setNiederlassungId(nl4Abt.getId());

                abteilungen.add(model);
            }

            return dispoVerteilenManuell(verlaufId, null, abteilungen, null, sessionId, false);
        }
        catch (FindException e) {
            throw new StoreException(StoreException.ERROR_BA_ABT_ERMITTELN, new Object[] { e.getMessage() }, e);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_VERTEILEN, e);
        }
    }

    @Override
    public List<VerlaufAbteilung> dispoVerteilenManuell(Long verlaufId, Long verlaufAbteilungId,
            List<SelectAbteilung4BAModel> abtIds, List<Pair<byte[], String>> attachments, Long sessionId)
            throws StoreException {
        return dispoVerteilenManuell(verlaufId, verlaufAbteilungId, abtIds, attachments, sessionId, true);
    }

    @Override
    public List<VerlaufAbteilung> anNetzplanungenVerteilen(Long verlaufId, Long parentVerlaufAbtId,
            List<Long> niederlassungsIds, Long sessionId) throws StoreException, FindException,
            ServiceNotFoundException {
        if ((niederlassungsIds == null) || niederlassungsIds.isEmpty()) {
            throw new StoreException("Es wurden keine Niederlassungen ausgewaehlt.");
        }
        checkParameterNullAndThrowExceptionIf(verlaufId, sessionId);

        Verlauf verlauf = getVerlaufDAO().findById(verlaufId, Verlauf.class);

        List<VerlaufAbteilung> returnValue = new ArrayList<>();
        for (Long nlId : niederlassungsIds) {
            VerlaufAbteilung va = new VerlaufAbteilung();
            va.setVerlaufId(verlaufId);
            va.setAbteilungId(Abteilung.NP);
            va.setNiederlassungId(nlId);
            va.setParentVerlaufAbteilungId(parentVerlaufAbtId);
            va.setDatumAn(new Date());
            va.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
            saveVerlaufAbteilung(va);
            returnValue.add(va);
        }

        // Verlauf von AM und zentrale Dispo ermitteln und Status auf 'in Bearbeitung' setzen
        VerlaufAbteilung vaAm = findVerlaufAbteilung(verlaufId, Abteilung.AM);
        if (vaAm == null) {
            throw new StoreException("AM-Verlauf konnte nicht gefunden werden!");
        }

        vaAm.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
        saveVerlaufAbteilung(vaAm);

        VerlaufAbteilung vaParent = findVerlaufAbteilung(parentVerlaufAbtId);
        if (vaParent == null) {
            throw new StoreException("Dispo-Verlauf konnte nicht gefunden werden!");
        }
        vaParent.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
        saveVerlaufAbteilung(vaParent);

        if (verlauf.getVerlaufStatusId() >= VerlaufStatus.KUENDIGUNG_BEI_DISPO) {
            verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_DISPO);
        }
        else {
            verlauf.setVerlaufStatusId(VerlaufStatus.BEI_DISPO);
        }
        getVerlaufDAO().store(verlauf);
        sendStatusToTaifun(verlaufId, sessionId);
        return returnValue;
    }

    private List<VerlaufAbteilung> dispoVerteilenManuell(Long verlaufId, Long verlaufAbteilungId,
            List<SelectAbteilung4BAModel> abtIds, List<Pair<byte[], String>> attachments, Long sessionId, boolean directCall)
            throws StoreException {
        try {
            if (CollectionTools.isEmpty(abtIds)) {
                throw new StoreException(
                        "Es wurden keine Abteilungen angegeben, für die ein Verlauf erstellt werden soll.");
            }
            Optional<AKUser> user = getUserIfAvailable(sessionId);

            // uebergeordneten Verlauf laden
            Verlauf verlauf = getVerlaufDAO().findById(verlaufId, Verlauf.class);
            if (verlauf == null) {
                throw new StoreException("Verlauf mit der ID " + verlaufId + " wurde nicht gefunden!");
            }

            // Verlauf von AM und DISPO/NP ermitteln und Status auf 'in Bearbeitung' setzen
            VerlaufAbteilung vaAm = findVerlaufAbteilung(verlaufId, Abteilung.AM);
            if (vaAm == null) {
                throw new StoreException("AM-Verlauf konnte nicht gefunden werden!");
            }
            vaAm.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
            getVerlaufAbteilungDAO().store(vaAm);

            VerlaufAbteilung vaVerteiler;
            if (verlaufAbteilungId == null) {
                vaVerteiler = findVAOfVerteilungsAbt(verlauf.getId());
            }
            else {
                vaVerteiler = getVerlaufAbteilungDAO().findById(verlaufAbteilungId, VerlaufAbteilung.class);
            }
            if (vaVerteiler == null) {
                throw new StoreException("Dispo- bzw. NP-Verlauf konnte nicht gefunden werden!");
            }
            vaVerteiler.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
            vaVerteiler.setBearbeiter(user.map(AKUser::getLoginName).orElse("hurrican"));
            getVerlaufAbteilungDAO().store(vaVerteiler);

            // Verlauf fuer die einzelnen Abteilungen erstellen - AM, Dispo und NP ignorieren!
            List<VerlaufAbteilung> retVal = new ArrayList<>();
            for (SelectAbteilung4BAModel model : abtIds) {
                if ((model != null) && !NumberTools.isIn(model.getAbteilungId(), new Number[] { Abteilung.AM, Abteilung.DISPO, Abteilung.NP })) {
                    VerlaufAbteilung va = new VerlaufAbteilung();
                    va.setVerlaufId(verlaufId);
                    va.setAbteilungId(model.getAbteilungId());
                    va.setNiederlassungId(model.getNiederlassungId());
                    va.setRealisierungsdatum(model.getRealDate());
                    va.setDatumAn(new Date());
                    va.setParentVerlaufAbteilungId(vaVerteiler.getId());
                    va.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
                    if (Abteilung.EXTERN.equals(model.getAbteilungId()) && (model.getExtServiceProviderId() != null)) {
                        va.setExtServiceProviderId(model.getExtServiceProviderId());
                        getVerlaufAbteilungDAO().store(va);

                        // Email-Benachrichtigung
                        ExtServiceProvider ep = extServiceProviderService.findById(model.getExtServiceProviderId());

                        if ((ep != null) && NumberTools.equal(ep.getContactType(), ExtServiceProvider.REF_ID_CONTACT_EMAIL)) {
                            extServiceProviderService.sendAuftragEmail(verlaufId, attachments, sessionId);
                        }
                    }
                    else {
                        getVerlaufAbteilungDAO().store(va);

                        if (Abteilung.FFM.equals(model.getAbteilungId()) && verlauf.isBauauftrag()) {
                            // Uebergabe des Bauauftrags an das FFM-System
                            // (durch die JMS-Transaction sollte sichergestellt sein, dass die JMS-Message entfernt
                            // wird, wenn in nachfolgendem Code noch eine Exception auftritt)
                            String workforceOrderId = ffmService.createAndSendOrder(verlauf);
                            verlauf.setWorkforceOrderId(workforceOrderId);
                        }
                    }

                    retVal.add(va);
                }
            }

            if ((verlauf.getVerlaufStatusId() != null) &&
                    (verlauf.getVerlaufStatusId() >= VerlaufStatus.KUENDIGUNG_BEI_DISPO)) {
                verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_TECHNIK);
            }
            else {
                verlauf.setVerlaufStatusId(VerlaufStatus.BEI_TECHNIK);
            }
            verlauf.setManuellVerteilt(directCall);
            getVerlaufDAO().store(verlauf);
            sendStatusToTaifun(verlauf.getId(), sessionId);

            return retVal;
        }
        catch (StoreException e) {
            throw new StoreException("Bauaufträge/Projektierungen wurden nicht verteilt! Grund:\n" + e.getMessage(), e);
        }
        catch (FFMServiceException e) {
            throw new StoreException(
                    String.format("Bauaufträge/Projektierungen wurden nicht verteilt, da bei der Übergabe an FFM ein " +
                            "Fehler aufgetreten ist: %s", e.getMessage()), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_VERTEILEN, e);
        }
    }

    @Override
    public void dispoVerlaufRueckruf(Long verlaufId, Long sessionId) throws StoreException {
        checkParameterNullAndThrowExceptionIf(verlaufId);
        //if (verlaufId == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            // Verlauf laden
            Verlauf verlauf = getVerlaufDAO().findById(verlaufId, Verlauf.class);
            if (verlauf == null) {
                throw new StoreException("Verlauf für den Rückruf konnte nicht ermittelt werden.");
            }

            // Datensatz der Abteilung laden, die den BA verteilt hat.
            VerlaufAbteilung vaVerteiler = findVAOfVerteilungsAbt(verlauf.getId());
            if (vaVerteiler == null) {
                throw new StoreException("Verlauf konnte nicht zurück gerufen werden, da die " +
                        "benötigten Verlaufs-Informationen nicht ermittelt werden.");
            }

            boolean deleted = false;
            if (NumberTools.equal(vaVerteiler.getVerlaufStatusId(), VerlaufStatus.STATUS_IM_UMLAUF)) {
                throw new StoreException("Bauauftrag ist noch nicht verteilt. Rückruf nicht notwendig!");
            }
            else if (NumberTools.equal(vaVerteiler.getVerlaufStatusId(), VerlaufStatus.STATUS_IN_BEARBEITUNG)) {
                if (!NumberTools.isIn(verlauf.getVerlaufStatusId(),
                        new Number[] { VerlaufStatus.BEI_TECHNIK, VerlaufStatus.KUENDIGUNG_BEI_TECHNIK })) {
                    throw new StoreException("Der Auftrag befindet sich bereits in Bearbeitung! Verlauf kann nur " +
                            "über die Rücklüufer storniert werden!");
                }
                // Verlauf zurueck rufen
                getVerlaufAbteilungDAO().deleteVerlaufAbteilung(verlaufId, true);
                deleted = true;

                verlauf.setVerlaufStatusId(VerlaufStatus.BEI_DISPO);
                getVerlaufDAO().store(verlauf);

                vaVerteiler.setBearbeiter(null);
                vaVerteiler.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
                getVerlaufAbteilungDAO().store(vaVerteiler);

                VerlaufAbteilung vaAm = findVerlaufAbteilung(verlaufId, Abteilung.AM);
                if (vaAm != null) {
                    vaAm.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
                    getVerlaufAbteilungDAO().store(vaAm);
                }
                sendStatusToTaifun(verlaufId, sessionId);

                ffmService.deleteOrder(verlauf);
            }

            if (!deleted) {
                throw new StoreException("Verlauf wurde aus unbekanntem Grund nicht zurück gerufen!");
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Verlauf konnte nicht mehr zurück gerufen werden.");
        }
    }


    @Override
    public void dispoVerlaufAbschluss(Long verlaufId, Long verlaufAbteilungId,
            Date inbetriebnahmedatum, Long sessionId, Boolean amRueckLaeuferErzwingen) throws StoreException {
        checkParameterNullAndThrowExceptionIf(verlaufId, sessionId);
        try {
            AKUser user = getUser(sessionId);

            Verlauf verlauf = getVerlaufDAO().findById(verlaufId, Verlauf.class);
            VerlaufAbteilung vaVerteiler;
            if (verlaufAbteilungId == null) {
                // eigentlich gibt es nur einen vaVerteiler, aber ohne Parent ist die Auswahl etwas schwieriger
                List<VerlaufAbteilung> vas = findVerlaufAbteilungen(verlaufId);
                CollectionUtils.filter(vas, new VerlaufAbteilungPredicate(Abteilung.DISPO, Abteilung.NP));
                if ((vas == null) || (vas.size() != 1)) {
                    throw new StoreException("Verlaufs-Daten konnten nicht ermittelt werden" +
                            " (keinen oder mehr als einen Verteiler-Abteilungs-Verlauf gefunden).");
                }
                vaVerteiler = vas.get(0);
            }
            else {
                vaVerteiler = findVerlaufAbteilung(verlaufAbteilungId);
            }
            if ((verlauf == null) || (vaVerteiler == null)) {
                throw new StoreException("Verlaufs-Daten konnten nicht ermittelt werden.");
            }

            //HUR-18659
            final List<VerlaufAbteilung> vas = findVerlaufAbteilungen(verlaufId);
            final Collection<VerlaufAbteilung> npVas = CollectionUtils.select(vas, new VerlaufAbteilungPredicate(Abteilung.NP));
            boolean alleNpVerlaeufeErledigt = true;
            int anzahlNpVerlaeufeNichtErledigt = 0;
            for (final VerlaufAbteilung verlaufNp : npVas) {
                if (verlaufNp.getDatumErledigt() == null) {
                    alleNpVerlaeufeErledigt = false;
                    anzahlNpVerlaeufeNichtErledigt++;
                }
            }
            if (!Abteilung.NP.equals(vaVerteiler.getAbteilungId()) && !alleNpVerlaeufeErledigt) {
                throw new StoreException("Abschluss durch DISPO ist nicht möglich, solange der Verlauf nicht von der Netzplanung abgeschlossen wurde!");
            }

            defineRealDateOfOrders(inbetriebnahmedatum, verlauf);

            Date now = new Date();
            vaVerteiler.setVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT);
            vaVerteiler.setDatumErledigt(now);
            vaVerteiler.setAusgetragenAm(now);
            vaVerteiler.setAusgetragenVon(user.getName());
            if (StringUtils.isBlank(vaVerteiler.getBearbeiter())) {
                vaVerteiler.setBearbeiter(user.getLoginName());
            }
            getVerlaufAbteilungDAO().store(vaVerteiler);

            //einziger offener NP-Verlauf wurde soeben beendet und dispo ist nicht involviert? -> Ruecklaeufer duerfen erzeugt werden bzw. BA kann abgeschlossen werden
            if (!alleNpVerlaeufeErledigt && anzahlNpVerlaeufeNichtErledigt == 1 && Abteilung.NP.equals(vaVerteiler.getAbteilungId()) &&
                    CollectionUtils.select(vas, new VerlaufAbteilungPredicate(Abteilung.DISPO)).isEmpty()) {
                alleNpVerlaeufeErledigt = true;
            }

            if (alleNpVerlaeufeErledigt) {

                final Produkt produkt = produktService.findProdukt4Auftrag(verlauf.getAuftragId());
                final AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(verlauf.getAuftragId());
                Collection<VerlaufAbteilung> verlAbts = CollectionUtils.select(vas,
                        new VerlaufAbteilungTechPredicate());
                FinishVerlauf4AbteilungCommand.NeedsBaRuecklaeuferResult needsBaRuecklaeufer =
                        new FinishVerlauf4AbteilungCommand.NeedsBaRuecklaeuferResult(
                                produkt,
                                auftragTechnik,
                                verlauf,
                                verlAbts);

                Long status = (verlauf.getVerlaufStatusId() >= VerlaufStatus.KUENDIGUNG_RL_DISPO)
                        ? VerlaufStatus.KUENDIGUNG_RL_AM : VerlaufStatus.RUECKLAEUFER_AM;
                // Falls der Parent Abteilungs-Verlauf nicht AM ist, den Status nur auf RL_DISPO/NP setzen
                if ((amRueckLaeuferErzwingen == null || !amRueckLaeuferErzwingen)
                        && vaVerteiler.getParentVerlaufAbteilungId() != null) {
                    VerlaufAbteilung parentVa = findVerlaufAbteilung(vaVerteiler.getParentVerlaufAbteilungId());
                    if (!Abteilung.AM.equals(parentVa.getAbteilungId())) {
                        status = (verlauf.getVerlaufStatusId() >= VerlaufStatus.KUENDIGUNG_RL_DISPO)
                                ? VerlaufStatus.KUENDIGUNG_RL_DISPO : VerlaufStatus.RUECKLAEUFER_DISPO;
                    }
                }

                verlauf.setVerlaufStatusId(status);
                getVerlaufDAO().store(verlauf);

                boolean sendStatusUpdate = true;
                if (getDecisionAbschliessen(amRueckLaeuferErzwingen, status, needsBaRuecklaeufer)) {
                    if (Boolean.TRUE.equals(verlauf.getProjektierung())) {
                        amPrAbschliessen(verlaufId, sessionId);
                    }
                    else {
                        amBaAbschliessen(verlaufId, sessionId);
                        sendStatusUpdate = false;
                    }
                }
                if (sendStatusUpdate) {
                    sendStatusToTaifun(verlaufId, sessionId);
                }
            }
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_ABSCHLIESSEN, new Object[] { e.getMessage() }, e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_ABSCHLIESSEN, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public void notifyWholesale(String wholesaleOrderId, Long verlaufId) {
        Validate.notNull(wholesaleOrderId);
        NotifyPortOrderUpdate baResponse;
        Pair<Long, String> reasonCode = getNotPossibelReason4Wholesale(verlaufId);
        if (reasonCode.isEmpty()) {
            //SUCCESS CASE
            baResponse = ReservePortRequestMapper.toBauauftragChangedSuccessNotification(wholesaleOrderId);
        }
        else {
            //ERROR CASE
            baResponse = ReservePortRequestMapper.toBauauftragChangedErrorNotification(wholesaleOrderId, reasonCode);
        }
        romNotificationService.notifyPortOrderUpdate(baResponse);
    }

    protected Pair<Long, String> getNotPossibelReason4Wholesale(Long verlaufId) {
        Pair<Long, String> resultPair = Pair.empty();
        try {
            Optional<VerlaufAbteilung> oAbteilung = Optional.ofNullable(findVerlaufAbteilung(verlaufId, Abteilung.FFM));
            final Optional<Long> oLong = oAbteilung.filter(f -> f.getNotPossible()).map(f -> f.getNotPossibleReasonRefId());
            if (oLong.isPresent()) {
                Optional<Reference> oReference = Optional.ofNullable(referenceService.findReference(oLong.get()));
                if (oReference.isPresent()) {
                    resultPair = Pair.create(oReference.get().getId(), oReference.get().getStrValue());
                }
            }
        }
        catch (FindException e) {
            LOGGER.warn("Failed to get NotPossibleReasons", e);
        }
        return resultPair;
    }


    private boolean getDecisionAbschliessen(Boolean amRueckLaeuferErzwingen, Long status, FinishVerlauf4AbteilungCommand.NeedsBaRuecklaeuferResult needsBaRuecklaeufer) {
        if (amRueckLaeuferErzwingen != null) {
            // Netzplanung ueberschreibt
            return !amRueckLaeuferErzwingen; // Wenn RL -> kein Abschluss
        }
        else {
            return !(needsBaRuecklaeufer.needsBaRuecklaeufer() // wird ein RL ueberhaupt benoetigt
                    && !needsBaRuecklaeufer.needsBaRuecklaeuferWegenBemerkung()); // es handelt sich nicht um eine Bemerkung
        }
    }


    public Collection<VerlaufAbteilung> findVerlaeufeForTechAbteilungen(final long verlaufId) throws FindException {
        List<VerlaufAbteilung> verlAbts = this.findVerlaufAbteilungen(verlaufId);
        return CollectionUtils.select(verlAbts, new VerlaufAbteilungTechPredicate());
    }

    private void defineRealDateOfOrders(Date inbetriebnahmedatum, Verlauf verlauf) throws FindException, StoreException {
        Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(orderId);
            if (auftragDaten == null) {
                throw new StoreException("Die Auftrags-Daten zu dem Verlauf konnten nicht ermittelt werden.");
            }

            if (!nullToFalse(verlauf.getProjektierung())) {
                if (auftragDaten.getInbetriebnahme() == null) {
                    if (inbetriebnahmedatum == null) {
                        throw new StoreException(
                                "Es muss ein Inbetriebnahmedatum fuer den Auftrag angegeben werden!");
                    }
                    auftragDaten.setInbetriebnahme(inbetriebnahmedatum);
                    auftragService.saveAuftragDaten(auftragDaten, false);
                }

                // Check auf offene Budgets (JG: 28.03.2008 auf Anforderung Winkler Michael)
                if (!nullToFalse(verlauf.getNotPossible()) && innenauftragService.hasOpenBudget(auftragDaten.getAuftragId())) {
                    throw new StoreException("Der Auftrag besitzt noch ein offenes Budget.\n" +
                            "Budget muss geschlossen werden, bevor der Bauauftrag abgeschlossen werden kann.");
                }
            }
        }
    }

    @Override
    public AKWarnings dispoVerlaufStorno(Long verlaufId, Long sessionId) throws StoreException {
        checkParameterNullAndThrowExceptionIf(verlaufId, sessionId);
        try {
            IServiceCommand cmd = getServiceCommand(StornoVerlaufCommand.class);
            cmd.prepare(StornoVerlaufCommand.VERLAUF_ID, verlaufId);
            cmd.prepare(StornoVerlaufCommand.SEND_MAIL, Boolean.FALSE);
            cmd.prepare(StornoVerlaufCommand.VA_STORNO, Boolean.TRUE);
            cmd.prepare(StornoVerlaufCommand.SESSION_ID, sessionId);
            Object result = cmd.execute();

            return (result instanceof AKWarnings) ? (AKWarnings) result : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Stornieren des Bauauftrags ist ein unerwarteter Fehler aufgetreten.", e);
        }
    }

    protected void checkIfBauauftragBereitsAbgeschlossen(Verlauf verlauf) throws StoreException {
        if (NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.VERLAUF_ABGESCHLOSSEN) ||
                NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN)) {
            throw new StoreException("Bauauftrag ist bereits abgeschlossen.");
        }
    }

    protected void checkParameterNullAndThrowExceptionIf(Object... parameters) throws StoreException {
        for (Object parameter : parameters) {
            if (parameter == null) {
                throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
            }
        }
    }

    protected void setVerlaufStatusAbgeschlossen(Verlauf verlauf) {
        if (isVerlaufStatusKuendigung(verlauf)) {
            verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN);
        }
        else {
            verlauf.setVerlaufStatusId(VerlaufStatus.VERLAUF_ABGESCHLOSSEN);
        }
    }

    protected boolean isVerlaufStatusKuendigung(Verlauf verlauf) {
        return verlauf.getStatusIdAlt() >= AuftragStatus.KUENDIGUNG;
    }

    protected void findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(Long verlaufId)
            throws FindException, StoreException {
        List<VerlaufAbteilung> verlaeufe = findVerlaufAbteilungen(verlaufId);
        if ((verlaeufe == null) || verlaeufe.isEmpty()) {
            throw new StoreException("Verlaufs-Daten konnten nicht ermittelt werden.");
        }
        else {
            for (VerlaufAbteilung va : verlaeufe) {
                if (isVerlaufErledigtDatumNotSetAndAbteilungNotAM(va)) {
                    throw new StoreException("Bauauftrag ist nicht von allen Abteilungen erledigt.");
                }
            }
        }
    }

    protected boolean isVerlaufErledigtDatumNotSetAndAbteilungNotAM(final VerlaufAbteilung verlaufAbteilung) {
        return !NumberTools.equal(verlaufAbteilung.getAbteilungId(), Abteilung.AM)
                && (verlaufAbteilung.getDatumErledigt() == null);
    }

    protected void setVerlaufAbteilungAmErledigt(Long verlaufId, Long sessionId) throws FindException,
            AKAuthenticationException {
        VerlaufAbteilung verlaufAbteilung = findVerlaufAbteilung(verlaufId, Abteilung.AM);
        AKUser user = getUser(sessionId);
        Date now = new Date();
        verlaufAbteilung.setDatumErledigt(now);
        verlaufAbteilung.setVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT);
        verlaufAbteilung.setAusgetragenAm(now);
        verlaufAbteilung.setAusgetragenVon(user.getName());
        getVerlaufAbteilungDAO().store(verlaufAbteilung);
    }

    protected Verlauf findVerlaufAndThrowExceptionIfNotFound(Long verlaufId) throws StoreException, FindException {
        Verlauf verlauf = findVerlauf(verlaufId);
        if (verlauf == null) {
            throw new StoreException("Verlaufs-Daten konnten nicht ermittelt werden.");
        }
        return verlauf;
    }

    protected AuftragDaten findAuftragDatenAndThrowExceptionIfNotFound(Long orderId) throws StoreException,
            FindException {
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(orderId);
        if (auftragDaten == null) {
            throw new StoreException("Auftrags-Daten konnten nicht ermittelt werden. Auftrags-ID: " + orderId);
        }
        return auftragDaten;
    }

    protected void saveAuftragDatenWithoutHistory(AuftragDaten auftragDaten) throws StoreException {
        auftragService.saveAuftragDaten(auftragDaten, false);
    }

    protected boolean findAuftragDatenAndChangeStatusErledigtIfPossible(Long orderId, Verlauf verlauf)
            throws StoreException, FindException {
        boolean successfullyClosed = false;

        AuftragDaten auftragDaten = findAuftragDatenAndThrowExceptionIfNotFound(orderId);
        if (nullToFalse(verlauf.getNotPossible())) {
            // falls Verlauf nicht realisierbar war, wird der Auftrags-Status
            // auf den urspruenglichen Wert gesetzt
            auftragDaten.setStatusId(verlauf.getStatusIdAlt());
        }
        else {
            auftragDaten.setStatusId(AuftragStatus.PROJEKTIERUNG_ERLEDIGT);
            successfullyClosed = true;
        }
        saveAuftragDatenWithoutHistory(auftragDaten);
        return successfullyClosed;
    }

    protected void finishActiveLocksIfPossible(Long orderId, Verlauf verlauf) throws StoreException {
        if (isVerlaufStatusKuendigung(verlauf) && !nullToFalse(verlauf.getNotPossible())) {
            lockService.finishActiveLocks(orderId);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.BAService#amPrAbschliessen(java.lang.Long, java.lang.Long)
     */
    @Override
    public Verlauf amPrAbschliessen(Long verlaufId, Long sessionId) throws StoreException {
        checkParameterNullAndThrowExceptionIf(verlaufId, sessionId);
        try {
            Verlauf verlauf = findVerlaufAndThrowExceptionIfNotFound(verlaufId);
            setVerlaufAbteilungAmErledigt(verlaufId, sessionId);
            findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(verlaufId);
            checkIfBauauftragBereitsAbgeschlossen(verlauf);

            verlauf.setAkt(Boolean.FALSE);
            verlauf.setVerlaufStatusId(VerlaufStatus.VERLAUF_ABGESCHLOSSEN);
            saveVerlauf(verlauf);

            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            for (Long orderId : orderIds) {
                findAuftragDatenAndChangeStatusErledigtIfPossible(orderId, verlauf);
                finishActiveLocksIfPossible(orderId, verlauf);
            }

            return verlauf;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_ABSCHLIESSEN, new Object[] { e.getMessage() }, e);
        }
    }

    protected void sendStatusToTaifun(Long verlaufId, Long sessionId) throws StoreException,
            ServiceNotFoundException {
        BillingWorkflowService wfService = getBillingService(BillingWorkflowService.class);
        wfService.changeOrderState4Verlauf(verlaufId, sessionId);
    }

    @Override
    public Verlauf amBaAbschliessen(Long verlaufId, Long sessionId) throws StoreException {
        checkParameterNullAndThrowExceptionIf(verlaufId, sessionId);
        try {
            Verlauf verlauf = findVerlaufAndThrowExceptionIfNotFound(verlaufId);
            setVerlaufAbteilungAmErledigt(verlaufId, sessionId);
            findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(verlaufId);
            checkIfBauauftragBereitsAbgeschlossen(verlauf);

            verlauf.setAkt(Boolean.FALSE);
            setVerlaufStatusAbgeschlossen(verlauf);
            saveVerlauf(verlauf);

            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            for (Long orderId : orderIds) {
                AuftragDaten auftragDaten = findAuftragDatenAndThrowExceptionIfNotFound(orderId);
                if (nullToFalse(verlauf.getNotPossible())) {
                    // falls Verlauf nicht realisierbar war, wird der Auftrags-Status
                    // auf den urspruenglichen Wert gesetzt
                    auftragDaten.setStatusId(verlauf.getStatusIdAlt());
                    saveAuftragDatenWithoutHistory(auftragDaten);
                }
                else {
                    if (isVerlaufStatusKuendigung(verlauf)) {
                        auftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
                    }
                    else {
                        auftragDaten.setStatusId(AuftragStatus.IN_BETRIEB);
                    }
                    saveAuftragDatenWithoutHistory(auftragDaten);
                    sendStatusToTaifun(verlauf.getId(), sessionId);
                }
                if (auftragDaten.isWholesaleAuftrag()) {
                    this.notifyWholesale(auftragDaten.getWholesaleAuftragsId(), verlauf.getId());
                }
                finishActiveLocksIfPossible(orderId, verlauf);
            }
            return verlauf;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_BA_ABSCHLIESSEN, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public void saveVerlauf(Verlauf toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            getVerlaufDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveVerlaufAbteilung(VerlaufAbteilung toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            getVerlaufAbteilungDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Verlauf findVerlauf(Long verlaufId) throws FindException {
        if (verlaufId == null) { return null; }
        try {
            return getVerlaufDAO().findById(verlaufId, Verlauf.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public Verlauf findVerlaufByWorkforceOrder(String workforceOrderId) {
        if (workforceOrderId == null) { return null; }

        try {
            List<Verlauf> verlaufList = getVerlaufDAO().findByWorkforceOrderId(workforceOrderId);
            if (CollectionUtils.isEmpty(verlaufList) || verlaufList.size() > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE);
            }

            return verlaufList.get(0);
        }
        catch (Exception e) {
            LOGGER.error(String.format("Cannot find Bauauftrag for unknown workforce order id '%s':%s", workforceOrderId, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<Verlauf> findActVerlaeufe(Date realDate, Boolean projektierungen) throws FindException {
        try {
            Verlauf example = new Verlauf();
            example.setRealisierungstermin(realDate);
            example.setAkt(Boolean.TRUE);
            example.setProjektierung(projektierungen);

            return getVerlaufDAO().queryByExample(example, Verlauf.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Verlauf> findVerlaeufe4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            return getVerlaufDAO().findByAuftragId(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<SimpleVerlaufView> findSimpleVerlaufViews4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            return getVerlaufDAO().findSimpleVerlaufViews(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Verlauf findActVerlauf4Auftrag(Long auftragId, boolean projektierung) throws FindException {
        try {
            List<Verlauf> result = getVerlaufDAO().findActive(auftragId, projektierung);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                else if (result.size() > 1) {
                    throw new FindException("Es wurde mehr als ein aktiver Verlauf fuer den Auftrag gefunden!");
                }
            }

            return null;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Verlauf findLastVerlauf4Auftrag(Long auftragId, boolean projektierung) throws FindException {
        try {
            return getVerlaufDAO().findLast4Auftrag(auftragId, projektierung);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Verlauf> findAllActVerlauf4Auftrag(Long auftragId) throws FindException {
        List<Verlauf> result = new LinkedList<>();

        Verlauf verlauf = findActVerlauf4Auftrag(auftragId, false);
        if (verlauf != null) {
            result.add(verlauf);
        }

        verlauf = findActVerlauf4Auftrag(auftragId, true);
        if (verlauf != null) {
            result.add(verlauf);
        }

        return (!result.isEmpty()) ? result : null;
    }

    @Override
    public VerlaufAbteilung findVerlaufAbteilung(Long verlaufAbtId) throws FindException {
        if (verlaufAbtId == null) { return null; }
        try {
            return getVerlaufAbteilungDAO().findById(verlaufAbtId, VerlaufAbteilung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VerlaufAbteilung findVerlaufAbteilung(Long verlaufId, Long abtId) throws FindException {
        return findVerlaufAbteilung(verlaufId, abtId, null);
    }

    @Override
    public VerlaufAbteilung findVerlaufAbteilung(Long verlaufId, Long abtId, Long niederlassungId) throws FindException {
        if ((verlaufId == null) || (abtId == null)) { return null; }
        try {
            return getVerlaufAbteilungDAO().findByVerlaufAndAbtId(verlaufId, abtId, niederlassungId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VerlaufAbteilung findVAOfVerteilungsAbt(Long verlaufId) throws FindException {
        if (verlaufId == null) {
            return null;
        }
        try {
            VerlaufAbteilung va = findVerlaufAbteilung(verlaufId, Abteilung.DISPO);
            if (va == null) {
                va = findVerlaufAbteilung(verlaufId, Abteilung.NP);
            }
            return va;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Es konnte nicht ermittelt werden, von welcher Abteilung der " +
                    "Verlauf verteilt wurde!", e);
        }
    }

    @Override
    public VerlaufAbteilung verlaufUebernahme(Long verlaufAbtId, Long sessionId) throws StoreException {
        if (verlaufAbtId == null) { return null; }
        if (sessionId == null) {
            throw new StoreException(StoreException.INVALID_SESSION_ID);
        }

        try {
            AKUser user = getAKUserBySessionId(sessionId);
            if (user != null) {
                VerlaufAbteilung va = getVerlaufAbteilungDAO().findById(
                        verlaufAbtId, VerlaufAbteilung.class);
                if (va != null) {
                    va.setBearbeiter(user.getLoginName());
                    va.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                    getVerlaufAbteilungDAO().store(va);

                    return va;
                }
                throw new StoreException("Der Verlauf konnte nicht übernommen werden (Datensatz nicht gefunden).");
            }
            throw new StoreException(StoreException.INVALID_SESSION_ID);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Übernahme des Verlaufs ist ein Fehler aufgetreten!", e);
        }
    }

    @Override
    public VerlaufAbteilung assignVerlauf(Long verlaufAbtId, AKUser user) throws StoreException {
        if (verlaufAbtId == null) { return null; }
        if (user == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            VerlaufAbteilung va = getVerlaufAbteilungDAO().findById(
                    verlaufAbtId, VerlaufAbteilung.class);
            if (va != null) {
                va.setBearbeiter(user.getLoginName());
                getVerlaufAbteilungDAO().store(va);

                return va;
            }
            throw new StoreException("Der Verlauf konnte nicht zugewiesen werden (Datensatz nicht gefunden).");
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Zuweisung des Verlaufs ist ein Fehler aufgetreten!", e);
        }
    }


    @Override
    public VerlaufAbteilung finishVerlauf4FFMWithDeleteOrder(VerlaufAbteilung va, String bearbeiter,
            String bemerkung, Date realDate, Long sessionId, Long zusatzAufwand,
            Boolean notPossible, Long notPossibleRefId) throws StoreException {
        try {
            Verlauf bauauftrag = findVerlauf(va.getVerlaufId());
            ffmService.deleteOrder(bauauftrag);

            return finishVerlauf4Abteilung(va, bearbeiter, bemerkung, realDate, sessionId, zusatzAufwand, notPossible, notPossibleRefId);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Abschluss des FFM-Verlaufs ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }


    @Override
    public VerlaufAbteilung finishVerlauf4Abteilung(VerlaufAbteilung va, String bearbeiter, String bemerkung, Date realDate,
            Long sessionId, Long zusatzAufwand, Boolean notPossible,
            Long notPossibleRefId) throws StoreException {

        try {
            IServiceCommand cmd = getServiceCommand(FinishVerlauf4AbteilungCommand.class);
            cmd.prepare(FinishVerlauf4AbteilungCommand.VERLAUF_ABTEILUNG, va);
            cmd.prepare(FinishVerlauf4AbteilungCommand.BEARBEITER, bearbeiter);
            cmd.prepare(FinishVerlauf4AbteilungCommand.BEMERKUNG, bemerkung);
            cmd.prepare(FinishVerlauf4AbteilungCommand.REALISIERUNGSTERMIN, realDate);
            cmd.prepare(FinishVerlauf4AbteilungCommand.AK_USER, getAKUserBySessionIdSilent(sessionId));
            cmd.prepare(FinishVerlauf4AbteilungCommand.ZUSATZ_AUFWAND, zusatzAufwand);
            cmd.prepare(FinishVerlauf4AbteilungCommand.SESSION_ID, sessionId);
            cmd.prepare(FinishVerlauf4AbteilungCommand.NOT_POSSIBLE, notPossible);
            cmd.prepare(FinishVerlauf4AbteilungCommand.NOT_POSSIBLE_REASON, notPossibleRefId);
            Object result = cmd.execute();

            if (result instanceof VerlaufAbteilung) {
                return (VerlaufAbteilung) result;
            }
            return null;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Abschluss des Verlaufs ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPhysikInfo4Verlauf(Verlauf verlauf) throws FindException {
        if (verlauf == null) { return null; }
        try {
            StringBuilder result = new StringBuilder();

            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            for (Long orderId : orderIds) {
                PhysikUebernahmeDAO dao = getPhysikUebernahmeDAO();
                PhysikUebernahme pu = dao.findByVerlaufId(orderId, verlauf.getId());
                if (pu != null) {
                    PhysikaenderungsTyp ptyp = dao.findById(pu.getAenderungstyp(), PhysikaenderungsTyp.class);
                    if (ptyp != null) {
                        if (result.length() > 0) {
                            result.append("; ");
                        }
                        result.append(ptyp.getName());
                        result.append(NumberTools.equal(pu.getKriterium(), PhysikUebernahme.KRITERIUM_ALT_NEU) ? " nach " : " von ");
                        result.append(pu.getAuftragIdB());
                    }
                }
            }

            return result.toString();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId) throws FindException {
        if (verlaufId == null) { return null; }
        try {
            VerlaufAbteilung example = new VerlaufAbteilung();
            example.setVerlaufId(verlaufId);
            return getVerlaufAbteilungDAO().queryByExample(example, VerlaufAbteilung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId, Long... abteilungIds) throws FindException {
        if (verlaufId == null) { return null; }
        try {
            return getVerlaufAbteilungDAO().findVerlaufAbteilungen(verlaufId, abteilungIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId, Long verlaufAbteilungId) throws FindException {
        if (verlaufId == null) { return null; }
        try {
            return getVerlaufAbteilungDAO().findByVerlaufAndVerlaufAbteilungId(verlaufId, verlaufAbteilungId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerlaufAbteilungView> findAffectedAbteilungViews(Long auftragId, boolean ignoreProjektierung,
            boolean onlyTechAbts, boolean ignoreSCT, boolean noticeBAVerlNeu) throws FindException {
        if (auftragId == null) { return null; }
        try {
            // Ermittle bereits betroffene Abteilungen
            List<VerlaufAbteilungView> views = new ArrayList<>();
            List<Verlauf> verlaeufe = findVerlaeufe4Auftrag(auftragId);
            if (CollectionTools.isNotEmpty(verlaeufe)) {
                for (Verlauf v : verlaeufe) {
                    if (!nullToFalse(v.getProjektierung()) || !ignoreProjektierung) {
                        List<VerlaufAbteilung> vas = findVerlaufAbteilungen(v.getId());
                        if (CollectionTools.isNotEmpty(vas)) {
                            for (VerlaufAbteilung va : vas) {
                                if (va != null) {
                                    VerlaufAbteilungView view = new VerlaufAbteilungView();
                                    view.setAbtId(va.getAbteilungId());
                                    view.setNlId(va.getNiederlassungId());
                                    views.add(view);
                                }
                            }
                        }
                    }
                }
            }

            // Abteilungen der Neuschaltung hinzufuegen
            if (noticeBAVerlNeu) {
                Niederlassung nl = niederlassungService.findNiederlassung4Auftrag(auftragId);
                if (nl != null) {
                    // Abteilungen ueber die Verlaufs-Konfiguration laden
                    Set<Long> abtIds = readAbtIdsNeu4Auftrag(null, auftragId);
                    if (abtIds != null) {
                        for (Long abtId : abtIds) {
                            for (VerlaufAbteilungView view : views) {
                                if (NumberTools.equal(view.getAbtId(), abtId)) {
                                    break;
                                }
                            }

                            // Abteilung noch nicht vorhanden -> hinzufuegen
                            Niederlassung nlAkt = baConfigService.findNL4Verteilung(abtId, nl.getId());
                            if (nlAkt != null) {
                                VerlaufAbteilungView view = new VerlaufAbteilungView();
                                view.setAbtId(abtId);
                                view.setNlId(nlAkt.getId());
                                views.add(view);
                            }
                        }
                    }
                }
            }

            // Filter Abteilungen
            VerlaufAbteilungPredicate abtIdPred = new VerlaufAbteilungPredicate();
            if (onlyTechAbts) {
                abtIdPred.addAbtIds(Abteilung.ST_VOICE, Abteilung.ST_CONNECT, Abteilung.ST_ONLINE);
            }

            if (!ignoreSCT) {
                abtIdPred.addAbtIds(Abteilung.FIELD_SERVICE);
            }

            List<VerlaufAbteilungView> result = new ArrayList<>();
            for (VerlaufAbteilungView view : result) {
                if (abtIdPred.evaluate(view.getAbtId())) {
                    result.add(view);
                }
            }

            return (CollectionTools.isNotEmpty(result)) ? result : null;
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerlaufStatus> findVerlaufStati() throws FindException {
        try {
            return getVerlaufDAO().findAll(VerlaufStatus.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerlaufAbteilungStatus> findVerlaufAbteilungStati() throws FindException {
        try {
            return getVerlaufAbteilungDAO().findAll(VerlaufAbteilungStatus.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CCAuftragIDsView> findAuftraege4Verlauf(Long verlaufId) throws FindException {
        try {
            Verlauf verlauf = getVerlaufDAO().findById(verlaufId, Verlauf.class);
            List<Long> auftragIds = new ArrayList<>();
            auftragIds.addAll(verlauf.getAllOrderIdsOfVerlauf());
            return auftragService.findAuftragIdAndVbz4AuftragIds(auftragIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /* ************** Methoden fuer die Ansicht der Verlaeufe der einzelnen Abteilungen ***************** */

    @Override
    @SuppressWarnings("unchecked")
    public List<ProjektierungsView> findProjektierungen4Abt(Long abtId, boolean ruecklaeufer) throws FindException {
        try {
            IServiceCommand cmd = getServiceCommand(FindVerlaufViews4AbtCommand.class);
            cmd.prepare(FindVerlaufViews4AbtCommand.ABTEILUNG_ID, abtId);
            cmd.prepare(FindVerlaufViews4AbtCommand.SEARCH_4_RUECKLAEUFER, (ruecklaeufer) ? Boolean.TRUE : Boolean.FALSE);
            cmd.prepare(FindVerlaufViews4AbtCommand.SEARCH_4_PROJEKTIERUNG, Boolean.TRUE);
            Object result = cmd.execute();
            return (result instanceof List<?>) ? (List<ProjektierungsView>) result : null;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Ermittlung des el. Verlaufs (Projektierung) ist ein Fehler aufgetreten.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractBauauftragView> findBAVerlaufViews4Abt(boolean universalQuery, Long abtId, boolean ruecklaeufer,
            Date realisierungFrom, Date realisierungTo) throws FindException {
        try {
            IServiceCommand cmd = getServiceCommand(FindVerlaufViews4AbtCommand.class);
            cmd.prepare(FindVerlaufViews4AbtCommand.USE_UNIVERSAL_QUERY, universalQuery);
            cmd.prepare(FindVerlaufViews4AbtCommand.ABTEILUNG_ID, abtId);
            cmd.prepare(FindVerlaufViews4AbtCommand.SEARCH_4_RUECKLAEUFER, ruecklaeufer);
            cmd.prepare(FindVerlaufViews4AbtCommand.REALISIERUNG_FROM, realisierungFrom);
            cmd.prepare(FindVerlaufViews4AbtCommand.REALISIERUNG_TO, realisierungTo);
            Object result = cmd.execute();
            return (result instanceof List) ? (List<AbstractBauauftragView>) result : null;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Ermittlung des el. Verlaufs (Bauauftraege) ist ein Fehler aufgetreten.", e);
        }
    }

    @Override
    public List<AbstractBauauftragView> findBAVerlaufViews4KundeInShortTerm(Long kundeNoOrig, Date baseDate, int weekCount) throws FindException {
        if (kundeNoOrig == null) { return null; }
        try {
            int change = weekCount * 7;
            Date min = DateTools.changeDate(baseDate, Calendar.DAY_OF_MONTH, (change * -1));
            Date max = DateTools.changeDate(baseDate, Calendar.DAY_OF_MONTH, change);

            return getVerlaufViewDAO().findBAVerlaufViews4KundeInShortTerm(kundeNoOrig, min, max);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /* Ermittelt die Abteilung-IDs, die einen Bauauftrag fuer eine Neuanlage
     * erhalten sollen.
     * @param verlauf (optional)
     * @param auftragId
     * @return
     */
    private Set<Long> readAbtIdsNeu4Auftrag(Verlauf verlauf, Long auftragId) throws FindException {
        try {
            Set<Long> retVal = new HashSet<>();

            Set<Long> orderIds = (verlauf != null) ? verlauf.getAllOrderIdsOfVerlauf() : new HashSet<>();
            orderIds.add(auftragId);
            for (Long orderId : orderIds) {
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(orderId);
                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(orderId);
                if ((auftragDaten == null) || (auftragTechnik == null)) {
                    throw new FindException("Auftrag-Daten nicht gefunden.");
                }

                BAVerlaufConfig verlConf =
                        baConfigService.findBAVerlaufConfig(BAVerlaufAnlass.NEUSCHALTUNG, auftragDaten.getProdId(), false);
                if (verlConf == null) {
                    throw new FindException("Verlaufs-Konfiguration nicht gefunden!");
                }

                List<Long> configuredAbtIds =
                        baConfigService.findAbteilungen4BAVerlaufConfig(verlConf.getAbtConfigId());
                Set<Long> abtIds = new HashSet<>();
                CollectionTools.addAllIgnoreNull(abtIds, configuredAbtIds);

                Endstelle esB = endstellenService.findEndstelle(auftragTechnik.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_B);

                retVal.addAll(abtIds);
                validateAbteilungsSet(verlauf, retVal);
                readZusaetze(verlauf, retVal, esB, verlConf.getId());
            }
            return retVal;
        }
        catch (Exception e) {
            throw new FindException("Abteilung-IDs konnten nicht ermittelt werden. Bitte manuelle Verteilung!", e);
        }
    }

    /**
     * Ermittelt die Abteilung-IDs, die einen Bauauftrag fuer eine Aenderung erhalten sollen.
     *
     * @param verlauf
     * @return AbteilungsIDs
     */
    private Set<Long> readAbtIdsAend4Anlass(Verlauf verlauf) throws FindException {
        try {
            Set<Long> retVal = new HashSet<>();

            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            for (Long orderId : orderIds) {
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(orderId);
                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(orderId);
                if ((auftragDaten == null) || (auftragTechnik == null)) {
                    throw new FindException("Auftrag-Daten nicht gefunden.");
                }

                BAVerlaufConfig verlConf =
                        baConfigService.findBAVerlaufConfig(verlauf.getAnlass(), auftragDaten.getProdId(), true);
                if ((verlConf == null) || NumberTools.equal(verlConf.getId(), BAVerlaufConfig.CONFIG_ID_MANUELL)) {
                    throw new FindException(
                            "Aufgrund der Konfiguration kann der Bauauftrag nur manuell verschickt werden!");
                }

                List<Long> configuredAbtIds =
                        baConfigService.findAbteilungen4BAVerlaufConfig(verlConf.getAbtConfigId());
                Set<Long> abtIds = new HashSet<>();
                CollectionTools.addAllIgnoreNull(abtIds, configuredAbtIds);

                retVal.addAll(abtIds);

                if (NumberTools.notEqual(verlauf.getAnlass(), BAVerlaufAnlass.KUENDIGUNG)) {
                    validateAbteilungsSet(verlauf, retVal);
                }

                final Endstelle esB = endstellenService.findEndstelle(auftragTechnik.getAuftragTechnik2EndstelleId(),
                        Endstelle.ENDSTELLEN_TYP_B);
                readZusaetze(verlauf, retVal, esB, verlauf.getAnlass());
            }
            return retVal;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FindException("Auftrag-IDs konnten nicht ermittelt werden. Bitte manuelle Verteilung!", e);
        }
    }

    /**
     * Durchlaeuft das Set mit den Abteilungs-IDs und prueft im Falle von FieldService und FFM, ob der Bauauftrag auch
     * wirklich an diese Abteilung verteilt werden soll. <br/> Falls nicht, so wird die entsprechende ID aus dem Set
     * entfernt.
     */
    void validateAbteilungsSet(Verlauf verlauf, Set<Long> abtIds) {
        if (verlauf == null) { return; }

        Set<Long> tempSet = new HashSet<>(abtIds);
        abtIds.clear();

        Set<Long> filteredAbtIds = tempSet.stream()
                .filter(abtId -> shouldAbteilungBeUsedForVerlauf(abtId, verlauf))
                .collect(Collectors.toSet());

        abtIds.addAll(filteredAbtIds);
    }

    /**
     * Fuehrt die Pruefung durch, ob eine Abteilung einen Verlauf wirklich erhalten soll. Aktuell nur fuer Abteilungen
     * FieldService und FFM!
     *
     * @return
     */
    private boolean shouldAbteilungBeUsedForVerlauf(Long abtId, Verlauf verlauf) {
        try {
            if (Abteilung.FIELD_SERVICE.equals(abtId)) {
                // Montageart pruefen
                return hasExternInstallation(verlauf.getId());
            }
            else if (Abteilung.FFM.equals(abtId)) {
                List<TechLeistung> techLeistungen = leistungsService.findTechLeistungen4Verlauf(verlauf.getId(), true);
                return techLeistungen.stream()
                        .filter(tl -> !ffmService.getFfmQualifications(Collections.singletonList(tl)).isEmpty())
                        .findFirst()
                        .isPresent();
            }
            return true;
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Fehler bei der Überprüfung, ob die Abteilung den Verlauf erhalten soll: %s", e.getMessage()), e);
        }
    }


    /* Liest die Zusatz-Konfiguration fuer einen Verlauf aus und speichert die Abteilung-IDs in dem Set. */
    void readZusaetze(Verlauf verlauf, Set<Long> abtIds, Endstelle esB, Long baVerlaufConfigId) throws FindException {
        Long hvtGrId = null;
        Long standortTypRefId = null;
        if (esB != null && esB.getHvtIdStandort() != null) {
            HVTStandort hvtStandort = hvtService.findHVTStandort(esB.getHvtIdStandort());
            hvtGrId = hvtStandort.getHvtGruppeId();
            standortTypRefId = hvtStandort.getStandortTypRefId();
        }
        // Zusatz-Eintraege suchen
        List<BAVerlaufZusatz> baZusaetze = baConfigService.findBAVerlaufZusaetze4BAVerlaufConfig(baVerlaufConfigId,
                hvtGrId, standortTypRefId);
        if (baZusaetze != null) {
            for (BAVerlaufZusatz zusatz : baZusaetze) {
                if (NumberTools.equal(zusatz.getAbtId(), Abteilung.FIELD_SERVICE)) {
                    if (esB != null) {
                        if (!hasExternInstallation(verlauf.getId()) && nullToFalse(zusatz.getAuchSelbstmontage())) {
                            // entspricht Selbstmontage
                            abtIds.add(zusatz.getAbtId());
                        }

                        if (hasExternInstallation(verlauf.getId())) {
                            abtIds.add(zusatz.getAbtId());
                        }
                    }
                }
                else {
                    abtIds.add(zusatz.getAbtId());
                }
            }
        }
    }

    /* Ermittelt die Abteilungs-IDs, die wegen der Physik-Uebernahme zus. den Verlauf
     * erhalten sollen.
     * @param verlauf der zu verteilende Verlauf
     * @param abtIds Liste der Abteilungs-IDs, die evtl. erweitert werden soll.
     * @param auftragId ID des Auftrags fuer den ein BA erstellt werden soll
     */
    private void addAbtIds4PhysikUebernahme(Verlauf verlauf, Set<Long> abtIds) throws FindException {
        try {
            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            for (Long orderId : orderIds) {
                PhysikaenderungsTyp pt = getPhysikUebernahmeDAO().findPhysikaenderungsTyp(orderId, verlauf.getId());
                if (pt != null) {
                    Set<Long> ids = pt.getAbteilungIdSet();
                    if ((ids != null) && (!ids.isEmpty())) {
                        Iterator<Long> keyIt = ids.iterator();
                        while (keyIt.hasNext()) {
                            Long id = keyIt.next();

                            boolean addId = true;
                            for (Long x : abtIds) {
                                if (NumberTools.equal(x, id)) {
                                    addId = false;
                                    break;
                                }
                            }

                            if (addId) {
                                abtIds.add(id);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Beim Auslesen der zu informierenden Abteilungen (wg. Physikuebernahme) " +
                    "ist ein nicht erwarteter Fehler aufgetreten.");
        }
    }

    /* Fuegt der Liste <code>abtIds</code> die Abteilungen hinzu, die aufgrund der
     * Leistungs-Konfiguration und der aktuellen Leistungs-Differenz (VerlaufAction) den
     * Bauauftrag erhalten sollen/muessen.
     * @param verlaufId Verlaufs-ID, ueber die die Leistungs-Differenz ermittelt werden kann.
     * @param abtIds Liste der Abteilungs-IDs, die evtl. erweitert werden soll.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    private void addAbtIds4LeistungsKonfig(Long verlaufId, Set<Long> abtIds) throws FindException {
        try {
            // Differenz der Abteilung-IDs ermitteln und der Liste hinzufuegen
            Collection<Long> sub = getAbtIDDifference4LeistungsKonfig(verlaufId, abtIds);
            abtIds.addAll(sub);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Abfrage der betroffenen Abteilungen aufgrund der " +
                    "Leistungs-Differenz ist ein Fehler aufgetreten.");
        }
    }

    @Override
    public Collection<Long> getMissingAbteilungIds(Long verlaufId, List<SelectAbteilung4BAModel> abtIds) throws FindException {
        Set<Long> abteilungIds = new HashSet<>();
        for (SelectAbteilung4BAModel model : abtIds) {
            if ((model != null) && (model.getAbteilungId() != null)) {
                abteilungIds.add(model.getAbteilungId());
            }
        }

        return getAbtIDDifference4LeistungsKonfig(verlaufId, abteilungIds);
    }

    /**
     * Ermittelt die Abteilung-IDs, die aufgrund der Leistungs-Konfiguration und der aktuellen Leistungs-Differenz des
     * Verlaufs den Bauauftrag erhalten sollten, aber noch nicht in der Liste <code>abtIds</code> enthalten sind.
     *
     * @param verlaufId Verlaufs-ID
     * @param abtIds    Liste mit den bisher zugeordneten Abteilungen
     * @return Collection mit den Abteilung-IDs, die aufgrund der Leistungs-Differenz fuer den Verlauf den Bauauftrag
     * erhalten sollten/muessen.
     * @throws FindException
     */
    @SuppressWarnings("unchecked")
    private Collection<Long> getAbtIDDifference4LeistungsKonfig(Long verlaufId, Set<Long> abtIds) throws FindException {
        List<Long> abtIds4Leistung = leistungsService.findAbtIds4VerlaufTechLs(verlaufId);

        // falls FieldService in Leistungsliste und bei zu verteilenden Abteilungen aus Leistungsliste entfernen
        if (abtIds4Leistung.contains(Abteilung.FIELD_SERVICE) && abtIds.contains(Abteilung.FIELD_SERVICE)) {
            abtIds4Leistung.remove(Abteilung.FIELD_SERVICE);
        }

        // Differenz der Abteilung-IDs ermitteln und der Liste hinzufuegen
        Collection<Long> sub = CollectionUtils.subtract(abtIds4Leistung, abtIds);
        if (abtIds.contains(Abteilung.MQUEUE)) {
            sub.remove(Abteilung.ST_ONLINE);
            sub.remove(Abteilung.ST_VOICE);
        }

        return sub;
    }

    /* ************************************************************** */

    @Override
    public JasperPrint reportVerlauf(Long verlaufId, Long sessionId, boolean projektierung, boolean compact)
            throws AKReportException {
        return reportBauauftrag(verlaufId, sessionId, compact);
    }

    @Override
    public JasperPrint reportVerlauf4Auftrag(Long auftragId, Long sessionId, boolean projektierung, boolean compact)
            throws AKReportException, FindException {
        if ((auftragId == null) || (sessionId == null)) {
            throw new AKReportException("Ungültige Report-Parameter!");
        }
        try {
            Verlauf last = getVerlaufDAO().findLast4Auftrag(auftragId, projektierung);
            if (last == null) {
                if (projektierung) {
                    throw new FindException("Für den Auftrag konnte keine Projektierung ermittelt werden!");
                }
                throw new FindException("Für den Auftrag konnte kein Bauauftrag ermittelt werden!");
            }

            return reportBauauftrag(last.getId(), sessionId, compact);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (projektierung) {
                throw new AKReportException("Fehler beim Erstellen des Projektierung-Reports:\n" + e.getMessage(), e);
            }
            throw new AKReportException("Fehler beim Erstellen des Bauauftrag-Reports:\n" + e.getMessage(), e);
        }
    }

    @Override
    public JasperPrint reportTechDetails4Auftrag(Long auftragId, Long sessionId)
            throws AKReportException {
        try {
            BauauftragEmptyJasperDS baDS = new BauauftragEmptyJasperDS(auftragId);
            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/verlauf/Bauauftrag.jasper",
                    new HashMap<>(), baDS);

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException(
                    "Fehler beim Erstellen des Reports mit den Auftragsdetails:\n" + e.getMessage(), e);
        }
    }

    /**
     * Erstellt einen Bauauftrags-Report fuer den Verlauf mit der ID <code>verlaufId</code>.
     *
     * @param verlaufId
     * @param sessionId
     * @param compact   kompakten (1-seitigen) oder Fax-Report (3-seitig)
     * @return
     * @throws AKReportException
     */
    protected JasperPrint reportBauauftrag(Long verlaufId, Long sessionId, boolean compact) throws AKReportException {
        if ((verlaufId == null) || (sessionId == null)) {
            throw new AKReportException("Ungültige Report-Parameter!");
        }
        try {
            BauauftragJasperDS baDS = new BauauftragJasperDS(verlaufId);
            AKJasperReportContext ctx = new AKJasperReportContext(compact ?
                    "de/augustakom/hurrican/reports/verlauf/compact/Bauauftrag.jasper" :
                    "de/augustakom/hurrican/reports/verlauf/Bauauftrag.jasper",
                    new HashMap<>(), baDS
            );

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Fehler beim Druck des Verlaufs:\n" + e.getMessage(), e);
        }
    }

    @Override
    public JasperPrint reportVerlaufDetails(Long verlaufId) throws AKReportException {
        if (verlaufId == null) {
            throw new AKReportException("Ungültige Report-Parameter!");
        }
        try {
            VerlaufDetailJasperDS detailsDS = new VerlaufDetailJasperDS(verlaufId);
            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/verlauf/VerlaufDetails.jasper",
                    new HashMap<>(), detailsDS);

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Fehler beim Druck der Projektierung:\n" + e.getMessage(), e);
        }
    }

    /* Ermittelt den User mit der Session-ID <code>sessionId</code> */
    protected AKUser getUser(Long sessionId) throws FindException, AKAuthenticationException {
        AKUser user = userService.findUserBySessionId(sessionId);
        if (user == null) {
            throw new FindException(StoreException.INVALID_SESSION_ID);
        }
        return user;
    }

    private Optional<AKUser> getUserIfAvailable(Long sessionId) throws AKAuthenticationException {
        AKUser user = userService.findUserBySessionId(sessionId);
        return Optional.ofNullable(user);
    }


    @Override
    public void changeRealDate(Long verlaufId, Date realDate, Long sessionId, List<SelectAbteilung4BAModel> abtModels)
            throws StoreException {
        try {
            AKUser user = userService.findUserBySessionId(sessionId);
            changeRealDate(verlaufId, realDate, user, abtModels);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new StoreException(e.getMessage(), e);
        }
    }

    private void changeRealDate(Long verlaufId, Date realDate, AKUser user, List<SelectAbteilung4BAModel> abtModels)
            throws StoreException {
        if (verlaufId == null || realDate == null || user == null || CollectionTools.isEmpty(abtModels)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            // Lade Verlauf
            Verlauf v = findVerlauf(verlaufId);
            if (v == null) {
                throw new StoreException("Fehler beim Laden des Bauauftrags");
            }

            // Datum darf nicht in der Vergangenheit liegen
            if (!DateTools.isDateAfterOrEqual(realDate, new Date())) {
                throw new TerminverschiebungException("Neuer Termin darf nicht in der Vergangenheit liegen");
            }
            if (DateTools.isDateBefore(realDate, v.getRealisierungstermin())) {
                throw new TerminverschiebungException("Terminverschiebungen sind nur in die Zukunft möglich");
            }
            if (!DateTools.isWorkDay(realDate)) {
                throw new TerminverschiebungException("Der Realisierungstermin darf ausschliesslich auf einen Arbeitstag fallen!");
            }

            // Pruefe Produkt, ob Terminverschiebung zulaessig ist
            Produkt prod = produktService.findProdukt4Auftrag(v.getAuftragId());
            AuftragTechnik at = auftragService.findAuftragTechnikByAuftragId(v.getAuftragId());

            if (((prod == null) || !nullToFalse(prod.getBaTerminVerschieben()))
                    && !((at != null) && (at.getVpnId() != null))) {
                throw new StoreException("Für dieses Produkt ist eine Terminverschiebung nicht möglich!");
            }

            String username = user.getLoginName();

            // Aenderer Realisierungstermin
            String bemerkung = "Terminverschiebung vom " + DateTools.formatDate(v.getRealisierungstermin(), DateTools.PATTERN_DAY_MONTH_YEAR)
                    + " zum " + DateTools.formatDate(realDate, DateTools.PATTERN_DAY_MONTH_YEAR) + " durch " + username;
            v.setBemerkung((StringUtils.isBlank(v.getBemerkung())) ? bemerkung : v.getBemerkung() + "\n" + bemerkung);
            v.setRealisierungstermin(realDate);
            saveVerlauf(v);

            // Verlauf-Abteilungen verschieben
            List<VerlaufAbteilung> vas = findVerlaufAbteilungen(verlaufId);
            if (CollectionTools.isNotEmpty(vas)) {
                for (VerlaufAbteilung va : vas) {
                    for (SelectAbteilung4BAModel abtModel : abtModels) {
                        if (NumberTools.equal(va.getNiederlassungId(), abtModel.getNiederlassungId())
                                && NumberTools.equal(va.getAbteilungId(), abtModel.getAbteilungId())) {
                            // Check Datum
                            if (DateTools.isDateBefore(abtModel.getRealDate(), new Date()) || DateTools.isDateAfter(abtModel.getRealDate(), realDate)) {
                                throw new StoreException(
                                        "Realisierungsdatum muss groesser/gleich heute und kleiner/gleich dem Realisierungsdatum sein.");
                            }
                            va.setRealisierungsdatum(abtModel.getRealDate());
                            saveVerlaufAbteilung(va);
                            break;
                        }
                    }
                }
            }

            // resend FFM if previous FFM workforce order id is available
            final String previousFfmWorkforceOrderId = v.getWorkforceOrderId();
            if (StringUtils.isNotEmpty(previousFfmWorkforceOrderId)) {
                final String newWorkforceOrderId = reCreateVerlaufFfm(v);
                v.setWorkforceOrderId(newWorkforceOrderId);
                saveVerlauf(v);
            }
        }
        catch (TerminverschiebungException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new StoreException(e.getMessage(), e);
        }
    }

    /**
     * Removes {@link de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder} from FFM, than creates and sends a
     * new one
     *
     * @param bauauftrag Der Hurrican-Bauauftrag, zu dem eine FFM WorkforceOrder erstellt werden soll.
     * @return die generierte UUID fuer der {@link de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder}
     */
    private String reCreateVerlaufFfm(Verlauf bauauftrag) {
        ffmService.deleteOrder(bauauftrag);
        final String workforceOrderId = ffmService.createAndSendOrder(bauauftrag);
        return workforceOrderId;
    }

    @Override
    public void changeRealDate(Long verlaufId, Date realDate, AKUser user) throws StoreException {
        try {
            List<SelectAbteilung4BAModel> baModels = new ArrayList<>();

            List<VerlaufAbteilung> vas = findVerlaufAbteilungen(verlaufId);
            if (CollectionTools.isNotEmpty(vas)) {
                for (VerlaufAbteilung va : vas) {
                    SelectAbteilung4BAModel baModel = new SelectAbteilung4BAModel();
                    baModel.setAbteilungId(va.getAbteilungId());
                    baModel.setNiederlassungId(va.getNiederlassungId());
                    baModel.setRealDate(realDate);
                    baModels.add(baModel);
                }
            }
            changeRealDate(verlaufId, realDate, user, baModels);
        }
        catch (TerminverschiebungException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasExternInstallation(Long verlaufId) throws FindException {
        try {
            Verlauf verlauf = findVerlauf(verlaufId);

            if (verlauf.getInstallationRefId() != null) {
                Reference installRef = referenceService.findReference(verlauf.getInstallationRefId());
                if ((installRef != null) && BooleanTools.getBoolean(installRef.getIntValue())) {
                    return true;
                }
            }

            return endgeraeteService.hasEGWithMontageMnet(verlauf.getAuftragId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Pair<byte[], String> saveVerlaufPDF(Long verlaufId, Long sessionId, boolean projektierung, boolean withDetails)
            throws AKReportException {
        try {
            JasperPrint jpBA = reportVerlauf(verlaufId, sessionId, projektierung, false);

            if (withDetails) {
                JasperPrint jpDetails = reportVerlaufDetails(verlaufId);
                AKJasperReportHelper.joinJasperPrints(jpBA, jpDetails);
            }

            String tmpPath = System.getProperty("java.io.tmpdir");
            if (!(new File(tmpPath)).canWrite()) {
                throw new AKReportException("Keine Schreibrechte im Zielpfad");
            }
            String tmpFile = tmpPath + "BA_" + verlaufId + ".pdf";

            JasperExportManager.exportReportToPdfFile(jpBA, tmpFile);

            final File pdfFile = FileUtils.getFile(tmpFile);
            return (pdfFile.canRead()) ? Pair.create(FileUtils.readFileToByteArray(pdfFile), tmpFile) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (projektierung) {
                throw new AKReportException("Projektierungs-Report konnte nicht erstellt werden!", e);
            }
            throw new AKReportException("Bauauftrags-Report konnte nicht erstellt werden!", e);
        }
    }

    @Override
    public String findBemerkung4Verlauf(Long verlaufId) throws FindException {
        if (verlaufId == null) {
            return null;
        }
        try {
            StringBuilder result = new StringBuilder();
            List<VerlaufAbteilung> vas = findVerlaufAbteilungen(verlaufId);
            for (VerlaufAbteilung va : vas) {
                if ((va != null) && StringUtils.isNotBlank(va.getBemerkung())) {
                    Abteilung abt = niederlassungService.findAbteilung(va.getAbteilungId());
                    if (abt != null) {
                        result.append(abt.getName()).append(": ").append(va.getBemerkung()).append("\n");
                    }
                }
            }

            return (StringUtils.trimToNull(result.toString()));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e);
        }
    }

    @Override
    public boolean isAutomaticallyDispatchable(Verlauf verlauf) throws FindException {
        try {
            boolean automaticallyDispatchable = false;
            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            for (Long orderId : orderIds) {
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(orderId);
                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(orderId);
                Endstelle endstelle = endstellenService.findEndstelle4Auftrag(orderId, Endstelle.ENDSTELLEN_TYP_B);
                BAVerlaufConfig verlaufConfig = baConfigService.findBAVerlaufConfig(verlauf.getAnlass(), auftragDaten.getProdId(), false);

                if (endstelle != null) {
                    HVTStandort hvtStandort = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
                    automaticallyDispatchable = (verlaufConfig != null)
                            && nullToFalse(isHvtConfiguredForAutomaticDispatching(hvtStandort))
                            // automatische Verteilung fuer Produkt/Anlass aktiviert und an Standort
                            && nullToFalse(verlaufConfig.getAutoVerteilen())
                            // Auftrag darf keinem VPN zugeordnet sein
                            && (auftragTechnik.getVpnId() == null)
                            // keine(!) technischen Leistungen zum BA, die autom. Verteilung verhindern
                            && !hasTechLsPreventAutoDispatch(verlauf);

                    if (automaticallyDispatchable && (verlaufConfig != null) && nullToFalse(verlaufConfig.getCpsNecessary())) {
                        // Auftrag und Standort fuer CPS-Provisionierung freigegeben
                        automaticallyDispatchable = !nullToFalse(verlauf.getPreventCPSProvisioning())
                                && cpsService.isCPSProvisioningAllowed(orderId, LazyInitMode.noInitialLoad, true,
                                false, true).isProvisioningAllowed();
                    }
                }
                else {
                    automaticallyDispatchable = (verlaufConfig != null) ? verlaufConfig.getAutoVerteilen() : false;
                }

                if (!automaticallyDispatchable) { break; }
            }

            return automaticallyDispatchable;
        }
        catch (Exception ex) {
            throw new FindException("Status für Verlauf konnte nicht ermittelt werden", ex);
        }
    }


    Boolean isHvtConfiguredForAutomaticDispatching(HVTStandort hvtStandort) {
        return (hvtStandort != null) ? hvtStandort.getAutoVerteilen() : Boolean.TRUE;
    }

    /**
     * Prueft, ob dem Bauauftrag min. eine technische Leistung (als Zugang) zugeordnet ist, die das Flag {@link
     * TechLeistung#preventAutoDispatch} gesetzt hat.
     *
     * @param verlauf
     * @return {@code true} wenn min. eine Leistung mit gesetztem PREVENT_AUTO_DISPATCH Flag durch den Bauauftrag
     * gezogen wird (als Zugang).
     */
    boolean hasTechLsPreventAutoDispatch(Verlauf verlauf) throws FindException {
        try {
            List<TechLeistung> techLsZugaenge = leistungsService.findTechLeistungen4Verlauf(verlauf.getId(), true);
            Optional<TechLeistung> techLsPreventAutoDispatch = techLsZugaenge
                    .stream()
                    .filter(tl -> nullToFalse(tl.getPreventAutoDispatch()))
                    .findFirst();
            return techLsPreventAutoDispatch.isPresent();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(
                    "Fehler bei der Ermittlung, ob techn. Leistungen vorhanden sind, die eine automatische "
                            + "Bauauftragsverteilung verhindern!", e);
        }
    }


    /**
     * @return Returns the verlaufAbteilungDAO.
     */
    public VerlaufAbteilungDAO getVerlaufAbteilungDAO() {
        return this.verlaufAbteilungDAO;
    }

    /**
     * @param verlaufAbteilungDAO The verlaufAbteilungDAO to set.
     */
    public void setVerlaufAbteilungDAO(VerlaufAbteilungDAO verlaufAbteilungDAO) {
        this.verlaufAbteilungDAO = verlaufAbteilungDAO;
    }

    /**
     * @return Returns the verlaufDAO.
     */
    public VerlaufDAO getVerlaufDAO() {
        return verlaufDAO;
    }

    /**
     * @param verlaufDAO The verlaufDAO to set.
     */
    public void setVerlaufDAO(VerlaufDAO verlaufDAO) {
        this.verlaufDAO = verlaufDAO;
    }

    /**
     * @return Returns the physikUebernahmeDAO.
     */
    public PhysikUebernahmeDAO getPhysikUebernahmeDAO() {
        return physikUebernahmeDAO;
    }

    /**
     * @param physikUebernahmeDAO The physikUebernahmeDAO to set.
     */
    public void setPhysikUebernahmeDAO(PhysikUebernahmeDAO physikUebernahmeDAO) {
        this.physikUebernahmeDAO = physikUebernahmeDAO;
    }

    /**
     * @return Returns the verlaufViewDAO.
     */
    public VerlaufViewDAO getVerlaufViewDAO() {
        return verlaufViewDAO;
    }

    /**
     * @param verlaufViewDAO The verlaufViewDAO to set.
     */
    public void setVerlaufViewDAO(VerlaufViewDAO verlaufViewDAO) {
        this.verlaufViewDAO = verlaufViewDAO;
    }

    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    public void setBaConfigService(BAConfigService baConfigService) {
        this.baConfigService = baConfigService;
    }

    public void setNiederlassungService(NiederlassungService niederlassungService) {
        this.niederlassungService = niederlassungService;
    }

    public void setExtServiceProviderService(ExtServiceProviderService extServiceProviderService) {
        this.extServiceProviderService = extServiceProviderService;
    }

    public void setInnenauftragService(InnenauftragService innenauftragService) {
        this.innenauftragService = innenauftragService;
    }

    public void setLeistungsService(CCLeistungsService leistungsService) {
        this.leistungsService = leistungsService;
    }

    public void setFfmService(FFMService ffmService) {
        this.ffmService = ffmService;
    }

    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    public void setEndgeraeteService(EndgeraeteService endgeraeteService) {
        this.endgeraeteService = endgeraeteService;
    }

    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }

    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    void setAkUserService(AKUserService akUserService) {
        this.userService = akUserService;
    }

    public void setRomNotificationService(ResourceOrderManagementNotificationService romNotificationService) {
        this.romNotificationService = romNotificationService;
    }

    void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    void setHardwareDAO(HardwareDAO hardwareDAO) {
        this.hardwareDAO = hardwareDAO;
    }

    void setEkpFrameContractService(EkpFrameContractService ekpFrameContractService) {
        this.ekpFrameContractService = ekpFrameContractService;
    }

    void setVlanService(VlanService vlanService) {
        this.vlanService = vlanService;
    }


}
