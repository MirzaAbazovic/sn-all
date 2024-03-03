/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 13:15:20
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import java.util.stream.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.billing.AdresseDAO;
import de.augustakom.hurrican.dao.billing.AnsprechpartnerDAO;
import de.augustakom.hurrican.dao.billing.KundeDAO;
import de.augustakom.hurrican.model.billing.AddressFormat;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Ansprechpartner;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.shared.view.DefaultSharedAuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Service-Implementierung fuer die Verwaltung von Kundendaten.
 *
 *
 */
@BillingTx
public class KundenServiceImpl extends DefaultBillingService implements KundenService {

    private static final Logger LOGGER = Logger.getLogger(KundenServiceImpl.class);

    private Object ansprechpartnerDAO = null;
    private Object adresseDAO = null;

    @Override
    public List<Kunde> findByKundeNos(List<Long> kundeNos) throws FindException {
        if (kundeNos == null) { return null; }
        try {
            Set<Long> kNoSet = kundeNos.stream().collect(Collectors.toSet());

            List<Long> kNos = new ArrayList<>();
            kNos.addAll(kNoSet);

            return ((KundeDAO) getDAO()).findByKundeNos(kNos);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Kunde findKunde(Long kundeNo) throws FindException {
        if (kundeNo == null) { return null; }
        try {
            return ((KundeDAO) getDAO()).findById(kundeNo, Kunde.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<Long, Kunde> findKundenByKundeNos(Long[] kNos) throws FindException {
        try {
            KundeDAO dao = (KundeDAO) getDAO();
            return dao.findByKundeNos(kNos);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<KundeAdresseView> findKundeAdresseViewsByQuery(KundeQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            KundeDAO dao = (KundeDAO) getDAO();
            return dao.findByQuery(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Ansprechpartner> getAnsprechpartner4Kunden(List<Long> kNos) throws FindException {
        if (kNos == null) { return null; }
        try {
            Set<Long> kNoSet = kNos.stream().collect(Collectors.toSet());

            List<Long> kNoList = new ArrayList<>();
            kNoList.addAll(kNoSet);

            return ((AnsprechpartnerDAO) getAnsprechpartnerDAO()).findByKundeNos(kNoList);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Adresse getAdresse4Kunde(Long kundenNo) throws FindException {
        try {
            Kunde kunde = findKunde(kundenNo);

            if ((kunde != null) && (kunde.getPostalAddrNo() != null)) {
                AdresseDAO dao = (AdresseDAO) getAdresseDAO();
                return dao.findById(kunde.getPostalAddrNo(), Adresse.class);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String[] formatAddress(Adresse adresse, String type) throws FindException {
        if (adresse == null) { return null; }
        try {
            AddressFormat af = ((AdresseDAO) getAdresseDAO()).findById(adresse.getFormatName(), AddressFormat.class);
            if (af == null) {
                throw new FindException(
                        "Format-Definition fuer Adresse wurde nicht gefunden: " + adresse.getFormatName());
            }

            return af.format(adresse, type);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Adresse getAdresseByAdressNo(Long adressNo) throws FindException {
        if (adressNo == null) { return null; }
        try {
            return ((AdresseDAO) getAdresseDAO()).findById(adressNo, Adresse.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Adresse> findAdressen4Kunden(List<Long> kundeNos) throws FindException {
        if (kundeNos == null) { return null; }
        try {
            List<Kunde> kunden = findByKundeNos(kundeNos);
            List<Long> addrIds = new ArrayList<>();

            if (kunden != null && kunden.isEmpty()) {
                addrIds.addAll(kunden.stream().filter(kunde -> (kunde != null) && (kunde.getPostalAddrNo() != null))
                        .map(Kunde::getPostalAddrNo).collect(Collectors.toList()));

                return ((AdresseDAO) getAdresseDAO()).findByANos(addrIds);
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void loadKundendaten4AuftragViews(List<? extends DefaultSharedAuftragView> views) {
        try {
            if (views != null) {
                Set<Long> keys = views.stream().map(DefaultSharedAuftragView::getKundeNo).collect(Collectors.toSet());

                Long[] kNoOrigs = keys.toArray(new Long[keys.size()]);

                if (kNoOrigs.length > 0) {
                    Map<Long, Kunde> kunden = findKundenByKundeNos(kNoOrigs);
                    if (kunden != null) {
                        for (Iterator<? extends DefaultSharedAuftragView> iter = views.iterator(); iter.hasNext(); ) {
                            DefaultSharedAuftragView view = iter.next();
                            Kunde k = kunden.get(view.getKundeNo());
                            if (k != null) {
                                if (k.isLocked()) { // View aus Ergebnis entfernen wenn Kunde gesperrt
                                    iter.remove();
                                }
                                else {
                                    view.setName(k.getName());
                                    view.setVorname(k.getVorname());
                                    view.setKundenTyp(k.getKundenTyp());
                                    view.setVip(k.getVip());
                                    view.setFernkatastrophe(k.getFernkatastrophe());
                                    view.setHauptKundenNo(k.getHauptKundenNo());
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Long> findKundeNos4HauptKunde(Long hauptKundeNo) throws FindException {
        try {
            return ((KundeDAO) getDAO()).findKundeNos4HauptKunde(hauptKundeNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<Long, String> findKundenNamen(List<Long> kundeNos) throws FindException {
        if ((kundeNos == null) || (kundeNos.isEmpty())) { return null; }
        try {
            return ((KundeDAO) getDAO()).findKundenNamen(kundeNos);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean isSameKundenkreis(Long kundeNoA, Long kundeNoB) throws FindException {
        if ((kundeNoA == null) || (kundeNoB == null)) { return false; }
        if (NumberTools.equal(kundeNoA, kundeNoB)) {
            return true;
        }
        else {
            Kunde kundeA = findKunde(kundeNoA);
            Kunde kundeB = findKunde(kundeNoB);

            if (NumberTools.equal(kundeA.getHauptKundenNo(), kundeNoB) ||
                    NumberTools.equal(kundeB.getHauptKundenNo(), kundeNoA)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt das DAO fuer Objekte des Typs <code>Ansprechpartner</code> zurueck.
     *
     * @return Returns the ansprechpartnerDAO.
     */
    public Object getAnsprechpartnerDAO() {
        return ansprechpartnerDAO;
    }

    /**
     * Setzt das DAO fuer Objekte des Typs <code>Ansprechpartner</code>
     *
     * @param ansprechpartnerDAO The ansprechpartnerDAO to set.
     */
    public void setAnsprechpartnerDAO(Object ansprechpartnerDAO) {
        this.ansprechpartnerDAO = ansprechpartnerDAO;
    }

    /**
     * Gibt das DAO fuer Objekte des Typs <code>Adresse</code> zurueck.
     *
     * @return Returns the adresseDAO.
     */
    public Object getAdresseDAO() {
        return adresseDAO;
    }

    /**
     * Setzt das DAO fuer Objekte des Typs <code>Adresse</code>
     *
     * @param adresseDAO The adresseDAO to set.
     */
    public void setAdresseDAO(Object adresseDAO) {
        this.adresseDAO = adresseDAO;
    }

}


