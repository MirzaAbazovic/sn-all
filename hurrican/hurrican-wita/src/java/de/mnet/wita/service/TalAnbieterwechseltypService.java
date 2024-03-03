/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 13:57:38
 */
package de.mnet.wita.service;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

public interface TalAnbieterwechseltypService extends TalDetermineGeschaeftsfallService {

    /**
     * Determines for an {@link WitaCBVorgang#TYP_ANBIETERWECHSEL} the expected {@link GeschaeftsfallTyp} in
     * consideration of an successful processed WBCI {@link VorabstimmungsAnfrage}.
     *
     * @param wbciVorabstimmungsId {@link WbciGeschaeftsfall#vorabstimmungsId}
     * @param equipment            the expected {@link Equipment}
     * @return {@link GeschaeftsfallTyp#PROVIDERWECHSEL} or {@link GeschaeftsfallTyp#VERBUNDLEISTUNG}.
     */
    GeschaeftsfallTyp determineAnbieterwechseltyp(String wbciVorabstimmungsId, Equipment equipment);

    /**
     * Determines for an {@link WitaCBVorgang#TYP_ANBIETERWECHSEL} the expected {@link GeschaeftsfallTyp} in
     * consideration of the provided {@link Vorabstimmung}.
     *
     * @param vorabstimmung {@link Vorabstimmung}
     * @param equipment     the expected {@link Equipment}
     * @return {@link GeschaeftsfallTyp#PROVIDERWECHSEL} or {@link GeschaeftsfallTyp#VERBUNDLEISTUNG}.
     */
    GeschaeftsfallTyp determineAnbieterwechseltyp(Vorabstimmung vorabstimmung, Equipment equipment)
            throws WitaBaseException;

    /**
     * Determines the {@link Equipment} for assigned data.
     *
     * @param carrierbestellung {@link Carrierbestellung}
     * @param auftragIdNew      {@link WitaCBVorgang#auftragId}
     * @return the configured {@link Equipment}
     */
    Equipment getEquipment(Carrierbestellung carrierbestellung, Long auftragIdNew);
}


