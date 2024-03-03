/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2005 07:54:57
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractAssignRangierungCommand;
import de.augustakom.hurrican.service.cc.impl.command.AssignRangierung2ESCommand;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Command-Klasse, um die EQ-Out-ID der Endstelle 'B' zweier Auftraege zu kreuzen. <br> Der Endstelle 'B' des neuen
 * Auftrags wird zuerst eine neue Rangierung zugewiesen. Dabei wird die übergebene Uevt-Cluster-No berücksichtigt, falls
 * übergeben.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.EqOutKreuzenCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EqOutKreuzenCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(EqOutKreuzenCommand.class);

    private Endstelle endstelleBDest = null;
    private Endstelle endstelleBSrc = null;
    private Rangierung rangierungOfEsBDest = null;
    private Rangierung rangierungOfEsBSrc = null;
    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();

        assignRangierung2EsB();

        eqOutKreuzung(endstelleBDest, rangierungOfEsBDest, rangierungOfEsBSrc);
        return null;
    }

    /**
     * In dieser Methode werden die EQ-Out Stifte der Rangierungen <code>rangierungDest</code> und
     * <code>rangierungSrc</code> gekreuzt. <br> Dabei werden von den Rangierungen Historisierungs-Datensaetze angelegt.
     * <br> Die 'alte' Endstelle B behaelt die Rangierungs-ID - diese Rangierung ist allerdings nicht mehr gueltig. <br>
     * Die 'neue' Endstelle B erhaelt dagegen die ID der Rangierung mit den gekreuzten Stiften.
     *
     * @param esDest         Endstelle des Ziel-Auftrags
     * @param rangierungDest 'neue' Rangierung
     * @param rangierungSrc  'alte' Rangierung
     * @throws FindException  wenn nicht alle benoetigten Daten vorhanden sind
     * @throws StoreException wenn beim Kreuzen der EQ-Out Stifte ein Fehler auftritt.
     */
    protected void eqOutKreuzung(Endstelle esDest, Rangierung rangierungDest, Rangierung rangierungSrc) throws FindException, StoreException {
        if ((rangierungDest == null) || (rangierungSrc == null)) {
            throw new FindException("Die zu kreuzenden Rangierungen konnten nicht ermittelt werden!");
        }
        try {
            LOGGER.debug("######## EQ-Out kreuzen #######");
            LOGGER.debug("   Endstelle (Ziel): " + esDest.getId());
            LOGGER.debug("   Rangierung (Ziel): " + rangierungDest.getId());
            LOGGER.debug("   Rangierung (Src) : " + rangierungSrc.getId());
            LOGGER.debug("###############################");

            RangierungsService rangierungsService = getCCService(RangierungsService.class);

            // pruefen, ob die zu kreuzende Rangierung noch gueltig ist
            if (DateTools.isDateBefore(rangierungSrc.getGueltigBis(), DateTools.getHurricanEndDate())) {
                throw new FindException("Die urspruengliche Rangierung ist bereits historisiert.\n" +
                        "Sie kann fuer die Kreuzung nicht mehr verwendet werden!");
            }
            // pruefen, ob die zu kreuzenden Rangierungen unterschiedliche EQ-Out IDs besitzen
            if (NumberTools.equal(rangierungDest.getEqOutId(), rangierungSrc.getEqOutId())) {
                throw new FindException("Die beiden Rangierungen besitzen den gleichen Carrier-Stift.\n" +
                        "Die Kreuzung kann deshalb nicht durchgefuehrt werden.");
            }
            // pruefen, ob die zu kreuzenden Rangierungen auf dem gleichen HVT sind
            if (NumberTools.notEqual(rangierungDest.getHvtIdStandort(), rangierungSrc.getHvtIdStandort())) {
                throw new FindException("Die beiden Rangierungen sind auf unterschiedlichen HVts.\n" +
                        "Die Kreuzung kann deshalb nicht durchgefuehrt werden.");
            }

            // EqOut-IDs kreuzen (Rangierungen werden historisiert!)
            Long newEqOutId = rangierungDest.getEqOutId();
            Long oldEqOutId = rangierungSrc.getEqOutId();

            LOGGER.info("Physik-Wandlung - kreuze EQ-Out-IDs (neu/alt): " + newEqOutId + "/" + oldEqOutId);
            rangierungDest.setEqOutId(oldEqOutId);
            rangierungDest.setEsId(esDest.getId());
            Rangierung rangierung4NewES = rangierungsService.saveRangierung(rangierungDest, true);

            rangierungSrc.setEqOutId(newEqOutId);
            Rangierung rangierung4OldES = rangierungsService.saveRangierung(rangierungSrc, true);

            EndstellenService endstellenService = getCCService(EndstellenService.class);
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            esDest.setRangierId(rangierung4NewES.getId());  // Endstelle die neue Rangierungs-ID uebergeben
            endstellenService.saveEndstelle(esDest);
            endgeraeteService.updateSchicht2Protokoll4Endstelle(esDest);

            // 'alte' Rangierung muss auf inaktiv gesetzt werden
            rangierung4OldES.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
            rangierungsService.saveRangierung(rangierung4OldES, false);

            preventCpsProvisioning();
        }
        catch (FindException e) {
            throw e;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Kreuzung des EQ-Out Stifts ist ein Fehler aufgetreten:\n" +
                    e.getMessage(), e);
        }
    }

    /* Setzt das Flag 'PREVENT_CPS_PROV' auf den beteiligten Auftraegen */
    private void preventCpsProvisioning() {
        try {
            AuftragTechnik auftragTechnikSrc = getAuftragTechnikTx(getAuftragIdSrc());
            AuftragTechnik auftragTechnikDest = getAuftragTechnikTx(getAuftragIdDest());

            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            if ((auftragTechnikSrc != null) && !BooleanTools.nullToFalse(auftragTechnikSrc.getPreventCPSProvisioning())) {
                auftragTechnikSrc.setPreventCPSProvisioning(Boolean.TRUE);
                auftragService.saveAuftragTechnik(auftragTechnikSrc, false);
            }

            if ((auftragTechnikDest != null) && !BooleanTools.nullToFalse(auftragTechnikDest.getPreventCPSProvisioning())) {
                auftragTechnikDest.setPreventCPSProvisioning(Boolean.TRUE);
                auftragService.saveAuftragTechnik(auftragTechnikDest, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            endstelleBDest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);
            endstelleBSrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            if ((endstelleBDest == null) || (endstelleBSrc == null)) {
                throw new FindException("Die fuer die Stift-Kreuzung benoetigten Endstellen konnten nicht " +
                        "ermittelt werden!");
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            rangierungOfEsBDest = rs.findRangierungTx(endstelleBDest.getRangierId());
            rangierungOfEsBSrc = rs.findRangierungTx(endstelleBSrc.getRangierId());
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Vergibt fuer die Enstelle-B eine neue Rangierung unter Beachtung der UEVT-Cluster-No. und lädt Endstelle-B neu
     */
    private void assignRangierung2EsB() throws StoreException {
        try {
            RangierungsService rs = getCCService(RangierungsService.class);
            Equipment eqOut = rs.findEquipment(rangierungOfEsBSrc.getEqOutId());
            if (eqOut == null) {
                throw new FindException("Keinen EqOut-Stift für die Quell-Rangierung gefunden.");
            }
            Uebertragungsverfahren uebertragungsverfahren = eqOut.getUetv();
            if (uebertragungsverfahren == null) {
                throw new FindException("Der EqOut-Stift der Quell-Rangierung hat kein Übertragungsverfahren gesetzt.");
            }
            if (StringUtils.isBlank(eqOut.getRangVerteiler())) {
                throw new FindException("Der EqOut-Stift der Quell-Rangierung hat keinen Üvt gesetzt.");
            }
            Integer uevtClusterNo = eqOut.getUevtClusterNo();
            if (uevtClusterNo == null) {
                throw new FindException("Der EqOut-Stift der Quell-Rangierung hat keine ÜVt-Cluster-Nr.");
            }

            IServiceCommand cmd = serviceLocator.getCmdBean(AssignRangierung2ESCommand.class);
            cmd.prepare(AbstractAssignRangierungCommand.ENDSTELLE_ID, endstelleBDest.getId());
            cmd.prepare(AssignRangierung2ESCommand.UEVT_CLUSTER_NO, uevtClusterNo);
            cmd.prepare(AssignRangierung2ESCommand.EQ_UEBERTRAGUNGSVERFAHREN, uebertragungsverfahren);
            cmd.prepare(AbstractAssignRangierungCommand.SERVICE_CALLBACK, getPreparedValue(KEY_SERVICE_CALLBACK));
            cmd.execute();

            endstelleBDest = getCCService(EndstellenService.class).findEndstelle(endstelleBDest.getId());
            rangierungOfEsBDest = getCCService(RangierungsService.class).findRangierungTx(endstelleBDest.getRangierId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Physikvergabe trat ein unerwarteter Fehler auf:\n" + e.getMessage(), e);
        }
    }
}
