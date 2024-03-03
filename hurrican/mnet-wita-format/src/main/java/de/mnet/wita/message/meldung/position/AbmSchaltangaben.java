/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2011 10:37:26
 */
package de.mnet.wita.message.meldung.position;

import java.io.*;
import java.util.*;
import javax.validation.*;
import javax.validation.constraints.*;

public class AbmSchaltangaben implements Serializable {

    private static final long serialVersionUID = 8768937464155339286L;

    private List<AbmSchaltung> schaltungen = new ArrayList<AbmSchaltung>();
    private String v5Id;
    private List<String> zeitSchlitz = new ArrayList<String>();

    public String getV5Id() {
        return v5Id;
    }

    public void setV5Id(String v5Id) {
        this.v5Id = v5Id;
    }

    @Valid
    @Size(min = 0, max = 2, message = "Maximal 2 Schaltangaben erlaubt.")
    public List<AbmSchaltung> getSchaltungen() {
        return schaltungen;
    }

    public void setSchaltungen(List<AbmSchaltung> schaltungen) {
        this.schaltungen = schaltungen;
    }

    @Size(min = 0, max = 2, message = "Maximal 2 Zeitschlitz-Angaben erlaubt.")
    public List<String> getZeitSchlitz() {
        return zeitSchlitz;
    }

    public void setZeitSchlitz(List<String> zeitSchlitz) {
        this.zeitSchlitz = zeitSchlitz;
    }

    @Override
    public String toString() {
        return "AbmSchaltangaben [schaltungen=" + schaltungen + ", v5Id=" + v5Id + ", zeitSchlitz=" + zeitSchlitz + "]";
    }
}
