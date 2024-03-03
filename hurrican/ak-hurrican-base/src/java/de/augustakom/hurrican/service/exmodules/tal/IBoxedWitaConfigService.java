/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2012 10:24:40
 */
package de.augustakom.hurrican.service.exmodules.tal;

/**
 * "Verpackter" WITA Config Service, um aus Hurrican Base heraus einen minimal Set aus der Wita Config zu lesen. Kann
 * keine WITA Datenstrukturen zurückgeben, da keine Sichtbarkeit von ak-hurrican-base auf hurrican-wita besteht.
 * <p/>
 * Wird vom hurrican-wita...WitaConfigService implementiert.
 *
 *
 */
public interface IBoxedWitaConfigService {

    /**
     * Prueft ob der FTTC KVZ TAL Bereitstellung (NEU) Geschäftsfall zulässig ist (sprich aktiviert ist).
     *
     * @return true wenn erlaubt
     */
    boolean fttcKvzBereitstellungAllowed();

}
