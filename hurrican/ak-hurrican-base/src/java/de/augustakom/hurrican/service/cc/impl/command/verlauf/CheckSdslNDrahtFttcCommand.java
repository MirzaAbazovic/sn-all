/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.03.2015
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command-Klasse prueft, ob es sich um die Bandbreite 50/50 MBit auf einem FTTC-Standort handelt. <br/>
 * Falls ja, dann wird geprueft, ob es zu dem aktuellen Taifun-Auftrag noch einen Hurrican n-Draht Auftrag
 * {@link Produkt#PROD_ID_SDSL_N_DRAHT_FTTC_OPTION} gibt.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckSdslNDrahtFttcCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckSdslNDrahtFttcCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckSdslNDrahtFttcCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Override
    public Object execute() throws Exception {
        try {
            AuftragDaten auftragDaten = getAuftragDatenTx(getAuftragId());
            Produkt produkt = produktService.findProdukt(auftragDaten.getProdId());
            if (produkt == null) {
                throw new FindException(String.format("Für den Auftrag %s konnte das Produkt mit der ID %s "
                                + "nicht ermittelt werden!",
                        auftragDaten.getAuftragId(), auftragDaten.getProdId()));
            }

            if (isFttc() && isNDrahtNecessary(produkt)) {
                List<AuftragDaten> auftragDatenList =
                        auftragService.findAuftragDaten4OrderNoOrigTx(auftragDaten.getAuftragNoOrig());
                List<AuftragDaten> activeOrdersWithRangierung = auftragDatenList.stream()
                        .filter(a -> a.isAuftragActive())
                        .filter(a -> hasRangierung(a.getAuftragId()))
                        .collect(Collectors.toList());

                if (activeOrdersWithRangierung.size() < produkt.getSdslNdraht().anzahlAuftraege) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            String.format(
                                    "Es muessen %s Hurrican Aufträge mit Rangierung vorhanden sein, um die "
                                            + "Bandbreite auf dieser Technologie zu realisieren!",
                                    produkt.getSdslNdraht().anzahlAuftraege),
                            getClass());
                }
                else if (hasRangierungOnDifferentBgs(activeOrdersWithRangierung)) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            "Die Ports der Aufträge sind auf unterschiedlichen Baugruppen. "
                                        + "Dies ist für n-Draht nicht zulässig!",
                            getClass());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der n-Draht Option ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }


    boolean hasRangierungOnDifferentBgs(List<AuftragDaten> auftraege) throws FindException {
        Set<Long> bgIds = new HashSet<>();
        for (AuftragDaten auftragDaten : auftraege) {
            Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(
                    auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            if (endstelleB != null && endstelleB.getRangierId() != null) {
                Rangierung rangierung = rangierungsService.findRangierung(endstelleB.getRangierId());
                if (rangierung.getEqInId() != null) {
                    Equipment equipment = rangierungsService.findEquipment(rangierung.getEqInId());
                    if (equipment != null && equipment.getHwBaugruppenId() != null) {
                        bgIds.add(equipment.getHwBaugruppenId());
                    }
                }
            }
        }

        return bgIds.size() != 1;
    }


    boolean isFttc() throws FindException, HurricanServiceCommandException {
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        if (endstelleB == null) {
            throw new HurricanServiceCommandException("Endstelle B des Auftrags konnte nicht ermittelt werden!");
        }

        if (endstelleB.getHvtIdStandort() != null) {
            HVTStandort hvtStandort = hvtService.findHVTStandort(endstelleB.getHvtIdStandort());
            return (hvtStandort != null && hvtStandort.isFttc());
        }

        return false;
    }


    /**
     * Prueft, ob das Produkt fuer SDSL n-Draht geflaggt ist und eine Bandbreite von min. 50 MBit Down- und Upstream
     * gebucht ist
     * @param produkt
     * @return
     */
    boolean isNDrahtNecessary(Produkt produkt) throws FindException {
        if (produkt.getSdslNdraht() != null) {
            List<TechLeistung> downstream = leistungsService.findTechLeistungen4Auftrag(getAuftragId(),
                    TechLeistung.TYP_DOWNSTREAM, getRealDate());
            List<TechLeistung> upstream = leistungsService.findTechLeistungen4Auftrag(getAuftragId(),
                    TechLeistung.TYP_UPSTREAM, getRealDate());

            return hasAtLeast50Mbit(downstream) && hasAtLeast50Mbit(upstream);
        }
        return false;
    }

    boolean hasAtLeast50Mbit(List<TechLeistung> toFilter) {
        return toFilter.stream()
                .filter(d -> NumberTools.isGreaterOrEqual(d.getIntegerValue(), 50000))
                .findFirst().isPresent();
    }

    boolean hasRangierung(Long auftragId) {
        try {
            return rangierungsService.hasRangierung(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        }
        catch (FindException e) {
            return false;
        }
    }

}
