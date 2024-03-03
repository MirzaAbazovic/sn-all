/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 13:04:59
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.validation.ObjectError;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.HVTGruppeDAO;
import de.augustakom.hurrican.dao.cc.HVTStandortDAO;
import de.augustakom.hurrican.dao.cc.HVTStandortTechTypeDAO;
import de.augustakom.hurrican.dao.cc.HVTTechnikDAO;
import de.augustakom.hurrican.dao.cc.KvzAdresseDAO;
import de.augustakom.hurrican.dao.cc.KvzSperreDAO;
import de.augustakom.hurrican.dao.cc.PhysikTypDAO;
import de.augustakom.hurrican.dao.cc.UEVT2ZielDAO;
import de.augustakom.hurrican.dao.cc.UevtDAO;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandort2Technik;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.UEVT2Ziel;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTClusterView;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.cc.view.UevtBuchtView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CounterService;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 * Implementierung von <code>HVTService</code>
 *
 *
 */
@CcTxRequired
public class HVTServiceImpl extends DefaultCCService implements HVTService {

    private static final Logger LOGGER = Logger.getLogger(HVTServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CounterService")
    private CounterService counterService;

    private HVTStandortDAO hvtStandortDAO = null;
    private HVTTechnikDAO hvtTechnikDAO = null;
    private UevtDAO uevtDAO = null;
    private UEVT2ZielDAO uevt2ZielDAO = null;
    private PhysikTypDAO physikTypDAO = null;
    private EquipmentDAO equipmentDAO = null;
    private HVTStandortTechTypeDAO hvtStandortTechTypeDAO = null;

    @Resource(name = "kvzAdresseDAO")
    private KvzAdresseDAO kvzAdresseDAO;

    @Resource(name = "kvzSperreDAO")
    private KvzSperreDAO kvzSperreDAO;

    @Override
    @CcTxRequiredReadOnly
    public HVTGruppe findHVTGruppeById(Long hvtGruppeId) throws FindException {
        if (hvtGruppeId == null) {
            return null;
        }
        try {
            FindDAO dao = (FindDAO) getDAO();
            return dao.findById(hvtGruppeId, HVTGruppe.class);
        }
        catch (ObjectRetrievalFailureException e) {
            LOGGER.info(e.getMessage(), e);
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTGruppe findHVTGruppe4Standort(Long hvtStandortId) throws FindException {
        if (hvtStandortId == null) {
            return null;
        }
        try {
            HVTGruppeDAO dao = (HVTGruppeDAO) getDAO();
            return dao.findHVTGruppe4Standort(hvtStandortId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTGruppe> findHVTGruppen() throws FindException {
        try {
            return ((ByExampleDAO) getDAO()).queryByExample(
                    new HVTGruppe(),
                    HVTGruppe.class,
                    new String[] { HVTGruppe.ID },
                    null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public Pair<List<HVTStandort>, List<HVTGruppe>> findHVTStandorteAndGruppen(String onkz, Integer asb, String ortsteil,
            String ort, Long standortTypRefId, String clusterId ) throws FindException {
        try {
            return getHvtStandortDAO().findHVTStandorteAndGruppen(onkz, asb, ortsteil, ort, standortTypRefId, clusterId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HVTGruppe saveHVTGruppe(HVTGruppe toSave) throws StoreException {
        try {
            StoreDAO dao = (StoreDAO) getDAO();
            return dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTStandort> findHVTStandorte4Gruppe(Long hvtGruppeId, boolean onlyActive) throws FindException {
        if (hvtGruppeId == null) {
            return null;
        }
        try {
            HVTStandortDAO dao = getHvtStandortDAO();
            if (onlyActive) {
                return dao.findActiveHVTStandorte4Gruppe(hvtGruppeId);
            }
            else {
                return dao.findHVTStandorte4Gruppe(hvtGruppeId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTStandort> findHVTStandorte() throws FindException {
        try {
            return getHvtStandortDAO().queryByExample(new HVTStandort(), HVTStandort.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTStandort findHVTStandort(Long hvtStdId) throws FindException {
        if (hvtStdId == null) {
            return null;
        }
        try {
            FindDAO dao = getHvtStandortDAO();
            return dao.findById(hvtStdId, HVTStandort.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTStandort findHVTStandort(String onkz, Integer asb) throws FindException {
        if (StringUtils.isBlank(onkz) || (asb == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            return getHvtStandortDAO().findHVTStandort(onkz, asb);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTStandort> findHVTStandort4DtagAsb(String onkz, Integer dtagAsb, Long standortTypRefId)
            throws FindException {
        try {
            return getHvtStandortDAO().findHVTStandort4DtagAsb(onkz, dtagAsb, standortTypRefId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTStandort> findHVTStandorte(HVTStandort example) throws FindException {
        if (example == null) {
            return null;
        }
        try {
            return getHvtStandortDAO().queryByExample(example, HVTStandort.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTStandort> findHVTStdZiele(Long uevtId, Long p2ptId) throws FindException {
        if ((uevtId == null) || (p2ptId == null)) {
            return null;
        }
        try {
            Produkt2PhysikTyp p2pt = getPhysikTypDAO().findById(p2ptId, Produkt2PhysikTyp.class);
            if (p2pt == null) {
                return null;
            }

            List<HVTStandort> result = new ArrayList<>();
            if ((p2pt.getVirtuell() != null) && p2pt.getVirtuell()) {
                // HVT-Ziele ueber Mapping-Tabelle suchen
                UEVT2Ziel example = new UEVT2Ziel();
                example.setUevtId(uevtId);
                example.setProduktId(p2pt.getProduktId());
                List<UEVT2Ziel> ziele = getUevt2ZielDAO().queryByExample(example, UEVT2Ziel.class);
                if (ziele != null) {
                    for (UEVT2Ziel u2z : ziele) {
                        HVTStandort hvtStd = findHVTStandort(u2z.getHvtStandortIdZiel());
                        if (hvtStd != null) {
                            result.add(hvtStd);
                        }
                    }
                }
            }
            else {
                // HVT-Ziel ist der HVT, dem der UEVT zugeordnet ist.
                HVTStandort hvtStd = findHVTStandort4UEVT(uevtId);
                if (hvtStd != null) {
                    result.add(hvtStd);
                }
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTStandort findHVTStandort4UEVT(Long uevtId) throws FindException {
        try {
            UEVT uevt = getUevtDAO().findById(uevtId, UEVT.class);
            if ((uevt != null) && (uevt.getHvtIdStandort() != null)) {
                return getHvtStandortDAO().findById(uevt.getHvtIdStandort(), HVTStandort.class);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTGruppeStdView> findHVTViews() throws FindException {
        try {
            return findHVTViews(new HVTQuery());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTGruppeStdView> findHVTViews(HVTQuery query) throws FindException {
        if (query == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            return getHvtStandortDAO().findHVTViews(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTClusterView> findHVTClusterViews(List<Long> hvtStandortIds) throws FindException {
        if (CollectionTools.isEmpty(hvtStandortIds)) {
            return Collections.emptyList();
        }
        try {
            return getHvtStandortDAO().findHVTClusterViews(hvtStandortIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HVTStandort saveHVTStandort(HVTStandort toSave) throws StoreException {
        try {
            StoreDAO dao = getHvtStandortDAO();
            return dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Integer generateAsb4HVTStandort(Integer asb) throws FindException {
        if (asb == null) {
            return null;
        }
        try {
            Integer result = counterService.getNewIntValue(CounterService.COUNTER_ASB);
            return (result * 1000) + asb;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<Equipment> findEquipments4Kvz(Long hvtStandortId, String kvzNummer) {
        return getEquipmentDAO().findEquipments4Kvz(hvtStandortId, kvzNummer);
    }

    @Override
    @CcTxRequiredReadOnly
    public List<UEVT> findUEVTs4HVTStandort(Long hvtStandortId) throws FindException {
        if (hvtStandortId == null) {
            return null;
        }
        try {
            return getUevtDAO().findByHVTStandortId(hvtStandortId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<UEVT> findUEVTs() throws FindException {
        try {
            return getUevtDAO().findAll(UEVT.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public UEVT findUEVT(Long uevtId) throws FindException {
        if (uevtId == null) {
            return null;
        }
        try {
            return getUevtDAO().findById(uevtId, UEVT.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public UEVT findUEVT(Long hvtIdStandort, String uevt) throws FindException {
        try {
            UEVT example = new UEVT();
            example.setHvtIdStandort(hvtIdStandort);
            example.setUevt(uevt);

            List<UEVT> uevts = getUevtDAO().queryByExample(example, UEVT.class);
            int size = (uevts != null) ? uevts.size() : 0;
            if ((uevts != null) && (size == 1)) {
                return uevts.get(0);
            }
            throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, size });
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
    @CcTxRequiredReadOnly
    public List<UEVT2Ziel> findUEVTZiele(Long uevtId) throws FindException {
        if (uevtId == null) {
            return null;
        }
        try {
            UEVT2Ziel example = new UEVT2Ziel();
            example.setUevtId(uevtId);
            return getUevt2ZielDAO().queryByExample(example, UEVT2Ziel.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveUEVT(UEVT toSave, List<UEVT2Ziel> uevtZiele) throws StoreException {
        try {
            getUevtDAO().store(toSave);
            if (uevtZiele != null) {
                // UEVT-Ziele fuer UEVT-Id loeschen
                getUevt2ZielDAO().deleteByUevtId(toSave.getId());

                // Liste durchlaufen und UEVT-Id zuordnen
                for (UEVT2Ziel u2z : uevtZiele) {
                    u2z.setId(null);
                    u2z.setUevtId(toSave.getId());
                }
                getUevt2ZielDAO().store(uevtZiele);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public UEVT saveUEVT(UEVT toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            return getUevtDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTTechnik> findHVTTechniken() throws FindException {
        try {
            return getHvtTechnikDAO().findAll(HVTTechnik.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTTechnik findHVTTechnik(Long technikId) throws FindException {
        if (technikId == null) {
            return null;
        }
        try {
            return getHvtTechnikDAO().findById(technikId, HVTTechnik.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTTechnik> findHVTTechniken4Standort(Long hvtIdStandort) throws FindException {
        if (hvtIdStandort == null) {
            return null;
        }
        try {
            return getHvtTechnikDAO().findByHVTStandort(hvtIdStandort);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveHVTTechniken4Standort(Long hvtIdStandort, List<Long> hvtTechnikIds) throws StoreException {
        if ((hvtIdStandort == null) || (hvtTechnikIds == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            HVTTechnikDAO dao = getHvtTechnikDAO();
            dao.deleteHVTTechniken4Standort(hvtIdStandort);

            for (Long technikId : hvtTechnikIds) {
                HVTStandort2Technik h2t = new HVTStandort2Technik();
                h2t.setHvtIdStandort(hvtIdStandort);
                h2t.setHvtTechnikId(technikId);
                dao.store(h2t);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public Long findNiederlassungId4HVTIdStandort(Long hvtIdStandort) throws FindException {
        if (hvtIdStandort == null) {
            return null;
        }
        HVTGruppe hvt = findHVTGruppe4Standort(hvtIdStandort);
        if (hvt != null) {
            return hvt.getNiederlassungId();
        }
        return null;
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTStandort> findHVTStandortByTyp(Long hvtTyp) throws FindException {
        if (hvtTyp == null) {
            return null;
        }
        try {
            HVTStandort example = new HVTStandort();
            example.setStandortTypRefId(hvtTyp);
            return getHvtStandortDAO().queryByExample(example, HVTStandort.class, new String[] { HVTStandort.ID }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTGruppe> findHVTGruppenForNiederlassung(Long niederlassungId) throws FindException {
        if (niederlassungId == null) {
            return null;
        }
        try {
            HVTGruppe example = new HVTGruppe();
            example.setNiederlassungId(niederlassungId);
            return ((HVTGruppeDAO) getDAO())
                    .queryByExample(example, HVTGruppe.class, new String[] { HVTGruppe.ORTSTEIL }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTRaum> findHVTRaeume4Standort(Long hvtIDStandort) throws FindException {
        if (hvtIDStandort == null) {
            return null;
        }
        try {
            HVTRaum example = new HVTRaum();
            example.setHvtIdStandort(hvtIDStandort);
            return getHvtStandortDAO().queryByExample(example, HVTRaum.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTRaum findHVTRaum(Long id) throws FindException {
        if (id == null) {
            return null;
        }
        try {
            return getHvtStandortDAO().findById(id, HVTRaum.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTRaum findHVTRaumByName(Long hvtStandortId, String name) throws FindException {
        if ((hvtStandortId == null) || StringUtils.isBlank(name)) {
            return null;
        }
        try {
            HVTRaum ex = new HVTRaum();
            ex.setHvtIdStandort(hvtStandortId);
            ex.setRaum(name);
            List<HVTRaum> list = getHvtStandortDAO().queryByExample(ex, HVTRaum.class);

            return ((list != null) && (list.size() == 1)) ? list.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HVTRaum saveHVTRaum(HVTRaum hvtRaum) throws StoreException {
        if (hvtRaum == null) {
            return null;
        }
        try {
            return getHvtStandortDAO().store(hvtRaum);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTGruppe findHVTGruppeByBezeichnung(String bezeichnung) throws FindException {
        if (StringUtils.isEmpty(bezeichnung)) {
            return null;
        }
        try {
            HVTGruppe example = new HVTGruppe();
            example.setOrtsteil(bezeichnung);
            List<HVTGruppe> result = getHvtStandortDAO().queryByExample(example, HVTGruppe.class);
            if (CollectionTools.isNotEmpty(result)) {
                if (CollectionTools.hasExpectedSize(result, 1)) {
                    return result.get(0);
                }
                throw new FindException(FindException.INVALID_RESULT_SIZE_WITH_FILTER,
                        new Object[] { "HVTGruppe: " + bezeichnung, 1, result.size() });
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTStandort findHVTStandortByBezeichnung(String bezeichnung) throws FindException {
        if (StringUtils.isBlank(bezeichnung)) {
            return null;
        }
        try {
            HVTGruppe hvtGruppe = findHVTGruppeByBezeichnung(bezeichnung);
            if (hvtGruppe != null) {
                List<HVTStandort> hvtStandorte = findHVTStandorte4Gruppe(hvtGruppe.getId(), true);
                if ((hvtStandorte != null) && (hvtStandorte.size() == 1)) {
                    return hvtStandorte.get(0);
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
    @CcTxRequiredReadOnly
    public List<HVTStandort> findHVTStandorteByBezeichnung(List<String> bezeichnungen, boolean onlyActive)
            throws FindException {
        if (CollectionTools.isEmpty(bezeichnungen)) {
            Collections.emptyList();
        }
        try {
            List<HVTStandort> hvtStandorte = new ArrayList<>();
            for (String bezeichnung : bezeichnungen) {
                HVTGruppe hvtGruppe = findHVTGruppeByBezeichnung(bezeichnung);
                if (hvtGruppe != null) {
                    List<HVTStandort> hvtStandorte4Gruppe = findHVTStandorte4Gruppe(hvtGruppe.getId(), onlyActive);
                    if (hvtStandorte4Gruppe != null) {
                        hvtStandorte.addAll(hvtStandorte4Gruppe);
                    }
                }
            }
            return hvtStandorte;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public HVTTechnik findHVTTechnikByHersteller(String hersteller) throws FindException {
        if (StringUtils.isBlank(hersteller)) {
            return null;
        }
        HVTTechnik example = new HVTTechnik();
        example.setHersteller(hersteller);
        try {
            List<HVTTechnik> result = getHvtTechnikDAO().queryByExample(example, HVTTechnik.class);
            if (result.size() > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, result.size() });
            }
            return (result.size() == 1) ? result.get(0) : null;
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
    @CcTxRequiredReadOnly
    public List<UevtBuchtView> findUevtBuchtenForUevt(Long hvtIdStandort, String uevt) throws FindException {
        try {
            return getUevtDAO().findUevtBuchtenForUevt(hvtIdStandort, uevt);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<HVTStandortTechType> findTechTypes4HVTStandort(Long hvtStandortId) throws FindException {
        if (hvtStandortId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            HVTStandortTechType example = new HVTStandortTechType();
            example.setHvtIdStandort(hvtStandortId);

            return getHvtStandortTechTypeDAO().queryByExample(example, HVTStandortTechType.class,
                    new String[] { HVTStandortTechType.ID }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveTechType(HVTStandortTechType toSave, Long sessionId) throws StoreException {
        try {
            if ((toSave == null) || (sessionId == null)) {
                throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
            }

            toSave.setUserW(getLoginNameSilent(sessionId));
            getHvtStandortTechTypeDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteTechType(HVTStandortTechType toDelete) throws DeleteException {
        try {
            if ((toDelete == null)) {
                throw new DeleteException(DeleteException.INVALID_PARAMETERS);
            }

            getHvtStandortTechTypeDAO().deleteById(toDelete.getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public Long findAnschlussart4HVTStandort(Long hvtStandortId) throws FindException {
        HVTStandort hvtStandort = findHVTStandort(hvtStandortId);

        if (hvtStandort != null) {
            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_HVT)) {
                return Anschlussart.ANSCHLUSSART_HVT;
            }
            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_KVZ)
                    || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                return Anschlussart.ANSCHLUSSART_KVZ;
            }
            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTB)) {
                return Anschlussart.ANSCHLUSSART_FTTB;
            }
            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTH)) {
                return Anschlussart.ANSCHLUSSART_FTTH;
            }
            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTX_BR)
                    || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTX_FC)
                    || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTB_H)) {
                return Anschlussart.ANSCHLUSSART_FTTX;
            }
        }

        return null;
    }

    @Override
    public HVTTechnik saveHVTTechnik(HVTTechnik hvtTechnik) throws StoreException {
        try {
            return hvtTechnikDAO.store(hvtTechnik);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public KvzAdresse saveKvzAdresse(KvzAdresse kvzAdresse) throws StoreException {
        HVTStandort standort;
        try {
            standort = this.findHVTStandort(kvzAdresse.getHvtStandortId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }

        if (standort == null) {
            throw new StoreException(String.format("Es wurde kein HVT-Standort mit Id %s gefunden!",
                    kvzAdresse.getId()));
        }
        else if (!standort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
            throw new StoreException(String.format("HVT-Standort mit Id %s ist kein FTTC_KVZ - Standort!",
                    standort.getId()));
        }

        try {
            return kvzAdresseDAO.store(kvzAdresse);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CheckForNull
    @CcTxRequiredReadOnly
    public KvzAdresse findKvzAdresse(@Nonnull Long hvtStandortId, @Nonnull String kvzNummer) throws FindException {
        KvzAdresse example = new KvzAdresse();
        example.setHvtStandortId(hvtStandortId);
        example.setKvzNummer(kvzNummer);

        List<KvzAdresse> kvzAdressen;

        try {
            kvzAdressen = kvzAdresseDAO.queryByExample(example, KvzAdresse.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        if (kvzAdressen.size() > 1) {
            throw new FindException(FindException.INVALID_RESULT_SIZE);
        }
        return Iterables.getFirst(kvzAdressen, null);
    }

    @Override
    @Nonnull
    @CcTxRequiredReadOnly
    public List<KvzAdresse> findKvzAdressen(@Nonnull Long hvtStandortId) throws FindException {
        KvzAdresse example = new KvzAdresse();
        example.setHvtStandortId(hvtStandortId);

        try {
            return kvzAdresseDAO.queryByExample(example, KvzAdresse.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteKvzAdresse(@Nonnull KvzAdresse kvzAdresse) throws DeleteException {
        try {
            kvzAdresseDAO.delete(kvzAdresse);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public KvzSperre createKvzSperre(Long hvtStandortId, String kvzNr, String comment) throws FindException {
        final HVTStandort hvtStandort = findHVTStandort(hvtStandortId);
        final HVTGruppe hvtGruppe = findHVTGruppe4Standort(hvtStandortId);
        if (hvtStandort == null || hvtGruppe == null) {
            throw new FindException(String.format("Es konnte kein HVT-Standort fuer die angebebene HVT-Standort-ID "
                    + "'%s' gefunden werden", hvtStandortId));
        }
        KvzSperre kvzSperre = new KvzSperre();
        kvzSperre.setKvzNummer(kvzNr);
        kvzSperre.setBemerkung(comment);
        kvzSperre.setAsb(hvtStandort.getDTAGAsb());
        kvzSperre.setOnkz(hvtGruppe.getOnkz());
        return kvzSperreDAO.store(kvzSperre);
    }

    @Override
    public KvzSperre findKvzSperre(Long hvtStandortId, Long geoId) throws FindException {
        HVTStandort hvtStandort = findHVTStandort(hvtStandortId);
        if (hvtStandort != null && geoId != null) {
            GeoId geoIdObj = hvtStandortDAO.findById(geoId, GeoId.class);
            if (geoIdObj != null && StringUtils.isNotEmpty(geoIdObj.getKvz())) {
                HVTGruppe hvtGruppe = findHVTGruppeById(hvtStandort.getHvtGruppeId());
                return kvzSperreDAO.find(hvtGruppe.getOnkz(), hvtStandort.getDTAGAsb(), geoIdObj.getKvz());
            }
        }
        return null;
    }

    @Override
    public KvzSperre findKvzSperre(Long kvzSperreId) throws FindException {
        if (kvzSperreId == null) { return null; }
        try {
            return kvzSperreDAO.findById(kvzSperreId, KvzSperre.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void validateKvzSperre(Endstelle endstelle) throws FindException, ValidationException {
        KvzSperre kvzSperre = findKvzSperre(endstelle.getHvtIdStandort(), endstelle.getGeoId());
        if (kvzSperre != null && kvzSperre.is4StandortTyp(findHVTStandort(endstelle.getHvtIdStandort()).getStandortTypRefId())) {
            ValidationException v = new ValidationException(endstelle, endstelle.getClass().getName());
            v.addError(new ObjectError("KvzSperre", "Endstelle ist für Portvergabe und WITA-Bestellungen gesperrt."
                    + (kvzSperre.getBemerkung() == null ? "" : " Grund: '" + kvzSperre.getBemerkung() + "'")));
            throw v;
        }
    }

    @Override
    public void deleteKvzSperre(Long kvzSperreId) {
        kvzSperreDAO.deleteById(kvzSperreId);
    }


    private HVTStandortDAO getHvtStandortDAO() {
        return hvtStandortDAO;
    }

    public void setHvtStandortDAO(HVTStandortDAO hvtStandortDAO) {
        this.hvtStandortDAO = hvtStandortDAO;
    }

    private UevtDAO getUevtDAO() {
        return uevtDAO;
    }

    public void setUevtDAO(UevtDAO uevtDAO) {
        this.uevtDAO = uevtDAO;
    }

    private HVTTechnikDAO getHvtTechnikDAO() {
        return hvtTechnikDAO;
    }

    public void setHvtTechnikDAO(HVTTechnikDAO hvtTechnikDAO) {
        this.hvtTechnikDAO = hvtTechnikDAO;
    }

    private UEVT2ZielDAO getUevt2ZielDAO() {
        return uevt2ZielDAO;
    }

    public void setUevt2ZielDAO(UEVT2ZielDAO uevt2ZielDAO) {
        this.uevt2ZielDAO = uevt2ZielDAO;
    }

    private PhysikTypDAO getPhysikTypDAO() {
        return physikTypDAO;
    }

    public void setPhysikTypDAO(PhysikTypDAO physikTypDAO) {
        this.physikTypDAO = physikTypDAO;
    }

    private EquipmentDAO getEquipmentDAO() {
        return equipmentDAO;
    }

    public void setEquipmentDAO(EquipmentDAO equipmentDAO) {
        this.equipmentDAO = equipmentDAO;
    }

    private HVTStandortTechTypeDAO getHvtStandortTechTypeDAO() {
        return hvtStandortTechTypeDAO;
    }

    public void setHvtStandortTechTypeDAO(HVTStandortTechTypeDAO hvtStandortTechTypeDAO) {
        this.hvtStandortTechTypeDAO = hvtStandortTechTypeDAO;
    }
}
