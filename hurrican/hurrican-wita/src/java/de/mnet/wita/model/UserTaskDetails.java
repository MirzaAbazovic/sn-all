/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 12:59:13
 */
package de.mnet.wita.model;

import java.io.*;

import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;

/**
 * DTO zur Anzeige der Details eines UserTasks.
 */
public class UserTaskDetails implements Serializable {

    private static final long serialVersionUID = -8800765896953151027L;

    private Kunde kunde;
    private Endstelle endstelle;
    private HVTGruppe hvtGruppe;

    public UserTaskDetails(Kunde kunde, Endstelle endstelle, HVTGruppe hvtGruppe) {
        this.kunde = kunde;
        this.endstelle = endstelle;
        this.hvtGruppe = hvtGruppe;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Endstelle getEndstelle() {
        return endstelle;
    }

    public void setEndstelle(Endstelle endstelle) {
        this.endstelle = endstelle;
    }

    public HVTGruppe getHvtGruppe() {
        return hvtGruppe;
    }

    public void setHvtGruppe(HVTGruppe hvtGruppe) {
        this.hvtGruppe = hvtGruppe;
    }

}
