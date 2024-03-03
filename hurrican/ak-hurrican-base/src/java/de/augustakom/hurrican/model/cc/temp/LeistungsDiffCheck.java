/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2006 16:42:54
 */
package de.augustakom.hurrican.model.cc.temp;

import java.util.*;
import org.apache.commons.lang.SystemUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Temporaeres Modell, um anzugeben, ob eine Leistungsdifferenz auf einem Auftrag synchronisiert werden kann.
 */
public class LeistungsDiffCheck extends AbstractCCModel {

    private boolean ok = true;
    private List<String> messages = null;
    private Date lastChangeDate = null;

    /**
     * @return Returns the messages.
     */
    public List<String> getMessages() {
        return this.messages;
    }

    /**
     * Gibt die gesetzten Messages als ein String zurueck.
     */
    public String getMessagesAsString() {
        StringBuilder sb = new StringBuilder();
        if (messages != null) {
            boolean addLineBreak = false;
            for (String message : messages) {
                if (addLineBreak) { sb.append(SystemUtils.LINE_SEPARATOR); }
                sb.append(message);
                addLineBreak = true;
            }
        }
        return sb.toString();
    }

    /**
     * @param message The messages to set.
     */
    public void addMessages(String message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
    }

    /**
     * Gibt an, ob die Leistungs-Differenz auf dem Auftrag abgeglichen werden kann.
     *
     * @return Returns the ok.
     */
    public boolean isOk() {
        return this.ok;
    }

    /**
     * @param ok The ok to set.
     */
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    /**
     * Gibt das spaeteste Datum der Leistungsaenderungen zurueck.
     *
     * @return Returns the lastChangeDate.
     */
    public Date getLastChangeDate() {
        return this.lastChangeDate;
    }


    /**
     * Setzt das Datum fuer die Leistungsaenderung. <br> Das Datum <code>lastChangeDate</code> wird nur gesetzt, wenn es
     * nach(!) einem bereits vorhanden Datum liegt.
     *
     * @param lastChangeDate The lastChangeDate to set.
     */
    public void setLastChangeDate(Date lastChangeDate) {
        if (this.lastChangeDate == null) {
            this.lastChangeDate = lastChangeDate;
        }
        else if (DateTools.isDateAfter(lastChangeDate, this.lastChangeDate)) {
            this.lastChangeDate = lastChangeDate;
        }
    }

}


