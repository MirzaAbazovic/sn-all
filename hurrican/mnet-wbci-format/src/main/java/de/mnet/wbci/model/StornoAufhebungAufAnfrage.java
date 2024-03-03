package de.mnet.wbci.model;

import javax.persistence.*;

/**
 * JPA Bean for modelling StornoAufhebung Requests originating from the Aufnehmende-EKP.
 *
 * @param <GF>
 */
@Entity
@DiscriminatorValue(RequestTyp.STR_AUFH_AUF_NAME)
public class StornoAufhebungAufAnfrage<GF extends WbciGeschaeftsfall> extends StornoAnfrage<GF> {

    private static final long serialVersionUID = -4671196282617926549L;

    @Override
    @Transient
    public RequestTyp getTyp() {
        return RequestTyp.STR_AUFH_AUF;
    }

    @Override
    public String toString() {
        return "StornoAufhebungAuf{" +
                "toString='" + super.toString() + '\'' +
                '}';
    }
}
