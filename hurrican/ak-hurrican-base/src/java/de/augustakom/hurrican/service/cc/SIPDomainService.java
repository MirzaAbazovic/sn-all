/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2011 09:01:21
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EGType2SIPDomain;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;

/**
 * Service-Interface fuer die Verwaltung von SIP Domain Daten.
 */
public interface SIPDomainService extends ICCService {

    /**
     * Persistiert die {@code Produkt2SIPDomain} Entität.
     */
    void saveProdukt2SIPDomain(Produkt2SIPDomain toSave, Long sessionId) throws StoreException;

    /**
     * Loescht die {@code Produkt2SIPDomain} Entität.
     */
    void deleteProdukt2SIPDomain(Produkt2SIPDomain toDelete);

    /**
     * Filtert {@code Produkt2SIPDomain} Entitaeten anhand der uebergebenen Kriterien.
     * @param prodId Produkt-ID, wenn {@code null} wird auf die Produkt-ID nicht gefiltert
     * @param hwSwitch Switchkenner, wenn {@code null} wird auf den Switch nicht gefiltert
     * @param defaultDomainsOnly Ausschliesslich default Domaenen, wenn {@code null} wird auf das Flag nicht gefiltert
     */
    List<Produkt2SIPDomain> findProdukt2SIPDomains(Long prodId, HWSwitch hwSwitch, Boolean defaultDomainsOnly);

    /**
     * Filtert {@code EGType2SIPDomain} Entitaeten anhand der uebergebenen Kriterien.
     * @param egType Das Endgeraet, wenn {@code null} wird auf die Endgeraete nicht gefiltert
     * @param hwSwitch Switchkenner, wenn {@code null} wird auf den Switch nicht gefiltert
     */
    List<EGType2SIPDomain> findEGType2SIPDomains(EGType egType, HWSwitch hwSwitch);

    CalculatedSipDomain4VoipAuftrag calculateSipDomain4VoipAuftrag(Long auftragId) throws FindException;

    /**
     * Sucht die default SIP Domäne zu einem Auftrag
     * @return Liefert die default SIP Domäne zu einem Auftrag. Wenn keiner verfügbar ist liefert die Methode {@code null}.
     */
    Reference findDefaultSIPDomain4Auftrag(Long auftragId) throws FindException;

    /**
     * Sucht alle SIP Domänen, die aufgrund Switch (von AuftagTechnik) und Produktkonfiguration möglich sind.
     */
    List<Reference> findPossibleSIPDomains4Auftrag(Long auftragId, boolean defaultDomainsOnly) throws FindException;

    /**
     * Sucht alle SIP Domänen, die aufgrund Switch und Produktkonfiguration möglich sind, sortiert nach deren Priorität
     *
     * @param prodId
     * @param auftragId
     * @param hwSwitch
     * @param defaultDomainsOnly
     * @return
     * @throws FindException
     */
    List<Reference> findPossibleSIPDomains(Long prodId, Long auftragId, HWSwitch hwSwitch, boolean defaultDomainsOnly) throws FindException;

    /**
     * Sucht nach einer SIP-Domaene, die ggf. die Endgeraete des Auftrags halten. Diese SIP-Domaene uebersteuert die
     * Domaenen des Auftrags.
     */
    public Reference findSipDomain4EgsByAuftragId(@NotNull Long auftragId) throws FindException;

    /**
     * Migriert eine SIP Domäne innerhalb eines Produktes auf einen neuen Switch <ul> <li>1) SIP Domäne nicht gesetzt ->
     * SIP Domäne bleibt weiterhin frei <li>2) SIP Domäne ist ohne Switch konfiguriert (Sonderfall Maxi Pur) -> SIP
     * Domäne bleibt unverändert <li>3) SIP Domäne ist auch auf neuem Switch konfiguriert -> SIP Domäne bleibt
     * unverändert <li>4) SIP Domäne ist nicht auf neuem Switch konfiguriert <ul> <li>4.1)default Domäne des neuen
     * Switches verfuegbar -> auf default SIP Domäne migrieren <li>4.2)default Domäne des neuen Switches nicht
     * verfuegbar -> aktuelle SIP Domäne loeschen </ul> </ul>
     */
    Reference migrateSIPDomain(Long prodId, Reference currentSIPDomain, HWSwitch destSwitch) throws FindException;

}
