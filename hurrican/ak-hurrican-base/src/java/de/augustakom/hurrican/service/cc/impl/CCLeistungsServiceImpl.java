/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2005 10:44:10
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.Auftrag2TechLeistungDAO;
import de.augustakom.hurrican.dao.cc.TechLeistungDAO;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.Produkt2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.temp.LeistungsDiffCheck;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.model.cc.view.TechLeistungSynchView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.ExterneAuftragsLeistungen;
import de.augustakom.hurrican.service.cc.impl.command.leistung.AbstractLeistungCommand;
import de.augustakom.hurrican.service.cc.impl.command.leistung.SynchTechLeistungenCommand;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Implementierung von <code>CCLeistungsService</code>.
 *
 *
 */
@CcTxRequired
public class CCLeistungsServiceImpl extends DefaultCCService implements CCLeistungsService {

    private static final Logger LOGGER = Logger.getLogger(CCLeistungsService.class);

    private TechLeistungDAO techLeistungDAO = null;
    private Auftrag2TechLeistungDAO auftrag2TechLeistungDAO = null;
    private VerlaufDAO verlaufDAO = null;

    private static volatile Map<Long, TechLeistung> techLsMap = null;

    private BillingAuftragService billingAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ChainService")
    private ChainService chainService;

    @Autowired
    private ServiceLocator serviceLocator;

    public void init() throws ServiceNotFoundException {
        setBillingAuftragService(getBillingService(BillingAuftragService.class));
    }

    @Override
    public List<TechLeistung> findTechLeistungen(boolean onlyActual) throws FindException {
        try {
            return getTechLeistungDAO().findTechLeistungen(onlyActual);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<TechLeistung> findTechLeistungenByType(String techLsTyp) throws FindException {
        if (StringUtils.isBlank(techLsTyp)) {
            return null;
        }
        try {
            TechLeistung example = new TechLeistung();
            example.setTyp(techLsTyp);

            return getTechLeistungDAO().queryByExample(example, TechLeistung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public TechLeistung findTechLeistung(Long techLeistungId) throws FindException {
        if (techLeistungId == null) {
            return null;
        }
        try {
            return getTechLeistungDAO().findById(techLeistungId, TechLeistung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt2TechLeistung> findProd2TechLs(Long prodId, String techLsTyp, Boolean defaults) throws FindException {
        if (prodId == null) {
            return null;
        }
        try {
            return getTechLeistungDAO().findProdukt2TechLs(prodId, techLsTyp, defaults);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteProdukt2TechLeistung(Long p2tlId) throws DeleteException {
        if (p2tlId == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getTechLeistungDAO().deleteProdukt2TechLeistung(p2tlId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public void saveProdukt2TechLeistung(Produkt2TechLeistung toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
        try {
            getTechLeistungDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public TechLeistung findTechLeistungByExtLstNo(Long externLeistungNo) throws FindException {
        if (externLeistungNo == null) {
            return null;
        }
        try {
            List<TechLeistung> result = getTechLeistungDAO().findTechLeistungen(externLeistungNo);
            if ((result != null) && (!result.isEmpty())) {
                if (result.size() > 1) {
                    throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, result.size() });
                }
                return result.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<TechLeistung> findTechLeistungen4Auftrag(Long auftragId, String lsTyp, boolean onlyActive)
            throws FindException {
        if (auftragId == null) {
            return null;
        }
        try {
            return getTechLeistungDAO().findTechLeistungen4Auftrag(auftragId, lsTyp, onlyActive);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Bandwidth findBandwidth4Auftrag(Long auftragId, boolean loadDefaultIfNotAssigned) throws FindException {
        TechLeistung downstream = findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_DOWNSTREAM, loadDefaultIfNotAssigned);
        TechLeistung upstream = findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_UPSTREAM, loadDefaultIfNotAssigned);

        Integer down = downstream == null ? null : downstream.getIntegerValue();
        Integer up = downstream == null ? null : upstream.getIntegerValue();

        return Bandwidth.create(down, up);
    }

    @Override
    public Map<Long, List<TechLeistung>> findTechLeistungen4Auftraege(List<Long> auftragIds, String lsTyp,
            boolean onlyActive) throws FindException {
        if ((auftragIds == null) || auftragIds.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return getTechLeistungDAO().findTechLeistungen4Auftraege(auftragIds, lsTyp, onlyActive);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public TechLeistung findTechLeistung4Auftrag(Long auftragId, String lsTyp, boolean loadDefaultIfNotAssigned)
            throws FindException {
        if ((auftragId == null) || StringUtils.isBlank(lsTyp)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            List<TechLeistung> result = findTechLeistungen4Auftrag(auftragId, lsTyp, true);
            int size = (result != null) ? result.size() : 0;
            if (size == 1) {
                return result.get(0);
            }
            else if (size > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, size });
            }
            else if ((size == 0) && loadDefaultIfNotAssigned) {
                // Default-Leistung des angegebenen Typs zum Produkt laden
                AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
                Long prodId = auftragDaten != null ? auftragDaten.getProdId() : null;
                List<TechLeistung> defTLs = findProd2TechLs(prodId, lsTyp, true).
                        stream().map(Produkt2TechLeistung::getTechLeistung).collect(Collectors.toList());
                if ((defTLs != null) && (defTLs.size() == 1)) {
                    return defTLs.get(0);
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
    public List<Auftrag2TechLeistung> findAuftrag2TechLeistungen(Long auftragId, @Nullable LocalDate when) {
        Auftrag2TechLeistung example = new Auftrag2TechLeistung();
        example.setAuftragId(auftragId);

        List<Auftrag2TechLeistung> assigned = getAuftrag2TechLeistungDAO().queryByExample(example,
                Auftrag2TechLeistung.class);

        if (when != null) {
            // filter active tech. leistungen
            final List<Auftrag2TechLeistung> validAtWhen = new ArrayList<>();
            for (Auftrag2TechLeistung auftrag2TechLs : assigned) {
                if (auftrag2TechLs.isAktiveAt(when)) {
                    validAtWhen.add(auftrag2TechLs);
                }
            }
            return validAtWhen;
        } else {
            // do not filter because when == null
            return assigned;
        }
    }

    @Override
    public Set<Long> findTechLeistungenNo4Auftrag(Long auftragId, @Nullable LocalDate when) {
        List<Auftrag2TechLeistung> techLs = findAuftrag2TechLeistungen(auftragId, when);
        Set<Long> techLsNo = Sets.newHashSet();
        techLsNo.addAll(techLs.stream().map(Auftrag2TechLeistung::getTechLeistungId).collect(Collectors.toList()));
        return techLsNo;
    }

    @Override
    @Nonnull
    public List<Auftrag2TechLeistung> findAuftrag2TechLeistungen(@Nonnull Long auftragId, @Nonnull String[] lsTypes,
            @Nonnull Date validDate, boolean ignoreAktivVon)
            throws FindException {
        if ((auftragId == null) || (lsTypes == null) || (validDate == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            List<TechLeistung> techLs4Type = new ArrayList<>();
            for (String lsTyp : lsTypes) {
                techLs4Type.addAll(findTechLeistungenByType(lsTyp));
            }

            if (CollectionTools.isNotEmpty(techLs4Type)) {
                List<Long> techLsIds = techLs4Type.stream().map(TechLeistung::getId).collect(Collectors.toList());

                Long[] techLsIdArray = techLsIds.toArray(new Long[techLsIds.size()]);
                return getAuftrag2TechLeistungDAO().findAuftragTechLeistungen(auftragId,
                        techLsIdArray, validDate, ignoreAktivVon);
            }
            else {
                throw new FindException(String.format(
                        "Es gibt keine zugeordneten technischen Leistungen zu den angegebenen Typen! Typen: %s",
                        Arrays.toString(lsTypes)));
            }
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
    public TechLeistung findTechLeistung4Auftrag(Long auftragId, String lsTyp, Date validDate) throws FindException {
        try {
            List<Auftrag2TechLeistung> auftrag2TechLeistungen = findAuftrag2TechLeistungen(auftragId,
                    new String[] { lsTyp }, validDate, false);
            int size = (auftrag2TechLeistungen != null) ? auftrag2TechLeistungen.size() : 0;
            if ((size == 1) && (auftrag2TechLeistungen != null)) {
                Auftrag2TechLeistung a2tl = auftrag2TechLeistungen.get(0);
                return findTechLeistung(a2tl.getTechLeistungId());
            }
            else if (size > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, size });
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
    public List<TechLeistung> findTechLeistungen4Auftrag(Long auftragId, String lsTyp, Date validDate)
            throws FindException {
        if ((auftragId == null) || StringUtils.isBlank(lsTyp) || (validDate == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            List<TechLeistung> techLs4Type = findTechLeistungenByType(lsTyp);
            if (CollectionTools.isNotEmpty(techLs4Type)) {
                List<Long> techLsIds = techLs4Type.stream().map(TechLeistung::getId).collect(Collectors.toList());

                Long[] techLsIdArray = techLsIds.toArray(new Long[techLsIds.size()]);
                List<Auftrag2TechLeistung> a2tls = getAuftrag2TechLeistungDAO().findAuftragTechLeistungen(auftragId,
                        techLsIdArray, validDate, false);

                List<TechLeistung> retVal = new ArrayList<>();
                if (CollectionTools.isNotEmpty(a2tls)) {
                    for (Auftrag2TechLeistung a2tl : a2tls) {
                        retVal.add(findTechLeistung(a2tl.getTechLeistungId()));
                    }
                }

                return retVal;
            }
            else {
                throw new FindException("Es gibt keine technischen Leistungen zu dem angegebenen Typ! Typ: " + lsTyp);
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Nonnull
    @Override
    public List<TechLeistung> findTechLeistungen4Auftrag(@Nonnull List<LeistungsDiffView> diffs, @Nonnull String lsTyp,
            @Nonnull Date validDate) throws FindException {
        List<LeistungsDiffView> filteredDiffs = diffs.stream()
                .filter(d -> DateTools.isDateAfterOrEqual(d.getAktivVon(), validDate))
                .collect(Collectors.toList());

        List<TechLeistung> leistungen = new ArrayList<>();
        for (LeistungsDiffView diff : filteredDiffs) {
            leistungen.add(findTechLeistung(diff.getTechLeistungId()));
        }
        return leistungen.stream()
                .filter(ls -> StringUtils.equals(ls.getTyp(), lsTyp))
                .collect(Collectors.toList());
    }

    @Override
    public List<TechLeistung> findTechLeistungen4Auftrag(Long auftragId, LocalDate validDate) throws FindException {
        try {
            List<Auftrag2TechLeistung> assignedTechLeistungen =
                    findAuftrag2TechLeistungen(auftragId, validDate);

            if (assignedTechLeistungen != null) {
                return assignedTechLeistungen
                        .stream()
                        .map(
                                assignedTL -> getTechLeistungDAO().findById(assignedTL.getTechLeistungId(), TechLeistung.class)
                        )
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean isTechLeistungActive(List<Long> auftragIds, Long extLeistungNo, Date validDate)
            throws FindException {
        for (Long auftragId : auftragIds) {
            if (isTechLeistungActive(auftragId, extLeistungNo, validDate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTechLeistungActive(Long auftragId, Long extLeistungNo, Date validDate) throws FindException {
        try {
            TechLeistung techLs = findTechLeistungByExtLstNo(extLeistungNo);
            if (techLs == null) {
                throw new FindException("Keine technische Leistung zu EXT_LST__NO gefunden! EXT_LST__NO: "
                        + extLeistungNo);
            }

            List<Auftrag2TechLeistung> auftrag2TechLs = findAuftrag2TechLeistungen(auftragId, techLs.getId(), false);
            if (CollectionTools.isNotEmpty(auftrag2TechLs)) {
                for (Auftrag2TechLeistung a2tl : auftrag2TechLs) {
                    if (DateTools.isDateBeforeOrEqual(a2tl.getAktivVon(), validDate)
                            && ((a2tl.getAktivBis() == null) || DateTools.isDateAfter(a2tl.getAktivBis(), validDate))) {
                        return true;
                    }
                }
            }

            return false;
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
    public List<TechLeistung> findTechLeistungen(String typ, String strValue, Long longValue) throws FindException {
        try {
            TechLeistung ex = new TechLeistung();
            ex.setTyp(typ);
            ex.setStrValue(strValue);
            ex.setLongValue(longValue);

            return getTechLeistungDAO().queryByExample(ex, TechLeistung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public TechLeistung findTechLeistung(String typ, Long longValue) throws FindException {
        try {
            TechLeistung ex = new TechLeistung();
            ex.setTyp(typ);
            ex.setLongValue(longValue);

            List<TechLeistung> result = getTechLeistungDAO().queryByExample(ex, TechLeistung.class);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() > 1) {
                    throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, result.size() });
                }
                return result.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasTechLeistungWithExtLeistungNo(Long externLeistungNo, Long auftragId, boolean onlyActive)
            throws FindException {
        if ((externLeistungNo == null) || (auftragId == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            TechLeistung techLs = findTechLeistungByExtLstNo(externLeistungNo);
            return techLs != null && hasTechLeistung(techLs.getId(), auftragId, onlyActive);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasTechLeistung(Long techLsId, Long auftragId, boolean onlyActive) throws FindException {
        if ((techLsId == null) || (auftragId == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            List<Auftrag2TechLeistung> atls = getAuftrag2TechLeistungDAO().findAuftragTechLeistungen(auftragId,
                    techLsId, onlyActive);
            return (CollectionTools.isNotEmpty(atls));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasTechLeistungEndsInFutureWithExtLeistungNo(Long externLeistungNo, Long auftragId) throws FindException {
        if ((externLeistungNo == null) || (auftragId == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            final TechLeistung techLs = findTechLeistungByExtLstNo(externLeistungNo);
            // ends in future
            return techLs != null && hasTechLeistung(techLs.getId(), auftragId, new Date(), true);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    private boolean hasTechLeistung(Long techLsId, Long auftragId, Date validDate, boolean ignoreAktivVon) throws FindException {
        if ((techLsId == null) || (auftragId == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            final Long[] techLsIdsArray = {techLsId};
            final List<Auftrag2TechLeistung> atls = getAuftrag2TechLeistungDAO().findAuftragTechLeistungen(auftragId,
                    techLsIdsArray, validDate, ignoreAktivVon);
            return (CollectionTools.isNotEmpty(atls));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveLeistungKonfig(TechLeistung toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            getTechLeistungDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Auftrag2TechLeistung> findAuftrag2TechLeistungen(Long ccAuftragId, Long techLsId, boolean onlyAct)
            throws FindException {
        if (ccAuftragId == null) {
            return null;
        }
        try {
            return getAuftrag2TechLeistungDAO().findAuftragTechLeistungen(ccAuftragId, techLsId, onlyAct);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Ermittelt alle aktiven technischen Leistungen, die einem bestimmten Auftrag zugeordnet sind. <br> Das Ergebnis
     * wird ueber die technische Leistung gruppiert - dadurch erhaelt man fuer jeden Leistungstyp lediglich ein Result.
     * Die Quantity ist auf-summiert.
     *
     * @param auftragId
     *
     * @return
     *
     * @throws FindException
     *
     *
     */
    @Nonnull
    private List<Auftrag2TechLeistung> findActiveA2TLGrouped(Long auftragId, LocalDate checkDate) throws FindException {
        if (auftragId == null) {
            return ImmutableList.of();
        }
        try {
            return getAuftrag2TechLeistungDAO().findActiveA2TLGrouped(auftragId, checkDate);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Auftrag2TechLeistung> findAuftrag2TechLeistungen4Verlauf(Long verlaufId) throws FindException {
        if (verlaufId == null) {
            return null;
        }
        try {
            return getAuftrag2TechLeistungDAO().findAuftragTechLeistungen4Verlauf(verlaufId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public @NotNull
    List<TechLeistung> findTechLeistungen4Verlauf(Long verlaufId, boolean onlyZugang) throws FindException {
        try {
            List<Auftrag2TechLeistung> auftrag2TechLeistungen = findAuftrag2TechLeistungen4Verlauf(verlaufId);
            if (CollectionUtils.isNotEmpty(auftrag2TechLeistungen)) {
                return auftrag2TechLeistungen
                        .stream()
                        .filter(a2tl ->
                                (onlyZugang)
                                        ? NumberTools.equal(a2tl.getVerlaufIdReal(), verlaufId)
                                        : NumberTools.isIn(verlaufId, new Number[] { a2tl.getVerlaufIdReal(), a2tl.getVerlaufIdKuend() }))
                        .map(a2tl -> techLeistungDAO.findById(a2tl.getTechLeistungId(), TechLeistung.class))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveAuftrag2TechLeistung(Auftrag2TechLeistung toSave) throws StoreException {
        try {
            getAuftrag2TechLeistungDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public List<Auftrag2TechLeistung> modifyAuftrag2TechLeistungen(Long auftragId, LocalDate originalDate,
            LocalDate modifiedDate, AuftragAktion auftragAktion)
            throws StoreException {
        try {
            List<Auftrag2TechLeistung> assignedTechLs = findAuftrag2TechLeistungen(auftragId, null, false);

            List<Auftrag2TechLeistung> modified = new ArrayList<>();
            for (Auftrag2TechLeistung assignedTechLeistung : assignedTechLs) {
                boolean isModified = false;
                if (auftragAktion != null) { // nur Leistungen mit verlinkter AuftragAktion beruecksichtigen
                    if (auftragAktion.isAuftragAktionAddFor(assignedTechLeistung)) {
                        assignedTechLeistung.setAktivVon(DateConverterUtils.asDate(modifiedDate));
                        isModified = true;
                    }
                    else if (auftragAktion.isAuftragAktionRemoveFor(assignedTechLeistung)) {
                        assignedTechLeistung.setAktivBis(DateConverterUtils.asDate(modifiedDate));
                        isModified = true;
                    }
                }
                else { // alle Leistungen mit aktivVon/aktivBis = originalDate beruecksichtigen
                    if (DateTools.isDateEqual(assignedTechLeistung.getAktivVon(),
                            DateConverterUtils.asDate(originalDate))) {
                        if (DateTools.isDateEqual(assignedTechLeistung.getAktivBis(),
                                DateConverterUtils.asDate(originalDate))) {
                            assignedTechLeistung.setAktivBis(DateConverterUtils.asDate(modifiedDate));
                        }
                        else {
                            assignedTechLeistung.setAktivVon(DateConverterUtils.asDate(modifiedDate));
                        }
                        isModified = true;
                    }
                    else if (DateTools.isDateEqual(assignedTechLeistung.getAktivBis(),
                            DateConverterUtils.asDate(originalDate))) {
                        assignedTechLeistung.setAktivBis(DateConverterUtils.asDate(modifiedDate));
                        isModified = true;
                    }
                }

                if (isModified) {
                    saveAuftrag2TechLeistung(assignedTechLeistung);
                    modified.add(assignedTechLeistung);
                }
            }

            return modified;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    public
    @Nonnull
    List<LeistungsDiffView> findLeistungsDiffs(Long ccAuftragId, Long auftragNoOrig, Long prodId)
            throws FindException {
        if (auftragNoOrig == null) {
            return Collections.emptyList();
        }

        // Auftragspositionen vom Billing-System ermitteln
        List<BAuftragLeistungView> activeLeistungViews = billingAuftragService.findAuftragLeistungViews4Auftrag(
                auftragNoOrig, true, true);

        if (activeLeistungViews == null) {
            activeLeistungViews = ImmutableList.of();
        }

        List<BAuftragLeistungView> allLeistungViews = billingAuftragService.findAuftragLeistungViews4Auftrag(
                auftragNoOrig, false, true);
        if (allLeistungViews == null) {
            allLeistungViews = ImmutableList.of();
        }

        // zeitlich befristete Leistungen herausfinden und zu den aktiven hinzufuegen. Dies sind Leistungen, welche mit
        // einem Gueltigkeitsdatum VOR Taifun-End-Date versehen sind
        // (man beachte: isActiveAuftragpos() liefert false auch wenn das gueltigBis Datum noch in der Zukunft liegt ...)
        Date now = new Date();
        for (BAuftragLeistungView view : allLeistungViews) {
            Date activeRefDate = new Date(view.getAuftragGueltigVon().getTime()).toInstant().isAfter(now.toInstant())
                    ? view.getAuftragGueltigVon() : now;
            if (view.getExternLeistungNo() != null && !view.isEinmalig() && !view.isActiveAuftragpos() && view.isActiveAt(activeRefDate)) {
                activeLeistungViews.add(view);
            }
        }

        activeLeistungViews = filterByProd2TechLsPriority(activeLeistungViews, prodId);

        Map<Long, BAuftragPos> positionen = Maps.newHashMap();
        ListMultimap<Long, BAuftragPos> extLeistNo2BAuftragPositionen = ArrayListMultimap.create();
        for (BAuftragLeistungView view : allLeistungViews) {
            if (view.getExternLeistungNo() != null) {
                Long externLeistungNo = view.getExternLeistungNo();
                BAuftragPos bap = billingAuftragService.findAuftragPosition(auftragNoOrig, externLeistungNo);
                if (bap != null) {
                    positionen.put(externLeistungNo, bap);
                }

                List<Long> externLNos = new ArrayList<>();
                externLNos.add(externLeistungNo);
                List<BAuftragPos> aps = billingAuftragService.findAuftragPositionen(auftragNoOrig, false,
                        externLNos);
                if (aps != null) {
                    extLeistNo2BAuftragPositionen.putAll(externLeistungNo, aps);
                }
            }
        }

        return findLeistungsDiffs(ccAuftragId, auftragNoOrig, prodId, new ExterneAuftragsLeistungen(positionen,
                extLeistNo2BAuftragPositionen, activeLeistungViews, allLeistungViews), null);
    }

    /**
     * Billing Leistungen anhand der Prio in Produkt2TechLeistung.priority filtern.
     *
     * @param bAuftragLeistungViews billing Leistungen
     * @param prodId                Hurrican Produkt-Id
     * @return die gefilterten Leistungen
     * @throws FindException
     */
    protected List<BAuftragLeistungView> filterByProd2TechLsPriority(List<BAuftragLeistungView> bAuftragLeistungViews, Long prodId) throws FindException {
        Map<Long, Produkt2TechLeistung> prod2TechLsByExtLeistungNo = findProd2TechLs(prodId, null, null)
                .stream()
                .filter(p2t -> p2t.getTechLeistungDependency() == null)
                .collect(Collectors.toMap(p2t -> p2t.getTechLeistung().getExternLeistungNo(), Function.identity()));

        // billing Leistungen mit Hurrican Leistungen paaren und nach Typ sortieren
        final String nullKeyReplace = "nullTechLeistungTyp";
        Map<String, List<Pair<BAuftragLeistungView, Produkt2TechLeistung>>> leistungenByTyp = bAuftragLeistungViews.stream()
                .map(al -> Pair.create(al, prod2TechLsByExtLeistungNo.get(al.getExternLeistungNo())))
                .collect(Collectors.groupingBy(pair -> pair.getSecond() != null ? pair.getSecond().getTechLeistung().getTyp() : nullKeyReplace));
        // pro Typ die Leistungen nach Produkt2TechLeistung priority sortieren (absteigend, nulls last)
        leistungenByTyp.values().stream().forEach(ls4Typ -> ls4Typ.sort(
                (p1, p2) -> Optional.ofNullable(p2.getSecond()).map(p2t -> p2t.getPriority()).orElse(-1)
                            .compareTo(Optional.ofNullable(p1.getSecond()).map(p2t->p2t.getPriority()).orElse(-1))));

        List<BAuftragLeistungView> result = new ArrayList<>();
        // Ergebnisse sammeln, bei unique types nur die hoechst-priore Leistung verwenden
        for (Map.Entry<String, List<Pair<BAuftragLeistungView, Produkt2TechLeistung>>> entry : leistungenByTyp.entrySet()) {
            if(nullKeyReplace.equals(entry.getKey()) || !TechLeistung.isTypUnique(entry.getKey())) {
                result.addAll(entry.getValue().stream().map(Pair::getFirst).collect(Collectors.toList()));
            }
            else {
                entry.getValue().stream().findFirst().ifPresent(p -> result.add(p.getFirst()));
            }
        }

        return result;
    }


    @Override
    public
    @Nonnull
    List<LeistungsDiffView> findLeistungsDiffs(Long ccAuftragId, Long auftragNoOrig,
            Long prodId, ExterneAuftragsLeistungen externeAuftragsLeistungen, Date defaultDate)
            throws FindException {
        if (ccAuftragId == null) {
            return Collections.emptyList();
        }

        try {
            List<LeistungsDiffView> result = Lists.newArrayList();

            // Produkt-2-TechLeistung Mapping laden
            List<Produkt2TechLeistung> p2tl = findProd2TechLs(prodId, null, null);

            Map<Long, TechLeistung> possibleTechLs = p2tl.stream().collect(
                    Collectors.toMap(p2t -> p2t.getTechLeistung().getId(), Produkt2TechLeistung::getTechLeistung, (p2t1, p2t2) -> p2t1));

            // techn. Leistungen ermitteln, die dem Auftrag bereits zugeordnet sind
            LocalDate checkDate = LocalDate.now();
            if (auftragNoOrig != null) {
                final BAuftrag bAuftrag = billingAuftragService.findAuftrag(auftragNoOrig);
                if (bAuftrag != null && bAuftrag.getGueltigVon() != null) {
                    final LocalDate bAugtragGueltigVon = DateConverterUtils.asLocalDate(bAuftrag.getGueltigVon());
                    // set checkDate to bAuftrag.getGueltigVon() if possible
                    checkDate = bAugtragGueltigVon.isAfter(LocalDate.now()) ? bAugtragGueltigVon : LocalDate.now();
                }
            }
            List<Auftrag2TechLeistung> techLsExist = findActiveA2TLGrouped(ccAuftragId, checkDate);

            // hinzuzufuegende Leistungen ermitteln
            result.addAll(determineHinzuzufuegen(ccAuftragId, auftragNoOrig, techLsExist,
                    externeAuftragsLeistungen.activeLeistungViews, possibleTechLs));

            // zu kuendigende Leistungen ermitteln
            result.addAll(determineZuKuendigen(ccAuftragId, auftragNoOrig, techLsExist,
                    externeAuftragsLeistungen.activeLeistungViews,
                    externeAuftragsLeistungen.extLeistNo2BAuftragPositionen, defaultDate));

            createDiff4OneTimeServices(result, ccAuftragId, externeAuftragsLeistungen.allLeistungViews);
            checkDefaultLeistungen(ccAuftragId, externeAuftragsLeistungen.positionen, prodId, result, techLsExist,
                    defaultDate);

            return result;
        } catch (FindException f) {
            throw f;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Leistungs-Differenzen: " + e.getMessage(), e);
        }
    }

    @Override
    public void cancelAuftrag2TechLeistungen(Long auftragId, AuftragAktion auftragAktion)
            throws StoreException {
        try {
            List<Auftrag2TechLeistung> assignedTechLs = findAuftrag2TechLeistungen(auftragId, null, false);

            for (Auftrag2TechLeistung assignedTechLeistung : assignedTechLs) {
                if (auftragAktion.isAuftragAktionAddFor(assignedTechLeistung)) {
                    getAuftrag2TechLeistungDAO().delete(assignedTechLeistung);
                }
                else if (auftragAktion.isAuftragAktionRemoveFor(assignedTechLeistung)) {
                    assignedTechLeistung.setAktivBis(null);
                    assignedTechLeistung.setAuftragAktionsIdRemove(null);
                    saveAuftrag2TechLeistung(assignedTechLeistung);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }

    }

    private List<LeistungsDiffView> determineHinzuzufuegen(Long ccAuftragId, Long auftragNoOrig,
            List<Auftrag2TechLeistung> techLsExist, List<BAuftragLeistungView> leistungViews,
            Map<Long, TechLeistung> possibleTechLeistungen) throws FindException, ServiceNotFoundException {
        List<LeistungsDiffView> result = Lists.newArrayList();
        for (BAuftragLeistungView blv : leistungViews) {
            TechLeistung techLs = findTechLeistungByExtLstNo(blv.getExternLeistungNo());
            if ((techLs != null) && BooleanTools.nullToFalse(techLs.getSnapshotRel())) {
                boolean doesExist = false;
                for (Auftrag2TechLeistung tlExist : techLsExist) {
                    if (NumberTools.equal(techLs.getId(), tlExist.getTechLeistungId())) {
                        doesExist = true;
                        break;
                    }
                }

                boolean techLeistungAllowed = true;
                // pruefen, ob Leistung fuer das Produkt moeglich ist!!!
                if (!doesExist && !possibleTechLeistungen.containsKey(techLs.getId())) {
                    techLeistungAllowed = false;
                }

                if (techLeistungAllowed && (!doesExist || BooleanTools.nullToFalse(techLs.getCheckQuantity()))) {
                    long quantityDiff = quantityDiff(ccAuftragId, auftragNoOrig, techLs, blv.getMenge(), true);
                    if (quantityDiff > 0) {
                        LeistungsDiffView diff = new LeistungsDiffView(ccAuftragId, techLs.getId());
                        diff.setAktivVon(blv.getAuftragPosGueltigVon());
                        diff.setTechLsName(techLs.getName());
                        diff.setQuantity(quantityDiff);
                        diff.setZugang(true);
                        result.add(diff);
                    }
                }
            }
        }
        return result;
    }

    private List<LeistungsDiffView> determineZuKuendigen(Long ccAuftragId, Long auftragNoOrig,
            List<Auftrag2TechLeistung> techLsExist, List<BAuftragLeistungView> leistungViews,
            ListMultimap<Long, BAuftragPos> extLeistNo2BAuftragPositionen, Date defaultCancelDate)
            throws FindException,
            ServiceNotFoundException {
        List<LeistungsDiffView> result = Lists.newArrayList();

        List<Long> checkedTL = new ArrayList<>();
        for (Auftrag2TechLeistung tlExist : techLsExist) {
            if (!checkedTL.contains(tlExist.getTechLeistungId())) {
                checkedTL.add(tlExist.getTechLeistungId());

                TechLeistung techLs = findTechLeistung(tlExist.getTechLeistungId());
                boolean stillExist = false;

                // alle Taifun-Positionen zu der techn. Leistung ermitteln
                List<BAuftragPos> aps = extLeistNo2BAuftragPositionen.get(techLs.getExternLeistungNo());
                BAuftragPos ap = null;
                if (CollectionTools.isNotEmpty(aps)) {
                    // Position mit hoechstem Kuendigungsdatum ermitteln
                    for (BAuftragPos tmp : aps) {
                        if (tmp.getChargeTo() == null){
                            ap = tmp;
                            break;
                        }
                        if ((ap == null) || DateTools.isDateAfter(tmp.getChargeTo(), ap.getChargeTo())) {
                            ap = tmp;
                        }
                    }
                }

                for (BAuftragLeistungView blv : leistungViews) {
                    if (NumberTools.equal(techLs.getExternLeistungNo(), blv.getExternLeistungNo())){
                        // Leistungen, die das Flag AutoExpire gesetzt haben, werden in Hurrican erst mit aktivBis versehen, wenn
                        // der Job processExpiredServicesJob die Leistung gekündigt hat (Job liest das AktivBis aus Taifun)
                        // wenn nach Jobausführung eine Kündigungsdatum in Hurrican existiert, darf kein Abgleich erfolgen
                        if (techLs.getAutoExpire()){
                            Date now = DateConverterUtils.asDate(LocalDate.now());
                            if(ap == null || ap.getChargeTo() == null ||
                              (ap.getChargeTo().compareTo(now) == 1 && tlExist.getAktivBis() == null) ||
                              (ap.getChargeTo().compareTo(now) <= 0 && tlExist.getAktivBis().compareTo(now) <= 0)) {
                               stillExist = true;
                               break;
                            }
                            // wenn in Hurrican bereits ein Kündigungsdatum für eine Leistung existiert, darf kein Abgleich erfolgen
                        }else if (ap == null || ap.getChargeTo() == null ||  ap.getChargeTo().equals(DateTools.getBillingEndDate()) ||
                                 (ap.getChargeTo() != null && tlExist.getAktivBis() != null)){
                                  stillExist = true;
                                  break;
                        }
                    }
                }

                // einmalige Leistungen nicht erneut sync-en (Diff wegen vorher nicht uebernommenem AktivBis)
                if (BooleanTools.nullToFalse(techLs.getAktivBisNullOnSync()) && ap != null && ap.getChargeFrom().equals(ap.getChargeTo())) {
                    continue;
                }

                if (!stillExist) { // Leistung ist nicht mehr im Billing eingetragen
                    LeistungsDiffView diff = new LeistungsDiffView(ccAuftragId, techLs.getId());
                    diff.setAktivVon((tlExist.getAktivVon() != null) ? tlExist.getAktivVon() : new Date());
                    Date cancelDate = (ap != null) ? ap.getChargeTo() : defaultCancelDate;
                    diff.setAktivBis((cancelDate != null) ? cancelDate : new Date());
                    diff.setTechLsName(techLs.getName());
                    diff.setQuantity(tlExist.getQuantity());
                    diff.setZugang(false);
                    result.add(diff);
                }
                else {
                    // Menge pruefen
                    Long quantity = (ap != null) ? ap.getMenge() : Long.valueOf(1);
                    long quantityDiff = quantityDiff(ccAuftragId, auftragNoOrig, techLs, quantity, false);
                    if (quantityDiff < 0) {
                        LeistungsDiffView diff = new LeistungsDiffView(ccAuftragId, techLs.getId());
                        diff.setAktivVon((tlExist.getAktivVon() != null) ? tlExist.getAktivVon() : new Date());
                        diff.setAktivBis((ap != null) ? ap.getChargeTo() : new Date());
                        diff.setTechLsName(techLs.getName());
                        diff.setQuantity(quantityDiff * -1);
                        diff.setZugang(false);
                        result.add(diff);
                    }
                }
            }
        }
        return result;
    }

    /* Ermittelt die Leistungs-Differenz fuer einmalige Leistungen */
    private void createDiff4OneTimeServices(List<LeistungsDiffView> diffResult, Long ccAuftragId,
            List<BAuftragLeistungView> leistungViews)
            throws FindException {
        // Einmalige Leistungen abgleichen
        List<TechLeistungSynchView> leistungEinmaligViews = findOneTimeTechServices(leistungViews);
        if (CollectionTools.isNotEmpty(leistungEinmaligViews)) {
            for (TechLeistungSynchView view : leistungEinmaligViews) {
                // Pruefen, ob techLS in Auftrag vorhanden
                List<Auftrag2TechLeistung> list = findAuftrag2TechLeistungen(ccAuftragId, view.getTechLstId(),
                        Boolean.FALSE);
                long menge = 0;

                if (CollectionTools.isNotEmpty(list)) {
                    for (Auftrag2TechLeistung a2tl : list) {
                        menge += a2tl.getQuantity();
                    }
                }

                // Falls nicht vorhanden, techn. Leistung anlegen mit Gueltigkeitsdatum aus View
                if (view.getQuantity() > menge) {
                    for (int i = 0; i < (view.getQuantity() - menge); i++) {
                        LeistungsDiffView leistungsDiffView = new LeistungsDiffView();
                        leistungsDiffView.setAuftragId(ccAuftragId);
                        leistungsDiffView.setTechLeistungId(view.getTechLstId());
                        leistungsDiffView.setAktivVon(view.getDate());
                        if (!BooleanTools.nullToFalse(findTechLeistung(view.getTechLstId()).getAktivBisNullOnSync())) {
                            leistungsDiffView.setAktivBis(view.getDate());
                        }
                        leistungsDiffView.setQuantity((long) 1);
                        leistungsDiffView.setTechLsName(view.getTechLstName());
                        leistungsDiffView.setZugang(Boolean.TRUE);
                        diffResult.add(leistungsDiffView);
                    }
                }
            }
        }
    }

    @Override
    public List<TechLeistungSynchView> findOneTimeTechServices(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        // Ermittle alle Leistungen, die ein Hurrican-Mapping besitzen
        List<BAuftragLeistungView> leistungViews = billingAuftragService.findAuftragLeistungViews4Auftrag(
                auftragNoOrig, false, true);
        return findOneTimeTechServices(leistungViews);
    }

    private List<TechLeistungSynchView> findOneTimeTechServices(List<BAuftragLeistungView> leistungViews)
            throws FindException {
        try {
            Map<Long, TechLeistungSynchView> map = new HashMap<>();

            if (CollectionTools.isNotEmpty(leistungViews)) {
                for (BAuftragLeistungView bAuftragLeistungView : leistungViews) {
                    // Ueberpruefung, auf einmalige Leistung
                    if (bAuftragLeistungView.isEinmalig()) {
                        TechLeistung techLs = findTechLeistungByExtLstNo(bAuftragLeistungView.getExternLeistungNo());
                        if (techLs == null) {
                            continue;
                        }
                        if (map.containsKey(techLs.getId())) {
                            TechLeistungSynchView view = map.get(techLs.getId());
                            view.setQuantity(view.getQuantity() + bAuftragLeistungView.getMenge());
                            if (DateTools.isDateAfter(bAuftragLeistungView.getAuftragPosGueltigBis(), view.getDate())) {
                                view.setDate(bAuftragLeistungView.getAuftragPosGueltigBis());
                            }
                        }
                        else {
                            TechLeistungSynchView view = new TechLeistungSynchView();
                            view.setTechLstId(techLs.getId());
                            view.setQuantity(bAuftragLeistungView.getMenge());
                            view.setDate(bAuftragLeistungView.getAuftragPosGueltigVon());
                            view.setTechLstName(techLs.getName());
                            map.put(techLs.getId(), view);
                        }
                    }
                }
            }
            if (!map.isEmpty()) {
                List<TechLeistungSynchView> list = new ArrayList<>();
                list.addAll(map.values());
                return list;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Leistungs-Differenzen: " + e.getMessage(), e);
        }
    }

    /*
     * Ermittelt die Differenz zwischen der im Billing-System eingetragenen Menge fuer eine Leistung und der Anzahl der
     * Zuordnungen im Hurrican-System. <br> Ist die Leistung als nicht mengen-relevant gekennzeichnet, wird - abhaengig
     * vom Parameter 'zugang' - der Wert 1 oder 0 zurueck gegeben.
     *
     * @param auftragNoOrig Auftragsnummer aus dem Billing-System
     *
     * @param techLeistung zu pruefende technische Leistung
     *
     * @param maxMenge Anzahl aus dem Billing-System
     *
     * @param zugang Flag, ob es sich um einen Leistungszu- oder -abgang handelt.
     */
    private long quantityDiff(Long ccAuftragId, Long auftragNoOrig, TechLeistung techLeistung, Long maxMenge,
            boolean zugang) throws ServiceNotFoundException, FindException {
        if (BooleanTools.nullToFalse(techLeistung.getCheckQuantity())) {
            long count = 0;
            List<AuftragDaten> adsTmp = (auftragNoOrig != null) ? ccAuftragService
                    .findAuftragDaten4OrderNoOrig(auftragNoOrig) : ImmutableList.of(ccAuftragService
                    .findAuftragDatenByAuftragIdTx(ccAuftragId));
            for (AuftragDaten adTmp : adsTmp) {
                if (adTmp.isAuftragActive()) {
                    List<Auftrag2TechLeistung> atls = findAuftrag2TechLeistungen(adTmp.getAuftragId(),
                            techLeistung.getId(), true);
                    if (CollectionTools.isNotEmpty(atls)) {
                        for (Auftrag2TechLeistung atl : atls) {
                            count += atl.getQuantity();
                        }
                    }
                }
            }

            return maxMenge - count;
        }
        return (zugang) ? 1 : 0;
    }

    /*
     * Prueft, ob Default-Leistungen des Hurrican-Produkts zugeordnet oder entfernt werden muessen.
     *
     * @param auftragId
     *
     * @param auftragNoOrig
     *
     * @param prodId
     *
     * @param diffs
     *
     * @param a2tlExist
     *
     *
     */
    private void checkDefaultLeistungen(Long auftragId, Map<Long, BAuftragPos> positionen, Long prodId,
            List<LeistungsDiffView> diffs, List<Auftrag2TechLeistung> a2tlExist, Date defaultDate) throws FindException {
        try {
            if (prodId == null) {
                throw new FindException("Hurrican-Produkt wurde nicht angegeben - Default Leistungen koennen "
                        + "deshalb nicht ermittelt werden!");
            }

            // pruefen, ob Produkt Default-Leistungen besitzt
            List<Produkt2TechLeistung> defaultLeistungen4Prod = findProd2TechLs(prodId, null, true);
            if (CollectionTools.isEmpty(defaultLeistungen4Prod)) {
                return;
            }

            // Liste mit allen Leistungs-IDs, die nach dem Synch aktiv sind
            List<Long> techLsIds = new ArrayList<>();
            // Liste mit allen Leistungs-IDs, die mit dem Synch entfernt werden sollen
            List<Long> techLsIds2Remove = new ArrayList<>();

            for (Auftrag2TechLeistung a2tl : a2tlExist) {
                TechLeistung tl = getTechLeistung(a2tl.getTechLeistungId());
                techLsIds.add(tl != null ? tl.getId() : null);
            }

            for (LeistungsDiffView diffView : diffs) {
                if (diffView.isZugang()) {
                    for (long i = 0; i < diffView.getQuantity(); i++) {
                        TechLeistung tl = getTechLeistung(diffView.getTechLeistungId());
                        techLsIds.add(tl != null ? tl.getId() : null);
                    }
                }
                else if (diffView.isKuendigung()) {
                    for (long i = 0; i < diffView.getQuantity(); i++) {
                        TechLeistung tl = getTechLeistung(diffView.getTechLeistungId());
                        techLsIds.remove(tl != null ? tl.getId() : null);
                        techLsIds2Remove.add(tl != null ? tl.getId() : null);
                    }
                }
            }

            // pruefen, ob Default-Leistung eine Dependency besitzt.
            // Ist dies der Fall muss geprüft werden, ob in der Liste 'techLsIds' die
            // Dependency-ID vorhanden ist. Ist dies nicht der Fall, wird die
            // Default-Leistung aus der Liste entfernt.
            List<Produkt2TechLeistung> defLs2Remove = defaultLeistungen4Prod.stream()
                    .filter(defLs -> (defLs.getTechLeistungDependency() != null) && !techLsIds.contains(defLs.getTechLeistungDependency()))
                    .collect(Collectors.toList());

            final List<Produkt2TechLeistung> toRemoveFinal = new ArrayList<>(defLs2Remove);
            CollectionUtils.filter(defaultLeistungen4Prod, object -> {
                Produkt2TechLeistung defLs = (Produkt2TechLeistung) object;
                for (Produkt2TechLeistung tmp : toRemoveFinal) {
                    if (NumberTools.equal(defLs.getTechLeistung().getId(), tmp.getTechLeistung().getId())
                            && NumberTools.equal(defLs.getTechLeistungDependency(), tmp.getTechLeistungDependency())) {
                        return false;
                    }
                }
                return true;
            });

            for (Produkt2TechLeistung defLs : defaultLeistungen4Prod) {
                List<TechLeistung> techLsOfTyp = findTechLeistungenByType(defLs.getTechLeistung().getTyp());
                if (techLsIds2Remove.contains(defLs.getTechLeistung().getId())) { // Default-Leistung soll entfernt werden
                    // es wird geprueft, ob noch eine weitere Leistung des gleichen Typs(!) aktiv ist.
                    // Ist dies nicht(!) der Fall, wird der Eintrag aus der Differenz-Liste entfernt.
                    boolean typFound = false;
                    TechLeistung techLeistungOfTyp = null;
                    for (TechLeistung toCheck : techLsOfTyp) {
                        if (techLsIds.contains(toCheck.getId()) && !toCheck.getId().equals(defLs.getTechLeistung().getId())) {
                            typFound = true;
                            techLeistungOfTyp = toCheck;
                            break;
                        }
                    }

                    final Long tmpId = defLs.getTechLeistung().getId();
                    LeistungsDiffView tmp = (LeistungsDiffView) CollectionUtils.find(diffs, obj -> ((LeistungsDiffView) obj).getTechLeistungId().equals(tmpId));

                    // keine weitere Leistung des Typs gefunden
                    // --> Default-Leistung muss weiterhin bestehen bleiben (wird aus Diff-Liste entfernt)
                    if (tmp != null) {
                        if (!typFound) {
                            diffs.remove(tmp);
                        }
                        else {
                            if (techLeistungOfTyp != null) {
                                BAuftragPos bap = positionen.get(techLeistungOfTyp.getExternLeistungNo());
                                tmp.setAktivBis((bap != null) ? bap.getChargeFrom() : new Date());
                            }
                        }
                    }
                }
                else if (!techLsIds.contains(defLs.getTechLeistung().getId())) {
                    // Default-Leistung nicht in Liste der aktiven Leistungen -->>
                    // es wird geprueft, ob zumindest eine Leistung des gleichen Typs(!) aktiv ist. Ist
                    // dies auch nicht der Fall, wird die Leistung hinzugenommen.
                    boolean typFound = false;
                    Date aktivVon4Default = null;
                    for (TechLeistung toCheck : techLsOfTyp) {
                        final Long id2Check = toCheck.getId();
                        if (techLsIds.contains(id2Check)) {
                            typFound = true;
                        }

                        if (aktivVon4Default == null) {
                            // ermitteln, ob Leistung in a2tlExist vorhanden ist
                            Auftrag2TechLeistung tmp = (Auftrag2TechLeistung) CollectionUtils.find(a2tlExist, obj -> ((Auftrag2TechLeistung) obj).getTechLeistungId().equals(id2Check)
                            );

                            if (tmp != null) {
                                // Billing-AP zur Leistung ermitteln und von dieser das Datum holen
                                BAuftragPos bap = positionen.get(toCheck.getExternLeistungNo());
                                if (defaultDate != null) {
                                    aktivVon4Default = defaultDate;
                                }
                                else if ((bap != null) && (bap.getChargeTo() != null)) {
                                    aktivVon4Default = bap.getChargeTo();
                                }
                                else {
                                    aktivVon4Default = (tmp.getAktivBis() != null) ? tmp.getAktivBis() : tmp
                                            .getAktivVon();
                                }
                            }
                        }

                        if (typFound) {
                            break;
                        }
                    }

                    if (!typFound) {
                        // falls kein aktivVon-Datum ermittelt werden konnte wird nach
                        // dem spaetesten Datum der vorhandenen Leistungen gesucht und
                        // dieses Datum verwendet.
                        if (aktivVon4Default == null) {
                            aktivVon4Default = new Date();
                            for (LeistungsDiffView diffView : diffs) {
                                if (DateTools.isDateAfter(diffView.getAktivVon(), aktivVon4Default)) {
                                    aktivVon4Default = diffView.getAktivVon();
                                }
                            }
                        }

                        // Default-Leistung wird hinzugenommen
                        LeistungsDiffView diff = new LeistungsDiffView(auftragId, defLs.getTechLeistung().getId());
                        diff.setAktivVon((aktivVon4Default != null) ? aktivVon4Default : new Date());
                        diff.setTechLsName(defLs.getTechLeistung().getName());
                        diff.setQuantity((long) 1);
                        diff.setZugang(true);
                        diffs.add(diff);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler beim Abgleich der Hurrican Default-Leistungen: " + e.getMessage(), e);
        }
    }

    @Override
    public AKWarnings synchTechLeistungen4Auftrag(Long ccAuftragId, Long auftragNoOrig, Long prodId,
            Long verlaufId, boolean executeLsCommands, Long sessionId)
            throws StoreException {
        List<LeistungsDiffView> diffs;
        try {
            diffs = findLeistungsDiffs(ccAuftragId, auftragNoOrig, prodId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Abgleich der Leistungen des Auftrags ist ein Fehler " + "aufgetreten:\n"
                    + e.getMessage(), e);
        }
        return synchTechLeistung4Auftrag(ccAuftragId, prodId, verlaufId, executeLsCommands, sessionId, diffs);
    }

    private AKWarnings synchTechLeistung4Auftrag(Long ccAuftragId, Long prodId, Long verlaufId,
            boolean executeLsCommands, Long sessionId, List<LeistungsDiffView> diffs, Date realisierungsTermin,
            AuftragAktion auftragAktion)
            throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(SynchTechLeistungenCommand.class);
            BAService bas = getCCService(BAService.class);
            Date when = realisierungsTermin;
            if (verlaufId != null) {
                Verlauf verlauf = bas.findVerlauf(verlaufId);
                when = (verlauf != null) ? verlauf.getRealisierungstermin() : new Date();
            }

            cmd.prepare(AbstractLeistungCommand.KEY_AUFTRAG_ID, ccAuftragId);
            cmd.prepare(SynchTechLeistungenCommand.KEY_LEISTUNG_DIFF, diffs);
            cmd.prepare(SynchTechLeistungenCommand.KEY_PROD_ID, prodId);
            cmd.prepare(SynchTechLeistungenCommand.KEY_VERLAUF_ID, verlaufId);
            cmd.prepare(SynchTechLeistungenCommand.KEY_EXECUTE_LS_COMMANDS, (executeLsCommands) ? Boolean.TRUE
                    : Boolean.FALSE);
            cmd.prepare(AbstractLeistungCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(SynchTechLeistungenCommand.KEY_REAL_DATE, when);
            cmd.prepare(SynchTechLeistungenCommand.KEY_AUFTRAG_AKTION, auftragAktion);
            cmd.execute();

            return ((IWarningAware) cmd).getWarnings();

        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Abgleich der Leistungen des Auftrags ist ein Fehler " + "aufgetreten:\n"
                    + e.getMessage(), e);
        }
    }

    @Override
    public AKWarnings synchTechLeistung4Auftrag(Long ccAuftragId, Long prodId, Long verlaufId,
            boolean executeLsCommands, Long sessionId, List<LeistungsDiffView> diffs) throws StoreException {
        return synchTechLeistung4Auftrag(ccAuftragId, prodId, verlaufId, executeLsCommands, sessionId, diffs, null,
                null);
    }

    @Override
    public AKWarnings synchTechLeistung4Auftrag(Long ccAuftragId, Long prodId, Date realisierungsTermin,
            boolean executeLsCommands, Long sessionId, List<LeistungsDiffView> diffs, AuftragAktion auftragAktion)
            throws StoreException {
        return synchTechLeistung4Auftrag(ccAuftragId, prodId, null, executeLsCommands, sessionId, diffs,
                realisierungsTermin, auftragAktion);
    }

    @Override
    public LeistungsDiffCheck checkLeistungsDiffs(List<LeistungsDiffView> leistungsDiff, Long sessionId)
            throws HurricanServiceCommandException {
        if (CollectionTools.isEmpty(leistungsDiff)) {
            return null;
        }

        try {
            LeistungsDiffCheck retVal = new LeistungsDiffCheck();
            for (LeistungsDiffView diff : leistungsDiff) {
                String commandTyp = (diff.isZugang()) ? ServiceCommand.COMMAND_TYPE_LS_CHECK_ZUGANG
                        : ServiceCommand.COMMAND_TYPE_LS_CHECK_KUENDIGUNG;

                List<ServiceCommand> commands = chainService.findServiceCommands4Reference(diff.getTechLeistungId(),
                        TechLeistung.class, commandTyp);
                if (CollectionTools.isNotEmpty(commands)) {
                    for (ServiceCommand cmd : commands) {
                        IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                        if (serviceCmd == null) {
                            throw new HurricanServiceCommandException("Fuer das definierte ServiceCommand "
                                    + cmd.getName() + " konnte kein Objekt geladen werden!\n"
                                    + "Zu synchronisierende Leistungen koennen nicht geprueft werden!");
                        }

                        serviceCmd.prepare(AbstractLeistungCommand.KEY_AUFTRAG_ID, diff.getAuftragId());
                        serviceCmd.prepare(AbstractLeistungCommand.KEY_SESSION_ID, sessionId);
                        serviceCmd.prepare(AbstractLeistungCommand.KEY_TECH_LEISTUNG_ID, diff.getTechLeistungId());
                        Object result = serviceCmd.execute();

                        if (result instanceof ServiceCommandResult) {
                            ServiceCommandResult cmdResult = (ServiceCommandResult) result;
                            if (!cmdResult.isOk()) {
                                retVal.setOk(false);
                                retVal.addMessages(cmdResult.getMessage());
                                retVal.setLastChangeDate((diff.getAktivBis() != null) ? diff.getAktivBis() : diff
                                        .getAktivVon());
                            }
                        }
                    }
                }
            }
            return retVal;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Bei der Ueberpruefung der zu synchronisierenden "
                    + "Leistungen ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Long> findAbtIds4VerlaufTechLs(Long verlaufId) throws FindException {
        if (verlaufId == null) {
            return null;
        }
        try {
            List<TechLeistung> konfigs = getTechLeistungDAO().findTechLeistungen4Verlauf(verlaufId);
            if (konfigs != null) {
                Set<Long> abtIds = new HashSet<>();
                for (TechLeistung blk : konfigs) {
                    abtIds.addAll(blk.getAbteilungIdSet());
                }

                return new ArrayList<>(abtIds);
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public Integer getCountEndgeraetPort(@Nonnull Long auftragId, @Nonnull Date when) throws FindException {
        return getAssignedQuantity(auftragId, when, null, TechLeistung.TYP_EG_PORT, TechLeistung.TYP_EG_PORT_ADD);
    }

    @Override
    public
    @Nonnull
    Integer getAssignedQuantity(Long auftragId, Date activeDate, Long extLeistungNoFilter, String... techLsTypes)
            throws FindException {
        // @formatter:off
        List<Auftrag2TechLeistung> auftrag2TechLeistungen = findAuftrag2TechLeistungen(
                auftragId,
                techLsTypes,
                activeDate,
                true);
        // @formatter:on

        if (CollectionTools.isEmpty(auftrag2TechLeistungen)) {
            return 0;
        }
        else {
            int count = 0;
            for (Auftrag2TechLeistung a2tl : auftrag2TechLeistungen) {
                if (extLeistungNoFilter != null) {
                    TechLeistung techLeistung = findTechLeistung(a2tl.getTechLeistungId());
                    if ((techLeistung != null)
                            && NumberTools.equal(extLeistungNoFilter, techLeistung.getExternLeistungNo())) {
                        count += a2tl.getQuantity();
                    }
                }
                else {
                    count += a2tl.getQuantity();
                }
            }

            return count;
        }
    }

    /*
     * Ermittelt die technische Leistung zu einer bestimmten ID. <br> Die Ermittlung erfolgt ueber eine statische Map.
     * Beim ersten Aufruf der Methode wird diese statische Map geladen.
     *
     * @param id
     *
     * @return
     */
    private TechLeistung getTechLeistung(Long id) {
        // Double-check idiom for lazy initialization
        Map<Long, TechLeistung> result = techLsMap;
        if (result == null) {
            synchronized (CCLeistungsServiceImpl.class) {
                result = techLsMap;
                if (result == null) {
                    List<TechLeistung> tls;
                    try {
                        tls = findTechLeistungen(true);
                    }
                    catch (FindException e) {
                        return null;
                    }
                    result = new HashMap<>();
                    CollectionMapConverter.convert2Map(tls, result, "getId", null);
                    techLsMap = result;
                }
            }
        }
        return techLsMap.get(id);
    }

    @Override
    public boolean checkMustAccountBeLocked(Long auftragId) throws FindException {
        List<TechLeistung> techLsVoips = findTechLeistungen4Auftrag(auftragId, TechLeistung.TYP_VOIP, true), techLsUpstreams = findTechLeistungen4Auftrag(
                auftragId, TechLeistung.TYP_UPSTREAM, true);
        if (CollectionUtils.isEmpty(techLsVoips) || CollectionUtils.isEmpty(techLsUpstreams)) {
            return false;
        }
        validateTechLsOfAuftrag(techLsVoips, techLsUpstreams);
        TechLeistung techLsVoip = techLsVoips.iterator().next(), techLsUpstream = techLsUpstreams.iterator().next();

        if (techLsUpstream.getLongValue() == null) {
            throw new FindException(
                    "Kein Wert für die techn. Leistung vom Typ Upstream hinterlegt(LONG_VALUE == null)!");
        }

        boolean pmx = techLsVoip.getId().equals(TechLeistung.ID_VOIP_PMX), upstreamLessOrEqual5Mb = techLsUpstream
                .getLongValue() <= 5000;

        return (pmx && upstreamLessOrEqual5Mb);
    }

    private void validateTechLsOfAuftrag(List<TechLeistung> techLsVoips, List<TechLeistung> techLsUpstreams)
            throws FindException {
        StringBuilder msg = new StringBuilder();
        boolean mt1VoipLs = techLsVoips.size() > 1, mt1UpstrLs = techLsUpstreams.size() > 1;
        if (mt1VoipLs) {
            msg.append("Es darf pro Auftrag nur eine technische Leistung vom Typ 'VOIP' konfiguriert sein");
        }
        if (mt1UpstrLs) {
            if (!msg.toString().isEmpty()) {
                msg.append("\n");
            }
            msg.append("Es darf pro Auftrag nur eine technische Leistung vom Typ 'UPSTREAM' konfiguriert sein");
        }
        if (mt1VoipLs || mt1UpstrLs) {
            throw new FindException(msg.toString());
        }
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
     * @return Returns the techLeistungDAO.
     */
    public TechLeistungDAO getTechLeistungDAO() {
        return this.techLeistungDAO;
    }

    /**
     * @param techLeistungDAO The techLeistungDAO to set.
     */
    public void setTechLeistungDAO(TechLeistungDAO techLeistungDAO) {
        this.techLeistungDAO = techLeistungDAO;
    }

    /**
     * @return Returns the auftragTechLeistungDAO.
     */
    public Auftrag2TechLeistungDAO getAuftrag2TechLeistungDAO() {
        return this.auftrag2TechLeistungDAO;
    }

    /**
     * @param auftragTechLeistungDAO The auftragTechLeistungDAO to set.
     */
    public void setAuftrag2TechLeistungDAO(Auftrag2TechLeistungDAO auftragTechLeistungDAO) {
        this.auftrag2TechLeistungDAO = auftragTechLeistungDAO;
    }

    /**
     * @param billingAuftragService The billingAuftragService to set.
     */
    public void setBillingAuftragService(BillingAuftragService billingAuftragService) {
        this.billingAuftragService = billingAuftragService;
    }

    @Override
    public String getBandwidthString(Long auftragId) throws FindException {
        TechLeistung downstream = findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_DOWNSTREAM, true);
        TechLeistung upstream = findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_UPSTREAM, true);

        if ((downstream != null) && (upstream != null)) {
            return StringTools.join(new String[] { downstream.getName(), upstream.getName() }, " / ", false);
        }
        return null;
    }

    @Override
    public IpMode queryIPMode(Long auftragId, @Nullable LocalDate when) {
        Set<Long> techLeistungIds = findTechLeistungenNo4Auftrag(auftragId, when);
        return IpMode.fromLeistungen(techLeistungIds);
    }

    @Override
    public boolean deviceNecessary(@NotNull Long auftragId, @NotNull Date execDate) throws FindException {
        // return false: Auftrag ist Leistung VOIP_TK, VOIP_PMX
        // zugeordnet --> IAD Device (Fritzbox) nicht notwendig
        return !(isTechLeistungActive(auftragId, TechLeistung.ExterneLeistung.VOIP_TK.leistungNo, execDate)
                || isTechLeistungActive(auftragId, TechLeistung.ExterneLeistung.VOIP_PMX.leistungNo, execDate));
    }

    @Override
    public boolean hasVoipLeistungFromThenOn(@NotNull Long auftragId, @NotNull Date date) throws FindException {
        List<TechLeistung> leistungen = findTechLeistungen4Auftrag(auftragId, TechLeistung.TYP_VOIP, date);
        if (CollectionTools.isNotEmpty(leistungen)) {
            return true;
        }

        Date firstFutureTechLsDate = getFirstFutureTechLsDate(auftragId);
        if (firstFutureTechLsDate == null) {
            return false;
        }

        leistungen = findTechLeistungen4Auftrag(auftragId, TechLeistung.TYP_VOIP, firstFutureTechLsDate);
        return CollectionTools.isNotEmpty(leistungen);
    }

    @Override
    public boolean hasVoipLeistungFromThenOn(@NotNull List<LeistungsDiffView> diffs, @NotNull Date date) throws FindException {
        List<TechLeistung> leistungen = findTechLeistungen4Auftrag(diffs, TechLeistung.TYP_VOIP, date);
        if (CollectionTools.isNotEmpty(leistungen)) {
            return true;
        }

        Date firstFutureTechLsDate = getFirstFutureTechLsDate(diffs);
        if (firstFutureTechLsDate == null) {
            return false;
        }

        leistungen = findTechLeistungen4Auftrag(diffs, TechLeistung.TYP_VOIP, firstFutureTechLsDate);
        return CollectionTools.isNotEmpty(leistungen);
    }

    @Override
    public Date getFirstFutureTechLsDate(Long auftragId) throws FindException {
        List<Auftrag2TechLeistung> assignedTechLeistungen = findAuftrag2TechLeistungen(auftragId, null, true);
        Date firstDate = null;
        for (Auftrag2TechLeistung assignedTechLs : assignedTechLeistungen) {
            if ((firstDate == null) || DateTools.isDateBefore(assignedTechLs.getAktivVon(), firstDate)) {
                firstDate = assignedTechLs.getAktivVon();
            }
        }
        return DateTools.isDateAfter(firstDate, new Date()) ? firstDate : null;
    }

    @Override
    public Date getFirstFutureTechLsDate(@NotNull List<LeistungsDiffView> diffs) throws FindException {
        Date firstDate = null;
        for (LeistungsDiffView diff : diffs) {
            if ((firstDate == null) || DateTools.isDateBefore(diff.getAktivVon(), firstDate)) {
                firstDate = diff.getAktivVon();
            }
        }
        return DateTools.isDateAfter(firstDate, new Date()) ? firstDate : null;
    }

    public CCAuftragService getCcAuftragService() {
        return ccAuftragService;
    }

    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }
}
