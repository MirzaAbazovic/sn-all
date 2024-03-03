/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 16:51:18
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import javax.annotation.*;
import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys.ApplicationKey;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import com.google.common.collect.ImmutableList;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlTokenSource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Node;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryEntryData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryGeneralParamsData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryIdData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.query.CPSQueryInstanceData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command-Klasse, welche die attainable Bitrate fuer einen gegebenen DSLAM Port ermittelt (synchroner CPS WS Call).
 * Liefert ein Pair<Integer, Integer> mit First=Downstream, Second=Upstream oder null falls keine Werte ermittelt werden
 * konnten
 */
public class CPSQueryAttainableBitrateCommand extends AbstractCPSCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSQueryAttainableBitrateCommand.class);

    /**
     * Key fuer die Uebergabe der zugehoerigen techn. Auftrags ID an das Command.
     */
    public static final String KEY_AUFTRAG_ID = "key.auftrag.id";
    /**
     * Key fuer die Uebergabe des zugehoerigen users an das Command.
     */
    public static final String KEY_USER_NAME = "key.user.name";

    public static final String BLANK = " ";

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;

    private HWRack hwRack;
    private String userName;
    private Equipment equipment;
    private Long ccAuftragId;
    private HWBaugruppe hwBaugruppe;
    private AuftragDaten auftragDaten;
    private CPSTransaction cpsQueryTx;

    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        try {
            checkValues();
            loadDefaultData();
            createTx();
            return executeAttainableBitrateQuery();
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler im Query Attainable Bitrate Command: " + e.getMessage(), e);
        }
    }

    /**
     * Sendet ein CPS-Query, um die Attainable Bitrates (Down/Up) zu erhalten.
     *
     * @return Pair mit den Attainable-Bitrates. (First=Downstream, Second=Upstream) oder null falls keine (sinnvollen)
     * Daten ermittelt werden konnten
     */
    Pair<Integer, Integer> executeAttainableBitrateQuery() throws FindException, ServiceNotFoundException, XmlException,
            ServiceCommandException {
        CPSQueryEntryData queryData = sendQuery();
        Pair<Integer, Integer> result = null;
        if (queryData != null && queryData.hasReasonableValues()) {
            // @formatter:off
            result = new Pair<>(
                    Integer.valueOf(queryData.getMaxAttBrDn()),
                    Integer.valueOf(queryData.getMaxAttBrUp()));
            // @formatter:on
        }
        return result;
    }

    /**
     * Extrahiert die SOServiceResponse (CData mit kompletten XML Baum, also XML in XML) und baut die CPSQueryData ueber
     * XStream.
     */
    private CPSQueryData parseServiceResponse2(ServiceResponse2 serviceResp)
            throws HurricanServiceCommandException {
        try {
            // SOData aus ServiceResponse auslesen
            Node node = serviceResp.getSOResponseData().getDomNode().getFirstChild();
            XStream xstream = ((XStreamMarshaller) getXmlMarshaller()).getXStream();
            return (CPSQueryData) xstream.fromXML(node.getNodeValue());
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Baut, und verschickt Query. Extrahiert Response Payload und delegiert das Parsen der SOServiceResponse
     */
    CPSQueryEntryData sendQuery() throws FindException, ServiceNotFoundException, XmlException, ServiceCommandException {
        boolean successful = false;
        try {
            ApplicationKey appKeyTaifun = createCPSApplicationKey(CPSTransaction.CPS_APPLICATION_KEY_TAIFUNNUMBER,
                    getAuftragDaten().getAuftragNoOrig().toString());
            List<ApplicationKey> appKeys = ImmutableList.of(appKeyTaifun);

            ServiceRequest serviceRequest = buildRequestPayload(cpsQueryTx, createCPSApplicationKeys(appKeys));
            WebServiceTemplate wsTemplate = configureAndGetWSTemplate(WebServiceConfig.WS_CFG_CPS_SOURCE_AGENT_SYNC);
            Object responsePayLoad = wsTemplate.marshalSendAndReceive(serviceRequest);

            XmlTokenSource xmlObject = (XmlTokenSource) responsePayLoad;
            XmlCursor cursor = xmlObject.newCursor();
            cursor.toFirstChild();
            XmlTokenSource o = cursor.getObject();

            ServiceResponse serviceResponse = ServiceResponse.Factory.parse(o.xmlText());
            CPSQueryData soData = null;
            if (serviceResponse != null) {
                ServiceResponse2 serviceResponse2 = serviceResponse.getServiceResponse();
                if ((serviceResponse2 != null) && (serviceResponse2.getSOResponseData() != null)) {
                    // SOResponseData in CPS-Tx eintragen
                    cpsQueryTx.setResponseData(serviceResponse2.toString().getBytes(StringTools.CC_DEFAULT_CHARSET));
                    saveQueryCPSTx();

                    // Result analysieren und parsen
                    if (serviceResponse2.getSOResult() == SERVICE_RESPONSE_SORESULT_CODE_SUCCESS) {
                        successful = true;
                        soData = parseServiceResponse2(serviceResponse2);
                    }
                }
            }
            return ((soData != null) && (soData.getInstance() != null)) ? soData.getInstance().getEntry() : null;
        }
        finally {
            cpsQueryTx.setTxState((successful) ? CPSTransaction.TX_STATE_SUCCESS : CPSTransaction.TX_STATE_FAILURE_CLOSED);
            saveQueryCPSTx();
        }
    }

    /**
     * Speichert die Query (lediglich nötig, da die TX ID gebraucht wird)
     */
    private void saveQueryCPSTx() throws ServiceCommandException {
        try {
            cpsService.saveCPSTransaction(cpsQueryTx, getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(String.format(
                    "Speichern der Attainable Bitrate Query für technischen "
                            + "Auftrag %d fehlgeschlagen!", ccAuftragId
            ), e);
        }
    }

    /**
     * Erstellt die Query, setzt TX Parameter und baut die SO Data
     */
    private void createTx() throws ServiceCommandException {
        this.cpsQueryTx = new CPSTransaction();
        this.cpsQueryTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_QUERY_HARDWARE);
        this.cpsQueryTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING);
        this.cpsQueryTx.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_ORDER);
        this.cpsQueryTx.setEstimatedExecTime(new Date());
        this.cpsQueryTx.setOrderNoOrig(getAuftragDaten().getAuftragNoOrig());
        this.cpsQueryTx.setAuftragId(getAuftragDaten().getAuftragId());
        this.cpsQueryTx.setServiceOrderPrio(CPSTransaction.SERVICE_ORDER_PRIO_HIGH);

        CPSQueryData soData = new CPSQueryData();
        loadGeneralParamsData(soData.getGeneralParams());
        loadInstanceData(soData.getInstance());
        String soDataAsXMLString = transformSOData2XML(soData, (XStreamMarshaller) getXmlMarshaller());

        if (StringUtils.isNotBlank(soDataAsXMLString)) {
            cpsQueryTx.setServiceOrderData(soDataAsXMLString.getBytes(StringTools.CC_DEFAULT_CHARSET));
        }
        else {
            throw new HurricanServiceCommandException(String.format("CPS Query Attainable Bitrate abgebrochen, da das Marshalling "
                    + "für den Auftrag %d nicht durchgeführt werden konnte!", ccAuftragId));
        }
        saveQueryCPSTx();
    }

    /**
     * 'GeneralParams' mit Daten versehen
     */
    void loadGeneralParamsData(CPSQueryGeneralParamsData generalParams) {
        generalParams.setTaifunNumber(getAuftragDaten().getAuftragNoOrig());
        generalParams.setUserName(getUserName());
    }

    /**
     * 'Instance' mit Daten versehen bzw. Aufgabe delegieren
     */
    private void loadInstanceData(CPSQueryInstanceData instance) {
        loadIdData(instance.getId());
        loadEntryData(instance.getEntry());
    }

    /**
     * 'ID' mit Daten versehen. Abhaengig ob es sich um Alcatel Hardware handelt oder nicht werden die
     * Rack/Shelf/Slot/Port Parameter unterschiedlich berechnet.
     */
    void loadIdData(CPSQueryIdData id) {
        HWBaugruppenTyp hwBaugruppenTyp = getHwBaugruppe().getHwBaugruppenTyp();
        HVTTechnik hvtTechnik = hwBaugruppenTyp.getHvtTechnik();
        id.setDslamName(getHwRack().getGeraeteBez());
        id.setCardType(hwBaugruppenTyp.getName());
        id.setDslamType(hvtTechnik);
        if (NumberTools.equal(hvtTechnik.getId(), HVTTechnik.ALCATEL)) {
            id.setRack(Integer.valueOf(getEquipment().getHwEQNPart(Equipment.HWEQNPART_DSLAM_RACK_ALCATEL)));
            id.setShelf(Integer.valueOf(getEquipment().getHwEQNPart(Equipment.HWEQNPART_DSLAM_SHELF_ALCATEL)));
            id.setPort(Integer.valueOf(getEquipment().getHwEQNPart(Equipment.HWEQNPART_DSLAM_PORT_ALCATEL)));
            id.setSlot(Integer.valueOf(getEquipment().getHwEQNPart(Equipment.HWEQNPART_DSLAM_SLOT_ALCATEL)));
        }
        else {
            id.setRack(0);
            id.setShelf(0);
            id.setPort(Integer.valueOf(getEquipment().getHwEQNPart(Equipment.HWEQNPART_DSLAM_PORT)));
            id.setSlot(Integer.valueOf(getEquipment().getHwEQNPart(Equipment.HWEQNPART_DSLAM_SLOT)));
        }
    }

    /**
     * 'Entry' mit Daten versehen
     */
    void loadEntryData(CPSQueryEntryData entry) {
        entry.setMaxAttBrDn(BLANK);
        entry.setMaxAttBrUp(BLANK);
    }

    /**
     * Prepared Werte pruefen und laden. Werte muessen gesetzt sein!
     */
    void checkValues() throws FindException {
        ccAuftragId = getPreparedValue(KEY_AUFTRAG_ID, Long.class, false,
                "Techn. Auftrags ID muss angegeben sein!");
        setUserName(getPreparedValue(KEY_USER_NAME, String.class, false,
                "Benutzername muss angegeben sein!"));
        getPreparedValue(KEY_SESSION_ID, Long.class, false,
                "Session ID muss angegeben sein!");
    }

    /**
     * Ermittlung aller relevanten Hardware Entitaeten die fuer die Query Parameter benoetigt werden. Alle benoetigten
     * Werte muessen gesetzt sein, andernfalls fliegt eine Exception.
     */
    void loadDefaultData() throws ServiceCommandException {
        try {
            setAuftragDaten(getCcAuftragService().findAuftragDatenByAuftragIdTx(ccAuftragId));
            Rangierung[] rangierungen = getRangierungsService().findRangierungenTx(ccAuftragId, Endstelle.ENDSTELLEN_TYP_B);
            setEquipment(((rangierungen != null) && (rangierungen.length > 0)) ?
                    getRangierungsService().findEquipment(rangierungen[0].getEqInId()) : null);
            setHwBaugruppe(((getEquipment() != null) && (getEquipment().getHwBaugruppenId() != null)) ?
                    hwService.findBaugruppe(getEquipment().getHwBaugruppenId()) : null);
            setHwRack(((getHwBaugruppe() != null) && (getHwBaugruppe().getRackId() != null)) ?
                    hwService.findRackById(getHwBaugruppe().getRackId()) : null);
            if ((getAuftragDaten() == null) || (getEquipment() == null) || (getHwRack() == null)
                    || (getHwBaugruppe() == null) || (getHwBaugruppe().getHwBaugruppenTyp() == null)
                    || (getHwBaugruppe().getHwBaugruppenTyp().getHvtTechnik() == null)) {
                throw new HurricanServiceCommandException(String.format("CPS Query Attainable Bitrate abgebrochen, da zum technischen "
                        + "Auftrag %d keine oder nicht alle Daten ermittelt werden konnten!", ccAuftragId));
            }
            if (!StringUtils.equals(HWRack.RACK_TYPE_DSLAM, getHwRack().getRackTyp())) {
                throw new HurricanServiceCommandException(String.format("CPS Query Attainable Bitrate abgebrochen, da zum technischen "
                        + "Auftrag %d das Rack kein DSLAM ist!", ccAuftragId));
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(String.format("CPS Query Attainable Bitrate abgebrochen, da zum technischen "
                    + "Auftrag %d folgender Fehler gefangen wurde: %s", ccAuftragId, e.getMessage()), e);
        }
    }

    // Abschnitt mit Gettern und Settern
    private CCAuftragService getCcAuftragService() {
        return ccAuftragService;
    }

    /**
     * Injected
     */
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    private RangierungsService getRangierungsService() {
        return rangierungsService;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    private HWRack getHwRack() {
        return hwRack;
    }

    /**
     * Test Injected
     */
    public void setHwRack(HWRack hwRack) {
        this.hwRack = hwRack;
    }

    public HWBaugruppe getHwBaugruppe() {
        return hwBaugruppe;
    }

    /**
     * Test Injected
     */
    public void setHwBaugruppe(HWBaugruppe hwBaugruppe) {
        this.hwBaugruppe = hwBaugruppe;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    /**
     * Test Injected
     */
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public AuftragDaten getAuftragDaten() {
        return auftragDaten;
    }

    /**
     * Test Injected
     */
    public void setAuftragDaten(AuftragDaten auftragDaten) {
        this.auftragDaten = auftragDaten;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * Test Injected
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
