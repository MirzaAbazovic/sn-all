/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.14
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;
import static de.mnet.wbci.service.impl.WbciAutomationServiceImpl.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.billing.helper.RufnummerPortierungSelectionHelper;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.InvalidRufnummerPortierungException;
import de.mnet.wbci.exception.WbciAutomationValidationException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.OverdueWitaOrderVO;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciWitaOrderDataVO;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.TalAnbieterwechseltypService;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaVorabstimmungService;
import de.mnet.wita.service.WitaWbciServiceFacade;
import de.mnet.wita.service.impl.WitaTalOrderServiceImpl;

/**
 *
 */
@CcTxRequired
public class WbciWitaServiceFacadeImpl implements WbciWitaServiceFacade {

    public static final int OVERDUE_DAYS_FOR_MISSING_WITA_ORDERS = 12;
    private static final String ENDSTELLEN_TYP = Endstelle.ENDSTELLEN_TYP_B;
    private static final String CB_VORGANGSTYP_NOT_SUPPORTED = "CB-Vorgangstyp '%s' is currently not supported for automatic WITA order creation";
    private static final String INCORRECT_DATA_TYPE = "Incorrect data type created expected '%s' but get '%s'";
    private static final String WRONG_COUNT_OF_CREATED_WITA_ORDERS = "There would have been %s WITA orders created, but only 1 was expected";
    private static final String GESCHAEFTSFALL_TYP_NOT_SUPPORTED = "Die Ermittlung des WITA-Geschaeftsfalltyps auf Basis des Carrierbestellung-Typs '%d' wird derzeit nicht unterstützt";
    private static final String WBCI_GF_USER_NOT_SET = "Zur automatisierten Erstellung eines WITA-Vorgangs muss ein Bearbeiter in der WBCI-Vorabstimmung '%s' gesetzt werden";

    @Autowired
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private CarrierElTALService carrierElTALService;
    @Autowired
    private CommonWorkflowService commonWorkflowService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private WitaVorabstimmungService witaVorabstimmungService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciDao wbciDao;
    @Autowired
    private ReferenceService referenceService;
    @Autowired
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    private WitaWbciServiceFacade witaWbciServiceFacade;
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    private TalAnbieterwechseltypService talAnbieterwechseltypService;
    @Autowired
    private AKUserService akUserService;

    private static List<Long> getOpenWitaStatuses() {
        return Arrays.asList(WitaCBVorgang.STATUS_ANSWERED, WitaCBVorgang.STATUS_SUBMITTED, WitaCBVorgang.STATUS_TRANSFERRED);
    }

    @Override
    public List<WitaCBVorgang> findWitaCbVorgaengeByAuftrag(Long auftragId) {
        try {
            final Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            final Carrierbestellung carrierbestellung = carrierService.findLastCB4Endstelle(endstelleB.getId());
            if (carrierbestellung != null) {
                final List<WitaCBVorgang> cbvs =
                        witaTalOrderService.getWitaCBVorgaengeForTVOrStorno(carrierbestellung.getId(), auftragId);
                return cbvs;
            } else {
                return Collections.emptyList();
            }
        }
        catch (FindException e) {
            throw new WbciServiceException(String.format("Waehrend der Suche nach CB-Vorgänge für Auftrag %s " +
                    " ist ein unerwarteter Fehler aufgetreten.", auftragId), e);
        }
    }

    @Override
    public List<WitaCBVorgang> findWitaCbVorgaenge(String vorabstimmungsId) {
        try {
            return witaTalOrderService.findCBVorgaengeByVorabstimmungsId(vorabstimmungsId);
        }
        catch (FindException e) {
            throw new WbciServiceException(String.format("Waehrend der Suche nach CB-Vorgänge für die " +
                    "Vorabstimmung mit der Id '%s' ist ein unerwarteter Fehler aufgetreten.", vorabstimmungsId), e);
        }
    }

    @Override
    public WitaCBVorgang findSingleActiveWitaCbVorgang(String vorabstimmungsId) {
        List<WitaCBVorgang> cbvs = findWitaCbVorgaenge(vorabstimmungsId);
        List<WitaCBVorgang> activeCbvs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cbvs)) {
            for (WitaCBVorgang cbv : cbvs) {
                if (commonWorkflowService.isProcessInstanceAlive(cbv.getBusinessKey())) {
                    activeCbvs.add(cbv);
                }
            }
        }

        return (activeCbvs.size() == 1) ? activeCbvs.get(0) : null;
    }


    @Override
    public WitaCBVorgang findSingleActiveWitaCbVorgang(Long auftragId, String vorabstimmungsId) {
        try {
            List<WitaCBVorgang> activeCbvs = new ArrayList<>();

            Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            Carrierbestellung carrierbestellung = carrierService.findLastCB4Endstelle(endstelleB.getId());
            if (carrierbestellung != null) {
                List<WitaCBVorgang> cbvs =
                        witaTalOrderService.getWitaCBVorgaengeForTVOrStorno(carrierbestellung.getId(), auftragId);
                
                if (CollectionUtils.isNotEmpty(cbvs)) {
                    for (WitaCBVorgang cbv : cbvs) {
                        if (vorabstimmungsId.equals(cbv.getVorabstimmungsId())
                                && commonWorkflowService.isProcessInstanceAlive(cbv.getBusinessKey())) {
                            activeCbvs.add(cbv);
                        }
                    }
                }
            }

            return (activeCbvs.size() == 1) ? activeCbvs.get(0) : null;
        }
        catch (FindException e) {
            throw new WbciServiceException(String.format("Waehrend der Suche nach CB-Vorgänge für Auftrag %s " +
                    "Vorabstimmung '%s' ist ein unerwarteter Fehler aufgetreten.", auftragId, vorabstimmungsId), e);
        }
    }


    @Override
    public
    @Nullable
    VorabstimmungAbgebend updateOrCreateWitaVorabstimmungAbgebend(final WbciRequest wbciRequest) {
        WbciGeschaeftsfall wbciGf = wbciRequest.getWbciGeschaeftsfall();
        if (CarrierRole.ABGEBEND.equals(CarrierRole.lookupMNetCarrierRole(wbciGf))
                && wbciGf.getAuftragId() != null) {
            StringBuilder bemerkung = new StringBuilder(255);
            boolean isPositive = false;

            switch (wbciRequest.getRequestStatus()) {
                case RUEM_VA_VERSENDET:
                    bemerkung.append("positive WBCI-Vorabstimmung - ").append(WbciRequestStatus.RUEM_VA_VERSENDET.getDescription());
                    isPositive = true;
                    break;
                case ABBM_VERSENDET:
                    bemerkung.append("negative WBCI-Vorabstimmung - ").append(WbciRequestStatus.ABBM_VERSENDET.getDescription());
                    break;
                case TV_ERLM_VERSENDET:
                    bemerkung.append("positive WBCI-Terminverschiebung - ").append(WbciRequestStatus.TV_ERLM_VERSENDET.getDescription());
                    isPositive = true;
                    break;
                case STORNO_ERLM_EMPFANGEN:
                    bemerkung.append("stornierte WBCI-Vorabstimmung - ").append(WbciRequestStatus.STORNO_ERLM_EMPFANGEN.getDescription());
                    break;
                case STORNO_ERLM_VERSENDET:
                    bemerkung.append("stornierte WBCI-Vorabstimmung - ").append(WbciRequestStatus.STORNO_ERLM_VERSENDET.getDescription());
                    break;
                default:
                    return null;
            }

            //check and build bemerkung
            bemerkung.append(" (").append(wbciGf.getVorabstimmungsId()).append(")");
            bemerkung.trimToSize();

            //build vorabstimmungAbgebend object
            VorabstimmungAbgebend vaAbgebend = witaVorabstimmungService.findVorabstimmungAbgebend(ENDSTELLEN_TYP, wbciGf.getAuftragId());
            if (vaAbgebend == null) {
                vaAbgebend = new VorabstimmungAbgebend();
            }

            if (!hasAkmTr(wbciRequest.getVorabstimmungsId()) || vaAbgebend.getCarrier() == null) {
                vaAbgebend.setCarrier(getCarrier(wbciGf.getEKPPartner()));
            }
            vaAbgebend.setAuftragId(wbciGf.getAuftragId());
            vaAbgebend.setEndstelleTyp(ENDSTELLEN_TYP);

            //for RUEM-VA, TV, Stornos
            if (wbciGf.getWechseltermin() != null) {
                vaAbgebend.setAbgestimmterProdiverwechsel(wbciGf.getWechseltermin());
            }
            //for ABBM
            else {
                vaAbgebend.setAbgestimmterProdiverwechsel(wbciGf.getKundenwunschtermin());
            }
            vaAbgebend.setBemerkung(bemerkung.toString());
            vaAbgebend.setRueckmeldung(isPositive);

            return witaVorabstimmungService.saveVorabstimmungAbgebend(vaAbgebend);
        }
        return null;
    }

    @Override
    public VorabstimmungAbgebend updateWitaVorabstimmungAbgebend(final UebernahmeRessourceMeldung uebernahmeRessourceMeldung) {
        WbciGeschaeftsfall wbciGf = uebernahmeRessourceMeldung.getWbciGeschaeftsfall();
        if (uebernahmeRessourceMeldung.isUebernahme()
                && CarrierRole.ABGEBEND.equals(CarrierRole.lookupMNetCarrierRole(wbciGf))
                && wbciGf.getAuftragId() != null
                && GeschaeftsfallTyp.VA_KUE_MRN.equals(wbciGf.getTyp())) {
            VorabstimmungAbgebend vaAbgebend = witaVorabstimmungService.findVorabstimmungAbgebend(ENDSTELLEN_TYP, wbciGf.getAuftragId());
            if (vaAbgebend != null) {
                vaAbgebend.setCarrier(getCarrier(uebernahmeRessourceMeldung.getPortierungskennungPKIauf()));
                return witaVorabstimmungService.saveVorabstimmungAbgebend(vaAbgebend);
            }
        }
        return null;
    }

    protected Carrier getCarrier(CarrierCode partnerEkp) {
        try {
            return carrierService.findCarrierByCarrierCode(partnerEkp);
        }
        catch (FindException e) {
            throw new WbciServiceException(e);
        }
    }

    protected Carrier getCarrier(String portierungskennung) {
        try {
            return carrierService.findCarrierByPortierungskennung(portierungskennung);
        }
        catch (FindException e) {
            throw new WbciServiceException(e);
        }
    }

    private boolean hasAkmTr(String vaId) {
        return wbciCommonService.findLastForVaId(vaId, UebernahmeRessourceMeldung.class) != null;
    }

    @Override
    public List<OverdueWitaOrderVO> getOverduePreagreementsWithMissingWitaOrder() {
        List<UebernahmeRessourceMeldung> akmTrs = wbciDao.findOverdueAkmTrsNearToWechseltermin(OVERDUE_DAYS_FOR_MISSING_WITA_ORDERS, CarrierCode.MNET, Technologie.getWitaOrderRelevantTechnologies());
        List<OverdueWitaOrderVO> resultList = new ArrayList<>();
        for (UebernahmeRessourceMeldung akmTr : akmTrs) {
            List<WitaCBVorgang> witaCbVorgaenge = findWitaCbVorgaenge(akmTr.getVorabstimmungsId());
            OverdueWitaOrderVO checkWitaVorgangResult = checkWitaVorgangIsCreated(witaCbVorgaenge, akmTr);
            if (checkWitaVorgangResult != null) {
                resultList.add(checkWitaVorgangResult);
            }
        }
        return resultList;
    }

    protected OverdueWitaOrderVO checkWitaVorgangIsCreated(List<WitaCBVorgang> witaCbVorgaenge, UebernahmeRessourceMeldung akmTr) {
        OverdueWitaOrderVO overdueWitaOrderVO = new OverdueWitaOrderVO();
        if (!CollectionUtils.isEmpty(witaCbVorgaenge)) {
            for (WitaCBVorgang witaCBVorgang : witaCbVorgaenge) {
                if (getOpenWitaStatuses().contains(witaCBVorgang.getStatus())
                        || isWitaOrderPositivCompleted(witaCBVorgang)) {
                    return null;
                }
                else {
                    overdueWitaOrderVO.addAssignedWitaOrder(new Pair<>(
                            witaCBVorgang.getBezeichnungMnet(),
                            getStatusName(witaCBVorgang.getStatus(), witaCBVorgang.getReturnOk())));
                }
            }
        }
        overdueWitaOrderVO.setEkpAuf(akmTr.getWbciGeschaeftsfall().getAufnehmenderEKP());
        overdueWitaOrderVO.setEkpAbg(akmTr.getWbciGeschaeftsfall().getAbgebenderEKP());
        overdueWitaOrderVO.setAuftragId(akmTr.getWbciGeschaeftsfall().getAuftragId());
        overdueWitaOrderVO.setAuftragNoOrig(akmTr.getWbciGeschaeftsfall().getBillingOrderNoOrig());
        overdueWitaOrderVO.setVaid(akmTr.getVorabstimmungsId());
        overdueWitaOrderVO.setmNetTechnologie(akmTr.getWbciGeschaeftsfall().getMnetTechnologie());
        overdueWitaOrderVO.setWechseltermin(Date.from(akmTr.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return overdueWitaOrderVO;
    }

    private boolean isWitaOrderPositivCompleted(WitaCBVorgang witaCBVorgang) {
        return WitaCBVorgang.STATUS_CLOSED.equals(witaCBVorgang.getStatus()) && witaCBVorgang.getReturnOk();
    }

    /**
     * Determines the status name of the {@link WitaCBVorgang#status}.
     *
     * @param status                    long value of status
     * @param witaOrderPositivCompleted
     * @return String representation of {@link WitaCBVorgang#status}.
     */
    protected String getStatusName(Long status, Boolean witaOrderPositivCompleted) {
        try {
            if (status != null) {
                Reference reference = referenceService.findReference(status);
                if (reference != null) {
                    if (witaOrderPositivCompleted == null) {
                        return reference.getStrValue();
                    }
                    String antwort = witaOrderPositivCompleted ? "positive" : "negative";
                    return reference.getStrValue() + ", " + antwort + " Rückmeldung";
                }
            }
            return "UNKNOWN";
        }
        catch (FindException e) {
            throw new WbciServiceException(String.format("Waehrend der Suche nach CB-Vorgangs-Status '%s' " +
                    "ist ein unerwarteter Fehler aufgetreten.", status));
        }
    }

    @CcTxRequiresNew
    @Override
    public WitaCBVorgang createWitaVorgang(AkmTrAutomationTask akmTrAutomationTask, String vorabstimmungsId, AKUser user) {
        UebernahmeRessourceMeldung akmTr = wbciCommonService.findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class);
        WbciWitaOrderDataVO vo = generateWitaDataForPreAggreement(vorabstimmungsId);
        WitaCBVorgang witaVorgang = createWitaVorgang(akmTrAutomationTask, vo);
        createAutomationTaskEntry(akmTr.getWbciGeschaeftsfall(), akmTr, akmTrAutomationTask.getTaskName(),
                COMPLETED, user,
                String.format("WITA-Vorgang mit der externen Auftragsnummer '%s' automatisch erzeugt",
                        witaVorgang.getCarrierRefNr()));
        return witaVorgang;
    }


    @CcTxRequiresNew
    @Override
    public List<WitaCBVorgang> createWitaCancellations(UebernahmeRessourceMeldung akmTr, SortedSet<String> witaVtrNrs, AKUser user) {
        List<WitaCBVorgang> createdWitaVorgaenge = new ArrayList<>();
        StringBuilder witaCarrierRefNrs = new StringBuilder();

        if (CollectionUtils.isNotEmpty(witaVtrNrs)) {
            WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(akmTr.getVorabstimmungsId());

            for (String witaVtrNr : witaVtrNrs) {
                Long hurricanOrderId = wbciCommonService.getHurricanOrderIdForWitaVtrNrAndCurrentVA(witaVtrNr,
                        wbciGeschaeftsfall.getBillingOrderNoOrig(),
                        wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs());
                WbciWitaOrderDataVO vo = generateWitaDataForPreAggreement(wbciGeschaeftsfall, hurricanOrderId);
                WitaCBVorgang witaVorgang = createWitaVorgang(AkmTrAutomationTask.KUENDIGUNG, vo);
                createdWitaVorgaenge.add(witaVorgang);

                if (witaCarrierRefNrs.length() > 0) {
                    witaCarrierRefNrs.append(", ");
                }
                witaCarrierRefNrs.append(witaVorgang.getCarrierRefNr());
            }

            createAutomationTaskEntry(wbciGeschaeftsfall, akmTr,
                    AkmTrAutomationTask.KUENDIGUNG.getTaskName(), COMPLETED, user,
                    String.format("WITA-Vorgang mit der externen Auftragsnummer '%s' automatisch erzeugt",
                            witaCarrierRefNrs));
        }

        return createdWitaVorgaenge;
    }


    @Override
    public WbciWitaOrderDataVO generateWitaDataForPreAggreement(String vorabstimmungsId) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        return generateWitaDataForPreAggreement(wbciGeschaeftsfall, wbciGeschaeftsfall.getAuftragId());
    }


    WbciWitaOrderDataVO generateWitaDataForPreAggreement(WbciGeschaeftsfall wbciGf, Long auftragId) {
        try {
            Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, ENDSTELLEN_TYP);
            Carrierbestellung carrierbestellung = carrierService.findLastCB4Endstelle(endstelle.getId());
            Carrier carrier = carrierService.findCarrier4HVT(endstelle.getHvtIdStandort());
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(auftragId);
            return new WbciWitaOrderDataVO(wbciGf, auftragDaten, carrierbestellung, carrier, endstelle);
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format(
                    "Unexpected error during the generation of the wita order data for VA-ID '%s'",
                    wbciGf.getVorabstimmungsId()), e);
        }
    }

    protected WitaCBVorgang createWitaVorgang(AkmTrAutomationTask akmTrAutomationTask, WbciWitaOrderDataVO vo) {
        if (!Arrays.asList(CBVorgang.TYP_ANBIETERWECHSEL, CBVorgang.TYP_NEU, CBVorgang.TYP_KUENDIGUNG)
                .contains(akmTrAutomationTask.getCbVorgangTyp())) {
            throw new WbciServiceException(
                    String.format(CB_VORGANGSTYP_NOT_SUPPORTED, akmTrAutomationTask.getCbVorgangTyp()));
        }

        try {
            WbciGeschaeftsfall wbciGf = vo.getWbciGeschaeftsfall();
            if (wbciGf.getWechseltermin() == null) {
                throw new WbciServiceException(
                        String.format("WBCI Geschaeftsfall %s besitzt keinen Wechseltermin!", wbciGf.getVorabstimmungsId()));
            }

            createNewCarrierbestellungIfNotExist(wbciGf, vo);

            if (!akmTrAutomationTask.equals(AkmTrAutomationTask.KUENDIGUNG)) {
                checkIfTalOrderExists(vo.getCarrierbestellung());
            }

            String montagehinweis = determineMontagehinweis(vo, akmTrAutomationTask.getCbVorgangTyp());
            Date vorgabeMnet = Date.from(wbciGf.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant());

            boolean witaAutomation = witaTalOrderService.checkAutoClosingAllowed(
                    vo.getAuftragDaten(), vo.getCarrierId(), akmTrAutomationTask.getCbVorgangTyp());

            AKUser user = findUser(wbciGf);
            CbVorgangData cbvData = new CbVorgangData()
                    .withCbId(vo.getCbId())
                    .addAuftragId(vo.getAuftragDaten().getAuftragId(), null)
                    .withCarrierId(vo.getCarrierId())
                    .withVorgabe(vorgabeMnet)
                    .withCbVorgangTyp(akmTrAutomationTask.getCbVorgangTyp())
                    .withMontagehinweis(montagehinweis)
                    .withAnbieterwechselTkg46(true)
                    .withAutomation(witaAutomation)
                    .withUser(user)
                    .withVorabstimmungsId(wbciGf.getVorabstimmungsId());

            if (!akmTrAutomationTask.equals(AkmTrAutomationTask.KUENDIGUNG) &&
                    !wbciGf.getTyp().equals(GeschaeftsfallTyp.VA_KUE_ORN)) {
                cbvData.withRufnummerIds(getRufnummerIds(wbciGf.getVorabstimmungsId()));
            }

            try {
                List<CBVorgang> cbVorgangList = witaTalOrderService.createCBVorgang(cbvData);
                return validateResultList(cbVorgangList);
            }
            catch (StoreException se) {
                throw new WbciAutomationValidationException(se.getMessage());
            }
        }
        catch (WbciValidationException | InvalidRufnummerPortierungException e) {
            //wrap validation exceptions in automation validation exception for better appearance in automation log
            throw new WbciAutomationValidationException(e);
        }
        catch (WbciServiceException e) {
            //do not handel wbci service exceptions
            throw e;
        }
        catch (Exception e) {
            //handel no expected exceptions
            throw new WbciServiceException("Unexpected error during the automatic creation of the WITA order of typ " + akmTrAutomationTask.name(), e);
        }
    }

    protected AKUser findUser(WbciGeschaeftsfall wbciGf) throws AKAuthenticationException {
        if (wbciGf.getUserId() == null) {
            throw new WbciValidationException(String.format(WBCI_GF_USER_NOT_SET, wbciGf.getVorabstimmungsId()));
        }
        return akUserService.findById(wbciGf.getUserId());
    }

    void createNewCarrierbestellungIfNotExist(WbciGeschaeftsfall wbciGf, WbciWitaOrderDataVO vo) throws StoreException, ValidationException {
        if (vo.getCarrierbestellung() == null) {
            Carrierbestellung newCb = new Carrierbestellung();
            newCb.setCarrier(Carrier.ID_DTAG);
            newCb.setBestelltAm(new Date());
            newCb.setVorgabedatum(Date.from(wbciGf.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            carrierService.saveCB(newCb, vo.getEndstelle());

            vo.setCarrierbestellung(newCb);
        }
    }
    
    
    void checkIfTalOrderExists(Carrierbestellung carrierbestellung) throws WbciValidationException, FindException {
        if (carrierbestellung == null) {
            return;
        }
        
        if (StringUtils.isNotBlank(carrierbestellung.getLbz()) || StringUtils.isNotBlank(carrierbestellung.getVtrNr())) {
            throw new WbciValidationException(
                    "WITA Bestellung nicht möglich, da auf der Carrierbestellung schon Daten zurück gemeldet sind!");
        }

        List<CBVorgang> oldCBVs = carrierElTALService.findCBVorgaenge4CB(carrierbestellung.getId());
        if (CollectionTools.isNotEmpty(oldCBVs)) {
            for (CBVorgang oldCBV : oldCBVs) {
                if (NumberTools.isLess(oldCBV.getStatus(), CBVorgang.STATUS_CLOSED)) {
                    throw new WbciValidationException(
                            "Zur Carrierbestellung gibt es noch einen offenen elektronischen Vorgang.");
                }
            }
        }
    }

    protected String determineMontagehinweis(WbciWitaOrderDataVO vo, Long cbVorgangTyp) {
        de.mnet.wita.message.GeschaeftsfallTyp gfTyp = determineWitaGeschaeftsfall(vo, cbVorgangTyp);
        WitaSendLimit witaSendLimit = witaConfigService.findWitaSendLimit(gfTyp, vo.getEndstelle().getHvtIdStandort());
        if (witaSendLimit != null && witaSendLimit.getMontageHinweis() != null) {
            return WitaCBVorgang.ANBIETERWECHSEL_46TKG + witaSendLimit.getMontageHinweis();
        }
        return WitaCBVorgang.ANBIETERWECHSEL_46TKG;
    }

    @Override
    public de.mnet.wita.message.GeschaeftsfallTyp determineWitaGeschaeftsfall(WbciWitaOrderDataVO vo, Long cbVorgangTyp) {
        if (WitaTalOrderServiceImpl.cbTyp2Geschaeftsfall.containsKey(cbVorgangTyp)) {
            ImmutableCollection<de.mnet.wita.message.GeschaeftsfallTyp> possibleGfTypes = WitaTalOrderServiceImpl.cbTyp2Geschaeftsfall.get(cbVorgangTyp);
            if (possibleGfTypes.size() == 1) {
                return Iterables.getOnlyElement(possibleGfTypes);
            }
            if (CBVorgang.TYP_ANBIETERWECHSEL.equals(cbVorgangTyp)) {
                Equipment equipment = talAnbieterwechseltypService.getEquipment(vo.getCarrierbestellung(), vo.getAuftragDaten().getAuftragId());
                return talAnbieterwechseltypService.determineAnbieterwechseltyp(vo.getVorabstimmungsId(), equipment);
            }
        }
        throw new WbciValidationException(String.format(GESCHAEFTSFALL_TYP_NOT_SUPPORTED, cbVorgangTyp));
    }

    /**
     * Validates that only one {@link WitaCBVorgang} is created. Else throw {@link WbciServiceException} with the
     * message {@link #INCORRECT_DATA_TYPE} or {@link #WRONG_COUNT_OF_CREATED_WITA_ORDERS}.
     */
    protected WitaCBVorgang validateResultList(List<? extends CBVorgang> cbVorgangList) {
        if (cbVorgangList != null && cbVorgangList.size() == 1) {
            CBVorgang cbVorgang = cbVorgangList.iterator().next();
            if (cbVorgang instanceof WitaCBVorgang) {
                return (WitaCBVorgang) cbVorgang;
            }
            throw new WbciServiceException(String.format(INCORRECT_DATA_TYPE, WitaCBVorgang.class.getSimpleName(), cbVorgang.getClass().getSimpleName()));
        }
        throw new WbciServiceException(String.format(WRONG_COUNT_OF_CREATED_WITA_ORDERS,
                cbVorgangList == null ? "0" : cbVorgangList.size()
        ));
    }

    /**
     * Determines all accepted {@link Rufnummer#dnNoOrig}s from the latest {@link RueckmeldungVorabstimmung}.
     *
     * @param vorabstimmungsId {@link WbciGeschaeftsfall#vorabstimmungsId}
     * @return Set of {@link Rufnummer#dnNoOrig}s
     */
    protected Set<Long> getRufnummerIds(String vorabstimmungsId) {
        RueckmeldungVorabstimmung ruemVA = wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
        Collection<RufnummerPortierungSelection> rufnummerList = witaTalOrderService.getRufnummerPortierungList(
                ruemVA.getWbciGeschaeftsfall().getBillingOrderNoOrig(), Date.from(ruemVA.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        rufnummerList = witaWbciServiceFacade.updateRufnummernSelectionForVaId(rufnummerList, vorabstimmungsId);
        return RufnummerPortierungSelectionHelper.getRufnummerIds(rufnummerList);
    }

    @Override
    @CcTxRequiresNew
    public WitaCBVorgang doWitaTerminverschiebung(String vorabstimmungsID, Long cbVorgangId, AKUser user, TamUserTask.TamBearbeitungsStatus tamBearbeitungsStatus) {
        try {
            ErledigtmeldungTerminverschiebung erlmTV = wbciCommonService.findLastForVaId(vorabstimmungsID, ErledigtmeldungTerminverschiebung.class);
            LocalDate tvTermin = erlmTV.getWechseltermin();
            WitaCBVorgang tv = witaTalOrderService.doTerminverschiebung(cbVorgangId, tvTermin, user, true, null, tamBearbeitungsStatus);

            createAutomationTaskEntry(erlmTV.getWbciGeschaeftsfall(), erlmTV,
                    AutomationTask.TaskName.WITA_SEND_TV, COMPLETED, user,
                    String.format("Terminverschiebung fuer WITA-Vorgang '%s' automatisch erzeugt bzw. KWT angepasst",
                            tv.getCarrierRefNr()));
            return tv;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format(
                    "Fehler bei der Generierung einer WITA Terminverschiebung aufgetreten: %s", e.getMessage()), e);
        }
    }

    @Override
    @CcTxRequiresNew
    public WitaCBVorgang doWitaStorno(ErledigtmeldungStornoAuf erlmStrAuf, Long cbVorgangId, AKUser user) {
        try {
            WitaCBVorgang storno = witaTalOrderService.doStorno(cbVorgangId, user);

            createAutomationTaskEntry(erlmStrAuf.getWbciGeschaeftsfall(), erlmStrAuf,
                    AutomationTask.TaskName.WITA_SEND_STORNO, COMPLETED, user,
                    String.format("Storno fuer WITA-Vorgang '%s' automatisch erzeugt", storno.getCarrierRefNr()));
            return storno;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format(
                    "Fehler bei der Generierung eines WITA Stornos aufgetreten: %s", e.getMessage()), e);
        }
    }

    private void createAutomationTaskEntry(
            @NotNull WbciGeschaeftsfall wbciGeschaeftsfall,
            @NotNull Meldung meldung,
            AutomationTask.TaskName task,
            AutomationTask.AutomationStatus automationStatus,
            AKUser user,
            String entryMessage) {
        wbciGeschaeftsfallService.createOrUpdateAutomationTask(
                wbciGeschaeftsfall,
                meldung,
                task,
                automationStatus,
                entryMessage,
                user);
    }
}
