package de.mnet.hurrican.wholesale.ws.outbound;


import java.util.*;

import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.wbci.model.PreAgreementVO;

/**
 * Service, which is used by the Hurrican client. Configured by wholesale-spri-client-context.xml
 */
public interface WholesaleOrderOutboundService extends ICCService {


    /**
     * The method create an order for pv.
     *
     * @param preAgreementVO which is selected in the UI
     * @param loginName name of the user currently logged in
     *
     */
    void sendWholesaleCreateOrderPV(PreAgreementVO preAgreementVO, String loginName);

    /**
     * The method sends update order for pv
     */
    void sendWholesaleUpdateOrderRUEMPV(AuftragstypType auftragsType, MeldungstypAKMPVType akmpv);

    /**
     * Loads wholesaleAudits from db using {@link de.mnet.hurrican.wholesale.service.WholesaleAuditService}
     *
     * @param vorabstimmungsId
     * @return die WholesaleAudits, zu einer vorabstimmungsId.
     */
    List<WholesaleAudit> loadWholesaleAuditsByVorabstimmungsId(String vorabstimmungsId);

}
