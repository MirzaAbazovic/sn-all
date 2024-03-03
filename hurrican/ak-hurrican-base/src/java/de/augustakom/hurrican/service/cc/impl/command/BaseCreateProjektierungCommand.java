/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2009 16:48:42
 */

package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.impl.command.verlauf.AbstractVerlaufCheckCommand;
import de.mnet.common.service.locator.ServiceLocator;


/**
 *
 */
public abstract class BaseCreateProjektierungCommand extends AbstractVerlaufCommand {

    private static final Logger LOGGER = Logger.getLogger(BaseCreateProjektierungCommand.class);

    /**
     * Key-Value fuer die prepare-Methode, um die Auftrags-ID zu uebergeben.
     */
    public static final String AUFTRAG_ID = "auftrag.id";
    /**
     * Key-Value fuer die prepare-Methode, um die 'alte' Auftrags-ID zu uebergeben.
     */
    public static final String AUFTRAG_ID_ALT = "auftrag.id.alt";
    /**
     * Key-Value fuer die prepare-Methode, um die Session-ID zu uebergeben.
     */
    public static final String SESSION_ID = "session.id";
    /**
     * Key-Value fuer die prepare-Methode, um ein Objekt mit den SubAuftragsIds zu uebergeben.
     */
    public static final String AUFTRAG_SUBORDERS = "auftrag.suborders";

    // Parameter
    protected Long auftragId = null;
    protected Long auftragIdAlt = null;
    protected Long sessionId = null;
    private Set<Long> subAuftragsIds = null;

    // Modelle
    protected AuftragDaten auftragDaten = null;
    protected AuftragTechnik auftragTechnik = null;
    protected VerbindungsBezeichnung vbzAlt = null;
    protected Produkt produkt = null;
    protected BAuftrag billingAuftrag = null;

    // Services
    private CCAuftragService auftragService;
    private CCLeistungsService ccLeistungsService;
    private ChainService chainService;
    private BAService baService;
    private ProduktService produktService;
    private PhysikService physikService;
    private BillingAuftragService billingAuftragService;
    @Autowired
    private ServiceLocator serviceLocator;


    // DAOs
    protected VerlaufDAO verlaufDAO = null;

    /**
     * Erstellt die Projektierung fuer den Auftrag.
     */
    protected abstract void createProjektierung() throws StoreException;

    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        checkValues();
        List<SubOrderDetail> orders4Projektierung = loadAuftragDaten();
        checkAuftragStatus(orders4Projektierung);
        executeCheckCommands(orders4Projektierung);
        createProjektierung();
        return null;
    }

    /**
     * Ermittelt die notwendige ServiceCommandChain und fuehrt diese aus.
     *
     * @throws FindException
     * @throws ServiceCommandException
     */
    private AKWarnings executeCheckCommands4Auftrag(Long paramAuftragId, Produkt paramProdukt) throws FindException, ServiceCommandException {
        AKWarnings warnings = new AKWarnings();
        if ((paramAuftragId == null) || (paramProdukt == null)) {
            return warnings;
        }

        Long chainId = paramProdukt.getProjektierungChainId();
        if (chainId != null) {
            List<ServiceCommand> cmds = chainService.findServiceCommands4Reference(chainId, ServiceChain.class, null);
            if (CollectionTools.isNotEmpty(cmds)) {
                AKServiceCommandChain chain = new AKServiceCommandChain();
                for (ServiceCommand cmd : cmds) {
                    IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                    if (serviceCmd == null) {
                        throw new FindException(new StringBuilder()
                                .append("Für Auftrag ")
                                .append(paramAuftragId.toString())
                                .append(" und das definierte ServiceCommand ")
                                .append(cmd.getName())
                                .append(" konnte kein Objekt geladen werden. Bauauftrag wurde nicht erstellt!")
                                .toString());
                    }

                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_SESSION_ID, sessionId);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_AUFTRAG_ID, paramAuftragId);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_PRODUKT, paramProdukt);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_BILLING_AUFTRAG, billingAuftrag);

                    chain.addCommand(serviceCmd);
                }

                chain.executeChain(true);

                warnings.addAKWarnings(chain.getWarnings());
            }
        }
        else {
            LOGGER.warn(new StringBuilder()
                    .append("No Project ServiceChain defined for order ")
                    .append(paramAuftragId.toString())
                    .append(" and product ")
                    .append(paramProdukt.getProdId())
                    .append(". Order is not checked!")
                    .toString());
        }
        return warnings;
    }

    /**
     * Ermittelt die notwendige ServiceCommandChain und fuehrt diese aus.
     *
     * @throws FindException
     */
    private void executeCheckCommands(List<SubOrderDetail> subOrders) throws FindException {
        try {
            AKWarnings warnings = new AKWarnings();
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (SubOrderDetail subOrder : subOrders) {
                    warnings.addAKWarnings(executeCheckCommands4Auftrag(subOrder.auftragId, subOrder.produkt));
                }
            }
            setWarnings(warnings);
        }
        catch (ServiceCommandException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Projektierung wurde nicht erstellt, da die Auftragspruefung einen Fehler entdeckt hat:\n" +
                    e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Unbekannter Fehler bei der Auftragspruefung! Projektierung wurde nicht erstellt.", e);
        }
    }

    protected Verlauf createNewVerlauf(Long verlaufStatusId) {
        // Verlaufs-Header erzeugen
        Verlauf verlauf = new Verlauf();
        verlauf.setAuftragId(auftragId);
        verlauf.setAkt(Boolean.TRUE);
        verlauf.setStatusIdAlt(auftragDaten.getStatusId());
        verlauf.setVerlaufStatusId(verlaufStatusId);
        verlauf.setProjektierung(Boolean.TRUE);
        verlauf.setAnlass(BAVerlaufAnlass.PROJEKTIERUNG);
        verlauf.setSubAuftragsIds(subAuftragsIds);
        getVerlaufDAO().store(verlauf);
        return verlauf;
    }

    /**
     * Laedt benoetigte Produkt-Daten fuer alle Unterauftraege und fuehrt Plausichecks durch.
     *
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    private void loadAuftragDaten4SubOrders(List<SubOrderDetail> subOrderDetails) throws ServiceNotFoundException, FindException {
        if (CollectionTools.isNotEmpty(subAuftragsIds)) {
            for (Long subOrderId : subAuftragsIds) {
                AuftragDaten subAuftragDaten = auftragService.findAuftragDatenByAuftragIdTx(subOrderId);
                AuftragTechnik subAuftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(subOrderId);

                checkAuftrag(subAuftragDaten, subAuftragTechnik, subOrderId, false);

                // Auftragsart pruefen
                checkAuftragsart(subAuftragTechnik, subOrderId, false);

                // Produkt
                Produkt subProdukt = produktService.findProdukt(subAuftragDaten.getProdId());
                checkProdukt(subProdukt, subOrderId, false);

                // Billing-Auftrag mit Hauptauftrag vergleichen
                // Da der Billing-Auftrag im Hauptauftrag schon geprueft ist, wird hier nur die Billing ID auf Gleichheit gecheckt.
                if ((subAuftragDaten.getAuftragNoOrig() == null)
                        || NumberTools.notEqual(auftragDaten.getAuftragNoOrig(), subAuftragDaten.getAuftragNoOrig())) {
                    throw new FindException(new StringBuilder()
                            .append("Der Billing-Auftrag für Unterauftrag ")
                            .append(subOrderId.toString())
                            .append(" ist nicht gesetzt oder weicht von Hauptauftrag ab. Projektierung wurde nicht erstellt!")
                            .toString());
                }

                subOrderDetails.add(new SubOrderDetail(subOrderId, subProdukt, subAuftragDaten));
            }
        }
    }

    /**
     * Laedt benoetigte Auftrags- und Produkt-Daten und prueft, ob fuer den Auftrag ueberhaupt ein Verlauf erstellt
     * werden darf.
     */
    private List<SubOrderDetail> loadAuftragDaten() throws FindException, StoreException {
        try {
            List<SubOrderDetail> subOrderDetails = new LinkedList<SubOrderDetail>();

            auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
            auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
            checkAuftrag(auftragDaten, auftragTechnik, auftragId, true);

            // Auftragsart pruefen
            checkAuftragsart(auftragTechnik, auftragId, true);

            // VerbindungsBezeichnung
            if (auftragIdAlt != null) {
                vbzAlt = physikService.findVerbindungsBezeichnungByAuftragId(auftragIdAlt);
                if (vbzAlt == null) {
                    throw new FindException(new StringBuilder()
                            .append("Für Hauptauftrag ")
                            .append(auftragId.toString())
                            .append(" konnte die Verbindungsbezeichnung (VBZ) des alten Auftrags")
                            .append(auftragIdAlt.toString())
                            .append(" nicht ermittelt werden. Projektierung wurde nicht erstellt!")
                            .toString());
                }
            }

            // Produkt
            produkt = produktService.findProdukt(auftragDaten.getProdId());
            checkProdukt(produkt, auftragId, true);

            // Billing-Auftrag laden
            if (auftragDaten.getAuftragNoOrig() != null) {
                billingAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());
                if (billingAuftrag == null) {
                    billingAuftrag = billingAuftragService.findAuftragStornoByAuftragNoOrig(auftragDaten.getAuftragNoOrig());
                    if (billingAuftrag == null) {
                        throw new FindException(new StringBuilder()
                                .append("Der Billing-Auftrag für Hauptauftrag ")
                                .append(auftragId.toString())
                                .append(" konnte nicht ermittelt werden. Projektierung wurde nicht erstellt!")
                                .toString());
                    }
                }
            }

            subOrderDetails.add(new SubOrderDetail(auftragId, produkt, auftragDaten));
            loadAuftragDaten4SubOrders(subOrderDetails);
            return subOrderDetails;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(new StringBuilder()
                    .append("Bei der Ermittlung der Auftragsdaten für Hauptauftrag ")
                    .append(auftragId.toString())
                    .append(" ist ein Fehler aufgetreten. Projektierung wurde nicht erstellt!")
                    .toString(), e);
        }
    }

    void checkAuftrag(AuftragDaten auftragDatenToCheck, AuftragTechnik auftragTechnikToCheck, Long orderId, boolean mainOrder) throws FindException {
        if ((auftragDatenToCheck == null) || (auftragTechnikToCheck == null)) {
            throw new FindException(new StringBuilder()
                    .append("Die Auftragsdaten für ")
                    .append((mainOrder) ? "Hauptauftrag " : "Unterauftrag ")
                    .append(String.format("%s", orderId))
                    .append(" konnten nicht ermittelt werden. Projektierung wurde nicht erstellt!")
                    .toString());
        }
    }

    void checkAuftragsart(AuftragTechnik auftragTechnik, Long orderId, boolean mainOrder) throws FindException {
        if (auftragTechnik.getAuftragsart() == null) {
            throw new FindException(new StringBuilder()
                    .append("Für ")
                    .append((mainOrder) ? "Hauptauftrag " : "Unterauftrag ")
                    .append(String.format("%s", orderId))
                    .append(" ist die Auftragsart nicht definiert. Projektierung wurde nicht erstellt!")
                    .toString());
        }
    }

    void checkProdukt(Produkt produkt, Long orderId, boolean mainOrder) throws FindException {
        if ((produkt == null) || !BooleanTools.nullToFalse(produkt.getElVerlauf())) {
            throw new FindException(new StringBuilder()
                    .append("Das Produkt für ")
                    .append((mainOrder) ? "Hauptauftrag " : "Unterauftrag ")
                    .append(String.format("%s", orderId))
                    .append(" konnte nicht ermittelt werden order ist nicht für den el. Verlauf konfiguriert. Projektierung wurde nicht erstellt!")
                    .toString());
        }
    }

    /**
     * Ueberprueft, ob fuer den aktuellen Auftrag-Status eine Projektierung erstellt werden kann.
     */
    private void checkAuftragStatus(List<SubOrderDetail> subOrders) throws FindException {
        if (CollectionTools.isNotEmpty(subOrders)) {
            for (SubOrderDetail subOrder : subOrders) {
                if ((subOrder.auftragDaten.getInbetriebnahme() != null) ||
                        NumberTools.isGreaterOrEqual(subOrder.auftragDaten.getStatusId(), AuftragStatus.PROJEKTIERUNG_ERLEDIGT)) {
                    throw new FindException(new StringBuilder()
                            .append("Der Auftrag ")
                            .append(subOrder.auftragId.toString())
                            .append(" ist in Betrieb oder befindet sich bereits im Verlauf bzw. in der Projektierung.")
                            .append(" Projektierung wurde nicht erstellt!")
                            .toString());
                }
            }
        }

        // nach bestehendem (aktiven) Verlauf suchen - hasActiveVerlauf prueft auch die Unterauftraege
        if (getVerlaufDAO().hasActiveVerlauf(auftragId)) {
            throw new FindException(new StringBuilder()
                    .append("Für den Hauptauftrag oder einen seiner assoziierten Aufträge existiert bereits ein aktiver Verlauf. ")
                    .append("Projektierung wurde nicht erstellt!")
                    .toString());
        }
    }

    /**
     * Ueberprueft die Parameter, die dem Command uebergeben wurden.
     */
    private void checkValues() throws FindException {
        Object id = getPreparedValue(AUFTRAG_ID);
        auftragId = (id instanceof Long) ? (Long) id : null;
        if (auftragId == null) {
            throw new FindException(
                    "Es wurde kein Auftrag angegeben, für den der Verlauf erstellt werden soll. Projektierung wird nicht erstellt!");
        }

        Object idalt = getPreparedValue(AUFTRAG_ID_ALT);
        auftragIdAlt = (idalt instanceof Long) ? (Long) idalt : null;

        Object sid = getPreparedValue(SESSION_ID);
        sessionId = (sid instanceof Long) ? (Long) sid : null;
        if (sessionId == null) {
            throw new FindException("Die Session-ID des Benutzers wurde nicht korrekt übermittelt!");
        }

        Object value = getPreparedValue(AUFTRAG_SUBORDERS);
        @SuppressWarnings("unchecked")
        Set<Long> subAuftragsIds = (Set<Long>) ((value instanceof Set<?>) ? value : null);
        this.subAuftragsIds = subAuftragsIds;
    }

    protected void syncWithAuftragData(Verlauf verlauf, List<VerlaufAbteilung> vas) throws StoreException, ServiceNotFoundException, FindException {
        String bemerkung = null;
        if ((vbzAlt != null) && (vas != null)) {
            for (VerlaufAbteilung va : vas) {
                if (NumberTools.equal(va.getAbteilungId(), Abteilung.AM)) {
                    bemerkung = "Projektierung bezieht sich auf bestehende VerbindungsBezeichnung: " + vbzAlt.getVbz();
                    va.setBemerkung(bemerkung);
                    baService.saveVerlaufAbteilung(va);
                }
            }
        }

        Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            AuftragDaten auftragDatenToChange = auftragService.findAuftragDatenByAuftragIdTx(orderId);
            AuftragTechnik auftragTechnikToChange = auftragService.findAuftragTechnikByAuftragIdTx(orderId);

            if (auftragDatenToChange.getAuftragNoOrig() != null) {
                // Auftrags-Leistungen abgleichen
                ccLeistungsService.synchTechLeistungen4Auftrag(orderId, auftragDatenToChange.getAuftragNoOrig(),
                        auftragDatenToChange.getProdId(), null, false, sessionId);

                // Auftrag-Status aendern
                auftragDatenToChange.setStatusId(AuftragStatus.PROJEKTIERUNG);
                if (bemerkung != null) {
                    String bemerkungen = (StringUtils.isBlank(auftragDatenToChange.getBemerkungen())) ?
                            bemerkung : (auftragDatenToChange.getBemerkungen() + SystemUtils.LINE_SEPARATOR + bemerkung);
                    auftragDatenToChange.setBemerkungen(bemerkungen);
                }
                auftragService.saveAuftragDaten(auftragDatenToChange, false);

                auftragTechnikToChange.setProjektierung(Boolean.TRUE);
                auftragService.saveAuftragTechnik(auftragTechnikToChange, false);
            }
        }
    }

    @Override
    public VerlaufDAO getVerlaufDAO() {
        return verlaufDAO;
    }

    /**
     * injected
     */
    @Override
    public void setVerlaufDAO(VerlaufDAO verlaufDAO) {
        this.verlaufDAO = verlaufDAO;
    }

    /**
     * injected
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * injected
     */
    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

    /**
     * injected
     */
    public void setChainService(ChainService chainService) {
        this.chainService = chainService;
    }

    /**
     * injected
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    /**
     * injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * injected
     */
    public void setBillingAuftragService(BillingAuftragService billingAuftragService) {
        this.billingAuftragService = billingAuftragService;
    }

    /**
     * injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    static class SubOrderDetail {
        Produkt produkt = null;
        Long auftragId = null;
        AuftragDaten auftragDaten = null;

        public SubOrderDetail(Long auftragId, Produkt produkt, AuftragDaten auftragDaten) {
            this.produkt = produkt;
            this.auftragId = auftragId;
            this.auftragDaten = auftragDaten;
        }
    }
}
