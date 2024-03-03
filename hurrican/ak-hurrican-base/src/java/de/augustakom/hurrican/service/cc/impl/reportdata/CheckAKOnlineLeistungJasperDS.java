/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2005 14:57:25
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import net.sf.jasperreports.engine.JRException;

import de.augustakom.common.tools.reports.AKReportException;


/**
 * DataSource um zu pruefen, ob der Kunde Internet-Leistungen (z.B. AK-eMail, AK-Hosting oder AK-flat) besitzt. <br> Ist
 * dies der Fall, liefert die Methode <code>next</code> einmal den Wert <code>true</code> zurueck. <br> <br> Diese
 * DataSource wird benoetigt, damit die Header vom Online-Report (fuer EMail- und Hosting-Daten) ausgeblendet wird, wenn
 * der Kunde keine entsprechenden Auftraege besitzt.
 *
 *
 */
public class CheckAKOnlineLeistungJasperDS extends AKMailHostingLeistungenJasperDS {

    private boolean checkDone = false;

    /**
     * @param kundeNoOrig
     * @throws AKReportException
     */
    public CheckAKOnlineLeistungJasperDS(Long kundeNoOrig) throws AKReportException {
        super(kundeNoOrig);
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        if (!checkDone) {
            checkDone = true;
            if (super.dataIterator != null && super.dataIterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

}


