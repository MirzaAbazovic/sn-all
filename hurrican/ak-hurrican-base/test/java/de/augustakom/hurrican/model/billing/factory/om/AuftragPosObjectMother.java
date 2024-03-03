/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;


import java.util.*;

import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPosBuilder;
import de.augustakom.hurrican.model.billing.Device;

public class AuftragPosObjectMother extends AbstractTaifunObjectMother {

    public static BAuftragPosBuilder createDefault(BAuftrag auftrag) {
        return new BAuftragPosBuilder()
                .withAuftragNoOrig(auftrag.getAuftragNoOrig())
                .withCreateAuftragNo(auftrag.getAuftragNo())
                .withChargeFrom(auftrag.getGueltigVon())
                .withMenge(1L)
                .withPreis(11.98F)
                .withLeistungNoOrig(46783L)
                .withTimestamp(new Date().getTime() / 1000);
    }


    public static List<BAuftragPosBuilder> auftragPositionenSurfAndFon(BAuftrag auftrag, Device device) {
        List<BAuftragPosBuilder> builders = new ArrayList<>();

        // 77776 - S%F Flat 18
        builders.add(createDefault(auftrag).withLeistungNoOrig(77776L));

        if (device != null) {
            // 72662 - HomeBox 7360
            builders.add(createDefault(auftrag).withLeistungNoOrig(72662L).withDeviceNo(device.getDevNo()));
        }

        return builders;
    }


    public static List<BAuftragPosBuilder> auftragPositionenPremiumCall(BAuftrag auftrag) {
        List<BAuftragPosBuilder> builders = new ArrayList<>();

        // 20298 - DSL 6000
        builders.add(createDefault(auftrag).withLeistungNoOrig(20298L));
        // 20230 - ISDN TK
        builders.add(createDefault(auftrag).withLeistungNoOrig(20230L).withParameter("."));

        return builders;
    }

    public static List<BAuftragPosBuilder> auftragPositionenMaxiGlasfaserDsl(BAuftrag auftrag, Device device) {
        List<BAuftragPosBuilder> builders = new ArrayList<>();

        // Ü/Maxi Glasfaser-DSL Telefon-Flat
        builders.add(createDefault(auftrag).withLeistungNoOrig(46783L));

        if (device != null) {
            // 85671 - HomeBox 7360
            builders.add(
                    createDefault(auftrag)
                            .withLeistungNoOrig(85671L)
                            .withDeviceNo(device.getDevNo())
            );
        }

        return builders;
    }

}
