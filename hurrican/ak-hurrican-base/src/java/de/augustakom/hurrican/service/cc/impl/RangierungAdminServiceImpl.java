/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 15:40:12
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.beans.EnumConvertUtilsBean;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKMessages;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.RangierungsAuftragDAO;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.ProduktEQConfig;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.query.RangierungsAuftragBudgetQuery;
import de.augustakom.hurrican.model.cc.view.RangierungsAuftragBudgetView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.CreateRangierungenCommand;
import de.augustakom.hurrican.service.cc.impl.reportdata.RangierungslisteJasperDS;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Service-Implementierung von <code>RangierungAdminService</code>.
 *
 *
 */
@CcTxRequired
public class RangierungAdminServiceImpl extends DefaultCCService implements RangierungAdminService {

    private static final Logger LOGGER = Logger.getLogger(RangierungAdminServiceImpl.class);

    private EquipmentDAO equipmentDAO = null;
    private RangierungsService rangierungsService;
    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public List<RangierungsAuftrag> findUnfinishedRAs() throws FindException {
        try {
            return ((RangierungsAuftragDAO) getDAO()).findUnfinishedRAs();
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public RangierungsAuftrag findRA(final Long raId) throws FindException {
        if (raId == null) { return null; }
        try {
            return ((FindDAO) getDAO()).findById(raId, RangierungsAuftrag.class);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveRangierungsAuftrag(final RangierungsAuftrag toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        final Integer size = toSave.getAnzahlPorts();
        if (size == null) {
            throw new StoreException("Die Anzahl Ports muss angegeben werden!");
        }

        try {
            ((RangierungsAuftragDAO) getDAO()).store(toSave);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void cancelRA(final Long raId, final Long sessionId) throws StoreException {
        if (raId == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            final RangierungsAuftrag ra = findRA(raId);
            if (ra == null) {
                throw new StoreException("Der Rangierungs-Auftrag konnte nicht ermittelt werden.");
            }

            if (BooleanTools.nullToFalse(ra.getCancelled())) {
                throw new StoreException("Der Rangierungs-Auftrag ist bereits storniert.");
            }

            // pruefen, ob zu dem RA schon Rangierungen definiert wurden
            final Rangierung example = new Rangierung();
            example.setRangierungsAuftragId(ra.getId());

            final List<Rangierung> rangierungen = ((ByExampleDAO) getDAO()).queryByExample(example, Rangierung.class);
            if (CollectionTools.isNotEmpty(rangierungen)) {
                throw new StoreException(
                        "Auftrag kann nicht mehr storniert werden, da bereits Rangierungen definiert wurden!");
            }

            ra.setCancelled(Boolean.TRUE);
            ra.setCancelledFrom(getLoginNameSilent(sessionId));
            ((StoreDAO) getDAO()).store(ra);
        }
        catch (final StoreException e) {
            throw e;
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public RangierungsAuftrag releaseRangierungen4RA(final Long raId, final Float technikStd, final Long sessionId)
            throws FindException, StoreException {
        if (raId == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            final RangierungsAuftrag ra = findRA(raId);
            if (ra == null) {
                throw new StoreException(
                        "Freigabe fehlgeschlagen: Rangierungsauftrag konnte nicht ermittelt werden!");
            }

            // Status vom RA muss geprueft werden
            if (ra.getAusgefuehrtAm() != null) {
                throw new StoreException("Der Rangierungsauftrag ist bereits freigeben!");
            }

            // Anzahl definierter Rangierungen pruefen
            final List<Rangierung> rangierungen = findRangierungen4RA(raId);

            final int expectedCount = (ra.getPhysiktypChild() == null) ? ra.getAnzahlPorts() : ra.getAnzahlPorts() * 2;
            if (CollectionTools.isEmpty(rangierungen) || (expectedCount != rangierungen.size())) {
                throw new StoreException(
                        "Freigabe fehlgeschlagen: die Anzahl der Rangierungen passt nicht mit der Vorgabe ueberein.");
            }

            // Rangierungen auf 'freigegeben' setzen; zugehoerige Equipments auf Status 'rang' setzen;
            final RangierungsService rs = getCCService(RangierungsService.class);
            for (final Rangierung rangierung : rangierungen) {
                changeEquipmentStatus(rs, rangierung, EqStatus.rang);

                rangierung.setFreigegeben(Freigegeben.freigegeben);
                rs.saveRangierung(rangierung, false);
            }

            // Status vom RA setzen
            final AKUser user = getAKUserBySessionIdSilent(sessionId);
            ra.setTechnikStunden(technikStd);
            ra.setAusgefuehrtAm(new Date());
            ra.setAusgefuehrtVon((user != null) ? user.getNameAndFirstName() : HurricanConstants.UNKNOWN);
            saveRangierungsAuftrag(ra);

            return ra;
        }
        catch (final StoreException e) {
            throw e;
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Ermittelt die Equipments zu der Rangierung und setzt den jeweiligen
     * Status auf 'status'.
     */
    private void changeEquipmentStatus(final RangierungsService rs, final Rangierung rangierung, final EqStatus status) throws StoreException {
        try {
            final List<Long> eqIds = new ArrayList<Long>();
            CollectionTools.addIfNotNull(eqIds, rangierung.getEqInId());
            CollectionTools.addIfNotNull(eqIds, rangierung.getEqOutId());

            for (final Long eqId : eqIds) {
                final Equipment eq = rs.findEquipment(eqId);
                eq.setStatus(status);
                rs.saveEquipment(eq);
            }
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler waehrend der Status-Aenderung der Equipments: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean[] validateNeededEquipments(final RangierungsAuftrag ra) {
        if (ra == null) { throw new IllegalArgumentException("Rangierungsauftrag ist nicht angegeben."); }
        boolean useCarrier = false;
        boolean useDSLAM = false;
        boolean useEWSD = false;
        boolean useSDHPDh = false;

        final Long ptParentId = ra.getPhysiktypParent();
        final Long ptChildId = ra.getPhysiktypChild();

        if ((ptParentId != null) && (ptChildId != null) && NumberTools.notEqual(ptParentId, PhysikTyp.PHYSIKTYP_CONNECT)) {
            useCarrier = true;
            useDSLAM = true;
            useEWSD = true;
        }
        else if (ra.getPhysiktypParent() != null) {
            // ermitteln, welche Listen zu sperren sind
            if (NumberTools.isNotIn(ptParentId, new Number[] {
                    PhysikTyp.PHYSIKTYP_2H,
                    PhysikTyp.PHYSIKTYP_4H })) {
                useEWSD = true;
                useCarrier = true;
            }
            else {
                useCarrier = true;
            }

            if (NumberTools.isIn(ptParentId, new Number[] {
                    PhysikTyp.PHYSIKTYP_ADSL2P_ONLY_MS_HUAWEI,
                    PhysikTyp.PHYSIKTYP_ADSL2P_ONLY_HUAWEI,
                    PhysikTyp.PHYSIKTYP_ADSL2P_ONLY_ALCATEL,
                    PhysikTyp.PHYSIKTYP_SDSL_ATM_ALCATEL,
                    PhysikTyp.PHYSIKTYP_SDSL_IP_ALCATEL,
                    PhysikTyp.PHYSIKTYP_SHDSL_IP_ALCATEL,
                    PhysikTyp.PHYSIKTYP_SDSL_DA,
                    PhysikTyp.PHYSIKTYP_SDSL_DA_HUAWEI,
                    PhysikTyp.PHYSIKTYP_SHDSL_HUAWEI,
                    PhysikTyp.PHYSIKTYP_FTTC_VDSL })) {
                useDSLAM = true;
                useCarrier = true;
                useEWSD = false;
            }

            if (NumberTools.isIn(ptParentId, new Number[] {
                    PhysikTyp.PHYSIKTYP_FTTB_DPO_VDSL,
                    PhysikTyp.PHYSIKTYP_FTTB_VDSL,
                    PhysikTyp.PHYSIKTYP_FTTB_POTS,
                    PhysikTyp.PHYSIKTYP_FTTH })) {
                useDSLAM = true;
                useCarrier = false;
                useEWSD = false;
            }

            if (NumberTools.isIn(ptParentId, new Number[] {
                    PhysikTyp.PHYSIKTYP_CONNECT })) {
                useDSLAM = false;
                useEWSD = false;
                useCarrier = true;
                useSDHPDh = true;
            }
        }

        return new boolean[] { useEWSD, useDSLAM, useCarrier, useSDHPDh };
    }

    @Override
    public AKMessages createRangierungen(final Long raId, final List<Long> eqIdsEWSD, final List<Long> eqIdsCarrier,
            final List<Long> eqIdsDSLAMIn, final List<Long> eqIdsDSLAMOut, final String rangSSType, final Uebertragungsverfahren uetv,
            final String iaNumber, final Long sessionId)
            throws StoreException {
        try {
            final IServiceCommand cmd = serviceLocator.getCmdBean(CreateRangierungenCommand.class);
            cmd.prepare(CreateRangierungenCommand.KEY_SESSION_ID, sessionId);
            cmd.prepare(CreateRangierungenCommand.KEY_RANGIERUNGSAUFTRAG_ID, raId);
            cmd.prepare(CreateRangierungenCommand.KEY_EQ_IDS_CARRIER, eqIdsCarrier);
            cmd.prepare(CreateRangierungenCommand.KEY_EQ_IDS_DSLAM_IN, eqIdsDSLAMIn);
            cmd.prepare(CreateRangierungenCommand.KEY_EQ_IDS_DSLAM_OUT, eqIdsDSLAMOut);
            cmd.prepare(CreateRangierungenCommand.KEY_EQ_IDS_EWSD, eqIdsEWSD);
            cmd.prepare(CreateRangierungenCommand.KEY_RANG_SS_TYPE, rangSSType);
            cmd.prepare(CreateRangierungenCommand.KEY_UETV, uetv);
            cmd.prepare(CreateRangierungenCommand.KEY_IA_NUMBER, iaNumber);

            final Object result = cmd.execute();
            return (result instanceof AKMessages) ? (AKMessages) result : null;
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Aufbau der Rangierungen ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Rangierung> findRangierungen4RA(final Long raId) throws FindException {
        if (raId == null) { return null; }
        try {
            final Rangierung example = new Rangierung();
            example.setRangierungsAuftragId(raId);
            example.setGueltigBis(DateTools.getHurricanEndDate());
            return ((ByExampleDAO) getDAO()).queryByExample(example, Rangierung.class);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint printRangierungsliste(final Long raId) throws AKReportException {
        if (raId == null) { return null; }
        try {
            final AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/rangierung/Rangierungsliste.jasper",
                    null, new RangierungslisteJasperDS(raId));
            final AKJasperReportHelper jrh = new AKJasperReportHelper();
            final JasperPrint jp = jrh.createReport(ctx);
            return jp;
        }
        catch (final AKReportException e) {
            throw e;
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Rangierungsliste konnte nicht erstellt werden: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RangierungsAuftragBudgetView> findRABudgetViews(final RangierungsAuftragBudgetQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            return ((RangierungsAuftragDAO) getDAO()).findRABudgetViews(query);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveProduktEQConfig(final ProduktEQConfig toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            getEquipmentDAO().store(toSave);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteProduktEQConfig(final ProduktEQConfig toDelete) throws DeleteException {
        if (toDelete == null) { throw new DeleteException(DeleteException.INVALID_PARAMETERS); }
        try {
            getEquipmentDAO().deleteProduktEQConfig(toDelete.getId());
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<ProduktEQConfig> findProduktEQConfigs(final Long prodId) throws FindException {
        try {
            final ProduktEQConfig example = new ProduktEQConfig();
            example.setProdId(prodId);

            return ((ByExampleDAO) getEquipmentDAO()).queryByExample(example, ProduktEQConfig.class);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Equipment> createExampleEquipmentsFromProdEqConfig(final Long prodId, final String eqType,
            final boolean rangierungsPartDefault, final boolean rangierungsPartAdditional) throws FindException {
        try {
            final ProduktEQConfig example = new ProduktEQConfig();
            example.setProdId(prodId);
            example.setRangierungsPartDefault(rangierungsPartDefault);
            example.setRangierungsPartAdditional(rangierungsPartAdditional);
            if (StringUtils.isNotBlank(eqType)) {
                example.setEqTyp(eqType);
            }

            final List<ProduktEQConfig> configs = getEquipmentDAO().queryByExample(example, ProduktEQConfig.class);
            if (configs != null) {
                // die Map dient dazu, um aus einer Konfigurationsgruppe mit EQ_IN und EQ_OUT
                // nur je eine Seite (EQ_IN oder EQ_OUT) zu verwenden
                final Map<String, Equipment> exampleMap = new HashMap<String, Equipment>();
                final BeanUtilsBean beanUtils = new BeanUtilsBean(new EnumConvertUtilsBean());
                for (final ProduktEQConfig config : configs) {
                    // Key setzt sich aus Gruppierung+EqTyp zusammen
                    final String key = config.getConfigGroup() + "-" + config.getEqTyp();
                    Equipment eq = exampleMap.get(key);
                    if (eq == null) {
                        eq = new Equipment();
                        exampleMap.put(key, eq);
                    }
                    beanUtils.setProperty(eq, config.getEqParam(), config.getEqValue());
                }

                return new ArrayList<Equipment>(exampleMap.values());
            }

            return null;
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @return Returns the equipmentDAO.
     */
    public EquipmentDAO getEquipmentDAO() {
        return equipmentDAO;
    }

    /**
     * @param equipmentDAO The equipmentDAO to set.
     */
    public void setEquipmentDAO(final EquipmentDAO equipmentDAO) {
        this.equipmentDAO = equipmentDAO;
    }


    public void setRangierungsService(final RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    @Override
    public AKMessages createSDHPDHRangierung(final Long raId, final List<Long> eqIdsSDH,
            final List<Long> eqIdsCarrier, final List<Long> eqIdsPDHIn, final List<Long> eqIdsPDHOut,
            final String rangSSType, final Uebertragungsverfahren uetv, final Long sessionId) throws StoreException {

        try {
            final RangierungsAuftrag ra = findRA(raId);
            final AKUser user = getAKUserBySessionId(sessionId);
            final String userName = user.getLoginName();

            for (int i = 0; i < eqIdsCarrier.size(); i++) {
                final Equipment eqCarrier = equipmentDAO.findById(eqIdsCarrier.get(i), Equipment.class);
                final Equipment eqPDHIn = equipmentDAO.findById(eqIdsPDHIn.get(i), Equipment.class);
                final Equipment eqPDHOut = eqIdsPDHOut.size() > i ? equipmentDAO.findById(eqIdsPDHOut.get(i), Equipment.class) : null;
                final Equipment eqSDH = eqIdsSDH.size() > i ? equipmentDAO.findById(eqIdsSDH.get(i), Equipment.class) : null;

                final Rangierung rang1 = buildRangierung(ra, eqCarrier, eqPDHIn, rangSSType, uetv, userName);
                if (eqPDHOut != null) {
                    final Rangierung rang2 = buildRangierung(ra, eqPDHOut, eqSDH, rangSSType, null, null);
                    connectRangierungen(rang1, rang2);
                    rangierungsService.saveRangierung(rang2, false);
                }
                rangierungsService.saveRangierung(rang1, false);
            }

            ra.setDefiniertAm(new Date());
            ra.setDefiniertVon(user.getNameAndFirstName());
            saveRangierungsAuftrag(ra);
        }
        catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, ex);
        }

        return null;
    }

    private void connectRangierungen(final Rangierung rang1, final Rangierung rang2) throws FindException {
        final Integer leitungGesamtId = rangierungsService.findNextLtgGesId4Rangierung();
        rang1.setLeitungGesamtId(leitungGesamtId);
        rang1.setLeitungLfdNr(1);
        rang1.setLeitungLoeschen(Boolean.TRUE);
        rang2.setLeitungGesamtId(leitungGesamtId);
        rang2.setLeitungLfdNr(2);
        rang2.setLeitungLoeschen(Boolean.TRUE);
    }

    private Rangierung buildRangierung(final RangierungsAuftrag ra, final Equipment eqOut, final Equipment eqIn, final String rangSSType, final Uebertragungsverfahren uetv, final String userName) throws StoreException {
        final Rangierung rangierung = new Rangierung();
        rangierung.setPhysikTypId(PhysikTyp.PHYSIKTYP_CONNECT);
        rangierung.setFreigegeben(Freigegeben.in_Aufbau);
        rangierung.setEqInId(eqIn.getId());
        rangierung.setEqOutId(eqOut.getId());
        rangierung.setHvtIdStandort(ra.getHvtStandortId());
        rangierung.setRangierungsAuftragId(ra.getId());
        rangierung.setGueltigVon(new Date());
        rangierung.setGueltigBis(DateTools.getHurricanEndDate());
        rangierung.setUserW(userName);
        eqIn.setStatus(EqStatus.vorb);
        eqOut.setStatus(EqStatus.vorb);
        if (StringUtils.isNotBlank(rangSSType)) {
            eqOut.setRangSSType(rangSSType);
        }
        if (uetv != null) {
            eqOut.setUetv(uetv);
        }
        rangierungsService.saveEquipment(eqIn);
        rangierungsService.saveEquipment(eqOut);
        return rangierung;
    }

}


