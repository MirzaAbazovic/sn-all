/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.tools.tal;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.view.CBVorgangView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 *
 */
public class AssignEquipmentHelper {

    /**
     * check FTTH
     */
    public static boolean isFTTH(final Long hvtStandortId) throws ServiceNotFoundException, FindException {
        return HVTStandort.HVT_STANDORT_TYP_FTTH.equals(getHvtStandortTypRefId(hvtStandortId));
    }

    /**
     * check Gewofag
     */
    public static boolean isGewofag(final Long hvtStandortId) throws ServiceNotFoundException, FindException {
        return HVTStandort.HVT_STANDORT_TYP_GEWOFAG.equals(getHvtStandortTypRefId(hvtStandortId));
    }

    /**
     * check "Port zuweisen"
     *
     * @throws ServiceNotFoundException
     * @throws FindException
     * @throws HurricanGUIException
     */
    public static void checkPortEQin(final CBVorgangView actCBVorgangView) throws ServiceNotFoundException, FindException, HurricanGUIException {
        final RangierungsService rs = CCServiceFinder.instance().getCCService(RangierungsService.class);
        final Rangierung[] rangs = rs.findRangierungen(actCBVorgangView.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);

        if (rangs == null) {
            return;
        }

        final Rangierung rangierung = rangs[0];

        if ((rangierung != null) && ((null != rangierung.getEqInId()) && (rangierung.getEqInId() > 0)) && isFTTH(actCBVorgangView.getHvtStandortId())) {
            throw new HurricanGUIException("Es existiert bereits eine Rangierung mit EQ_IN.");
        }
    }

    private static Long getHvtStandortTypRefId(final Long hvtStandortId) throws ServiceNotFoundException, FindException {
        final HVTService hvtService = CCServiceFinder.instance().getCCService(HVTService.class);
        final HVTStandort hvtStandOrt = hvtService.findHVTStandort(hvtStandortId);
        if (hvtStandOrt == null) {
            throw new FindException("HvtStandort nicht gefunden.");
        }
        final Long hvtStandOrtTypRefId = hvtStandOrt.getStandortTypRefId();
        return hvtStandOrtTypRefId;
    }
}
