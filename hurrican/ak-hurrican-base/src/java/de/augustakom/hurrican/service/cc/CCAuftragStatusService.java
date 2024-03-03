/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2012 11:48:14
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 *
 */
public interface CCAuftragStatusService extends ICCService {

    /**
     * kuendigt nach Moeglichkeit die Carrierbestellung, setzt die Rangierung freigabebereit und setzt den Auftrag in
     * einen Kuendigungsstatus.
     * (Bei der Carrierbestellung wird dabei lediglich ein Kuendigungsdatum gesetzt; die eigentlich Kuendigung - z.B.
     * ueber WITA - erfolgt hier nicht!)
     *
     * @return ggf Warnhinweise ueber noch noetiges manuelles Doing oder ein leeres AKWarnings
     */
    @Nonnull
    AKWarnings kuendigeAuftragUndPhysik(@Nonnull Long auftragId, @Nonnull Date kuendigungsDatum, @Nonnull Long sessionId);

    /**
     * Ermittelt alle 'kuendigungs-relevanten' Hurrican Auftraege zu dem Taifun Auftrag und kuendigt diese (inkl.
     * Erstellung eines Kuendigungs-BAs).
     * Fuer alle Auftraege, die sich noch 'in Realisierung' befinden, wird ein Eintrag in den {@link AKWarnings}
     * erstellt. <br/>
     * Bei Auftraegen, die sich schon 'in Kuendigung' befinden, wird zusaetzlich ein evtl. vorhandenes Kuendigungsdatum
     * (auf dem Auftrag und Bauauftrag) geprueft und ggf. angepasst. Sollte dies nicht moeglich sein (z.B. weil der
     * Bauauftrag schon zu weit fortgeschritten ist), wird dies ebenfalls in den {@link AKWarnings} eingetragen.
     *
     * @param orderNoOrig Billing Auftrags-Id
     * @param kuendigungsDatum Datum, zu dem die Hurrican-Auftraege gekuendigt werden sollen
     * @param user
     * @return {@link AKWarnings} Objekt mit allen aufgetretenen 'Unklarheiten' bei der Kuendigung
     */
    @Nonnull AKWarnings cancelHurricanOrdersAndCreateBA(@Nonnull Long orderNoOrig, @Nonnull Date kuendigungsDatum,
            @Nonnull AKUser user, @Nonnull Long sessionId);


    /**
     * pruefen, ob Auftrag in (gueltigem) VPN_KONF verwendet wird! Wenn ja, Warnhinweis, dass VPN-Konfig zu
     * Kuendigungstermin auf anderen phys. Auftrag verweisen muss!
     */
    AKWarnings checkVpn(Long auftragId, @Nonnull AKWarnings warnings);

    /**
     * Setzt den Status des Auftrags mit der ID <code>auftragId</code> auf 'Kuendigung'.
     *
     * @param auftragId        ID des zu kuendigenden Auftrags.
     * @param kuendigungsDatum Datum, zu dem der Auftrag gekuendigt werden soll.
     * @param sessionId        Session Information des aktuellen Users.
     * @throws StoreException wenn bei der Kuendigung ein Fehler auftritt.
     */
    public void kuendigeAuftrag(Long auftragId, Date kuendigungsDatum, Long sessionId) throws StoreException;

    /**
     * Setzt den Status des Auftrags auf 'Absage' und gibt die Rangierung des Auftrags frei. <br> Bei bereits
     * zugeordneten Rufnummernleistungen wird das Kuendigungsdatum der Leistung auf den Realisierungsdatum der Leistung
     * gesetzt.
     *
     * @param auftragId Hurrican Auftrags-ID.
     * @param sessionId Session-ID des aktuellen Users.
     * @throws StoreException
     */
    public abstract void performAuftragAbsagen(Long auftragId, Long sessionId) throws StoreException, FindException;

    /**
     * Prüft den Auftrag, ob ein Absagen möglich ist. Diese Methode wird auch intern von #performAuftragAbsagen gerufen.
     * Hiermit ist es jedoch in der GUI möglich, <ol> <li>mit #checkAuftragAbsagen zu prüfen,</li> <li>die Aktion
     * nochmals vom Benutzer bestätigen zu lassen (Sicherheitsabfrage) und anschließend</li> <li>die Aktion mit
     * #performAuftragAbsagen auszuführen.</li> </ol>
     *
     * @param auftragId
     * @throws FindException
     * @thrown IllegalStateException Auftrag im falschen Status (Absagen nicht möglich)
     */
    void checkAuftragAbsagen(Long auftragId) throws FindException, IllegalStateException;

    /**
     * Datencontainer für Informationen, die für die Erstellung der CPS-Transaktion bei Auftrag-Absage benötigt werden.
     */
    public class CpsTxInfosForAuftragAbsage {
        public enum CpsTxType {
            NONE(null),
            MODIFY_SUB(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB),
            CANCEL_SUB(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);

            CpsTxType(final Long serviceOrderType) {
                this.serviceOrderType = serviceOrderType;
            }

            public final Long serviceOrderType;
        }

        private final CpsTxType cpsTxType;
        private final Date executionDate;
        private final Long auftragId;

        public CpsTxInfosForAuftragAbsage(CpsTxType cpsTxType, Date executionDate, Long auftragId) {
            this.cpsTxType = cpsTxType;
            this.executionDate = executionDate;
            this.auftragId = auftragId;
        }

        public CpsTxType getCpsTxType() {
            return cpsTxType;
        }

        public Date getExecutionDateForAbsage() {
            return this.executionDate;
        }

        public Long getAuftragId() {
            return auftragId;
        }
    }

    /**
     * Ermittelt den Typ und das Ausführungsdatum der CPS-Transaktion, die bei der Absage des Auftrags mit der ID
     * auftragId erstellt werden muss.
     */
    CpsTxInfosForAuftragAbsage determineCpsTxInfosForAuftragAbsage(Long auftragId) throws FindException;

    /**
     * Setzt den CpsService neu (fuer Tests geeignet)
     */
    void setCpsService(CPSService cpsService);

}
