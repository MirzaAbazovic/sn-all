/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.14
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.billing.PurchaseOrderService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command-Klasse fuer die Bauauftragsertellung, die fuer das Hurrican Produkt 'FTTX Telefon' gedacht ist und
 * die Kombination aus verwendetem Port und CPE (Endgeraet) prueft. <br/>
 * Die Checks: <br/>
 * <ul>
 *     <li>VDSL Port: als CPE muss eine FritzBox gebucht sein; Versand der FritzBox muss getriggert sein</li>
 *     <li>POTS Port: es darf KEINE FritzBox gebucht sein!</li>
 * </ul>
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckPortCpeCombinationFttxTelefonCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckPortCpeCombinationFttxTelefonCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckPortCpeCombinationFttxTelefonCommand.class);

    @Autowired
    EndstellenService endstellenService;
    @Autowired
    RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.billing.DeviceService")
    private DeviceService deviceService;
    @Resource(name = "de.augustakom.hurrican.service.billing.PurchaseOrderService")
    private PurchaseOrderService purchaseOrderService;

    @Override
    public Object execute() throws Exception {
        try {
            // Rangierung ermitteln und Port-Typ pruefen
            Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            Assert.notNull(endstelleB, "Endstelle B des Auftrags konnte nicht ermittelt werden!");
            Assert.notNull(endstelleB.getRangierId(), "Der Endstelle B des Auftrags ist keine Rangierung zugeordnet!");

            Rangierung rangierung = rangierungsService.findRangierung(endstelleB.getRangierId());
            Assert.notNull(rangierung,
                    String.format("Rangierung mit Id %s wurde nicht gefunden!", endstelleB.getRangierId()));
            Assert.notNull(rangierung.getEqInId(), "Rangierung besitzt keinen EQ-In Port!");

            Equipment eqIn = rangierungsService.findEquipment(rangierung.getEqInId());
            Assert.notNull(eqIn, String.format("Equipmnet (IN) zur Rangierung konnte nicht ermittelt werden!"));

            List<Device> devices = getDevicesAtRealDate();

            if (eqIn.isPotsPort()) {
                // bei POTS: FritzBox darf NICHT gebucht sein
                if (isFritzBoxAssigned(devices)) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            String.format("Dem Auftrag ist eine FritzBox zugeordnet.%n" +
                                    "Dies ist bei Realisierung über einen POTS Port allerdings nicht zulässig!"),
                            getClass());
                }
            }
            else {
                // bei VDSL:
                //  + FritzBox muss gebucht sein
                //  + FritzBox Versand muss getriggert sein
                if (!isFritzBoxAssigned(devices) || !isFritzBoxPurchaseOrderActivated(devices)) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            String.format("Dem Auftrag ist keine FritzBox zugeordnet oder die Lieferung ist noch nicht aktiviert.%n" +
                                    "Dies ist bei Realisierung über einen VDSL/ETH Port allerdings unbedingt notwendig!"),
                            getClass());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    String.format("Fehler bei der Ueberpruefung der Port / CPE Kombination: %s", e.getMessage()), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /**
     * Ermittelt alle IADs (=FritzBox), die zum Realisierungstermin des Bauauftrags gueltig sind. <br/>
     * Falls noch kein IAD geliefert wurde und somit auch kein Gueltigkeitsdatum geprueft werden kann werden als
     * Fallback die Geraetebestellungen verwendet.
     * @return
     * @throws FindException
     */
    private List<Device> getDevicesAtRealDate() throws FindException {
        List<Device> devices = deviceService.findDevices4Auftrag(getBillingAuftrag().getAuftragNoOrig(),
                Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_IAD);
        boolean noDevicesAssignedToOrder = CollectionUtils.isEmpty(devices);

        CollectionUtils.filter(devices, new Predicate() {
            @Override
            public boolean evaluate(Object obj) {
                return ((Device) obj).isValid(getRealDate());
            }
        });

        if (noDevicesAssignedToOrder) {
            devices = deviceService.findOrderedDevices4Auftrag(getBillingAuftrag().getAuftragNoOrig(),
                    Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_IAD);
        }

        return devices;
    }


    /**
     * Prueft, ob dem (Billing) Auftrag eine FritzBox (=IAD) zugeordnet ist.
     * @throws ServiceCommandException
     */
    boolean isFritzBoxAssigned(List<Device> devices) throws FindException {
        return CollectionUtils.isNotEmpty(devices);
    }

    /**
     * Prueft, ob fuer die FritzBox des Auftrags eine Lieferung in Taifun aktiviert (oder bereits abgeschlossen) ist.
     * @return
     * @throws ServiceCommandException
     */
    boolean isFritzBoxPurchaseOrderActivated(List<Device> devices) throws FindException {
        for (Device device : devices) {
            if (device.getPurchaseOrderNo() != null) {
                // PurchaseOrder ermitteln und auf Status pruefen
                PurchaseOrder purchaseOrder = purchaseOrderService.findPurchaseOrder(device.getPurchaseOrderNo());
                if (purchaseOrder != null && purchaseOrder.isActivatedOrDone()) {
                    return true;
                }
            }
        }

        return false;
    }

}
