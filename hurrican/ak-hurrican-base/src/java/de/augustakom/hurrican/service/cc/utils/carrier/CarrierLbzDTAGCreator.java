/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2005 10:30:28
 */
package de.augustakom.hurrican.service.cc.utils.carrier;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Creator-Klasse, um die Basis-LBZ fuer eine Carrierbestellung beim Carrier DTAG zu erstellen. <br> Die erstellte
 * Basis-LBZ sieht wie folgt aus: /\d{5}/\d{5}/  <br> Beispiel: /82100/82100/  <br>
 *
 *
 */
public class CarrierLbzDTAGCreator {

    private static final Logger LOGGER = Logger.getLogger(CarrierLbzDTAGCreator.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;

    private String regExp = "/[1-9]\\d{4}/[1-9]\\d{4}/";

    public String createLbz(Long endstelleId) {
        try {
            Endstelle es = endstellenService.findEndstelle(endstelleId);
            if (es != null) {
                HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(es.getHvtIdStandort());
                if (hvtGruppe != null) {
                    String onkz = StringUtils.rightPad(hvtGruppe.getOnkzWithoutLeadingNulls(), 5, "0");
                    String delim = "/";

                    StringBuilder result = new StringBuilder(delim);
                    result.append(onkz);
                    result.append(delim);
                    result.append(onkz);
                    result.append(delim);

                    if (!result.toString().matches(regExp)) {
                        LOGGER.error("Erzeugter String stimmt nicht mit RegExp ueberein! String: " +
                                result.toString() + "  -  RegExp: " + regExp);
                        return null;
                    }

                    return result.toString();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}


