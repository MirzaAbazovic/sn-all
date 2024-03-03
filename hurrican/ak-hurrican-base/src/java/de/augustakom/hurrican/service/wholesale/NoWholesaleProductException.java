/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2012 08:27:30
 */
package de.augustakom.hurrican.service.wholesale;


/**
 * Wird geworfen, wenn der ermittelte Hurrican Auftrag kein Wholesale-Auftrag ist.
 */
public class NoWholesaleProductException extends WholesaleException {

    private static final long serialVersionUID = -6815704746572151369L;

    private final String lineId;
    private String errorMsg;

    public NoWholesaleProductException(String lineId) {
        super(WholesaleFehlerCode.NOT_A_WHOLESALE_PRODUCT);
        this.lineId = lineId;
    }

    public NoWholesaleProductException(String lineId, String errorMsg) {
        super(WholesaleFehlerCode.ERROR_GET_ORDER_PARAMETERS);
        this.errorMsg = errorMsg;
        this.lineId = lineId;
    }


    @Override
    public String getFehlerBeschreibung() {
        return String.format("The order referenced by lineId %s is not a Wholesale-product! %s", lineId, errorMsg);
    }

}

