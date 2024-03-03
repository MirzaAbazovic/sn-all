/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 14:53:36
 */
package de.augustakom.hurrican.model.billing.query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche nach Kunden. <br><br> <strong> Parameter, die auf <code>null</code> gesetzt sind, werden
 * ignoriert! </strong>
 *
 *
 */
public class KundeQuery extends AbstractHurricanQuery {

    private Long kundeNo = null;
    private Long hauptKundenNo = null;
    private String name = null;
    private String vorname = null;
    private String strasse = null;
    private String plz = null;
    private String ort = null;
    private String ortsteil;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        KundeQuery query = (KundeQuery) obj;
        if ((this.kundeNo == null) && (query.kundeNo != null)) {
            return false;
        }
        if (NumberTools.notEqual(this.kundeNo, query.kundeNo)) {
            return false;
        }
        if ((this.hauptKundenNo == null) && (query.hauptKundenNo != null)) {
            return false;
        }
        if (NumberTools.notEqual(this.hauptKundenNo, query.hauptKundenNo)) {
            return false;
        }
        if ((this.name == null) && (query.name != null)) {
            return false;
        }
        if (!StringUtils.equals(this.name, query.name)) {
            return false;
        }
        if ((this.vorname == null) && (query.vorname != null)) {
            return false;
        }
        if (!StringUtils.equals(this.vorname, query.vorname)) {
            return false;
        }
        if ((this.strasse == null) && (query.strasse != null)) {
            return false;
        }
        if (!StringUtils.equals(this.strasse, query.strasse)) {
            return false;
        }
        if ((this.plz == null) && (query.plz != null)) {
            return false;
        }
        if (!StringUtils.equals(this.plz, query.plz)) {
            return false;
        }
        if ((this.ort == null) && (query.ort != null)) {
            return false;
        }
        if (!StringUtils.equals(this.ort, query.ort)) {
            return false;
        }
        if ((this.ortsteil == null) && (query.ortsteil != null)) {
            return false;
        }
        if (!StringUtils.equals(this.ortsteil, query.ortsteil)) {
            return false;
        }

        return super.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(27, 43)
                .append(kundeNo)
                .append(hauptKundenNo)
                .append(name)
                .append(vorname)
                .append(strasse)
                .append(plz)
                .append(ort).append(ortsteil).toHashCode();
    }

    /**
     * @return Returns the hauptKundenNo.
     */
    public Long getHauptKundenNo() {
        return hauptKundenNo;
    }

    /**
     * @param hauptKundenNo The hauptKundenNo to set.
     */
    public void setHauptKundenNo(Long hauptKundenNo) {
        this.hauptKundenNo = hauptKundenNo;
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
     * @return Returns the ort.
     */
    public String getOrt() {
        return ort;
    }

    /**
     * @param ort The ort to set.
     */
    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getOrtsteil() {
        return ortsteil;
    }

    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

    /**
     * @return Returns the plz.
     */
    public String getPlz() {
        return plz;
    }

    /**
     * @param plz The plz to set.
     */
    public void setPlz(String plz) {
        this.plz = plz;
    }

    /**
     * @return Returns the strasse.
     */
    public String getStrasse() {
        return strasse;
    }

    /**
     * @param strasse The strasse to set.
     */
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    /**
     * @return Returns the vorname.
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * @param vorname The vorname to set.
     */
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getKundeNo() != null) {
            return false;
        }
        if (getHauptKundenNo() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getName())) {
            return false;
        }
        if (StringUtils.isNotBlank(getVorname())) {
            return false;
        }
        if (StringUtils.isNotBlank(getStrasse())) {
            return false;
        }
        if (StringUtils.isNotBlank(getPlz())) {
            return false;
        }
        if (StringUtils.isNotBlank(getOrt())) {
            return false;
        }
        if (StringUtils.isNotBlank(getOrtsteil())) {
            return false;
        }

        return true;
    }
}


