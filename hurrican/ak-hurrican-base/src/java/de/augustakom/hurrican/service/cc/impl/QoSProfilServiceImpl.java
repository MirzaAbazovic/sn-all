/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.2013
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import java.util.regex.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.QoSProfilService;

@CcTxRequired
public class QoSProfilServiceImpl extends DefaultCCService implements QoSProfilService {

    private static final Logger LOGGER = Logger.getLogger(QoSProfilServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;

    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMService")
    private DSLAMService dslamService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;

    @Override
    @CheckForNull
    public QosProfileWithValidFromAndDownstream getQoSProfilDownStreamAndValidDate4Auftrag(final long auftragId,
            final Date validDate)
            throws FindException {
        final List<Auftrag2TechLeistung> a2techLs = ccLeistungsService.findAuftrag2TechLeistungen(auftragId,
                new String[] { TechLeistung.TYP_SIPTRUNK_QOS_PROFILE }, validDate, true);

        Auftrag2TechLeistung nearestInFuture = null;
        for (final Auftrag2TechLeistung a2t : a2techLs) {
            if ((nearestInFuture == null) || (DateTools.isDateBefore(a2t.getAktivVon(), nearestInFuture.getAktivVon()))) {
                nearestInFuture = a2t;
            }
        }

        if (nearestInFuture == null) {
            return null;
        }
        else {
            final TechLeistung qosProfile = ccLeistungsService.findTechLeistung(nearestInFuture.getTechLeistungId());
            final Long ds = getQoSDownStream4Auftrag(qosProfile, auftragId, nearestInFuture.getAktivVon());
            return new QosProfileWithValidFromAndDownstream(qosProfile, nearestInFuture.getAktivVon(), ds);
        }
    }

    public Long getQoSDownStream4Auftrag(final TechLeistung qosProfil, final long auftragId,
            final Date validDate) throws FindException {
        if (qosProfil == null) {
            return null;
        }
        final Long downStream = getDownStream4Auftrag(auftragId, validDate);
        if (downStream == null) {
            return null;
        }
        return downStream * qosProfil.getLongValue() / 100;
    }

    public Long getDownStream4Auftrag(final long auftragId, final Date validDate) throws FindException {
        Long downStream = null;
        DSLAMProfile dslamProfile = dslamService.findDSLAMProfile4AuftragNoEx(auftragId, validDate, false);

        if (dslamProfile != null) {
            downStream = Long.valueOf(dslamProfile.getBandwidth().getDownstream());
        }
        else {
            final TechLeistung dsLeistung = findDsLeistung(auftragId, validDate);
            if (dsLeistung != null) {
                downStream = dsLeistung.getLongValue();
            }
            else {
                downStream = extractDsFromAnschlussart(auftragId);
            }
        }
        return downStream;
    }

    private TechLeistung findDsLeistung(final long auftragId, final Date validDate) throws FindException {
        TechLeistung dsLeistung =
                ccLeistungsService.findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_DOWNSTREAM, validDate);

        if (dsLeistung == null) {
            dsLeistung =
                    ccLeistungsService.findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_CONNECT_LEITUNG, validDate);
        }
        return dsLeistung;
    }

    private Long extractDsFromAnschlussart(long auftragId) throws FindException {
        final Pattern pattern = Pattern.compile("\\S\\d+\\S*");
        final Produkt produkt = produktService.findProdukt4Auftrag(auftragId);
        final Matcher matcher = pattern.matcher(produkt.getAnschlussart());
        if (matcher.find()) {
            try {
                return Long.valueOf(matcher.group(0).trim());
            }
            catch (NumberFormatException nfe) {
                LOGGER.error(nfe.getMessage(), nfe);
            }
        }
        return null;
    }
}
