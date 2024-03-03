/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2015
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 *
 */
public class CheckRufnummerTALCommand extends AbstractTALCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckRufnummerTALCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;

    @Override
    public Object execute() throws Exception {
        try {
            Long auftragId = (Long) getPreparedValue(KEY_AUFTRAG_ID);
            AuftragDaten auftragDaten = getAuftragDatenTx(auftragId);
            Produkt produkt = produktService.findProdukt(auftragDaten.getProdId());

            List<Rufnummer> rufnummerList = rufnummerService.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                    new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.TRUE, Boolean.TRUE });

            if (BooleanTools.nullToFalse(produkt.needsDn()) && CollectionTools.isEmpty(rufnummerList)) {
                LOGGER.warn(String.format("Es sind keine Rufnummern zum Auftrag %s hinterlegt - "
                        + "el. TAL-Bestellung wird dennoch zugelassen!", auftragId));
            }

            for (Rufnummer rufnummer : rufnummerList) {
                if (Rufnummer.PORT_MODE_KOMMEND.equals(rufnummer.getPortMode()) &&
                        rufnummer.getRufnummerLength() > 12) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            String.format("Die Rufnummer '%s' ist zu lang und fuer die el. TAL-Bestellung nicht zugelassen!", rufnummer.getRufnummer()), getClass());
                }
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Fehler bei der Ueberpruefung der Rufnummern für die TAL-Bestellung: " + e.getMessage(), getClass());
        }
    }
}
