/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.mnet.wbci.unmarshal.v1;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.unmarshal.v1.meldung.AbbruchmeldungTechnRessourceUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.AbbruchmeldungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.ErledigtmeldungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.RueckmeldungVorabstimmungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.meldung.UebernahmeRessourceMeldungUnmarshaller;

/**
 *
 */
@Component
public class UpdateCarrierChangeUnmarshaller<M extends Meldung<POS>, POS extends MeldungPosition> implements Function<UpdateCarrierChange, M> {

    @Autowired
    private RueckmeldungVorabstimmungUnmarshaller rueckmeldungVorabstimmungUnmarshaller;
    @Autowired
    private AbbruchmeldungUnmarshaller abbruchmeldungUnmarshaller;
    @Autowired
    private AbbruchmeldungTechnRessourceUnmarshaller abbruchmeldungTechnRessourceUnmarshaller;
    @Autowired
    private UebernahmeRessourceMeldungUnmarshaller uebernahmeRessourceMeldungUnmarshaller;
    @Autowired
    private ErledigtmeldungUnmarshaller erledigtmeldungUnmarshaller;

    @Nullable
    @Override
    public M apply(@Nullable UpdateCarrierChange input) {
        if (input != null) {
            if (input.getRUEMVA() != null) {
                return (M) rueckmeldungVorabstimmungUnmarshaller.apply(input.getRUEMVA());
            }
            else if (input.getABBM() != null) {
                return (M) abbruchmeldungUnmarshaller.apply(input.getABBM());
            }
            else if (input.getABBMTR() != null) {
                return (M) abbruchmeldungTechnRessourceUnmarshaller.apply(input.getABBMTR());
            }
            else if (input.getAKMTR() != null) {
                return (M) uebernahmeRessourceMeldungUnmarshaller.apply(input.getAKMTR());
            }
            else if (input.getERLM() != null) {
                return (M) erledigtmeldungUnmarshaller.apply(input.getERLM());
            }
        }
        return null;
    }
}
