package de.augustakom.hurrican.model.cc;

import de.mnet.annotation.ObjectsAreNonnullByDefault;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * Modell-Klasse fuer G.fast Auftrag Parameters Values
 */
@Embeddable
@ObjectsAreNonnullByDefault
public class ProfileAuftragValue implements Serializable {
    protected static final long serialVersionUID = 1L;

    private String parameterName;
    private String parameterValue;

    protected ProfileAuftragValue() {
        super();
    }

    public ProfileAuftragValue(
            final String parameterName, final String parameterValue) {
        super();
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    @Column(name = "PARAMETER_NAME")
    @NotNull
    public String getParameterName() {
        return parameterName;
    }

    protected void setParameterName(String parameterName) {
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

    @Override
    public int hashCode() {
        return Objects.hash(parameterName, parameterValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        final ProfileAuftragValue other = (ProfileAuftragValue) obj;
        return Objects.equals(this.parameterName, other.parameterName)
                && Objects.equals(this.parameterValue, other.parameterValue);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("parameterName", parameterName).
                append("parameterValue", parameterValue).
                toString();
    }

}
