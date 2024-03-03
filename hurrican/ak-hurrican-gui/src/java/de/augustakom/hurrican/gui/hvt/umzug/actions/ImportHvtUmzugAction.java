/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2015 14:16
 */
package de.augustakom.hurrican.gui.hvt.umzug.actions;

import java.awt.event.*;
import com.google.common.base.Function;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.hvt.actions.AbstractImportAction;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView;
import de.augustakom.hurrican.service.cc.XlsImportService;

/**
 * Action oeffnet den Dialog zur Anlage eines neuen {@link HvtUmzug}s
 */
public class ImportHvtUmzugAction extends AbstractImportAction {

    private static final Logger LOGGER = Logger.getLogger(ImportHvtUmzugAction.class);

    private final HvtUmzug hvtUmzug;

    public ImportHvtUmzugAction(final HvtUmzug hvtUmzug) {
        super();
        this.hvtUmzug = hvtUmzug;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        executeOperation(new Operation(), "HOME", null, null);
    }

    private class Operation implements Function<byte[], XlsImportResultView[]> {
        @Override
        public XlsImportResultView[] apply(byte[] input) {
            try {
                XlsImportService xlsImportService = getCCService(XlsImportService.class);
                final XlsImportResultView[] result = xlsImportService.importHvtUmzugDetails(hvtUmzug, input);
                hvtUmzug.setExcelBlob(input);
                return result;
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), ex);
            }
            return new XlsImportResultView[0];
        }

    }
}
