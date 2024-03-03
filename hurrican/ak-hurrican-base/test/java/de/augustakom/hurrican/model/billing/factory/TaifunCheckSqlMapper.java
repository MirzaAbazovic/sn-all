/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2015
 */
package de.augustakom.hurrican.model.billing.factory;

import com.google.common.collect.ImmutableMap;

import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Rufnummer;

/**
 * Hilfsklasse, um das CHECK-SQL für die Hurrican DB zu einer Taifun-Klasse zu bestimmen.
 */
public class TaifunCheckSqlMapper {

    public static final String ID_PARAM = "id";
    // @formatter:off
    private static final ImmutableMap<Class, String> sequenceMappings =
            ImmutableMap.<Class, String>builder()
            .put(Kunde.class, "SELECT count(*) FROM T_AUFTRAG WHERE KUNDE__NO = :" + ID_PARAM)
            .put(BAuftrag.class, "SELECT count(*) FROM T_AUFTRAG_DATEN WHERE PRODAK_ORDER__NO = :" + ID_PARAM)
            .put(Rufnummer.class, "SELECT sum(data) "
                                + "FROM (SELECT count(*) AS data "
                                      + "FROM T_LEISTUNG_DN "
                                      + "WHERE DN_NO = :" + ID_PARAM + " "
                                      + "UNION ALL "
                                      + "SELECT count(*) AS data "
                                      + "FROM T_AUFTRAG_VOIP_DN "
                                      + "WHERE DN__NO = :" + ID_PARAM + " "
                                      + ")")
            .build();
    // @formatter:on

    public static String getHurricanCheckSQL(Class taifunClazz) {
        return taifunClazz == null ? null : sequenceMappings.get(taifunClazz);
    }

}
