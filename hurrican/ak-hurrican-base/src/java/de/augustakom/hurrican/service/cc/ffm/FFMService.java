/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2014
 */
package de.augustakom.hurrican.service.cc.ffm;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.ffm.FfmFeedbackMaterial;
import de.augustakom.hurrican.model.cc.ffm.FfmQualification;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyOrderFeedback;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 * Interface definition for all Field Force Management System (FFM) tasks.
 */
public interface FFMService extends ICCService {

    /**
     * Erstellt und versendet eine FFM WorkforceOrder für den Bauauftrag.
     *
     * @param bauauftrag Der Hurrican-Bauauftrag, zu dem eine FFM WorkforceOrder erstellt werden soll.
     * @return die generierte UUID fuer der {@link WorkforceOrder}
     */
    String createAndSendOrder(Verlauf bauauftrag);

    /**
     * siehe updateAndSendOrder(Verlauf bauauftrag)
     *
     * @param verlaufId
     */
    void updateAndSendOrder(@Nonnull Long verlaufId);

    /**
     * Aktualisiert die FFM WorkforceOrder zu dem angegebenen Bauauftrag. <br/> Bedingungen: <br/> <ul> <li>Bauauftrag
     * besitzt bereits eine WorkforceOrder</li> <li>FFM Datensatz des Bauauftrags ist noch nicht abgeschlossen</li>
     * </ul>
     *
     * @param bauauftrag der Bauauftrag zu dem die Workforce Order gehoeren soll
     * @throws FFMServiceException in folgenden Faellen: <ul> <li>beim Update tritt ein Fehler auf</li> <li>Bauauftrag
     *                             besitzt keine FFM WorkforceOrderId</li> <li>FFM Datensatz vom Bauauftrag existiert
     *                             nicht oder ist bereits erledigt</li> </ul>
     */
    void updateAndSendOrder(Verlauf bauauftrag);

    /**
     * Prueft, ob der Bauauftrag eine WorkforceOrder-Id besitzt und ob der zugehoerige FFM Verlaufs-Datensatz noch aktiv
     * ist.
     *
     * @param bauauftrag
     * @return
     */
    boolean hasActiveFfmRecord(Verlauf bauauftrag, boolean checkWorkforceOrder);

    /**
     * Erstellt eine WorkforceOrder zu dem Bauauftrag.
     *
     * @param bauauftrag der Bauauftrag zu dem die Workforce Order gehoeren soll
     */
    WorkforceOrder createOrder(Verlauf bauauftrag);

    /**
     * Erstellt eine WorkforceOrder zu dem Auftrag und Anlass (OHNE versenden!).
     *
     * @param auftragId         Hurrican Auftrags-ID
     * @param baVerlaufAnlassId ID eines Bauauftrag Anlasses
     * @return
     */
    WorkforceOrder createOrder(Long auftragId, Long baVerlaufAnlassId);

    /**
     * Performs update on Hurrican-Bauauftrag based on FFM notification. Notification state evaluates to type of
     * notification and related update tasks.
     *
     * @param in
     * @param sessionId
     */
    void notifyUpdateOrder(NotifyUpdateOrder in, Long sessionId);

    // @formatter:off
    /**
     * Loescht die zu dem Bauauftrag hinterlegte {@link WorkforceOrder}. <br/>
     * Von dem Verlauf wird dabei die protokollierte workforceOrderId entfernt.
     * @param bauauftrag
     */
    // @formatter:on
    void deleteOrder(Verlauf bauauftrag);

    /**
     * Adds the given {@code textInfo} to the FFM VerlaufAbteilung record identified by {@code workforceOrderId}
     * @param workforceOrderId
     * @param textInfo
     * @param occured
     */
    void textFeedback(String workforceOrderId, String textInfo, LocalDateTime occured);

    /**
     * Logs the given {@link NotifyOrderFeedback.Material} to the Workforce order
     *
     * @param workforceOrderId
     * @param material
     */
    void materialFeedback(String workforceOrderId, @NotNull NotifyOrderFeedback.Material material);

    // @formatter:off
    /**
     * Markiert das angegebene {@link FfmFeedbackMaterial} Objekt als 'prozessiert'. <br/>
     * Das bedeutet, dass diese Material-Rueckmeldung bereits per Mail an eine zustaendige Stelle geschickt wurde.
     * @param ffmFeedbackMaterial
     */
    // @formatter:on
    void markFfmFeedbackMaterialAsProcessed(@NotNull FfmFeedbackMaterial ffmFeedbackMaterial);

    // @formatter:off
    /**
     * Ermittelt alle {@link FfmFeedbackMaterial} Datensaetze, die noch nicht als 'prozessiert' markiert sind. <br/>
     * Zu jedem Feedback wird dann (gruppiert nach der zugehoerigen Taifun Auftragsnummer) eine eMail mit der
     * Material-Rueckmeldung generiert und in die {@link Mail} Tabelle eingetragen. <br/>
     */
    // @formatter:on
    void createMailsForFfmFeedbacks();

    /**
     * Ermittelt alle {@link FfmQualification}, die mit den technischen Leistungen verknüpft sind.
     * @param techLeistungen
     * @return
     */
    List<FfmQualification> getFfmQualifications(List<TechLeistung> techLeistungen);

    /**
     * Ermittelt alle {@link FfmQualification}, die mit dem Produkt aus den AuftragDaten verknüpft sind. <br/>
     * Sofern der Auftrag einem VPN zugeordnet ist, werden auch alle {@link FfmQualification}s ermittelt, die fuer VPN
     * konfiguriert sind.
     * @param auftragDaten
     * @return
     */
    List<FfmQualification> getFfmQualifications(AuftragDaten auftragDaten);

    /**
     * Ermittelt alle {@link FfmQualification}, die mit dem Standort verknüpft sind.
     * @param hvtStandort
     * @return
     */
    List<FfmQualification> getFfmQualifications(HVTStandort hvtStandort);
}
