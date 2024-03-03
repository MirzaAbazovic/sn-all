package de.augustakom.hurrican.service.cc.impl.logindata.model;

import java.io.*;

import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoip;

/**
 *  Zugangsdata
 *
 */
public class LoginData implements Serializable {

    private final LoginDataInternet loginDataInternet;
    private final LoginDataVoip loginDataVoip;

    public LoginData(LoginDataInternet loginDataInternet, LoginDataVoip loginDataVoip) {
        this.loginDataInternet = loginDataInternet;
        this.loginDataVoip = loginDataVoip;
    }

    public LoginDataInternet getLoginDataInternet() {
        return loginDataInternet;
    }

    public LoginDataVoip getLoginDataVoip() {
        return loginDataVoip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LoginData loginData = (LoginData) o;

        if (loginDataInternet != null ? !loginDataInternet.equals(loginData.loginDataInternet) : loginData.loginDataInternet != null)
            return false;
        return loginDataVoip != null ? loginDataVoip.equals(loginData.loginDataVoip) : loginData.loginDataVoip == null;

    }

    @Override
    public int hashCode() {
        int result = loginDataInternet != null ? loginDataInternet.hashCode() : 0;
        result = 31 * result + (loginDataVoip != null ? loginDataVoip.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "loginDataInternet=" + loginDataInternet +
                ", loginDataVoip=" + loginDataVoip +
                '}';
    }
}
