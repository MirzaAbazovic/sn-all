/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 09:45:52
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.DNLeistungDAO;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.model.cc.view.LeistungInLeistungsbuendelView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Implementierung von <code>CCRufnummernService</code>.
 */
@CcTxRequired
public class CCRufnummernServiceImpl extends DefaultCCService implements CCRufnummernService {

    private static final Logger LOGGER = Logger.getLogger(CCRufnummernService.class);

    private AuftragDatenDAO auftragDatenDAO = null;

    @Resource(name = "de.augustakom.hurrican.service.billing.LeistungService")
    private LeistungService leistungService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService;
    @Autowired
    private DNLeistungDAO dnLeistungDao;

    @Override
    public List<DNLeistungsView> findDNLeistungen(List<Long> dnNos, Long leistungsbuendelId) throws FindException {
        if ((dnNos == null) || (dnNos.isEmpty())) {
            return null;
        }
        try {
            return ((DNLeistungDAO) getDAO()).findLeistungViews(dnNos, leistungsbuendelId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistungsbuendel2Produkt> findLeistungsbuendel2Produkt(Long prodLeistungNoOrig) throws FindException {
        try {
            return ((DNLeistungDAO) getDAO()).findLeistungsbuendel2Produkt(prodLeistungNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Leistungsbuendel findLeistungsbuendel4Auftrag(Long ccAuftragId) throws FindException {
        try {
            return findLeistungsbuendel4Auftrag(ccAuftragId, leistungService);
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
    public Leistungsbuendel findLeistungsbuendel4Auftrag(Long ccAuftragId, LeistungService leistungService)
            throws FindException {
        try {
            AuftragDaten ad = auftragService.findAuftragDatenByAuftragIdTx(ccAuftragId);

            Leistung leistung = leistungService.findProductLeistung4Auftrag(ccAuftragId,
                    ProduktMapping.MAPPING_PART_TYPE_PHONE);
            if (leistung != null) {
                List<Leistungsbuendel2Produkt> result =
                        ((DNLeistungDAO) getDAO()).findLeistungsbuendel2Produkt(leistung.getLeistungNoOrig());
                if (CollectionTools.isNotEmpty(result)) {
                    Leistungsbuendel2Produkt matchingL2P = null;
                    for (Leistungsbuendel2Produkt l2p : result) {
                        if (l2p.getProtokollLeistungNoOrig() != null) {
                            // pruefen, ob dem Auftrag die Protokoll-Leistung zugeordnet ist
                            List<Leistung> leistungen = leistungService.findLeistungen4Auftrag(ad.getAuftragNoOrig());
                            if (CollectionTools.isNotEmpty(leistungen)) {
                                for (Leistung l : leistungen) {
                                    if (NumberTools.equal(l2p.getProtokollLeistungNoOrig(), l.getLeistungNoOrig())) {
                                        matchingL2P = l2p;
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            // Leistungsbuendel beachtet keine Protokoll-Leistung
                            matchingL2P = l2p;
                        }

                        if (matchingL2P != null) {
                            break;
                        }
                    }

                    if (matchingL2P != null) {
                        return ((DNLeistungDAO) getDAO())
                                .findById(matchingL2P.getLbId(), Leistungsbuendel.class);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return null;
    }

    @Override
    public void saveLeistungsbuendel2Produkt(Long productOeNoOrig, Long leistungNoOrig, Collection<Long> oeNos)
            throws StoreException {
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            dao.deleteLeistungsbuendel2Produkt(productOeNoOrig, leistungNoOrig);
            List<Leistungsbuendel2Produkt> relations = new ArrayList<>();
            for (Long oeNo : oeNos) {
                Leistungsbuendel2Produkt d2p = new Leistungsbuendel2Produkt();
                d2p.setProductOeNo(productOeNoOrig);
                d2p.setLeistungNoOrig(leistungNoOrig);
                d2p.setLbId(oeNo);
                relations.add(d2p);
            }
            dao.saveLeistungsbuendel2Produkt(relations);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung4Dn> findLeistungen() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getDAO();
            return dao.findAll(Leistung4Dn.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveLb2Leistung(Lb2Leistung lb2Leistung) throws StoreException {
        if (lb2Leistung != null) {
            try {
                DNLeistungDAO dao = (DNLeistungDAO) getDAO();
                dao.store(lb2Leistung);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
            }
        }
    }

    @Override
    public List<LeistungParameter> findAllParameter() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getDAO();
            return dao.findAll(LeistungParameter.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung2Parameter> findLeistung2Parameter(Long leistungId) throws FindException {
        if (leistungId == null) {
            return null;
        }
        try {
            return ((DNLeistungDAO) getDAO()).findLeistung2Parameter(leistungId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveLeistung2Parameter(Long leistungId, Collection<Long> parameterIds) throws StoreException {
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            dao.deleteLeistung2Parameter(leistungId);
            List<Leistung2Parameter> relations = new ArrayList<>();
            for (Long pId : parameterIds) {
                Leistung2Parameter l2p = new Leistung2Parameter();
                l2p.setLeistungId(leistungId);
                l2p.setParameterId(pId);
                relations.add(l2p);
            }
            dao.saveLeistung2Parameter(relations);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveLeistungParameter(LeistungParameter leistungParameter) throws StoreException {
        if (leistungParameter != null) {
            try {
                DNLeistungDAO dao = (DNLeistungDAO) getDAO();
                dao.store(leistungParameter);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
            }
        }
    }

    @Override
    public void saveDNLeistung(Leistung4Dn leistung4Dn) throws StoreException {
        if (leistung4Dn != null) {
            try {
                DNLeistungDAO dao = (DNLeistungDAO) getDAO();
                dao.store(leistung4Dn);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
            }
        }
    }

    @Override
    public List<Leistungsbuendel> findLeistungsbuendel() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getDAO();
            return dao.findAll(Leistungsbuendel.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung4Dn> findDNLeistungen4Buendel(Long lbId) throws FindException {
        if (lbId == null) {
            return null;
        }
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            return dao.findDNLeistungen4Buendel(lbId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Lb2Leistung> findLeistungsbuendelKonfig(Long lbId, boolean onlyDefault) throws FindException {
        if (lbId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            Lb2Leistung exampleLb2L = new Lb2Leistung();
            exampleLb2L.setLbId(lbId);
            if (onlyDefault) {
                exampleLb2L.setStandard(Boolean.TRUE);
            }

            return dao.queryByExample(exampleLb2L, Lb2Leistung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<LeistungParameter> findSignedParameter2Leistung(Long lId) throws FindException {
        if (lId == null) {
            return Collections.emptyList();
        }
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            return dao.findSignedParameter2Leistung(lId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveLeistung2DN(Rufnummer rufnummer, Long leistungId, String parameterText,
            boolean defaultLeistungen, Long leistungsbuendelId, Date realDate, Long leistungParamId,
            Long sessionId) throws StoreException {
        if (rufnummer == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        if (defaultLeistungen) {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            List<Leistung4Dn> standardL =
                    dao.findDefaultLeistungen4Buendel(leistungsbuendelId);

            if (CollectionTools.isNotEmpty(standardL)) {
                for (Leistung4Dn l4Dn : standardL) {
                    Long tmpLsParamId = null;
                    String paramVal = null;
                    Lb2Leistung lb2L = dao.findLb2Leistung(l4Dn.getId(), leistungsbuendelId);
                    if (lb2L != null) {
                        // Default-Parameter ermitteln
                        paramVal = lb2L.getDefParamValue();
                    }

                    if (StringUtils.isNotBlank(paramVal)) {
                        // Parameter-ID ermitteln
                        List<Leistung2Parameter> l2ps = dao.findLeistung2Parameter(l4Dn.getId());
                        if (CollectionTools.isNotEmpty(l2ps)) {
                            // Index 0 verwenden, da pro DN-Leistung immer nur
                            // max. ein Parameter verwendet werden kann
                            tmpLsParamId = l2ps.get(0).getParameterId();
                        }
                    }

                    saveOneLeistung4Dn(l4Dn.getId(), leistungsbuendelId,
                            paramVal, rufnummer, realDate, tmpLsParamId, sessionId);
                }
            }
            else {
                if (NumberTools.equal(Rufnummer.OE__NO_DEFAULT, rufnummer.getOeNoOrig())) {
                    // Bei Rufnummerntyp 'Standard' werden zwingend Rufnummernleistungen benoetigt.
                    throw new StoreException("Zu dem Produkt sind keine Standard-Rufnummernleistungen konfiguriert!");
                }
            }
        }
        else {
            if (leistungId != null) {
                saveOneLeistung4Dn(leistungId, leistungsbuendelId,
                        parameterText, rufnummer, realDate, leistungParamId, sessionId);
            }
        }
    }

    /*
     * Speichert die Leistung zur Rufnummer
     *
     * @param leistungId
     *
     * @param leistungsbuendelId
     *
     * @param paramterText
     *
     * @param rufnummer
     *
     * @param realDate
     *
     * @param leistungParamId
     *
     * @param sessionId
     *
     * @throws StoreException
     */
    private void saveOneLeistung4Dn(Long leistungId, Long leistungsbuendelId, String paramterText,
            Rufnummer rufnummer, Date realDate, Long leistungParamId, Long sessionId) throws StoreException {
        try {
            AKUser user = getAKUserBySessionIdSilent(sessionId);

            Leistung2DN l2d = new Leistung2DN();
            l2d.setDnNo(rufnummer.getDnNo());
            l2d.setLbId(leistungsbuendelId);
            l2d.setLeistung4DnId(leistungId);
            l2d.setParameterId(leistungParamId);

            // pruefen, ob die Leistung bereits auf der Rufnummer vorhanden und aktiv ist
            List<Leistung2DN> lV = findLeistung2DnByExample(l2d);
            if (lV != null) {
                for (Leistung2DN lVview : lV) {
                    if (DateTools.isDateAfter(lVview.getScvKuendigung(), realDate) ||
                            (lVview.getScvKuendigung() == null)) {
                        throw new StoreException(StoreException.ERROR_DN_SERVICE_EXISTS, new Object[] { lVview.getId() });
                    }
                }
            }

            // TODO: Validator aufnehmen (bisher werden die Methoden-Parameter
            // in der GUI ueberprueft - sollte aber bei Gelegenheit in Validator wandern)

            // restliche Parameter erst hier setzen, da das Objekt zuvor als Example verwendet wird!
            l2d.setLeistungParameter(paramterText);
            l2d.setScvRealisierung(realDate);
            l2d.setScvUserRealisierung((user != null) ? user.getName() : HurricanConstants.UNKNOWN);

            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            dao.store(l2d);
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
    public List<Leistung2DN> findLeistung2DnByExample(Leistung2DN example) throws FindException {
        if (example == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            return dao.queryByExample(example, Leistung2DN.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung2DN> findDNLeistungen4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) {
            return null;
        }
        try {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
            if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
                Leistungsbuendel leistungsbuendel = findLeistungsbuendel4Auftrag(auftragDaten.getAuftragId());
                List<Rufnummer> result = rufnummerService.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                        new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.TRUE, Boolean.TRUE });
                if (result != null) {
                    Leistung2DN example = new Leistung2DN();
                    List<Leistung2DN> leistungen = new ArrayList<>();
                    for (Rufnummer rn : result) {
                        example.setDnNo(rn.getDnNo());
                        if (leistungsbuendel != null) {
                            example.setLbId(leistungsbuendel.getId());
                        }

                        List<Leistung2DN> l2dn = ((DNLeistungDAO) getDAO()).queryByExample(example, Leistung2DN.class);
                        leistungen.addAll(l2dn);
                    }
                    return leistungen;
                }
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung2DN> findLeistung2DN(Long dnNo, List<Long> dnServiceIDs, Date validDate) throws FindException {
        if ((dnNo == null) || CollectionTools.isEmpty(dnServiceIDs) || (validDate == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            Leistung2DN example = new Leistung2DN();
            example.setDnNo(dnNo);
            List<Leistung2DN> leistung2DNs = ((DNLeistungDAO) getDAO()).queryByExample(example, Leistung2DN.class);

            if (CollectionTools.isNotEmpty(leistung2DNs)) {
                List<Leistung2DN> result = new ArrayList<>();
                for (Leistung2DN l2dn : leistung2DNs) {
                    if (dnServiceIDs.contains(l2dn.getLeistung4DnId())) {
                        Date endDate = (l2dn.getScvKuendigung() != null) ? l2dn.getScvKuendigung() : DateTools
                                .getHurricanEndDate();
                        if (DateTools.isDateBetween(validDate, l2dn.getScvRealisierung(), endDate)) {
                            result.add(l2dn);
                        }
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

    @Override
    public List<Leistung2DN> findUnProvisionedDNServices(Date provDate) throws FindException {
        if (provDate == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return ((DNLeistungDAO) getDAO()).findUnProvisionedDNServices(provDate);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Leistung2DN findLeistung2DnById(Long l2DnId) throws FindException {
        if (l2DnId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            return dao.findById(l2DnId, Leistung2DN.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung2DN> findLeistung2DN4CPSTx(Long cpsTxId) throws FindException {
        if (cpsTxId == null) {
            return null;
        }
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            return dao.findLeistung2DN4CPSTx(cpsTxId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveLeistung2DN(Leistung2DN leistung2Dn) throws StoreException {
        if (leistung2Dn == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            ((DNLeistungDAO) getDAO()).store(leistung2Dn);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void attachDefaultLeistungen2DN(Rufnummer rufnummer, Long auftragId, Date realDate, Long sessionId)
            throws StoreException {
        try {
            Leistung productLeistung = leistungService.findProductLeistung4Auftrag(
                    auftragId, ProduktMapping.MAPPING_PART_TYPE_PHONE);
            if (productLeistung == null) {
                throw new StoreException("Die Produkt-Leistung vom Billing-System konnte nicht ermittelt werden!");
            }

            Leistungsbuendel leistungsbuendel = findLeistungsbuendel4Auftrag(auftragId);
            if (leistungsbuendel == null) {
                throw new StoreException("DN-Leistungsbuendel konnte nicht ermittelt werden!");
            }

            Leistung2DN example = new Leistung2DN();
            example.setDnNo(rufnummer.getDnNo());
            example.setLbId(leistungsbuendel.getId());
            List<Leistung2DN> dnLeistungen = findLeistung2DnByExample(example);

            boolean attachDefaultDn = checkDefaultDnRequired(leistungsbuendel, dnLeistungen);
            if (attachDefaultDn) {
                // Ordnet der Rufnummer die Default-Leistungen zu, die zu dem Produkt konfiguriert sind
                saveLeistung2DN(rufnummer, null, null, true, leistungsbuendel.getId(), realDate, null, sessionId);
            }
            else {
                // Pruefen, ob die DN-Leistungen ein AM- und EWSD-Realisierungsdatum besitzen. Wenn nicht,
                // wird als AM-Realisierungsdatum der Realisierungstermin eingetragen. Die Leistung
                // kann dann kein AM-Datum besitzen, wenn bei dem Auftrag bereits ein BA-Storno
                // durchgefuehrt wurde.
                AKUser user = getAKUserBySessionIdSilent(sessionId);
                for (Leistung2DN l2dn : dnLeistungen) {
                    if ((l2dn.getScvRealisierung() == null) && (l2dn.getEwsdRealisierung() == null)) {
                        l2dn.setScvRealisierung(realDate);
                        l2dn.setScvUserRealisierung((user != null) ? user.getName() : HurricanConstants.UNKNOWN);
                        saveLeistung2DN(l2dn);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_ATTACHING_DEFAULT_DN_SERVICES,
                    new Object[] { rufnummer.getRufnummer(), e.getMessage() }, e);
        }
    }

    /**
     * Ueberprueft, ob die Default-Leistungen zugoerdnet werden muessen. Dies ist dann der Fall, wenn die Liste mit den
     * zugeordneten Rufnummernleistungen {@code dnLeistungen} leer ist oder KEINE Rufnummernleistungen besitzt, die als
     * Default dem Leistungsbuendel zugeordnet sind.
     *
     * @param leistungsbuendel
     * @param dnLeistungen
     * @return
     */
    private boolean checkDefaultDnRequired(Leistungsbuendel leistungsbuendel, List<Leistung2DN> dnLeistungen) {
        boolean attachDefaultDn = (CollectionTools.isEmpty(dnLeistungen));
        if (!attachDefaultDn) {
            // pruefen, ob nur "nicht-Standard"-Leistungen zugeordnet sind
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            List<Leistung4Dn> defaultLeistungen = dao.findDefaultLeistungen4Buendel(leistungsbuendel.getId());
            Map<Long, Leistung4Dn> defaultLeistungMap = new HashMap<>();
            CollectionMapConverter.convert2Map(defaultLeistungen, defaultLeistungMap, "getId", null);

            boolean defaultLeistungFound = false;
            for (Leistung2DN l2dn : dnLeistungen) {
                if (defaultLeistungMap.containsKey(l2dn.getLeistung4DnId())) {
                    defaultLeistungFound = true;
                    break;
                }
            }
            attachDefaultDn = !defaultLeistungFound;
        }
        return attachDefaultDn;
    }

    @Override
    public Boolean hasLeistung(Rufnummer rufnummer) throws FindException {
        if (rufnummer == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            Leistung2DN l2Dn = new Leistung2DN();
            l2Dn.setDnNo(rufnummer.getDnNo());
            List<Leistung2DN> lV = findLeistung2DnByExample(l2Dn);
            return (lV != null && !lV.isEmpty());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<LeistungInLeistungsbuendelView> findAllLb2Leistung(Long lbId) throws FindException {
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            return dao.findAllLb2Leistung(lbId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void updateLb2Leistung(Date vBis, Long lb2LID) throws StoreException {
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            dao.updateLB2Leistung(vBis, lb2LID);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveLeistungsbuendel(Leistungsbuendel lb) throws StoreException {
        if (lb == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            ((DNLeistungDAO) getDAO()).store(lb);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Long> groupedDnNos() throws FindException {
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            return dao.groupedDnNos();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Leistung4Dn findDNLeistungByExternLeistungNo(Long extProdID) throws FindException {
        try {
            DNLeistungDAO dao = (DNLeistungDAO) getDAO();
            List<Leistung4Dn> result = dao.findByProperty(Leistung4Dn.class, "externLeistungNo", extProdID);
            if ((result == null) || result.isEmpty()) {
                return null;
            }
            return result.get(0);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Leistung4Dn> findMissingDnLeistungen(Long auftragId) throws FindException {
        List<Leistung4Dn> missing = new ArrayList<>();
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
        if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
            List<Leistung> leistungen = leistungService.findLeistungen4Auftrag(auftragDaten.getAuftragNoOrig());
            if (leistungen != null) {
                Collection<Leistung2DN> leistungen4Auftrag = findDNLeistungen4Auftrag(auftragId);
                leistungen4Auftrag = CollectionTools.select(leistungen4Auftrag,
                        leistung -> (leistung.getScvKuendigung() == null) && (leistung.getScvUserKuendigung() == null));
                Map<Long, Leistung2DN> map = CollectionTools.toMap(leistungen4Auftrag, Leistung2DN::getLeistung4DnId);
                for (Leistung leistung : leistungen) {
                    Long externLeistungNo = leistung.getExternLeistungNo();
                    if (externLeistungNo != null) {
                        Leistung4Dn leistung4dn = findDNLeistungByExternLeistungNo(externLeistungNo);
                        if ((leistung4dn != null) && !map.containsKey(leistung4dn.getId())) {
                            missing.add(leistung4dn);
                        }
                    }
                }
            }
        }
        return missing;
    }

    @Override
    public List<Leistung2DN> kuendigeLeistung4Rufnummern(final @Nonnull Long leistungId,
            final @Nonnull Date kuendigungAm, final @Nonnull String username, final @Nonnull List<Rufnummer> rufnummern)
            throws StoreException {
        final List<Long> rufnummerIds = Lists.transform(rufnummern, new Rufnummer2DnNoFunction());
        final List<Leistung2DN> rufnrLeistungenToCancle = dnLeistungDao.findAktiveLeistung2DnByRufnummern(leistungId,
                rufnummerIds);
        for (Leistung2DN leistung2Dn : rufnrLeistungenToCancle) {
            leistung2Dn.setScvKuendigung(kuendigungAm);
            leistung2Dn.setScvUserKuendigung(username);
            try {
                saveLeistung2DN(leistung2Dn);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
            }
        }
        return rufnrLeistungenToCancle;
    }

    @Override
    public List<Leistung4Dn> findDNLeistungenWithParameter(List<Leistung4Dn> leistungen) throws FindException {
        List<Leistung4Dn> leistungenWithParameter = new LinkedList<>();
        for (Leistung4Dn leistung4Dn : leistungen) {
            List<Leistung2Parameter> leistung2Parameter = findLeistung2Parameter(leistung4Dn.getId());
            if ((leistung2Parameter != null) && !leistung2Parameter.isEmpty()) {
                leistungenWithParameter.add(leistung4Dn);
            }
        }
        return leistungenWithParameter;
    }

    @CcTxRequiresNew
    @Override
    public void copyDnLeistung(Rufnummer source, Rufnummer target, Long sessionId) throws FindException, StoreException {
        if (source == null) {
            throw new StoreException("DN Leistung kann nicht kopiert werden. Source DN ist null");
        }
        if (target == null) {
            throw new StoreException("DN Leistung kann nicht kopiert werden. Target DN ist null");
        }
        Leistung2DN l2Dn = new Leistung2DN();
        l2Dn.setDnNo(source.getDnNo());
        List<Leistung2DN> lV = findLeistung2DnByExample(l2Dn);

        for (Leistung2DN l : lV) {
            saveOneLeistung4Dn(
                    l.getLeistung4DnId(),
                    l.getLbId(),
                    l.getLeistungParameter(),
                    target,
                    target.getGueltigVon(),
                    l.getParameterId(),
                    sessionId);
        }
    }

    @Override
    public List<Leistung4Dn> findDNLeistungenWithoutParameter(List<Leistung4Dn> leistungen) throws FindException {
        List<Leistung4Dn> leistungenWithoutParameter = new LinkedList<>();
        for (Leistung4Dn leistung4Dn : leistungen) {
            List<Leistung2Parameter> leistung2Parameter = findLeistung2Parameter(leistung4Dn.getId());
            if ((leistung2Parameter == null) || leistung2Parameter.isEmpty()) {
                leistungenWithoutParameter.add(leistung4Dn);
            }
        }
        return leistungenWithoutParameter;
    }

    private static final class Rufnummer2DnNoFunction implements Function<Rufnummer, Long> {
        @Override
        @Nullable
        public Long apply(@Nullable final Rufnummer input) {
            return (input != null) ? input.getDnNo() : null;
        }
    }

    /**
     * @return Returns the auftragDatenDAO.
     */
    public AuftragDatenDAO getAuftragDatenDAO() {
        return auftragDatenDAO;
    }

    /**
     * @param auftragDatenDAO The auftragDatenDAO to set.
     */
    public void setAuftragDatenDAO(AuftragDatenDAO auftragDatenDAO) {
        this.auftragDatenDAO = auftragDatenDAO;
    }

    public void setLeistungService(LeistungService leistungService) {
        this.leistungService = leistungService;
    }

    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    public void setRufnummerService(RufnummerService rufnummerService) {
        this.rufnummerService = rufnummerService;
    }

}
