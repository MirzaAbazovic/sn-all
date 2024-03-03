package de.mnet.wita.message.builder.meldung.position;

import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.AbstractWitaBuilder;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.Positionsattribute;

/**
 *
 */
public class MeldungsPositionBuilder extends AbstractWitaBuilder<MeldungsPosition> {

    private String meldungsCode;
    private String meldungsText;
    private Positionsattribute positionsAttribute;

    public MeldungsPositionBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public MeldungsPosition buildValid() {
        switch (getWitaVersion()) {
            case V1: {
                if (meldungsText == null) {
                    meldungsText = "123";
                }
                break;
            }
        }
        if (meldungsCode == null) {
            meldungsCode = AbbmMeldungsCode.AUFTRAG_AUSGEFUEHRT_TV_NOT_POSSIBLE.meldungsCode;
        }
        return build();
    }

    public MeldungsPosition build() {
        MeldungsPosition meldungsPosition = new MeldungsPosition();
        meldungsPosition.setMeldungsCode(meldungsCode);
        meldungsPosition.setMeldungsText(meldungsText);
        meldungsPosition.setPositionsattribute(positionsAttribute);
        return meldungsPosition;
    }

    public MeldungsPositionBuilder withMeldungsText(String meldungsText) {
        this.meldungsText = meldungsText;
        return this;
    }

    public MeldungsPositionBuilder withMeldungsCode(String meldungsCode) {
        this.meldungsCode = meldungsCode;
        return this;
    }

    public MeldungsPositionBuilder withPositionsattribute(Positionsattribute positionsAttribute) {
        this.positionsAttribute = positionsAttribute;
        return this;
    }

}
