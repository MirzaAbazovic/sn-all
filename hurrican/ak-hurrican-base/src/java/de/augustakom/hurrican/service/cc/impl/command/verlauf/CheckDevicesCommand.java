package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.billing.PurchaseOrderService;

/**
 *  Prüft ob ein FritzBox gebucht oder zugewiesen ist. Eine Warning wenn
 *      - Dem Auftrag ist keine FritzBox zugeordnet oder gekauft ist
 *      - Dem Auftrag ist mehr als 1 FritzBox zugeordnet
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDevicesCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckDevicesCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckDevicesCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.billing.DeviceService")
    private DeviceService deviceService;
    @Resource(name = "de.augustakom.hurrican.service.billing.PurchaseOrderService")
    private PurchaseOrderService purchaseOrderService;

    @Override
    public Object execute() throws Exception {
        try {
            final Long auftragNoOrig = getBillingAuftrag().getAuftragNoOrig();
            final List<Device> activeIadDevices = getActiveIadDevices(auftragNoOrig, getRealDate());
            final Map<Device, PurchaseOrder> purchasedIadDevices = getPurchasedIadDevices(auftragNoOrig);

            final int amountOfActiveIadDevices = activeIadDevices.size();
            final int amountOfPurchasedIadDevices = purchasedIadDevices.size();

            if ( (amountOfActiveIadDevices + amountOfPurchasedIadDevices) == 0) {
                final String warning = "Dem Auftrag ist keine FritzBox zugeordnet oder die Lieferung ist noch nicht aktiviert.";
                addWarning(this, warning);
            } else if (amountOfActiveIadDevices > 1) {
                final String warning = String.format("Dem Auftrag sind %d FritzBoxen zugeordnet.", amountOfActiveIadDevices);
                addWarning(this, warning);
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    String.format("Fehler bei der Ueberpruefung der Port / CPE Kombination: %s", e.getMessage()), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }


    private Map<Device, PurchaseOrder> getPurchasedIadDevices(Long auftragNoOrig) throws FindException {
        final List<Device> devices = deviceService.findOrderedDevices4Auftrag(auftragNoOrig, Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_IAD);

        final ImmutableMap.Builder<Device, PurchaseOrder> devicesWithOrders = ImmutableMap.builder();
        for (Device device : devices) {
            if (device.getPurchaseOrderNo() != null) {
                final PurchaseOrder purchaseOrder = purchaseOrderService.findPurchaseOrder(device.getPurchaseOrderNo());
                if (purchaseOrder != null && purchaseOrder.isActivatedOrDone()) {
                    devicesWithOrders.put(device, purchaseOrder);
                }
            }
        }

        return devicesWithOrders.build();
    }

    private List<Device> getActiveIadDevices(Long auftragNoOrig, Date realDate) throws FindException {
        final List<Device> devices = deviceService.findDevices4Auftrag(auftragNoOrig, Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_IAD);
        return devices.stream()
                .filter(d -> d.isValid(realDate))
                .collect(Collectors.toList());
    }
}
