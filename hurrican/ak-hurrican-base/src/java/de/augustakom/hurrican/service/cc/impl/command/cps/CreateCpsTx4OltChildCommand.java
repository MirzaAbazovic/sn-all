/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2014
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static java.lang.String.*;

import java.lang.String;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.oxm.xstream.XStreamMarshaller;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsEndpointDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsItem;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsNetworkDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsResourceOrder;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CPSGetDataService;

/**
 * CPS Command um ein OltChild (ONT, MDU, DPO) zu erstellen, zu modifizieren oder zu löschen.
 */
public class CreateCpsTx4OltChildCommand extends AbstractCPSDataCommand {

    public static final String KEY_RACK_ID = "RACK_ID";

    public static final String KEY_SERVICE_ORDER_TYPE = "SERVICE_ORDER_TYPE";

    private Long hwRackId;

    private Long serviceOrderType;

    private CPSGetDataService cpsGetDataService;

    @Override
    @CcTxMandatory
    public CPSTransaction execute() throws Exception {
        checkValues();
        return createCPSTx();
    }

    /* Ueberprueft die Command-Parameter */
    private void checkValues() throws FindException {
        hwRackId = getPreparedValue(KEY_RACK_ID, Long.class, false, "Rack ID for OltChild is not defined!");
        serviceOrderType = getPreparedValue(KEY_SERVICE_ORDER_TYPE, Long.class, false,
                "Service Order Type is not defined!");

        final List<Long> supportedSoTypes = new ArrayList<>();
        supportedSoTypes.add(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        supportedSoTypes.add(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE);
        supportedSoTypes.add(CPSTransaction.SERVICE_ORDER_TYPE_DELETE_DEVICE);
        if (!supportedSoTypes.contains(serviceOrderType)) {
            throw new FindException("Unsupported Service Order Type: " + serviceOrderType);
        }
    }

    private CPSTransaction createCPSTx() throws HurricanServiceCommandException {
        CPSTransaction cpsTx;
        try {
            HWOltChild oltChild = (HWOltChild) hwService.findRackById(hwRackId);
            if ((oltChild == null) || (oltChild.getOltRackId() == null)) {
                throw new HurricanServiceCommandException("Fehler beim Laden des OltChild-Objekts mit id=" + hwRackId);
            }

            cpsTx = new CPSTransaction();
            cpsTx.setHwRackId(hwRackId);
            cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING);
            cpsTx.setTxSource(getTxSource(oltChild));
            cpsTx.setServiceOrderPrio(CPSTransaction.SERVICE_ORDER_PRIO_HIGH);
            cpsTx.setServiceOrderType(serviceOrderType);
            cpsTx.setEstimatedExecTime(new Date());

            CpsResourceOrder data = getOltChildData(oltChild);
            String soDataAsXMLString = transformSOData2XML(data, (XStreamMarshaller) getXmlMarshaller());
            if (StringUtils.isNotBlank(soDataAsXMLString)) {
                cpsTx.setServiceOrderData(soDataAsXMLString.getBytes(StringTools.CC_DEFAULT_CHARSET));
            }
            else {
                throw new HurricanServiceCommandException("Data for OltChild-CpsTx not defined!");
            }

            cpsService.saveCPSTransaction(cpsTx, getSessionId());
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(
                    "Error creating CPS-Tx for OltChild: " + e.getMessage(), e);
        }
        return cpsTx;
    }

    protected Long getTxSource(HWOltChild oltChild) {
        if (oltChild.getRackTyp() == null) {
            throw new IllegalStateException(
                    format("Fuer OltChild ID:%s ist kein Rack Typ gesetzt ", oltChild.getId()));
        }

        switch (oltChild.getRackTyp()) {
            case HWRack.RACK_TYPE_MDU:
                return CPSTransaction.TX_SOURCE_HURRICAN_MDU;
            case HWRack.RACK_TYPE_DPU:
                return CPSTransaction.TX_SOURCE_HURRICAN_DPU;
            case HWRack.RACK_TYPE_ONT:
                return CPSTransaction.TX_SOURCE_HURRICAN_ONT;
            case HWRack.RACK_TYPE_DPO:
                return CPSTransaction.TX_SOURCE_HURRICAN_DPO;
            default:
                break;
        }
        throw new IllegalStateException(
                format("TX-Source fuer Rack vom Typ %s nicht definiert", oltChild.getRackTyp()));
    }

    private CpsResourceOrder getOltChildData(HWOltChild oltChild) throws Exception {
        HWRack rack = hwService.findRackById(oltChild.getOltRackId());
        if (rack == null) {
            throw new HurricanServiceCommandException(
                    "Fehler beim Laden des OLT-Objekts mit id=" + oltChild.getOltRackId());
        }
        CpsResourceOrder resOrder = new CpsResourceOrder();
        CpsAccessDevice accessDevice = new CpsAccessDevice();
        CpsNetworkDevice networkDevice = cpsGetDataService.getNetworkDevice(oltChild.getGponPort(), rack);
        resOrder.setAccessDevice(accessDevice);
        CpsItem item = new CpsItem();
        accessDevice.setItems(Arrays.asList(item));
        // IP-Gateway soll nur für createDevice bei DPU erzeugt werden
        networkDevice.setIpGateway(determinateIpGateway(oltChild, rack));
        item.setNetworkDevice(networkDevice);
        CpsEndpointDevice endpointDevice = cpsGetDataService.getEndpointDevice(oltChild);
        // Der TddProfil soll nur für createDevice erzeugt werden
        endpointDevice.setProfileTdd(determineTddProfile(oltChild));
        // Die Ip Adresse der DPU auch nur bei createDevice
        endpointDevice.setIpAddress(determinateIpAdress(oltChild));
        endpointDevice.setIpMask(determinateIpMask(oltChild));
        item.setEndpointDevice(endpointDevice);
        return resOrder;
    }

    private String determinateIpAdress(HWRack endpointRack) {
        if (endpointRack.isDpuRack())
            return ((HWDpu) endpointRack).getIpAddress();
        return null;
    }

    private String determinateIpMask(HWRack endpointRack) {
        if (endpointRack.isDpuRack())
            // erstmal einen statischen Wert uebermitteln
            return "255.255.240.0";
        return null;
    }

    private String determinateIpGateway(HWOltChild endpointRack, HWRack rack) throws Exception {
        if (endpointRack.isDpuRack()) {
            HWOlt hwOlt = (HWOlt)rack;
                return hwOlt.getIpNetzVon();
        }
        return null;
    }

    private String determineTddProfile(HWRack endpointRack) {
        if (endpointRack.isDpuRack()) {
            return ((HWDpu) endpointRack).getTddProfil();
        }
        return null;
    }


    public void setCpsGetDataService(CPSGetDataService cpsGetDataService) {
        this.cpsGetDataService = cpsGetDataService;
    }
}
