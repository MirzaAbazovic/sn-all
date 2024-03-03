/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2009 09:54:26
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPortData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSTelephoneData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSTelephoneNumberData;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um die notwendigen Daten fuer Standard-Telephonie (ueber EWSD) zu ermitteln und in ein entsprechendes
 * XML-Element einzutragen.
 *
 *
 */
public class CPSGetEWSDDataCommand extends AbstractCPSGetDNDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetEWSDDataCommand.class);

    private EndstellenService endstellenService;
    private HVTService hvtService;
    private RangierungsService rangierungsService;
    private HWService hwService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            // Rufnummern ermitteln
            List<Rufnummer> activeDNs = getActiveDNs(getCPSTransaction().getOrderNoOrig());
            if (CollectionTools.isNotEmpty(activeDNs)) {
                CPSTelephoneData telephoneData = new CPSTelephoneData();
                boolean hasBlockDN = false;

                for (Rufnummer dn : activeDNs) {
                    if (!hasBlockDN) {
                        // pruefen, ob es sich um eine Block-Rufnummer handelt
                        hasBlockDN = isBlockDN(dn);
                    }

                    // pruefen, ob Rufnummer von M-net oder extern
                    boolean mnetDN = isMnetDN(dn, rufnummerService);

                    // Datenobjekt fuer Rufnummer anlegen
                    CPSTelephoneNumberData number = new CPSTelephoneNumberData();
                    number.transferDNData(dn);
                    number.setMainDN(BooleanTools.getBooleanAsString(dn.isMainNumber()));
                    number.setMnetDN(BooleanTools.getBooleanAsString(Boolean.valueOf(mnetDN)));

                    // Rufnummernleistungen laden
                    loadDNServices(getCPSTransaction().getAuftragId(), dn, number, true, getSessionId());

                    // EWSD-Nummer in SO-Datenobjekt eintragen
                    telephoneData.addCPSTelephoneNumber(number);
                }

                // Typ- und Port-Informationen ermitteln
                loadTypeData(telephoneData, hasBlockDN);
                loadOrig1(telephoneData);
                loadPortData(telephoneData);

                // kombinierte EWSD-Daten in SO-Datenobjekt eintragen
                getServiceOrderData().setTelephone(telephoneData);
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                    null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error while loading EWSD DN data: " + e.getMessage(), this.getClass());
        }
    }

    /*
     * Ueberprueft, ob es sich bei der Rufnummer um eine Block-Rufnummer handelt.
     * Dies ist dann der Fall, wenn die Rufnummer
     */
    private boolean isBlockDN(Rufnummer dn) {
        return StringUtils.isNotBlank(dn.getRangeFrom());
    }

    /*
     * Laedt die Typ-Daten fuer einen EWSD-Anschluss.
     * @param telData Daten-Objekt, in das die Typ-Informationen eingetragen werden sollen.
     * @param activeDNs die aktiven Rufnummern
     * @throws ServiceCommandException
     */
    private void loadTypeData(CPSTelephoneData telData, boolean hasBlockDN) throws ServiceCommandException {
        try {
            // Telephonie-Typ (Analog/ISDN/PBX) ermitteln (ISDN/PBX bei Anzahl DN > 1 ODER wenn DN-Block)
            Produkt produkt = getProdukt();
            if ((produkt.getMaxDnCount().intValue() > 1) || BooleanTools.nullToFalse(produkt.getDnBlock())) {
                boolean hasTK = ccLeistungsService.isTechLeistungActive(
                        getCPSTransaction().getAuftragId(),
                        TechLeistung.ExterneLeistung.ISDN_TYP_TK.leistungNo,
                        getCPSTransaction().getEstimatedExecTime());

                // falls bei den Rufnummern eine Block-Rufnummer dabei ist, bzw. es sich um eine TK-Anlage handelt --> PBX
                if (hasBlockDN || hasTK) {
                    telData.setType(CPSTelephoneData.TELEPHONE_TYPE_PBX);
                }
                else {
                    telData.setType(CPSTelephoneData.TELEPHONE_TYPE_ISDN);
                }
            }
            else {
                telData.setType(CPSTelephoneData.TELEPHONE_TYPE_ANALOG);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while loading type data for EWSD: " + e.getMessage(), e);
        }
    }

    /*
     * Laedt die Leitwegkennung des Anschlusses.
     * @param telData
     * @throws ServiceCommandException
     */
    private void loadOrig1(CPSTelephoneData telData) throws ServiceCommandException {
        try {
            // HVT ermitteln und ORIG1 setzen (=Ursprungskennung Leitweg)
            Endstelle esB = endstellenService.findEndstelle4Auftrag(
                    getCPSTransaction().getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            if ((esB != null) && (esB.getHvtIdStandort() != null)) {
                HVTStandort hvtStd = hvtService.findHVTStandort(esB.getHvtIdStandort());
                telData.setOrig1(hvtStd.getEwsdOr1());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while loading switch ID for EWSD: " + e.getMessage(), e);
        }
    }

    /*
     * Laedt die Port-Daten des Auftrags.
     * @param telData Daten-Objekt, in das die Port-Informationen eingetragen werden.
     * @throws ServiceCommandException
     */
    private void loadPortData(CPSTelephoneData telData) throws ServiceCommandException {
        try {
            Set<Long> activeOrderIDs = getOrderIDs4CPSTx();

            List<CPSPortData> portDataList = new ArrayList<CPSPortData>();
            for (Long auftragId : activeOrderIDs) {
                if (isAuftragActive(auftragId)) {
                    // EWSD-Equipment Definition laden
                    Equipment ewsdEquipment = loadEWSDEquipment(auftragId);
                    if (ewsdEquipment != null) {
                        CPSPortData port = new CPSPortData();
                        port.setSwitchName(ewsdEquipment.getHwSwitch() != null ? ewsdEquipment.getHwSwitch().getName() : null);
                        port.setEqn(ewsdEquipment.getHwEQN());
                        port.setV5Port(ewsdEquipment.getV5Port());

                        // MediaGateway und AccessController laden
                        HWRack rack = hwService.findRackForBaugruppe(ewsdEquipment.getHwBaugruppenId());
                        if ((rack != null) && (rack instanceof HWDlu)) {
                            HWDlu hwDlu = (HWDlu) rack;
                            port.setMgName(hwDlu.getMediaGatewayName());
                            port.setAccessController(hwDlu.getAccessController());
                        }

                        portDataList.add(port);
                    }
                    else {
                        throw new HurricanServiceCommandException("EWSD equipment could not be found!");
                    }
                }
            }

            telData.setPorts(portDataList);
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while loading Port data for EWSD: " + e.getMessage(), e);
        }
    }

    /*
     * Prueft, ob der angegebene Auftrag zum Ausfuehrungszeitpunkt der CPS-Tx aktiv ist.
     * Dies ist dann der Fall, wenn der Auftrag wirklich in Betrieb ist, oder
     * zum Zeitpunkt der CPS-Tx aktiv wird.
     * Ist der Auftrag in einem Kuendigungsstatus, ist der Auftrag fuer die CPS-Tx dann
     * aktiv, wenn das Kuendigungsdatum > der CPS-Tx Ausfuehrung ist.
     */
    private boolean isAuftragActive(Long auftragId) throws FindException, ServiceNotFoundException {
        AuftragDaten auftragDaten = getCCService(CCAuftragService.class).findAuftragDatenByAuftragIdTx(auftragId);
        if (auftragDaten.isInBetriebOrAenderung()) {
            return true;
        }
        else if (auftragDaten.isInKuendigung()) {
            if (DateTools.isDateAfter(auftragDaten.getKuendigung(), getCPSTransaction().getEstimatedExecTime()) ||
                    getServiceOrderData().isInitialLoad()) {
                // Kuendigungsdatum des Auftrags ist NACH der CPS-Tx --> Port ist also noch aktiv!
                return true;
            }
            else {
                return false;
            }
        }

        return true;
    }

    /*
     * Ermittelt das EWSD-Equipment. <br>
     * Die Ermittlung erfolgt zuerst ueber Endstelle B und falls dort kein
     * entsprechender EWSD-Port vorhanden, ueber die Endstelle A.
     * @param auftragId
     * @return
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    private Equipment loadEWSDEquipment(Long auftragId) throws FindException {
        Endstelle esB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);

        // EWSD-Equipment von Endstelle B ermitteln
        Long rangId = (esB.getRangierIdAdditional() != null)
                ? esB.getRangierIdAdditional() : esB.getRangierId();
        if (rangId != null) {
            Rangierung rangierung = rangierungsService.findRangierung(rangId);
            if (rangierung.getEqInId() != null) {
                return rangierungsService.findEquipment(rangierung.getEqInId());
            }
        }

        // EWSD-Equipment von Endstelle A ermitteln (z.B. bei PMX oder V5.1)
        Endstelle esA = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_A);
        if ((esA != null) && (esA.getRangierId() != null)) {
            Rangierung rangierung = rangierungsService.findRangierung(esA.getRangierId());
            return rangierungsService.findEquipment(rangierung.getEqInId());
        }

        return null;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected
     */
    @Override
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

}


