/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2005 09:27:31
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;


/**
 * View-Klasse zur Abbildung von UEVT-Daten. (Wird fuer das HVT-Tool benoetigt.)
 *
 *
 */
public class UevtCuDAView extends EqCuDAView {

    private Long uevtId = null;
    private String onkz = null;
    private Integer asb = null;
    private String hvtName = null;
    private Integer cudaVorbereitet = null;
    private Integer cudaFrei = null;
    private Integer cudaRangiert = null;

    private Integer cudaFreigegeben = null;
    private Integer cudaBelegt = null;
    private String rangLeiste1 = null;

    private Integer cudaVorhanden = null;
    private Integer delta = null;
    private Integer deltaScv = null;
    private Integer deltaBestellung = null;

    private Integer anzahlCuDAs = null;

    private Long niederlassungID = null;
    private String cluster = null;

    /**
     * Default-Konstruktor.
     */
    public UevtCuDAView() {
        super();
    }

    /**
     * Konstruktor mit Angabe eines Objekts vom Typ <code>HVTBelegungView</code>. Der Konstruktor kopiert die Werte des
     * Objekts <code>hvtView</code> in dieses Objekt.
     *
     * @param hvtView
     */
    public UevtCuDAView(HVTBelegungView hvtView) {
        super();
        if (hvtView != null) {
            setUevt(hvtView.getUevt());
            setHvtIdStandort(hvtView.getHvtIdStandort());
            setRangLeiste1(hvtView.getRangLeiste1());
            setRangSSType(hvtView.getRangSSType());
            setCudaPhysik(hvtView.getCudaPhysik());
            setCudaBelegt(hvtView.getBelegt());
            setCudaFrei(hvtView.getFrei());
        }
    }

    /**
     * Gibt die Summe der vorbereiteten, rangierten und freien CuDAs zurueck.
     *
     * @return
     */
    public Integer getCudaVorhanden() {
        if (cudaVorhanden != null) {
            return cudaVorhanden;
        }

        cudaVorhanden = NumberTools.add(getCudaVorbereitet(), getCudaRangiert());
        cudaVorhanden = NumberTools.add(cudaVorhanden, getCudaFrei());
        return cudaVorhanden;
    }

    /**
     * Gibt die Differenz zwischen vorhandenen und freigegebenen Stiften zurueck.
     *
     * @return
     */
    public Integer getDelta() {
        if (delta == null) {
            int a = getCudaVorhanden() != null ? getCudaVorhanden().intValue() : 0;
            int b = getCudaFreigegeben() != null ? getCudaFreigegeben().intValue() : 0;

            delta = Integer.valueOf(a - b);
        }
        return delta;
    }

    /**
     * Gibt die Differenz zwischen freigegebenen und belegten Stiften zurueck (<code>null</code>, falls einer der beiden
     * Werte <code>null</code> ist);
     *
     * @return
     */
    public Integer getDeltaScv() {
        if (deltaScv == null) {
            int a = getCudaFreigegeben() != null ? getCudaFreigegeben().intValue() : 0;
            int b = getCudaBelegt() != null ? getCudaBelegt().intValue() : 0;
            deltaScv = Integer.valueOf(a - b);
        }
        return deltaScv;
    }

    /**
     * Gibt die Differenz zwischen vorhandenen und bereits rangierten Stiften zurueck: vorhanden-(rangiert+vorbereitet)
     *
     * @return
     *
     */
    public Integer getDeltaBestellung() {
        if (deltaBestellung == null) {
            int a = getCudaVorhanden();
            int b = NumberTools.add(getCudaRangiert(), getCudaVorbereitet());

            deltaBestellung = Integer.valueOf(a - b);
        }
        return deltaBestellung;
    }

    /**
     * Funktion prueft den Schwellwert des Ressourcenmonitors
     *
     * @param schwellwert zu pruefender Schwellwert
     * @return Konstante aus RSMonitorConfig
     *
     */
    public int checkSchwellwert(Integer schwellwert) {
        // Kein Schwellwert definiert
        if (schwellwert == null) {
            return RSMonitorConfig.SCHWELLWERT_NICHT_DEFINIERT;
        }
        // Schwellwert unterschritten
        else if (NumberTools.isLess(getCudaFrei(), schwellwert)) {
            return RSMonitorConfig.SCHWELLWERT_UNTERSCHRITTEN;
        }
        // Schwellwert fast unterschritten
        else if (NumberTools.isLess(getCudaFrei(), schwellwert.doubleValue() * 1.25d)) {
            return RSMonitorConfig.SCHWELLWERT_FAST_UNTERSCHRITTEN;
        }
        else {
            return RSMonitorConfig.SCHWELLWERT_OK;
        }
    }

    /**
     * @return Returns the asb.
     */
    public Integer getAsb() {
        return asb;
    }

    /**
     * @param asb The asb to set.
     */
    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    /**
     * Gibt die Anzahl der CuDAs zurueck, die bereits vorrangiert sind.
     *
     * @return Returns the cudaRangiert.
     */
    public Integer getCudaRangiert() {
        return cudaRangiert;
    }

    /**
     * @param cudaRangiert The cudaRangiert to set.
     */
    public void setCudaRangiert(Integer cudaRangiert) {
        this.cudaRangiert = cudaRangiert;
    }

    /**
     * Gibt die Anzahl der CuDAs zurueck, die fuer die Rangierung vorbereitet sind
     *
     * @return Returns the cudaVorbereitet.
     */
    public Integer getCudaVorbereitet() {
        return cudaVorbereitet;
    }

    /**
     * @param cudaVorbereitet The cudaVorbereitet to set.
     */
    public void setCudaVorbereitet(Integer cudaVorbereitet) {
        this.cudaVorbereitet = cudaVorbereitet;
    }

    /**
     * @return Returns the cudaFrei.
     */
    public Integer getCudaFrei() {
        return cudaFrei;
    }

    /**
     * Gibt die Anzahl der CuDAs zurueck, die noch nicht verwendet werden.
     *
     * @param cudaFrei The cudaFrei to set.
     */
    public void setCudaFrei(Integer cudaFrei) {
        this.cudaFrei = cudaFrei;
    }

    /**
     * @return Returns the onkz.
     */
    public String getOnkz() {
        return onkz;
    }

    /**
     * @param onkz The onkz to set.
     */
    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    /**
     * @return Returns the ortsteil.
     */
    public String getHvtName() {
        return hvtName;
    }

    /**
     * @param ortsteil The ortsteil to set.
     */
    public void setHvtName(String ortsteil) {
        this.hvtName = ortsteil;
    }

    /**
     * @return Returns the uevtId.
     */
    public Long getUevtId() {
        return uevtId;
    }

    /**
     * @param uevtId The uevtId to set.
     */
    public void setUevtId(Long uevtId) {
        this.uevtId = uevtId;
    }

    /**
     * Gibt die Anzahl der bereits belegten (geschalteten) CuDAs zurueck.
     *
     * @return Returns the cudaBelegt.
     */
    public Integer getCudaBelegt() {
        return cudaBelegt;
    }

    /**
     * @param cudaBelegt The cudaBelegt to set.
     */
    public void setCudaBelegt(Integer cudaBelegt) {
        this.cudaBelegt = cudaBelegt;
    }

    /**
     * Gibt die Anzahl der noch freigegebenen CuDAs fuer einen UEVT zurueck.
     *
     * @return Returns the cudaFreigegeben.
     */
    public Integer getCudaFreigegeben() {
        return cudaFreigegeben;
    }

    /**
     * @param cudaFreigegeben The cudaFreigegeben to set.
     */
    public void setCudaFreigegeben(Integer cudaFreigegeben) {
        this.cudaFreigegeben = cudaFreigegeben;
    }

    /**
     * @return Returns the rangLeiste1.
     */
    public String getRangLeiste1() {
        return rangLeiste1;
    }

    /**
     * @param rangLeiste1 The rangLeiste1 to set.
     */
    public void setRangLeiste1(String rangLeiste1) {
        this.rangLeiste1 = rangLeiste1;
    }

    /**
     * Gibt die gesamte Anzahl von CuDAs einer best. Leiste zurueck.
     *
     * @return Returns the anzahlCuDAs.
     */
    public Integer getAnzahlCuDAs() {
        return anzahlCuDAs;
    }

    /**
     * @param anzahlCuDAs The anzahlCuDAs to set.
     */
    public void setAnzahlCuDAs(Integer anzahlCuDAs) {
        this.anzahlCuDAs = anzahlCuDAs;
    }

    /**
     * Gibt die Anzahl rangierter und vorbereiteter Cudas zurueck
     *
     * @return
     *
     */
    public Integer getCudaRangVorb() {
        return NumberTools.add(getCudaVorbereitet(), getCudaRangiert());
    }

    /**
     * Setzt den Namen der Niederlassung zu dieser Entitaet
     *
     * @return
     *
     */
    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungID = niederlassungId;
    }

    /**
     * Gibt den Namen der Niederlassung zu dieser Entitaet zurueck
     *
     * @return
     *
     */
    public Long getNiederlassungId() {
        return niederlassungID;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}


