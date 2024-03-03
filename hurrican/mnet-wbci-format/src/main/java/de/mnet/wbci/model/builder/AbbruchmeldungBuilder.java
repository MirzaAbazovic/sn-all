/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;
import java.util.*;
import javax.validation.constraints.*;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
public class AbbruchmeldungBuilder extends MeldungBuilder<Abbruchmeldung, MeldungPositionAbbruchmeldung> {

    protected String begruendung;
    protected LocalDate wechseltermin;
    protected String aenderungsIdRef;
    protected String stornoIdRef;

    @Override
    public Abbruchmeldung build() {
        Abbruchmeldung meldung = new Abbruchmeldung();
        enrich(meldung);

        return meldung;
    }

    /**
     * Build ABBM meldung especially for TVS-VA terminverschiebung.
     */
    public AbbruchmeldungTerminverschiebung buildForTv() {
        AbbruchmeldungTerminverschiebung meldung = new AbbruchmeldungTerminverschiebung();
        enrich(meldung);

        return meldung;
    }

    /**
     * Build ABBM meldung especially for STR and provided request type.
     */
    public Abbruchmeldung buildForStorno(RequestTyp requestTyp) {
        Abbruchmeldung meldung;
        if (requestTyp == RequestTyp.STR_AUFH_ABG || requestTyp == RequestTyp.STR_AUFH_AUF) {
            meldung = new AbbruchmeldungStornoAuf();
        }
        else if (requestTyp == RequestTyp.STR_AEN_ABG || requestTyp == RequestTyp.STR_AEN_AUF) {
            meldung = new AbbruchmeldungStornoAen();
        }
        else {
            throw new IllegalArgumentException(String.format("invalid request type: '%s'", requestTyp));
        }
        enrich(meldung);
        return meldung;
    }

    /**
     * Build an outgoing ABBM meldung especially for VA and provided geschaeftsfall, reason and meldungs codes.
     */
    public Abbruchmeldung buildOutgoingForVa(@NotNull WbciGeschaeftsfall wbciGeschaeftsfall,
            String reason,
            @NotNull MeldungsCode... meldungsCodes) {

        withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withIoType(IOType.OUT)
                .withAbsender(CarrierCode.MNET)
                .withBegruendung(reason)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall);

        for (MeldungsCode code : meldungsCodes) {
            addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder()
                    .withMeldungsCode(code)
                    .withMeldungsText(code.getStandardText())
                    .build());
        }

        return build();
    }

    @Override
    protected void enrich(Abbruchmeldung meldung) {
        meldung.setBegruendung(begruendung);
        meldung.setWechseltermin(wechseltermin != null ? wechseltermin : null);
        meldung.setAenderungsIdRef(aenderungsIdRef);
        meldung.setStornoIdRef(stornoIdRef);

        super.enrich(meldung);
    }

    /**
     * Sets the {@code begrundung} in the Abbruchmeldung. <br /> If the {@code begrundung} contains text (i.e. is not an
     * empty string or null) then the {@link MeldungsCode#SONST} is automatically added to the Abbruchmeldung, if it
     * doesn't already contain one of the following Meldungscodes: <ul> <li>{@link MeldungsCode#SONST}</li> <li>{@link
     * MeldungsCode#TV_ABG}</li> <li>{@link MeldungsCode#STORNO_ABG}</li> <li>{@link MeldungsCode#BVID}</li> </ul> <br
     * /> If the {@code begrundung} is empty or null then the {@link MeldungsCode#SONST} is automatically removed from
     * the Abbruchmeldung, if it has been added. <br /> Be careful when using this when unmarshalling an ABBM Message,
     * since the MeldungPositions are adapted according to the {@code begrundung} - which may result in MeldungPositions
     * being automatically added or removed and in turn an ABBM that no represents the message received from the partner
     * carrier.
     *
     * @param begruendung the begruendung to set
     */
    public AbbruchmeldungBuilder withBegruendung(String begruendung) {
        return this.withBegruendung(begruendung, true);
    }

    /**
     * Sets the {@code begrundung} in the Abbruchmeldung. <br /> If {@code automaticallyAdaptMeldungsCodes} is
     * <b>true</b> and the {@code begrundung} contains text (i.e. is not an empty string or null) then the {@link
     * MeldungsCode#SONST} is automatically added to the Abbruchmeldung, if it doesn't already contain one of the
     * following Meldungscodes: <ul> <li>{@link MeldungsCode#SONST}</li> <li>{@link MeldungsCode#TV_ABG}</li> <li>{@link
     * MeldungsCode#STORNO_ABG}</li> <li>{@link MeldungsCode#BVID}</li> </ul> <br /> If {@code
     * automaticallyAdaptMeldungsCodes} is <b>true</b> and the {@code begrundung} is empty or null then the {@link
     * MeldungsCode#SONST} is automatically removed from the Abbruchmeldung, if it has been added.
     *
     * @param begruendung                     the begruendung to set
     * @param automaticallyAdaptMeldungsCodes when set to true the MeldungPosition is automatically adapted according to
     *                                        the {@code begruendung}. Otherwise only the {@code begruendung} is
     *                                        updated
     */
    public AbbruchmeldungBuilder withBegruendung(String begruendung, boolean automaticallyAdaptMeldungsCodes) {
        this.begruendung = begruendung;
        if (automaticallyAdaptMeldungsCodes) {
            if (begruendung != null) {
                if (!(isMeldungsCodeIncluded(MeldungsCode.TV_ABG)
                        || isMeldungsCodeIncluded(MeldungsCode.STORNO_ABG)
                        || isMeldungsCodeIncluded(MeldungsCode.BVID)
                        || isMeldungsCodeIncluded(MeldungsCode.SONST))) {
                    addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder()
                            .withMeldungsCode(MeldungsCode.SONST)
                            .withMeldungsText(MeldungsCode.SONST.getStandardText())
                            .build());
                }
            }
            else {
                for (MeldungPositionAbbruchmeldung pos : meldungsPositionen) {
                    if (pos.getMeldungsCode().equals(MeldungsCode.SONST)) {
                        meldungsPositionen.remove(pos);
                    }
                }
            }
        }
        return this;
    }

    public AbbruchmeldungBuilder withWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
        return this;
    }

    public AbbruchmeldungBuilder withAenderungsIdRef(String aenderungsIdRef) {
        this.aenderungsIdRef = aenderungsIdRef;
        return this;
    }

    public AbbruchmeldungBuilder withStornoIdRef(String stornoIdRef) {
        this.stornoIdRef = stornoIdRef;
        return this;
    }

    @Override
    public AbbruchmeldungBuilder withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        return (AbbruchmeldungBuilder) super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
    }

    @Override
    public AbbruchmeldungBuilder withWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        return (AbbruchmeldungBuilder) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public AbbruchmeldungBuilder withAbsender(CarrierCode absender) {
        return (AbbruchmeldungBuilder) super.withAbsender(absender);
    }

    @Override
    public AbbruchmeldungBuilder withWbciVersion(WbciVersion wbciVersion) {
        return (AbbruchmeldungBuilder) super.withWbciVersion(wbciVersion);
    }

    @Override
    public AbbruchmeldungBuilder withProcessedAt(LocalDateTime processedAt) {
        return (AbbruchmeldungBuilder) super.withProcessedAt(processedAt);
    }

    @Override
    public AbbruchmeldungBuilder withIoType(IOType ioType) {
        return (AbbruchmeldungBuilder) super.withIoType(ioType);
    }

    @Override
    public AbbruchmeldungBuilder withMeldungsPositionen(Set<MeldungPositionAbbruchmeldung> positionen) {
        return (AbbruchmeldungBuilder) super.withMeldungsPositionen(positionen);
    }

    @Override
    public AbbruchmeldungBuilder addMeldungPosition(MeldungPositionAbbruchmeldung position) {
        return (AbbruchmeldungBuilder) super.addMeldungPosition(position);
    }

    public AbbruchmeldungBuilder addMeldungPositionen(Set<MeldungPositionAbbruchmeldung> positionen) {
        for (MeldungPositionAbbruchmeldung mpa : positionen) {
            super.addMeldungPosition(mpa);
        }
        return this;
    }

    private boolean isMeldungsCodeIncluded(MeldungsCode code) {
        if (code == null || meldungsPositionen == null) {
            return true;
        }
        for (MeldungPosition pos : meldungsPositionen) {
            if (code.equals(pos.getMeldungsCode())) {
                return true;
            }
        }
        return false;
    }
}
