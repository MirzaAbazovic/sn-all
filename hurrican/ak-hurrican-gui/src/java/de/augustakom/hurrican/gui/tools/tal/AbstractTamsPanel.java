/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2016
 */
package de.augustakom.hurrican.gui.tools.tal;

import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.model.CbTask;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;

abstract class AbstractTamsPanel<T extends CbTask> extends AbstractCbTaskListPanel<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractCbTaskListPanel.class);
    protected static final String STATUS_COLUMN_NAME = "Status";
    protected static final String TAM_ART_COLUMN_NAME = "TAM Art";


    static final String TAM_STATUS = "tam.status";
    static final String TAM_STATUS_FILTER = "tam.status.filter";
    static final String BEMERKUNGEN = "bemerkungen";
    static final String STATUS = "status";
    static final String ES_HVT = "es.hvt";
    static final String ES_PLZ_ORT = "es.plz.ort";
    static final String ES_ORT = "es.ort";
    static final String ES_PLZ = "es.plz";
    static final String ES_STRASSE = "es.strasse";
    static final String ES_NAME = "es.name";
    static final String ENDSTELLE = "endstelle";
    static final String KUNDE_EMAIL = "kunde.email";
    static final String KUNDE_VOIPNUMMER = "kunde.voipnummer";
    static final String KUNDE_MOBILNUMMER = "kunde.mobilnummer";
    static final String KUNDE_PRIVATNUMMER = "kunde.privatnummer";
    static final String KUNDE_GESCHAEFTSNUMMER = "kunde.geschaeftsnummer";
    static final String KUNDE_HAUPTRUFNUMMER = "kunde.hauptrufnummer";
    static final String KONTAKTDATEN = "kontaktdaten";
    static final String KUNDE_VORNAME = "kunde.vorname";
    static final String KUNDE_NAME = "kunde.name";
    static final String KUNDE = "kunde";

    static final String SAVE_TAM_USER_TASK = "save.tam.user.task";
    static final String CLOSE_TAM_USER_TASK = "tam.ta.task.schliessen";
    static final String STORNO = "storno";
    static final String TERMINVERSCHIEBUNG = "tv";
    static final String TV_60TAGE = "60tage";
    static final String ERLMK = "erlmk";

    static final String LAST_CHANGE_COLUMN_NAME = "Letzte Änderung";
    static final String CARRIER_COLUMN_NAME = "Carrier";
    static final String EIGENE_TASKS_FILTER_NAME = "eigene.tasks.filter";
    static final String TASK_BEARBEITER_COLUMN_NAME = "Task-Bearb.";
    static final String BEARBEITER_COLUMN_NAME = "Bearbeiter";
    static final String KLAERFALL_COLUMN_NAME = "Klärfall";

    // Services
    CarrierService carrierService;
    WitaUsertaskService witaUsertaskService;
    CarrierElTALService carrierElTalService;
    WitaTalOrderService witaTalOrderService;
    WitaConfigService witaConfigService;


    AbstractTamsPanel(String resource) {
        super(resource);
    }

    void initServices() {
        try {
            carrierService = getCCService(CarrierService.class);
            witaUsertaskService = getCCService(WitaUsertaskService.class);
            carrierElTalService = getCCService(CarrierElTALService.class);
            witaTalOrderService = getCCService(WitaTalOrderService.class);
            witaConfigService = getCCService(WitaConfigService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


}

