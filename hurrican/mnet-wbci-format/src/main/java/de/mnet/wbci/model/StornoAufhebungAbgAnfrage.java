package de.mnet.wbci.model;

import javax.persistence.*;

/**
 * JPA Bean for modelling StornoAufhebung Requests originating from the Abgebende-EKP.
 *
 * @param <GF>
 */
@Entity
@DiscriminatorValue(RequestTyp.STR_AUFH_ABG_NAME)
public class StornoAufhebungAbgAnfrage<GF extends WbciGeschaeftsfall> extends StornoAbgAnfrage<GF> {

    private static final long serialVersionUID = -4671662282617926549L;

    @Override
    @Transient
    public RequestTyp getTyp() {
        return RequestTyp.STR_AUFH_ABG;
    }

    @Override
    public String toString() {
        return "StornoAufhebungAbg{" +
                "stornoGrund=" + getStornoGrund() +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
