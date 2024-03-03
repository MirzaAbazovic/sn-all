package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;

public class VorabstimmungsAnfrageTestBuilder<GF extends WbciGeschaeftsfall> extends VorabstimmungsAnfrageBuilder<GF> implements
        WbciTestBuilder<VorabstimmungsAnfrage<GF>> {

    private boolean withoutProcessedAt;

    @Override
    public VorabstimmungsAnfrage<GF> buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        WbciRequestTestBuilder.enrich(wbciRequest, wbciCdmVersion, gfTyp);
        if (withoutProcessedAt) {
            wbciRequest.setProcessedAt(null);
        }
        if (this.vaKundenwunschtermin == null && wbciRequest.getWbciGeschaeftsfall() != null) {
            withVaKundenwunschtermin(wbciRequest.getWbciGeschaeftsfall().getKundenwunschtermin());
        }
        return build();
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withProcessedAt(LocalDateTime processedAt) {
        super.withProcessedAt(processedAt);
        return this;
    }

    public VorabstimmungsAnfrageTestBuilder<GF> withoutProcessedAt() {
        this.withoutProcessedAt = true;
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withCreationDate(LocalDateTime creationDate) {
        super.withCreationDate(creationDate);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withUpdatedAt(LocalDateTime updatedAt) {
        super.withUpdatedAt(updatedAt);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withSendAfter(LocalDateTime sendAfter) {
        super.withSendAfter(sendAfter);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withIoType(IOType ioType) {
        super.withIoType(ioType);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withAnswerDeadline(LocalDate answerDeadline) {
        super.withAnswerDeadline(answerDeadline);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withIsMnetDeadline(boolean isMnetDedline) {
        super.withIsMnetDeadline(isMnetDedline);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withRequestStatus(WbciRequestStatus requestStatus) {
        super.withRequestStatus(requestStatus);
        return this;
    }

    @Override
    public VorabstimmungsAnfrageTestBuilder<GF> withVaKundenwunschtermin(LocalDate vaKundenwunschtermin) {
        super.withVaKundenwunschtermin(vaKundenwunschtermin);
        return this;
    }

}
