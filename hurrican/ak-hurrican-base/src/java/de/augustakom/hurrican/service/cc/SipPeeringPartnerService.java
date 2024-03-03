package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * SipPeeringPartnerService
 */
public interface SipPeeringPartnerService extends ICCService {

    /**
     * Speichert den Peering Partner
     */
    public SipPeeringPartner savePeeringPartner(SipPeeringPartner sipPeeringPartner);

    /**
     * Speichert den Peering Partner
     */
    public SipPeeringPartner savePeeringPartnerWithValidation(SipPeeringPartner sipPeeringPartner) throws StoreException;

    /**
     * Ermittelt die Liste der aktiven/nicht aktiven/aller Peering Partner alphabetisch aufsteigend sortiert.
     * @param activeOnly null = alle, false = nur die nicht aktiven, true = nur die aktiven
     */
    public List<SipPeeringPartner> findAllPeeringPartner(Boolean activeOnly);

    /**
     * Ermittelt den mit <b>id</b> angegebenen Peering Partner
     */
    public SipPeeringPartner findPeeringPartnerById(Long id);

    /**
     * Ermittelt den {@link SipPeeringPartner}, der einem Auftrag zu dem angegebenen Zeitpunkt zugeordnet ist.
     */
    public SipPeeringPartner findPeeringPartner4Auftrag(Long auftragId, Date validAt) throws FindException;

    /**
     * Ermittelt alle {@link Auftrag2PeeringPartner} Zuordnungen zu dem angegebenen Auftrag. <br/>
     * Die Ermittlung erfolgt in aufsteigener Reihenfolge nach der ID.
     */
    public List<Auftrag2PeeringPartner> findAuftragPeeringPartners(Long auftragId) throws FindException;

    /**
     * Prueft, ob sich der hineingereichte Parameter mit mindestens einem anderen Peering Partner in ihren
     * Zeitspannen ueberlappt.
     */
    public void saveAuftrag2PeeringPartnerButCheckOverlapping(Auftrag2PeeringPartner toCheck) throws StoreException;

    /**
     * Speichert den angegebenen {@link Auftrag2PeeringPartner} ab.
     */
    public void saveAuftrag2PeeringPartner(Auftrag2PeeringPartner toSave) throws StoreException;

    /**
     * Ordnet dem angegebenen Auftrag einen neuen PeeringPartner zu. <br/>
     * Falls der Auftrag bereits einen PeeringPartner zu dem angegebenen Zeitraum besitzt, wird dieser zu
     * {@code validAt} deaktiviert. Somit entsteht keine Luecke zwischen den Zeitspannen.
     */
    public void addAuftrag2PeeringPartner(Long auftragId, Long peeringPartnerId, Date validAt) throws StoreException;

    /**
     * Ermittelt alle Auftraege, die dem PeeringPartner mit der Id {@code peeringPartnerId} zum angegebenen Zeitpunkt
     * zugeordnet sind und gleichzeitig aktiv sind. <br/>
     * Zusaetzlich muss auch der Auftrag zum angegebenen Zeitpunkt aktiv sein (@see AuftragDaten#isActiveAt(java.util.Date)}
     */
    public List<AuftragDaten> findActiveOrdersAssignedToPeeringPartner(Long peeringPartnerId, Date forDate)
            throws FindException;


    public AKWarnings createAndSendCpsTxForAssignedOrders(Long peeringPartnerId, Date forDate, Long sessionId)
            throws StoreException;

}
