/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.14
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.model.WitaCBVorgang;

/**
 *
 */
public abstract class AbstractWitaTalTestAction extends AbstractWitaTestAction {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWitaTalTestAction.class);
    protected CarrierElTALService carrierElTalService;

    /**
     * Constructor setting the action name field.
     *
     * @param actionName
     */
    public AbstractWitaTalTestAction(String actionName, CarrierElTALService carrierElTalService) {
        super(actionName);
        this.carrierElTalService = carrierElTalService;
    }

    protected WitaCBVorgang findWitaCbVorgang(String externeAuftragsnummer) {
        WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
        example.setCarrierRefNr(externeAuftragsnummer);
        try {
            LOG.info(String.format("Searching for CbVorgang for externalOrderId '%s'", externeAuftragsnummer));
            return Iterables.getOnlyElement(carrierElTalService.findCBVorgaengeByExample(example), null);
        }
        catch (FindException e) {
            throw new CitrusRuntimeException(e);
        }
    }

    protected Long getSessionId() {
        return -1L;
    }
}
