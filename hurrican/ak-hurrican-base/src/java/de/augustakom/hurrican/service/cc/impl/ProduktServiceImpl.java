/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 10:53:19
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import java.util.Map.*;
import javax.annotation.*;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DefaultDeleteDAO;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.LeitungsartDAO;
import de.augustakom.hurrican.dao.cc.PhysikTypDAO;
import de.augustakom.hurrican.dao.cc.ProduktDAO;
import de.augustakom.hurrican.dao.cc.ProduktViewDAO;
import de.augustakom.hurrican.dao.cc.SchnittstelleDAO;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Schnittstelle;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationType;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;
import de.augustakom.hurrican.model.shared.view.Billing2HurricanProdMapping;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Service-Implementierung von <code>ProduktService</code>
 *
 *
 */
@CcTxRequired
public class ProduktServiceImpl extends DefaultCCService implements ProduktService {

    private static final Logger LOGGER = Logger.getLogger(ProduktServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;

    private Object produktGruppeDAO = null;
    private SchnittstelleDAO schnittstelleDAO = null;
    private LeitungsartDAO leitungsartDAO = null;
    private ProduktViewDAO produktViewDAO = null;
    private PhysikTypDAO physikTypDAO = null;
    private Hibernate4DefaultDeleteDAO defaultDeleteDAO = null;

    private static volatile Map<Long, List<Long>> produktMappings = null;

    @Override
    public List<ProduktGruppe> findProduktGruppen() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getProduktGruppeDAO();
            return dao.findAll(ProduktGruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ProduktGruppe> findProduktGruppen4Hurrican() throws FindException {
        try {
            ProduktViewDAO dao = produktViewDAO;
            return dao.findProduktGruppen4Hurrican();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public ProduktGruppe findProduktGruppe(Long produktGruppeId) throws FindException {
        try {
            return ((FindDAO) getProduktGruppeDAO()).findById(produktGruppeId, ProduktGruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt> findProdukte(boolean onlyActual) throws FindException {
        try {
            return ((ProduktDAO) getDAO()).find(onlyActual);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Produkt findProdukt(Long produktId) throws FindException {
        if (produktId == null) { return null; }
        try {
            FindDAO dao = (FindDAO) getDAO();
            return dao.findById(produktId, Produkt.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Produkt findProdukt4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            Long prodId = findProduktId4Auftrag(auftragId);
            if (prodId == null) {
                return null;
            }
            else {
                return ((ProduktDAO) getDAO()).findById(prodId, Produkt.class);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Long findProduktId4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            CCAuftragService ccAS = getCCService(CCAuftragService.class.getName(), CCAuftragService.class);
            AuftragDaten ad = ccAS.findAuftragDatenByAuftragIdTx(auftragId);
            return (ad != null) ? ad.getProdId() : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String generateProduktName4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            Produkt produkt = findProdukt4Auftrag(auftragId);
            return generateProduktName4Auftrag(auftragId, produkt);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fuer den Auftrag " + auftragId +
                    " konnte der Produkt-Name nicht ermittelt werden.");
        }
    }

    @Override
    public String generateProduktName4Auftrag(Long auftragId, Produkt produkt) throws FindException {
        if ((auftragId == null) || (produkt == null)) { return null; }
        try {
            CCLeistungsService leistungsService = getCCService(CCLeistungsService.class);
            List<TechLeistung> techLeistungen = leistungsService.findTechLeistungen4Auftrag(auftragId, null, true);
            return generateProduktName(produkt, techLeistungen);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fuer den Auftrag " + auftragId +
                    " konnte der Produkt-Name nicht ermittelt werden.");
        }
    }


    /**
     * Generiert den Produktnamen aus Produkt und Technischen Leistungen
     */
    @Override
    public String generateProduktName(Produkt produkt, List<TechLeistung> techLeistungen) {
        String name = produkt.getProductNamePattern();
        if (StringUtils.isBlank(name)) {
            return produkt.getAnschlussart();
        }
        else {
            TechLeistung downstreamLeistung = filterTechLeistung(techLeistungen, TechLeistung.TYP_DOWNSTREAM);
            name = name.replace("{" + Produkt.PROD_NAME_PATTERN_DOWNSTREAM + "}",
                    downstreamLeistung != null ? downstreamLeistung.getProdNameStr() : "?");
            TechLeistung upstreamLeistung = filterTechLeistung(techLeistungen, TechLeistung.TYP_UPSTREAM);
            name = name.replace("{" + Produkt.PROD_NAME_PATTERN_UPSTREAM + "}",
                    upstreamLeistung != null ? upstreamLeistung.getProdNameStr() : "?");
            TechLeistung voipLeistung = filterTechLeistung(techLeistungen, TechLeistung.TYP_VOIP);
            name = name.replace("{" + Produkt.PROD_NAME_PATTERN_VOIP + "}",
                    voipLeistung != null ? voipLeistung.getProdNameStr() : "?");
            TechLeistung realVariante = filterTechLeistung(techLeistungen, TechLeistung.TYP_REALVARIANTE);
            name = name.replace("{" + Produkt.PROD_NAME_PATTERN_REALVARIANTE + "}",
                    realVariante != null ? realVariante.getProdNameStr() : "");
            return name;
        }
    }


    /**
     * Filtert TechLeistungen, so dass genau eine uebrig bleibt, oder {@code null}
     *
     * @param techLeistungen falls {@code null}, ist das Resultat auch {@code null}
     */
    private TechLeistung filterTechLeistung(List<TechLeistung> techLeistungen, String typ) {
        TechLeistung result = null;
        if (techLeistungen == null) {
            return result;
        }
        for (TechLeistung techLeistung : techLeistungen) {
            if (typ.equals(techLeistung.getTyp())) {
                if (result != null) {
                    return null;
                }
                result = techLeistung;
            }
        }
        return result;
    }


    @Override
    public ProduktGruppe findPG4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            return getProduktViewDAO().findPG4Auftrag(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt> findProdukte4PGAndHurrican(Long produktGruppeId) throws FindException {
        if (produktGruppeId == null) { return null; }
        try {
            ByExampleDAO dao = (ByExampleDAO) getDAO();
            Produkt example = new Produkt();
            example.setProduktGruppeId(produktGruppeId);
            example.setAuftragserstellung(Boolean.TRUE);

            return dao.queryByExample(example, Produkt.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt> findProdukte4PGs(Long... produktGruppenIds) throws FindException {
        if ((produktGruppenIds == null) || (produktGruppenIds.length <= 0)) {
            return Collections.emptyList();
        }
        try {
            List<Produkt> produkte4PGs = new ArrayList<>();
            ByExampleDAO dao = (ByExampleDAO) getDAO();
            Produkt example = new Produkt();
            for (Long produktGruppeId : produktGruppenIds) {
                example.setProduktGruppeId(produktGruppeId);
                List<Produkt> produkte4PG = dao.queryByExample(example, Produkt.class);
                produkte4PGs.addAll(produkte4PG);
            }
            return produkte4PGs;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt> findChildProdukte(Long parentProdId) throws FindException {
        if (parentProdId == null) { return null; }
        try {
            Produkt2PhysikTypQuery query = new Produkt2PhysikTypQuery();
            query.setProduktId(parentProdId);

            List<Produkt2PhysikTyp> p2pts = getPhysikTypDAO().findP2PTsByQuery(query);
            if ((p2pts == null) || (p2pts.isEmpty())) {
                throw new FindException("Die moeglichen Child-Produkte konnten nicht ermittelt werden!");
            }

            List<Long> parentPhysikTypen = new ArrayList<>();
            for (Produkt2PhysikTyp p2pt : p2pts) {
                parentPhysikTypen.add(p2pt.getPhysikTypId());
            }

            // Child-Produkte ermitteln
            return ((ProduktDAO) getDAO()).findByParentPhysikTypIds(parentPhysikTypen);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveProdukt(Produkt toSave) throws StoreException, ValidationException {
        ValidationException ve = new ValidationException(toSave, "Produkt");
        getValidator().validate(toSave, ve);
        if (ve.hasErrors()) {
            throw ve;
        }

        try {
            StoreDAO dao = (StoreDAO) getDAO();
            dao.store(toSave);
            toSave.notifyObservers(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveProduktGruppe(ProduktGruppe toSave) throws StoreException {
        try {
            StoreDAO dao = (StoreDAO) getDAO();
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Schnittstelle> findSchnittstellen() throws FindException {
        try {
            return getSchnittstelleDAO().findAll(Schnittstelle.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Schnittstelle findSchnittstelle(Long id) throws FindException {
        if (id == null) { return null; }
        try {
            return getSchnittstelleDAO().findById(id, Schnittstelle.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Produkt2Schnittstelle> findSchnittstellenMappings4Produkt(Long produktId) throws FindException {
        try {
            SchnittstelleDAO dao = getSchnittstelleDAO();
            return (List<Produkt2Schnittstelle>) dao.findSchnittstellenMappings4Produkt(produktId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Schnittstelle> findSchnittstellen4Produkt(Long produktId) throws FindException {
        try {
            SchnittstelleDAO dao = getSchnittstelleDAO();
            return dao.findSchnittstellen4Produkt(produktId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveSchnittstellen4Produkt(Long produktId, Collection<Long> schnittstellenIds) throws StoreException {
        // Transaction ueber Spring konfiguriert
        try {
            SchnittstelleDAO dao = getSchnittstelleDAO();
            dao.deleteSchnittstellen4Produkt(produktId);

            List<Produkt2Schnittstelle> relations = new ArrayList<>();
            for (Long sId : schnittstellenIds) {
                Produkt2Schnittstelle p2s = new Produkt2Schnittstelle();
                p2s.setProduktId(produktId);
                p2s.setSchnittstelleId(sId);
                relations.add(p2s);
            }
            dao.saveSchnittstellen4Produkt(relations);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leitungsart> findLeitungsarten() throws FindException {
        try {
            return getLeitungsartDAO().findAll(Leitungsart.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Produkt2Produkt findProdukt2Produkt(Long physikaendTyp, Long prodIdSrc, Long prodIdDest) throws FindException {
        if ((physikaendTyp < 0) || (prodIdSrc == null) || (prodIdDest == null)) { return null; }
        try {
            Produkt2Produkt example = new Produkt2Produkt();
            example.setPhysikaenderungsTyp(physikaendTyp);
            example.setProdIdSrc(prodIdSrc);
            example.setProdIdDest(prodIdDest);

            List<Produkt2Produkt> result = ((ByExampleDAO) getDAO()).queryByExample(example, Produkt2Produkt.class);
            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt2Produkt> findProdukt2Produkte(Long prodIdSrc) throws FindException {
        if (prodIdSrc == null) { return null; }
        try {
            return ((ProduktDAO) getDAO()).findProdukt2Produkte(prodIdSrc);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt2Produkt> findProdukt2Produkt(Long prodIdSrc, Long prodIdDest) throws FindException {
        if ((prodIdSrc == null) || (prodIdDest == null)) { return null; }
        try {
            Produkt2Produkt example = new Produkt2Produkt();
            example.setProdIdSrc(prodIdSrc);
            example.setProdIdDest(prodIdDest);

            return ((ProduktDAO) getDAO()).queryByExample(example, Produkt2Produkt.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt2Produkt> findProdukt2ProdukteByDest(Long prodIdDest) throws FindException {
        if (prodIdDest == null) { return null; }
        try {
            return ((ProduktDAO) getDAO()).findProdukt2ProdukteByDest(prodIdDest);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveProdukt2Produkt(Produkt2Produkt toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            ((ProduktDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteProdukt2Produkt(Long p2pId) throws DeleteException {
        if (p2pId == null) { throw new DeleteException(DeleteException.INVALID_PARAMETERS); }
        try {
            ((ProduktDAO) getDAO()).deleteProd2Prod(p2pId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ProduktMapping> findProduktMappings() throws FindException {
        try {
            return ((ProduktDAO) getDAO()).findAll(ProduktMapping.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ProduktMapping> findProduktMappings(Long prodId, String mappingPartType) throws FindException {
        if (prodId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            return ((ProduktDAO) getDAO()).findProduktMappings(prodId, mappingPartType);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Long> findExtProdNos(Long prodId, String mappingPartType) throws FindException {
        if (prodId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            List<ProduktMapping> mappings =
                    ((ProduktDAO) getDAO()).findProduktMappings(prodId, mappingPartType);

            if (CollectionTools.isNotEmpty(mappings)) {
                List<Long> result = new ArrayList<>();
                for (ProduktMapping pm : mappings) {
                    Long extProdNo = pm.getExtProdNo();
                    if (!result.contains(extProdNo)) {
                        result.add(extProdNo);
                    }
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

    /**
     * Ermittelt zu einer Produkt-Mapping Gruppe die Hurrican Produkt-ID.
     *
     * @param mappingGroup
     * @return
     *
     */
    Long findProdId4MappingGroup(Long mappingGroup) throws FindException {
        List<ProduktMapping> mappings = findProduktMappings();
        for (ProduktMapping pm : mappings) {
            if (NumberTools.equal(pm.getMappingGroup(), mappingGroup)) {
                return pm.getProdId();
            }
        }
        return null;
    }

    /**
     * Erstellt aus den geladenen Produkt-Mappings eine Map, in der zur Hurrican Mapping-Gruppe-ID alle notwendigen
     * EXT_PROD__NOs aus dem Billing-System zugeordnet sind. <br> Map-Key: ID der Mapping-Gruppe <br> Map-Value: Liste
     * mit allen notwendigen EXT_PROD__NOs aus dem Billing-System
     */
    Map<Long, List<Long>> getHurrican2BillingProdMappings() throws FindException {
        // Double-check idiom for lazy initialization
        Map<Long, List<Long>> result = produktMappings;
        if (result == null) {
            synchronized (ProduktServiceImpl.class) {
                result = produktMappings;
                if (result == null) {
                    List<ProduktMapping> mappings = findProduktMappings();

                    result = new HashMap<>();
                    for (ProduktMapping pm : mappings) {
                        List<Long> prodExtNOs;
                        if (result.containsKey(pm.getMappingGroup())) {
                            prodExtNOs = result.get(pm.getMappingGroup());
                        }
                        else {
                            prodExtNOs = new ArrayList<>();
                            result.put(pm.getMappingGroup(), prodExtNOs);
                        }
                        prodExtNOs.add(pm.getExtProdNo());
                    }
                    produktMappings = result;
                }
            }
        }
        return produktMappings;
    }

    @Override
    public List<Billing2HurricanProdMapping> doProductMapping(List<BAuftragLeistungView> views) throws FindException {
        List<Billing2HurricanProdMapping> result = new ArrayList<>();

        // Map mit Listen der moeglichen Produkt-Mappings erstellen
        Map<Long, List<Long>> pmGroup2ExtProdNos = getHurrican2BillingProdMappings();
        // Liste mit ExtProdNos erstellen
        Map<Long, List<Long>> auftrag2ExtProdNos = createExternProdNoList(views);

        for (final Entry<Long, List<Long>> entry : auftrag2ExtProdNos.entrySet()) {
            Long auftragNoOrig = entry.getKey();
            List<Long> extProdNos = entry.getValue();
            // aus der Liste 'extProdNos' evtl. mehrere Listen erstellen
            //  --> in einer Liste darf eine EXT_PROD__NO nur einmal vorkommen!
            //  (Liste mit '100,100,102' ergibt eine Liste mit '100,102' und eine mit '100')
            List<List<Long>> splitted = new ArrayList<>();
            splitExtProdNOs2Lists(extProdNos, splitted);

            createBilling2HurricanMappings(views, result, pmGroup2ExtProdNos, auftragNoOrig, splitted);
        }

        return result;
    }

    /**
     * Erstellt aus den Billing-Views fuer jeden Auftrag eine Liste mit den gefunden EXT_PROD__NOs (bei Menge>1 werden
     * die EXT_PROD__NOs mehrfach aufgenommen). Key: Auftragsnummer (Taifun); Value: Liste mit den zugehoerigen
     * ExtProdNos
     */
    Map<Long, List<Long>> createExternProdNoList(List<BAuftragLeistungView> views) {
        Map<Long, List<Long>> auftrag2ExtProdNos = new HashMap<>();
        for (BAuftragLeistungView view : views) {
            if (view.getExternProduktNo() != null) {
                List<Long> extProdNosList;
                if (auftrag2ExtProdNos.containsKey(view.getAuftragNoOrig())) {
                    extProdNosList = auftrag2ExtProdNos.get(view.getAuftragNoOrig());
                }
                else {
                    extProdNosList = new ArrayList<>();
                    auftrag2ExtProdNos.put(view.getAuftragNoOrig(), extProdNosList);
                }

                for (long i = 0; i < view.getMenge(); i++) {
                    extProdNosList.add(view.getExternProduktNo());
                }
            }
        }
        return auftrag2ExtProdNos;
    }

    /**
     * Ermittelt aus den angegebenen ExtProdNo-Kombinationen die zugehoerigen Billing2HurricanProdMapping Objekte und
     * ordnet diese dem {@code result} Objekt zu.
     */
    void createBilling2HurricanMappings(List<BAuftragLeistungView> views,
            List<Billing2HurricanProdMapping> result, Map<Long, List<Long>> pmGroup2ExtProdNos, Long auftragNoOrig,
            List<List<Long>> splitted) throws FindException {
        for (List<Long> extProdNOList2Search : splitted) {
            List<Long> matchingMappingGroups = new ArrayList<>();
            for (Entry<Long, List<Long>> entry : pmGroup2ExtProdNos.entrySet()) {
                // pruefen, ob alle Elemente von pmExtProdNOs und extProdNOList2Search uebereinstimmen
                if (CollectionUtils.isEqualCollection(entry.getValue(), extProdNOList2Search)) {
                    matchingMappingGroups.add(entry.getKey());
                }
            }

            if (CollectionTools.isEmpty(matchingMappingGroups)) {
                // keine Uebereinstimmung gefunden --> versuchen, die Liste mit den
                // EXT_PROD__NOs in einzelne Teile zu zerlegen und mit diesen nach
                // entsprechenden Hurrican Produkten suchen.
                for (Long extProdNo : extProdNOList2Search) {
                    Long matchingMappingGroup = null;
                    for (Entry<Long, List<Long>> pmGroupSubEntry : pmGroup2ExtProdNos.entrySet()) {
                        List<Long> pmExtProdNOs = pmGroupSubEntry.getValue();
                        if ((pmExtProdNOs != null)
                                && (pmExtProdNOs.size() == 1)
                                && NumberTools.equal(pmExtProdNOs.get(0), extProdNo)) {
                            matchingMappingGroup = pmGroupSubEntry.getKey();
                            break;
                        }
                    }

                    if (matchingMappingGroup != null) {
                        Long prodId = findProdId4MappingGroup(matchingMappingGroup);
                        result.add(createBilling2HurricanMapping(auftragNoOrig, prodId, views));
                    }
                }
            }
            else {
                Long prodId = filterBestProduktId(auftragNoOrig, matchingMappingGroups);
                result.add(createBilling2HurricanMapping(auftragNoOrig, prodId, views));
            }
        }
    }

    /**
     * Ermittelt die "beste" Hurrican Produkt-ID fuer den Taifun Auftrag an Hand der angegebenen Produkt-Mappings. Logik
     * wie folgt: - Produkte auf Grund von Prioritaet aus ProduktMapping sortieren - Produkt2TechLocation mit
     * GeoId2TechLocation vergleichen und Produkte somit filtern - falls weiterhin mehr als ein Produkt moeglich:
     * hoechst priorisiertes Produkt liefern
     *
     * @param auftragNoOrig
     * @param mappingGroups
     * @return
     */
    Long filterBestProduktId(Long auftragNoOrig, List<Long> mappingGroups) {
        try {
            List<Long> prodIds = findProduktIdsSortedByPrio(mappingGroups);
            if (CollectionTools.isNotEmpty(prodIds)) {
                if (prodIds.size() > 1) {
                    Adresse address = billingAuftragService.findAnschlussAdresse4Auftrag(auftragNoOrig, Endstelle.ENDSTELLEN_TYP_B);
                    List<GeoId2TechLocation> geoId2TechLocations = availabilityService.findGeoId2TechLocations(address.getGeoId());

                    for (Long prodId : prodIds) {
                        if (isProductPossibleAtGeoId(geoId2TechLocations, prodId)) {
                            return prodId;
                        }
                    }
                }
                else {
                    return prodIds.get(0);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }


    @Override
    public List<Long> findProduktIdsSortedByPrio(List<Long> mappingGroups) {
        return ((ProduktDAO) getDAO()).findProdIdsForProdMappings(mappingGroups);
    }


    /**
     * Ermittelt die Standort-Typen der GeoID und vergleicht diese mit den moeglichen Standort-Typen fuer das Produkt.
     * Sofern min. ein Match der Standort-Typen gefunden wurde, so kann das Produkt realisiert werden.
     *
     * @param geoId2TechLocations
     * @param produktId
     * @return
     */
    boolean isProductPossibleAtGeoId(List<GeoId2TechLocation> geoId2TechLocations, Long produktId) {
        try {
            List<Produkt2TechLocationType> produkt2TechLocationTypes = findProdukt2TechLocationTypes(produktId);
            for (GeoId2TechLocation geoId2TechLocation : geoId2TechLocations) {
                HVTStandort hvtStandort = hvtService.findHVTStandort(geoId2TechLocation.getHvtIdStandort());

                for (Produkt2TechLocationType prod2TechLocationType : produkt2TechLocationTypes) {
                    if (NumberTools.equal(hvtStandort.getStandortTypRefId(), prod2TechLocationType.getTechLocationTypeRefId())) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Erstellt aus den uebergebenen Parametern ein Objekt vom Typ <code>Billing2HurricanProdMapping</code>.
     *
     * @param auftragNoOrig (original) Auftragsnummer aus dem Billing-System
     * @param prodId        resultierende Produkt-ID
     * @param views         Leistungs-Views des Billing-Auftrags
     * @return
     */
    Billing2HurricanProdMapping createBilling2HurricanMapping(Long auftragNoOrig,
            Long prodId, List<BAuftragLeistungView> views) {
        Billing2HurricanProdMapping view = new Billing2HurricanProdMapping();
        view.setAuftragNoOrig(auftragNoOrig);
        view.setProdId(prodId);

        for (BAuftragLeistungView balv : views) {
            if (balv.getAuftragNoOrig().equals(auftragNoOrig)) {
                view.setOeName(balv.getOeName());
                view.setBundleOrderNo(balv.getBundleOrderNo());
                break;
            }
        }
        return view;
    }

    /**
     * Teilt die Eintraege aus 'extProdNOs' in mehrere Listen auf. In jeder Liste darf jedes Element nur einmal
     * vorhanden sein. <br> Ein Element aus 'extProdNOs', das einer Liste zugeordnet wurde, darf keiner weiteren Liste
     * mehr zugeordnet werden. Bsp.: extProdNOs: 100,100,200 --> Liste mit 100,200 und Liste mit 100
     *
     * @param extProdNOs
     * @param splitted
     *
     */
    void splitExtProdNOs2Lists(List<Long> extProdNOs, List<List<Long>> splitted) {
        for (Long epn : extProdNOs) {
            boolean added = false;
            for (List<Long> split : splitted) {
                if (!split.contains(epn)) {
                    split.add(epn);
                    added = true;
                    break;
                }
            }

            if (!added) {
                List<Long> epns = new ArrayList<>();
                epns.add(epn);
                splitted.add(epns);
            }
        }
    }

    @Override
    public Produkt doProduktMapping4AuftragNo(Long auftragNo) throws FindException {
        if (auftragNo == null) {
            return null;
        }
        try {
            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            List<BAuftragLeistungView> views = bas.findAuftragLeistungViews4Auftrag(auftragNo, false, true);

            List<Billing2HurricanProdMapping> mappings = doProductMapping(views);

            if ((mappings != null) && ((mappings.size() == 1) || noDifferentMappingsFound(mappings))) {
                return findProdukt(mappings.get(0).getProdId());
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new FindException(e);
        }
    }

    /**
     * Ueberprueft, ob in den Mappings immer die gleiche Kombination aus OE__NO und ProdId eingetragen ist oder
     * mehrere verschiedene.
     * (Die gleichen Mappings koennen z.B. dann entstehen, wenn in Taifun eine Menge >1 auf der Position eingetragen
     * ist.)
     * @param mappings
     * @return {@code true} wenn in den Mappings mehr als eine Kombination von OE__NO/ProdId enthalten ist
     */
    private boolean noDifferentMappingsFound(final List<Billing2HurricanProdMapping> mappings) {
        Set<Pair<Long, Long>> prodIdWithOeNoOrig = Sets.newHashSet();
        for (final Billing2HurricanProdMapping mapping : mappings) {
            prodIdWithOeNoOrig.add(Pair.create(mapping.getOeNoOrig(), mapping.getProdId()));
        }
        return prodIdWithOeNoOrig.size() == 1;
    }

    @Override
    public Boolean isVierDrahtProdukt(Long prodId) throws FindException {
        if (prodId == null) {
            return Boolean.FALSE;
        }
        try {
            Produkt prod = findProdukt(prodId);
            if (prod != null) {
                return prod.getIsVierDraht();
            }
            return Boolean.FALSE;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new FindException(e);
        }
    }

    @Override
    public List<Produkt2TechLocationType> findProdukt2TechLocationTypes(Long prodId) throws FindException {
        try {
            Produkt2TechLocationType example = new Produkt2TechLocationType();
            example.setProduktId(prodId);

            return ((ByExampleDAO) getDAO()).queryByExample(example, Produkt2TechLocationType.class,
                    new String[] { Produkt2TechLocationType.PRIORITY }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt> findProductsByTechLeistungType(String... techLeistungTypes) throws FindException {
        try {
            return ((ProduktDAO) getDAO()).findProductsByTechLeistungType(techLeistungTypes);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveProdukt2TechLocationTypes(Long produktId, List<Produkt2TechLocationType> listToSave,
            Long sessionId)
            throws StoreException {
        if (produktId == null) {
            throw new StoreException(StoreException.INVALID_PRODUCT_ID);
        }

        try {
            String userName = getUserNameAndFirstNameSilent(sessionId);
            int priority = 0;

            List<Produkt2TechLocationType> listToDelete = findProdukt2TechLocationTypes(produktId);
            // alle existierenden Objekte loeschen, da sonst UNIQUE CONSTRAINTS der DB beim erstellen der
            // prioritaet verletzten werden koennten!
            if (CollectionTools.isNotEmpty(listToDelete)) {
                for (Produkt2TechLocationType toDelete : listToDelete) {
                    defaultDeleteDAO.delete(toDelete);
                }
            }
            // Geloeschte Entitaeten flushen, da andernfalls Hibernate mit den Insert Statements weiter unten
            // und den UNIQUE CONSTRAINTS in Konflikt kommt. Hibernate setzt die 'delete from' Statements
            // zu spaet (nach den 'insert' Statements?) ab. Dadurch der Konflikt...
            defaultDeleteDAO.flushSession();

            // Liste speichern
            if (CollectionTools.isNotEmpty(listToSave)) {
                for (Produkt2TechLocationType toSave : listToSave) {
                    // Daten vorbereiten, pruefen
                    if (toSave.getTechLocationTypeRefId() == null) {
                        throw new StoreException("Die TechLocationTypeRefId muss gesetzt sein!");
                    }
                    if (toSave.getProduktId() == null) {
                        toSave.setProduktId(produktId);
                    }
                    else {
                        if (!toSave.getProduktId().equals(produktId)) {
                            throw new StoreException(
                                    "Alle Eintraege in der List muessen sich auf das angegebene Produkt beziehen!");
                        }
                    }

                    toSave.setPriority(++priority);
                    toSave.setDateW(new Date());
                    toSave.setUserW(userName);
                    toSave.setVersion(null);
                    toSave.setId(null);

                    ((ProduktDAO) getDAO()).store(toSave);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }
    /* ****************** DAOs und Validators *************** */


    private Object getProduktGruppeDAO() {
        return produktGruppeDAO;
    }

    public void setProduktGruppeDAO(Object produktGruppeDAO) {
        this.produktGruppeDAO = produktGruppeDAO;
    }

    private SchnittstelleDAO getSchnittstelleDAO() {
        return schnittstelleDAO;
    }

    public void setSchnittstelleDAO(SchnittstelleDAO schnittstelleDAO) {
        this.schnittstelleDAO = schnittstelleDAO;
    }

    private ProduktViewDAO getProduktViewDAO() {
        return produktViewDAO;
    }

    public void setProduktViewDAO(ProduktViewDAO produktViewDAO) {
        this.produktViewDAO = produktViewDAO;
    }

    private LeitungsartDAO getLeitungsartDAO() {
        return leitungsartDAO;
    }

    public void setLeitungsartDAO(LeitungsartDAO leitungsartDAO) {
        this.leitungsartDAO = leitungsartDAO;
    }

    private PhysikTypDAO getPhysikTypDAO() {
        return physikTypDAO;
    }

    public void setPhysikTypDAO(PhysikTypDAO physikTypDAO) {
        this.physikTypDAO = physikTypDAO;
    }

    public void setDefaultDeleteDAO(Hibernate4DefaultDeleteDAO defaultDeleteDAO) {
        this.defaultDeleteDAO = defaultDeleteDAO;
    }

}


