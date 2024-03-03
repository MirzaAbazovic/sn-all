/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.03.2005 11:45:35
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Jasper-DataSource, um die Endstellen zu einem Auftrag zu ermitteln.
 *
 *
 */
public class Endstellen4AuftragJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(Endstellen4AuftragJasperDS.class);

    private Long auftragId = null;
    private Iterator<Endstelle> dataIterator = null;
    private Endstelle currentData = null;
    private EndstelleLtgDaten ltgDaten = null;
    private HWSwitch hwSwitch = null;

    private static Map<Long, Leitungsart> leitungsarten = null;
    private static Map<Long, Schnittstelle> schnittstellen = null;

    /* Laedt die Basis-Daten fuer die DataSource */
    static {
        try {
            schnittstellen = new HashMap<Long, Schnittstelle>();
            leitungsarten = new HashMap<Long, Leitungsart>();

            IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.HURRICAN_CC_SERVICE);

            ProduktService ps = locator.getService(ProduktService.class.getName(),
                    ProduktService.class, null);
            List<Schnittstelle> schnitts = ps.findSchnittstellen();
            if (schnitts != null) {
                CollectionMapConverter.convert2Map(schnitts, schnittstellen, "getId", null);
            }

            List<Leitungsart> larts = ps.findLeitungsarten();
            if (larts != null) {
                CollectionMapConverter.convert2Map(larts, leitungsarten, "getId", null);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Konstruktor mit Angabe der (CC-)Auftrags-ID.
     *
     * @param auftragId
     */
    public Endstellen4AuftragJasperDS(Long auftragId) {
        super();
        this.auftragId = auftragId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(auftragId);
            dataIterator = (endstellen != null) ? endstellen.iterator() : null;

            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            hwSwitch = auftragService.getSwitchKennung4Auftrag(auftragId);
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
                ltgDaten = null;
                this.currentData = this.dataIterator.next();
                if (currentData != null) {
                    try {
                        EndstellenService esSrv = getCCService(EndstellenService.class);
                        ltgDaten = esSrv.findESLtgDaten4ES(currentData.getId());
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        return hasNext;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        try {
            if (currentData != null) {
                switch (field) {
                    case "ES_ID":
                        return currentData.getId();
                    case "ES_TYP":
                        return currentData.getEndstelleTyp();
                    case "ENDSTELLE":
                        return currentData.getEndstelle();
                    case "ES_NAME":
                        return currentData.getName();
                    case "ES_ORT":
                        return currentData.getOrt();
                    case "SWITCH":
                        if (hwSwitch != null) {
                            return hwSwitch.getName();
                        }
                        break;
                    case "LEITUNGSART":
                        if (ltgDaten != null) {
                            Leitungsart leitungsart = leitungsarten.get(ltgDaten.getLeitungsartId());
                            return leitungsart != null ? leitungsart.getName() : null;
                        }
                        break;
                    case "SCHNITTSTELLE":
                        if (ltgDaten != null) {
                            Schnittstelle sst = schnittstellen.get(ltgDaten.getSchnittstelleId());
                            return sst != null ? sst.getSchnittstelle() : null;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}


