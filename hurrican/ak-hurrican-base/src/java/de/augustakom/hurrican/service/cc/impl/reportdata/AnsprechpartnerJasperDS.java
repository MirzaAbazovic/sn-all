/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2009 11:50:58
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Jasper-DS fuer die Ermittlung der Ansprechpartner zu einem Aufrag.
 *
 *
 */
public class AnsprechpartnerJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(AnsprechpartnerJasperDS.class);

    private Long auftragId = null;
    private Ansprechpartner currentData = null;
    private Iterator<Ansprechpartner> dataIterator = null;
    private Map<Long, Reference> ansprechpartnerTypMap = null;

    // Services
    private AnsprechpartnerService ansprechpartnerService;
    private ReferenceService referenceService;

    /**
     * @param auftragId
     * @throws AKReportException
     */
    public AnsprechpartnerJasperDS(Long auftragId) throws AKReportException {
        super();
        this.auftragId = auftragId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            ansprechpartnerService = getCCService(AnsprechpartnerService.class);
            referenceService = getCCService(ReferenceService.class);

            List<Reference> types = referenceService.findReferencesByType(Reference.REF_TYPE_ANSPRECHPARTNER, Boolean.FALSE);
            ansprechpartnerTypMap = new HashMap<Long, Reference>();
            CollectionMapConverter.convert2Map(types, ansprechpartnerTypMap, "getId", null);

            List<Ansprechpartner> ansprechpartner = new ArrayList<Ansprechpartner>();

            Ansprechpartner techAnsp = ansprechpartnerService.findPreferredAnsprechpartner(TECH_SERVICE, auftragId);
            CollectionTools.addIfNotNull(ansprechpartner, techAnsp);

            Ansprechpartner hotlineAnsp = ansprechpartnerService.findPreferredAnsprechpartner(HOTLINE_SERVICE, auftragId);
            CollectionTools.addIfNotNull(ansprechpartner, hotlineAnsp);

            this.dataIterator = ansprechpartner.iterator();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Error loading contacts for order.", e);
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
        if ("TYP".equals(field)) {
            Reference typeRef = ansprechpartnerTypMap.get(currentData.getTypeRefId());
            return (typeRef != null) ? typeRef.getStrValue() : HurricanConstants.UNKNOWN;
        }
        else if ("NAME".equals(field)) {
            return (currentData.getAddress() != null) ? currentData.getAddress().getCombinedNameData() : currentData.getText();
        }
        else if ("ORT".equals(field)) {
            return (currentData.getAddress() != null) ? currentData.getAddress().getCombinedOrtOrtsteil() : null;
        }
        else if ("TELEFON".equals(field)) {
            return (currentData.getAddress() != null) ? currentData.getAddress().getTelefon() : null;
        }
        else if ("FAX".equals(field)) {
            return (currentData.getAddress() != null) ? currentData.getAddress().getFax() : null;
        }
        else if ("MAIL".equals(field)) {
            return (currentData.getAddress() != null) ? currentData.getAddress().getEmail() : null;
        }
        return null;
    }

}


