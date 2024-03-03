/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2009 09:55:40
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.math.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.DefaultServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSDNData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDNServiceData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVoIPData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVoIPIADPortsData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVoIPSIPAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CpsCallScreening;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CpsVoipRangeData;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 * Command-Klasse, um die notwendigen Daten fuer Telephonie-Daten (ueber VoIP) zu ermitteln und in ein entsprechendes
 * XML-Element einzutragen.
 *
 *
 */
public class CPSGetVoIPDataCommand extends AbstractCPSGetDNDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetVoIPDataCommand.class);

    private VoIPService voipService;
    private CCAuftragService auftragService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            List<Rufnummer> dns = getActiveDNs(getCPSTransaction().getOrderNoOrig());
            if (CollectionTools.isNotEmpty(dns)) {

                CPSVoIPData voipData = new CPSVoIPData();
                int dnIndex = 0;
                final TechLeistung sprachkanaeleLs = ccLeistungsService.findTechLeistung4Auftrag(getCPSTransaction()
                        .getAuftragId(), TechLeistung.TYP_SPRACHKANAELE, getCPSTransaction().getEstimatedExecTime());
                final Long anzSprachkanaele = (sprachkanaeleLs != null) ? sprachkanaeleLs.getLongValue() : null;

                for (final Rufnummer dn : dns) {
                    final AuftragVoIPDN voipDN = findAuftragVoipDn(dn);
                    dnIndex++;
                    voipData.addSipAccount(getCpsVoIPSIPAccountData(dnIndex, anzSprachkanaele, dn, voipDN));
                    voipData.setNumberingPlan(getCpsVoipRangeDatas(dn, voipDN));
                }

                getServiceOrderData().setVoip(voipData);
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading VoIP-Data: " + e.getMessage(), this.getClass());
        }
    }

    @Nullable
    final List<CpsVoipRangeData> getCpsVoipRangeDatas(final Rufnummer dn, final AuftragVoIPDN voipDN) {
        final List<CpsVoipRangeData> voipRanges = Lists.newArrayList();
        final VoipDnPlan activeDnPlan = voipDN.getActiveRufnummernplan(getCPSTransaction().getEstimatedExecTime());
        if (activeDnPlan != null) {
            voipRanges.addAll(
                    activeDnPlan.getVoipDnBlocks().stream()
                            .map(block ->
                                    new CpsVoipRangeData(dn.getOnKz(), dn.getDnBase(), block.getAnfang(),
                                            block.getEnde(), BooleanTools.getBooleanAsString(block.getZentrale())))
                            .collect(Collectors.toList())
            );
        }
        return (voipRanges.isEmpty()) ? null : voipRanges;
    }

    private AuftragVoIPDN findAuftragVoipDn(final Rufnummer dn) throws FindException, DefaultServiceCommandException {
        // SIP-Passwort ermitteln
        AuftragVoIPDN voipDN = voipService.findByAuftragIDDN(getCPSTransaction().getAuftragId(),
                dn.getDnNoOrig());
        if (voipDN == null) {
            throw new DefaultServiceCommandException(
                    String.format(
                            "AuftragVoIPDN konnte nicht ermittelt werden für auftragId %s und DnNoOrig %s",
                            getCPSTransaction().getAuftragId(), dn.getDnNoOrig())
            );
        }
        return voipDN;
    }

    private CPSVoIPSIPAccountData getCpsVoIPSIPAccountData(final int dnIndex, final Long anzSprachkanaele,
            final Rufnummer dn, final AuftragVoIPDN voipDN) throws FindException,
            ServiceCommandException {
        final boolean mnetDN = (dn.getBlockNoOrig() != null) && rufnummerService.isMnetBlock(dn.getBlockNoOrig());
        final HWSwitch hwSwitch = auftragService.getSwitchKennung4Auftrag(getCPSTransaction().getAuftragId());
        final CPSVoIPSIPAccountData sipAcc = new CPSVoIPSIPAccountData();
        // pruefen, ob Rufnummer von M-net oder extern
        sipAcc.transferDNData(dn);
        sipAcc.setCountryCode(AbstractCPSDNData.COUNTRY_CODE_GERMANY);
        sipAcc.setDnIndex(dnIndex); // Sortierung bereits durch DN-Ermittlung
        // sichergestellt
        sipAcc.setPassword(voipDN.getSipPassword());
        sipAcc.setSwitchKennung(hwSwitch != null ? hwSwitch.getName() : null);
        sipAcc.setMnetDN(BooleanTools.getBooleanAsString(mnetDN));
        sipAcc.setAnzahlSprachkanaele(anzSprachkanaele);
        if (!dn.isBlock()) {
            sipAcc.setSipDomain((voipDN.getSipDomain() != null) ? voipDN.getSipDomain().getStrValue() : null);
        }
        else {
            final VoipDnPlan plan = voipDN.getActiveRufnummernplan(getCPSTransaction().getEstimatedExecTime());
            if (plan == null || plan.getSipLogin() == null) {
                throw new HurricanServiceCommandException("SipLogin darf nicht leer sein!");
            }
            sipAcc.setSipLogin(plan.getSipLogin());
            sipAcc.setMainNumber(plan.getSipHauptrufnummer());
        }

        // VoIP-Option ermitteln; resultiert in entsprechendem NumberType
        // VOIP_ADD Option hat Vorrang, vor VOIP Option! (notwendig wg. NumberType ISDN/POTS)
        List<TechLeistung> voipTL = ccLeistungsService.findTechLeistungen4Auftrag(getCPSTransaction()
                .getAuftragId(), TechLeistung.TYP_VOIP_ADD, getCPSTransaction().getEstimatedExecTime());
        if (CollectionTools.isEmpty(voipTL)) {
            voipTL = ccLeistungsService.findTechLeistungen4Auftrag(getCPSTransaction()
                    .getAuftragId(), TechLeistung.TYP_VOIP, getCPSTransaction().getEstimatedExecTime());
        }

        if (CollectionTools.isNotEmpty(voipTL)) {
            TechLeistung voipTechLeistung = voipTL.get(0);
            sipAcc.setNumberType(voipTechLeistung.getStrValue());
        }

        // Rufnummernleistungen ermitteln und in SIP-Account eintragen
        loadDNServices(getCPSTransaction().getAuftragId(), dn, sipAcc, true, getSessionId());

        if (hwSwitch != null && HWSwitchType.isImsOrNsp(hwSwitch.getType())) {
            moveDnServicesToCallScreeningServicesStructure(sipAcc);
        }

        if (dn.isBlock()) {
            addBlockToSipAccData(sipAcc, dn);
        }

        final CPSVoIPIADPortsData ports = new CPSVoIPIADPortsData();
        for (final AuftragVoIPDN2EGPort a2egport : voipService.findAuftragVoIPDN2EGPortsValidAt(voipDN.getId(),
                getCPSTransaction().getEstimatedExecTime())) {
            ports.addPort(a2egport.getEgPort().getNumber());
        }
        sipAcc.setIadPorts(ports);
        return sipAcc;
    }

    private void moveDnServicesToCallScreeningServicesStructure(CPSVoIPSIPAccountData sipAcc) {
        // fuer IMS- bzw. NSP-Switche neue CALL_SCREENING Struktur verwenden und nicht unter SERVICES listen
        List<CPSDNServiceData> allDnServices = sipAcc.getDnServicesEmptyIfNull();
        Iterable<CPSDNServiceData> callScreeningServices = Iterables.filter(allDnServices, input -> {
            return CpsCallScreening.CS_OLD_NEW_MAP.containsKey(input.getServiceName());
        });

        for (CPSDNServiceData cpsdnServiceData : callScreeningServices) {
            CpsCallScreening.Type type = CpsCallScreening.CS_OLD_NEW_MAP.get(cpsdnServiceData.getServiceName());
            String rufnummern[] = cpsdnServiceData.getServiceValue().split(";");
            for (String rufnummer : rufnummern) {
                final CpsCallScreening callScreening;
                // rufnummer format: <lfd-nr>-<cc>-<lac>-<dn> oder <lfd-nr>-<cc>-<lac> oder <lfd-nr>-<cc>
                String parts[] = rufnummer.split("-");
                switch (parts.length) {
                    case 2:
                        callScreening = new CpsCallScreening(type, new BigInteger(parts[1]));
                        break;
                    case 3:
                        callScreening = new CpsCallScreening(type, new BigInteger(parts[1]), parts[2]);
                        break;
                    case 4:
                        callScreening = new CpsCallScreening(type, new BigInteger(parts[1]), parts[2],
                                new BigInteger(parts[3]));
                        break;
                    default:
                        continue;
                }
                sipAcc.addCallscreening(callScreening);
            }
        }
        allDnServices.removeAll(ImmutableList.copyOf(callScreeningServices));
        sipAcc.setDnServices(!allDnServices.isEmpty() ? allDnServices : null);
    }

    void addBlockToSipAccData(CPSVoIPSIPAccountData sipAcc, Rufnummer dn) {
        String blockStart = StringUtils.defaultIfBlank(dn.getRangeFrom(), StringUtils.EMPTY), blockEnd = StringUtils
                .defaultIfBlank(dn.getRangeTo(), StringUtils.EMPTY);

        Pair<String, String> blocks = adjustBlockLength(blockStart, blockEnd);

        sipAcc.setBlockStart(blocks.getFirst());
        sipAcc.setBlockEnd(blocks.getSecond());
    }

    // Injected
    public void setVoipService(VoIPService voipService) {
        this.voipService = voipService;
    }

    // Injected
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

}
