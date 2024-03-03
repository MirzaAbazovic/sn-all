/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 14:25:18
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ObjectRetrievalFailureException;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.InhouseDAO;
import de.augustakom.hurrican.dao.cc.LeitungsartDAO;
import de.augustakom.hurrican.dao.cc.MontageartDAO;
import de.augustakom.hurrican.dao.cc.PhysikTypDAO;
import de.augustakom.hurrican.dao.cc.PhysikUebernahmeDAO;
import de.augustakom.hurrican.dao.cc.SchnittstelleDAO;
import de.augustakom.hurrican.dao.cc.VerbindungsBezeichnungDAO;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Inhouse;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Montageart;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;
import de.augustakom.hurrican.model.cc.view.VerbindungsBezeichnungHistoryView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Service-Implementierung von <code>PhysikService</code>.
 *
 *
 */
@CcTxRequired
public class PhysikServiceImpl extends DefaultCCService implements PhysikService {

    private static final Logger LOGGER = Logger.getLogger(PhysikServiceImpl.class);

    private static final String LINE_ID_FORMAT = "DEU.MNET.%s";

    // DAOs
    private MontageartDAO montageartDAO = null;
    private VerbindungsBezeichnungDAO vbzDAO = null;
    private InhouseDAO inhouseDAO = null;
    private PhysikTypDAO physikTypDAO = null;
    private LeitungsartDAO leitungsartDAO = null;
    private SchnittstelleDAO schnittstelleDAO = null;
    private Object anschlussartDAO = null;
    private PhysikUebernahmeDAO physikUebernahmeDAO = null;
    // Services
    private ProduktService produktService;
    private CCAuftragService auftragService;
    private RangierungsService rangierungsService;
    private EndstellenService endstellenService;

    @Override
    public List<PhysikTyp> findPhysikTypen() throws FindException {
        try {
            return getPhysikTypDAO().findAll(PhysikTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<PhysikTyp> findPhysikTypen4ParentPhysik(List<Long> parentPhysikIds) throws FindException {
        if ((parentPhysikIds == null) || (parentPhysikIds.isEmpty())) { return null; }
        try {
            return getPhysikTypDAO().find4ParentPhysiktypen(parentPhysikIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public PhysikTyp findPhysikTyp(Long physikTypId) throws FindException {
        if (physikTypId == null) { return null; }
        try {
            return getPhysikTypDAO().findById(physikTypId, PhysikTyp.class);
        }
        catch (ObjectRetrievalFailureException e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void savePhysikTyp(PhysikTyp toSave) throws StoreException {
        try {
            StoreDAO dao = (StoreDAO) getPhysikTypDAO();
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<PhysikTyp> findPhysikTypen(PhysikTyp example) throws FindException {
        if (example == null) { return null; }
        try {
            return getPhysikTypDAO().queryByExample(example, PhysikTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public PhysikTyp findCorrespondingPhysiktyp(PhysikTyp oldPhysikTyp, Long newManufacturer) throws FindException {
        PhysikTyp example = new PhysikTyp();
        example.setHwSchnittstelle(oldPhysikTyp.getHwSchnittstelle());
        example.setHvtTechnikId(newManufacturer);

        List<PhysikTyp> correspondingPTs = findPhysikTypen(example);
        int size = (correspondingPTs != null) ? correspondingPTs.size() : 0;
        if (size == 1) {
            return correspondingPTs.get(0);
        }

        throw new FindException(
                String.format(
                        "Es konnte kein (oder kein eindeutiger) korrespondierender PhysikTyp fuer " +
                        "den Hersteller mit Id %s und HW-Schnittstelle %s ermittelt werden! " +
                        "Ermittelte Anzahl: %s",
                        newManufacturer, oldPhysikTyp.getHwSchnittstelle(), size));
    }

    @Override
    public boolean manufacturerChanged(PhysikTyp oldPhysikTyp, PhysikTyp newPhysikTyp) {
        if (newPhysikTyp == null) {
            return false;
        }

        return NumberTools.notEqual(oldPhysikTyp.getHvtTechnikId(), newPhysikTyp.getHvtTechnikId());
    }

    @Override
    @Nonnull
    public List<Produkt2PhysikTyp> findSimpleP2PTs4Produkt(Long produktId) throws FindException {
        if (produktId == null) {
            return Collections.emptyList();
        }
        try {
            return getPhysikTypDAO().findSimpleP2PTs4Produkt(produktId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Produkt2PhysikTyp> findP2PTs4Produkt(Long produktId, Boolean useInRangMatrix) throws FindException {
        if (produktId == null) { return null; }
        try {
            Produkt2PhysikTyp example = new Produkt2PhysikTyp();
            example.setProduktId(produktId);
            example.setUseInRangMatrix(useInRangMatrix);
            return getPhysikTypDAO().queryByExample(example, Produkt2PhysikTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Long> findPhysikTypen4Produkt(Long produktId) throws FindException {
        final List<Long> physikTypIds = Lists.newArrayList();
        final List<Produkt2PhysikTyp> p2Pts = findP2PTs4Produkt(produktId, null);
        if (p2Pts != null) {
            for (final Produkt2PhysikTyp produkt2PhysikTyp : p2Pts) {
                physikTypIds.add(produkt2PhysikTyp.getPhysikTypId());
            }
        }
        return physikTypIds;
    }

    @Override
    public List<Produkt2PhysikTyp> findP2PTsByQuery(Produkt2PhysikTypQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            return getPhysikTypDAO().findP2PTsByQuery(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Produkt2PhysikTyp findP2PT(Long p2ptId) throws FindException {
        if (p2ptId == null) { return null; }
        try {
            return getPhysikTypDAO().findById(p2ptId, Produkt2PhysikTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveP2PTs4Produkt(Long produktId, List<Produkt2PhysikTyp> toSave) throws StoreException {
        if ((produktId == null) || (toSave == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            // aktuelle P2PT-Eintraege laden
            Produkt2PhysikTyp example = new Produkt2PhysikTyp();
            example.setProduktId(produktId);
            List<Produkt2PhysikTyp> actP2PTs = getPhysikTypDAO().queryByExample(example, Produkt2PhysikTyp.class);

            // zu loeschende/aktualisierende P2PT-Eintraege ermitteln
            if (actP2PTs != null) {
                List<Produkt2PhysikTyp> toUpdate = new ArrayList<>();
                List<Produkt2PhysikTyp> toDelete = new ArrayList<>();
                for (Produkt2PhysikTyp p2pt : actP2PTs) {
                    boolean found = false;
                    for (Produkt2PhysikTyp toSaveP2PT : toSave) {
                        if (NumberTools.equal(p2pt.getId(), toSaveP2PT.getId())) {
                            found = true;
                            PropertyUtils.copyProperties(p2pt, toSaveP2PT);
                            p2pt.setProduktId(produktId);
                            toUpdate.add(p2pt);
                            break;
                        }
                    }

                    if (!found) {
                        toDelete.add(p2pt);
                    }
                }

                deletePhysiktyp(toDelete);

                getPhysikTypDAO().saveP2PTs(toUpdate);
            }

            // neu anzulegende Eintraege ermitteln
            List<Produkt2PhysikTyp> toInsert = new ArrayList<>();
            for (Produkt2PhysikTyp toInsertP2PT : toSave) {
                toInsertP2PT.setProduktId(produktId);
                if ((actP2PTs != null) && (!actP2PTs.isEmpty())) {
                    boolean found = false;
                    for (Produkt2PhysikTyp actP2PT : actP2PTs) {
                        if (NumberTools.equal(toInsertP2PT.getId(), actP2PT.getId())) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        toInsert.add(toInsertP2PT);
                    }
                }
                else {
                    toInsert = toSave;
                }
            }
            getPhysikTypDAO().saveP2PTs(toInsert);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void deletePhysiktyp(List<Produkt2PhysikTyp> toDelete) throws DeleteException {
        try {
            for (Produkt2PhysikTyp toDeleteP2PT : toDelete) {
                getPhysikTypDAO().deleteP2PTById(toDeleteP2PT.getId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException("Produkt-Physiktyp-Zuordnung konnte nicht gelöscht werden! " +
                    "Wahrscheinlich verweist eine Rangierungsmatrix noch auf das Mapping.");
        }
    }

    @Override
    public VerbindungsBezeichnung calculateVerbindungsBezeichnung(Long productId, Long taifunAuftragNoOrig) throws FindException {
        if (productId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            Produkt produkt = produktService.findProdukt(productId);
            if (produkt == null) {
                throw new FindException("VerbindungsBezeichnung not calculated because product not found!");
            }

            if (BooleanTools.nullToFalse(produkt.getLeitungsNrAnlegen())) {
                if (BooleanTools.nullToFalse(produkt.getVbzUseFromMaster())) {
                    VerbindungsBezeichnung verbindungsBezeichnung = getVerbindungsBezeichnungFromMaster(taifunAuftragNoOrig);
                    if (verbindungsBezeichnung == null) {
                        throw new FindException("VerbindungsBezeichnung from master order not found!");
                    }
                    return verbindungsBezeichnung;
                }
                else {
                    if (produkt.isWholesale()) {
                        Integer lineId = vbzDAO.getNextLineId();
                        VerbindungsBezeichnung verbindungsBezeichnung = new VerbindungsBezeichnung();
                        verbindungsBezeichnung.setVbz(String.format(LINE_ID_FORMAT, lineId));
                        return verbindungsBezeichnung;
                    }
                    else {
                        return createVerbindungsBezeichnung(produkt.getVbzKindOfUseProduct(),
                                produkt.getVbzKindOfUseType());
                    }
                }
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VerbindungsBezeichnung createVerbindungsBezeichnung(String kindOfUseProduct, String kindOfUseType)
            throws StoreException {
        VerbindungsBezeichnung verbindungsBezeichnung = new VerbindungsBezeichnung();
        verbindungsBezeichnung.setUniqueCode(getVbzDAO().getNextUniqueCode());
        verbindungsBezeichnung.setKindOfUseProduct(kindOfUseProduct);
        verbindungsBezeichnung.setKindOfUseType(kindOfUseType);
        verbindungsBezeichnung.setVbz(verbindungsBezeichnung.createVbzValue());
        return verbindungsBezeichnung;
    }

    @Override
    public VerbindungsBezeichnung createVerbindungsBezeichnung(Long productId, Long taifunAuftragNoOrig) throws StoreException {
        try {
            VerbindungsBezeichnung calculatedVbz = calculateVerbindungsBezeichnung(productId, taifunAuftragNoOrig);
            if ((calculatedVbz != null) && (calculatedVbz.getId() == null)) {
                getVbzDAO().store(calculatedVbz);
            }

            return calculatedVbz;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VerbindungsBezeichnung getVerbindungsBezeichnungFromMaster(Long taifunAuftragNoOrig) throws FindException {
        List<AuftragDaten> auftragDaten = auftragService.findAuftragDaten4OrderNoOrigTx(taifunAuftragNoOrig);
        if (CollectionTools.isNotEmpty(auftragDaten)) {

            for (AuftragDaten auftragToCheck : auftragDaten) {
                if (auftragToCheck.isAuftragActive()) {
                    Produkt produkt = produktService.findProdukt(auftragToCheck.getProdId());
                    if (BooleanTools.nullToFalse(produkt.getLeitungsNrAnlegen())
                            && StringUtils.isNotBlank(produkt.getVbzKindOfUseProduct())) {
                        return findVerbindungsBezeichnungByAuftragId(auftragToCheck.getAuftragId());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void reCalculateVerbindungsBezeichnung(Long auftragId) throws StoreException {
        try {
            VerbindungsBezeichnung verbindungsBezeichnung = findVerbindungsBezeichnungByAuftragId(auftragId);
            if ((verbindungsBezeichnung != null) && !BooleanTools.nullToFalse(verbindungsBezeichnung.getOverwritten()) && (verbindungsBezeichnung.getUniqueCode() != null)) {
                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
                // Produkt-Kennzeichnung ermitteln
                Produkt produkt = produktService.findProdukt4Auftrag(auftragId);
                if (produkt == null) {
                    throw new StoreException("VerbindungsBezeichnung not created because product not found!");
                }

                if (auftragTechnik.getVpnId() != null) {
                    // VPN-Service nicht per Injection wg. zirkulaerer Referenz (PhysikService <--> VPNService)!
                    VPN vpn = getCCService(VPNService.class).findVPN(auftragTechnik.getVpnId());
                    if ((vpn != null) && StringUtils.isNotBlank(vpn.getVpnName())) {
                        verbindungsBezeichnung.setCustomerIdent(vpn.getVpnName().toUpperCase());
                    }
                    verbindungsBezeichnung.setKindOfUseProduct(VerbindungsBezeichnung.KindOfUseProduct.V);
                    if (produkt.getVbzKindOfUseTypeVpn() != null) {
                        verbindungsBezeichnung.setKindOfUseType(produkt.getVbzKindOfUseTypeVpn());
                    }
                }
                else {
                    verbindungsBezeichnung.setCustomerIdent(null);
                    if (produkt.getVbzKindOfUseProduct() != null) {
                        verbindungsBezeichnung.setKindOfUseProduct(produkt.getVbzKindOfUseProduct());
                    }
                    if (produkt.getVbzKindOfUseType() != null) {
                        verbindungsBezeichnung.setKindOfUseType(produkt.getVbzKindOfUseType());
                    }
                }
                saveVerbindungsBezeichnung(verbindungsBezeichnung);
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
    public VerbindungsBezeichnung saveVerbindungsBezeichnung(VerbindungsBezeichnung toSave) throws StoreException, ValidationException {
        try {
            //   - UniqueIdent gesetzt --> KindOfUse muessen gesetzt sein!!!
            if (toSave.getUniqueCode() != null) {
                ValidationException validationException = new ValidationException(toSave, "verbindungsBezeichnung");
                if (toSave.getCustomerIdent() != null) {
                    if (toSave.getCustomerIdent().length() != 6) {
                        validationException.rejectValue(VerbindungsBezeichnung.CUSTOMER_IDENT,
                                AbstractValidator.ERRCODE_INVALID,
                                "Nutzerbezeichnung - Falsche Länge (muss 6 Zeichen lang sein)");
                    }
                    if (toSave.getCustomerIdent().matches(".*[^A-Z0-9x].*")) {
                        validationException.rejectValue(VerbindungsBezeichnung.CUSTOMER_IDENT,
                                AbstractValidator.ERRCODE_INVALID,
                                "Nutzerbezeichnung - Darf nur Großbuchstaben und Zahlen enthalten)");
                    }
                }
                if (StringUtils.isBlank(toSave.getKindOfUseProduct())) {
                    validationException.rejectValue(VerbindungsBezeichnung.KIND_OF_USE_PRODUCT, AbstractValidator.ERRCODE_REQUIRED, "Nutzungsart - Produkt nicht definiert");
                }
                if (StringUtils.isBlank(toSave.getKindOfUseType())) {
                    validationException.rejectValue(VerbindungsBezeichnung.KIND_OF_USE_TYPE, AbstractValidator.ERRCODE_REQUIRED, "Nutzungsart - Typ nicht definiert");
                }

                if (validationException.hasErrors()) {
                    throw validationException;
                }
            }

            toSave.setVbz(toSave.createVbzValue());
            return getVbzDAO().store(toSave);
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String getVbzValue4TAL(Long auftragId, String endstelleTyp) throws FindException {
        try {
            VerbindungsBezeichnung verbindungsBezeichnung = findVerbindungsBezeichnungByAuftragId(auftragId);
            if (verbindungsBezeichnung != null) {
                StringBuilder builder = new StringBuilder();
                // do not use customer ident here!
                builder.append(VerbindungsBezeichnung.createVbzValue(verbindungsBezeichnung.getVbz(), null, verbindungsBezeichnung.getUniqueCode(),
                        verbindungsBezeichnung.getKindOfUseProduct(), verbindungsBezeichnung.getKindOfUseType()));
                builder.append(VerbindungsBezeichnung.TAL_ID_SEPARATOR);
                builder.append(endstelleTyp.toUpperCase());
                builder.append(VerbindungsBezeichnung.TAL_ID_SEPARATOR);
                builder.append(getPortId4TAL(auftragId, endstelleTyp));

                return builder.toString();
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Verbindungsbezeichnung fuer TAL-Bestellungen! " + e.getMessage(), e);
        }
    }

    /*
     * Ermittelt den Port (Stift) fuer eine TAL-Bestellung. <br>
     */
    String getPortId4TAL(Long auftragId, String endstelleTyp) {
        StringBuilder portId = new StringBuilder();
        try {
            Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, endstelleTyp);
            if ((endstelle != null) && (endstelle.getRangierId() != null)) {
                Rangierung rangierung = rangierungsService.findRangierung(endstelle.getRangierId());
                if ((rangierung != null) && (rangierung.getEqOutId() != null)) {
                    Equipment equipment = rangierungsService.findEquipment(rangierung.getEqOutId());
                    if ((equipment != null) && (equipment.getRangStift1() != null)) {
                        portId.append(equipment.getRangStift1());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return StringTools.fillToSize(portId.toString(), 4, '0', true);
    }


    @Override
    public VerbindungsBezeichnung findOrCreateVerbindungsBezeichnungForWbci(Long auftragId) throws FindException, StoreException, ValidationException {
        VerbindungsBezeichnung vbz = findVerbindungsBezeichnungByAuftragId(auftragId);
        if (vbz != null && StringUtils.isEmpty(vbz.getWbciLineId())) {
            final String lineIdValueWithLeadingZeros = String.format("%09d", vbzDAO.getNextWbciLineIdValue());
            vbz.setWbciLineId(String.format(LINE_ID_FORMAT, "W" + lineIdValueWithLeadingZeros));
            saveVerbindungsBezeichnung(vbz);
        }
        return vbz;
    }

    @Override
    public VerbindungsBezeichnung findVerbindungsBezeichnung(String vbz) throws FindException {
        try {
            VerbindungsBezeichnungDAO dao = getVbzDAO();
            VerbindungsBezeichnung example = new VerbindungsBezeichnung();
            example.setVbz(vbz);
            List<VerbindungsBezeichnung> result = dao.queryByExample(example, VerbindungsBezeichnung.class);
            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerbindungsBezeichnung> findVerbindungsBezeichnungLike(String vbz) throws FindException {
        if ((vbz == null) || StringUtils.isBlank(vbz)) { return null; }
        try {
            VerbindungsBezeichnungDAO dao = getVbzDAO();
            return dao.findVerbindungsBezeichnungLike(vbz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VerbindungsBezeichnung findVerbindungsBezeichnungById(Long vbzId) throws FindException {
        if (vbzId == null) { return null; }
        try {
            return getVbzDAO().findById(vbzId, VerbindungsBezeichnung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VerbindungsBezeichnung findVerbindungsBezeichnungByAuftragId(Long ccAuftragId) throws FindException {
        return findVerbindungsBezeichnungByAuftragIdTx(ccAuftragId);
    }

    @Override
    public VerbindungsBezeichnung findVerbindungsBezeichnungByAuftragIdTx(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) { return null; }
        try {
            return getVbzDAO().findByAuftragId(ccAuftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerbindungsBezeichnung> findVerbindungsBezeichnungenByKundeNoOrig(Long kundeNo) throws FindException {
        if (kundeNo == null) { return null; }
        try {
            return getVbzDAO().findVerbindungsBezeichnungenByKundeNo(kundeNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<VerbindungsBezeichnungHistoryView> findVerbindungsBezeichnungHistory(String vbz) throws FindException {
        if (StringUtils.isBlank(vbz)) { return null; }
        try {
            return getVbzDAO().findVerbindungsBezeichnungHistory(vbz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Leitungsart findLeitungsartByName(String name) throws FindException {
        try {
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            Leitungsart example = new Leitungsart();
            example.setName(name);
            List<Leitungsart> list = getLeitungsartDAO().queryByExample(example, Leitungsart.class);
            if (CollectionTools.isNotEmpty(list) && (list.size() == 1)) {
                return list.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Montageart> findMontagearten() throws FindException {
        try {
            return getMontageartDAO().findAll(Montageart.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Inhouse findInhouse4ES(Long esId) throws FindException {
        if (esId == null) { return null; }
        try {
            return getInhouseDAO().findByEsId(esId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Inhouse> findInhouses4ES(Long esId) throws FindException {
        if (esId == null) { return null; }
        try {
            Inhouse example = new Inhouse();
            example.setEndstelleId(esId);
            return getInhouseDAO().queryByExample(example, Inhouse.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Inhouse saveInhouse(Inhouse toSave, boolean makeHistory) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            InhouseDAO dao = getInhouseDAO();
            if (makeHistory && (toSave.getId() != null)) {
                Date now = new Date();
                return dao.update4History(toSave, toSave.getId(), now);
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                dao.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_SAVE_INHOUSE, e);
        }
    }

    @Override
    public Leitungsart findLeitungsart4ES(Long esId) throws FindException {
        if (esId == null) { return null; }
        try {
            return getLeitungsartDAO().findByEsId(esId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Schnittstelle findSchnittstelle4ES(Long esId) throws FindException {
        if (esId == null) { return null; }
        try {
            return getSchnittstelleDAO().findByEsId(esId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Anschlussart findAnschlussart(Long anschlussArtId) throws FindException {
        if (anschlussArtId == null) {
            return null;
        }

        try {
            FindDAO dao = (FindDAO) getAnschlussartDAO();
            return dao.findById(anschlussArtId, Anschlussart.class);
        }
        catch (ObjectRetrievalFailureException e) {
            LOGGER.info(e.getMessage(), e);
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Anschlussart> findAnschlussarten() throws FindException {
        try {
            FindAllDAO dao = (FindAllDAO) getAnschlussartDAO();
            return dao.findAll(Anschlussart.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveAnschlussart(Anschlussart toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            StoreDAO dao = (StoreDAO) getAnschlussartDAO();
            dao.store(toSave);
            toSave.notifyObservers(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public PhysikUebernahme findLastPhysikUebernahme(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            return getPhysikUebernahmeDAO().findLast4AuftragA(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public PhysikUebernahme findPhysikUebernahme4Verlauf(Long auftragId, Long verlaufId) throws FindException {
        if (verlaufId == null) { return null; }
        try {
            return getPhysikUebernahmeDAO().findByVerlaufId(auftragId, verlaufId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<PhysikUebernahme> findPhysikUebernahmen4Verlauf(Long verlaufId) throws FindException {
        if (verlaufId == null) { return null; }
        try {
            PhysikUebernahme example = new PhysikUebernahme();
            example.setVerlaufId(verlaufId);

            return getPhysikUebernahmeDAO().queryByExample(example, PhysikUebernahme.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public PhysikUebernahme findPhysikUebernahme(Long vorgang, Integer kriterium) throws FindException {
        try {
            PhysikUebernahme example = new PhysikUebernahme();
            example.setVorgang(vorgang);
            example.setKriterium(kriterium);

            List<PhysikUebernahme> result = getPhysikUebernahmeDAO().queryByExample(example, PhysikUebernahme.class);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() > 1) {
                    throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] { 1, result.size() });
                }
                return result.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<PhysikaenderungsTyp> findPhysikaenderungsTypen() throws FindException {
        try {
            return getPhysikTypDAO().findAll(PhysikaenderungsTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Object[]> findPhysiktypKombinationen() throws FindException {
        List<Object[]> physiktypCombinations = new ArrayList<>();
        List<PhysikTyp> physiktypen = findPhysikTypen();

        if (physiktypen != null) {
            for (PhysikTyp pt : physiktypen) {
                if (NumberTools.notEqual(pt.getId(), PhysikTyp.PHYSIKTYP_UNDEFINIERT)) {
                    List<Long> parentIds = new ArrayList<>();
                    parentIds.add(pt.getId());
                    List<PhysikTyp> childPTs = findPhysikTypen4ParentPhysik(parentIds);

                    if (!CollectionTools.isEmpty(childPTs)) {
                        for (PhysikTyp childPT : childPTs) {
                            Object[] combination = new Object[2];
                            combination[0] = pt.getId();
                            combination[1] = childPT.getId();

                            physiktypCombinations.add(combination);
                        }
                    }
                    else {
                        Object[] combination = new Object[2];
                        combination[0] = pt.getId();

                        physiktypCombinations.add(combination);
                    }
                }
            }
        }

        // PT-Kombinationen filtern, die als Parent eine Physik besitzen,
        // die auch als Child-Physik verwendet wird.
        List<Object[]> toRemove = new ArrayList<>();
        for (Object[] combination : physiktypCombinations) {
            for (Object[] comb2Check : physiktypCombinations) {
                if (combination[0].equals(comb2Check[1])) {
                    toRemove.add(combination);
                }
            }
        }

        physiktypCombinations = (List<Object[]>) CollectionUtils.subtract(physiktypCombinations, toRemove);

        return physiktypCombinations;
    }

    @Override
    public PhysikTyp findPhysikTypByName(String name) throws FindException {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        try {
            PhysikTyp example = new PhysikTyp();
            example.setName(name);
            List<PhysikTyp> pts = getPhysikTypDAO().queryByExample(example, PhysikTyp.class);
            return ((pts != null) && (pts.size() == 1)) ? pts.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /* ****************** DAO-Definitionen ******************** */

    /**
     * @return Returns the vbzDAO.
     */
    public VerbindungsBezeichnungDAO getVbzDAO() {
        return vbzDAO;
    }

    /**
     * @param verbindungsBezeichnungDAO The vbzDAO to set.
     */
    public void setVbzDAO(VerbindungsBezeichnungDAO verbindungsBezeichnungDAO) {
        this.vbzDAO = verbindungsBezeichnungDAO;
    }

    /**
     * @return Returns the montageartDAO.
     */
    public MontageartDAO getMontageartDAO() {
        return montageartDAO;
    }

    /**
     * @param montageartDAO The montageartDAO to set.
     */
    public void setMontageartDAO(MontageartDAO montageartDAO) {
        this.montageartDAO = montageartDAO;
    }

    /**
     * @return Returns the inhouseDAO.
     */
    public InhouseDAO getInhouseDAO() {
        return inhouseDAO;
    }

    /**
     * @param inhouseDAO The inhouseDAO to set.
     */
    public void setInhouseDAO(InhouseDAO inhouseDAO) {
        this.inhouseDAO = inhouseDAO;
    }

    /**
     * @return Returns the physikTypDAO.
     */
    public PhysikTypDAO getPhysikTypDAO() {
        return physikTypDAO;
    }

    /**
     * @param physikTypDAO The physikTypDAO to set.
     */
    public void setPhysikTypDAO(PhysikTypDAO physikTypDAO) {
        this.physikTypDAO = physikTypDAO;
    }

    /**
     * @return Returns the leitungsartDAO.
     */
    public LeitungsartDAO getLeitungsartDAO() {
        return leitungsartDAO;
    }

    /**
     * @param leitungsartDAO The leitungsartDAO to set.
     */
    public void setLeitungsartDAO(LeitungsartDAO leitungsartDAO) {
        this.leitungsartDAO = leitungsartDAO;
    }

    /**
     * @return Returns the schnittstelleDAO.
     */
    public SchnittstelleDAO getSchnittstelleDAO() {
        return schnittstelleDAO;
    }

    /**
     * @param schnittstelleDAO The schnittstelleDAO to set.
     */
    public void setSchnittstelleDAO(SchnittstelleDAO schnittstelleDAO) {
        this.schnittstelleDAO = schnittstelleDAO;
    }

    /**
     * @return Returns the anschlussartDAO.
     */
    public Object getAnschlussartDAO() {
        return anschlussartDAO;
    }

    /**
     * @param anschlussartDAO The anschlussartDAO to set.
     */
    public void setAnschlussartDAO(Object anschlussartDAO) {
        this.anschlussartDAO = anschlussartDAO;
    }

    /**
     * @return Returns the physikUebernahmeDAO.
     */
    public PhysikUebernahmeDAO getPhysikUebernahmeDAO() {
        return physikUebernahmeDAO;
    }

    /**
     * @param physikUebernahmeDAO The physikUebernahmeDAO to set.
     */
    public void setPhysikUebernahmeDAO(PhysikUebernahmeDAO physikUebernahmeDAO) {
        this.physikUebernahmeDAO = physikUebernahmeDAO;
    }

    /* ****************** Validators ******************** */

    /**
     * Injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    @Override
    public void deleteVerbindungsBezeichnung(VerbindungsBezeichnung vbz) {
        getVbzDAO().delete(vbz);
    }
}
