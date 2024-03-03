/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 16:30:51
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.HWSubrackDAO;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDluView;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.model.cc.view.HWBaugruppeView;
import de.augustakom.hurrican.model.exceptions.ModelCalculationException;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationSearchCriteria;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;

/**
 * Implementierung von <code>HWService</code>
 *
 *
 */
@CcTxRequiredReadOnly
public class HWServiceImpl extends DefaultCCService implements HWService {
    private static final Logger LOGGER = Logger.getLogger(HWServiceImpl.class);

    private HardwareDAO hwDAO = null;
    private HWSubrackDAO hwSubrackDAO = null;
    private AbstractValidator hwRackValidator = null;
    private AbstractValidator hwSubrackValidator = null;
    private AbstractValidator hwBaugruppeValidator = null;

    @Override
    public List<HWRack> findRacks(Long hvtIdStandort) throws FindException {
        if (hvtIdStandort == null) {
            return null;
        }
        try {
            return getHwDAO().findRacks(hvtIdStandort);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    public boolean standortContainsGFastTech(Long hvtIDStandort) throws FindException {
        try {
            return getHwDAO().findRacks(hvtIDStandort).stream().anyMatch(HWRack::isDpuRack);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequired
    public void saveHWSubrack(HWSubrack hwSubrack) throws ValidationException, StoreException {
        ValidationException ve = new ValidationException(hwSubrack, "HW-Subrack");
        getHwSubrackValidator().validate(hwSubrack, ve);
        if (ve.hasErrors()) {
            throw ve;
        }
        try {
            getHwSubrackDAO().store(hwSubrack);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public <T extends HWRack> T saveHWRackNewTx(T toSave) throws StoreException, ValidationException {
        return saveHWRack(toSave);
    }

    @Override
    @CcTxRequired
    public <T extends HWRack> T saveHWRack(T toSave) throws StoreException, ValidationException {
        ValidationException ve = new ValidationException(toSave, "HW-Rack");
        getHwRackValidator().validate(toSave, ve);
        if (ve.hasErrors()) {
            throw ve;
        }
        try {
            return getHwDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public <T extends HWRack> List<T> findRacks(Long hvtIdStandort, Class<T> typ, Boolean onlyActive)
            throws FindException {
        if (hvtIdStandort == null) {
            throw new FindException("Standort ist nicht definiert!");
        }
        try {
            return getHwDAO().findRacks(hvtIdStandort, typ, onlyActive);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWRack> findAllRacksForFtth(final Long betriebsraumId) throws FindException {
        try {
            final List<HWOlt> olts = this.findRacks(betriebsraumId, HWOlt.class, false);
            final HWBaugruppenTyp hwBaugruppenTyp = this.findBaugruppenTypByName(HWBaugruppenTyp.TYP_NGLTA_NAME);
            @SuppressWarnings("unchecked")
            final List<HWDslam> gslams =
                    (List<HWDslam>) getHwDAO()
                            .findRacksWithBaugruppenTyp(betriebsraumId, HWDslam.class, hwBaugruppenTyp.getId(), false);
            final List<HWRack> all = Lists.newArrayListWithCapacity(olts.size() + gslams.size());
            all.addAll(olts);
            all.addAll(gslams);
            return all;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequired
    public HWBaugruppe saveHWBaugruppe(HWBaugruppe baugruppe) throws StoreException, ValidationException {
        ValidationException ve = new ValidationException(baugruppe, "HW-Baugruppe");
        getHwBaugruppeValidator().validate(baugruppe, ve);
        if (ve.hasErrors()) {
            throw ve;
        }
        try {
            return getHwDAO().store(baugruppe);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWBaugruppe> findBaugruppen(Long hvtIdStandort, Boolean onlyActive) throws FindException {
        if (hvtIdStandort == null) {
            return null;
        }
        try {
            List<HWRack> racks = findRacks(hvtIdStandort, null, onlyActive);

            List<HWBaugruppe> result = new ArrayList<>();
            for (HWRack rack : racks) {
                HWBaugruppe model = new HWBaugruppe();
                model.setRackId(rack.getId());
                result.addAll(getHwDAO().queryByExample(model, HWBaugruppe.class));
            }
            return (CollectionTools.isNotEmpty(result)) ? result : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWBaugruppe> findBaugruppen(HWBaugruppe example) throws FindException {
        try {
            return getHwDAO().queryByExample(example, HWBaugruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HWBaugruppe findBaugruppe(Long bgId) throws FindException {
        if (bgId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            return getHwDAO().findById(bgId, HWBaugruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWBaugruppeView> findHWBaugruppenViews(Long hvtIdStandort) throws FindException {
        if (hvtIdStandort == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            HWBaugruppeView example = new HWBaugruppeView();
            example.setHvtIdStandort(hvtIdStandort);

            return getHwDAO().queryByExample(example, HWBaugruppeView.class, new String[] { HWBaugruppeView.RACK_ID },
                    null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HWRack findRackForBaugruppe(Long hwBaugruppeId) throws FindException {
        if (hwBaugruppeId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            HWBaugruppe bg = findBaugruppe(hwBaugruppeId);
            if (bg == null) {
                throw new FindException(
                        "Baugruppe zur angegebenen ID konnte nicht ermittelt werden! ID: " + hwBaugruppeId);
            }
            return findRackById(bg.getRackId());
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
    public HWDslam findDslamByIP(String ipAddress) throws FindException {
        try {
            return getHwDAO().findHWDslamByIP(ipAddress);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public HWRack findRackByIdNewTx(Long rackId) throws FindException {
        return findRackById(rackId);
    }
    
    @Override
    public HWRack findRackById(Long rackId) throws FindException {
        if (rackId == null) {
            return null;
        }
        try {
            return getHwDAO().findById(rackId, HWRack.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HWSubrack findSubrackById(Long hwSubrackId) throws FindException {
        if (hwSubrackId == null) {
            return null;
        }
        try {
            return getHwSubrackDAO().findById(hwSubrackId, HWSubrack.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWSubrack> findSubracksForRack(Long hwRackId) throws FindException {
        if (hwRackId == null) {
            return new ArrayList<>();
        }
        try {
            return getHwSubrackDAO().findSubracksForRack(hwRackId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWSubrack> findSubracksForStandort(Long hwRackId) throws FindException {
        if (hwRackId == null) {
            return null;
        }
        try {
            return getHwSubrackDAO().findSubracksForStandort(hwRackId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWSubrackTyp> findAllSubrackTypes() throws FindException {
        try {
            return getHwSubrackDAO().findSubrackTypes(null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<HWSubrackTyp> findAllSubrackTypes(String rackType) throws FindException {
        try {
            return getHwSubrackDAO().findSubrackTypes(rackType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<HWBaugruppenTyp> findAllBaugruppenTypen() throws FindException {
        try {
            return getHwDAO().findAll(HWBaugruppenTyp.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<HWBaugruppenTyp> findBaugruppenTypen(String prefix, boolean onlyActive) throws FindException {
        if (StringUtils.isBlank(prefix)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            return getHwDAO().findBaugruppenTypen(prefix, onlyActive);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWDluView> findEWSDBaugruppen(Long hvtIdStandort, boolean onlyFree) throws FindException {
        if (hvtIdStandort == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return getHwDAO().findEWSDBaugruppen(hvtIdStandort, onlyFree);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HWRack findRackByBezeichnung(String bezeichnung) throws FindException {
        if (StringUtils.isBlank(bezeichnung)) {
            return null;
        }
        try {
            List<HWRack> racks = getHwDAO().findByProperty(HWRack.class, HWRack.GERAETE_BEZ, bezeichnung);
            if ((racks != null) && (racks.size() > 1)) {
                throw new FindException("Es existieren mehr als zwei HWRack-Datensätze mit derselben Bezeichnung.");
            }
            return ((racks != null) && (racks.size() == 1)) ? racks.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nullable
    public HWDslam findGslamByAltBez(@Nullable String altGslamBez) throws FindException {
        if (StringUtils.isBlank(altGslamBez)) {
            return null;
        }
        try {
            List<HWDslam> gslams = getHwDAO().findByProperty(HWDslam.class, HWDslam.ALT_GSLAM_BEZ, altGslamBez);
            if ((gslams != null) && (gslams.size() > 1)) {
                throw new FindException("Es existieren mehr als zwei GSLAM-Datensätze mit derselben Bezeichnung.");
            }
            return ((gslams != null) && (gslams.size() == 1)) ? gslams.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HWRack findActiveRackByBezeichnung(String bezeichnung) throws FindException {

        try {
            return getHwDAO().findActiveRackByBezeichnung(bezeichnung);
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWBaugruppe> findBaugruppen4Rack(Long rackId) throws FindException {
        if (rackId == null) {
            return null;
        }
        try {
            HWBaugruppe ex = new HWBaugruppe();
            ex.setRackId(rackId);

            return getHwDAO().queryByExample(ex, HWBaugruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HWBaugruppe findBaugruppe4RackWithModName(String modName) throws FindException {
        if(modName == null){
            return null;
        }
        try {
            HWBaugruppe ex = new HWBaugruppe();
            ex.setModName(modName);

            List<HWBaugruppe> result= getHwDAO().queryByExample(ex, HWBaugruppe.class);
            if (result.size() > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE);
            }

            return Iterables.getFirst(result, null);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HWBaugruppenTyp findBaugruppenTypByName(String hwBaugruppenTypName) throws FindException {
        if (hwBaugruppenTypName == null) {
            return null;
        }
        try {
            HWBaugruppenTyp ex = new HWBaugruppenTyp();
            ex.setName(hwBaugruppenTypName.toUpperCase());
            List<HWBaugruppenTyp> baugruppenTypen = getHwDAO().queryByExample(ex, HWBaugruppenTyp.class);
            return (CollectionTools.isNotEmpty(baugruppenTypen)) ? baugruppenTypen.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public <T extends HWRack> List<T> findRacksByType(Class<T> type) throws FindException {
        try {
            return getHwDAO().findAll(type);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HWRack> findRacksByManagementBez(String managementBez) throws FindException {
        try {
            return getHwDAO().findByProperty(HWRack.class, HWRack.MANAGEMENT_BEZ, managementBez.toUpperCase());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequired
    public void freigabeMDU(Long rackId, Date date) throws StoreException {
        if ((rackId == null) || (date == null)) {
            return;
        }
        try {
            // Setze Freigabedatum
            HWMdu mdu = (HWMdu) findRackById(rackId);
            if (mdu == null) {
                throw new StoreException("HW-Rack (MDU) mit der Id " + rackId.toString() + " wurde nicht gefunden.");
            }
            mdu.setFreigabe(date);
            saveHWRack(mdu);

            // Freigabe Ports
            RangierungFreigabeService rs = getCCService(RangierungFreigabeService.class);
            rs.freigabeMduDpuRangierungen(mdu.getId(), date);
        }
        catch (Exception e) {
            throw new StoreException("Fehler freigabe MDU", e);
        }
    }

    @Override
    @CcTxRequired
    public void freigabeDPU(Long rackId, Date date) throws StoreException {
        if ((rackId == null) || (date == null)) {
            return;
        }
        try {
            // Setze Freigabedatum
            HWDpu dpu = (HWDpu) findRackById(rackId);
            if (dpu == null) {
                throw new StoreException(String.format("HW-Rack (DPU) mit der Id %d wurde nicht gefunden.", rackId));
            }
            dpu.setFreigabe(date);
            saveHWRack(dpu);

            // Freigabe Ports
            RangierungFreigabeService rs = getCCService(RangierungFreigabeService.class);
            rs.freigabeMduDpuRangierungen(dpu.getId(), date);
        }
        catch (Exception e) {
            throw new StoreException("Fehler freigabe DPU", e);
        }
    }

    @Override
    @CcTxRequired
    public List<SwitchMigrationView> createSwitchMigrationViews(SwitchMigrationSearchCriteria searchCriteria) {
        return hwDAO.createSwitchMigrationViews(searchCriteria);
    }

    @Override
    @CcTxRequired
    public HWBaugruppenTyp saveHWBaugruppenTyp(HWBaugruppenTyp hwBaugruppenTyp) throws StoreException {
        try {
            return getHwDAO().store(hwBaugruppenTyp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CheckForNull
    public HWSubrack findSubrackByHwRackAndModNumber(Long hwRackId, String modNumber) throws FindException {
        try {
            List<HWSubrack> subracks = getHwSubrackDAO().findHwSubracksByRackIdAndModNumber(hwRackId, modNumber);

            if (subracks.size() > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE);
            }

            return Iterables.getFirst(subracks, null);
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
    @CheckForNull
    public HWBaugruppe findBaugruppe(Long rackId, Long subrackId, String modNumber) throws FindException {
        try {
            HWBaugruppe example = new HWBaugruppe();
            example.setRackId(rackId);
            example.setSubrackId(subrackId);
            example.setModNumber(modNumber);
            List<HWBaugruppe> hwBaugruppen = getHwDAO().queryByExample(example, HWBaugruppe.class);
            if (hwBaugruppen.size() > 1) {
                throw new FindException(FindException.INVALID_RESULT_SIZE);
            }
            return Iterables.getFirst(hwBaugruppen, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    @Nonnull
    public <T extends HWOltChild> List<T> findHWOltChildByOlt(Long oltId, Class<T> clazz) throws FindException {
        try {
            return getHwDAO().findHWOltChildByOlt(oltId, clazz);
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public <T extends HWOltChild> List<T> findHWOltChildBySerialNo(String serialNo, Class<T> clazz) throws FindException {
        try {
            return getHwDAO().findHWOltChildBySerialNo(serialNo, clazz);
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String generateOltChildIp(HWOlt olt, @Nonnull HWOltChild oltChild) throws ModelCalculationException {
        if (olt == null) {
            return null;
        }

        // IP-Adresse aufsplitten
        String ipVon = olt.getIpNetzVon();
        String[] ipBereich = StringUtils.split(ipVon, ".");
        if ((ipBereich != null) && (ipBereich.length == 4)) {
            Integer ip3 = Integer.valueOf(ipBereich[2]);
            int oltSlot = Integer.parseInt(oltChild.getOltSlot());
            if ((oltSlot == 9) || (oltSlot == 10)) {
                throw new ModelCalculationException("Falscher OLT-Slot");
            }
            else if (oltSlot <= 8) {
                ip3 = (ip3 + oltSlot) - 1;
            }
            else {
                ip3 = (ip3 + oltSlot) - 3;
            }

            int ip = Integer.parseInt(ipBereich[3]);
            int oltPort = Integer.parseInt(oltChild.getOltGPONPort());
            int oltGpon = Integer.parseInt(oltChild.getOltGPONId());
            ip = ip + (32 * oltPort) + oltGpon;

            return StringTools.join(new String[] {
                            ipBereich[0], ipBereich[1], String.format("%s", ip3), String.format("%s", ip) },
                    ".", false
            );
        }
        return null;
    }

    private HardwareDAO getHwDAO() {
        return hwDAO;
    }

    /**
     * Injected
     */
    public void setHwDAO(HardwareDAO hwDAO) {
        this.hwDAO = hwDAO;
    }

    private HWSubrackDAO getHwSubrackDAO() {
        return hwSubrackDAO;
    }

    /**
     * Injected
     */
    public void setHwSubrackDAO(HWSubrackDAO hwSubrackDAO) {
        this.hwSubrackDAO = hwSubrackDAO;
    }

    private AbstractValidator getHwRackValidator() {
        return hwRackValidator;
    }

    /**
     * Injected
     */
    public void setHwRackValidator(AbstractValidator hwRackValidator) {
        this.hwRackValidator = hwRackValidator;
    }

    private AbstractValidator getHwSubrackValidator() {
        return hwSubrackValidator;
    }

    /**
     * Injected
     */
    public void setHwSubrackValidator(AbstractValidator hwSubrackValidator) {
        this.hwSubrackValidator = hwSubrackValidator;
    }

    private AbstractValidator getHwBaugruppeValidator() {
        return hwBaugruppeValidator;
    }

    /**
     * Injected
     */
    public void setHwBaugruppeValidator(AbstractValidator hwBaugruppeValidator) {
        this.hwBaugruppeValidator = hwBaugruppeValidator;
    }
}
