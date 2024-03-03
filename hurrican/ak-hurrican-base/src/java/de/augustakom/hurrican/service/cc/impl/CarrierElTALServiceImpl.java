/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.06.2007 10:36:46
 */
package de.augustakom.hurrican.service.cc.impl;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.tal.CBUsecase;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassungList;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.cc.view.CBVorgangView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.impl.command.tal.AbstractTALCommand;
import de.augustakom.hurrican.service.exmodules.tal.TALService;
import de.augustakom.hurrican.service.exmodules.tal.utils.TALServiceFinder;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Service-Implementierung von <code>CarrierElTALService</code>.
 *
 *
 */
@CcTxRequired
public class CarrierElTALServiceImpl extends DefaultCCService implements CarrierElTALService {

    private static final Logger LOGGER = Logger.getLogger(CarrierElTALServiceImpl.class);

    private CarrierService carrierService;
    private ReferenceService referenceService;
    private ChainService chainService;
    private PhysikService physikService;
    private HVTService hvtService;
    private EndstellenService endstellenService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public void executeCheckCommands4CBV(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long typ, Long usecaseId, CarrierKennung ck) throws FindException, ServiceCommandException,
            StoreException {
        Reference typRef = referenceService.findReference(typ);
        List<ServiceCommand> cmds = chainService.findServiceCommands4Reference(Long.valueOf(typRef.getIntValue()),
                ServiceChain.class, null);
        if (CollectionTools.isNotEmpty(cmds)) {
            AKServiceCommandChain chain = new AKServiceCommandChain();
            for (ServiceCommand cmd : cmds) {
                IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                if (serviceCmd == null) {
                    throw new FindException("Fuer das definierte ServiceCommand " + cmd.getName()
                            + " konnte kein Objekt geladen werden!");
                }

                serviceCmd.prepare(AbstractTALCommand.KEY_CARRIERBESTELLUNG_ID, cbId);
                serviceCmd.prepare(AbstractTALCommand.KEY_AUFTRAG_ID, auftragId);
                serviceCmd.prepare(AbstractTALCommand.KEY_SUB_ORDERS, subOrders4Klammer);
                serviceCmd.prepare(AbstractTALCommand.KEY_CBVORGANG_TYP, typ);
                serviceCmd.prepare(AbstractTALCommand.KEY_CBUSECASE_ID, usecaseId);
                serviceCmd.prepare(AbstractTALCommand.KEY_CARRIER_KENNUNG_ABS, ck);

                chain.addCommand(serviceCmd);
            }

            chain.executeChain(true);
        }
        else {
            throw new StoreException("Es konnten keine Checks fuer den angegebenen Vorgang ermittelt werden!");
        }
    }

    @Override
    public CBVorgang saveCBVorgang(CBVorgang toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            if ((toSave.getReturnOk() != null) && NumberTools.isLess(toSave.getStatus(), CBVorgang.STATUS_ANSWERED)) {
                // Status wird umgesetzt, wenn ein OK-Wert eingetragen ist
                toSave.setStatus(CBVorgang.STATUS_ANSWERED);
                toSave.setAnsweredAt(new Date());
            }

            if (NumberTools.equal(toSave.getStatus(), CBVorgang.STATUS_SUBMITTED) && (toSave.getSubmittedAt() == null)) {
                // Datum setzen, zu wann der Vorgang an den Carrier uebermittelt wurde
                toSave.setSubmittedAt(new Date());
            }

            ((StoreDAO) getDAO()).store(toSave);
            return toSave;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CBVorgang findCBVorgang(Long id) throws FindException {
        if (id == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return getDAO().findById(id, CBVorgang.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CBVorgang> findCBVorgaenge4CB(Long... cbIDs) throws FindException {
        if (cbIDs == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return getDAO().findCBVorgaenge4CB(cbIDs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public <T extends CBVorgang> List<T> findCBVorgaengeByExample(T example) throws FindException {
        if (example == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            @SuppressWarnings("unchecked")
            List<T> result = (List<T>) getDAO().queryByExample(example, example.getClass());
            // Set required due to same instance is returned X times if WitaCbVorgang contains X Anlagen
            return newArrayList(newHashSet(result));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CBVorgangNiederlassung> findOpenCBVorgaengeNiederlassungWithWiedervorlage() throws FindException {
        try {
            CBVorgangNiederlassungList result = new CBVorgangNiederlassungList(getDAO()
                    .findOpenCBVorgaengeNiederlassungWithWiedervorlage());

            result.sortOpenCbVorgaenge();

            // Achtung: Muss vorher sortiert sein
            result.setAuftragsKlammerSymbole();
            return result.getList();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CBVorgangView> findOpenCBVorgaenge4Intern() throws FindException {
        try {
            // CBVorgaenge mit Stati mit > 'Submitted' und < 'Answered' ermitteln
            return getDAO().findOpenCBVorgaenge(Carrier.mnetCarrierIds,
                    CBVorgang.STATUS_SUBMITTED, Boolean.TRUE,
                    CBVorgang.STATUS_ANSWERED, Boolean.FALSE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CBUsecase findCBUsecase(Long usecaseId) throws FindException {
        if (usecaseId == null) {
            return null;
        }
        try {
            return getDAO().findById(usecaseId, CBUsecase.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public CBVorgang findCBVorgangByCarrierRefNr(String carrierRefNr) {
        if (carrierRefNr == null) {
            return null;
        }
        return getDAO().findCBVorgangByCarrierRefNr(carrierRefNr);
    }

    @Override
    protected CBVorgangDAO getDAO() {
        return (CBVorgangDAO) super.getDAO();
    }

    /**
     * Injected
     */
    public void setCarrierService(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * Injected
     */
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    /**
     * Injected
     */
    public void setChainService(ChainService chainService) {
        this.chainService = chainService;
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }
}
