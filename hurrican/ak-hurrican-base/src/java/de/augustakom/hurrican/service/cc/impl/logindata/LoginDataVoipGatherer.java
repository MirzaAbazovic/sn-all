package de.augustakom.hurrican.service.cc.impl.logindata;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoip;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoipDn;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Service bean für die Erfassung von Sprach-/SIP-Zugangsdata
 *
 */
@Service("loginDataVoipGatherer")
public class LoginDataVoipGatherer {
    private static final Logger LOGGER = Logger.getLogger(LoginDataVoipGatherer.class);

    @Autowired
    protected VoIPService voIPService;

    public Optional<LoginDataVoip> getLoginDataVoip(Long auftragId) throws LoginDataException {
        return this.getLoginDataVoip(auftragId, false);
    }

    public Optional<LoginDataVoip> getLoginDataVoip(Long auftragId, boolean alsoEverythingInfFuture) throws LoginDataException {
        final LocalDate today = LocalDate.now();
        final AuftragVoIP voIP4Auftrag = getVoIP4Auftrag(auftragId);
        final ImmutableList.Builder<LoginDataVoipDn> dnListBuilder = ImmutableList.builder();
        if ( voIP4Auftrag == null || !voIP4Auftrag.getIsActive()) {
            return Optional.empty();
        }

        try {
            List<AuftragVoipDNView> auftragVoipDNView = voIPService.findVoIPDNView(auftragId);
            if (!auftragVoipDNView.isEmpty()){
                for (AuftragVoipDNView auftragVoipDN: auftragVoipDNView){
                    if (!auftragVoipDN.isBlock()){
                        final LoginDataVoipDn loginDataVoipDn = getLoginDataVoipEinzelrufnummer(auftragVoipDN, today);
                        if (loginDataVoipDn != null) {
                            dnListBuilder.add(loginDataVoipDn);
                        }
                    }else{
                        final List<LoginDataVoipDn> lst = getLoginDataVoipDnBlock(auftragVoipDN, today, alsoEverythingInfFuture);
                        dnListBuilder.addAll(lst);
                    }
                }
            }
        }
        catch (FindException e) {
            throw new LoginDataException(e);
        }
        final List<LoginDataVoipDn> voipDnList = dnListBuilder.build();
        if ( !voipDnList.isEmpty()) {
            return Optional.of(new LoginDataVoip(voipDnList));
        } else {
            return Optional.empty();
        }
    }

    private LoginDataVoipDn getLoginDataVoipEinzelrufnummer(AuftragVoipDNView auftragVoipDNView, LocalDate today) {

        if (DateConverterUtils.asLocalDate(auftragVoipDNView.getGueltigBis()).isBefore(today)){
            return null;
        }
        String sipHauptrufnummer = voIPService.generateSipHauptrufnummer(auftragVoipDNView.getOnKz(),auftragVoipDNView.getDnBase(), null);
        if ((sipHauptrufnummer != null || sipHauptrufnummer.isEmpty()) &&
                (auftragVoipDNView.getSipDomain() != null || auftragVoipDNView.getSipDomain().getStrValue().isEmpty())) {
            String sipDomain = auftragVoipDNView.getSipDomain().getStrValue();
            String sipPassword = auftragVoipDNView.getSipPassword();
            return new LoginDataVoipDn(sipHauptrufnummer, sipDomain, sipPassword);
        }else{
            LOGGER.warn(String.format("Keine VoipDnDaten fuer AuftragVoIPDN, techn. Auftrag: [%d] und Datum: [%s] vorhanden.",
                    auftragVoipDNView.getAuftragId(), today));
            return null;
        }
    }

    private List<LoginDataVoipDn> getLoginDataVoipDnBlock(AuftragVoipDNView auftragVoipDNView, LocalDate today, boolean alsoEverythingInfFuture) throws FindException {
        final List<LoginDataVoipDn> result = new ArrayList<>();
        final Optional<VoipDnPlanView> activePlan = auftragVoipDNView.getActiveVoipDnPlanView(DateConverterUtils.asDate(today));
        final List<VoipDnPlanView> futurePlans = auftragVoipDNView.getVoipDnPlanViewsAfterDate(DateConverterUtils.asDate(today));
        if (activePlan.isPresent()) {
            addPlan(result, activePlan.get(), auftragVoipDNView);
        }
        futurePlans.forEach(p -> addPlan(result, p, auftragVoipDNView));
        if (alsoEverythingInfFuture) {
            return result;
        }
        else {
            if (result.size() > 1) {
                return Collections.singletonList(result.get(0));
            }
            else {
                return result;  // one element or empty
            }
        }
    }

    private void addPlan(List<LoginDataVoipDn> result, VoipDnPlanView plan, AuftragVoipDNView auftragVoipDNView) {
        if (plan != null) {
            final String sipHauptrufnummer = plan.getSipHauptrufnummer();
            if ((sipHauptrufnummer != null || sipHauptrufnummer.isEmpty()) &&
                    (auftragVoipDNView.getSipDomain() != null || auftragVoipDNView.getSipDomain().getStrValue().isEmpty())) {
                final String sipDomain = auftragVoipDNView.getSipDomain().getStrValue();
                final String sipPassword = auftragVoipDNView.getSipPassword();
                final LocalDate validFrom = plan.getGueltigAb() != null && DateConverterUtils.asLocalDate(plan.getGueltigAb()).isAfter(LocalDate.now())
                        ? DateConverterUtils.asLocalDate(plan.getGueltigAb()) : null;
                final LoginDataVoipDn dataVoipDn = new LoginDataVoipDn(sipHauptrufnummer, sipDomain, sipPassword, validFrom);
                result.add(dataVoipDn);
            }
            else {
                LOGGER.warn(String.format("Kein VoipDnPlan fuer AuftragVoIPDN, techn. Auftrag: [%d] ",
                        auftragVoipDNView.getAuftragId()));
            }
        }
    }

    private AuftragVoIP getVoIP4Auftrag(Long auftragId) {
        try {
            return voIPService.findVoIP4Auftrag(auftragId);
        }
        catch (FindException e) {
            throw new RuntimeException(String.format("Fehler: fuer den techn. Auftrag: [%d] konnten keine VoIP-Daten ermittelt werden.", auftragId), e);
        }
    }
}
