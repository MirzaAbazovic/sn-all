/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.2004 11:41:29
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.lang.reflect.*;
import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;
import de.augustakom.hurrican.model.shared.view.Billing2HurricanProdMapping;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * ServiceCommand sucht nach einer freien Rangierung und ordnet diese der Endstelle zu. <br> <br> Ablauf der
 * Rangierungssuche: <br> <ul> <li>Endstelle mit der angegebenen ID laden. Ueber die Endstelle ist der HVT-Standort und
 * damit die UEVTs bekannt. <li>Suche nach dem Auftrag, zu dem die Endstelle gehoert. <li>Produkt zu dem Auftrag suchen.
 * Darueber kann ermittelt werden, ob es ein 'Vater'-Produkt gibt. Ist dies der Fall, wird nach dem entsprechenden
 * Auftrag gesucht. <li>Vater-Produkt/Auftrag vorhanden: verwendeter PhysikTyp ermitteln. <li>  -->
 * Rangierungsmatrix-Suche mit Produkt, Vater-Physiktyp und HVT <li>kein Vater-Produkt zugeordnet: Physiktyp ist egal
 * </ul>  --> Rangierungsmatrix-Suche mit Produkt und HVT
 *
 *
 */
@CcTxRequired
public class AssignRangierung2ESCommand extends AbstractAssignRangierungCommand {

    private static final Logger LOGGER = Logger.getLogger(AssignRangierung2ESCommand.class);

    /**
     * Key fuer die prepare-Methode, um dem Command eine optionale Uevt-Cluster-No zu uebergeben.
     */
    public static final String UEVT_CLUSTER_NO = "uevt.cluster.no";
    /**
     * Key fuer die prepare-Methode, um dem Command eine optionales Uebertragsungsverfahren zu uebergeben.
     */
    public static final String EQ_UEBERTRAGUNGSVERFAHREN = "eq.uebertragungsverfahren";

    // geladene Objekte
    private Endstelle endstelle = null;
    private Long auftragId = null;
    private Produkt actProdukt = null;
    private AuftragTechnik actAuftragTechnik = null;
    private AuftragDaten actAuftragDaten = null;
    private AuftragDaten parentAuftrag = null;
    private Rangierung rangierung4ES = null;
    private Rangierung rangierung4ESAdd = null;
    private Boolean useProjektierung = null;
    private IServiceCallback serviceCallback = null;
    private Bandwidth bandwidth = null;
    private Integer uevtClusterNo = null;
    private Uebertragungsverfahren uebertragungsverfahren = null;

    // Services
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;

    // Sonstiges
    private boolean needsParent = false;

    @Override
    public Object execute() throws FindException, StoreException, HurricanServiceCommandException {
        Long endstelleId;
        try {
            endstelleId = (Long) getPreparedValue(ENDSTELLE_ID);
            this.uevtClusterNo = getPreparedValue(UEVT_CLUSTER_NO, Integer.class, true,
                    "Uevt-Cluster-No ist keine gültige Zahl.");
            this.uebertragungsverfahren = getPreparedValue(EQ_UEBERTRAGUNGSVERFAHREN, Uebertragungsverfahren.class, true,
                    "Kein gültiges Übertragunsverfahren angegeben.");

            LOGGER.debug("Endstelle, fuer die eine Rangierung gesucht wird: " + endstelleId
                    + "; Uevt-Cluster-No: " + uevtClusterNo + "; Übertragungsverfahren: " + uebertragungsverfahren);

            this.serviceCallback = (IServiceCallback) getPreparedValue(SERVICE_CALLBACK);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(HurricanServiceCommandException.INVALID_PARAMETER_4_COMMAND);
        }

        setEndstelle(loadEndstelle(endstelleId));  // Endstelle komplett laden
        loadAuftragsDaten4ES();        // Auftrags-Daten zur Endstelle laden
        loadTechLeistungBandwidth();  // Downstream-Leistung des Auftrags laden
        findProdukt4Auftrag();         // Produkt zum Auftrag laden/suchen
        if (((this.actProdukt.getBuendelProdukt() != null) && this.actProdukt.getBuendelProdukt()) &&
                ((this.actProdukt.getIsParent() == null) || !this.actProdukt.getIsParent())) {
            needsParent = true;
            findParentAuftrag();
        }

        this.findRangierung();
        if (rangierung4ES != null) {
            LOGGER.debug("Rangierung der Endstelle uebergeben und speichern");
            validateRangierung(rangierung4ES);

            bundleRangierungAndEndstelle();
        }

        return rangierung4ES;
    }

    /**
     * Gibt die ID des {@link de.augustakom.hurrican.model.cc.HVTStandort}s zurueck, von dem die Rangierung ermittelt
     * werden soll.
     * @return
     * @throws FindException
     */
    protected Long getHvtIdStandort() throws FindException {
        return (endstelle != null) ? endstelle.getHvtIdStandort() : null;
    }

    /**
     * Fuehrt die Buendelung der Rangierung und Endstelle durch.
     */
    void bundleRangierungAndEndstelle() throws StoreException {
        endstelle.setRangierId(rangierung4ES.getId());
        endstelle.setRangierIdAdditional((rangierung4ESAdd != null) ? rangierung4ESAdd.getId() : null);
        endstellenService.saveEndstelle(endstelle);
        endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);

        rangierung4ES.setEsId(endstelle.getId());
        rangierung4ES.setFreigabeAb(null);
        rangierungsService.saveRangierung(rangierung4ES, false);

        if (rangierung4ESAdd != null) {
            rangierung4ESAdd.setEsId(endstelle.getId());
            rangierung4ESAdd.setFreigabeAb(null);
            rangierungsService.saveRangierung(rangierung4ESAdd, false);
        }

        updateAuftragTechnik();
    }


    public Rangierung getRangierung4ES() {
        return rangierung4ES;
    }

    public Rangierung getRangierung4ESAdd() {
        return rangierung4ESAdd;
    }


    /**
     * Sucht nach der Auftrags- und Kunden-ID des Auftrags, zu dem die Endstelle gehoert.
     */
    private void loadAuftragsDaten4ES() throws FindException {
        try {
            actAuftragTechnik = ccAuftragService.findAuftragTechnik4ESGruppe(this.endstelle.getEndstelleGruppeId());
            if ((actAuftragTechnik == null) || (actAuftragTechnik.getAuftragId() == null)) {
                throw new FindException("Auftrags-ID der Endstelle konnte nicht ermittelt werden!");
            }
            this.auftragId = actAuftragTechnik.getAuftragId();

            this.actAuftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(this.auftragId);  // war ohne Tx
            if (this.actAuftragDaten == null) {
                throw new FindException("Die Auftrags-Daten zur Endstelle konnten nicht ermittelt werden!");
            }
            if (this.actAuftragDaten.getStatusId() >= AuftragStatus.KUENDIGUNG) {
                throw new FindException("Der Endstelle kann keine Physik zugeordnet werden, da der " +
                        "zugehörige Auftrag bereits gekündigt ist!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }


    /**
     * Laedt die technischen Leistungen fuer Up- und Downstream fuer den Auftrags.
     */
    private void loadTechLeistungBandwidth() {
        try {
            bandwidth = ccLeistungsService.findBandwidth4Auftrag(auftragId, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    /**
     * Sucht nach dem Produkt, das dem Auftrag zugeordnet ist.
     */
    private void findProdukt4Auftrag() throws FindException {
        try {
            this.actProdukt = produktService.findProdukt4Auftrag(this.auftragId);
            if (this.actProdukt == null) {
                throw new FindException("Aktuelles Produkt konnte nicht ermittelt werden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Aktuelles Produkt konnte nicht ermittelt werden!", e);
        }
    }


    /**
     * Sucht nach einem Parent-Auftrag, falls das Produkt des aktuellen Auftrags ein Parent-Produkt definiert hat.
     */
    private void findParentAuftrag() throws FindException {
        if (needsParent) {
            try {
                Auftrag auftrag = ccAuftragService.findAuftragById(this.auftragId);
                if ((auftrag == null) || (auftrag.getKundeNo() == null)) {
                    throw new FindException("Der Kunde, dem die Endstelle zugeordnet ist, " +
                            "konnte nicht ermittelt werden!");
                }

                parentAuftrag = ccAuftragService.findParentAuftragDaten(auftrag.getKundeNo(),
                        actAuftragDaten.getBuendelNr(), actAuftragDaten.getBuendelNrHerkunft());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            if (parentAuftrag == null) {
                throw new FindException("Zugehöriger Bündel-Auftrag konnte nicht ermittelt werden! " +
                        "Die Physik-Zuordnung kann deshalb nicht durchgeführt werden.");
            }
        }
    }


    /**
     * Sucht nach einer freien Rangierung fuer die Endstelle.
     */
    private void findRangierung() throws FindException {
        LOGGER.debug("AssignRangierung2ESCommand.findRangierung");
        Exception ex = null;
        try {
            if (needsParent) {
                // Rangierung wird ueber das Parent-Produkt ermittelt
                rangierung4ES = findRangierung4ESWithParent();
            }
            else {
                if (BooleanTools.nullToFalse(actProdukt.getCheckChild())) {
                    // Physiktyp des Childs wird fuer Rangierungs-Ermittlung des Parent benoetigt
                    rangierung4ES = findRangierungByChildAware();
                }
                else if (BooleanTools.nullToFalse(actProdukt.getIsCombiProdukt())) {
                    Pair<Rangierung, Rangierung> combiRangierungen;
                    if (physikService.findSimpleP2PTs4Produkt(actProdukt.getId()).isEmpty()) {
                        combiRangierungen = findRangierungen4CombiProduktOnly();
                    }
                    else {
                        combiRangierungen = findRangierungen4ESOrCombiProdukt();
                    }
                    if (combiRangierungen != null) {
                        rangierung4ES = combiRangierungen.getFirst();
                        rangierung4ESAdd = combiRangierungen.getSecond();
                    }
                }
                else {
                    // 'einfache' Rangierungs-Ermittlung
                    Pair<Rangierung, Rangierungsmatrix> freieRang = findRangierung4ES(null);
                    rangierung4ES = (freieRang != null) ? freieRang.getFirst() : null;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            ex = e;
        }

        if (rangierung4ES == null) {
            String msg = "Es konnte keine Physik für die Endstelle ermittelt werden!";
            if (uevtClusterNo != null) {
                msg += "\n  mit Üvt-Cluster-No = " + uevtClusterNo;
            }
            if (uebertragungsverfahren != null) {
                msg += "\n  mit Übertragungsverfahren = " + uebertragungsverfahren;
            }

            throw new FindException(msg, ex);
        }
        LOGGER.debug("gefundene, freie Rangierung - ID: " + rangierung4ES.getId());
    }

    Pair<Rangierung, Rangierung> findRangierungen4CombiProduktOnly() throws FindException {
        // Kombi-Produkt --> zwei Rangierungen ermitteln und zuordnen!!!
        Pair<Rangierung, Rangierungsmatrix>[] result = findRangierungen4CombiProdukt(null);
        if (result.length != 2) {
            throw new FindException("Anzahl der ermittelten Rangierungen fuer das Kombi-Produkt ist ungueltig!");
        }
        else {
            return new Pair<>(result[0].getFirst(), result[1].getFirst());
        }
    }

    Pair<Rangierung, Rangierung> findRangierungen4ESOrCombiProdukt() throws FindException {
        Pair<Rangierung, Rangierungsmatrix>[] combiRangierungen = findRangierungen4CombiProdukt(Boolean.FALSE);
        Pair<Rangierung, Rangierungsmatrix> simpleRang = findRangierung4ES(Boolean.TRUE);
        if (combiRangierungen.length > 0 && simpleRang == null) {
            return new Pair<>(combiRangierungen[0].getFirst(), combiRangierungen[1].getFirst());
        }
        else if (combiRangierungen.length == 0 && simpleRang != null) {
            return new Pair<>(simpleRang.getFirst(), null);
        }
        else if (combiRangierungen.length > 0 && simpleRang != null) {
            Rangierungsmatrix combiMatrix = combiRangierungen[0].getSecond();
            Rangierungsmatrix singleMatrix = simpleRang.getSecond();
            Rangierung rang4ESAdd = null;
            Rangierung rang4ES;
            if ((combiMatrix.getPriority() != null) && (singleMatrix.getPriority() == null)) {
                rang4ES = combiRangierungen[0].getFirst();
                rang4ESAdd = combiRangierungen[1].getFirst();
            }
            else if ((combiMatrix.getPriority() == null) && (singleMatrix.getPriority() != null)) {
                rang4ES = simpleRang.getFirst();
            }
            else if ((combiMatrix.getPriority() != null) && (singleMatrix.getPriority() != null)) {
                if (NumberTools.isGreater(combiMatrix.getPriority(), singleMatrix.getPriority())) {
                    rang4ES = combiRangierungen[0].getFirst();
                    rang4ESAdd = combiRangierungen[1].getFirst();
                }
                else {
                    // wenn die Prio gleich ist hat die einfache Rangierung
                    // Vorrang vor der combi Rangierung (Resourcen sparen)
                    rang4ES = simpleRang.getFirst();
                }
            }
            else {
                // die einfache Rangierung hat Vorrang vor der combi Rangierung
                // (Resourcen sparen)
                rang4ES = simpleRang.getFirst();
            }
            return new Pair<>(rang4ES, rang4ESAdd);
        }
        return null;
    }


    /**
     * Sucht nach einer freien Rangierung fuer eine Endstelle. Die Methode beruecksichtigt keine Parent-Auftraege!
     */
    Pair<Rangierung, Rangierungsmatrix> findRangierung4ES(Boolean simpleOnly) throws FindException {
        LOGGER.debug("AssignRangierung2ESCommand.findRangierung4ES");
        try {
            RangierungsmatrixQuery matrixQuery = new RangierungsmatrixQuery();
            matrixQuery.setHvtIdStandort(getHvtIdStandort());
            matrixQuery.setProduktId(actProdukt.getId());
            matrixQuery.debugModel(LOGGER);

            Pair<Rangierung, Rangierungsmatrix> freieRang = findRangierung4Matrix(matrixQuery, null, false, null,
                    false, simpleOnly);
            if ((freieRang != null) && (freieRang.getFirst() != null)) {
                if (BooleanTools.nullToFalse(actProdukt.getIsParent())) {
                    createLeitungskennzeichnung(freieRang.getFirst());
                }
                return freieRang;
            }
        }
        catch (Exception e) {
            throw new FindException(e);
        }
        return null;
    }


    /**
     * Sucht nach den Rangierungen fuer ein Combi-Produkt. <br> Das Result-Array enthaelt bei einer erfolgreichen Suche
     * zwei Eintraege. Der erste Eintrag ist nach Definition die DSL-, der zweite die Phone-Physik.
     *
     * @return Pair&lt;Rangierung4ES, Rangierung4ESAdd&gt;<br> != null, wenn freie CombiRangierungen gefunden werden
     * konnten, die Rangierungsmatrix ist nur auf dem ersten Pair gesetzt<br> == null, wenn keine Rangierung oder keine
     * AddRangierung gefunnden werden konnte<br>
     */
    Pair<Rangierung, Rangierungsmatrix>[] findRangierungen4CombiProdukt(Boolean simpleOnly) throws FindException {
        LOGGER.debug("AssignRangierung2ESCommand.findRangierungen4CombiProdukt");
        try {
            RangierungsmatrixQuery matrixQuery = new RangierungsmatrixQuery();
            matrixQuery.setHvtIdStandort(getHvtIdStandort());
            matrixQuery.setProduktId(actProdukt.getId());

            Pair<Rangierung, Rangierungsmatrix> freieRang = findRangierung4Matrix(matrixQuery, null, true, null, true,
                    simpleOnly);
            if ((freieRang != null) && (freieRang.getFirst() != null)
                    && (freieRang.getFirst().getLeitungGesamtId() != null)) {
                List<Rangierung> result = rangierungsService.findByLtgGesId(freieRang.getFirst().getLeitungGesamtId());
                if ((result != null) && (result.size() == 2)) {
                    @SuppressWarnings("unchecked")
                    Pair<Rangierung, Rangierungsmatrix>[] retVal = (Pair<Rangierung, Rangierungsmatrix>[]) Array
                            .newInstance(Pair.class, 2);
                    retVal[0] = freieRang;
                    retVal[1] = new Pair<>((NumberTools.equal(
                            freieRang.getFirst().getId(), result.get(0).getId())) ? result.get(1) : result.get(0), null);
                    return retVal;
                }
            }
            return new Pair[0];
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Rangierungen fuer das Kombi-Produkt: " +
                    e.getMessage(), e);
        }
    }


    /**
     * Ermittelt eine freie Rangierung fuer den Auftrag. Es wird der Physik-Typ des 'Child'-Auftrags beruecksichtigt, um
     * die richtige vorbelegte Rangierung zu erhalten. Ablauf: 1. Child-Auftrag und -Produkt ermitteln 2. freie
     * Rangierung ueber Child-Physik und Parent-HVT ermitteln 3. Produkt-Physiktyp-Mapping von Child-Rangierung
     * ermitteln 4. Produkt-Physiktyp-Mapping fuer Parent-Produkt und Physiktyp-ID ermitteln 5. freie Rangierung ueber
     * Parent-HVT, Parent-Produkt und P2PT von Parent ermitteln
     */
    private Rangierung findRangierungByChildAware() throws FindException {
        // 1.
        try {
            Rangierung freieRangierung = null;
            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            List<BAuftrag> auftraege = bas.findByBuendelNo(actAuftragDaten.getBuendelNr());
            if (auftraege != null) {
                BAuftrag childAuftrag = null;
                for (BAuftrag ba : auftraege) {
                    if (!NumberTools.equal(ba.getAuftragNoOrig(), actAuftragDaten.getAuftragNoOrig())) {
                        childAuftrag = ba;
                        break;
                    }
                }

                if (childAuftrag != null) {
                    List<BAuftragLeistungView> balViews =
                            bas.findAuftragLeistungViews4Auftrag(childAuftrag.getAuftragNoOrig(), true, true);
                    List<Billing2HurricanProdMapping> prodMappings = produktService.doProductMapping(balViews);
                    if (CollectionTools.isNotEmpty(prodMappings)) {
                        for (Billing2HurricanProdMapping pm : prodMappings) {
                            Produkt childProdukt = produktService.findProdukt(pm.getProdId());
                            if (childProdukt != null) {
                                List<Produkt2PhysikTyp> p2ptsChild = physikService
                                        .findP2PTs4Produkt(childProdukt.getId(), null);
                                for (Produkt2PhysikTyp p2ptChild : p2ptsChild) {
                                    // 2.
                                    RangierungsmatrixQuery matrixQuery = new RangierungsmatrixQuery();
                                    matrixQuery.setHvtIdStandort(getHvtIdStandort());
                                    matrixQuery.setProduktId(childProdukt.getId());
                                    matrixQuery.setProdukt2PhysikTypId(p2ptChild.getId());

                                    Pair<Rangierung, Rangierungsmatrix> freieRang4Matrix = findRangierung4Matrix(
                                            matrixQuery, null, true, null, false, null);
                                    Rangierung childRang = (freieRang4Matrix != null) ? freieRang4Matrix.getFirst()
                                            : null;
                                    if (childRang != null) {
                                        freieRangierung = findRangierungWithChildPhysik(childRang.getPhysikTypId());
                                        if (freieRangierung != null) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    // Abfrage, welches Child-Produkt (und damit Physiktyp) verwendet wird
                    Long askedChildProdukt = ask4ChildProdukt(actProdukt.getId());
                    freieRangierung = findRangierung4ChildProdukt(askedChildProdukt);
                }
            }

            if (freieRangierung == null) {
                throw new FindException("Für die Endstelle konnte keine Rangierung gefunden werden. " +
                        "Wahrscheinlich konnte das zugehoerige Buendel-Produkt oder dessen Physiktyp nicht ermittelt werden.");
            }
            return freieRangierung;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e);
        }
    }


    /**
     * Sucht ueber das 'Child'-Produkt (bzw. dessen moegliche Physiktypen) nach einer freien Rangierung.
     *
     * @param childProduktId
     * @return
     * @throws FindException
     */
    private Rangierung findRangierung4ChildProdukt(Long childProduktId) throws FindException {
        try {
            Produkt2PhysikTypQuery query = new Produkt2PhysikTypQuery();
            query.setProduktId(childProduktId);

            List<Produkt2PhysikTyp> p2pts = physikService.findP2PTsByQuery(query);
            if (p2pts != null && !p2pts.isEmpty()) {
                for (Produkt2PhysikTyp p2pt : p2pts) {
                    Rangierung rang = findRangierungWithChildPhysik(p2pt.getPhysikTypId());
                    if (rang != null) {
                        return rang;
                    }
                }
            }
            else {
                throw new FindException("Von dem Buendel-Produkt konnte keine Physiktyp-Zuordnung ermittelt werden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Mit dem zugehoerigen Buendel-Produkt konnte keine freie Rangierung ermittelt werden!", e);
        }

        return null;
    }


    /**
     * Sucht nach einer freien Rangierung. <br> Die Rangierung wird in Abhaengigkeit der Child-Physik ermittelt.
     */
    private Rangierung findRangierungWithChildPhysik(Long childPhysiktypId) throws FindException {
        try {
            // 3.
            Produkt2PhysikTypQuery p2ptQuery = new Produkt2PhysikTypQuery();
            p2ptQuery.setPhysikTypId(childPhysiktypId);

            List<Produkt2PhysikTyp> p2ptsChild = physikService.findP2PTsByQuery(p2ptQuery);
            Rangierung freieRang = null;
            for (Produkt2PhysikTyp p2ptChild : p2ptsChild) {
                // 4.
                p2ptQuery.setProduktId(actProdukt.getId());
                p2ptQuery.setPhysikTypId(p2ptChild.getParentPhysikTypId());

                List<Produkt2PhysikTyp> p2ptsParent = physikService.findP2PTsByQuery(p2ptQuery);
                if (p2ptsParent != null) {
                    // 5.
                    RangierungsmatrixQuery matrixQuery = new RangierungsmatrixQuery();
                    matrixQuery.setHvtIdStandort(getHvtIdStandort());
                    matrixQuery.setProduktId(actProdukt.getId());
                    for (Produkt2PhysikTyp p2pt : p2ptsParent) {
                        matrixQuery.addProdukt2PhysikTypId(p2pt.getId());
                    }

                    Pair<Rangierung, Rangierungsmatrix> freieRang4Matrix = findRangierung4Matrix(matrixQuery, null,
                            true, childPhysiktypId, false, null);
                    freieRang = (freieRang4Matrix != null) ? freieRang4Matrix.getFirst() : null;
                }

                if (freieRang != null) {
                    // Pruefen, ob Parent-Physiktyp auch passt
                    List<Produkt2PhysikTyp> parentPTs = physikService.findP2PTs4Produkt(actProdukt.getId(), null);
                    if (parentPTs != null) {
                        boolean found = false;
                        for (Produkt2PhysikTyp parentPT : parentPTs) {
                            if (NumberTools.equal(freieRang.getPhysikTypId(), parentPT.getPhysikTypId())) {
                                found = true;
                            }
                        }

                        if (!found) {
                            freieRang = null;
                        }
                    }

                    if (freieRang != null) {
                        break;
                    }
                }
            }
            return freieRang;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e);
        }
    }


    /**
     * 'Fragt' ueber den Service-Callback nach dem zugehoerigen Buendel-Produkt.
     *
     * @param parentProdId Produkt-ID des aktuellen Produkts
     * @return ID des zugehoerigen Buendel-Produkts
     * @throws FindException wenn die ID des Buendel-Produkts nicht ermittelt werden konnte.
     */
    private Long ask4ChildProdukt(Long parentProdId) throws FindException {
        Map<String, Long> params = new HashMap<>();
        params.put(RangierungsService.CALLBACK_PARAM_PARENT_PRODUKT_ID, parentProdId);
        Object result = serviceCallback.doServiceCallback(this,
                RangierungsService.CALLBACK_ASK_4_BUENDEL_PRODUKT, params);
        if (result instanceof Long) {
            return ((Long) result);
        }
        else {
            throw new FindException("Die Physik des zugehoerigen Child-Auftrags wurde nicht angegeben!");
        }
    }


    /**
     * Sucht nach einer freien Rangierung ueber ein Parent-Produkt bzw. Buendel-Auftrag.
     */
    private Rangierung findRangierung4ESWithParent() throws FindException {
        LOGGER.debug("AssignRangierung2ESCommand.findRangierung4ESWithParent");

        // Rangierung zu 'parentAuftrag' ermitteln
        Rangierung parentRangierung = getRangierung4Parent(parentAuftrag.getAuftragId());
        LOGGER.debug("ID der ermittelten Parent-Rangierung: " + parentRangierung.getId());

        try {
            // P2PT mit Parent-PhysikTyp und neuem Produkt ermitteln!
            Produkt2PhysikTypQuery p2ptQuery = new Produkt2PhysikTypQuery();
            p2ptQuery.setParentPhysikTypId(parentRangierung.getPhysikTypId());
            p2ptQuery.setProduktId(actProdukt.getId());

            List<Produkt2PhysikTyp> p2pts = physikService.findP2PTsByQuery(p2ptQuery);
            if (p2pts != null) {
                // Rangierungsmatrizen suchen
                RangierungsmatrixQuery matrixQuery = new RangierungsmatrixQuery();
                matrixQuery.setHvtIdStandort(getHvtIdStandort());
                for (Produkt2PhysikTyp p2pt : p2pts) {
                    matrixQuery.addProdukt2PhysikTypId(p2pt.getId());
                }

                Pair<Rangierung, Rangierungsmatrix> freieRang4Matrix = findRangierung4Matrix(matrixQuery,
                        parentRangierung.getLeitungGesamtId(), false, null, false, null);
                Rangierung freieRang = (freieRang4Matrix != null) ? freieRang4Matrix.getFirst() : null;
                if (freieRang != null) {
                    if (parentRangierung.getLeitungGesamtId() == null) {
                        createLeitungskennzeichnung(parentRangierung);
                        getRangierungDAO().store(parentRangierung);
                        LOGGER.debug("vergebene Leitung_Gesamt_ID fuer Parent-Rangierung: " +
                                parentRangierung.getLeitungGesamtId());
                    }

                    if (freieRang.getLeitungGesamtId() == null) {
                        freieRang.setLeitungGesamtId(parentRangierung.getLeitungGesamtId());
                        freieRang.setLeitungLoeschen(Boolean.TRUE);
                    }
                    if (freieRang.getLeitungLfdNr() == null) {
                        Integer maxLtgLfdNr = getRangierungDAO().getMaxLfdNr(freieRang.getLeitungGesamtId());
                        freieRang.setLeitungLfdNr((maxLtgLfdNr != null)
                                ? Integer.valueOf(maxLtgLfdNr + 1) : Integer.valueOf(1));
                    }

                    return freieRang;
                }
            }
            else {
                throw new FindException("Es konnte keine passende Produkt-Physiktyp-Zuordnung gefunden werden! " +
                        "Produkt: " + actProdukt.getId() + "; übergeordneter Physiktyp: " + parentRangierung.getPhysikTypId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e);
        }
        return null;
    }


    /**
     * Sucht nach einer freien Rangierung. <br> Ablauf: 1. Alle Rangierungsmatrizen suchen, die mit dem Query
     * <code>matrixQuery</code> uebereinstimmen. 2. Suche einer freien Rangierung mit den Matrix-Informationen und der
     * evtl. vorhandenen Leitung-Gesamt-ID einer Parent-Rangierung 3. Pruefen, ob die zugeordnete Strasse eine KVZ
     * Nummer besitzt und der Standort vom Typ 'KVZ' ist; In diesem Fall muss auch der EQ-OUT Port der 1. Rangierung die
     * KVZ Nummer der Strasse besitzen.
     *
     * @param matrixQuery               Query fuer die Suche nach den Rangierungsmatrizen
     * @param ltgGesId                  Leitung-Gesamt-ID der Parent-Rangierung (oder <code>null</code>).
     * @param checkLtgGesId             ist dieses Flag gesetzt, muessen ALLE Rangierungungen mit der gleichen
     *                                  Leitung-Gesamt-ID 'frei' sein.
     * @param childPhysiktypId          notwendiger Child-Physiktyp
     * @param regardPTAdditionalAsChild Flag, ob der Zusatz-Physiktyp der Produkt-Physiktyp-Zuordnung als
     *                                  Child-Physiktyp beruecksichtigt werden soll.
     * @return eine freie Rangierung oder <code>null</code>.
     */
    Pair<Rangierung, Rangierungsmatrix> findRangierung4Matrix(RangierungsmatrixQuery matrixQuery, Integer ltgGesId,
            boolean checkLtgGesId, Long childPhysiktypId, boolean regardPTAdditionalAsChild, Boolean simpleOnly)
            throws FindException {
        try {
            Rangierung freieRangierung = null;

            List<Rangierungsmatrix> matrizen = rangierungsService.findMatrix(matrixQuery);
            if (matrizen != null && !matrizen.isEmpty()) {
                RangierungQuery rq = new RangierungQuery();
                rq.setLeitungGesamtId(ltgGesId);

                for (Rangierungsmatrix matrix : matrizen) {
                    // Produkt-Physiktyp-Zuordnung der Matrix laden
                    Produkt2PhysikTyp p2pt = physikService.findP2PT(matrix.getProdukt2PhysikTypId());

                    if ((p2pt != null) && (p2pt.getPhysikTypId() != null)
                            && (simpleOnly == null || simpleOnly == p2pt.isSimple())) {
                        rq.setHvtStandortId(matrix.getHvtStandortIdZiel());
                        rq.setPhysikTypId(p2pt.getPhysikTypId());
                        rq.debugModel(LOGGER);

                        // Child-Physiktyp entweder von Methoden-Parameter oder
                        // von P2PT-Konfiguration
                        Long childPT = null;
                        if (childPhysiktypId != null) {
                            childPT = childPhysiktypId;
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Child-Physiktyp fuer Suche nach freier Rangierung: " + childPhysiktypId);
                            }
                        }
                        else if (regardPTAdditionalAsChild) {
                            childPT = p2pt.getPhysikTypAdditionalId();
                        }

                        freieRangierung = rangierungsService.findFreieRangierung(rq, endstelle, checkLtgGesId, childPT,
                                uevtClusterNo, uebertragungsverfahren, getBandwidth());
                    }
                    else {
                        if ((p2pt == null) || (p2pt.getPhysikTypId() == null)) {
                            LOGGER.warn("Rangierungsmatrix besitzt keine Produkt-PhysikTyp-Zuordnung!");
                        }
                    }

                    if (freieRangierung != null) {
                        useProjektierung = matrix.getProjektierung();
                        LOGGER.info("Freie Rangierung (ID): " + freieRangierung.getId());
                        return new Pair<>(freieRangierung, matrix);
                    }
                }
            }
            else {
                LOGGER.warn("Rangierungsmatrix wurde nicht gefunden!");
            }
        }
        catch (Exception e) {
            throw new FindException(e);
        }
        return null;
    }


    /**
     * Ermittelt die Rangierung des Parent-Auftrags.
     */
    private Rangierung getRangierung4Parent(Long parentAuftragId) throws FindException {
        Rangierung parentRang = getRangierungDAO().findByAuftragAndEsTyp(
                parentAuftragId, endstelle.getEndstelleTyp());
        if (parentRang == null) {
            throw new FindException("Die Physik des Vater-Auftrags (ID: " + parentAuftragId + ") konnte nicht " +
                    "ermittelt werden! Deshalb kann zu dem aktuellen Auftrag keine Physik zugeordnet werden.");
        }
        return parentRang;
    }


    /**
     * Fuegt der Rangierung eine neue Leitungskennzeichnung hinzu.
     */
    private void createLeitungskennzeichnung(Rangierung rangierung) {
        if ((rangierung != null) && (rangierung.getLeitungGesamtId() == null)) {
            rangierung.setLeitungLoeschen(Boolean.TRUE);
            // hoechste LeitungGesamtId der Rangierungen ermitteln
            Integer nextLtgGesId = getRangierungDAO().getNextLtgGesId();
            rangierung.setLeitungGesamtId((nextLtgGesId != null) ? nextLtgGesId : Integer.valueOf(1));
            rangierung.setLeitungLfdNr(1);
        }
    }


    /**
     * Aktualisiert die Auftrag-Technik, die zur aktuellen Endstelle gehoert.
     */
    private void updateAuftragTechnik() {
        try {
            actAuftragTechnik.setProjektierung(useProjektierung);
            ccAuftragService.saveAuftragTechnik(actAuftragTechnik, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    /**
     * Fuehrt eine Validierung der ermittelten / angegebenen Rangierung durch. Pruefungen: - ES_ID der Rangierung wird
     * vorsichtshalber noch einmal geprueft - es wird geprueft, ob die Rangierung doch einem 'aktiven' Auftrag
     * zugeordnet ist
     *
     * @param toValidate
     * @throws HurricanServiceCommandException
     */
    private void validateRangierung(Rangierung toValidate) throws HurricanServiceCommandException {
        if (toValidate.getEsId() != null) {
            throw new HurricanServiceCommandException("Die Rangierung ist noch einem Auftrag zugeordnet!");
        }

        try {
            Long eqId = (toValidate.getEqInId() != null) ? toValidate.getEqInId() : toValidate.getEqOutId();
            if (eqId != null) {
                List<AuftragDaten> auftragDaten4Equipment = ccAuftragService.findAuftragDatenByEquipment(eqId);
                if (CollectionTools.isNotEmpty(auftragDaten4Equipment)) {
                    for (AuftragDaten auftragToCheck : auftragDaten4Equipment) {
                        if (auftragToCheck.isAuftragActive()) {
                            throw new HurricanServiceCommandException(
                                    "Die Rangierung (ID: {0}) ist noch min. einem anderen aktiven Auftrag (ID: {1}) zugeordnet!\n" +
                                            "Bitte Fehler an Hurrican Administration melden!",
                                    new Object[] { String.format("%s", toValidate.getId()),
                                            String.format("%s", auftragToCheck.getAuftragId()) }
                            );
                        }
                    }
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Validierung der ermittelten Rangierung: " + e.getMessage(), e);
        }
    }

    private Bandwidth getBandwidth() {
        return bandwidth;
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
    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

    /**
     * Injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    public void setEndstelle(Endstelle endstelle) {
        this.endstelle = endstelle;
    }

}
