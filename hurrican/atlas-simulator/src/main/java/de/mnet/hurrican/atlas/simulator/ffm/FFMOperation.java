package de.mnet.hurrican.atlas.simulator.ffm;

/**
 *
 */
public enum FFMOperation {

    CREATE_ORDER("createOrder", "/WorkforceService/createOrder"),
    DELETE_ORDER("deleteOrder", "/WorkforceService/deleteOrder"),
    NOTIFY_ORDER_UPDATE("notifyOrderUpdate", "/WorkforceService/notifyOrderUpdate"),
    NOTIFY_ORDER_FEEDBACK("notifyOrderFeedback", "/WorkforceService/notifyOrderFeedback");

    private final String name;
    private final String soapAction;

    FFMOperation(String name, String soapAction) {
        this.name = name;
        this.soapAction = soapAction;
    }

    public String getName() {
        return name;
    }

    public String getSoapAction() {
        return soapAction;
    }

    @Override
    public String toString() {
        return soapAction;
    }
}
