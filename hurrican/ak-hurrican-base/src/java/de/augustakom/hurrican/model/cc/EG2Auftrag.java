/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.06.2006 09:45:19
 */
package de.augustakom.hurrican.model.cc;

import static de.augustakom.hurrican.model.cc.EndgeraetIp.AddressType.*;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Bildet das Mapping eines Endgerätetyps auf einen Auftrag ab.
 *
 *
 *
 */
public class EG2Auftrag extends AbstractCCIDModel implements CCAuftragModel {

    private static final long serialVersionUID = -8303625657461411055L;

    private Long auftragId;
    private Long egId;
    private Long montageart;
    private String bemerkung;
    private String bearbeiter;
    private String raum;
    private String etage;
    private String gebaeude;
    private Boolean deactivated;
    private IPAddress ipAdresseRef;

    /**
     * Optionale Zuordnung des Endgerats zu einer Endstelle
     */
    private Long endstelleId;
    private Set<EndgeraetIp> endgeraetIps = new HashSet<>();
    private Set<Routing> routings = new HashSet<>();
    // Dummy um EG2Auftrag kaskadiert loeschen zu koennen
    private Set<EGConfig> egConfigs = new HashSet<>();

    /**
     * Erzeugt eine Kopie des angegebenen Objekts. Diese Kopie kann dazu verwendet werden, die Endgeraete-Zuordnung fuer
     * einen neuen Auftrag zu verwenden.
     *
     * @param eg2a          zu kopierende EG2Auftrag Daten
     * @param destAuftragId ID des Ziel-Auftrags, dem das neue Objekt zugeordnet werden soll
     * @return das kopierte Objekt
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static EG2Auftrag createCopy(EG2Auftrag eg2a, Long destAuftragId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        EG2Auftrag newEG2A = new EG2Auftrag();
        PropertyUtils.copyProperties(newEG2A, eg2a);
        newEG2A.setId(null);
        newEG2A.setAuftragId(destAuftragId);
        if (newEG2A.getMontageart() != null) {
            newEG2A.setMontageart(Montageart.MONTAGEART_UEBERNAHME);
        }

        // IPs kopieren
        Set<EndgeraetIp> egIPs = new HashSet<>();
        for (EndgeraetIp ip : eg2a.getEndgeraetIps()) {
            EndgeraetIp copyOfIp = new EndgeraetIp();
            PropertyUtils.copyProperties(copyOfIp, ip);
            copyOfIp.setId(null);
            egIPs.add(copyOfIp);
            if (copyOfIp.getIpAddressRef() != null) {
                copyOfIp.setIpAddressRef(copyIPAddress(copyOfIp.getIpAddressRef()));
            }
        }
        newEG2A.setEndgeraetIps(egIPs);

        // Routings kopieren
        Set<Routing> routings = new HashSet<>();
        for (Routing routing : eg2a.getRoutings()) {
            Routing copyOfRouting = new Routing();
            PropertyUtils.copyProperties(copyOfRouting, routing);
            copyOfRouting.setId(null);
            if (copyOfRouting.getDestinationAdressRef() != null) {
                copyOfRouting.setDestinationAdressRef(copyIPAddress(copyOfRouting.getDestinationAdressRef()));
            }
            routings.add(copyOfRouting);
        }
        newEG2A.setRoutings(routings);

        return newEG2A;
    }

    private static IPAddress copyIPAddress(IPAddress toCopy) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (toCopy == null) {return null;}
        IPAddress ipAddressCopy = new IPAddress();
        PropertyUtils.copyProperties(ipAddressCopy, toCopy);
        ipAddressCopy.setId(null);
        ipAddressCopy.setGueltigVon(null);
        return ipAddressCopy;

    }

    /**
     * Fuegt eine Endgeraete-IP hinzu.
     */
    public void addEndgeraetIp(EndgeraetIp endgeraetIp) {
        endgeraetIps.add(endgeraetIp);
    }

    /**
     * Loescht eine Endgeraete-IP.
     */
    public void removeEndgeraetIp(EndgeraetIp endgeraetIp) {
        endgeraetIps.remove(endgeraetIp);
    }

    private Set<EndgeraetIp> getEndgeraetIpsOfType(EndgeraetIp.AddressType addressType) {
        Set<EndgeraetIp> endgeraetIpsToReturn = new HashSet<>();
        for (EndgeraetIp ip : endgeraetIps) {
            switch (addressType) {
                case LAN:
                    if (ip.isLanIp()) {
                        endgeraetIpsToReturn.add(ip);
                    }
                    break;
                case WAN:
                    if (ip.isWanIp()) {
                        endgeraetIpsToReturn.add(ip);
                    }
                    break;
                case LAN_VRRP:
                    if (ip.isLanVRRPIp()) {
                        endgeraetIpsToReturn.add(ip);
                    }
                    break;
                default:
                    break;
            }
        }
        return endgeraetIpsToReturn;
    }

    public Set<EndgeraetIp> getLanEndgeraetIps() {
        return getEndgeraetIpsOfType(LAN);
    }

    public Set<EndgeraetIp> getVrrpEndgeraetIps() {
        return getEndgeraetIpsOfType(LAN_VRRP);
    }

    public Set<EndgeraetIp> getWanEndgeraetIps() {
        return getEndgeraetIpsOfType(WAN);
    }

    /**
     * Fuegt dem Endgeraet eine statische Route hinzu.
     */
    public void addRouting(Routing routing) {
        routings.add(routing);
    }

    /**
     * Entfernt eine statische Route des Endgeraets.
     */
    public void removeRouting(Routing routing) {
        routings.remove(routing);
    }

    public Long getEgId() {
        return egId;
    }

    public void setEgId(Long egId) {
        this.egId = egId;
    }

    @Override
    public Long getAuftragId() {
        return this.auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getMontageart() {
        return this.montageart;
    }

    public void setMontageart(Long montageart) {
        this.montageart = montageart;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public String getEtage() {
        return etage;
    }

    public void setEtage(String etage) {
        this.etage = etage;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(String raum) {
        this.raum = raum;
    }

    public Set<EndgeraetIp> getEndgeraetIps() {
        return endgeraetIps;
    }

    public void setEndgeraetIps(Set<EndgeraetIp> endgeraetIps) {
        if (endgeraetIps != null) {
            this.endgeraetIps = endgeraetIps;
        }
        else {
            this.endgeraetIps = new HashSet<>();
        }
    }

    public Set<Routing> getRoutings() {
        return routings;
    }

    public void setRoutings(Set<Routing> routings) {
        if (routings != null) {
            this.routings = routings;
        }
        else {
            this.routings = new HashSet<>();
        }
    }

    public Set<EGConfig> getEgConfigs() {
        return egConfigs;
    }

    public void setEgConfigs(Set<EGConfig> egConfigs) {
        if (egConfigs != null) {
            this.egConfigs = egConfigs;
        }
        else {
            this.egConfigs = new HashSet<>();
        }
    }

    public Long getEndstelleId() {
        return endstelleId;
    }

    public void setEndstelleId(Long endstelleId) {
        this.endstelleId = endstelleId;
    }

    public String getGebaeude() {
        return gebaeude;
    }

    public void setGebaeude(String gebaeude) {
        this.gebaeude = gebaeude;
    }


    public Boolean getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        this.deactivated = deactivated;
    }

    public IPAddress getIpAdresseRef() {
        return ipAdresseRef;
    }

    public void setIpAdresseRef(IPAddress ipAdresseRef) {
        this.ipAdresseRef = ipAdresseRef;
    }

}
