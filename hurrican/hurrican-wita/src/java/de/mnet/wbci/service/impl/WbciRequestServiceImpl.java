/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.13
 */
package de.mnet.wbci.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciRequestService;
import de.mnet.wbci.service.WbciStornoService;
import de.mnet.wbci.service.WbciTvService;
import de.mnet.wbci.service.WbciVaService;

/**
 * Default implementation of {@link WbciRequestService}
 */
@CcTxRequired
public class WbciRequestServiceImpl implements WbciRequestService {

    private static final Logger LOGGER = Logger.getLogger(WbciRequestServiceImpl.class);

    @Autowired
    private WbciStornoService wbciStornoService;
    @Autowired
    private WbciTvService wbciTvService;
    @Autowired
    @Qualifier("WbciVaKueMrnService")
    private WbciVaService wbciVaKueMrnService;
    @Autowired
    @Qualifier("WbciVaKueOrnService")
    private WbciVaService wbciVaKueOrnService;
    @Autowired
    @Qualifier("WbciVaRrnpService")
    private WbciVaService wbciVaRrnpService;

    @Override
    public void processIncomingRequest(MessageProcessingMetadata metadata, WbciRequest wbciRequest) {
        if (wbciRequest instanceof TerminverschiebungsAnfrage) {
            wbciTvService.processIncomingTv(metadata, (TerminverschiebungsAnfrage) wbciRequest);
        }
        else if (wbciRequest instanceof StornoAnfrage) {
            wbciStornoService.processIncomingStorno(metadata, (StornoAnfrage) wbciRequest);
        }
        else if (wbciRequest instanceof VorabstimmungsAnfrage) {
            final VorabstimmungsAnfrage vorabstimmungsAnfrage = (VorabstimmungsAnfrage) wbciRequest;
            switch (vorabstimmungsAnfrage.getWbciGeschaeftsfall().getTyp()) {
                case VA_KUE_MRN:
                    wbciVaKueMrnService.processIncomingVA(metadata, vorabstimmungsAnfrage);
                    break;
                case VA_KUE_ORN:
                    wbciVaKueOrnService.processIncomingVA(metadata, vorabstimmungsAnfrage);
                    break;
                case VA_RRNP:
                    wbciVaRrnpService.processIncomingVA(metadata, vorabstimmungsAnfrage);
                    break;
                default:
                    throw new WbciServiceException(String.format("Unsupported Geschaeftsfalltyp: '%s'",
                            vorabstimmungsAnfrage.getWbciGeschaeftsfall().getTyp()));
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Unsupported incoming request: '%s'", wbciRequest));
        }
    }

}
