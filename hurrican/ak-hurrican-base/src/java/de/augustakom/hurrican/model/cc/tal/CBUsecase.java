/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 10:09:02
 */
package de.augustakom.hurrican.model.cc.tal;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell fuer die Abbildung der moeglichen TAL-Bestellungsvorfaelle zu einem Vorgangs-Typ (z.B. Typ='NEU'; Usecase='mit
 * Rufnummernuebernahme').
 *
 *
 */
public class CBUsecase extends AbstractCCIDModel {

    /**
     * Usecase-ID fuer den DTAG-Vorfall 'TALN'.
     */
    public static final Long CBUSECASE_ID_TALN = Long.valueOf(2);
    /**
     * Usecase-ID fuer den DTAG-Vorfall 'NTAL'
     */
    public static final Long CBUSECASE_ID_NTAL = Long.valueOf(14);
    /**
     * Usecase-ID fuer den DTAG-Vorfall 'TALM'.
     */
    public static final Long CBUSECASE_ID_TALM = Long.valueOf(4);
    /**
     * Usecase-ID fuer den DTAG-Vorfall 'TALO'.
     */
    public static final Long CBUSECASE_ID_TALO = Long.valueOf(3);
    /**
     * Usecase-ID fuer den DTAG-Vorfall 'TALK'.
     */
    public static final Long CBUSECASE_ID_TALK = Long.valueOf(6);

    private Long referenceId = null;
    private Long exmTbvId = null;
    private Boolean active = null;

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * ID definiert den auszufuehrenden TAL-Bestellungsvorgang eines externen Moduls.
     *
     * @return Returns the exmTbvId.
     */
    public Long getExmTbvId() {
        return exmTbvId;
    }

    public void setExmTbvId(Long exmTbvId) {
        this.exmTbvId = exmTbvId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
