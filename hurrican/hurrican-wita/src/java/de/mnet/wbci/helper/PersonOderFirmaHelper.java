/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.helper;

import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.Pair;
import de.mnet.common.tools.PhoneticCheck;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;

/**
 * Hilfsklasse fuer {@link de.mnet.wbci.model.PersonOderFirma} Objekte.
 */
public class PersonOderFirmaHelper {

    /**
     * Vergleicht die Werte aus den angegebenen {@link PersonOderFirma} Objekten mit {@code waiNameOderFirma} bzw.
     * {@code waiVornameOderFirmenzusatz}. (Der Vergleich findet phonetisch statt.) <br>
     *
     * @param personOderFirmas
     * @param waiNameOderFirma
     * @param waiVornameOderFirmenzusatz
     * @return Pair mit Listen von {@link PersonOderFirma} Objekten. Das erste Objekt ist eine Liste mit den
     * 'matchenden' Objekten; das zweite Objekt ist die Liste mit den nicht matchenden Objekten.
     */
    public static
    @NotNull
    Pair<List<PersonOderFirma>, List<PersonOderFirma>> divideInMatchingAndNotMatching(
            @NotNull List<PersonOderFirma> personOderFirmas,
            String waiNameOderFirma,
            String waiVornameOderFirmenzusatz) {

        List<PersonOderFirma> matches = new ArrayList<>();
        List<PersonOderFirma> matchesNot = new ArrayList<>();
        for (PersonOderFirma personOderFirma : personOderFirmas) {
            String nameOderFirma = null;
            String vornameOderFirmenzusatz = null;
            if (personOderFirma instanceof Person) {
                Person person = (Person) personOderFirma;
                nameOderFirma = person.getNachname();
                vornameOderFirmenzusatz = person.getVorname();
            }
            else if (personOderFirma instanceof Firma) {
                Firma firma = (Firma) personOderFirma;
                nameOderFirma = firma.getFirmenname();
                vornameOderFirmenzusatz = firma.getFirmennamenZusatz();
            }

            PhoneticCheck phoneticCheck = new PhoneticCheck(PhoneticCheck.Codec.COLOGNE, true);
            if (phoneticCheck.isPhoneticEqual(nameOderFirma, waiNameOderFirma) &&
                    phoneticCheck.isPhoneticEqual(vornameOderFirmenzusatz, waiVornameOderFirmenzusatz)) {
                matches.add(personOderFirma);
            }
            else {
                matchesNot.add(personOderFirma);
            }
        }
        return Pair.create(matches, matchesNot);
    }

}
