/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 14:07:17
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.billing.LeistungDAO;
import de.augustakom.hurrican.dao.billing.ServiceValueDAO;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.ServiceBlockPrice;
import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.model.billing.query.KundeLeistungQuery;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.KundeLeistungView;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Service-Implementierung, um Objekte vom Typ <code>Leistung</code> zu verwalten.
 *
 *
 */
@BillingTx
public class LeistungServiceImpl extends DefaultBillingService implements LeistungService {

    private static final Logger LOGGER = Logger.getLogger(LeistungServiceImpl.class);

    private ServiceValueDAO serviceValueDAO = null;

    @Autowired
    private CCAuftragService as;
    
    @Autowired
    private ProduktService ps;

    @Autowired
    private LeistungDAO leistungDAO;

    @Override
    public Leistung findLeistung(Long leistungNoOrig) throws FindException {
        return leistungDAO.findLeistungByNoOrig(leistungNoOrig);
    }

    @Override
    public List<KundeLeistungView> findLeistungen4Kunde(KundeLeistungQuery query) throws FindException {
        if ((query == null) || query.isEmpty() || (query.getKundeNo() == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            List<BAuftragLeistungView> views = bas.findAuftragLeistungViews(query.getKundeNo(), true);
            if (views != null) {
                Number[] oeNoOrigs = query.getOeNoOrigs().toArray(new Number[query.getOeNoOrigs().size()]);

                List<KundeLeistungView> retVal = new ArrayList<>();
                for (BAuftragLeistungView leistungsView : views) {
                    if (NumberTools.isIn(leistungsView.getOeNoOrig(), oeNoOrigs)) {
                        KundeLeistungView view = new KundeLeistungView();
                        view.setKundeNo(query.getKundeNo());
                        view.setOeNoOrig(leistungsView.getOeNoOrig());
                        view.setLeistungName(leistungsView.getLeistungName());
                        String rText = findRechnungstext(leistungsView.getLeistungNoOrig(),
                                leistungsView.getServiceValueParam(), BillingConstants.LANG_CODE_GERMAN);
                        view.setLeistungRechnungstext(rText);
                        view.setMenge(leistungsView.getMenge());
                        retVal.add(view);
                    }
                }
                return retVal;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String findRechnungstext(Long leistungNoOrig, String value, String language) throws FindException {
        try {
            Leistung leistung = findLeistung(leistungNoOrig);

            Long leistungNo = (leistung != null) ? leistung.getLeistungNo() : leistungNoOrig;
            String lang = (StringUtils.isNotBlank(language) ? language : BillingConstants.LANG_CODE_GERMAN);
            return leistungDAO.findRechnungstext(leistungNo, value, lang);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung> findLeistungen4Auftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) { return null; }
        try {
            // aktive AuftragsPositionen zum Auftrag ermitteln und anschliessend die Leistungen
            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            List<BAuftragPos> positionen = bas.findAuftragPositionen(auftragNoOrig, true);

            if (CollectionTools.isNotEmpty(positionen)) {
                List<Long> leistungNoOrigs = new ArrayList<>();
                Multimap<Long,String> leistungNoOrigsWithServiceValueParameter = ArrayListMultimap.create();
                List<Leistung> result = new ArrayList<>();
                
                for (BAuftragPos pos : positionen) {
                    if(StringUtils.isNotBlank(pos.getParameter())){
                        leistungNoOrigsWithServiceValueParameter.put(pos.getLeistungNoOrig(),pos.getParameter());
                    }else{
                        leistungNoOrigs.add(pos.getLeistungNoOrig());
                    }
                }

                if (!leistungNoOrigs.isEmpty()){
                    result.addAll(leistungDAO.findLeistungen(leistungNoOrigs));
                }
                // HUR-27991 Einträge in Werteliste (SERVICE_VALUE_PRICE) sollen auch mit berücksichtigt werden.
                for(Map.Entry entry: leistungNoOrigsWithServiceValueParameter.entries()){
                    Leistung leistung = leistungDAO.findLeistungen(Collections.singletonList((Long)entry.getKey())).get(0);
                    ServiceValue sv = findServiceValue((Long)entry.getKey(),(String)entry.getValue());
                    if ((sv != null) && sv.isProduktOrLeistung()) {
                        leistung.setName(sv.getValue());
                        leistung.setExternMiscNo(sv.getExternMiscNo());
                        leistung.setExternProduktNo(sv.getExternProduktNo());
                        leistung.setExternLeistungNo(sv.getExternLeistungNo());
                    }
                    result.add(leistung);
                }
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
    public Leistung findProductLeistung4Auftrag(Long ccAuftragId, String productType) throws FindException {
        if (ccAuftragId == null) { return null; }
        try {
            AuftragDaten ad = as.findAuftragDatenByAuftragId(ccAuftragId);
            if (ad == null) {
                throw new FindException(
                        "Produkt-Leistung konnte nicht ermittelt werden, da die Auftragsdaten nicht gefunden wurden!");
            }

            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            List<BAuftragLeistungView> views = bas.findAuftragLeistungViews4Auftrag(ad.getAuftragNoOrig(), false, true);
            if (CollectionTools.isNotEmpty(views)) {
                Long leistungNoOrig = null;
                List<Long> possibleExtProdNos = ps.findExtProdNos(ad.getProdId(), productType);
                if (CollectionTools.isNotEmpty(possibleExtProdNos)) {
                    leistungNoOrig = findMatchingExtProdNo(ad, views, possibleExtProdNos);
                }

                if ((leistungNoOrig == null) && StringUtils.equals(productType, ProduktMapping.MAPPING_PART_TYPE_PHONE)) {
                    // Sonderkonfiguration fuer Auftraege ohne explizite Phone-Leistung
                    possibleExtProdNos = ps.findExtProdNos(ad.getProdId(), ProduktMapping.MAPPING_PART_TYPE_PHONE_DSL);
                    if (CollectionTools.isNotEmpty(possibleExtProdNos)) {
                        leistungNoOrig = findMatchingExtProdNo(ad, views, possibleExtProdNos);
                    }
                }

                if (leistungNoOrig != null) {
                    return findLeistung(leistungNoOrig);
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

    /*
     * Ermittelt die passende EXT_PROD__NO
     * @param ad
     * @param views
     * @param leistungNoOrig
     * @param possibleExtProdNos
     * @return
     */
    private Long findMatchingExtProdNo(AuftragDaten ad, List<BAuftragLeistungView> views, List<Long> possibleExtProdNos) {
        Long leistungNoOrig = null;
        for (Long extProdNo : possibleExtProdNos) {
            // zutreffende/aktuelle Produkt-Leistung ermitteln
            for (BAuftragLeistungView view : views) {
                if (NumberTools.equal(view.getExternProduktNo(), extProdNo)) {
                    if (!ad.isAuftragActive() ||
                            StringUtils.equals(view.getAtyp(), BillingConstants.ATYP_KUEND)) {
                        // Auftrag in Kuendigung --> Leistung muss auch in Kuendigung sein
                        if (view.isAuftragPosInKuendigung()) {
                            leistungNoOrig = view.getLeistungNoOrig();
                            break;
                        }
                    }
                    else {
                        // Auftrag aktiv --> Leistung muss auch noch aktiv (ohne Kuendigungsdatum) sein
                        if ((view.getAuftragPosGueltigBis() == null) ||
                                DateTools.isDateEqual(
                                        view.getAuftragPosGueltigBis(), DateTools.getBillingEndDate())) {
                            leistungNoOrig = view.getLeistungNoOrig();
                            break;
                        }
                    }
                }
            }

            if (leistungNoOrig != null) {
                break;
            }
        }
        return leistungNoOrig;
    }

    @Override
    public int getUDRTarifType4Auftrag(Long auftragNoOrig, Date validDate) throws FindException {
        if ((auftragNoOrig == null) || (validDate == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            int udrTarifType = -1;
            List<Leistung> udrServices = leistungDAO.findUDRServices4Order(auftragNoOrig, validDate);
            if (CollectionTools.isNotEmpty(udrServices)) {
                for (Leistung udrService : udrServices) {
                    if (udrService.getBillingCode().startsWith(Leistung.LEISTUNG_BILLING_CODE_PREFIX_UDR_VOLUME)) {
                        // Leistung auf Volumen / Flat pruefen
                        List<ServiceBlockPrice> blockPrices =
                                leistungDAO.findServiceBlockPrices(udrService.getLeistungNo());
                        if (CollectionTools.isNotEmpty(blockPrices)) {
                            for (ServiceBlockPrice sbp : blockPrices) {
                                if ((sbp.getBlockPrice() != null) && (sbp.getBlockPrice() > 0)) {
                                    // Block-Price mit Preis vorhanden
                                    //    ---> volumen-basierter Tarif mit Abrechnung
                                    udrTarifType = Leistung.LEISTUNG_VOL_TYPE_VOLUME;
                                }
                            }
                        }

                        if (udrTarifType < 0) {
                            // keine Block-Prices oder kein Block-Price mit Preis > 0
                            //    ---> Flat-Tarif
                            udrTarifType = Leistung.LEISTUNG_VOL_TYPE_FLAT;
                        }
                    }
                    else if (udrService.getBillingCode().startsWith(Leistung.LEISTUNG_BILLING_CODE_PREFIX_UDR_TIME)
                            && (udrTarifType < Leistung.LEISTUNG_VOL_TYPE_TIME)) {
                        // zeit-basierter Volumentarif
                        udrTarifType = Leistung.LEISTUNG_VOL_TYPE_TIME;
                    }

                    if (udrTarifType == Leistung.LEISTUNG_VOL_TYPE_FLAT) {
                        break;
                    }
                }
            }

            return udrTarifType;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung> findLeistungen4OE(Long oeNoOrig, boolean onlyProdLeistungen4Extern) throws FindException {
        if (oeNoOrig == null) { return null; }
        try {
            Leistung example = new Leistung();
            example.setHistStatus(BillingConstants.HIST_STATUS_AKT);
            example.setOeNoOrig(oeNoOrig);

            List<Leistung> result = leistungDAO.queryByExample(example, Leistung.class);
            if (onlyProdLeistungen4Extern && CollectionTools.isNotEmpty(result)) {
                CollectionUtils.filter(result, value -> ((Leistung) value).getExternProduktNo() != null);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public ServiceValue findServiceValue(Long leistungNoOrig, String value) throws FindException {
        if ((leistungNoOrig == null) || StringUtils.isBlank(value)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            Leistung aktLeistung = leistungDAO.findLeistungByNoOrigWithoutLang(leistungNoOrig);
            if (aktLeistung != null) {
                return getServiceValueDAO().findServiceValue(aktLeistung.getLeistungNo(), value);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Float findVatRate(String vatCodeId) throws FindException {
        if (StringUtils.isEmpty(vatCodeId)) {
            return null;
        }

        try {
            return leistungDAO.findVatRate(vatCodeId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @return Returns the serviceValueDAO.
     */
    public ServiceValueDAO getServiceValueDAO() {
        return this.serviceValueDAO;
    }

    /**
     * @param serviceValueDAO The serviceValueDAO to set.
     */
    public void setServiceValueDAO(ServiceValueDAO serviceValueDAO) {
        this.serviceValueDAO = serviceValueDAO;
    }

}


