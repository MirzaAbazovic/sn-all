/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2005 07:38:08
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Jasper-DataSource, um die Endstellen-Daten fuer einen Bauauftrag zu laden.
 *
 *
 */
public class Endstelle4BAJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(Endstelle4BAJasperDS.class);

    private static final String KREUZUNGSTYP_EQ_OUT = "EQ-Out";
    private static final String KREUZUNGSTYP_EQ_IN = "EQ-In";

    private Long esId = null;
    private Long verlaufId = null;

    private VerbindungsBezeichnung vbz = null;
    private Endstelle endstelle = null;
    private Anschlussart anschlussart = null;
    private HVTGruppe hvtGruppe = null;
    private HVTStandort hvtStd = null;
    private Ansprechpartner ansprechpartner = null;
    private Leitungsart leitungsart = null;
    private Schnittstelle schnittstelle = null;
    private Rangierung rangierung1 = null;
    private Rangierung rangierungAdd = null;
    private Rangierung rangierungX = null;
    private Equipment eqIn = null;
    private Equipment eqOut = null;
    private Equipment eqIn2 = null;
    private Equipment eqOut2 = null;
    private Equipment eqInX = null;  // EQ-In der gekreuzten Rangierung
    private Equipment eqOutX = null; // EQ-Out der gekreuzten Rangierung
    private HWRack hwRackEqIn = null;
    private HWBaugruppe hwBgEqIn = null;
    private HWRack hwRackEqIn2 = null;
    private String kreuzungsTyp = null;
    private Carrierbestellung carrierbestellung = null;
    private TechLeistung upstream = null;
    private TechLeistung downstream = null;
    private static Map<Long, PhysikTyp> physiktypMap = null;
    private HWSwitch hwSwitch = null;

    private boolean printData = true;

    private EndstellenService endstellenService;
    private AnsprechpartnerService ansprechpartnerService;
    private PhysikService physikService;
    private RangierungsService rangierungsService;
    private HVTService hvtService;
    private CCAuftragService auftragService;
    private CarrierService carrierService;
    private CCLeistungsService leistungsService;
    private HWService hwService;

    /**
     * @param esId      ID der Endstelle, deren Daten ermittelt werden sollen
     * @param verlaufId (optional) ID des Verlaufs, fuer den der Report erstellt wird
     * @throws AKReportException
     */
    public Endstelle4BAJasperDS(Long esId, Long verlaufId) throws AKReportException {
        super();
        this.esId = esId;
        this.verlaufId = verlaufId;

        init();
    }

    @Override
    protected void init() throws AKReportException {
        try {
            endstellenService = getCCService(EndstellenService.class);
            ansprechpartnerService = getCCService(AnsprechpartnerService.class);
            physikService = getCCService(PhysikService.class);
            hvtService = getCCService(HVTService.class);
            auftragService = getCCService(CCAuftragService.class);
            carrierService = getCCService(CarrierService.class);
            rangierungsService = getCCService(RangierungsService.class);
            leistungsService = getCCService(CCLeistungsService.class);
            hwService = getCCService(HWService.class);

            endstelle = endstellenService.findEndstelle(esId);
            if (endstelle != null) {
                AuftragDaten ad = auftragService.findAuftragDatenByEndstelle(esId);
                hwSwitch = auftragService.getSwitchKennung4Auftrag(ad.getAuftragId());
                hvtStd = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
                hvtGruppe = hvtService.findHVTGruppe4Standort(endstelle.getHvtIdStandort());
                vbz = physikService.findVerbindungsBezeichnungByAuftragId(ad.getAuftragId());

                anschlussart = physikService.findAnschlussart(endstelle.getAnschlussart());
                leitungsart = physikService.findLeitungsart4ES(esId);
                schnittstelle = physikService.findSchnittstelle4ES(esId);
                Ansprechpartner.Typ ansprechpartnerTyp = (endstelle.isEndstelleB()) ? ENDSTELLE_B : ENDSTELLE_A;
                ansprechpartner = ansprechpartnerService.findPreferredAnsprechpartner(ansprechpartnerTyp, ad.getAuftragId());

                // Up-/Downstream ermitteln
                upstream = leistungsService.findTechLeistung4Auftrag(ad.getAuftragId(), TechLeistung.TYP_UPSTREAM, false);
                downstream = leistungsService.findTechLeistung4Auftrag(ad.getAuftragId(), TechLeistung.TYP_DOWNSTREAM, false);

                List<Carrierbestellung> cbs = carrierService.findCBs4Endstelle(esId);
                carrierbestellung = ((cbs != null) && (!cbs.isEmpty())) ? cbs.get(0) : null;

                rangierung1 = rangierungsService.findRangierung(endstelle.getRangierId());
                if (rangierung1 != null) {
                    eqIn = rangierungsService.findEquipment(rangierung1.getEqInId());
                    eqOut = rangierungsService.findEquipment(rangierung1.getEqOutId());
                    if ((eqIn != null) && (eqIn.getHwBaugruppenId() != null)) {
                        hwRackEqIn = hwService.findRackForBaugruppe(eqIn.getHwBaugruppenId());
                        hwBgEqIn = hwService.findBaugruppe(eqIn.getHwBaugruppenId());
                    }
                }

                rangierungAdd = rangierungsService.findRangierung(endstelle.getRangierIdAdditional());
                if (rangierungAdd != null) {
                    eqIn2 = rangierungsService.findEquipment(rangierungAdd.getEqInId());
                    eqOut2 = rangierungsService.findEquipment(rangierungAdd.getEqOutId());
                    if ((eqIn2 != null) && (eqIn2.getHwBaugruppenId() != null)) {
                        hwRackEqIn2 = hwService.findRackForBaugruppe(eqIn2.getHwBaugruppenId());
                    }
                }

                // gekreuzte Rangierung ermitteln
                loadRangierung4Kreuzung(rangierung1);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Endstellen-Daten konnten nicht ermittelt werden.");
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Endstellen-Daten konnten nicht ermittelt werden," +
                    " da Services nicht gefunden wurden.");
        }
    }

    /*
     * Laedt die Rangierungs-Daten fuer eine evtl. durchzufuehrende Stift-Kreuzung
     * (bei DSL-Kreuzungen oder TAL-Nutzungsaenderungen).
     */
    private void loadRangierung4Kreuzung(Rangierung rangierungAct) throws AKReportException {
        try {
            AuftragTechnik auftragTechnik = auftragService.findAuftragTechnik4ESGruppe(endstelle.getEndstelleGruppeId());
            if (auftragTechnik != null) {
                // Physikuebernahme ueber Verlaufs-ID ermitteln
                PhysikUebernahme pu = physikService.findPhysikUebernahme4Verlauf(auftragTechnik.getAuftragId(), verlaufId);
                if ((pu != null) && NumberTools.isIn(pu.getAenderungstyp(),
                        new Number[] { PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG,
                                PhysikaenderungsTyp.STRATEGY_WANDEL_ANALOG_ISDN, PhysikaenderungsTyp.STRATEGY_WANDEL_ISDN_ANALOG,
                                PhysikaenderungsTyp.STRATEGY_WANDEL_SDSL_2TO4DRAHT }
                )) {

                    if (NumberTools.notEqual(pu.getAuftragIdA(), pu.getAuftragIdB())) {
                        // gekreuzte Rangierung ermitteln (Endversion der Rangierung)
                        List<Endstelle> endstellenX = endstellenService.findEndstellen4Auftrag(pu.getAuftragIdB());
                        if (endstellenX != null) {
                            for (Endstelle esX : endstellenX) {
                                if ((esX.getRangierId() != null) &&
                                        StringUtils.equals(esX.getEndstelleTyp(), endstelle.getEndstelleTyp())) {
                                    Rangierung history = rangierungsService.findHistoryFrom(esX.getRangierId());
                                    if (history != null) {
                                        eqInX = rangierungsService.findEquipment(history.getEqInId());
                                        eqOutX = rangierungsService.findEquipment(history.getEqOutId());
                                        kreuzungsTyp = KREUZUNGSTYP_EQ_OUT;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (rangierungAct != null) {
                            // Kreuzung wurde auf gleichem Auftrag durchgefuehrt
                            boolean filterWithEqOut = (!NumberTools.equal(
                                    pu.getAenderungstyp(), PhysikaenderungsTyp.STRATEGY_WANDEL_SDSL_2TO4DRAHT));
                            rangierungX = rangierungsService.findKreuzung(rangierungAct.getId(), filterWithEqOut);
                            if (rangierungX != null) {
                                eqInX = rangierungsService.findEquipment(rangierungX.getEqInId());
                                eqOutX = rangierungsService.findEquipment(rangierungX.getEqOutId());
                                kreuzungsTyp = (filterWithEqOut) ? KREUZUNGSTYP_EQ_OUT : KREUZUNGSTYP_EQ_IN;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Rangierungs-Daten fuer die Kreuzung konnten nicht ermittelt werden!");
        }
    }

    @Override
    public boolean next() throws JRException {
        if (printData) {
            printData = false;
            return true;
        }
        return false;
    }

    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("ANSCHLUSSART".equals(field)) {
            String kvzNummer = (eqOut != null) ? eqOut.getKvzNummer() : null;
            String clusterId = (hvtStd != null) ? hvtStd.getClusterId() : null;
            String anschlussartName = (anschlussart != null) ? anschlussart.getAnschlussart() : null;
            return StringTools.join(new String[] { anschlussartName, kvzNummer, clusterId }, " - ", true);
        }
        else if ("SCHNITTSTELLE".equals(field)) {
            return (schnittstelle != null) ? schnittstelle.getSchnittstelle() : null;
        }
        else if ("LEITUNGSART".equals(field)) {
            return (leitungsart != null) ? leitungsart.getName() : null;
        }
        else if ("ES_ID".equals(field)) {
            return (endstelle != null) ? endstelle.getId() : null;
        }
        else if ("ENDSTELLE".equals(field)) {
            return (endstelle != null) ? endstelle.getEndstelle() : null;
        }
        else if ("ES_NAME".equals(field)) {
            return (endstelle != null) ? endstelle.getName() : null;
        }
        else if ("ES_PLZ_ORT".equals(field)) {
            if (endstelle != null) {
                StringBuilder s = new StringBuilder();
                s.append((StringUtils.isNotBlank(endstelle.getPlz())) ? endstelle.getPlz() + " " : "");
                s.append((endstelle.getOrt() != null) ? endstelle.getOrt() : "");
                return s.toString();
            }
            return null;
        }
        else if ("ES_ANSP".equals(field)) {
            return (ansprechpartner != null) ? ansprechpartner.getDisplayText() : null;
        }
        else if ("SWITCH".equals(field)) {
            return (hwSwitch != null) ? hwSwitch.getName() : null;
        }
        else if ("ES_TYP".equals(field)) {
            return (endstelle != null) ? endstelle.getEndstelleTyp() : null;
        }
        else if ("ES_BEMERKUNG".equals(field)) {
            return (endstelle != null) ? endstelle.getBemerkungStawa() : null;
        }
        else if ("ES_VLAN".equals(field)) {
            return (endstelle != null) ? endstelle.getVlan() : null;
        }
        else if ("HVT_ORT".equals(field)) {
            return (hvtGruppe != null) ? hvtGruppe.getOrt() : null;
        }
        else if ("HVT_ORTSTEIL".equals(field)) {
            return (hvtGruppe != null)
                    ? String.format("%s (%s, %s)", hvtGruppe.getOrtsteil(), hvtGruppe.getStreetAndHouseNum(), hvtGruppe.getOrt())
                    : null;
        }
        else if ("HVT_ONKZ".equals(field)) {
            return (hvtGruppe != null) ? hvtGruppe.getOnkz() : null;
        }
        else if ("HVT_ASB".equals(field)) {
            return (hvtStd != null) ? hvtStd.getAsb() : null;
        }
        else if ("CB_LBZ".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getLbz() : null;
        }
        else if ("CB_VTRNR".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getVtrNr() : null;
        }
        else if ("CB_BEREITSTELLUNG".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getBereitstellungAm() : null;
        }
        else if ("CB_LL".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getLl() : null;
        }
        else if ("CB_AQS".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getAqs() : null;
        }
        else if ("CB_MAX_BITRATE".equals(field)) {
            return (carrierbestellung != null) ? carrierbestellung.getMaxBruttoBitrate() : null;
        }
        else if ("UPSTREAM".equals(field)) {
            return (upstream != null) ? upstream.getName() : null;
        }
        else if ("DOWNSTREAM".equals(field)) {
            return (downstream != null) ? downstream.getName() : null;
        }
        // Equipment-Daten von Rangierung 1
        else if ("EQOUT_VERTEILER".equals(field)) {
            return (eqOut != null) ? eqOut.getRangVerteiler() : null;
        }
        else if ("EQOUT_LEISTE_STIFT1".equals(field)) {
            return (eqOut != null) ? eqOut.getEinbau1() : null;
        }
        else if ("EQOUT_LEISTE_STIFT2".equals(field)) {
            return (eqOut != null) ? eqOut.getEinbau2() : null;
        }
        else if ("EQOUT_HWEQN".equals(field)) {
            return (eqOut != null) ? eqOut.getHwEQN() : null;
        }
        else if ("EQOUT_PHYSIKTYP".equals(field) || "EQIN_PHYSIKTYP".equals(field)) {
            return (rangierung1 != null) ? getPhysiktyp(rangierung1.getPhysikTypId()) : null;
        }
        else if ("EQIN_VERTEILER".equals(field)) {
            return (eqIn != null) ? eqIn.getRangVerteiler() : null;
        }
        else if ("EQIN_LEISTE_STIFT1".equals(field)) {
            return (eqIn != null) ? eqIn.getEinbau1() : null;
        }
        else if ("EQIN_LEISTE_STIFT2".equals(field)) {
            return (eqIn != null) ? eqIn.getEinbau2() : null;
        }
        else if ("EQIN_HWEQN".equals(field)) {
            return (eqIn != null) ? eqIn.getHwEQN() : null;
        }
        else if ("EQIN_GERAETEBEZ".equals(field)) {
            return (hwRackEqIn != null) ? createHWRackName(hwRackEqIn) : null;
        }
        else if ("EQIN_SERIALNO".equals(field)) {
            return (hwRackEqIn instanceof HWOltChild) ? ((HWOltChild) hwRackEqIn).getSerialNo() : null;
        }
        else if ("EQIN_CHASSIS_IDENTIFIER".equals(field)) {
            return (hwRackEqIn instanceof HWDpo) ? ((HWDpo) hwRackEqIn).getChassisIdentifier() : null;
        }
        else if ("EQIN_CHASSIS_SLOT".equals(field)) {
            return (hwRackEqIn instanceof HWDpo) ? ((HWDpo) hwRackEqIn).getChassisSlot() : null;
        }
        else if ("EQIN_BAUGRUPPENTYP".equals(field)) {
            return ((hwBgEqIn != null) && (hwBgEqIn.getHwBaugruppenTyp() != null)) ? hwBgEqIn.getHwBaugruppenTyp().getName() : null;
        }
        else if ("EQIN_LAYER2PROTOCOL".equals(field)) {
            return ((eqIn != null) && (eqIn.getSchicht2Protokoll() != null)) ? eqIn.getSchicht2Protokoll().name() : null;
        }
        // Equipment-Daten von Rangierung 2
        else if ("EQOUT2_VERTEILER".equals(field)) {
            return (eqOut2 != null) ? eqOut2.getRangVerteiler() : null;
        }
        else if ("EQOUT2_LEISTE_STIFT1".equals(field)) {
            return (eqOut2 != null) ? eqOut2.getEinbau1() : null;
        }
        else if ("EQOUT2_LEISTE_STIFT2".equals(field)) {
            return (eqOut2 != null) ? eqOut2.getEinbau2() : null;
        }
        else if ("EQOUT2_HWEQN".equals(field)) {
            return (eqOut2 != null) ? eqOut2.getHwEQN() : null;
        }
        else if ("EQOUT2_PHYSIKTYP".equals(field) || "EQIN2_PHYSIKTYP".equals(field)) {
            return (rangierungAdd != null) ? getPhysiktyp(rangierungAdd.getPhysikTypId()) : null;
        }
        else if ("EQIN2_VERTEILER".equals(field)) {
            return (eqIn2 != null) ? eqIn2.getRangVerteiler() : null;
        }
        else if ("EQIN2_LEISTE_STIFT1".equals(field)) {
            return (eqIn2 != null) ? eqIn2.getEinbau1() : null;
        }
        else if ("EQIN2_LEISTE_STIFT2".equals(field)) {
            return (eqIn2 != null) ? eqIn2.getEinbau2() : null;
        }
        else if ("EQIN2_HWEQN".equals(field)) {
            return (eqIn2 != null) ? eqIn2.getHwEQN() : null;
        }
        else if ("EQIN2_GERAETEBEZ".equals(field)) {
            return (hwRackEqIn2 != null) ? createHWRackName(hwRackEqIn2) : null;
        }
        // Equipment-Daten der gekreuzten Rangierung
        else if ("EQOUT_VERTEILER_X".equals(field)) {
            return (eqOutX != null) ? eqOutX.getRangVerteiler() : null;
        }
        else if ("EQOUT_LEISTE_STIFT1_X".equals(field)) {
            return (eqOutX != null) ? eqOutX.getEinbau1() : null;
        }
        else if ("EQOUT_LEISTE_STIFT2_X".equals(field)) {
            return (eqOutX != null) ? eqOutX.getEinbau2() : null;
        }
        else if ("EQOUT_HWEQN_X".equals(field)) {
            return (eqOutX != null) ? eqOutX.getHwEQN() : null;
        }
        else if ("EQOUT_PHYSIKTYP_X".equals(field) || "EQIN_PHYSIKTYP_X".equals(field)) {
            return (rangierungX != null) ? getPhysiktyp(rangierungX.getPhysikTypId()) : null;
        }
        else if ("EQIN_VERTEILER_X".equals(field)) {
            return (eqInX != null) ? eqInX.getRangVerteiler() : null;
        }
        else if ("EQIN_LEISTE_STIFT1_X".equals(field)) {
            return (eqInX != null) ? eqInX.getEinbau1() : null;
        }
        else if ("EQIN_LEISTE_STIFT2_X".equals(field)) {
            return (eqInX != null) ? eqInX.getEinbau2() : null;
        }
        else if ("EQIN_HWEQN_X".equals(field)) {
            return (eqInX != null) ? eqInX.getHwEQN() : null;
        }
        // Kreuzungstyp
        else if ("KREUZUNGS_TYP".equals(field)) {
            return kreuzungsTyp;
        }
        else if ("VBZ".equals(field)) {
            return (vbz != null) ? vbz.getVbz() : null;
        }

        LOGGER.warn("Field " + field + " not supported from " + this.getClass().getName());
        return null;
    }

    /* Joined die beiden Werte 'geraeteBez' und 'managementBez' des Racks. */
    private String createHWRackName(HWRack rack) {
        return StringTools.join(
                new String[] { rack.getManagementBez(), rack.getGeraeteBez() }, " / ", true);
    }

    /* Laedt die Physiktypen in eine Map - statisch! */
    private void loadPhysiktypen() {
        synchronized (Endstelle4BAJasperDS.class) {
            if (physiktypMap == null) {
                try {
                    //Physiktypen laden
                    List<PhysikTyp> pts = physikService.findPhysikTypen();
                    if (CollectionTools.isNotEmpty(pts)) {
                        physiktypMap = new HashMap<Long, PhysikTyp>();
                        CollectionMapConverter.convert2Map(pts, physiktypMap, "getId", null);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /* Gibt den Namen des Physiktyps mit der ID 'ptId' zurueck. */
    private String getPhysiktyp(Long ptId) {
        loadPhysiktypen();
        PhysikTyp pt = null;
        if (physiktypMap != null) {
            pt = physiktypMap.get(ptId);
        }
        return (pt != null) ? pt.getName() : null;
    }

}
