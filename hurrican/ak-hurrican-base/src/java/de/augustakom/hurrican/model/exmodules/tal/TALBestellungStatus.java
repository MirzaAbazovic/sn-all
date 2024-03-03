/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2007 17:07:00
 */
package de.augustakom.hurrican.model.exmodules.tal;


/**
 * Modell fuer die Abbildung der TAL Bestellungs-Stati.
 *
 *
 */
public class TALBestellungStatus extends AbstractTALModel {

    public static final Long STATUS_NEW = Long.valueOf(0);
    public static final Long STATUS_PROCESSING = Long.valueOf(1);
    public static final Long STATUS_WAITING_FOR_ANSWER = Long.valueOf(2);
    public static final Long STATUS_ANSWER_OK = Long.valueOf(3);
    public static final Long STATUS_ANSWER_ERR = Long.valueOf(4);
    public static final Long STATUS_UNABLE_TO_SEND = Long.valueOf(5);
    public static final Long STATUS_DATA_ERROR = Long.valueOf(6);
    public static final Long STATUS_EXPORTED = Long.valueOf(7);

    private String name = null;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

}


