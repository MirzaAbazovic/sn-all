/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2011 08:31:45
 */
package de.mnet.wita.aggregator.utils;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.AddressFormat;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.mnet.common.tools.NameUtils;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.Personenname;

/**
 * Hilfsklasse fuer Adress-Operationen.
 */
public class AggregationAddressHelper {

    /**
     * Ermittelt die (WITA) Anrede fuer die angegebene Adresse.
     */
    public static Anrede getAnredeForAddress(AddressModel addressModel) {
        if (addressModel == null) { return Anrede.UNBEKANNT; }
        if (StringUtils.equalsIgnoreCase(addressModel.getFormatName(), AddressFormat.ADDRESS_FORMAT_NAME_BUSINESS)) {
            return Anrede.FIRMA;
        }
        else if (addressModel instanceof Adresse) {
            Adresse address = (Adresse) addressModel;
            if (StringUtils.equalsIgnoreCase(address.getAnrede(), AddressFormat.SALUTATION_HERR)
                    || StringUtils.equalsIgnoreCase(address.getAnrede(), AddressFormat.SALUTATION_HERRFRAU)) {
                return Anrede.HERR;
            }
            else if (StringUtils.equalsIgnoreCase(address.getAnrede(), AddressFormat.SALUTATION_FRAU)
                    || StringUtils.equalsIgnoreCase(address.getAnrede(), AddressFormat.SALUTATION_FRAUHERR)) {
                return Anrede.FRAU;
            }
        }
        else if (addressModel instanceof CCAddress) {
            CCAddress address = (CCAddress) addressModel;
            if (StringUtils.startsWith(address.getFormatName(), "Herr")
                    || StringUtils.startsWith(address.getFormatName(), "Mr.")) {
                return Anrede.HERR;
            }
            else if (StringUtils.startsWith(address.getFormatName(), "Frau")
                    || StringUtils.startsWith(address.getFormatName(), "Mrs.")) {
                return Anrede.FRAU;
            }
            else if (StringUtils.startsWith(address.getFormatName(), "Firma")) { return Anrede.FIRMA; }
        }

        return Anrede.UNBEKANNT;
    }

    public static String getVornameWithName2AndVorname2(AddressModel addressModel) {
        String vorname = addressModel.getVorname();
        if (StringUtils.isNotBlank(addressModel.getName2()) || StringUtils.isNotBlank(addressModel.getVorname2())) {
            // Name2/Vorname2 in 'Vorname' integrieren
            // (Handling analog zu ESAA; ist mit DTAG so definiert)
            String additionalName = StringTools.join(
                    new String[] { addressModel.getName2(), addressModel.getVorname2() }, " ", true);
            vorname = StringTools.join(new String[] { addressModel.getVorname(), additionalName }, " und ", true);
        }
        return vorname;
    }

    public static Kundenname getKundenname(AddressModel addressModel) {
        Anrede anrede = getAnredeForAddress(addressModel);

        if (anrede == null) {
            throw new WitaDataAggregationException("Addresse konnte nicht ermittelt werden");
        }

        if (anrede.equals(Anrede.FIRMA)) {
            Firmenname firmenname = new Firmenname();
            firmenname.setAnrede(anrede);

            Pair<String, String> firmenNamePair = NameUtils.normalizeToLength(addressModel.getName(),
                    AggregationAddressHelper.getVornameWithName2AndVorname2(addressModel), 30);
            firmenname.setErsterTeil(firmenNamePair.getFirst());
            firmenname.setZweiterTeil(firmenNamePair.getSecond());
            return firmenname;
        }

        // Private-Person
        Personenname personenname = new Personenname();
        personenname.setAnrede(anrede);
        personenname.setNachname(getNameWithName2(addressModel));
        personenname.setVorname(getVornameWithVorname2(addressModel));
        return personenname;
    }

    static String getNameWithName2(AddressModel addressModel) {
        return joinNames(addressModel.getName(), addressModel.getName2(), Personenname.NACHNAME_MAX_SIZE);
    }

    static String getVornameWithVorname2(AddressModel addressModel) {
        return joinNames(addressModel.getVorname(), addressModel.getVorname2(), Personenname.VORNAME_MAX_SIZE);
    }

    private static String joinNames(String name1In, String name2In, int maxSize) {
        String name1 = name1In;
        String name2 = name2In;
        if (name1 != null) {
            name1 = name1.trim();
        }
        if (name2 != null) {
            name2 = name2.trim();
        }
        String name = StringTools.join(new String[] { name1, name2 }, " und ", true);
        if ((name != null) && (name.length() > maxSize)) {
            return name.substring(0, maxSize);
        }
        return name;
    }

    public static String getLandId(AddressModel addressModel) {
        if (StringUtils.isNotBlank(addressModel.getLandId())) {
            return addressModel.getLandId().toLowerCase();
        }
        return null;
    }

}
