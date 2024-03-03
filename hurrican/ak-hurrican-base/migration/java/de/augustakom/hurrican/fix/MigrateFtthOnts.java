package de.augustakom.hurrican.fix;

import java.io.*;
import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.Test;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntPortImportView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Existierende ONTs werden auf die neuen Datenstrukturen (HWOnt, HWBaugruppen, Ports(Equipment)) umgezogen.
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link de.augustakom.common.AbstractTransactionalServiceTest#NO_ROLLBACK_TEST}
 * für den Test setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über
 * die System Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul>
 */

// TODO: nur lokal bei Bedarf einschalten
//@Test(groups = AbstractTransactionalServiceTest.NO_ROLLBACK_TEST)
//@Test
public class MigrateFtthOnts extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(MigrateFtthOnts.class);

    @Inject
    private HWService hwService;

    @Inject
    private HVTService hvtService;

    @Inject
    private CPSService cpsService;

    @Inject
    private VlanService vlanService;

    @Inject
    private DeviceService deviceService;

    @Inject
    private EndstellenService endstellenService;

    @Inject
    private RangierungsService rangierungsService;

    @Inject
    private FTTXHardwareService fttxHardwareService;

    @Inject
    private AuftragTechnikDAO auftragTechnikDao;

    @Resource(name = "cc.hibernateTxManager")
    private PlatformTransactionManager tm;

    @SuppressWarnings("JpaQlInspection")
    final String hql = "select auft, at, ad, es, hvt, r, eq, prod from de.augustakom.hurrican.model.cc.Auftrag auft, AuftragTechnik at, AuftragDaten ad, " +
                    "Endstelle es, HVTStandort hvt, Rangierung r, Equipment eq, de.augustakom.hurrican.model.cc.Produkt prod \n"
                    + " where auft.id = at.auftragId \n"
                    + " and at.gueltigBis = '01.01.2200' \n"
                    + " and ad.gueltigBis = '01.01.2200' \n"
                    + " and ad.statusId != 3400 \n"
                    + " and ad.statusId != 1150 \n"
                    + " and ad.statusId < 9800 \n"
                    + " and ad.prodId = prod.id \n"
                    + " and at.auftragId = ad.auftragId \n"
                    + " and at.auftragTechnik2EndstelleId = es.endstelleGruppeId \n"
                    + " and es.hvtIdStandort = hvt.id \n"
                    + " and (es.rangierId is not null and es.rangierId = r.id) \n"
            + " and (r.eqInId is not null and eq.id = r.eqInId and r.physikTypId = 803)"
            + " and r.esId = es.id and es.rangierId = r.id";

    @SuppressWarnings("JpaQlInspection")
    final String hqlVlan = "select v from de.augustakom.hurrican.model.cc.Auftrag auft, AuftragTechnik at, AuftragDaten ad, " +
                    "Endstelle es, HVTStandort hvt, Rangierung r, Equipment eq, EqVlan v \n"
                    + " where auft.id = at.auftragId \n"
                    + " and at.gueltigBis = '01.01.2200' \n"
                    + " and ad.gueltigBis = '01.01.2200' \n"
                    + " and ad.statusId != 3400 \n"
                    + " and ad.statusId != 1150 \n"
                    + " and ad.statusId < 9800 \n"
                    + " and at.auftragId = ad.auftragId \n"
                    + " and at.auftragTechnik2EndstelleId = es.endstelleGruppeId \n"
                    + " and es.hvtIdStandort = hvt.id \n"
                    + " and (es.rangierId is not null and es.rangierId = r.id) \n"
                    + " and (r.eqInId is not null and eq.id = r.eqInId and r.physikTypId = 803) \n"
            + " and (v.equipmentId is null or v.equipmentId = eq.id)"
            + " and r.esId = es.id and es.rangierId = r.id";

    private enum EntityIndex {
        Auftrag(0),
        AuftragTechnik(1),
        AuftragDaten(2),
        Endstelle(3),
        HvtStandort(4),
        Rangierung(5),
        Equipment(6),
        Produkt(7);

        private int index;

        EntityIndex(int index) {
            this.index = index;
        }

        @SuppressWarnings("unchecked")
        public <T> T getEntity(Object[] entities) {
            if (entities != null && entities.length > index) {
                return (T) entities[index];
            }
            return null;
        }
    }

    private enum SSTypes {
        ETH(new int[] {}),
        POTS(new int[] {511}),
        RF(new int[] {500, 521});

        private int[] prodIds;

        SSTypes(int[] prodIds) {
            this.prodIds = prodIds;
        }

        private boolean isAssigned2Produkt(int prodId) {
            if (prodIds.length == 0) {
                return true;
            }
            for (int prodId1 : prodIds) {
                if (prodId == prodId1) {
                    return true;
                }
            }
            return false;
        }

        public static SSTypes getSSType4Produkt(int prodId) {
            if (POTS.isAssigned2Produkt(prodId)) {
                return POTS;
            }
            else if (RF.isAssigned2Produkt(prodId)) {
                return RF;
            }
            return ETH;
        }
    }

    @Test(enabled = false)
    @SuppressWarnings("unchecked")
    public void doMigration() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheetSuccess = workbook.createSheet("Erfolgreich");
        Sheet sheetErrors = workbook.createSheet("Fehler!");
        writeHeaderSuccess(sheetSuccess);
        writeHeaderErrors(sheetErrors);

        try {
            final Object[] queryResults = prepareAuftragData();
            forEachAuftrag((List<Object[]>) queryResults[0], (Multimap<Long, EqVlan>) queryResults[1],
                    sheetSuccess, sheetErrors);
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.getMessage(), e);
        }
        finally {
            File xlsFile = new File(System.getProperty("user.home"), getClass().getSimpleName()
                    + "_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xls");
            workbook.write(new FileOutputStream(xlsFile));
            LOGGER.info("XLS File=" + xlsFile.getPath());
        }
    }

    protected void writeHeaderSuccess(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "techn. Auftrag");
        XlsPoiTool.setContent(row, 1, "kaufm. Auftrag");
        XlsPoiTool.setContent(row, 2, "Status");
        XlsPoiTool.setContent(row, 3, "Produkt");
        XlsPoiTool.setContent(row, 4, "migrierte VLANs");
    }

    protected void writeHeaderErrors(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "techn. Auftrag");
        XlsPoiTool.setContent(row, 1, "kaufm. Auftrag");
        XlsPoiTool.setContent(row, 2, "real. Datum");
        XlsPoiTool.setContent(row, 3, "Status");
        XlsPoiTool.setContent(row, 4, "Produkt");
        XlsPoiTool.setContent(row, 5, "Produktbezeichnung");
        XlsPoiTool.setContent(row, 6, "Fehlerbeschreibung");
    }

    private void writeRowSuccess(Sheet sheet, Object[] auftragEntities, boolean hasVlans) {
        final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, auftragDaten.getAuftragId().toString());
        XlsPoiTool.setContent(row, 1, auftragDaten.getAuftragNoOrig().toString());
        XlsPoiTool.setContent(row, 2, auftragDaten.getStatusId().toString());
        XlsPoiTool.setContent(row, 3, auftragDaten.getProdId().toString());
        XlsPoiTool.setContent(row, 4, (hasVlans) ? "ja" : "nein");
    }

    private void writeRowErrors(Sheet sheet, Object[] auftragEntities, String message) {
        final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
        final Produkt produkt = EntityIndex.Produkt.getEntity(auftragEntities);
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        Date realisierung = (auftragDaten.getVorgabeSCV() != null) ? auftragDaten.getVorgabeSCV() :
                auftragDaten.getVorgabeKunde();
        XlsPoiTool.setContent(row, 0, auftragDaten.getAuftragId().toString());
        XlsPoiTool.setContent(row, 1, auftragDaten.getAuftragNoOrig().toString());
        XlsPoiTool.setContent(row, 2, (realisierung != null) ? realisierung.toString() : "");
        XlsPoiTool.setContent(row, 3, auftragDaten.getStatusId().toString());
        XlsPoiTool.setContent(row, 4, auftragDaten.getProdId().toString());
        XlsPoiTool.setContent(row, 5, produkt.getAnschlussart());
        XlsPoiTool.setContent(row, 6, message);
    }

    @SuppressWarnings("unchecked")
    private Object[] prepareAuftragData() {
        Object[] queryResults = new Object[2];
        queryResults[0] = auftragTechnikDao.find(hql);
        List<EqVlan> vlans = ((Hibernate4DAOImpl) auftragTechnikDao).find(hqlVlan);
        Multimap<Long, EqVlan> vlanMultimap = ArrayListMultimap.create();
        if (vlans != null) {
            for (EqVlan eqVlan : vlans) {
                vlanMultimap.put(eqVlan.getEquipmentId(), eqVlan);
            }
        }
        queryResults[1] = vlanMultimap;
        return queryResults;
    }

    private void forEachAuftrag(final List<Object[]> alleAuftraege, final Multimap<Long, EqVlan> vlans,
            final Sheet sheetSuccess, final Sheet sheetErrors) {
        final AKWarnings warnings = new AKWarnings();
        if (alleAuftraege != null && !alleAuftraege.isEmpty()) {
            final Map<String, List<Object[]>> associatedAuftraege = createAssociatedAuftraegeMap(alleAuftraege);
            logStatistics(alleAuftraege, vlans, warnings);
            for (Object[] auftragEntities : alleAuftraege) {
                processAuftrag(associatedAuftraege, auftragEntities, vlans, warnings, sheetSuccess, sheetErrors);
            }
        }
        else {
            LOGGER.info("Es wurden keine Auftraege zur Bearbeitung gefunden!");
        }
        LOGGER.info(String.format("Es sind %d Auftraege mit Fehler abgebrochen:\n%s",
                (warnings.getAKMessages() != null) ? warnings.getAKMessages().size() : 0,
                warnings.getWarningsAsText()));
    }

    private void logStatistics(final List<Object[]> alleAuftraege, final Multimap<Long, EqVlan> vlans,
            AKWarnings warnings) {
        String message = String.format("Die ONT Migration hat %d Auftraege zur Bearbeitung gefunden.",
                alleAuftraege.size());
        LOGGER.info(message);
        warnings.addAKWarning(this, message);
        message = String.format("Insgesamt werden voraussichtlich %d VLANs an %d Ports migriert.",
                vlans.size(), vlans.asMap().size());
        LOGGER.info(message);
        warnings.addAKWarning(this, message);

    }

    private void processAuftrag(final Map<String, List<Object[]>> associatedAuftraege,
            final Object[] auftragEntities, final Multimap<Long, EqVlan> vlans, final AKWarnings warnings,
            final Sheet sheetSuccess, final Sheet sheetErrors) {
        final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        HWOnt hwOnt = findHwOnt(auftragEntities);
                        if (hwOnt == null) {
                            HWRack oltOrGslam = findOltOrGslam(auftragEntities);
                            HVTGruppe hvtGruppe = findHvtGruppe(auftragEntities);
                            Device ontDevice = findOnt(associatedAuftraege, auftragEntities);
                            checkPreconditions(auftragEntities, hvtGruppe, ontDevice, oltOrGslam);
                            hwOnt = createOnt(oltOrGslam, hvtGruppe, ontDevice, auftragEntities);
                            createOntPorts(hwOnt);
                        }
                        createDummyCpsTx(auftragEntities, hwOnt);
                        boolean hasVlans = switchPort(hwOnt, auftragEntities, vlans);
                        writeRowSuccess(sheetSuccess, auftragEntities, hasVlans);
                    }
                    catch (Exception e) {
                        status.setRollbackOnly();
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        catch (RuntimeException e) {
            StringBuilder builder = new StringBuilder();
            Throwable cause = e;
            while (cause != null) {
                if (cause.getMessage() != null) {
                    if (builder.length() > 0) {
                        builder.append("\n");
                    }
                    builder.append(cause.getMessage());
                }
                cause = cause.getCause();
            }
            final String message = String
                    .format("ProcessAuftrag hat fuer den techn. Auftrag %d einen Fehler gefangen. Grund: %s",
                            auftragDaten.getAuftragId(), builder.toString());
            LOGGER.error(message, e);
            warnings.addAKWarning(this, message);
            writeRowErrors(sheetErrors, auftragEntities, message);
        }
    }

    private void createDummyCpsTx(Object[] auftragEntities, HWOnt hwOnt) throws StoreException, FindException {
        final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
        List<CPSTransactionExt> transactions = cpsService.findSuccessfulCPSTransactions(auftragDaten.getAuftragNoOrig(),
                null, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        if (transactions != null && !transactions.isEmpty() && !StringUtils.isBlank(hwOnt.getSerialNo())) {
            transactions = cpsService.findSuccessfulCPSTransactions(null, hwOnt.getId(),
                    CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
            if (transactions == null || transactions.isEmpty()) {
                final CPSTransactionResult cpsTxResult =
                        cpsService.createCPSTransaction4OltChild(hwOnt.getId(),
                                CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, getSessionId());
                final CPSTransaction dumyTransaction = cpsTxResult.getCpsTransactions().get(0);
                dumyTransaction.setTxState(CPSTransaction.TX_STATE_SUCCESS);
                dumyTransaction.setResponseAt(new Date());
                cpsService.saveCPSTransaction(dumyTransaction, getSessionId());
            }
        }
    }

    private boolean switchPort(final HWOnt hwOnt, final Object[] auftragEntities, final Multimap<Long, EqVlan> vlans)
            throws StoreException, FindException, ValidationException {
        writeHwEqn2Ont(hwOnt, auftragEntities);
        writeOlt2Ont(hwOnt, auftragEntities);
        hwService.saveHWRack(hwOnt);
        return switchPorts4Endstelle(hwOnt, auftragEntities, vlans);
    }

    private boolean switchPorts4Endstelle(final HWOnt hwOnt, final Object[] auftragEntities,
            final Multimap<Long, EqVlan> vlans) throws FindException, StoreException {
        final HWBaugruppe hwBaugruppeNeu = findBaugruppe4Ont(hwOnt, auftragEntities);
        final Equipment equipmentNeu = findEquipment4Baugruppe(hwBaugruppeNeu);
        final Rangierung rangierungNeu = rangierungsService.findRangierung4Equipment(equipmentNeu.getId());
        if (rangierungNeu == null) {
            throw new FindException(String.format("ONT ID %d: Keine Rangierung fuer gezogenen Port %d verfuegbar!",
                    hwOnt.getId(), equipmentNeu.getId()));
        }
        if (rangierungNeu.getFreigegebenBoolean() == Boolean.FALSE) {
            throw new FindException(String.format("ONT ID %d: Neu gezogene Rangierung %d ist nicht freigegeben!",
                    hwOnt.getId(), rangierungNeu.getId()));
        }
        final Endstelle endstelle = EntityIndex.Endstelle.getEntity(auftragEntities);
        final Rangierung rangierungAlt = EntityIndex.Rangierung.getEntity(auftragEntities);
        Equipment equipmentAlt = null; // beenden, wenn verfuegbar
        if (rangierungAlt.getEqInId() != null) {
            equipmentAlt = rangierungsService.findEquipment(rangierungAlt.getEqInId());
        }
        rangierungAlt.setFreigegeben(Rangierung.Freigegeben.gesperrt);
        rangierungAlt.setGueltigBis(new Date());
        if (equipmentAlt != null) {
            equipmentAlt.setStatus(EqStatus.locked);
            equipmentAlt.setGueltigBis(new Date());
        }
        endstelle.setRangierId(rangierungNeu.getId());
        rangierungNeu.setEsId(endstelle.getId());
        equipmentNeu.setStatus(EqStatus.rang);
        boolean hasVlans = alterVlans(equipmentNeu.getId(), rangierungAlt.getEqInId(), vlans);

        // Speichern!
        rangierungsService.saveRangierung(rangierungAlt, false);
        if (equipmentAlt != null) {
            rangierungsService.saveEquipment(equipmentAlt);
        }
        endstellenService.saveEndstelle(endstelle);
        rangierungsService.saveRangierung(rangierungNeu, false);
        rangierungsService.saveEquipment(equipmentNeu);

        return hasVlans;
    }

    private boolean alterVlans(Long eqIdNeu, Long eqIdAlt, final Multimap<Long, EqVlan> vlans) {
        Collection<EqVlan> vlans4Eq = vlans.asMap().get(eqIdAlt);
        if (vlans4Eq != null) {
            for (EqVlan eqVlan : vlans4Eq) {
                eqVlan.setEquipmentId(eqIdNeu);
                vlanService.saveEqVlan(eqVlan);
            }
        }
        return (vlans4Eq != null && !vlans4Eq.isEmpty());
    }

    private HWBaugruppe findBaugruppe4Ont(HWOnt hwOnt, Object[] auftragEntities) throws FindException {
        final List<HWBaugruppe> hwBaugruppen = hwService.findBaugruppen4Rack(hwOnt.getId());
        if (hwBaugruppen == null || hwBaugruppen.isEmpty()) {
            throw new FindException(String.format("ONT ID %d: Keine Baugruppen fuer ONT verfuegbar!",
                    hwOnt.getId()));
        }
        final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
        final SSTypes ssType = SSTypes.getSSType4Produkt(auftragDaten.getProdId().intValue());

        for (HWBaugruppe hwBaugruppe : hwBaugruppen) {
            if (StringUtils.equals(ssType.name(),
                    hwBaugruppe.getHwBaugruppenTyp().getHwSchnittstelleName())) {
                return hwBaugruppe;
            }
        }
        throw new FindException(String.format("ONT ID %d: Keine Baugruppe fuer den Typ %s verfuegbar!",
                hwOnt.getId(), ssType.name()));
    }

    private Equipment findEquipment4Baugruppe(HWBaugruppe hwBaugruppe) throws FindException {
        final List<Equipment> equipments = rangierungsService.findEquipments4HWBaugruppe(hwBaugruppe.getId());
        if (equipments == null || equipments.isEmpty()) {
            throw new FindException(String.format("Baugruppe ID %d: Keine Ports fuer Baugruppe verfuegbar!",
                    hwBaugruppe.getId()));
        }
        Equipment equipment = null;
        for (int portNumber = 1; portNumber <= hwBaugruppe.getHwBaugruppenTyp().getPortCount(); portNumber++) {
            equipment = findEquipmentByPortNumber(equipments, portNumber);
            if (equipment != null && equipment.getStatus() == EqStatus.frei) {
                break;
            }
        }
        if (equipment == null) {
            throw new FindException(String.format("Baugruppe ID %d: Kein freier Port fuer Baugruppe verfuegbar!",
                    hwBaugruppe.getId()));
        }
        return equipment;
    }

    private Equipment findEquipmentByPortNumber(List<Equipment> equipments, int portNumber) {
        for (Equipment equipment : equipments) {
            if (portNumber == equipment.getHwEQNPartAsInt(Equipment.HWEQNPART_FTTX_ONT_PORT_NUMBER)) {
                return equipment;
            }
        }
        return null;
    }

    private void writeHwEqn2Ont(HWOnt hwOnt, Object[] auftragEntities) throws StoreException {
        final Equipment equipment = EntityIndex.Equipment.getEntity(auftragEntities);
        boolean alreadySet = (hwOnt.getOltFrame() != null || hwOnt.getOltSlot() != null
                || hwOnt.getOltGPONPort() != null || hwOnt.getOltGPONId() != null);
        hwOnt.setOltFrame(extractPartAndCheck(equipment, hwOnt.getOltFrame(),
                Equipment.HWEQNPART_FTTX_OLT_FRAME, alreadySet));
        hwOnt.setOltSubrack(extractPartAndCheck(equipment, hwOnt.getOltSubrack(),
                Equipment.HWEQNPART_FTTX_OLT_SUBRACK, alreadySet));
        hwOnt.setOltSlot(extractPartAndCheck(equipment, hwOnt.getOltSlot(),
                Equipment.HWEQNPART_FTTX_OLT_GPON_SLOT, alreadySet));
        hwOnt.setOltGPONPort(extractPartAndCheck(equipment, hwOnt.getOltGPONPort(),
                Equipment.HWEQNPART_FTTX_OLT_GPON_PORT, alreadySet));
        hwOnt.setOltGPONId(extractPartAndCheck(equipment, hwOnt.getOltGPONId(),
                Equipment.HWEQNPART_FTTX_OLT_GPON_ID, alreadySet));
    }

    private String extractPartAndCheck(Equipment equipment, String oltPart, int hwEqnPartId, boolean alreadySet)
            throws StoreException {
        String hwEqnPart = equipment.getHwEQNPart(hwEqnPartId);
        if (hwEqnPart != null) {
            hwEqnPart = Integer.valueOf(hwEqnPart).toString();
        }
        if (alreadySet && !StringUtils.equals(hwEqnPart, oltPart)) {
            throw new StoreException(String.format("Der HwEqnPart %s weicht ab: Ist '%s', neu '%s'!",
                    getHwEqnPartIdentifier(hwEqnPartId), oltPart, hwEqnPart));
        }
        return hwEqnPart;
    }

    private String getHwEqnPartIdentifier(int hwEqnPartId) {
        switch (hwEqnPartId) {
            case Equipment.HWEQNPART_FTTX_OLT_FRAME:
                return "OLT Frame";
            case Equipment.HWEQNPART_FTTX_OLT_SUBRACK:
                return "OLT Subrack";
            case Equipment.HWEQNPART_FTTX_OLT_GPON_SLOT:
                return "OLT Slot";
            case Equipment.HWEQNPART_FTTX_OLT_GPON_PORT:
                return "OLT Port";
            case Equipment.HWEQNPART_FTTX_OLT_GPON_ID:
                return "OLT GPON ID";
        }
        return "<null>";
    }

    private void writeOlt2Ont(HWOnt hwOnt, Object[] auftragEntities) throws FindException, StoreException {
        HWRack hwRack = findOltOrGslam(auftragEntities);
        if (hwOnt.getOltRackId() != null && !hwOnt.getOltRackId().equals(hwRack.getId())) {
            throw new StoreException(String.format("ONT %s: OLT/GSLAM ID %d weicht von neuer OLT/GSLAM ID %d ab!",
                    hwOnt.getGeraeteBez(), hwOnt.getOltRackId(), hwRack.getId()));
        }
        hwOnt.setOltRackId(hwRack.getId());
    }

    private void createOntPorts(HWOnt hwOnt) throws StoreException, FindException {
        final FTTHOntPortImportView importView = new FTTHOntPortImportView();
        importView.setOltChild(hwOnt.getGeraeteBez());
        createOntPorts4Schnittstelle(importView, hwOnt, SSTypes.ETH);
        createOntPorts4Schnittstelle(importView, hwOnt, SSTypes.POTS);
        createOntPorts4Schnittstelle(importView, hwOnt, SSTypes.RF);
    }

    private void createOntPorts4Schnittstelle(FTTHOntPortImportView importView, HWOnt hwOnt, SSTypes ssType)
            throws FindException, StoreException {
        String bgTyp = hwOnt.getOntType() + "_" + ssType.name();
        int portNummer = 1;

        switch (bgTyp) {
            case "I-010G-P_RF":
                portNummer = 0;
                break;
            case "I-010G-P_POTS":
                portNummer = 0;
                break;
            case "HG865_ETH":
                portNummer = 4;
                break;
        }

        if (portNummer > 0) {
            importView.setSchnittstelle(ssType.name());
            importView.setPort(createHwEqn(ssType, portNummer));
            fttxHardwareService.generateFTTHOntPort(importView, getSessionId());
        }
    }

    private String createHwEqn(SSTypes ssType, Integer portNo) {
        StringBuilder builder = new StringBuilder();
        switch (ssType) {
            case ETH:
                builder.append("1-");
                break;
            case POTS:
                builder.append("2-");
                break;
            case RF:
                builder.append("3-");
                break;
        }
        builder.append(portNo);
        return builder.toString();
    }

    private HWOnt createOnt(HWRack oltOrGslam, HVTGruppe hvtGruppe, Device ontDevice,
            Object[] auftragEntities) throws StoreException, ValidationException {
        final Rangierung rangierung = EntityIndex.Rangierung.getEntity(auftragEntities);
        final Equipment equipment = EntityIndex.Equipment.getEntity(auftragEntities);
        final FTTHOntImportView importView = new FTTHOntImportView();
        importView.setBezeichnung(rangierung.getOntId());
        importView.setHersteller(ontDevice.getManufacturer());
        importView.setSeriennummer(ontDevice.getSerialNumber());
        importView.setModellnummer(ontDevice.getTechName());
        importView.setOlt(oltOrGslam.getGeraeteBez());
        importView.setStandort(hvtGruppe.getOrtsteil());
        importView.setRaumbezeichung(String.format("<%s Migration: Raumbzeichnung nicht bekannt>",
                rangierung.getOntId()));
        importView.setOltRack(Integer.valueOf(equipment.getHwEQNPartAsInt(Equipment.HWEQNPART_FTTX_OLT_FRAME)).longValue());
        if (equipment.getHwEQNPart(Equipment.HWEQNPART_FTTX_OLT_SUBRACK) != null) {
            importView.setOltSubrack(
                    Integer.valueOf(equipment.getHwEQNPartAsInt(Equipment.HWEQNPART_FTTX_OLT_SUBRACK)).longValue());
        }
        importView.setOltSlot(
                Integer.valueOf(equipment.getHwEQNPartAsInt(Equipment.HWEQNPART_FTTX_OLT_GPON_SLOT)).longValue());
        importView.setOltPort(
                Integer.valueOf(equipment.getHwEQNPartAsInt(Equipment.HWEQNPART_FTTX_OLT_GPON_PORT)).longValue());
        importView.setGponId(
                Integer.valueOf(equipment.getHwEQNPartAsInt(Equipment.HWEQNPART_FTTX_OLT_GPON_ID)).longValue());
        // Freigabe = heute, wenn OLT/GSLAM aktiv ist
        // Freigabe = gueltigVon, wenn OLT/GSLAM in Zukunft gueltig ist
        LocalDateTime freigabe = null;
        Date to = (oltOrGslam.getGueltigBis() != null) ? oltOrGslam.getGueltigBis() : DateTools.getHurricanEndDate();
        if (DateTools.isDateBetween(new Date(), oltOrGslam.getGueltigVon(), to)) {
            freigabe = LocalDateTime.now();
        }
        else if (oltOrGslam.getGueltigVon() != null && DateTools.isAfter(oltOrGslam.getGueltigVon(), new Date())) {
            freigabe = DateConverterUtils.asLocalDateTime(oltOrGslam.getGueltigVon());
        }
        HWOnt saved = fttxHardwareService.generateFTTHOnt(importView, getSessionId());
        saved.setFreigabe(DateConverterUtils.asDate(freigabe));
        return hwService.saveHWRack(saved);
    }

    private void checkPreconditions(Object[] auftragEntities, HVTGruppe hvtGruppe, Device ontDevice, HWRack oltOrGslam)
            throws FindException {
        final Rangierung rangierung = EntityIndex.Rangierung.getEntity(auftragEntities);
        if (StringUtils.isEmpty(rangierung.getOntId())) {
            throw new FindException("Keine ONT-ID auf der Rangierung!");
        }
        if (StringUtils.isEmpty(hvtGruppe.getOrtsteil())) {
            throw new FindException("Die Standortbezeichnung fehlt!");
        }
        if (StringUtils.isEmpty(ontDevice.getManufacturer())) {
            throw new FindException("Dem ONT Device fehlt der Hersteller!");
        }
        if (StringUtils.isEmpty(ontDevice.getTechName())) {
            throw new FindException("Dem ONT Device fehlt der Baugruppentyp!");
        }
        if (StringUtils.isEmpty(oltOrGslam.getGeraeteBez())) {
            throw new FindException("Der OLT fehlt die Geraetebezeichnung!");
        }
    }

    private HWRack findOltOrGslam(Object[] auftragEntities) throws FindException {
        final Equipment equipment = EntityIndex.Equipment.getEntity(auftragEntities);
        HWBaugruppe hwBaugruppe = hwService.findBaugruppe(equipment.getHwBaugruppenId());
        if (hwBaugruppe == null) {
            throw new FindException(String.format("Equipment %d: Es konnte keine Baugruppe ermittelt werden!",
                    equipment.getId()));
        }
        HWRack hwRack = hwService.findRackById(hwBaugruppe.getRackId());
        if (hwRack == null) {
            throw new FindException(
                    String.format("Equipment %d -> Baugruppe %d: Das Rack %d konnte nicht ermittelt werden!",
                            equipment.getId(), hwBaugruppe.getId(), hwBaugruppe.getRackId()));
        }
        if (!(hwRack instanceof HWOlt)
                && !(hwRack instanceof HWDslam)) {
            throw new FindException(
                    String.format("Equipment %d -> Baugruppe %d -> Rack %d: Das Rack muss vom Typ OLT oder GSLAM sein!",
                            equipment.getId(), hwBaugruppe.getId(), hwRack.getId()));
        }
        return hwRack;
    }

    private HVTGruppe findHvtGruppe(Object[] auftragEntities) throws FindException {
        final Rangierung rangierung = EntityIndex.Rangierung.getEntity(auftragEntities);
        HVTStandort hvtStandort = hvtService.findHVTStandort(rangierung.getHvtIdStandort());
        if (hvtStandort == null) {
            throw new FindException(String.format("Es ist kein Standort auf der Rangierung hinterlegt!"));
        }
        HVTGruppe hvtGruppe = hvtService.findHVTGruppeById(hvtStandort.getHvtGruppeId());
        if (hvtGruppe == null) {
            throw new FindException(String.format("Standort %d: Zu einem Standort fehlt die Gruppe!",
                    hvtStandort.getId()));
        }
        return hvtGruppe;
    }

    private HWOnt findHwOnt(Object[] auftragEntities) throws FindException {
        final Rangierung rangierung = EntityIndex.Rangierung.getEntity(auftragEntities);
        if (rangierung.getOntId() != null) {
            HWRack hwRack = hwService.findRackByBezeichnung(rangierung.getOntId());
            if (hwRack != null && hwRack instanceof HWOnt) {
                return (HWOnt) hwRack;
            }
        }
        return null;
    }

    private Device findOnt(Map<String, List<Object[]>> associatedAuftraege, Object[] auftragEntities)
            throws FindException {
        List<Object[]> auftraege = findAssociatedAuftraege(associatedAuftraege, auftragEntities);
        Device ont;
        List<Device> onts = new ArrayList<>();
        for (Object[] auftrag : auftraege) {
            ont = findInstalledOnt(auftrag);
            if (ont != null) {
                onts.add(ont);
            }
        }
        ont = getBestMatch(onts);
        if (ont == null) {
            onts.clear();
            for (Object[] auftrag : auftraege) {
                ont = findOrderedOnt(auftrag);
                if (ont != null) {
                    onts.add(ont);
                }
            }
            ont = getBestMatch(onts);
        }
        if (ont == null) {
            final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
            throw new FindException(String.format("Fuer den kaufm. Auftrag %d ist keine ONT hinterlegt!",
                    auftragDaten.getAuftragNoOrig()));
        }
        mapHersteller(ont);
        mapTechName(ont);
        return ont;
    }

    private Device getBestMatch(List<Device> onts) {
        if (onts.isEmpty()) {
            return null;
        }
        Device ont2Return = null;
        for (Device ont : onts) {
            if (!StringUtils.isBlank(ont.getTechName())
                    && !StringUtils.isBlank(ont.getManufacturer())) {
                if (!StringUtils.isBlank(ont.getSerialNumber())) {
                    return ont;
                }
                else if (ont2Return == null) {
                    ont2Return = ont;
                }
                else {
                    if (StringUtils.isBlank(ont2Return.getSerialNumber())) {
                        ont2Return = ont;
                    }
                }
            }
        }
        return ont2Return;
    }

    private void mapTechName(Device ont) {
        if (ont.getTechName() != null) {
            if (StringUtils.equals("I-010-P", ont.getTechName())) {
                ont.setTechName("I-010G-P");
            }
            else if (StringUtils.equals("I-241GA", ont.getTechName())) {
                ont.setTechName("I-221E-A");
            }
            else if (StringUtils.equals("I-241GQ", ont.getTechName())) {
                ont.setTechName("I-241G-Q");
            }
        }
    }

    private void mapHersteller(Device ont) {
        if (ont.getManufacturer() != null) {
            if (StringUtils.equals("Alcatel Lucent", ont.getManufacturer())) {
                ont.setManufacturer("Alcatel-Lucent");
            }
        }
    }

    private Map<String, List<Object[]>> createAssociatedAuftraegeMap(List<Object[]> alleAuftraege) {
        final Map<String, List<Object[]>> associatedAuftraege = Maps.newHashMap();
        for (Object[] auftragEntities : alleAuftraege) {
            final Rangierung rangierung = EntityIndex.Rangierung.getEntity(auftragEntities);
            List<Object[]> auftraege = associatedAuftraege.get(rangierung.getOntId());
            if (auftraege == null) {
                auftraege = Lists.newArrayList();
                associatedAuftraege.put(rangierung.getOntId(), auftraege);
            }
            auftraege.add(auftragEntities);
        }

        // Aktive Auftraege vor gekuendigten Auftraegen vor abgesagten, stornierten Auftraegen
        Ordering<Object[]> byAuftragStatusOrdering = new Ordering<Object[]>() {
            public int compare(Object[] left, Object[] right) {
                final AuftragDaten auftragDatenLeft = EntityIndex.AuftragDaten.getEntity(left);
                final AuftragDaten auftragDatenRight = EntityIndex.AuftragDaten.getEntity(right);
                if (auftragDatenLeft.getStatusId().compareTo(AuftragStatus.ABSAGE) == 0
                        || auftragDatenLeft.getStatusId().compareTo(AuftragStatus.STORNO) == 0
                        || auftragDatenLeft.getStatusId().compareTo(AuftragStatus.KONSOLIDIERT) == 0) {
                    if (auftragDatenRight.getStatusId().compareTo(AuftragStatus.ABSAGE) == 0
                            || auftragDatenRight.getStatusId().compareTo(AuftragStatus.STORNO) == 0
                            || auftragDatenRight.getStatusId().compareTo(AuftragStatus.KONSOLIDIERT) == 0) {
                        return 0;
                    }
                    return 1;
                }
                else if (auftragDatenLeft.getStatusId().compareTo(AuftragStatus.AUFTRAG_GEKUENDIGT) == 0
                        || auftragDatenLeft.getStatusId().compareTo(AuftragStatus.AUFTRAG_GEKUENDIGT) > 0) {
                    if (auftragDatenRight.getStatusId().compareTo(AuftragStatus.ABSAGE) == 0
                            || auftragDatenRight.getStatusId().compareTo(AuftragStatus.STORNO) == 0
                            || auftragDatenRight.getStatusId().compareTo(AuftragStatus.KONSOLIDIERT) == 0) {
                        return -1;
                    }
                    if (auftragDatenRight.getStatusId().compareTo(AuftragStatus.ABSAGE) == 0
                            || auftragDatenRight.getStatusId().compareTo(AuftragStatus.STORNO) == 0
                            || auftragDatenRight.getStatusId().compareTo(AuftragStatus.KONSOLIDIERT) == 0) {
                        return 0;
                    }
                    return 1;
                }
                else {
                    if (auftragDatenLeft.getStatusId().compareTo(AuftragStatus.IN_BETRIEB) == 0
                            && auftragDatenRight.getStatusId().compareTo(AuftragStatus.IN_BETRIEB) != 0) {
                        return -1;
                    }
                    else if (auftragDatenLeft.getStatusId().compareTo(AuftragStatus.IN_BETRIEB) != 0
                            && auftragDatenRight.getStatusId().compareTo(AuftragStatus.IN_BETRIEB) == 0) {
                        return 1;
                    }
                    return 0;
                }
            }
        };
        for (String ontId : associatedAuftraege.keySet()) {
            List<Object[]> auftraege = associatedAuftraege.get(ontId);
            Collections.sort(auftraege, byAuftragStatusOrdering);
        }
        return associatedAuftraege;
    }

    private List<Object[]> findAssociatedAuftraege(Map<String, List<Object[]>> associatedAuftraege,
            Object[] auftragEntities) {
        final Rangierung rangierung = EntityIndex.Rangierung.getEntity(auftragEntities);
        return associatedAuftraege.get(rangierung.getOntId());
    }

    private Device findInstalledOnt(Object[] auftragEntities) throws FindException {
        final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
        List<Device> devices = deviceService.findDevices4Auftrag(auftragDaten.getAuftragNoOrig(),
                Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_ONT);
        return getBestJoice(devices);
    }

    private Device findOrderedOnt(Object[] auftragEntities) throws FindException {
        final AuftragDaten auftragDaten = EntityIndex.AuftragDaten.getEntity(auftragEntities);
        List<Device> devices = deviceService.findOrderedDevices4Auftrag(auftragDaten.getAuftragNoOrig(),
                Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_ONT);
        return getBestJoice(devices);
    }

    private Device getBestJoice(List<Device> devices) {
        Device bestJoice = null;
        if (devices != null && !devices.isEmpty()) {
            for (Device device : devices) {
                if (device.getValidTo() == null
                        || DateTools.isDateEqual(device.getValidTo(), DateTools.getBillingEndDate())) {
                    bestJoice = device;
                    break;
                }
                if (bestJoice == null) {
                    bestJoice = device;
                }
                else if (device.getValidTo() != null
                        && DateTools.isAfter(device.getValidTo(), bestJoice.getValidTo())) {
                    bestJoice = device;
                }
                else if (DateTools.isAfter(device.getValidFrom(), bestJoice.getValidFrom())) {
                    bestJoice = device;
                }
            }
        }
        return bestJoice;
    }
}
