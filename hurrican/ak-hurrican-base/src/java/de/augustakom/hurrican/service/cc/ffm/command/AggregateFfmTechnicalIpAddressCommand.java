/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>IPAddress</li>
 *     <ul>
 *         <li>die dem Auftrag zugeordneten IP-Adressen</li>
 *         <li>die IP-Adressen, die den Endgeraeten zugeordnet sind (werden mit Typ 'M-net Endgeraet' versehen)</li>
 *     </ul>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalIpAddressCommand")
@Scope("prototype")
public class AggregateFfmTechnicalIpAddressCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalIpAddressCommand.class);
    public static final String V4 = "v4";
    public static final String V6 = "v6";

    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressService")
    private IPAddressService ipAddressService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService egService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            loadIPsFromAuftrag();
            loadIPsFromEgConfig();

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams IPAddress Data: " + e.getMessage(), this.getClass());
        }
    }

    void loadIPsFromAuftrag() throws FindException {
        AuftragDaten auftragDaten = getAuftragDaten();
        if (auftragDaten.getAuftragNoOrig() != null) {
            final Date validAt = DateConverterUtils.asDate(getReferenceDate());
            Collection<IPAddressPanelView> ips = ipAddressService.findAllIPAddressPanelViews(auftragDaten.getAuftragNoOrig());
            ips = Collections2.filter(ips, new Predicate<IPAddressPanelView>() {
                @Override
                public boolean apply(@Nullable IPAddressPanelView input) {
                    return (input != null && input.getIpAddress() != null)
                            && DateTools.isDateBetween(validAt, input.getIpAddress().getGueltigVon(), input.getIpAddress().getGueltigBis());
                }
            });

            if (CollectionUtils.isNotEmpty(ips)) {
                for (IPAddressPanelView ipAddress : ips) {
                    IPAddress ip = ipAddress.getIpAddress();
                    String purpose = (ip.getPurpose() != null) ? ip.getPurpose().getStrValue() : null;
                    String type = StringTools.join(new String[]{purpose, ip.getIpType().name()}, " - ", true);

                    getWorkforceOrder().getDescription().getTechParams().getIPAddress().add(
                            new IPAddressBuilder()
                                    .withAddress(ipAddress.getIpAddress().getAddress())
                                    .withVersion((ipAddress.getIpAddress().isIPV4()) ? V4 : V6)
                                    .withType(type)
                                    .build()
                    );
                }
            }
        }
    }


    void loadIPsFromEgConfig() throws FindException {
        Optional<List<EG2Auftrag>> egs2a = Optional.of(egService.findEGs4Auftrag(getAuftragId()));
        if (egs2a.isPresent()) {
            List<EG2Auftrag> egs2aWithIps = egs2a.get().stream()
                    .filter(eg2a -> CollectionUtils.isNotEmpty(eg2a.getEndgeraetIps()))
                    .collect(Collectors.toList());

            List<OrderTechnicalParams.IPAddress> ipsToAdd = new ArrayList<>();
            for (EG2Auftrag eg2Auftrag : egs2aWithIps) {
                EGConfig egConfig = egService.findEGConfig(eg2Auftrag.getId());
                ipsToAdd.addAll(convertEgIPsToIpAddress(egConfig, eg2Auftrag.getEndgeraetIps()));
            }

            if (CollectionUtils.isNotEmpty(ipsToAdd)) {
                getWorkforceOrder().getDescription().getTechParams().getIPAddress().addAll(ipsToAdd);
            }
        }
    }


    List<OrderTechnicalParams.IPAddress> convertEgIPsToIpAddress(EGConfig egConfig, Set<EndgeraetIp> ips) {
        if (ips == null) {
            return Collections.emptyList();
        }

        return ips.stream()
                .filter(ip -> ip.getIpAddressRef() != null)
                .map(ip -> new IPAddressBuilder()
                        .withAddress(ip.getIpAddressRef().getEgDisplayAddress())
                        .withVersion((ip.getIpAddressRef().isIPV4()) ? V4 : V6)
                        .withType(String.format("%s %s", "M-net Endgerät", ip.getAddressTypeFormatted(egConfig)))
                        .build())
                .collect(Collectors.toList());
    }

}
