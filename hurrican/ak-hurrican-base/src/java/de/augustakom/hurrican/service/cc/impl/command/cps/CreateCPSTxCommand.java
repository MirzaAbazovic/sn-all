/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2009 15:05:54
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberRange;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xstream.XStreamMarshaller;

import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.HibernateSessionHelper;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.cps.CPSDataChainConfig;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSConfigService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Command-Klasse, um eine CPS-Transaction anzulegen. <br> Neben der Anlage des eigentlichen Header-Datensatzes der
 * CPS-Transaction ist das Command auch dafuer verantwortlich, die Provisionierungsdaten zu ermitteln und die notwendige
 * XML-Struktur aufzubauen. Dies geschieht ueber den Aufruf einer zugehoerigen ServiceChain (Zuordnung ueber Produkt und
 * ServiceOrder-Type). <br> Von den einzelnen Data-Commands wird als Result jeweils ein XML-Element erwartet, dass die
 * Struktur fuer die ServiceOrder-Data der CPS-Transaction aus dem jeweiligen Bereich (z.B. EWSD, DSL, Radius etc.)
 * darstellt. Diese XML-Elemente werden dann von diesem Command zusammengefuegt und evtl. um weitere Daten (z.B.
 * ProductCode) erweitert. <br> <br> Das Command gibt als Ergebnis ein Objekt vom Typ <code>ServiceCommandResult</code>
 * zurueck. Bei erfolgreichem Abschluss des Commands wird die generierte CPS-Transaction als sog. 'ResultObject' in das
 * ServiceCommandResult eingetragen.
 *
 *
 */
public class CreateCPSTxCommand extends AbstractCPSCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateCPSTxCommand.class);

    /**
     * Key fuer die Uebergabe der zugehoerigen CPS-Transaction an das Command.
     */
    public static final String KEY_CPS_TRANSACTION = "cps.transaction";
    /**
     * Key fuer die Uebergabe des Flags, das die CPS-Tx aus einem automatischen Job erzeugt wird.
     */
    public static final String KEY_AUTO_CREATION = "cps.auto.creation";
    /**
     * Key fuer die Uebergabe des Flags, welches die CPS-Tx zwingt die manuell ausgewaehlte Provisionierungsart
     * beizubehalten.
     */
    public static final String KEY_FORCE_EXEC_TYPE = "cps.force.execution.type";

    private CPSConfigService cpsConfigService;
    private ChainService chainService;
    private CCAuftragService ccAuftragService;
    private BAService baService;
    private NiederlassungService niederlassungService;
    //private PhysikService physikService;
    private EndstellenService endstellenService;

    private CPSTransaction cpsTx = null;
    private AuftragDaten auftragDaten = null;
    private Produkt produkt = null;
    private Niederlassung niederlassung = null;
    private VerbindungsBezeichnung vbzOfTechOrder = null;
    private Boolean autoCreation = null;
    private Boolean forceExecType = null;
    private Long geoid;

    @Autowired
    private ServiceLocator serviceLocator;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        boolean cpsTxSaved = false;
        try {
            init();

            // Default-Daten (z.B. Billing-Auftragsnummer) ermitteln und in CPS-Tx setzen
            loadDefaultData();

            // Lazy-Init fuer den Auftrag ausfuehren
            lazyInit();

            LOGGER.info("Create CPS-Tx for taifun order " + cpsTx.getOrderNoOrig());

            // CPS-Transaction bereits speichern, damit ID fuer Referenzen verwendet werden kann
            saveCPSTx();
            cpsTxSaved = true;

            // Sub-Orders laden u. protokollieren
            findAndLogSubOrders();

            // Hibernate Session flushen!
            HibernateSessionHelper.flushSession(serviceLocator);

            CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
            // ServiceOrder-Data ermitteln
            collectServiceOrderData(serviceOrderData);
            collectWholesaleProductData(serviceOrderData, cpsTx.getAuftragId(), cpsTx.getEstimatedExecTime());

            // ServiceOrder-Data in CPS-Tx eintragen und erneut speichern
            String soDataAsXMLString = transformSOData2XML(serviceOrderData, (XStreamMarshaller) getXmlMarshaller());
            if (StringUtils.isNotBlank(soDataAsXMLString)) {
                cpsTx.setServiceOrderData(soDataAsXMLString.getBytes(StringTools.CC_DEFAULT_CHARSET));
            }
            else {
                throw new HurricanServiceCommandException("ServiceOrder-Data not defined!");
            }

            saveCPSTx();

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                    null, this.getClass(), cpsTx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (cpsTxSaved) {
                try {
                    cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING_FAILURE);
                    saveCPSTx();

                    // Disjoin CPS-Transaction
                    cpsService.disjoinCPSTransaction(cpsTx.getId(), getSessionId());

                    createCPSTxLog(cpsTx, e.getMessage(), false);
                }
                catch (Exception disjoinEx) {
                    LOGGER.error(disjoinEx.getMessage(), disjoinEx);
                    createCPSTxLog(cpsTx, "Error disjoining CPS-Tx: " + ExceptionUtils.getFullStackTrace(disjoinEx));
                }
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), this.getClass());
        }
    }

    /**
     * Ermittelt die notwendigen Provisionierungsdaten anhand der Produkt/SO-Type Konfiguration. Die entsprechende
     * ServiceChain wird aufgerufen und die erhaltenen XML-Elemente zurueck gegeben.
     *
     * @param serviceOrderData Objekt, in das die einzelnen Commands die Provisionierungsdaten eintragen
     * @throws ServiceCommandException
     */
    private void collectServiceOrderData(CPSServiceOrderData serviceOrderData) throws ServiceCommandException {
        try {
            serviceOrderData.setExecDate(cpsTx.getEstimatedExecTime());
            serviceOrderData.setTaifunnumber(String.valueOf(cpsTx.getOrderNoOrig()));
            serviceOrderData.setVerbindungsbezeichnung(vbzOfTechOrder.getVbz());
            serviceOrderData.setNiederlassung(niederlassung.getCpsProvisioningName());
            serviceOrderData.setGeoid(geoid);
            serviceOrderData.setProductCode(StringUtils.deleteWhitespace(produkt.getCpsProductName()));
            serviceOrderData
                    .setInitialLoad((getLazyInitMode() != null) ? getLazyInitMode().getLazyInitModeCps() : null);
            serviceOrderData.setLockMode(getLockMode());

            Pair<String, Integer> onkzAsb = ccAuftragService.findOnkzAsb4Auftrag(cpsTx.getAuftragId());
            if (onkzAsb != null) {
                serviceOrderData.setOnkz(onkzAsb.getFirst());
                serviceOrderData.setAsb(onkzAsb.getSecond());
            }

            if (StringUtils.isBlank(serviceOrderData.getProductCode())) {
                throw new HurricanServiceCommandException("ProductCode is not defined! ProdID: " + produkt.getId());
            }

            // ServiceChain fuer Produkt/SO-Type ermitteln und ausfuehren.
            Long soType4Chain = cpsTx.getServiceOrderType();
            CPSDataChainConfig chainCfg = cpsConfigService.findCPSDataChainConfig(auftragDaten.getProdId(),
                    soType4Chain);
            // fallback auf MODIFY wenn fuer GET_SODATA keine Chain konfiguriert ist
            if (chainCfg == null && CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA.equals(soType4Chain)) {
                chainCfg = cpsConfigService.findCPSDataChainConfig(auftragDaten.getProdId(),
                        CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);
            }
            if (chainCfg == null) {
                throw new HurricanServiceCommandException("CPS DataChain-Config not found for ProdId/SO-Type: " +
                        auftragDaten.getProdId() + "/" + cpsTx.getServiceOrderType());
            }

            // ServiceChain und zugehoerige Commands ermitteln
            List<ServiceCommand> cmds = chainService.findServiceCommands4Reference(
                    chainCfg.getServiceChainId(), ServiceChain.class, null);

            if (CollectionTools.isEmpty(cmds) &&
                    NumberTools.notEqual(cpsTx.getServiceOrderType(), CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB)) {
                // bei Kuendigungen muessen keine Data-Commands definiert sein
                throw new HurricanServiceCommandException(
                        "No commands defined for service chain ID " + chainCfg.getServiceChainId());
            }

            AKServiceCommandChain chain = new AKServiceCommandChain();
            for (ServiceCommand cmd : cmds) {
                // Commands konfigurieren und der Chain zuordnen
                IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                if (serviceCmd == null) {
                    throw new FindException("Couldn´t load ServiceCommand " + cmd.getName());
                }

                serviceCmd.prepare(AbstractCPSDataCommand.KEY_SESSION_ID, getSessionId());
                serviceCmd.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
                serviceCmd.prepare(AbstractCPSDataCommand.KEY_AUFTRAG_DATEN, auftragDaten);
                serviceCmd.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
                serviceCmd.prepare(AbstractCPSDataCommand.KEY_LOCK_MODE, getLockMode());

                chain.addCommand(serviceCmd);
            }

            if (chain.hasCommands()) {
                // ServiceChain ausfuehren (Result-Status wird ueber Chain geprueft)
                chain.executeChain(true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error collecting the service order datas for tech order ID " + cpsTx.getAuftragId() + ": "
                            + e.getMessage(), e
            );
        }
    }

    /* Initialisiert das Command. */
    private void init() throws Exception {
        checkValues();
    }

    /* Speichert die aktuelle CPS-Transaction. */
    private void saveCPSTx() throws ServiceCommandException {
        try {
            cpsService.saveCPSTransaction(cpsTx, getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error saving CPS-Transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt bei Multi-Draht Auftraegen (z.B. TK-Anlagen, SDSL) alle (Hurrican) Auftraege, die zu dem aktuellen
     * Auftrag gehoeren (gebuendelt ueber Taifun-Auftrag). <br> Diese Auftraege werden zu der CPS-Transaction in einer
     * Sub-Tabelle protokolliert. Somit koennen bei der Datenermittlung die Daten von mehreren Auftraegen in einer
     * CPS-Tx angegeben werden. <br>
     */
    private void findAndLogSubOrders() throws ServiceCommandException {
        try {
            List<AuftragDaten> bundledOrders = ccAuftragService.findAuftragDaten4OrderNoOrig(cpsTx.getOrderNoOrig());
            if (bundledOrders == null) {
                bundledOrders = Collections.EMPTY_LIST;
            }
            Pair<Boolean, Boolean> isMultiDrahtPossible = isMultiDraht(bundledOrders);

            if (BooleanTools.nullToFalse(isMultiDrahtPossible.getFirst())) {
                List<CPSTransactionSubOrder> subOrders = new ArrayList<>();

                if (CollectionTools.isNotEmpty(bundledOrders)) {
                    for (AuftragDaten bundledOrder : bundledOrders) {
                        if (NumberTools.notEqual(cpsTx.getAuftragId(), bundledOrder.getAuftragId())) {
                            Verlauf actVerlauf = baService.findActVerlauf4Auftrag(bundledOrder.getAuftragId(), false);

                            boolean addBundledOrder = isOrderStateValid4CpsBundle(bundledOrder, actVerlauf,
                                    isMultiDrahtPossible.getSecond());
                            if (addBundledOrder) {
                                CPSTransactionSubOrder subOrder = new CPSTransactionSubOrder();
                                subOrder.setAuftragId(bundledOrder.getAuftragId());
                                subOrder.setCpsTxId(cpsTx.getId());
                                if ((actVerlauf != null)
                                        &&
                                        DateTools.isDateEqual(actVerlauf.getRealisierungstermin(),
                                                cpsTx.getEstimatedExecTime())) {
                                    subOrder.setVerlaufId(actVerlauf.getId());
                                }
                                subOrders.add(subOrder);
                            }
                            else {
                                createCPSTxLog(cpsTx,
                                        "State of tech order with ID " + bundledOrder.getAuftragId() +
                                                " is not in valid range.", false);
                            }
                        }
                    }
                }

                checkAndModifyCPSServiceOrderType(subOrders);
                saveSubOrders(subOrders);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error loading and logging sub-orders for tech order ID " + cpsTx.getAuftragId() + ": "
                            + e.getMessage(), e
            );
        }
    }

    /**
     * Prueft, ob der angegebene Auftrag als SubOrder in die aktuelle CPS-Tx mit aufgenommen werden muss.
     */
    boolean isOrderStateValid4CpsBundle(AuftragDaten bundledOrder, Verlauf actVerlauf, Boolean productFlaggedForNDraht) {
        boolean addBundledOrder = true;
        NumberRange stateRange = new NumberRange(AuftragStatus.TECHNISCHE_REALISIERUNG,
                AuftragStatus.KUENDIGUNG_TECHN_REAL);
        if (stateRange.containsInteger(bundledOrder.getStatusId())) {
            if (BooleanTools.nullToFalse(productFlaggedForNDraht)) {
                // ist das Produkt fuer n-Draht markiert, werden nur Auftraege mit gleicharten Stati beruecksichtigt
                if (cpsTx.isCancelSubscriber()) {
                    if (!bundledOrder.isInKuendigung()) {
                        addBundledOrder = false;
                    }
                }
                else {
                    if (auftragDaten.isInKuendigung() && bundledOrder.isAuftragActive()) {
                        addBundledOrder = false;
                    }
                    else if (!bundledOrder.isAuftragActive()) {
                        addBundledOrder = false;
                    }
                }
            }
            else if (cpsTx.isCancelSubscriber()
                    && actVerlauf != null
                    && DateTools.isDateAfter(cpsTx.getEstimatedExecTime(), actVerlauf.getRealisierungstermin())) {
                addBundledOrder = false;
            }
        }
        else if (LazyInitMode.isInitialLoad(getLazyInitMode())
                && bundledOrder.isCancelled()
                && DateTools.isDateEqual(bundledOrder.getKuendigung(), auftragDaten.getKuendigung())) {
            // falls InitialLoad=1 und Auftrag schon gekuendigt (und Kuendigungsdatum = Kuendigungsdatum von
            // Hauptauftrag)
            // --> addBundledOrder=true
            addBundledOrder = true;
        }
        else {
            addBundledOrder = false;
        }
        return addBundledOrder;
    }

    /**
     * Bei Kuendigungen wird geprueft, ob einer der Sub-Auftraege noch aktiv ist. Ist dies der Fall, wird der
     * ServiceOrderTyp auf 'modify' geaendert. Bei Neuschaltungen wird geprueft, ob einer der Auftraege bereits eine
     * erfolgreiche (abgeschlossene) CPS-Tx besitzt. Ist dies der Fall, wird der ServiceOrderTyp auf 'modify' gesetzt.
     */
    void checkAndModifyCPSServiceOrderType(List<CPSTransactionSubOrder> subOrders) throws FindException,
            ServiceCommandException {
        if (!BooleanTools.nullToFalse(forceExecType)) {
            if (cpsTx.isCancelSubscriber()
                    && CollectionTools.isNotEmpty(subOrders)) {
                for (CPSTransactionSubOrder subOrder : subOrders) {
                    AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(subOrder.getAuftragId());
                    if (auftragDaten.isAuftragActive()) {
                        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);
                        saveCPSTx();
                        break;
                    }
                }
            }
            else if (cpsTx.isCreateSubscriber()
                    && CollectionTools.isNotEmpty(subOrders)
                    && !LazyInitMode.isInitialLoad(getLazyInitMode())
                    && checkTxs4SubOrders(subOrders)) {
                cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB);
                saveCPSTx();
            }
        }
    }

    /**
     * Prüft alle Sub-Aufträge <lu> <li>Ist Auftrag in Betrieb</li> <li>Gibt es fuer den Auftrag bereits erfolgreich
     * abgeschlossene Transaktionen</li> <lu>
     */
    private boolean checkTxs4SubOrders(List<CPSTransactionSubOrder> subOrders)
            throws FindException, ServiceCommandException {
        if (CollectionTools.isEmpty(subOrders)) {
            return false;
        }

        for (CPSTransactionSubOrder subOrder : subOrders) {
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(subOrder.getAuftragId());
            if ((auftragDaten != null) && NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB)) {
                // Diese Liste enthält auch die neue bereits (vorsorglich) gespeicherte Transaktion!
                List<CPSTransaction> successfulCpsTx = cpsService.findSuccessfulCPSTransaction4TechOrder(auftragDaten
                        .getAuftragId());
                if (CollectionTools.isNotEmpty(successfulCpsTx)) {
                    // Prüft ob die Liste mindestens eine Transaktion enthält
                    // (die bereits vorsorglich gespeicherte neue Transaktion wird ignoriert)
                    for (CPSTransaction cpsTransaction : successfulCpsTx) {
                        if (!NumberTools.equal(cpsTransaction.getId(), cpsTx.getId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Speichert die Sub-Orders fuer die CPS-Transaction.
     *
     * @param subOrders
     * @throws ServiceCommandException
     */
    private void saveSubOrders(List<CPSTransactionSubOrder> subOrders) throws ServiceCommandException {
        try {
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (CPSTransactionSubOrder subOrder : subOrders) {
                    CPSProvisioningAllowed allowed = cpsService.isCPSProvisioningAllowed(
                            subOrder.getAuftragId(),
                            getLazyInitMode(),
                            BooleanTools.nullToFalse(autoCreation),
                            true,
                            true);

                    if (allowed.isProvisioningAllowed()) {
                        cpsService.saveCPSTransactionSubOrder(subOrder);
                    }
                    else {
                        throw new HurricanServiceCommandException(
                                "Existing Sub-Order is not allowed for provisioning! Base tech order ID: {0}; " +
                                        "Sub-Order ID: {1};\nReason: {2}",
                                new Object[] { String.format("%s", cpsTx.getAuftragId()),
                                        String.format("%s", subOrder.getAuftragId()),
                                        allowed.getNoCPSProvisioningReason() }
                        );
                    }
                }
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error saving sub-orders: " + e.getMessage(), e);
        }
    }

    /**
     * Ueberprueft, ob es sich bei dem aktuellen Auftrag um einen potentiellen Multi-Draht Auftrag handelt. Dies ist
     * dann der Fall, wenn dem Auftrag die techn. Leistung 'TK' zugeordnet ist oder das Produkt-Flag {@code
     * Produkt.cpsMultiDraht} gesetzt ist.
     *
     * @return Pair mit folgenden Werten: first: definiert, ob das Produkt n-Draht faehig ist second: definiert, ob auf
     * dem Produkt das CPS n-Draht Flag gesetzt ist
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    protected Pair<Boolean, Boolean> isMultiDraht(List<AuftragDaten> bundledOrders) throws ServiceNotFoundException,
            FindException {
        boolean isPossibleMultiDraht = false;
        boolean isProductFlagged = false;
        Produkt produkt = produktService.findProdukt4Auftrag(cpsTx.getAuftragId());
        if ((produkt != null) && BooleanTools.nullToFalse(produkt.getCpsMultiDraht())) {
            isPossibleMultiDraht = true;
            isProductFlagged = true;
        }
        else {
            List<Long> auftragIds = extractAuftragIds(bundledOrders);
            isPossibleMultiDraht = ccLeistungsService.isTechLeistungActive(
                    auftragIds, ExterneLeistung.ISDN_TYP_TK.leistungNo, cpsTx.getEstimatedExecTime());
        }
        return Pair.create(isPossibleMultiDraht, isProductFlagged);
    }

    private List<Long> extractAuftragIds(List<AuftragDaten> orders) {
        List<Long> auftragIds = new ArrayList<>();
        for (AuftragDaten ad : orders) {
            auftragIds.add(ad.getAuftragId());
        }
        return auftragIds;
    }

    /**
     * Laedt Default-Daten fuer die CPS-Transaction.
     */
    private void loadDefaultData() throws ServiceCommandException {
        try {
            setAuftragDaten(ccAuftragService.findAuftragDatenByAuftragIdTx(cpsTx.getAuftragId()));
            AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragId(cpsTx.getAuftragId());

            cpsTx.setOrderNoOrig(auftragDaten.getAuftragNoOrig());
            cpsTx.setRegion(auftragTechnik.getNiederlassungId());

            vbzOfTechOrder = physikService.findVerbindungsBezeichnungByAuftragId(cpsTx.getAuftragId());

            // Produkt ermitteln
            produkt = produktService.findProdukt4Auftrag(cpsTx.getAuftragId());
            if (produkt == null) {
                throw new HurricanServiceCommandException("Product not found!");
            }

            // Niederlassung ermitteln
            niederlassung = niederlassungService.findNiederlassung(auftragTechnik.getNiederlassungId());
            if (niederlassung == null) {
                throw new HurricanServiceCommandException(
                        "Niederlassung konnte nicht ermittelt werden. Niederlassungs-ID: "
                                + auftragTechnik.getNiederlassungId()
                );
            }

            final Endstelle endstelle = endstellenService.findEndstelle4Auftrag(cpsTx.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            geoid = (endstelle != null) ? endstelle.getGeoId() : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error loading data for CPS provisioning: " + e.getMessage(), e);
        }
    }

    /**
     * Ueberprueft die uebergebenen Daten.
     */
    private void checkValues() throws FindException {
        setCpsTx(getPreparedValue(KEY_CPS_TRANSACTION, CPSTransaction.class, false,
                "CPS-Transaction object is not defined for command!"));

        autoCreation = getPreparedValue(KEY_AUTO_CREATION, Boolean.class, true, null);
        forceExecType = getPreparedValue(KEY_FORCE_EXEC_TYPE, Boolean.class, true, null);
    }

    // @formatter:off
    /**
     * Fuehrt ein 'lazyInit' fuer den Auftrag durch. <br/>
     * Der 'lazyInit' wird in folgenden Faellen durchgefuehrt: <br/>
     * <ul>
     *     <li>SO-Type ist in (modifySubscriber, cancelSubscriber, lockSubscriber)
     *     <li>Auftrag besitzt keine erfolgreiche CPS-Transaction
     * </ul> <br/>
     * Der Init wird benoetigt, damit der CPS die zu provisionierende Differenz ermitteln kann. <br/>
     * Als Execution-Date wird immer (now - 1 Tag) angegeben. <br> Der 'lazyInit' wird per synchronem WS-Call an den
     * CPS uebergeben.
     */
    // @formatter:on
    private void lazyInit() throws ServiceCommandException {
        try {
            CPSTransactionResult lazyInitResult =
                    cpsService.doLazyInit(LazyInitMode.initialLoad, cpsTx.getAuftragId(), cpsTx, getSessionId());

            if (lazyInitResult != null) {
                List<CPSTransaction> lazyInitCPSTx = lazyInitResult.getCpsTransactions();
                if (CollectionTools.isEmpty(lazyInitCPSTx) || lazyInitCPSTx.get(0).isFailure()) {
                    String errors = (lazyInitResult.getWarnings() != null) ? lazyInitResult.getWarnings()
                            .getWarningsAsText() : "";
                    throw new HurricanServiceCommandException("CPS-Tx for Lazy-Init has errors: " + errors);
                }
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error during Lazy-Init for order: " + e.getMessage(), e);
        }
    }

    /**
     * Injected
     */
    public void setCpsConfigService(CPSConfigService cpsConfigService) {
        this.cpsConfigService = cpsConfigService;
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
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    /**
     * Injected
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    /**
     * Injected
     */
    public void setNiederlassungService(NiederlassungService niederlassungService) {
        this.niederlassungService = niederlassungService;
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
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    public void setCpsTx(CPSTransaction cpsTx) {
        this.cpsTx = cpsTx;
    }

    public void setAuftragDaten(AuftragDaten auftragDaten) {
        this.auftragDaten = auftragDaten;
    }

    /**
     * Diese Methode ist lediglich fuer Unit Tests implementiert. Der 'normale' Weg fuehrt ueber die 'prepare' Methode,
     * wie bei Commands ueblich. Die Sichtbarkeit 'protected' soll somit sicherstellen, dass nur die Tests aus dem
     * selben Package zugriff erhalten.
     */
    protected void setForceExecType(Boolean forceExecType) {
        this.forceExecType = forceExecType;
    }

    void setProdukt(final Produkt produkt) {
        this.produkt = produkt;
    }
}
