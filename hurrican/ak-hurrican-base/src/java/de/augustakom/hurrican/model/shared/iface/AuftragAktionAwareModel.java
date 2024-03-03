/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2012 14:45:54
 */
package de.augustakom.hurrican.model.shared.iface;

import de.augustakom.hurrican.model.cc.AuftragAktion;

/**
 * Interface fuer Modelle, die 'Kenntnis' ueber eine Auftrags-Aktion besitzen und somit wissen, durch welche Aktion ein
 * Datensatz hinzugefuegt bzw. entfernt (=deaktiviert) wurde.
 */
public interface AuftragAktionAwareModel {

    /**
     * Gibt die ID der {@link AuftragAktion} an, durch die der Datensatz dem Auftrag hinzugefuegt wurde.
     *
     * @return
     */
    public Long getAuftragAktionsIdAdd();

    public void setAuftragAktionsIdAdd(Long auftragAktionsIdAdd);

    /**
     * Gibt die ID der {@link AuftragAktion} an, durch die der Datensatz vom Auftrag entfernt bzw. deaktiviert wurde.
     *
     * @return
     */
    public Long getAuftragAktionsIdRemove();

    public void setAuftragAktionsIdRemove(Long auftragAktionsIdRemove);

}


