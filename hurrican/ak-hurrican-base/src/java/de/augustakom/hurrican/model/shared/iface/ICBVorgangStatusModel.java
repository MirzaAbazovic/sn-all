/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2007 09:50:58
 */
package de.augustakom.hurrican.model.shared.iface;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;


/**
 * Interface fuer Modelle, die einen CBVorgangs-Status beinhalten.
 *
 *
 */
public interface ICBVorgangStatusModel {

    /**
     * Gibt den Status eines CB-Vorgangs zurueck.
     *
     * @return Status-Wert vom CB-Vorgang (als Konstante im Modell CBVorgang hinterlegt).
     *
     */
    public Long getStatus();

    /**
     * Gibt an, ob die Rueckmeldung positiv (true) oder negativ (false) ist.
     *
     * @return
     *
     */
    public Boolean getReturnOk();

    /**
     * Gibt an, ob zu dem {@link CBVorgang} Fehlermeldungen aus der automatischen Verarbeitung protokolliert sind.
     *
     * @return
     */
    public boolean hasAutomationErrors();

}


