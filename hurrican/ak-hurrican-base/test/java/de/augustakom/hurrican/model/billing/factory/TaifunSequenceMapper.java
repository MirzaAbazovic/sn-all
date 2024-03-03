/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2015
 */
package de.augustakom.hurrican.model.billing.factory;

import com.google.common.collect.ImmutableMap;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Person;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.Rufnummer;

/**
 * Hilfsklasse, um den Namen der ID Sequence zu einer Taifun-Klasse zu bestimmen.
 */
public class TaifunSequenceMapper {

    // @formatter:off
    private static final ImmutableMap<Class, String> sequenceMappings =
            ImmutableMap.<Class, String>builder()

            .put(Kunde.class, "S_CUSTOMER_0")
            .put(Person.class, "S_PERSON_0")
            .put(BAuftrag.class, "S_AUFTRAG_0")
            .put(BAuftragPos.class, "S_AUFTRAGPOS_0")
            .put(RInfo.class, "S_BILL_SPEC_0")
            .put(Rufnummer.class, "S_DN_0")
            .put(Adresse.class, "S_ADDRESS_0")
            .put(Device.class, "S_DEVICE_0")
            .build();
    // @formatter:on

    public static String getSequenceName(Class taifunClazz) {
        return sequenceMappings.get(taifunClazz);
    }

}
