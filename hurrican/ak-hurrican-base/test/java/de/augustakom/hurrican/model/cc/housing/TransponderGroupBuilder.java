/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 09:00:16
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;

@SuppressWarnings("unused")
public class TransponderGroupBuilder extends EntityBuilder<TransponderGroupBuilder, TransponderGroup> {

    private Long kundeNo = Long.valueOf(999);
    private String transponderDescription = randomString(20);
    private Set<Transponder> transponders = new HashSet<Transponder>();

    public TransponderGroupBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public TransponderGroupBuilder withTransponderDescription(String transponderDescription) {
        this.transponderDescription = transponderDescription;
        return this;
    }

    public TransponderGroupBuilder addTransponder(Transponder transponder) {
        transponders.add(transponder);
        return this;
    }

}


