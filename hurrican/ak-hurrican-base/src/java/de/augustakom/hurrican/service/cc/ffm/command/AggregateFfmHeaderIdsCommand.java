/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.math.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmQualification;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'Header' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert: <br/>
 * <ul>
 *   <li>IDs (UniqueId, DisplayId, CustomerOrderId</li>
 *   <li>FFM Aktivitaets-Typ sowie notwendige Qualifikationen</li>
 *   <li>Produktname</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderIdsCommand")
@Scope("prototype")
public class AggregateFfmHeaderIdsCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmHeaderIdsCommand.class);

    /* Pattern fuer die FFM Unique-Id in Form HUR_<UUID> */
    private static final String UNIQUE_ID_PATTERN = "HUR_%s";
    /* Pattern fuer eine FFM Display-Id */
    private static final String DISPLAY_ID_PATTERN = "%s-%s-%s";

    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.billing.OEService")
    private OEService oeService;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAConfigService")
    private BAConfigService baConfigService;

    @Override
    public Object execute() throws Exception {
        try {
            WorkforceOrder order = getWorkforceOrder();

            order.setId(createFfmUniqueId());
            order.setDisplayId(createFfmDisplayId());
            order.setCustomerOrderId(getCustomerOrder());

            // aus Konfiguration
            FfmProductMapping ffmProductMapping = getFfmProductMapping();
            order.setActivityType(ffmProductMapping.getFfmActivityType());
            Optional<BAVerlaufAnlass> bauauftragAnlass = getBauauftragAnlass();
            if (bauauftragAnlass.isPresent()) {
                order.setActivitySubtype(bauauftragAnlass.get().getName());
            }
            order.setPlannedDuration(BigInteger.valueOf(getPlannedDuration()));

            aggregateQualifications(order);

            // Produktname
            order.setType(getProduktName());

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM Header Id Data: " + e.getMessage(), this.getClass());
        }
    }

    private void aggregateQualifications(WorkforceOrder order) {
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

        // Qualifications sortieren, um Acc-Tests stabil zu halten
        List<FfmQualification> ffmQualificationsSorted =
                ffmQualifications.stream().sorted(new Comparator<FfmQualification>() {
                    @Override
                    public int compare(FfmQualification o1, FfmQualification o2) {
                        return o1.getQualification().compareTo(o2.getQualification());
                    }}).collect(Collectors.toList());

        for (FfmQualification ffmQualification : ffmQualificationsSorted) {
            order.getQualification().add(ffmQualification.getQualification());
        }
    }

    // @formatter:off
    /**
     * Generiert eine Display-Id fuer eine neue {@link WorkforceOrder}. <br/>
     * <p/>
     * Die Id setzt sich wie folgt zusammen: <b>TaifunId-Datum-Zeit</b> <br/>
     * <br/>
     * Falls der Hurrican-Auftrag keine Taifun-Referenz besitzt wird folgendes Schema verwendet: <br/>
     * <b>VBZ-Datum-Zeit</b> (wobei VBZ die Verbindungsbezeichnung ist).
     * Die Zeit wird jeweils bis auf Millisekunden definiert.
     * <br/><p/>
     * Beispiele: <br/>
     * mit Taifun-Id: 123456-2014-05-09-08:30:34.344 <br/>
     * mit VBZ:  IA245003-2014-05-09-08:30:34.344 <br/>
     *
     * @return die eindeutige Id
     */
    // @formatter:on
    String createFfmDisplayId() throws FindException {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"));

        String customerOrderId = getCustomerOrder();
        return String.format(DISPLAY_ID_PATTERN, customerOrderId, date, time);
    }

    // @formatter:off
    /**
     * Ermittelt die 'CustomerOrderId' zu dem aktuellen Auftrag. <br/>
     * Sofern der verlinkte Hurrican-Auftrag eine Taifun-Referenz besitzt wird die Taifun-Auftrags-Id zurueck gegeben;
     * ansonsten die Verbindungsbezeichnung (VBZ) des Auftrags.
     *
     * @return
     */
    // @formatter:on
    String getCustomerOrder() throws FindException {
        String customerOrderId = null;
        AuftragDaten auftragDaten = getAuftragDaten();
        if (auftragDaten != null) {
            if (auftragDaten.getAuftragNoOrig() != null) {
                customerOrderId = auftragDaten.getAuftragNoOrig().toString();
            }
            else {
                VerbindungsBezeichnung vbz =
                        physikService.findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
                if (vbz != null) {
                    customerOrderId = vbz.getVbz();
                }
            }
        }

        if (StringUtils.isBlank(customerOrderId)) {
            throw new FindException(
                    "CustomerOrderId für FFM konnte nicht generiert werden, da zum Bauauftrag weder ein Taifun-Auftrag noch eine VBZ ermittelt werden konnte.");
        }
        return customerOrderId;
    }

    // @formatter:off
    /**
     * Ermittelt den Produktnamen des aktuellen Auftrags. <br/>
     * Sofern ein Taifun-Auftrag verlinkt ist, wird das Taifun-Produkt ermittelt; ansonsten das Hurrican-Produkt.
     * @return
     */
    // @formatter:on
    String getProduktName() throws FindException {
        AuftragDaten auftragDaten = getAuftragDaten();

        String produktName = null;
        if (auftragDaten.getAuftragNoOrig() != null) {
            produktName = oeService.findProduktName4Auftrag(auftragDaten.getAuftragNoOrig());
        }

        if (StringUtils.isBlank(produktName)) {
            produktName = produktService.findProdukt4Auftrag(auftragDaten.getAuftragId()).getAnschlussart();
        }
        return produktName;
    }

    // @formatter:off
    /**
     * Generiert eine fuer FFM eindeutige Id im Format HUR_<UUID>. <br/>
     * Der Prefix 'HUR' ist notwendig, um das Routing im ESB zu realisieren. Die UUID ist eine zufaellig generierte Id.
     * Die Gesamt-Laenge der generierten Id wird auf 100 Zeichen begrenzt.
     *
     * @return
     */
    // @formatter:on
    String createFfmUniqueId() {
        String uuid = String.format(UNIQUE_ID_PATTERN, UUID.randomUUID().toString());
        return StringUtils.substring(uuid, 0, 99);
    }

}
