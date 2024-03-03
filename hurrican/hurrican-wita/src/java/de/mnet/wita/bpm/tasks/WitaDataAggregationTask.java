/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 14:44:36
 */
package de.mnet.wita.bpm.tasks;

import java.time.*;
import java.util.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.aggregator.AuftragsKennerAggregator;
import de.mnet.wita.aggregator.GeschaeftsfallAggregator;
import de.mnet.wita.aggregator.KundeAggregator;
import de.mnet.wita.aggregator.ProduktBezeichnerAggregator;
import de.mnet.wita.aggregator.ProduktBezeichnerKueKdAggregator;
import de.mnet.wita.aggregator.ProjektAggregator;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.WorkflowTaskValidationService;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * WITA Activiti Task, um die notwendigen Daten fuer den WITA Vorgang zu aggregieren.
 */
public class WitaDataAggregationTask extends AbstractSendingWitaTask {

    /**
     * Fictitious date very far in the future
     */
    static final LocalDateTime FICTITIOUS_EARLIEST_SEND_DATE = LocalDateTime.of(3000, 1, 1, 0, 0, 0, 0);

    @Autowired
    KundeAggregator kundeAggregator;
    @Autowired
    GeschaeftsfallAggregator geschaeftsfallAggregator;
    @Autowired
    AuftragsKennerAggregator auftragsKennerAggregator;
    @Autowired
    ProjektAggregator projektAggregator;

    /**
     * public for testing
     */
    @Autowired
    public ProduktBezeichnerAggregator produktBezeichnerAggregator;
    @Autowired
    public ProduktBezeichnerKueKdAggregator produktBezeichnerKueKdAggregator;

    @Autowired
    WorkflowTaskValidationService workflowTaskValidationService;

    @Override
    public void send(DelegateExecution execution, WitaCdmVersion witaCdmVersion) throws Exception {
        Boolean skip = (Boolean) execution.getVariable(WitaTaskVariables.RESTART.id);
        if (BooleanUtils.isTrue(skip)) {
            return;
        }

        WitaCBVorgang cbVorgang = getCbVorgang(execution);
        Auftrag auftrag = createAuftrag(cbVorgang);
        auftrag.setCdmVersion(witaCdmVersion);

        workflowTaskValidationService.validateMwfOutput(auftrag);
        workflowTaskService.sendToWita(auftrag);
    }

    private Auftrag createAuftrag(WitaCBVorgang cbVorgang) throws FindException {
        Auftrag auftrag = new Auftrag();
        auftrag.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        // Set earliestSendDate to dummy value, so that the KVZ Auftrag is NOT sent yet.
        // This date will be adapted later on, when the HVT Kuendigung is confirmed (ABM)
        // Das earliestSendDate wird nur dann gesetzt, wenn die HVt-Kuendigung noch nicht beantwortet oder bereits
        // abgeschlossen wurde. Das ist auch normalerweise der Fall. Es kann aber vorkommen, dass nachdem ein KVz-Auftrag
        // mit ABBM beantwortet wurde, ein neuer KVz-Auftrag, der dieselbe HVt-Kuendigung referenziert, ausgelöst wird
        // und dieser sollte sofort zu DTAG rausgeschickt werden.
        if (cbVorgang.getCbVorgangRefId() != null) {
            final CBVorgang cbVorgangHvtKue = carrierElTalService.findCBVorgang(cbVorgang.getCbVorgangRefId());
            if (!cbVorgangHvtKue.isInStatusAnswered()) {
                auftrag.setEarliestSendDate(Date.from(FICTITIOUS_EARLIEST_SEND_DATE.atZone(ZoneId.systemDefault()).toInstant()));
            }
        }
        Pair<Kunde, Kunde> kunden = kundeAggregator.aggregate(cbVorgang);
        auftrag.setKunde(kunden.getFirst());
        auftrag.setBesteller(kunden.getSecond());

        auftrag.setGeschaeftsfall(geschaeftsfallAggregator.aggregate(cbVorgang));

        ProduktBezeichner produktBezeichner = aggregateProduktBezeichner(auftrag, cbVorgang);
        auftrag.getGeschaeftsfall().getAuftragsPosition().setProduktBezeichner(produktBezeichner);

        // Setze den gleichen Bezeichner fuer die Subposition, falls vorhanden
        if ((produktBezeichner != null) && (auftrag.getGeschaeftsfall().getAuftragsPosition().getPosition() != null)) {
            auftrag.getGeschaeftsfall().getAuftragsPosition().getPosition().setProduktBezeichner(produktBezeichner);
        }

        auftrag.setCbVorgangId(cbVorgang.getId());
        auftrag.setAuftragsKenner(auftragsKennerAggregator.aggregate(cbVorgang));
        auftrag.setProjekt(projektAggregator.aggregate(cbVorgang));
        return auftrag;
    }

    /**
     * Der Produktbezeichner wird nachgelagert aggregiert.
     */
    private ProduktBezeichner aggregateProduktBezeichner(Auftrag auftrag, WitaCBVorgang cbVorgang) {
        ProduktBezeichner produktBezeichner;
        if (cbVorgang.isKuendigung()) {
            produktBezeichner = produktBezeichnerKueKdAggregator.aggregate(auftrag);
        }
        else {
            produktBezeichner = produktBezeichnerAggregator.aggregate(auftrag);
        }
        return produktBezeichner;
    }

}
