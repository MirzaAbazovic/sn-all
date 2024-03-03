/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2014
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.time.*;
import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 * Ermittelt alle aktuell gültigen technischen Leistungen zu einem Auftrag.
 */
public class TechLeistungenJasperDs extends AbstractCCJasperDS {
    private static final Logger LOGGER = Logger.getLogger(TechLeistungenJasperDs.class);

    private final Long auftragId;
    private Iterator<TechLeistung> techLeistungIterator = Collections.emptyIterator();
    private TechLeistung currentLeistung;

    public TechLeistungenJasperDs(final Long auftragId) {
        super();
        this.auftragId = auftragId;
        init();
    }

    @Override
    public void init() {
        try {
            final CCLeistungsService leistungsService = getCCService(CCLeistungsService.class);
            final List<Auftrag2TechLeistung> auftrag2TechLeistungen =
                    leistungsService.findAuftrag2TechLeistungen(auftragId, LocalDate.now());

            List<TechLeistung> techLeistungList = new ArrayList<>();
            for (final Auftrag2TechLeistung auftrag2TechLeistung : auftrag2TechLeistungen) {
                final TechLeistung techLeistung =
                        leistungsService.findTechLeistung(auftrag2TechLeistung.getTechLeistungId());
                techLeistungList.add(techLeistung);
            }
            techLeistungIterator = techLeistungList.iterator();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean next() throws JRException {
        final boolean hasNext = techLeistungIterator.hasNext();
        if (hasNext) {
            currentLeistung = techLeistungIterator.next();
        }
        return hasNext;
    }

    @Override
    protected Object getFieldValue(final String field) throws JRException {
        if ("LEISTUNG_NAME".equals(field)) {
            return (currentLeistung != null) ? currentLeistung.getTypAndName() : null;
        }
        return null;
    }
}
