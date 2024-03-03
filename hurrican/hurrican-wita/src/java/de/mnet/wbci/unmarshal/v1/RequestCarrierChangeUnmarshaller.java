/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1;

import java.time.*;
import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.unmarshal.v1.request.vorabstimmung.KuendigungMitRNPGeschaeftsfallUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.vorabstimmung.KuendigungOhneRNPGeschaeftsfallUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.vorabstimmung.ReineRufnummernPortierungGeschaeftsfallUnmarshaller;

@Component
public class RequestCarrierChangeUnmarshaller implements Function<RequestCarrierChange, WbciRequest> {

    @Autowired
    private KuendigungMitRNPGeschaeftsfallUnmarshaller kueMrnUnmarshaller;
    @Autowired
    private KuendigungOhneRNPGeschaeftsfallUnmarshaller kueOrnUnmarshaller;
    @Autowired
    private ReineRufnummernPortierungGeschaeftsfallUnmarshaller rrnpUnmarshaller;

    @Nullable
    @Override
    public WbciRequest apply(@Nullable RequestCarrierChange input) {
        if (input == null) {
            return null;
        }

        WbciGeschaeftsfall wbciGeschaeftsfall = null;
        if (input.getVAKUEMRN() != null) {
            wbciGeschaeftsfall = kueMrnUnmarshaller.apply(input.getVAKUEMRN());
        }
        else if (input.getVAKUEORN() != null) {
            wbciGeschaeftsfall = kueOrnUnmarshaller.apply(input.getVAKUEORN());
        }
        else if (input.getVARRNP() != null) {
            wbciGeschaeftsfall = rrnpUnmarshaller.apply(input.getVARRNP());
        }

        LocalDate vaKundenwunschtermin = (wbciGeschaeftsfall == null) ? null : wbciGeschaeftsfall.getKundenwunschtermin();
        return new VorabstimmungsAnfrageBuilder<>()
                .withVaKundenwunschtermin(vaKundenwunschtermin)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .build();
    }

}
