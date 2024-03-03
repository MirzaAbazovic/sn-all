/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.13
 */
package de.mnet.wbci.model;

import java.io.*;

/**
 * Decision value object for evaluating incoming Vorabstimmung requests. Object holds decision data which is included in
 * the decision making process whether an incoming Vorabstimmung is accepted or declined with RUEMVA or ABBM.
 * <p/>
 * Property value of incoming Vorabstimmung and stored M-net data is compared upon evaluating the decision outcome. The
 * automated process tries to give a decision suggestion based on specific comparison logic.
 * <p/>
 * User can overrule decision in Hurrican GUI. The decision aims to give a {@link MeldungsCode} as outcome.
 *
 *
 */
public class DecisionVO implements Serializable {
    private static final long serialVersionUID = 8681181367089349383L;

    private final DecisionAttribute attribute;
    private Object controlObject;
    private String controlValue;
    private String propertyValue;
    private DecisionResult suggestedResult = DecisionResult.OK;
    private DecisionResult finalResult = DecisionResult.OK;
    private MeldungsCode suggestedMeldungsCode = MeldungsCode.ZWA;
    private MeldungsCode finalMeldungsCode = MeldungsCode.ZWA;
    private String finalValue;

    public DecisionVO(DecisionAttribute attribute) {
        this.attribute = attribute;
    }

    public DecisionAttribute getAttribute() {
        return attribute;
    }

    public String getName() {
        return attribute.getDisplayName();
    }

    public String getControlValue() {
        return controlValue;
    }

    public void setControlValue(String control) {
        this.controlValue = control;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String property) {
        this.propertyValue = property;
    }

    public DecisionResult getSuggestedResult() {
        return suggestedResult;
    }

    public void setSuggestedResult(DecisionResult result) {
        this.suggestedResult = result;
    }

    public DecisionResult getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(DecisionResult decision) {
        this.finalResult = decision;
    }

    public MeldungsCode getSuggestedMeldungsCode() {
        return suggestedMeldungsCode;
    }

    public void setSuggestedMeldungsCode(MeldungsCode code) {
        this.suggestedMeldungsCode = code;
    }

    public MeldungsCode getFinalMeldungsCode() {
        return finalMeldungsCode;
    }

    public void setFinalMeldungsCode(MeldungsCode code) {
        this.finalMeldungsCode = code;
    }

    public Object getControlObject() {
        return controlObject;
    }

    public void setControlObject(Object controlObject) {
        this.controlObject = controlObject;
    }

    public String getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(String finalValue) {
        this.finalValue = finalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecisionVO)) {
            return false;
        }

        DecisionVO that = (DecisionVO) o;

        if (attribute != that.attribute) {
            return false;
        }
        if (controlObject != null ? !controlObject.equals(that.controlObject) : that.controlObject != null) {
            return false;
        }
        if (controlValue != null ? !controlValue.equals(that.controlValue) : that.controlValue != null) {
            return false;
        }
        if (finalMeldungsCode != that.finalMeldungsCode) {
            return false;
        }
        if (finalResult != that.finalResult) {
            return false;
        }
        if (propertyValue != null ? !propertyValue.equals(that.propertyValue) : that.propertyValue != null) {
            return false;
        }
        if (suggestedMeldungsCode != that.suggestedMeldungsCode) {
            return false;
        }
        if (suggestedResult != that.suggestedResult) {
            return false;
        }
        if (finalValue != null ? !finalValue.equals(that.finalValue) : that.finalValue != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = attribute.hashCode();
        result = 31 * result + (controlObject != null ? controlObject.hashCode() : 0);
        result = 31 * result + (controlValue != null ? controlValue.hashCode() : 0);
        result = 31 * result + (propertyValue != null ? propertyValue.hashCode() : 0);
        result = 31 * result + (suggestedResult != null ? suggestedResult.hashCode() : 0);
        result = 31 * result + (finalResult != null ? finalResult.hashCode() : 0);
        result = 31 * result + (suggestedMeldungsCode != null ? suggestedMeldungsCode.hashCode() : 0);
        result = 31 * result + (finalMeldungsCode != null ? finalMeldungsCode.hashCode() : 0);
        result = 31 * result + (finalValue != null ? finalValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DecisionVO{" +
                "attribute=" + attribute +
                ", controlObject=" + controlObject +
                ", controlValue='" + controlValue + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                ", suggestedResult=" + suggestedResult +
                ", finalResult=" + finalResult +
                ", suggestedMeldungsCode=" + suggestedMeldungsCode +
                ", finalMeldungsCode=" + finalMeldungsCode +
                ", finalValue='" + finalValue + '\'' +
                '}';
    }
}
