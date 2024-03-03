package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class VorabstimmungsAnfrageBuilder<GF extends WbciGeschaeftsfall> extends WbciRequestBuilder<VorabstimmungsAnfrage<GF>, GF> {

    protected LocalDate vaKundenwunschtermin;

    public VorabstimmungsAnfrageBuilder() {
        wbciRequest = new VorabstimmungsAnfrage<>();
    }

    @Override
    public VorabstimmungsAnfrage<GF> build() {
        wbciRequest.setVaKundenwunschtermin(vaKundenwunschtermin != null ? vaKundenwunschtermin : null);
        return wbciRequest;
    }

    public VorabstimmungsAnfrageBuilder<GF> withVaKundenwunschtermin(LocalDate vaKundenwunschtermin) {
        this.vaKundenwunschtermin = vaKundenwunschtermin;
        return this;
    }

}
