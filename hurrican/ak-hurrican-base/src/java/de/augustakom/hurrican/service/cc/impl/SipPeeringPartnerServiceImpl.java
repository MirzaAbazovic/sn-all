package de.augustakom.hurrican.service.cc.impl;

import java.text.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.Auftrag2PeeringPartnerDAO;
import de.augustakom.hurrican.dao.cc.SipPeeringPartnerDao;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;

/**
 * SipPeeringPartnerServiceImpl
 */
@CcTxRequired
public class SipPeeringPartnerServiceImpl extends DefaultCCService implements SipPeeringPartnerService {

    private static final Logger LOGGER = Logger.getLogger(SipPeeringPartnerServiceImpl.class);

    @Resource(name = "sipPeeringPartnerDAO")
    private SipPeeringPartnerDao sipPeeringPartnerDao;

    @Resource(name = "auftrag2PeeringPartnerDAO")
    private Auftrag2PeeringPartnerDAO auftrag2PeeringPartnerDAO;

    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private CPSService cpsService;

    @Override
    public SipPeeringPartner savePeeringPartner(SipPeeringPartner sipPeeringPartner) {
        return sipPeeringPartnerDao.store(sipPeeringPartner);
    }

    @Override
    public SipPeeringPartner savePeeringPartnerWithValidation(SipPeeringPartner sipPeeringPartner)
            throws StoreException {
        sipPeeringPartnerCheckDuplicates(sipPeeringPartner);
        sipPeeringPartnerCheckFutures(sipPeeringPartner);
        return savePeeringPartner(sipPeeringPartner);
    }

    void sipPeeringPartnerCheckFutures(SipPeeringPartner sipPeeringPartner) throws StoreException {
        Date now = new Date();
        List<SipSbcIpSet> futures = sipPeeringPartner.getSbcIpSets().stream().filter(in ->
                DateTools.isDateAfter(in.getGueltigAb(), now)).collect(Collectors.toList());
        if (futures != null && futures.size() > 1) {
            throw new StoreException("Es darf nur jeweils ein SBC-IP-Set in der Zukunft liegen!");
        }
    }

    void sipPeeringPartnerCheckDuplicates(SipPeeringPartner sipPeeringPartner) throws StoreException {
        List<SipSbcIpSet> duplicated = sipPeeringPartner.getSbcIpSets().stream().filter(inner ->
                sipPeeringPartner.getSbcIpSets().stream().filter(outer ->
                        outer.getGueltigAb().getTime() == inner.getGueltigAb().getTime()).count() > 1)
                .collect(Collectors.toList());
        if (duplicated != null && duplicated.size() > 1) {
            final SimpleDateFormat formater = new SimpleDateFormat(DateTools.PATTERN_DAY_MONTH_YEAR);
            String dates = duplicated.stream()
                    .map(SipSbcIpSet::getGueltigAb)
                    .map(formater::format)
                    .reduce("", (String a, String b) -> {
                        if (a.isEmpty())
                            return b;
                        else
                            return a + ", " + b;
                    });
            throw new StoreException(String.format("Das GültigAb-Datum mindestens zweier Sets sind identisch: %s !",
                    dates));
        }
    }

    @Override
    public List<SipPeeringPartner> findAllPeeringPartner(Boolean activeOnly) {
        return sipPeeringPartnerDao.findAllPeeringPartner(activeOnly);
    }

    @Override
    public SipPeeringPartner findPeeringPartnerById(Long id) {
        return sipPeeringPartnerDao.findPeeringPartnerById(id);
    }

    @Override
    public SipPeeringPartner findPeeringPartner4Auftrag(Long auftragId, Date validAt) throws FindException {
        try {
            List<Auftrag2PeeringPartner> result = auftrag2PeeringPartnerDAO.findValidAuftrag2PeeringPartner(auftragId, validAt);
            if (CollectionUtils.isNotEmpty(result)) {
                if (!CollectionTools.hasExpectedSize(result, 1)) {
                    throw new FindException("Es wurde mehr als ein PeeringPartner zu dem Auftrag gefunden.");
                }

                return sipPeeringPartnerDao.findPeeringPartnerById(result.get(0).getPeeringPartnerId());
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
    public List<Auftrag2PeeringPartner> findAuftragPeeringPartners(Long auftragId) throws FindException {
        try {
            Auftrag2PeeringPartner example = new Auftrag2PeeringPartner();
            example.setAuftragId(auftragId);

            return auftrag2PeeringPartnerDAO.queryByExample(
                    example, Auftrag2PeeringPartner.class, new String[]{"id"}, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveAuftrag2PeeringPartnerButCheckOverlapping(@Nonnull Auftrag2PeeringPartner toCheck) throws StoreException {
        checkAuftrag2PeeringPartner(toCheck);
        try {
            if (!DateTools.isDateEqual(toCheck.getGueltigVon(), toCheck.getGueltigBis())) {
                // Pruefung, wenn toCheck einen Bereich abdeckt
                List<Auftrag2PeeringPartner> auftrag2PeeringPartners = findAuftragPeeringPartners(toCheck.getAuftragId());
                Optional<Auftrag2PeeringPartner> firstFiltered = auftrag2PeeringPartners
                        .stream()
                        .filter(pp -> !pp.getId().equals(toCheck.getId()))
                        .filter(pp -> !DateTools.isDateEqual(pp.getGueltigVon(), pp.getGueltigBis()))
                        .filter(pp -> (DateTools.isDateAfterOrEqual(toCheck.getGueltigVon(), pp.getGueltigVon())
                                && DateTools.isDateBefore(toCheck.getGueltigVon(), pp.getGueltigBis()))
                                || (DateTools.isDateAfter(toCheck.getGueltigBis(), pp.getGueltigVon())
                                && DateTools.isDateBeforeOrEqual(toCheck.getGueltigBis(), pp.getGueltigBis())))
                        .findFirst();
                if (firstFiltered.isPresent()) {
                    throw new StoreException("Die eingestellten Datumswerte sind ungültig, da sich mindestens zwei "
                            + "Zeitspannen überschneiden!");
                }
            }
            auftrag2PeeringPartnerDAO.merge(toCheck);
        }
        catch (FindException e) {
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public void saveAuftrag2PeeringPartner(@Nonnull Auftrag2PeeringPartner toSave) throws StoreException {
        checkAuftrag2PeeringPartner(toSave);
        try {
            auftrag2PeeringPartnerDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void checkAuftrag2PeeringPartner(Auftrag2PeeringPartner auftrag2PeeringPartner) throws StoreException {
        Preconditions.checkNotNull(auftrag2PeeringPartner, "Peering Partner muss gesetzt sein");
        Preconditions.checkNotNull(auftrag2PeeringPartner.getGueltigVon(), "GUELTIG_VON des Peering Partners muss gesetzt sein");
        Preconditions.checkNotNull(auftrag2PeeringPartner.getGueltigBis(), "GUELTIG_BIS des Peering Partners muss gesetzt sein");
        if (DateTools.isDateBefore(auftrag2PeeringPartner.getGueltigBis(), auftrag2PeeringPartner.getGueltigVon())) {
            throw new StoreException("Die eingestellte Zeitspanne ist ungültig, da 'von' > 'bis' ist!");
        }
    }

    @Override
    public void addAuftrag2PeeringPartner(Long auftragId, Long peeringPartnerId, Date validAt) throws StoreException {
        try {
            List<Auftrag2PeeringPartner> assigned = auftrag2PeeringPartnerDAO.findByAuftragId(auftragId);
            if (CollectionUtils.isNotEmpty(assigned)) {
                List<Auftrag2PeeringPartner> validAfterGivenDate = assigned
                        .stream()
                        .filter(pp -> DateTools.isDateAfterOrEqual(pp.getGueltigVon(), validAt)) // filtert alle alten Zeitspannen aus
                        .filter(pp -> DateTools.isDateAfter(pp.getGueltigBis(), pp.getGueltigVon())) // filtert alle leeren (gueltigBis <= gueltigVon) Zeitspannen aus
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(validAfterGivenDate)) {
                    throw new StoreException(String.format(
                            "Es ist bereits ein Peering Partner ab bzw. nach dem %s zugeordnet. Bitte Peering Partner "
                                    + "beenden oder Startdatum weiter in die Zukunft anpassen!",
                            DateTools.formatDate(validAt, DateTools.PATTERN_DAY_MONTH_YEAR)));
                }
            }

            List<Auftrag2PeeringPartner> alreadyAssigned =
                    auftrag2PeeringPartnerDAO.findValidAuftrag2PeeringPartner(auftragId, validAt);
            if (CollectionUtils.isNotEmpty(alreadyAssigned)) {
                final Date deactivationDate = validAt;
                alreadyAssigned
                        .stream()
                        .forEach(a2p -> deactivateAuftrag2PeeringPartner(a2p, deactivationDate));
            }

            Auftrag2PeeringPartner toAdd = new Auftrag2PeeringPartner();
            toAdd.setAuftragId(auftragId);
            toAdd.setPeeringPartnerId(peeringPartnerId);
            toAdd.setGueltigVon(validAt);
            toAdd.setGueltigBis(DateTools.getHurricanEndDate());
            saveAuftrag2PeeringPartner(toAdd);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    void deactivateAuftrag2PeeringPartner(Auftrag2PeeringPartner toDeactivate, Date validTo) {
        toDeactivate.setGueltigBis(validTo);
        auftrag2PeeringPartnerDAO.store(toDeactivate);
    }

    @Override
    public List<AuftragDaten> findActiveOrdersAssignedToPeeringPartner(Long peeringPartnerId, Date forDate) throws FindException {
        try {
            List<Auftrag2PeeringPartner> peeringPartners =
                    auftrag2PeeringPartnerDAO.findAuftrag2PeeringPartner(peeringPartnerId, forDate);
            if (CollectionUtils.isNotEmpty(peeringPartners)) {
                return peeringPartners
                        .stream()
                        .map(pp -> loadAuftragDaten(pp.getAuftragId()))
                        .filter(opt -> opt.isPresent() && !opt.get().isAuftragClosed() && opt.get().isActiveAt(forDate))
                        .map(opt -> opt.get())
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    Optional<AuftragDaten> loadAuftragDaten(Long auftragId) {
        try {
            return Optional.of(auftragService.findAuftragDatenByAuftragId(auftragId));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public AKWarnings createAndSendCpsTxForAssignedOrders(final Long peeringPartnerId, final Date forDate,
            final Long sessionId) throws StoreException {
        try {
            AKWarnings warnings = new AKWarnings();

            List<AuftragDaten> assignedOrders = findActiveOrdersAssignedToPeeringPartner(peeringPartnerId, forDate);
            if (CollectionUtils.isNotEmpty(assignedOrders)) {
                assignedOrders
                        .stream()
                        .forEach(ad -> createAndSendModifySubscriber(ad, forDate, warnings, sessionId));
            }
            return warnings;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    void createAndSendModifySubscriber(AuftragDaten auftragDaten, Date execDate, AKWarnings warnings, Long sessionId) {
        try {
            CPSProvisioningAllowed cpsProvisioningAllowed = cpsService.isCPSProvisioningAllowed(
                    auftragDaten.getAuftragId(), null, false, false, true);
            if (!cpsProvisioningAllowed.isProvisioningAllowed()) {
                warnings.addAKWarning(this,
                        String.format("CPS-Tx für Auftrag %s nicht erlaubt: %s",
                                auftragDaten.getAuftragId(), cpsProvisioningAllowed.getNoCPSProvisioningReason()));
            }

            CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(
                    new CreateCPSTransactionParameter(auftragDaten.getAuftragId(), null,
                            CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                            CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                            CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, execDate, null, null, null, null, false, false,
                            sessionId));

            if (CollectionTools.hasExpectedSize(cpsTxResult.getCpsTransactions(), 1)) {
                cpsService.sendCPSTx2CPS(cpsTxResult.getCpsTransactions().get(0), sessionId);
            }

            if (cpsTxResult.getWarnings().isNotEmpty()) {
                warnings.addAKWarnings(new AKWarnings().addAKWarning(this,
                        String.format("Problem zu Auftrag %s: %s", auftragDaten.getAuftragId(), cpsTxResult.getWarnings())));
            }
        }
        catch (Exception e) {
            warnings.addAKWarning(this, String.format("Fehler beim Generieren oder Senden der CPS-Tx für Auftrag %s: %s",
                    auftragDaten.getAuftragId(), e.getMessage()));
        }
    }

}
