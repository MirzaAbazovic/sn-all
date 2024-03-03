/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungPositionErledigtmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciVersion;

public class ErledigtmeldungBuilder extends MeldungBuilder<Erledigtmeldung, MeldungPositionErledigtmeldung> {

    protected LocalDate wechseltermin;
    protected String aenderungsIdRef;
    protected String stornoIdRef;

    @Override
    public Erledigtmeldung build() {
        Erledigtmeldung meldung = new Erledigtmeldung();
        enrich(meldung);

        return meldung;
    }

    /**
     * Build ERLM meldung especially for TVS-VA terminverschiebung.
     */
    public ErledigtmeldungTerminverschiebung buildForTv(MeldungsCode... meldungsCodes) {
        ErledigtmeldungTerminverschiebung meldung = new ErledigtmeldungTerminverschiebung();
        enrich(meldung);
        withMeldungsCodes(meldungsCodes);

        return meldung;
    }

    /**
     * Build ERLM meldung especially for Storno terminverschiebung.
     */
    public Erledigtmeldung buildForStorno(RequestTyp requestTyp, MeldungsCode... meldungsCodes) {
        Erledigtmeldung meldung;
        if (requestTyp == RequestTyp.STR_AUFH_ABG || requestTyp == RequestTyp.STR_AUFH_AUF) {
            meldung = new ErledigtmeldungStornoAuf();
        }
        else if (requestTyp == RequestTyp.STR_AEN_ABG || requestTyp == RequestTyp.STR_AEN_AUF) {
            meldung = new ErledigtmeldungStornoAen();
        }
        else {
            throw new IllegalArgumentException(String.format("invalid request type: '%s'", requestTyp));
        }
        enrich(meldung);
        withMeldungsCodes(meldungsCodes);

        return meldung;
    }

    @Override
    protected void enrich(Erledigtmeldung meldung) {
        meldung.setWechseltermin(wechseltermin != null ? wechseltermin : null);
        meldung.setAenderungsIdRef(aenderungsIdRef);
        meldung.setStornoIdRef(stornoIdRef);

        super.enrich(meldung);
    }

    public ErledigtmeldungBuilder withMeldungsCodes(MeldungsCode... meldungsCodes) {
        if (meldungsCodes != null) {
            for (MeldungsCode code : meldungsCodes) {
                addMeldungPosition(new MeldungPositionErledigtmeldungBuilder()
                        .withMeldungsCode(code)
                        .withMeldungsText(code.getStandardText())
                        .build());
            }
        }

        return this;
    }

    public ErledigtmeldungBuilder withWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
        return this;
    }

    public ErledigtmeldungBuilder withAenderungsIdRef(String aenderungsIdRef) {
        this.aenderungsIdRef = aenderungsIdRef;
        return this;
    }

    public ErledigtmeldungBuilder withStornoIdRef(String stornoIdRef) {
        this.stornoIdRef = stornoIdRef;
        return this;
    }

    @Override
    public ErledigtmeldungBuilder withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        this.vorabstimmungsIdRef = vorabstimmungsIdRef;
        return this;
    }

    @Override
    public ErledigtmeldungBuilder withWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        return (ErledigtmeldungBuilder) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public ErledigtmeldungBuilder withAbsender(CarrierCode absender) {
        return (ErledigtmeldungBuilder) super.withAbsender(absender);
    }

    @Override
    public ErledigtmeldungBuilder withWbciVersion(WbciVersion wbciVersion) {
        return (ErledigtmeldungBuilder) super.withWbciVersion(wbciVersion);
    }

    @Override
    public ErledigtmeldungBuilder withProcessedAt(LocalDateTime processedAt) {
        return (ErledigtmeldungBuilder) super.withProcessedAt(processedAt);
    }

    @Override
    public ErledigtmeldungBuilder withIoType(IOType ioType) {
        return (ErledigtmeldungBuilder) super.withIoType(ioType);
    }

    @Override
    public ErledigtmeldungBuilder withMeldungsPositionen(Set<MeldungPositionErledigtmeldung> positionen) {
        return (ErledigtmeldungBuilder) super.withMeldungsPositionen(positionen);
    }

    @Override
    public ErledigtmeldungBuilder addMeldungPosition(MeldungPositionErledigtmeldung position) {
        return (ErledigtmeldungBuilder) super.addMeldungPosition(position);
    }

}
