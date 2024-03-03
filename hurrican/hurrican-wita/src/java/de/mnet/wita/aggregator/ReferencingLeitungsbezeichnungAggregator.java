/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 12:53:26
 */
package de.mnet.wita.aggregator;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Leitungsbezeichnung der TAL eines Auftrags zu ermitteln, mit dem die Leistungsaenderung
 * 'Produktvariante' bzw. eine Leistungsmerkmalaenderung durchgefuehrt werden soll. <br> Der zugehoerige ('alte')
 * Auftrag ist ueber die {@link Carrierbestellung#getAuftragId4TalNA()} verknuepft.
 */
public class ReferencingLeitungsbezeichnungAggregator extends AbstractWitaDataAggregator<LeitungsBezeichnung> {

    private static final Logger LOGGER = Logger.getLogger(ReferencingLeitungsbezeichnungAggregator.class);

    @Override
    public LeitungsBezeichnung aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        try {
            Carrierbestellung actualCarrierbestellung = carrierService.findCB(cbVorgang.getCbId());
            Preconditions.checkNotNull(actualCarrierbestellung, "Carrierbestellung konnte nicht ermittelt werden!");
            return aggregateByReferencingCb(cbVorgang, actualCarrierbestellung);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Fehler bei der Ermittlung der LeitungsBezeichnung: "
                    + e.getMessage(), e);
        }
    }

    private LeitungsBezeichnung aggregateByReferencingCb(WitaCBVorgang cbVorgang,
            Carrierbestellung actualCarrierbestellung) throws WitaDataAggregationException {
        Preconditions.checkNotNull(actualCarrierbestellung.getAuftragId4TalNA(),
                "Es ist kein Ursprungsauftrag für die Leistungs(merkmal)änderung angegeben!");

        Carrierbestellung referencingCb = witaDataService.getReferencingCarrierbestellung(cbVorgang,
                actualCarrierbestellung);
        Preconditions.checkNotNull(referencingCb, "Es wurde keine Carrierbestellung für den Usprungsauftrag der "
                + "Leistungs(merkmal)änderung gefunden!");
        return createLeitungsBezeichnung(referencingCb);
    }

    private LeitungsBezeichnung createLeitungsBezeichnung(Carrierbestellung referencingCb) {
        if (StringUtils.isBlank(referencingCb.getLbz())) {
            return null;
        }
        return new LeitungsBezeichnung(referencingCb.getLbz(), witaDataService.loadHVTStandortOnkz4Cb(referencingCb.getId()));
    }


}
