package de.mnet.wita.message.builder.auftrag;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;
import de.mnet.wita.message.builder.AbstractWitaBuilder;
import de.mnet.wita.message.common.Uebertragungsverfahren;

/**
 *
 */
public class SchaltungKvzTalBuilder extends AbstractWitaBuilder<SchaltungKvzTal> {

    private Uebertragungsverfahren uebertragungsverfahren;
    private String kvz;
    private String kvzSchaltnummer;

    public SchaltungKvzTalBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public SchaltungKvzTal buildValid() {
        if (kvz == null) {
            kvz = "A001";
        }
        if (kvzSchaltnummer == null) {
            kvzSchaltnummer = "1234";
        }
        if (uebertragungsverfahren == null) {
            uebertragungsverfahren = Uebertragungsverfahren.H01;
        }
        return build();
    }

    public SchaltungKvzTal build() {
        SchaltungKvzTal schaltungKvzTal = new SchaltungKvzTal();
        schaltungKvzTal.setKvz(kvz);
        schaltungKvzTal.setKvzSchaltnummer(kvzSchaltnummer);
        schaltungKvzTal.setUebertragungsverfahren(uebertragungsverfahren);
        return schaltungKvzTal;
    }

}
