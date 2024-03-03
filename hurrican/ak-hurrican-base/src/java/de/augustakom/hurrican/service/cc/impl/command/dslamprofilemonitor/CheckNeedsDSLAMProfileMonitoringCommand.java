/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 13:48:08
 */
package de.augustakom.hurrican.service.cc.impl.command.dslamprofilemonitor;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;

/**
 * Command das prueft, ob DSLAM-Ueberwachung fuer einen Auftrag notwendig ist. Dies ist dann der Fall, wenn folgende
 * Bedingungen erfuellt sind: <br> <ul> <li>Auftrag ist eine Neuschaltung <li>verwendeter Standort ist vom Typ HVT
 * <li>Downstream-Bandbreite betraegt 18.000 </ul>
 */
@CcTxRequired
public class CheckNeedsDSLAMProfileMonitoringCommand extends AbstractServiceCommand {

    public static final String CCAUFTRAG_ID = "auftrag.id";

    private Long ccAuftragId;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        ccAuftragId = (Long) getPreparedValue(CCAUFTRAG_ID);
        if (ccAuftragId == null) {
            throw new NullPointerException("Prepared Value 'CCAUFTRAG_ID' darf nicht null sein!");
        }
        return checkIsNeuschaltung(ccAuftragId) && checkIsStandortTypHVT(ccAuftragId)
                && checkIsDownstream18000(ccAuftragId);
    }

    boolean checkIsNeuschaltung(Long ccAuftragId) throws FindException, ServiceNotFoundException {
        return (checkIsAuftragInRealisierung(ccAuftragId) || checkIsRealDateToday(ccAuftragId));
    }

    boolean checkIsAuftragInRealisierung(Long ccAuftragId) throws FindException, ServiceNotFoundException {
        AuftragDaten auftragDaten = getCCService(CCAuftragService.class).findAuftragDatenByAuftragIdTx(ccAuftragId);
        if (auftragDaten == null) {
            throw new FindException(String.format("Es wurden keine AuftragDaten zum techn. Auftrag mit Id %d gefunden!", ccAuftragId));
        }
        return NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG);
    }

    boolean checkIsRealDateToday(Long ccAuftragId) throws FindException, ServiceNotFoundException {
        final Verlauf verlauf = getCCService(BAService.class).findActVerlauf4Auftrag(ccAuftragId, false);
        // @formatter:off
        return (verlauf != null)
                    ? verlauf.isNeuschaltungOrAnschlussuebernahme() && DateTools.isDateEqual(verlauf.getRealisierungstermin(), new Date())
                    : false;
        // @formatter:on
    }

    boolean checkIsStandortTypHVT(Long ccAuftragId) throws FindException, ServiceNotFoundException {
        final Endstelle endstelle = getCCService(EndstellenService.class).findEndstelle4Auftrag(ccAuftragId,
                Endstelle.ENDSTELLEN_TYP_B);
        if (endstelle == null) {
            return false;
        }

        final HVTStandort hvtStandort = getCCService(HVTService.class).findHVTStandort(endstelle.getHvtIdStandort());
        return (hvtStandort != null) ? hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_HVT) : false;
    }

    boolean checkIsDownstream18000(Long ccAuftragId) throws FindException, ServiceNotFoundException {
        final TechLeistung techLs = getCCService(CCLeistungsService.class).findTechLeistung4Auftrag(ccAuftragId,
                TechLeistung.TYP_DOWNSTREAM, new Date());
        return (techLs != null) ? NumberTools.equal(techLs.getLongValue(), Long.valueOf(18000L)) : false;
    }
}
