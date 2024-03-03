/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;

/**
 *
 */
public abstract class MeldungPositionBuilder<T extends MeldungPosition> implements WbciBuilder<T> {

    protected MeldungsCode meldungsCode;
    protected String meldungsText;

    protected void enrich(T position) {
        position.setMeldungsCode(meldungsCode);
        position.setMeldungsText(meldungsText);
    }

    public MeldungPositionBuilder<T> withMeldungsCode(MeldungsCode meldungsCode) {
        this.meldungsCode = meldungsCode;
        return this;
    }

    public MeldungPositionBuilder<T> withMeldungsText(String meldungsText) {
        this.meldungsText = meldungsText;
        return this;
    }
}
