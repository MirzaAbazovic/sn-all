package de.mnet.wbci.model;

import javax.persistence.*;

/**
 * JPA Bean for modelling StornoAenderung Requests originating from the Aufnehmende-EKP.
 *
 * @param <GF>
 */
@Entity
@DiscriminatorValue(RequestTyp.STR_AEN_AUF_NAME)
public class StornoAenderungAufAnfrage<GF extends WbciGeschaeftsfall> extends StornoMitEndkundeStandortAnfrage<GF> {

    private static final long serialVersionUID = -4671666282617126549L;

    @Override
    @Transient
    public RequestTyp getTyp() {
        return RequestTyp.STR_AEN_AUF;
    }

    @Override
    public String toString() {
        return "StornoAenderungAuf{" +
                "toString='" + super.toString() + '\'' +
                '}';
    }
}
