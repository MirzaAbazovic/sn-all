package de.augustakom.hurrican.gui.tools.command.actions;

import java.awt.event.*;
import java.util.*;

import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.service.cc.AvailabilityService;


public class ImportKVZGeoIdAction extends AbstractServiceAction implements GeoIdImportAction {

    protected final CommonImportGeoIdAction commonImportGeoIdAction;

    protected AvailabilityService availabilityService;

    public ImportKVZGeoIdAction() throws Exception {
        commonImportGeoIdAction = new CommonImportGeoIdAction();
        commonImportGeoIdAction.currentAction = this;
        commonImportGeoIdAction.CONFIRM_DIALOG_MESSAGE = "Wollen Sie den Import ausführen?";
        commonImportGeoIdAction.CONFIRM_DIALOG_TITLE = "Import von Straßendaten an KVZ";
        commonImportGeoIdAction.FILE_CHOOSER_DIALOG_TITLE = "Import von Straßendaten KVZ";

        availabilityService = getCCService(AvailabilityService.class);
    }

    /**
     * Führt die Service-Methode aus
     */
    @Override
    public Map<String, Object> execute(String absolutePath) throws Exception {
        return availabilityService.importGeoIdsAnKVZ(absolutePath, HurricanSystemRegistry.instance().getSessionId());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        commonImportGeoIdAction.actionPerformed(e);
    }

}
