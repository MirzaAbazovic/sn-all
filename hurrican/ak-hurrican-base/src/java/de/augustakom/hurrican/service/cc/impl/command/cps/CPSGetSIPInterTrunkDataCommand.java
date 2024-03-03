/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 12:24:06
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunk;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSSIPInterTrunkData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.SIPInterTrunkService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Command-Klasse, um die SIP-InterTrunk Daten zu ermitteln.
 */
public class CPSGetSIPInterTrunkDataCommand extends AbstractCPSGetDNDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetSIPInterTrunkDataCommand.class);

    private SIPInterTrunkService sipInterTrunkService;

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            // Rufnummern ermitteln
            Long orderNoOrig = getCPSTransaction().getOrderNoOrig();
            List<Rufnummer> activeDNs = getActiveDNs(orderNoOrig);
            if (CollectionTools.isEmpty(activeDNs)) {
                throw new FindException(String.format("no trunked numbers found! (orderNoOrig:%s)", orderNoOrig));
            }

            Kunde reseller = loadReseller();

            List<AuftragSIPInterTrunk> auftragSipInterTrunks = loadSIPInterTrunkData();
            if (CollectionTools.isEmpty(auftragSipInterTrunks)) {
                throw new FindException("SIP InterTrunk data not found!");
            }

            List<CPSSIPInterTrunkData> sipInterTrunkDatas = new ArrayList<>();
            for (Rufnummer dn : activeDNs) {
                // pruefen, ob Rufnummer von M-net oder extern
                boolean mnetDN = isMnetDN(dn, rufnummerService);

                CPSSIPInterTrunkData sipInterTrunk = new CPSSIPInterTrunkData();
                sipInterTrunk.setResellerId(String.format("%s", reseller.getKundeNo()));
                sipInterTrunk.transferDNData(dn);
                sipInterTrunk.setMnetDN(BooleanTools.getBooleanAsString(mnetDN));
                sipInterTrunk.setCarrierId(dn.getActCarrierPortKennung());

                for (AuftragSIPInterTrunk auftragSipInterTrunk : auftragSipInterTrunks) {
                    sipInterTrunk.addSwitch(auftragSipInterTrunk.getHwSwitch().getName());
                }

                sipInterTrunkDatas.add(sipInterTrunk);
            }

            getServiceOrderData().setSipInterTrunk(sipInterTrunkDatas);

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                    null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error while loading EWSD DN data: " + e.getMessage(), this.getClass());
        }
    }


    /**
     * Ermittelt die SIP InterTrunk Daten des SIP InterTrunk Hauptauftrags.
     *
     * @return
     * @throws FindException
     */
    List<AuftragSIPInterTrunk> loadSIPInterTrunkData() throws FindException {
        AuftragDaten sipInterTrunkAuftragDaten = ccAuftragService.findMainOrder4SIPCustomer(getCPSTransaction().getAuftragId());
        if (sipInterTrunkAuftragDaten != null) {
            return sipInterTrunkService.findSIPInterTrunks4Order(sipInterTrunkAuftragDaten.getAuftragId());
        }

        return null;
    }


    /**
     * Ermittelt den Hauptkunden zu dem aktuellen Auftrag bzw. Kunden. Der Hauptkunde entspricht dem SIP InterTrunk
     * Reseller.
     *
     * @return
     * @throws HurricanServiceCommandException wenn zu dem Auftrag kein Reseller ermittelt werden kann.
     */
    Kunde loadReseller() throws HurricanServiceCommandException {
        try {
            Auftrag sipAuftrag = ccAuftragService.findAuftragById(getAuftragDaten().getAuftragId());
            if (sipAuftrag == null) {
                throw new HurricanServiceCommandException("Could not load actual order!");
            }

            Kunde customer = kundenService.findKunde(sipAuftrag.getKundeNo());
            if ((customer != null) && (customer.getHauptKundenNo() != null)) {
                return kundenService.findKunde(customer.getHauptKundenNo());
            }
            else {
                throw new HurricanServiceCommandException("Could not find the SIP InterTrunk reseller!");
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Reseller ID of SIP InterTrunk not found!");
        }
    }


    /**
     * Injected
     */
    public void setSipInterTrunkService(SIPInterTrunkService sipInterTrunkService) {
        this.sipInterTrunkService = sipInterTrunkService;
    }

}


