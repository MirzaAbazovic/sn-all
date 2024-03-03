/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13 11:09
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPBITData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.PbitAware;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 *
 */
@CcTxRequired
public class PbitHelper {

    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    protected CCLeistungsService ccLeistungsService;

    public void addVoipPbitIfNecessary(final PbitAware pbitAware, @CheckForNull final DSLAMProfile profile, final long auftragId, final Date execTime) throws FindException {
        final CPSPBITData voipPbit = getVoipPbitIfNecessary(profile, auftragId, execTime);
        if (voipPbit != null) {
            pbitAware.addPbit(voipPbit);
        }
    }

    public CPSPBITData getVoipPbitIfNecessary(final @CheckForNull DSLAMProfile profile, final long auftragId, final Date execTime) throws FindException {
        final TechLeistung qosProfile =
                ccLeistungsService.findTechLeistung4Auftrag(auftragId,
                        TechLeistung.TYP_SIPTRUNK_QOS_PROFILE, execTime);
        if (qosProfile != null) {
            final Long prio = qosProfile.getLongValue();
            if (prio == null) {
                throw new FindException(String.format("keine Prio für QoS-Profil %s angegeben",
                        qosProfile.getStrValue()));
            }
            else if (profile != null) {
                return new CPSPBITData("VOIP", (long) ((prio / 100.0) * profile.getBandwidth().getDownstream()));
            }
        }
        return null;
    }
}
