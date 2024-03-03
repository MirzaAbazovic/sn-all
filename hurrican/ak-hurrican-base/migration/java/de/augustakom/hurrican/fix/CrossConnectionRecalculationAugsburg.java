/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2013 11:16:24
 */
package de.augustakom.hurrican.fix;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.AbstractTransactionalServiceTest;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndstellenService;

/**
 * Die bearbeiteten CrossConnections werden in einer XLS Datei protokolliert.
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link AbstractTransactionalServiceTest#NO_ROLLBACK_TEST} für den Test
 * setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über die System
 * Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über die System
 * Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul>
 */
// TODO: nur lokal bei Bedarf einschalten
// @Test(groups = AbstractTransactionalServiceTest.NO_ROLLBACK_TEST)
// @Test
public class CrossConnectionRecalculationAugsburg extends AbstractHurricanBaseServiceTest {

    @Autowired
    private EQCrossConnectionService eqCrossConnectionService;
    @Autowired
    private HardwareDAO hardwareDAO;
    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private EquipmentDAO equipmentDAO;

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    private static final Logger LOGGER = Logger.getLogger(CrossConnectionRecalculationAugsburg.class);

    private static final Date ACTIVATION_DATE = new Date();
    private static final String PATH_TO_XLS_IN = "c://Project/cc_08.03.2013_filtered.xlsx";
    private static final String PATH_TO_XLS_OUT = "c://Project/CC_Huawei_diff.xlsx";
    private static final String PATH_TO_CC_OUT = "c://Project/CCs_neu.csv";

    private static final int COL_MANAGEMENTBEZ_IN = 0;
    private static final int COL_SHELF_IN = 2;
    private static final int COL_SLOT_IN = 3;
    private static final int COL_PORT_IN = 4;
    private static final int COL_VPI_IN = 5;
    private static final int COL_VCI_IN = 6;
    private static final int COL_VLANID_IN = 10;
    private static final int COL_INNERVLANID_IN = 11;

    private enum COL_OUT {
        EQID,
        MANAGEMENTBEZ,
        SHELF,
        SLOT,
        PORT,
        VPI_OLD,
        VPI_NEW,
        VCI_OLD,
        VCI_NEW,
        VLANID_OLD,
        VLANID_NEW,
        INNERVLANID_OLD,
        INNERVLANID_NEW,
    }

    public void recalculateAndCompare() throws Exception {
        // final Long startTime = System.currentTimeMillis();

        // Zeile einkommentieren um neuberechneten Bestand gegen Excel-File von Raimund abgzugleichen
        // final Future<Set<Pair<String, String>>> huaweiSdslHwEqnsFuture = findHwEqnPartsOfAllSdslOutEquipments();

        List<HWDslam> dslamsHuawei = findDslamsHuawei();
        Map<HWBaugruppe, String> baugruppenHuawei = findBaugruppenHuawei(dslamsHuawei);
        dslamsHuawei = null;

        final Set<Pair<String, String>> hwEqnPartsOhneAktivenAuftrag = Sets.newHashSet();
        findEquipmentsHuaweiAndCalculateCrossConnections(
                baugruppenHuawei, hwEqnPartsOhneAktivenAuftrag);
        baugruppenHuawei = null;

        // Zeile einkommentieren um neuberechneten Bestand gegen Excel-File von Raimund abgzugleichen
        // findDiffToExcelAndWriteResult(startTime, huaweiSdslHwEqnsFuture, hwEqnPartsOhneAktivenAuftrag,
        // equipmentsHuawei);
    }

    private List<Object[]> findAuftragDatenInKuendigungWithEquipmentHuawei() {
        final StringBuilder hql = new StringBuilder()
                .append("select ad, e from AuftragDaten ad, AuftragTechnik at, Endstelle es, Rangierung rang, Equipment e, HWBaugruppe b, HWRack rack ")
                .append("where ad.auftragId = at.auftragId ")
                .append("and ad.gueltigBis = '01.01.2200' ")
                .append("and ad.statusId == 4000 ")
                        // .append("and ad.statusId >= 3000 ")
                .append("and at.gueltigBis = '01.01.2200' ")
                .append("and at.auftragTechnik2EndstelleId = es.endstelleGruppeId ")
                .append("and es.endstelleTyp = 'B' ")
                .append("and es.rangierId = rang.id ")
                        // .append("and rang.esId = -1")
                .append("and rang.eqInId = e.id ")
                .append("and e.hwBaugruppenId = b.id ")
                .append("and e.hwSchnittstelle = 'ADSL-OUT' ")
                .append("and b.rackId = rack.id ")
                .append("and rack.rackTyp = 'DSLAM' ")
                .append("and rack.hwProducer = 2 ");

        return ((Hibernate4DAOImpl) equipmentDAO).find(hql.toString());
    }

    public void calculateCrossConnections4AuftragInKuendigungAndHuawei() throws Exception {
        final List<Object[]> auftragDatens = findAuftragDatenInKuendigungWithEquipmentHuawei();
        final File outFile = new File(PATH_TO_CC_OUT);
        deleteAndRecreateFile(outFile);

        try (final FileWriter writer = new FileWriter(outFile)) {
            for (final Object[] adWithEq : auftragDatens) {
                final AuftragDaten ad = (AuftragDaten) adWithEq[0];
                final Equipment eq = (Equipment) adWithEq[1];

                if (eqCrossConnectionService.findEQCrossConnections(eq.getId()).isEmpty()) {
                    // eqCrossConnectionService.deleteEQCrossConnectionsOfEquipment(eq.getId());

                    try {
                        final List<EQCrossConnection> crossConnections4Auftrag = eqCrossConnectionService
                                .calculateDefaultCcs(eq, "migration", ACTIVATION_DATE,
                                        ad.getAuftragId());
                        eqCrossConnectionService.saveEQCrossConnections(crossConnections4Auftrag);
                        writeCrossConnections4AuftragToCsv(writer, eq, "",
                                crossConnections4Auftrag, ad.getAuftragNoOrig());
                    }
                    catch (FindException e) {
                        LOGGER.error(String.format("AuftragNo: %s", ad.getAuftragNoOrig()), e);
                    }
                }
            }
        }
    }

    private void findDiffToExcelAndWriteResult(final long startTime,
            final Future<Set<Pair<String, String>>> huaweiSdslHwEqnsFuture,
            final Set<Pair<String, String>> hwEqnPartsOhneAktivenAuftrag,
            final Map<String, Map<String, Pair<Equipment, List<EQCrossConnection>>>> equipmentsHuawei)
            throws IOException, InvalidFormatException, InterruptedException, ExecutionException, FileNotFoundException {
        final File outFile = new File(PATH_TO_XLS_OUT);
        deleteAndRecreateFile(outFile);

        try (final InputStream inStream = new FileInputStream(PATH_TO_XLS_IN);
                final FileOutputStream outStream = new FileOutputStream(outFile)) {
            final Sheet sheetIn = XlsPoiTool.loadExcelFile(inStream);
            final XSSFWorkbook workbookOut = new XSSFWorkbook();
            final Sheet sheetOut = workbookOut.createSheet();
            final int lastRowNoIn = sheetIn.getLastRowNum();
            int rowCountOut = compareExcelWithDB(startTime, equipmentsHuawei, sheetIn, sheetOut, lastRowNoIn,
                    hwEqnPartsOhneAktivenAuftrag, huaweiSdslHwEqnsFuture.get());

            LOGGER.info(String.format("starting to write all Equipments that could not be found in Excel at %d",
                    (System.currentTimeMillis() - startTime)));

            rowCountOut = addEquipmentsNotFoundInInputToExcelOut(equipmentsHuawei, sheetOut, rowCountOut);

            LOGGER.info(String.format("starting to write data to result file at %d ms",
                    (System.currentTimeMillis() - startTime)));

            workbookOut.write(outStream);

            LOGGER.info(String.format("finished at %d ms",
                    (System.currentTimeMillis() - startTime)));
        }
    }

    private int addEquipmentsNotFoundInInputToExcelOut(
            final Map<String, Map<String, Pair<Equipment, List<EQCrossConnection>>>> equipmentsHuawei,
            final Sheet sheetOut, int rowCountOut) {
        for (final Map<String, Pair<Equipment, List<EQCrossConnection>>> equipmentsByHwEqnPart : equipmentsHuawei
                .values()) {
            for (Pair<Equipment, List<EQCrossConnection>> equipmentWithCCs : equipmentsByHwEqnPart.values()) {
                EQCrossConnection ccHsi = null;
                for (EQCrossConnection crossConnection : equipmentWithCCs.getSecond()) {
                    if (crossConnection.isHsi()) {
                        ccHsi = crossConnection;
                        break;
                    }
                }
                rowCountOut = addRowToSheet(sheetOut, rowCountOut, null, equipmentWithCCs.getFirst(), ccHsi);
            }
        }
        return rowCountOut;
    }

    private int compareExcelWithDB(final long startTime,
            final Map<String, Map<String, Pair<Equipment, List<EQCrossConnection>>>> equipmentsHuawei,
            final Sheet sheetIn, final Sheet sheetOut, final int lastRowNoIn,
            final Set<Pair<String, String>> hwEqnPartsOhneAktivenAuftrag, final Set<Pair<String, String>> hwEqnPartsSdsl)
            throws InterruptedException {
        int rowCountOut = 0;
        LOGGER.info(String.format("starting to compare Excel with DB after %d ms", (System.currentTimeMillis()
                - startTime)));
        for (int rowCountIn = 1; rowCountIn <= lastRowNoIn; rowCountIn++) {
            try {
                LOGGER.info(String.format("started to process row number %d", rowCountIn));
                rowCountOut = compareExcelRowWithDB(equipmentsHuawei, sheetIn, sheetOut, rowCountOut, rowCountIn,
                        hwEqnPartsOhneAktivenAuftrag, hwEqnPartsSdsl);
            }
            catch (FindException e) {
                throw new RuntimeException(e);
            }
        }

        return rowCountOut;
    }

    private void deleteAndRecreateFile(final File outFile) throws IOException {
        if (outFile.exists()) {
            outFile.delete();
        }
        outFile.createNewFile();
    }

    private List<HWDslam> findDslamsHuawei() {
        final HWDslam dslamExample = new HWDslam();
        dslamExample.setHwProducer(HVTTechnik.HUAWEI);
        return hardwareDAO.queryByExample(dslamExample, HWDslam.class);
    }

    private Map<HWBaugruppe, String> findBaugruppenHuawei(List<HWDslam> dslamsHuawei) {
        Map<HWBaugruppe, String> baugruppenHuawei = Maps.newHashMap();
        for (final HWDslam dslam : dslamsHuawei) {
            final HWBaugruppe baugruppeExample = new HWBaugruppe();
            baugruppeExample.setRackId(dslam.getId());
            final List<HWBaugruppe> baugruppen4Dslam =
                    hardwareDAO.queryByExample(baugruppeExample, HWBaugruppe.class);
            for (HWBaugruppe hwBaugruppe : baugruppen4Dslam) {
                baugruppenHuawei.put(hwBaugruppe, dslam.getManagementBez());
            }
        }
        return baugruppenHuawei;
    }

    private Map<String, Map<String, Pair<Equipment, List<EQCrossConnection>>>> findEquipmentsHuaweiAndCalculateCrossConnections(
            final Map<HWBaugruppe, String> baugruppenHuawei,
            final Set<Pair<String, String>> hwEqnPartsOhneAktivenAuftrag)
            throws Exception {
        final Map<String, Map<String, Pair<Equipment, List<EQCrossConnection>>>> equipmentsHuawei = Maps.newHashMap();

        final File outFile = new File(PATH_TO_CC_OUT);
        deleteAndRecreateFile(outFile);

        try (final FileWriter writer = new FileWriter(outFile)) {
            for (final Entry<HWBaugruppe, String> baugruppeWithManagementbez : baugruppenHuawei.entrySet()) {
                final List<Equipment> equipments4Baugruppe = findAdslOutEquipmentsWithHwEqnNotNullForBaugruppe(baugruppeWithManagementbez);
                for (Equipment eq : equipments4Baugruppe) {
                    final String hwEqnPart = eq.getHwEQNPart(Equipment.HWEQNPART_DSLAM_SLOT_AND_PORT);
                    final Rangierung rangierung = findRangierung4Equipment(eq);
                    final String managementBez = baugruppeWithManagementbez.getValue();
                    if (rangierung != null) {
                        final Endstelle endstelle = endstellenService.findEndstelle(rangierung.getEsId());
                        if (endstelle != null) {
                            final AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByEndstelleTx(endstelle
                                    .getId());
                            if (isAuftragActive(auftragDaten)) {
                                try {
                                    final List<EQCrossConnection> crossConnections4Auftrag = eqCrossConnectionService
                                            .calculateDefaultCcs(eq, "migration", ACTIVATION_DATE,
                                                    auftragDaten.getAuftragId());
                                    eqCrossConnectionService.deleteEQCrossConnectionsOfEquipment(eq.getId());
                                    eqCrossConnectionService.saveEQCrossConnections(crossConnections4Auftrag);

                                    writeCrossConnections4AuftragToCsv(writer, eq, managementBez,
                                            crossConnections4Auftrag, auftragDaten.getAuftragNoOrig());

                                    Map<String, Pair<Equipment, List<EQCrossConnection>>> equipmentByHwEqnPart = equipmentsHuawei
                                            .get(managementBez);
                                    if (equipmentByHwEqnPart == null) {
                                        equipmentByHwEqnPart = Maps.newHashMap();
                                        equipmentsHuawei.put(managementBez, equipmentByHwEqnPart);
                                    }
                                    if (hwEqnPart != null && equipmentByHwEqnPart.get(hwEqnPart) != null) {
                                        throw new IllegalStateException(
                                                String.format(
                                                        "found more than one Equipments for Rack with managementBez %s and hwEqnPart %s !",
                                                        managementBez, hwEqnPart)
                                        );
                                    }
                                    equipmentByHwEqnPart.put(hwEqnPart, Pair.create(eq, crossConnections4Auftrag));
                                }
                                catch (FindException e) {
                                    LOGGER.error(String
                                            .format("IllegalStateException while calculating CrossConnections for Auftrag with id %s",
                                                    auftragDaten.getAuftragId()), e);
                                }
                            }
                            else if (hwEqnPart != null) {
                                hwEqnPartsOhneAktivenAuftrag.add(Pair.create(managementBez, hwEqnPart));
                            }
                        }
                        else if (hwEqnPart != null) {
                            hwEqnPartsOhneAktivenAuftrag.add(Pair.create(managementBez, hwEqnPart));
                        }
                    }
                    else if (hwEqnPart != null) {
                        hwEqnPartsOhneAktivenAuftrag.add(Pair.create(managementBez, hwEqnPart));
                    }
                }
            }
        }
        return equipmentsHuawei;
    }

    private void writeCrossConnections4AuftragToCsv(final FileWriter writer, Equipment eq, final String managementBez,
            final List<EQCrossConnection> crossConnections4Auftrag, final Long billingOrderNo) throws IOException {
        for (EQCrossConnection crossConnection : crossConnections4Auftrag) {
            final StringBuilder csvLine = new StringBuilder()
                    .append(billingOrderNo).append(",")
                    .append(managementBez).append(",")
                    .append(eq.getHwEQNPart(Equipment.HWEQNPART_DSLAM_SLOT)).append(",")
                    .append(eq.getHwEQNPart(Equipment.HWEQNPART_DSLAM_PORT)).append(",")
                    .append(crossConnection.getLtInner()).append(",")
                    .append(crossConnection.getLtOuter()).append(",")
                    .append(crossConnection.getNtInner()).append(",")
                    .append(crossConnection.getNtOuter()).append(",")
                    .append(crossConnection.getBrasInner()).append(",")
                    .append(crossConnection.getBrasOuter()).append(",")
                    .append(crossConnection.getBrasPoolId()).append(",");

            if (crossConnection.getCrossConnectionTypeRefId().equals(
                    EQCrossConnection.REF_ID_XCONN_CPE_MGM_RB)) {
                csvLine.append("CPE");
            }
            else if (crossConnection.getCrossConnectionTypeRefId().equals(
                    EQCrossConnection.REF_ID_XCONN_HSI_XCONN)) {
                csvLine.append("HSI");
            }
            else if (crossConnection.getCrossConnectionTypeRefId().equals(
                    EQCrossConnection.REF_ID_XCONN_IAD_MGM_RB)) {
                csvLine.append("IAD");
            }
            else if (crossConnection.getCrossConnectionTypeRefId().equals(
                    EQCrossConnection.REF_ID_XCONN_QSC_HSI)) {
                csvLine.append("QSC_HSI");
            }
            else if (crossConnection.getCrossConnectionTypeRefId().equals(
                    EQCrossConnection.REF_ID_XCONN_QSC_MGMT)) {
                csvLine.append("QSC_MNGMT");
            }
            else if (crossConnection.getCrossConnectionTypeRefId().equals(
                    EQCrossConnection.REF_ID_XCONN_UNKNOWN_XCONN)) {
                csvLine.append("UNKNOWN");
            }
            else if (crossConnection.getCrossConnectionTypeRefId().equals(
                    EQCrossConnection.REF_ID_XCONN_VOIP_XCONN)) {
                csvLine.append("VOIP");
            }
            csvLine.append("\n");
            writer.write(csvLine.toString());
        }
    }

    private boolean isAuftragActive(final AuftragDaten auftragDaten) {
        return auftragDaten != null && auftragDaten.isAuftragActive()
                && DateTools.isHurricanEndDate(auftragDaten.getGueltigBis());
    }

    @SuppressWarnings("unchecked")
    private List<Equipment> findAdslOutEquipmentsWithHwEqnNotNullForBaugruppe(
            final Entry<HWBaugruppe, String> baugruppeWithManagementbez) throws FindException {
        final DetachedCriteria criteria = DetachedCriteria.forClass(Equipment.class)
                .add(Property.forName(Equipment.HW_BAUGRUPPEN_ID).eq(baugruppeWithManagementbez.getKey().getId()))
                .add(Property.forName(Equipment.HW_EQN).isNotNull())
                .add(Property.forName(Equipment.HW_SCHNITTSTELLE).eq(Equipment.HW_SCHNITTSTELLE_ADSL_OUT))
                .setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);

        Session session = sessionFactory.getCurrentSession();
        return (List<Equipment>) criteria.getExecutableCriteria(session).list();
    }

    private Future<Set<Pair<String, String>>> findHwEqnPartsOfAllSdslOutEquipments() {
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        return executor.submit(new Callable<Set<Pair<String, String>>>() {
            @Override
            public Set<Pair<String, String>> call() throws Exception {
                final String hql = new StringBuilder("select e, d from Equipment e, HWBaugruppe b, HWDslam d ")
                        .append("where e.hwBaugruppenId = b.id ")
                        .append("and b.rackId = d.id ")
                        .append("and e.hwEQN is not null ")
                        .append("and e.hwSchnittstelle = ? ")
                        .append("and d.hwProducer = ?")
                        .toString();

                @SuppressWarnings("unchecked")
                final List<Object[]> results = ((Hibernate4DAOImpl) equipmentDAO).find(hql,
                        Equipment.HW_SCHNITTSTELLE_SDSL_OUT, HVTTechnik.HUAWEI);
                final Set<Pair<String, String>> hwEqnParts = Sets.newHashSet();
                for (Object[] result : results) {
                    final Equipment equipment = (Equipment) result[0];
                    final HWDslam dslam = (HWDslam) result[1];
                    final String hwEqnPart = equipment.getHwEQNPart(Equipment.HWEQNPART_DSLAM_SLOT_AND_PORT);
                    final String managementBez = dslam.getManagementBez();
                    hwEqnParts.add(Pair.create(managementBez, hwEqnPart));
                }
                return hwEqnParts;
            }
        });
    }

    private Rangierung findRangierung4Equipment(final Equipment eq) {
        final DetachedCriteria criteria = createRangierungCriteria().add(
                Restrictions.eq(Rangierung.EQ_IN_ID, eq.getId()));

        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        final List<Rangierung> rangierungen = (List<Rangierung>) criteria.getExecutableCriteria(session).list();

        Rangierung actualRangierung = null;
        if (!rangierungen.isEmpty()) {
            if (rangierungen.size() == 1) {
                actualRangierung = rangierungen.get(0);
            }
            else {
                for (final Rangierung rangierung : rangierungen) {
                    if (rangierung.getHistoryFrom() != null) {
                        if (actualRangierung == null || rangierung.getId() > actualRangierung.getId()) {
                            actualRangierung = rangierung;
                        }
                    }
                }
                if (actualRangierung == null) {
                    throw new RuntimeException("EqId %s ist mehreren Rangierungen als EQ_IN_ID zugeordnet!");
                }
            }
        }

        if (actualRangierung == null) {
            final DetachedCriteria criteria4NextTry = createRangierungCriteria().add(
                    Restrictions.eq(Rangierung.EQ_OUT_ID, eq.getId()));

            @SuppressWarnings("unchecked")
            final List<Rangierung> rangierungen4NextTry = (List<Rangierung>) criteria4NextTry.getExecutableCriteria(session).list();
            actualRangierung = !rangierungen4NextTry.isEmpty() ? Iterables.getOnlyElement(rangierungen4NextTry) : null;
        }

        return actualRangierung;
    }

    private DetachedCriteria createRangierungCriteria() {
        return DetachedCriteria.forClass(Rangierung.class)
                .add(Restrictions.eq(Rangierung.GUELTIG_BIS, DateTools.getHurricanEndDate()))
                .add(Restrictions.ne("freigegeben", Freigegeben.freigegeben))
                .add(Restrictions.isNotNull("esId"))
                .add(Restrictions.ne("esId", -1L));
    }

    private int compareExcelRowWithDB(
            final Map<String, Map<String, Pair<Equipment, List<EQCrossConnection>>>> equipmentsHuawei,
            final Sheet sheetIn, final Sheet sheetOut, final int rowNrOut, final int rowCountIn,
            final Set<Pair<String, String>> hwEqnPartsOhneAktivenAuftrag, final Set<Pair<String, String>> hwEqnPartsSdsl)
            throws FindException {
        final Row currentRowIn = sheetIn.getRow(rowCountIn);
        final String managementBez = currentRowIn.getCell(COL_MANAGEMENTBEZ_IN).getStringCellValue().trim();
        final int slot = (int) currentRowIn.getCell(COL_SLOT_IN).getNumericCellValue();
        final int port = (int) currentRowIn.getCell(COL_PORT_IN).getNumericCellValue();
        final int ltOuterOld = (int) currentRowIn.getCell(COL_VPI_IN).getNumericCellValue();
        final int ltInnerOld = (int) currentRowIn.getCell(COL_VCI_IN).getNumericCellValue();
        final int ntOuterOld =
                // Integer.valueOf(
                // currentRowIn.getCell(COL_VLANID_IN).getStringCellValue().split(": ")[1].trim()).intValue();
                (int) currentRowIn.getCell(COL_VLANID_IN).getNumericCellValue();
        final int ntInnerOld = (int) currentRowIn.getCell(COL_INNERVLANID_IN).getNumericCellValue();
        final InputValuesFromExcelParam inputFromExcel = new InputValuesFromExcelParam();
        inputFromExcel.managementBez = managementBez;
        inputFromExcel.shelf = String.valueOf(currentRowIn.getCell(COL_SHELF_IN).getNumericCellValue());
        inputFromExcel.slot = String.valueOf(slot);
        inputFromExcel.port = String.valueOf(port);
        inputFromExcel.ltOuterOld = NumberTools.convertToString(ltOuterOld, "");
        inputFromExcel.ltInnerOld = NumberTools.convertToString(ltInnerOld, "");
        inputFromExcel.ntOuterOld = NumberTools.convertToString(ntOuterOld, "");
        inputFromExcel.ntInnerOld = NumberTools.convertToString(ntInnerOld, "");

        final Map<String, Pair<Equipment, List<EQCrossConnection>>> equipmentsByHwEqn = equipmentsHuawei
                .get(managementBez);

        int rowNrOutToReturn = rowNrOut;
        if (equipmentsByHwEqn != null) {
            final String hwEqnOhneSchrankOhneEinbauplatz = createHwEqnPart(inputFromExcel.slot, inputFromExcel.port);
            final Pair<Equipment, List<EQCrossConnection>> eqWithCrossConnections = equipmentsByHwEqn
                    .get(hwEqnOhneSchrankOhneEinbauplatz);
            EQCrossConnection crossConnectionHSI = null;
            final Pair<String, String> mngmtBezAndHwEqnPart = Pair
                    .create(managementBez, hwEqnOhneSchrankOhneEinbauplatz);
            if (eqWithCrossConnections != null) {
                for (final EQCrossConnection crossConnection : eqWithCrossConnections.getSecond()) {
                    if (crossConnection.isHsi()) {
                        crossConnectionHSI = crossConnection;
                        if ((!NumberTools.equal(crossConnection.getLtInner(), ltInnerOld)) ||
                                (!NumberTools.equal(crossConnection.getLtOuter(), ltOuterOld)) ||
                                (!NumberTools.equal(crossConnection.getNtInner(), ntInnerOld)) ||
                                (!NumberTools.equal(crossConnection.getNtOuter(), ntOuterOld))) {
                            rowNrOutToReturn = addRowToSheet(sheetOut, rowNrOut, inputFromExcel,
                                    eqWithCrossConnections.getFirst(),
                                    crossConnectionHSI);
                        }
                        break;
                    }
                }
                equipmentsByHwEqn.remove(hwEqnOhneSchrankOhneEinbauplatz);
            }
            else if (!hwEqnPartsOhneAktivenAuftrag.contains(mngmtBezAndHwEqnPart)
                    && !hwEqnPartsSdsl.contains(mngmtBezAndHwEqnPart)) {
                rowNrOutToReturn = addRowToSheet(sheetOut, rowNrOut, inputFromExcel, null, null);
            }
        }
        else {
            // HWRack nicht gefunden!
            rowNrOutToReturn = addRowToSheet(sheetOut, rowNrOut, inputFromExcel, null, null);
            LOGGER.warn(String.format("could not find rack with Managementbez. %s", managementBez));
        }
        return rowNrOutToReturn;
    }

    private String createHwEqnPart(final String slot, final String port) {
        final StringBuilder hwEqnOhneSchrankOhneEinbauplatzBuilder = new StringBuilder();
        if (slot.length() == 1) {
            hwEqnOhneSchrankOhneEinbauplatzBuilder.append("00");
        }
        else if (slot.length() == 2) {
            hwEqnOhneSchrankOhneEinbauplatzBuilder.append("0");
        }
        hwEqnOhneSchrankOhneEinbauplatzBuilder.append(slot).append("-")
                .append((port.length() == 1) ? "0" + port : port);
        return hwEqnOhneSchrankOhneEinbauplatzBuilder.toString();
    }

    // private Integer getIntFromExcel(final Row currentRowIn, final int colNr) {
    // final String asString = currentRowIn.getCell(colNr).getStringCellValue().trim();
    // return asString.isEmpty() ? null : Integer.valueOf(asString);
    // }

    private int addRowToSheet(final Sheet sheetOut, final int rowNrOut, final InputValuesFromExcelParam fromExcel,
            final Equipment equipment, final EQCrossConnection crossConnection) {
        int currentRow = rowNrOut;
        if (fromExcel != null || equipment != null) {
            final Row currentRowOut = sheetOut.createRow(currentRow++);
            if (equipment != null) {
                XlsPoiTool.setContent(currentRowOut, COL_OUT.EQID.ordinal(), equipment.getId()
                        .toString());
            }
            if (fromExcel != null) {
                XlsPoiTool.setContent(currentRowOut, COL_OUT.MANAGEMENTBEZ.ordinal(), fromExcel.managementBez);
                XlsPoiTool.setContent(currentRowOut, COL_OUT.SHELF.ordinal(), fromExcel.shelf);
                XlsPoiTool.setContent(currentRowOut, COL_OUT.SLOT.ordinal(), fromExcel.slot);
                XlsPoiTool.setContent(currentRowOut, COL_OUT.PORT.ordinal(), fromExcel.port);
                XlsPoiTool.setContent(currentRowOut, COL_OUT.VPI_OLD.ordinal(),
                        fromExcel.ltOuterOld.toString());
                XlsPoiTool.setContent(currentRowOut, COL_OUT.VCI_OLD.ordinal(),
                        fromExcel.ltInnerOld.toString());
                XlsPoiTool.setContent(currentRowOut, COL_OUT.VLANID_OLD.ordinal(),
                        fromExcel.ntOuterOld.toString());
                XlsPoiTool.setContent(currentRowOut, COL_OUT.INNERVLANID_OLD.ordinal(),
                        fromExcel.ntInnerOld.toString());
            }
            if (crossConnection != null) {
                XlsPoiTool.setContent(currentRowOut, COL_OUT.VPI_NEW.ordinal(), crossConnection
                        .getLtOuter().toString());
                XlsPoiTool.setContent(currentRowOut, COL_OUT.VCI_NEW.ordinal(), crossConnection
                        .getLtInner().toString());
                XlsPoiTool.setContent(currentRowOut, COL_OUT.VLANID_NEW.ordinal(), crossConnection
                        .getNtOuter().toString());
                XlsPoiTool.setContent(currentRowOut, COL_OUT.INNERVLANID_NEW.ordinal(),
                        crossConnection.getNtInner().toString());
            }
        }
        return currentRow;
    }

    private static class InputValuesFromExcelParam {
        public String managementBez = "";
        public String shelf = "";
        public String slot = "";
        public String port = "";
        public String ltOuterOld = "";
        public String ltInnerOld = "";
        public String ntOuterOld = "";
        public String ntInnerOld = "";
    }
}
