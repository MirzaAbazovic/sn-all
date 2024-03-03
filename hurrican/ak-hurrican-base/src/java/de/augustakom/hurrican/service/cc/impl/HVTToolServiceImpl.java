/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 16:39:09
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.HVTBestellungDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTBestellHistory;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.model.cc.view.HVTBestellungView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTToolService;
import de.augustakom.hurrican.service.cc.impl.command.FillUevtCommand;
import de.augustakom.hurrican.service.cc.impl.command.command.CommandActivateMDUCommand;
import de.augustakom.hurrican.service.cc.impl.command.command.CommandSendLoginCommand;
import de.augustakom.hurrican.service.cc.impl.command.command.CommandSendLogoutCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Implementierung von <code>HVTToolService</code>.
 *
 *
 */
@CcTxRequired
public class HVTToolServiceImpl extends DefaultCCService implements HVTToolService {

    private static final Logger LOGGER = Logger.getLogger(HVTToolService.class);

    private EquipmentDAO equipmentDAO = null;
    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public void saveHVTBestellung(HVTBestellung toSave) throws StoreException, ValidationException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        ValidationException ve = new ValidationException(toSave, "HVTBestellung");
        getValidator().validate(toSave, ve);
        if (ve.hasErrors()) {
            throw ve;
        }

        try {
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HVTBestellung findHVTBestellung(Long id) throws FindException {
        if (id == null) {
            return null;
        }
        try {
            return ((FindDAO) getDAO()).findById(id, HVTBestellung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HVTBestellung> findHVTBestellungen(Long uevtId) throws FindException {
        try {
            HVTBestellung example = new HVTBestellung();
            example.setUevtId(uevtId);
            return ((ByExampleDAO) getDAO()).queryByExample(
                    example, HVTBestellung.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HVTBestellungView> findHVTBestellungViews() throws FindException {
        try {
            return ((HVTBestellungDAO) getDAO()).findHVTBestellungViews();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HVTBestellHistory> findHVTBestellHistories(Long hvtBestellId) throws FindException {
        if (hvtBestellId == null) {
            return null;
        }
        try {
            HVTBestellHistory example = new HVTBestellHistory();
            example.setBestellId(hvtBestellId);

            return ((ByExampleDAO) getDAO()).queryByExample(
                    example, HVTBestellHistory.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EquipmentBelegungView> findEquipmentBelegung(Long uevtId) throws FindException {
        if (uevtId == null) {
            return Collections.emptyList();
        }
        try {
            HVTBestellungDAO hvtDAO = (HVTBestellungDAO) getDAO();
            List<EquipmentBelegungView> eqViews = hvtDAO.findEQs4Uevt(uevtId, null);
            for (EquipmentBelegungView eqView : eqViews) {
                hvtDAO.loadStifte(eqView);

                Integer rangiert = hvtDAO.getCountStifteRangiert(eqView.getHvtIdStandort(),
                        eqView.getUevt(), eqView.getLeiste1());
                eqView.setStifteRangiert(rangiert);

                eqView.debugModel(LOGGER);
            }

            return eqViews;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Equipment> fillUevt(Long hvtBestellungId, String leiste1, String kvzNummer, Integer uevtClusterNo,
            IServiceCallback serviceCallback, boolean createLeiste, int createStifte, Long sessionId) throws StoreException {
        if ((hvtBestellungId == null) || StringUtils.isBlank(leiste1)) {
            throw new StoreException("Ungueltige Daten fuer die Stiftvergabe definiert. Stifte wurden nicht zugeteilt!");
        }
        if ((leiste1.length() != 2)) {
            throw new StoreException(
                    String.format(
                            "Leistenbezeichnung '%s' ist nicht korrekt. Die Bezeichnung muss zweistellig (z.B. '09') erfolgen.",
                            leiste1)
            );
        }
        if (serviceCallback == null) {
            throw new StoreException(
                    "Es wurde kein ServiceCallback-Handler definiert. Stifte können nicht eingespielt werden!");
        }
        if (uevtClusterNo == null) {
            throw new StoreException(
                    "Es wurde keine benötigte ÜVt-Cluster-Nr gesetzt. Stifte können nicht eingespielt werden!");
        }

        try {
            AKUser user = getAKUserBySessionIdSilent(sessionId);

            IServiceCommand cmd = serviceLocator.getCmdBean(FillUevtCommand.class);
            cmd.prepare(FillUevtCommand.HVT_BESTELLUNG_ID, hvtBestellungId);
            cmd.prepare(FillUevtCommand.UEVT_LEISTE1, leiste1);
            cmd.prepare(FillUevtCommand.KVZ_NUMMER, kvzNummer);
            cmd.prepare(FillUevtCommand.UEVT_CLUSTER_NO, uevtClusterNo);
            cmd.prepare(FillUevtCommand.USER_NAME, (user != null) ? user.getName() : "unbekannt");
            cmd.prepare(FillUevtCommand.SERVICE_CALLBACK, serviceCallback);
            cmd.prepare(FillUevtCommand.CREATE_LEISTE, createLeiste);
            cmd.prepare(FillUevtCommand.CREATE_STIFTE, createStifte);
            Object result = cmd.execute();

            return (result instanceof List) ? (List) result : null;
        }
        catch (ServiceCommandException e) {
            throw new StoreException(e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Bei der Stiftvergabe ist ein unerwarteter Fehler aufgetreten:\n" + e.getMessage(), e);
        }
    }

    @Override
    public List<Equipment> findEquipments(Long hvtIdStd, String uevt, String leiste1) throws FindException {
        if ((hvtIdStd == null) || StringUtils.isBlank(uevt) || StringUtils.isBlank(leiste1)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            return equipmentDAO.findEquipments(hvtIdStd, uevt, leiste1);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<RangSchnittstelle> findAvailableSchnittstellen4HVT(Long hvtStandortId) throws FindException {
        if (hvtStandortId == null) {
            return Collections.emptyList();
        }
        try {
            return equipmentDAO.findAvailableSchnittstellen4HVT(hvtStandortId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.HVTToolService#activateMDU(java.lang.String, java.util.Date)
     */
    @Override
    public void activateMDU(String mdu, Date datum) throws Exception {
        if (StringUtils.isEmpty(mdu)) {
            throw new IllegalArgumentException("mdu not set");
        }

        if (datum == null) {
            throw new IllegalArgumentException("datum not set");
        }

        String commandSessionId = loginCommand();

        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CommandActivateMDUCommand.class.getName());
            cmd.prepare(CommandActivateMDUCommand.KEY_SESSION_ID, commandSessionId);
            cmd.prepare(CommandActivateMDUCommand.KEY_MDU, mdu);
            cmd.prepare(CommandActivateMDUCommand.KEY_DATUM, datum);
            cmd.execute();
        }
        finally {
            logoutCommand(commandSessionId);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.HVTToolService#loginCommand()
     */
    @Override
    public String loginCommand() throws Exception {
        IServiceCommand cmd = serviceLocator.getCmdBean(CommandSendLoginCommand.class.getName());
        Object sessionId = cmd.execute();
        return sessionId.toString();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.HVTToolService#logoutCommand(java.lang.String)
     */
    @Override
    public void logoutCommand(String sessionId) throws Exception {
        IServiceCommand cmd = serviceLocator.getCmdBean(CommandSendLogoutCommand.class.getName());
        cmd.prepare(CommandSendLogoutCommand.KEY_SESSION_ID, sessionId);
        cmd.execute();
    }

    /**
     * @param equipmentDAO The equipmentDAO to set.
     */
    public void setEquipmentDAO(EquipmentDAO equipmentDAO) {
        this.equipmentDAO = equipmentDAO;
    }

}
