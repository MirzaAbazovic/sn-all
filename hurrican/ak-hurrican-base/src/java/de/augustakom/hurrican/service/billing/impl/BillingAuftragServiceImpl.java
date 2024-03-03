/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 09:35:45
 */
package de.augustakom.hurrican.service.billing.impl;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.billing.AccountDAO;
import de.augustakom.hurrican.dao.billing.AdresseDAO;
import de.augustakom.hurrican.dao.billing.AuftragDAO;
import de.augustakom.hurrican.dao.billing.BAuftragZusatzDAO;
import de.augustakom.hurrican.model.billing.Account;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragConnect;
import de.augustakom.hurrican.model.billing.BAuftragKombi;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BAuftragScv;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.augustakom.hurrican.model.billing.view.BAuftragView;
import de.augustakom.hurrican.model.billing.view.BVPNAuftragView;
import de.augustakom.hurrican.model.billing.view.EndstelleAnsprechpartnerView;
import de.augustakom.hurrican.model.billing.view.TimeSlotView;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Service-Implementierung, um Objekte vom Typ <code>de.augustakom.hurrican.model.billing.BAuftrag</code> zu verwalten.
 *
 *
 */
@BillingTx
public class BillingAuftragServiceImpl extends DefaultBillingService implements BillingAuftragService {

    private static final Logger LOGGER = Logger.getLogger(BillingAuftragServiceImpl.class);

    @Autowired
    AccountDAO accountDAO;
    @Autowired
    BAuftragZusatzDAO auftragZusatzDAO;
    @Autowired
    AdresseDAO adresseDAO;

    @Override
    public List<BAuftrag> findByBuendelNo(Integer buendelNo) throws FindException {
        if (buendelNo == null) {
            return null;
        }
        try {
            return ((AuftragDAO) getDAO()).findByBuendelNo(buendelNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftrag findAuftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            AuftragDAO dao = (AuftragDAO) getDAO();
            return dao.findByAuftragNoOrig(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftrag findAuftragAkt(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            AuftragDAO dao = (AuftragDAO) getDAO();
            return dao.findAuftragAkt(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAuftragPos> findAuftragPositionen(Long auftragNoOrig, boolean onlyAct) throws FindException {
        return findAuftragPositionen(auftragNoOrig, onlyAct, null);
    }

    @Override
    public List<BAuftragPos> findAuftragPositionen(Long auftragNoOrig, boolean onlyAct, List<Long> externLeistungNos)
            throws FindException {
        try {
            AuftragDAO dao = (AuftragDAO) getDAO();
            Collection<BAuftragPos> result;
            if (CollectionTools.isEmpty(externLeistungNos)) {
                result = (Collection<BAuftragPos>) dao.findAuftragPos4Auftrag(auftragNoOrig);
            }
            else {
                // nur bestimmte Leistungen beachten (aus Leistung und Werteliste)!
                List<BAuftragPos> resultFromLeistung =
                        dao.findAuftragPos4Auftrag(auftragNoOrig, externLeistungNos, false, null, null);
                List<BAuftragPos> resultFromServiceValues =
                        dao.findAuftragPos4Auftrag(auftragNoOrig, externLeistungNos, true, null, null);

                Map<Long, BAuftragPos> aps = new HashMap<>();
                CollectionMapConverter.convert2Map(resultFromLeistung, aps, "getItemNo", null);
                CollectionMapConverter.convert2Map(resultFromServiceValues, aps, "getItemNo", null);

                result = aps.values();
            }

            if (result != null) {
                Map<Long, BAuftragPos> map = new HashMap<>();
                for (BAuftragPos ap : result) {
                    if (onlyAct) {
                        // nur aktive Auftragspositionen/Leistungen ermitteln - jede Leistung nur einmal
                        if (ap.isActive()) {
                            map.put(ap.getItemNoOrig(), ap);
                        }
                        // Auftragsposition inaktiv --> entfernen, falls der Map zugeordnet
                        else if (map.containsKey(ap.getItemNoOrig())) {
                            map.remove(ap.getItemNoOrig());
                        }
                    }
                    else {
                        // jede Leistung nur einmal!
                        map.put(ap.getItemNoOrig(), ap);
                    }
                }

                return new ArrayList<>(map.values());
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftragPos findAuftragPosition(Long auftragNoOrig, Long externLeistungNo) throws FindException {
        if ((auftragNoOrig == null) || (externLeistungNo == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            List<Long> externLNos = new ArrayList<>();
            externLNos.add(externLeistungNo);

            List<BAuftragPos> result = findAuftragPositionen(auftragNoOrig, false, externLNos);
            return ((result != null) && (!result.isEmpty())) ? result.get(result.size() - 1) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public Set<Long> findAuftragNoOrigsWithExtLeistungNos(Set<Long> externLeistungNos, LocalDate chargeTo, int daysToGoBack) throws FindException {
        try {
            AuftragDAO dao = (AuftragDAO) getDAO();
            List<BAuftragPos> resultFromLeistung =
                    dao.findAuftragPos4Auftrag(null, new ArrayList<>(externLeistungNos), false,
                            DateConverterUtils.asDate(chargeTo.minusDays(daysToGoBack)),
                            DateConverterUtils.asDate(chargeTo));
            List<BAuftragPos> resultFromServiceValues =
                    dao.findAuftragPos4Auftrag(null, new ArrayList<>(externLeistungNos), true,
                            DateConverterUtils.asDate(chargeTo.minusDays(daysToGoBack)),
                            DateConverterUtils.asDate(chargeTo));

            Set<Long> auftragNoOrigs = resultFromLeistung.stream()
                    .map(ap -> ap.getAuftragNoOrig()).collect(Collectors.toSet());
            auftragNoOrigs.addAll(resultFromServiceValues.stream()
                    .map(ap -> ap.getAuftragNoOrig()).collect(Collectors.toSet()));

            return auftragNoOrigs;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAuftragView> findAuftragViews(Long kundeNo) throws FindException {
        if (kundeNo == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            return ((AuftragDAO) getDAO()).findAuftragViews(kundeNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftragVO getBasicOrderInformation(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) { return null; }

        try {
            BAuftrag billingAuftrag = findAuftrag(auftragNoOrig);
            if (billingAuftrag != null) {
                Adresse accesspointAddress = (billingAuftrag.getApAddressNo() != null)
                        ? adresseDAO.findById(billingAuftrag.getApAddressNo(), Adresse.class)
                        : null;

                // alle Rufnummern des Auftrags ermitteln, unabhaengig vom Status
                // (Sortierung erfolgt nach Haupt-Rufnummer, deshalb Zugriff auf Index 0)
                List<Rufnummer> rufnummern = getBillingService(RufnummerService.class).findAllRNs4Auftrag(auftragNoOrig);
                Rufnummer mainDn = (CollectionUtils.isNotEmpty(rufnummern)) ? rufnummern.get(0) : null;

                // nicht billing-relevante Rufnummern werden nicht verwendet! (WITA-1942)
                Rufnummer dnToShow = (mainDn != null && !BooleanTools.nullToFalse(mainDn.getNonBillable())) ? mainDn : null;

                return new BAuftragVO(billingAuftrag, accesspointAddress, dnToShow);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAuftragLeistungView> findAuftragLeistungViews(Long kundeNo, boolean onlyAct) throws FindException {
        if (kundeNo == null) {
            return new ArrayList<>();
        }
        try {
            return findAuftragLeistungView(kundeNo, null, false, onlyAct, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAuftragLeistungView> findAuftragLeistungViews4Auftrag(Long auftragNoOrig, boolean onlyAct,
            boolean onlyLeistungen4Extern) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            BAuftrag auftrag = findAuftrag(auftragNoOrig);
            if (auftrag == null) {
                return null;
            }
            return createAuftragLeistungView(auftrag, onlyAct, onlyLeistungen4Extern);
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
    public List<BAuftragLeistungView> findProduktLeistungen4Auftrag(Long auftragNoOrig) throws FindException {
        List<BAuftragLeistungView> views = findAuftragLeistungViews4Auftrag(auftragNoOrig, false, true);
        if (CollectionTools.isNotEmpty(views)) {
            List<BAuftragLeistungView> result = new ArrayList<>();
            for (BAuftragLeistungView view : views) {
                if (view.getExternProduktNo() != null) {
                    result.add(view);
                }
            }
            return result;
        }

        return Collections.emptyList();
    }

    /**
     * Ermittelt alle relevanten Auftrags- und Leistungsdaten zu einem bestimmten Auftrag
     *
     * @param auftrag               der Auftragsdatensatz aus dem Billing-System
     * @param onlyActLeistungen     Flag, ob nur nach aktiven Leistungen gesucht werden soll oder ob auch
     *                              beendete/gekuendigte Leistungen ermittelt werden sollen
     * @param onlyLeistungen4Extern Flag ob nur nach Leistungen/Positionen gesucht werden soll, die fuer externe Systeme
     *                              relevant sind.
     * @return
     * @throws FindException
     *
     */
    private List<BAuftragLeistungView> createAuftragLeistungView(BAuftrag auftrag, boolean onlyActLeistungen,
            boolean onlyLeistungen4Extern) throws FindException, ServiceNotFoundException {
        if (auftrag == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        OEService oes = getBillingService(OEService.class);
        OE oe = oes.findOE(auftrag.getOeNoOrig());

        LeistungService ls = getBillingService(LeistungService.class);
        List<BAuftragPos> auftragPositionen = findAuftragPositionen(auftrag.getAuftragNoOrig(), onlyActLeistungen);
        if (auftragPositionen != null) {
            List<BAuftragLeistungView> retVal = new ArrayList<>();
            for (BAuftragPos ap : auftragPositionen) {
                Leistung leistung = ls.findLeistung(ap.getLeistungNoOrig());
                if (leistung != null) {
                    if (onlyLeistungen4Extern) { // nur Leistungen fuer Steuerung ext. Systeme
                        if (StringUtils.isNotBlank(ap.getParameter())) { // Leistungswerte abfragen
                            ServiceValue sv = ls.findServiceValue(ap.getLeistungNoOrig(), ap.getParameter());
                            if ((sv != null) && sv.isProduktOrLeistung()) {
                                // Steuerung ueber Werteliste
                                retVal.add(new BAuftragLeistungView(auftrag, oe, ap, leistung, sv));
                            }
                            else {
                                if (leistung.isProduktOrLeistung()) {
                                    // Steuerung ueber Leistung
                                    retVal.add(new BAuftragLeistungView(auftrag, oe, ap, leistung));
                                }
                            }
                        }
                        else if (leistung.isProduktOrLeistung()) {
                            retVal.add(new BAuftragLeistungView(auftrag, oe, ap, leistung));
                        }
                    }
                    else {
                        if (StringUtils.isNotBlank(ap.getParameter())) { // Leistungswerte abfragen
                            ServiceValue sv = ls.findServiceValue(ap.getLeistungNoOrig(), ap.getParameter());
                            retVal.add(new BAuftragLeistungView(auftrag, oe, ap, leistung, sv));
                        }
                        else {
                            retVal.add(new BAuftragLeistungView(auftrag, oe, ap, leistung));
                        }
                    }
                }
            }
            return retVal;
        }
        return null;
    }

    private List<BAuftragLeistungView> findAuftragLeistungView(Long kundeNo, Long taifunOrderNoOrig, boolean noKuendAuftraege,
            boolean onlyActLeistungen, boolean onlyLeistungen4Extern) throws FindException {
        if (kundeNo == null) {
            return new ArrayList<>();
        }
        try {
            AuftragDAO dao = (AuftragDAO) getDAO();
            return dao.findAuftragLeistungView(kundeNo, taifunOrderNoOrig, noKuendAuftraege, onlyActLeistungen, onlyLeistungen4Extern);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BAuftragLeistungView> findActiveAuftraege4AM(Long kundeNo, Long taifunOrderNoOrig) throws FindException {
        try {
            // alle Auftragspositionen (aktiv/inaktiv) von nicht gekuendigten Auftraegen ermitteln
            // --> Gruppierung (Mengenermittlung!) durch Programmlogik statt SQL erstellen

            List<BAuftragLeistungView> result = findAuftragLeistungView(kundeNo, taifunOrderNoOrig, true, true, true);
            for (Iterator<BAuftragLeistungView> it = result.iterator(); it.hasNext(); ) {
                BAuftragLeistungView bAuftragLeistungView = it.next();
                if (bAuftragLeistungView.getExternProduktNo() == null) {
                    it.remove();
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
    public List<BVPNAuftragView> findVPNAuftraege() throws FindException {
        try {
            return ((AuftragDAO) getDAO()).findVPNAuftraege();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Account> findAccounts4Auftrag(Long auftragNo) throws FindException {
        if (auftragNo == null) {
            return null;
        }
        try {
            return accountDAO.findAccounts4Auftrag(auftragNo, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Account findAccount4Auftrag(Long auftragNoOrig, boolean useAkt) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            BAuftrag example = new BAuftrag();
            example.setAuftragNoOrig(auftragNoOrig);
            if (useAkt) {
                example.setHistStatus(BillingConstants.HIST_STATUS_AKT);
            }
            else {
                example.setHistLast(Boolean.TRUE);
            }

            List<BAuftrag> auftraege = ((AuftragDAO) getDAO()).queryByExample(example, BAuftrag.class);
            if (auftraege != null) {
                if (auftraege.size() == 1) {
                    BAuftrag auftrag = auftraege.get(0);
                    List<Account> accounts = findAccounts4Auftrag(auftrag.getAuftragNo());
                    if (CollectionTools.isNotEmpty(accounts)) {
                        return accounts.get(0);
                    }
                }
                else if (auftraege.size() > 1) {
                    throw new FindException("Auftrag konnte nicht eindeutig ermittelt werden!");
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
    public Adresse findAnschlussAdresse4Auftrag(Long auftragNoOrig) throws FindException {
        return findAnschlussAdresse4Auftrag(auftragNoOrig, Endstelle.ENDSTELLEN_TYP_B);
    }

    @Override
    public Adresse findAnschlussAdresse4Auftrag(Long auftragNoOrig, String esTyp) throws FindException {
        if (auftragNoOrig == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            BAuftrag auftrag = findAuftrag(auftragNoOrig);
            if (auftrag == null) {
                return null;
            }

            KundenService kundenService = getKundenService();
            BAuftragConnect connect = getBAuftragConnect(auftrag);

            if (connect != null) {
                Long addressNo = (StringUtils.equals(esTyp, Endstelle.ENDSTELLEN_TYP_A)) ? connect
                        .getAccessPointAAddress() : connect.getAccessPointBAddress();
                return kundenService.getAdresseByAdressNo(addressNo);
            }

            // Bei Endstelle A darf Adresse nur von Connect-Zusatz ermittelt werden!
            return (StringUtils.equals(esTyp, Endstelle.ENDSTELLEN_TYP_A)) ? null : kundenService
                    .getAdresseByAdressNo(auftrag.getApAddressNo());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    private BAuftragConnect getBAuftragConnect(BAuftrag auftrag) {
        BAuftragConnect connect = null;
        try {
            connect = findAuftragConnectByAuftragNoPrivate(auftrag.getAuftragNo(), false);
        }
        catch (FindException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Keinen Connect-Zusatz fuer Auftrags-Nr " + auftrag.getAuftragNo() + " gefunden.");
            }
        }
        return connect;
    }

    KundenService getKundenService() throws ServiceNotFoundException {
        return getBillingService(KundenService.class);
    }

    @Override
    public List<BAuftragPos> findBAuftragPos4Report(Long auftragNoOrig, Boolean onlyAkt) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            return ((AuftragDAO) getDAO()).findBAuftragPos4Report(auftragNoOrig, onlyAkt);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftragKombi findAuftragKombiByAuftragNo(Long auftragNo) throws FindException {
        if (auftragNo == null) {
            return null;
        }
        try {
            return auftragZusatzDAO.findById(auftragNo, BAuftragKombi.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftragScv findAuftragScvByAuftragNo(Long auftragNo) throws FindException {
        if (auftragNo == null) {
            return null;
        }
        try {
            return auftragZusatzDAO.findById(auftragNo, BAuftragScv.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftragConnect findAuftragConnectByAuftragNo(Long auftragNo) throws FindException {
        return findAuftragConnectByAuftragNoPrivate(auftragNo, true);
    }

    private BAuftragConnect findAuftragConnectByAuftragNoPrivate(Long auftragNo, boolean log) throws FindException {
        if (auftragNo == null) {
            return null;
        }
        try {
            FindDAO dao = (FindDAO) getDAO();
            return dao.findById(auftragNo, BAuftragConnect.class);
        }
        catch (Exception e) {
            if (log) {
                LOGGER.error(e.getMessage(), e);
            }
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftrag findAuftragStornoByAuftragNoOrig(Long auftragNoOrig) throws FindException {
        Preconditions.checkNotNull(auftragNoOrig, "AuftragNoOrig muss gesetzt sein");
        try {
            return ((AuftragDAO) getDAO()).findAuftragStornoByAuftragNoOrig(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftrag findAuftrag4SAPOrderId(String sapOrderId) throws FindException {
        if (StringUtils.isBlank(sapOrderId)) {
            return null;
        }
        try {
            AuftragDAO dao = (AuftragDAO) getDAO();
            return dao.findBySAPOrderId(sapOrderId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public List<BAuftrag> findAuftraege4SAP(AuftragSAPQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            AuftragDAO dao = (AuftragDAO) getDAO();
            return dao.findAuftraege4SAP(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public void updateAccount(Long auftragNo, IntAccount acc) throws StoreException {
        if ((auftragNo == null) || (acc == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            if (CollectionTools.isNotEmpty(accountDAO.findAccounts4Auftrag(auftragNo, acc.getAccount()))) {
                accountDAO.updatePassword(auftragNo, acc.getAccount(), acc.getPasswort());
            }
            else {
                accountDAO.saveAccount(auftragNo, acc.getAccount(), acc.getPasswort());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public TimeSlotView findTimeSlotView4Auftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            return ((AuftragDAO) getDAO()).findTimeSlotView4Auftrag(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public Map<Long, TimeSlotView> findTimeSlotViews4Auftrag(List<Long> auftragNoOrigs) throws FindException {
        if (auftragNoOrigs == null) {
            return null;
        }
        try {
            return ((AuftragDAO) getDAO()).findTimeSlotViews4Auftrag(auftragNoOrigs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public String findDebitorNrForAuftragNo(Long auftragNo) throws FindException {
        Preconditions.checkNotNull(auftragNo, "Die interne Taifun Auftrag Nr. muss gesetzt sein!");
        try {
            return ((AuftragDAO) getDAO()).findDebitorNrForAuftragNo(auftragNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public List<BAuftragLeistungView> findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(final Long auftragNoOrig,
            final Long extProdNo, final Collection<Long> externMiscNOs) throws FindException {
        if ((extProdNo == null) && (CollectionTools.isEmpty(externMiscNOs))) {
            return Collections.emptyList();
        }

        final Collection<Long> extMiscNosToCheck = (externMiscNOs != null) ? externMiscNOs : Collections.<Long>emptyList();
        try {
            List<BAuftragLeistungView> leistungen = findAuftragLeistungViews4Auftrag(auftragNoOrig, false, false);
            CollectionUtils.filter(leistungen, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    boolean result = false;
                    if ((object != null) && (object instanceof BAuftragLeistungView)) {
                        BAuftragLeistungView leistung = (BAuftragLeistungView) object;
                        result = (NumberTools.equal(leistung.getExternProduktNo(), extProdNo))
                                || (extMiscNosToCheck.contains(leistung.getExternMiscNo()));
                    }
                    return result;
                }
            });
            return leistungen;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasUnchargedServiceElementsWithExtMiscNo(Long auftragNoOrig, Long externMiscNo) throws FindException {
        try {
            BAuftrag auftrag = findAuftrag(auftragNoOrig);
            if (auftrag == null) {
                throw new FindException(String.format("Kein Billing-Auftrag mit Nr: %d vorhanden", auftragNoOrig));
            }
            List<BAuftragLeistungView> auftragLeistungViews =
                    findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(auftragNoOrig, auftrag.getOeNoOrig(), Collections.singletonList(externMiscNo));
            if (CollectionTools.isNotEmpty(auftragLeistungViews)) {
                for (BAuftragLeistungView view : auftragLeistungViews) {
                    if (view.getAuftragPosChargedUntil() == null) {
                        return true;
                    }
                }
            }
        }
        catch (FindException e) {
            throw e;
        }
        return false;
    }

    @Override
    public BAuftrag findAuftrag4CwmpId(String cwmpId, Date valid) throws FindException {
        try {
            return ((AuftragDAO) getDAO()).findAuftrag4CwmpId(cwmpId, valid);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> findAuftragIdsByGeoId(Long geoId, boolean ignoreCancelledOrders) throws FindException {
        if (geoId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            final List<Long> auftragIdsByGeoId = ((AuftragDAO) getDAO()).findAuftragIdsByGeoId(geoId, ignoreCancelledOrders);
            return new HashSet<>(auftragIdsByGeoId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    public EndstelleAnsprechpartnerView findEndstelleAnsprechpartner(Long auftragNoOrig) throws FindException {
        BAuftrag bAuftrag = findAuftrag(auftragNoOrig);
        KundenService kundenService = null;
        try {
            kundenService = getKundenService();
        }
        catch (ServiceNotFoundException e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        Kunde kunde = kundenService.findKunde(bAuftrag.getKundeNo());
        EndstelleAnsprechpartnerView endstAnsp = new EndstelleAnsprechpartnerView();
        if (kunde.isBusinessCustomer()) {
            endstAnsp.setEmail(bAuftrag.getBearbeiterKundeEmail());
            endstAnsp.setFax(bAuftrag.getBearbeiterKundeFax());
            endstAnsp.setName(bAuftrag.getBearbeiterKundeName());
            endstAnsp.setTelefon(bAuftrag.getBearbeiterKundeRN());
        }
        else {
            endstAnsp.setEmail(kunde.getEmail());
            endstAnsp.setFax(kunde.getRnFax());
            endstAnsp.setMobil(kunde.getRnMobile());
            endstAnsp.setName(kunde.getName());
            endstAnsp.setTelefon(kunde.getHauptRufnummer());
            endstAnsp.setVorname(kunde.getVorname());
        }
        return endstAnsp;
    }
}
