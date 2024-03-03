package de.augustakom.hurrican.service.cc.impl.logindata;

import java.util.*;
import java.util.stream.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginData;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataActiveOrderNotFoundException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataImsOrderException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataNotUniqueOrderException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataNotValidProductException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataOrderNotFoundException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoip;

/**
 * Service bean für die Erfassung von Zugangdata
 *
 */
@CcTxRequiresNew
@Service
public class LoginDataService {
    private static final Logger LOGGER = Logger.getLogger(LoginDataService.class);

    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private AuftragTechnikDAO auftragTechnikDAO;
    @Autowired
    private OEService oeService;
    @Autowired
    private ReferenceService referenceService;
    @Autowired
    private LoginDataInternetGatherer loginDataInternetGatherer;
    @Autowired
    private LoginDataVoipGatherer loginDataVoipGatherer;
    @Autowired
    private BillingAuftragService billingAuftragService;

    public LoginData getLoginData(Long orderNoOrig) throws LoginDataException {
        return getLoginData(orderNoOrig, false, false);
    }

    public LoginData getLoginData(Long orderNoOrig, boolean isLoginDataInternetOnly, boolean isLoginDataVoipOnly) throws LoginDataException {

        final AuftragDaten auftragDaten = getSingleAuftrag(orderNoOrig);
        final AuftragTechnik auftragTechnik = auftragTechnikDAO.findByAuftragId(auftragDaten.getAuftragId());
        if (checkImsProduct(orderNoOrig) && isImsHwSwitch(auftragTechnik)) {
            throw new LoginDataImsOrderException();
        }

        // product check after auftrag id check
        checkProduct(orderNoOrig);

        if (isLoginDataInternetOnly) {
            final LoginDataInternet pppLoginData = loginDataInternetGatherer.getInternetLoginData(auftragDaten, auftragTechnik);
            return new LoginData(pppLoginData, null);
        }
        if (isLoginDataVoipOnly){
            final Optional<LoginDataVoip> loginDataVoip = loginDataVoipGatherer.getLoginDataVoip(auftragDaten.getAuftragId());
            return new LoginData(null, loginDataVoip.orElse(null));
        }
        else {
            final LoginDataInternet pppLoginData = loginDataInternetGatherer.getInternetLoginData(auftragDaten, auftragTechnik);
            final Optional<LoginDataVoip> loginDataVoip = loginDataVoipGatherer.getLoginDataVoip(auftragDaten.getAuftragId());
            return new LoginData(pppLoginData, loginDataVoip.orElse(null));
        }
    }

    public LoginData getLoginDataVoip(Long orderNoOrig, boolean alsoEverythingInfFuture, boolean withImsSwitchCheck, boolean withProductCheck) throws LoginDataException {
        final AuftragDaten auftragDaten = getSingleAuftrag(orderNoOrig);
        final AuftragTechnik auftragTechnik = auftragTechnikDAO.findByAuftragId(auftragDaten.getAuftragId());
        if (withImsSwitchCheck && checkImsProduct(orderNoOrig) && isImsHwSwitch(auftragTechnik)) {
            throw new LoginDataImsOrderException();
        }

        if (withProductCheck) {
            // product check after auftrag id check
            checkProduct(orderNoOrig);
        }

        final Optional<LoginDataVoip> loginDataVoip = loginDataVoipGatherer.getLoginDataVoip(auftragDaten.getAuftragId(), alsoEverythingInfFuture);
        return new LoginData(null, loginDataVoip.orElse(null));
    }

    private void checkProduct(Long orderNoOrig) throws LoginDataException {
        final Long oeNoOrig = getOeNoOrig4Auftrag(orderNoOrig);
        final Long vaterOeNoOrig = getVaterOeNoOrig4Auftrag(orderNoOrig);

        final Set<Long> oeNoOrigSet = new HashSet<>();
        if (oeNoOrig != null && oeNoOrig != 0L) {
            oeNoOrigSet.add(oeNoOrig);
        }
        if (vaterOeNoOrig != null && vaterOeNoOrig != 0L) {
            oeNoOrigSet.add(vaterOeNoOrig);
        }

        if (oeNoOrigSet.isEmpty() || !containsValidOeNoOrig(oeNoOrigSet)) {
            final String oeNoAsString = oeNoOrig != null ? oeNoOrig.toString() : "nicht vorhanden";
            final String vaterOeNoAsString = vaterOeNoOrig != null ? vaterOeNoOrig.toString() : "nicht vorhanden";

            final String msg = String.format("Die Billing Auftrag Nr. [%s] gehoert zu einer Produktgruppe "
                            + "(Billing OE_NO = [%s], VATER_OE_NO = [%s]), fuer die keine Zugangsdaten geliefert werden.",
                    orderNoOrig, oeNoAsString, vaterOeNoAsString);

            throw new LoginDataNotValidProductException(msg);
        }
    }

    protected AuftragDaten getSingleAuftrag(Long orderNoOrig) throws LoginDataOrderNotFoundException,
            LoginDataActiveOrderNotFoundException, LoginDataNotUniqueOrderException {
        final List<AuftragDaten> auftragDatenList = getAuftragDaten4OrderNoOrig(orderNoOrig);
        if (auftragDatenList.isEmpty()) {
            throw new LoginDataOrderNotFoundException();
        }
        final List<AuftragDaten> auftragDatenFilteredList = filterAuftragDaten(auftragDatenList);
        if (auftragDatenFilteredList.isEmpty()) {
            throw new LoginDataActiveOrderNotFoundException();
        } else if (auftragDatenFilteredList.size() > 1) {
            throw new LoginDataNotUniqueOrderException();
        }
        return auftragDatenFilteredList.get(0);
    }

    protected boolean isImsHwSwitch(AuftragTechnik auftragTechnik) {
        HWSwitch hwSwitch = null;
        if (auftragTechnik != null){
               hwSwitch = ccAuftragService.getSwitchKennung4Auftrag(auftragTechnik.getAuftragId());
        }
        return hwSwitch != null && HWSwitchType.IMS.equals(hwSwitch.getType());
    }

    protected boolean checkImsProduct(Long orderNoOrig) throws LoginDataException {
        try {
            final List<Reference> referenceList = referenceService.findReferencesByType(Reference.REF_NO_IMS_CHECK_4_OE_NO_TYPE, false);
            if (referenceList != null && referenceList.size() == 1) {
                final List<Long> validOeNoOrigList = Arrays.stream(referenceList.get(0).getStrValue().split(","))
                        .map(String::trim).map(Long::parseLong).collect(Collectors.toList());

                final Long oeNoOrig = getOeNoOrig4Auftrag(orderNoOrig);

                return !validOeNoOrigList.contains(oeNoOrig);
            }
            return true;
        }
        catch (FindException e) {
            throw new LoginDataException(e);
        }
    }

    private List<AuftragDaten> filterAuftragDaten(List<AuftragDaten> auftragDaten) {
        final List<AuftragDaten> active = auftragDaten.stream()
                .filter(AuftragDaten::isAuftragActiveAndInBetrieb)
                .collect(Collectors.toList());
        if (active.size() <= 1) {
            return active;
        } else {
            return active.stream()
                    .filter(ad -> !Produkt.isSdslNDraht(ad.getProdId()))
                    .filter(ad -> !( Produkt.PROD_ID_ISDN_MSN.equals(ad.getProdId()) || Produkt.PROD_ID_ISDN_TK.equals(ad.getProdId()) ) )
                    .collect(Collectors.toList());
        }
    }

    private List<AuftragDaten> getAuftragDaten4OrderNoOrig(Long orderNoOrig) {
        try {
            final List<AuftragDaten> auftragDaten4OrderNoOrig = ccAuftragService.findAuftragDaten4OrderNoOrig(orderNoOrig);
            return auftragDaten4OrderNoOrig != null ? auftragDaten4OrderNoOrig : Collections.EMPTY_LIST;
        }
        catch (FindException e) {
            LOGGER.error(String.format("Error by finding order by customer order number [%d]", orderNoOrig), e);
            return Collections.EMPTY_LIST;
        }
    }


    private boolean containsValidOeNoOrig(Set<Long> oeNos) throws LoginDataException {
        try {
            final List<Reference> referenceList = referenceService.findReferencesByType(Reference.REF_TYPE_PROVIDED_OE_NO, false);
            if (referenceList != null && referenceList.size() == 1) {
                final List<String> validOeNoOrigList = Arrays.stream(referenceList.get(0).getStrValue().split(","))
                        .map(String::trim).collect(Collectors.toList());

                final Set<String> intersection = oeNos.stream().map(Object::toString).collect(Collectors.toSet());
                intersection.retainAll(validOeNoOrigList);

                return !intersection.isEmpty();
            }
        }
        catch (FindException e) {
            throw new LoginDataException(e);
        }
        return false;
    }

    private Long getOeNoOrig4Auftrag(Long orderNoOrig) throws LoginDataException {
        try {
            BAuftrag auftrag = billingAuftragService.findAuftrag(orderNoOrig);
            return auftrag != null ? auftrag.getOeNoOrig() : null;
        }
        catch (FindException e) {
            throw new LoginDataException(e);
        }
    }

    private Long getVaterOeNoOrig4Auftrag(Long orderNoOrig) throws LoginDataException {
        try {
            return oeService.findVaterOeNoOrig4Auftrag(orderNoOrig);
        }
        catch (FindException e) {
            throw new LoginDataException(e);
        }
    }

}
