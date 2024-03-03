/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 11:22:50
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive.actions;

import java.awt.*;
import java.awt.event.*;
import org.netbeans.swing.outline.Outline;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.IoArchiveRowModel;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.xml.HistoryFormatter;
import de.mnet.wita.IOArchiveProperties;

public abstract class AbstractShowXMLAction extends AKAbstractAction {

    private static final long serialVersionUID = -2701165171703366181L;

    private static final Dimension XML_DIALOG_SIZE = new Dimension(800, 650);

    private final Integer columnIndex;
    private final boolean isRequest;
    private final boolean isCompleteXML;

    AbstractShowXMLAction(Integer columnIndex, boolean isRequest, boolean isCompleteXML) {
        this.columnIndex = columnIndex;
        this.isRequest = isRequest;
        this.isCompleteXML = isCompleteXML;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Outline outline = (Outline) getValue(AKAbstractAction.OBJECT_4_ACTION);
        MouseEvent event = (MouseEvent) getValue(AKAbstractAction.ACTION_SOURCE);

        int selectedRow = outline.rowAtPoint(event.getPoint());
        String title;
        if (selectedRow != -1) {
            title = isRequest ? "Request XML" : "Response XML";
            IOArchiveProperties.IOSource ioSource = (IOArchiveProperties.IOSource)
                    outline.getModel().getValueAt(selectedRow, IoArchiveRowModel.IO_SOURCE_COLUMN);
            String requestXml = (String) outline.getModel().getValueAt(selectedRow, columnIndex);
            if (isCompleteXML) {
                MessageHelper.showInfoDialogWithXml(GUISystemRegistry.instance().getMainFrame(),
                        XML_DIALOG_SIZE,
                        HistoryFormatter.formatSilent(requestXml), title, null, false);
            }
            else {
                final String formattedMessage;
                switch (ioSource) {
                    case WBCI:
                        formattedMessage = HistoryFormatter.filterAndFormatSilentWbci(requestXml);
                        break;
                    case WITA:
                        formattedMessage = HistoryFormatter.filterAndFormatSilentWita(requestXml);
                        break;
                    default:
                        throw new IllegalArgumentException(String.format(
                                "The provided ioSource '%s' is not supported by the HistoryFormatter.", ioSource));
                }
                MessageHelper.showInfoDialogWithXml(GUISystemRegistry.instance().getMainFrame(), XML_DIALOG_SIZE,
                        formattedMessage, title, null, false);
            }
        }
    }
}
