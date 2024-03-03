package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

@MappedSuperclass
public abstract class StornoAnfrage<GF extends WbciGeschaeftsfall> extends WbciRequest<GF> {

    private static final long serialVersionUID = -4671666282617926549L;

    private String vorabstimmungsIdRef;
    private String aenderungsId;

    @NotNull
    @Column(name = WbciRequest.COL_NAME_AENDERUNGS_ID)
    public String getAenderungsId() {
        return aenderungsId;
    }

    public void setAenderungsId(String aenderungsId) {
        this.aenderungsId = aenderungsId;
    }

    /**
     * <b>Achtung:</b> Feld wird nur fuer eingehende Stornoanfragen verwendet, um die Referenz auf die Vorabstimmung zu
     * definieren, auf die sich die Storno bezieht.
     *
     * @return die Referenz auf die Vorabstimmung
     */
    @Transient
    public String getVorabstimmungsIdRef() {
        return vorabstimmungsIdRef;
    }

    public void setVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        this.vorabstimmungsIdRef = vorabstimmungsIdRef;
    }

    @Override
    public String toString() {
        return "StornoAnfrage{" +
                "vorabstimmungsIdRef=" + vorabstimmungsIdRef +
                ", aenderungsId=" + aenderungsId +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
