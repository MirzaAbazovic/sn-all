package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1Request;

@MappedSuperclass
public abstract class StornoMitEndkundeStandortAnfrage<GF extends WbciGeschaeftsfall> extends StornoAnfrage<GF> {

    private static final long serialVersionUID = -4671666282617926549L;

    private PersonOderFirma endkunde;

    private Standort standort;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, targetEntity = Standort.class)
    @JoinColumn(name = WbciRequest.COL_NAME_STANDORT_ID)
    @Valid
    /***
     * According to WBCI-FAQs No. 9, the field should only be set in the FAX process.
     */
    @Null(groups = V1Request.class)
    public Standort getStandort() {
        return standort;
    }

    public void setStandort(Standort standort) {
        this.standort = standort;
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, targetEntity = PersonOderFirma.class)
    @JoinColumn(name = WbciRequest.COL_NAME_ENDKUNDE_ID)
    @Valid
    /***
     * According to WBCI-FAQs No. 9, the field should only be set in the FAX process.
     */
    @Null(groups = V1Request.class)
    public PersonOderFirma getEndkunde() {
        return endkunde;
    }

    public void setEndkunde(PersonOderFirma endkunde) {
        this.endkunde = endkunde;
    }

    @Override
    public String toString() {
        return "StornoMitEndkundeStandort{" +
                "endkunde=" + endkunde +
                ", standort=" + standort +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
