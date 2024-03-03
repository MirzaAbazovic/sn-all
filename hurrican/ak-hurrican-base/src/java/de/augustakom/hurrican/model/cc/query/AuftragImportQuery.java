/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2005 15:00:33
 */
package de.augustakom.hurrican.model.cc.query;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche nach AuftragImport-Objekten.
 *
 *
 */
public class AuftragImportQuery extends AbstractHurricanQuery implements KundenModel {

    private Boolean active = null;
    private Boolean all = null;
    private Long kundeNo = null;
    private String agName = null;
    private String agVorname = null;
    private String agStrasse = null;
    private String agOrt = null;
    private String anschlussName = null;
    private String anschlussVorname = null;
    private String anschlussStrasse = null;
    private String anschlussOrt = null;
    private String altName = null;
    private String altVorname = null;
    private String altStrasse = null;
    private String altOrt = null;
    private String rufnummer = null;
    private String refId = null;

    private Date minIncome = null;
    private Date maxIncome = null;
    private Long minStatus = null;
    private Long maxStatus = null;

    /**
     * @return Returns the all.
     */
    public Boolean getAll() {
        return all;
    }

    /**
     * @param all The all to set.
     */
    public void setAll(Boolean all) {
        this.all = all;
    }

    /**
     * @return Returns the agName.
     */
    public String getAgName() {
        return agName;
    }

    /**
     * @param agName The agName to set.
     */
    public void setAgName(String agName) {
        this.agName = agName;
    }

    /**
     * @return Returns the agVorname.
     */
    public String getAgVorname() {
        return agVorname;
    }

    /**
     * @param agVorname The agVorname to set.
     */
    public void setAgVorname(String agVorname) {
        this.agVorname = agVorname;
    }

    /**
     * @return Returns the agOrt.
     */
    public String getAgOrt() {
        return agOrt;
    }

    /**
     * @param agOrt The agOrt to set.
     */
    public void setAgOrt(String agOrt) {
        this.agOrt = agOrt;
    }

    /**
     * @return Returns the agStrasse.
     */
    public String getAgStrasse() {
        return agStrasse;
    }

    /**
     * @param agStrasse The agStrasse to set.
     */
    public void setAgStrasse(String agStrasse) {
        this.agStrasse = agStrasse;
    }

    /**
     * @return Returns the anschlussName.
     */
    public String getAnschlussName() {
        return anschlussName;
    }

    /**
     * @param anschlussName The anschlussName to set.
     */
    public void setAnschlussName(String anschlussName) {
        this.anschlussName = anschlussName;
    }

    /**
     * @return Returns the anschlussOrt.
     */
    public String getAnschlussOrt() {
        return anschlussOrt;
    }

    /**
     * @param anschlussOrt The anschlussOrt to set.
     */
    public void setAnschlussOrt(String anschlussOrt) {
        this.anschlussOrt = anschlussOrt;
    }

    /**
     * @return Returns the anschlussStrasse.
     */
    public String getAnschlussStrasse() {
        return anschlussStrasse;
    }

    /**
     * @param anschlussStrasse The anschlussStrasse to set.
     */
    public void setAnschlussStrasse(String anschlussStrasse) {
        this.anschlussStrasse = anschlussStrasse;
    }

    /**
     * @return Returns the anschlussVorname.
     */
    public String getAnschlussVorname() {
        return anschlussVorname;
    }

    /**
     * @param anschlussVorname The anschlussVorname to set.
     */
    public void setAnschlussVorname(String anschlussVorname) {
        this.anschlussVorname = anschlussVorname;
    }

    /**
     * @return Returns the kundeNo.
     */
    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the rufnummer.
     */
    public String getRufnummer() {
        return rufnummer;
    }

    /**
     * @param rufnummer The rufnummer to set.
     */
    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    /**
     * @return Returns the active.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the altName.
     */
    public String getAltName() {
        return altName;
    }

    /**
     * @param altName The altName to set.
     */
    public void setAltName(String altName) {
        this.altName = altName;
    }

    /**
     * @return Returns the altOrt.
     */
    public String getAltOrt() {
        return altOrt;
    }

    /**
     * @param altOrt The altOrt to set.
     */
    public void setAltOrt(String altOrt) {
        this.altOrt = altOrt;
    }

    /**
     * @return Returns the altStrasse.
     */
    public String getAltStrasse() {
        return altStrasse;
    }

    /**
     * @param altStrasse The altStrasse to set.
     */
    public void setAltStrasse(String altStrasse) {
        this.altStrasse = altStrasse;
    }

    /**
     * @return Returns the altVorname.
     */
    public String getAltVorname() {
        return altVorname;
    }

    /**
     * @param altVorname The altVorname to set.
     */
    public void setAltVorname(String altVorname) {
        this.altVorname = altVorname;
    }

    /**
     * @return Returns the maxStatus.
     */
    public Long getMaxStatus() {
        return maxStatus;
    }

    /**
     * @param maxStatus The maxStatus to set.
     */
    public void setMaxStatus(Long maxStatus) {
        this.maxStatus = maxStatus;
    }

    /**
     * @return Returns the minStatus.
     */
    public Long getMinStatus() {
        return minStatus;
    }

    /**
     * @param minStatus The minStatus to set.
     */
    public void setMinStatus(Long minStatus) {
        this.minStatus = minStatus;
    }

    /**
     * @return Returns the maxIncome.
     */
    public Date getMaxIncome() {
        return maxIncome;
    }

    /**
     * @param maxIncome The maxIncome to set.
     */
    public void setMaxIncome(Date maxIncome) {
        this.maxIncome = maxIncome;
    }

    /**
     * @return Returns the minIncome.
     */
    public Date getMinIncome() {
        return minIncome;
    }

    /**
     * @param minIncome The minIncome to set.
     */
    public void setMinIncome(Date minIncome) {
        this.minIncome = minIncome;
    }

    /**
     * @return Returns the refId.
     */
    public String getRefId() {
        return this.refId;
    }

    /**
     * @param refId The refId to set.
     */
    public void setRefId(String refId) {
        this.refId = refId;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getKundeNo() != null) {
            return false;
        }
        if (getActive() != null) {
            return false;
        }
        if (getAll() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getAgName())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAgVorname())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAnschlussName())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAnschlussVorname())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAnschlussStrasse())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAnschlussOrt())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAltName())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAltVorname())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAltStrasse())) {
            return false;
        }
        if (StringUtils.isNotBlank(getAltOrt())) {
            return false;
        }
        if (StringUtils.isNotBlank(getRufnummer())) {
            return false;
        }
        if (StringUtils.isNotBlank(getRefId())) {
            return false;
        }
        if (getMinStatus() != null) {
            return false;
        }
        if (getMaxStatus() != null) {
            return false;
        }
        if (getMinIncome() != null) {
            return false;
        }
        if (getMaxIncome() != null) {
            return false;
        }
        return true;
    }

}


