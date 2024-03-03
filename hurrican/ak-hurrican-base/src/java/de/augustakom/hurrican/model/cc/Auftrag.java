/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2004 08:05:28
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Modell fuer einen Auftrag. <br> Dieses Modell ist die 'Einstiegsklasse' fuer die Auftraege. Es enthaelt eine
 * eindeutige ID fuer einen Auftrag sowie die Kundennummer (Kunde__No). <br> Alle Daten, die zu einem Auftrag gehoeren
 * (z.B. allgemeine Daten, technische Daten etc.) koennen mit Hilfe der Auftrags-ID abgefragt werden.
 *
 *
 */
public class Auftrag extends AbstractCCIDModel implements CCAuftragModel, KundenModel {

    private Long kundeNo = null;

    /**
     * Leitet an die Methode <code>setId(Long)</code> weiter
     *
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#setAuftragId(java.lang.Integer)
     */
    public void setAuftragId(Long auftragId) {
        setId(auftragId);
    }

    /**
     * Gibt den Wert von <code>getId()</code> zurueck.
     *
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#getAuftragId()
     */
    public Long getAuftragId() {
        return getId();
    }

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Auftrag a = (Auftrag) obj;
        if (getId() != null) {
            if (!getId().equals(a.getId())) {
                return false;
            }
        }
        else if (a.getId() != null) {
            return false;
        }

        if (getKundeNo() != null) {
            if (!getKundeNo().equals(a.getKundeNo())) {
                return false;
            }
        }
        else if (a.getKundeNo() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(33, 97)
                .append(getId())
                .append(kundeNo)
                .toHashCode();
    }

}


