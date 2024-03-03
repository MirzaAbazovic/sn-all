/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.20084 15:06:05
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.mail.MailSender;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.cc.PhysikUebernahmeDAO;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingWorkflowService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Abstrakte Klasse fuer Verlauf-Commands.
 *
 *
 */
public abstract class AbstractVerlaufCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractVerlaufCommand.class);

    // DAOs
    private VerlaufDAO verlaufDAO = null;
    private PhysikUebernahmeDAO physikUebernahmeDAO = null;

    // Sonstiges
    private MailSender mailSender = null;

    /**
     * Veraendert das Freigabedatum in einer Rangierung und aktualisiert die Bemerkung (Kuendigung). (Als Freigabedatum
     * wird entweder das angegebene Datum verwendet oder das Kuendigungsbestaetigungsdatum der letzten
     * Carrierbestellung, falls dieses weiter in der Zukunft liegt.) Vorgesehen für Stornierung oder Erstellung eines
     * Kuendigungs-Bauauftrags. Bei Verwendung für einen anderen Bauauftragsanlass (!=Kuendigung) --> Exception!
     * Bedingung für die Veraenderung: ES_ID == -1
     *
     * @param verlauf  zugeordneter Verlauf
     * @param freeDate Freigabedatum fuer die Rangierung des Auftrags
     * @throws FindException
     * @throws StoreException
     * @throws ServiceNotFoundException
     * @throws HurricanServiceCommandException
     *
     */
    protected void manipulateFreigabedatum(Verlauf verlauf, Date freeDate)
            throws FindException, StoreException, ServiceNotFoundException, HurricanServiceCommandException {
        // ist Bauauftrag ein Kuendigungsbauauftrag?
        if (!NumberTools.equal(verlauf.getAnlass(), BAVerlaufAnlass.KUENDIGUNG)) {
            throw new HurricanServiceCommandException(
                    "Bauauftrag ist kein Kuendigungsbauauftrag. " +
                            "Rangierungsfreigabe darf nicht geaendert werden."
            );
        }

        EndstellenService esSrv = getCCService(EndstellenService.class);
        RangierungsService rs = getCCService(RangierungsService.class);
        ProduktService prodS = getCCService(ProduktService.class);
        CarrierService carrierService = getCCService(CarrierService.class);

        Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(orderId);

            // wenn keine Endstellen und Produkt benoetigt Endstellen --> Exception
            Produkt prod = prodS.findProdukt4Auftrag(orderId);
            boolean esFound = CollectionTools.isNotEmpty(endstellen);
            if (NumberTools.notEqual(prod.getEndstellenTyp(), Produkt.ES_TYP_KEINE_ENDSTELLEN) && !esFound) {
                throw new FindException("Endstellen konnten nicht geladen werden!");
            }

            for (Endstelle es : endstellen) {
                Long[] rangierIds = es.getRangierIds();
                // sind rangierIds vorhanden?
                if (!NumberTools.equal(rangierIds[0], null)) {
                    for (Long rId : rangierIds) {
                        Rangierung rang = rs.findRangierung(rId);
                        // Nur wenn ES_ID == -1!
                        if (NumberTools.equal(rang.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE)) {
                            Carrierbestellung lastCBOfOrder = carrierService.findLastCB4Endstelle(es.getId());

                            freeDate = ((lastCBOfOrder != null)
                                    && DateTools.isDateAfter(lastCBOfOrder.getKuendBestaetigungCarrier(), freeDate))
                                    ? lastCBOfOrder.getKuendBestaetigungCarrier()
                                    : freeDate;

                            rang.setFreigabeAb(freeDate);
                            rang.setBemerkung("Kuendigungsbauauftrag wurde verändert.");
                            rs.saveRangierung(rang, false);
                        }
                    }
                }
            }
        }
    }


    /**
     * Ueberprueft den uebergebenen Auftrag, ob er einem VPN zugeordnet ist. Ist der Auftrag einem VPN zugeordnet, so
     * wird in der uebergebenen Liste mit Abteilungs-IDS die Abteilung Dispo (falls enthalten) durch die Abteilung
     * Netzplanung ersetzt.
     *
     * @param auftragTechnik Uebergebener Auftrag
     * @param abtIds         Uebergebene Liste mit Abteilungs-IDS
     *
     */
    protected void vpnCheck(AuftragTechnik auftragTechnik, List<Long> abtIds) {
        if ((auftragTechnik.getVpnId() != null) && abtIds.contains(Abteilung.DISPO)) {
            abtIds.set(abtIds.indexOf(Abteilung.DISPO), Abteilung.NP);
        }
    }

    /**
     * Funktion liefert eine Statusmeldung an Taifun
     *
     * @param verlauf   Verlauf-Objekt
     * @param value     Zu uebertragender Wert
     * @param sessionId Id der Benutzersession
     *
     */
    protected void changeTaifunAuftragStatus(Verlauf verlauf, Long sessionId) throws StoreException {
        try {
            BillingWorkflowService stService = getBillingService(BillingWorkflowService.class);
            stService.changeOrderState4Verlauf(verlauf.getId(), sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Error updating order state in billing system: " + e.getMessage(), e);
        }
    }

    /**
     * @param verlauf
     * @param sessionId
     *
     * @see changeTaifunAuftragStatus(Verlauf, Integer) Exceptions werden unterdrueckt!
     */
    protected void changeTaifunAuftragStatusSilent(Verlauf verlauf, Long sessionId) {
        try {
            BillingWorkflowService stService = getBillingService(BillingWorkflowService.class);
            stService.changeOrderState4Verlauf(verlauf.getId(), sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @return the mailSender
     */
    public MailSender getMailSender() {
        return mailSender;
    }

    /**
     * @param mailSender the mailSender to set
     */
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * @return the verlaufDAO
     */
    public VerlaufDAO getVerlaufDAO() {
        return verlaufDAO;
    }

    /**
     * @param verlaufDAO the verlaufDAO to set
     */
    public void setVerlaufDAO(VerlaufDAO verlaufDAO) {
        this.verlaufDAO = verlaufDAO;
    }

    /**
     * @return the physikUebernahmeDAO
     */
    public PhysikUebernahmeDAO getPhysikUebernahmeDAO() {
        return physikUebernahmeDAO;
    }

    /**
     * @param physikUebernahmeDAO the physikUebernahmeDAO to set
     */
    public void setPhysikUebernahmeDAO(PhysikUebernahmeDAO physikUebernahmeDAO) {
        this.physikUebernahmeDAO = physikUebernahmeDAO;
    }
}
