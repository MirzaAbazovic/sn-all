/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.common.model.EntityBuilder;

/**
 * Created by glinkjo on 18.02.2015.
 */
@SuppressWarnings("unused")
public class VerlaufUniversalViewBuilder extends EntityBuilder<VerlaufUniversalViewBuilder, VerlaufUniversalView> {

    private Long verlaufId;
    private boolean erledigt = false;
    private boolean guiFinished = false;

    public VerlaufUniversalViewBuilder() {
        setPersist(false);
    }

    public VerlaufUniversalViewBuilder withVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
        return this;
    }

}
