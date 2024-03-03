/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.13
 */
package de.mnet.wbci.service.impl;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.*;
import static de.augustakom.hurrican.model.shared.iface.CCAuftragModel.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.inject.*;
import javax.validation.constraints.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKTeamService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.DNTNB;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.billing.TnbKennungService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.converter.HurricanToWbciConverter;
import de.mnet.wbci.dao.VorabstimmungIdsByBillingOrderNoCriteria;
import de.mnet.wbci.dao.WBCIVorabstimmungFaxDAO;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.AenderungsIdAware;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoIdAware;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.TechnischeRessourceBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciCustomerService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;


/**
 *
 */
@CcTxRequired
public class WbciCommonServiceImpl implements WbciCommonService, WbciConstants {

    private static final Logger LOGGER = Logger.getLogger(WbciCommonServiceImpl.class);

    @Autowired
    private WbciDao wbciDao;
    @Autowired
    private WbciCustomerService wbciCustomerService;
    @Autowired
    private RufnummerService rufnummerService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private RangierungsService rangierungsService;
    @Autowired
    CarrierElTALService carrierElTALService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private HVTService hvtService;
    @Autowired
    private ReferenceService referenceService;
    @Autowired
    private BillingAuftragService billingAuftragService;
    @Autowired
    private KundenService kundenService;
    @Autowired
    private AKTeamService akTeamService;
    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private PhysikService physikService;
    @Autowired
    private WbciGeschaeftsfallStatusUpdateService wbciGeschaeftsfallStatusUpdateService;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private TnbKennungService taifunWebService;
    @Inject
    private WBCIVorabstimmungFaxDAO wbciVorabstimmungFaxDAO;

    private boolean isCarrierDTAG;
    private boolean isWitaVtrFound;

    @Override
    public WBCIVorabstimmungFax save(WBCIVorabstimmungFax wbciVorabstimmungFax) {
        return wbciVorabstimmungFaxDAO.store(wbciVorabstimmungFax);
    }

    @Override
    public WBCIVorabstimmungFax findByVorabstimmungsID(String id) {
        return wbciVorabstimmungFaxDAO.findByVorabstimmungsID(id);
    }

    @Override
    public List<WBCIVorabstimmungFax> findAll(long auftragsId, RequestTyp requestTyp) {
        return wbciVorabstimmungFaxDAO.findAll(auftragsId, requestTyp);
    }

    @Override
    public List<WBCIVorabstimmungFax> findAll(long auftragsId) {
        return wbciVorabstimmungFaxDAO.findAll(auftragsId);
    }

    @Override
    public void delete(Collection<WBCIVorabstimmungFax> ids) {
        wbciVorabstimmungFaxDAO.delete(ids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTnbKennung(Long hurricanAuftragId) {
        if (featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)) {
            final Optional<String> tnbKennungViaRufnummernOpt = getTnbKennungViaRufnummern(hurricanAuftragId);
            if (tnbKennungViaRufnummernOpt.isPresent()) {
                return tnbKennungViaRufnummernOpt.get();
            }
            else {
                // getting tnb kennung via taifun webservice
                return getTnbKennungFromTaifunWebservice(hurricanAuftragId);
            }
        }
        else {
            return getTnbKennungViaStandort(hurricanAuftragId);
        }
    }

    private String getTnbKennungFromTaifunWebservice(Long auftragId) {
        try {
            final AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
            return taifunWebService.getTnbKennungFromTaifunWebservice(auftragDaten.getAuftragNoOrig());
        }
        catch (FindException e) {
            throw new WbciServiceException(String.format("Waehrend der Ermittlung der PKIauf für die " +
                    "Auftragsnummern '%s' ist ein unerwarteter Fehler aufgetreten.", auftragId), e);
        }
    }

    /**
     * Old way to get TnbKennung via Standort With NGN migration is switched to new one getTnbKennungViaRufnummern()
     */
    @Deprecated
    private String getTnbKennungViaStandort(Long auftragId) {
        try {
            final Endstelle endstelle4Auftrag = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            String tnbKennung = null;
            HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(endstelle4Auftrag.getHvtIdStandort());
            if (hvtGruppe != null) {
                tnbKennung = rufnummerService.findTnbKennung4Onkz(hvtGruppe.getOnkz());
            }

            return tnbKennung;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Waehrend der Ermittlung der PKIauf für die " +
                    "Auftragsnummern '%s' ist ein unerwarteter Fehler aufgetreten.", auftragId), e);
        }
    }

    /**
     * Ermittelt die TNB-Kennung (z.B. 'D052') zur der angegebenen Endstelle. Die TNB-Kennung wird ueber die
     * CarrierKennung des technischen Standorts von der Endstelle 'B' des Auftrags ermittelt.
     *
     * @param endstelle Endstelle des Hurrican-Auftrags
     *                  <p>
     *                  Depricated weil die Ermittlung von TNB-Kennung über AuftragId {@link
     *                  WbciCommonServiceImpl#getTnbKennung(Long)} gehen soll. Also to be removed {@link
     *                  HurricanToWbciConverter#extractPKIaufFromElTalAbsenderId(CarrierKennung)}
     */
    @Deprecated
    String getTnbKennung(Endstelle endstelle) {
        try {
            final CarrierKennung carrierKennung4Hvt = carrierService.findCarrierKennung4Hvt(endstelle.getHvtIdStandort());
            return HurricanToWbciConverter.extractPKIaufFromElTalAbsenderId(carrierKennung4Hvt);
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Waehrend der Ermittlung der PKIauf für die " +
                            "Endstelle '%s' mit der Geo-Id '%s' ist ein unerwarteter Fehler aufgetreten.",
                    endstelle.getEndstelle(), endstelle.getGeoId()
            ), e);
        }
    }

    private Optional<String> getTnbKennungViaRufnummern(Long auftragId) {
        try {
            final AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
            final Collection<Rufnummer> rufnummern = rufnummerService.findRNs4Auftrag(auftragDaten.getAuftragNoOrig());
            if (CollectionTools.isEmpty(rufnummern)) {
                final String msg = String.format("Für Auftrag [%d] sind keine Rufnummern angegeben!", auftragId);
                LOGGER.warn(msg);
                return Optional.empty();
            }

            final List<String> onkzList = rufnummern.stream()
                    .map(rn -> rn.getOnKz())
                    .filter(onkz -> StringUtils.hasText(onkz))
                    .distinct()
                    .collect(Collectors.toList());
            if (onkzList.isEmpty()) {
                final String msg = String.format("ONKZ konnte für die Rufnummern [%s] nicht identifiziert werden!", rufnummern.toString());
                throw new WbciServiceException(msg);
            }
            final String onkz = onkzList.get(0);

            final String tnbKennung = rufnummerService.findTnbKennung4Onkz(onkz);
            if (StringUtils.isEmpty(tnbKennung)) {
                final String msg = String.format("TNB konnte für die Rufnummern ONKZ [%s] nicht identifiziert werden!", onkz);
                LOGGER.warn(msg);
                return Optional.empty();
            }

            final DNTNB tnb = rufnummerService.findTNB(tnbKennung);
            if (tnb == null) {
                final String msg = String.format("TNB konnte für die Kennung [%s] nicht identifiziert werden!", tnbKennung);
                LOGGER.warn(msg);
                return Optional.empty();

            }
            final CarrierKennung carrierKennung = carrierService.findCarrierKennung(tnb.getPortKennung());
            return carrierKennung != null && StringUtils.hasText(carrierKennung.getPortierungsKennung())
                    ? Optional.of(carrierKennung.getPortierungsKennung()) : Optional.empty();
        }
        catch (FindException e) {
            final String msg = String.format("TNB Kennung für die Bestellung [%d] konnte nicht ermittelt werden!", auftragId);
            LOGGER.error(msg, e);
            throw new WbciServiceException(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Technologie getMnetTechnologie(Long auftragId) {
        try {
            Endstelle endstelle4Auftrag = getEndstelle4Auftrag(auftragId);

            if (endstelle4Auftrag != null && endstelle4Auftrag.getHvtIdStandort() != null) {
                HVTStandort hvtStandort = hvtService.findHVTStandort(endstelle4Auftrag.getHvtIdStandort());

                Technologie result = Technologie.SONSTIGES;
                if (hvtStandort != null && !Anschlussart.isSpecialConnection(endstelle4Auftrag.getAnschlussart())) {
                    if (endstelle4Auftrag.getRangierId() != null
                            && hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_HVT)) {
                        if (Carrier.ID_DTAG.equals(hvtStandort.getCarrierId())) {
                            // bei DTAG Ports am HVT wird der Technologietyp ueber das Uebertragungsverfahren des Ports
                            // ermittelt
                            Rangierung rangierung = rangierungsService.findRangierung(endstelle4Auftrag.getRangierId());
                            if (rangierung != null && rangierung.getEqOutId() != null) {
                                Equipment equipment = rangierungsService.findEquipment(rangierung.getEqOutId());
                                if (equipment != null && equipment.getUetv() != null) {
                                    result = Technologie.lookUpWbciTechnologieCode(equipment.getUetv().getWbciCode());
                                }
                            }
                        }
                        else {
                            // bei HVTs anderer Carrier soll 'Kupfer' zurueck gemeldet werden
                            result = Technologie.KUPFER;
                        }
                    }
                    else {
                        Reference hvtTypeRef = referenceService.findReference(hvtStandort.getStandortTypRefId());
                        if (hvtTypeRef != null) {
                            result = Technologie.lookUpWbciTechnologieCode(hvtTypeRef.getWbciTechnologieCode());
                        }
                    }
                }

                return result;
            } else {
                return Technologie.SONSTIGES;
            }

        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Waehrend der Ermittlung der M-net Technologie für die " +
                    "Auftragsnummer '%s' ist ein unerwarteter Fehler aufgetreten.", auftragId), e);
        }
    }

    /**
     * Public da readOnly -> entspricht einer Util-Klasse.
     *
     * @param auftragId
     * @return Endstelle
     * @throws FindException
     */
    public Endstelle getEndstelle4Auftrag(Long auftragId) throws FindException {
        List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
        if (endstellen == null || endstellen.isEmpty()){
            return null;
        }
        Endstelle endstelle4Auftrag = getEndstelle(endstellen, Endstelle.ENDSTELLEN_TYP_A);

        if (endstelle4Auftrag == null) {
            endstelle4Auftrag = getEndstelle(endstellen, Endstelle.ENDSTELLEN_TYP_B);
        }
        else {
            Carrierbestellung carrierbestellung = carrierService.findLastCB4Endstelle(endstelle4Auftrag.getId());
            if (carrierbestellung != null) {
                CBVorgang cbVorgang = carrierElTALService.findCBVorgang(carrierbestellung.getId());
                // wenn es für EndstelleTypA keine Carrierbestellung gibt, dann Endstelle B verwenden
                if (!((cbVorgang != null) && auftragId.equals(cbVorgang.getAuftragId()))) {
                    endstelle4Auftrag = getEndstelle(endstellen, Endstelle.ENDSTELLEN_TYP_B);
                }
            }
            else {
                endstelle4Auftrag = getEndstelle(endstellen, Endstelle.ENDSTELLEN_TYP_B);
            }
        }
        return endstelle4Auftrag;
    }

    private Endstelle getEndstelle(List<Endstelle> endstellen, String endstellenTyp) {

        for (Endstelle endstelle : endstellen) {
            if (endstellenTyp.equals(endstelle.getEndstelleTyp())) {
                return endstelle;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public KundenTyp getKundenTyp(Long billingOrderNoOrig) throws FindException {
        return getKundenTypForKundenNo(getBillingAuftrag(billingOrderNoOrig).getKundeNo(), billingOrderNoOrig);
    }

    /**
     * see {@link #getKundenTyp(Long)}. *
     */
    KundenTyp getKundenTypForKundenNo(Long kundenNo, Long taifunOrderId) throws FindException {
        Kunde kunde = kundenService.findKunde(kundenNo);
        if (kunde == null) {
            throw new WbciServiceException("Der Kunde mit der ID '" + kundenNo
                    + "' zum Taifun Auftrag '" + taifunOrderId + "' konnte nicht ermittelt werden!");
        }
        if (kunde.isBusinessCustomer()) {
            return KundenTyp.GK;
        }
        return KundenTyp.PK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<TechnischeRessource> getTechnischeRessourcen(@NotNull Long billingOrderNoOrig,
            @Nullable Set<Long> nonBillableBillingOrderNoOrigs) {
        final String expMessage = "Die %s konnte nicht ermittelt werden; Aufbau der technischen Ressource ist deshalb nicht möglich.";
        final Set<Long> wbciRelevantHurricanOrderNos = getWbciRelevantHurricanOrderNos(billingOrderNoOrig,
                nonBillableBillingOrderNoOrigs);
        Set<TechnischeRessource> resultSet = new HashSet<>();
        isCarrierDTAG = false;
        isWitaVtrFound = false;

        for (Long auftragId : wbciRelevantHurricanOrderNos) {
            try {
                // Ermittlung der Endstelle
                Endstelle endstelle4Auftrag = getEndstelle4Auftrag(auftragId);
                if(endstelle4Auftrag == null){
                    throw new WbciServiceException(
                            String.format("Im System konnte keine gueltige Endstelle für den Auftrag mit Nr '%s' "  +
                                    "gefunden werden. Dadurch ist das Erzeugen einer Vertragsnummer nicht moeglich.", auftragId
                            )
                    );
                }
                boolean isVirtuell = Anschlussart.ANSCHLUSSART_VIRTUELL.equals(endstelle4Auftrag.getAnschlussart());

                HVTStandort hvtStandort = hvtService.findHVTStandort(endstelle4Auftrag.getHvtIdStandort());
                if (hvtStandort != null) {
                    boolean createWbciLineId = true;

                    // Falls der Carrier DTAG ist, wird die Wita-Vertragsnummer verwendet, ansonsten wird eine
                    // WbciLineId verwendet
                    if (Carrier.ID_DTAG.equals(hvtStandort.getCarrierId())) {
                        createWbciLineId = createResultWithWitaVtrNr(resultSet, auftragId, endstelle4Auftrag, isVirtuell);
                    }

                    if (createWbciLineId) {
                        createResultWithWbciLineId(expMessage, resultSet, auftragId);
                    }
                }
                else {
                    throw new WbciServiceException(
                            String.format("Im System konnte kein HvtStandort fuer die Endstelle zum Auftrag mit Nr '%s' "  +
                                            "gefunden werden. Dadurch ist das Erzeugen einer Vertragsnummer nicht moeglich.", auftragId
                            )
                    );
                }
            }
            catch (WbciServiceException e) {
                throw e;
            }
            catch (Exception e) {
                throw new WbciServiceException(
                        String.format("Bei der Ermittlung der Technischen Ressourcen für die Auftragsn-Nr '%s' ist ein Fehler aufgetreten.",
                                auftragId), e
                );
            }
        }

        if (isCarrierDTAG && !isWitaVtrFound) {
            throw new WbciServiceException(String.format(expMessage, "WITA-Vertrags-Nr."));
        }

        return resultSet;
    }

    private boolean createResultWithWitaVtrNr(Set<TechnischeRessource> resultSet, Long auftragId, Endstelle endstelle4Auftrag, boolean isVirtuell) {
        boolean createWbciLineId = true;
        isCarrierDTAG = true;
        String witaVtrnr = getWitaVtrNr(endstelle4Auftrag);
        if (witaVtrnr != null) {
            isWitaVtrFound = true;
            createWbciLineId = false;
            resultSet.add(
                    new TechnischeRessourceBuilder()
                            .withVertragsnummer(witaVtrnr)
                            .withTnbKennungAbg(getTnbKennung(auftragId))
                            .build()
            );
        }
        else {
            if (isVirtuell) {
                isWitaVtrFound = true;
            }
            else {
                // in diesem Fall darf kein createWbciLineId ausgeführt werden, da
                // WitaVtr eigentlich da sein müsste, aber fehlt.
                // wenn alle wbciRelevantHurricanOderNos in diesen Else-Zweig kommen,
                // fliegt am Ende die WbciServiceException, dass WITA-Vertrags-Nr. fehlt. (HUR-25987)
                createWbciLineId = false;
            }
        }
        return createWbciLineId;
    }

    private void createResultWithWbciLineId(String expMessage, Set<TechnischeRessource> resultSet, Long auftragId) {
        String wbciLineId = getWbciLineId(auftragId);
        if (wbciLineId != null) {
            resultSet.add(
                    new TechnischeRessourceBuilder()
                            // WITA-2023 vorerst wird die generierte Id einfach als Identifizierer
                            // uebergeben; wenn spaeter FTTB/H Ressourcen per Wholesale abgegeben werden
                            // koennen, dann wird eine weitere Unterscheidung auf den Standorttyp
                            // durchgefuehrt und diese Id dann als WBCI LineId verwendet; fuer
                            // andere Standorte (z.B. Gewofag) wird es aber weiterhin als Identifizierer
                            // geschrieben. Dadurch wird dem Partner-EKP mitgeteilt, dass die Ressource
                            // nicht uebernommen werden kann.
                            .withIdentifizierer(wbciLineId)
                            .withTnbKennungAbg(getTnbKennung(auftragId))
                            .build()
            );
        }
        else {
            throw new WbciServiceException(String.format(expMessage, "WBCI-Line-ID"));
        }
    }


    /**
     * Sucht anhand der Hurrican-Auftrags-Id nach einer WBCI-Line-Id
     */
    String getWbciLineId(Long auftragId) {
        try {
            VerbindungsBezeichnung vbz = physikService.findOrCreateVerbindungsBezeichnungForWbci(auftragId);
            if (vbz != null) {
                return vbz.getWbciLineId();
            }
            return null;

        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Waehrend der Ermittlung der WBCI-LINE-ID für die " +
                    "Hurrican-Auftrag '%s' ist ein unerwarteter Fehler aufgetreten.", auftragId), e);
        }
    }

    /**
     * Sucht anhand der Endstelle nach einer WITA-Vertrags-Nr.
     */
    String getWitaVtrNr(Endstelle endstelle) {
        try {
            Carrierbestellung carrierbestellung = carrierService.findLastCB4Endstelle(endstelle.getId());
            if (carrierbestellung != null && !StringUtils.isEmpty(carrierbestellung.getVtrNr())) {
                return carrierbestellung.getVtrNr();
            }
            return null;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Waehrend der Ermittlung der WITA-Vertragsnummer für die " +
                            "Endstelle '%s' mit der Geo-Id '%s' ist ein unerwarteter Fehler aufgetreten.",
                    endstelle.getEndstelle(), endstelle.getGeoId()
            ), e);
        }
    }

    @Override
    public Set<Long> getWbciRelevantHurricanOrderNos(@Nullable Set<Long> billingOrderNoOrigs) {
        try {
            if (CollectionTools.isNotEmpty(billingOrderNoOrigs)) {

                List<AuftragDaten> auftragDatenList = new ArrayList<>();
                for (Long billingOrderNoOrig : billingOrderNoOrigs) {
                    List<AuftragDaten> auftragDaten4OrderNoOrig = ccAuftragService.findAuftragDaten4OrderNoOrig(billingOrderNoOrig);
                    if (auftragDaten4OrderNoOrig != null) {
                        auftragDatenList.addAll(auftragDaten4OrderNoOrig);
                    }
                }

                if (CollectionTools.isNotEmpty(auftragDatenList)) {
                    if (CollectionTools.hasExpectedSize(auftragDatenList, 1)) {
                        Set<Long> auftragIds = new HashSet<>();
                        auftragIds.add(auftragDatenList.get(0).getAuftragId());
                        return auftragIds;
                    }
                    else {
                        List<AuftragDaten> cancelledOrders = newArrayList(filter(auftragDatenList,
                                AuftragDaten::isCancelled));
                        List<AuftragDaten> activeOrders = newArrayList(filter(auftragDatenList,
                                not(AuftragDaten::isCancelled)));

                        Iterable<Long> relevantOrders;
                        if (!activeOrders.isEmpty()) {
                            relevantOrders = transform(activeOrders, GET_AUFTRAG_ID);
                        }
                        else {
                            relevantOrders = transform(cancelledOrders, GET_AUFTRAG_ID);
                        }
                        return Sets.newHashSet(relevantOrders);
                    }
                }
            }
            return Collections.emptySet();
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WbciServiceException("Error searching for relevant Hurrican orders: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<Long> getWbciRelevantHurricanOrderNos(@NotNull Long billingOrderNoOrig,
            @Nullable Set<Long> nonBillableBillingOrderNoOrigs) {
        Set<Long> billingOrderNoSet = Sets.newHashSet(billingOrderNoOrig);
        if (nonBillableBillingOrderNoOrigs != null) {
            billingOrderNoSet.addAll(nonBillableBillingOrderNoOrigs);
        }
        return getWbciRelevantHurricanOrderNos(billingOrderNoSet);
    }

    @Override
    public Long getHurricanOrderIdForWitaVtrNrAndCurrentVA(@NotNull String witaVtrNr, @NotNull Long billingOrderNoOrig, @Nullable Set<Long> nonBillableBillingOderNoOrigs) {
        try {
            Set<Long> possibleHurricanOrders = getWbciRelevantHurricanOrderNos(billingOrderNoOrig, nonBillableBillingOderNoOrigs);
            for (Long hurricanOrderId : possibleHurricanOrders) {
                Endstelle endstelle = endstellenService.findEndstelle4Auftrag(hurricanOrderId, Endstelle.ENDSTELLEN_TYP_B);
                if (endstelle != null) {
                    Carrierbestellung carrierbestellung = carrierService.findLastCB4Endstelle(endstelle.getId());
                    if (witaVtrNr.equalsIgnoreCase(carrierbestellung.getVtrNr())) {
                        return hurricanOrderId;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            // do nothing!
        }

        return null;
    }

    @Override
    public String getNextSequence4RequestType(RequestTyp requestTyp) {
        return String.format("%09d", wbciDao.getNextSequenceValue(requestTyp));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNextPreAgreementId(RequestTyp requestTyp) {
        return generateNextPreAgreementId(requestTyp, VA_ROUTING_PREFIX_HURRICAN);
    }

    @Override
    public String getNextPreAgreementIdFax(RequestTyp requestTyp) {
        return generateNextPreAgreementId(requestTyp, VA_ROUTING_PREFIX_FAX);
    }

    private String generateNextPreAgreementId(RequestTyp requestTyp, String prefix) {
        String nextValueWithLeadingZeros = String.format("%08d", wbciDao.getNextSequenceValue(requestTyp));
        // z.B. 'DEU.MNET.VH12345678'
        return String.format("%s.%s%s%s",
                CarrierCode.MNET.getITUCarrierCode(),
                requestTyp.getPreAgreementIdCode(),
                prefix,
                nextValueWithLeadingZeros);
    }

    @Override
    public List<RufnummerPortierungSelection> getRufnummerPortierungList(Long auftragNoOrig) {
        try {
            Preconditions.checkNotNull(auftragNoOrig);

            Collection<Rufnummer> rufnummernKommend = rufnummerService.findDnsKommendForWbci(auftragNoOrig);
            // Mobilnummer werden nicht betrachtet und müssen dementsprechend aussortiert werden
            return rufnummernKommend.stream()
                    .filter(rufnummer -> !rufnummer.isMobile())
                    .map(RufnummerPortierungSelection::new)
                    .collect(Collectors.toList());
        }
        catch (FindException e) {
            throw new WbciServiceException(String.format("Waehrend der Suche der zu portierenden Rufnummern für die " +
                    "Auftragsnummern '%s' ist ein unerwarteter Fehler aufgetreten.", auftragNoOrig), e);
        }
    }

    @Override
    public Adresse getAnschlussAdresse(Long auftragNoOrig) {
        try {
            Preconditions.checkNotNull(auftragNoOrig);
            return billingAuftragService.findAnschlussAdresse4Auftrag(auftragNoOrig, Endstelle.ENDSTELLEN_TYP_B);
        }
        catch (FindException e) {
            throw new WbciServiceException(String.format("Waehrend der Suche der Anschlussadresse für die " +
                    "Auftragsnummern '%s' ist ein unerwarteter Fehler aufgetreten.", auftragNoOrig), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PreAgreementVO> findPreAgreements(CarrierRole mnetCarrierRole) {
        return findPreAgreements(mnetCarrierRole, null);
    }

    @Override
    public List<PreAgreementVO> findPreAgreements(CarrierRole mnetCarrierRole, String singlePreAgreementIdToLoad) {
        try {
            return enrichTeamDescriptions(wbciDao.findMostRecentPreagreements(mnetCarrierRole, singlePreAgreementIdToLoad));
        }
        catch (Exception e) {
            throw new WbciServiceException("Waehrend der Suche nach allen VorabstimmungsProzesse ist ein " +
                    "unerwarteter Fehler aufgetreten.", e);
        }
    }

    /**
     * Makes a lookup for the current user teams and add the team names to the {@link PreAgreementVO}.
     *
     * @param preAgreementVOs list of preloaded {@link PreAgreementVO}s.
     * @return an enriched list
     * @throws AKAuthenticationException
     */
    List<PreAgreementVO> enrichTeamDescriptions(List<PreAgreementVO> preAgreementVOs)
            throws AKAuthenticationException {
        Map<Long, AKTeam> lookupMap = akTeamService.findAllAsMap();
        for (PreAgreementVO vo : preAgreementVOs) {
            Long teamId = vo.getTeamId();
            if (teamId != null && lookupMap.containsKey(teamId)) {
                vo.setTeamName(lookupMap.get(teamId).getName());
            }
        }
        return preAgreementVOs;
    }

    @Override
    public WbciRequest findLastWbciRequest(String vorabstimmungsId) {
        try {
            return wbciDao.findLastWbciRequestByType(vorabstimmungsId, WbciRequest.class);
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    String.format(
                            "Waehrend der Suche nach dem letzten WbciRequests zur Vorabstimmungs-Id '%s' ist ein unerwarteter Fehler aufgetreten.",
                            vorabstimmungsId), e
            );
        }
    }

    @Override
    public <T extends WbciRequest> List<T> findWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp) {
        try {
            return wbciDao.findWbciRequestByType(vorabstimmungsId, requestTyp);
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    String.format(
                            "Waehrend der Suche nach den WbciRequests zur Vorabstimmungs-Id '%s' und requestTyp '%s' ist ein unerwarteter Fehler aufgetreten.",
                            vorabstimmungsId, requestTyp), e
            );
        }
    }

    @Override
    public WbciRequest findWbciRequestById(Long id) {
        try {
            return wbciDao.findById(id, WbciRequest.class);
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format(
                    "Waehrend der Suche nach dem WbciRequest mit der Id %s ist ein unerwarteter Fehler aufgetreten.",
                    id), e);
        }
    }

    @Override
    public WbciRequest findWbciRequestByChangeId(String vorabstimmungsId, String changeId) {
        try {
            List<WbciRequest> requests = wbciDao.findWbciRequestByChangeId(vorabstimmungsId, changeId);
            if (CollectionUtils.isNotEmpty(requests)) {
                return requests.get(0);
            }
            return null;
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    String.format(
                            "Waehrend der Suche nach dem WbciRequest mit der ChangeId '%s' ist ein unerwarteter Fehler aufgetreten.",
                            changeId), e
            );
        }
    }

    @Override
    public WbciGeschaeftsfall findWbciGeschaeftsfall(String vorabstimmungsId) {
        try {
            return wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    String.format(
                            "Waehrend der Suche nach den WbciRequests zu Vorabstimmungs-Id %s ist ein unerwarteter Fehler aufgetreten.",
                            vorabstimmungsId), e
            );
        }
    }

    @Override
    public VorabstimmungsAnfrage findVorabstimmungsAnfrage(String vorabstimmungsId) {
        List<VorabstimmungsAnfrage> result = wbciDao.findWbciRequestByType(vorabstimmungsId, VorabstimmungsAnfrage.class);
        if (CollectionUtils.isEmpty(result)) {
            throw new WbciServiceException(String.format("Vorabstimmung zur Id '%s' wurde nicht gefunden!", vorabstimmungsId));
        }
        else if (result.size() == 1) {
            return result.get(0);
        }
        //result.size() > 1
        throw new WbciServiceException(String.format(
                "Während der Suche nach den Vorabstimmung  zur Id '%s' wurden '%s' Vorabstimmungen gefunden, diese wurde nicht erwartet!",
                vorabstimmungsId, result.size()));
    }

    @Override
    public Set<Long> findNonBillingRelevantTaifunAuftragIds(String vorabstimmungsId) {
        WbciGeschaeftsfall wbciGeschaeftsfall = findWbciGeschaeftsfall(vorabstimmungsId);
        return wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs();
    }

    @Override
    public Meldung findLastForVaId(String vorabstimmungsId) {
        try {
            final List<Meldung> wbciMeldungen = findMeldungenForVaId(vorabstimmungsId);
            if (CollectionTools.isNotEmpty(wbciMeldungen)) {
                return wbciMeldungen.get(0);
            }
            return null;
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    "Waehrend der Suche nach den Meldungen zu einer Vorabstimmungs-Id ist ein Fehler aufgetreten.", e);
        }
    }

    @Override
    public <M extends Meldung> M findLastForVaId(String vorabstimmungsId, Class<M> classDef) {
        try {
            final List<M> wbciMeldungen = wbciDao.findMeldungenForVaIdAndMeldungClass(vorabstimmungsId, classDef);
            if (CollectionTools.isNotEmpty(wbciMeldungen)) {
                return wbciMeldungen.get(0);
            }
            return null;
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    "Waehrend der Suche nach den Meldungen zu einer Vorabstimmungs-Id ist ein Fehler aufgetreten.", e);
        }
    }

    @Override
    public List<Meldung> findMeldungenForVaId(String vorabstimmungsId) {
        try {
            return wbciDao.findMeldungenForVaId(vorabstimmungsId);
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    "Waehrend der Suche nach den Meldungen zu einer Vorabstimmungs-Id ist ein Fehler aufgetreten.", e);
        }
    }

    @Override
    public List<Meldung> filterMeldungenForVa(@NotNull List<Meldung> meldungen) {
        List<Meldung> result = new ArrayList<>();
        for (Meldung meldung : meldungen) {
            boolean meldungIsNotForStornoOrTv = true;

            if (meldung instanceof AenderungsIdAware && ((AenderungsIdAware) meldung).getAenderungsIdRef() != null) {
                meldungIsNotForStornoOrTv = false;
            }
            else if (meldung instanceof StornoIdAware && ((StornoIdAware) meldung).getStornoIdRef() != null) {
                meldungIsNotForStornoOrTv = false;
            }

            if (meldungIsNotForStornoOrTv) {
                result.add(meldung);
            }
        }
        return result;
    }

    @Override
    public List<Meldung> filterMeldungenForTv(@NotNull List<Meldung> meldungen, @NotNull String tvId) {
        return meldungen.stream()
                .filter(meldung ->
                        meldung instanceof AenderungsIdAware && tvId != null
                                && tvId.equals(((AenderungsIdAware) meldung).getAenderungsIdRef()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meldung> filterMeldungenForStorno(@NotNull List<Meldung> meldungen, @NotNull String stornoId) {
        return meldungen.stream()
                .filter(meldung ->
                        meldung instanceof StornoIdAware && stornoId != null
                                && stornoId.equals(((StornoIdAware) meldung).getStornoIdRef()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getPreAgreementIdsByOrderNoOrig(Long billingOrderNoOrig) {
        try {
            final List<WbciGeschaeftsfall> wbciGeschaeftsfallList = wbciDao.findGfByOrderNoOrig(billingOrderNoOrig);
            Set<String> preAgreementIDs = new HashSet<>(wbciGeschaeftsfallList.size());
            preAgreementIDs.addAll(
                    wbciGeschaeftsfallList.stream()
                            .map(WbciGeschaeftsfall::getVorabstimmungsId)
                            .collect(Collectors.toList())
            );
            return preAgreementIDs;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Waehrend der Suche nach allen VorabstimmungIds fuer den " +
                    "Billing-Auftrag '%s' ist ein unerwarteter Fehler aufgetreten.", billingOrderNoOrig), e);
        }
    }

    @Override
    public void assignTaifunOrder(@NotNull String vorabstimmungsId,
            @NotNull Long taifunOrderNo,
            boolean addCustomerCommunication)
            throws FindException {

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        if (CarrierCode.MNET != wbciGeschaeftsfall.getAbgebenderEKP()) {
            throw new WbciServiceException(String.format("Die Zuordnung eines Taifun Auftrags zu einer " +
                    "Vorabstimmungsanfrage ist nur fuer eingehende Vorabstimmungsanfragen (AbgebenderEKP = MNET) " +
                    "moeglich! Die Vorabstimmungsanfrage mit der id '%s' ist eine ausgehende.", vorabstimmungsId));
        }
        final BAuftrag auftrag = getBillingAuftrag(taifunOrderNo);
        wbciGeschaeftsfall.setBillingOrderNoOrig(taifunOrderNo);

        Set<Long> nonBillableAuftragIds = rufnummerService.getCorrespondingOrderNoOrigs(taifunOrderNo);
        wbciGeschaeftsfall.setNonBillingRelevantOrderNoOrigs(nonBillableAuftragIds);

        // set the customer typ for the Taifun order
        wbciGeschaeftsfall.getEndkunde().setKundenTyp(getKundenTypForKundenNo(auftrag.getKundeNo(), taifunOrderNo));

        Set<Long> hurricanOrderIds = getWbciRelevantHurricanOrderNos(Sets.newHashSet(taifunOrderNo));
        // failsafe - only set hurrican order id and mnet_technologie when exactly single relevant hurrican order was
        // found
        if (!hurricanOrderIds.isEmpty()) {
            // Ersten gefundenen Hurrican-Auftrag fuer die Zuordnung und Technologie-Ermittlung verwenden.
            // (Das sollte kein Problem darstellen, da eine Technologie-Mischelung eigentlich nicht vorkommen kann. Bei
            // Umschaltungen von z.B. HVT auf KVZ gibt es neue TAI-Auftraege; und falls doch einmal eine Umschaltung auf
            // dem gleichen TAI-Auftrag durchgefuehrt wird, dann ist ueber getWbciRelevantHurricanOrderNos
            // sichergestellt,
            // dass nur gekuendigte oder nur aktive Auftraege zurueck gegeben werden; und somit sollte die Technologie
            // auch wieder eindeutig sein.)
            Long hurricanOrderId = hurricanOrderIds.iterator().next();
            wbciGeschaeftsfall.setAuftragId(hurricanOrderId);
            wbciGeschaeftsfall.setMnetTechnologie(getMnetTechnologie(hurricanOrderId));
        }
        else {
            LOGGER.warn(String.format(
                    "Es wurden keine oder mehrere relevante Hurrican Aufträge zur TaifunOrderId (%s) gefunden. " +
                            "Keine automatische Zuordnung der Hurrican AuftragsId und der Mnet-Technologie möglich!",
                    taifunOrderNo
            ));
        }

        wbciDao.store(wbciGeschaeftsfall);

        LOGGER.info(String.format("Successfully assigned Taifun order '%s' to VA with VA-Id: '%s'", taifunOrderNo, vorabstimmungsId));

        if (addCustomerCommunication) {
            sendCustomerServiceProtocol(wbciGeschaeftsfall);
        }
    }


    /**
     * Determine the {@link BAuftrag} for the taifun oder no)
     */
    BAuftrag getBillingAuftrag(Long taifunOrderNo) throws FindException {
        final BAuftrag auftrag = billingAuftragService.findAuftrag(taifunOrderNo);
        if (auftrag == null) {
            throw new WbciServiceException(String.format("Es konnte kein Taifun Auftrag mit der Id '%s' " +
                    "gefunden werden!", taifunOrderNo));
        }
        return auftrag;
    }

    /**
     * Sends out a customer service protocol for VA Requests that are manually associated with the Taifun order <br />
     * Typically this protocol is automatically sent when the incoming VA request is received. However in the event that
     * the incoming VA request cannot be automatically associated with a Taifun Order, or in the event that the VA
     * request was associated with the wrong Taifun order and needs to be corrected, then the protocol has to be sent
     * (or indeed resent) to customer services.
     */
    private void sendCustomerServiceProtocol(WbciGeschaeftsfall wbciGeschaeftsfall) {
        List<VorabstimmungsAnfrage> vorabstimmungsAnfrages = wbciDao.findWbciRequestByType(
                wbciGeschaeftsfall.getVorabstimmungsId(), VorabstimmungsAnfrage.class);
        if (!vorabstimmungsAnfrages.isEmpty()) {
            wbciCustomerService.sendCustomerServiceProtocol(vorabstimmungsAnfrages.get(0));
        }
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public Set<String> findVorabstimmungIdsByBillingOrderNoOrig(VorabstimmungIdsByBillingOrderNoCriteria criteria) {
        try {
            List<String> result = wbciDao.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
            return (result != null) ? new HashSet<>(result) : Collections.<String>emptySet();
        }
        catch (Exception e) {
            throw new WbciServiceException("Waehrend der Suche nach den VorabstimmungIds zu einer Taifunauftrag-Id ist"
                    +
                    " ein Fehler aufgetreten.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WbciGeschaeftsfall> findActiveGfByTaifunId(Long billingOrderNoOrig, boolean interpretNewVaStatusesAsActive) {
        try {
            return wbciDao.findActiveGfByOrderNoOrig(billingOrderNoOrig, interpretNewVaStatusesAsActive);
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    "Waehrend der Suche nach aktiven Geschaeftsfaelle zu einer Taifunauftrag-Id ist" +
                            " ein Fehler aufgetreten.", e
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WbciGeschaeftsfall> findCompleteGfByTaifunId(Long billingOrderNoOrig) {
        try {
            return wbciDao.findCompleteGfByOrderNoOrig(billingOrderNoOrig);
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    "Waehrend der Suche nach geschlossenen Geschaeftsfaellen zu einer Taifunauftrag-Id ist" +
                            " ein Fehler aufgetreten.", e
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WbciGeschaeftsfall> findAllGfByTaifunId(Long billingOrderNoOrig) {
        try {
            return wbciDao.findGfByOrderNoOrig(billingOrderNoOrig);
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    "Waehrend der Suche nach allen Geschaeftsfaellen zu einer Taifunauftrag-Id ist" +
                            " ein Fehler aufgetreten.", e
            );
        }
    }

    @Override
    public String saveComment(@NotNull String vorabstimmungsId, String comment, AKUser user) {
        WbciGeschaeftsfall wbciGeschaeftsfall = findWbciGeschaeftsfall(vorabstimmungsId);

        String oldCommentData = wbciGeschaeftsfall.getBemerkungen();

        if (!StringUtils.hasText(comment) || comment.equals(oldCommentData)) {
            return oldCommentData;
        }

        String userLoginName;
        if (user == null) {
            userLoginName = HurricanConstants.UNKNOWN;
        }
        else {
            userLoginName = user.getLoginName();
        }

        wbciGeschaeftsfall.setBemerkungen(comment + " (" + userLoginName + ", "
                + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME) + ")");
        wbciDao.store(wbciGeschaeftsfall);

        return wbciGeschaeftsfall.getBemerkungen();
    }

    @Override
    public String addComment(@NotNull String vorabstimmungsId, String comment, AKUser user) {
        WbciGeschaeftsfall wbciGeschaeftsfall = findWbciGeschaeftsfall(vorabstimmungsId);
        String oldCommentData = wbciGeschaeftsfall.getBemerkungen();

        if (!StringUtils.hasText(comment) || comment.equals(oldCommentData)) {
            return oldCommentData;
        }

        String newCommentData = comment;
        if (StringUtils.hasText(oldCommentData)) {
            // make sure we keep the old comment data and append the new comment to it
            newCommentData = String.format("%s%n%s", oldCommentData, comment);
        }
        return saveComment(vorabstimmungsId, newCommentData, user);
    }

    @Override
    public Boolean isSichererHafenRequested(String vorabstimmungsId) {
        UebernahmeRessourceMeldung last = getLastUebernahmeRessourceMeldung(vorabstimmungsId);
        return (last != null && Boolean.TRUE.equals(last.isSichererHafen()));
    }

    @Override
    public Boolean isResourceUebernahmeRequested(String vorabstimmungsId) {
        UebernahmeRessourceMeldung last = getLastUebernahmeRessourceMeldung(vorabstimmungsId);
        return (last != null && Boolean.TRUE.equals(last.isUebernahme()));
    }

    @Override
    public UebernahmeRessourceMeldung getLastUebernahmeRessourceMeldung(String vorabstimmungsId) {
        LocalDateTime lastProcessedDate = null;
        UebernahmeRessourceMeldung latest = null;
        List<UebernahmeRessourceMeldung> akmtrs = wbciDao.findMeldungenForVaIdAndMeldungClass(vorabstimmungsId,
                UebernahmeRessourceMeldung.class);
        for (UebernahmeRessourceMeldung akmtr : akmtrs) {
            if (akmtr.getProcessedAt() != null
                    && (lastProcessedDate == null || lastProcessedDate.isBefore(DateConverterUtils.asLocalDateTime(akmtr.getProcessedAt())))) {
                lastProcessedDate = DateConverterUtils.asLocalDateTime(akmtr.getProcessedAt());
                latest = akmtr;
            }
        }
        return latest;
    }

    @Override
    public void assignTask(@NotNull String vorabstimmungsId, AKUser user) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);

        if (wbciGeschaeftsfall.getUserId() == null) {
            wbciGeschaeftsfall.setUserAndTeam(user);
        }
        wbciGeschaeftsfall.setCurrentUser(user);
        wbciDao.store(wbciGeschaeftsfall);
    }

    @Override
    public void releaseTask(@NotNull String vorabstimmungsId) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        if (wbciGeschaeftsfall.getCurrentUserId() != null) {
            wbciGeschaeftsfall.setCurrentUser(null);
            wbciDao.store(wbciGeschaeftsfall);
        }
    }

    Date getLatestRealDate(final WbciGeschaeftsfall wbciGeschaeftsfall) throws FindException {
        final List<Rufnummer> rNs4Auftrag = rufnummerService
                .findRNs4Auftrag(wbciGeschaeftsfall.getBillingOrderNoOrig());
        Date latestRealDate = null;
        if (rNs4Auftrag != null) {
            for (Rufnummer rufnummer : rNs4Auftrag) {
                if (Rufnummer.PORT_MODE_KOMMEND.equals(rufnummer.getPortMode())) {
                    final Date realDate = rufnummer.getRealDate();
                    if (latestRealDate == null || latestRealDate.before(realDate)) {
                        latestRealDate = realDate;
                    }
                }
            }
        }
        return latestRealDate;
    }


    @Override
    public void closeProcessing(Long requestId) {
        WbciRequest wbciRequest = wbciDao.findById(requestId, WbciRequest.class);
        passivateWbciRequest(wbciRequest);
    }

    @CcTxRequiresNew
    @Override
    public void closeProcessing(WbciRequest wbciRequest) {
        passivateWbciRequest(wbciRequest);
    }

    void passivateWbciRequest(WbciRequest toPassivate) {
        if (toPassivate != null && !toPassivate.getRequestStatus().isInitialStatus()) {
            WbciGeschaeftsfall gf = toPassivate.getWbciGeschaeftsfall();
            if (gf != null && !WbciGeschaeftsfallStatus.NEW_VA.equals(gf.getStatus())) {
                wbciGeschaeftsfallStatusUpdateService.updateGeschaeftsfallStatus(gf.getId(), WbciGeschaeftsfallStatus.PASSIVE);
            }
        }
    }

    @Override
    public Collection<Rufnummer> getRNs(Long billingOrderNoOrig) {
        try {
            List<Rufnummer> rufnummern = rufnummerService.findRNs4Auftrag(billingOrderNoOrig);
            if (CollectionUtils.isNotEmpty(rufnummern)) {
                return Collections2.filter(rufnummern, input -> {
                    return !input.isMobile(); // nur Festnetznummer betrachten
                });
            }

            // keine Rufnummern gefunden --> alle Rufnummern des Auftrags laden, die zum Kuendigungsdatum noch aktiv
            // waren
            BAuftrag billingOrder = billingAuftragService.findAuftrag(billingOrderNoOrig);
            rufnummern = (billingOrder != null)
                    ? rufnummerService.findRNs4Auftrag(billingOrderNoOrig, billingOrder.getGueltigBis())
                    : null;

            if (CollectionUtils.isNotEmpty(rufnummern)) {
                Map<Long, Rufnummer> rufnummernMap = new HashMap<>();
                for (Rufnummer dn : rufnummern) {
                    // nur Festnetznummer betrachten
                    if (!dn.isMobile()) {
                        Rufnummer lastDn = rufnummerService.findLastRN(dn.getDnNoOrig());

                        // nur Rufnummern ermitteln, die als Rueckfall und Deaktivierung (=M-net Bestand) markiert sind
                        if (lastDn != null
                                && !rufnummernMap.containsKey(lastDn.getDnNoOrig())
                                && (lastDn.isPortierungRueckfall() || lastDn.isPortierungDeaktivierung())) {
                            rufnummernMap.put(lastDn.getDnNoOrig(), lastDn);
                        }
                    }
                }

                return new ArrayList<>(rufnummernMap.values());
            }

            return Collections.emptyList();
        }
        catch (FindException e) {
            throw new WbciServiceException("Unable to load assigned dial-numbers from billing system!", e);
        }
    }

    @Override
    public List<OverdueAbmPvVO> findPreagreementsWithOverdueAbmPv() {
        return wbciDao.findPreagreementsWithOverdueAbmPv(DateCalculationHelper.getDateInWorkingDaysFromNow(14).toLocalDate());
    }

    @Override
    public WbciGeschaeftsfall findOriginalWbciGeschaeftsfall(String vorabstimmungsId) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = this.findWbciGeschaeftsfall(vorabstimmungsId);
        if (wbciGeschaeftsfall.getStrAenVorabstimmungsId() != null) {
            // has older Geschaeftsfall, doing recurrsion
            return findOriginalWbciGeschaeftsfall(wbciGeschaeftsfall.getStrAenVorabstimmungsId());
        }
        else {
            // this one is an original Geschaeftsfall
            return wbciGeschaeftsfall;
        }
    }
}
