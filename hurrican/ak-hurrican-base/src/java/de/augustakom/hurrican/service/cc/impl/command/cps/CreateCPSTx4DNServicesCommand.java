/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2009 09:49:35
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;


/**
 * Command-Klasse, die CPS-Transaktionen an Hand von Aenderungen an Rufnummern-Leistungen generiert.
 *
 *
 */
public class CreateCPSTx4DNServicesCommand extends AbstractCPSCommand {

    public static final String KEY_DN_SERVICES_CHANGE_DATE = "dn.services.change.date";
    private static final Logger LOGGER = Logger.getLogger(CreateCPSTx4DNServicesCommand.class);
    private CCRufnummernService ccRufnummernService;
    private CCAuftragService ccAuftragService;
    private RufnummerService rufnummerService;
    private BAService baService;

    private Date changeDate = null;

    /**
     * called by Spring
     */
    public void init() throws ServiceNotFoundException {
        setRufnummerService(getBillingService(RufnummerService.class));
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            loadRequiredData();

            // noch nicht provisionierte DN-Services ermitteln
            List<Leistung2DN> dnServices = ccRufnummernService.findUnProvisionedDNServices(changeDate);
            if (CollectionTools.isNotEmpty(dnServices)) {
                List<Long> auftragIDs = findAuftragIDs(dnServices);
                if (CollectionTools.isNotEmpty(auftragIDs)) {
                    // CPS-Transactions erzeugen
                    return createCPSTransactions(auftragIDs);
                }
            }

            return null;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /*
     * Erstellt fuer jeden angegebenen Auftrag eine CPS-Tx.
     * @param auftragIDs Liste mit den Auftrag-IDs, fuer die eine CPS-Tx erstellt werden soll
     * @return Liste mit den generierten CPS-Transactions
     */
    private List<CPSTransaction> createCPSTransactions(List<Long> auftragIDs) throws HurricanServiceCommandException {
        try {
            List<CPSTransaction> result = new ArrayList<CPSTransaction>();
            for (Long auftragId : auftragIDs) {
                CPSTransaction cpsTx = getCpsTransaction(auftragId);
                result.add(cpsTx);
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Unexpected error during creation of CPS Transactions: " + e.getMessage(), e);
        }
    }

    private CPSTransaction getCpsTransaction(Long auftragId) {
        CPSTransaction cpsTx = null;
        try {
            // CPS-Transaction anlegen
            Date execTime = DateTools.changeDate(new Date(), Calendar.MINUTE, 5);
            CPSTransactionResult txResult = cpsService.createCPSTransaction(
                    new CreateCPSTransactionParameter(auftragId, null, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.TX_SOURCE_HURRICAN_DN,
                            CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, execTime, null, null, null, null, Boolean.TRUE, Boolean.FALSE,
                            getSessionId())
            );

            if (CollectionTools.isNotEmpty(txResult.getCpsTransactions())) {
                cpsTx = txResult.getCpsTransactions().get(0);
            }
            else {
                String warnings = (txResult.getWarnings() != null)
                        ? txResult.getWarnings().getWarningsAsText() : null;
                throw new HurricanServiceCommandException(warnings);
            }

        }
        catch (Exception e) {
            if (cpsTx != null) {
                createCPSTxLog(cpsTx, e.getMessage());
            }
            addWarning(auftragId, "Error creating CPS-Tx: " + e.getMessage());
        }
        return cpsTx;
    }

    /**
     * Ermittelt aus den DN-Services die Auftrag-IDs, fuer die eine CPS-Provisionierung durchgefuehrt werden soll/darf.
     *
     * @param dnServices Liste mit den offenen DN-Services
     * @return Auftrag-IDs, fuer die eine CPS-Provisionierung wg. Aenderung der DN-Services durchgefuehrt werden soll.
     */
    private List<Long> findAuftragIDs(List<Leistung2DN> dnServices) throws HurricanServiceCommandException {
        try {
            List<Long> auftragIDs = new ArrayList<Long>();
            Map<Long, Object> billingOrderMap = new HashMap<Long, Object>();
            Map<Long, Object> dnNoMap = new HashMap<Long, Object>();
            for (Leistung2DN l2dn : dnServices) {
                if (!dnNoMap.containsKey(l2dn.getDnNo())) {
                    dnNoMap.put(l2dn.getDnNo(), null);

                    Rufnummer dn = getRufnummer(l2dn);

                    try {
                        // technischen Auftrag ueber die DN ermitteln
                        if ((dn != null) && (dn.getAuftragNoOrig() != null)
                                && !dn.isRouting()
                                && !billingOrderMap.containsKey(dn.getAuftragNoOrig())) {

                            boolean ignoreDn = false;
                            if (StringTools.isIn(dn.getHistStatus(), new String[] { BillingConstants.HIST_STATUS_UNG }) ||
                                    DateTools.isDateBeforeOrEqual(dn.getGueltigBis(), changeDate)) {
                                // ungueltige Rufnummern, bzw. nicht mehr aktive Rufnummern werden ignoriert!
                                ignoreDn = true;
                            }

                            if (!ignoreDn) {
                                List<AuftragDaten> techOrders = ccAuftragService.findAuftragDaten4OrderNoOrig(dn.getAuftragNoOrig());
                                if (CollectionTools.isNotEmpty(techOrders)) {
                                    // Auftragsstatus pruefen
                                    for (AuftragDaten auftragDaten : techOrders) {
                                        if (NumberTools.isGreaterOrEqual(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB) &&
                                                NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT)) {
                                            boolean acceptOrder = true;

                                            // bei aktivem Kuendigungsbauauftrag - ignorieren
                                            if (NumberTools.isGreaterOrEqual(auftragDaten.getStatusId(), AuftragStatus.KUENDIGUNG)
                                                    && hasActiveProvisioningToday(auftragDaten.getAuftragId())) {
                                                acceptOrder = false;
                                            }

                                            if (acceptOrder) {
                                                billingOrderMap.put(dn.getAuftragNoOrig(), null);
                                                auftragIDs.add(auftragDaten.getAuftragId());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        addWarning(this, "Error while loading tech. order for DN_NO " + l2dn.getDnNo());
                    }
                }
            }

            return auftragIdsAllowedForCpsProvisioning(auftragIDs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error while finding the Hurrican orders for CPS provisioning (DN-Services): " + e.getMessage(), e);
        }
    }

    private List<Long> auftragIdsAllowedForCpsProvisioning(List<Long> auftragIDs) throws FindException {
        List<Long> retVal = new ArrayList<>();
        if (CollectionTools.isNotEmpty(auftragIDs)) {
            for (Long auftragId : auftragIDs) {
                if (!retVal.contains(auftragId)) {
                    // pruefen, ob CPS-Provisionierung zulaessig ist
                    CPSProvisioningAllowed allowed = cpsService.isCPSProvisioningAllowed(
                            auftragId,
                            LazyInitMode.noInitialLoad,
                            true,
                            false,
                            true);

                    if (allowed.isProvisioningAllowed()) {
                        retVal.add(auftragId);
                    }
                    else {
                        addWarning(this, String.format("Order (%s) not allowed for CPS provisioning. Reason: %s",
                                auftragId, allowed.getNoCPSProvisioningReason()));
                    }
                }
            }
        }
        return retVal;
    }

    private Rufnummer getRufnummer(Leistung2DN l2dn) {
        Rufnummer dn = null;
        try {
            dn = rufnummerService.findDN(l2dn.getDnNo());
        }
        catch (FindException e) {
            // nothing to do (DN evtl. nicht mehr vorhanden)
            addWarning(this, "No DN reference in billing system found for DN_NO " + l2dn.getDnNo());
        }
        return dn;
    }

    /*
     * Prueft, ob fuer den angegebenen Auftrag zu <=HEUTE ein aktiver
     * Bauauftrag vorhanden ist.
     */
    private boolean hasActiveProvisioningToday(Long auftragId) throws FindException {
        Verlauf active = baService.findActVerlauf4Auftrag(auftragId, false);
        if ((active != null) && DateTools.isDateBeforeOrEqual(active.getRealisierungstermin(), new Date())) {
            return true;
        }
        return false;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        changeDate = (Date) getPreparedValue(KEY_DN_SERVICES_CHANGE_DATE);
        if (changeDate == null) {
            LOGGER.warn("ChangeDate is not defined. Using actual date to find DN services...");
            changeDate = new Date();
        }
    }

    /**
     * Injected
     */
    public void setCcRufnummernService(CCRufnummernService ccRufnummernService) {
        this.ccRufnummernService = ccRufnummernService;
    }

    /**
     * Injected
     */
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    /**
     * Injected
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    /**
     * @param rufnummerService The rufnummerService to set.
     */
    public void setRufnummerService(RufnummerService rufnummerService) {
        this.rufnummerService = rufnummerService;
    }


}
