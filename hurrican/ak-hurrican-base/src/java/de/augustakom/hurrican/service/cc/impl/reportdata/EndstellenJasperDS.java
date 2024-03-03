/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.09.2010 11:40:46
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Jasper Data Source, um die relevanten Endstellen zu ermitteln. Abhaengig von den Uebergabeparametern werden folgende
 * Endstellen ermittelt: <br> AUFTRAG_ID gesetzt: Endstellen aller Auftraege < gekuendigt und <> Storno/Absage <br>
 * VERLAUF_ID gesetzt: Endstellen aller vom Verlauf betroffenen Auftraege <br>
 */
public class EndstellenJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(EndstellenJasperDS.class);

    private Long auftragId;
    private Long verlaufId;

    private EndstellenService endstellenService;
    private CCAuftragService auftragService;
    private BAService baService;

    private Iterator<EndstellenData> dataIterator;
    private EndstellenData currentData;

    /**
     * @param auftragId
     * @param verlaufId
     * @throws AKReportException
     */
    public EndstellenJasperDS(Long auftragId, Long verlaufId) throws AKReportException {
        this.auftragId = auftragId;
        this.verlaufId = verlaufId;
        init();
    }

    @Override
    protected void init() throws AKReportException {
        try {
            endstellenService = getCCService(EndstellenService.class);
            auftragService = getCCService(CCAuftragService.class);
            baService = getCCService(BAService.class);

            List<EndstellenData> endstellenData = new ArrayList<>();

            Set<Long> auftragIds = new TreeSet<>();
            if (verlaufId == null) {
                // Ermittelt alle (ueber die Taifun Auftragsnummer) zusammengehoerigen Hurrican Auftraege
                AuftragDaten actAuftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
                if (actAuftragDaten.getAuftragNoOrig() != null) {
                    List<AuftragDaten> allOrders = auftragService.findAuftragDaten4OrderNoOrig(actAuftragDaten.getAuftragNoOrig());
                    if (CollectionTools.isNotEmpty(allOrders)) {
                        for (AuftragDaten auftragDaten : allOrders) {
                            if (!auftragDaten.isCancelled()) {
                                auftragIds.add(auftragDaten.getAuftragId());
                            }
                        }
                    }
                }
            }
            else {
                // Ermittelt alle Hurrican Auftraege, die vom Bauauftrag betroffen sind
                Verlauf verlauf = baService.findVerlauf(verlaufId);
                if (verlauf != null) {
                    auftragIds.addAll(verlauf.getAllOrderIdsOfVerlauf());
                }
            }

            for (Long orderId : auftragIds) {
                List<Endstelle> endstellen4Order =
                        endstellenService.findEndstellen4Auftrag(orderId);
                if (CollectionTools.isNotEmpty(endstellen4Order)) {
                    endstellenData.add(new EndstellenData(endstellen4Order.get(0), endstellen4Order.get(1), orderId));
                }
            }

            dataIterator = endstellenData.iterator();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Fehler bei der Ermittlung der Endstellen: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                currentData = this.dataIterator.next();
            }
        }
        return hasNext;
    }

    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("ES_ID_A".equals(field)) {
            return (currentData.endstelleA != null) ? currentData.endstelleA.getId() : null;
        }
        else if ("ES_ID_B".equals(field)) {
            return (currentData.endstelleB != null) ? currentData.endstelleB.getId() : null;
        }
        else if ("AUFTRAG_ID_OF_ES".equals(field)) {
            return currentData.auftragId;
        }
        return null;
    }

    static class EndstellenData {
        private Endstelle endstelleA;
        private Endstelle endstelleB;
        private Long auftragId;

        public EndstellenData(Endstelle endstelleA, Endstelle endstelleB, Long auftragId) {
            this.endstelleA = endstelleA;
            this.endstelleB = endstelleB;
            this.auftragId = auftragId;
        }
    }

}


