/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 14:26:38
 */
package de.augustakom.hurrican.model.cc;


/**
 *
 */
@SuppressWarnings("unused")
public class ReferenceBuilder extends AbstractCCIDModelBuilder<ReferenceBuilder, Reference> {

    {id = getLongId();}

    private String strValue = "Test";
    private Integer intValue = null;
    private Float floatValue = null;
    private String type = "TEST";
    private Long unitId = null;
    private Boolean guiVisible = Boolean.FALSE;
    private Integer orderNo = Integer.valueOf(10);
    private String wbciTechnologieCode = null;

    // nicht persistentes Property!
    private String guiText = null;

    public ReferenceBuilder withStrValue(String strValue) {
        this.strValue = strValue;
        return this;
    }

    public ReferenceBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public ReferenceBuilder withIntValue(int intValue) {
        this.intValue = intValue;
        return this;
    }

    public ReferenceBuilder withWbciTechnologieCode(String wbciTechnologieCode) {
        this.wbciTechnologieCode = wbciTechnologieCode;
        return this;
    }
}
