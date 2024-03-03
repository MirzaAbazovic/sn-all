/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 10:17:30
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um den Status des Ursprungs-Auftrags auf 'gekuendigt' (=9800) zu setzen. <br> Ausserdem wird fuer die
 * Kuendigung ein Verlaufs-Eintrag erzeugt, der dies protokolliert. <br><br> Sollte der Ursprungs-Auftrag noch einen
 * aktiven Verlauf besitzen, bricht das Command mit einer entsprechenden Fehlermeldung ab. <br> <br><br> WICHTIG:
 * sollten die Auftraege unterschiedliche Einwahlaccounts besitzen, wird der Ursprungsauftrag nicht gekuendigt! In
 * diesem Fall wird eine Warnung generiert und dem User gemeldet.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.KuendigungSrcAuftragCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class KuendigungSrcAuftragCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(KuendigungSrcAuftragCommand.class);

    protected boolean kuendPossible = false;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        AuftragDaten adSrc = getAuftragDatenTx(getAuftragIdSrc());
        kuendPossible = kuendigungPossible();
        if (kuendPossible) {
            changeStatusOfOldAuftrag(adSrc);
            createVerlaufKuendigung(adSrc);
        }
        return null;
    }

    /*
     * Ueberprueft, ob der Ursprungsauftrag auf 'gekuendigt' gesetzt
     * werden darf. <br>
     * Dies ist dann der Fall, wenn die Einwahlaccounts der beteiligten
     * Auftraege identisch sind bzw. die Auftraege keinen Einwahlaccount
     * (sondern z.B. Abrechnungsaccounts) besitzen.
     */
    private boolean kuendigungPossible() {
        try {
            AuftragTechnik atSrc = getAuftragTechnikTx(getAuftragIdSrc());
            AuftragTechnik atDest = getAuftragTechnikTx(getAuftragIdDest());

            if ((atSrc.getIntAccountId() != null) && (atDest.getIntAccountId() != null)) {
                AccountService as = getCCService(AccountService.class);
                IntAccount intAccSrc = as.findIntAccountById(atSrc.getIntAccountId());
                IntAccount intAccDest = as.findIntAccountById(atDest.getIntAccountId());

                if (intAccSrc.isEinwahlaccount() && intAccDest.isEinwahlaccount()) {
                    boolean equal = NumberTools.equal(intAccSrc.getId(), intAccDest.getId());
                    if (!equal) {
                        addWarning(this, "Die Accounts der beiden Auftraege sind nicht identisch.\n" +
                                "Bitte erstellen Sie den Kuendigungsbauauftrag fuer den Ursprungsauftrag selbst!");
                    }
                    return equal;
                }
            }

            return true;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Bei der Ueberpruefung, ob der Ursprungsauftrag gekuendigt werden kann " +
                    "ist ein Fehler aufgetreten. Deshalb wurde der Ursprungsauftrag nicht auf gekuendigt gesetzt.\n\n" +
                    "Bitte erstellen Sie den Kuendigungsbauauftrag selbst!");
            return false;
        }
    }

    /**
     * Veraendert den Status des 'alten' Auftrags (=Auftrag, von dem die Physik uebernommen wird).
     */
    protected void changeStatusOfOldAuftrag(AuftragDaten adSrc) throws StoreException {
        if (adSrc != null) {
            try {
                BAService bas = getCCService(BAService.class);
                Verlauf actVerlauf = bas.findActVerlauf4Auftrag(adSrc.getAuftragId(), false);
                if (actVerlauf != null) {
                    throw new StoreException("Aenderung kann nicht durchgefuehrt werden, da fuer den " +
                            "Ursprungs-Auftrag noch ein aktiver Bauauftrag vorhanden ist!");
                }
            }
            catch (StoreException e) {
                throw e;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            try {
                adSrc.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);

                // Kuendigungsdatum pruefen und wenn nicht vorhanden abfragen!
                if (adSrc.getKuendigung() == null) {
                    Date kuendDate = ask4KuendigungsDatum();
                    if (kuendDate != null) {
                        adSrc.setKuendigung(kuendDate);
                    }
                    else {
                        addWarning(this, "Das Kuendigungsdatum fuer den Ursprungs-Auftrag konnte nicht " +
                                "gesetzt werden.\nBitte lassen Sie es nachtragen (Eintrag in DB-Services).");
                    }
                }

                CCAuftragService as = getCCService(CCAuftragService.class);
                as.saveAuftragDaten(adSrc, false);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException("Fehler bei der Status-Aktualisierung des alten Auftrags.", e);
            }
        }
    }

    /*
     * 'Fragt' nach dem Kuendigungsdatum fuer den Ursprungs-Auftrag. <br>
     * Als Vorschlag wird das Vorgabe-SCV Datum des Ziel-Auftrags mit uebergeben.
     */
    private Date ask4KuendigungsDatum() {
        try {
            Object tmp = getPreparedValue(KEY_SERVICE_CALLBACK);
            IServiceCallback serviceCallback = null;
            if (tmp instanceof IServiceCallback) {
                serviceCallback = (IServiceCallback) tmp;
            }
            else {
                return null;
            }

            AuftragDaten adDest = getAuftragDatenTx(getAuftragIdDest());
            Map<String, Date> params = new HashMap<String, Date>();
            params.put(RangierungsService.CALLBACK_PARAM_VORGABE_SCV, adDest.getVorgabeSCV());

            Object result = serviceCallback.doServiceCallback(this,
                    RangierungsService.CALLBACK_ASK_4_KUENDIGUNGS_DATUM, params);
            if (result instanceof Date) {
                return (Date) result;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Erstellt einen BA-Verlauf fuer die Kuendigung des 'alten' Auftrags. Der Verlauf wird sofort auf 'abgeschlossen'
     * gesetzt - er wird nicht an die Abteilungen verteilt (dient nur zur Dokumentation!).
     */
    protected void createVerlaufKuendigung(AuftragDaten adSrc) {
        try {
            if (adSrc == null) {
                return;
            }

            // Verlaufs-Datum auf 'heute' oder auf Kuendigungsdatum vom Auftrag setzen
            Date realDate = new Date();
            if (DateTools.isDateAfter(adSrc.getKuendigung(), realDate)) {
                realDate = adSrc.getKuendigung();
            }

            Verlauf verlauf = new Verlauf();
            verlauf.setRealisierungstermin(realDate);
            verlauf.setAkt(Boolean.FALSE);
            verlauf.setAuftragId(adSrc.getAuftragId());
            verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN);
            verlauf.setBemerkung("Kuendigungsbauauftrag von System erstellt und abgeschlossen!");
            verlauf.setStatusIdAlt(adSrc.getStatusId());

            long strategy = getStrategy();
            if (NumberTools.equal(strategy, PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME)) {
                verlauf.setAnlass(BAVerlaufAnlass.KUENDIGUNG_ANSCHLUSSUEBERNAHME);
            }
            else if (NumberTools.equal(strategy, PhysikaenderungsTyp.STRATEGY_BANDBREITENAENDERUNG)) {
                verlauf.setAnlass(BAVerlaufAnlass.KUENDIGUNG_BANDBREITENAENDERUNG);
            }

            BAService bas = getCCService(BAService.class);
            bas.saveVerlauf(verlauf);

            // technische Leistungen auf gekuendigt setzen und den Verlauf zuordnen
            // (Commands der Leistungen werden nicht ausgefuehrt!)
            CCLeistungsService ccls = getCCService(CCLeistungsService.class);
            List<Auftrag2TechLeistung> techLsExist = ccls.findAuftrag2TechLeistungen(adSrc.getAuftragId(), null, true);
            if (CollectionTools.isNotEmpty(techLsExist)) {
                for (Auftrag2TechLeistung a2tl : techLsExist) {
                    a2tl.setAktivBis(adSrc.getKuendigung());
                    a2tl.setVerlaufIdKuend(verlauf.getId());
                    ccls.saveAuftrag2TechLeistung(a2tl);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Verlauf fuer die Kuendigung des Auftrags " + getAuftragIdSrc() + " konnte nicht erstellt werden!");
            LOGGER.error(e.getMessage(), e);
        }
    }

}


