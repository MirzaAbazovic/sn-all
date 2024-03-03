/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 10:11:45
 */
package de.mnet.hurrican.acceptance.builder;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlan.CVlanProtocoll;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;

/**
 * Builder-Klasse, um die notwendigen EKP Daten zu generieren.
 */
@Component
@Scope("prototype")
public class EkpDataBuilder {

    private EkpData ekpData;

    private List<CVlan> cvlans;

    @Autowired
    private EkpFrameContractService ekpFrameContractService;

    @Autowired
    private PhysikService physikService;

    public EkpDataBuilder withCvlans(List<CVlan> cvlans) {
        this.cvlans = cvlans;
        return this;
    }

    public synchronized EkpData getEkpData() throws Exception {
        if (ekpData == null) {
            ekpData = build();
        }
        return ekpData;
    }

    public EkpData build() throws Exception {
        VerbindungsBezeichnung vbz = physikService.createVerbindungsBezeichnung("F", "E");
        vbz = physikService.saveVerbindungsBezeichnung(vbz);

        A10Nsp a10Nsp = new A10NspBuilder().setPersist(false).build();
        a10Nsp = ekpFrameContractService.saveA10Nsp(a10Nsp);
        A10NspPort a10NspPort = ekpFrameContractService.createA10NspPort(a10Nsp);

        EkpFrameContractBuilder ekpFrameContractBuilder = new EkpFrameContractBuilder()
                .addA10NspPort(a10NspPort, Boolean.TRUE)
                .setPersist(false);
        if (cvlans == null) {
            // @formatter:off
            ekpFrameContractBuilder
                .addCVlan(new CVlanBuilder().withTyp(CvlanServiceTyp.VOIP).withProtocoll(CVlanProtocoll.PPPoE).withValue(7).withPbitLimit(100).setPersist(false).build())
                .addCVlan(new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withProtocoll(CVlanProtocoll.PPPoE).withValue(7).setPersist(false).build())
                .addCVlan(new CVlanBuilder().withTyp(CvlanServiceTyp.MC).withProtocoll(CVlanProtocoll.PPPoE).withValue(8).setPersist(false).build())
                .addCVlan(new CVlanBuilder().withTyp(CvlanServiceTyp.VOD).withProtocoll(CVlanProtocoll.IPoE).withValue(8).setPersist(false).build());
            // @formatter:on
        }
        else {
            ekpFrameContractBuilder.withCVlans(cvlans);
        }

        EkpFrameContract ekpFrameContract = ekpFrameContractBuilder.build();
        ekpFrameContract = ekpFrameContractService.saveEkpFrameContract(ekpFrameContract);

        return new EkpData(ekpFrameContract);
    }

    public static class EkpData {
        public EkpFrameContract ekpFrameContract;

        public EkpData(EkpFrameContract ekpFrameContract) {
            this.ekpFrameContract = ekpFrameContract;
        }
    }

}


