/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2009 14:13:50
 */
package de.augustakom.hurrican.model.cc.cps;

import java.util.*;
import com.google.common.collect.ImmutableMap;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Abbildung einer CPS-Transaktion. <br> In diesem Modell sind die Header-Daten fuer eine
 * einzelne Provisionierung ueber den CPS (CommonProvisioningServer) enthalten. Achtung: Die Reihenfolge der IDs bzw.
 * Zustaende wird z.B. von CPSTransactionDAOImpl verwendet.
 *
 *
 */
public class CPSTransaction extends AbstractCCIDModel implements CCAuftragModel {

    public static final String CPS_APPLICATION_KEY_TAIFUNNUMBER = "TAIFUNNUMBER";
    public static final String CPS_APPLICATION_KEY_STACK_SEQUENCE = "STACK_SEQUENCE";

    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine 'Create-Subscriber'-Funktion handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_CREATE_SUB = 14000L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine 'Modify-Subscriber'-Funktion handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_MODIFY_SUB = 14001L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine 'Cancel-Subscriber'-Funktion handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_CANCEL_SUB = 14002L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine 'Query'-Funktion handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_QUERY = 14003L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um einen Storno einer CPS-Tx handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_CANCEL_TX = 14004L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine MDU-Initialisieurng handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_INIT_MDU = 14005L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine Sperre handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_LOCK_SUB = 14006L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine MDU-Initialisieurng handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_UPDATE_MDU = 14007L;
    /**
     * Referenz-ID fuer 'serviceOrderType' definiert, dass es sich um eine Hardware Query handelt.
     */
    public static final Long SERVICE_ORDER_TYPE_QUERY_HARDWARE = 14008L;
    /**
     * 'serviceOrderType' fuer eine Abfrage beim Hurrican-Webserver-Server
     */
    public static final Long SERVICE_ORDER_TYPE_GET_SODATA = 14009L;
    /**
     * 'serviceOrderType' fuer createDevice
     */
    public static final Long SERVICE_ORDER_TYPE_CREATE_DEVICE = 14010L;
    /**
     * 'serviceOrderType' fuer modifyDevice
     */
    public static final Long SERVICE_ORDER_TYPE_MODIFY_DEVICE = 14011L;
    /**
     * 'serviceOrderType' fuer deleteDevice
     */
    public static final Long SERVICE_ORDER_TYPE_DELETE_DEVICE = 14012L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX durch einen Bauauftrag ausgeloest wurde.
     */
    public static final Long TX_SOURCE_HURRICAN_VERLAUF = 14100L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX durch eine Aenderung von Rufnummernleistungen ausgeloest
     * wurde.
     */
    public static final Long TX_SOURCE_HURRICAN_DN = 14101L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX durch eine (Ent-)Sperre ausgeloest wurde.
     */
    public static final Long TX_SOURCE_HURRICAN_LOCK = 14102L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX auf einem Auftrag ausgeloest wurde.
     */
    public static final Long TX_SOURCE_HURRICAN_ORDER = 14103L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX fuer eine MDU bestimmt ist.
     */
    public static final Long TX_SOURCE_HURRICAN_MDU = 14104L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX durch einen Webservice erstellt wurde.
     */
    public static final Long TX_SOURCE_HURRICAN_WEBSERVICE = 14105L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX fuer eine ONT bestimmt ist.
     */
    public static final Long TX_SOURCE_HURRICAN_ONT = 14106L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX fuer eine DPO bestimmt ist.
     */
    public static final Long TX_SOURCE_HURRICAN_DPO = 14107L;
    /**
     * Referenz-ID fuer 'txSource' definiert, dass die TX fuer eine DPU bestimmt ist.
     */
    public static final Long TX_SOURCE_HURRICAN_DPU = 14108L;   // TODO es ist nur naechste freie nummer. muss man ewt mit CPS geklaert werden?
    /**
     * Referenz-ID fuer 'txState' definiert, dass die Daten vorbereitet/gesammelt sind - noch nicht an CPS
     * uebermittelt.
     */
    public static final Long TX_STATE_IN_PREPARING = 14200L;
    /**
     * Referenz-ID fuer 'txState' definiert, dass waehrend der Zusammenstellung der Tx ein Fehler aufgetreten ist.
     */
    public static final Long TX_STATE_IN_PREPARING_FAILURE = 14205L;
    /**
     * Referenz-ID fuer 'txState' definiert, dass die Provisionierung noch beim CPS ist.
     */
    public static final Long TX_STATE_IN_PROVISIONING = 14210L;
    /**
     * Referenz-ID fuer 'txState' definiert, dass die Provisionierung abgebrochen wurde.
     */
    public static final Long TX_STATE_CANCELLED = 14220L;
    /**
     * Referenz-ID fuer 'txState' definiert, dass die Uebertragung zum CPS fehl geschlagen ist.
     */
    public static final Long TX_STATE_TRANSMISSION_FAILURE = 14230L;
    /**
     * Referenz-ID fuer 'txState' definiert, dass die Provisionierung Fehler verursacht hat.
     */
    public static final Long TX_STATE_FAILURE = 14240L;
    /**
     * Referenz-ID fuer 'txState' definiert, dass die Provisionierung fehlerhaft war aber geschlossen wurde.
     */
    public static final Long TX_STATE_FAILURE_CLOSED = 14250L;
    /**
     * Referenz-ID fuer 'txState' definiert, dass die Provisionierung erfolgreich beendet wurde.
     */
    public static final Long TX_STATE_SUCCESS = 14260L;
    /**
     * Referenz-ID fuer eine hohe Prioritaet einer CPS-Transaction.
     */
    public static final Long SERVICE_ORDER_PRIO_HIGH = 14300L;
    /**
     * Referenz-ID fuer eine normale Prioritaet einer CPS-Transaction.
     */
    public static final Long SERVICE_ORDER_PRIO_DEFAULT = 14310L;
    /**
     * Referenz-ID fuer eine niedrige Prioritaet einer CPS-Transaction.
     */
    public static final Long SERVICE_ORDER_PRIO_LOW = 14320L;
    /**
     * Definiert die Uhrzeit (6 Uhr), zu der eine CPS-TX standardmaessig ausgefuehrt werden soll.
     */
    public static final int EXEC_HOUR_DEFAULT = 6;
    private static final Map<Long, String> READABLE_SERVICE_ORDER_TYPES = ImmutableMap.<Long, String>builder()
            .put(SERVICE_ORDER_TYPE_CREATE_SUB, "Create-Subscriber")
            .put(SERVICE_ORDER_TYPE_MODIFY_SUB, "Modify-Subscriber")
            .put(SERVICE_ORDER_TYPE_CANCEL_SUB, "Cancel-Subscriber")
            .put(SERVICE_ORDER_TYPE_QUERY, "Query")
            .put(SERVICE_ORDER_TYPE_CANCEL_TX, "Storno")
            .put(SERVICE_ORDER_TYPE_INIT_MDU, "MDU-Initialisieurng")
            .put(SERVICE_ORDER_TYPE_LOCK_SUB, "Sperre")
            .put(SERVICE_ORDER_TYPE_UPDATE_MDU, "MDU-Initialisieurng")
            .put(SERVICE_ORDER_TYPE_QUERY_HARDWARE, "Query Hardware")
            .put(SERVICE_ORDER_TYPE_GET_SODATA, "Query Hurrican")
            .put(SERVICE_ORDER_TYPE_CREATE_DEVICE, "Create Device")
            .put(SERVICE_ORDER_TYPE_MODIFY_DEVICE, "Modify Device")
            .put(SERVICE_ORDER_TYPE_DELETE_DEVICE, "Delete Device")
            .build();
    private Long orderNoOrig = null;
    private Long auftragId = null;
    private Long verlaufId = null;
    private Long hwRackId = null;
    private Long txState = null;
    private Long txSource = null;
    private Long serviceOrderType = null;
    private Long serviceOrderPrio = null;
    private Long serviceOrderStackId = null;
    private Long serviceOrderStackSeq = null;
    private Long region = null;
    private Date estimatedExecTime = null;
    private Date requestAt = null;
    private Date responseAt = null;
    private byte[] requestData = null;
    private byte[] responseData = null;
    private byte[] serviceOrderData = null;
    private String txUser = null;
    private String userW = null;

    public static String getReadableServiceOrderType(long serviceOrderType) {
        if (READABLE_SERVICE_ORDER_TYPES.containsKey(serviceOrderType)) {
            return READABLE_SERVICE_ORDER_TYPES.get(serviceOrderType);
        }
        return "";
    }

    /**
     * Ueberprueft, ob es sich bei der Transaction um eine aktive CPS-Transaction handelt. Dies ist dann der Fall, wenn
     * der Status 'PREPARING' oder 'IN_PROVISIONING' ist.
     *
     * @return true, wenn die CPS-Transaction aktiv ist.
     */
    public boolean isActive() {
        return NumberTools.isIn(getTxState(),
                new Number[] { TX_STATE_IN_PREPARING, TX_STATE_IN_PROVISIONING });
    }

    /**
     * Ueberprueft, ob die CPS-Transaction noch im Status 'PREPARING' ist.
     *
     * @return
     */
    public boolean isPreparing() {
        return NumberTools.equal(getTxState(), TX_STATE_IN_PREPARING);
    }

    /**
     * Ueberprueft, ob die CPS-Transaction abgeschlossen ist. <br> Dies ist dann der Fall, wenn der Status 'FAILURE',
     * 'FAILURE_CLOSED' oder 'SUCCESS' ist.
     *
     * @return true, wenn die CPS-Transaction abgeschlossen ist.
     */
    public boolean isFinished() {
        return NumberTools.isIn(getTxState(),
                new Number[] { TX_STATE_SUCCESS, TX_STATE_FAILURE, TX_STATE_FAILURE_CLOSED });
    }

    /**
     * Ueberprueft, ob der Status der Tx auf 'TX_STATE_CANCELLED' gesetzt ist.
     *
     * @return true, wenn der Status der Tx auf 'TX_STATE_CANCELLED' gesetzt ist
     */
    public boolean isCancelled() {
        return NumberTools.equal(getTxState(), TX_STATE_CANCELLED);
    }

    /**
     * Ueberprueft, ob die CPS-Tx auf einem Fehler-Status steht.
     *
     * @return
     */
    public boolean isFailure() {
        return NumberTools.isIn(getTxState(),
                new Number[] { TX_STATE_FAILURE,
                        TX_STATE_FAILURE_CLOSED,
                        TX_STATE_IN_PREPARING_FAILURE,
                        TX_STATE_TRANSMISSION_FAILURE }
        );
    }

    /**
     * Prueft, ob es sich bei der CPS-Tx um eine der UseCases 'createSubscriber', 'modifySubscriber' oder
     * 'cancelSubscriber' handelt.
     */
    public boolean isSubscriberType() {
        return NumberTools.isIn(getServiceOrderType(), new Number[] {
                SERVICE_ORDER_TYPE_CREATE_SUB, SERVICE_ORDER_TYPE_CANCEL_SUB, SERVICE_ORDER_TYPE_MODIFY_SUB });
    }

    /**
     * Prueft, ob es sich bei der CPS-Tx um den UseCase 'createSubscriber' handelt.
     *
     * @return true, wenn der UseCase 'createSubscriber' ist.
     */
    public boolean isCreateSubscriber() {
        return NumberTools.equal(getServiceOrderType(), SERVICE_ORDER_TYPE_CREATE_SUB);
    }

    /**
     * Prueft, ob es sich bei der CPS-Tx um den UseCase 'modifySubscriber' handelt.
     *
     * @return true, wenn der UseCase 'modifySubscriber' ist.
     */
    public boolean isModifySubscriber() {
        return NumberTools.equal(getServiceOrderType(), SERVICE_ORDER_TYPE_MODIFY_SUB);
    }

    /**
     * Prueft, ob es sich bei der CPS-Tx um den UseCase 'cancelSubscriber' handelt.
     *
     * @return true, wenn der UseCase 'cancelSubscriber' ist.
     */
    public boolean isCancelSubscriber() {
        return NumberTools.equal(getServiceOrderType(), SERVICE_ORDER_TYPE_CANCEL_SUB);
    }

    /**
     * Prueft, ob es sich bei der CPS-Tx um den UseCase 'lockSubscriber' handelt.
     *
     * @return true, wenn der UseCase 'lockSubscriber' ist.
     */
    public boolean isLockSubscriber() {
        return NumberTools.equal(getServiceOrderType(), SERVICE_ORDER_TYPE_LOCK_SUB);
    }

    /**
     * @return Returns the orderNoOrig.
     */
    public Long getOrderNoOrig() {
        return orderNoOrig;
    }

    /**
     * @param orderNoOrig The orderNoOrig to set.
     */
    public void setOrderNoOrig(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
    }

    /**
     * @return Returns the auftragId.
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the verlaufId.
     */
    public Long getVerlaufId() {
        return verlaufId;
    }

    /**
     * @param verlaufId The verlaufId to set.
     */
    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

    /**
     * @return the hwRackId
     */
    public Long getHwRackId() {
        return hwRackId;
    }

    /**
     * @param hwRackId the hwRackId to set
     */
    public void setHwRackId(Long hwRackId) {
        this.hwRackId = hwRackId;
    }

    /**
     * @return Returns the txState.
     */
    public Long getTxState() {
        return txState;
    }

    /**
     * @param txState The txState to set.
     */
    public void setTxState(Long txState) {
        this.txState = txState;
    }

    /**
     * @return Returns the txSource.
     */
    public Long getTxSource() {
        return txSource;
    }

    /**
     * @param txSource The txSource to set.
     */
    public void setTxSource(Long txSource) {
        this.txSource = txSource;
    }

    /**
     * @return Returns the serviceOrderType.
     */
    public Long getServiceOrderType() {
        return serviceOrderType;
    }

    /**
     * @param serviceOrderType The serviceOrderType to set.
     */
    public void setServiceOrderType(Long serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
    }

    /**
     * @return Returns the serviceOrderPrio.
     */
    public Long getServiceOrderPrio() {
        return serviceOrderPrio;
    }

    /**
     * @param serviceOrderPrio The serviceOrderPrio to set.
     */
    public void setServiceOrderPrio(Long serviceOrderPrio) {
        this.serviceOrderPrio = serviceOrderPrio;
    }

    /**
     * @return Returns the serviceOrderStackId.
     */
    public Long getServiceOrderStackId() {
        return serviceOrderStackId;
    }

    /**
     * Definiert die Reihenfolge innerhalb einer Stack-Sequence.
     *
     * @param serviceOrderStackId The serviceOrderStackId to set.
     */
    public void setServiceOrderStackId(Long serviceOrderStackId) {
        this.serviceOrderStackId = serviceOrderStackId;
    }

    /**
     * @return Returns the serviceOrderStackSeq.
     */
    public Long getServiceOrderStackSeq() {
        return serviceOrderStackSeq;
    }

    /**
     * Definiert eine Stack-Sequence. Ueber diesen Parameter koennen mehrere CPS-Transactions als 'zusammengehoerig'
     * markiert werden.
     *
     * @param serviceOrderStackSeq The serviceOrderStackSeq to set.
     */
    public void setServiceOrderStackSeq(Long serviceOrderStackSeq) {
        this.serviceOrderStackSeq = serviceOrderStackSeq;
    }

    /**
     * @return Returns the region.
     */
    public Long getRegion() {
        return region;
    }

    /**
     * @param region The region to set.
     */
    public void setRegion(Long region) {
        this.region = region;
    }

    /**
     * @return Returns the estimatedExecTime.
     */
    public Date getEstimatedExecTime() {
        return estimatedExecTime;
    }

    /**
     * @param estimatedExecTime The estimatedExecTime to set.
     */
    public void setEstimatedExecTime(Date estimatedExecTime) {
        this.estimatedExecTime = estimatedExecTime;
    }

    /**
     * @return Returns the requestAt.
     */
    public Date getRequestAt() {
        return requestAt;
    }

    /**
     * @param requestAt The requestAt to set.
     */
    public void setRequestAt(Date requestAt) {
        this.requestAt = requestAt;
    }

    /**
     * @return Returns the responseAt.
     */
    public Date getResponseAt() {
        return responseAt;
    }

    /**
     * @param responseAt The responseAt to set.
     */
    public void setResponseAt(Date responseAt) {
        this.responseAt = responseAt;
    }

    /**
     * @return Returns the requestData.
     */
    public byte[] getRequestData() {
        return requestData;
    }

    /**
     * @param requestData The requestData to set.
     */
    public void setRequestData(byte[] requestData) {
        this.requestData = requestData;
    }

    /**
     * @return Returns the responseData.
     */
    public byte[] getResponseData() {
        return responseData;
    }

    /**
     * @param responseData The responseData to set.
     */
    public void setResponseData(byte[] responseData) {
        this.responseData = responseData;
    }

    /**
     * @return the serviceOrderData
     */
    public byte[] getServiceOrderData() {
        return serviceOrderData;
    }

    /**
     * @param serviceOrderData the serviceOrderData to set
     */
    public void setServiceOrderData(byte[] serviceOrderData) {
        this.serviceOrderData = serviceOrderData;
    }

    /**
     * @return Returns the txUser.
     */
    public String getTxUser() {
        return txUser;
    }

    /**
     * @param txUser The txUser to set.
     */
    public void setTxUser(String txUser) {
        this.txUser = txUser;
    }

    /**
     * @return Returns the userW.
     */
    public String getUserW() {
        return userW;
    }

    /**
     * @param userW The userW to set.
     */
    public void setUserW(String userW) {
        this.userW = userW;
    }
}


