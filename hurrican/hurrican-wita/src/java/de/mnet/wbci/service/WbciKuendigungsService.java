/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.mnet.wbci.service;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;

import de.augustakom.hurrican.model.billing.BAuftrag;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;

/**
 * Service zur Ermittlung / Pruefung von Kuendigungsdaten zu einem Auftrag.
 */
@ObjectsAreNonnullByDefault
public interface WbciKuendigungsService extends WbciService {

    /**
     * Definiert die Vorlaufszeit in Arbeitstagen, die bei einen bereits gekündigten Kundenauftrag empfehlenswerter
     * Weise eingehalten werden sollte, um den Kundenwunschtermin des WBCI-Vorabstimmung (für einen Anbieterwechsel) zu
     * ermöglichen. Die Vorlaufszeit ist nur fuer die Vorabstimmungen VA_KUE_MRN und VA_KUE_ORN relevant.
     */
    int ALLOWED_WORKING_DAYS_BEFORE_WECHSELTERMIN_WARNING = 10;
    /**
     * Definiert die Vorlaufszeit in Arbeitstagen, die bei einen bereits gekündigten Kundenauftrag ZWINGEND eingehalten
     * werden müssen, um den Kundenwunschtermin des WBCI-Vorabstimmung (für einen Anbieterwechsel) zu ermöglichen. Die
     * Vorlaufszeit ist nur fuer die Vorabstimmungen VA_KUE_MRN und VA_KUE_ORN relevant.
     */
    int ALLOWED_WORKING_DAYS_BEFORE_WECHSELTERMIN_ERROR = 5;
    /**
     * Definiert die Karenzzeit in Kalendertagen, die bei einen bereits gekündigten Kundenauftrag in der Vergangenheit
     * liegen darf, um noch eine reine Rufnummernportierung zu ermöglichen.
     */
    int ALLOWED_DAYS_AFTER_CANCELLATION = 90;

    /**
     * Fuehrt einen 'gesamten' Kuendigungs-Check fuer den angegebenen Auftrag durch und gibt das Ergebnis in einem VO
     * zurueck.
     *
     * @param auftragNoOrig
     * @param cancellationIncome Eingangsdatum der Kuendigung
     * @return
     */
    KuendigungsCheckVO doKuendigungsCheck(Long auftragNoOrig, LocalDateTime cancellationIncome) throws WbciServiceException;


    /**
     * Ermittelt den Billing-Auftrag zu der angegebenen Id.
     * @param billingAuftragNo
     * @return
     * @throws WbciServiceException wenn kein gueltiger Billing-Auftrag gefunden wurde
     */
    @NotNull BAuftrag getCheckedBillingAuftrag(Long billingAuftragNo);

    /**
     * Berechnet zu dem angegebenen Auftrag das naechst moegliche Kuendigungsdatum und traegt die zur Berechnung heran
     * gezogenen Daten in das {@code kuendigungsCheckVO} Objekt ein.
     *
     * @param billingOrder
     * @param cancellationIncome Eingangsdatum der Kuendigung
     * @param kuendigungsCheckVO
     */
    void evaluateCancelCheckForOrder(BAuftrag billingOrder, LocalDateTime cancellationIncome, KuendigungsCheckVO kuendigungsCheckVO);

    /**
     * Ermittelt zu der angegebenen Taifun-Auftrags, dass dort eingetragene Kuendigungsdatum
     *
     * @param billingOrderNo Taifun-Auftrags-Nr.
     * @return das Kündigungsdatum oder null wenn kein Kündigungsdatum in Taifun gesetzt wurde; falls zu der angegebenen
     *         Id kein Auftrag gefunden wird, wird {@code new DateTime(0)} zurueck gegeben
     */
    LocalDateTime getTaifunKuendigungstermin(Long billingOrderNo);

    /**
     * Determines all cancellable {@link Leitung#vertragsnummer} of the latest {@link UebernahmeRessourceMeldung}, in
     * the view of M-Net as {@link CarrierRole#ABGEBEND}.
     *
     * @param vorabstimmungsId {@link VorabstimmungsAnfrage#getVorabstimmungsId()}
     * @return a Set of cancellable {@link Leitung#vertragsnummer} or a empty Set if no cancellable WITA-Vertragsnummern
     * have been found
     */
    SortedSet<String> getCancellableWitaVertragsnummern(String vorabstimmungsId);
}
