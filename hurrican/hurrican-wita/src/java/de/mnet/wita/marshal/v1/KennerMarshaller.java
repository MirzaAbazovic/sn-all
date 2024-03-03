/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import com.google.common.base.Function;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragskennerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KennerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProjektIDType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.AuftragsKenner;
import de.mnet.wita.message.auftrag.Projekt;

public class KennerMarshaller  implements Function<MnetWitaRequest, KennerType> {

    @Override
    public KennerType apply(MnetWitaRequest input) {
        Projekt projekt = input.getProjekt();
        AuftragsKenner auftragsKenner = input.getAuftragsKenner();
        if ((auftragsKenner == null) && (projekt == null)) {
            return null;
        }
        KennerType kenner = new KennerType();
        if (auftragsKenner != null) {
            kenner.setAuftragskenner(marshalAuftragsKenner(auftragsKenner));
        }
        if (projekt != null) {
            kenner.setProjektID(marshalProjekt(projekt));
        }
        return kenner;
    }

    private AuftragskennerType marshalAuftragsKenner(AuftragsKenner auftragsKenner) {
        AuftragskennerType auftragskennerType = new AuftragskennerType();
        auftragskennerType.setAuftragsklammer(auftragsKenner.getAuftragsKlammer().toString());
        auftragskennerType.setAnzahlAuftraege(auftragsKenner.getAnzahlAuftraege());
        return auftragskennerType;
    }

    private ProjektIDType marshalProjekt(Projekt projekt) {
        ProjektIDType projektIDType = new ProjektIDType();
        projektIDType.setProjektkenner(projekt.getProjektKenner());
        projektIDType.setKopplungskenner(projekt.getKopplungsKenner());
        return projektIDType;
    }

}
