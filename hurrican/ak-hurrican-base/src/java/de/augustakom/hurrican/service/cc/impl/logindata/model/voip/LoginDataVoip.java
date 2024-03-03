package de.augustakom.hurrican.service.cc.impl.logindata.model.voip;

import java.io.*;
import java.util.*;

/**
 *  Sprach-/SIP-Zugangsdata
 *
 */
public class LoginDataVoip implements Serializable {

    private final List<LoginDataVoipDn> voipDnList;

    public LoginDataVoip(List<LoginDataVoipDn> voipDnList) {
        this.voipDnList = voipDnList;
    }

    public List<LoginDataVoipDn> getVoipDnList() {
        return voipDnList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LoginDataVoip that = (LoginDataVoip) o;

        return voipDnList != null ? voipDnList.equals(that.voipDnList) : that.voipDnList == null;

    }

    @Override
    public int hashCode() {
        return voipDnList != null ? voipDnList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LoginDataVoip{" +
                "voipDnList=" + voipDnList +
                '}';
    }
}
