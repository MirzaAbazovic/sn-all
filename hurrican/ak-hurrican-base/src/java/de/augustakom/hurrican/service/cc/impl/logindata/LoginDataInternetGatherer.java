package de.augustakom.hurrican.service.cc.impl.logindata;

import java.time.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;

/**
 * Service bean für die Erfassung von Internet-Zugangdata
 *
 */
@Service("loginDataInternetGatherer")
public class LoginDataInternetGatherer {
    private static final Logger LOGGER = Logger.getLogger(LoginDataInternetGatherer.class);

    @Autowired
    protected AccountService accountService;
    @Autowired
    protected CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService")
    private EkpFrameContractService ekpFrameContractService;
    @Autowired
    private ProduktService produktService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private ReferenceService referenceService;
    @Autowired
    private HVTService hvtService;

    public LoginDataInternet getInternetLoginData(AuftragDaten auftragDaten, AuftragTechnik auftragTechnik) {

        final Long intAccountId = auftragTechnik.getIntAccountId();
        final IntAccount intAccount = getIntAccountById(intAccountId);
        final String pppUser = intAccount != null ? intAccount.getAccount() : null;
        final String pppPassword = intAccount != null ? intAccount.getPasswort() : null;

        final Long auftragId = auftragTechnik.getAuftragId();
        final String pppUserRealmSuffix = getPppUserRealmSuffix(auftragId);
        final IpMode ipMode = getIPMode(auftragId);

        Produkt produkt = getProdukt(auftragDaten.getProdId());
        final Integer vlanIdDaten = produkt.isPremiumDslHVt() ? null : 40;
        final Integer vlanIdVoip = getVlanIdVoip(auftragId);

        final String aftrAdress = produkt.getAftrAddress() != null ? produkt.getAftrAddress() : null;
        final Integer pbitDaten = produkt.getPbitDaten() != null ? produkt.getPbitDaten() : null;
        final Integer pbitVoip = produkt.getPbitVoip() != null ? produkt.getPbitVoip() : null;

        final boolean isHvt = isHvtStandortTyp(auftragId);

        final Integer atmParameterVPI = isHvt ? 1 : null;
        final Integer atmParameterVCI = isHvt ? 32 : null;

        return new LoginDataInternet(pppUser, pppUserRealmSuffix, pppPassword, ipMode, pbitDaten,
                pbitVoip, vlanIdDaten, vlanIdVoip, aftrAdress,
                atmParameterVPI, atmParameterVCI);
    }

    public IpMode getIPMode(Long auftragId) {
        final IpMode ipModeActualLeisungen = ccLeistungsService.queryIPMode(auftragId, LocalDate.now());
        if (ipModeActualLeisungen != null && !IpMode.IPV4.equals(ipModeActualLeisungen)) {
            // not IP4
            return ipModeActualLeisungen;
        } else {
            // null or IP4 -> try to get it from leistungen in future
            final IpMode ipModeAllLeistungen = ccLeistungsService.queryIPMode(auftragId, null); // null = all leistungen
            return ipModeAllLeistungen;
        }
    }

    private boolean isHvtStandortTyp(Long auftragId) {
        final Endstelle endstelle = findEndstelleB4Auftrag(auftragId);
        if (endstelle != null) {
            final HVTStandort hvtStandort = getHvtStandort(endstelle.getHvtIdStandort());
            final Long hvtStandortTypRefId = getHvtStandortTypRefId();
            if (hvtStandort != null) {
                return hvtStandortTypRefId != null ? hvtStandortTypRefId.equals(hvtStandort.getStandortTypRefId()) : null;
            }
        }
        return false;
    }

    private Long getHvtStandortTypRefId(){
        try {
            Long hvtStandortTypRefId = referenceService.findReference(Reference.REF_TYPE_STANDORT_TYP, "HVT").getId();
            return hvtStandortTypRefId != null ? hvtStandortTypRefId : null;
        }catch (FindException e){
            final String msg = String.format("Fehler beim Ermitteln des STANDORT_TYP,HVT aus T_REFERENCE");
            LOGGER.error(msg, e);
        }
        return null;
    }

    private Produkt getProdukt(Long productId) {
        if (productId != null) {
            try {
                return produktService.findProdukt(productId);
            }
            catch (FindException e) {
                final String msg = String.format("Fehler: Produkt mit PROD_ID: [%d] konnte nicht ermittelt werden.", productId);
                LOGGER.error(msg, e);
            }
        }
        return null;
    }

    private Integer getVlanIdVoip(Long auftragId) {
        try {
            // has voip tech. leistung
            if (ccLeistungsService.hasVoipLeistungFromNowOn(auftragId)) {
                return 40;  // constant
            } else {
                return null;
            }
        }
        catch (FindException e) {
            final String msg = String.format("Fehler bei der Ermittlung von aktuell gültigen VoiPLeistungen für den techn. Auftrag: [%d].",
                   auftragId);
            LOGGER.error(msg, e);
        }
        return null;
    }

    private IntAccount getIntAccountById(Long intAccountId) {
        try {
            return accountService.findIntAccountById(intAccountId);
        }
        catch (FindException e) {
            LOGGER.error(String.format("Fehler bei der Ermittlung der IntAccount Id: [%d]", intAccountId), e);
        }
        return null;
    }

    private String getPppUserRealmSuffix(Long auftragId) {
        try {
            return accountService.getAccountRealm(auftragId);
        }
        catch (FindException e) {
            LOGGER.error(String.format("Fehler bei der Ermittlung des AccountRealm für AuftragId: [%d]", auftragId), e);
        }
        return null;
    }

    private Endstelle findEndstelleB4Auftrag(Long auftragId) {
        try {
            final Endstelle es = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            return es;
        }
        catch (FindException e) {
            final String msg = String.format("Fehler bei der Ermittlung der Endstelle für Auftrag: [%d]", auftragId);
            LOGGER.error(msg, e);
        }
        return null;
    }

    private HVTStandort getHvtStandort(Long standortId) {
        try {
            return hvtService.findHVTStandort(standortId);
        }
        catch (FindException e) {
            final String msg = String.format("Fehler bei der Ermittlung von HHT Standort [%d]", standortId);
            LOGGER.error(msg, e);
        }
        return null;
    }

}
