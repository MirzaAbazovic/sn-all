/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.13
 */
package de.mnet.wbci.converter;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.mnet.common.tools.NameUtils;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.PersonOderFirmaTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.builder.FirmaBuilder;
import de.mnet.wbci.model.builder.PersonBuilder;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;

/**
 * Hilfsklasse zum Konvertieren von Objekten aus dem Hurrican-Modell ins Wbci-Modell
 *
 *
 */
public class HurricanToWbciConverter {

    private static final String TITLE_MR = "HERR";
    private static final String TITLE_MS = "FRAU";

    /**
     * Extract the person or company, which is contained in the provided address
     */
    public static PersonOderFirma extractPersonOderFirma(Adresse adresse) {
        final PersonOderFirmaTyp personOderFirmaTyp = convertAddressFormat(adresse.getFormatName());
        switch (personOderFirmaTyp) {
            case PERSON:
                return new PersonBuilder()
                        .withVorname(adresse.getVorname())
                        .withNachname(adresse.getName())
                        .withAnrede(convertTitle(adresse.getAnrede()))
                        .build();
            case FIRMA:
                Pair<String, String> pair = NameUtils.normalizeToLength(adresse.getName(), adresse.getVorname(), PersonOderFirma.MAX_CHARS_OF_NAME_FIELDS);
                return new FirmaBuilder()
                        .withFirmename(pair.getFirst())
                        .withFirmennamenZusatz(pair.getSecond())
                        .withAnrede(Anrede.FIRMA)
                        .build();
            default:
                throw new RuntimeException(String.format("PersonOderFirmaTyp '%s' nicht unterstuetzt", personOderFirmaTyp));
        }
    }

    /**
     * Converts the provided address to a Wbci Standort object.
     */
    public static Standort extractStandort(AddressModel adresse) {
        return new StandortBuilder()
                .withOrt(adresse.getOrt())
                .withPostleitzahl(adresse.getPlzTrimmed())
                .withStrasse(new StrasseBuilder()
                        .withStrassenname(adresse.getStrasse())
                        .withHausnummer(adresse.getNummer())
                        .withHausnummernZusatz(adresse.getHausnummerZusatz())
                        .build())
                .build();
    }

    /**
     * To be removed together with {@link de.mnet.wbci.service.impl.WbciCommonServiceImpl#getTnbKennung(Endstelle)}
     *
     * Use {@link de.mnet.wbci.service.impl.WbciCommonServiceImpl#getTnbKennung(Long)}
     */
    @Deprecated
    public static String extractPKIaufFromElTalAbsenderId(CarrierKennung carrierKennung) {
        return (carrierKennung != null) ? carrierKennung.getElTalAbsenderId().substring(0, 4) : null;
    }

    private static PersonOderFirmaTyp convertAddressFormat(String addressFormat) {
        if (CCAddress.ADDRESS_FORMAT_BUSINESS.equals(addressFormat)) {
            return PersonOderFirmaTyp.FIRMA;
        }
        else {
            return PersonOderFirmaTyp.PERSON;
        }
    }

    private static Anrede convertTitle(String title) {
        if (TITLE_MR.equalsIgnoreCase(title)) {
            return Anrede.HERR;
        }
        else if (TITLE_MS.equalsIgnoreCase(title)) {
            return Anrede.FRAU;
        }
        return Anrede.UNBEKANNT;
    }

}
