package de.mnet.wbci.model.builder;


import java.time.*;
import java.util.*;

import de.mnet.wbci.converter.MeldungsCodeConverter;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;

public abstract class WbciRequestBuilder<T extends WbciRequest<GF>, GF extends WbciGeschaeftsfall> implements WbciBuilder<T> {

    protected T wbciRequest;

    public WbciRequestBuilder<T, GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        wbciRequest.setWbciGeschaeftsfall(wbciGeschaeftsfall);
        return this;
    }

    public WbciRequestBuilder<T, GF> withProcessedAt(LocalDateTime processedAt) {
        wbciRequest.setProcessedAt(processedAt != null ? Date.from(processedAt.atZone(ZoneId.systemDefault()).toInstant()) : null);
        return this;
    }

    public WbciRequestBuilder<T, GF> withCreationDate(LocalDateTime creationDate) {
        wbciRequest.setCreationDate(creationDate != null ? Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()) : null);
        return this;
    }

    public WbciRequestBuilder<T, GF> withUpdatedAt(LocalDateTime updatedAt) {
        wbciRequest.setUpdatedAt(updatedAt != null ? Date.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant()) : null);
        return this;
    }

    public WbciRequestBuilder<T, GF> withSendAfter(LocalDateTime sendAfter) {
        wbciRequest.setSendAfter(sendAfter != null ? Date.from(sendAfter.atZone(ZoneId.systemDefault()).toInstant()) : null);
        return this;
    }

    public WbciRequestBuilder<T, GF> withIoType(IOType ioType) {
        wbciRequest.setIoType(ioType);
        return this;
    }

    public WbciRequestBuilder<T, GF> withAnswerDeadline(LocalDate answerDeadline) {
        wbciRequest.setAnswerDeadline(answerDeadline != null ? answerDeadline : null);
        return this;
    }

    public WbciRequestBuilder<T, GF> withIsMnetDeadline(boolean isMnetDedline) {
        wbciRequest.setIsMnetDeadline(isMnetDedline);
        return this;
    }

    public WbciRequestBuilder<T, GF> withRequestStatus(WbciRequestStatus requestStatus) {
        wbciRequest.setRequestStatus(requestStatus);
        return this;
    }

    public WbciRequestBuilder<T, GF> withAbsender(CarrierCode absender) {
        wbciRequest.setAbsender(absender);
        return this;
    }

    public WbciRequestBuilder<T, GF> withLastMeldung(MeldungTyp lastMeldungTyp, LocalDateTime lastMeldungDate,
            MeldungsCode... lastMeldungCodes) {
        wbciRequest.setLastMeldungCodes(MeldungsCodeConverter.meldungcodesToCodeString(lastMeldungCodes));
        wbciRequest.setLastMeldungType(lastMeldungTyp);
        wbciRequest.setLastMeldungDate(lastMeldungDate != null ? Date.from(lastMeldungDate.atZone(ZoneId.systemDefault()).toInstant()) : null);
        return this;
    }
}
