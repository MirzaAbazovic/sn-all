/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2010 10:48:30
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Command-Klasse, um einen Verlauf mit Sub-Auftraegen aufzutrennen.
 */
public class SplitVerlaufCommand extends AbstractVerlaufCommand {

    private static final Logger LOGGER = Logger.getLogger(SplitVerlaufCommand.class);

    /**
     * Key fuer die prepare-Methode, um das Verlaufs-Objekt zu uebermitteln.
     */
    public static final String VERLAUF = "verlauf";
    /**
     * Key fuer die prepare-Methode, um das Set mit den zu entfernenden Auftrag-IDs anzugeben.
     */
    public static final String ORDER_IDS_TO_REMOVE = "order.ids.to.remove";
    /**
     * Key fuer die prepare-Methode, um die Session-ID zu uebermitteln.
     */
    public static final String SESSION_ID = "session.id";

    // Modelle
    private Verlauf verlaufToSplit = null;
    private Set<Long> orderIdsToRemove = null;
    private Long sessionId = null;
    private AKUser currentUser = null;

    // Services
    private BAService baService;
    private CCLeistungsService ccLeistungsService;

    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        init();
        checkValues();
        List<Verlauf> verlaeufe = splitVerlauf();
        return verlaeufe;
    }

    private void init() throws ServiceNotFoundException {
        setBaService(getCCService(BAService.class));
        setCcLeistungsService(getCCService(CCLeistungsService.class));
    }

    /* Fuehrt die Auftrennung des Verlaufs durch. */
    List<Verlauf> splitVerlauf() throws StoreException {
        List<Verlauf> verlaeufe = new ArrayList<Verlauf>();
        try {
            boolean removeMainOrder = false;
            if (orderIdsToRemove.contains(verlaufToSplit.getAuftragId())) {
                removeMainOrder = true;
            }

            if (removeMainOrder) {
                // falls Hauptauftragsnummer aufgetrennt werden soll und die Anzahl der zu entfernenden
                // Auftraege < der Sub-Auftraege-1 des Verlaufs ist, muss ein Switch der Haupt-AuftragsID
                // erfolgen. Andernfalls wuerden alle betroffenen Auftraege des Verlaufs aufgesplittet.
                if (orderIdsToRemove.size() < verlaufToSplit.getSubAuftragsIds().size()) {
                    Set<Long> subOrders = verlaufToSplit.getSubAuftragsIds();
                    Long subOrderToUseAsMainOrder = null;
                    for (Long orderId : subOrders) {
                        if (!orderIdsToRemove.contains(orderId)) {
                            subOrderToUseAsMainOrder = orderId;
                            break;
                        }
                    }

                    // Haupt-Auftragsnummer auf Verlauf switchen
                    verlaufToSplit.getSubAuftragsIds().add(verlaufToSplit.getAuftragId());
                    verlaufToSplit.getSubAuftragsIds().remove(subOrderToUseAsMainOrder);
                    verlaufToSplit.setAuftragId(subOrderToUseAsMainOrder);
                    baService.saveVerlauf(verlaufToSplit);
                }
                else {
                    // alle Auftraege (inkl. Hauptauftrag) bis auf einen Sub-Auftrag heraus loesen
                    //  --> alle Sub-Auftraege heraus loesen und aktuellen Verlauf unveraendert lassen
                    orderIdsToRemove = new HashSet<Long>();
                    orderIdsToRemove.addAll(verlaufToSplit.getSubAuftragsIds());
                }
            }

            // SubOrders von Verlauf entfernen
            verlaufToSplit.getSubAuftragsIds().removeAll(orderIdsToRemove);
            baService.saveVerlauf(verlaufToSplit);

            // Verlauf fuer jeden angegebenen Auftrag kopieren und verknuepfen
            for (Long orderId : orderIdsToRemove) {
                Verlauf verlauf = createCopyOfVerlauf(orderId);
                if (verlauf != null) {
                    verlaeufe.add(verlauf);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Auftrennen des Verlaufs: " + e.getMessage(), e);
        }
        return verlaeufe;
    }

    /**
     * Erzeugt eine Kopie des Verlaufs und ordnet diesem die angegebene Auftrags-ID zu. Die IDs der Kopien werden
     * natuerlich angepasst.
     */
    Verlauf createCopyOfVerlauf(Long orderId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, StoreException, FindException {
        String bemerkung4Copy = "{0}: Auftrag aus Verlauf heraus gelöst!\n{1}";
        String userName = (currentUser != null) ? currentUser.getLoginName() : null;

        Verlauf copyOfVerlauf = new Verlauf();
        PropertyUtils.copyProperties(copyOfVerlauf, verlaufToSplit);
        copyOfVerlauf.setId(null);
        copyOfVerlauf.setVersion(Long.valueOf(0));
        copyOfVerlauf.setSubAuftragsIds(null);
        copyOfVerlauf.setAuftragId(orderId);
        copyOfVerlauf.setBemerkung(StringTools.formatString(bemerkung4Copy,
                new String[] { userName, (copyOfVerlauf.getBemerkung() != null) ? copyOfVerlauf.getBemerkung() : "" }));
        baService.saveVerlauf(copyOfVerlauf);

        List<VerlaufAbteilung> verlaufAbteilungen = baService.findVerlaufAbteilungen(verlaufToSplit.getId());
        if (CollectionTools.isNotEmpty(verlaufAbteilungen)) {
            for (VerlaufAbteilung vaToCopy : verlaufAbteilungen) {
                VerlaufAbteilung copyOfVA = new VerlaufAbteilung();
                PropertyUtils.copyProperties(copyOfVA, vaToCopy);
                copyOfVA.setId(null);
                copyOfVA.setVerlaufId(copyOfVerlauf.getId());
                baService.saveVerlaufAbteilung(copyOfVA);
            }
        }

        switchAuftrag2TechLs(orderId, verlaufToSplit.getId(), copyOfVerlauf.getId());
        return copyOfVerlauf;
    }

    /**
     * Aendert die Verlaufs-Zuordnungen der technischen Leistungen des angegebenen Auftrags.
     *
     * @param orderId      ID des Auftrags, dessen technische Leistungen einem anderen Verlauf zugeordnet werden sollen
     * @param oldVerlaufId ID des alten Verlaufs
     * @param newVerlaufId ID des neuen Verlaufs
     * @throws StoreException
     */
    void switchAuftrag2TechLs(Long orderId, Long oldVerlaufId, Long newVerlaufId) throws StoreException {
        try {
            List<Auftrag2TechLeistung> a2tls = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(oldVerlaufId);
            if (CollectionTools.isNotEmpty(a2tls)) {
                for (Auftrag2TechLeistung a2tl : a2tls) {
                    if (NumberTools.equal(a2tl.getAuftragId(), orderId)) {
                        if (NumberTools.equal(a2tl.getVerlaufIdReal(), oldVerlaufId)) {
                            a2tl.setVerlaufIdReal(newVerlaufId);
                        }
                        if (NumberTools.equal(a2tl.getVerlaufIdKuend(), oldVerlaufId)) {
                            a2tl.setVerlaufIdKuend(newVerlaufId);
                        }
                        ccLeistungsService.saveAuftrag2TechLeistung(a2tl);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Zuordnung der technischen Leistungen zum neuen Verlauf: " + e.getMessage(), e);
        }
    }

    /**
     * Ueberprueft, ob dem Command alle benoetigten Parameter uebergeben wurden.
     */
    private void checkValues() throws FindException {
        setVerlaufToSplit(getPreparedValue(VERLAUF, Verlauf.class, false,
                "Der aufzutrennende Verlauf wurde nicht angegeben!"));

        if (CollectionTools.isEmpty(verlaufToSplit.getSubAuftragsIds())) {
            throw new FindException("Der Verlauf kann nicht aufgetrennt werden, da er keine Sub-Auftraege besitzt!");
        }

        setSessionId(getPreparedValue(SESSION_ID, Long.class, false, "Keine Session-ID angegeben!"));
        currentUser = getAKUserSilent(sessionId);

        @SuppressWarnings("unchecked")
        Set<Long> tmpIds = getPreparedValue(ORDER_IDS_TO_REMOVE, Set.class, false,
                "Es wurden keine Auftraege angegeben, die von dem Verlauf entfernt werden sollen!");
        setOrderIdsToRemove(tmpIds);
    }

    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    public void setVerlaufToSplit(Verlauf verlaufToSplit) {
        this.verlaufToSplit = verlaufToSplit;
    }

    public void setOrderIdsToRemove(Set<Long> orderIdsToRemove) {
        this.orderIdsToRemove = orderIdsToRemove;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

}
