/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 15:50:15
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DefaultDeleteDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.AuftragAktionDAO;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.dao.cc.AuftragWholesaleDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragViewDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktion.AktionType;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Leitungsnummer;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Wohnheim;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.model.cc.temp.RevokeCreationModel;
import de.augustakom.hurrican.model.cc.temp.RevokeTerminationModel;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountQuery;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzQuery;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierQuery;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentView;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungQuery;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungView;
import de.augustakom.hurrican.model.shared.view.AuftragVorlaufView;
import de.augustakom.hurrican.model.shared.view.DefaultSharedAuftragView;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.LoadException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractRevokeCommand;
import de.augustakom.hurrican.service.cc.impl.command.ChangeProductCommand;
import de.augustakom.hurrican.service.cc.impl.command.CopyAuftragCommand;
import de.augustakom.hurrican.service.cc.impl.command.CreateAuftragCommand;
import de.augustakom.hurrican.service.cc.impl.command.CreateAuftragMonitorCommand;
import de.augustakom.hurrican.service.cc.impl.command.CreateTechAuftragCommand;
import de.augustakom.hurrican.service.cc.impl.command.FindMainOrder4SIPCustomer;
import de.augustakom.hurrican.service.cc.impl.command.ReportAccountCommand;
import de.augustakom.hurrican.service.cc.impl.command.RevokeCreationCommand;
import de.augustakom.hurrican.service.cc.impl.command.RevokeTerminationCommand;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;
import de.augustakom.hurrican.service.utils.HistoryHelper;
import de.augustakom.hurrican.service.wholesale.ModifyPortPendingException;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Service-Implementierung von CCAuftragService.
 *
 *
 */
@CcTxRequired
public class CCAuftragServiceImpl extends DefaultCCService implements CCAuftragService {

    private static final Logger LOGGER = Logger.getLogger(CCAuftragServiceImpl.class);


    private static final String ERROR_IN_BUENDEL_HAUPTAUFTRAG = "Zum angegeben Billing-Hauptauftrag '%s' wurde bereits "
            + "ein Hurrican-Bündelauftrag '%s' zugeordnet. Eine Zuordnung auf Basis der Billing Haupt-/Unterauftragsstruktur"
            + " kann daher nicht durchgeführt werden!";

    private CCAuftragViewDAO auftragViewDAO;
    private Object auftragStatusDAO;
    private AuftragDatenDAO auftragDatenDAO;
    private AuftragTechnikDAO auftragTechnikDAO;
    private CCAuftragDAO auftragDAO;
    private Hibernate4DefaultDeleteDAO defaultDeleteDAO;
    private AuftragAktionDAO auftragAktionDAO;
    private AuftragWholesaleDAO auftragWholesaleDAO;

    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    private AKUserService userService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.VoIPService")
    private VoIPService voIPService;

    @Autowired
    private ServiceLocator serviceLocator;

    // WORKAROUNDS FOR CIRCULAR DEPENDENCIES -> see below
    private ProduktService produktService;
    private RangierungsService rangierungsService;

    @Override
    public Pair<CheckAnzNdrahtResult, Collection<AuftragDaten>> checkAnzahlNdrahtOptionAuftraege(final long auftragId) {
        try {
            final AuftragDaten hauptAuftrag = findAuftragDatenByAuftragIdTx(auftragId);
            final Produkt produkt = getProduktService().findProdukt(hauptAuftrag.getProdId());

            final CheckAnzNdrahtResult result;
            final Collection<AuftragDaten> relAuftragDaten;
            if (produkt.getSdslNdraht() == null) {
                result = CheckAnzNdrahtResult.NO_NDRAHT_CONFIG;
                relAuftragDaten = Collections.emptyList();
            }
            else {
                final Collection<AuftragDaten> nDrahtOptionAuftraege = getRelevantNdrahtOptionAuftragDatens(
                        hauptAuftrag);
                // anzahl n-Draht-Optionen + eigentl. SDSL-Auftrag
                result = getCheckAnzNdrahtResult(produkt, nDrahtOptionAuftraege.size() + 1);
                relAuftragDaten = new ImmutableList.Builder()
                        .add(hauptAuftrag)
                        .addAll(nDrahtOptionAuftraege)
                        .build();
            }
            return Pair.create(result, relAuftragDaten);
        }
        catch (FindException fe) {
            throw new RuntimeException(fe);
        }
    }

    private Collection<AuftragDaten> getRelevantNdrahtOptionAuftragDatens(final AuftragDaten hauptAuftrag)
            throws FindException {
        final List<AuftragDaten> auftragDaten4Buendel =
                findAuftragDaten4OrderNoOrigTx(hauptAuftrag.getAuftragNoOrig());
        return (auftragDaten4Buendel == null)
                ? Collections.<AuftragDaten>emptyList()
                : Collections2.filter(
                auftragDaten4Buendel,
                input -> ((input != null)
                        && (hauptAuftrag.getAuftragStatusId().equals(input.getAuftragStatusId()))
                        && (Produkt.isSdslNDraht(input.getProdId())))
        );
    }

    private CheckAnzNdrahtResult getCheckAnzNdrahtResult(final Produkt produkt, final int anzRelevanterAuftraege) {
        final CheckAnzNdrahtResult result;

        if (produkt == null || produkt.getSdslNdraht() == null) {
            result = CheckAnzNdrahtResult.NO_NDRAHT_CONFIG;
        }
        else if (produkt.getSdslNdraht().mandatory) {
            result = (produkt.getSdslNdraht().anzahlAuftraege == anzRelevanterAuftraege)
                    ? CheckAnzNdrahtResult.AS_EXPECTED
                    : (produkt.getSdslNdraht().anzahlAuftraege > anzRelevanterAuftraege)
                    ? CheckAnzNdrahtResult.LESS_THAN_EXPECTED
                    : CheckAnzNdrahtResult.MORE_THAN_EXPECTED;
        }
        else {
            result = CheckAnzNdrahtResult.AS_EXPECTED;
        }
        return result;
    }

    @Override
    public Auftrag createAuftrag(Long kundeNo, AuftragDaten aDaten, AuftragTechnik aTechnik,
            Long sessionId, IServiceCallback serviceCallback) throws StoreException {
        return createAuftrag(kundeNo, aDaten, aTechnik, null, sessionId, serviceCallback);
    }

    @Override
    public Auftrag createAuftrag(Long kundeNo, AuftragDaten aDaten, AuftragTechnik aTechnik,
            Wohnheim wohnheim, Long sessionId, IServiceCallback serviceCallback) throws StoreException {
        try {
            if (aDaten.getProdId() == null) {
                throw new StoreException(StoreException.INVALID_PRODUCT_ID);
            }

            IServiceCommand cmd = serviceLocator.getCmdBean(CreateAuftragCommand.class);
            cmd.prepare(CreateAuftragCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(CreateAuftragCommand.KEY_AUFTRAG_DAO, getAuftragDAO());
            cmd.prepare(CreateAuftragCommand.KEY_KUNDE_NO, kundeNo);
            cmd.prepare(CreateAuftragCommand.KEY_AUFTRAG_DATEN, aDaten);
            cmd.prepare(CreateAuftragCommand.KEY_AUFTRAG_TECHNIK, aTechnik);
            cmd.prepare(CreateAuftragCommand.KEY_WOHNHEIM, wohnheim);
            cmd.prepare(CreateAuftragCommand.KEY_SERVICE_CALLBACK, serviceCallback);
            Object result = cmd.execute();

            if (result instanceof Auftrag) {
                if (aDaten.getAuftragNoOrig() != null) {
                    final BAuftrag bAuftrag = billingAuftragService.findAuftrag(aDaten.getAuftragNoOrig());
                    createBillingHauptauftragsBuendel(aDaten, bAuftrag);

                }
                return (Auftrag) result;
            }
            else {
                return null;
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_CC_AUFTRAG, e);
        }
    }


    @Override
    public void createBillingHauptauftragsBuendel(AuftragDaten ad, BAuftrag bAuftrag) throws FindException, StoreException {
        if (bAuftrag != null && bAuftrag.getHauptAuftragNo() != null) {
            Long bHauptAuftragNo = bAuftrag.getHauptAuftragNo();
            ad.setBuendelNr(bHauptAuftragNo.intValue());
            ad.setBuendelNrHerkunft(AuftragDaten.BUENDEL_HERKUNFT_BILLING_HAUPTAUFTRAG);
            this.saveAuftragDaten(ad, false);

            //check if main order is correct
            List<AuftragDaten> hurMainOrders = this.findAllAuftragDaten4OrderNoOrig(bHauptAuftragNo);
            if (CollectionUtils.isNotEmpty(hurMainOrders)) {
                for (AuftragDaten hurMainOrder : hurMainOrders) {
                    //if no bundle number is set, set the billing main order number
                    if (hurMainOrder.getBuendelNr() == null) {
                        hurMainOrder.setBuendelNrHerkunft(AuftragDaten.BUENDEL_HERKUNFT_BILLING_HAUPTAUFTRAG);
                        hurMainOrder.setBuendelNr(bHauptAuftragNo.intValue());
                        this.saveAuftragDaten(hurMainOrder, false);
                    }
                    else if (hurMainOrder.getBuendelNr().compareTo(bHauptAuftragNo.intValue()) != 0) {
                        throw new StoreException(String.format(ERROR_IN_BUENDEL_HAUPTAUFTRAG, bHauptAuftragNo, hurMainOrder.getBuendelNr()));
                    }
                }
            }
        }
    }

    @Override
    public Auftrag changeProduct(Long auftragIdOld, Long physikaenderungsTyp, Long newProdId,
            Long newAuftragNoOrig, Date date, Long sessionId, IServiceCallback serviceCallback) throws StoreException {
        try {
            if (newProdId == null) {
                throw new StoreException(StoreException.INVALID_PRODUCT_ID);
            }

            IServiceCommand cmd = serviceLocator.getCmdBean(ChangeProductCommand.class);
            cmd.prepare(ChangeProductCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(ChangeProductCommand.KEY_OLD_AUFTRAGS_ID, auftragIdOld);
            cmd.prepare(ChangeProductCommand.KEY_NEW_PRODUCT_ID, newProdId);
            cmd.prepare(ChangeProductCommand.KEY_PHYSIKAENDERUNGSTYP, physikaenderungsTyp);
            cmd.prepare(ChangeProductCommand.KEY_CHANGE_DATE, date);
            cmd.prepare(ChangeProductCommand.KEY_SERVICE_CALLBACK, serviceCallback);
            cmd.prepare(ChangeProductCommand.KEY_NEW_ORDER__NO, newAuftragNoOrig);
            Object result = cmd.execute();
            return result instanceof Auftrag ? (Auftrag) result : null;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CHANGE_PRODUCT, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public List<Auftrag> copyAuftrag(Long sessionId, Long id2Copy, Long parentAuftragId, Integer buendelNr,
            String buendelHerkunft, int anzahlCopies, Date vorgabeSCV, boolean copyES) throws StoreException {
        try {
            AKUser user = getAKUserBySessionId(sessionId);

            IServiceCommand cmd = serviceLocator.getCmdBean(CopyAuftragCommand.class);
            cmd.prepare(CopyAuftragCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(CopyAuftragCommand.KEY_AK_USER, user);
            cmd.prepare(CopyAuftragCommand.KEY_AUFTRAGID_2_COPY, id2Copy);
            cmd.prepare(CopyAuftragCommand.KEY_PARENT_AUFTRAG_ID, parentAuftragId);
            cmd.prepare(CopyAuftragCommand.KEY_BUENDEL_NR, buendelNr);
            cmd.prepare(CopyAuftragCommand.KEY_BUENDEL_HERKUNFT, buendelHerkunft);
            cmd.prepare(CopyAuftragCommand.KEY_COPY_COUNT, anzahlCopies);
            cmd.prepare(CopyAuftragCommand.KEY_VORGABE_SCV, vorgabeSCV);
            cmd.prepare(CopyAuftragCommand.KEY_COPY_ES, copyES ? Boolean.TRUE : Boolean.FALSE);
            Object resultObj = cmd.execute();
            @SuppressWarnings("unchecked")
            List<Auftrag> result = (List<Auftrag>) resultObj;
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_CC_AUFTRAG, e);
        }
    }

    @Override
    public List<AuftragsMonitor> createAuftragMonitor(Long kundeNo, Long taifunOrderNoOrig) throws LoadException {
        try {
            CreateAuftragMonitorCommand cmd = new CreateAuftragMonitorCommand();
            cmd.prepare(CreateAuftragMonitorCommand.AUFTRAG_VIEW_DAO, getAuftragViewDAO());
            cmd.prepare(CreateAuftragMonitorCommand.KUNDE_NO, kundeNo);
            cmd.prepare(CreateAuftragMonitorCommand.TAIFUN_ORDER__NO, taifunOrderNoOrig);
            cmd.prepare(CreateAuftragMonitorCommand.BILLING_AUFTRAG_SERVICE, billingAuftragService);
            Object resultObj = cmd.execute();
            @SuppressWarnings("unchecked")
            List<AuftragsMonitor> result = resultObj instanceof List<?> ? (List<AuftragsMonitor>) resultObj : null;
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LoadException(LoadException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Auftrag saveAuftrag(Auftrag auftragToSave) throws StoreException {
        try {
            getAuftragDAO().save(auftragToSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
        return auftragToSave;
    }

    @Override
    public AuftragDaten saveAuftragDaten(AuftragDaten auftragDaten, boolean createHistory) throws StoreException {
        if (auftragDaten == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            AuftragDatenDAO dao = getAuftragDatenDAO();
            if (createHistory && auftragDaten.getId() != null) {
                return dao.update4History(auftragDaten, auftragDaten.getId(), new Date());
            }
            HistoryHelper.checkHistoryDates(auftragDaten);
            dao.store(auftragDaten);
            return auftragDaten;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public void saveAuftragDatenNoTx(AuftragDaten auftragDaten) throws StoreException {
        try {
            AuftragDatenDAO dao = getAuftragDatenDAO();
            dao.store(auftragDaten);
            dao.flushSession();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragTechnik saveAuftragTechnik(AuftragTechnik auftragTechnik, boolean createHistory)
            throws StoreException {
        if (auftragTechnik == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            AuftragTechnikDAO dao = getAuftragTechnikDAO();
            if (createHistory && auftragTechnik.getId() != null) {
                return dao.update4History(auftragTechnik, auftragTechnik.getId(), new Date());
            }
            HistoryHelper.checkHistoryDates(auftragTechnik);
            dao.store(auftragTechnik);
            return auftragTechnik;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Auftrag findAuftragById(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) {
            return null;
        }
        try {
            CCAuftragDAO dao = getAuftragDAO();
            return dao.findById(ccAuftragId, Auftrag.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Collection<CCAuftragIDsView> findAufragIdAndVbz4AuftragNoOrigAndBuendel(Long auftragNoOrig, Integer buendelNr,
            String buendelNrHerkunft) throws FindException {
        List<CCAuftragIDsView> auftraegeByBuendel = findAuftragIdAndVbz4Buendel(buendelNr, buendelNrHerkunft);
        List<CCAuftragIDsView> auftraegeByAuftragNoOrig = findAufragIdAndVbz4AuftragNoOrig(auftragNoOrig);

        Collection<CCAuftragIDsView> buendel = Stream.concat(auftraegeByBuendel.stream(), auftraegeByAuftragNoOrig.stream())
                .collect(Collectors.toMap(CCAuftragIDsView::getAuftragId, Function.identity(), (a, b) -> a))
                .values();
        return buendel;
    }

    @Override
    public List<CCAuftragIDsView> findAufragIdAndVbz4AuftragNoOrig(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return Collections.emptyList();
        }
        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            return dao.findAufragIdAndVbz4AuftragNoOrig(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CCAuftragIDsView> findAuftragIdAndVbz4Buendel(Integer buendel, String buendelNrHerkunft)
            throws FindException {
        if (buendel == null || buendel == 0) {
            return Collections.emptyList();
        }
        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            return dao.findAuftragIdAndVbz4BuendelNr(buendel, buendelNrHerkunft);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CCAuftragIDsView> findAuftragIdAndVbz4AuftragIds(Collection<Long> auftragIds) throws FindException {
        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            return dao.findAufragIdAndVbz4AuftragIds(auftragIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAuftragDaten4Sperre(Long kundeNo) throws FindException {
        if (kundeNo == null) {
            return null;
        }
        try {
            return getAuftragDatenDAO().find4Sperre(kundeNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragDaten findMainOrder4SIPCustomer(Long auftragId) throws FindException {
        try {
            if (auftragId == null) {
                throw new FindException(FindException.INVALID_FIND_PARAMETER);
            }

            IServiceCommand cmd = serviceLocator.getCmdBean(FindMainOrder4SIPCustomer.class);
            cmd.prepare(FindMainOrder4SIPCustomer.KEY_AUFTRAG_ID, auftragId);
            Object result = cmd.execute();
            return result instanceof AuftragDaten ? (AuftragDaten) result : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDatenView> findAuftragDatenViews(AuftragDatenQuery query, boolean loadBillingData)
            throws FindException {
        if (query == null || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            List<AuftragDatenView> result = dao.findAuftragDatenViews(query);
            if (CollectionTools.isNotEmpty(result)) {
                List<Long> auftragIds = new ArrayList<>();
                for (AuftragDatenView view : result) {
                    if (StringUtils.isNotBlank(view.getProdNamePattern())) {
                        auftragIds.add(view.getAuftragId());
                    }
                }

                ProduktService produktService = getCCService(ProduktService.class);
                List<Produkt> produkte = produktService.findProdukte(false);
                Map<Long, Produkt> produktMap = new HashMap<>();
                for (Produkt produkt : produkte) {
                    produktMap.put(produkt.getId(), produkt);
                }

                CCLeistungsService leistungsService = getCCService(CCLeistungsService.class);
                Map<Long, List<TechLeistung>> techLeistungen =
                        leistungsService.findTechLeistungen4Auftraege(auftragIds, null, true);
                for (AuftragDatenView view : result) {
                    if (StringUtils.isNotBlank(view.getProdNamePattern())) {
                        String name = produktService.generateProduktName(produktMap.get(view.getProdId()),
                                techLeistungen.get(view.getAuftragId()));
                        view.setProduktName(name);
                    }
                }

                if (loadBillingData) {
                    kundenService.loadKundendaten4AuftragViews(result);
                }
                loadProjectResponsibleUserNames(result);

            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDatenView> findAuftragDatenViews(AuftragSAPQuery query) throws FindException {
        if (query == null || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            List<BAuftrag> list = billingAuftragService.findAuftraege4SAP(query);
            if (CollectionTools.isNotEmpty(list)) {
                List<AuftragDatenView> result = new ArrayList<>();
                List<Long> auftragNos = new ArrayList<>();
                for (BAuftrag auftrag : list) {
                    if (auftrag != null && !auftragNos.contains(auftrag.getAuftragNoOrig())) {
                        auftragNos.add(auftrag.getAuftragNoOrig());
                        AuftragDatenQuery auftragDatenQuery = new AuftragDatenQuery();
                        auftragDatenQuery.setAuftragNoOrig(auftrag.getAuftragNoOrig());
                        result.addAll(getAuftragViewDAO().findAuftragDatenViews(auftragDatenQuery));
                    }
                }

                kundenService.loadKundendaten4AuftragViews(result);
                loadProjectResponsibleUserNames(result);

                return result;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragRealisierungView> findRealisierungViews(AuftragRealisierungQuery query, boolean loadBillingData)
            throws FindException {
        if (query == null || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        if (query.getInbetriebnahmeFrom() == null || query.getInbetriebnahmeTo() == null) {
            throw new FindException("Es muss ein Datumsbereich angegeben werden!");
        }
        if (!query.getInbetriebnahme() && !query.getRealisierung() && !query.getKuendigung()) {
            throw new FindException("Eine Filtermöglichkeit muss selektiert sein.");
        }

        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            List<AuftragRealisierungView> result = dao.findRealisierungViews(query);

            if (loadBillingData) {
                kundenService.loadKundendaten4AuftragViews(result);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragCarrierView> findAuftragCarrierViews(AuftragCarrierQuery query) throws FindException {
        if (query == null || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            List<AuftragCarrierView> result = dao.findAuftragCarrierViews(query);

            kundenService.loadKundendaten4AuftragViews(result);

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<WbciRequestCarrierView> findWbciRequestCarrierViews(String vorabstimmungsId) throws FindException {
        if (StringUtils.isEmpty(vorabstimmungsId)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            return dao.findWbciRequestCarrierViews(vorabstimmungsId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragEndstelleView> findAuftragEndstelleViews(AuftragEndstelleQuery query) throws FindException {
        long begin = System.currentTimeMillis();
        if (query == null || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            List<AuftragEndstelleView> result = dao.findAuftragEndstelleViews(query);
            loadProjectResponsibleUserNames(result);
            kundenService.loadKundendaten4AuftragViews(result);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        finally {
            LOGGER.debug("Dauer 'findAuftragEndstelleViews': " + (System.currentTimeMillis() - begin));
        }
    }

    private void loadProjectResponsibleUserNames(List<? extends DefaultSharedAuftragView> result)
            throws AKAuthenticationException {
        Map<Long, String> users = userService.findUserIdToNames();
        for (DefaultSharedAuftragView view : result) {
            Long projectResponsibleUserId = view.getProjectResponsibleUserId();
            if (projectResponsibleUserId != null) {
                view.setProjectResponsibleUserName(users.get(projectResponsibleUserId));
            }
        }
    }

    @Override
    public List<AuftragEquipmentView> findAuftragEquipmentViews(AuftragEquipmentQuery query) throws FindException {
        if (query == null || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        if (query.isIncomplete()) {
            throw new FindException(FindException.INSUFFICIENT_FIND_PARAMETER);
        }

        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            List<AuftragEquipmentView> result = dao.findAuftragEquipmentViews(query);

            kundenService.loadKundendaten4AuftragViews(result);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragEndstelleView> findAuftragEndstelleViews4VPN(Long vpnId, List<Long> kNos) throws FindException {
        if (vpnId == null && (kNos == null || kNos.isEmpty())) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            List<AuftragEndstelleView> result = dao.findAuftragEndstelleViews4VPN(vpnId, kNos);

            kundenService.loadKundendaten4AuftragViews(result);

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CCAuftragProduktVbzView> findAuftragProduktVbzViews(CCAuftragProduktVbzQuery query)
            throws FindException {
        if (query == null || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            return getAuftragViewDAO().findAuftragProduktVbzViews(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragVorlaufView> findAuftragsVorlauf() throws FindException {
        try {
            List<AuftragVorlaufView> result = getAuftragViewDAO().findAuftragsVorlauf();
            try {
                kundenService.loadKundendaten4AuftragViews(result);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragIntAccountView> findAuftragAccountViews(AuftragIntAccountQuery query) throws FindException {
        try {
            CCAuftragViewDAO dao = getAuftragViewDAO();
            return dao.findAuftragAccountViews(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragStatus> findAuftragStati() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getAuftragStatusDAO();
            return dao.findAll(AuftragStatus.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragStatus findAuftragStatus(Long statusId) throws FindException {
        if (statusId == null) {
            return null;
        }
        try {
            FindDAO dao = (FindDAO) getAuftragStatusDAO();
            return dao.findById(statusId, AuftragStatus.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public AuftragDaten findAuftragDatenByAuftragId(Long auftragId) throws FindException {
        return findAuftragDatenByAuftragIdTx(auftragId);
    }

    @Override
    public AuftragDaten findAuftragDatenByAuftragIdTx(Long auftragId) throws FindException {
        if (auftragId == null) {
            return null;
        }
        try {
            AuftragDatenDAO dao = getAuftragDatenDAO();
            return dao.findByAuftragId(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public List<AuftragDaten> findAuftragDatenByKundeNo(@Nonnull Long kundeNo) throws FindException {
        try {
            AuftragDatenDAO auftragDatenDAO = getAuftragDatenDAO();
            return auftragDatenDAO.findByKundeNo(kundeNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public List<AuftragDaten> findAuftragDaten4Buendel(Integer buendelNr, String buendelNrHerkunft)
            throws FindException {
        return findAuftragDaten4BuendelTx(buendelNr, buendelNrHerkunft);
    }

    @Override
    public List<AuftragDaten> findAuftragDaten4BuendelTx(Integer buendelNr, String buendelNrHerkunft)
            throws FindException {
        if (buendelNr == null || buendelNrHerkunft == null) {
            return null;
        }
        try {
            AuftragDatenDAO dao = getAuftragDatenDAO();
            return dao.findByBuendelNr(buendelNr, buendelNrHerkunft);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public List<AuftragDaten> findAuftragDaten4OrderNoOrig(Long orderNoOrig) throws FindException {
        return findAuftragDaten4OrderNoOrigHelper(orderNoOrig, false);
    }

    @Override
    public List<AuftragDaten> findAuftragDaten4OrderNoOrigTx(Long orderNoOrig) throws FindException {
        return findAuftragDaten4OrderNoOrigHelper(orderNoOrig, false);
    }

    @Override
    public List<AuftragDaten> findAllAuftragDaten4OrderNoOrig(Long orderNoOrig) throws FindException {
        return findAuftragDaten4OrderNoOrigHelper(orderNoOrig, true);
    }

    @Override
    public List<AuftragDaten> findAllAuftragDaten4OrderNoOrigTx(Long orderNoOrig) throws FindException {
        return findAuftragDaten4OrderNoOrigHelper(orderNoOrig, true);
    }

    private List<AuftragDaten> findAuftragDaten4OrderNoOrigHelper(Long orderNoOrig, boolean ignoreStatus) throws FindException {
        if (orderNoOrig == null) {
            return null;
        }
        try {
            return getAuftragDatenDAO().findByOrderNoOrig(orderNoOrig, ignoreStatus);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public AuftragDaten findAuftragDatenByEndstelle(Long endstelleId) throws FindException {
        return findAuftragDatenByEndstelleTx(endstelleId);
    }

    @Override
    public AuftragDaten findAuftragDatenByEndstelleTx(Long endstelleId) throws FindException {
        if (endstelleId == null) {
            return null;
        }
        try {
            return getAuftragDatenDAO().findByEndstelleId(endstelleId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByGeoIdProduktIds(Long geoId, Long... produktIds) {
        if (geoId == null) {
            return Lists.newArrayList();
        }
        return getAuftragDatenDAO().findByGeoIdProduktIds(geoId, produktIds);
    }

    @Override
    public AuftragDaten findParentAuftragDaten(Long kundeNo, Integer buendelNr, String buendelHerkunft)
            throws FindException {
        if (kundeNo == null || buendelNr == null || buendelHerkunft == null) {
            return null;
        }
        try {
            return getAuftragDatenDAO().findParent4Buendel(kundeNo, buendelNr, buendelHerkunft);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public AuftragTechnik findAuftragTechnikByAuftragId(Long auftragId) throws FindException {
        return findAuftragTechnikByAuftragIdTx(auftragId);
    }

    @Override
    public AuftragTechnik findAuftragTechnikByAuftragIdTx(Long auftragId) throws FindException {
        if (auftragId == null) {
            return null;
        }
        try {
            return getAuftragTechnikDAO().findByAuftragId(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragTechnik findAuftragTechnik4ESGruppe(Long esGruppeId) throws FindException {
        if (esGruppeId == null) {
            return null;
        }
        try {
            return getAuftragTechnikDAO().findAuftragTechnik4ESGruppe(esGruppeId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByEquipment(Long equipmentId) throws FindException {
        if (equipmentId == null) {
            return new ArrayList<>(0);
        }
        try {
            return getAuftragDatenDAO().findAuftragDatenByEquipment(equipmentId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByEquipment(String switchAK, String hwEQN, Date gueltig)
            throws FindException {
        try {
            return getAuftragDatenDAO().findAuftragDatenByEquipment(switchAK, hwEQN, gueltig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByRackAndEqn(String rackBezeichnung, String hwEQN, Date gueltig)
            throws FindException {
        try {
            return getAuftragDatenDAO().findAuftragDatenByRackAndEqn(rackBezeichnung, hwEQN, gueltig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByBaugruppe(Long baugruppeId) throws FindException {
        if (baugruppeId == null) {
            return new ArrayList<>(0);
        }
        try {
            return getAuftragDatenDAO().findAuftragDatenByBaugruppe(baugruppeId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragTechnik> findAuftragTechnik4IntAccount(Long intAccountId) throws FindException {
        if (intAccountId == null) {
            return new ArrayList<>(0);
        }
        try {
            return getAuftragTechnikDAO().findByIntAccountId(intAccountId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint reportAccounts(Long auftragId, Long sessionId) throws AKReportException {
        try {
            ReportAccountCommand cmd = new ReportAccountCommand();
            cmd.prepare(ReportAccountCommand.AUFTRAG_ID, auftragId);
            cmd.prepare(ReportAccountCommand.SESSION_ID, sessionId);
            Object result = cmd.execute();
            if (result instanceof JasperPrint) {
                return (JasperPrint) result;
            }

            throw new AKReportException("Report konnte aus unbekanntem Grund nicht erstellt werden!");
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Der Report mit den Account-Daten konnte nicht erstellt werden!", e);
        }
    }

    @Override
    public AKWarnings revokeTermination(RevokeTerminationModel revokeTermination) throws StoreException {
        try {
            if (revokeTermination == null) {
                throw new StoreException(StoreException.ERROR_REVOKE_TERMINATION);
            }

            IServiceCommand cmd = serviceLocator.getCmdBean(RevokeTerminationCommand.class);
            cmd.prepare(AbstractRevokeCommand.KEY_REVOKE_MODEL, revokeTermination);

            Object result = cmd.execute();
            return result instanceof AKWarnings ? (AKWarnings) result : null;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_REVOKE_TERMINATION, e);
        }
    }

    @Override
    public AKWarnings revokeCreation(RevokeCreationModel revokeCreationModel) throws StoreException {
        try {
            if (revokeCreationModel == null) {
                throw new StoreException(StoreException.ERROR_REVOKE_CREATION);
            }

            IServiceCommand cmd = serviceLocator.getCmdBean(RevokeCreationCommand.class);
            cmd.prepare(AbstractRevokeCommand.KEY_REVOKE_MODEL, revokeCreationModel);

            Object result = cmd.execute();
            return result instanceof AKWarnings ? (AKWarnings) result : null;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_REVOKE_CREATION, e);
        }
    }

    @Override
    public Auftrag createTechAuftrag(Long kundeNo, Long auftragNo, Long sessionId) throws StoreException {
        if (kundeNo == null || auftragNo == null) {
            throw new StoreException("Unvollständige Parameter übergeben. "
                    + "Technischer Auftrag kann nicht angelegt werden.");
        }
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateTechAuftragCommand.class);
            cmd.prepare(CreateTechAuftragCommand.SESSION_ID, sessionId);
            cmd.prepare(CreateTechAuftragCommand.KUNDE_NO, kundeNo);
            cmd.prepare(CreateTechAuftragCommand.AUFTRAG_NO, auftragNo);
            Object result = cmd.execute();
            return result instanceof Auftrag ? (Auftrag) result : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public boolean has4DrahtOption(Long auftragId) throws FindException {
        AuftragDaten ad = findAuftragDatenByAuftragId(auftragId);
        return has4DrahtOptionOrderNo(ad.getAuftragNoOrig());
    }

    @Override
    public boolean has4DrahtOptionOrderNo(Long orderNo) throws FindException {
        if (orderNo == null) {
            return false;
        }
        try {
            List<AuftragDaten> auftragDatenList = findAuftragDaten4OrderNoOrigTx(orderNo);
            if (CollectionTools.isNotEmpty(auftragDatenList)) {
                for (AuftragDaten auftragDaten : auftragDatenList) {
                    Long auftragStatus = auftragDaten.getAuftragStatusId();
                    if (NumberTools.isLess(auftragStatus, AuftragStatus.AUFTRAG_GEKUENDIGT)
                            && NumberTools.notEqual(auftragStatus, AuftragStatus.STORNO)
                            && NumberTools.notEqual(auftragStatus, AuftragStatus.ABSAGE)
                            && BooleanTools
                            .nullToFalse(getProduktService().isVierDrahtProdukt(auftragDaten.getProdId()))) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public Long findMaxAuftragId() {
        return auftragDAO.getMaxAuftragId();
    }

    /* Methods for Leitungsnummer */

    @Override
    public Leitungsnummer findLeitungsnummerById(Long leitungsnummerId) {
        return getDefaultDeleteDAO().findById(leitungsnummerId, Leitungsnummer.class);
    }

    @Override
    public List<Leitungsnummer> findLeitungsnummerByAuftrag(CCAuftragModel auftrag) {
        Leitungsnummer example = new Leitungsnummer();
        example.setAuftragId(auftrag.getAuftragId());

        return getDefaultDeleteDAO().queryByExample(example, Leitungsnummer.class);
    }

    @Override
    public void saveLeitungsnummer(Leitungsnummer toStore) {
        getDefaultDeleteDAO().store(toStore);
    }

    @Override
    public void deleteLeitungsnummer(Leitungsnummer toDelete) {
        getDefaultDeleteDAO().delete(toDelete);
    }

    @Override
    public void changeCustomerIdOnAuftrag(Long auftragNoOrig, Long sourceCustomerNo, Long targetCustomerNo)
            throws StoreException {
        try {
            Kunde kunde = kundenService.findKunde(targetCustomerNo);
            if (kunde == null) {
                throw new StoreException(String.format("Kein Kunde für Kundennummer %d gefunden!", targetCustomerNo));
            }
            List<AuftragDaten> auftraege = auftragDatenDAO.findByOrderNoOrig(auftragNoOrig, false);
            for (AuftragDaten auftragDaten : auftraege) {
                Auftrag auftrag = findAuftragById(auftragDaten.getAuftragId());
                if (!auftrag.getKundeNo().equals(sourceCustomerNo)) {
                    throw new StoreException(String.format("Auftrag %d(%d) gehoert nicht zu Kunde %d!",
                            auftragNoOrig, auftrag.getAuftragId(), sourceCustomerNo));
                }
                auftrag.setKundeNo(targetCustomerNo);
                saveAuftrag(auftrag);
            }
        }
        catch (Exception ex) {
            LOGGER.error("could not update customerId", ex);
            if (ex instanceof StoreException) {
                throw (StoreException) ex;
            }
            throw new StoreException(StoreException._UNEXPECTED_ERROR, ex);
        }
    }

    Equipment getAdditionalPortAndIfNotAvailableDefault(Endstelle endstelle) throws FindException {
        Equipment additionalPort = getRangierungsService().findEquipment4Endstelle(endstelle, true, false);
        if (additionalPort != null && additionalPort.getHwSwitch() != null) {
            return additionalPort;
        }
        Equipment defaultPort = getRangierungsService().findEquipment4Endstelle(endstelle, false, false);
        if (defaultPort == null) {
            throw new FindException(String.format("EQ_IN auf Endstelle B mit Id '%s' nicht gefunden", endstelle.getId()));
        }
        return defaultPort;
    }

    @Override
    public void updateSwitchForAuftrag(Long auftragId, HWSwitch switchTo) {
        if (auftragId == null || switchTo == null) {
            throw new IllegalArgumentException("Die AuftragId und der Zielswitch müssen gesetzt sein.");
        }
        AuftragTechnik auftragTechnik = auftragTechnikDAO.findByAuftragId(auftragId);
        auftragTechnik.setHwSwitch(switchTo);
        auftragTechnikDAO.store(auftragTechnik);
    }

    @Override
    public HWSwitch getSwitchKennung4Auftrag(Long auftragId) {
        if (auftragId == null) {
            return null;
        }
        try {
            if (ccLeistungsService.hasVoipLeistungFromNowOn(auftragId)) {
                final AuftragTechnik auftragTechnik = findAuftragTechnikByAuftragId(auftragId);
                return (auftragTechnik != null) ? auftragTechnik.getHwSwitch() : null;
            }
            else {
                return getSwitchFromEquipment(auftragId);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public Either<CalculatedSwitch4VoipAuftrag, Boolean> calculateSwitch4VoipAuftrag(Long auftragId)
            throws FindException {
        if (!ccLeistungsService.hasVoipLeistungFromNowOn(auftragId)) {
            return Either.right(Boolean.TRUE);
        }

        CalculatedSwitch4VoipAuftrag result = new CalculatedSwitch4VoipAuftrag();
        result.isOverride = true;
        result.calculatedHwSwitch = getSwitchFromEndgeraete(auftragId);
        if (result.calculatedHwSwitch == null) {
            result.calculatedHwSwitch = getSwitchFromProdukt(auftragId);
            result.isOverride = false;
        }
        return Either.left(result);
    }

    private HWSwitch getSwitchFromEndgeraete(Long auftragId) throws FindException {
        final List<List<HWSwitch>> allSwitches = new ArrayList<>();
        for (final EG2Auftrag eg2auftrag : endgeraeteService.findEGs4Auftrag(auftragId)) {
            final EGConfig egConfig = endgeraeteService.findEGConfig(eg2auftrag.getId());
            final List<HWSwitch> switches = (egConfig != null && egConfig.getEgType() != null)
                    ? egConfig.getEgType().getOrderedCertifiedSwitches()
                    : Collections.emptyList();
            allSwitches.add(switches);
        }
        if (allSwitches.isEmpty()) {
            return null;
        }
        else {
            // compute the intersection of allSwitches
            final List<HWSwitch> result = allSwitches.remove(0);
            allSwitches.forEach(result::retainAll);
            return result.stream().findFirst().orElse(null);
        }
    }

    HWSwitch getSwitchFromProdukt(Long auftragId) throws FindException {
        AuftragDaten auftragDaten = findAuftragDatenByAuftragIdTx(auftragId);
        if (auftragDaten == null) {
            throw new FindException(String.format(
                    "Ermittlung der AuftragDaten für den techn. Auftrag %d fehlgeschlagen!", auftragId));
        }
        Produkt produkt = getProduktService().findProdukt(auftragDaten.getProdId());
        if (produkt == null) {
            throw new FindException(String.format("Ermittlung des Produktes für den techn. Auftrag %d fehlgeschlagen!",
                    auftragId));
        }
        return produkt.getHwSwitch();
    }

    private HWSwitch getSwitchFromEquipment(Long auftragId) throws FindException {
        Endstelle esB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if (esB == null) {
            throw new FindException(String.format(
                    "Die Endstelle B für den Auftrag '%s' konnte nicht gefunden werden!", auftragId));
        }
        Equipment port = getAdditionalPortAndIfNotAvailableDefault(esB);
        return port.getHwSwitch();
    }

    @Override
    public Auftrag findActiveOrderByLineId(String lineId, LocalDate when) throws FindException {
        try {
            List<Auftrag> activeOrders = getAuftragDAO().findActiveOrdersByLineId(lineId, when);
            if (CollectionTools.isNotEmpty(activeOrders)) {
                if (activeOrders.size() > 1) {
                    throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, activeOrders.size() });
                }
                return activeOrders.get(0);
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
    public AuftragDaten findAuftragDatenByLineIdAndStatus(String lineId, Long... auftragStatus) throws FindException {
        try {
            List<AuftragDaten> orders = getAuftragDAO().findAuftragDatenByLineIdAndStatus(lineId, auftragStatus);
            if (CollectionTools.isNotEmpty(orders)) {

                if (orders.size() > 1) {
                    throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, orders.size() });
                }
                return orders.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public Auftrag findOrderByLineIdAndAuftragStatus(String lineId, Long auftragStatus) throws FindException {
        try {
            List<Auftrag> activeOrders = getAuftragDAO().findActiveOrdersByLineIdAndAuftragStatus(lineId, auftragStatus);
            if (CollectionTools.isNotEmpty(activeOrders)) {
                if (activeOrders.size() > 1) {
                    throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, activeOrders.size() });
                }
                return activeOrders.get(0);
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
    public AuftragAktion getActiveAktion(Long auftragId, AktionType type) throws FindException {
        try {
            return auftragAktionDAO.getActiveAktion(auftragId, type);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveAuftragAktion(AuftragAktion aktion) throws StoreException {
        // Precondition checks - diese muessen innerhalb dieser transaktionalen Methode passieren.
        switch (aktion.getAction()) {
            case MODIFY_PORT:
                AuftragAktion pendingModify = auftragAktionDAO.getActiveAktion(aktion.getAuftragId(),
                        aktion.getAction());
                if (pendingModify != null) {
                    // modify auf modify zu heute? -> vorheriges modify canceln
                    if (pendingModify.getDesiredExecutionDate().isEqual(LocalDate.now())) {
                        pendingModify.setCancelled(Boolean.TRUE);
                        auftragAktionDAO.store(pendingModify);
                    }
                    // ansonsten darf es nur einen offenen modify-Port request geben
                    else {
                        throw new ModifyPortPendingException();
                    }
                }
                break;
            default:
                break;
        }
        try {
            auftragAktionDAO.store(aktion);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public void saveAuftragWholesale(AuftragWholesale auftragWholesale) throws StoreException {
        try {
            AuftragWholesaleDAO dao = getAuftragWholesaleDAO();
            dao.store(auftragWholesale);
            dao.flushSession();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    public AuftragWholesale findAuftragWholesaleByAuftragId(Long auftragId) throws FindException {
        return auftragWholesaleDAO.findByAuftragId(auftragId);
    }


    @Override
    public AuftragAktion modifyActiveAktion(Long auftragId, AuftragAktion.AktionType type,
            LocalDate modifiedExecutionDate) throws StoreException {
        try {
            Preconditions.checkArgument(auftragId != null, "Auftrag ID nicht angegeben!");
            Preconditions.checkArgument(type != null, "Auftrag Aktion Typ nicht angegeben!");
            Preconditions.checkArgument(modifiedExecutionDate != null, "Zu modifizierendes Datum nicht angegeben!");

            AuftragAktion auftragAktion = getActiveAktion(auftragId, type);
            if (auftragAktion != null) {
                auftragAktion.setDesiredExecutionDate(modifiedExecutionDate);
                auftragAktionDAO.store(auftragAktion);
            }
            return auftragAktion;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void moveModifyPortAktion(AuftragAktion auftragAktion, Long newOrderId) {
        auftragAktion.setPreviousAuftragId(auftragAktion.getAuftragId());
        auftragAktion.setAuftragId(newOrderId);
        auftragAktionDAO.store(auftragAktion);
    }

    @Override
    public void cancelAuftragAktion(@Nonnull AuftragAktion toCancel) throws StoreException {
        try {
            toCancel.setCancelled(Boolean.TRUE);
            auftragAktionDAO.store(toCancel);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAktiveAuftragDatenByBaugruppe(@Nonnull Long baugruppeId) {
        return auftragDatenDAO.findAktiveAuftragDatenByBaugruppe(baugruppeId);
    }

    @Override
    @Nonnull
    public List<AuftragDaten> findAktiveAuftragDatenByOrtsteilAndProduktGroup(@Nonnull String ortsteil, @Nonnull String produktGrouppe) {
        return auftragDatenDAO.findAktiveAuftragDatenByOrtsteilAndProduktGroup(ortsteil, produktGrouppe);
    }

    @Override
    public Boolean isAutomationPossible(AuftragDaten auftragDaten, Long cbVorgangTyp) {
        if (auftragDaten == null) {
            return false;
        }

        try {
            Produkt produkt = getProduktService().findProdukt(auftragDaten.getProdId());
            AuftragTechnik auftragTechnik = findAuftragTechnikByAuftragId(auftragDaten.getAuftragId());
            BAuftrag bAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());

            boolean automPossible = BooleanTools.nullToFalse(produkt.getAutomationPossible())
                    && auftragTechnik != null
                    && auftragTechnik.getVpnId() == null;

            if (CBVorgang.TYP_KUENDIGUNG.equals(cbVorgangTyp)) {
                return automPossible && isAuftragKuend(bAuftrag);
            }
            else {
                return automPossible && isAuftragNeu(bAuftrag);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    @CheckForNull
    public Pair<String, Integer> findOnkzAsb4Auftrag(long auftragId) {
        try {
            final AuftragDaten auftragDaten = findAuftragDatenByAuftragIdTx(auftragId);
            final Produkt produkt = getProduktService().findProdukt(auftragDaten.getProdId());
            if (!produkt.isOnkzAsbNeededForCps()) {
                return null;
            }

            // Bitte keine Pruefung, ob ONKZ/ASB gesetzt sind.
            // Im Falle eines HVTs, muss es sich bspw. nicht zwangslaeufig um
            // einen DTAG Standort handeln. Somit ist ein gesetzter ASB nicht
            // immer vorauszusetzen.
            final Pair<String, Integer> onkzAsb;
            switch (produkt.getGeoIdSource()) {
                case HVT:
                    onkzAsb = getOnkzAsbFromHvtStandortGruppe(auftragId);
                    break;
                case GEO_ID:
                    onkzAsb = getOnkzAsbFromGeoIdCache(auftragId);
                    break;
                case OHNE:
                    onkzAsb = null;
                    break;
                default:
                    throw new RuntimeException(String.format("Unbekannte GeoId-Quelle: %s",
                            produkt.getGeoIdSource()));
            }
            return onkzAsb;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(String.format(
                    String.format("Error getting Onkz/Asb for order %s: ", auftragId)
                            + e.getMessage(), e
            ));
        }
    }

    private Pair<String, Integer> getOnkzAsbFromGeoIdCache(final long auftragId) throws Exception {
        final Endstelle esB = findEndstelleB4Auftrag(auftragId);
        if (esB.getGeoId() == null) {
            throw new HurricanServiceCommandException(
                    String.format("Der Endstelle B des Auftrags %s ist keine GeoId zugeordnet!", auftragId));
        }
        final GeoId geoId = availabilityService.findGeoId(esB.getGeoId());
        if (geoId == null) {
            throw new HurricanServiceCommandException(
                    String.format("Die GeoId %s befindet sich nicht im Hurrican Cache (Auftrag %s)!",
                            esB.getGeoId(), auftragId)
            );
        }
        return Pair.create(geoId.getOnkz(), Integer.valueOf(geoId.getAsb()));
    }

    private Pair<String, Integer> getOnkzAsbFromHvtStandortGruppe(final long auftragId) throws Exception {
        final Endstelle esB = findEndstelleB4Auftrag(auftragId);
        HVTStandort hvtStd = hvtService.findHVTStandort(esB.getHvtIdStandort());
        if (esB.getHvtIdStandort() == null) {
            throw new RuntimeException(
                    String.format("technischer Standort ist auf Endstelle B für Auftrag %d nicht gesetzt!", auftragId));
        }
        HVTGruppe hvtGruppe = (hvtStd != null) ? hvtService.findHVTGruppeById(hvtStd.getHvtGruppeId()) : null;
        if ((hvtStd == null) || (hvtGruppe == null)) {
            throw new HurricanServiceCommandException(
                    "HVTStandort bzw. Gruppe für Endstelle B nicht gefunden!");
        }
        return Pair.create(hvtGruppe.getOnkz(), hvtStd.getAsb());
    }

    private Endstelle findEndstelleB4Auftrag(long auftragId) throws FindException {
        Endstelle esB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if (esB == null) {
            throw new FindException(
                    "Endstelle B nicht gefunden!");
        }
        return esB;
    }

    public void checkAgsn4Auftrag(final long auftragId) throws FindException {
        Endstelle endstelleB = findEndstelleB4Auftrag(auftragId);
        if (endstelleB.getGeoId() == null) {
            throw new FindException(String.format("Der Endstelle %s ist noch keine GeoId zugeordnet!",
                    endstelleB.getEndstelleTyp()));
        }
        GeoId geoId = availabilityService.findGeoId(endstelleB.getGeoId());
        if (geoId == null) {
            throw new FindException(String.format("GeoId %d konnte nicht ermittelt werden!",
                    endstelleB.getGeoId()));
        }
        if (StringUtils.isBlank(geoId.getAgsn())) {
            throw new FindException(String.format("Dieser Adresse [GeoId=%d] muss in VENTO ein amtlicher " +
                    "Gemeindeschlüssel für Notruf (AGSN) zugeordnet werden.!", endstelleB.getGeoId()));
        }
    }

    @Override
    public HWRack findHwRackByAuftragId(Long auftragId) throws FindException {
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if (endstelleB != null) {
            Equipment equipment = getRangierungsService().findEquipment4Endstelle(endstelleB, false, false);
            if (equipment != null && equipment.isValid()) {

                HWBaugruppe hwBaugruppe = hwService.findBaugruppe(equipment.getHwBaugruppenId());
                if (hwBaugruppe != null) {
                    HWRack rack = hwService.findRackForBaugruppe(hwBaugruppe.getId());
                    if (rack != null && rack.isValid()) {
                        return rack;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Ermittelt ob der Auftrag im Status KUEND ist.
     *
     * @param bAuftrag
     * @return True falls der Auftrag Status KUEN ist, sonst false
     */
    private boolean isAuftragKuend(BAuftrag bAuftrag) {
        return bAuftrag != null && BillingConstants.ATYP_KUEND.equals(bAuftrag.getAtyp());
    }

    /**
     * Ermittelt ob der BAuftrag im Status NEU ist.
     *
     * @param bAuftrag
     * @return True falls der Auftrag Status NEU ist, sonst false
     */
    private boolean isAuftragNeu(BAuftrag bAuftrag) {
        return bAuftrag != null && BillingConstants.HIST_STATUS_NEU.equals(bAuftrag.getHistStatus())
                && Integer.valueOf(0).equals(bAuftrag.getHistCnt());
    }

    // DAOs

    protected CCAuftragViewDAO getAuftragViewDAO() {
        return auftragViewDAO;
    }

    public void setAuftragViewDAO(CCAuftragViewDAO auftragViewDAO) {
        this.auftragViewDAO = auftragViewDAO;
    }

    protected Object getAuftragStatusDAO() {
        return auftragStatusDAO;
    }

    public void setAuftragStatusDAO(Object auftragStatusDAO) {
        this.auftragStatusDAO = auftragStatusDAO;
    }

    protected AuftragDatenDAO getAuftragDatenDAO() {
        return auftragDatenDAO;
    }

    public void setAuftragDatenDAO(AuftragDatenDAO auftragDatenDAO) {
        this.auftragDatenDAO = auftragDatenDAO;
    }


    protected AuftragWholesaleDAO getAuftragWholesaleDAO() {
        return auftragWholesaleDAO;
    }

    public void setAuftragWholesaleDAO(AuftragWholesaleDAO auftragWholesaleDAO) {
        this.auftragWholesaleDAO = auftragWholesaleDAO;
    }

    protected CCAuftragDAO getAuftragDAO() {
        return auftragDAO;
    }

    public void setAuftragDAO(CCAuftragDAO auftragDAO) {
        this.auftragDAO = auftragDAO;
    }

    protected AuftragTechnikDAO getAuftragTechnikDAO() {
        return auftragTechnikDAO;
    }

    public void setAuftragTechnikDAO(AuftragTechnikDAO auftragTechnikDAO) {
        this.auftragTechnikDAO = auftragTechnikDAO;
    }

    public Hibernate4DefaultDeleteDAO getDefaultDeleteDAO() {
        return defaultDeleteDAO;
    }

    public void setDefaultDeleteDAO(Hibernate4DefaultDeleteDAO defaultDeleteDAO) {
        this.defaultDeleteDAO = defaultDeleteDAO;
    }

    public void setAuftragAktionDAO(AuftragAktionDAO auftragAktionDAO) {
        this.auftragAktionDAO = auftragAktionDAO;
    }

    // SERVICES

    public KundenService getKundenService() {
        return kundenService;
    }

    public void setKundenService(KundenService kundenService) {
        this.kundenService = kundenService;
    }

    public BillingAuftragService getBillingAuftragService() {
        return billingAuftragService;
    }

    public void setBillingAuftragService(BillingAuftragService billingAuftragService) {
        this.billingAuftragService = billingAuftragService;
    }

    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    public void setHWService(HWService hwService) {
        this.hwService = hwService;
    }

    // WORKAROUNDS FOR CIRCULAR DEPENDENCIES!

    public ProduktService getProduktService() {
        if (produktService == null) {
            try {
                produktService = getCCService(ProduktService.class);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return produktService;
    }

    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    public RangierungsService getRangierungsService() {
        if (rangierungsService == null) {
            try {
                rangierungsService = getCCService(RangierungsService.class);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return rangierungsService;
    }

    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    @Override
    public AuftragTechnik findAuftragTechnik4VbzId(Long vbzId) {
        if(vbzId == null ) {
            return null;
        }
        return getAuftragTechnikDAO().findAuftragTechnik4VbzId(vbzId);
    }
}
