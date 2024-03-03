/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 13:56:01
 */
package de.augustakom.hurrican.model.wholesale;

import de.augustakom.hurrican.model.cc.DSLAMProfile;

/**
 * DTO für ein Wholesale VDSL Profil.
 *
 *
 */
public class WholesaleVdslProfile {
    private Long id;
    private String name;

    public WholesaleVdslProfile(DSLAMProfile dslamProfile) {
        this.id = dslamProfile.getId();
        this.name = dslamProfile.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}


