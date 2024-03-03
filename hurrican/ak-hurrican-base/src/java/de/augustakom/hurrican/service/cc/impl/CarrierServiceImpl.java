/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 10:12:31
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.validation.constraints.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.CarrierbestellungDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierMapping;
import de.augustakom.hurrican.model.cc.CarrierVaModus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.view.AQSView;
import de.augustakom.hurrican.model.cc.view.CuDAVorschau;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.carrier.SaveCBDistance2GeoId2TechLocationsCommand;
import de.augustakom.hurrican.service.cc.impl.reportdata.CBJasperDS;
import de.augustakom.hurrican.service.cc.utils.carrier.CarrierLbzDTAGCreator;
import de.augustakom.hurrican.validation.cc.CarrierLbzDTAGValidator;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.wbci.model.CarrierCode;

/**
 * Implementierung von <code>CarrierService</code>.
 */
@CcTxRequired
public class CarrierServiceImpl extends DefaultCCService implements CarrierService {

    private static final Logger LOGGER = Logger.getLogger(CarrierServiceImpl.class);

    private CarrierbestellungDAO carrierbestellungDAO;
    private AbstractValidator carrierbestellungValidator;

    @Autowired
    private CarrierLbzDTAGCreator carrierLbzDTAGCreator;
    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    @CcTxRequiresNew
    public List<Carrierbestellung> findCBs4Endstelle(Long endstelleId) throws FindException {
        return findCBs4EndstelleTx(endstelleId);
    }

    @Override
    public List<Carrierbestellung> findCBs4EndstelleTx(Long endstelleId) throws FindException {
        if (endstelleId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().findByEndstelle(endstelleId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Carrierbestellung> findCBsByNotExactVertragsnummer(String vertragsnummer) {
        // First try exact search
        List<Carrierbestellung> carrierbestellungen = carrierbestellungDAO.findByVertragsnummer(vertragsnummer);
        if (carrierbestellungen.isEmpty() && StringUtils.isNotBlank(vertragsnummer)) {
            if (vertragsnummer.startsWith("0")) {
                // Try to remove leading zeros
                carrierbestellungen = carrierbestellungDAO.findByVertragsnummer(CharMatcher.is('0').trimLeadingFrom(
                        vertragsnummer));
            }
            else if (vertragsnummer.matches("[0-9]{3}[A-Z][0-9]+")) {
                // try to remove leading zeros after 'A' (example: 123A000456 matches with 123A456)
                String prefix = StringUtils.substring(vertragsnummer, 0, 4);
                String suffix = StringUtils.substring(vertragsnummer, 4);

                String search = prefix + CharMatcher.is('0').trimLeadingFrom(suffix);
                carrierbestellungen = carrierbestellungDAO.findByVertragsnummer(search);
            }
        }

        return carrierbestellungen;
    }

    @Override
    public Carrierbestellung findLastCB4Endstelle(Long endstelleId) throws FindException {
        if (endstelleId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().findLastByEndstelle(endstelleId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Carrierbestellung> findCBs(Long cb2esId) throws FindException {
        return findCBsTx(cb2esId);
    }

    @Override
    public List<Carrierbestellung> findCBsTx(Long cb2esId) throws FindException {
        if (cb2esId == null) {
            return new ArrayList<>();
        }
        try {
            Carrierbestellung example = new Carrierbestellung();
            example.setCb2EsId(cb2esId);

            return getCarrierbestellungDAO().queryByExample(example, Carrierbestellung.class, new String[] { "id" },
                    null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Carrierbestellung findCB(Long cbId) throws FindException {
        if (cbId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().findById(cbId, Carrierbestellung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Carrierbestellung> findCBs4LBZ(String lbz) throws FindException {
        if (lbz == null) {
            return new ArrayList<>();
        }
        try {
            Carrierbestellung example = new Carrierbestellung();
            example.setLbz(lbz);

            return getCarrierbestellungDAO().queryByExample(example, Carrierbestellung.class, new String[] { "id" },
                    null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AQSView> findAqsLL4GeoId(Long geoId) throws FindException {
        if (geoId == null) {
            return Collections.emptyList();
        }
        try {
            return getCarrierbestellungDAO().findAqsLL4GeoId(geoId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Carrierbestellung> findCBs4Carrier(Long carrierId, int maxResults, int beginWith)
            throws FindException {
        if (carrierId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().findCBs4Carrier(carrierId, maxResults, beginWith);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void cbKuendigen(Long cbId) throws StoreException {
        if (cbId == null) {
            throw new StoreException("Es wurde keine ID für die zu kündigende Carrierbestellung angegeben.");
        }

        try {
            CarrierbestellungDAO dao = getCarrierbestellungDAO();
            Carrierbestellung cb = dao.findById(cbId, Carrierbestellung.class);
            if (cb != null) {
                if (StringUtils.isBlank(cb.getLbz())) {
                    throw new StoreException("Die Carrierbestellung konnte nicht gekündigt werden, da "
                            + "keine LBZ eingetragen ist!");
                }

                if (cb.getKuendBestaetigungCarrier() != null) {
                    throw new StoreException("Die Carrierbestellung (LBZ: " + cb.getLbz()
                            + ") wurde bereits gekündigt!");
                }

                cb.setKuendigungAnCarrier(new Date());
                dao.store(cb);
            }
            else {
                throw new StoreException("Die zu kündigende Carrierbestellung konnte nicht gefunden werden.");
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public void cbKuendigenNewTx(Long cbId) throws StoreException {
        cbKuendigen(cbId);
    }

    @Override
    public List<Carrier> findCarrier() throws FindException {
        try {
            return getCarrierbestellungDAO().findCarrier();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Carrier> findCarrierForAnbieterwechsel() throws FindException {
        try {
            return getCarrierbestellungDAO().findCarrierForAnbieterwechsel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Carrier findCarrier(Long carrierId) throws FindException {
        if (carrierId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().findById(carrierId, Carrier.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Carrier> findCarrier(CarrierVaModus modus) throws FindException {
        try {
            return getCarrierbestellungDAO().findCarrierByVaModus(modus);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CarrierKennung findCarrierKennung(Long ckId) throws FindException {
        if (ckId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().findById(ckId, CarrierKennung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CarrierKennung findCarrierKennung(String portierungsKennung) throws FindException {
        if (StringUtils.isBlank(portierungsKennung)) {
            return null;
        }

        try {
            CarrierKennung example = new CarrierKennung();
            example.setPortierungsKennung(portierungsKennung);

            List<CarrierKennung> result = getCarrierbestellungDAO().queryByExample(example, CarrierKennung.class);
            if (!CollectionTools.hasExpectedSize(result, 1)) {
                throw new FindException(String.format(
                        "Zur Portierungskennung %s konnte keine (eindeutige) CarrierKennung ermittelt werden!",
                        portierungsKennung));
            }

            return result.get(0);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CarrierKennung> findCarrierKennungen() throws FindException {
        try {
            return getCarrierbestellungDAO().findAll(CarrierKennung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CarrierKennung findCarrierKennung4Hvt(Long hvtStandortId) throws FindException {
        if (hvtStandortId == null) {
            return null;
        }
        try {
            HVTStandort hvts = (getCCService(HVTService.class)).findHVTStandort(hvtStandortId);
            return findCarrierKennung(hvts.getCarrierKennungId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Long findHvtStdId4Cb(Long cbId) throws FindException {
        if (cbId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().findHvtStdId4Cb(cbId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Carrier findCarrier4HVT(Long hvtId) throws FindException {
        if (hvtId == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().find4HVT(hvtId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void validateLbz(Long carrierId, String lbz) throws ValidationException {
        if ((carrierId == null) || StringUtils.isBlank(lbz)) {
            throw new ValidationException(null, "Kein Objekt angegeben!");
        }
        try {
            if (Carrier.ID_DTAG.equals(carrierId)) {
                ValidationException ve = new ValidationException(lbz, "Lbz");
                CarrierLbzDTAGValidator validator = new CarrierLbzDTAGValidator();
                validator.validate(lbz, ve);

                if (ve.hasErrors()) {
                    throw ve;
                }
            }
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(lbz, e.getMessage());
        }
    }

    @Override
    public String createLbz(Long carrierId, Long endstelleId) {
        if ((carrierId == null) || (endstelleId == null) || (!Carrier.ID_DTAG.equals(carrierId))) {
            return null;
        }
        try {
            return carrierLbzDTAGCreator.createLbz(endstelleId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Carrierbestellung saveCB(Carrierbestellung toSave, Endstelle endstelle4CB) throws StoreException,
            ValidationException {
        if ((toSave == null) || (endstelle4CB == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        ValidationException ve = new ValidationException(toSave, "Carrierbestellung");
        getCarrierbestellungValidator().validate(toSave, ve);
        if (ve.hasErrors()) {
            throw ve;
        }

        try {
            if (endstelle4CB.getCb2EsId() == null) {
                Long mappingId = getCarrierbestellungDAO().createNewMappingId();
                if (mappingId == null) {
                    throw new StoreException("Es konnte keine Verbindung zwischen der Endstelle und der "
                            + "Carrierbestellung hergestellt werden. CB wurde nicht gespeichert!");
                }
                endstelle4CB.setCb2EsId(mappingId);
                EndstellenService esSrv = getCCService(EndstellenService.class);
                esSrv.saveEndstelle(endstelle4CB);
            }

            if ((toSave.getId() == null) && (toSave.getAiAddressId() == null)
                    && (endstelle4CB.getHvtIdStandort() != null)) {
                // bei einer neuen CB die Standortadresse ermitteln und als Anschlussinhaberadresse
                // auf der Carrierbestellung halten
                CCAuftragService as = getCCService(CCAuftragService.class);
                AuftragDaten ad = as.findAuftragDatenByEndstelleTx(endstelle4CB.getId());

                EndstellenService esSrv = getCCService(EndstellenService.class);
                AddressModel address = esSrv.findAnschlussadresse4Auftrag(ad.getAuftragId(),
                        endstelle4CB.getEndstelleTyp());
                if (address != null) {
                    // Adresse als Anschlussinhaberadresse kopieren
                    CCAddress aiAdr = new CCAddress();
                    PropertyUtils.copyProperties(aiAdr, address);
                    aiAdr.setId(null);
                    aiAdr.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT_OWNER);

                    CCKundenService ks = getCCService(CCKundenService.class);
                    ks.saveCCAddress(aiAdr);

                    toSave.setAiAddressId(aiAdr.getId());
                }

                // bei neuer Bestellung den EQ-OUT Port ermitteln, der fuer
                // die Carrierbestellung relevant ist
                Long eqOutId = getEqOutId4ES(endstelle4CB);
                toSave.setEqOutId(eqOutId);
            }

            toSave.setCb2EsId(endstelle4CB.getCb2EsId());
            return getCarrierbestellungDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_SAVE_CARRIERBESTELLUNG, e);
        }
    }

    @Override
    public void deleteCB(Carrierbestellung toDelete) throws DeleteException {
        Preconditions.checkNotNull(toDelete.getId());
        try {
            List<CBVorgang> cbVorgaenge = getCCService(CarrierElTALService.class).findCBVorgaenge4CB(toDelete.getId());
            if (CollectionTools.isNotEmpty(cbVorgaenge)) {
                throw new DeleteException(
                        "Für die Carrierbestellung gibt es einen elektronischen Vorgang. Nur Carrierbestellungen ohne elektronische Vorgänge dürfen gelöscht werden.");
            }
            if (!toDelete.isLeereCarrierbestellung()) {
                throw new DeleteException(
                        "Nur leere Carrierbestellungen dürfen gelöscht werden. Entfernen sie alle Daten um die Carrierbestellung löschen zu können.");
            }
            carrierbestellungDAO.deleteById(toDelete.getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException("Fehler beim Loeschen der Carrierbestellung.", e);
        }
    }

    @Override
    public void saveCBWithoutAddress(Carrierbestellung toSave, Endstelle endstelle4CB) throws StoreException,
            ValidationException {
        if ((toSave == null) || (endstelle4CB == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        ValidationException ve = new ValidationException(toSave, "Carrierbestellung");
        getCarrierbestellungValidator().validate(toSave, ve);
        if (ve.hasErrors()) {
            throw ve;
        }

        try {
            if (endstelle4CB.getCb2EsId() == null) {
                Long mappingId = getCarrierbestellungDAO().createNewMappingId();
                if (mappingId == null) {
                    throw new StoreException("Es konnte keine Verbindung zwischen der Endstelle und der "
                            + "Carrierbestellung hergestellt werden. CB wurde nicht gespeichert!");
                }
                endstelle4CB.setCb2EsId(mappingId);
                EndstellenService esSrv = getCCService(EndstellenService.class);
                esSrv.saveEndstelle(endstelle4CB);
            }

            toSave.setCb2EsId(endstelle4CB.getCb2EsId());
            getCarrierbestellungDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_SAVE_CARRIERBESTELLUNG, e);
        }
    }

    @Override
    public void saveCarrier(Carrier toSave, Long sessionId) throws StoreException {
        try {
            AKUser user = getAKUserBySessionId(sessionId);
            toSave.setUserW(user.getLoginName());

            StoreDAO dao = getCarrierbestellungDAO();
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveCarrierContact(CarrierContact toSave, Long sessionId) throws StoreException {
        try {
            AKUser user = getAKUserBySessionId(sessionId);
            toSave.setUserW(user.getLoginName());

            StoreDAO dao = getCarrierbestellungDAO();
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveCarrierIdentifier(CarrierKennung toSave, Long sessionId) throws StoreException {
        try {
            AKUser user = getAKUserBySessionId(sessionId);
            toSave.setUserW(user.getLoginName());

            StoreDAO dao = getCarrierbestellungDAO();
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveCarrierMapping(CarrierMapping toSave, Long sessionId) throws StoreException {
        try {
            AKUser user = getAKUserBySessionId(sessionId);
            toSave.setUserW(user.getLoginName());

            StoreDAO dao = getCarrierbestellungDAO();
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Ermittelt den EQ-OUT Port der angegebenen Endstelle.
     *
     * @param endstelle4CB
     * @return ID des EQ-OUT Ports der der angegebenen Endstelle zugeordnet ist.
     */
    private Long getEqOutId4ES(Endstelle endstelle4CB) throws FindException {
        if (endstelle4CB.getRangierId() != null) {
            try {
                RangierungsService rs = getCCService(RangierungsService.class);
                Rangierung rangierung = rs.findRangierung(endstelle4CB.getRangierId());
                return (rangierung != null) ? rangierung.getEqOutId() : null;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new FindException(
                        "Relevanter EQ-OUT Port fuer die Carrierbestellung konnte nicht ermittelt werden.");
            }
        }
        return null;
    }

    @Override
    public void saveCB(Carrierbestellung toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            getCarrierbestellungDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_SAVE_CARRIERBESTELLUNG, e);
        }
    }

    @Override
    public void saveCBDistance2GeoId2TechLocations(Carrierbestellung carrierBestellung, Long sessionId)
            throws StoreException {
        if (carrierBestellung == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(
                    SaveCBDistance2GeoId2TechLocationsCommand.class.getName());
            cmd.prepare(SaveCBDistance2GeoId2TechLocationsCommand.KEY_CARRIER_ORDER, carrierBestellung);
            cmd.prepare(SaveCBDistance2GeoId2TechLocationsCommand.KEY_SESSION_ID, sessionId);
            cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CuDAVorschau> createCuDAVorschau(Date vorgabeSCV) throws FindException {
        if (vorgabeSCV == null) {
            return null;
        }
        try {
            return getCarrierbestellungDAO().createCuDAVorschau(vorgabeSCV);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragDaten> findAuftragDaten4CB(Long cbId) throws FindException {
        List<AuftragDaten> result = Lists.newArrayList();
        if (cbId != null) {
            try {
                List<Long> ads = getCarrierbestellungDAO().findAuftragIds4CB(cbId);
                if (ads != null) {
                    CCAuftragService ccAS = getCCService(CCAuftragService.class);
                    for (Long aId : ads) {
                        result.add(ccAS.findAuftragDatenByAuftragIdTx(aId));
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new FindException(FindException._UNEXPECTED_ERROR, e);
            }
        }
        return result;
    }

    @Override
    public AuftragDaten findReferencingOrder(@NotNull CBVorgang cbVorgang) throws FindException {
        try {
            AuftragDaten auftragDaten = null;
            if (cbVorgang.isAenderung()) {
                CCAuftragService auftragService = getCCService(CCAuftragService.class);
                Carrierbestellung carrierBestellung = findCB(cbVorgang.getCbId());
                if (carrierBestellung != null) {
                    Long auftragId4TalNa = carrierBestellung.getAuftragId4TalNA();
                    auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId4TalNa);
                }
            }
            return auftragDaten;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint reportCuDAKuendigung(Long cbId, Long esId, Long sessionId) throws AKReportException {
        if ((cbId == null) || (esId == null)) {
            throw new AKReportException("Ungueltige Parameter fuer die CuDA-Kuendigung!");
        }
        try {
            AKUser user = getAKUserBySessionId(sessionId);
            CBJasperDS cbDS = new CBJasperDS(cbId, esId, user);

            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/auftrag/CuDAKuendigung.jasper", new HashMap<>(), cbDS);

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            JasperPrint jp = jrh.createReport(ctx);
            return jp;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("CuDA-Kuendigung konnte nicht erstellt werden!", e);
        }
    }


    /**
     * @return Returns the carrierbestellungDAO.
     */
    public CarrierbestellungDAO getCarrierbestellungDAO() {
        return carrierbestellungDAO;
    }

    /**
     * @param carrierbestellungDAO The carrierbestellungDAO to set.
     */
    public void setCarrierbestellungDAO(CarrierbestellungDAO carrierbestellungDAO) {
        this.carrierbestellungDAO = carrierbestellungDAO;
    }

    /**
     * @return Returns the carrierbestellungValidator.
     */
    public AbstractValidator getCarrierbestellungValidator() {
        return carrierbestellungValidator;
    }

    /**
     * @param carrierbestellungValidator The carrierbestellungValidator to set.
     */
    public void setCarrierbestellungValidator(AbstractValidator carrierbestellungValidator) {
        this.carrierbestellungValidator = carrierbestellungValidator;
    }

    @Override
    public void storeCarrierbestellung(Carrierbestellung carierBestellung) {
        carrierbestellungDAO.store(carierBestellung);
        carrierbestellungDAO.flushSession();
    }

    @Override
    public List<Carrier> findWbciAwareCarrier() throws FindException {
        try {
            return getCarrierbestellungDAO().findWbciAwareCarrier();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Carrier findCarrierByCarrierCode(CarrierCode carrierCode) throws FindException {
        try {
            return getCarrierbestellungDAO().findCarrierByCarrierCode(carrierCode);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Carrier findCarrierByPortierungskennung(String portierungskennung) throws FindException {
        try {
            return getCarrierbestellungDAO().findCarrierByPortierungskennung(portierungskennung);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

}
