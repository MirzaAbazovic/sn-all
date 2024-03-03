/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.augustakom.hurrican.model.cc.temp;


/**
 * Temp. Modell, um die Optionen fuer eine Kuendigungs-Ruecknahme zu definieren.
 */
public class RevokeTerminationModel extends AbstractRevokeModel {

    private Boolean isAuftragStatus = null;
    private Boolean isTechLeistungen = null;
    private Boolean isRangierung = null;

    public Boolean getIsAuftragStatus() {
        return isAuftragStatus;
    }

    public void setIsAuftragStatus(Boolean isAuftragStatus) {
        this.isAuftragStatus = isAuftragStatus;
    }

    public Boolean getIsTechLeistungen() {
        return isTechLeistungen;
    }

    public void setIsTechLeistungen(Boolean isTechLeistungen) {
        this.isTechLeistungen = isTechLeistungen;
    }

    public Boolean getIsRangierung() {
        return isRangierung;
    }

    public void setIsRangierung(Boolean isRangierung) {
        this.isRangierung = isRangierung;
    }

}
