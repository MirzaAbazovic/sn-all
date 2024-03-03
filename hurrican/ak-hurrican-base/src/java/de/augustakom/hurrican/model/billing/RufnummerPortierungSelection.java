/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.10.2011 18:00:24
 */
package de.augustakom.hurrican.model.billing;

import java.io.*;
import java.time.*;
import java.util.*;

import de.mnet.common.tools.DateConverterUtils;

/**
 * DTO zur Auswahl der Rufnummern, die im Rahmen einer TAL-Bestellung portiert werden sollen.
 */
public class RufnummerPortierungSelection implements Serializable {

    private static final long serialVersionUID = -7727011746878630301L;

    private Rufnummer rufnummer;
    private Boolean selected = Boolean.FALSE;

    public RufnummerPortierungSelection(Rufnummer rufnummer) {
        this(rufnummer, null);
    }

    public RufnummerPortierungSelection(Rufnummer rufnummer, Date vorgabeMnet) {
        this.rufnummer = rufnummer;
        updateSelectionWithVorgabeMnet(vorgabeMnet);
    }

    /**
     * Waehlt alle Rufnummern aus, deren Realisierungsdatum gleich der Vorgabe-Mnet ist.
     */
    public void updateSelectionWithVorgabeMnet(Date vorgabeMnet) {
        LocalDate vorgabe = DateConverterUtils.asLocalDate(vorgabeMnet);
        LocalDate realDate = DateConverterUtils.asLocalDate(rufnummer.getRealDate());

        if ((vorgabe != null) && (realDate != null) && (realDate.compareTo(vorgabe) == 0)) {
            selected = Boolean.TRUE;
        }
        else {
            selected = Boolean.FALSE;
        }
    }

    public Rufnummer getRufnummer() {
        return rufnummer;
    }

    public void setRufnummer(Rufnummer rufnummer) {
        this.rufnummer = rufnummer;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
