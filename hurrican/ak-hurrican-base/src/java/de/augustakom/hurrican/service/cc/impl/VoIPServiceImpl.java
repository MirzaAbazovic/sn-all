/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 09:49:46
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragVoIPDAO;
import de.augustakom.hurrican.dao.cc.AuftragVoIPDNDAO;
import de.augustakom.hurrican.dao.cc.EndgeraetPortDAO;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.BlockDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.impl.command.voip.AssignVoIPDNs2EGPortsCommand;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;
import de.augustakom.hurrican.service.utils.HistoryHelper;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Service-Implementierung von <code>VoIPService</code>.
 *
 *
 */
@CcTxRequired
public class VoIPServiceImpl extends DefaultCCService implements VoIPService {

    private static final Logger LOGGER = Logger.getLogger(VoIPServiceImpl.class);

    @Resource(name = "auftragVoIPDAO")
    private AuftragVoIPDAO auftragVoIPDAO;

    @Resource(name = "auftragVoIPDNDAO")
    private AuftragVoIPDNDAO auftragVoIPDNDAO;

    @Resource(name = "endgeraetPortDao")
    private EndgeraetPortDAO endgeraetPortDao;

    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.SIPDomainService")
    private SIPDomainService sipDomainService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public AuftragVoIP createVoIP4Auftrag(Long auftragId, Long sessionId) throws StoreException {
        if (auftragId == null) {
            throw new StoreException(StoreException.UNABLE_TO_CREATE_VOIP,
                    new Object[] { "Auftrags-ID nicht angegeben!" });
        }

        try {
            AuftragVoIP existingVoIP = findVoIP4Auftrag(auftragId);
            if (existingVoIP != null) {
                if (BooleanTools.nullToFalse(existingVoIP.getIsActive())) {
                    return existingVoIP;
                }
                else {
                    // VoIP-Daten bereits inaktiv --> History-Datum setzen
                    existingVoIP.setGueltigBis(new Date());
                    saveAuftragVoIP(existingVoIP, false, sessionId);
                }
            }

            AuftragVoIP voip = new AuftragVoIP();
            voip.setAuftragId(auftragId);
            voip.setIsActive(Boolean.TRUE);
            voip.setUserW(getLoginNameSilent(sessionId));
            HistoryHelper.setHistoryData(voip, new Date());

            auftragVoIPDAO.store(voip);
            return voip;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_VOIP, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public AuftragVoIP findVoIP4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) {
            return null;
        }
        try {
            return auftragVoIPDAO.findAuftragVoIP(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragVoIP saveAuftragVoIP(AuftragVoIP toSave, boolean createHistory, Long sessionId)
            throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            if (createHistory && (toSave.getId() != null)) {
                Date now = new Date();
                return auftragVoIPDAO.update4History(toSave, toSave.getId(), now);
            }
            else {
                toSave.setUserW(getLoginNameSilent(sessionId));
                HistoryHelper.checkHistoryDates(toSave);
                auftragVoIPDAO.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragVoIPDN createVoIPDN4Auftrag(Long auftragId, Long dnNoOrig) throws StoreException {
        return createVoIPDN4Auftrag(auftragId, dnNoOrig, generateSipPassword());
    }

    @Override
    public void createVoIPLoginDaten(VoipDnPlan newPlan, VoipDnPlan activePlan, final String onkz, final String dnBase,
            final String rangeFrom, final String sipDomain) throws StoreException{
        try {
            if (shouldCreateSipLoginData(newPlan, activePlan)) {
                String rangeFromOrNull = null;
                VoipDnBlock voipDnBlock = getVoipDnBlockWithZentrale(newPlan);
                if (voipDnBlock == null) {
                    voipDnBlock = newPlan.getVoipDnBlocks().get(0);
                    rangeFromOrNull = rangeFrom;
                }

                if (StringUtils.isBlank(newPlan.getSipHauptrufnummer())) {
                    newPlan.setSipHauptrufnummer(generateSipHauptrufnummer(onkz, dnBase, voipDnBlock.getAnfang()));
                }
                if (StringUtils.isBlank(newPlan.getSipLogin())) {
                    newPlan.setSipLogin(generateSipLogin(onkz, dnBase, voipDnBlock.getAnfang(), rangeFromOrNull, sipDomain));
                }
            } else {
                newPlan.setSipLogin(activePlan.getSipLogin());
                newPlan.setSipHauptrufnummer(activePlan.getSipHauptrufnummer());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_VOIP, new Object[] { e.getMessage() }, e);
        }
    }

    /**
     * Tests rufnummern block arrangements in order to decide if new sip login data is needed. This is the case when
     * zentrale has changed.
     */
    private boolean shouldCreateSipLoginData(VoipDnPlan newPlan, VoipDnPlan activePlan) {
        if (activePlan == null || StringUtils.isBlank(activePlan.getSipLogin())) {
            return true;
        }

        VoipDnBlock newBlockZentrale = getVoipDnBlockWithZentrale(newPlan);
        VoipDnBlock activeBlockZentrale = getVoipDnBlockWithZentrale(activePlan);
        //noinspection RedundantIfStatement
        if (activeBlockZentrale != null && newBlockZentrale != null &&
                activeBlockZentrale.getAnfang().equals(newBlockZentrale.getAnfang())) {
            return false;
        }

        return true;
    }

    private VoipDnBlock getVoipDnBlockWithZentrale(VoipDnPlan dnPlan) {
        for (VoipDnBlock block : dnPlan.getVoipDnBlocks() ){
            if (block.getZentrale()) {
                return block;
            }
        }

        return null;
    }

    public String generateSipHauptrufnummer(final String onkz, final String dnBase, final String start) {
        return  countryCodeDe() + onkz.substring(1) + dnBase + Strings.nullToEmpty(start);
    }

    private String generateSipLogin(final String onkz, final String dnBase, final String start, final String rangeFrom, final String sipDomain) {
        String localPart;
        if(rangeFrom != null){
            localPart = countryCodeDe() + onkz.substring(1) + dnBase + Strings.nullToEmpty(rangeFrom);
        } else{
            localPart = generateSipHauptrufnummer(onkz, dnBase, start);
        }
        return localPart + "@" + sipDomain;
    }

    @SuppressWarnings("SameReturnValue")
    private String countryCodeDe() {
        return "+49";
    }

    @Override
    public AuftragVoIPDN createVoIPDN4Auftrag(Long auftragId, Long dnNoOrig, String pw) throws StoreException {
        if ((auftragId == null) || (dnNoOrig == null) || StringUtils.isBlank(pw)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            AuftragVoIPDN existingVoIPDN = findByAuftragIDDN(auftragId, dnNoOrig);
            if (existingVoIPDN != null) {
                if (existingVoIPDN.getSipDomain() == null) {
                    existingVoIPDN.setSipDomain(sipDomainService.findDefaultSIPDomain4Auftrag(auftragId));
                }
                return existingVoIPDN;
            }

            AuftragVoIPDN voipDn = new AuftragVoIPDN();
            voipDn.setDnNoOrig(dnNoOrig);
            voipDn.setAuftragId(auftragId);
            voipDn.setSipPassword(pw);
            voipDn.setSipDomain(sipDomainService.findDefaultSIPDomain4Auftrag(auftragId));

            auftragVoIPDNDAO.store(voipDn);

            return voipDn;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_VOIP, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public AuftragVoIPDN findByAuftragIDDN(Long auftragId, Long dnNoOrig) throws FindException {
        if (auftragId == null) {
            return null;
        }
        try {
            return auftragVoIPDNDAO.findByAuftragIDDN(auftragId, dnNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @NotNull
    public List<AuftragVoIPDN> findByAuftragId(Long auftragId) throws FindException {
        if (auftragId == null) {
            return Collections.emptyList();
        }
        try {
            return auftragVoIPDNDAO.findAuftragVoIPDN(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    /**
     * liefert eine Liste an {@link Rufnummer}n aus Taifun. Diese Liste beinhaltet nur Rufnummern mit
     * Auftragszuordnung.
     */
    private List<Rufnummer> findTyp60RufnummerByAuftragId(Long auftragId) throws ServiceNotFoundException,
            FindException {
        AuftragDaten ad = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if (ad == null) {
            throw new FindException("Auftragsdaten konnten nicht ermittelt werden.");
        }

        RufnummerService serviceRn = getBillingService(RufnummerService.class);
        List<Rufnummer> rufnummern = serviceRn.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG, new Object[] {
                ad.getAuftragNoOrig(), Boolean.FALSE });

        return rufnummern.stream()
                .filter(in -> (in.getAuftragNoOrig() != null)
                        && (in.getOeNoOrig() != null)
                        && in.getOeNoOrig().equals(Rufnummer.OE__NO_DEFAULT))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuftragVoipDNView> createVoipDNView(Collection<AuftragVoIPDN> dialNumbers) throws FindException,
            ServiceNotFoundException {
        List<AuftragVoipDNView> result = new ArrayList<>();
        if (CollectionTools.isEmpty(dialNumbers)) {
            return result;
        }
        else {
            Long auftragId = dialNumbers.iterator().next().getAuftragId();
            List<Rufnummer> rufnummern = findTyp60RufnummerByAuftragId(auftragId);
            for (Rufnummer rufNummer : rufnummern) {
                AuftragVoIPDN voipDN = getAuftragVoIPDN4DN(rufNummer.getDnNoOrig(), dialNumbers);
                if (voipDN != null) {
                    result.add(createRufnummerView(rufNummer, voipDN));
                }
            }
        }
        return result;
    }

    @Override
    public List<AuftragVoipDNView> findVoIPDNView(Long auftragId) throws FindException {
        if (auftragId == null) {
            return Collections.emptyList();
        }

        List<AuftragVoipDNView> result;
        try {
            List<AuftragVoIPDN> pwdResult = auftragVoIPDNDAO.findAuftragVoIPDN(auftragId);
            if (CollectionUtils.isEmpty(pwdResult)) {
                result = Collections.emptyList();
            }
            else {
                result = createVoipDNView(pwdResult);
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return result;
    }

    @Override
    public void saveSipDomainOnVoIPDNs(List<AuftragVoipDNView> auftragVoipDNViews, Long auftragId)
            throws StoreException {
        if (CollectionTools.isEmpty(auftragVoipDNViews) || (auftragId == null)) {
            return;
        }
        AuftragVoIPDN voipRufnummer;
        try {
            for (AuftragVoipDNView auftragVoipDNView : auftragVoipDNViews) {
                voipRufnummer = auftragVoIPDNDAO.findByAuftragIDDN(auftragId, auftragVoipDNView.getDnNoOrig());
                Reference sipDomain = referenceService.findReference(Reference.REF_TYPE_SIP_DOMAIN_TYPE,
                        auftragVoipDNView.getSipDomain().getStrValue());
                if (sipDomain == null) {
                    throw new StoreException("Rufnummer kann nicht ohne Angabe einer SIP-Domain gespeichert werden!");
                }
                voipRufnummer.setSipDomain(sipDomain);
                auftragVoIPDNDAO.store(voipRufnummer);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveAuftragVoIPDNs(Collection<AuftragVoipDNView> auftragVoipDNViews) throws StoreException {
        boolean paramsValid = !(CollectionTools.isEmpty(auftragVoipDNViews));
        if (paramsValid) {
            try {
                for (AuftragVoipDNView auftragVoipDNView : auftragVoipDNViews) {
                    AuftragVoIPDN auftragVoIPDN = auftragVoIPDNDAO.findByAuftragIDDN(auftragVoipDNView.getAuftragId(),
                            auftragVoipDNView.getDnNoOrig());
                    if (auftragVoIPDN == null) {
                        return;
                    }
                    final boolean sipDomainChanged = hasSipDomainChanged(auftragVoipDNView, auftragVoIPDN);
                    updatePortSelection(auftragVoipDNView, auftragVoIPDN);
                    updateSipDomainAndPassword(auftragVoipDNView, auftragVoIPDN);
                    updateRufnummernplaene(auftragVoipDNView, auftragVoIPDN, sipDomainChanged);
                }
                // Neue Portbelegung ist nicht eindeutig -> Exception / rollback
                if (!validatePortzuordnungEindeutig(this.findVoIPDNView(auftragVoipDNViews.iterator().next()
                        .getAuftragId()))) {
                    throw new StoreException(
                            "Es existieren zu einer Rufnummer mindestens zwei Portbelegungen gleichzeitig!");
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
    }

    boolean hasSipDomainChanged(@Nonnull AuftragVoipDNView auftragVoipDNView, @Nonnull AuftragVoIPDN auftragVoIPDN) {
        return ObjectUtils.notEqual(auftragVoipDNView.getSipDomain(), auftragVoIPDN.getSipDomain());
    }

    @Override
    public void updateRufnummernplaene(final AuftragVoipDNView auftragVoipDNView,
            final AuftragVoIPDN auftragVoIPDN, boolean sipDomainChanged) throws StoreException {
        final List<VoipDnPlan> toRemove = Lists.newArrayList(auftragVoIPDN.getRufnummernplaene());

        if (sipDomainChanged) {
            updateSipDomainForVoipDNView(auftragVoipDNView);
        }
        for (final VoipDnPlanView voipDnPlanView : auftragVoipDNView.getVoipDnPlanViewsDesc()) {
            final VoipDnPlan voipDnPlan = voipDnPlanView.toEntity();
            if (voipDnPlan.getId() == null) {
                auftragVoIPDN.addRufnummernplan(voipDnPlan);
            }
            else {
                for (final VoipDnPlan voipDnPlanAct : auftragVoIPDN.getRufnummernplaene()) {
                    if (voipDnPlanAct.equals(voipDnPlan)) {
                        toRemove.remove(voipDnPlan);
                        voipDnPlanAct.setGueltigAb(voipDnPlan.getGueltigAb());
                        voipDnPlanAct.getVoipDnBlocks().clear();
                        voipDnPlanAct.getVoipDnBlocks().addAll(voipDnPlan.getVoipDnBlocks());

                        validateAndUpdateVoipLoginDaten(voipDnPlanView, voipDnPlanAct);

                        break;
                    }
                }
            }
        }

        toRemove.stream().filter(voipDnPlan -> DateTools.isDateAfterOrEqual(voipDnPlan.getGueltigAb(), new Date()))
                .forEach(voipDnPlan -> {
                    auftragVoIPDN.getRufnummernplaene().remove(voipDnPlan);
                    auftragVoIPDNDAO.deleteVoipDnPlan(voipDnPlan);
                });
    }

    void updateSipDomainForVoipDNView(AuftragVoipDNView auftragVoipDNView) {
        List<VoipDnPlanView> validVoipDnPlaene = auftragVoipDNView.getValidVoipDnPlanViews();
        String newSipDomain = auftragVoipDNView.getSipDomain() != null ? auftragVoipDNView.getSipDomain().getStrValue()
                : null;
        for (VoipDnPlanView voipDnPlanView : validVoipDnPlaene) {
            voipDnPlanView.setSipLogin(updateSipLogin(voipDnPlanView.getSipLogin(), newSipDomain));
        }
    }

    private void updatePortSelection(final AuftragVoipDNView auftragVoipDNView, final AuftragVoIPDN auftragVoIPDN)
            throws StoreException {
        final List<SelectedPortsView> selectedPorts = auftragVoipDNView.getSelectedPorts();
        for (final SelectedPortsView selectedPort : selectedPorts) {
            final List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPortsToCreate = Lists.newArrayList();
            for (int i = 0; i < selectedPort.getNumberOfPorts(); i++) {
                final SelectedPortsView.SelectedPort port = selectedPort.getPortAtIndex(i);
                if (port.getAuftragVoipDn2EgPortId() != null) {
                    updateExistingPortSelection(selectedPort, i, port);
                }
                else if (port.isSelected()) {
                    auftragVoIPDN2EGPortsToCreate.add(createNewPortSelection(auftragVoIPDN, selectedPort, i));
                }
            }
            auftragVoIPDN2EGPortsToCreate.forEach(auftragVoIPDNDAO::store);
        }
    }

    private AuftragVoIPDN2EGPort createNewPortSelection(final AuftragVoIPDN auftragVoIPDN,
            final SelectedPortsView selectedPort, final int i) throws StoreException {
        if (DateTools.isDateBefore(selectedPort.getValidTo(), selectedPort.getValidFrom())) {
            throw new StoreException("'Aktiv bis' darf nicht vor 'Aktiv von' sein!");
        }
        final AuftragVoIPDN2EGPort newSelectedPort = new AuftragVoIPDN2EGPort();
        newSelectedPort.setEgPort(Iterables.getOnlyElement(
                endgeraetPortDao.findByNumbers(ImmutableList.of(i + 1))));
        newSelectedPort.setValidFrom(selectedPort.getValidFrom());
        newSelectedPort.setValidTo(selectedPort.getValidTo());
        newSelectedPort.setAuftragVoipDnId(auftragVoIPDN.getId());
        final List<AuftragVoIPDN2EGPort> existingPortSelectionsToEnd =
                auftragVoIPDNDAO.findAuftragVoIpdn2EgPorts(auftragVoIPDN.getId(),
                        selectedPort.getValidFrom());
        for (final AuftragVoIPDN2EGPort toEnd : existingPortSelectionsToEnd) {
            toEnd.setValidTo(selectedPort.getValidFrom());
        }
        return newSelectedPort;
    }

    private void updateExistingPortSelection(final SelectedPortsView selectedPort, final int i,
            final SelectedPortsView.SelectedPort port) throws StoreException {
        final AuftragVoIPDN2EGPort dn2EgPort =
                auftragVoIPDNDAO.findById(port.getAuftragVoipDn2EgPortId(), AuftragVoIPDN2EGPort.class);
        if (port.isSelected()) {
            if (DateTools.isDateBefore(selectedPort.getValidTo(), selectedPort.getValidFrom())) {
                throw new StoreException("'Aktiv bis' darf nicht vor 'Aktiv von' sein!");
            }
            dn2EgPort.setValidFrom(selectedPort.getValidFrom());
            dn2EgPort.setValidTo(selectedPort.getValidTo());
            if (dn2EgPort.getEgPort().getNumber() != (i + 1)) {
                final EndgeraetPort neuerPort =
                        Iterables.getOnlyElement(endgeraetPortDao.findByNumbers(ImmutableList.of(i + 1)));
                dn2EgPort.setEgPort(neuerPort);
            }
        }
    }

    private void updateSipDomainAndPassword(@Nonnull final AuftragVoipDNView auftragVoipDNView, @Nonnull final AuftragVoIPDN auftragVoIPDN) throws StoreException, FindException {
        if (auftragVoipDNView.getSipDomain() == null) {
            throw new StoreException(
                    String.format(
                            "Der Rufnummer %s/%s%s ist keine SIP-Domäne zugeordnet.%nSpeichern ist deshalb nicht möglich!",
                            auftragVoipDNView.getOnKz(), auftragVoipDNView.getDnBase(),
                            auftragVoipDNView.getDirectDial())
            );
        }

        Reference sipDomain = referenceService.findReference(Reference.REF_TYPE_SIP_DOMAIN_TYPE,
                auftragVoipDNView.getSipDomain().getStrValue());
        if (sipDomain == null) {
            throw new StoreException(
                    "Rufnummer kann nicht ohne Angabe einer SIP-Domain gespeichert werden!");
        }
        auftragVoIPDN.setSipDomain(sipDomain);

        AKWarnings warnings = new AKWarnings();
        if (auftragVoipDNView.getSipPassword() != null && valdiateSipPassword(auftragVoipDNView.getSipPassword(), warnings)) {
            auftragVoIPDN.setSipPassword(auftragVoipDNView.getSipPassword());
        }

        if (!warnings.isEmpty()){
            throw new StoreException(warnings.getWarningsAsText());
        }

        auftragVoIPDNDAO.store(auftragVoIPDN);
    }

    private void validateAndUpdateVoipLoginDaten(VoipDnPlanView voipDnPlanView, VoipDnPlan plan) throws StoreException {
        AKWarnings warnings = new AKWarnings();

        if (voipDnPlanView.getSipLogin() != null && !voipDnPlanView.getSipLogin().matches("^[a-zA-Z0-9+-.:@]+")) {
            warnings.addAKWarning(this, "SipLogin: nur Ziffern von 0 bis 9, Buchstaben groß/klein a bis z Sonderzeichen nur \"+ - . : @\" erlaubt. " +
                    "Speichern ist nicht möglich!");
        } else {
            //noinspection ConstantConditions
            plan.setSipLogin(voipDnPlanView.getSipLogin());
        }

        if (voipDnPlanView.getSipHauptrufnummer() != null && !voipDnPlanView.getSipHauptrufnummer().matches("^[0-9+]+")) {
            warnings.addAKWarning(this, "SipHauptrufnummer: nur Ziffern von 0 bis 9, Sonderzeichen nur \"+\" erlaubt. " +
                    "Speichern ist nicht möglich!");
        } else {
            //noinspection ConstantConditions
            plan.setSipHauptrufnummer(voipDnPlanView.getSipHauptrufnummer());
        }

        if (!warnings.isEmpty()){
            throw new StoreException(warnings.getWarningsAsText());
        }
    }


    private boolean valdiateSipPassword(String sipPassword, AKWarnings warnings) {
        if (sipPassword.length() < 5 || sipPassword.length() > 16) {
            warnings.addAKWarning(this, "SipPasswort: mindestens 5, maximal 16 Stellen erlaubt." +
                    "Speichern ist nicht möglich!");
            return false;
        }

        if (!sipPassword.matches("^[a-zA-Z0-9!#$%'()\\[\\]*+,-./:;=?@^_`~]+")) {
            warnings.addAKWarning(this, "SipPasswort: nur Ziffern von 0 bis 9, Buchstaben groß/klein a bis z erlaubt und" +
                    "Sonderzeichen nur \"!#$%()[]'*+,-./:;=?@^_`~\" erlaubt. " +
                    "Speichern ist nicht möglich!");
            return false;
        }
        return true;
    }

    /**
     * Erzeugt aus einem Voip-Rufnummern Model eine View
     */
    private AuftragVoipDNView createRufnummerView(Rufnummer rufNummer, AuftragVoIPDN voipDN)
            throws FindException {
        BlockDNView blockNr = (rufNummer.isBlock()) ? new BlockDNView(rufNummer.getDirectDial(),
                rufNummer.getRangeFrom(), rufNummer.getRangeTo()) : BlockDNView.NO_BLOCK;

        AuftragVoipDNView view = new AuftragVoipDNView();
        view.setDnNoOrig(rufNummer.getDnNoOrig());
        view.setAuftragId(voipDN.getAuftragId());
        view.setSipDomain(voipDN.getSipDomain());
        view.setSipPassword(voipDN.getSipPassword());
        view.setOnKz(rufNummer.getOnKz());
        view.setDnBase(rufNummer.getDnBase());
        view.setGueltigVon(rufNummer.getGueltigVon());
        view.setGueltigBis(rufNummer.getGueltigBis());
        view.setHistStatus(rufNummer.getHistStatus());
        view.setMainNumber(rufNummer.isMainNumber());
        view.setBlock(blockNr);
        view.setTaifunDescription(rufNummer.getRufnummer());

        for (final VoipDnPlan voipDnPlan : voipDN.getRufnummernplaene()) {
            view.addVoipDnPlanView(new VoipDnPlanView(rufNummer.getOnKz(), rufNummer.getDnBase(), voipDnPlan));
        }

        final List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts =
                findAuftragVoIPDN2EGPortSortedByValidFromValidTo(voipDN.getId());

        List<AuftragVoIPDN2EGPort> dn2EgPort4TimeRange = Lists.newArrayList();
        // Portbelegung(en) fuer evtl. unterschiedliche Zeitraeume aufbauen
        for (final AuftragVoIPDN2EGPort auftragVoIPDN2EGPort : auftragVoIPDN2EGPorts) {
            if (dn2EgPort4TimeRange.isEmpty()) {
                dn2EgPort4TimeRange.add(auftragVoIPDN2EGPort);
            }
            else {
                final AuftragVoIPDN2EGPort prev = dn2EgPort4TimeRange.get(0);
                if (DateTools.isDateEqual(prev.getValidFrom(), auftragVoIPDN2EGPort.getValidFrom())
                        && DateTools.isDateEqual(prev.getValidTo(), auftragVoIPDN2EGPort.getValidTo())) {
                    dn2EgPort4TimeRange.add(auftragVoIPDN2EGPort);
                }
                else {
                    final Integer portCount = leistungsService.getCountEndgeraetPort(voipDN.getAuftragId(),
                            prev.getValidFrom());
                    SelectedPortsView selectedPorts4TimeRange = SelectedPortsView.createFromAuftragVoipDn2EgPorts(
                            dn2EgPort4TimeRange,
                            prev.getValidFrom(), prev.getValidTo(), portCount);
                    view.addSelectedPort(selectedPorts4TimeRange);

                    dn2EgPort4TimeRange = Lists.newArrayList();
                    dn2EgPort4TimeRange.add(auftragVoIPDN2EGPort);
                }
            }
        }
        if (!dn2EgPort4TimeRange.isEmpty()) {
            final Integer portCount = leistungsService.getCountEndgeraetPort(voipDN.getAuftragId(),
                    dn2EgPort4TimeRange.get(0).getValidFrom());
            SelectedPortsView selectedPorts4TimeRange = SelectedPortsView.createFromAuftragVoipDn2EgPorts(
                    dn2EgPort4TimeRange,
                    dn2EgPort4TimeRange.get(0).getValidFrom(), dn2EgPort4TimeRange.get(0).getValidTo(), portCount);
            view.addSelectedPort(selectedPorts4TimeRange);
        }

        return view;
    }

    private List<AuftragVoIPDN2EGPort> findAuftragVoIPDN2EGPortSortedByValidFromValidTo(final Long auftragVoipDnId) {
        return Ordering.from(AuftragVoIPDN2EGPortValidFromToComparator.instance)
                .immutableSortedCopy(
                        this.auftragVoIPDNDAO.findByProperty(AuftragVoIPDN2EGPort.class, "auftragVoipDnId",
                                auftragVoipDnId)
                );
    }

    @Override
    public List<AuftragVoIPDN2EGPort> findAuftragVoIPDN2EGPorts(final Long auftragVoipDnId) {
        return this.auftragVoIPDNDAO.findByProperty(AuftragVoIPDN2EGPort.class, "auftragVoipDnId", auftragVoipDnId);
    }

    @Override
    public List<AuftragVoIPDN2EGPort> findAuftragVoIPDN2EGPortsValidAt(final Long auftragVoipDnId, Date validAt) {
        return this.auftragVoIPDNDAO.findAuftragVoIpdn2EgPorts(auftragVoipDnId, validAt);
    }

    /**
     * Ermittelt den relevanten Passwort-Eintrag aus der Liste zur angegebenen DN__NO.
     *
     * @param dnNoOrig ID der Rufnummer, zu der der PW-Eintrag ermittelt werden soll
     * @param voipDNs  Liste der DN/Passwort-Mappings, aus der der Eintrag ermittelt werden soll
     */
    private AuftragVoIPDN getAuftragVoIPDN4DN(Long dnNoOrig, Collection<AuftragVoIPDN> voipDNs) {
        if (CollectionTools.isNotEmpty(voipDNs)) {
            for (AuftragVoIPDN voipDN : voipDNs) {
                if (NumberTools.equal(dnNoOrig, voipDN.getDnNoOrig())) {
                    return voipDN;
                }
            }
        }
        return null;
    }

    @Override
    public void saveAuftragVoIPDN(AuftragVoIPDN toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            auftragVoIPDNDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAuftragVoIPDAO(AuftragVoIPDAO auftragVoIPDAO) {
        this.auftragVoIPDAO = auftragVoIPDAO;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAuftragVoIPDNDAO(AuftragVoIPDNDAO auftragVoIPDNDAO) {
        this.auftragVoIPDNDAO = auftragVoIPDNDAO;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setEndgeraetPortDao(EndgeraetPortDAO endgeraetPortDao) {
        this.endgeraetPortDao = endgeraetPortDao;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLeistungsService(CCLeistungsService leistungsService) {
        this.leistungsService = leistungsService;
    }

    @Override
    public boolean validateHauprufnrToPortAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews) {
        boolean result = true;
        for (final Date when : getPortSelectionTimeRanges(auftragVoipDNViews)) {
            final List<Pair<AuftragVoipDNView, SelectedPortsView>> portSelectionsAtTime =
                    getPortSelectionsAtTime(when, auftragVoipDNViews);
            List<Integer> portsWithHauptrufNr = Lists.newArrayList();
            int portCount = portsWithHauprufNrAssigendFromViews(portSelectionsAtTime, portsWithHauptrufNr);
            result &= checkHauptrufnrToPortAssignment(portsWithHauptrufNr, portCount);
        }

        return result;
    }

    private List<Pair<AuftragVoipDNView, SelectedPortsView>> getPortSelectionsAtTime(final Date when,
            final Collection<AuftragVoipDNView> auftragVoipDNViews) {
        List<Pair<AuftragVoipDNView, SelectedPortsView>> result = Lists.newArrayList();
        for (AuftragVoipDNView auftragVoipDNView : auftragVoipDNViews) {
            boolean addNoSelection = false;
            for (SelectedPortsView portSelection : auftragVoipDNView.getSelectedPorts()) {
                if (DateTools.isDateBeforeOrEqual(portSelection.getValidFrom(), when)
                        && DateTools.isDateAfter(portSelection.getValidTo(), when)) {
                    result.add(Pair.create(auftragVoipDNView, portSelection));
                    addNoSelection = false;
                    break;
                }
                else {
                    addNoSelection = true;
                }
            }
            if (addNoSelection) {
                result.add(Pair.create(auftragVoipDNView, null));
            }
        }
        return result;
    }

    private Set<Date> getPortSelectionTimeRanges(final Collection<AuftragVoipDNView> auftragVoipDNViews) {
        final List<Date> toReturn = Lists.newArrayList();
        for (AuftragVoipDNView auftragVoipDNView : auftragVoipDNViews) {
            toReturn.addAll(auftragVoipDNView.getSelectedPorts().stream()
                    .filter(portSelection -> DateTools.isDateAfter(portSelection.getValidTo(), portSelection.getValidFrom()))
                    .map(SelectedPortsView::getValidFrom)
                    .collect(Collectors.toList()));
        }
        return Sets.newHashSet(Ordering.from(new Comparator<Date>() {
            @Override
            public int compare(final Date o1, final Date o2) {
                if (o1.before(o2)) {
                    return -1;
                }
                else if (o1.after(o2)) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        }).immutableSortedCopy(toReturn));
    }

    private int portsWithHauprufNrAssigendFromViews(
            Collection<Pair<AuftragVoipDNView, SelectedPortsView>> auftragVoipDNViews,
            List<Integer> portsWithHauptrufn) {
        int portCount = 0;
        for (Pair<AuftragVoipDNView, SelectedPortsView> view : auftragVoipDNViews) {
            final AuftragVoipDNView auftragVoipDNView = view.getFirst();
            final SelectedPortsView ports = view.getSecond();
            if (ports != null) {
                if (ports.getNumberOfPorts() > portCount) {
                    portCount = ports.getNumberOfPorts();
                }
                if (auftragVoipDNView.getMainNumber()) {
                    //noinspection Convert2streamapi
                    for (Integer portSelected : ports.getSelectedPortNumbers()) {
                        portsWithHauptrufn.add(portSelected);
                    }
                }
            }
        }
        return portCount;
    }

    private boolean checkHauptrufnrToPortAssignment(List<Integer> portsWithHauptrufNr, int portCount) {
        boolean result = true;
        for (int i = 1; i <= portCount; i++) {
            if (CollectionUtils.cardinality(i, portsWithHauptrufNr) != 1) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public void assignVoIPDNs2EGPorts(Collection<AuftragVoipDNView> auftragVoipDNViews, Long auftragId)
            throws StoreException {
        if ((auftragId == null) || CollectionUtils.isEmpty(auftragVoipDNViews)) {
            return;
        }

        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(AssignVoIPDNs2EGPortsCommand.class);
            cmd.prepare(AssignVoIPDNs2EGPortsCommand.AUFTRAG_VOIP_DN_VIEWS, auftragVoipDNViews);
            cmd.prepare(AssignVoIPDNs2EGPortsCommand.AUFTRAG_ID, auftragId);
            cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean validateRufnrCountToPortAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews) {
        boolean result = true;
        if (auftragVoipDNViews.size() > EndgeraetPort.getMaxDNsPerDefaultPort()) {
            for (final Date when : getPortSelectionTimeRanges(auftragVoipDNViews)) {
                final List<Pair<AuftragVoipDNView, SelectedPortsView>> portSelectionsAtTime =
                        getPortSelectionsAtTime(when, auftragVoipDNViews);
                Map<Integer, Integer> rufnrPerPort = rufnrPerPortFromFromViews(portSelectionsAtTime);
                result &= checkRufnrPerPortAssignment(rufnrPerPort);
            }
        }
        return result;
    }

    private Map<Integer, Integer> rufnrPerPortFromFromViews(Collection<Pair<AuftragVoipDNView, SelectedPortsView>> views) {
        Map<Integer, Integer> rufnrPerPort = new HashMap<>();
        for (Pair<AuftragVoipDNView, SelectedPortsView> view : views) {
            final SelectedPortsView ports = view.getSecond();
            if (ports != null) {
                for (int portNr : ports.getSelectedPortNumbers()) {
                    rufnrPerPort.put(portNr, (rufnrPerPort.containsKey(portNr)) ? (rufnrPerPort.get(portNr) + 1) : 1);
                }
            }
        }
        return rufnrPerPort;
    }

    private boolean checkRufnrPerPortAssignment(Map<Integer, Integer> rufnrPerPort) {
        boolean result = true;
        for (Integer count : rufnrPerPort.values()) {
            if (count > EndgeraetPort.getMaxDNsPerDefaultPort()) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean validatePortToDNAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews) {
        for (final Date when : getPortSelectionTimeRanges(auftragVoipDNViews)) {
            final List<Pair<AuftragVoipDNView, SelectedPortsView>> portSelectionsAtTime =
                    getPortSelectionsAtTime(when, auftragVoipDNViews);
            for (final Pair<AuftragVoipDNView, SelectedPortsView> portSelectionAtTime : portSelectionsAtTime) {
                final SelectedPortsView portsView = portSelectionAtTime.getSecond();
                if ((portsView == null) || ((portsView.getNumberOfPorts() > 0) && (portsView.isNoneSelected()
                        || DateTools.isDateEqual(portsView.getValidFrom(), portsView.getValidTo())))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean checkDNBlocks(Collection<AuftragVoipDNView> auftragVoipDNViews) {
        boolean foundDNs = false;
        boolean foundBlocks = false;
        for (AuftragVoipDNView voipDNView : auftragVoipDNViews) {
            if (voipDNView.isBlock()) {
                foundBlocks = true;
            }
            else {
                foundDNs = true;
            }
        }
        if (foundDNs && foundBlocks) {
            throw new IllegalArgumentException("Eine gemischte Zuordnung von Rufnummern UND Rufnummernblöcken "
                    + "ist nicht möglich!");

        }
        return foundBlocks;
    }

    @Override
    public AKWarnings validatePortAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews) {
        AKWarnings warnings = new AKWarnings();
        if (CollectionUtils.isNotEmpty(auftragVoipDNViews)) {
            // blöcke
            if (checkDNBlocks(auftragVoipDNViews)) {
                if (!validatePortToDNAssignment(auftragVoipDNViews)) {
                    warnings.addAKWarning(this, "Es wurden nicht allen Blöcken Endgeräteports zugeordnet!");
                }
            }
            // rufnummern
            else {
                if (!validateHauprufnrToPortAssignment(auftragVoipDNViews)) {
                    warnings.addAKWarning(this, "Es ist nicht jedem Endgeräteport eine Hauptrufnummer zugeordnet!");
                }
                if (!validateRufnrCountToPortAssignment(auftragVoipDNViews)) {
                    warnings.addAKWarning(this,
                            "Es sind mindestens einem Endgeräteport mehr Rufnummern zugeordnet als maximal erlaubt!");
                }
                if (!validatePortToDNAssignment(auftragVoipDNViews)) {
                    warnings.addAKWarning(this, "Es sind nicht allen Rufnummern Endgeräteports zugeordnet!");
                }
            }
        }
        return warnings;
    }

    @Override
    public boolean validatePortzuordnungEindeutig(final Collection<AuftragVoipDNView> auftragVoipDNViews) {
        boolean result = true;
        for (final AuftragVoipDNView auftragVoipDNView : auftragVoipDNViews) {
            Date minValidFrom = null, maxValidTo = null;
            final List<SelectedPortsView> sortedPortSelections =
                    Ordering.from(SelectedPortsViewValidFromToComparator.instance)
                            .immutableSortedCopy(auftragVoipDNView.getSelectedPorts());
            for (final SelectedPortsView selectedPortsView : sortedPortSelections) {
                if ((DateTools.isDateBefore(selectedPortsView.getValidFrom(), maxValidTo) &&
                        DateTools.isDateAfterOrEqual(selectedPortsView.getValidFrom(), minValidFrom)) ||
                        (DateTools.isDateBeforeOrEqual(selectedPortsView.getValidFrom(), minValidFrom) &&
                                DateTools.isDateAfter(selectedPortsView.getValidTo(), minValidFrom) &&
                                !DateTools.isDateEqual(minValidFrom, maxValidTo))) {
                    result = false;
                    break;
                }
                maxValidTo = DateTools.maxDate(selectedPortsView.getValidTo(), maxValidTo);
                minValidFrom = DateTools.minDate(selectedPortsView.getValidFrom(), minValidFrom);
            }
        }
        return result;
    }

    @Override
    public String generateSipPassword() {
        return RandomTools.createPassword(AuftragVoIPDN.PASSWORD_LENGTH);
    }

    @Override
    public Either<String, String> migrateSipDomainOfVoipDNs(Long auftragId, boolean switchModified,
            CalculatedSipDomain4VoipAuftrag initialSipDomainState) throws ServiceNotFoundException, FindException {
        Either<Reference, String> newSipDomainOrError = applyRules4SipDomain(auftragId, switchModified,
                initialSipDomainState);
        if (newSipDomainOrError.isRight()) {
            return Either.right(newSipDomainOrError.getRight());
        }
        else if (newSipDomainOrError.getLeft() != null) {
            List<AuftragVoIPDN> auftragVoIPDNs = findAuftragVoIPDNByAuftragId(auftragId);
            if (CollectionTools.isNotEmpty(auftragVoIPDNs)) {
                for (AuftragVoIPDN voipDN : auftragVoIPDNs) {
                    voipDN.setSipDomain(newSipDomainOrError.getLeft());
                    auftragVoIPDNDAO.store(voipDN);
                    migrateSipDomainForVoipDNPlan(voipDN);
                }
                return Either.left(String.format("Die SIP-Domäne des Auftrags wurde auf \"%s\" geändert.",
                        newSipDomainOrError.getLeft().getStrValue()));
            }
        }
        return Either.left(null);
    }

    private List<AuftragVoIPDN> findAuftragVoIPDNByAuftragId(Long auftragId) throws ServiceNotFoundException,
            FindException {
        List<Rufnummer> rufnummern = findTyp60RufnummerByAuftragId(auftragId);

        List<AuftragVoIPDN> pwdResult = auftragVoIPDNDAO.findAuftragVoIPDN(auftragId);

        List<AuftragVoIPDN> result = new ArrayList<>();
        for (Rufnummer rufNummer : rufnummern) {
            AuftragVoIPDN voipDN = getAuftragVoIPDN4DN(rufNummer.getDnNoOrig(), pwdResult);
            if (voipDN != null) {
                result.add(voipDN);
            }
        }
        return result;
    }

    private Either<Reference, String> applyRules4SipDomain(Long auftragId, boolean switchModified,
            CalculatedSipDomain4VoipAuftrag initialSipDomainState) throws FindException {
        CalculatedSipDomain4VoipAuftrag newSipDomain = sipDomainService.calculateSipDomain4VoipAuftrag(auftragId);
        if (switchModified || newSipDomain.isOverride
                || (initialSipDomainState.isOverride && !newSipDomain.isOverride)) {
            if (newSipDomain.calculatedSipDomain == null) {
                return Either.right("SIP-Domäne zum umschreiben des Auftrags ist nicht konfiguriert!");
            }
            return Either.left(newSipDomain.calculatedSipDomain);
        }
        return Either.left(null);
    }

    void migrateSipDomainForVoipDNPlan(AuftragVoIPDN voipDN) {
        List<VoipDnPlan> rufnummernplaene = voipDN.getRufnummernplaene();
        Date now = Date.from(DateTools.stripTimeFromDate(LocalDateTime.now()).atZone(ZoneId.systemDefault()).toInstant());
        String newSipDomain = voipDN.getSipDomain() != null ? voipDN.getSipDomain().getStrValue() : null;
        // update sipLogin for all future DNPlans
        rufnummernplaene
                .stream()
                .filter(voipDnPlan -> voipDnPlan.getGueltigAb().after(now))
                .forEach(voipDnPlan -> {
                    voipDnPlan.setSipLogin(updateSipLogin(voipDnPlan.getSipLogin(), newSipDomain));
                    auftragVoIPDNDAO.store(voipDnPlan);
                });
        // update sipLogin for the current DNPlan
        VoipDnPlan currentVoipDnPlan = rufnummernplaene
                .stream()
                .filter(voipDnPlan -> !voipDnPlan.getGueltigAb().after(now))
                .max((p1, p2) -> p1.getGueltigAb().compareTo(p2.getGueltigAb()))
                .orElse(null);
        if (currentVoipDnPlan != null) {
            if (currentVoipDnPlan.getSipLogin() != null) {
                currentVoipDnPlan.setSipLogin(updateSipLogin(currentVoipDnPlan.getSipLogin(), newSipDomain));
            }
            auftragVoIPDNDAO.store(currentVoipDnPlan);
        }
    }

    String updateSipLogin(final String sipLogin, final String newSipDomain) {
        return sipLogin.substring(0, sipLogin.indexOf('@') + 1) + newSipDomain;
    }

    private final static class AuftragVoIPDN2EGPortValidFromToComparator implements Comparator<AuftragVoIPDN2EGPort> {
        static final AuftragVoIPDN2EGPortValidFromToComparator instance = new AuftragVoIPDN2EGPortValidFromToComparator();

        private AuftragVoIPDN2EGPortValidFromToComparator() {
        }

        @Override
        public int compare(AuftragVoIPDN2EGPort o1, AuftragVoIPDN2EGPort o2) {
            return compareDates(o1.getValidFrom(), o2.getValidFrom(), o1.getValidTo(), o2.getValidTo());
        }
    }

    private final static class SelectedPortsViewValidFromToComparator implements Comparator<SelectedPortsView> {
        static final SelectedPortsViewValidFromToComparator instance = new SelectedPortsViewValidFromToComparator();

        private SelectedPortsViewValidFromToComparator() {
        }

        @Override
        public int compare(final SelectedPortsView o1, final SelectedPortsView o2) {
            return compareDates(o1.getValidFrom(), o2.getValidFrom(), o1.getValidTo(), o2.getValidTo());
        }
    }

    private static int compareDates(Date validFromO1, Date validFromO2, Date validToO1, Date validToO2) {
        if (DateTools.isDateBefore(validFromO1, validFromO2)) {
            return -1;
        }
        else if (DateTools.isDateEqual(validFromO1, validFromO2)) {
            if (DateTools.isDateBefore(validToO1, validToO2)) {
                return -1;
            }
            else if (DateTools.isDateAfter(validToO1, validToO2)) {
                return 1;
            }
            else {
                return 0;
            }
        }
        else {
            return 1;
        }
    }

} // end
