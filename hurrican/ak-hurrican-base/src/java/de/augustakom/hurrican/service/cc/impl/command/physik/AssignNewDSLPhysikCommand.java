/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2005 09:41:00
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.AssignRangierung2ESCommand;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Command-Klasse, um die Physik von einem DSL-Auftrag zu entfernen und eine neue DSL-Physik zuzuordnen - falls
 * notwendig. <br> Dies ist notwendig, wenn der Phone-Anteil eines DSL-Auftrags von a/b auf ISDN (bzw. umgekehrt)
 * geaendert wird, der DSL-Auftrag dabei jedoch der gleiche bleibt. <br><br> Im Anschluss an die Neuvergabe der Physik
 * wird der EQ-Out Stift der 'alten' und 'neuen' Rangierung gekreuzt.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.AssignNewDSLPhysikCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AssignNewDSLPhysikCommand extends EqOutKreuzenCommand {

    private static final Logger LOGGER = Logger.getLogger(AssignNewDSLPhysikCommand.class);

    private Endstelle endstelleBDest = null;
    private Rangierung rangierungOfEsBSrc = null;
    private Rangierung newlyAssignedRangierung = null;
    @Autowired
    private ServiceLocator serviceLocator;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();

        Object[] values = physikKuendigung4DSLKreuzung();
        if ((values[0] != null) && (Boolean) values[0]) {
            // Wandlung des Phone-Anteils eines AK-ADSLplus Auftrags (ISDN-->a/b bzw. a/b-->ISDN)
            // und Kreuzung von EQ-Out des ADSL-Anteils
            dslPhysikKuendigen((Date) values[1]);
            dslPhysikVergabe();

            // Rangierungen muessen noch gekreuzt werden.
            eqOutKreuzung(endstelleBDest, newlyAssignedRangierung, rangierungOfEsBSrc);
        }

        return null;
    }

    /* Kuendigt die Physik des aktuellen DSL-Auftrags. */
    private void dslPhysikKuendigen(Date kuendigungsdatum) throws StoreException {
        try {
            RangierungsService rs = getCCService(RangierungsService.class);
            rs.rangierungFreigabebereit(endstelleBDest, kuendigungsdatum);

            if ((rangierungOfEsBSrc != null) && (rangierungOfEsBSrc.getFreigabeAb() == null)) {
                Date date = (kuendigungsdatum != null) ? kuendigungsdatum : new Date();
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                cal.add(Calendar.DATE, RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE);

                rangierungOfEsBSrc.setFreigabeAb(cal.getTime());
                rs.saveRangierung(rangierungOfEsBSrc, false);
            }

            EndstellenService endstellenService = getCCService(EndstellenService.class);
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            endstelleBDest.setRangierId(null);
            endstellenService.saveEndstelle(endstelleBDest);
            endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelleBDest);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die aktuelle DSL-Physik konnte nicht gekündigt werden!");
        }
    }

    /* Vergibt fuer den DSL-Auftrag eine neue Physik. */
    private void dslPhysikVergabe() throws StoreException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(AssignRangierung2ESCommand.class);
            cmd.prepare(AssignRangierung2ESCommand.ENDSTELLE_ID, endstelleBDest.getId());
            cmd.prepare(AssignRangierung2ESCommand.SERVICE_CALLBACK, getPreparedValue(KEY_SERVICE_CALLBACK));
            cmd.execute();

            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle esTmp = esSrv.findEndstelle(endstelleBDest.getId());

            RangierungsService rs = getCCService(RangierungsService.class);
            newlyAssignedRangierung = rs.findRangierungTx(esTmp.getRangierId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Physikvergabe trat ein unerwarteter Fehler auf:\n" + e.getMessage(), e);
        }
    }

    /* Prueft, ob die DSL-Physik gekuendigt werden muss.
     * Dies ist dann der Fall, wenn der zughoerige Phone-Auftrag bereits gekuendigt ist.
     * @return Object[] mit folgenden Daten:
     *   Index 0: Boolean-Objekt, ob die DSL-Physik gekuendigt werden soll
     *   Index 1: Kuendigungsdatum des zug. Buendel-Auftrags
     */
    private Object[] physikKuendigung4DSLKreuzung() throws StoreException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = getAuftragDatenTx(getAuftragIdDest());
            List<AuftragDaten> buendel = as.findAuftragDaten4BuendelTx(
                    auftragDaten.getBuendelNr(), auftragDaten.getBuendelNrHerkunft());
            if ((buendel == null) || (buendel.isEmpty())) {
                throw new StoreException("Es konnte kein Bündel-Auftrag ermittelt werden!");
            }

            boolean kuendigen = false;
            Date kuendigungsdatum = null;
            for (AuftragDaten ad : buendel) {
                if (!NumberTools.equal(auftragDaten.getAuftragId(), ad.getAuftragId())
                        && (ad.getStatusId() >= AuftragStatus.KUENDIGUNG)) {
                    kuendigen = true;
                    if ((kuendigungsdatum == null) || DateTools.isAfter(ad.getKuendigung(), kuendigungsdatum)) {
                        kuendigungsdatum = ad.getKuendigung();
                    }
                }
            }

            if (kuendigen) {
                // Pruefen, ob einer der Phone-Auftraege (mit Status <4000) noch keine Physik besitzt.
                boolean hasAuftragWithoutPhysik = false;
                for (AuftragDaten ad : buendel) {
                    if (!NumberTools.equal(auftragDaten.getAuftragId(), ad.getAuftragId())
                            && ad.isAuftragActive()
                            && NumberTools.isLess(ad.getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG)) {
                        EndstellenService esSrv = getCCService(EndstellenService.class);
                        Endstelle esB = esSrv.findEndstelle4Auftrag(ad.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
                        hasAuftragWithoutPhysik = (esB.getRangierId() == null);
                        break;
                    }
                }

                if (!hasAuftragWithoutPhysik) {
                    throw new StoreException("Es konnte kein zugehoeriger Phone-Auftrag ermittelt werden, zu " +
                            "dem eine DSL-Kreuzung ausgefuehrt werden kann.");
                }
            }

            Boolean kuendigung = (kuendigen) ? Boolean.TRUE : Boolean.FALSE;
            return new Object[] { kuendigung, kuendigungsdatum };
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Es konnte nicht ermittelt werden, ob die bestehende DSL-Physik gekuendigt werden muss.");
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
            Endstelle endstelleBSrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);

            if ((endstelleBDest == null) || (endstelleBSrc == null)) {
                throw new FindException("Die benoetigten Endstellen fuer die DSL-Physikzuordnung konnten " +
                        "nicht ermittelt werden.");
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            rangierungOfEsBSrc = rs.findRangierungTx(endstelleBSrc.getRangierId());
            if (rangierungOfEsBSrc == null) {
                throw new FindException("Die Rangierung des Ursprungs-Auftrags konnte nicht ermittelt werden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Die Daten fuer die Physik-Zuordnung konnten nicht ermittelt werden.\nGrund: " +
                    e.getMessage(), e);
        }
    }
}


