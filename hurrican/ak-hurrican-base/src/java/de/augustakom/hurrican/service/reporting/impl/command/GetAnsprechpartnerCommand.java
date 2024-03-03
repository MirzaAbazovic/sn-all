/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 11:08:28
 */

package de.augustakom.hurrican.service.reporting.impl.command;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;


/**
 * Command-Klasse um die Ansprechpartner-Daten zu einem Auftrag bereitzustellen.
 *
 *
 */
public class GetAnsprechpartnerCommand extends AbstractReportCommand {
    private static final Logger LOGGER = Logger.getLogger(GetAnsprechpartnerCommand.class);

    public static final String AP_TECHNISCHER_SERVICE = "technService";
    public static final String AP_TECHNISCHER_SERVICE_TEL = "technServiceTel";
    public static final String AP_TECHNISCHER_SERVICE_HANDY = "technServiceHandy";

    public static final String AP_VOR_ORT = "vorOrt";
    public static final String AP_VOR_ORT_TEL = "vorOrtTel";
    public static final String AP_VOR_ORT_HANDY = "vorOrtHandy";

    private Long auftragId;
    private Map<String, Object> map;

    @Override
    public Object execute() throws Exception {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<String, Object>();
            readAnsprechpartnerDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    private void checkValues() throws HurricanServiceCommandException {
        Object tempId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tempId instanceof Long) ? (Long) tempId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("Auftrag-ID wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    private void readAnsprechpartnerDaten() throws ServiceNotFoundException {
        AnsprechpartnerService as = getCCService(AnsprechpartnerService.class);
        Ansprechpartner preferredTechnischerAnsprechpartner = null;
        try {
            preferredTechnischerAnsprechpartner = as.findPreferredAnsprechpartner(TECH_SERVICE, auftragId);
        }
        catch (FindException e) {
            LOGGER.error("Exception trying to find preferred Ansprechpartner", e);
        }
        if ((preferredTechnischerAnsprechpartner != null) && (preferredTechnischerAnsprechpartner.getAddress() != null)) {
            map.put(getPropName(AP_TECHNISCHER_SERVICE), preferredTechnischerAnsprechpartner.getAddress().getCombinedNameData());
            map.put(getPropName(AP_TECHNISCHER_SERVICE_TEL), preferredTechnischerAnsprechpartner.getAddress().getTelefon());
            map.put(getPropName(AP_TECHNISCHER_SERVICE_HANDY), preferredTechnischerAnsprechpartner.getAddress().getHandy());
        }
        else {
            map.put(getPropName(AP_TECHNISCHER_SERVICE), null);
            map.put(getPropName(AP_TECHNISCHER_SERVICE_TEL), null);
            map.put(getPropName(AP_TECHNISCHER_SERVICE_HANDY), null);
        }

        Ansprechpartner preferredVorOrtAnsprechpartner = null;
        try {
            preferredVorOrtAnsprechpartner = as.findPreferredAnsprechpartner(VOR_ORT, auftragId);
        }
        catch (FindException e) {
            LOGGER.error("Exception trying to find preferred Ansprechpartner", e);
        }
        if ((preferredVorOrtAnsprechpartner != null) && (preferredVorOrtAnsprechpartner.getAddress() != null)) {
            map.put(getPropName(AP_VOR_ORT), preferredVorOrtAnsprechpartner.getAddress().getCombinedNameData());
            map.put(getPropName(AP_VOR_ORT_TEL), preferredVorOrtAnsprechpartner.getAddress().getTelefon());
            map.put(getPropName(AP_VOR_ORT_HANDY), preferredVorOrtAnsprechpartner.getAddress().getHandy());
        }
        else {
            map.put(getPropName(AP_VOR_ORT), null);
            map.put(getPropName(AP_VOR_ORT_TEL), null);
            map.put(getPropName(AP_VOR_ORT_HANDY), null);
        }
    }

    @Override
    public String getPrefix() {
        return ANSPRECHPARTNER;
    }

    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetAnsprechpartnerCommand.properties";
    }

}
