package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;

public class PortStatisticsView extends AbstractCCModel {

    private Integer asb;
    private String onkz;
    private String niederlassung;
    private String monat;
    private long analogPorts;
    private long isdnPorts;
    private long isdnTkPorts;
    private long telephonySumme;
    private long telephonyDiff;
    private float telephonyProzent;
    private long adslPorts;
    private long adslDiff;
    private float adslProzent;
    private long sdslPorts;
    private long pmxPorts;

    public Integer getAsb() {
        return asb;
    }

    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public String getMonat() {
        return monat;
    }

    public void setMonat(String monat) {
        this.monat = monat;
    }

    public long getAnalogPorts() {
        return analogPorts;
    }

    public void setAnalogPorts(long analogPorts) {
        this.analogPorts = analogPorts;
    }

    public long getIsdnPorts() {
        return isdnPorts;
    }

    public void setIsdnPorts(long isdnPorts) {
        this.isdnPorts = isdnPorts;
    }

    public long getIsdnTkPorts() {
        return isdnTkPorts;
    }

    public void setIsdnTkPorts(long isdnTkPorts) {
        this.isdnTkPorts = isdnTkPorts;
    }

    public long getTelephonySumme() {
        return telephonySumme;
    }

    public void setTelephonySumme(long telephonySumme) {
        this.telephonySumme = telephonySumme;
    }

    public long getTelephonyDiff() {
        return telephonyDiff;
    }

    public void setTelephonyDiff(long telephonyDiff) {
        this.telephonyDiff = telephonyDiff;
    }

    public float getTelephonyProzent() {
        return telephonyProzent;
    }

    public void setTelephonyProzent(float telephonyProzent) {
        this.telephonyProzent = telephonyProzent;
    }

    public long getAdslPorts() {
        return adslPorts;
    }

    public void setAdslPorts(long adslPorts) {
        this.adslPorts = adslPorts;
    }

    public long getAdslDiff() {
        return adslDiff;
    }

    public void setAdslDiff(long adslDiff) {
        this.adslDiff = adslDiff;
    }

    public float getAdslProzent() {
        return adslProzent;
    }

    public void setAdslProzent(float adslProzent) {
        this.adslProzent = adslProzent;
    }

    public long getSdslPorts() {
        return sdslPorts;
    }

    public void setSdslPorts(long sdslPorts) {
        this.sdslPorts = sdslPorts;
    }

    public long getPmxPorts() {
        return pmxPorts;
    }

    public void setPmxPorts(long pmxPorts) {
        this.pmxPorts = pmxPorts;
    }

    public void addPmxPorts(long anzahl) {
        pmxPorts += anzahl;
    }

    public void addIsdnTkPorts(long anzahl) {
        isdnTkPorts += anzahl;
        telephonySumme += anzahl;
    }

    public void addAnalogPorts(long anzahl) {
        analogPorts += anzahl;
        telephonySumme += anzahl;
    }

    public void addIsdnPorts(long anzahl) {
        isdnPorts += anzahl;
        telephonySumme += anzahl;
    }

    public void addAdslPorts(long anzahl) {
        adslPorts += anzahl;
    }

    public void addSdslPorts(long anzahl) {
        sdslPorts += anzahl;
    }

}
