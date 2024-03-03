/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2009 11:20:33
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.text.*;
import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import com.evolving.wsdl.sa.v1.types.ServiceResponseDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.HibernateSessionHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceResponseSOData;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungCPSPredicate;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Command-Klasse, um eine CPS-Transaction abzuschliessen. <br> Dieses Command wird durch den M-net WebService
 * aufgerufen, an den der CPS abgeschlossene Transactions meldet. <br> <br> Ablauf fuer den Abschluss einer CPS-Tx: <ul>
 * <li>CPS-Tx zur angegebenen ID ermitteln <li>Status der CPS-Tx pruefen und ggf. Exception generieren (falls Status
 * bereits 'zu hoch') <li>Response-Daten vom CPS auswerten <li>Status der CPS-Tx abhaengig von den Response-Daten auf
 * 'success' od. 'failure' setzen <li>falls CPS-Tx mit Verlauf verbunden, den Status der Verlaufs-Datensaetze
 * aktualisieren (und ggf. den gesamten Verlauf abschliessen) </ul>
 *
 *
 */
public class FinishCPSTxCommand extends AbstractCPSCommand {

    /** Fttx hardware service */
    private FTTXHardwareService fttxHardwareService;

    /*
     * TODO createCPSTxLog umstellen Die Aufrufe von createCPSTxLog sollten aus dem einzelnen Command-Klassen entfernt
     * werden. Grund: die Methode createCPSTxLog oeffnet eine neue Transaction (PROPAGATION_REQUIRES_NEW). Dieser
     * Umstand macht es fuer die automatischen Tests schwierig, die Command-Klasse vollstaendig zu testen. Beim Aufruf
     * von createCPSTxLog "haengt" die Connection naemlich, da der Test in einer eigenen Transaktion mit
     * auto-commit=false gestartet ist. Somit kann die CPS-Tx Log nicht gespeichert werden, da die zugehoerige CPS-Tx
     * noch nicht committed ist. moegliche Loesung: - die Commands "sammeln" die Logs. - der Aufrufer der Commands
     * (CPSService) holt die Logs und speichert sie in einer eigenen Transaktion Vorteil: die Commands koennten ohne
     * Transaction Einfluesse getestet werden!
     */

    private static final Logger LOGGER = Logger.getLogger(FinishCPSTxCommand.class);

    /**
     * Key, ueber den das CPS-Result dem Command uebergeben wird.
     */
    public static final String KEY_CPS_RESULT = "cps.result";

    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService")
    private DSLAMProfileMonitorService dslamProfileMonitorService;

    /**
     * @return Boolean.TRUE wenn finish erfolgreich war
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Boolean execute() throws Exception {
        LOGGER.info("FinishCPSTxCommand.execute called for CPS-Tx ID " + getCPSTxId());
        CPSTransaction cpsTx = null;
        try {
            cpsTx = getCPSTransaction();
            loadRequiredData();

            int count = 0;
            while (count < 3) {
                count++;
                if (NumberTools.isLess(cpsTx.getTxState(), CPSTransaction.TX_STATE_IN_PROVISIONING)) {
                    LOGGER.warn("State of CPS-Tx " + cpsTx.getId() + " is < IN_PROVISIONING; wait and re-read.");
                    Thread.sleep(2500);
                    HibernateSessionHelper.clearAndFlushSession(serviceLocator);
                    cpsTx = getCPSTransaction();
                }
                else {
                    break;
                }
            }

            if (NumberTools.isLess(cpsTx.getTxState(), CPSTransaction.TX_STATE_IN_PROVISIONING)) {
                String msg = "State of CPS-Tx is < IN_PROVISIONING; Response from CPS is unexpected!";
                createCPSTxLog(cpsTx, msg);
                throw new HurricanServiceCommandException(msg);
            }
            else if (cpsTx.isFinished() || cpsTx.isCancelled()) {
                // abgeschlossene/abgebrochene Transaktionen erwarten keinen CPS-Response!
                String msg = "CPS-Tx is already finished or cancelled! Response from CPS was not expected!";
                createCPSTxLog(cpsTx, msg);
                throw new HurricanServiceCommandException(msg);
            }
            else {
                // CPS-Tx abschliessen
                return finishCPSTx(cpsTx);
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "Unexpected error finishing CPS-Tx: " + e.getMessage();
            createCPSTxLog(cpsTx, msg);
            throw new HurricanServiceCommandException("Unexpected error finishing CPS-Tx", e);
        }
    }

    /*
     * Schliesst die aktuelle CPS-Transaction ab.
     *
     * @throws ServiceCommandException
     */
    protected boolean finishCPSTx(CPSTransaction cpsTx) throws ServiceCommandException {
        try {
            ServiceResponseDocument serviceResponseDoc = (ServiceResponseDocument) getPreparedValue(KEY_CPS_RESULT);
            ServiceResponse tmpServiceResp = serviceResponseDoc.getServiceResponse();
            ServiceResponse2 serviceResp = tmpServiceResp.getServiceResponse();

            CPSServiceResponseSOData soData = getCpsServiceResponseSOData(cpsTx, serviceResponseDoc, serviceResp);

            // Result analysieren und Status entsprechend setzen
            boolean success = false;
            if (serviceResp.getSOResult() == SERVICE_RESPONSE_SORESULT_CODE_SUCCESS) {
                success = true;
                cpsTx.setTxState(CPSTransaction.TX_STATE_SUCCESS);
                createCPSTxLog(cpsTx, "CPS-Tx successful", true, false);
            }
            else {
                cpsTx.setTxState(CPSTransaction.TX_STATE_FAILURE_CLOSED);
                createCPSTxLog(cpsTx, convertSOResult2Message(serviceResp.getSOResult()));

                if (soData != null) {
                    // falls ErrorMsg vorhanden, diese loggen; sonst die 'normale' Message
                    if (StringUtils.isNotEmpty(soData.getErrorMessage())) {
                        createCPSTxLog(cpsTx, soData.getErrorMessage());
                    }
                    else if (StringUtils.isNotEmpty(soData.getComment())) {
                        createCPSTxLog(cpsTx, soData.getComment());
                    }
                }
                else {
                    createCPSTxLog(cpsTx, "CPS-Tx failed. Reason: unknown!");
                }
            }

            // CPS-Tx speichern
            cpsTx.setResponseAt(new Date());
            cpsService.saveCPSTransaction(cpsTx, getSessionId());

            if (success) {
                doTxSourceDependentActions(cpsTx, soData);
            }

            createCPSTxLog(cpsTx, "Response-Data from CPS processed at " +
                    DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_LONG));
            return success;
        }
        catch (Exception e) {
            String msg = "Error finishing CPS-Tx: " + e.getMessage();
            createCPSTxLog(cpsTx, msg);
            changeCPSTxStateToFailure(cpsTx);
            throw new HurricanServiceCommandException("Error finishing CPS-Tx", e);
        }
    }

    private CPSServiceResponseSOData getCpsServiceResponseSOData(CPSTransaction cpsTx, ServiceResponseDocument serviceResponseDoc, ServiceResponse2 serviceResp) {
        CPSServiceResponseSOData soData = null;
        if (serviceResp.getSOResponseData() != null) {
            // SOResponseData in CPS-Tx eintragen
            cpsTx.setResponseData(serviceResponseDoc.toString().getBytes(StringTools.CC_DEFAULT_CHARSET));

            try {
                soData = parseServiceResponseSOData(serviceResp);
            }
            catch (HurricanServiceCommandException ex) {
                LOGGER.error("finishCPSTx Fehler beim SOData parsen", ex);
                createCPSTxLog(cpsTx, ex.getMessage());
            }
        }
        return soData;
    }

    protected void doTxSourceDependentActions(CPSTransaction cpsTx, CPSServiceResponseSOData soData)
            throws StoreException, FindException, ServiceNotFoundException, ValidationException {
        
        if (NumberTools.equal(cpsTx.getTxSource(), CPSTransaction.TX_SOURCE_HURRICAN_VERLAUF)) {
            // (HUR-8466) Ueberpruefen, ob der Auftrag zur DSLAM-Ueberwachung hinzugefuegt werden muss!
            monitorDSLAMProfileForAuftragIfNeccessary(cpsTx.getAuftragId());

            // Bauauftrag abschliessen
            finishProvisioningOrders(cpsTx, soData);
        }
        else if (NumberTools.equal(cpsTx.getTxSource(), CPSTransaction.TX_SOURCE_HURRICAN_MDU)
                && NumberTools.equal(cpsTx.getServiceOrderType(), CPSTransaction.SERVICE_ORDER_TYPE_INIT_MDU)) {
            // MDU-Initialisierung: MDU-Freigabedatum setzen und Ports freigeben
            HWMdu mdu = (HWMdu) hwService.findRackById(cpsTx.getHwRackId());
            if (mdu == null) {
                throw new FindException(
                        String.format("HW-Rack (MDU) mit der Id %d wurde nicht gefunden.", cpsTx.getHwRackId()));
            }
            if (mdu.getFreigabe() == null) {
                hwService.freigabeMDU(cpsTx.getHwRackId(), new Date());
            }
        }
        else if (isOltChildTx(cpsTx)
                && NumberTools.equal(cpsTx.getServiceOrderType(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)
                && NumberTools.equal(cpsTx.getTxSource(), CPSTransaction.TX_SOURCE_HURRICAN_DPU)) {
            // DPU-Initialisierung analog zu MDU: DPU-Freigabedatum setzen und Ports freigeben
            final HWDpu dpu = (HWDpu) hwService.findRackById(cpsTx.getHwRackId());
            if (dpu == null) {
                throw new FindException(
                        String.format("HW-Rack (DPU) mit der Id %d wurde nicht gefunden.", cpsTx.getHwRackId()));
            }
            if (dpu.getFreigabe() == null) {
                hwService.freigabeDPU(cpsTx.getHwRackId(), new Date());
            }
        }
        else if (isOltChildTx(cpsTx) && NumberTools.isIn(cpsTx.getServiceOrderType(),
                new Number[] { CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE })) {
            // ONT/DPO Freigabe
            HWOltChild oltChild = (HWOltChild) hwService.findRackByIdNewTx(cpsTx.getHwRackId());
            if (oltChild == null) {
                throw new FindException(String.format("HW-Rack (ONT, DPO oder DPU) mit der Id %d wurde nicht gefunden.",
                        cpsTx.getHwRackId()));
            }

            if (oltChild.getFreigabe() == null) {
                try {
                    oltChild.setFreigabe(soData.getExecDateAsDate());
                }
                catch (ParseException e) {
                    LOGGER.error("Kann ExecDate in SoData nicht parsen: " + soData.getExecDate(), e);
                    oltChild.setFreigabe(new Date());
                }
                hwService.saveHWRackNewTx(oltChild);
            }

            createSubscriberCPSTransaction(oltChild, cpsTx);
        } 
        else {
            LOGGER.info(String.format("Keine Aktion konfiguriert fuer die CPS Transaction '%s' mit ServiceOrderType '%s'", cpsTx.getTxSource(), cpsTx.getServiceOrderType()));
        }
    }

    private boolean isOltChildTx(CPSTransaction cpsTx) {
        return ArrayUtils.contains(new Long[] { CPSTransaction.TX_SOURCE_HURRICAN_ONT, CPSTransaction.TX_SOURCE_HURRICAN_DPO,
                CPSTransaction.TX_SOURCE_HURRICAN_MDU, CPSTransaction.TX_SOURCE_HURRICAN_DPU,}, cpsTx.getTxSource());
    }

    /**
     * Performs automatic create subscriber CPS transaction as follow up on successful create device CPS transaction.
     * Prerequisites are that hwOltChild has ports that are already bound to a Hurrican Auftrag.
     * @param oltChild
     * @param cpsTx
     */
    void createSubscriberCPSTransaction(HWOltChild oltChild, CPSTransaction cpsTx) {
        try {
            Set<AuftragDaten> auftragDatenSet = fttxHardwareService.findAuftraege4OltChild(oltChild);
            if(CollectionUtils.isNotEmpty(auftragDatenSet)) {
                for (AuftragDaten auftragDaten : auftragDatenSet) {
                    createSubscriberCPSTransactionForAutrag(oltChild, cpsTx, auftragDaten);
                }
            } else {
                LOGGER.info(String.format(
                        "No CREATE/MODIFY_SUBSCRIBER as no auftrag could be found for OLT Child %s",
                        oltChild.getId()));
            }

        }
        catch (Exception e) {
            createCPSTxLog(cpsTx, String.format("Error in CREATE_SUBSCRIBER CPS-Tx automation: %s", e.getMessage()));
        }
    }

    private void createSubscriberCPSTransactionForAutrag(HWOltChild oltChild, CPSTransaction cpsTx, AuftragDaten auftragDaten) throws FindException {
        try {
            if (!auftragDaten.isAuftragClosed() && auftragDaten.isActiveAt(Date.from(ZonedDateTime.now().toInstant()))) {
                final Long createSubOrderType = cpsService.getExecutableCpsTxServiceOrderType4Subscriber(auftragDaten
                        .getAuftragId());

                CreateCPSTransactionParameter parameters = new CreateCPSTransactionParameter(
                        auftragDaten.getAuftragId(),
                        null,
                        createSubOrderType,
                        CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                        CPSTransaction.SERVICE_ORDER_PRIO_HIGH,
                        new Date(), null, null, null, null, Boolean.FALSE, Boolean.FALSE,
                        getSessionId());

                List<String> errors = cpsService.checkIfTxPermitted4OltChild(oltChild, auftragDaten, createSubOrderType);
                if (CollectionUtils.isEmpty(errors)) {
                    //automatic create subscriber CPS transaction
                    CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(parameters);
                    for (CPSTransaction cpsTransaction : cpsTxResult.getCpsTransactions()) {
                        cpsService.sendCPSTx2CPS(cpsTransaction, getSessionId());
                    }
                }
                else {
                    LOGGER.info(String.format("CREATE/MODIFY_SUBSCRIBER CPS-Tx automation for auftrag %s not possible. Reason: %s",
                            auftragDaten.getAuftragId(),
                            errors.get(0)));
                }
            }
            else {
                LOGGER.info(String.format(
                        "No CREATE/MODIFY_SUBSCRIBER for auftrag %s because auftrag is not active at current date",
                        auftragDaten.getAuftragId()));
            }
        }
        catch (StoreException e) {
            createCPSTxLog(cpsTx, String.format("Error in CREATE_SUBSCRIBER CPS-Tx automation for auftrag %s - %s",
                    auftragDaten.getAuftragId(), e.getMessage()));
        }
    }


    /**
     * prueft den Auftrag mit der angegebenen Auftragsnummer, ob der Auftrag bezueglich seines DSLAM-Profils ueberwacht
     * werden muss. Falls das so ist wird der Auftrag zur Ueberwachung notiert.
     *
     * @param auftragId fuer diesen Auftrag
     * @throws StoreException
     * @throws FindException
     */
    protected void monitorDSLAMProfileForAuftragIfNeccessary(Long auftragId) throws StoreException, FindException {
        final boolean monitoringNeeded = getDslamProfileMonitorService().needsMonitoring(auftragId);
        if (monitoringNeeded) {
            getDslamProfileMonitorService().createDSLAMProfileMonitor(auftragId);
        }
    }

    /**
     * Ermittelt die vom CPS erledigten/betroffenen Verlaufs-Datensaetze und schliesst diese ab.
     */
    void finishProvisioningOrders(CPSTransaction cpsTx, CPSServiceResponseSOData soData) {
        try {
            Set<Long> verlaufIDs = getAffectedVerlaufIDs4CPSTx(cpsTx);
            if (CollectionTools.isNotEmpty(verlaufIDs)) {
                for (Long verlaufId : verlaufIDs) {
                    List<VerlaufAbteilung> verlaufAbteilungen = getBaService().findVerlaufAbteilungen(verlaufId);
                    CollectionUtils.filter(verlaufAbteilungen, new VerlaufAbteilungCPSPredicate());

                    if (CollectionTools.isNotEmpty(verlaufAbteilungen)) {
                        for (VerlaufAbteilung verlaufAbteilung : verlaufAbteilungen) {
                            getBaService().finishVerlauf4Abteilung(
                                    verlaufAbteilung,
                                    "CPS",
                                    (soData != null) ? soData.getComment() : null,
                                    ((soData != null) && (soData.getExecDate() != null)) ? soData.getExecDateAsDate()
                                            : new Date(),
                                    getSessionId(),
                                    null,
                                    null,
                                    null
                            );
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Bauauftrag konnte nicht abgeschlossen werden", e);
            createCPSTxLog(cpsTx,
                    "Bauauftrag konnte nicht abgeschlossen werden:\n" + ExceptionUtils.getFullStackTrace(e));
        }
    }

    /**
     * Ermittelt die Verlauf-IDs, die durch die CPS-Tx bearbeitet werden.
     */
    Set<Long> getAffectedVerlaufIDs4CPSTx(CPSTransaction cpsTx) throws FindException {
        Set<Long> verlaufIDs = new HashSet<>();
        if (cpsTx.getVerlaufId() != null) {
            verlaufIDs.add(cpsTx.getVerlaufId());
        }

        List<CPSTransactionSubOrder> subOrders = cpsService.findCPSTransactionSubOrders(cpsTx.getId());
        if (CollectionTools.isNotEmpty(subOrders)) {
            for (CPSTransactionSubOrder subOrder : subOrders) {
                if (subOrder.getVerlaufId() != null) {
                    verlaufIDs.add(subOrder.getVerlaufId());
                }
            }
        }

        return verlaufIDs;
    }

    /* Setzt den Tx-Status auf FAILURE */
    private void changeCPSTxStateToFailure(CPSTransaction cpsTx) {
        try {
            cpsTx.setTxState(CPSTransaction.TX_STATE_FAILURE);
            cpsService.saveCPSTransaction(cpsTx, getSessionId());
        }
        catch (Exception e) {
            LOGGER.error("changeCPSTxStateToFailure", e);
        }
    }

    /*
     * Ermittelt die CPS-Transaction zur ID.
     *
     * @return
     *
     * @throws ServiceCommandException
     */
    private CPSTransaction getCPSTransaction() throws ServiceCommandException {
        try {
            CPSTransaction cpsTx = cpsService.findCPSTransactionById(getCPSTxId());
            if (cpsTx == null) {
                throw new HurricanServiceCommandException("CPS-Tx not found for ID " + getCPSTxId());
            }

            return cpsTx;
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException("Error loading the CPS-Tx for ID " + getCPSTxId(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        getPreparedValue(KEY_CPS_RESULT, ServiceResponseDocument.class, false,
                "CPS-Result is not of expected type ServiceResponseDocument.class");
    }

    /**
     * Injected
     */
    public void setFttxHardwareService(FTTXHardwareService fttxHardwareService) {
        this.fttxHardwareService = fttxHardwareService;
    }

    protected BAService getBaService() {
        return baService;
    }

    protected ServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    protected DSLAMProfileMonitorService getDslamProfileMonitorService() {
        return dslamProfileMonitorService;
    }

}
