/**
 *
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Modellklasse zur Abbildung von MVS-Enterprise-Lizens-Daten fuer die CPS-Provisionierung.
 */
@XStreamAlias("LICENCES")
public class CPSMVSEnterpriseLicences extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("LINES")
    private Long lines;
    @XStreamAlias("IVR")
    private Long ivr;
    @XStreamAlias("MOBILE_CLIENT")
    private Long mobileClient;
    @XStreamAlias("FAX2MAIL")
    private Long faxToMail;
    @XStreamAlias("ATTENDANT_CONSOLE")
    private Long attendantConsole;
    @XStreamAlias("THREEWAY_CONFERENCE")
    private Long threeWayConference;

    public CPSMVSEnterpriseLicences(Long lines, Long ivr, Long mobileClient, Long faxToMail, Long attendantConsole, Long threeWayConference) {
        this.lines = lines;
        this.ivr = ivr;
        this.mobileClient = mobileClient;
        this.faxToMail = faxToMail;
        this.attendantConsole = attendantConsole;
        this.threeWayConference = threeWayConference;
    }

    public CPSMVSEnterpriseLicences() {
    }

    public Long getLines() {
        return lines;
    }

    public Long getIvr() {
        return ivr;
    }

    public Long getMobileClient() {
        return mobileClient;
    }

    public Long getFaxToMail() {
        return faxToMail;
    }

    public Long getAttendantConsole() {
        return attendantConsole;
    }

    public Long getThreeWayConference() {
        return threeWayConference;
    }

    public void setLines(Long lines) {
        this.lines = lines;
    }

    public void setIvr(Long ivr) {
        this.ivr = ivr;
    }

    public void setMobileClient(Long mobileClient) {
        this.mobileClient = mobileClient;
    }

    public void setFaxToMail(Long faxToMail) {
        this.faxToMail = faxToMail;
    }

    public void setAttendantConsole(Long attendantConsole) {
        this.attendantConsole = attendantConsole;
    }

    public void setThreeWayConference(Long threeWayConference) {
        this.threeWayConference = threeWayConference;
    }
}
