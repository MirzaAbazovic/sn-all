/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 03.02.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.mapper;


import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.billing.Adresse;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.FirmaMitTelefonType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.OrtType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.PersonMitTelefonType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.StandortAType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.StrasseType;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Standort;

/**
 * Maps Adresse  to StandortA
 * <p>
 * Created by wieran on 03.02.2017.
 */
@Component("WholesaleStandortAMapper")
public class WholesaleStandortAMapper {

    /**
     * Maps {@link Adresse} to {@link StandortAType}.
     *
     * @param standort                The {@link Standort} to map. Optional.
     * @param endkundePersonOderFirma
     * @return The mapped {@link StandortAType} or null if standort is null.
     */
    public StandortAType createStandortA(Standort standort, PersonOderFirma endkundePersonOderFirma) {
        if (standort == null || endkundePersonOderFirma == null) {
            return null;
        }
        StandortAType standortA = new StandortAType();
        standortA.setStrasse(createStrasse(standort));
        standortA.setPostleitzahl(standort.getPostleitzahl());
        standortA.setOrt(createOrt(standort));
        KundenTyp kundenTyp = endkundePersonOderFirma.getKundenTyp();
        switch (kundenTyp) {
            case GK:
                standortA.setFirma(createFirma(endkundePersonOderFirma));
                break;
            case PK:
                standortA.setPerson(createPerson(endkundePersonOderFirma));
                break;
            default:
                throw new RuntimeException("kundenTyp not know: " + kundenTyp.name());
        }
        return standortA;
    }

    private FirmaMitTelefonType createFirma(PersonOderFirma endkundePersonOderFirma) {
        FirmaMitTelefonType firmaMitTelefonType = new FirmaMitTelefonType();
        firmaMitTelefonType.setAnrede(createAnredeGk(endkundePersonOderFirma));
        firmaMitTelefonType.setFirmenname(endkundePersonOderFirma.getNameOrFirma());
        firmaMitTelefonType.setFirmennameZweiterTeil(endkundePersonOderFirma.getVornameOrZusatz());
        return firmaMitTelefonType;
    }

    private PersonMitTelefonType createPerson(PersonOderFirma endkundePersonOderFirma) {
        PersonMitTelefonType personMitTelefonType = new PersonMitTelefonType();
        personMitTelefonType.setAnrede(createAnredePk(endkundePersonOderFirma));
        personMitTelefonType.setVorname(endkundePersonOderFirma.getVornameOrZusatz());
        personMitTelefonType.setNachname(endkundePersonOderFirma.getNameOrFirma());
        return personMitTelefonType;
    }

    private String createAnredePk(PersonOderFirma endkundePersonOderFirma) {
        switch (endkundePersonOderFirma.getAnrede()) {
            case HERR:
                return "1";
            case FRAU:
                return "2";
            default:
                return "9";
        }
    }

    private String createAnredeGk(PersonOderFirma endkundePersonOderFirma) {
        switch (endkundePersonOderFirma.getAnrede()) {
            case FIRMA:
                return "4";
            default:
                return "9";
        }
    }

    private OrtType createOrt(Standort standort) {
        OrtType ort = new OrtType();
        ort.setOrtsname(standort.getOrt());
        return ort;
    }

    private StrasseType createStrasse(Standort standort) {
        StrasseType strasse = new StrasseType();
        strasse.setStrassenname(standort.getStrasse().getStrassenname());
        strasse.setHausnummer(standort.getStrasse().getHausnummer());
        strasse.setHausnummernZusatz(standort.getStrasse().getHausnummernZusatz());
        return strasse;
    }
}
