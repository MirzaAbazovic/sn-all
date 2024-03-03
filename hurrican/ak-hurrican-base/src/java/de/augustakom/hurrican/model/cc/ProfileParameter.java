package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


/**
 * Modell-Klasse fuer G.fast Profile Defaults
 */
@Entity
@Table(name = "T_PROFILE_PARAMETER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_PROFILE_PARAMETER_0")
public class ProfileParameter extends AbstractCCIDModel {

    public static final String TRAFFIC_TABLE_UPSTREAM = "Traffic Table Upstream";
    public static final String TRAFFIC_TABLE_DOWNSTREAM = "Traffic Table Downstream";
    public static final String TDD = "TDD";

    private static final long serialVersionUID = 1L;

    private HWBaugruppenTyp baugruppenTyp;
    private String parameterName;
    private String parameterValue;
    private Boolean isDefault;

    public ProfileParameter() {
        super();
    }

    public ProfileParameter(HWBaugruppenTyp baugruppenTyp, String parameterName,
            String parameterValue, Boolean isDefault) {
        super();
        this.baugruppenTyp = baugruppenTyp;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
        this.isDefault = isDefault;
    }

    @ManyToOne
    @JoinColumn(name = "HW_BAUGRUPPEN_TYP_ID", nullable = false)
    @NotNull
    public HWBaugruppenTyp getBaugruppenTyp() {
        return baugruppenTyp;
    }

    public void setBaugruppenTyp(HWBaugruppenTyp baugruppenTyp) {
        this.baugruppenTyp = baugruppenTyp;
    }

    @Column(name = "PARAMETER_NAME")
    @NotNull
    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Column(name = "PARAMETER_VALUE")
    @NotNull
    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    @Column(name = "IS_DEFAULT")
    @NotNull
    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public ProfileParameter copy() {
        return new ProfileParameter(this.baugruppenTyp, this.getParameterName(), this.parameterValue, this.isDefault);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        ProfileParameter that = (ProfileParameter) o;

        if (baugruppenTyp != null ? !baugruppenTyp.equals(that.baugruppenTyp) : that.baugruppenTyp != null)
            return false;
        if (parameterName != null ? !parameterName.equals(that.parameterName) : that.parameterName != null)
            return false;
        if (parameterValue != null ? !parameterValue.equals(that.parameterValue) : that.parameterValue != null)
            return false;
        return isDefault != null ? isDefault.equals(that.isDefault) : that.isDefault == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (baugruppenTyp != null ? baugruppenTyp.hashCode() : 0);
        result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
        result = 31 * result + (parameterValue != null ? parameterValue.hashCode() : 0);
        result = 31 * result + (isDefault != null ? isDefault.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProfileParameter{" +
                "baugruppenTyp=" + baugruppenTyp +
                ", parameterName='" + parameterName + '\'' +
                ", parameterValue='" + parameterValue + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
