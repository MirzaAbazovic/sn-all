/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2010 08:12:24
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;


/**
 * Hilfsklasse, um einen Port-Wechsel (EQ_OUT) durchzufuehren.
 *
 *
 */
public class ChangeEqOutPortTool {

    private static final Logger LOGGER = Logger.getLogger(ChangeEqOutPortTool.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EQCrossConnectionService")
    private EQCrossConnectionService crossConnectionService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.VlanService")
    private VlanService vlanService;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService")
    private EkpFrameContractService ekpFrameContractService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungFreigabeService")
    private RangierungFreigabeService rangierungFreigabeService;

    /**
     * Fuehrt den Port-Wechsel auf den angegebenen Rangierungen durch. Portwechsel von einfacher Rangierung auf eine
     * Kombi-Rangierung und umgekehrt ist moeglich.
     */
    public List<Rangierung> changeEqOut(Reference changeReason, Endstelle endstelle,
            Rangierung rangierungToChange, Rangierung rangierungAddToChange,
            Rangierung rangierungToUse, Rangierung rangierungAddToUse, AKUser user, Long sessionId)
            throws StoreException {
        if (rangierungToUse == null) {
            throw new StoreException("Es wurde kein Port fuer den Wechsel ausgewaehlt!");
        }
        if ((changeReason == null) || (changeReason.getIntValue() == null)) {
            throw new StoreException("Grund fuer den Wechsel nicht angegeben oder nicht richtig konfiguriert!");
        }

        try {
            String userName = (user != null) ? user.getLoginName() : HurricanConstants.UNKNOWN;
            String changeReasonTxt = new StringBuilder("Port-Wechsel! Change-Reason:")
                    .append(changeReason.getStrValue())
                    .toString();

            List<Rangierung> result = new ArrayList<Rangierung>();
            result.add(rangierungToChange);
            result.add(rangierungToUse);
            if (rangierungAddToChange != null) {
                result.add(rangierungAddToChange);
            }
            if (rangierungAddToUse != null) {
                result.add(rangierungAddToUse);
            }

            AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());

            initializeCPS(auftragDaten, sessionId);

            if (!rangierungToUse.isFttBOrH()) {
                definePhysikTypes(rangierungToChange, rangierungToUse);
            }

            Long eqOutIdSave = rangierungToUse.getEqOutId();
            rangierungToUse.setEqOutId(rangierungToChange.getEqOutId());
            rangierungToUse.setEsId(endstelle.getId());
            rangierungToUse.setUserW(userName);
            if (rangierungToUse.isRangierungDefekt()) {
                // eine als defekt markierte Rangierung muss wieder freigegeben werden, wenn auf diesen Port
                // gewechselt werden soll. (Ist z.B. notwendig, wenn Kreuzung wieder rueckgaengig gemacht werden soll.)
                rangierungToUse.setFreigegeben(Freigegeben.freigegeben);
            }

            Rangierung newRangierungAddForOrder;
            Rangierung newRangierungForOrder;

            newRangierungForOrder = rangierungsService.saveRangierung(rangierungToUse, true);
            result.add(newRangierungForOrder);

            rangierungToChange.setEqOutId(eqOutIdSave);
            rangierungToChange.setFreigegeben(Freigegeben.getFreigegeben(changeReason.getIntValue()));
            rangierungToChange.setUserW(userName);
            Rangierung rangierungToChangeHistNew = rangierungFreigabeService.freigebenRangierung(rangierungToChange,
                    changeReasonTxt, true);
            result.add(rangierungToChangeHistNew);

            if (rangierungAddToUse != null) {
                rangierungAddToUse.setEsId(endstelle.getId());
                rangierungAddToUse.setUserW(userName);
                newRangierungAddForOrder = rangierungsService.saveRangierung(rangierungAddToUse, true);
                result.add(newRangierungAddForOrder);
            }
            else {
                newRangierungAddForOrder = null;
            }
            if (rangierungAddToChange != null) {
                rangierungAddToChange.setFreigegeben(Freigegeben.getFreigegeben(changeReason.getIntValue()));
                rangierungAddToChange.setUserW(userName);
                Rangierung rangierungAddToChangeHistNew = rangierungFreigabeService.freigebenRangierung(rangierungAddToChange,
                        changeReasonTxt, true);
                result.add(rangierungAddToChangeHistNew);
            }

            // HUR-20214: NonUniqueObjectException "A different object with the same identifier value was already
            // associated with the session"
            // Wenn fuer den Auftrag ein InitCPS notwendig ist, bekommt die Hibernate Session die Endstelle zugeordnet.
            // Sollte danach mit der Endstelleninstanz (von aussen) ein Save durchgefuehrt werden, kracht es.
            Endstelle endstelleInSession = endstellenService.findEndstelle(endstelle.getId());
            endstelleInSession.setRangierId((newRangierungForOrder != null) ? newRangierungForOrder.getId() : null);
            endstelleInSession.setRangierIdAdditional((newRangierungAddForOrder != null) ? newRangierungAddForOrder.getId() : null);
            endstellenService.saveEndstelle(endstelleInSession);
            endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelleInSession);

            if (rangierungToUse.isFttBOrH()) {
                calculateVlans(auftragDaten);
            }
            else {
                defineDefaultCrossConnection(newRangierungForOrder, auftragDaten, sessionId);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Kreuzen des EQ-OUT Ports!", e);
        }
    }

    /**
     * Ermittelt an Hand der Uebertragungsverfahren der EQ-Out Ports evtl. notwendige Aenderungen des Physiktyps. Fuer
     * eine Rangierung kann die Aenderung nur innerhalb des Herstellers erfolgen. ToChange und ToUse koennen allerdings
     * durchaus verschiedene Hersteller sein. Erkannte Physiktyp-Aenderungen werden den beiden Rangierungen direkt
     * zugeordnet.
     */
    void definePhysikTypes(Rangierung rangierungToChange, Rangierung rangierungToUse) throws FindException {
        try {
            Equipment eqOutToChange = rangierungsService.findEquipment(rangierungToChange.getEqOutId());
            Equipment eqOutToUse = rangierungsService.findEquipment(rangierungToUse.getEqOutId());

            Uebertragungsverfahren uetvToChange = eqOutToChange.getUetv();
            Uebertragungsverfahren uetvToUse = eqOutToUse.getUetv();
            if ((uetvToChange == null) || (uetvToUse == null)) {
                return;
            }

            if ((eqOutToChange.isUetvHochbit() && !eqOutToUse.isUetvHochbit()) ||
                    (!eqOutToChange.isUetvHochbit() && eqOutToUse.isUetvHochbit())) {
                throw new FindException("Die Uebertragungsverfahren der beiden Ports sind unterschiedlich (N <--> H)!");
            }

            Long hwBaugruppeTypeToChange = findHwBaugruppe4EqIn(rangierungToChange.getEqInId());
            Long hwBaugruppeTypeToUse = findHwBaugruppe4EqIn(rangierungToUse.getEqInId());

            //@formatter:off
            Set<PhysikTypChange> changeSet = new HashSet<PhysikTypChange>();
            // wenn von Uetv (alt) nach Uetv (neu) getauscht wird, fuer (optional) Baugruppentyp und bestehenden PT, dann wird daraus neuer PT
            changeSet.add(new PhysikTypChange("H04", "H13", HWBaugruppenTyp.HW_ADBF,  PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI,     PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI));
            changeSet.add(new PhysikTypChange("H13", "H04", null,                     PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI,      PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI));
            changeSet.add(new PhysikTypChange("H04", "H13", HWBaugruppenTyp.HW_ADBF2, PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI,     PhysikTyp.PHYSIKTYP_ADSL2P_MS_HUAWEI));
            changeSet.add(new PhysikTypChange("H13", "H04", null,                     PhysikTyp.PHYSIKTYP_ADSL2P_MS_HUAWEI,   PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI));
            changeSet.add(new PhysikTypChange("H04", "H13", null,                     PhysikTyp.PHYSIKTYP_ADSL_ATM_ALCATEL,   PhysikTyp.PHYSIKTYP_ADSL2P_ATM_ALCATEL));
            changeSet.add(new PhysikTypChange("H13", "H04", null,                     PhysikTyp.PHYSIKTYP_ADSL2P_ATM_ALCATEL, PhysikTyp.PHYSIKTYP_ADSL_ATM_ALCATEL));
            changeSet.add(new PhysikTypChange("H11", "H16", null,                     PhysikTyp.PHYSIKTYP_SDSL_DA_HUAWEI,     PhysikTyp.PHYSIKTYP_SDSL_DA_HUAWEI));
            changeSet.add(new PhysikTypChange("H16", "H11", null,                     PhysikTyp.PHYSIKTYP_SHDSL_HUAWEI,       PhysikTyp.PHYSIKTYP_SDSL_DA_HUAWEI));
            //@formatter:on

            if (eqOutToChange.isUetvHochbit()) {
                Iterator<PhysikTypChange> changeIterator = changeSet.iterator();
                while (changeIterator.hasNext()) {
                    PhysikTypChange physikTypChange = changeIterator.next();
                    if (physikTypChange.matches(eqOutToChange.getUetv(), eqOutToUse.getUetv(),
                            rangierungToChange.getPhysikTypId(), hwBaugruppeTypeToChange)) {
                        rangierungToChange.setPhysikTypId(physikTypChange.physikTypNew);
                    }

                    if (physikTypChange.matches(eqOutToUse.getUetv(), eqOutToChange.getUetv(),
                            rangierungToUse.getPhysikTypId(), hwBaugruppeTypeToUse)) {
                        rangierungToUse.setPhysikTypId(physikTypChange.physikTypNew);
                    }
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Ziel-Physiktypen: " + e.getMessage(), e);
        }
    }

    Long findHwBaugruppe4EqIn(Long eqInId) throws FindException {
        Long hwBaugruppeTyp = null;
        Equipment eqIn = rangierungsService.findEquipment(eqInId);
        if ((eqIn != null) && (eqIn.getHwBaugruppenId() != null)) {
            HWBaugruppe hwBaugruppe = hwService.findBaugruppe(eqIn.getHwBaugruppenId());
            if ((hwBaugruppe != null) && (hwBaugruppe.getHwBaugruppenTyp() != null)) {
                hwBaugruppeTyp = hwBaugruppe.getHwBaugruppenTyp().getId();
            }
        }
        return hwBaugruppeTyp;
    }

    /*
     * Falls der Auftrag bisher noch nicht an den CPS uebergeben wurde, wird der
     * Auftrag noch initialisiert.
     * Dies ist notwendig, damit der CPS bei dem Port-Wechsel auch eine Aenderung
     * des Auftrags erkennt.
     */
    private void initializeCPS(AuftragDaten auftragDaten, Long sessionId) throws StoreException {
        try {
            if ((auftragDaten != null) && NumberTools.isGreaterOrEqual(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB)) {
                boolean orderIsInitialized = false;

                List<CPSTransaction> cpsTransactions = cpsService.findSuccessfulCPSTransaction4TechOrder(auftragDaten.getAuftragId());
                if (CollectionTools.isNotEmpty(cpsTransactions)) {
                    orderIsInitialized = true;
                }

                if (!orderIsInitialized) {
                    CPSProvisioningAllowed allowed = cpsService.isCPSProvisioningAllowed(
                            auftragDaten.getAuftragId(), LazyInitMode.noInitialLoad, false, false, true);
                    if ((allowed != null) && allowed.isProvisioningAllowed()) {
                        try {
                            CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(
                                    new CreateCPSTransactionParameter(auftragDaten.getAuftragId(), null, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                                            CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, new Date(), null, null, null, LazyInitMode.initialLoad, false,
                                            false, sessionId)
                            );

                            String warnings = (cpsTxResult.getWarnings() != null) ? cpsTxResult.getWarnings().getWarningsAsText() : null;
                            if (StringUtils.isNotBlank(warnings)) {
                                throw new StoreException("Error or warnings during CPS init: " + warnings);
                            }

                            if ((cpsTxResult.getCpsTransactions() != null) && (cpsTxResult.getCpsTransactions().size() == 1)) {
                                cpsService.sendCPSTx2CPS(cpsTxResult.getCpsTransactions().get(0), sessionId);
                            }
                        }
                        catch (StoreException e) {
                            LOGGER.error(e.getMessage(), e);
                            throw new StoreException("CPS-Init war fehlerhaft: " + e.getMessage(), e);
                        }
                    }
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Ermittlung des CPS-Status ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    /**
     * Generiert die Default-CrossConnections fuer den Port wenn dieser in einem Subrack ist (ansonsten wird keine
     * CrossConnection generiert).
     *
     * @param rangierungForCrossConnection
     * @param auftragDaten
     * @param sessionId
     * @throws StoreException
     * @throws FindException
     */
    private void defineDefaultCrossConnection(Rangierung rangierungForCrossConnection, AuftragDaten auftragDaten,
            Long sessionId) throws StoreException, FindException {
        if (rangierungForCrossConnection.getEqInId() != null) {
            Equipment eqIn = rangierungsService.findEquipment(rangierungForCrossConnection.getEqInId());

            if (eqIn != null) {
                if (BooleanTools.nullToFalse(eqIn.getManualConfiguration())) {
                    eqIn.setManualConfiguration(Boolean.FALSE);
                    rangierungsService.saveEquipment(eqIn);
                }
                if (crossConnectionService.isCrossConnectionEnabled(eqIn)) {
                    Boolean vierDrahtProdukt = produktService.isVierDrahtProdukt(auftragDaten.getProdId());

                    crossConnectionService.defineDefaultCrossConnections4Port(
                            eqIn, auftragDaten.getAuftragId(), new Date(), vierDrahtProdukt, sessionId);
                }
            }
        }
    }

    private void calculateVlans(AuftragDaten auftragDaten) throws FindException, StoreException {
        EkpFrameContract ekp = ekpFrameContractService.findEkp4AuftragOrDefaultMnet(auftragDaten.getAuftragId(), LocalDate.now(), true);
        if (ekp == null) {
            throw new StoreException("Could not calculate VLANs because EKP frame contract not found for order!");
        }
        vlanService.assignEqVlans(ekp, auftragDaten.getAuftragId(), auftragDaten.getProdId(), LocalDate.now(), null);
    }

    /* Hilfsklasse, um Aenderungen des Uebertragungsverfahrens auf den Physiktyp zu matchen. */
    static class PhysikTypChange {
        String uetvOrig;
        String uetvNew;
        Long physikTypOrig;
        Long physikTypNew;
        Long hwBaugruppenTyp; // optional

        PhysikTypChange(String uetvOrig, String uetvNew, Long hwBaugruppenTyp, Long physikTypOrig, Long physikTypNew) {
            super();
            this.uetvOrig = uetvOrig;
            this.uetvNew = uetvNew;
            this.physikTypOrig = physikTypOrig;
            this.physikTypNew = physikTypNew;
            this.hwBaugruppenTyp = hwBaugruppenTyp;
        }

        public boolean matches(Uebertragungsverfahren uetvOrig, Uebertragungsverfahren uetvNew, Long physikTypOrig,
                Long hwBaugruppenTyp) throws FindException {
            boolean match = true;
            if ((((uetvOrig != null) && (uetvOrig.name() != null)) && !uetvOrig.name().equals(this.uetvOrig))
                    || ((uetvOrig == null) && (this.uetvOrig != null))) {
                match = false;
            }
            if ((((uetvNew != null) && (uetvNew.name() != null)) && !uetvNew.name().equals(this.uetvNew))
                    || ((uetvNew == null) && (this.uetvNew != null))) {
                match = false;
            }
            if (((physikTypOrig != null) && !physikTypOrig.equals(this.physikTypOrig))
                    || ((physikTypOrig == null) && (this.physikTypOrig != null))) {
                match = false;
            }
            if ((this.hwBaugruppenTyp != null)) {
                if (match && (hwBaugruppenTyp == null)) {
                    throw new FindException(
                            "Zur Ermittlung des korrekten Physiktyps ist der Baugruppentyp erforderlich!");
                }
                if (!this.hwBaugruppenTyp.equals(hwBaugruppenTyp)) {
                    match = false;
                }
            }
            return match;
        }

    }

    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

}


