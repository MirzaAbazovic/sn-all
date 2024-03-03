/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 11:48:18
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import javax.annotation.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.cc.cps.serviceorder.utils.CPSPhoneNumberModificator;


/**
 * Modell-Klasse zur Abbildung von VoIP-Daten zur CPS-Provisionierung.
 *
 *
 */
@XStreamAlias("VOIP")
public class CPSVoIPData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("SIPACCOUNTS")
    private List<CPSVoIPSIPAccountData> sipAccounts = null;

    @XStreamAlias("NUMBERING_PLAN")
    private List<CpsVoipRangeData> numberingPlan;  //no empty list as default so Xstream does not generate a tag

    /**
     * Fuegt dem Modell ein weiteres Objekt vom Typ <code>CPSDNServiceData</code> hinzu. <br> Das Modell wird der Liste
     * <code>sipAccounts</code> zugeordnet.
     *
     * @param toAdd
     */
    @SuppressWarnings("unchecked")
    public void addSipAccount(@Nonnull CPSVoIPSIPAccountData toAdd) {
        if (sipAccounts == null) {
            sipAccounts = new ArrayList<CPSVoIPSIPAccountData>();
        }

        // DIRECT_DIAL pruefen und ggf. modifizieren!
        CPSPhoneNumberModificator.modifyDirectDial((List) sipAccounts, (AbstractCPSDNData) toAdd);
        sipAccounts.add(toAdd);
    }

    /**
     * @return the sipAccounts
     */
    @CheckForNull
    public List<CPSVoIPSIPAccountData> getSipAccounts() {
        return sipAccounts;
    }

    /**
     * @param sipAccounts the sipAccounts to set
     */
    public void setSipAccounts(List<CPSVoIPSIPAccountData> sipAccounts) {
        this.sipAccounts = sipAccounts;
    }

    @CheckForNull
    public List<CpsVoipRangeData> getNumberingPlan() {
        return numberingPlan;
    }

    public void setNumberingPlan(final List<CpsVoipRangeData> numberingPlan) {
        this.numberingPlan = numberingPlan;
    }
}
