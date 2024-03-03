/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.model.builder;


import java.time.*;
import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 *
 */
public abstract class WbciRequestTestBuilder {

    public static <GF extends WbciGeschaeftsfall> void enrich(WbciRequest<GF> wbciRequest, WbciCdmVersion wbciCdmVersion,
            GeschaeftsfallTyp gfTyp) {
        if (wbciRequest.getCreationDate() == null) {
            wbciRequest.setCreationDate(new Date());
        }
        if (wbciRequest.getUpdatedAt() == null) {
            wbciRequest.setUpdatedAt(new Date());
        }
        if (wbciRequest.getProcessedAt() == null) {
            wbciRequest.setProcessedAt(Date.from(ZonedDateTime.now().plusHours(1).toInstant()));
        }
        if (wbciRequest.getIoType() == null) {
            wbciRequest.setIoType(IOType.OUT);
        }
        if (wbciRequest.getWbciGeschaeftsfall() == null) {
            switch (gfTyp) {
                case VA_KUE_MRN:
                    wbciRequest.setWbciGeschaeftsfall((GF) new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(
                            wbciCdmVersion, gfTyp));
                    break;
                case VA_KUE_ORN:
                    wbciRequest.setWbciGeschaeftsfall((GF) new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(
                            wbciCdmVersion, gfTyp));
                    break;
                case VA_RRNP:
                    wbciRequest.setWbciGeschaeftsfall((GF) new WbciGeschaeftsfallRrnpTestBuilder().buildValid(
                            wbciCdmVersion, gfTyp));
                    break;
            }
        }

        // set the request status last, as this assumes that the IoType has been set correctly
        if (wbciRequest.getRequestStatus() == null) {
            setRequestStatus(wbciRequest);
        }

    }

    private static <GF extends WbciGeschaeftsfall> void setRequestStatus(WbciRequest<GF> wbciRequest) {
        if (IOType.OUT.equals(wbciRequest.getIoType())) {
            switch (wbciRequest.getTyp()) {
                case VA:
                    wbciRequest.setRequestStatus(WbciRequestStatus.VA_VORGEHALTEN);
                    return;
                case TV:
                    wbciRequest.setRequestStatus(WbciRequestStatus.TV_VERSENDET);
                    return;
                case STR_AEN_ABG:
                case STR_AEN_AUF:
                case STR_AUFH_ABG:
                case STR_AUFH_AUF:
                    wbciRequest.setRequestStatus(WbciRequestStatus.STORNO_VERSENDET);
                    return;
            }
        }
        else {
            switch (wbciRequest.getTyp()) {
                case VA:
                    wbciRequest.setRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
                    return;
                case TV:
                    wbciRequest.setRequestStatus(WbciRequestStatus.TV_EMPFANGEN);
                    return;
                case STR_AEN_ABG:
                case STR_AEN_AUF:
                case STR_AUFH_ABG:
                case STR_AUFH_AUF:
                    wbciRequest.setRequestStatus(WbciRequestStatus.STORNO_EMPFANGEN);
                    return;
            }
        }
    }
}
