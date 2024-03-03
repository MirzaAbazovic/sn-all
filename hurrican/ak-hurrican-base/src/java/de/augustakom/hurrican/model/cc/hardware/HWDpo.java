package de.augustakom.hurrican.model.cc.hardware;

/**
 * HWDpo
 */
public class HWDpo extends HWOltChild {

    private static final long serialVersionUID = 8098407587714031156L;

    /**
     * Huawei Typen MA5651
     */
    public static String DPO_TYPE_MA5651 = "MA5651";

    private String dpoType;
    private String chassisIdentifier;
    private String chassisSlot;

    public String getDpoType() {
        return dpoType;
    }

    public void setDpoType(String dpoType) {
        this.dpoType = dpoType;
    }

    public String getChassisIdentifier() {
        return chassisIdentifier;
    }

    public void setChassisIdentifier(String chassisIdentifier) {
        this.chassisIdentifier = chassisIdentifier;
    }

    public String getChassisSlot() {
        return chassisSlot;
    }

    public void setChassisSlot(String chassisSlot) {
        this.chassisSlot = chassisSlot;
    }

}
