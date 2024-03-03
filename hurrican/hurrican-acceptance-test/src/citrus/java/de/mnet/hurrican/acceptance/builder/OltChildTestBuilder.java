/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2014
 */
package de.mnet.hurrican.acceptance.builder;

import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;

/**
 *
 */
@Component
@Scope("prototype")
public class OltChildTestBuilder {

    @Autowired
    protected Provider<HWMduBuilder> mduBuilderProvider;
    @Autowired
    protected Provider<HWOntBuilder> ontBuilderProvider;
    @Autowired
    protected Provider<HWDpoBuilder> dpoBuilderProvider;
    @Autowired
    protected Provider<HWOltBuilder> oltBuilderProvider;
    @Autowired
    protected HWService hwService;

    public HWOnt buildOnt(HVTStandort hvtStandort) throws StoreException, ValidationException {
        HWOlt olt = buildOlt(hvtStandort);

        HWOnt ont = ontBuilderProvider.get().build();
        ont.setHwProducer(HVTTechnik.HUAWEI);
        ont.setHvtIdStandort(hvtStandort.getId());
        ont.setOltRackId(olt.getId());
        ont = hwService.saveHWRack(ont);
        return ont;
    }

    public HWMdu buildMdu(HVTStandort hvtStandort) throws StoreException, ValidationException {
        HWOlt olt = buildOlt(hvtStandort);

        HWMdu mdu = mduBuilderProvider.get().build();
        mdu.setHvtIdStandort(hvtStandort.getId());
        mdu.setHwProducer(HVTTechnik.HUAWEI);
        mdu.setOltRackId(olt.getId());
        mdu = hwService.saveHWRack(mdu);
        return mdu;
    }

    public HWDpo buildDpo(HVTStandort hvtStandort) throws StoreException, ValidationException {
        HWOlt olt = buildOlt(hvtStandort);

        HWDpo dpo = dpoBuilderProvider.get().build();
        dpo.setHvtIdStandort(hvtStandort.getId());
        dpo.setHwProducer(HVTTechnik.HUAWEI);
        dpo.setOltRackId(olt.getId());
        dpo = hwService.saveHWRack(dpo);
        return dpo;
    }

    private HWOlt buildOlt(HVTStandort hvtStandort) throws StoreException, ValidationException {
        HWOltBuilder oltBuilder = oltBuilderProvider.get();
        HWOlt olt = oltBuilder.build();
        olt.setHwProducer(HVTTechnik.HUAWEI);
        olt.setHvtIdStandort(hvtStandort.getId());
        olt = hwService.saveHWRack(olt);
        return olt;
    }

}
