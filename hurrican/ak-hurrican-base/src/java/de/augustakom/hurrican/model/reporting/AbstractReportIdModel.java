/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 11:22:48
 */
package de.augustakom.hurrican.model.reporting;

import de.augustakom.hurrican.model.shared.iface.LongIdModel;


/**
 * Abstrakte Klasse fuer alle Report-Modelle, die eine eindeutige ID besitzen.
 *
 *
 */
public class AbstractReportIdModel extends AbstractReportModel implements LongIdModel {

    private Long id = null;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Festzulegender id
     */
    public void setId(Long id) {
        this.id = id;
    }


}
