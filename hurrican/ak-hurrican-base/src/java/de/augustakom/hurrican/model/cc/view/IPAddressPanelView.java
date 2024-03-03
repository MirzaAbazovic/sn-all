/**
 *
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.IPAddress;

/**
 * View-Modell, fuer die Anzeige der zugewiesenen IP Adressen pro Auftrag
 */
public class IPAddressPanelView {

    private String status;
    private IPAddress ipAddress;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setIpAddress(IPAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }
}
