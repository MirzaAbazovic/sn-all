package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 *
 */
@XStreamAlias("RESOURCE_ORDER")
public class CpsResourceOrder extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("ACCESS_DEVICE")
    private CpsAccessDevice accessDevice;

    public CpsAccessDevice getAccessDevice() {
        return accessDevice;
    }

    public void setAccessDevice(CpsAccessDevice accessDevice) {
        this.accessDevice = accessDevice;
    }
}
