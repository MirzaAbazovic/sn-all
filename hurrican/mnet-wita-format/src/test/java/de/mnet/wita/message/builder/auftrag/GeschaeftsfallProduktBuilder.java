package de.mnet.wita.message.builder.auftrag;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.builder.AbstractWitaBuilder;

/**
 *
 */
public class GeschaeftsfallProduktBuilder extends AbstractWitaBuilder<GeschaeftsfallProdukt> {

    private String vorabstimmungsId;
    private GeschaeftsfallTyp geschaeftsfallTyp;
    private Vormieter vormieter;

    public GeschaeftsfallProduktBuilder() {
        super(WitaCdmVersion.getDefault());
    }

    public GeschaeftsfallProduktBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public GeschaeftsfallProdukt buildValid() {
        if (getWitaVersion() == WitaCdmVersion.V1 && vorabstimmungsId == null) {
            if (KUENDIGUNG_KUNDE.equals(geschaeftsfallTyp) || PROVIDERWECHSEL.equals(geschaeftsfallTyp)
                    || BEREITSTELLUNG.equals(geschaeftsfallTyp) || VERBUNDLEISTUNG.equals(geschaeftsfallTyp)) {
                vorabstimmungsId = "DEU.MNET.V123456789";
            }
        }
        return build();
    }

    public GeschaeftsfallProdukt build() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = new GeschaeftsfallProdukt();
        geschaeftsfallProdukt.setVorabstimmungsId(vorabstimmungsId);
        geschaeftsfallProdukt.setVormieter(vormieter);
        return geschaeftsfallProdukt;
    }

    public GeschaeftsfallProduktBuilder withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    public GeschaeftsfallProduktBuilder withGeschaeftsfallTyp(GeschaeftsfallTyp geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        return this;
    }

}
