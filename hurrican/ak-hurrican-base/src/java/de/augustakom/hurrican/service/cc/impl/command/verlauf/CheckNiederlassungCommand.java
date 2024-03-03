/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2009 11:05:54
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.NiederlassungService;


/**
 * Command prueft die Zuordnung der Niederlassung zum Auftrag. - Niederlassung muss erfasst sein - Pruefung, ob
 * Niederlassung mit Vorgabe uebereinstimmt: + Falls keine Endstelle -> Zentral + nur Endstelle B -> Niederlassung des
 * HVTs (anhand Strassenzuordnung) + Endstelle A+B -> Reseller des Kunden
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckNiederlassungCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckNiederlassungCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckNiederlassungCommand.class);

    private AuftragTechnik auftragTechnik = null;
    private Produkt produkt = null;
    private Endstelle esB = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.NiederlassungService")
    private NiederlassungService niederlassungService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            loadRequiredData();

            // Keine Niederlassung zugeordnet
            if ((auftragTechnik == null) || (auftragTechnik.getNiederlassungId() == null)) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Dem Auftrag ist keine Niederlassung zugeordnet.", getClass());
            }

            Niederlassung nl = null;
            // Falls keine Endstelle erzeugt wird -> Niederlassung Zentral
            if (NumberTools.equal(Produkt.ES_TYP_KEINE_ENDSTELLEN, produkt.getEndstellenTyp())) {
                Auftrag auftrag = auftragService.findAuftragById(auftragTechnik.getAuftragId());
                nl = (auftrag != null) ? niederlassungService.findNiederlassung4Kunde(auftrag.getKundeNo()) : null;
            }
            else if (NumberTools.equal(Produkt.ES_TYP_NUR_B, produkt.getEndstellenTyp())) {
                HVTStandort hvtStandort = hvtService.findHVTStandort(esB.getHvtIdStandort());
                if ((hvtStandort != null) && !hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_ABSTRACT)) {
                    HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(esB.getHvtIdStandort());
                    if (hvtGruppe != null) {
                        nl = niederlassungService.findNiederlassung(hvtGruppe.getNiederlassungId());
                    }
                }
            }
            // Falls Endstelle A+B -> Verwende Reseller des Kunden
            else if (NumberTools.equal(Produkt.ES_TYP_A_UND_B, produkt.getEndstellenTyp())) {
                nl = niederlassungService.findNiederlassung(Niederlassung.ID_ZENTRAL);
            }

            if ((nl != null) && NumberTools.notEqual(nl.getId(), auftragTechnik.getNiederlassungId())) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                        "Auftrag ist nicht der Niederlassung " + nl.getName() + " zugeordnet.", getClass());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der Niederlassung ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            auftragTechnik = getAuftragTechnikTx(getAuftragId());
            produkt = getProdukt();

            EndstellenService esSrv = getCCService(EndstellenService.class);
            esB = esSrv.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Daten zur HVT-Ueberpruefung: " + e.getMessage(), e);
        }
    }
}


