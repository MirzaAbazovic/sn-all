package de.augustakom.hurrican.model.tools;

import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * FtthSurfNFonBuilder
 */
public class FtthSurfNFonBuilder {

    public class FtthSurfNFon {
        // Auftrag
        public Rangierung rangierung;
        public Endstelle endstelle;
        public AuftragTechnik auftragTechnik;
        public AuftragDaten auftragDaten;
        public Auftrag auftrag;

        // Technik
        public HVTStandort standortOnt;
        public HVTStandort standortOlt;
        public HWOnt hwOnt;
        public HWOlt hwOlt;
        public HWBaugruppe oltBaugruppe;
        public HWBaugruppe ontEthBaugruppe;
        public HWBaugruppe ontPotsBaugruppe;
        public HWBaugruppe ontRfBaugruppe;
        public Equipment[] ontEthPorts;
        public Equipment[] ontPotsPorts;
        public Equipment[] ontRfPorts;
    }

    private Long auftragStatusId = AuftragStatus.AUS_TAIFUN_UEBERNOMMEN;

    public void withAuftragStatusId(Long auftragStatusId) {
        this.auftragStatusId = auftragStatusId;
    }

    public FtthSurfNFon build(AbstractHurricanBaseServiceTest test) {
        final StandortFtthBuilder standortFtthBuilder = new StandortFtthBuilder();
        final AuftragFtthSuFBuilder auftragFtthSuFBuilder = new AuftragFtthSuFBuilder()
                .withStatusId(auftragStatusId);

        final StandortFtthBuilder.StandortFtth standortFtth = standortFtthBuilder.prepare(test, null);
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = auftragFtthSuFBuilder.prepare(test, standortFtth);

        auftragFtthSuFBuilder.build(auftragFtthSuF);
        FtthSurfNFon ftthSurfNFon = new FtthSurfNFon();

        ftthSurfNFon.rangierung = auftragFtthSuF.rangierungBuilder.get();
        ftthSurfNFon.endstelle = auftragFtthSuF.endstelleBuilder.get();
        ftthSurfNFon.auftragTechnik = auftragFtthSuF.auftragTechnikBuilder.get();
        ftthSurfNFon.auftragDaten = auftragFtthSuF.auftragDatenBuilder.get();
        ftthSurfNFon.auftrag = auftragFtthSuF.auftragBuilder.get();

        ftthSurfNFon.standortOnt = standortFtth.standortOnt.get();
        ftthSurfNFon.standortOlt = standortFtth.standortOlt.get();
        ftthSurfNFon.hwOnt = standortFtth.hwOntBuilder.get();
        ftthSurfNFon.hwOlt = standortFtth.hwOltBuilder.get();
        ftthSurfNFon.oltBaugruppe = standortFtth.oltBaugruppe.get();
        ftthSurfNFon.ontEthBaugruppe = standortFtth.ontEthBaugruppe.get();
        ftthSurfNFon.ontPotsBaugruppe = standortFtth.ontPotsBaugruppe.get();
        ftthSurfNFon.ontRfBaugruppe = standortFtth.ontRfBaugruppe.get();
        ftthSurfNFon.ontEthPorts = forEachEquipmentBuilder(standortFtth.ontEthPorts);
        ftthSurfNFon.ontPotsPorts = forEachEquipmentBuilder(standortFtth.ontPotsPorts);
        ftthSurfNFon.ontRfPorts = forEachEquipmentBuilder(standortFtth.ontRfPorts);

        return ftthSurfNFon;
    }

    private Equipment[] forEachEquipmentBuilder(final EquipmentBuilder[] in) {
        Equipment[] ports = new Equipment[in.length];
        for (int i = 0; i < in.length; i++) {
            ports[i] = in[i].get();
        }
        return ports;
    }
}
