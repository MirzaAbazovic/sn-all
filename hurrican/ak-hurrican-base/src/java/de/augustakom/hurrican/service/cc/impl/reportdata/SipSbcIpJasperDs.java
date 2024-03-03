package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;

/**
 * Jasper-DataSource, um SIP-Inter-Trunk SBC-IP-Adressen zu einem Auftrags zu ermitteln.
 */
public class SipSbcIpJasperDs extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(SipSbcIpJasperDs.class);

    private static final String IP = "IP";
    private static final String ART = "ART";
    private static final String PEERING_PARTNER = "PEERING_PARTNER";

    private Iterator<IPAddress> ipAddressIterator;
    private SipPeeringPartner sipPeeringPartner;
    private IPAddress currentIpAddress;
    private Long auftragId;
    private Date validDate;

    public SipSbcIpJasperDs(Long auftragId) throws AKReportException {
        this(auftragId, null);
    }

    public SipSbcIpJasperDs(Long auftragId, Date validDate) throws AKReportException {
        this.validDate = (validDate != null) ? validDate : new Date();
        this.auftragId = auftragId;
        init();
    }

    @Override
    protected void init() throws AKReportException {
        try {
            SipPeeringPartnerService sipPeeringPartnerService = getCCService(SipPeeringPartnerService.class);
            ipAddressIterator = null;
            sipPeeringPartner = sipPeeringPartnerService.findPeeringPartner4Auftrag(auftragId, validDate);
            if (sipPeeringPartner != null) {
                Optional<SipSbcIpSet> sipSbcIpSetOptional = sipPeeringPartner.getCurrentSbcIpSetAt(validDate);
                ipAddressIterator = (sipSbcIpSetOptional.isPresent()) ? sipSbcIpSetOptional.get().getSbcIps().iterator()
                        : null;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Der SIP Peering Partner bzw. die SBC-IP-Adressen des Auftrags konnten nicht "
                    + "ermittelt werden!", e);
        }
    }

    @Override
    public boolean next() throws JRException {
        if (ipAddressIterator != null && ipAddressIterator.hasNext()) {
            currentIpAddress = ipAddressIterator.next();
            return true;
        }
        return false;
    }

    private String translateType(IPAddress ipAddress) {
        if (ipAddress.isIPV4()) {
            return "IPV4";
        }
        else if (ipAddress.isIPV6()) {
            return "IPV6";
        }
        return "?";
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        switch (jrField.getName()) {
            case ART:
                return (currentIpAddress != null) ? translateType(currentIpAddress) : null;
            case IP:
                return (currentIpAddress != null) ? currentIpAddress.getAddress() : null;
            case PEERING_PARTNER:
                return sipPeeringPartner.getName();
            default:
                return null;
        }
    }
}
