/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 09:04:02
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.query;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 * Modell-Klasse fuer die SO-Data, die bei einem Query vom CPS verwendet wird Sektion zur Angabe der Hardware Kennungen
 */
@XStreamAlias("ID")
public class CPSQueryIdData extends AbstractCPSServiceOrderDataModel {
    public enum DSLAMType {
        unknown(0L),
        NSNDSLAM(1L),
        HUAWEIDSLAM(2L),
        ALUDSLAM(6L);

        private DSLAMType(Long id) {
            this.id = id;
        }

        private Long id;

        public Long getId() {
            return id;
        }

        public static DSLAMType getById(Long id) {
            if (id == null) {
                throw new IllegalArgumentException("Keine Id fuer den DSLAM Typen angegeben!");
            }
            DSLAMType[] typeValues = DSLAMType.values();
            if ((typeValues != null) && (typeValues.length > 0)) {
                for (DSLAMType type : typeValues) {
                    if (NumberTools.equal(id, type.getId())) {
                        return type;
                    }
                }
            }
            return DSLAMType.unknown;
        }

    } // end

    @XStreamAlias("CARD_TYPE")
    private String cardType;
    @XStreamAlias("DSLAM_NAME")
    private String dslamName;
    @XStreamAlias("PORT")
    private Integer port;
    @XStreamAlias("RACK")
    private Integer rack;
    @XStreamAlias("SHELF")
    private Integer shelf;
    @XStreamAlias("SLOT")
    private Integer slot;
    @XStreamAlias("TYPE")
    private String dslamType;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getDslamName() {
        return dslamName;
    }

    public void setDslamName(String dslamName) {
        this.dslamName = dslamName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getRack() {
        return rack;
    }

    public void setRack(Integer rack) {
        this.rack = rack;
    }

    public Integer getShelf() {
        return shelf;
    }

    public void setShelf(Integer shelf) {
        this.shelf = shelf;
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public DSLAMType getDslamType() {
        return DSLAMType.valueOf(dslamType);
    }

    public void setDslamType(HVTTechnik hvtTechnik) {
        this.dslamType = DSLAMType.getById(hvtTechnik.getId()).name();
    }

}


