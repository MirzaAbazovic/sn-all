/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2014
 */
package de.mnet.wbci.model;

import java.io.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;

/**
 * Helper VO for all WITA relevant order data
 *
 *
 */
public class WbciWitaOrderDataVO implements Serializable {
    private WbciGeschaeftsfall wbciGeschaeftsfall;
    private Endstelle endstelle;
    private AuftragDaten auftragDaten;
    private Carrierbestellung carrierbestellung;
    private Carrier carrier;

    public WbciWitaOrderDataVO() {
        // just for tests!
    }

    public WbciWitaOrderDataVO(WbciGeschaeftsfall wbciGeschaeftsfall, AuftragDaten auftragDaten, Carrierbestellung carrierbestellung, Carrier carrier, Endstelle endstelle) {
        this.wbciGeschaeftsfall = wbciGeschaeftsfall;
        this.auftragDaten = auftragDaten;
        this.carrierbestellung = carrierbestellung;
        this.carrier = carrier;
        this.endstelle = endstelle;
    }

    public WbciGeschaeftsfall getWbciGeschaeftsfall() {
        return wbciGeschaeftsfall;
    }

    public void setWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        this.wbciGeschaeftsfall = wbciGeschaeftsfall;
    }

    public AuftragDaten getAuftragDaten() {
        return auftragDaten;
    }

    public void setAuftragDaten(AuftragDaten auftragDaten) {
        this.auftragDaten = auftragDaten;
    }

    public Carrierbestellung getCarrierbestellung() {
        return carrierbestellung;
    }

    public void setCarrierbestellung(Carrierbestellung carrierbestellung) {
        this.carrierbestellung = carrierbestellung;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public Endstelle getEndstelle() {
        return endstelle;
    }

    public void setEndstelle(Endstelle endstelle) {
        this.endstelle = endstelle;
    }

    public String getVorabstimmungsId() {
        return wbciGeschaeftsfall != null ? wbciGeschaeftsfall.getVorabstimmungsId() : null;
    }

    public Long getCbId() {
        return (carrierbestellung != null) ? carrierbestellung.getId() : null;
    }

    public Long getCarrierId() {
        return (carrier != null) ? carrier.getId() : null;
    }
}
