/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2007 14:59:32
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.dao.cc.DNLeistungDAO;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.CleanService;


/**
 * Service-Implementierung von <code>CleanService</code>
 */
public class CleanServiceImpl extends DefaultCCService implements CleanService {

    private static final Logger LOGGER = Logger.getLogger(CleanServiceImpl.class);

    /**
     * Pfad zum Archiv-File fuer die zu loeschenden Rufnummernleistungen
     */
    private String archivFileDnLeistungenPath;

    private static final String DATE_PATTERN_4_FILENAME = "yyyyMMddHHmmss";

    /**
     * @see de.augustakom.hurrican.service.cc.CleanService#cleanLeistungDn()
     */
    @Override
    public AKWarnings cleanLeistungDn() {
        RufnummerService rnService;
        CCRufnummernService ccRnService;
        try {
            rnService = getBillingService(RufnummerService.class.getName(), RufnummerService.class);
            ccRnService = getCCService(CCRufnummernService.class.getName(),
                    CCRufnummernService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error("cleanLeistungDn() - ServiceNotFoundException " + e.getMessage(), e);
            return new AKWarnings().addAKWarning(this,
                    "Fehler beim Bereinigen der Rufnummernleistungen. \n" + e.getMessage() + e);
        }

        Date date = DateTools.minusWorkDays(60);
        List<Long> dnNoBilling;
        // sucht die gruppierte dn_no aus der Tabelle t_leistung_dn
        List<Long> dnNoCCGrouped;
        try {
            dnNoBilling = rnService.findDnNotHistLastTillDate(date);
            dnNoCCGrouped = ccRnService.groupedDnNos();
        }
        catch (FindException e) {
            LOGGER.error("cleanLeistungDn() - FindException " + e.getMessage(), e);
            return new AKWarnings().addAKWarning(this,
                    "Fehler beim Bereinigen der Rufnummernleistungen. \n" + e.getMessage() + e);
        }
        // ist die ccdnno in der Liste aus dem Billingsystem vorhanden dann
        List<Long> tmp = new ArrayList<Long>();
        int i = 1;
        for (Long l : dnNoCCGrouped) {
            if (dnNoBilling.contains(l) && (i < 2000)) {
                i++;
                tmp.add(l);
            }
        }
        // suchen der Datensätze anhand der Liste tmp
        DNLeistungDAO dao = (DNLeistungDAO) getDAO();
        String filePath = StringTools.formatString(archivFileDnLeistungenPath, new Object[] { DateTools.formatDate(
                new Date(), DATE_PATTERN_4_FILENAME) }, null);
        File logFile = new File(filePath);
        BufferedWriter out1;
        try {
            out1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StringTools.CC_DEFAULT_CHARSET));
            boolean created = logFile.createNewFile();
            if (!created) {
                throw new IOException("File was not created: " + logFile.getName());
            }
        }
        catch (IOException e) {
            LOGGER.error("cleanLeistungDn() - IOException " + e.getMessage(), e);
            return new AKWarnings().addAKWarning(this,
                    "Fehler beim Bereinigen der Rufnummernleistungen. \n" + e.getMessage() + e);
        }
        for (Long lTmp : tmp) {
            List<Long> listTemp = new ArrayList<>();
            listTemp.add(lTmp);
            List<Leistung2DN> l2dn = dao.findLeistung2DnByDnNos(listTemp);
            // exportieren Datensätze mit der dnno aus Tabelle t_leistung_dn
            // in Textfile mit datum
            if (l2dn != null) {
                LOGGER.debug("l2dn != null");
                String strTemp = "";
                for (Leistung2DN objL2dn : l2dn) {
                    LOGGER.debug("strTemp: " + strTemp);
                    strTemp = strTemp
                            + (objL2dn.getId() == null ? "" : objL2dn.getId().toString())
                            + ";"
                            + (objL2dn.getDnNo() == null ? "" : objL2dn.getDnNo().toString())
                            + ";"
                            + (objL2dn.getLbId() == null ? "" : objL2dn.getLbId().toString())
                            + ";"
                            + (objL2dn.getLeistung4DnId() == null ? "" : objL2dn.getLeistung4DnId()
                            .toString())
                            + ";"
                            + (objL2dn.getLeistungParameter() == null ? "" : objL2dn
                            .getLeistungParameter().toString())
                            + ";"
                            + (objL2dn.getScvUserRealisierung() == null ? "" : objL2dn
                            .getScvUserRealisierung().toString())
                            + ";"
                            + (objL2dn.getScvRealisierung() == null ? "" : objL2dn
                            .getScvRealisierung().toString())
                            + ";"
                            + (objL2dn.getScvUserKuendigung() == null ? "" : objL2dn
                            .getScvUserKuendigung().toString())
                            + ";"
                            + (objL2dn.getScvKuendigung() == null ? "" : objL2dn.getScvKuendigung()
                            .toString())
                            + ";"
                            + (objL2dn.getEwsdUserKuendigung() == null ? "" : objL2dn
                            .getEwsdUserKuendigung().toString())
                            + ";"
                            + (objL2dn.getEwsdUserRealisierung() == null ? "" : objL2dn
                            .getEwsdUserRealisierung().toString())
                            + ";"
                            + (objL2dn.getEwsdKuendigung() == null ? "" : objL2dn
                            .getEwsdKuendigung().toString())
                            + ";"
                            + (objL2dn.getEwsdRealisierung() == null ? "" : objL2dn
                            .getEwsdRealisierung().toString())
                            + ";"
                            + (objL2dn.getBillingCheck() == null ? "" : objL2dn.getBillingCheck()
                            .toString())
                            + ";"
                            + (objL2dn.getParameterId() == null ? "" : objL2dn.getParameterId()
                            .toString()) + ";" + " \n";
                }
                try {
                    out1.write(strTemp);
                    out1.flush();
                }
                catch (IOException e) {
                    LOGGER.error("cleanLeistungDn() - IOException " + e.getMessage(), e);
                    return new AKWarnings().addAKWarning(this,
                            "Fehler beim Bereinigen der Rufnummernleistungen. \n" + e.getMessage() + e);
                }
                // loeschen der Daten in der Tabelle t_leistung_dn
                LOGGER.debug("delete");
                dao.deleteLeistung2DnByDnNo(lTmp);
            }
        }
        try {
            out1.close();
        }
        catch (IOException e) {
            LOGGER.error("cleanLeistungDn() - IOException " + e.getMessage(), e);
            return new AKWarnings().addAKWarning(this,
                    "Fehler beim Bereinigen der Rufnummernleistungen. \n" + e.getMessage() + e);
        }
        return null;
    }

    public void setArchivFileDnLeistungenPath(String archivFileDnLeistungenPath) {
        this.archivFileDnLeistungenPath = archivFileDnLeistungenPath;
    }


}

