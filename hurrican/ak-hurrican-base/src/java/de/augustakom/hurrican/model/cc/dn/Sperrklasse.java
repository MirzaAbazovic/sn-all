/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.01.2006 13:32:09
 */
package de.augustakom.hurrican.model.cc.dn;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Modell stellt die Sperrklassen zu den Rufnummernleistungen dar
 *
 *
 */
public class Sperrklasse extends AbstractCCIDModel {

    public enum SperrklassenTypEnum {

        ABGEHEND(1L, "abgehend"),
        NATIONAL(2L, "national"),
        INNOVATIVE_DIENSTE(3L, "innovativeDienste"),
        MABEZ(4L, "mabez"),
        MOBIL(5L, "mobil"),
        VPN(6L, "vpn"),
        PRD(7L, "prd"),
        AUSKUNFTSDIENSTE(8L, "auskunftsdienste"),
        INTERNATIONAL(9L, "international"),
        OFFLINE(10L, "offline"),
        PREMIUM_SERVICES_INT(11L, "premiumServicesInt");

        SperrklassenTypEnum(Long id, String propertyName) {
            this.id = id;
            this.propertyName = propertyName;
        }

        private final Long id;
        private final String propertyName;

        public Long getId() {
            return id;
        }

        public String getPropertyName() {
            return propertyName;
        }

    } // end

    // Konstanten die spezielle Sperrklassen definieren
    public static final Integer SPERRKLASSE_KEINE = 0;
    public static final Integer SPERRKLASSE_STANDARD = 60;

    private Integer sperrklasse = null;
    private Integer sperrklasseIms = null;
    private Boolean abgehend = null;
    private Boolean national = null;
    private Boolean innovativeDienste = null;
    private Boolean mabez = null;
    private Boolean mobil = null;
    private Boolean vpn = null;
    private Boolean prd = null;
    private Boolean auskunftsdienste = null;
    private Boolean international = null;
    private Boolean offline = null;
    private Boolean premiumServicesInt = null;
    private String name;

    /**
     * liefert abhaengig vom Typ des Switches die gespeicherte Sperrklassennummer
     *
     * @param hwSwitchType
     * @return
     */
    @Transient
    public Integer getSperrklasseByHwSwitchType(HWSwitchType hwSwitchType) {
        if (HWSwitchType.isImsOrNsp(hwSwitchType)) {
            return getSperrklasseIms();
        }
        else {
            return getSperrklasse();
        }
    }

    public Integer getSperrklasse() {
        return sperrklasse;
    }

    public void setSperrklasse(Integer sperrklasse) {
        this.sperrklasse = sperrklasse;
    }

    public Integer getSperrklasseIms() {
        return sperrklasseIms;
    }

    public void setSperrklasseIms(Integer sperrklasseIms) {
        this.sperrklasseIms = sperrklasseIms;
    }

    public Boolean getAbgehend() {
        return abgehend;
    }

    public void setAbgehend(Boolean abgehend) {
        this.abgehend = abgehend;
    }

    public Boolean getAuskunftsdienste() {
        return auskunftsdienste;
    }

    public void setAuskunftsdienste(Boolean auskunftsdienste) {
        this.auskunftsdienste = auskunftsdienste;
    }

    public Boolean getInnovativeDienste() {
        return innovativeDienste;
    }

    public void setInnovativeDienste(Boolean innovativeDienste) {
        this.innovativeDienste = innovativeDienste;
    }

    public Boolean getMabez() {
        return mabez;
    }

    public void setMabez(Boolean mabez) {
        this.mabez = mabez;
    }

    public Boolean getMobil() {
        return mobil;
    }

    public void setMobil(Boolean mobil) {
        this.mobil = mobil;
    }

    public Boolean getNational() {
        return national;
    }

    public void setNational(Boolean national) {
        this.national = national;
    }

    public Boolean getPrd() {
        return prd;
    }

    public void setPrd(Boolean prd) {
        this.prd = prd;
    }

    public Boolean getVpn() {
        return vpn;
    }

    public void setVpn(Boolean vpn) {
        this.vpn = vpn;
    }

    public Boolean getInternational() {
        return international;
    }

    public void setInternational(Boolean international) {
        this.international = international;
    }

    public Boolean getOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public Boolean getPremiumServicesInt() {
        return premiumServicesInt;
    }

    public void setPremiumServicesInt(Boolean premiumServicesInt) {
        this.premiumServicesInt = premiumServicesInt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((abgehend == null) ? 0 : abgehend.hashCode());
        result = (prime * result) + ((auskunftsdienste == null) ? 0 : auskunftsdienste.hashCode());
        result = (prime * result) + ((innovativeDienste == null) ? 0 : innovativeDienste.hashCode());
        result = (prime * result) + ((international == null) ? 0 : international.hashCode());
        result = (prime * result) + ((mabez == null) ? 0 : mabez.hashCode());
        result = (prime * result) + ((mobil == null) ? 0 : mobil.hashCode());
        result = (prime * result) + ((national == null) ? 0 : national.hashCode());
        result = (prime * result) + ((offline == null) ? 0 : offline.hashCode());
        result = (prime * result) + ((prd == null) ? 0 : prd.hashCode());
        result = (prime * result) + ((premiumServicesInt == null) ? 0 : premiumServicesInt.hashCode());
        result = (prime * result) + ((sperrklasse == null) ? 0 : sperrklasse.hashCode());
        result = (prime * result) + ((sperrklasseIms == null) ? 0 : sperrklasseIms.hashCode());
        result = (prime * result) + ((vpn == null) ? 0 : vpn.hashCode());
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Sperrklasse)) {
            return false;
        }
        Sperrklasse other = (Sperrklasse) obj;
        if (abgehend == null) {
            if (other.abgehend != null) {
                return false;
            }
        }
        else if (!abgehend.equals(other.abgehend)) {
            return false;
        }
        if (auskunftsdienste == null) {
            if (other.auskunftsdienste != null) {
                return false;
            }
        }
        else if (!auskunftsdienste.equals(other.auskunftsdienste)) {
            return false;
        }
        if (innovativeDienste == null) {
            if (other.innovativeDienste != null) {
                return false;
            }
        }
        else if (!innovativeDienste.equals(other.innovativeDienste)) {
            return false;
        }
        if (international == null) {
            if (other.international != null) {
                return false;
            }
        }
        else if (!international.equals(other.international)) {
            return false;
        }
        if (mabez == null) {
            if (other.mabez != null) {
                return false;
            }
        }
        else if (!mabez.equals(other.mabez)) {
            return false;
        }
        if (mobil == null) {
            if (other.mobil != null) {
                return false;
            }
        }
        else if (!mobil.equals(other.mobil)) {
            return false;
        }
        if (national == null) {
            if (other.national != null) {
                return false;
            }
        }
        else if (!national.equals(other.national)) {
            return false;
        }
        if (offline == null) {
            if (other.offline != null) {
                return false;
            }
        }
        else if (!offline.equals(other.offline)) {
            return false;
        }
        if (prd == null) {
            if (other.prd != null) {
                return false;
            }
        }
        else if (!prd.equals(other.prd)) {
            return false;
        }
        if (premiumServicesInt == null) {
            if (other.premiumServicesInt != null) {
                return false;
            }
        }
        else if (!premiumServicesInt.equals(other.premiumServicesInt)) {
            return false;
        }
        if (sperrklasse == null) {
            if (other.sperrklasse != null) {
                return false;
            }
        }
        else if (!sperrklasse.equals(other.sperrklasse)) {
            return false;
        }
        if (sperrklasseIms == null) {
            if (other.sperrklasseIms != null) {
                return false;
            }
        }
        else if (!sperrklasseIms.equals(other.sperrklasseIms)) {
            return false;
        }
        if (vpn == null) {
            if (other.vpn != null) {
                return false;
            }
        }
        else if (!vpn.equals(other.vpn)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Sperrklasse [sperrklasse=%s, sperrklasseIms=%s, abgehend=%s, national=%s, innovativeDienste=%s, "
                        + "mabez=%s, mobil=%s, vpn=%s, prd=%s, auskunftsdienste=%s, international=%s, offline=%s, premiumServicesInt=%s, name=%s]",
                sperrklasse, sperrklasseIms, abgehend, national, innovativeDienste, mabez, mobil, vpn, prd, auskunftsdienste,
                international, offline, premiumServicesInt, name
        );
    }

} // end
