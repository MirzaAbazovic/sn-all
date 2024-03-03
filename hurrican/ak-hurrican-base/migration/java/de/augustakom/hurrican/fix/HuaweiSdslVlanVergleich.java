/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2015 14:32
 */
package de.augustakom.hurrican.fix;

import java.util.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
public class HuaweiSdslVlanVergleich extends AbstractHurricanBaseServiceTest {

    @Autowired
    HardwareDAO hwDAO;
    @Autowired
    HWService hwService;
    @Autowired
    CCAuftragService auftragService;
    @Autowired
    EQCrossConnectionService crossConnectionService;
    @Autowired
    RangierungsService rangierungsService;



    List<HWDslam> findAllHuaweiDslams() {
       return hwDAO.findByProperty(HWDslam.class, "hwProducer", 2L);
    }

    List<AuftragDaten> findActiveAuftraegeByDslam(HWDslam dslam) throws Exception  {
        final List<HWBaugruppe> baugruppen = hwService.findBaugruppen4Rack(dslam.getId());
        final List<AuftragDaten> auftragDaten = Lists.newLinkedList();
        for (final HWBaugruppe baugruppe : baugruppen) {
            if(baugruppe.getHwBaugruppenTyp().getHwSchnittstelleName().equals(HWBaugruppenTyp.HW_SCHNITTSTELLE_SDSL))
             auftragDaten.addAll(auftragService.findAktiveAuftragDatenByBaugruppe(baugruppe.getId()));
        }
        return auftragDaten;
    }

    List<EQCrossConnection> findCrossConnectionsForAuftrag(long auftragId) throws Exception{
        final Rangierung[] rangierungen = rangierungsService.findRangierungenTx(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        final Equipment equipment = rangierungsService.findEquipment(rangierungen[0].getEqInId());
        return crossConnectionService.findEQCrossConnections(equipment.getId(), new Date());
    }

    List<EQCrossConnection> calculateNewCrossConnectionsForAuftrag(long auftragId)  {
        return Lists.newArrayList();
    }
}

