package de.augustakom.hurrican.service.cc.impl.evn.model;

import java.io.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.exceptions.AtlasErrorHandlingService;

/**
 * Evn service faults
 * Delivered via {@link AtlasErrorHandlingService}
 */
public class EvnServiceFault implements Serializable {

    public enum FaultEnum {
        EVN_STATUS_CHANGE_IN_PROGRESS("HUR-011"),
        EVN_STATUS_ACCOUNT_NOT_FOUND("HUR-012"),
        EVN_STATUS_CPS_ERROR("HUR-013"),
        EVN_STATUS_TECHNICAL_ERROR("HUR-006");  // --> CustomerOrderServiceErrorCode.UNKNOWN("HUR-006")

        private String externalErrorCode;

        FaultEnum(String externalErrorCode) {
            this.externalErrorCode = externalErrorCode;
        }

        public String getExternalErrorCode() {
            return externalErrorCode;
        }
    }

    private final FaultEnum faultEnum;
    private final AuftragDaten auftragDaten;
    private final String accountNumber;

    public EvnServiceFault(FaultEnum faultEnum, AuftragDaten auftragDaten, String accountNumber) {
        this.faultEnum = faultEnum;
        this.auftragDaten = auftragDaten;
        this.accountNumber = accountNumber;
    }

    public static EvnServiceFault create(FaultEnum faultEnum, AuftragDaten auftragDaten, String accountNumber) {
        return new EvnServiceFault(faultEnum, auftragDaten, accountNumber);
    }

    public FaultEnum getFaultEnum() {
        return faultEnum;
    }

    public AuftragDaten getAuftragDaten() {
        return auftragDaten;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
