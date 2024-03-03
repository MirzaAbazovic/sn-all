/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.14
 */
package de.augustakom.hurrican.service.elektra.builder;

import javax.validation.constraints.*;

import de.augustakom.hurrican.service.elektra.ElektraCancelOrderResponseDto;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.mnet.elektra.services.CancelOrderResponse;
import de.mnet.elektra.services.UndoPlannedOrderCancellationResponse;

/**
 * Created by glinkjo on 05.12.14.
 */
public class ElektraCancelOrderResponseDtoBuilder extends ElektraResponseDtoBuilder {

    private boolean reclaimPositions;
    
    public ElektraCancelOrderResponseDto build() {
        ElektraCancelOrderResponseDto elektraResponseDto = new ElektraCancelOrderResponseDto();
        elektraResponseDto.setStatus(status);
        elektraResponseDto.setModifications(modifications);
        elektraResponseDto.setReclaimPositions(reclaimPositions);
        return elektraResponseDto;
    }
    
    public ElektraCancelOrderResponseDtoBuilder withReclaimPositions(boolean reclaimPositions) {
        this.reclaimPositions = reclaimPositions;
        return this;
    }

    public ElektraCancelOrderResponseDtoBuilder withModifications(String modifications) {
        return (ElektraCancelOrderResponseDtoBuilder) super.withModifications(modifications);
    }

    public ElektraCancelOrderResponseDtoBuilder withStatus(ElektraResponseDto.ResponseStatus status) {
        return (ElektraCancelOrderResponseDtoBuilder) super.withStatus(status);
    }

    public ElektraCancelOrderResponseDto buildFrom(@NotNull CancelOrderResponse cancelOrderResponse) {
        return withReclaimPositions(cancelOrderResponse.isReclaimPositions())
                .withStatus(getResponseStatusType(cancelOrderResponse.getStatus()))
                .withModifications(cancelOrderResponse.getModifications())
                .build();
    }


    public ElektraCancelOrderResponseDto buildFrom(@NotNull UndoPlannedOrderCancellationResponse undoPlannedOrderCancellationResponse) {
        return withReclaimPositions(undoPlannedOrderCancellationResponse.isReclaimPositions())
                .withStatus(getResponseStatusType(undoPlannedOrderCancellationResponse.getStatus()))
                .withModifications(undoPlannedOrderCancellationResponse.getModifications())
                .build();
    }

}
