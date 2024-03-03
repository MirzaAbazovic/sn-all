/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2009 10:15:07
 */
package de.mnet.hurrican.webservice.loadtest.endpoint;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint;
import de.mnet.hurricanweb.loadtest.types.LoadTestRequest;
import de.mnet.hurricanweb.loadtest.types.LoadTestRequestDocument;


/**
 * Endpoint-Implementierung fuer einen Load-Test
 *
 *
 */
public class LoadTestEndpoint extends MnetAbstractMarshallingPayloadEndpoint {

    private static final Logger LOGGER = Logger.getLogger(LoadTestEndpoint.class);

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object obj) throws Exception {
        if (obj instanceof LoadTestRequestDocument) {
            return execute(((LoadTestRequestDocument) obj).getLoadTestRequest());
        }
        else {
            throw new Exception("Input for LoadTest invalid!");
        }
    }

    /* Fuehrt den LoadTest durch. */
    private LoadTestRequest execute(LoadTestRequest loadTestRequest) throws Exception {
        try {
            LOGGER.info("--> LoadTestEndpoint invoked!");

            KundenService ks = getBillingService(KundenService.class);
            Kunde kunde = ks.findKunde(loadTestRequest.getCustomerNo());
            if (kunde == null) {
                throw new Exception("couldn´t load customer for " + loadTestRequest.getCustomerNo());
            }

            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            BAuftrag bauftrag = bas.findAuftrag(loadTestRequest.getOrderNo());
            if (bauftrag == null) {
                throw new Exception("couldn´t load order for " + loadTestRequest.getOrderNo());
            }

            // Rufnummern laden (nur wg. Performance / Last)
            RufnummerService rs = getBillingService(RufnummerService.class);
            rs.findRNs4Auftrag(loadTestRequest.getOrderNo());

            ReferenceService refs = getCCService(ReferenceService.class);
            Reference reqTypeRef = refs.findReference(
                    Reference.REF_TYPE_CPS_SERVICE_ORDER_TYPE, loadTestRequest.getRequestType());
            if (reqTypeRef.getId() == null) {
                throw new Exception("couldn´t load SERVICE_ORDER_TYPE reference: " + loadTestRequest.getRequestType());
            }

            CCAuftragService as = getCCService(CCAuftragService.class);
            List<AuftragDaten> auftragDaten = as.findAuftragDaten4OrderNoOrig(loadTestRequest.getOrderNo());
            if (CollectionTools.isEmpty(auftragDaten)) {
                throw new Exception("couldn´t load hurrican reference for " + loadTestRequest.getOrderNo());
            }
            AuftragDaten ad = auftragDaten.get(0);
            LOGGER.debug("Auftrag-ID: " + ad.getAuftragId());

            // Endstellen des Auftrags laden (nur wg. Performance / Last)
            EndstellenService es = getCCService(EndstellenService.class);
            List<Endstelle> endstellen = es.findEndstellen4Auftrag(ad.getAuftragId());

            if (CollectionTools.isNotEmpty(endstellen)) {
                RangierungsService rangs = getCCService(RangierungsService.class);
                for (Endstelle e : endstellen) {
                    if (e.getRangierId() != null) {
                        rangs.findRangierung(e.getRangierId());
                    }
                }
            }

            // Rufnummernleistungen laden (nur wg. Performance / Last)
            CCRufnummernService ccrs = getCCService(CCRufnummernService.class);
            ccrs.findDNLeistungen4Auftrag(ad.getAuftragId());

            // CPS-Transaction protokollieren
            CPSTransaction cpsTx = new CPSTransaction();
            cpsTx.setOrderNoOrig(loadTestRequest.getOrderNo());
            cpsTx.setAuftragId(ad.getAuftragId());
            cpsTx.setEstimatedExecTime(new Date());
            cpsTx.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_ORDER);
            cpsTx.setServiceOrderType(reqTypeRef.getId());
            cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING);

            CPSService cps = getCCService(CPSService.class);
            cps.saveCPSTransaction(cpsTx, getSessionId());
            LOGGER.debug("<<< saved cps tx: " + cpsTx.getId());

            // Transaction per WebService an CPS uebergeben
            LOGGER.info("call to CPS not implemented");

            // Status der CPS-Tx aendern
            cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PROVISIONING);
            cpsTx.setRequestAt(new Date());
            cps.saveCPSTransaction(cpsTx, getSessionId());

            // generierte Tx-ID in CPSTrigger-Objekt eintragen (als Result)
            loadTestRequest.setTransactionId(cpsTx.getId());

            return loadTestRequest;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        finally {
            LOGGER.info("<-- LoadTestEndpoint finished!");
        }
    }

}


