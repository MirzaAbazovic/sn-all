/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.2007 13:26:56
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.CCProduktModel;


/**
 * Modellklasse fuer die Konfiguration von moeglichen Equipment-Parametern zu einem Produkt. <br> Die Klasse dient dazu,
 * die Auswahl von Equipments bei einem manuellen Rangierungsaufbau einzuschraenken.
 *
 *
 */
public class ProduktEQConfig extends AbstractCCIDModel implements CCProduktModel {

    /**
     * Konstant fuer 'eqTyp' kennzeichnet eine Konfiguration fuer EQ_IN Ports.
     */
    public static final String EQ_TYP_IN = "EQ_IN";
    /**
     * Konstant fuer 'eqTyp' kennzeichnet eine Konfiguration fuer EQ_OUT Ports.
     */
    public static final String EQ_TYP_OUT = "EQ_OUT";

    private Long prodId = null;
    private Long configGroup = null;
    private String eqTyp = null;
    private String eqParam = null;
    private String eqValue = null;
    private Boolean rangierungsPartDefault = null;
    private Boolean rangierungsPartAdditional = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCProduktModel#getProdId()
     */
    public Long getProdId() {
        return prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * Gibt die Konfiurationsgruppe zurueck. In einer Gruppe koennen sowohl EQ_IN als auch EQ_OUT Parameter zugeordnet
     * sein. <br> Auf einer Gruppe kann/darf pro EQ-Typ jeder Parameter jedoch nur einmal aufgenommen sein. <br>
     * (Achtung: die Gruppe ist immer in Zusammenhang mit dem Produkt zu sehen.)
     *
     * @return Returns the configGroup.
     */
    public Long getConfigGroup() {
        return configGroup;
    }

    /**
     * @param configGroup The configGroup to set.
     */
    public void setConfigGroup(Long configGroup) {
        this.configGroup = configGroup;
    }

    /**
     * Gibt den EQ-Typ (EQ_IN od. EQ_OUT) zurueck, zu dem der Parameter definiert ist.
     *
     * @return Returns the eqTyp.
     */
    public String getEqTyp() {
        return eqTyp;
    }

    /**
     * @param eqTyp The eqTyp to set.
     */
    public void setEqTyp(String eqTyp) {
        this.eqTyp = eqTyp;
    }

    /**
     * Gibt den Parameternamen zurueck, zu dem der Wert definiert ist. <br> Der Parametername muss identisch zu einem
     * Property aus der Modellklasse <code>Equipment</code> sein.
     *
     * @return Returns the eqParam.
     */
    public String getEqParam() {
        return eqParam;
    }

    /**
     * @param eqParam The eqParam to set.
     */
    public void setEqParam(String eqParam) {
        this.eqParam = eqParam;
    }

    /**
     * Wert, der zu dem angegebenen Equipment-Property verwendet werden soll.
     *
     * @return Returns the eqValue.
     */
    public String getEqValue() {
        return eqValue;
    }

    /**
     * @param eqValue The eqValue to set.
     */
    public void setEqValue(String eqValue) {
        this.eqValue = eqValue;
    }

    public Boolean getRangierungsPartDefault() {
        return rangierungsPartDefault;
    }

    public void setRangierungsPartDefault(Boolean rangierungsPartDefault) {
        this.rangierungsPartDefault = rangierungsPartDefault;
    }

    public Boolean getRangierungsPartAdditional() {
        return rangierungsPartAdditional;
    }

    public void setRangierungsPartAdditional(Boolean rangierungsPartAdditional) {
        this.rangierungsPartAdditional = rangierungsPartAdditional;
    }

}


