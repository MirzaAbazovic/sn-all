/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 15:03:12
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Modell-Klasse fuer die Definition von Connect-Daten.
 */
@Entity
@Table(name = "T_ENDSTELLE_CONNECT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_ENDSTELLE_CONNECT_0", allocationSize = 1)
public class EndstelleConnect extends AbstractCCIDModel {

    /**
     * Enum für die Schnittstelle einer Endstelle
     */
    public enum ESSchnittstelle {
        // @formatter:off
        UNDEFINED(" ", false),
        G703("G703", false),
        G704("G704", false),
        G957("G957", false),
        X21("X.21", false),
        _10BASET("10 Base T", true),
        _100BASET("100 Base T", true),
        _100BASEFX("100 Base FX", true),
        _1000BASET("1000 Base T", true),
        _1000BASESX("1000 Base SX", true),
        _1000BASELX("1000 Base LX", true),
        _10GE("10 GE", false),
        _10GB_LAN_MM("10Gbit/s LAN-PHY MM", false),
        _10GB_LANG_SM("10Gbit/s LAN-PHY SM", false),
        NETZKOPPLUNG("Netzkopplung", false),
        FIBRE_CHANNEL_MM("FibreChannel MM", false),
        FIBRE_CHANNEL_SM("FibreChannel SM", false),
        G_652D_E_2000_SM_LWL("G.652D E 2000 SM LWL", false),
        G_652D_LC_APC_SM_LWL("G.652D LC/APC SM LWL", false),
        G_652D_SC_APC_SM_LWL("G.652D SC/APC SM LWL", false),
        G_657A_LC_APC_SM_LWL("G.657A LC/APC SM LWL", false),
        G_657A_SC_APC_SM_LWL("G.657A SC/APC SM LWL", false),
        OS_2_LC_MM_LWL("OS 2 LC MM LWL", false),
        OS_2_SC_MM_LWL("OS 2 SC MM LWL", false),
        OS_3_LC_MM_LWL("OS 3 LC MM LWL", false),
        OS_3_SC_MM_LWL("OS 3 SC MM LWL", false),
        OS_4_LC_MM_LWL("OS 4 LC MM LWL", false),
        OS_4_SC_MM_LWL("OS 4 SC MM LWL", false);
        // @formatter:on

        private final String display;
        private final Boolean needsEinstellung;

        private ESSchnittstelle(String display, Boolean needsEinstellung) {
            this.display = display;
            this.needsEinstellung = needsEinstellung;
        }

        /**
         * @return spezielle Bezeichnung zum Anzeigen des Enum
         */
        @Override
        public String toString() {
            return display;
        }

        /**
         * @return true falls dieser Enum auch ESEinstellung benötigt
         */
        public boolean needsEinstellung() {
            return needsEinstellung;
        }
    }

    /**
     * Enum, um für bestimmte Schnittstellen zusätzliche Einstellungen festzulegen
     */
    public enum ESEinstellung {
        FULLDUPLEX("fullduplex"), HALFDUPLEX("halfduplex"), AUTONEG_ON("autoneg on");

        private final String display;

        private ESEinstellung(String display) {
            this.display = display;
        }

        /**
         * @return spezielle Bezeichnung zum Anzeigen des Enum
         */
        @Override
        public String toString() {
            return display;
        }
    }

    /**
     * Enum, um zu definieren, ob ein Router gekauft, gemietet oder vom Kunden ist
     */
    public enum ESRouterInfo {
        UNDEFINED(" "), MIETROUTER("Mietrouter"), KAUFROUTER("Kaufrouter"), KUNDENROUTER("Kundenrouter");

        private final String display;

        private ESRouterInfo(String display) {
            this.display = display;
        }

        /**
         * @return spezielle Bezeichnung zum Anzeigen des Enum
         */
        @Override
        public String toString() {
            return display;
        }
    }

    private Long endstelleId;
    private String gebaeude;
    private String etage;
    private String raum;
    private String schrank;
    private String uebergabe;
    private String bandbreite;
    private String defaultGateway;
    private ESSchnittstelle schnittstelle;
    private ESEinstellung einstellung;
    private ESRouterInfo routerinfo;
    private String routertyp;
    private String bemerkung;

    @Column(name = "ENDSTELLE_ID")
    @NotNull
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

    public String getSchrank() {
        return schrank;
    }

    public void setSchrank(String schrank) {
        this.schrank = schrank;
    }

    public String getUebergabe() {
        return uebergabe;
    }

    public void setUebergabe(String uebergabe) {
        this.uebergabe = uebergabe;
    }

    public String getBandbreite() {
        return bandbreite;
    }

    public void setBandbreite(String bandbreite) {
        this.bandbreite = bandbreite;
    }

    @Enumerated(EnumType.STRING)
    public ESSchnittstelle getSchnittstelle() {
        return schnittstelle;
    }

    public void setSchnittstelle(ESSchnittstelle schnittstelle) {
        this.schnittstelle = schnittstelle;
    }

    @Enumerated(EnumType.STRING)
    public ESEinstellung getEinstellung() {
        return einstellung;
    }

    public void setEinstellung(ESEinstellung einstellung) {
        this.einstellung = einstellung;
    }

    @Enumerated(EnumType.STRING)
    public ESRouterInfo getRouterinfo() {
        return routerinfo;
    }

    public void setRouterinfo(ESRouterInfo routerinfo) {
        this.routerinfo = routerinfo;
    }

    public String getRoutertyp() {
        return routertyp;
    }

    public void setRoutertyp(String routertyp) {
        this.routertyp = routertyp;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @Column(name = "DEFAULT_GATEWAY")
    public String getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }
}
