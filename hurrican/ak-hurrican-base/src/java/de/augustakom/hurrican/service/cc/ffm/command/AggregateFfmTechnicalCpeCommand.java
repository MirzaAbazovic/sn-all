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
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.ACLBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.CPEBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DHCPBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>CPE</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalCpeCommand")
@Scope("prototype")
public class AggregateFfmTechnicalCpeCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalCpeCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.billing.DeviceService")
    private DeviceService deviceService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            AuftragDaten auftragDaten = getAuftragDaten();

            if (auftragDaten.getAuftragNoOrig() != null) {
                List<Device> devices = deviceService.findDevices4Auftrag(auftragDaten.getAuftragNoOrig(), null, null);
                if (CollectionUtils.isNotEmpty(devices)) {
                    for (Device device : devices) {
                        getWorkforceOrder().getDescription().getTechParams().getCPE().add(new CPEBuilder()
                                        .withType(device.getDevType())
                                        .withSerialNumber(device.getSerialNumber())
                                        .withManufacturer(device.getManufacturer())
                                        .withModel(device.getTechName())
                                        .build()
                        );
                    }
                }
            }

            List<EG2AuftragView> views = endgeraeteService.findEG2AuftragViews(auftragDaten.getAuftragId());
            if (CollectionUtils.isNotEmpty(views)) {
                for (EG2AuftragView view : views) {
                    EGConfig egConfig = endgeraeteService.findEGConfig(view.getEg2AuftragId());

                    CPEBuilder cpeBuilder = new CPEBuilder().withType(view.getEgName());
                    if (egConfig != null) {
                        cpeBuilder
                                .withModel((egConfig.getEgType() != null) ? egConfig.getEgType().getModell() : null)
                                .withManufacturer(
                                        (egConfig.getEgType() != null) ? egConfig.getEgType().getHersteller() : null)
                                .withSerialNumber(egConfig.getSerialNumber())
                                .withSoftwarestand(egConfig.getSoftwarestand())
                                .withCpeBemerkung(egConfig.getBemerkung())
                                .withDns(egConfig.getDnsServerActive())
                                .withDnsServerIp(egConfig.getDnsServerIP())
                                .withMtu(egConfig.getMtu())
                                .withSnmpMnet(egConfig.getSnmpMNet())
                                .withSnmpCustomer(egConfig.getSnmpCustomer())
                                .withAccessData(egConfig.getAccessData())
                                .withNAT(egConfig.getNatActive())
                                .withWANVPVC(egConfig.getWanVpVc())
                                .withQoS(egConfig.getQosActive())
                                .withLayer2Protocol((egConfig.getSchicht2Protokoll() != null)
                                        ? egConfig.getSchicht2Protokoll().name() : null)
                                .withDHCP(convertDhcp(egConfig))
                                .withACL(convert(egConfig.getEndgeraetAcls()));
                    }

                    getWorkforceOrder().getDescription().getTechParams().getCPE().add(cpeBuilder.build());
                }
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams CPE Data: " + e.getMessage(), this.getClass());
        }
    }

    OrderTechnicalParams.CPE.DHCP convertDhcp(EGConfig egConfig) {
        if (BooleanTools.nullToFalse(egConfig.getDhcpActive())) {
            return new DHCPBuilder()
                    .withPoolFrom((egConfig.getDhcpPoolFromRef() != null)
                            ? egConfig.getDhcpPoolFromRef().getAddress() : null)
                    .withPoolTo((egConfig.getDhcpPoolToRef() != null)
                            ? egConfig.getDhcpPoolToRef().getAddress() : null)
                    .build();
        }
        return null;
    }

    List<OrderTechnicalParams.CPE.ACL> convert(Set<EndgeraetAcl> endgeraetAcls) {
        if (endgeraetAcls == null) {
            return null;
        }
        return endgeraetAcls
                .stream()
                .map(
                        endgeraetAcl ->
                                new ACLBuilder()
                                        .withName(endgeraetAcl.getName())
                                        .withRouterType(endgeraetAcl.getRouterTyp())
                                        .build()
                )
                .collect(Collectors.toList());
    }

}
