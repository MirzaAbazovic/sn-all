/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2004 07:41:22
 */
package de.augustakom.common.model;

import java.util.*;


/**
 * Interface fuer alle Modelle, die historisiert werden koennen.
 *
 *
 */
public interface HistoryModel {

    public static final String GUELTIG_VON = "gueltigVon";
    public static final String GUELTIG_BIS = "gueltigBis";

    /**
     * Gibt das Datum zurueck, ab dem das Modell bzw. der Datensatz gueltig ist.
     *
     * @return
     */
    public Date getGueltigVon();

    /**
     * Setzt das Datum, ab dem das Modell bzw. der Datensatz gueltig ist.
     *
     * @param gueltigVon
     */
    public void setGueltigVon(Date gueltigVon);

    /**
     * Gibt das Datum zurueck, ab dem das Modell bzw. der Datensatz ungueltig ist.
     *
     * @return
     */
    public Date getGueltigBis();

    /**
     * Setzt das Datum, ab dem das Modell bzw. der Datensatz ungueltig ist.
     *
     * @param gueltigBis
     */
    public void setGueltigBis(Date gueltigBis);

}


