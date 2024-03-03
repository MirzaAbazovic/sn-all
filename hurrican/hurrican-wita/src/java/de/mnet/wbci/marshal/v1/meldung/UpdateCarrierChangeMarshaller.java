/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;

/**
 *
 */
@Component
public class UpdateCarrierChangeMarshaller<M extends Meldung<?>> extends AbstractBaseMarshaller implements
        Function<M, UpdateCarrierChange> {

    @Autowired
    private UebernahmeRessourceMeldungMarshaller uebernahmeMeldungMarshaller;
    @Autowired
    private AbbruchmeldungVaMarshaller abbruchmeldungMarshaller;
    @Autowired
    private AbbruchmeldungStornoAufMarshaller abbruchmeldungStornoAufMarshaller;
    @Autowired
    private AbbruchmeldungStornoAenMarshaller abbruchmeldungStornoAenMarshaller;
    @Autowired
    private AbbruchmeldungTvMarshaller abbruchmeldungTvMarshaller;
    @Autowired
    private AbbruchmeldungTechnRessourceMarshaller abbruchmeldungTechnRessourceMarshaller;
    @Autowired
    private RueckmeldungVorabstimmungMarshaller rueckmeldungVorabstimmungMarshaller;
    @Autowired
    private ErledigtmeldungVaMarshaller erledigtmeldungMarshaller;
    @Autowired
    private ErledigtmeldungStornoAufMarshaller erledigtmeldungStornoAufMarshaller;
    @Autowired
    private ErledigtmeldungStornoAenMarshaller erledigtmeldungStornoAenMarshaller;
    @Autowired
    private ErledigtmeldungTvMarshaller erledigtmeldungTvMarshaller;


    @Nullable
    @Override
    public UpdateCarrierChange apply(@Nullable M meldung) {
        UpdateCarrierChange updateCarrierChange = V1_OBJECT_FACTORY.createUpdateCarrierChange();

        if (meldung instanceof UebernahmeRessourceMeldung) {
            updateCarrierChange.setAKMTR(uebernahmeMeldungMarshaller.apply((UebernahmeRessourceMeldung) meldung));
        }
        else if (meldung instanceof AbbruchmeldungStornoAuf) {
            updateCarrierChange.setABBM(abbruchmeldungStornoAufMarshaller.apply((AbbruchmeldungStornoAuf) meldung));
        }
        else if (meldung instanceof AbbruchmeldungStornoAen) {
            updateCarrierChange.setABBM(abbruchmeldungStornoAenMarshaller.apply((AbbruchmeldungStornoAen) meldung));
        }
        else if (meldung instanceof AbbruchmeldungTerminverschiebung) {
            updateCarrierChange.setABBM(abbruchmeldungTvMarshaller.apply((AbbruchmeldungTerminverschiebung) meldung));
        }
        else if (meldung instanceof Abbruchmeldung) {
            updateCarrierChange.setABBM(abbruchmeldungMarshaller.apply((Abbruchmeldung) meldung));
        }
        else if (meldung instanceof AbbruchmeldungTechnRessource) {
            updateCarrierChange.setABBMTR(abbruchmeldungTechnRessourceMarshaller.apply((AbbruchmeldungTechnRessource) meldung));
        }
        else if (meldung instanceof RueckmeldungVorabstimmung) {
            updateCarrierChange.setRUEMVA(rueckmeldungVorabstimmungMarshaller.apply((RueckmeldungVorabstimmung) meldung));
        }
        else if (meldung instanceof ErledigtmeldungStornoAuf) {
            updateCarrierChange.setERLM(erledigtmeldungStornoAufMarshaller.apply((ErledigtmeldungStornoAuf) meldung));
        }
        else if (meldung instanceof ErledigtmeldungStornoAen) {
            updateCarrierChange.setERLM(erledigtmeldungStornoAenMarshaller.apply((ErledigtmeldungStornoAen) meldung));
        }
        else if (meldung instanceof ErledigtmeldungTerminverschiebung) {
            updateCarrierChange.setERLM(erledigtmeldungTvMarshaller.apply((ErledigtmeldungTerminverschiebung) meldung));
        }
        else if (meldung instanceof Erledigtmeldung) {
            updateCarrierChange.setERLM(erledigtmeldungMarshaller.apply((Erledigtmeldung) meldung));
        }

        return updateCarrierChange;
    }
}
