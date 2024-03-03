/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2009 14:41:01
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDSLData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;

/**
 * Abstrakte Klasse fuer alle CPS-Commands, die Daten fuer die Provisionierung (ServiceOrder-Data) ermitteln. <br> Es
 * ist vorgesehen, dass jedes Sub-Element (wie z.B. 'TELEPHONE', 'DSL' etc.) der ServiceOrder-Data Definition ueber ein
 * eigenes Command ermittelt wird. <br> <br> Jedes Data-Command ist dafuer verantwortlich, die relevanten Daten zu
 * ermitteln und in entsprechende Daten-Objekte (Ableitungen von <code>de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel</code>)
 * einzutragen. <br> Die erstellen Daten-Objekte muessen wiederum in das Objekt vom Typ <code>CPSServiceOrder</code>
 * eingetragen werden, das dem Command bereitgestellt wird. <br> <br> Der Caller der Daten-Commands (der auch das Objekt
 * vom Typ CPSServiceOrder bereitstellt) kann aus dieser Objekt-Struktur dann die notwendige XML-Struktur erstellen.
 * <br> <br> Command-Results: <br> Die Commands muessen als Result Objekte vom Typ ServiceCommandResult liefern. <br>
 * Ueber den Status in diesem Objekt kann definiert werden, ob die Datenermittlung funktioniert hat (Status
 * CHECK_STATUS_OK) oder einen Fehler verursacht hat (CHECK_STATUS_INVALID). <br> Falls bei der Datenermittlung nicht
 * entschieden werden kann, ob alles korrekt ablief, kann dies als Warnung ueber <code>CPSTransactionLog</code>
 * protokolliert werden.
 */
public abstract class AbstractCPSDataCommand extends AbstractCPSCommand {

    /**
     * Key fuer die Uebergabe der zugehoerigen CPS-Transaction an das Command.
     */
    public static final String KEY_CPS_TRANSACTION = "cps.transaction";
    /**
     * Key fuer die Uebergabe des AuftragDaten-Objekts des Auftrags, zu dem die Daten ermittelt werden sollen.
     */
    public static final String KEY_AUFTRAG_DATEN = "auftrag.daten";
    /**
     * Key fuer die Uebergabe des CPSServiceOrderData-Objekts, in dem die zu provisionierenden Daten eingetragen werden
     * und aus dem anschliessend die notwendige XML-Struktur generiert wird.
     */
    public static final String KEY_SERVICE_ORDER_DATA = "service.order.data";
    private static final Logger LOGGER = Logger.getLogger(AbstractCPSDataCommand.class);
    protected DSLAMService dslamService;
    protected KundenService kundenService;
    protected CCAuftragService ccAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    protected HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    protected EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    protected RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService")
    protected EkpFrameContractService ekpFrameContractService;


    public void init() throws ServiceNotFoundException {
        setKundenService(getBillingService(KundenService.class));
    }

    final protected boolean isFtthAuftrag(final long auftragId) throws FindException {
        final HWRack hwRack = findHwRack4FttxAuftrag(auftragId);
        return (hwRack instanceof HWOnt);
    }

    final protected boolean isFttbAuftrag(final long auftragId) throws FindException {
        final HWRack hwRack = findHwRack4FttxAuftrag(auftragId);
        return (hwRack instanceof HWMdu) || (hwRack instanceof HWDpo);
    }

    private HWRack findHwRack4FttxAuftrag(final long auftragId) throws FindException {
        final Endstelle esB =
                endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        final Equipment eqIn = (esB != null) ? rangierungsService.findEquipment4Endstelle(esB, false, false) : null;
        final HWRack hwRack = (eqIn != null && eqIn.getHwBaugruppenId() != null) ?
                hwService.findRackForBaugruppe(eqIn.getHwBaugruppenId()) : null;
        if (hwRack == null) {
            throw new FindException(String.format("Für den Auftrag %s konnte das Hardware Rack nicht ermittelt " +
                    "werden!", auftragId));
        }
        return hwRack;
    }

    /**
     * Prueft, ob dem Auftrag eine techn. Leistung vom Typ {@link TechLeistung#TYP_ENDGERAET} zugeordnet ist. (In diesem
     * Fall benoetigt der Auftrag ein in Taifun gebuchtes Endgeraet = {@link Device}).
     *
     * @param auftragId
     * @return
     * @throws FindException
     */
    final protected boolean hasAuftragTypEndgeraetLeistung(final long auftragId) throws FindException {
        return !ccLeistungsService.findTechLeistungen4Auftrag(auftragId, TechLeistung.TYP_ENDGERAET,
                getCPSTransaction().getEstimatedExecTime()).isEmpty();
    }

    /**
     * Gibt die CPS-Transaction zurueck.
     *
     * @return CPS-Transaction, fuer die die Daten ermittelt werden sollen.
     */
    protected CPSTransaction getCPSTransaction() {
        return (CPSTransaction) getPreparedValue(KEY_CPS_TRANSACTION);
    }

    /**
     * Gibt das ServiceOrder-Data Objekt zurueck, in das die Commands die Provisionierungsdaten eintragen.
     *
     * @return
     */
    protected CPSServiceOrderData getServiceOrderData() {
        return (CPSServiceOrderData) getPreparedValue(KEY_SERVICE_ORDER_DATA);
    }

    /**
     * Gibt das AuftragDaten-Objekt des Auftrags zurueck, fuer den die Provisionierungsdaten ermittelt werden sollen.
     *
     * @return AuftragDaten-Objekt.
     */
    protected AuftragDaten getAuftragDaten() {
        return (AuftragDaten) getPreparedValue(KEY_AUFTRAG_DATEN);
    }

    /**
     * Ermittelt das Hurrican-Produkt des Auftrags. Als Auftrags-ID wird dabei der Wert aus der CPS-Transaction
     * verwendet.
     *
     * @return Produkt des Hurrican-Auftrags.
     * @throws ServiceCommandException
     */
    protected Produkt getProdukt() throws ServiceCommandException {
        return getProdukt4Auftrag(getCPSTransaction().getAuftragId());
    }

    /**
     * Gibt an, ob die Ermittlung von Daten notwendig ist. <br> (Kann von den Ableitungen ueberschrieben werden, falls
     * die Daten-Ermittlung optional sein soll.)
     *
     * @return 'true', wenn die Daten-Ermittlung fuer das Command unbedingt notwendig ist; sonst 'false'.
     */
    protected boolean isNecessary() {
        return true;
    }

    /**
     * Ermittelt alle Auftrag-IDs, die fuer die CPS-Transaction relevant sind. <br> Als Ergebnis wird die Auftrags-ID
     * der aktuellen CPS-Tx sowie der protokollierten Sub-Orders zurueck geliefert. <br>
     *
     * @return Set mit den Auftrag-IDs
     * @throws ServiceCommandException
     */
    protected Set<Long> getOrderIDs4CPSTx() throws ServiceCommandException {
        try {
            Set<Long> orderIDs = new HashSet<>();
            orderIDs.add(getCPSTransaction().getAuftragId());

            List<CPSTransactionSubOrder> subOrders =
                    cpsService.findCPSTransactionSubOrders(getCPSTransaction().getId());
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (CPSTransactionSubOrder subOrder : subOrders) {
                    orderIDs.add(subOrder.getAuftragId());
                }
            }

            return orderIDs;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error loading order IDs for CPS-Tx! " + e.getMessage(), e);
        }
    }

    /**
     * Laedt den fuer die xDSL-Daten relevanten technischen Auftrag. (Bei TK-Anlagen u.U. nicht CPSTx.auftragId, sondern
     * ein protokollierter Sub-Auftrag.)
     *
     * @param exceptionIfNotFound Flag definiert, ob eine Exception geworfen werden soll, falls kein(!) techn. Auftrag
     *                            nicht ermittelt werden konnte.
     * @throws ServiceCommandException wenn kein oder mehr als ein xDSL-Auftrag fuer die CPS-Transaction gefunden wird.
     */
    protected Long findTechOrderId4XDSL(boolean exceptionIfNotFound) throws ServiceCommandException {
        Long techOrderId = null;

        // zuerst ohne Produkt-Type ermitteln, da z.B. bei MaxiPur der Type nur auf 'phone' gestellt ist!
        Set<Long> techOrderIDs = getOrderIDs4CPSTx();
        if (CollectionTools.isNotEmpty(techOrderIDs)) {
            try {
                for (Long id : techOrderIDs) {
                    Produkt produkt = produktService.findProdukt4Auftrag(id);
                    if (isDSLProduct(produkt)) {
                        techOrderId = id;
                        break;
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new HurricanServiceCommandException(
                        "Error while loading xDSL order: " + e.getMessage(), e);
            }
        }

        if ((techOrderId == null) && exceptionIfNotFound) {
            throw new HurricanServiceCommandException("Tech Order ID for xDSL data not found!");
        }
        return techOrderId;
    }

    /*
     * Prueft, ob es sich bei dem Produkt um ein xDSL-Produkt handelt.
     */
    protected boolean isDSLProduct(Produkt produkt) throws ServiceNotFoundException, FindException {
        if (produkt != null) {
            return BooleanTools.nullToFalse(produkt.getCpsDSLProduct());
        }
        return false;
    }

    /*
     * Ermittelt das DSLAM-Profile vom Auftrag. <br> Ist dem Auftrag kein Profil zugeordnet, wird das Default-Profil
     * ermittelt.
     */
    protected DSLAMProfile getDSLAMProfile(Long auftragId) throws ServiceCommandException {
        try {
            return dslamService.findDslamProfile4AuftragOrCalculateDefault(auftragId, getCPSTransaction()
                    .getEstimatedExecTime());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error loading DSLAM profile: " + e.getMessage(), e);
        }
    }

    /**
     * Generiert die Port-ID fuer den Auftrag und setzt den Wert in das Datenobjekt. Aufbau der Port-ID: <br>
     * Verbindungsbezeichnung + "--" + Kundenname maximale Laenge: 32 Zeichen Sonderzeichen / Leerzeichen werden
     * ersetzt
     *
     * @param techOrderId
     */
    protected String definePortId(Long techOrderId) throws ServiceCommandException {
        try {
            Auftrag auftrag = ccAuftragService.findAuftragById(techOrderId);
            if (auftrag == null) {
                throw new HurricanServiceCommandException("Tech. Order not found");
            }

            StringBuilder builder = new StringBuilder();

            VerbindungsBezeichnung verbindungsBezeichnung = this.physikService
                    .findVerbindungsBezeichnungByAuftragIdTx(techOrderId);
            if (verbindungsBezeichnung == null) {
                throw new HurricanServiceCommandException("VerbindungsBezeichnung not found for order!");
            }

            Kunde kunde = kundenService.findKunde(auftrag.getKundeNo());

            String kundeName = "";
            if (kunde != null) {
                kundeName = StringTools.replaceGermanUmlaute(kunde.getName());
                kundeName = StringTools.filterSpecialChars(kundeName);
            }

            builder.append(verbindungsBezeichnung.getUniqeCodeWithUseTypeOrVbz());
            builder.append("--");
            builder.append(kundeName);
            String portId = builder.toString();

            if (StringUtils.isBlank(portId)) {
                throw new HurricanServiceCommandException("Generated Port-ID is empty!");
            }

            return StringUtils.substring(portId, 0, CPSDSLData.PORT_ID_MAX_LENGTH);
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error generating DSLAM Port ID: " + e.getMessage(), e);
        }
    }

    /**
     * Prueft, ob ein IAD Device fuer den Auftrag erfasst sein muss.
     *
     * @return
     * @throws FindException
     */
    boolean deviceNecessary() throws FindException, ServiceCommandException {
        Long auftragId = getCPSTransaction().getAuftragId();
        if (!ccLeistungsService.deviceNecessary(auftragId, getCPSTransaction().getEstimatedExecTime())) {
            // Leistungen zeigen an, dass fuer den Auftrag kein IAD Device notwendig ist.
            return false;
        }
        final Produkt produkt = getProdukt4Auftrag(auftragId);
        if (isFtthAuftrag(auftragId)) {
            if (Produkt.PROD_ID_FTTX_TELEFONIE.equals(produkt.getProdId())) {
                return hasAuftragTypEndgeraetLeistung(auftragId);
            }
            else {
                return true;
            }
        }
        final Rangierung[] rangierungen = rangierungsService.findRangierungenTxWithoutExplicitFlush(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        final Rangierung rangierung = (rangierungen != null) ? rangierungen[0] : null;
        if (isFttbAuftrag(auftragId) && rangierung != null
                && (PhysikTyp.PHYSIKTYP_FTTB_POTS.equals(rangierung.getPhysikTypId()) || PhysikTyp.PHYSIKTYP_FTTB_RF.equals(rangierung.getPhysikTypId()))) {
            return false;
        }
        return true;
    }

    /**
     * Injected
     */
    public void setDslamService(DSLAMService dslamService) {
        this.dslamService = dslamService;
    }

    /**
     * @param ccAuftragService the ccAuftragService to set
     */
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    /**
     * @return the kundenService
     */
    public KundenService getKundenService() {
        return kundenService;
    }

    /**
     * @param kundenService the kundenService to set
     */
    public void setKundenService(KundenService kundenService) {
        this.kundenService = kundenService;
    }

}
