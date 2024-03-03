/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2004 07:55:59
 */
package de.augustakom.hurrican.service.utils;

import java.util.*;
import org.apache.commons.lang.time.DateUtils;

import de.augustakom.common.model.HistoryModel;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.shared.iface.LongIdModel;


/**
 * Hilfsklasse, um Objekte zu historisieren.
 *
 *
 */
public class HistoryHelper {

    /**
     * Uebergibt dem Objekt <code>historyModel</code> die Werte, die fuer die Historisierung notwendig sind. <br> Der
     * Wert von <code>gueltigVon</code> wird ueber die Methode <code>setGueltigVon</code> gesetzt. Der Methode
     * <code>setGueltigBis</code> wird DateTools.getHurricanEndDate() uebergeben. <br> Ist das Modell vom Typ {@link
     * LongIdModel}, wird die ID auf <code>null</code> gesetzt.
     *
     * @param historyModel Modell, dessen GueltigVon- und GueltigBis-Wert geaendert werden soll.
     * @param gueltigVon   GueltigVon-Wert fuer das History-Model.
     */
    public static void setHistoryData(HistoryModel historyModel, Date gueltigVon) {
        if (historyModel != null) {
            historyModel.setGueltigVon(gueltigVon);
            historyModel.setGueltigBis(DateTools.getHurricanEndDate());

            if (historyModel instanceof LongIdModel) {
                ((LongIdModel) historyModel).setId(null);
            }
        }
    }

    /**
     * Ueberprueft, ob das HistoryModel <code>historyModel</code> Werte fuer gueltigVon und gueltigBis besitzt. <br> Ist
     * dies nicht der Fall, werden entsprechende Werte (new Date() fuer gueltigVon und DateTools.getHurricanEndDate()
     * fuer gueltigBis) gesetzt.
     *
     * @param historyModel
     */
    public static void checkHistoryDates(HistoryModel historyModel) {
        if (historyModel.getGueltigVon() == null) {
            historyModel.setGueltigVon(new Date());
        }

        if (historyModel.getGueltigBis() == null) {
            historyModel.setGueltigBis(DateTools.getHurricanEndDate());
        }
    }

    public static Date checkGueltigVon(Date date) {
        if (date == null) {
            return DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        }
        return date;
    }

    public static Date checkGueltigBis(Date date) {
        if (date == null) {
            return DateTools.getHurricanEndDate();
        }
        return date;
    }
}


