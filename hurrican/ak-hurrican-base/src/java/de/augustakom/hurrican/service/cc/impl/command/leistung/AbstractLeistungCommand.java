/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2006 16:29:45
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Basisklasse fuer alle Commands, die bei Leistungsaenderungen (Zugang/Abgang) Aktionen durchfuehren sollen.
 *
 *
 */
public abstract class AbstractLeistungCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractLeistungCommand.class);

    public static final String KEY_SESSION_ID = "user.session.id";
    public static final String KEY_AUFTRAG_ID = "auftrag.id";
    public static final String KEY_TECH_LEISTUNG_ID = "tech.leistung.id";
    public static final String KEY_TECH_LEISTUNG_AKTIV_VON = "tech.ls.aktiv.von";
    public static final String KEY_TECH_LEISTUNG_AKTIV_BIS = "tech.ls.aktiv.bis";
    public static final String KEY_SERVICE_CALLBACK = "service.callback";

    /**
     * Gibt die Auftrags-ID zurueck.
     */
    protected Long getAuftragId() {
        return (Long) getPreparedValue(KEY_AUFTRAG_ID);
    }

    /**
     * Gibt die ID der technischen Leistung zurueck, zu der das Command-Objekt aufgerufen wurde.
     */
    protected Long getTechLeistungsId() {
        return (Long) getPreparedValue(KEY_TECH_LEISTUNG_ID);
    }

    /**
     * Gibt das Datum zurueck, ab dem die technische Leistung aktiv sein soll.
     */
    protected Date getAktivVon() {
        return (Date) getPreparedValue(KEY_TECH_LEISTUNG_AKTIV_VON);
    }

    /**
     * Gibt das Datum zurueck, bis zu dem die technische Leistung aktiv sein soll.
     */
    protected Date getAktivBis() {
        return (Date) getPreparedValue(KEY_TECH_LEISTUNG_AKTIV_BIS);
    }

    /**
     * Gibt die Session-ID zurueck.
     */
    protected Long getSessionId() {
        return (Long) getPreparedValue(KEY_SESSION_ID);
    }

    /**
     * Ermittelt die technische Leistung, zu der das Command-Objekt aufgerufen wurde.
     *
     * @return Objekt vom Typ <code>TechLeistung</code>
     * @throws FindException
     *
     */
    protected TechLeistung getTechLeistung() throws FindException {
        try {
            CCLeistungsService ls = getCCService(CCLeistungsService.class);
            TechLeistung techLs = ls.findTechLeistung(getTechLeistungsId());
            if (techLs != null) {
                return techLs;
            }
            else {
                throw new FindException("Technische Leistung zur ID " + getTechLeistungsId() + " nicht gefunden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der techn. Leistung: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt den Namen der technischen Leistung, fuer die das Command-Objekt aufgerufen wurde. <br> Evtl.
     * auftretende Exceptions werden unterdrueckt.
     *
     * @return
     *
     */
    protected String getTechLeistungName() {
        try {
            TechLeistung tl = getTechLeistung();
            return (tl != null) ? tl.getName() : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}


