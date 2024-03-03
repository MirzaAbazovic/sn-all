/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.07.2007 11:38:04
 */
package de.augustakom.hurrican.model.exmodules.tal;


/**
 * View-Modell fuer fehlgeschlagene TAL-Bestellungen.
 *
 *
 */
public class TALReturnErrorView extends AbstractTALReturnView {

    /**
     * @see de.augustakom.hurrican.model.exmodules.tal.AbstractTALReturnView#isError()
     */
    public boolean isError() {
        return true;
    }

}


