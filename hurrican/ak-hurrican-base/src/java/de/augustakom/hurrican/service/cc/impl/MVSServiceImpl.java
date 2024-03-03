/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:32:42
 */

package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragMVSDAO;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.MVSService;

@CcTxRequired
public class MVSServiceImpl extends DefaultCCService implements MVSService {

    private static final Logger LOGGER = Logger.getLogger(MVSServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.AuftragMVSDAO")
    private AuftragMVSDAO auftragMVSDAO;

    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService kundenService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService ccKundenService;

    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;

    @Override
    public void saveAuftragMvs(AuftragMVS toSave) throws StoreException {
        try {
            auftragMVSDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Transactional(
            value = "cc.hibernateTxManager",
            rollbackFor = Exception.class,
            noRollbackFor = de.augustakom.common.tools.exceptions.NoDataFoundException.class,
            propagation = Propagation.REQUIRED
    )
    public AuftragMVSEnterprise findEnterpriseForSiteAuftragId(Long siteAuftragId) throws FindException {
        Auftrag siteAuftrag = getAuftrag(siteAuftragId);
        if (siteAuftrag == null) {
            throw new NoDataFoundException(String.format(
                    "Zu dem MVS Site Auftrag %d kann der Auftrag nicht ermittelt werden!", siteAuftragId));
        }
        final Long siteKundeNo = siteAuftrag.getKundeNo();
        Long enterpriseAuftragId = getEnterpriseAuftragId(siteKundeNo);

        // HUR-9466 -> kein MVS-Enterprise Auftrag fuer den Kunden, dann suche nach Auftrag fuer den Hauptkunden
        if (enterpriseAuftragId == null) {
            enterpriseAuftragId = findEnterpriseAuftragIdForHauptkunde(siteKundeNo);
        }

        AuftragMVSEnterprise enterprise = findMvsEnterprise4Auftrag(enterpriseAuftragId);
        if (enterprise == null) {
            final String errorMsg = String.format(
                    "Fuer diesen MVS Site Auftrag wurden bisher keine MVS Enterprise Daten angelegt! "
                            + "Bitte erst fuer den Auftrag (%s) Daten anlegen!", enterpriseAuftragId
            );
            throw new NoDataFoundException(errorMsg);
        }
        return enterprise;
    }

    @CheckForNull
    private Long findEnterpriseAuftragIdForHauptkunde(final Long siteKundeNo)
            throws FindException {
        final Kunde siteKunde = findKunde(siteKundeNo);
        final Long hauptKundeNo = siteKunde.getHauptKundenNo();
        Long enterpriseAuftragId = null;
        if (hauptKundeNo != null) {
            enterpriseAuftragId = getEnterpriseAuftragId(hauptKundeNo);
        }
        return enterpriseAuftragId;
    }

    /**
     * Ermittelt die Auftragsnummer des MVS Enterprise Auftrag fuer den gegebenen {@link Kunde}n.
     */
    @CheckForNull
    private Long getEnterpriseAuftragId(Long kundeNo) throws FindException {
        List<Long> auftragIds = ccKundenService.findActiveAuftragInProdGruppe(kundeNo,
                Arrays.asList(ProduktGruppe.MVS_ENTERPRISE));
        if (auftragIds.size() > 1) {
            throw new FindException(
                    String.format(
                            "Inkonsistenter Datenbestand: es wurden mehrere MVS Enterprise Aufträge für die Kundennummer %d gefunden!",
                            kundeNo)
            );
        }
        return Iterables.getFirst(auftragIds, null);
    }

    @Nonnull
    private Kunde findKunde(final Long kundeNo) throws FindException {
        final Kunde kunde = kundenService.findKunde(kundeNo);
        if (kunde == null) {
            throw new FindException(String.format("Es konnte kein Kunde mit No. %d aus Taifun ermittelt werden!",
                    kundeNo));
        }
        return kunde;
    }

    /**
     * Ermittelt den {@link Auftrag} fuer die gegebene Auftragsnummer.
     */
    private Auftrag getAuftrag(Long auftragId) throws FindException {
        return auftragService.findAuftragById(auftragId);
    }

    @Override
    public AuftragMVSEnterprise findMvsEnterprise4Auftrag(Long auftragId) throws FindException {
        AuftragMVSEnterprise result = null;
        try {
            result = auftragMVSDAO.find4Auftrag(auftragId, AuftragMVSEnterprise.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return result;
    }

    @Override
    public AuftragMVSSite findMvsSite4Auftrag(Long auftragId, boolean loadStandortKuerzel)
            throws FindException {
        AuftragMVSSite result = null;
        try {
            result = auftragMVSDAO.find4Auftrag(auftragId, AuftragMVSSite.class);

            if (loadStandortKuerzel && (result != null)) {
                final Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId,
                        Endstelle.ENDSTELLEN_TYP_B);
                if (endstelle == null) {
                    throw new FindException(String.format(
                            "Keine Endstelle vom Typ B für den techn. Auftrag mit Id %d gefunden", auftragId));
                }
                final Long geoId = endstelle.getGeoId();
                result.setStandortKuerzel(availabilityService.findStandortKuerzelForGeoId(geoId));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return result;
    }

    @Override
    public Collection<String> findAllUsedDomains() throws FindException {
        Collection<String> result = Collections.emptyList();
        try {
            result = auftragMVSDAO.findAllUsedDomains();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return result;
    }

    @Override
    public boolean isSubdomainAlreadyUsedInEnterpriseDomain(AuftragMVSEnterprise mvsEnterprise, String subdomain)
            throws FindException {
        Collection<String> allSubdomains = auftragMVSDAO.findAllUsedSubdomains(mvsEnterprise);
        return allSubdomains.contains(subdomain);
    }

    @Override
    public Collection<String> findAllUsedSubdomains(AuftragMVSEnterprise mvsEnterprise) throws FindException {
        Collection<String> result = Collections.emptyList();
        try {
            result = auftragMVSDAO.findAllUsedSubdomains(mvsEnterprise);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return result;
    }

    @Override
    public boolean isDomainAlreadyUsed(String domain) throws FindException {
        Collection<String> allDomains = auftragMVSDAO.findAllUsedDomains();
        final boolean result = allDomains.contains(domain);
        return result;
    }

    @Override
    public Long findAuftragIdByEnterpriseDomain(String enterpriseDomain) throws FindException {
        Long aIdByDomain = null;
        try {
            List<AuftragMVSEnterprise> auftragAsList = auftragMVSDAO.findByProperty(AuftragMVSEnterprise.class,
                    "domain", enterpriseDomain);
            if (CollectionUtils.isNotEmpty(auftragAsList)) {
                aIdByDomain = Iterables.getOnlyElement(auftragAsList).getAuftragId();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return aIdByDomain;
    }

    @Override
    public String getQualifiedDomain(AuftragMVSEnterprise auftragMVSEnterprise, AuftragMVSSite auftragMVSSite)
            throws NoDataFoundException {
        // Check Preconditions
        if ((auftragMVSSite == null) || StringUtils.isEmpty(auftragMVSSite.getSubdomain())
                || (auftragMVSEnterprise == null) || StringUtils.isEmpty(auftragMVSEnterprise.getDomain())) {
            throw new NoDataFoundException(
                    String.format(
                            "Domain und/oder Subdomain zu dem MVS Site Auftrag mit techn. Auftrag-Id %d sind nicht gesetzt! (domain=%s, subdomain=%s)",
                            (auftragMVSSite != null) ? auftragMVSSite.getAuftragId() : null,
                            (auftragMVSEnterprise != null) ? auftragMVSEnterprise.getDomain() : null,
                            (auftragMVSSite != null) ? auftragMVSSite.getSubdomain() : null)
            );
        }
        return new StringBuilder(auftragMVSSite.getSubdomain()).append(".").append(auftragMVSEnterprise.getDomain())
                .toString();
    }

}
