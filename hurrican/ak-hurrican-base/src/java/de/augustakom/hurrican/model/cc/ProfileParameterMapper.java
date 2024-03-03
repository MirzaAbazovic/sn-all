package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;


/**
 * Modell-Klasse fuer G.fast Parameter Mapper
 */
@Entity
@Table(name = "T_PROFILE_PARAMETER_MAPPER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_PROFILE_PARAMETER_MAPPER_0")
public class ProfileParameterMapper extends AbstractCCIDModel {
    private static final long serialVersionUID = 1L;

    private String parameterName;
    private String parameterGuiName;
    private String parameterGuiDescription;
    private String parameterCpsName;
    private Integer sortOrder;

    public ProfileParameterMapper() {
        super();
    }

    public ProfileParameterMapper(String parameterName, String parameterGuiName, String parameterGuiDescription,
            String parameterCpsName, Integer sortOrder) {
        super();
        this.parameterName = parameterName;
        this.parameterGuiName = parameterGuiName;
        this.parameterGuiDescription = parameterGuiDescription;
        this.parameterCpsName = parameterCpsName;
        this.sortOrder = sortOrder;
    }

    @Column(name = "SORTORDER")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Column(name = "PARAM_NAME")
    @NotNull
    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Column(name = "PARAM_GUI_NAME")
    public String getParameterGuiName() {
        return parameterGuiName;
    }

    public void setParameterGuiName(String parameterGuiName) {
        this.parameterGuiName = parameterGuiName;
    }

    @Column(name = "PARAM_GUI_DESCRIPTION")
    public String getParameterGuiDescription() {
        return parameterGuiDescription;
    }

    public void setParameterGuiDescription(String parameterGuiDescription) {
        this.parameterGuiDescription = parameterGuiDescription;
    }

    @Column(name = "PARAM_CPS_NAME")
    public String getParameterCpsName() {
        return parameterCpsName;
    }

    public void setParameterCpsName(String parameterCpsName) {
        this.parameterCpsName = parameterCpsName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        ProfileParameterMapper that = (ProfileParameterMapper) o;

        if (parameterName != null ? !parameterName.equals(that.parameterName) : that.parameterName != null)
            return false;
        if (parameterGuiName != null ? !parameterGuiName.equals(that.parameterGuiName) : that.parameterGuiName != null)
            return false;
        if (parameterGuiDescription != null ? !parameterGuiDescription.equals(that.parameterGuiDescription) : that.parameterGuiDescription != null)
            return false;
        return parameterCpsName != null ? parameterCpsName.equals(that.parameterCpsName) : that.parameterCpsName == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
        result = 31 * result + (parameterGuiName != null ? parameterGuiName.hashCode() : 0);
        result = 31 * result + (parameterGuiDescription != null ? parameterGuiDescription.hashCode() : 0);
        result = 31 * result + (parameterCpsName != null ? parameterCpsName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProfileParameterMapper{" +
                "parameterName='" + parameterName + '\'' +
                ", parameterGuiName='" + parameterGuiName + '\'' +
                ", parameterGuiDescription='" + parameterGuiDescription + '\'' +
                ", parameterCpsName='" + parameterCpsName + '\'' +
                '}';
    }
}
