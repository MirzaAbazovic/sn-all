package de.augustakom.hurrican.service.cc.impl.evn.model;

/**
 *
 */
public class EvnServiceException extends Exception {

    private final EvnServiceFault evnServiceFault;

    public EvnServiceException(String s, EvnServiceFault evnServiceFault) {
        super(s);
        this.evnServiceFault = evnServiceFault;
    }

    public EvnServiceException(String s, Throwable throwable, EvnServiceFault evnServiceFault) {
        super(s, throwable);
        this.evnServiceFault = evnServiceFault;
    }

    public EvnServiceFault getEvnServiceFault() {
        return evnServiceFault;
    }
}
