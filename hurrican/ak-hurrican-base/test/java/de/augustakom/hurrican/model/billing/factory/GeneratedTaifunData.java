/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.01.2015
 */
package de.augustakom.hurrican.model.billing.factory;

import java.util.*;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceFritzBox;
import de.augustakom.hurrican.model.billing.DnOnkz2Carrier;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Person;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.Rufnummer;


public class GeneratedTaifunData {

    private TaifunDataHandler taifunDataHandler;

    private Kunde kunde;
    private Person kundenBetreuer;
    private Adresse address;
    private RInfo rInfo;
    private BAuftrag billingAuftrag;
    private List<BAuftragPos> billingAuftragPositionen = new ArrayList<>();
    private List<Rufnummer> dialNumbers = new ArrayList<>();
    private List<Device> devices = new ArrayList<>();
    private List<DeviceFritzBox> devicesFritzBox = new ArrayList<>();
    private boolean persisted = false;

    public GeneratedTaifunData(TaifunDataHandler taifunDataHandler, Kunde kunde, Person kundenBetreuer, RInfo rInfo, Adresse address) {
        this.taifunDataHandler = taifunDataHandler;
        this.kunde = kunde;
        this.kundenBetreuer = kundenBetreuer;
        this.rInfo = rInfo;
        this.address = address;
    }

    /**
     * Persistiert alle enthaltenen Objekte.
     * @return
     */
    public GeneratedTaifunData persist() {
        if(!persisted) {
            persistCustomerDataOnly();
            persistOrderDataOnly();
            persisted = true;
        }
        return this;
    }


    /**
     * Persistiert die enthaltenen, auftrags-bezogenen Objekte. <br/>
     * Kunde/Adresse/RInfo werden hier nicht mehr persistiert!
     * @return
     */
    public GeneratedTaifunData persistOrderDataOnly() {
        taifunDataHandler.insert(billingAuftrag);
        for (Device device : devices) {
            taifunDataHandler.insert(device);
        }
        for (DeviceFritzBox deviceFritzBox : devicesFritzBox) {
            taifunDataHandler.insert(deviceFritzBox);
        }

        for (BAuftragPos auftragPos : billingAuftragPositionen) {
            taifunDataHandler.insert(auftragPos);
        }

        for (Rufnummer dialNumber : dialNumbers) {
            taifunDataHandler.insert(dialNumber);

            if (StringUtils.isNotEmpty(dialNumber.getOnKz()) && StringUtils.isNotEmpty(dialNumber.getActCarrier())) {
                // also insert DnOnkz2Carrier
                final DnOnkz2Carrier dnOnkz2Carrier = new DnOnkz2Carrier();
                dnOnkz2Carrier.setCarrier(dialNumber.getActCarrier());
                dnOnkz2Carrier.setOnkz(dialNumber.getOnKz());
                taifunDataHandler.insert(dnOnkz2Carrier);
            }
        }

        return this;
    }

    /**
     * Persistiert Kunden Daten
     */
    public GeneratedTaifunData persistCustomerDataOnly() {
        taifunDataHandler.insert(kunde);
        taifunDataHandler.insert(address);
        if (kundenBetreuer != null) {
            taifunDataHandler.insert(kundenBetreuer);
            kunde.setKundenbetreuerNo(kundenBetreuer.getPersonNo());
        }

        kunde.setPostalAddrNo(address.getAdresseNo());
        taifunDataHandler.update(kunde);

        taifunDataHandler.insert(rInfo);
        return this;
    }


    public void addDevice(Device device) {
        devices.add(device);
    }

    public void addDeviceFritzBox(DeviceFritzBox deviceFritzBox) {
        devicesFritzBox.add(deviceFritzBox);
    }

    public void addDialNumber(Rufnummer dialNumber) {
        dialNumbers.add(dialNumber);
    }

    public void addAuftragPos(BAuftragPos auftragPos) {
        billingAuftragPositionen.add(auftragPos);
    }

    public Kunde getKunde() {
        return kunde;
    }

    public RInfo getRInfo() {
        return rInfo;
    }

    public BAuftrag getBillingAuftrag() {
        return billingAuftrag;
    }

    public void setBillingAuftrag(BAuftrag billingAuftrag) {
        this.billingAuftrag = billingAuftrag;
    }

    public List<Rufnummer> getDialNumbers() {
        return dialNumbers;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public List<DeviceFritzBox> getDevicesFritzBox() {
        return devicesFritzBox;
    }

    public Adresse getAddress() {
        return address;
    }

    public List<BAuftragPos> getBillingAuftragPositionen() {
        return billingAuftragPositionen;
    }

    public void setKundenname(String name) {
        getKunde().setName(name);
        getAddress().setName(name);
    }

    public void setKundenvorname(String vorname) {
        getKunde().setVorname(vorname);
        getAddress().setVorname(vorname);
    }

    public Person getKundenBetreuer() {
        return kundenBetreuer;
    }

    public RInfo getrInfo() {
        return rInfo;
    }
}
