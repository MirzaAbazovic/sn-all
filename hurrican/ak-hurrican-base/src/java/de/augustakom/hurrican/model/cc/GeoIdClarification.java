/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2011 14:11:13
 */

package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell-Klasse fuer die Abbildung der Klärungsliste.
 */
@Entity
@Table(name = "T_GEO_ID_CLARIFICATION")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_GEO_ID_CLARIFICATION_0", allocationSize = 1)
public class GeoIdClarification extends AbstractCCIDModel {

    public enum Status {
        OPEN(22300L),
        SOLVED(22301L),
        CLOSED(22302L),
        IN_PROGRESS(22303L),
        SUSPENDED(22304L);

        private final Long refId;

        private Status(Long refId) {
            this.refId = refId;
        }

        public Long getRefId() {
            return refId;
        }

        public static final Status findById(Long refId) {
            if (refId == null) { return null;}

            Status[] values = Status.values();
            for (Status status : values) {
                if (NumberTools.equal(status.getRefId(), refId)) {
                    return status;
                }
            }
            return null;
        }
    }

    public enum Type {
        ONKZ_ASB(22310L, "ONKZ_ASB"),
        INSERT_GEO_ID(22311L, "INSERT_GEO_ID"),
        UPDATE_GEO_ID(22312L, "UPDATE_GEO_ID"),
        KVZ_ASSIGNMENT_POSSIBLE(22313L, "KVZ_ASSIGNMENT"),
        KVZ_DIFFERENT(22314L, "KVZ_DIFFERENT");

        private final Long refId;
        private final String name;

        private Type(Long refId, String name) {
            this.refId = refId;
            this.name = name;
        }

        public Long getRefId() {
            return refId;
        }

        public String getName() {
            return name;
        }

        public static final Type findById(Long refId) {
            if (refId == null) { return null;}

            Type[] values = Type.values();
            for (Type type : values) {
                if (NumberTools.equal(type.getRefId(), refId)) {
                    return type;
                }
            }
            return null;
        }
    }

    private Long geoId;
    private Reference status;
    private Reference type;
    private String info;
    private String userW;
    private Date dateW;

    @Column(name = "GEO_ID")
    @NotNull
    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_REF_ID", nullable = false)
    public Reference getStatus() {
        return status;
    }

    public void setStatus(Reference status) {
        this.status = status;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TYPE_REF_ID", nullable = false)
    public Reference getType() {
        return type;
    }

    public void setType(Reference revisionType) {
        this.type = revisionType;
    }

    @Column(name = "INFO")
    public String getInfo() {
        return info;
    }

    public void setInfo(String revisionData) {
        this.info = revisionData;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @Column(name = "DATEW")
    public Date getDateW() {
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }


    /**
     * Prueft, ob die Werte {@code type} und {@code info} mit dem aktuellen Objekt uebereinstimmen.
     *
     * @param type
     * @param info
     * @return
     */
    public boolean doesMatch(Type type, String info) {
        if (NumberTools.equal(type.refId, getType().getId()) && StringUtils.equals(info, getInfo())) {
            return true;
        }
        return false;
    }
}
