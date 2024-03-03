/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.03.2012 09:52:54
 */
package de.augustakom.hurrican.service.wholesale;

/**
 * Exception, wenn zu der LineId kein aktiver Hurrican Auftrag gefunden wird.
 */
public class LineIdNotFoundException extends WholesaleException {

    private static final long serialVersionUID = -4770604748771151323L;
    private final String lineId;

    public LineIdNotFoundException(String lineId) {
        super(WholesaleFehlerCode.LINE_ID_DOES_NOT_EXIST);
        this.lineId = lineId;
    }

    @Override
    public String getFehlerBeschreibung() {
        return String.format("The lineId %s has not been found!", lineId);
    }

}


