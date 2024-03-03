/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2011 12:19:05
 */
package de.mnet.wita.acceptance.common;

import java.time.*;
import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;

/**
 * DTO
 */
public class CreatedData {
    public Auftrag auftrag;
    public AuftragDaten auftragDaten;
    public AuftragTechnik auftragTechnik;
    public List<Endstelle> endstellen = new ArrayList<>();
    public Carrierbestellung carrierbestellung;
    public CCAddress address;
    public AKUser user;
    public GeoId geoId;
    public GeoId2TechLocation geoId2TechLocation;
    public Set<CBVorgangSubOrder> cbVorgangSubOrders = new HashSet<>();
    public Equipment dtagPort;
    public List<Ansprechpartner> ansprechpartner = new ArrayList<>();
    public LocalDateTime vorgabeMnet;
    public AuftragDaten referencingAuftragDaten;
    public AuftragTechnik referencingAuftragTechnik;
    public List<Endstelle> referencingEndstellen;
    public Carrierbestellung referencingCarrierbestellung;
    //    public ProduktBezeichner produktBezeichner;
    public String montagehinweis;
    public String terminReservierungsId;
    public boolean anbieterwechselTKG46;
    public Set<Pair<ArchiveDocumentDto, String>> archiveDocuments;
    public String projektKenner;
    public String vorabstimmungsId;
    public Uebertragungsverfahren previousUetv;
    public boolean vierDraht = false;
    public List<Rufnummer> rufnummern = new ArrayList<>();
}
