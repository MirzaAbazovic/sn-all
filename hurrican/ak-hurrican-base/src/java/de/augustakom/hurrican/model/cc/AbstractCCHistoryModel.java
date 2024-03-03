/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 10:17:50
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.common.model.HistoryModel;
import de.augustakom.common.tools.lang.DateTools;


/**
 * Abstrakte Klasse fuer CC-Modelle, die historisiert werden koennen.
 *
 *
 */
@MappedSuperclass
public abstract class AbstractCCHistoryModel extends AbstractCCIDModel implements HistoryModel {

    private Date gueltigVon = null;
    public static final String GUELTIG_VON = "gueltigVon";
    private Date gueltigBis = null;
    public static final String GUELTIG_BIS = "gueltigBis";

    /**
     * Ueberprueft, ob das aktuelle Objekt historisiert ist. Dies ist dann der Fall, wenn <code>gueltigBis</code> <
     * '2200-01-01' ist.
     *
     * @return
     *
     */
    @Transient
    public boolean isHistorised() {
        return (DateTools.isDateBefore(getGueltigBis(), DateTools.getHurricanEndDate()));
    }

    @Column(name = "GUELTIG_BIS")
    @Override
    public Date getGueltigBis() {
        return gueltigBis;
    }

    /**
     * @param gueltigBis The gueltigBis to set.
     */
    @Override
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    @Column(name = "GUELTIG_VON")
    @Override
    public Date getGueltigVon() {
        return gueltigVon;
    }

    /**
     * @param gueltigVon The gueltigVon to set.
     */
    @Override
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    /**
     * Ueberprueft, ob das aktuelle Objekt zu den angegebenen Datum gueltig ist. Dies ist dann der Fall, wenn
     * das angegebene Datum sich zwischen <code>gueltigVon</code> und <code>gueltigBis</code> befindet.
     */
    @Transient
    public boolean isValid(@NotNull Date referenceDate) {
        return DateTools.isDateBeforeOrEqual(getGueltigVon(), referenceDate)
                && DateTools.isDateAfterOrEqual(getGueltigBis(), referenceDate);
    }

    /**
     * Ueberprueft, ob das aktuelle Objekt zu dem jetztigen Zeitpunkt gueltig ist.
     */
    @Transient
    public boolean isValid() {
        return isValid(new Date());
    }

}
