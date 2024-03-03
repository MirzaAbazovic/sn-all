/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 15:55:28
 */
package de.augustakom.hurrican.service.wholesale;

import java.time.*;

import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.service.cc.ICCService;

/**
 * Definiert den Hurrican Wholesale-Service.
 */
public interface WholesaleService extends ICCService {

    /**
     * Legt einen techn. Auftrag (Auftrag, AuftragDaten, AuftragTechnik) an, erzeugt eine LineId, erstellt techn.
     * Leistungen, ordnet techn. Standort zu und sowie eine freie Rangierung dem Auftrag zu.
     *
     * @param reservePortRequest Objekt mit Angabe des gewuenschten Produkts, des Standorts (GeoId) sowie des
     *                           gewuenschten Realisierungsdatums.
     * @return Response-Objekt mit Angabe der LineId und Realisierungsdatum
     */
    WholesaleReservePortResponse reservePort(WholesaleReservePortRequest reservePortRequest);

    /**
     * Aendert Leistungen und Leistungsmerkmale eines bestehenden Auftrags (identifiziert ueber die LineId) ab.
     *
     * @param modifyPortRequest Objekt mit den gewuenschten kuenftigen Daten zu einer existierenden LineId.
     * @return
     */
    WholesaleModifyPortResponse modifyPort(WholesaleModifyPortRequest modifyPortRequest);

    /**
     * Aendert das Datum der Port-Reservierung bzw. eines offenen modifyPorts auf das im Request angegebene Datum ab.
     *
     * @param request
     * @return
     */
    WholesaleModifyPortReservationDateResponse modifyPortReservationDate(WholesaleModifyPortReservationDateRequest request);

    /**
     * Macht einen aktiven modifyPort Request rueckgaengig.
     *
     * @param request
     * @return
     */
    WholesaleCancelModifyPortResponse cancelModifyPort(WholesaleCancelModifyPortRequest request);

    /**
     * Gibt den Port, der der angegebenen LineId zugoerdnet ist zu 'jetzt' frei. Der zugehoerige Auftrag wird dabei auf
     * den Status 'gekuendigt' gesetzt.
     *
     * @param request Objekt mit Angaben zum Port (lineId) der freigegeben werden soll.
     * @return aktuelles Datum, da Freigabe immer zu 'jetzt' erfolgt
     */
    LocalDate releasePort(WholesaleReleasePortRequest request);


}
