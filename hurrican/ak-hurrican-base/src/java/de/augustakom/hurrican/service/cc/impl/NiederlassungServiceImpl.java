/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:29:31
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AbteilungDAO;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Abt2NL;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Service-Implementierung fuer die Verwaltung von Niederlassungen und Abteilungen.
 *
 *
 */
@CcTxRequired
public class NiederlassungServiceImpl extends DefaultCCService implements NiederlassungService {

    private static final Logger LOGGER = Logger.getLogger(NiederlassungServiceImpl.class);

    private AbteilungDAO abteilungDAO = null;

    @Override
    public List<Niederlassung> findNiederlassungen() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getDAO();
            return dao.findAll(Niederlassung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Abteilung findAbteilung(Long id) throws FindException {
        if (id == null) { return null; }
        try {
            return getAbteilungDAO().findById(id, Abteilung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Abteilung> findAbteilungen() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getAbteilungDAO();
            return dao.findAll(Abteilung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Abteilung> findAbteilungen4Proj() throws FindException {
        try {
            Abteilung example = new Abteilung();
            example.setRelevant4Proj(Boolean.TRUE);
            return getAbteilungDAO().queryByExample(example, Abteilung.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Abteilung> findAbteilungen4Ba() throws FindException {
        try {
            Abteilung example = new Abteilung();
            example.setRelevant4Ba(Boolean.TRUE);
            return getAbteilungDAO().queryByExample(example, Abteilung.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Abteilung> findAbteilungen4UniversalGui() throws FindException {
        try {
            Abteilung example = new Abteilung();
            example.setValid4UniversalGui(Boolean.TRUE);
            return getAbteilungDAO().queryByExample(example, Abteilung.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Abteilung> findAbteilungen(Collection<Long> abtIds) throws FindException {
        if ((abtIds == null) || (abtIds.isEmpty())) { return null; }
        try {
            return getAbteilungDAO().findByIds(abtIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Niederlassung findNiederlassung(Long niederlassungId) throws FindException {
        if (niederlassungId == null) { return null; }
        try {
            return getAbteilungDAO().findById(niederlassungId, Niederlassung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Niederlassung findNiederlassung4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            CCAuftragService aService = getCCService(CCAuftragService.class);
            AuftragTechnik at = aService.findAuftragTechnikByAuftragIdTx(auftragId);
            if ((at != null) && (at.getNiederlassungId() != null)) {
                return findNiederlassung(at.getNiederlassungId());
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Niederlassung findNiederlassung4Kunde(Long kundeNo) throws FindException {
        if (kundeNo == null) { return null; }
        try {
            KundenService kService = getBillingService(KundenService.class);
            Kunde kunde = kService.findKunde(kundeNo);

            ReferenceService rs = getCCService(ReferenceService.class);
            Reference mappingRef = rs.findReference(
                    Reference.REF_TYPE_RESELLER_2_NL, "" + kunde.getResellerKundeNo());
            return ((mappingRef != null) && (mappingRef.getIntValue() != null))
                    ? findNiederlassung(Long.valueOf(mappingRef.getIntValue())) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Niederlassung findNiederlassung4User(Long sessionId) throws FindException {
        if (sessionId == null) {
            return null;
        }
        try {
            AKUser user = getAKUserBySessionId(sessionId);
            if ((user != null) && (user.getNiederlassungId() != null)) {
                return findNiederlassung(user.getNiederlassungId());
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Niederlassung> findNL4Abteilung(Long abtId) throws FindException {
        if (abtId == null) {
            return null;
        }
        try {
            return abteilungDAO.findNL4Abteilung(abtId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Abt2NL findAbt2NL(Long abtId, Long nlId) throws FindException {
        if ((abtId == null) || (nlId == null)) {
            return null;
        }
        try {
            Abt2NL example = new Abt2NL();
            example.setAbteilungId(abtId);
            example.setNiederlassungId(nlId);
            List<Abt2NL> result = abteilungDAO.queryByExample(example, Abt2NL.class);

            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void setNiederlassung4Auftrag(Long auftragId) throws StoreException {
        if (auftragId == null) {
            return;
        }
        try {
            ProduktService produktService = getCCService(ProduktService.class);
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            EndstellenService endstellenService = getCCService(EndstellenService.class);
            AvailabilityService availabilityService = getCCService(AvailabilityService.class);
            HVTService hvtService = getCCService(HVTService.class);

            Produkt prod = produktService.findProdukt4Auftrag(auftragId);

            if (prod != null) {
                Niederlassung nl = null;

                // Falls Endstelle A+B erzeugt wird -> Niederlassung Zentral
                if (NumberTools.equal(Produkt.ES_TYP_A_UND_B, prod.getEndstellenTyp())) {
                    nl = findNiederlassung(Niederlassung.ID_ZENTRAL);
                }
                // Falls nur Endstelle B -> Niederlassung anhand des hoechst priorisierten Standorts von GeoID/Produkt
                else if (NumberTools.equal(Produkt.ES_TYP_NUR_B, prod.getEndstellenTyp())) {
                    Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
                    if ((endstelle != null) && (endstelle.getGeoId() != null)) {
                        GeoId geoId = availabilityService.findGeoId(endstelle.getGeoId());
                        List<GeoId2TechLocation> possibleGeoId2TechLocations =
                                availabilityService.findPossibleGeoId2TechLocations(geoId, prod.getId());
                        if (CollectionTools.isNotEmpty(possibleGeoId2TechLocations)) {
                            HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(
                                    possibleGeoId2TechLocations.get(0).getHvtIdStandort());
                            nl = findNiederlassung(hvtGruppe.getNiederlassungId());
                        }
                    }
                }
                // falls keine Endstelle -> Niederlassung ueber Reseller des Kunden
                else if (NumberTools.equal(Produkt.ES_TYP_KEINE_ENDSTELLEN, prod.getEndstellenTyp())) {
                    Auftrag auftrag = auftragService.findAuftragById(auftragId);
                    nl = (auftrag != null) ? findNiederlassung4Kunde(auftrag.getKundeNo()) : null;
                }

                // AuftragTechnik speichern
                if (nl != null) {
                    AuftragTechnik at = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
                    at.setNiederlassungId(nl.getId());
                    auftragService.saveAuftragTechnik(at, false);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    /**
     * @see de.augustakom.hurrican.service.cc.NiederlassungService#findNiederlassungByName(java.lang.String)
     */
    @Override
    public Niederlassung findNiederlassungByName(String name) throws FindException {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        try {
            Niederlassung example = new Niederlassung();
            example.setName(name);
            List<Niederlassung> result = getAbteilungDAO().queryByExample(example, Niederlassung.class);
            if ((result != null) && (result.size() > 1)) {
                throw new FindException(FindException.INVALID_RESULT_SIZE);
            }
            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @return Returns the abteilungDAO.
     */
    protected AbteilungDAO getAbteilungDAO() {
        return abteilungDAO;
    }

    /**
     * @param abteilungDAO The abteilungDAO to set.
     */
    public void setAbteilungDAO(AbteilungDAO abteilungDAO) {
        this.abteilungDAO = abteilungDAO;
    }
}


