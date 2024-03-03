/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 16:07:13
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive.xml;

import de.augustakom.common.tools.xml.XmlFormatter;

/**
 * Hilfsklasse, um die WITA und WBCI Request/Response Daten zu formatieren.
 */
public final class HistoryFormatter {

    private HistoryFormatter() {
        // must not be instantiated
    }

    public static String formatSilent(String xml) {
        return XmlFormatter.formatSilent(xml,
                new RemoveAllNodesFunction("//anlagen/*/inhalt"));
    }

    public static String filterAndFormatSilentWita(String xml) {
        return XmlFormatter.formatSilent(xml,
                new RemoveEnvelopeFunction("//SOAP-ENV:Body"),
                new RemoveNodeFunction("//control/signaturId", false),

                // BKTOFaktura is optional, d.h. nicht in allen Nachrichten
                new RemoveNodeFunction("//BKTOFaktura", true),

                new RemoveAllNodesFunction("//anlagen/*/inhalt")
        );
    }

    public static String filterAndFormatSilentWbci(String xml) {
        return XmlFormatter.formatSilent(xml,
                new RemoveEnvelopeFunction("//SOAP-ENV:Body"),
                new RemoveNodeFunction("//Control/signaturId", false),
                new RemoveAllNodesFunction("//anlagen/*/inhalt")
        );
    }

}
