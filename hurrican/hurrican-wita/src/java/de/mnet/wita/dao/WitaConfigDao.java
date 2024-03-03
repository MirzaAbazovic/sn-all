/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 11:20:50
 */
package de.mnet.wita.dao;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaConfig;
import de.mnet.wita.model.WitaSendLimit;

/**
 * DAO Interface fuer die WITA-Konfigurations Modelle.
 */
public interface WitaConfigDao extends StoreDAO {

    /**
     * Ermittelt das konfigurierte Send-Limit fuer den angegebenen GeschaeftsfallTyp.
     *
     * @param geschaeftsfallTyp GeschaeftsfallTyp fuer den die Konfiguration ermittelt werden soll
     * @param kollokationsTyp   gibt an, fuer welchen Kollokations-Typ (HVT, FTTC_KVZ) das Sende-Limit ermittelt werden
     *                          soll. Fuer Wbci-Sendelimits muss das Feld null sein.
     * @param ituCarrierCode    gibt an, fuer welchen Carrier das Sende-Limit ermittelt werden soll. Fuer
     *                          Wita-Sendelimits muss das Feld null sein.
     * @return die zugehoerige Konfiguration oder {@code NULL}, wenn keine vorhanden
     */
    WitaSendLimit findWitaSendLimit(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, String ituCarrierCode);

    /**
     * Ermittelt die Anzahl der protokollierten WITA Vorgaenge fuer den angegebenen {@link GeschaeftsfallTyp}
     *
     * @param geschaeftsfallTyp Geschaeftsfalltyp, fuer den die Anzahl der gesendeten Vorgaenge ermittelt werden soll.
     * @param kollokationsTyp   {@link KollokationsTyp}, fuer den die Anzahl der gesendeten Vorgaenge ermittelt werden
     *                          soll. Fuer Wbci-Sendelimits muss das Feld null sein.
     * @param ituCarrierCode    gibt an, fuer welchen Carrier das Sende-Limit ermittelt werden soll. Fuer
     *                          Wita-Sendelimits muss das Feld null sein.
     * @return Anzahl der protokollierten Eintraege fuer den {@link GeschaeftsfallTyp}
     */
    Long getWitaSentCount(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, String ituCarrierCode);

    /**
     * Ermittelt die {@link WitaConfig} fuer die Default WITA Version.
     */
    WitaConfig getWitaDefaultVersion();

    /**
     * Ermittelt den WitaConfigEintrag zu einem Schluessel
     *
     * @param key Der Schluessel, zu dem der Eintrag gefunden werden soll
     * @return der Eintrag, fuer den Schluessel oder null, wenn der Schluessel nicht gefunden wurde.
     */
    String getWitaConfigValue(String key);

    /**
     * Ermittelt den WitaConfig zu einem Schluessel
     *
     * @param key Der Schluessel, zu dem die WitaConfig gefunden werden soll
     * @return WitaConfig fuer den Schluessel oder null, wenn der Schluessel nicht gefunden wurde.
     */
    WitaConfig findWitaConfig(String key);

    /**
     * Loescht alle Eintraege zum {@link GeschaeftsfallTyp} und {@link KollokationsTyp} aus der WitaSendCount Tabelle
     */
    void resetWitaSentCount(GeschaeftsfallTyp geschaeftsfallTyp, KollokationsTyp kollokationsTyp);

}
