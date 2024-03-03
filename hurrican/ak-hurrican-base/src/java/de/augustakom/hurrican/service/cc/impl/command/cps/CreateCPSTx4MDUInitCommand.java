/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2009 14:27:57
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.oxm.xstream.XStreamMarshaller;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMduInitializeData;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 * Command-Klasse, um eine CPS-Transaction zur Initialisierung einer MDU zu erzeugen. <br> Das Command sammelt die
 * notwendigen MDU-Daten, erstellt die CPS-Transaction und sendet diese auch sofort an den CPS.
 *
 *
 */
public class CreateCPSTx4MDUInitCommand extends AbstractCPSCommand {

    /**
     * Konstante, um dem Command die Rack-ID der zu initialisierenden MDU zu uebergeben.
     */
    public static final String KEY_MDU_RACK_ID = "mdu.rack.id";
    /**
     * Konstante, um dem Command mitzuteilen, ob ein updateMDU (true) oder initMDU (false) geschickt werden soll.
     */
    public static final String KEY_SEND_UPDATE_FLAG = "send.update";
    public static final String KEY_USE_INITIALIZED = "use.initialized";
    private static final Logger LOGGER = Logger.getLogger(CreateCPSTx4MDUInitCommand.class);
    Long hwRackId = null;
    Boolean sendUpdate = null;
    Boolean useInitialized = null;
    CPSMduInitializeData mduInitData = null;
    private HVTService hvtService;
    private CPSTransaction cpsTx = null;

    /**
     * called by Spring
     */
    public void init() {
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            checkValues();
            createMIData();
            createCPSTx();

            return cpsTx;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error creating or sending CPS-Tx for MDU init: " + e.getMessage(), e);
        }
    }

    /* Erzeugt und speichert die CPS-Transaction. */
    private void createCPSTx() throws HurricanServiceCommandException {
        try {
            cpsTx = new CPSTransaction();
            cpsTx.setHwRackId(hwRackId);
            cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING);
            cpsTx.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_MDU);
            cpsTx.setServiceOrderPrio(CPSTransaction.SERVICE_ORDER_PRIO_HIGH);
            cpsTx.setServiceOrderType((BooleanTools.nullToFalse(sendUpdate))
                    ? CPSTransaction.SERVICE_ORDER_TYPE_UPDATE_MDU
                    : CPSTransaction.SERVICE_ORDER_TYPE_INIT_MDU);
            cpsTx.setEstimatedExecTime(new Date());

            String soDataAsXMLString = transformSOData2XML(mduInitData, (XStreamMarshaller) getXmlMarshaller());
            if (StringUtils.isNotBlank(soDataAsXMLString)) {
                cpsTx.setServiceOrderData(soDataAsXMLString.getBytes(StringTools.CC_DEFAULT_CHARSET));
            }
            else {
                throw new HurricanServiceCommandException("MDU-Init-Data not defined!");
            }

            cpsService.saveCPSTransaction(cpsTx, getSessionId());

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error creating CPS-Tx for MDU init: " + e.getMessage(), e);
        }
    }

    /* Laedt die MDU-Daten und generiert das MDU-Init-Objekt. */
    void createMIData() throws HurricanServiceCommandException {
        try {
            HWMdu mdu = (HWMdu) hwService.findRackById(hwRackId);
            if ((mdu == null) || (mdu.getOltRackId() == null)) {
                throw new HurricanServiceCommandException("Fehler beim Laden des MDU-Objekts");
            }
            HWOlt olt = (HWOlt) hwService.findRackById(mdu.getOltRackId());
            if (olt == null) {
                throw new HurricanServiceCommandException("Fehler beim Laden des OLT-Objekts");
            }

            HVTTechnik manufacturer = hvtService.findHVTTechnik(mdu.getHwProducer());

            HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(mdu.getHvtIdStandort());
            if ((hvtGruppe == null) || StringUtils.isBlank(hvtGruppe.getOrtsteil())) {
                throw new HurricanServiceCommandException("Standort nicht ermittelbar!");
            }

            mduInitData = new CPSMduInitializeData();
            mduInitData.setMduGeraeteBezeichnung(mdu.getGeraeteBez());
            mduInitData.setMduTyp(mdu.getMduType());
            mduInitData.setMduStandort(hvtGruppe.getOrtsteil());
            mduInitData.setManufacturer(manufacturer.getCpsName());
            mduInitData.setMduIpAdress(mdu.getIpAddress());
            mduInitData.setSerialNo(mdu.getSerialNo());
            mduInitData.setOltBezeichnung(olt.getGeraeteBez());
            mduInitData.setQinqStart(olt.getQinqVon());

            String gponPort = mdu.getGponPort();
            mduInitData.setGponPort(gponPort);

            // INITIALIZED nur wenn angefordert an den CPS uebermitteln
            if (useInitialized) {
                mduInitData.setInitialized(mdu.getFreigabe() != null);
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error loading MDU data: " + e.getMessage(), e);
        }
    }

    /* Ueberprueft die Command-Parameter */
    private void checkValues() throws FindException {
        hwRackId = getPreparedValue(KEY_MDU_RACK_ID, Long.class, false,
                "MDU Rack ID is not defined!");

        sendUpdate = getPreparedValue(KEY_SEND_UPDATE_FLAG, Boolean.class, false, "Init or Update Flag is not defined!");
        useInitialized = getPreparedValue(KEY_USE_INITIALIZED, Boolean.class, false,
                "KEY_USE_INITIALIZED is not defined!");
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

}
