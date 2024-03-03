/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 13:05:53
 */
package de.mnet.wita.service.impl;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.TalQueryService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;

@CcTxRequired
public class TalQueryServiceImpl implements TalQueryService {

    static final Predicate<AuftragDaten> TERMINVERSCHIEBUNG_POSSIBLE = AuftragDaten::isTerminverschiebungPossible;
    @Autowired
    CCAuftragService auftragService;
    @Autowired
    PhysikService physikService;
    @Autowired
    RangierungsService rangierungsService;
    @Autowired
    EndstellenService endstellenService;
    @Autowired
    CarrierService carrierService;
    @Autowired
    CarrierElTALService carrierElTalService;
    @Autowired
    WitaUsertaskService witaUsertaskService;
    @Autowired
    CommonWorkflowService commonWorkflowService;
    @Autowired
    WitaTalOrderService witaTalOrderService;

    @Override
    public List<TalSubOrder> findPossibleSubOrdersForTerminverschiebung(List<WitaCBVorgang> cbVorgaenge,
            AuftragDaten auftragDaten) throws FindException {
        return findPossibleSubOrders(cbVorgaenge, auftragDaten, TERMINVERSCHIEBUNG_POSSIBLE);
    }

    @Override
    public List<TalSubOrder> findPossibleSubOrdersForStorno(List<WitaCBVorgang> cbVorgaenge, AuftragDaten auftragDaten)
            throws FindException {
        return findPossibleSubOrders(cbVorgaenge, auftragDaten, Predicates.<AuftragDaten>alwaysTrue());
    }

    private List<TalSubOrder> findPossibleSubOrders(List<WitaCBVorgang> cbVorgaenge, AuftragDaten auftragDaten,
            Predicate<AuftragDaten> predicate) throws FindException {
        if (Iterables.all(cbVorgaenge, CBVorgang.IS_REXMK_PREDICATE)) {
            return findSubOrdersForRexMk(cbVorgaenge, auftragDaten);
        }

        if (!Iterables.any(cbVorgaenge, CBVorgang.IS_REXMK_PREDICATE)) {
            WitaCBVorgang cbv = cbVorgaenge.iterator().next();

            // Just use the first cbVorgang - all others are identified via Carrierbestellung
            return findPossibleSubOrders(
                    cbv.getCbId(),
                    cbv.getAuftragsKlammer(),
                    auftragDaten,
                    predicate,
                    false);
        }

        throw new IllegalArgumentException("Allowed cbVorgaenge: Either only REX-MK or only not-REX-MK");
    }

    private List<TalSubOrder> findSubOrdersForRexMk(List<WitaCBVorgang> cbVorgaenge, AuftragDaten auftragDaten) {
        List<TalSubOrder> result = Lists.newArrayList();
        for (WitaCBVorgang cbVorgang : cbVorgaenge) {
            TalSubOrder subOrder = new TalSubOrder(auftragDaten.getAuftragId(), null, null, cbVorgang.getId(),
                    cbVorgang.getCarrierRefNr());
            subOrder.setSelected(Boolean.TRUE);
            result.add(subOrder);
        }
        return result;
    }

    @Override
    public List<TalSubOrder> findPossibleSubOrdersForErlmk(CBVorgang cbVorgang, AuftragDaten auftragDaten)
            throws FindException {

        Long auftragsklammer = (cbVorgang instanceof WitaCBVorgang) ? ((WitaCBVorgang) cbVorgang).getAuftragsKlammer()
                : null;

        List<TalSubOrder> allSubOrders = findPossibleSubOrders(cbVorgang.getCbId(),
                auftragsklammer,
                auftragDaten,
                Predicates.<AuftragDaten>alwaysTrue(),
                true);
        List<TalSubOrder> subOrdersForSelection = Lists.newArrayList();

        // mit Tam-Vorgangsliste abgleichen und nur Uebereinstimmungen in zurueckzugebene Liste einfuegen
        List<TamVorgang> tamVorgaenge = witaUsertaskService.findOpenTamTasks();
        for (TalSubOrder talSubOrder : allSubOrders) {
            if (isCbVorgangInList(talSubOrder.getCbVorgangId(), tamVorgaenge)) {
                subOrdersForSelection.add(talSubOrder);
            }
        }

        return subOrdersForSelection;
    }

    List<TalSubOrder> findPossibleSubOrders(Long cbId, Long auftragsklammer, AuftragDaten auftragDaten,
            Predicate<AuftragDaten> predicate, boolean ignoreCbReferences) throws FindException {
        List<TalSubOrder> subOrdersForSelection = new ArrayList<>();

        // Hurrican-Auftraege zum Taifun-Auftrag ermitteln
        List<AuftragDaten> subOrders = auftragService.findAuftragDaten4OrderNoOrigTx(auftragDaten.getAuftragNoOrig());
        if (CollectionTools.isEmpty(subOrders)) {
            return Collections.emptyList();
        }

        // zusaetzlich alle Hurrican-Auftraege ermitteln, bei denen die WITA-Klammerung mit der aus 'cbId' ueberein
        // stimmt
        if (auftragsklammer != null) {
            WitaCBVorgang example = new WitaCBVorgang();
            example.setAuftragsKlammer(auftragsklammer);

            List<WitaCBVorgang> cbvsInKlammer = carrierElTalService.findCBVorgaengeByExample(example);
            for (WitaCBVorgang cbv : cbvsInKlammer) {
                if (!containsOrder(subOrders, cbv.getAuftragId())) {
                    subOrders.add(auftragService.findAuftragDatenByAuftragIdTx(cbv.getAuftragId()));
                }
            }
        }

        Endstelle endstelle = endstellenService.findEndstelle4CarrierbestellungAndAuftrag(cbId,
                auftragDaten.getAuftragId());
        for (AuftragDaten subOrder : Iterables.filter(subOrders, predicate)) {
            subOrdersForSelection.addAll(getSubOrderForSelection(endstelle.getEndstelleTyp(), subOrder, ignoreCbReferences));
        }
        return subOrdersForSelection;
    }

    private Boolean isCbVorgangInList(Long cbId, List<TamVorgang> tamVorgaenge) {
        for (TamVorgang tamVorgang : tamVorgaenge) {
            if (NumberTools.equal(tamVorgang.getCbVorgang().getId(), cbId)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsOrder(List<AuftragDaten> auftragDaten, Long auftragId) {
        for (AuftragDaten ad : auftragDaten) {
            if (auftragId.equals(ad.getAuftragId())) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Liefert eine leere Liste oder eine Liste mit einem Element zurück
     */
    List<TalSubOrder> getSubOrderForSelection(String endstellenTyp, AuftragDaten subOrder,
          boolean ignoreCbReferences) throws FindException {
        Endstelle endstelleOfSubOrder = endstellenService.findEndstelle4Auftrag(subOrder.getAuftragId(), endstellenTyp);

        // @formatter:off
        if ((endstelleOfSubOrder == null)
                || (endstelleOfSubOrder.getRangierId() == null)
                || (endstelleOfSubOrder.getCb2EsId() == null)) {
            return Collections.emptyList();
        }
        // @formatter:on

        List<Carrierbestellung> carrierbestellungen = carrierService.findCBsTx(endstelleOfSubOrder.getCb2EsId());
        String dtagPort = getDtagPortOfSubOrder(endstelleOfSubOrder);
        String vbzValue = getVerbindungsbezeichnung(subOrder.getAuftragId());

        List<TalSubOrder> result = Lists.newArrayList();
        for (Carrierbestellung carrierbestellung : carrierbestellungen) {
            List<CBVorgang> cbVorgaengeSubOrders = carrierElTalService.findCBVorgaenge4CB(carrierbestellung.getId());
            for (CBVorgang cbVorgang : cbVorgaengeSubOrders) {
                checkAndAddCbVorgang(cbVorgang, result, subOrder.getAuftragId(), vbzValue, dtagPort);
                if (!ignoreCbReferences && cbVorgang instanceof WitaCBVorgang) {
                    final WitaCBVorgang witaCBVorgang = (WitaCBVorgang) cbVorgang;
                    if (CBVorgang.TYP_NEU.equals(witaCBVorgang.getTyp()) && witaCBVorgang.getCbVorgangRefId() != null) {
                        // referenzierte KUE-KD ermitteln
                        final WitaCBVorgang cbVorgangKue =
                                (WitaCBVorgang) carrierElTalService.findCBVorgang(witaCBVorgang.getCbVorgangRefId());
                        checkAndAddCbVorgang(cbVorgangKue, endstellenTyp, result);
                    }
                    else if (CBVorgang.TYP_KUENDIGUNG.equals(witaCBVorgang.getTyp())) {
                        // Auftrag ermitteln, der die KUE-KD referenziert
                        final WitaCBVorgang cbVorgangNeu = witaTalOrderService.findWitaCBVorgangByRefId(witaCBVorgang.getId());
                        if (cbVorgangNeu != null) {
                            checkAndAddCbVorgang(cbVorgangNeu, endstellenTyp, result);
                        }
                    }
                }
            }
        }
        return result;
    }

    private void checkAndAddCbVorgang(CBVorgang cbVorgang, String endstellenTyp, List<TalSubOrder> talSubOrders) throws FindException {
        Endstelle endstelleOfAuftragKue = endstellenService.findEndstelle4Auftrag(cbVorgang.getAuftragId(), endstellenTyp);
        String dtagPortKue = getDtagPortOfSubOrder(endstelleOfAuftragKue);
        String vbzValueKue = getVerbindungsbezeichnung(cbVorgang.getAuftragId());
        checkAndAddCbVorgang(cbVorgang, talSubOrders, cbVorgang.getAuftragId(), vbzValueKue, dtagPortKue);
    }

    /*
     * Ueberprueft, ob der angegebene CBVorgang noch einen aktiven Workflow besitzt. Falls ja, dann wird
     * der Vorgang der Liste {@code talSubOrders} hinzugefuegt.
     */
    private void checkAndAddCbVorgang(CBVorgang cbVorgang, List<TalSubOrder> talSubOrders, Long auftragId,
            String vbzValue, String dtagPort) {
        String extOrderNo = cbVorgang.getCarrierRefNr();
        if (StringUtils.isNotBlank(extOrderNo) && commonWorkflowService.isProcessInstanceAlive(extOrderNo)) {
            talSubOrders.add(new TalSubOrder(auftragId, vbzValue, dtagPort, cbVorgang.getId(), extOrderNo));
        }
    }

    private String getVerbindungsbezeichnung(Long auftragId) throws FindException {
        VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragId(auftragId);
        return (verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : null;
    }

    String getDtagPortOfSubOrder(Endstelle endstelleOfSubOrder) throws FindException {
        if (endstelleOfSubOrder == null || endstelleOfSubOrder.getRangierId() == null)
            return null;
        Rangierung rangierungOfSubOrder = rangierungsService.findRangierung(endstelleOfSubOrder.getRangierId());
        Equipment dtagEquipment = rangierungsService.findEquipment(rangierungOfSubOrder.getEqOutId());
        return (dtagEquipment != null) ? dtagEquipment.getDtagVerteilerLeisteStift() : null;
    }

    @Override
    public boolean hasEsaaOrInterneCBVorgaenge(Long cbId) throws FindException {
        for (CBVorgang cbVorgang : carrierElTalService.findCBVorgaenge4CB(cbId)) {
            if (!(cbVorgang instanceof WitaCBVorgang)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Endstelle findEndstelleForCBVorgang(CBVorgang cbVorgang) throws FindException {
        if ((cbVorgang instanceof WitaCBVorgang)
                && GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG.equals(((WitaCBVorgang) cbVorgang)
                .getWitaGeschaeftsfallTyp())) {
            return endstellenService.findEndstelle4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        }
        else {
            return findEndstelle4Auftrag(cbVorgang.getAuftragId(), cbVorgang.getCbId());
        }
    }

    private Endstelle findEndstelle4Auftrag(Long auftragId, Long cbId) throws FindException {
        Carrierbestellung cb = carrierService.findCB(cbId);
        if (cb != null) {
            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
            for (Endstelle es : endstellen) {
                if (NumberTools.equal(cb.getCb2EsId(), es.getCb2EsId())) {
                    return es;
                }
            }
        }
        return null;
    }
}
