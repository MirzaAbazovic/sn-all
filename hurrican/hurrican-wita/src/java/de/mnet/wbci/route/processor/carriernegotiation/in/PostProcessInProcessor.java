/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.common.route.HurricanInProcessor;
import de.mnet.common.route.HurricanProcessor;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciStornoService;
import de.mnet.wbci.service.WbciTvService;
import de.mnet.wbci.service.WbciVaService;

/**
 * Camel-Prozessor, der sich um die Verarbeitung eines eingehenden bereits im IO-Archiv gespeicherten Requests kuemmert.
 * Der Prozessor delegiert die entsprechende Nachricht an einem WBCI-Service weiter.
 */
@Component
public class PostProcessInProcessor extends HurricanInProcessor implements HurricanProcessor {

    private static final Logger LOGGER = Logger.getLogger(PostProcessInProcessor.class);
    @Autowired
    private FeatureService featureService;
    @Autowired
    @Qualifier("WbciVaKueMrnService")
    private WbciVaService wbciVaKueMrnService;
    @Autowired
    @Qualifier("WbciVaKueOrnService")
    private WbciVaService wbciVaKueOrnService;
    @Autowired
    @Qualifier("WbciVaRrnpService")
    private WbciVaService wbciVaRrnpService;
    @Autowired
    @Qualifier("WbciTvService")
    private WbciTvService wbciTvService;
    @Autowired
    @Qualifier("WbciStornoService")
    private WbciStornoService wbciStornoService;
    @Autowired
    @Qualifier("WbciMeldungService")
    private WbciMeldungService wbciMeldungService;

    @Override
    public void process(Exchange exchange) throws Exception {
        final WbciMessage wbciMessage = getOriginalMessage(exchange);
        if (featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)) {
            if (wbciMessage instanceof VorabstimmungsAnfrage) {
                final VorabstimmungsAnfrage vorabstimmungsAnfrage = (VorabstimmungsAnfrage) wbciMessage;
                switch (vorabstimmungsAnfrage.getWbciGeschaeftsfall().getTyp()) {
                    case VA_KUE_MRN:
                        wbciVaKueMrnService.autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
                        break;
                    case VA_KUE_ORN:
                        wbciVaKueOrnService.autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
                        break;
                    case VA_RRNP:
                        wbciVaRrnpService.autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
                        break;
                    default:
                        return;
                }
            }
            else if (wbciMessage instanceof TerminverschiebungsAnfrage) {
                wbciTvService.postProcessIncomingTv((TerminverschiebungsAnfrage) wbciMessage);
            }
            else if (wbciMessage instanceof UebernahmeRessourceMeldung) {
                wbciMeldungService.postProcessIncomingAkmTr((UebernahmeRessourceMeldung) wbciMessage);
            }
        }
    }

}
