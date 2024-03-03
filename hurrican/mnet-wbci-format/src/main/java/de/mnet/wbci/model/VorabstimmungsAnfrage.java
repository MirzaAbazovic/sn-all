package de.mnet.wbci.model;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wbci.validation.groups.V1RequestVa;

@Entity
@DiscriminatorValue(RequestTyp.VA_NAME)
public class VorabstimmungsAnfrage<GF extends WbciGeschaeftsfall> extends WbciRequest<GF> {

    private static final long serialVersionUID = -4671666286617926549L;

    private LocalDate vaKundenwunschtermin;

    @Override
    @Transient
    public RequestTyp getTyp() {
        return RequestTyp.VA;
    }

    public void setVaKundenwunschtermin(LocalDate vaKundenwunschtermin) {
        this.vaKundenwunschtermin = vaKundenwunschtermin;
    }

    @NotNull(groups = V1RequestVa.class, message = "darf nicht leer sein")
    @Column(name = "VA_KUNDENWUNSCHTERMIN")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getVaKundenwunschtermin() {
        return vaKundenwunschtermin;
    }

}
