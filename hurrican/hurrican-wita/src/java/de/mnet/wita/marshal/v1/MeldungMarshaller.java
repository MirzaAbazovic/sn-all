/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import com.google.common.base.Function;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypAbstractType;
import de.mnet.wita.message.meldung.OutgoingMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public abstract class MeldungMarshaller<T extends OutgoingMeldung, U extends MeldungstypAbstractType>
        extends AbstractBaseMarshaller
        implements Function<T, U> {

    @Override
    public U apply(T input) {
        return createMeldungstyp(input);
    }

    MeldungspositionType createMeldungsPosition(MeldungsPosition meldungsPosition) {
        MeldungspositionType meldungspositionType = new MeldungspositionType();
        meldungspositionType.setMeldungscode(meldungsPosition.getMeldungsCode());
        meldungspositionType.setMeldungstext(meldungsPosition.getMeldungsText());
        return meldungspositionType;
    }

    protected abstract U createMeldungstyp(T input);
}
