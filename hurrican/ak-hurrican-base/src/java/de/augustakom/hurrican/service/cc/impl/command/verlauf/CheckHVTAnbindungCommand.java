/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 08:45:54
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;

// @formatter:off
/**
 * Command-Klasse, um bei Auftraegen, die ueber HVT realisiert sind, folgende Daten zu pruefen: <br/>
 * <ul>
 *      <li>vollstaendig ausgefuellte Carrierbestellung
 *      <li>bei TAL-Nutzungsaenderung wird geprueft, ob fuer den Ursprungsauftrag ein Kuendigungsbauauftrag vorhanden ist
 *      <li>Montageart der Endgeraete muss gesetzt sein
 *      <li>Pruefung, ob die Endstellen eine Rangierung besitzen
 *      <li>ONKZ vom HVT wird mit der ONKZ der Auftrags-Rufnummern verglichen (falls Produkt entsprechend konfiguriert)
 *      <li>bei TAL-Nutzungsaenderungen oder DSL-Kreuzungen wird der Schaltungstag des HVTs mit dem Realisierungstermin
 *          verglichen. Sind diese abweichend, wird eine Benutzerbestaetigung angefordert (ServiceCallback).
 * </ul>
 *
 *
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckHVTAnbindungCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckHVTAnbindungCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckHVTAnbindungCommand.class);

    private static final String MSG_RESOURCE = "de.augustakom.hurrican.service.cc.resources.BAVerlauf";

    private AuftragDaten auftragDaten = null;
    private Produkt produkt = null;
    private Endstelle esA = null;
    private Endstelle esB = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            loadRequiredData();
            checkApAdresse();
            checkAnbindungHVT();
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der HVT-Anbindung ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            auftragDaten = getAuftragDatenTx(getAuftragId());
            produkt = getProdukt();

            EndstellenService esSrv = getCCService(EndstellenService.class);
            esA = esSrv.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_A);
            esB = esSrv.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Daten zur HVT-Ueberpruefung: " + e.getMessage(), e);
        }
    }

    /**
     * Wenn Anbindung = HVT oder KVZ, dann werden alle für das Produkt benötigten Endstellen überprüft. Dazu wird der
     * Endstellentyp des Produkts ausgewertet (ob nur B oder A+B oder keine) und pro Endstelle des zug. Standorts auf
     * HVT / KVZ geprueft. Falls keine Endstelle benötigt wird, läuft der Check fehlerfrei durch, dennoch sollte dieser
     * Check-Command dann erst gar nicht ausgeführt werden.
     */
    void checkAnbindungHVT() throws FindException {
        Set<Endstelle> endstellen2Check = new HashSet<>();
        if (produkt.getEndstellenTyp().equals(Produkt.ES_TYP_A_UND_B)) {
            endstellen2Check.addAll(Arrays.asList(esA, esB));
        }
        else if (produkt.getEndstellenTyp().equals(Produkt.ES_TYP_NUR_B)) {
            endstellen2Check.add(esB);
        }

        for (Endstelle es : endstellen2Check) {
            checkEndstelle4Anbindung(es);
        }
    }

    // @formatter:off
    /**
     * Für die übergebene Endstelle wird folgendes überprüft:
     * <ul>
     *     <li>Carrierbestellung muss vollstaendig erfasst sein
     *     <li>pruefen, ob der notwendigen Endstellen (A+B oder nur B) eine Rangierung zugeordnet ist
     *     <li>ONKZ vom HVT mit der Rufnummer vom Auftrag vergleichen - falls Produkt eine RN besitzt (nur B)
     *     <li>Realisierungstermin mit vorgesehenen Schaltungstagen pruefen - nur bei DSL-Kreuzungen und TAL-Nutzungsaenderungen
     * </ul>
     *
     * @throws FindException falls eine Fehlermeldung auftacht
     */
    // @formatter:on
    void checkEndstelle4Anbindung(Endstelle es) throws FindException {
        if (NumberTools.isIn(es.getAnschlussart(), new Long[] { Anschlussart.ANSCHLUSSART_HVT, Anschlussart.ANSCHLUSSART_KVZ })) {
            try {
                checkCarrierbestellung(es);

                // Rangierung pruefen
                if (es.getRangierId() == null) {
                    throw new FindException("Mindestens einer Endstelle ist keine Rangierung zugeorndet"
                            + ". Bitte Rangierung zuordnen oder Projektierung erstellen");
                }

                HVTService hs = getCCService(HVTService.class);
                HVTGruppe hvt = hs.findHVTGruppe4Standort(es.getHvtIdStandort());

                if (es.isEndstelleB()) {
                    checkONKZ(hvt);
                }
            }
            catch (FindException e) {
                throw e;
            }
            catch (Exception e) {
                throw new FindException("Mindestens eine für das Produkt nötige Endstelle konnte nicht geprüft werden.");
            }
        }
    }

    void checkONKZ(HVTGruppe hvt) throws ServiceNotFoundException, FindException {
        if (produkt.isDnAllowed()) {
            RufnummerService rs = getBillingService(RufnummerService.class);
            if (hvt == null) {
                throw new FindException("Für die Endstelle B konnte keine HVT-Gruppe ermittelt werden.");
            }

            // Rufnummern aus Taifun ermitteln
            List<String> onkzs = rs.findOnkz4AuftragNoOrig(auftragDaten.getAuftragNoOrig());
            if ((onkzs != null) && (!onkzs.isEmpty())) {
                String hvtOnkz = hvt.getOnkz();
                for (String toCheck : onkzs) {
                    if (!StringUtils.equals(hvtOnkz, toCheck)) {
                        throw new FindException("Die ONKZ des HVTs und der in Taifun " +
                                "vergebenen Rufnummern stimmen nicht überein.");
                    }
                }
            }
        }
    }

    /**
     * Prueft, ob die Carrierbestellungen richtig erfasst wurden. Carrierbestellung (mit LBZ) muss vorhanden sein
     * (ausser bei Child-Buendel-Produkten).
     */
    void checkCarrierbestellung(Endstelle es) throws FindException {
        if (!(BooleanTools.nullToFalse(produkt.getBuendelProdukt() && !BooleanTools.nullToFalse(produkt.getIsParent())))) {
            checkCarrierbestellung(es, produkt);
            // Bei TAL-Nutzungsaenderung muss der Ursprungs-Auftrag gekuendigt sein!
            checkSourceAuftrag4TalNA(es);
        }
    }

    /**
     * Ueberprueft, ob die Endstelle <code>es</code> eine Carrierbestellung benoetigt und wenn ja, ob diese vollstaendig
     * erfasst ist.
     *
     * @param es zu pruefende Endstelle
     * @throws FindException
     */
    void checkCarrierbestellung(Endstelle es, Produkt produkt) throws FindException {
        if (es == null) { return; }
        try {
            if (!NumberTools.isIn(es.getAnschlussart(), new Number[] { Anschlussart.ANSCHLUSSART_DIREKT,
                    Anschlussart.ANSCHLUSSART_VIRTUELL, Anschlussart.ANSCHLUSSART_NETZKOPPLUNG })) {

                boolean cbOk = false;
                CarrierService cs = getCCService(CarrierService.class);
                Carrier carrier = cs.findCarrier4HVT(es.getHvtIdStandort());
                if ((carrier != null) && BooleanTools.nullToFalse(carrier.getCbNotwendig())) {
                    List<Carrierbestellung> cbs = cs.findCBs4Endstelle(es.getId());

                    // JG / 2009-06-24: Workaround fuer Connect:
                    // Da bei Connect teilweise ein Master-Auftrag ohne TAL geschrieben wird, darf
                    // die Pruefung nicht so streng sein
                    //  --> Endstelle ohne Port und ohne Carrierbestellung == Check OK
                    if (NumberTools.equal(produkt.getProduktGruppeId(), ProduktGruppe.AK_CONNECT)
                            && (es.getRangierId() == null)
                            && CollectionTools.isEmpty(cbs)) {
                        cbOk = true;
                    }

                    if (!cbOk && (cbs != null)) {
                        for (Carrierbestellung c : cbs) {
                            if (StringUtils.isNotBlank(c.getLbz())) {
                                cbOk = true;
                                break;
                            }
                        }
                    }
                }
                else {
                    cbOk = true;
                }

                if (!cbOk) {
                    throw new FindException("Die Carrierbestellung für die Endstelle " + es.getEndstelleTyp() +
                            " ist nicht oder nur unvollständig erfasst.");
                }
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Ueberpruefung der Carrierbestellung trat ein Fehler auf:\n" +
                    e.getMessage(), e);
        }
    }


    /**
     * Prueft, ob fuer den Ursprungs-Auftrag der TAL-Nutzungsaenderung ein Kuendigungsbauauftrag vorhanden ist und ob in
     * der zugehoerigen Carrierbestellung ein Kuendigungsdatum eingetragen ist. Die Pruefung wird z.Z. nur bei
     * TAL-Nutzungsaenderungen durchgefuehrt!
     *
     * @throws FindException
     */
    void checkSourceAuftrag4TalNA(Endstelle es) throws FindException {
        try {
            CarrierService cs = getCCService(CarrierService.class);
            Carrierbestellung cbDest = cs.findLastCB4Endstelle(es.getId());
            if ((cbDest != null) && (cbDest.getTalNATyp() != null) && (cbDest.getAuftragId4TalNA() != null)) {
                Long auftragIdSrc = cbDest.getAuftragId4TalNA();
                ResourceReader rr = new ResourceReader(MSG_RESOURCE);
                StringBuilder msg = new StringBuilder();

                // pruefen, ob im Source-Auftrag die Cuda-Kuendigung eingetragen wurde
                EndstellenService esSrv = getCCService(EndstellenService.class);
                Endstelle esSrc = esSrv.findEndstelle4Auftrag(auftragIdSrc, es.getEndstelleTyp());
                Carrierbestellung cb = cs.findLastCB4Endstelle(esSrc.getId());
                if ((cb == null) || (cb.getKuendBestaetigungCarrier() == null)) {
                    msg.append(rr.getValue("cuda.kuendigung.missing", new Object[] { "" + auftragIdSrc }));
                }

                // pruefen, ob fuer den Source-Auftrag ein Kuendigungsbauauftrag erstellt wurde
                // oder ob der Ursprungsauftrag bereits gekuendigt ist
                BAService bas = getCCService(BAService.class);
                CCAuftragService ccAS = getCCService(CCAuftragService.class);
                Verlauf actVerlSrc = bas.findActVerlauf4Auftrag(auftragIdSrc, false);
                AuftragDaten ad = ccAS.findAuftragDatenByAuftragId(auftragIdSrc);
                if ((actVerlSrc == null) || !BAVerlaufAnlass.isKuendigung(actVerlSrc.getAnlass())) {
                    if ((ad == null) || NumberTools.notEqual(ad.getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT)) {
                        msg.append(rr.getValue("src.auftrag.not.quit", new Object[] { "" + auftragIdSrc }));
                    }
                    else {
                        msg = new StringBuilder();
                    }
                }

                if (msg.length() > 0) {
                    throw new FindException(msg.toString());
                }
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Ueberpruefung der Physik ist ein Fehler aufgetreten:\n" + e.getMessage(), e);
        }
    }

    /*
     * Prueft Aenderungen an der Endstellen-Adresse und kopiert diese bei Bedarf
     */
    void checkApAdresse() throws FindException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            if ((produkt != null) && (auftragDaten != null)
                    && !BooleanTools.nullToFalse(produkt.getCreateAPAddress())
                    && (auftragDaten.getStatusId() < AuftragStatus.TECHNISCHE_REALISIERUNG)
                    && esSrv.hasAPAddressChanged(auftragDaten)) {
                esSrv.copyAPAddress(auftragDaten, getSessionId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    void setAuftragDaten(AuftragDaten auftragDaten) {
        this.auftragDaten = auftragDaten;
    }

    void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    void setEsA(Endstelle esA) {
        this.esA = esA;
    }

    void setEsB(Endstelle esB) {
        this.esB = esB;
    }

}
