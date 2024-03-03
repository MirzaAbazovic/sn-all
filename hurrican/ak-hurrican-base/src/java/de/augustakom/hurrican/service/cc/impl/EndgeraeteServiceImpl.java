/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2004 15:16:19
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectRetrievalFailureException;

import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.dao.cc.EGConfigDAO;
import de.augustakom.hurrican.dao.cc.EGTypeDAO;
import de.augustakom.hurrican.dao.cc.EndgeraetDAO;
import de.augustakom.hurrican.dao.cc.ZugangDAO;
import de.augustakom.hurrican.dao.internet.EndgeraeteDao;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.EndgeraetIp.AddressType;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Montageart;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.Produkt2EG;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Routing;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Zugang;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.model.internet.IntEndgeraet;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.eg.AbstractEGCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Service-Implementierung von <code>EndgeraeteService</code>.
 */
@CcTxRequired
public class EndgeraeteServiceImpl extends DefaultCCService implements EndgeraeteService {

    private static final Logger LOGGER = Logger.getLogger(EndgeraeteServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;

    @Resource(name = "endgeraetDAO")
    private EndgeraetDAO endgeraetDAO;
    @Resource(name = "zugangDAO")
    private ZugangDAO zugangDAO;
    @Resource(name = "egConfigDAO")
    private EGConfigDAO egConfigDAO;
    @Resource(name = "egTypeDAO")
    private EGTypeDAO egTypeDAO;
    @Resource(name = "internet.endgeraeteDao")
    private EndgeraeteDao intEndgerateDao;
    @Resource(name = "auftragTechnikDAO")
    private AuftragTechnikDAO auftragTechnikDAO;

    @Resource(name = "egConfigValidator")
    private AbstractValidator egConfigValidator;
    @Resource(name = "portForwardingValidator")
    private AbstractValidator portForwardingValidator;
    @Resource(name = "endgeraetIpValidator")
    private AbstractValidator endgeraetIpValidator;
    @Resource(name = "routingValidator")
    private AbstractValidator routingValidator;

    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public EG findEgById(Long egId) throws FindException {
        if (egId == null) {
            return null;
        }
        try {
            return endgeraetDAO.findById(egId, EG.class);
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
    public List<Zugang> findZugaenge4Auftrag(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) {
            return Collections.emptyList();
        }
        try {
            Zugang example = new Zugang();
            example.setAuftragId(ccAuftragId);
            return zugangDAO.queryByExample(example, Zugang.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveZugang(Zugang toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            zugangDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public EGConfig findEGConfig(Long eg2AuftragId) throws FindException {
        if (eg2AuftragId == null) {
            return null;
        }
        return egConfigDAO.findEGConfig(eg2AuftragId);
    }

    @Override
    @CheckForNull
    public Schicht2Protokoll getSchicht2Protokoll4Auftrag(@Nonnull EG2Auftrag eg2Auftrag) throws FindException {
        return getSchicht2Protokoll4Auftrag(eg2Auftrag.getAuftragId(), eg2Auftrag.getEndstelleId());
    }

    @Override
    @CheckForNull
    public Schicht2Protokoll getSchicht2Protokoll4Auftrag(Long auftragId, Long endstelleId) throws FindException {
        if (auftragId == null) {
            return null;
        }
        List<Endstelle> possibleEndstellen =
                endstellenService.findEndstellen4AuftragWithoutExplicitFlush(auftragId);
        if (possibleEndstellen != null) {
            for (Endstelle endstelle : possibleEndstellen) {
                if ((endstelleId != null && endstelle.getId().equals(endstelleId))
                        || (endstelleId == null && Endstelle.ENDSTELLEN_TYP_B.equals(endstelle.getEndstelleTyp()))) {
                    Equipment eqIn = rangierungsService.findEquipment4Endstelle(endstelle, false, false);
                    if (eqIn != null) {
                        return eqIn.getSchicht2Protokoll();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void updateSchicht2Protokoll4Endstelle(Endstelle endstelle) throws StoreException {
        if (endstelle == null) {
            return;
        }
        try {
            AuftragTechnik auftragTechnik = auftragTechnikDAO.findAuftragTechnik4ESGruppe(endstelle
                    .getEndstelleGruppeId());
            List<EG2Auftrag> eg2Auftraege = findEGs4Auftrag(auftragTechnik.getAuftragId());
            if (eg2Auftraege != null && !eg2Auftraege.isEmpty()) {
                for (EG2Auftrag eg2Auftrag : eg2Auftraege) {
                    final Schicht2Protokoll schicht2Protokoll = getSchicht2Protokoll4Auftrag(eg2Auftrag);
                    for (EGConfig egConfig : eg2Auftrag.getEgConfigs()) {
                        egConfig.setSchicht2Protokoll(schicht2Protokoll);
                        egConfigDAO.store(egConfig);
                    }
                }
            }
        }
        catch (FindException e) {
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public void updateSchicht2Protokoll4Rangierungen(@Nonnull List<Rangierung> rangierungen) throws StoreException {
        if (rangierungen == null) {
            return;
        }
        try {
            for (Rangierung rangierung : rangierungen) {
                if (rangierung != null && rangierung.getEsId() != null) {
                    Endstelle endstelle = endstellenService.findEndstelle(rangierung.getEsId());
                    updateSchicht2Protokoll4Endstelle(endstelle);
                }
            }
        }
        catch (FindException e) {
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public List<EGConfig> findEgConfigs4Auftrag(Long auftragId) throws FindException {
        List<EGConfig> egConfigs = new ArrayList<>();
        List<EG2Auftrag> eg2AuftragListe = findEGs4Auftrag(auftragId);
        if (eg2AuftragListe != null) {
            for (EG2Auftrag eg2Auftrag : eg2AuftragListe) {
                try {
                    egConfigs.add(findEGConfig(eg2Auftrag.getId()));
                }
                catch (FindException e) {
                    LOGGER.warn(String.format("Keine gueltige Endgeraete Konfiguration für EG %s gefunden", eg2Auftrag.getId()));
                }
            }
        }

        return egConfigs;
    }

    @Override
    public EGConfig saveEGConfig(EGConfig toSave, Long sessionId) throws StoreException, ValidationException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        String loginName = getLoginNameSilent(sessionId);
        toSave.setBearbeiter(loginName);

        ValidationException valEx = new ValidationException(toSave, "EGConfig");
        egConfigValidator.validate(toSave, valEx);
        if (valEx.hasErrors()) {
            throw valEx;
        }

        for (PortForwarding portforwading : toSave.getPortForwardings()) {
            if (portforwading.getBearbeiter() == null) {
                portforwading.setBearbeiter(loginName);
            }

            // validate port forwardings
            ValidationException valExc = new ValidationException(portforwading, "PortForwarding");
            portForwardingValidator.validate(portforwading, valExc);
            if (valExc.hasErrors()) {
                throw valExc;
            }
        }

        try {
            egConfigDAO.store(toSave);
            return toSave;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EG> findValidEG(Long extLeistungNoOrig, Long prodId) throws FindException {
        try {
            return endgeraetDAO.findValidEG(extLeistungNoOrig, prodId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EG> findEGs4Produkt(Long prodId, boolean onlyDefault) throws FindException {
        if (prodId == null) {
            return Collections.emptyList();
        }
        try {
            return endgeraetDAO.findEGs4Produkt(prodId, onlyDefault);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EG> findDefaultEGs4Order(Long auftragId) throws FindException {
        if (auftragId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = as.findAuftragDatenByAuftragId(auftragId);

            return findEGs4Produkt(auftragDaten.getProdId(), true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void executeEGCheckCommand(EG eg, Long auftragId) throws ServiceCommandException {
        try {
            ChainService cs = getCCService(ChainService.class);
            List<ServiceCommand> cmds = cs.findServiceCommands4Reference(eg.getId(), EG.class, null);
            if (CollectionTools.isNotEmpty(cmds)) {
                AKServiceCommandChain chain = new AKServiceCommandChain();
                for (ServiceCommand cmd : cmds) {
                    IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                    if (serviceCmd == null) {
                        throw new FindException("Fuer das definierte ServiceCommand " + cmd.getName() +
                                " konnte kein Objekt geladen werden!");
                    }

                    serviceCmd.prepare(AbstractEGCommand.KEY_AUFTRAG_ID, auftragId);
                    serviceCmd.prepare(AbstractEGCommand.KEY_EG_TO_ADD, eg);

                    chain.addCommand(serviceCmd);
                }

                chain.executeChain(true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler bei der EG-Pruefung: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produkt2EG> findProdukt2EGs(Long prodId) throws FindException {
        if (prodId == null) {
            return Collections.emptyList();
        }
        try {
            Produkt2EG example = new Produkt2EG();
            example.setProdId(prodId);

            return endgeraetDAO.queryByExample(example, Produkt2EG.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void assignEGs2Produkt(Long prodId, List<Produkt2EG> toAssign) throws StoreException {
        if (prodId == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {

            if (CollectionTools.isNotEmpty(toAssign)) {
                for (Produkt2EG p2eg : toAssign) {
                    Produkt2EG newP2EG = new Produkt2EG();
                    PropertyUtils.copyProperties(newP2EG, p2eg);
                    newP2EG.setId(null);
                    endgeraetDAO.store(newP2EG);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteEGs2Produkt(Long prodId) throws DeleteException {
        if (prodId == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            endgeraetDAO.deleteProdukt2EG(prodId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EG> findEGs(Long typ) throws FindException {
        try {
            EG example = new EG();
            example.setEgTyp(typ);

            return endgeraetDAO.queryByExample(example, EG.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EG2Auftrag> findEGs4Auftrag(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) {
            return Collections.emptyList();
        }
        try {
            EG2Auftrag example = new EG2Auftrag();
            example.setAuftragId(ccAuftragId);

            return endgeraetDAO.queryByExample(example, EG2Auftrag.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean hasEGWithMontageMnet(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            List<EG2Auftrag> egs = findEGs4Auftrag(ccAuftragId);
            if (CollectionTools.isNotEmpty(egs)) {
                for (EG2Auftrag eg2a : egs) {
                    if (Montageart.isMontageAKomOrAllgaeukom(eg2a.getMontageart())) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EG2AuftragView> findEG2AuftragViews(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) {
            return Collections.emptyList();
        }
        try {
            return endgeraetDAO.findEG2AuftragViews(ccAuftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public EG2Auftrag findEG2AuftragById(Long id) throws FindException {
        try {
            return endgeraetDAO.findEG2Auftrag(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveEG2Auftrag(EG2Auftrag eg2a, Long sessionId) throws StoreException, ValidationException {
        if (eg2a == null) {
            return;
        }

        if (eg2a.getEgConfigs() != null) {
            for (EGConfig config : eg2a.getEgConfigs()) {
                saveEGConfig(config, sessionId);
            }
        }
        validateEndgeraetIpsOfEg2Auf(eg2a);
        validateRoutingsOfEg2Auf(eg2a);
        try {
            if (sessionId != null) {
                eg2a.setBearbeiter(getLoginNameSilent(sessionId));
            }

            if (eg2a.getDeactivated() == null) {
                eg2a.setDeactivated(Boolean.FALSE);
            }

            endgeraetDAO.store(eg2a);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void validateEndgeraetIpsOfEg2Auf(EG2Auftrag eg2a) throws ValidationException {
        for (EndgeraetIp egIp : eg2a.getEndgeraetIps()) {
            ValidationException valEx = new ValidationException(egIp, "EndgeraetIp");
            endgeraetIpValidator.validate(egIp, valEx);
            if (valEx.hasErrors()) {
                throw valEx;
            }
        }
    }

    private void validateRoutingsOfEg2Auf(EG2Auftrag eg2a) throws ValidationException {
        for (Routing routing : eg2a.getRoutings()) {
            ValidationException valEx = new ValidationException(routing, "Routing");
            routingValidator.validate(routing, valEx);
            if (valEx.hasErrors()) {
                throw valEx;
            }
        }
    }

    @Override
    public void saveEGs2Auftrag(List<EG2AuftragView> views, Long sessionId) throws StoreException {
        if (views != null) {
            try {
                for (EG2AuftragView view : views) {
                    saveEG2AuftragView(view, sessionId);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
            }
        }
    }

    @Override
    public EG2AuftragView saveEG2AuftragView(EG2AuftragView view, Long sessionId) throws StoreException {
        if (view == null) {
            return null;
        }
        try {
            if (view.getAuftragId() == null) {
                throw new StoreException("Auftrags-ID muss angegeben werden!");
            }

            EG2Auftrag toSave;
            if (view.getEg2AuftragId() != null) {
                toSave = findEG2AuftragById(view.getEg2AuftragId());
            }
            else {
                toSave = new EG2Auftrag();
            }

            toSave.setAuftragId(view.getAuftragId());
            toSave.setEgId(view.getEgId());
            toSave.setMontageart(view.getMontageartId());
            toSave.setEtage(view.getEtage());
            toSave.setRaum(view.getRaum());
            toSave.setDeactivated(view.getDeactivated());
            saveEG2Auftrag(toSave, sessionId);

            view.setEg2AuftragId(toSave.getId());
            return view;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteEG2Auftrag(Long eg2AuftragId, Long sessionId) throws DeleteException {
        if (eg2AuftragId == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }

        try {
            endgeraetDAO.deleteEG2Auftrag(eg2AuftragId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException.DELETE_FAILED, e);
        }
    }

    @Override
    public List<EndgeraetAcl> findAllEndgeraetAcls() throws FindException {
        try {
            return endgeraetDAO.findAllEndgeraetAcls();
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new FindException(e.getMessage());
        }
    }

    @Override
    public EndgeraetAcl findEndgeraetAclByName(String name) throws FindException {
        try {
            return endgeraetDAO.findEndgeraetAclByName(name);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new FindException(e.getMessage());
        }
    }

    @Override
    public void saveEndgeraetAcl(EndgeraetAcl endgeraetAcl) throws StoreException {
        try {
            endgeraetDAO.store(endgeraetAcl);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new StoreException(e.getMessage());
        }
    }

    @Override
    public void deleteEndgeraetAcl(EndgeraetAcl endgeraetAcl) {
        endgeraetDAO.deleteEndgeraetAcl(endgeraetAcl);
    }

    @Override
    public List<IntEndgeraet> findIntEndgeraeteNotInHurrican(Long auftragId) throws FindException {
        try {
            VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungByAuftragId(auftragId);
            if ((vbz == null) || StringUtils.isBlank(vbz.getVbz())) {
                throw new FindException(
                        "Konnte keine Endgeraete finden, da dieser Auftrag keine Verbindungsbezeichnung hat.");
            }

            List<String> serialNosAlreadyInHurrican = findSerialNosAlreadyInHurricanForAuftrag(auftragId);
            List<IntEndgeraet> intEndgeraete = intEndgerateDao.findEndgeraeteForVerbindungsbezeichnung(vbz.getVbz());

            // remove Endgeraete already in Hurrican
            List<IntEndgeraet> resultList = new ArrayList<>();
            for (IntEndgeraet intEg : intEndgeraete) {
                String serialNo = intEg.getSerialNo();
                if (StringUtils.isNotBlank(serialNo) && serialNosAlreadyInHurrican.contains(serialNo)) {
                    continue;
                }
                resultList.add(intEg);
            }

            return resultList;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new FindException(e.getMessage());
        }
    }

    private List<String> findSerialNosAlreadyInHurricanForAuftrag(Long auftragId) throws FindException {
        List<String> serialNosAlreadyInHurrican = new ArrayList<>();
        List<EG2Auftrag> eg2Auftraege = findEGs4Auftrag(auftragId);
        if (CollectionTools.isNotEmpty(eg2Auftraege)) {
            for (EG2Auftrag eg2a : eg2Auftraege) {
                EGConfig egConfig = findEGConfig(eg2a.getId());
                if ((egConfig != null) && StringUtils.isNotBlank(egConfig.getSerialNumber())) {
                    serialNosAlreadyInHurrican.add(egConfig.getSerialNumber());
                }
            }
        }
        return serialNosAlreadyInHurrican;
    }

    private IPAddress getIpAddress(String address, Long billingOrderNo) throws StoreException {
        if (StringUtils.isBlank(address)) {
            return null;
        }
        if (billingOrderNo == null) {
            throw new StoreException("Die Billing Auftragsnummer ist für die Management IP nicht gesetzt!");
        }
        IPAddress ipAddress = new IPAddress();
        ipAddress.setAddress(address);
        ipAddress.setIpType(AddressTypeEnum.IPV4);
        return ipAddress;
    }

    @Override
    public void importEg(Long auftragId, Long billingOrderNo, IntEndgeraet eg, Long sessionId)
            throws StoreException {
        try {
            EG2Auftrag eg2a = new EG2Auftrag();
            eg2a.setAuftragId(auftragId);
            eg2a.setEgId(EG.EG_ROUTER);

            if (StringUtils.isNotBlank(eg.getManagementIp())) {
                EndgeraetIp wanIp = new EndgeraetIp();
                wanIp.setAddressType(AddressType.WAN);
                wanIp.setIpAddressRef(getIpAddress(eg.getManagementIp(), billingOrderNo));
                eg2a.addEndgeraetIp(wanIp);
            }
            saveEG2Auftrag(eg2a, sessionId);

            EGConfig egConfig = new EGConfig();
            egConfig.setEg2AuftragId(eg2a.getId());
            egConfig.setSerialNumber(eg.getSerialNo());
            egConfig.setEgUser(eg.getLoginUser());
            egConfig.setEgPassword(eg.getLoginPass());
            egConfig.setWanIpFest(Boolean.FALSE);
            egConfig.setSchicht2Protokoll(getSchicht2Protokoll4Auftrag(eg2a));
            EGType egType = findEGTypeByHerstellerAndModell(eg.getVendor(), eg.getType());
            if (egType != null) {
                egConfig.setEgType(egType);
            }

            saveEGConfig(egConfig, sessionId);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new StoreException(e.getMessage());
        }
    }

    @Override
    public List<String> getDistinctListOfModelsByManufacturer(String manufacturer) throws FindException {
        return egTypeDAO.getDistinctListOfModelsByManufacturer(manufacturer);
    }

    @Override
    public List<String> getDistinctListOfEGManufacturer() throws FindException {
        return egTypeDAO.getDistinctListOfManufacturer();
    }

    @Override
    public List<String> getDistinctListOfEGModels() throws FindException {
        return egTypeDAO.getDistinctListOfModels();
    }

    @Override
    public void saveEGType(EGType toSave, Long sessionId) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            toSave.setUserW(getUserNameAndFirstNameSilent(sessionId));
            egTypeDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EGType> findAllEGTypes() {
        List<EGType> egTypes = endgeraetDAO.findAllEGTypes();
        if (egTypes == null) {
            return Collections.emptyList();
        }
        Collections.sort(egTypes, (o1, o2) -> {
            if ((o1 == null) && (o2 == null)) {
                return 0;
            }
            else if ((o1 == null) && (o2 != null)) {
                return -1;
            }
            else if ((o1 != null) && (o2 == null)) {
                return 1;
            }

            if (NumberTools.equal(o1.getId(), o2.getId())) {
                return 0;
            }
            else if (NumberTools.isLess(o1.getId(), o2.getId())) {
                return -1;
            }
            else {
                return 1;
            }
        });
        return egTypes;
    }

    @Override
    public List<EG> findPossibleEGs4EGType(Long egTypeId) throws FindException {
        try {
            return egTypeDAO.findPossibleEGs4EGType(egTypeId);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new FindException(e.getMessage());
        }
    }

    @Override
    public EGType findEGTypeByHerstellerAndModell(String hersteller, String modell) throws FindException {
        try {
            EGType egType = new EGType();
            egType.setHersteller(hersteller);
            egType.setModell(modell);
            List<EGType> egTypes = endgeraetDAO.queryByExample(egType, EGType.class);
            if (CollectionTools.isNotEmpty(egTypes)) {
                return egTypes.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<Integer, EndgeraetPort> findDefaultEndgeraetPorts4Count(Integer count) throws FindException {
        List<EndgeraetPort> endgeraetPorts = endgeraetDAO.findDefaultEndgeraetPorts4Count(count);
        Map<Integer, EndgeraetPort> mapEndgeraetPorts = new HashMap<>();
        if (CollectionTools.isNotEmpty(endgeraetPorts)) {
            for (EndgeraetPort endgeraetPort : endgeraetPorts) {
                mapEndgeraetPorts.put(endgeraetPort.getNumber(), endgeraetPort);
            }
        }
        return mapEndgeraetPorts;
    }

    @Override
    public Integer getMaxDefaultEndgeraetPorts() throws FindException {
        List<EndgeraetPort> result = endgeraetDAO.findDefaultEndgeraetPorts4Count(null);
        return (result != null) ? Integer.valueOf(result.size()) : Integer.valueOf(0);
    }

}
