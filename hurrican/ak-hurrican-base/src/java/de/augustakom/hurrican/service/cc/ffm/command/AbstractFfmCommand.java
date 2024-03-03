/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmQualification;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Abstrakte Klasse fuer alle FFM Aggregator Commands. <br/> Die einzelnen Ableitungen dieser Klasse sind selbst dafuer
 * verantwortlich, dass sie ihre aggregierten Daten in das {@link WorkforceOrder} Objekt eintragen. <br/> Der Client /
 * Aufrufer der Commands ist fuer die Reihenfolge der Aufrufe zustaendig, sofern dies auf Grund der Command-Aufteilung
 * notwendig ist.
 */
// @formatter:on
public abstract class AbstractFfmCommand extends AbstractServiceCommand {

    /**
     * Key fuer die {@link AbstractServiceCommand#prepare(String, Object)} Methode, um dem Command das zu befuellende
     * {@link WorkforceOrder} Objekt zu uebergeben.
     */
    public static final String KEY_WORKFORCE_ORDER = "ffm.workforce.order";

    /**
     * Key fuer die {@link AbstractServiceCommand#prepare(String, Object)} Methode, um dem Command das zu verwendende
     * Referenz-Datum (z.B. fuer Abfrage der Rufnummern oder techn. Leistungen) zu uebergeben.
     */
    public static final String KEY_REFERENCE_DATE = "reference.date";

    /**
     * Key fuer die {@link AbstractServiceCommand#prepare(String, Object)} Methode, um dem Command die ID des
     * betroffenen Hurrican-Auftrags zu uebergeben.
     */
    public static final String KEY_AUFTRAG_ID = "hurrican.auftrag.id";

    /**
     * Key fuer die {@link AbstractServiceCommand#prepare(String, Object)} Methode, um dem Command eine Liste mit
     * Hurrican Auftrag-IDs zu uebergeben, die als Sub-Orders einem Bauauftrag zugeordnet sind.
     */
    public static final String KEY_SUB_ORDERS = "hurrican.bauauftrag.sub.orders";

    /**
     * Key fuer die {@link AbstractServiceCommand#prepare(String, Object)} Methode, um dem Command den (Hurrican)
     * Bauauftrag {@link Verlauf} zu uebergeben, zu dem die {@link WorkforceOrder} erstellt werden soll.
     */
    public static final String KEY_VERLAUF = "hurrican.verlauf";

    /**
     * Key fuer die {@link AbstractServiceCommand#prepare(String, Object)} Methode, um dem Command den (Hurrican)
     * BauauftragAnlass {@link BAVerlaufAnlass} zu uebergeben, der einem Bauauftrag zugeordnet ist.
     */
    public static final String KEY_VERLAUF_ANLASS = "hurrican.verlauf.anlass";

    /**
     * Key fuer die Uebergabe des {@link FfmProductMapping} Objekts an das Command, ueber das div. FFM-Parameter fuer
     * den aktuellen Auftrag festgelegt sind.
     */
    public static final String KEY_FFM_PRODUCT_MAPPING = "ffm.product.mapping";

    /**
     * Key fuer die Uebergabe des {@link HVTStandort} Objekts an das Command, ueber das div. FFM-Parameter fuer
     * den aktuellen Auftrag festgelegt sind.
     */
    public static final String KEY_HVT_STANDORT = "hurrican.hvt.standort";

    /**
     * Key fuer die Uebergabe des {@link HVTStandort} Objekts an das Command, ueber das div. FFM-Parameter fuer
     * den aktuellen Auftrag festgelegt sind.
     */
    public static final String KEY_AUFTRAG_DATEN = "hurrican.auftrag.daten";

    /**
     * Key fuer die Uebergabe der Liste von {@link TechLeistung} Objekten an das Command.
     */
    public static final String KEY_TECH_LEISTUNGEN = "hurrican.tech.leistungen";

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    protected CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    protected EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ffm.FFMService")
    protected FFMService ffmService;

    /**
     * Gibt das ueber {@link IServiceCommand#prepare(String, Object)} definierte {@link WorkforceOrder} Objekt zurueck.
     *
     * @return
     */
    protected WorkforceOrder getWorkforceOrder() {
        Object workforceOrder = getPreparedValue(KEY_WORKFORCE_ORDER);
        if (workforceOrder instanceof WorkforceOrder) {
            return (WorkforceOrder) workforceOrder;
        }
        throw new FFMServiceException(
                "Command-Klasse nicht korrekt konfiguriert - WorkforceOrder ist nicht uebergeben!");
    }

    /**
     * Prueft, ob in der {@link WorkforceOrder} ein {@link OrderTechnicalParams} Objekt hinterlegt ist. <br/> Ist dies
     * nicht der Fall, wird eine FfmServiceException generiert.
     */
    protected void checkThatWorkforceOrderHasTechnicalParams() {
        if (getWorkforceOrder() == null
                || getWorkforceOrder().getDescription() == null
                || getWorkforceOrder().getDescription().getTechParams() == null) {
            throw new FFMServiceException("WorkforceOrder ist nicht definiert oder besitzt keine technischen Parameter!");
        }
    }

    /**
     * Gibt das ueber {@link IServiceCommand#prepare(String, Object)} definierte {@link Verlauf} Objekt zurueck.
     *
     * @return
     */
    protected Optional<Verlauf> getBauauftrag() {
        Object verlauf = getPreparedValue(KEY_VERLAUF);
        if (verlauf instanceof Verlauf) {
            return Optional.of((Verlauf) verlauf);
        }
        return Optional.empty();
    }

    /**
     * Gibt das ueber {@link IServiceCommand#prepare(String, Object)} definierte {@link Verlauf} Objekt zurueck.
     *
     * @return
     */
    protected Optional<BAVerlaufAnlass> getBauauftragAnlass() {
        Object verlaufAnlass = getPreparedValue(KEY_VERLAUF_ANLASS);
        if (verlaufAnlass instanceof BAVerlaufAnlass) {
            return Optional.of((BAVerlaufAnlass) verlaufAnlass);
        }
        return Optional.empty();
    }

    /**
     * Gibt das Datum zurueck, das fuer die Datenermittlung relevant ist.
     * @return
     */
    protected
    @Nonnull
    LocalDateTime getReferenceDate() {
        Object refDate = getPreparedValue(KEY_REFERENCE_DATE);
        if (refDate instanceof LocalDateTime) {
            return (LocalDateTime) refDate;
        }
        throw new FFMServiceException(
                "Command-Klasse nicht korrekt konfiguriert - Referenz-Datum für FFM-Datenermittlung ist nicht angegeben!");
    }

    /**
     * Gibt das ueber {@link IServiceCommand#prepare(String, Object)} definierte {@link FfmProductMapping} Objekt
     * zurueck.
     * @return
     */
    protected FfmProductMapping getFfmProductMapping() {
        Object ffmProductMapping = getPreparedValue(KEY_FFM_PRODUCT_MAPPING);
        if (ffmProductMapping instanceof FfmProductMapping) {
            return (FfmProductMapping) ffmProductMapping;
        }
        throw new FFMServiceException(
                "Command-Klasse nicht korrekt konfiguriert - FfmProductMapping ist nicht uebergeben!");
    }

    /**
     * Gibt das ueber {@link IServiceCommand#prepare(String, Object)} definierte {@link HVTStandort} Objekt
     * zurueck.
     * @return
     */
    protected HVTStandort getHvtStandort() {
        Object hvtStandort = getPreparedValue(KEY_HVT_STANDORT);
        if (hvtStandort instanceof HVTStandort) {
            return (HVTStandort) hvtStandort;
        }

        return null;
    }

    /**
     * Gibt das ueber {@link IServiceCommand#prepare(String, Object)} definierte {@link AuftragDaten} Objekt
     * zurueck.
     * @return
     */
    protected AuftragDaten getAuftragDaten() {
        Object auftragDaten = getPreparedValue(KEY_AUFTRAG_DATEN);
        if (auftragDaten instanceof AuftragDaten) {
            return (AuftragDaten) auftragDaten;
        }

        throw new FFMServiceException(
                "Command-Klasse nicht korrekt konfiguriert - AuftragDaten ist nicht uebergeben!");
    }

    /**
     * Gibt das ueber {@link IServiceCommand#prepare(String, Object)} definierte {@link FfmProductMapping} Objekt
     * zurueck.
     *
     * @return
     */
    protected List<TechLeistung> getTechLeistungen() {
        Object leistungen = getPreparedValue(KEY_TECH_LEISTUNGEN);
        if (leistungen instanceof List) {
            return (List) leistungen;
        }
        throw new FFMServiceException(
                "Command-Klasse nicht korrekt konfiguriert - Liste der technischen Leistungen ist nicht uebergeben!");
    }

    /**
     * Calculates planned duration which is basically the duration defined in product mapping plus
     * optional ffm qualification durations. <br/>
     * The additional durations from the loaded qualifications are only added, if the FFM type is not
     * 'ENTSTOERUNG' or 'KUENDIGUNG'.
     * @return
     */
    protected int getPlannedDuration() {
        FfmProductMapping ffmProductMapping = getFfmProductMapping();
        int plannedDuration = ffmProductMapping.getFfmPlannedDuration();

        if (!FfmTyp.ENTSTOERUNG.equals(ffmProductMapping.getBaFfmTyp())
                && !FfmTyp.KUENDIGUNG.equals(ffmProductMapping.getBaFfmTyp())) {

            Set<FfmQualification> ffmQualifications = new HashSet<>();

            AuftragDaten auftragDaten = getAuftragDaten();
            ffmQualifications.addAll(ffmService.getFfmQualifications(auftragDaten));

            HVTStandort hvtStandort = getHvtStandort();
            if (hvtStandort != null) {
                ffmQualifications.addAll(ffmService.getFfmQualifications(hvtStandort));
            }

            if (!CollectionUtils.isEmpty(getTechLeistungen())) {
                ffmQualifications.addAll(ffmService.getFfmQualifications(getTechLeistungen()));
            }

            for (FfmQualification ffmQualification : ffmQualifications) {
                if (ffmQualification.getAdditionalDuration() != null) {
                    plannedDuration += ffmQualification.getAdditionalDuration();
                }
            }
        }

        return plannedDuration;
    }

    protected Long getAuftragId() throws FindException {
        return getPreparedValue(KEY_AUFTRAG_ID, Long.class, false,
                "Command-Klasse nicht korrekt konfiguriert - Auftrags-Id ist nicht angegeben!");
    }

    /**
     * Gibt alle Auftrag-IDs zurueck, die dem Command uebergeben wurden. <br/>
     * Dies ist dann ein Set aus KEY_AUFTRAG_ID und KEY_SUB_ORDERS
     * @return
     * @throws FindException
     */
    protected Set<Long> getAllAuftragIds() throws FindException {
        Set<Long> allOrderIds = new HashSet<>();
        allOrderIds.add(getAuftragId());

        Object subOrders = getPreparedValue(KEY_SUB_ORDERS);
        if (subOrders instanceof Collection) {
            allOrderIds.addAll((Collection) subOrders);
        }

        return allOrderIds;
    }

    /**
     * Ermittelt aus dem gesetzten Bauauftrag (bzw. dessen verlinkten {@link AuftragDaten}) die Endstelle B des
     * Auftrags.
     *
     * @return
     * @throws FindException
     */
    protected Endstelle getEndstelleB(boolean throwExceptionIfNotFound) throws FindException {
        Endstelle endstelle =
                endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        if (throwExceptionIfNotFound && endstelle == null) {
            throw new FFMServiceException(
                    String.format("Endstelle B zum Auftrag %s nicht gefunden!", getAuftragId()));
        }
        return endstelle;
    }
}
