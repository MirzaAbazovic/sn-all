/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2015
 */
package de.augustakom.hurrican.model.builder.hurrican;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragHousingBuilder;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.AuftragInternBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2Endstelle;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingBuildingBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingFloorBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingParcel;
import de.augustakom.hurrican.model.cc.housing.HousingParcelBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingRoom;
import de.augustakom.hurrican.model.cc.housing.HousingRoomBuilder;

public class AbstractHurricanAuftragBuilder {
    protected final CCAuftragDAO ccAuftragDAO;
    private Long carrierID;
    private HWRack hwRack;

    protected AbstractHurricanAuftragBuilder(CCAuftragDAO ccAuftragDAO) {
        this.ccAuftragDAO = ccAuftragDAO;
    }

    public Pair<Auftrag, AuftragDaten> buildHurricanAuftrag(Long prodId, Long customerNo, Long billingOrderNoOrig, Long standortTypRefId,
            boolean addVpn, boolean addHousing, boolean interneArbeit) {

        AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withKundeNo(customerNo)
                .setPersist(false);
        Auftrag auftrag = ccAuftragDAO.store(auftragBuilder.build());

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(prodId)
                .withAuftragNoOrig(billingOrderNoOrig)
                .setPersist(false)
                .build();
        auftragDaten.setAuftragId(auftrag.getAuftragId());
        ccAuftragDAO.store(auftragDaten);

        AuftragTechnik2Endstelle at2Es = new AuftragTechnik2EndstelleBuilder().setPersist(false).build();
        ccAuftragDAO.store(at2Es);

        IntAccount account = new IntAccountBuilder().withRandomAccount().setPersist(false).build();
        ccAuftragDAO.store(account);

        VerbindungsBezeichnung vbz = new VerbindungsBezeichnungBuilder().withRandomUniqueCode().setPersist(false).build();
        ccAuftragDAO.store(vbz);

        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .setPersist(false).build();
        auftragTechnik.setAuftragId(auftrag.getAuftragId());
        auftragTechnik.setAuftragTechnik2EndstelleId(at2Es.getId());
        auftragTechnik.setIntAccountId(account.getId());
        auftragTechnik.setVbzId(vbz.getId());

        if (addVpn) {
            VPN vpn = new VPNBuilder()
                    .withVpnNr(1111L)
                    .withVpnName("VPN789")
                    .withEinwahl("0123456789")
                    .withVpnType(3000L)
                    .setPersist(false).build();
            ccAuftragDAO.store(vpn);
            auftragTechnik.setVpnId(vpn.getId());
        }
        ccAuftragDAO.store(auftragTechnik);

        HVTGruppe hvtGruppe = new HVTGruppeBuilder().setPersist(false).build();
        ccAuftragDAO.store(hvtGruppe);

        HVTStandort hvtStandort = new HVTStandortBuilder()
                .withStandortTypRefId(standortTypRefId)
                .setPersist(false).build();
        if (carrierID != null) {
            hvtStandort.setCarrierId(carrierID);
        }
        hvtStandort.setHvtGruppeId(hvtGruppe.getId());
        ccAuftragDAO.store(hvtStandort);

        if (interneArbeit) {
            AuftragIntern auftragIntern = new AuftragInternBuilder()
                    .withWorkingTypeRefId(Reference.REF_ID_WORK_TYPE_NEW)
                    .withAuftragBuilder(auftragBuilder)
                    .withHvtStandortId(hvtStandort.getHvtIdStandort())
                    .setPersist(false)
                    .build();

            ccAuftragDAO.store(auftragIntern);
        }

        CCAddress address = new CCAddressBuilder()
                .setPersist(false).build();
        address.setNummer(String.format("%s", auftrag.getAuftragId())); // Auftrags-Id als HNr, erleichtert Fehlersuche!
        address.setHandy("+49 176 123456");
        address.setTelefon("+49 89 555222");
        ccAuftragDAO.store(address);

        Ansprechpartner ansprechpartner = new AnsprechpartnerBuilder()
                .withType(Ansprechpartner.Typ.ENDSTELLE_B)
                .setPersist(false).build();
        ansprechpartner.setAddress(address);
        ansprechpartner.setAuftragId(auftrag.getId());
        ccAuftragDAO.store(ansprechpartner);

        Pair<Rangierung, Rangierung> createdRangierungen = buildHardware(standortTypRefId, hvtStandort);

        if (addHousing) {
            buildHousing(auftragDaten, address);
        }
        else {
            Endstelle endstelle = new EndstelleBuilder()
                    .setPersist(false).build();
            endstelle.setEndstelleGruppeId(at2Es.getId());
            endstelle.setHvtIdStandort(hvtStandort.getId());
            endstelle.setAddressId(address.getId());
            endstelle.setEndstelle(String.format("Endstelle %s - Auftrag %s", endstelle.getEndstelleTyp(), auftrag.getAuftragId()));
            if (createdRangierungen != null && createdRangierungen.getFirst() != null) {
                endstelle.setRangierId(createdRangierungen.getFirst().getId());
            }
            if (createdRangierungen != null && createdRangierungen.getSecond() != null) {
                endstelle.setRangierIdAdditional(createdRangierungen.getSecond().getId());
            }
            ccAuftragDAO.store(endstelle);
        }
        return Pair.create(auftrag, auftragDaten);
    }

    private void buildHousing(AuftragDaten auftragDaten, CCAddress address) {
        HousingBuilding building = new HousingBuildingBuilder()
                .withCCAddress(address)
                .withFloor(new HousingFloorBuilder()
                        .withRoom(new HousingRoomBuilder()
                                .withParcel(new HousingParcelBuilder()
                                        .setPersist(false).build())
                                .setPersist(false).build())
                        .setPersist(false).build())
                .setPersist(false).build();
        ccAuftragDAO.store(building);

        HousingFloor floor = building.getFloors().iterator().next();
        HousingRoom room = floor.getRooms().iterator().next();
        HousingParcel parcel = room.getParcels().iterator().next();

        AuftragHousing housing = new AuftragHousingBuilder()
                .withAuftragId(auftragDaten.getAuftragId())
                .withGueltigBis(DateTools.getHurricanEndDate())
                .withBuilding(building)
                .withFloorId(floor.getId())
                .withRoomId(room.getId())
                .withParcelId(parcel.getId())
                .setPersist(false).build();
        ccAuftragDAO.store(housing);

        //        AuftragHousingKey housingKey = new AuftragHousingKeyBuilder()
        //                .withAuftragId(auftragDaten.getAuftragId())
        //                .setPersist(false).build();
    }

    private Pair<Rangierung, Rangierung> buildHardware(Long standortTypRefId, HVTStandort hvtStandort) {
        HVTTechnik hvtTechnik = new HVTTechnikBuilder().withHersteller("hersteller")
                .setPersist(false).build();
        ccAuftragDAO.store(hvtTechnik);

        HWBaugruppenTypBuilder hwBgTypBuilder = new HWBaugruppenTypBuilder().setPersist(false);
        ccAuftragDAO.store(hwBgTypBuilder.get());

        boolean createEqOut = false;

        Long physikTypId;
        if (HVTStandort.HVT_STANDORT_TYP_FTTH.equals(standortTypRefId)) {
            physikTypId = PhysikTyp.PHYSIKTYP_FTTH;

            if (hwRack ==  null) {
                hwRack = new HWOntBuilder().withSerialNo("serial-no").setPersist(false).build();
                hwRack.setRackTyp(HWRack.RACK_TYPE_ONT);
            }
        }
        else if (HVTStandort.HVT_STANDORT_TYP_FTTB.equals(standortTypRefId)) {
            physikTypId = PhysikTyp.PHYSIKTYP_FTTB_VDSL;

            HWOlt olt = new HWOltBuilder().setPersist(false).build();
            olt.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            olt.setRackTyp(HWRack.RACK_TYPE_OLT);
            olt.setHwProducer(hvtTechnik.getId());
            ccAuftragDAO.store(olt);

            if (hwRack ==  null) {
                hwRack = new HWMduBuilder().withSerialNo("serial-no").setPersist(false).build();
                hwRack.setRackTyp(HWRack.RACK_TYPE_MDU);
            }
            ((HWOltChild)hwRack).setOltRackId(olt.getId());
        }
        else {
            physikTypId = PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI;

            if (hwRack ==  null) {
                hwRack = new HWDslamBuilder().withIpAddress("0.0.0.0").setPersist(false).build();
                hwRack.setRackTyp(HWRack.RACK_TYPE_DSLAM);
            }
            createEqOut = true;
        }
        hwRack.setHwProducer(hvtTechnik.getId());
        hwRack.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        ccAuftragDAO.store(hwRack);

        HWBaugruppe hwBaugruppe = new HWBaugruppeBuilder()
                .withBaugruppenTypBuilder(hwBgTypBuilder)
                .setPersist(false).build();
        hwBaugruppe.setRackId(hwRack.getId());
        ccAuftragDAO.store(hwBaugruppe);

        Equipment eqIn = new EquipmentBuilder()
                .withRangLeiste1("L1").withRangStift1("R1")
                .setPersist(false).build();
        eqIn.setHwBaugruppenId(hwBaugruppe.getId());
        eqIn.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        ccAuftragDAO.store(eqIn);

        Equipment eqOut = null;
        if (createEqOut) {
            eqOut = new EquipmentBuilder()
                    .withRangLeiste1("0204")
                    .withRangLeiste1("01").withRangStift1("56")
                    .withCarrier("DTAG")
                    .withUETV(Uebertragungsverfahren.H13)
                    .setPersist(false).build();
            if (HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ.equals(standortTypRefId)) {
                eqOut.setKvzDoppelader("0056");
                eqOut.setKvzNummer("A023");
            }
            eqOut.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            ccAuftragDAO.store(eqOut);
        }

        Rangierung rangierung = new RangierungBuilder().setPersist(false).build();
        rangierung.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        rangierung.setPhysikTypId(physikTypId);
        rangierung.setEqInId(eqIn.getId());
        rangierung.setEqOutId((eqOut != null) ? eqOut.getId() : null);
        ccAuftragDAO.store(rangierung);

        return Pair.create(rangierung, null);
    }

    @SuppressWarnings("unchecked")
    public <B extends AbstractHurricanAuftragBuilder> B withCarrierId(Long carrierID) {
        this.carrierID = carrierID;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends AbstractHurricanAuftragBuilder> B withHWRack(HWRack hwRack) {
        this.hwRack = hwRack;
        return (B) this;
    }
}
