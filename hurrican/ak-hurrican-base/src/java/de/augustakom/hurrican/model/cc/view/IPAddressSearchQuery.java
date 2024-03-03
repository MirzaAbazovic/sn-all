/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 16:06:19
 */
package de.augustakom.hurrican.model.cc.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.net.IPAddressConverter;
import de.augustakom.hurrican.model.base.AbstractHurricanQuery;

/**
 * Kapselt Suchparameter fuer die IP-Adresssuche.
 */
public class IPAddressSearchQuery extends AbstractHurricanQuery {

    private String ipBinary;
    private boolean ipV4Search;
    private boolean findOnlyActive;
    private Integer resultLimit;

    public static IPAddressSearchQuery createEmptyIPAddressSearchQuery() {
        return new IPAddressSearchQuery();
    }

    /**
     * Konstruktor fuer Suche ohne Limit
     *
     * @param searchPattern eine IP-Addresse V4/V6 deren Binaerrepraesentation zur Suche verwendet wird
     * @param ipV4Search    flag das angibt ob nach einer IPV4 (true) oder IPv6(false) gesucht werden soll
     * @param onlyActive    flag such nach historisierten Daten (true=>nein, false=ja)
     */
    public IPAddressSearchQuery(String searchPattern, boolean ipV4Search, boolean onlyActive) {
        super();
        setIpSearchPattern(searchPattern);
        this.ipV4Search = ipV4Search;
        this.findOnlyActive = onlyActive;
    }

    /**
     * Konstruktor fuer Suche mit Limit
     *
     * @param searchPattern eine IP-Addresse V4/V6 deren Binaerrepraesentation zur Suche verwendet wird
     * @param ipV4Search    flag das angibt ob nach einer IPV4 (true) oder IPv6(false) gesucht werden soll
     * @param onlyActive    flag such nach historisierten Daten (true=>nein, false=ja)
     * @param resultLimit   maximale Groesse des Resultsets einer Suche
     */
    public IPAddressSearchQuery(String searchPattern, boolean ipV4Search, boolean findOnlyActive, Integer resultLimit) {
        super();
        setIpSearchPattern(searchPattern);
        this.ipV4Search = ipV4Search;
        this.findOnlyActive = findOnlyActive;
        this.resultLimit = resultLimit;
    }

    /**
     * Konstruktor um eine leeres Suchobjekt zu erzeugen
     */
    public IPAddressSearchQuery() {
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return StringUtils.isEmpty(ipBinary);
    }

    /**
     * @param ipSearchPattern das IP-Pattern nach dem gesucht werden soll. Zulaessig sind bspws. 192.168.2.1,
     *                        192.168.2.1/16; wenn eine Praefix Laenge angegeben ist wird diese beachtet, selbst wenn es
     *                        sich um eine vollstaendige Adresse handelt
     */
    public void setIpSearchPattern(String ipSearchPattern) {
        ipBinary = IPAddressConverter.parseIPAddress(ipSearchPattern, false);
    }

    /**
     * @return die Binaerrepraesentation des IPv4-Suchpatterns
     */
    public String getIpBinary() {
        return ipBinary;
    }

    /**
     * @return
     */
    public boolean isV4Search() {
        return ipV4Search;
    }

    public boolean isFindOnlyActive() {
        return findOnlyActive;
    }

    public Integer getResultLimit() {
        return resultLimit;
    }
}
