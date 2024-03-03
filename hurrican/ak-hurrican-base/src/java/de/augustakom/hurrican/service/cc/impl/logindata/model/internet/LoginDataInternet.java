package de.augustakom.hurrican.service.cc.impl.logindata.model.internet;

import java.io.*;

import de.augustakom.hurrican.model.cc.IpMode;

/**
 * Internet-Zugangdata
 *
 */
public class LoginDataInternet implements Serializable {

    private final String pppUser;
    private final String pppUserRealmSuffix;
    private final String pppPassword;
    private final IpMode ipMode;
    private final Integer pbitDaten;
    private final Integer pbitVoip;
    private final Integer vlanIdDaten;
    private final Integer vlanIdVoip;
    private final String aftrAddress;
    private final Integer atmParameterVPI;
    private final Integer atmParameterVCI;

    public LoginDataInternet(String pppUser, String pppUserRealmSuffix, String pppPassword, IpMode ipMode, Integer pbitDaten,
            Integer pbitVoip, Integer vlanIdDaten, Integer vlanIdVoip, String aftrAddress,
            Integer atmParameterVPI, Integer atmParameterVCI) {
        this.pppUser = pppUser;
        this.pppUserRealmSuffix = pppUserRealmSuffix;
        this.pppPassword = pppPassword;
        this.ipMode = ipMode;
        this.pbitDaten = pbitDaten;
        this.pbitVoip = pbitVoip;
        this.vlanIdDaten = vlanIdDaten;
        this.vlanIdVoip = vlanIdVoip;
        this.aftrAddress = aftrAddress;
        this.atmParameterVPI = atmParameterVPI;
        this.atmParameterVCI = atmParameterVCI;
    }

    public String getPppUser() {
        return pppUser;
    }

    public String getPppUserWithRealm() {
        return pppUser + pppUserRealmSuffix;
    }

    public String getPppPassword() {
        return pppPassword;
    }

    public IpMode getIpMode() {
        return ipMode;
    }

    public Integer getVlanIdDaten() {
        return vlanIdDaten;
    }

    public Integer getVlanIdVoip() {
        return vlanIdVoip;
    }

    public String getAftrAddress() {
        return aftrAddress;
    }

    public Integer getPbitDaten() {
        return pbitDaten;
    }

    public Integer getPbitVoip() {
        return pbitVoip;
    }

    public Integer getAtmParameterVPI() {
        return atmParameterVPI;
    }

    public Integer getAtmParameterVCI() {
        return atmParameterVCI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LoginDataInternet that = (LoginDataInternet) o;

        if (pppUser != null ? !pppUser.equals(that.pppUser) : that.pppUser != null)
            return false;
        if (pppUserRealmSuffix != null ? !pppUserRealmSuffix.equals(that.pppUserRealmSuffix) : that.pppUserRealmSuffix != null)
            return false;
        if (pppPassword != null ? !pppPassword.equals(that.pppPassword) : that.pppPassword != null)
            return false;
        if (ipMode != that.ipMode)
            return false;
        if (pbitDaten != null ? !pbitDaten.equals(that.pbitDaten) : that.pbitDaten != null)
            return false;
        if (pbitVoip != null ? !pbitVoip.equals(that.pbitVoip) : that.pbitVoip != null)
            return false;
        if (vlanIdDaten != null ? !vlanIdDaten.equals(that.vlanIdDaten) : that.vlanIdDaten != null)
            return false;
        if (vlanIdVoip != null ? !vlanIdVoip.equals(that.vlanIdVoip) : that.vlanIdVoip != null)
            return false;
        if (aftrAddress != null ? !aftrAddress.equals(that.aftrAddress) : that.aftrAddress != null)
            return false;
        if (atmParameterVPI != null ? !atmParameterVPI.equals(that.atmParameterVPI) : that.atmParameterVPI != null)
            return false;
        return atmParameterVCI != null ? atmParameterVCI.equals(that.atmParameterVCI) : that.atmParameterVCI == null;

    }

    @Override
    public int hashCode() {
        int result = pppUser != null ? pppUser.hashCode() : 0;
        result = 31 * result + (pppUserRealmSuffix != null ? pppUserRealmSuffix.hashCode() : 0);
        result = 31 * result + (pppPassword != null ? pppPassword.hashCode() : 0);
        result = 31 * result + (ipMode != null ? ipMode.hashCode() : 0);
        result = 31 * result + (pbitDaten != null ? pbitDaten.hashCode() : 0);
        result = 31 * result + (pbitVoip != null ? pbitVoip.hashCode() : 0);
        result = 31 * result + (vlanIdDaten != null ? vlanIdDaten.hashCode() : 0);
        result = 31 * result + (vlanIdVoip != null ? vlanIdVoip.hashCode() : 0);
        result = 31 * result + (aftrAddress != null ? aftrAddress.hashCode() : 0);
        result = 31 * result + (atmParameterVPI != null ? atmParameterVPI.hashCode() : 0);
        result = 31 * result + (atmParameterVCI != null ? atmParameterVCI.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginDataInternet{" +
                "pppUser='" + pppUser + '\'' +
                ", pppUserRealmSuffix='" + pppUserRealmSuffix + '\'' +
                ", pppPassword='" + "[hidden]" + '\'' +
                ", ipMode=" + ipMode +
                ", pbitDaten" + "=" + pbitDaten +
                ", pbitVoIP" + "=" + pbitVoip +
                ", vlanIdDaten=" + vlanIdDaten +
                ", vlanIdVoip=" + vlanIdVoip +
                ", aftrAddress='" + aftrAddress + '\'' +
                ", atmParameterVPI=" + atmParameterVPI +
                ", atmParameterVCI=" + atmParameterVCI +
                '}';
    }
}
