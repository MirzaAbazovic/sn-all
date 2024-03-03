/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.2012 13:07:40
 */
package de.augustakom.hurrican.service.elektra;

import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.elektra.services.DialingNumberType;
import de.mnet.elektra.services.NumberPortierungType;
import de.mnet.wbci.model.CarrierCode;

/**
 * Service-Definition fuer die Elektra-Fassade.
 */
@ObjectsAreNonnullByDefault
public interface ElektraFacadeService extends IElektraService {

    /**
     * Definiert den Report-Typ fuer {@link ElektraFacadeService#generateAndPrintReportByType(Long, String)}, um 
     * eine 'Kuendigungsbestaetigung ohne Rueckforderungspositionen' zu erstellen. 
     */
    public static final String REPORT_TYPE_KB_OHNE_RUECKFORDERUNG = "KB_OHNE_RUECKFORDERUNG";

    /**
     * Definiert den Report-Typ fuer {@link ElektraFacadeService#generateAndPrintReportByType(Long, String)}, um 
     * eine 'Kuendigungsbestaetigung mit Rueckforderungspositionen' zu erstellen. 
     */
    public static final String REPORT_TYPE_KB_MIT_RUECKFORDERUNG = "KB_MIT_RUECKFORDERUNG";

    /**
     * Definiert den Report-Typ fuer {@link ElektraFacadeService#generateAndPrintReportByType(Long, String)}, um 
     * eine 'Kuendigungs-Storno' zu erstellen. 
     */
    public static final String REPORT_TYPE_KB_STORNO = "KB_STORNO";

    /**
     * Ruft den Elektra-WebService changeOrderRealDateRequest auf. <br> Dadurch wird das Realisierungsdatum des Auftrags
     * (inkl. Positionen, Rufnummern etc.) auf das angegebene Datum gesetzt. <br> Voraussetzungen: <br> <ul> <li>Auftrag
     * ist NEU <li>Rufnummern besitzen keinen 'tatsaechlichen Termin' </ul>
     *
     * @param auftragDaten
     * @param referencedOrderNoOrig
     * @param cbVorgang
     * @param subOrderIds           Liste mit den Hurrican Auftrag-Ids, die als Sub-Auftraege fuer den angegebenen
     *                              {@link CBVorgang} hinterlegt sind
     * @return
     */
    String changeOrderRealDate(AuftragDaten auftragDaten, @Nullable Long referencedOrderNoOrig, CBVorgang cbVorgang, @Nullable Set<Long> subOrderIds)
            throws StoreException, FindException;

    /**
     * Ruft den Elektra-WebService generateAndPrintReportWithEvaluationRequest auf.
     *
     * @param auftragDaten
     * @param cbVorgang
     * @return
     * @throws StoreException
     * @throws FindException
     */
    String generateAndPrintReportWithEvaluation(AuftragDaten auftragDaten, CBVorgang cbVorgang) throws StoreException, FindException;

    /**
     * Ruft den Elektra-WebService generateAndPrintReportByTypeRequest auf.
     *
     * @param billingOrderNoOrig
     * @param documentName
     * @return
     * @throws StoreException
     * @throws FindException
     */
    String generateAndPrintReportByType(Long billingOrderNoOrig, String documentName) throws StoreException, FindException;

    /**
     * Ruft den Elektra-WebService terminateOrder auf. <br> Daurch wird der Auftragstatus in Scanview geaendert. <br>
     *
     * @param cbVorgang
     * @return
     */
    String terminateOrder(CBVorgang cbVorgang) throws StoreException;

    /**
     * Ruft den Elektra-WebService changeOrderDialNumber auf. <br> Dadurch werden die Rufnummern und den abgestimmten
     * Wechseltermin in Taifun angepasst<br>
     * @param realDate
     * @param ruemVaReceivedAt
     * @param vaOrderSentAt
     * @param billingOrderNos
     * @param numberPortierung ist optional und wird nur bei KUEMRN und RRNP gesetzt
     * @return
     */
    ElektraResponseDto changeOrderDialNumber(LocalDate realDate, LocalDate ruemVaReceivedAt, LocalDate vaOrderSentAt, List<Long> billingOrderNos, NumberPortierungType numberPortierung)
        throws StoreException;

    /**
     * Ruft den Elektra-WebService updatePortKennungTnb auf. <br> Dadurch wird die Portierungskennung auf allen Rufnummern für den
     * gegebenen Taifun Auftrag angepasst.<br>
     *
     * @param auftragOrderNo
     * @param pkiKennungAuf
     * @param pkiKennungAbg
     * @return
     */
    ElektraResponseDto updatePortKennungTnb(List<Long> auftragOrderNo, String pkiKennungAuf, String pkiKennungAbg) throws StoreException;

    /**
     * Ruft den Elektra-WebService changeOrderCancellationDate auf. <br> Dadurch werden die Rufnummern und der
     * abgestimmte Wechseltermin in Taifun angepasst.<br>
     *
     * @param referencedOrderNoOrigs
     * @param realDate
     * @return
     */
    ElektraResponseDto changeOrderCancellationDate(@Nullable Set<Long> referencedOrderNoOrigs, LocalDate realDate)
            throws StoreException, FindException;

    /**
     * Ruft den Elektra-WebService portCancelledDialNumber auf. <br> Dadurch werden die Rufnummern aus einer RRNP
     * und der abgestimmte Wechseltermin in Taifun angepasst.<br>
     *
     * @param realTimeFrom
     * @param realTimeTo
     * @param dialNumbers
     * @param rrnpReceivedAt
     * @param pkiKennungAuf
     * @return
     */
    ElektraResponseDto portCancelledDialNumber(LocalDateTime realTimeFrom, LocalDateTime realTimeTo, List<DialingNumberType> dialNumbers, LocalDate rrnpReceivedAt, String pkiKennungAuf);

    /**
     * Ruft den Elektra-WebService addDialNumbers auf, um die angegebene Rufnummer dem Auftrag hinzuzufuegen.
     * @param orderNoOrig
     * @param toAdd
     * @param pkiAuf
     * @param pkiAbg
     * @param realDate
     * @return
     * @throws StoreException
     */
    ElektraResponseDto addDialNumber(Long orderNoOrig, DialingNumberType toAdd, String pkiAuf, String pkiAbg, LocalDate realDate)
                throws StoreException;

    /**
     * Ruft den Elektra-WebService deleteDialNumbers auf, um die angegebene Rufnummer zu loeschen
     * @param orderNoOrig
     * @param toDelete
     * @return
     * @throws StoreException
     */
    ElektraResponseDto deleteDialNumber(Long orderNoOrig, DialingNumberType toDelete) throws StoreException;


    /**
     * Ruft den Elektra-Webservice cancelOrder auf, um einen Taifun Auftrag zu kuendigen.
     * @param orderNoOrig
     * @param cancelDate
     * @param futureCarrier
     * @param dialingNumberTypesToPort
     * @return
     */
    ElektraCancelOrderResponseDto cancelOrder(Long orderNoOrig, LocalDate cancelDate, LocalDate entryDate,
            CarrierCode futureCarrier, List<DialingNumberType> dialingNumberTypesToPort) throws StoreException;
    
    /**
     * Macht die Stornierung eines Taifun-Auftrags rueckgaengig
     *
     * @param billingOrderNoOrig
     * @return
     */
    ElektraCancelOrderResponseDto undoCancellation(Long billingOrderNoOrig) throws StoreException;
}
