/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2013
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByExtOrderNoDialog;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;


/**
 * Action, um den IoArchive History Dialog anzuzeigen.
 */
public class OpenIoArchiveDialogAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(OpenIoArchiveDialogAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        Object value = getValue(OBJECT_4_ACTION);

        String extOrderNo = null;
        if (value instanceof WbciRequestCarrierView) {
            extOrderNo = ((WbciRequestCarrierView) value).getVorabstimmungsId();
        }
        else if (value instanceof AuftragCarrierView) {
            extOrderNo = ((AuftragCarrierView) value).getCarrierRefNr();
        }

        try {
            if (StringUtils.isEmpty(extOrderNo)) {
                throw new HurricanGUIException(
                        String.format("Datensatz besitzt keine WITA bzw. WBCI Id.%nDie History kann deshalb nicht angezeigt werden!"));
            }

            HistoryByExtOrderNoDialog dlg = new HistoryByExtOrderNoDialog(extOrderNo);
            DialogHelper.showDialog(getMainFrame(), dlg, false, true);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

}


