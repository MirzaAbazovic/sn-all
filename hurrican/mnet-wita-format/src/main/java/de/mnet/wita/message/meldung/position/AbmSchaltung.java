/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2011 10:41:41
 */
package de.mnet.wita.message.meldung.position;

import java.io.*;
import javax.validation.constraints.*;


public class AbmSchaltung implements Serializable {

    private static final long serialVersionUID = 8239919429647690743L;

    private String uevt;
    private String evs;
    private String doppelader;

    public AbmSchaltung(String uevt, String evs, String doppelader) {
        super();
        this.uevt = uevt;
        this.evs = evs;
        this.doppelader = doppelader;
    }

    @NotNull(message = "Uebergabeverteiler muss gesetzt sein.")
    public String getUevt() {
        return uevt;
    }

    public void setUevt(String uevt) {
        this.uevt = uevt;
    }

    @NotNull(message = "Endverschluss muss gesetzt sein.")
    public String getEvs() {
        return evs;
    }

    public void setEvs(String evs) {
        this.evs = evs;
    }

    @NotNull(message = "Doppelader muss gesetzt sein.")
    public String getDoppelader() {
        return doppelader;
    }

    public void setDoppelader(String doppelader) {
        this.doppelader = doppelader;
    }

    @Override
    public String toString() {
        return "AbmSchaltung [uevt=" + uevt + ", evs=" + evs + ", doppelader=" + doppelader + "]";
    }
}
