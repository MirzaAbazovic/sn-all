/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 08:54:33
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.aggregator.execution.WitaDataAggregationExecuter.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wita.config.WitaConstants;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.impl.DateTimeCalculationService;

/**
 * Aggregator-Klasse, um ein Objekt des Typs {@link Kundenwunschtermin} zu generieren.
 */
public class KundenwunschterminAggregator extends AbstractWitaDataAggregator<Kundenwunschtermin> {

    @Resource(name = "de.mnet.wita.aggregator.ZeitfensterAggregator")
    ZeitfensterAggregator zeitfensterAggregator;

    @Autowired
    DateTimeCalculationService dateTimeCalculationService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    CarrierElTALService carrierElTALService;

    @Autowired
    WbciCommonService wbciCommonService;

    @Override
    public Kundenwunschtermin aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        if (cbVorgang == null) {
            throw new WitaDataAggregationException(
                    "Termin-Angabe konnte nicht erstellt werden, da TAL-Vorgang nicht angegeben!");
        }

        LocalDateTime vorgabe = Instant.ofEpochMilli(cbVorgang.getVorgabeMnet().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                
        // WBCI-Integration: bei KUE-KD mit referenzierter Vorabstimmung wird das angegebene
        // Vorgabedatum automatisch um einen in der DB konfigurierten Offset erhoeht (Stichwort 'sicherer Hafen' im 
        // WBCI Kontext).
        if (cbVorgang.isKuendigung() && cbVorgang.getVorabstimmungsId() != null) {
            vorgabe = addOffset(cbVorgang.getVorgabeMnet());
            
            try {
                cbVorgang.setVorgabeMnet(Date.from(vorgabe.atZone(ZoneId.systemDefault()).toInstant()));
                carrierElTALService.saveCBVorgang(cbVorgang);
            }
            catch (StoreException e) {
                throw new WitaDataAggregationException(
                        String.format(
                                "Fehler bei der Aktualisierung des Vorgabedatums des CBVorgangs mit Id %s auf %s", 
                                cbVorgang.getId(), DateTools.formatDate(Date.from(vorgabe.atZone(ZoneId.systemDefault()).toInstant()), DateTools.PATTERN_DAY_MONTH_YEAR)),
                        e);
            }
        }

        checkKundenwunschtermin(Date.from(vorgabe.atZone(ZoneId.systemDefault()).toInstant()), cbVorgang.getWitaGeschaeftsfallTyp(),
                cbVorgang.getVorabstimmungsId(), cbVorgang.isHvtToKvz());

        Kundenwunschtermin termin = new Kundenwunschtermin();
        termin.setDatum(vorgabe.toLocalDate());

        executeAggregator(cbVorgang, zeitfensterAggregator, termin);
        return termin;
    }

    /**
     * Prueft, ob bei dem Kundenwunschtermin die Mindestvorlaufzeit eingehalten wurde und dieser auf einem Arbeitstag
     * (Mo-Fr) liegt.
     *
     * @throws WitaDataAggregationException wenn der Kundenwunschtermin ungültig ist
     */
    void checkKundenwunschtermin(Date kundenwunschtermin, GeschaeftsfallTyp geschaeftsfallTyp,
            String vorabstimmungsId, boolean isHvtToKvz) throws WitaDataAggregationException {
        if (!dateTimeCalculationService.isKundenwunschTerminValid(LocalDateTime.now(), Instant.ofEpochMilli(kundenwunschtermin.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                false, false, geschaeftsfallTyp, vorabstimmungsId, isHvtToKvz)) {
            throw new WitaDataAggregationException(String.format(
                    "Der Kundenwunschtermin für die Bestellung ist ungültig.%nEntweder wurde Mindestvorlaufzeit von "
                            + "%s Arbeitstagen für den WITA-Vorgang nicht eingehalten oder der "
                            + "Kundenwunschtermin liegt auf einem Samstag, Sonntag oder Feiertag.%n"
                            + "Bestellung kann somit nicht ausgeführt werden!", WitaConstants.MINDESTVORLAUFZEIT
            ));
        }
    }
    
    LocalDateTime addOffset(Date baseDate) {
        int offset = witaConfigService.getWbciWitaKuendigungsOffset();
        LocalDateTime kuendigungPlusOffset =
                DateCalculationHelper.addWorkingDays(DateConverterUtils.asLocalDate(baseDate), offset).atStartOfDay();
        while (!DateCalculationHelper.isWorkingDayAndNextDayNotHoliday(kuendigungPlusOffset.toLocalDate())) {
            kuendigungPlusOffset =
                    DateCalculationHelper.addWorkingDays(kuendigungPlusOffset.toLocalDate(), 1).atStartOfDay();
        }

        return kuendigungPlusOffset;
    }

}
