package de.augustakom.hurrican.service.cc.impl.logindata.model.voip;

import java.io.*;
import java.time.*;

/**
 *  SIP-Zugangsdata pro Rufnummer
 */
public class LoginDataVoipDn implements Serializable {

    private final String sipHauptrufnummer;
    private final String sipDomain;
    private final String sipPassword;
    private final LocalDate validFrom;

    public LoginDataVoipDn(String sipHauptrufnummer, String sipDomain, String sipPassword) {
        this(sipHauptrufnummer, sipDomain, sipPassword, null);
    }

    public LoginDataVoipDn(String sipHauptrufnummer, String sipDomain, String sipPassword, LocalDate validFrom) {
        this.sipHauptrufnummer = sipHauptrufnummer;
        this.sipDomain = sipDomain;
        this.sipPassword = sipPassword;
        this.validFrom = validFrom;
    }

    public String getSipHauptrufnummer() {
        return sipHauptrufnummer;
    }

    public String getSipDomain() {
        return sipDomain;
    }

    public String getSipPassword() {
        return sipPassword;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LoginDataVoipDn that = (LoginDataVoipDn) o;

        if (sipHauptrufnummer != null ? !sipHauptrufnummer.equals(that.sipHauptrufnummer) : that.sipHauptrufnummer != null)
            return false;
        if (sipDomain != null ? !sipDomain.equals(that.sipDomain) : that.sipDomain != null)
            return false;
        if (sipPassword != null ? !sipPassword.equals(that.sipPassword) : that.sipPassword != null)
            return false;
        return validFrom != null ? validFrom.equals(that.validFrom) : that.validFrom == null;
    }

    @Override
    public int hashCode() {
        int result = sipHauptrufnummer != null ? sipHauptrufnummer.hashCode() : 0;
        result = 31 * result + (sipDomain != null ? sipDomain.hashCode() : 0);
        result = 31 * result + (sipPassword != null ? sipPassword.hashCode() : 0);
        result = 31 * result + (validFrom != null ? validFrom.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginDataVoipDn{" +
                "sipHauptrufnummer='" + sipHauptrufnummer + '\'' +
                ", sipDomain='" + sipDomain + '\'' +
                ", sipPassword='" + sipPassword + '\'' +
                ", validFrom=" + validFrom +
                '}';
    }
}
