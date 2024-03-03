/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 15:38:05
 */
package de.augustakom.hurrican.service.billing.impl;

import static com.google.common.collect.Lists.*;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.dao.billing.BAuftragBNFCDAO;
import de.augustakom.hurrican.dao.billing.RufnummerDAO;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBNFC;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.DNBlock;
import de.augustakom.hurrican.model.billing.DNTNB;
import de.augustakom.hurrican.model.billing.DnOnkz2Carrier;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.billing.query.BAuftragBNFCQuery;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;
import de.augustakom.hurrican.model.shared.view.AuftragINRufnummerView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.iface.FindByParamService;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Service-Implementierung fuer die Verwaltung von Objekten vom Typ <code>Rufnummer</code>.
 */
@BillingTx
public class RufnummerServiceImpl extends DefaultBillingService implements FindByParamService<Rufnummer>,
        RufnummerService {

    private static final Logger LOGGER = Logger.getLogger(RufnummerServiceImpl.class);

    private BAuftragBNFCDAO auftragBNFCDAO = null;

    @Autowired
    private CCAuftragService ccAuftragService;

    @Autowired
    private BillingAuftragService billingAuftragService;

    @Override
    public Rufnummer findDN(Long dnNo) throws FindException {
        try {
            return ((FindDAO) getDAO()).findById(dnNo, Rufnummer.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<Rufnummer> findDNsByDnNoOrig(Long dnNoOrig, Date validFrom) throws FindException {
        try {
            return ((RufnummerDAO) getDAO()).findDNsByDnNoOrig(dnNoOrig, validFrom);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<Rufnummer> findByParam(short strategy, Object[] params) throws FindException {
        try {
            if ((strategy == Rufnummer.STRATEGY_FIND_BY_QUERY) && (params != null) &&
                    (params.length == 1) && (params[0] instanceof RufnummerQuery)) {
                RufnummerDAO dao = (RufnummerDAO) getDAO();
                return dao.findByQuery((RufnummerQuery) params[0]);
            }
            else if ((strategy == Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG) && (params != null) &&
                    (params.length >= 2) && (params.length <= 3) && (params[0] instanceof Long)
                    && (params[1] instanceof Boolean)) {
                RufnummerDAO dao = (RufnummerDAO) getDAO();
                boolean onlyLast = false; // Bug-ID 16
                if ((params.length == 3) && (params[2] instanceof Boolean)) {
                    onlyLast = (Boolean) params[2];
                }
                return dao.findByAuftragNoOrig((Long) params[0], (Boolean) params[1], onlyLast);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }

        throw new FindException(FindException.FIND_STRATEGY_NOT_SUPPORTED);
    }

    @Override
    public List<AuftragDNView> findAuftragDNViews(RufnummerQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            RufnummerDAO dao = (RufnummerDAO) getDAO();
            List<AuftragDNView> adnViews = dao.findAuftragDNViewsByQuery(query);

            // Auftrags-ID und VerbindungsBezeichnung aus CC-System ermitteln
            if (adnViews != null) {
                try {
                    List<AuftragDNView> toAdd = new ArrayList<>();
                    for (AuftragDNView view : adnViews) {

                        /*
                         * Fuer einen Billing-Auftrag koennen im CC-System mehrere Auftraege existieren (z.B. bei einem
                         * Anlagenanschluss). In diesem Fall muss die View mit den Billing-Daten geklont, die
                         * zusaetzlichen CC-Daten uebergeben und der Klon schliesslich der Ergebnismenge hinzugefuegt
                         * werden.
                         */
                        List<CCAuftragIDsView> ccIds = ccAuftragService.findAufragIdAndVbz4AuftragNoOrig(view
                                .getAuftragNoOrig());

                        if (ccIds != null) {
                            int i = 0;
                            for (CCAuftragIDsView ccView : ccIds) {
                                if (i == 0) {
                                    view.setAuftragId(ccView.getAuftragId());
                                    view.setVbz(ccView.getVbz());
                                    view.setAuftragStatusId(ccView.getAuftragStatusId());
                                    view.setAuftragStatusText(ccView.getAuftragStatusText());
                                }
                                else if (i > 0) {
                                    // Kopie von View erzeugen und spaeter der Result-Liste hinzufuegen
                                    AuftragDNView dnView = (AuftragDNView) BeanUtils.cloneBean(view);
                                    dnView.setFernkatastrophe(view.getFernkatastrophe()); // wird nicht richtig
                                    // 'geklont'
                                    dnView.setAuftragId(ccView.getAuftragId());
                                    dnView.setVbz(ccView.getVbz());
                                    dnView.setAuftragStatusId(ccView.getAuftragStatusId());
                                    dnView.setAuftragStatusText(ccView.getAuftragStatusText());
                                    toAdd.add(dnView);
                                }

                                i++;
                            }
                        }
                    }
                    adnViews.addAll(toAdd);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            return adnViews;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<String> findOnkz4AuftragNoOrig(Long auftragNoOrig) throws FindException {
        try {
            return ((RufnummerDAO) getDAO()).findOnkz4AuftragNoOrig(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<Long, String> findHauptRNs(List<Long> auftragNoOrigs) throws FindException {
        if ((auftragNoOrigs == null) || (auftragNoOrigs.isEmpty())) {
            return null;
        }
        try {
            return ((RufnummerDAO) getDAO()).findHauptRNs(auftragNoOrigs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Pair<Long, Integer>> findRNCount(List<Long> auftragNoOrigs) throws FindException {
        if(auftragNoOrigs == null || auftragNoOrigs.isEmpty())
            return Collections.emptyList();
        RufnummerDAO dao = (RufnummerDAO) getDAO();
        return auftragNoOrigs.stream().map(auftragNr -> Pair.create(auftragNr,
                dao.findAllRNs4Auftrag(auftragNr).size())).collect(Collectors.toList());
    }

    @Override
    public Rufnummer findHauptRN4Auftrag(Long auftragNoOrig, boolean findLast) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            Rufnummer example = new Rufnummer();
            example.setAuftragNoOrig(auftragNoOrig);
            example.setMainNumber(true);
            if (findLast) {
                example.setHistLast(true);
            }
            else {
                example.setHistStatus(BillingConstants.HIST_STATUS_AKT);
            }

            List<Rufnummer> result = ((RufnummerDAO) getDAO()).queryByExample(example, Rufnummer.class);
            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Rufnummer> findRNs4Auftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            return ((RufnummerDAO) getDAO()).findByAuftragNoOrig(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Rufnummer> findAllRNs4Auftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }
        try {
            return ((RufnummerDAO) getDAO()).findAllRNs4Auftrag(auftragNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Rufnummer> findRNs4Auftrag(Long auftragNoOrig, Date validDate) throws FindException {
        if ((auftragNoOrig == null) || (validDate == null)) {
            return null;
        }
        try {
            Date dateToCheck = DateUtils.truncate(validDate, Calendar.DAY_OF_MONTH);
            return ((RufnummerDAO) getDAO()).findRNs4Auftrag(auftragNoOrig, dateToCheck);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Rufnummer findLastRN(Long dnNoOrig) throws FindException {
        if (dnNoOrig == null) {
            return null;
        }
        try {
            return ((RufnummerDAO) getDAO()).findLastRufnummer(dnNoOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean isMnetBlock(Long blockNoOrig) throws FindException {
        if (blockNoOrig == null) {
            return false;
        }
        try {
            DNBlock dnb = ((RufnummerDAO) getDAO()).findLastDNBlock(blockNoOrig);

            if (dnb == null) {
                return false;
            }

            return StringTools.isIn(StringUtils.trimToEmpty(dnb.getCarrier()).toUpperCase(),
                    new String[] {
                            TNB.AKOM.carrierNameUC,
                            TNB.MNET.carrierNameUC,
                            TNB.NEFKOM.carrierNameUC
                    }
            );
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BAuftragBNFC findBusinessNumber(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            BAuftrag auftrag = bas.findAuftrag(auftragNoOrig);
            if (auftrag == null) {
                throw new FindException(String.format("Kein Billing-Auftrag mit auftragNoOrig=%d vorhanden.",
                        auftragNoOrig));
            }
            return getAuftragBNFCDAO().findById(auftrag.getAuftragNo(), BAuftragBNFC.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragINRufnummerView> findAuftragINRufnummerViews(BAuftragBNFCQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            List<AuftragINRufnummerView> result = getAuftragBNFCDAO().findINViews(query);
            if (CollectionTools.isNotEmpty(result)) {
                KundenService ks = getBillingService(KundenService.class);
                ks.loadKundendaten4AuftragViews(result);

                List<AuftragINRufnummerView> toAdd = new ArrayList<>();
                for (Iterator<AuftragINRufnummerView> iter = result.iterator(); iter.hasNext(); ) {
                    AuftragINRufnummerView view = iter.next();
                    Adresse adresse = ks.getAdresse4Kunde(view.getKundeNo());
                    if (adresse != null) {
                        view.setOrt(adresse.getOrt());
                    }

                    // falls mehrere CC-Auftraege vorhanden, Objekte duplizieren
                    List<CCAuftragIDsView> ccIds = ccAuftragService.findAufragIdAndVbz4AuftragNoOrig(view
                            .getAuftragNoOrig());
                    if (ccIds != null) {
                        int i = 0;
                        for (Iterator<CCAuftragIDsView> idIterator = ccIds.iterator(); idIterator.hasNext(); ) {
                            CCAuftragIDsView ccView = idIterator.next();
                            if (i == 0) {
                                view.setAuftragId(ccView.getAuftragId());
                                view.setAuftragStatusId(ccView.getAuftragStatusId());
                                view.setAuftragStatusText(ccView.getAuftragStatusText());
                            }
                            else if (i > 0) {
                                // Kopie von View erzeugen und spaeter der Result-Liste hinzufuegen
                                AuftragINRufnummerView inView = (AuftragINRufnummerView) BeanUtils.cloneBean(view);
                                inView.setAuftragId(ccView.getAuftragId());
                                inView.setAuftragStatusId(ccView.getAuftragStatusId());
                                inView.setAuftragStatusText(ccView.getAuftragStatusText());
                                toAdd.add(inView);
                            }

                            i++;
                        }
                    }
                }
                result.addAll(toAdd);
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Long> findDnNotHistLastTillDate(Date date) throws FindException {
        if (date == null) {
            return null;
        }
        try {
            return ((RufnummerDAO) getDAO()).findDnNotHistLastTillDate(date);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public DNTNB findTNB(String tnb) throws FindException {
        try {
            return ((FindDAO) getDAO()).findById(tnb, DNTNB.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Nullable
    @Override
    public String findTnbKennung4Onkz(@NotNull String onkz) throws FindException {
        try {
            DnOnkz2Carrier result = ((FindDAO) getDAO()).findById(onkz, DnOnkz2Carrier.class);
            return (result != null && result.getTNB() != null) ? result.getTNB().tnbKennung
                    : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<RufnummerPortierungSelection> findDNs4TalOrder(Long auftragNoOrig, Date vorgabeMnet)
            throws FindException {
        Preconditions.checkNotNull(auftragNoOrig);

        Collection<Rufnummer> rufnummernKommend = findDnsKommend(auftragNoOrig);
        List<RufnummerPortierungSelection> resultList = newArrayList();
        for (Rufnummer rufnummer : rufnummernKommend) {
            resultList.add(new RufnummerPortierungSelection(rufnummer, vorgabeMnet));
        }
        return resultList;
    }

    @Override
    public List<Rufnummer> findDnsAbgehend(Long auftragNoOrig) throws FindException {
        Preconditions.checkNotNull(auftragNoOrig);

        // nur letzte Version (HIST_LAST=1) der Rufnummer sind interessant (haben aber kein Zuordnung zum
        // Taifun-Auftrag)
        List<Rufnummer> allRufnummern = new ArrayList<>();
        for (Rufnummer rufnummer : findRNs4Auftrag(auftragNoOrig)) {
            allRufnummern.add(findLastRN(rufnummer.getDnNoOrig()));
        }
        Collection<Rufnummer> rufnummernFiltered = Collections2.filter(
                Collections2.filter(allRufnummern, Rufnummer.PORTMODE_ABGEHEND), Rufnummer.HAS_FUTURE_CARRIER);
        return newArrayList(rufnummernFiltered);
    }

    @Override
    public List<Rufnummer> findDnsKommend(Long auftragNoOrig) throws FindException {
        Preconditions.checkNotNull(auftragNoOrig);

        List<Rufnummer> allRufnummern = findRNs4Auftrag(auftragNoOrig);
        Collection<Rufnummer> rufnummernFiltered = Collections2
                .filter(
                        Collections2.filter(allRufnummern, Rufnummer.PORTMODE_KOMMEND),
                        Rufnummer.LAST_CARRIER_NOT_MNET_CARRIER);
        return newArrayList(rufnummernFiltered);
    }

    @Override
    public List<Rufnummer> findDnsKommendForWbci(Long auftragNoOrig) throws FindException {
        Preconditions.checkNotNull(auftragNoOrig);

        List<Rufnummer> allRufnummern = findDnsKommend(auftragNoOrig);
        Collection<Rufnummer> rufnummernFiltered = Collections2.filter(allRufnummern,
                Rufnummer.REAL_DATE_NOT_IN_THE_PAST);
        return newArrayList(rufnummernFiltered);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> findAuftragIdsByEinzelrufnummer(String onkz, String dnBase) throws FindException {
        try {
            return ((RufnummerDAO) getDAO()).findAuftragIdsByEinzelrufnummer(addLeadingZeros(onkz), dnBase);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> findAuftragIdsByBlockrufnummer(String onkz, String dnBase) throws FindException {
        try {
            return ((RufnummerDAO) getDAO()).findAuftragIdsByBlockrufnummer(addLeadingZeros(onkz), dnBase);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public
    @NotNull
    Set<Long> getCorrespondingOrderNoOrigs(Long orderNoOrig) throws FindException {
        if (orderNoOrig == null) {
            return Collections.emptySet();
        }

        List<Rufnummer> rufnummern = findRNs4Auftrag(orderNoOrig);
        if (CollectionUtils.isEmpty(rufnummern)) {
            return Collections.emptySet();
        }

        Set<Long> auftragIds = new HashSet<>();
        for (Rufnummer rufnummer : rufnummern) {
            // nur fuer billing relevante Rufnummern nach Auftraegen suchen, die die Rufnummer als non-billing-relevant
            // eingetragen haben
            if (!BooleanTools.nullToFalse(rufnummer.getNonBillable())) {
                Set<Long> nonBillableIds = findNonBillableAuftragIds(rufnummer.getOnKz(),
                        rufnummer.getDnBase(), rufnummer.getRangeFrom(), rufnummer.getRangeTo());
                if (nonBillableIds != null) {
                    auftragIds.addAll(nonBillableIds);
                }
            }
        }

        return auftragIds;
    }

    @Override
    public Set<Long> getCorrespondingBillingOrders4Klammer(Long billingOrderNoOrig) throws FindException {
        final BAuftrag billingOrder = billingAuftragService.findAuftrag(billingOrderNoOrig);

        Set<Long> correspondingBillingOrders = getCorrespondingOrderNoOrigs(billingOrderNoOrig);
        final Set<Long> filtered = filterCorrespondingBillingOrders(billingOrder, correspondingBillingOrders);

        // urspruenglich angegebenen Auftrag dem Set zuordnen
        filtered.add(billingOrderNoOrig);

        return filtered;
    }

    private Set<Long> filterCorrespondingBillingOrders(final BAuftrag billingOrder, final Set<Long> correspondingBillingOrders) {
        Set<Long> filtered = new HashSet<>();
        for (Long correspondingBillingOrder : correspondingBillingOrders) {
            try {
                BAuftrag correspondingOrder = billingAuftragService.findAuftrag(correspondingBillingOrder);
                if (correspondingOrder != null
                        && billingOrder.getAstatus().equals(correspondingOrder.getAstatus())) {
                    filtered.add(correspondingBillingOrder);
                }
            }
            catch (FindException e) {
                // do nothing
            }
        }
        return filtered;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> findNonBillableAuftragIds(String onkz, String dnBase, String rangeFrom, String rangeTo)
            throws FindException {
        try {
            return ((RufnummerDAO) getDAO()).findNonBillableAuftragIds(addLeadingZeros(onkz), dnBase, rangeFrom, rangeTo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    protected String addLeadingZeros(String onkz) {
        if (onkz == null || onkz.isEmpty() || onkz.charAt(0) == '0') {
            return onkz;
        }
        else {
            return "0" + onkz;
        }
    }

    /**
     * @return Returns the auftragBNFCDAO.
     */
    public BAuftragBNFCDAO getAuftragBNFCDAO() {
        return this.auftragBNFCDAO;
    }

    /**
     * @param auftragBNFCDAO The auftragBNFCDAO to set.
     */
    public void setAuftragBNFCDAO(BAuftragBNFCDAO auftragBNFCDAO) {
        this.auftragBNFCDAO = auftragBNFCDAO;
    }

}
