/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.07.2007 11:37:34
 */
package de.augustakom.hurrican.model.exmodules.tal;


/**
 * View-Modell fuer erfolgreiche TAL-Bestellungen.
 *
 *
 */
public class TALReturnOkView extends AbstractTALReturnView {

    /**
     * @see de.augustakom.hurrican.model.exmodules.tal.AbstractTALReturnView#isError()
     */
    public boolean isError() {
        return false;
    }

}


