/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UebernahmeLeitungType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Leitung;

/**
 *
 */
@Component
public class UebernahmeLeitungMarshaller extends AbstractBaseMarshaller implements Function<Leitung, UebernahmeLeitungType> {
    @Nullable
    @Override
    public UebernahmeLeitungType apply(@Nullable Leitung leitung) {
        UebernahmeLeitungType leitungType = V1_OBJECT_FACTORY.createUebernahmeLeitungType();

        leitungType.setLineID(leitung.getLineId());
        leitungType.setVertragsnummer(leitung.getVertragsnummer());

        return leitungType;
    }
}
