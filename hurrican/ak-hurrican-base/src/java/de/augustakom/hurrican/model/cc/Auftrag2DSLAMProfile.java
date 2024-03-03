/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2007 16:22:12
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.AuftragAktionAwareModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell fuer die Zuordnung eines DSLAM-Profils zu einem Auftrag.
 *
 *
 */
public class Auftrag2DSLAMProfile extends AbstractCCHistoryUserModel implements CCAuftragModel, AuftragAktionAwareModel {

    public static final String DSLAM_PROFILE_ID = "dslamProfileId";

    private Long auftragId = null;
    private Long dslamProfileId = null;
    private Long changeReasonId = null;
    private String bemerkung = null;
    private Long auftragAktionsIdAdd = null;
    private Long auftragAktionsIdRemove = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#getAuftragId()
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#setAuftragId(java.lang.Long)
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the dslamProfileId.
     */
    public Long getDslamProfileId() {
        return dslamProfileId;
    }

    /**
     * @param dslamProfileId The dslamProfileId to set.
     */
    public void setDslamProfileId(Long dslamProfileId) {
        this.dslamProfileId = dslamProfileId;
    }

    /**
     * @return Returns the bemerkung.
     */
    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * @param bemerkung The bemerkung to set.
     */
    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * @return Returns the changeReasonId.
     */
    public Long getChangeReasonId() {
        return changeReasonId;
    }

    /**
     * @param changeReasonId The changeReasonId to set.
     */
    public void setChangeReasonId(Long changeReasonId) {
        this.changeReasonId = changeReasonId;
    }

    @Override
    public Long getAuftragAktionsIdAdd() {
        return auftragAktionsIdAdd;
    }

    @Override
    public void setAuftragAktionsIdAdd(Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
    }

    @Override
    public Long getAuftragAktionsIdRemove() {
        return auftragAktionsIdRemove;
    }

    @Override
    public void setAuftragAktionsIdRemove(Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
    }

}


