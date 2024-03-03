package de.mnet.wita.message.builder.meldung.position;

import static de.mnet.wita.message.auftrag.Auftragsposition.*;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.builder.AbstractWitaBuilder;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.meldung.position.ProduktPosition;

/**
 *
 */
public class ProduktPositionBuilder extends AbstractWitaBuilder<ProduktPosition> {

    private AktionsCode aktionsCode;
    private ProduktBezeichner produktBezeichner;
    private Uebertragungsverfahren uebertragungsVerfahren;

    public ProduktPositionBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public ProduktPosition buildValid() {
        if (aktionsCode == null) {
            aktionsCode = AktionsCode.AENDERUNG;
        }
        if (produktBezeichner == null) {
            produktBezeichner = ProduktBezeichner.HVT_2AAL;
        }
        return build();
    }

    public ProduktPosition build() {
        ProduktPosition produktPosition = new ProduktPosition(aktionsCode, produktBezeichner);
        produktPosition.setUebertragungsVerfahren(uebertragungsVerfahren);
        return produktPosition;
    }

    public ProduktPositionBuilder withAktionsCode(AktionsCode aktionsCode) {
        this.aktionsCode = aktionsCode;
        return this;
    }

    public ProduktPositionBuilder withProduktBezeichner(ProduktBezeichner produktBezeichner) {
        this.produktBezeichner = produktBezeichner;
        return this;
    }

    public ProduktPositionBuilder withUebertragungsVerfahren(Uebertragungsverfahren uebertragungsVerfahren) {
        this.uebertragungsVerfahren = uebertragungsVerfahren;
        return this;
    }

}
