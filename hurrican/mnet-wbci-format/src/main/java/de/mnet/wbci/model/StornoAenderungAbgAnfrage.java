package de.mnet.wbci.model;

import javax.persistence.*;

/**
 * JPA Bean for modelling StornoAenderung Requests originating from the Abgebende-EKP.
 *
 * @param <GF>
 */
@Entity
@DiscriminatorValue(RequestTyp.STR_AEN_ABG_NAME)
public class StornoAenderungAbgAnfrage<GF extends WbciGeschaeftsfall> extends StornoAbgAnfrage<GF> {

    private static final long serialVersionUID = -4271666282617926549L;

    @Override
    @Transient
    public RequestTyp getTyp() {
        return RequestTyp.STR_AEN_ABG;
    }

    @Override
    public String toString() {
        return "StornoAenderungAbg{" +
                "stornoGrund=" + getStornoGrund() +
                ", toString='" + super.toString() + '\'' +
                '}';
    }

}
