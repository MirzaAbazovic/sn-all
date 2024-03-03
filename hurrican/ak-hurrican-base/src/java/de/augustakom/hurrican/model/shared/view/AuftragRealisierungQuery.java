/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2005 07:54:00
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche nach Auftraegen, die zu einem best. Zeitpunkt in Betrieb gegangen, geaendert oder
 * gekuendigt wurden.
 *
 *
 */
public class AuftragRealisierungQuery extends AbstractHurricanQuery implements KundenModel {

    private Long produktGruppeId = null;
    private Date inbetriebnahmeFrom = null;
    private Date inbetriebnahmeTo = null;
    private Long kundeNo = null;
    private boolean withoutAKom = false;
    private Long niederlassungId = null;
    private Boolean inbetriebnahme = null;
    private Boolean realisierung = null;
    private Boolean kuendigung = null;

    /**
     * @return Returns the inbetriebnahmeFrom.
     */
    public Date getInbetriebnahmeFrom() {
        return inbetriebnahmeFrom;
    }

    /**
     * @param inbetriebnahmeFrom The inbetriebnahmeFrom to set.
     */
    public void setInbetriebnahmeFrom(Date inbetriebnahmeFrom) {
        this.inbetriebnahmeFrom = inbetriebnahmeFrom;
    }

    /**
     * @return Returns the inbetriebnahmeTo.
     */
    public Date getInbetriebnahmeTo() {
        return inbetriebnahmeTo;
    }

    /**
     * @param inbetriebnahmeTo The inbetriebnahmeTo to set.
     */
    public void setInbetriebnahmeTo(Date inbetriebnahmeTo) {
        this.inbetriebnahmeTo = inbetriebnahmeTo;
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
     * @return Returns the produktGruppeId.
     */
    public Long getProduktGruppeId() {
        return produktGruppeId;
    }

    /**
     * @param produktGruppeId The produktGruppeId to set.
     */
    public void setProduktGruppeId(Long produktGruppeId) {
        this.produktGruppeId = produktGruppeId;
    }

    /**
     * @return Returns the withoutAKom.
     */
    public boolean isWithoutAKom() {
        return withoutAKom;
    }

    /**
     * @param withoutAKom The withoutAKom to set.
     */
    public void setWithoutAKom(boolean withoutAKom) {
        this.withoutAKom = withoutAKom;
    }


    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    /**
     * @return the inbetriebnahme
     */
    public Boolean getInbetriebnahme() {
        return inbetriebnahme;
    }

    /**
     * @param inbetriebnahme the inbetriebnahme to set
     */
    public void setInbetriebnahme(Boolean inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

    /**
     * @return the realisierung
     */
    public Boolean getRealisierung() {
        return realisierung;
    }

    /**
     * @param realisierung the realisierung to set
     */
    public void setRealisierung(Boolean realisierung) {
        this.realisierung = realisierung;
    }

    /**
     * @return the kuendigung
     */
    public Boolean getKuendigung() {
        return kuendigung;
    }

    /**
     * @param kuendigung the kuendigung to set
     */
    public void setKuendigung(Boolean kuendigung) {
        this.kuendigung = kuendigung;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getInbetriebnahmeFrom() != null) {
            return false;
        }
        if (getInbetriebnahmeTo() != null) {
            return false;
        }
        if (getKundeNo() != null) {
            return false;
        }
        if (getProduktGruppeId() != null) {
            return false;
        }
        if (getNiederlassungId() != null) {
            return false;
        }
        if (getInbetriebnahme() != null) {
            return false;
        }
        if (getRealisierung() != null) {
            return false;
        }
        if (getKuendigung() != null) {
            return false;
        }
        return true;
    }

}


