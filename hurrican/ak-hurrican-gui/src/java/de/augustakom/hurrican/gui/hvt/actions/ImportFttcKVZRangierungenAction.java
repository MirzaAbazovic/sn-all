/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2012 09:09:21
 */
package de.augustakom.hurrican.gui.hvt.actions;

import java.awt.event.*;
import com.google.common.base.Function;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView;
import de.augustakom.hurrican.service.cc.XlsImportService;

/**
 * Action um Fttc KVZ Rangierungen via Excel Liste zu Importieren
 */
public class ImportFttcKVZRangierungenAction extends AbstractImportAction {
    private static final Logger LOGGER = Logger.getLogger(ImportFttcKVZRangierungenAction.class);

    private static final String IMPORT_FTTC_KVZ_RANGIERUNGEN_ABSOLUTE_PATH = "import.fttc.kvz.rangierungen.absolute.path";

    private class Operation implements Function<byte[], XlsImportResultView[]> {
        @Override
        public XlsImportResultView[] apply(byte[] input) {
            try {
                XlsImportService xlsImportService = getCCService(XlsImportService.class);
                return xlsImportService.importFttcKVZRangierungen(input, HurricanSystemRegistry.instance()
                        .getSessionId());
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), ex);
            }
            return null;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        executeOperation(new Operation(), IMPORT_FTTC_KVZ_RANGIERUNGEN_ABSOLUTE_PATH, null, null);
    }
}


