/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 14:02:40
 */
package de.mnet.wita.service.impl;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.TalAenderungstypService;

/**
 * Implementierung von {@code TalAenderungstypService}. <br> <br> <br> Die Implementierung ermittelt immer nur die Ports
 * des ersten angegebenen Auftrags in dem CBVorgang. Dies ist bei WITA auch i.O., da hier eine Klammerung (im Gegensatz
 * zur ESAA) ueber verschiedene CBVorgaenge realisiert wird.
 */
@CcTxRequired
public class TalAenderungstypServiceImpl implements TalAenderungstypService {

    private static final Logger LOGGER = Logger.getLogger(TalAenderungstypServiceImpl.class);

    @Resource(name = "de.mnet.wita.service.impl.WitaDataService")
    WitaDataService witaDataService;

    @Override
    public GeschaeftsfallTyp determineGeschaeftsfall(Carrierbestellung carrierbestellung, WitaCBVorgang cbVorgang)
            throws WitaBaseException {
        // @formatter:off
        if (cbVorgang == null) {
            throw new WitaBaseException("Referenzierter CBVorgang ist nicht angegeben!");
        }
        // DTAG Portaenderung
        if (cbVorgang.getPreviousUebertragungsVerfahren() != null ) {
            return GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG;
        }

        if (carrierbestellung == null) {
            throw new WitaBaseException("Carrierbestellung ist nicht angegeben!");
        }
        Long auftragIdNew = cbVorgang.getAuftragId();
        if (auftragIdNew == null) {
            throw new WitaBaseException("Referenzierter Auftrag ist nicht angegeben!");
        }

        if (carrierbestellung.getAuftragId4TalNA() == null) {
            throw new WitaBaseException("Es ist kein Ursprungsauftrag auf der Carrierbestellung angegeben!");
        }
        // @formatter:on

        Pair<Equipment, Equipment> equipments;
        try {
            equipments = witaDataService.loadEquipments(carrierbestellung, auftragIdNew);
            if ((equipments.getFirst() == null) || (equipments.getSecond() == null)) {
                throw new WitaBaseException(
                        "Fuer die Ermittlung des Aenderungsgeschaeftsfalls muessen zwei Ports angegeben werden!");
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaBaseException("Fehler bei der Ermittlung des Aenderungsgeschaeftsfalls: " + e.getMessage(), e);
        }

        return determineGeschaeftsfallTypAenderung(equipments.getFirst(), equipments.getSecond());
    }


    GeschaeftsfallTyp determineGeschaeftsfallTypAenderung(Equipment equipmentOld, Equipment equipmentNew) {
        if (!portsManagedByDtag(equipmentOld, equipmentNew) || portsEqual(equipmentOld, equipmentNew)) { return null; }

        if (rangSchnittstelleEqual(equipmentOld, equipmentNew)) {
            // @formatter:off
            // SER-POW oder LMAE
            if (uetvEqual(equipmentOld, equipmentNew)) {
                return GeschaeftsfallTyp.PORTWECHSEL;
            }

            if (equipmentNew.getRangSchnittstelle() == RangSchnittstelle.H) {
                return GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG;
            }
            // @formatter:on
        }
        else {
            return GeschaeftsfallTyp.LEISTUNGS_AENDERUNG;
        }

        return null;
    }

    /* Prueft, ob beide Ports vom Carrier DTAG sind */
    private boolean portsManagedByDtag(Equipment equipmentOld, Equipment equipmentNew) {
        return (StringUtils.equalsIgnoreCase(equipmentOld.getCarrier(), Carrier.DTAG.toString())
                && StringUtils.equalsIgnoreCase(equipmentNew.getCarrier(), Carrier.DTAG.toString()));
    }

    /* Prueft, ob beide Ports identisch sind (ueber die Equipment-ID) */
    private boolean portsEqual(Equipment equipmentOld, Equipment equipmentNew) {
        return NumberTools.equal(equipmentOld.getId(), equipmentNew.getId());
    }

    /* Prueft, ob die RangSchnittstelle (z.B. 'N', 'H', etc.) identisch sind. */
    private boolean rangSchnittstelleEqual(Equipment equipmentOld, Equipment equipmentNew) {
        return equipmentOld.getRangSchnittstelle() == equipmentNew.getRangSchnittstelle();
    }

    /* Prueft, ob das Uebertragungsverfahren der beiden Ports identisch ist. */
    private boolean uetvEqual(Equipment equipmentOld, Equipment equipmentNew) {
        return equipmentOld.getUetv() == equipmentNew.getUetv();
    }

}
