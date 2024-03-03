/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2011 12:21:33
 */
package de.augustakom.hurrican.service.cc.impl;

import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.IPAddressAssignmentRemoteService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4Request;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4RequestDocument;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4Response;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4ResponseDocument;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6Request;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6RequestDocument;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6Response;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6ResponseDocument;
import de.mnet.monline.ipProviderService.ipp.ProductGroup;
import de.mnet.monline.ipProviderService.ipp.ProductGroup.Enum;
import de.mnet.monline.ipProviderService.ipp.Purpose;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV4Request;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV4RequestDocument;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV4Response;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV4ResponseDocument;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV6Request;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV6RequestDocument;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV6Response;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV6ResponseDocument;
import de.mnet.monline.ipProviderService.ipp.Site;
import de.mnet.monline.ipProviderService.ipp.Success;

/**
 * Webserviceimplementierung von {@link IPAddressAssignmentRemoteService}.
 *
 *
 * @since Release 10
 */
public class IPAddressAssignmentRemoteServiceImpl implements IPAddressAssignmentRemoteService {

    @Resource(name = "de.augustakom.hurrican.service.cc.QueryCCService")
    private QueryCCService queryCcService;

    @Resource(name = "monlineWSTemplate")
    private MnetWebServiceTemplate monlineWebServiceTemplate = null;

    private static final Logger LOGGER = Logger.getLogger(IPAddressAssignmentRemoteServiceImpl.class);

    /**
     * @return Returns the monlineWebServiceTemplate.
     */
    private MnetWebServiceTemplate getWebserviceTemplate() {
        return monlineWebServiceTemplate;
    }

    @Override
    public AssignIPV4Response assignIPv4(Enum productGroupEnum, Long vpnId,
            de.mnet.monline.ipProviderService.ipp.Purpose.Enum purposeEnum, Integer netMaskSize,
            de.mnet.monline.ipProviderService.ipp.Site.Enum siteEnum, String userW, Long auftragId)
            throws StoreException {

        final AssignIPV4RequestDocument requestDocument = createIPV4Request(productGroupEnum, vpnId, purposeEnum,
                netMaskSize, siteEnum, userW);
        Object result = getWebserviceTemplate().marshalSendAndReceive(requestDocument);

        AssignIPV4Response assignResponse = null;
        if (result instanceof AssignIPV4ResponseDocument) {
            AssignIPV4ResponseDocument assignResponseDocument = (AssignIPV4ResponseDocument) result;
            assignResponse = assignResponseDocument.getAssignIPV4Response();
            if (assignResponse.getSuccess() != Success.YES) {
                String errorMessage = String.format("Der MOnline WebService konnte dem Auftrag %s keine "
                        + "IP V4 Adresse/Netz zuweisen!", auftragId);
                if (assignResponse.getErrorDetails() != null) {
                    errorMessage += "\nGrund: " + assignResponse.getErrorDetails();
                }
                else {
                    errorMessage += "\nGrund: MOnline hat keinen Grund angegeben!";
                }
                throw new StoreException(errorMessage);
            }
        }
        if (assignResponse == null) {
            throw new StoreException("Der Aufruf des MOnline WebServices ist mit Fehler abgebrochen!");
        }
        return assignResponse;
    }

    @Override
    public AssignIPV6Response assignIPv6(Enum productGroupEnum, Long vpnId, Integer netMaskSize,
            de.mnet.monline.ipProviderService.ipp.Site.Enum siteEnum, String userW, Long auftragId)
            throws StoreException {

        final AssignIPV6RequestDocument requestDocument = createIPV6Request(productGroupEnum, vpnId, netMaskSize,
                siteEnum, userW);
        Object result = getWebserviceTemplate().marshalSendAndReceive(requestDocument);

        AssignIPV6Response assignResponse = null;
        if (result instanceof AssignIPV6ResponseDocument) {
            AssignIPV6ResponseDocument assignResponseDocument = (AssignIPV6ResponseDocument) result;
            assignResponse = assignResponseDocument.getAssignIPV6Response();
            if (assignResponse.getSuccess() != Success.YES) {
                String errorMessage = String.format("Der MOnline WebService konnte dem Auftrag %s keine "
                        + "IP V6 Adresse/Netz zuweisen!", auftragId);
                if (assignResponse.getErrorDetails() != null) {
                    errorMessage += "\nGrund: " + assignResponse.getErrorDetails();
                }
                else {
                    errorMessage += "\nGrund: MOnline hat keinen Grund angegeben!";
                }
                throw new StoreException(errorMessage);
            }
        }
        if (assignResponse == null) {
            throw new StoreException("Der Aufruf des MOnline WebServices ist mit Fehler abgebrochen!");
        }
        return assignResponse;
    }

    AssignIPV4RequestDocument createIPV4Request(ProductGroup.Enum productGroupEnum, Long vpnId,
            Purpose.Enum purposeEnum, Integer netMaskSize, Site.Enum siteEnum, String userW) {
        AssignIPV4RequestDocument assignRequestDocument = AssignIPV4RequestDocument.Factory.newInstance();
        AssignIPV4Request assignRequest = assignRequestDocument.addNewAssignIPV4Request();
        assignRequest.setProductGroup(productGroupEnum);
        assignRequest.setVpnId((vpnId != null) ? vpnId.longValue() : 0);
        assignRequest.setPurpose(purposeEnum);
        assignRequest.setNetmaskSize(netMaskSize.intValue());
        assignRequest.setSite(siteEnum);
        assignRequest.setUser(userW);
        return assignRequestDocument;
    }

    /**
     * Erstellt und liefert zu den Parametern ein Request fuer den Monline Webservice.
     *
     * @param productGroupEnum
     * @param vpnId
     * @param netMaskSize
     * @param siteEnum
     * @param userW
     * @return
     */
    AssignIPV6RequestDocument createIPV6Request(ProductGroup.Enum productGroupEnum, Long vpnId, Integer netMaskSize,
            Site.Enum siteEnum, String userW) {
        AssignIPV6RequestDocument assignRequestDocument = AssignIPV6RequestDocument.Factory.newInstance();
        AssignIPV6Request assignRequest = assignRequestDocument.addNewAssignIPV6Request();
        assignRequest.setProductGroup(productGroupEnum);
        assignRequest.setVpnId((vpnId != null) ? vpnId.longValue() : 0);
        assignRequest.setNetmaskSize(netMaskSize.intValue());
        assignRequest.setSite(siteEnum);
        assignRequest.setUser(userW);
        return assignRequestDocument;
    }

    /**
     * Konfiguriert das WebService-Template für den MOnline WebService
     */
    @PostConstruct
    void configureMonlineWSTemplate() throws HurricanServiceCommandException {
        try {
            WebServiceConfig config = queryCcService.findById(WebServiceConfig.WS_CFG_MONLINE, WebServiceConfig.class);
            monlineWebServiceTemplate.configureWSTemplate(config);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error configuring WebServiceTemplate: " + e.getMessage(), e);
        }
    }

    /**
     * Setzt das Webservice Template. Nur fuer Tests genutzt.
     *
     * @param webserviceTemplate
     */
    void setWebserviceTemplate(MnetWebServiceTemplate webserviceTemplate) {
        this.monlineWebServiceTemplate = webserviceTemplate;
    }

    @Override
    public void releaseIPv4(final String netAddress, final Long netId, final int netmaskSize) throws StoreException {
        final ReleaseIPV4RequestDocument relReqDoc = createIPv4ReleaseReq(netAddress, netId, netmaskSize);
        final ReleaseIPV4ResponseDocument result = (ReleaseIPV4ResponseDocument) getWebserviceTemplate()
                .marshalSendAndReceive(relReqDoc);
        final ReleaseIPV4Response resp = result.getReleaseIPV4Response();
        if (resp.getSuccess() != Success.YES) {
            String errorMsg = String.format(
                    "Fehler beim Freigeben der IP mit Address=%s, NetId=%d und netmaskSize=%d M-Online. Details: %s",
                    netAddress, netId, netmaskSize, resp.getErrorDetails());
            LOGGER.error(errorMsg);
            throw new StoreException(errorMsg);
        }
    }

    private ReleaseIPV4RequestDocument createIPv4ReleaseReq(String netAddress, Long netId, int netmaskSize) {
        final ReleaseIPV4RequestDocument relReqDoc = ReleaseIPV4RequestDocument.Factory.newInstance();
        final ReleaseIPV4Request relReq = relReqDoc.addNewReleaseIPV4Request();
        relReq.setNetAddress(netAddress);
        relReq.setNetId(netId);
        relReq.setNetmaskSize(netmaskSize);
        return relReqDoc;
    }

    @Override
    public void releaseIPv6(String netAddress, Long netId, int netmaskSize) throws StoreException {
        final ReleaseIPV6RequestDocument relReqDoc = createIPv6ReleaseReq(netAddress, netId, netmaskSize);
        final ReleaseIPV6ResponseDocument result = (ReleaseIPV6ResponseDocument) getWebserviceTemplate()
                .marshalSendAndReceive(relReqDoc);
        final ReleaseIPV6Response resp = result.getReleaseIPV6Response();
        if (resp.getSuccess() != Success.YES) {
            String errorMsg = String.format(
                    "Fehler beim Freigeben der IP mit Address=%s, NetId=%d und netmaskSize=%d M-Online. Details: %s",
                    netAddress, netId, netmaskSize, resp.getErrorDetails());
            LOGGER.error(errorMsg);
            throw new StoreException(errorMsg);
        }
    }

    private ReleaseIPV6RequestDocument createIPv6ReleaseReq(String netAddress, Long netId, int netmaskSize) {
        final ReleaseIPV6RequestDocument relReqDoc = ReleaseIPV6RequestDocument.Factory.newInstance();
        final ReleaseIPV6Request relReq = relReqDoc.addNewReleaseIPV6Request();
        relReq.setNetAddress(netAddress);
        relReq.setNetId(netId);
        relReq.setNetmaskSize(netmaskSize);
        return relReqDoc;
    }

} // end

