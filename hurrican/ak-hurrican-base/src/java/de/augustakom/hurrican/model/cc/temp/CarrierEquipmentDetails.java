/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2007 12:03:17
 */
package de.augustakom.hurrican.model.cc.temp;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.hurrican.exceptions.UncategorizedHurricanException;
import de.augustakom.hurrican.model.base.AbstractHurricanModel;


/**
 * Temp. Objekt fuer Detail-Angaben von Carrier-Equipments fuer die Ermittlung von Connect-Rangierungen. <br> Die
 * Zusammensetzung des Objekts wird vorerst aus einem Resource-File geladen.
 *
 *
 */
public class CarrierEquipmentDetails extends AbstractHurricanModel {

    private static final Logger LOGGER = Logger.getLogger(CarrierEquipmentDetails.class);

    private String name = null;
    private String uetv = null;
    private String rangSSType = null;
    private Long physiktypId = null;

    /**
     * Liest aus einem Resource-File die moeglichen Kombinationen von UETV und RangSSType aus und erstellt daraus
     * Objekte dieser Klasse. Die erstellten Objekte werden in einer Liste zurueck gegeben.
     *
     * @return Liste mit Objekten des Typs <code>CarrierEquipmentDetails</code>
     * @throws UncategorizedHurricanException
     *
     */
    public static List<CarrierEquipmentDetails> getCEDetails() throws UncategorizedHurricanException {
        try {
            List<CarrierEquipmentDetails> retVal = new ArrayList<CarrierEquipmentDetails>();

            ResourceReader rr = new ResourceReader(
                    "de.augustakom.hurrican.model.cc.temp.CarrierEquipmentDetails");
            String combinations = rr.getValue("possible.combinations");
            String[] combNames = StringUtils.split(combinations, ",");
            for (String combName : combNames) {
                CarrierEquipmentDetails ced = new CarrierEquipmentDetails();
                ced.setName(rr.getValue(combName + ".name"));
                ced.setUetv(rr.getValue(combName + ".uetv"));
                ced.setRangSSType(rr.getValue(combName + ".rang.ss.type"));
                ced.setPhysiktypId(Long.valueOf(rr.getValue(combName + ".physiktyp")));
                retVal.add(ced);
            }

            return retVal;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new UncategorizedHurricanException(
                    "Fehler bei der Ermittlung der Carrier-Details Kombinationen: " + e.getMessage(), e);
        }
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the uetv.
     */
    public String getUetv() {
        return uetv;
    }

    /**
     * @param uetv The uetv to set.
     */
    public void setUetv(String uetv) {
        this.uetv = uetv;
    }

    /**
     * @return Returns the rangSSType.
     */
    public String getRangSSType() {
        return rangSSType;
    }

    /**
     * @param rangSSType The rangSSType to set.
     */
    public void setRangSSType(String rangSSType) {
        this.rangSSType = rangSSType;
    }

    /**
     * @return Returns the physiktypId.
     */
    public Long getPhysiktypId() {
        return physiktypId;
    }

    /**
     * @param physiktypId The physiktypId to set.
     */
    public void setPhysiktypId(Long physiktypId) {
        this.physiktypId = physiktypId;
    }

}


