/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:08:04
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell bildet eine Niederlassung ab.
 *
 *
 */
public class Niederlassung extends AbstractCCIDModel {

    /**
     * NL-ID fuer die Niederlassung Augsburg.
     */
    public static final Long ID_AUGSBURG = Long.valueOf(1);
    /**
     * NL-ID fuer die Niederlassung Kempten.
     */
    public static final Long ID_KEMPTEN = Long.valueOf(2);
    /**
     * NL-ID fuer die Niederlassung Muenchen.
     */
    public static final Long ID_MUENCHEN = Long.valueOf(3);
    /**
     * NL-ID fuer die Niederlassung Nuernberg.
     */
    public static final Long ID_NUERNBERG = Long.valueOf(4);
    /**
     * NL-ID fuer die Niederlassung Zentral.
     */
    public static final Long ID_ZENTRAL = Long.valueOf(5);
    /**
     * NL-ID fuer die Niederlassung Main-Kinzig.
     */
    public static final Long ID_MAIN_KINZIG = Long.valueOf(6);
    /**
     * NL-ID fuer die Niederlassung Landshut.
     */
    public static final Long ID_LANDSHUT = Long.valueOf(7);

    private String name = null;
    public static final String NAME = "name";
    private String dispoTeampostfach = null;
    private Long parentId = null;
    private String dispoPhone = null;
    private Long areaNo = null;
    private String cpsProvisioningName = null;
    private Reference ipLocation = null;

    public Long getMappedFallbackOrId() {
        if (Niederlassung.ID_LANDSHUT.equals(getId())) {
            return Niederlassung.ID_MUENCHEN;
        }
        return getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDispoTeampostfach() {
        return dispoTeampostfach;
    }

    public void setDispoTeampostfach(String dispoTeampostfach) {
        this.dispoTeampostfach = dispoTeampostfach;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDispoPhone() {
        return dispoPhone;
    }

    public void setDispoPhone(String dispoPhone) {
        this.dispoPhone = dispoPhone;
    }

    public Long getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(Long areaNo) {
        this.areaNo = areaNo;
    }

    public String getCpsProvisioningName() {
        return cpsProvisioningName;
    }

    public void setCpsProvisioningName(String cpsProvisioningName) {
        this.cpsProvisioningName = cpsProvisioningName;
    }

    public Reference getIpLocation() {
        return ipLocation;
    }

    public void setIpLocation(Reference ipLocation) {
        this.ipLocation = ipLocation;
    }

}


