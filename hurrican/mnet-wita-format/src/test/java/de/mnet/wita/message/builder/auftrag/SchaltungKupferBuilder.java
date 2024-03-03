package de.mnet.wita.message.builder.auftrag;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.builder.AbstractWitaBuilder;
import de.mnet.wita.message.common.Uebertragungsverfahren;

/**
 *
 */
public class SchaltungKupferBuilder extends AbstractWitaBuilder<SchaltungKupfer> {

    private Uebertragungsverfahren uebertragungsverfahren;
    private String uevt;
    private String evs;
    private String doppelader;

    public SchaltungKupferBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public SchaltungKupferBuilder() {
        this(WitaCdmVersion.getDefault());
    }

    public SchaltungKupfer buildValid() {
        if (doppelader == null) {
            doppelader = "1";
        }
        if (evs == null) {
            evs = "1";
        }
        if (uevt == null) {
            uevt = "ab";
        }
        if (uebertragungsverfahren == null) {
            uebertragungsverfahren = Uebertragungsverfahren.H01;
        }
        return build();
    }

    public SchaltungKupfer build() {
        SchaltungKupfer schaltungKupfer = new SchaltungKupfer();
        schaltungKupfer.setDoppelader(doppelader);
        schaltungKupfer.setEVS(evs);
        schaltungKupfer.setUEVT(uevt);
        schaltungKupfer.setUebertragungsverfahren(uebertragungsverfahren);
        return schaltungKupfer;
    }

    public SchaltungKupferBuilder withDoppelader(String doppelader) {
        this.doppelader = doppelader;
        return this;
    }

    public SchaltungKupferBuilder withEVS(String evs) {
        this.evs = evs;
        return this;
    }

    public SchaltungKupferBuilder withUevt(String uevt) {
        this.uevt = uevt;
        return this;
    }

    public SchaltungKupferBuilder withUebertragungsverfahren(Uebertragungsverfahren uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
        return this;
    }

}
