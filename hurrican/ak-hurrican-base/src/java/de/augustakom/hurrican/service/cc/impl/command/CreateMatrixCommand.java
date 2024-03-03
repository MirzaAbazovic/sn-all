/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2004 08:10:58
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.RangierungsmatrixDAO;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * ServiceCommand, um eine Rangierungsmatrix fuer UEVTs und Produkte zu erstellen. <br><br> Ablauf: <br> 1. Liste mit
 * den Produkten durchlaufen <br> 2. Zu jedem Produkt die Produkt/PhysikTyp-Mappings laden <br> 3. Produkt/Physiktyp-
 * und UEVT-Liste durchlaufen <br> 4. Pruefen, ob Matrix fuer UEVT/Produkt/Produkt-Physiktyp bereits angelegt ist <br>
 * 5. Ziel-HVT fuer UEVT ermitteln und falls eindeutig setzen <br> 6. Matrix speichern <br>
 *
 *
 */
@CcTxRequired
public class CreateMatrixCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateMatrixCommand.class);

    // Property-Keys
    /**
     * Key fuer die prepare-Methode, um dem Command eine Liste mit UEVT-IDs zu uebergeben.
     */
    public static final String UEVT_IDS = "matrix.uevts";
    /**
     * Key fuer die prepare-Methode, um dem Command eine Liste mit Produkt-IDs zu uebergeben.
     */
    public static final String PRODUKT_IDS = "matrix.produkte";
    /**
     * Key fuer die prepare-Methode, um dem Command eine Liste mit Physiktyp-IDs zu uebergeben.
     */
    public static final String PHYSIKTYP_IDS = "matrix.physiktypen";
    /**
     * Key fuer die prepare-Methode, um dem Command die Session-ID zu uebergeben.
     */
    public static final String SESSION_ID = "user.session.id";

    // DAOs
    private RangierungsmatrixDAO rangierungsmatrixDAO = null;

    // Parameter, die dem ServiceCommand uebergeben werden muessen
    private List<Long> uevtIds = null;
    private List<Long> produktIds = null;
    private List<Long> physiktypIds = null;
    private Long sessionId = null;

    // Sonstiges
    private String bearbeiter = null;
    private List<Rangierungsmatrix> createdMatrix = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws ServiceCommandException {
        uevtIds = (List<Long>) getPreparedValue(UEVT_IDS);
        produktIds = (List<Long>) getPreparedValue(PRODUKT_IDS);
        physiktypIds = (List<Long>) getPreparedValue(PHYSIKTYP_IDS);
        sessionId = (Long) getPreparedValue(SESSION_ID);

        try {
            bearbeiter = getUserName();
            createMatrix();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }

        return createdMatrix;
    }

    /* Versucht den aktuellen User ueber die Session-ID zu ermitteln. */
    private String getUserName() {
        try {
            AKUserService userService = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            AKUser user = userService.findUserBySessionId(sessionId);
            if (user != null) {
                return user.getName();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return HurricanConstants.UNKNOWN;
    }

    /* Basis-Methode, um die Matrix fuer UEVTs/Produkte zu erzeugen */
    private void createMatrix() throws FindException, StoreException, ServiceNotFoundException {
        createdMatrix = new ArrayList<>();
        for (Long prodId : produktIds) {
            List<Produkt2PhysikTyp> produktPhysiktypen = findP2PTs(prodId);
            if ((produktPhysiktypen != null) && (!produktPhysiktypen.isEmpty())) {
                for (Long uevtId : uevtIds) {
                    for (Produkt2PhysikTyp p2pt : produktPhysiktypen) {
                        // pruefen, ob PhysikTyp am HVT-Standort vorhanden ist
                        if (isPhysiktypAvailable(uevtId, p2pt)) {
                            if (!NumberTools.equal(p2pt.getPhysikTypId(), PhysikTyp.PHYSIKTYP_UNDEFINIERT)) {
                                // nach bestehender Rangierungsmatrix suchen
                                RangierungsmatrixQuery matrixQuery = new RangierungsmatrixQuery();
                                matrixQuery.setProduktId(prodId);
                                matrixQuery.setUevtId(uevtId);
                                matrixQuery.setProdukt2PhysikTypId(p2pt.getId());

                                List<Rangierungsmatrix> actualMatrix = getRangierungsmatrixDAO().findByQuery(matrixQuery);
                                if ((actualMatrix != null) && (!actualMatrix.isEmpty())) {
                                    if (LOGGER.isInfoEnabled()) {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("RMatrix: Rangierungsmatrix existiert bereits! ");
                                        sb.append("UEVT=").append(uevtId);
                                        sb.append(" - Produkt=").append(prodId);
                                        sb.append(" - P2PT=").append(p2pt.getId());
                                        LOGGER.info(sb.toString());
                                    }
                                }
                                else {
                                    // moegliche Ziel-HVTs fuer UEVT ermitteln
                                    Long hvtId = search4HVTZiel(uevtId, p2pt.getId());

                                    // Matrix speichern
                                    Rangierungsmatrix toSave = new Rangierungsmatrix();
                                    toSave.setUevtId(uevtId);
                                    toSave.setProduktId(prodId);
                                    toSave.setProdukt2PhysikTypId(p2pt.getId());
                                    toSave.setHvtStandortIdZiel(hvtId);
                                    toSave.setGueltigVon(new Date());
                                    toSave.setGueltigBis(DateTools.getHurricanEndDate());
                                    toSave.setBearbeiter(bearbeiter);

                                    getRangierungsmatrixDAO().store(toSave);
                                    createdMatrix.add(toSave);
                                }
                            }
                        }
                        else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("RMatrix: HVTTechnik an Standort nicht vorhanden - Matrix nicht erzeugt fuer: ");
                            sb.append("UEVT=").append(uevtId);
                            sb.append(" - Produkt=").append(prodId);
                            sb.append(" - P2PT=").append(p2pt.getId());
                            LOGGER.info(sb.toString());
                        }
                    }
                }
            }
            else {
                LOGGER.warn("RMatrix anlegen: der Produkt-ID " + prodId + " sind keine Physiktypen zugeordnet!");
            }
        }
    }

    /* Sucht nach allen Produkt-Physiktyp-Zuordnungen fuer ein best. Produkt.
     * @param prodId ID des Produkts
     */
    private List<Produkt2PhysikTyp> findP2PTs(Long prodId) throws FindException, ServiceNotFoundException {
        PhysikService physikService = getCCService(PhysikService.class);
        if (CollectionTools.isEmpty(physiktypIds)) {
            return physikService.findP2PTs4Produkt(prodId, Boolean.TRUE);
        }
        else {
            List<Produkt2PhysikTyp> result = new ArrayList<>();
            for (Long physikTypId : physiktypIds) {
                Produkt2PhysikTypQuery query = new Produkt2PhysikTypQuery();
                query.setProduktId(prodId);
                query.setPhysikTypId(physikTypId);

                List<Produkt2PhysikTyp> p2pts = physikService.findP2PTsByQuery(query);
                CollectionTools.addAllIgnoreNull(result, p2pts);
            }
            return result;
        }
    }

    /* Sucht nach der ID eines HVT-Standorts, der als Ziel-HVT fuer die Kombination
     * UEVT/Produkt2PhysikTyp verwendet werden kann. <br>
     * Die Methode gibt jedoch nur dann einen HVT-Standort (bzw. eine ID) zurueck, wenn
     * ein <strong>eindeutiger</strong> HVT-Standort ermittelt werden konnte!
     * @param uevtId ID des UEVTs
     * @param produkt2PhysikTypId ID des Produkt/PhysikTyp-Mappings.
     * @return ID des HVT-Standorts, der als Ziel-HVT fuer die UEVT/Produkt-Kombination
     * verwendet werden kann.
     */
    private Long search4HVTZiel(Long uevtId, Long produkt2PhysikTypId)
            throws FindException, ServiceNotFoundException {
        HVTService hs = getCCService(HVTService.class);
        List<HVTStandort> hvtStds = hs.findHVTStdZiele(uevtId, produkt2PhysikTypId);
        return ((hvtStds != null) && (hvtStds.size() == 1)) ? hvtStds.get(0).getId() : null;
    }

    /* Ueberprueft, ob der PhysikTyp aus dem Mapping <code>p2pt</code> an dem
     * HVT-Standort des UEVTs mit der ID <code>uevtId</code> vorhanden ist.
     * @param uevtId
     * @param p2pt
     * @return true, wenn der Physiktyp am zugehoerigen HVT-Standort vorhanden ist - sonst false
     */
    private boolean isPhysiktypAvailable(Long uevtId, Produkt2PhysikTyp p2pt)
            throws FindException, ServiceNotFoundException {
        HVTService hs = getCCService(HVTService.class);
        HVTStandort hstd = hs.findHVTStandort4UEVT(uevtId);

        PhysikService ps = getCCService(PhysikService.class);
        PhysikTyp pt = ps.findPhysikTyp(p2pt.getPhysikTypId());

        boolean available = false;
        if ((pt != null) && (pt.getHvtTechnikId() != null)) {
            HVTTechnik hvtTechnik4PT = hs.findHVTTechnik(pt.getHvtTechnikId());

            if ((hstd == null) || (hvtTechnik4PT == null)) {
                throw new FindException("Es konnte nicht ermittelt werden, ob der Physiktyp am HVT-Standort vorhanden ist!");
            }

            List<HVTTechnik> hvtTechniken = hs.findHVTTechniken4Standort(hstd.getId());
            if (hvtTechniken == null) {
                throw new FindException("Es konnte nicht ermittelt werden, welche Techniken an dem HVT-Standort vorhanden sind!");
            }

            for (HVTTechnik hvtTechnik : hvtTechniken) {
                if (NumberTools.equal(hvtTechnik4PT.getId(), hvtTechnik.getId())) {
                    available = true;
                    break;
                }
            }
        }
        else {
            // notwendig fuer "virtuelle" Physiktypen
            available = true;
        }

        return available;
    }

    /**
     * @return Returns the rangierungsmatrixDAO.
     */
    public RangierungsmatrixDAO getRangierungsmatrixDAO() {
        return rangierungsmatrixDAO;
    }

    /**
     * @param rangierungsmatrixDAO The rangierungsmatrixDAO to set.
     */
    public void setRangierungsmatrixDAO(RangierungsmatrixDAO rangierungsmatrixDAO) {
        this.rangierungsmatrixDAO = rangierungsmatrixDAO;
    }

}


