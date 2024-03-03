/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.2014
 */
package de.augustakom.hurrican.service.elektra.builder;

import static de.augustakom.hurrican.service.elektra.ElektraResponseDto.*;

import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.mnet.elektra.services.DefaultResponseType;
import de.mnet.elektra.services.ResponseStatusType;

/**
 *
 */
public class ElektraResponseDtoBuilder {

    ResponseStatus status;
    String modifications;

    public ElektraResponseDto build() {
        ElektraResponseDto elektraResponseDto = new ElektraResponseDto();
        elektraResponseDto.setStatus(status);
        elektraResponseDto.setModifications(modifications);
        return elektraResponseDto;
    }

    public ElektraResponseDto buildFrom(DefaultResponseType defaultResponseType) {
        return withStatus(getResponseStatusType(defaultResponseType.getStatus()))
                .withModifications(defaultResponseType.getModifications())
                .build();
    }

    public ElektraResponseDtoBuilder withStatus(ResponseStatus status) {
        this.status = status;
        return this;
    }

    public ElektraResponseDtoBuilder withModifications(String modifications) {
        this.modifications = modifications;
        return this;
    }

    protected ResponseStatus getResponseStatusType(ResponseStatusType responseStatusType) {
        switch (responseStatusType) {
            case OK:
                return ResponseStatus.OK;
            case ERROR:
                return ResponseStatus.ERROR;
            default:
                throw new IllegalStateException(String.format("Unerwarteter ResponseStatusType '%s'", responseStatusType));
        }
    }

}
