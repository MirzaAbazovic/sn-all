/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2010 11:57:25
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.HibernateSessionHelper;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.temp.AbstractRevokeModel;
import de.augustakom.hurrican.model.cc.temp.RevokeCreationModel;
import de.augustakom.hurrican.model.cc.temp.RevokeTerminationModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Abstrakte Command-Klasse um Auftrags-Operationen rueckgaengig zu machen.
 */
public abstract class AbstractRevokeCommand extends AbstractServiceCommand {

    /**
     * Definition der Keys fuer die Methode <code>prepare(String, Object)</code>
     */
    public static final String KEY_REVOKE_MODEL = "revoke.model";

    private static final Logger LOGGER = Logger.getLogger(AbstractRevokeCommand.class);

    protected AuftragDaten auftragDaten = null;

    protected CCAuftragService auftragService;
    protected BAService baService;
    protected CPSService cpsService;
    @Autowired
    private ServiceLocator serviceLocator;

    /**
     * Methode, um die Auftrags-Operation rueckgaengig zu machen.
     *
     * @throws HurricanServiceCommandException
     */
    protected abstract void doRevoke() throws HurricanServiceCommandException;

    /*
     * Ueberprueft, ob dem Command alle benoetigten Parameter uebergeben wurden.
     */
    protected void checkValues(AbstractRevokeModel revokeModel) throws HurricanServiceCommandException {
        try {
            if (revokeModel == null) {
                throw new HurricanServiceCommandException("Parameter nicht korrekt.");
            }

            if (revokeModel.getAuftragId() == null) {
                throw new HurricanServiceCommandException("Auftragsnummer nicht definiert.");
            }

            auftragDaten = auftragService.findAuftragDatenByAuftragId(revokeModel.getAuftragId());
            if (auftragDaten == null) {
                throw new HurricanServiceCommandException("Keine Auftragdaten gefunden!");
            }

            if (revokeModel instanceof RevokeCreationModel) {
                if (!auftragDaten.isInBetriebOrAenderung()) {
                    throw new HurricanServiceCommandException("Auftrag befindet sich nicht im Status 'in Betrieb' bzw 'Aenderung'.");
                }
            }
            else if ((revokeModel instanceof RevokeTerminationModel) && !auftragDaten.isInKuendigung()) {
                throw new HurricanServiceCommandException(
                        "Auftrag befindet sich nicht im Status 'Kuendigung Erfassung' oder später.");
            }

            if (revokeModel.getSessionId() == null) {
                throw new HurricanServiceCommandException("Session-Id nicht definiert");
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler bei der Ermittlung der Auftragsdaten: " + e.getMessage(), e);
        }
    }

    /**
     * Prueft, ob ein aktiver Bauauftrag vorhanden ist und storniert diesen gegebenenfalls. Warning wird gesetzt.
     */
    protected void revokeProvisioningOrder(AbstractRevokeModel revokeModel) throws HurricanServiceCommandException {
        try {
            Verlauf actVerlauf = baService.findActVerlauf4Auftrag(revokeModel.getAuftragId(), false);
            if (actVerlauf != null) {
                if (isRevokeTermination(revokeModel) && actVerlauf.getAnlass().equals(BAVerlaufAnlass.KUENDIGUNG)) {
                    actVerlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
                    actVerlauf.setAkt(Boolean.FALSE);
                    addWarning(this, "Kündigungsbauauftrag wurde storniert!");
                }
                else {
                    actVerlauf.setVerlaufStatusId(VerlaufStatus.VERLAUF_STORNIERT);
                    actVerlauf.setAkt(Boolean.FALSE);
                    addWarning(this, "Bauauftrag wurde storniert!");
                }

                baService.saveVerlauf(actVerlauf);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * Auftragsart umschreiben (evtl. als Auswahl)
     */
    protected void changeOrderType(AbstractRevokeModel revokeModel) throws HurricanServiceCommandException {
        try {
            if (revokeModel.getAuftragsArtId() != null) {
                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(revokeModel.getAuftragId());
                if (auftragTechnik == null) {
                    throw new HurricanServiceCommandException("Keine Daten in AuftragTechnik gefunden!");
                }

                auftragTechnik.setAuftragsart(revokeModel.getAuftragsArtId());
                auftragService.saveAuftragTechnik(auftragTechnik, Boolean.FALSE);
            }
        }
        catch (FindException | StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * Setzt den Auftrags-Status und das Kuendigungs- bzw. Inbetriebnahmedatum zurueck.
     */
    protected void changeOrderState(AbstractRevokeModel revokeModel) throws HurricanServiceCommandException {
        if (auftragDaten != null) {
            if (isRevokeTermination(revokeModel)) {
                auftragDaten.setKuendigung(null);
                auftragDaten.setStatusId(AuftragStatus.IN_BETRIEB);
            }
            else {
                auftragDaten.setInbetriebnahme(null);
                auftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
            }

            try {
                auftragService.saveAuftragDatenNoTx(auftragDaten);
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HurricanServiceCommandException(e);
            }
        }
    }

    /**
     * Erstellt eine CPS-Tx mit dem angegebenen SO-Type.
     */
    protected void sendCpsTransaction(AbstractRevokeModel revokeModel) {
        try {
            if (revokeModel.getCpsTxServiceOrderType() != null) {
                HibernateSessionHelper.flushSession(serviceLocator);

                CPSProvisioningAllowed allowed =
                        cpsService.isCPSProvisioningAllowed(auftragDaten.getAuftragId(), null, false, false, true);
                if ((allowed != null) && allowed.isProvisioningAllowed()) {
                    CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(
                            new CreateCPSTransactionParameter(auftragDaten.getAuftragId(), null, revokeModel.getCpsTxServiceOrderType(), CPSTransaction.TX_SOURCE_HURRICAN_ORDER, CPSTransaction.SERVICE_ORDER_PRIO_HIGH,
                                    new Date(), null, null, null, null, Boolean.FALSE, Boolean.FALSE, revokeModel.getSessionId())
                    );

                    if ((cpsTxResult != null) && CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
                        CPSTransaction cpsTxToSend = cpsTxResult.getCpsTransactions().get(0);
                        cpsService.sendCPSTx2CPS(cpsTxToSend, revokeModel.getSessionId());
                    }
                    else {
                        addWarning(this, "Es wurde keine CPS-Tx generiert!");
                    }

                    if ((cpsTxResult != null) && (cpsTxResult.getWarnings() != null) && cpsTxResult.getWarnings().isNotEmpty()) {
                        addWarning(this, "CPS Errors/Warnings: " + cpsTxResult.getWarnings().getWarningsAsText());
                    }
                }
                else {
                    addWarning(this, "CPS Provisioning ist fuer den Auftrag nicht moeglich!");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Error during CPS Transaction: " + e.getMessage());
        }
    }

    /**
     * Prueft, ob es sich bei der Revoke-Operation um eine Kuendigungsruecknahme oder Ruecknahme einer Inbetriebnahme
     * handelt.
     */
    boolean isRevokeTermination(AbstractRevokeModel revokeModel) {
        return (revokeModel instanceof RevokeTerminationModel);
    }

    /**
     * Injected
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Injected
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    /**
     * Injected
     */
    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }

}
