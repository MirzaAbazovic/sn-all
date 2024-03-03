/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.MeldungsCode;

/**
 *
 */
public class DecisionVOBuilder {

    protected final DecisionAttribute attribute;
    private Object controlObject;
    private String controlValue;
    private String propertyValue;
    private DecisionResult suggestedResult;
    private DecisionResult finalResult;
    private MeldungsCode suggestedMeldungsCode;
    private MeldungsCode finalMeldungsCode;
    private String finalValue;

    public DecisionVOBuilder(DecisionAttribute attribute) {
        this.attribute = attribute;
        this.suggestedResult = DecisionResult.OK;
        this.finalResult = DecisionResult.OK;
        this.suggestedMeldungsCode = MeldungsCode.ZWA;
        this.finalMeldungsCode = MeldungsCode.ZWA;
    }

    public DecisionVO build() {
        return enrich(new DecisionVO(attribute));
    }

    protected <D extends DecisionVO> D enrich(D result) {
        result.setControlObject(controlObject);
        result.setControlValue(controlValue);
        result.setPropertyValue(propertyValue);
        result.setSuggestedResult(suggestedResult);
        result.setFinalResult(finalResult);
        result.setSuggestedMeldungsCode(suggestedMeldungsCode);
        result.setFinalMeldungsCode(finalMeldungsCode);
        result.setFinalValue(finalValue);
        return result;
    }

    public DecisionVOBuilder withControlObject(Object controlObject) {
        this.controlObject = controlObject;
        return this;
    }

    public DecisionVOBuilder withControlValue(String controlValue) {
        this.controlValue = controlValue;
        return this;
    }

    public DecisionVOBuilder withPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    public DecisionVOBuilder withSuggestedResult(DecisionResult suggestedResult) {
        this.suggestedResult = suggestedResult;
        return this;
    }

    public DecisionVOBuilder withFinalResult(DecisionResult finalResult) {
        this.finalResult = finalResult;
        return this;
    }

    public DecisionVOBuilder withFinalValue(String finalValue) {
        this.finalValue = finalValue;
        return this;
    }

    public DecisionVOBuilder withSuggestedMeldungsCode(MeldungsCode suggestedMeldungsCode) {
        this.suggestedMeldungsCode = suggestedMeldungsCode;
        return this;
    }

    public DecisionVOBuilder withFinalMeldungsCode(MeldungsCode finalMeldungsCode) {
        this.finalMeldungsCode = finalMeldungsCode;
        return this;
    }

}
