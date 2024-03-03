/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2009 14:26:13
 */
package de.augustakom.hurrican.service.billing.impl;

import ch.ergon.taifun.ws.messages.UpdateHurricanOrderStateDocument;
import ch.ergon.taifun.ws.messages.UpdateHurricanOrderStateResponseDocument;
import ch.ergon.taifun.ws.messages.UpdateHurricanOrderStateResponseType;
import ch.ergon.taifun.ws.messages.UpdateHurricanOrderStateType;
import ch.ergon.taifun.ws.types.AuthenticationType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingWorkflowService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Implementierung von <code>StatusService</code>.
 *
 *
 */
@BillingTxRequiresNew
public class BillingWorkflowServiceImpl extends DefaultBillingService implements BillingWorkflowService {

    private static final Logger LOGGER = Logger.getLogger(BillingWorkflowServiceImpl.class);

    @Autowired
    private CCAuftragService auftragService;

    @Autowired
    private BAService baService;

    @Override
    public void changeOrderState(Long auftragId, Long statusId, String value, Long sessionId)
            throws StoreException {
        if ((auftragId == null) || (statusId == null)) {
            return;
        }
        try {
            // Ermittle AuftragDaten
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);

            if ((auftragDaten.getStatusmeldungen() == null) || !BooleanTools.nullToFalse(auftragDaten.getStatusmeldungen())) {
                return;
            }

            if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
                throw new StoreException(
                        "Kann Auftragdaten-Objekt nicht ermitteln. Statusmeldung wurde nicht übertragen.");
            }

            // Ermittle Benutzername
            String userName = null;
            if (sessionId != null) {
                userName = getLoginNameSilent(sessionId);
            }
            if (StringUtils.isBlank(userName)) {
                userName = "Hurrican";
            }

            LOGGER.info("Calling Billing WebService <UpdateHurricanOrderState> for ORDER__NO " + auftragDaten.getAuftragNoOrig());
            AuthenticationType authType = configureAndGetAuthenticationType();
            UpdateHurricanOrderStateDocument updateDoc = UpdateHurricanOrderStateDocument.Factory.newInstance();
            UpdateHurricanOrderStateType updateType = updateDoc.addNewUpdateHurricanOrderState();

            updateType.setAuthentication(authType);
            updateType.setServiceNo(auftragDaten.getAuftragNoOrig().intValue());
            updateType.setHurricanServiceNo(auftragDaten.getAuftragId().intValue());
            updateType.setStatusNo(statusId.intValue());
            updateType.setUser(userName);
            if (value != null) {
                // Check auf != null, da sonst das VALUE-Element mit xsi:nil="true" versehen wird
                // --> fuehrt auf Seite Taifun zu einer SAXParseException
                updateType.setValue(value);
            }
            updateType.setTestMode(0);

            WebServiceTemplate billingWSTemplate = configureAndGetBillingWSTemplate();
            Object result = billingWSTemplate.marshalSendAndReceive(updateType);

            if (result instanceof UpdateHurricanOrderStateResponseDocument) {
                UpdateHurricanOrderStateResponseDocument respDoc = (UpdateHurricanOrderStateResponseDocument) result;
                UpdateHurricanOrderStateResponseType respType = respDoc.getUpdateHurricanOrderStateResponse();
                if (!respType.getSuccessful()) {
                    throw new StoreException("Error while updating Hurrican order state: " + respType.getError());
                }
            }

            LOGGER.info("DONE calling Billing WebService <UpdateHurricanOrderState> for ORDER__NO "
                    + auftragDaten.getAuftragNoOrig());
        }
        catch (WebServiceFaultException e) {
            LOGGER.error(e);
            throw new StoreException("Error changing order state: " + e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new StoreException("Error changing order state: " + e.getMessage(), e);
        }
    }

    @Override
    public void changeOrderState4Verlauf(Long verlaufId, Long sessionId) throws StoreException {
        try {
            // Lade Verlauf
            Verlauf verlauf = baService.findVerlauf(verlaufId);

            // Aktuell werden nur Statusmeldungen fuer Neuschaltungen und Anschlussuebernahmen uebermittelt
            if ((verlauf != null)
                    && !BooleanTools.nullToFalse(verlauf.getProjektierung())
                    && NumberTools.isIn(verlauf.getAnlass(), new Number[] { BAVerlaufAnlass.NEUSCHALTUNG,
                    BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG, BAVerlaufAnlass.ANSCHLUSSUEBERNAHME })
                    && NumberTools.isIn(verlauf.getVerlaufStatusId(), new Number[] { VerlaufStatus.BEI_DISPO,
                    VerlaufStatus.BEI_TECHNIK, VerlaufStatus.VERLAUF_ABGESCHLOSSEN,
                    VerlaufStatus.VERLAUF_STORNIERT, VerlaufStatus.RUECKLAEUFER_AM })) {
                // Ermittle Value
                String value = null;
                if (NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.BEI_DISPO)) {
                    value = DateTools.formatDate(verlauf.getRealisierungstermin(), DateTools.PATTERN_YEAR_MONTH_DAY);
                }
                else if (NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.RUECKLAEUFER_AM)) {
                    value = baService.findBemerkung4Verlauf(verlaufId);
                }
                else if (NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.VERLAUF_ABGESCHLOSSEN)) {
                    AuftragDaten ad = auftragService.findAuftragDatenByAuftragId(verlauf.getAuftragId());
                    value = (ad != null) ? DateTools.formatDate(ad.getInbetriebnahme(),
                            DateTools.PATTERN_YEAR_MONTH_DAY) : null;
                }

                changeOrderState(verlauf.getAuftragId(), verlauf.getVerlaufStatusId(), value, sessionId);
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Unexpected error while updating billing order state: " + e.getMessage(), e);
        }
    }

}
