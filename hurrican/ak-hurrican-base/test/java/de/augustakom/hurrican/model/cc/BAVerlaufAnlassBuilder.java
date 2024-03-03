/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2015
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;

/**
 * Created by glinkjo on 11.02.2015.
 */
public class BAVerlaufAnlassBuilder extends EntityBuilder<BAVerlaufAnlassBuilder, BAVerlaufAnlass> {

    private String name = randomString(15);
    private Long baVerlGruppe = 0L;
    private Boolean configurable = Boolean.FALSE;
    private Boolean auftragsart = Boolean.FALSE;
    private Integer positionNo = null;
    private Boolean akt = Boolean.FALSE;
    private Long cpsServiceOrderType = null;
    private FfmTyp ffmTyp = FfmTyp.NEU;

    public BAVerlaufAnlassBuilder withFfmTyp(FfmTyp ffmTyp) {
        this.ffmTyp = ffmTyp;
        return this;
    }

    public BAVerlaufAnlassBuilder withName(String name) {
        this.name = name;
        return this;
    }

}
