/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.13
 */
package de.mnet.wbci.marshal.v1.request;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.marshal.v1.request.vorabstimmung.KuendigungMitRNPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.request.vorabstimmung.KuendigungOhneRNPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.request.vorabstimmung.ReineRufnummernportierungGeschaeftsfallMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciRequest;

/**
 * The RequestMarshaller decides according to the {@link de.mnet.wbci.model.WbciGeschaeftsfall} which depper marshaller
 * should be called.
 *
 *
 */
@Component
public class RequestCarrierChangeMarshaller<REQ extends WbciRequest> extends AbstractBaseMarshaller implements
        Function<REQ, RequestCarrierChange> {

    @Autowired
    private KuendigungMitRNPGeschaeftsfallMarshaller kuendigungMitRNPGeschaeftsfallMarshaller;
    @Autowired
    private KuendigungOhneRNPGeschaeftsfallMarshaller kuendigungOhneRNPGeschaeftsfallMarshaller;
    @Autowired
    private ReineRufnummernportierungGeschaeftsfallMarshaller reineRufnummernportierungGeschaeftsfallMarshaller;

    /**
     * marshals any kind of {@link WbciRequest} to an {@link RequestCarrierChange} object.
     *
     * @param input an extended object of the class {@link WbciRequest}
     * @return a JAXB-Root-Element of type {@link RequestCarrierChange}
     */
    @Nullable
    @Override
    public RequestCarrierChange apply(@Nullable REQ input) {
        if (input == null || input.getWbciGeschaeftsfall() == null) {
            return null;
        }
        RequestCarrierChange requestCarrierChange = V1_OBJECT_FACTORY.createRequestCarrierChange();
        switch (input.getWbciGeschaeftsfall().getTyp()) {
            case VA_KUE_MRN:
                requestCarrierChange.setVAKUEMRN(kuendigungMitRNPGeschaeftsfallMarshaller
                        .apply((WbciGeschaeftsfallKueMrn) input.getWbciGeschaeftsfall()));
                break;
            case VA_KUE_ORN:
                requestCarrierChange.setVAKUEORN(kuendigungOhneRNPGeschaeftsfallMarshaller
                        .apply((WbciGeschaeftsfallKueOrn) input.getWbciGeschaeftsfall()));
                break;
            case VA_RRNP:
                requestCarrierChange.setVARRNP(reineRufnummernportierungGeschaeftsfallMarshaller
                        .apply((WbciGeschaeftsfallRrnp) input.getWbciGeschaeftsfall()));
                break;
            default:
                return null;
        }
        return requestCarrierChange;
    }
}
