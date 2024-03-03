/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2009 10:02:43
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.exceptions.LanguageException;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSFTTBData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSGetDataService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command-Klasse, um die FttX Hardware-Daten (MDU/OLT/DPO) zu einem Auftrag zu laden.
 */
public class CPSGetFTTXDataCommand extends AbstractCPSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetFTTXDataCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CPSGetDataService")
    private CPSGetDataService cpsGetDataService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;

    /**
     * called by Spring
     *
     * @throws ServiceNotFoundException
     */
    @Override
    public void init() throws ServiceNotFoundException {
        super.init();
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            final Long auftragId = getCPSTransaction().getAuftragId();
            final HWRack hwRack = getHWRack(auftragId);

            setAccessDevice(auftragId, hwRack);

            // RFC-435 (HUR-4053): nur die letzten drei Ziffern, die den korrekten DTAG HVT ASB angeben übermitteln
            Integer asb = getServiceOrderData().getAsb();
            if (asb != null) {
                String asbString = StringUtils.right(asb.toString(), 3);
                asb = Integer.parseInt(asbString);
                getServiceOrderData().setAsb(asb);
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FTTX-Data: " + e.getMessage(), this.getClass());
        }
    }

    private HWRack getHWRack(final Long auftragId) throws FindException {
        final Endstelle esB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        final Equipment eqIn = (esB != null) ? rangierungsService.findEquipment4Endstelle(esB, false, false) : null;
        final HWRack hwRack = (eqIn != null && eqIn.getHwBaugruppenId() != null) ?
                hwService.findRackForBaugruppe(eqIn.getHwBaugruppenId()) : null;
        if (hwRack == null) {
            throw new RuntimeException(String.format("Für den Auftrag %s konnte das Hardware Rack nicht ermittelt " +
                    "werden!", auftragId));
        }
        return hwRack;
    }

    private void setAccessDevice(final Long auftragId, final HWRack hwRack) throws LanguageException {
        final String portId = definePortId(auftragId);
        final Date execTime = getCPSTransaction().getEstimatedExecTime();
        //Die Unterscheidung ob ONT, MDU oder DPO, kann nicht am Standorttyp festgemacht werden,
        //da ONTs auch an FTTB- oder FTTX_BR - Standorten realisiert sein koennen.
        if (hwRack instanceof HWMdu || hwRack instanceof HWDpo || hwRack instanceof HWDpu) {
            final CpsAccessDevice accessDeviceFttb = cpsGetDataService.getFttbData(auftragId, portId, execTime);
            getServiceOrderData().setAccessDevice(accessDeviceFttb);
            if (hwRack instanceof HWMdu) {
                //TODO: Jan. 2015 - Diesen 'Hack' entfernen, sobald der CPS mit einem AccessDevice fuer MDUs zurechtkommt
                final CPSFTTBData fttbData = cpsGetDataService.getFttbData4Wholesale(auftragId, portId, execTime);
                getServiceOrderData().setFttb(fttbData);
            }
        }
        else if (hwRack instanceof HWOnt) {
            final CpsAccessDevice accessDeviceFtth = cpsGetDataService.getFtthData(auftragId, execTime,
                    deviceNecessary(), portId);
            getServiceOrderData().setAccessDevice(accessDeviceFtth);
        }
    }
}


