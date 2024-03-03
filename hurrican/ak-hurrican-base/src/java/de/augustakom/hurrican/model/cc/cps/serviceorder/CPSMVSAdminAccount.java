/**
 *
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Modellklasse zur Abbildung von MVS-Enterprise-Admin-Account-Daten fuer die CPS-Provisionierung.
 */
@XStreamAlias("ADMIN_ACCOUNT")
public class CPSMVSAdminAccount extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("USERNAME")
    private String username;

    @XStreamAlias("PASSWORD")
    private String password;

    @XStreamAlias("EMAIL")
    private String email;

    public CPSMVSAdminAccount(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public CPSMVSAdminAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
