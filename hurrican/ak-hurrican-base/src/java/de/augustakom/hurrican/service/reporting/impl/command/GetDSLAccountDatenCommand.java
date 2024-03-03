/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2007 16:27:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import static de.augustakom.hurrican.model.cc.VerbindungsBezeichnung.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Command-Klasse, um Account- und VPN-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetDSLAccountDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDSLAccountDatenCommand.class);

    public final static String ACCOUNTTITLE = "AccountTitle";
    public final static String PRODUKT = "Produkt";
    public final static String VBZ = OLD_VBZ_NAME;
    public final static String PASSWORT = "Passwort";
    public final static String BENUTZERNAME = "Benutzername";
    public final static String AUFTRAGID = "Auftrag_ID";
    public final static String RUFNUMMER = "Rufnummer";
    public final static String EINWAHL_RN = "Einwahl_RN";
    public final static String PRINTIPS = "PrintIPs";
    public final static String IPS = "IPs";
    public final static String DSLACCOUNTS = "Accounts";
    public final static String ART = "Art";
    public final static String IP = "IP";
    public final static String GATEWAY = "Gateway";


    private Long kundeNoOrig = null;
    private Long auftragId = null;
    private Map<String, Object> map = null;

    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<>();

            readDSLAccounts();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);

        }
    }

    @Override
    public String getPrefix() {
        return DSL_ACCOUNT;
    }

    /*
     * Ermittelt alle DSL-Accounts
     */
    private void readDSLAccounts() throws HurricanServiceCommandException {
        try {
            // Ermittle, ob Account ausgelesen werden kann / muss
            ProduktService prodService = getCCService(ProduktService.class);
            ProduktGruppe prodGruppe = prodService.findPG4Auftrag(auftragId);

            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragTechnik at = as.findAuftragTechnikByAuftragId(auftragId);
            if ((at != null) && (at.getIntAccountId() != null)) {
                AccountService accs = getCCService(AccountService.class);
                IntAccount acc = accs.findIntAccountById(at.getIntAccountId());
                List<Map<String, Object>> list = new ArrayList<>();
                if (acc != null) {
                    Map<String, Object> map2 = new HashMap<>();

                    ReferenceService refService = getCCService(ReferenceService.class);

                    Produkt prod = prodService.findProdukt4Auftrag(auftragId);
                    Long prodId = prod.getProdId();
                    Reference einwahlRNReference = refService.findReference(
                            Reference.REF_TYPE_EINWAHLDN_4_PRODUKT, (prodId == null ? null : prodId.intValue()));

                    // Ermittle Produktgruppen-Name
                    map2.put(ACCOUNTTITLE, (prodGruppe != null) ? prodGruppe.getProduktGruppe() : null);

                    // Ermittle Produkt-Name
                    String prodName = prodService.generateProduktName4Auftrag(auftragId);
                    map2.put(PRODUKT, prodName);

                    // Ermittle Vbz
                    PhysikService ps = getCCService(PhysikService.class);
                    VerbindungsBezeichnung verbindungsBezeichnung = ps.findVerbindungsBezeichnungByAuftragId(auftragId);
                    map2.put(VBZ, (verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : null);

                    map2.put(PASSWORT, (NumberTools.equal(IntAccount.LINR_EINWAHLACCOUNT, acc.getLiNr())) ? acc.getPasswort() : null);
                    map2.put(BENUTZERNAME, (NumberTools.equal(IntAccount.LINR_EINWAHLACCOUNT, acc.getLiNr())) ? acc.getAccount() : null);
                    map2.put(AUFTRAGID, auftragId);
                    map2.put(RUFNUMMER, StringUtils.isNotBlank(acc.getRufnummer()) ? acc.getRufnummer() : null);
                    map2.put(EINWAHL_RN, ((einwahlRNReference != null) && StringUtils.isNotBlank(einwahlRNReference.getStrValue())) ? einwahlRNReference.getStrValue() : null);
                    List<Map<String, String>> listIPs = readIPs4Auftrag(auftragId);
                    map2.put(PRINTIPS, (listIPs != null) ? "true" : "false");
                    map2.put(IPS, listIPs);
                    list.add(map2);
                }
                map.put(getPropName(DSLACCOUNTS), list);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    private List<Map<String, String>> readIPs4Auftrag(Long auftragId) throws HurricanServiceCommandException {
        try {
            boolean loadIPs = true;
            boolean loadNets = true;

            List<Map<String, String>> ipModels = new ArrayList<>();

            EndgeraeteService egs = getCCService(EndgeraeteService.class);
            List<EG2Auftrag> egs2a = egs.findEGs4Auftrag(auftragId);
            if (CollectionTools.isNotEmpty(egs2a)) {
                boolean natActive = false;
                boolean hasEGConfig = false;
                for (EG2Auftrag eg2a : egs2a) {
                    EGConfig config = egs.findEGConfig(eg2a.getId());
                    if (config != null) {
                        hasEGConfig = true;
                        if (BooleanTools.nullToFalse(config.getNatActive())) {
                            natActive = true;
                        }

                        for (EndgeraetIp endgeraetIp : eg2a.getEndgeraetIps()) {
                            IPAddress ipRef = endgeraetIp.getIpAddressRef();
                            if ((ipRef != null) && StringUtils.isNotBlank(ipRef.getAddress())) {
                                ipModels.add(getMap4IP("M-net Endgeraet",
                                        ipRef.getAddress(),
                                        ipRef.getAddress()));
                            }
                        }
                    }
                }

                loadIPs = (!hasEGConfig) || natActive;
                loadNets = (ipModels.isEmpty());
            }

            if (loadIPs) {
                IPAddressService ipAddressService = getCCService(IPAddressService.class);
                List<IPAddress> ips = ipAddressService.findAssignedIPs4TechnicalOrder(auftragId);

                if (CollectionTools.isNotEmpty(ips)) {
                    for (IPAddress ip : ips) {
                        if (BooleanTools.nullToFalse(ip.isPrefixAddress()) && loadNets) {
                            ipModels.add(getMap4IP("IP-Netz", ip.getAddress(), null));
                        }
                        else if (!BooleanTools.nullToFalse(ip.isPrefixAddress())) {
                            ipModels.add(getMap4IP("IP-Adresse", ip.getAddress(), null));
                        }
                    }
                }
            }
            return (!ipModels.isEmpty()) ? ipModels : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }


    private Map<String, String> getMap4IP(String art, String ip, String gateway) {
        Map<String, String> map2 = new HashMap<>();
        map2.put(ART, art);
        map2.put(IP, ip);
        map2.put(GATEWAY, gateway);
        return map2;
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        tmpId = getPreparedValue(KUNDE_NO_ORIG);
        kundeNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if ((auftragId == null) || (kundeNoOrig == null)) {
            throw new HurricanServiceCommandException("AuftragId wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetDSLAccountDatenCommand.properties";
    }


}
