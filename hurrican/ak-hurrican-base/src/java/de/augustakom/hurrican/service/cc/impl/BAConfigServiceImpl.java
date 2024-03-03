/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2006 15:38:22
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.BAVerlaufDefinitionDAO;
import de.augustakom.hurrican.model.cc.Abt2NL;
import de.augustakom.hurrican.model.cc.BAVerlaufAG2Produkt;
import de.augustakom.hurrican.model.cc.BAVerlaufAbtConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufAbtConfig2Abt;
import de.augustakom.hurrican.model.cc.BAVerlaufAenderungGruppe;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufZusatz;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.view.VerlaufAbteilungView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Service-Implementierung von <code>BAConfigService</code>.
 *
 *
 */
@CcTxRequired
public class BAConfigServiceImpl extends DefaultCCService implements BAConfigService {

    private static final Logger LOGGER = Logger.getLogger(BAConfigServiceImpl.class);

    @Resource(name = "baVerlaufDefinitionDAO")
    private BAVerlaufDefinitionDAO baVerlaufDefDao;

    @Override
    public BAVerlaufAnlass findBAVerlaufAnlass(Long anlassId) throws FindException {
        if (anlassId == null) { return null; }
        try {
            return baVerlaufDefDao.findById(anlassId, BAVerlaufAnlass.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAVerlaufAnlass findBAVerlaufAnlass(String name) throws FindException {
        try {
            List<BAVerlaufAnlass> anlaesse = baVerlaufDefDao.findByProperty(BAVerlaufAnlass.class, BAVerlaufAnlass.NAME, name);
            if (!anlaesse.isEmpty()) {
                return anlaesse.get(0);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return null;
    }

    @Override
    public List<BAVerlaufAnlass> findBAVerlaufAnlaesse(Boolean onlyAct, Boolean onlyAuftragsart) throws FindException {
        try {
            return baVerlaufDefDao.findBAVerlaufAnlaesse(onlyAct, onlyAuftragsart);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAVerlaufConfig findBAVerlaufConfig(Long anlass, Long prodId, boolean switch2Default) throws FindException {
        if ((anlass == null) || (prodId == null)) { throw new FindException(FindException.INVALID_FIND_PARAMETER);}
        try {
            List<BAVerlaufConfig> configs = baVerlaufDefDao.findBAVerlaufConfigs(anlass, prodId, false);
            if (switch2Default && CollectionTools.isEmpty(configs)) {
                configs = baVerlaufDefDao.findBAVerlaufConfigs(anlass, null, true);
            }

            if ((configs != null) && (configs.size() > 1)) {
                throw new FindException(FindException.INVALID_RESULT_SIZE,
                        new Object[] { 1, (configs.size()) });
            }
            else if ((configs == null) || (configs.isEmpty())) {
                return null;
            }
            return configs.get(0);
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
    public List<BAVerlaufConfig> findBAVerlaufConfigs(Long anlass, Long prodId) throws FindException {
        try {
            BAVerlaufConfig example = new BAVerlaufConfig();
            example.setBaVerlAnlass(anlass);
            example.setProdId(prodId);
            example.setGueltigBis(DateTools.getHurricanEndDate());

            return baVerlaufDefDao.queryByExample(example, BAVerlaufConfig.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteBAVerlaufConfig(BAVerlaufConfig toDelete, Long sessionId) throws DeleteException {
        if (sessionId == null) { throw new DeleteException(DeleteException.INVALID_SESSION_ID); }
        if (toDelete == null) { return; }

        try {
            toDelete.setUserW(getLoginNameSilent(sessionId));
            toDelete.setGueltigBis(new Date());
            baVerlaufDefDao.store(toDelete);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAVerlaufConfig saveBAVerlaufConfig(BAVerlaufConfig toSave, Long sessionId) throws StoreException {
        if (sessionId == null) { throw new StoreException(StoreException.INVALID_SESSION_ID); }
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }

        try {
            if (toSave.getId() != null) {
                Date now = new Date();
                BAVerlaufConfig newConf = (BAVerlaufConfig) baVerlaufDefDao.update4History(toSave, toSave.getId(), now);
                newConf.setUserW(getLoginNameSilent(sessionId));
                // BaVerlaufZusatz auch historisieren
                List<BAVerlaufZusatz> oldBaVerlaufZusatzs = findBAVerlaufZusaetze4BAVerlaufConfig(toSave.getId(), null, null);
                for(BAVerlaufZusatz oldZusatz: oldBaVerlaufZusatzs) {
                    BAVerlaufZusatz newZusatz = new BAVerlaufZusatz();
                    PropertyUtils.copyProperties(newZusatz, oldZusatz);
                    newZusatz.setId(null);
                    newZusatz.setVersion(0L);
                    newZusatz.setBaVerlaufConfigId(newConf.getId());
                    newZusatz.setUserW(newConf.getUserW());
                    newZusatz.setGueltigVon(newConf.getGueltigVon());
                    baVerlaufDefDao.store(newZusatz);
                }

                return baVerlaufDefDao.store(newConf);
            }
            else {
                // pruefen, ob die Kombination aus Anlass und Produkt bereits vorhanden ist
                List<BAVerlaufConfig> existing =
                        baVerlaufDefDao.findBAVerlaufConfigs(toSave.getBaVerlAnlass(), toSave.getProdId(), true);
                if (CollectionTools.isNotEmpty(existing)) {
                    throw new StoreException(
                            "Zu der angegebenen Kombination aus Anlass und Produkt existiert bereits eine Konfiguration!");
                }

                HistoryHelper.checkHistoryDates(toSave);
                toSave.setUserW(getLoginNameSilent(sessionId));
                return baVerlaufDefDao.store(toSave);
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
    public List<BAVerlaufAbtConfig> findBAVerlaufAbtConfigs() throws FindException {
        try {
            return baVerlaufDefDao.findAll(BAVerlaufAbtConfig.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Long> findAbteilungen4BAVerlaufConfig(Long abtConfigId) throws FindException {
        try {
            BAVerlaufAbtConfig2Abt example = new BAVerlaufAbtConfig2Abt();
            example.setAbtConfigId(abtConfigId);

            List<BAVerlaufAbtConfig2Abt> result =
                    baVerlaufDefDao.queryByExample(example, BAVerlaufAbtConfig2Abt.class);
            if (CollectionTools.isNotEmpty(result)) {
                List<Long> abtIds = new ArrayList<Long>(result.size());
                for (BAVerlaufAbtConfig2Abt abtConf : result) {
                    abtIds.add(abtConf.getAbtId());
                }

                return abtIds;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerlaufAbteilungView> findAbteilungView4BAVerlaufConfig(Long abtConfigId, Long nlId) throws FindException {
        if ((abtConfigId == null) || (nlId == null)) {
            return null;
        }
        try {
            // Ermittle alle Abteilungen
            List<Long> abtIds = findAbteilungen4BAVerlaufConfig(abtConfigId);
            if (CollectionTools.isNotEmpty(abtIds)) {
                List<VerlaufAbteilungView> result = new ArrayList<VerlaufAbteilungView>();
                for (Long abtId : abtIds) {
                    // Ermittle zustaendige Niederlassung
                    Niederlassung nl = findNL4Verteilung(abtId, nlId);
                    if (nl != null) {
                        VerlaufAbteilungView view = new VerlaufAbteilungView();
                        view.setNlId(nl.getId());
                        view.setAbtId(abtId);
                        result.add(view);
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
    public List<BAVerlaufZusatz> findBAVerlaufZusaetze4BAVerlaufConfig(Long baVerlConfigId, Long hvtGruppeId, Long standortTypRefId) throws FindException {
        final Set<BAVerlaufZusatz> zusatzList = Sets.newHashSet();
        try {
            BAVerlaufZusatz example = new BAVerlaufZusatz();
            example.setBaVerlaufConfigId(baVerlConfigId);
            example.setGueltigBis(DateTools.getHurricanEndDate());

            example.setHvtGruppeId(hvtGruppeId);
            zusatzList.addAll(baVerlaufDefDao.queryByExample(example, BAVerlaufZusatz.class));

            example.setHvtGruppeId(null);
            example.setStandortTypRefId(standortTypRefId);
            zusatzList.addAll(baVerlaufDefDao.queryByExample(example, BAVerlaufZusatz.class));

            return Lists.newArrayList(zusatzList);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAVerlaufAenderungGruppe> findBAVerlaufAenderungGruppen() throws FindException {
        try {
            return baVerlaufDefDao.findAll(BAVerlaufAenderungGruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAVerlaufAnlass> findPossibleBAAnlaesse4Produkt(Long produktId, Boolean onlyAuftragsart) throws FindException {
        if (produktId == null) { return null; }
        try {
            return baVerlaufDefDao.findPossibleBAAnlaesse4Produkt(produktId, onlyAuftragsart);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteBAVerlaufZusatz(BAVerlaufZusatz toDelete, Long sessionId) throws DeleteException {
        if (sessionId == null) { throw new DeleteException(DeleteException.INVALID_SESSION_ID); }
        if (toDelete == null) { return; }
        try {
            toDelete.setUserW(getLoginNameSilent(sessionId));
            toDelete.setGueltigBis(new Date());
            baVerlaufDefDao.store(toDelete);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAVerlaufZusatz saveBAVerlaufZusatz(BAVerlaufZusatz toSave, Long sessionId) throws StoreException {
        if (sessionId == null) { throw new StoreException(StoreException.INVALID_SESSION_ID); }
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }

        try {
            if (toSave.getId() != null) {
                Date now = new Date();
                BAVerlaufZusatz newConf = (BAVerlaufZusatz) baVerlaufDefDao.update4History(toSave, toSave.getId(), now);
                newConf.setUserW(getLoginNameSilent(sessionId));
                return newConf;
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                toSave.setUserW(getLoginNameSilent(sessionId));
                baVerlaufDefDao.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAVerlaufAG2Produkt> findBAVAG4Produkt(Long produktId) throws FindException {
        if (produktId == null) { return null; }

        try {
            return baVerlaufDefDao.findBAVAG4Produkt(produktId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveBAVAG4Produkt(Long produktId, Collection<Long> bavagIds) throws StoreException {
        if (produktId == null) {
            throw new StoreException(
                    "Es wurde keine Produkt-ID angegeben, für die die BA-Verläufe gespeichert werden sollen.");
        }

        try {
            baVerlaufDefDao.deleteBAVAG4Produkt(produktId);

            List<BAVerlaufAG2Produkt> relations = new ArrayList<BAVerlaufAG2Produkt>();
            for (Long element : bavagIds) {
                BAVerlaufAG2Produkt ag2p = new BAVerlaufAG2Produkt();
                ag2p.setProduktId(produktId);
                ag2p.setBaVerlaufAenderungGruppeId(element);
                relations.add(ag2p);
            }
            baVerlaufDefDao.saveBAVerlaufAG2Produkte(relations);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Niederlassung findNL4Verteilung(Long abtId, Long nlId) throws FindException {
        if ((abtId == null) || (nlId == null)) {
            return null;
        }
        try {
            Niederlassung nl = getNiederlassungService().findNiederlassung(nlId);
            if (nl == null) {
                return null;
            }

            Abt2NL a2l = getNiederlassungService().findAbt2NL(abtId, nlId);
            if (a2l != null) {
                // Niederlassung nlId besitzt die gesuchte Abteilung
                return nl;
            }

            // Ermittle Niederlassung ueber parentId
            return findNL4Verteilung(abtId, nl.getParentId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    private NiederlassungService getNiederlassungService() {
        try {
            return getCCService(NiederlassungService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e);
        }
        return null;
    }

}


