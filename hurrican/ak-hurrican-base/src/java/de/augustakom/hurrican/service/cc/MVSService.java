/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:30:25
 */

package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Definition fuer die Bearbeitung von MVS-Daten.
 */
public interface MVSService extends ICCService {

    /**
     * Speichert das angegebene AuftragMVS-Objekt.
     *
     * @param toSave
     * @throws StoreException
     */
    void saveAuftragMvs(AuftragMVS toSave) throws StoreException;

    /**
     * Ermittelt die MVS Enterprise Daten zum Auftrag mit der Id {@code auftragId}
     *
     * @param auftragId ID des Auftrags, dessen {@link AuftragMVSEnterprise} (Zusatz)Daten ermittelt werden sollen
     * @return {@link AuftragMVSEnterprise} Daten des Auftrags
     */
    AuftragMVSEnterprise findMvsEnterprise4Auftrag(Long auftragId) throws FindException;

    /**
     * Ermittelt die MVS-Site Daten zum Auftrag mit der Id {@code auftragId}.
     *
     * @param auftragId
     * @param loadStandortKuerzel ob das Standort Kuerzel aus Vento geladen werden soll
     * @return {@link AuftragMVSSite} Daten des Auftrags.
     */
    AuftragMVSSite findMvsSite4Auftrag(Long auftragId, boolean loadStandortKuerzel) throws FindException;

    /**
     * Ermittelt alle Domains, die fuer die aktuellen {@link AuftragMVS} gespeichert wurden.
     */
    Collection<String> findAllUsedDomains() throws FindException;

    /**
     * Ermittelt alle Subdomains, die fuer die aktuellen {@link AuftragMVS} gespeichert wurden.
     */
    Collection<String> findAllUsedSubdomains(AuftragMVSEnterprise mvsEnterprise) throws FindException;

    /**
     * Ermittelt fuer die Auftragsnummer einer MVS Site die MVS Enterprise Daten. Falls keine Daten vorhanden sind, wird
     * eine {@link NoDataFoundException} geworfen.
     *
     * @param siteAuftragId
     * @return
     * @throws FindException
     */
    AuftragMVSEnterprise findEnterpriseForSiteAuftragId(Long siteAuftragId) throws FindException,
            NoDataFoundException;

    /**
     * Prueft ob bereits eine Subdomain wie die Angegebene in einem MVS Site Auftrag existiert fuer eine Domain aus dem
     * uebergeordneten MVS Enterprise Auftrag.
     *
     * @param mvsEnterprise
     * @param subdomain
     * @return
     */
    boolean isSubdomainAlreadyUsedInEnterpriseDomain(AuftragMVSEnterprise mvsEnterprise, String subdomain)
            throws FindException;

    /**
     * Prueft ob die angegebene Domain bereits in anderen MVS Enterprise Auftraegen verwendet wird.
     *
     * @param domain
     * @return
     * @throws FindException
     */
    boolean isDomainAlreadyUsed(String domain) throws FindException;

    /**
     * Ermittelt die ID des technischen Auftrages zu einer Enterprise Domaene.
     *
     * @param domain
     * @return die Auftragsnr. der die angegebene Domain zugewiesen ist oder null falls die Domain keinem MVS Enterprise
     * Auftrag zugeordnet ist
     * @throws FindException
     */
    public Long findAuftragIdByEnterpriseDomain(String enterpriseDomain) throws FindException;

    /**
     * Ermittelt die Qualified Domain. Diese setzt sich aus der {@code subdomain} des Site Auftrages und der {@code
     * domain} des Enterprise Auftrages zusammen.
     *
     * @return {@code subdomain} + {@code domain}
     * @throws FindException wennn {@code subdomain} oder {@code domain} nicht gesetzt sind
     */
    public String getQualifiedDomain(AuftragMVSEnterprise auftragMVSEnterprise, AuftragMVSSite auftragMVSSite)
            throws NoDataFoundException;

}
