/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.03.2005 14:57:53
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.NiederlassungService;


/**
 * Jasper-DataSource, um die Details eines best. Verlaufs zu laden.
 *
 *
 */
public class VerlaufDetailJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(VerlaufDetailJasperDS.class);

    private Long verlaufId = null;
    private Iterator<VerlaufAbteilung> dataIterator = null;
    private VerlaufAbteilung currentData = null;
    private Map<Long, Abteilung> abteilungen = null;
    private Map<Long, VerlaufStatus> verlaufStati = null;

    /**
     * Konstruktor mit Angabe der Verlaufs-ID.
     *
     * @param verlaufId
     * @throws AKReportException
     */
    public VerlaufDetailJasperDS(Long verlaufId) throws AKReportException {
        super();
        this.verlaufId = verlaufId;
        init();
    }

    @Override
    protected void init() throws AKReportException {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = bas.findVerlauf(verlaufId);
            List<VerlaufAbteilung> vabts = bas.findVerlaufAbteilungen(verlaufId);
            if ((vabts != null) && (!vabts.isEmpty())) {
                // Details nur drucken, wenn Bemerkungen vorhanden sind oder
                // bei einer Projektierung ein Realisierungstermin fuer eine Abteilung definiert ist!
                boolean printDetails = false;
                for (VerlaufAbteilung va : vabts) {
                    if (StringUtils.isNotBlank(va.getBemerkung())) {
                        printDetails = true;
                        break;
                    }
                    else if (BooleanTools.nullToFalse(verlauf.getProjektierung()) && (va.getRealisierungsdatum() != null)) {
                        printDetails = true;
                        break;
                    }
                }

                if (printDetails) {
                    dataIterator = vabts.iterator();

                    List<VerlaufStatus> stati = bas.findVerlaufStati();
                    if (stati != null) {
                        verlaufStati = new HashMap<Long, VerlaufStatus>();
                        CollectionMapConverter.convert2Map(stati, verlaufStati, "getId", null);
                    }

                    NiederlassungService ns = getCCService(NiederlassungService.class);
                    List<Abteilung> abts = ns.findAbteilungen();
                    if (abts != null) {
                        abteilungen = new HashMap<Long, Abteilung>();
                        CollectionMapConverter.convert2Map(abts, abteilungen, "getId", null);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                this.currentData = this.dataIterator.next();
            }
        }
        return hasNext;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            if ("ABTEILUNG".equals(field)) {
                Abteilung abt = abteilungen.get(currentData.getAbteilungId());
                return (abt != null) ? abt.getName() : null;
            }
            else if ("BEARBEITER".equals(field)) {
                return currentData.getBearbeiter();
            }
            else if ("STATUS".equals(field)) {
                VerlaufStatus vs = verlaufStati.get(currentData.getVerlaufStatusId());
                return (vs != null) ? vs.getStatus() : null;
            }
            else if ("DATUM_AN".equals(field)) {
                return currentData.getDatumAn();
            }
            else if ("ERLEDIGT_AM".equals(field)) {
                return currentData.getDatumErledigt();
            }
            else if ("BEMERKUNG".equals(field)) {
                return currentData.getBemerkung();
            }
            else if ("REALISIERUNGSDATUM".equals(field)) {
                return currentData.getRealisierungsdatum();
            }
        }

        return null;
    }

}


