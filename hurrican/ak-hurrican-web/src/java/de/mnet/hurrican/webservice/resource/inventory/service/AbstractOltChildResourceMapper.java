package de.mnet.hurrican.webservice.resource.inventory.service;

import java.util.*;
import javax.inject.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.view.OltChildImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HWService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;

public abstract class AbstractOltChildResourceMapper<VIEW extends OltChildImportView, HW extends HWOltChild> extends AbstractResourceMapper implements RackResourceMapper {

    public static final String CHARACTERISTIC_BEZEICHNUNG = "bezeichung";
    public static final String CHARACTERISTIC_HERSTELLER = "hersteller";
    public static final String CHARACTERISTIC_SERIENNUMMER = "seriennummer";
    public static final String CHARACTERISTIC_MODELLNUMMER = "modellnummer";
    public static final String CHARACTERISTIC_OLT = "olt";
    public static final String CHARACTERISTIC_OLTRACK = "oltrack";
    public static final String CHARACTERISTIC_OLTSUBRACK = "oltsubrack";
    public static final String CHARACTERISTIC_OLTSLOT = "oltslot";
    public static final String CHARACTERISTIC_OLTPORT = "oltport";
    public static final String CHARACTERISTIC_OLTGPONID = "oltgponid";
    public static final String CHARACTERISTIC_STANDORT = "standort";
    public static final String CHARACTERISTIC_RAUMBEZEICHNUNG = "raumbezeichung";
    public static final String CHARACTERISTIC_INSTALLATIONSSTATUS = "installationsstatus";
    public static final String CHARACTERISTIC_FREIGABE = "freigabe";

    @Inject
    private CPSService cpsService;
    @Inject
    protected FTTXHardwareService fttxHardwareService;
    @Inject
    protected HWService hwService;

    abstract String getResourceSpecId();

    abstract VIEW createImportView(Resource resource);

    abstract Class<HW> getChildHwClass();

    abstract boolean isEqual(VIEW oltChildView, HW oltChildHw) throws FindException;

    abstract boolean isChildHwComplete(HW oltChildHw);

    abstract HW createChildHwFromView(VIEW oltChildView, Long sessionId) throws StoreException;

    abstract String getOltChildType();

    @Override
    public void processResource(Resource resource, Long sessionId) throws ResourceProcessException {
        if (!isResourceSupported(resource)) {
            throw new ResourceProcessException("Resource Verarbeitung abgelehnt!");
        }
        try {
            VIEW oltChildView = createImportView(resource);

            @SuppressWarnings("unchecked")
            final HW oltChildHw = (HW) hwService.findActiveRackByBezeichnung(oltChildView.getBezeichnung());

            if (oltChildView.isInstallationsstatusDeleted()) {
                handleDeleteOltChild(sessionId, oltChildHw);
                return;
            }

            if (oltChildView.getSeriennummer() != null) {
                checkAndHandleMoveOltChild(sessionId, oltChildView, oltChildHw);
            }

            if (oltChildHw == null) {
                handleNewOltChild(oltChildView, sessionId);
            }
            else {
                handleUpdateOltChild(oltChildView, oltChildHw, sessionId);
            }
        }
        catch (FindException | StoreException | ValidationException e) {
            throw new ResourceProcessException(e.getMessage(), e);
        }
    }

    protected void mapOltChildResource(Resource in, VIEW out) {
        out.setBezeichnung(in.getName());

        for (ResourceCharacteristic characteristic : in.getCharacteristic()) {
            switch (characteristic.getName().toLowerCase()) {
                case CHARACTERISTIC_HERSTELLER:
                    out.setHersteller(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_SERIENNUMMER:
                    out.setSeriennummer(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_MODELLNUMMER:
                    out.setModellnummer(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_OLT:
                    out.setOlt(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_OLTRACK:
                    out.setOltRack(extractLong(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_OLTSUBRACK:
                    out.setOltSubrack(extractLong(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_OLTSLOT:
                    out.setOltSlot(extractLong(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_OLTPORT:
                    out.setOltPort(extractLong(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_OLTGPONID:
                    out.setGponId(extractLong(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_STANDORT:
                    out.setStandort(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_RAUMBEZEICHNUNG:
                    out.setRaumbezeichung(extractString(characteristic.getValue(), 0));
                    break;
                case CHARACTERISTIC_INSTALLATIONSSTATUS:
                    out.setInstallationsstatus(extractString(characteristic.getValue(), 0));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean isResourceSupported(Resource resource) {
        if (resource == null || resource.getResourceSpec() == null) {
            return false;
        }
        return getResourceSpecId().equals(resource.getResourceSpec().getId())
                && COMMAND_INVENTORY.equals(resource.getResourceSpec().getInventory());
    }

    private void handleNewOltChild(final VIEW oltChildView, final Long sessionId) throws FindException, StoreException {
        final HW oltChildHw = createChildHwFromView(oltChildView, sessionId);
        if (oltChildHw != null && isChildHwComplete(oltChildHw)
                && cpsService.isCpsTxServiceOrderTypeExecuteable(oltChildHw.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)) {
            createAndSendCpsTxForOltChild(oltChildHw, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, sessionId);
        }
    }

    private void handleDeleteOltChild(Long sessionId, HW oltChildHw) throws FindException, StoreException, ValidationException {
        if (oltChildHw != null) {
            fttxHardwareService.checkHwOltChildForActiveAuftraegeAndDelete(oltChildHw, true, sessionId);
        }
    }

    private void checkAndHandleMoveOltChild(Long sessionId, VIEW oltChildView, HW oltChildHw) throws FindException, ResourceProcessException, StoreException, ValidationException {
        final List<HW> movedHwOltChildList = hwService.findHWOltChildBySerialNo(oltChildView.getSeriennummer(), getChildHwClass());
        if (movedHwOltChildList != null && !movedHwOltChildList.isEmpty()) {
            if (oltChildHw != null) {
                movedHwOltChildList.remove(oltChildHw);
            }
            StringBuilder strBuffAuftragsId = new StringBuilder();

            for (HW movedHwOltChild : movedHwOltChildList) {
                String strAuftragsId = fttxHardwareService.checkHwOltChildForActiveAuftraege(movedHwOltChild);
                if (strAuftragsId != null) {
                    strBuffAuftragsId.append(strAuftragsId);
                }
            }

            String resultAuftragsId = (strBuffAuftragsId.length() > 0) ? strBuffAuftragsId.toString() : null;

            if (resultAuftragsId != null) {
                String msg = "%s Update mit Seriennummer %s abgelehnt! Zur Seriennummer existieren noch %ss " +
                        "mit folgenden aktiven Auftraegen: %s";
                throw new ResourceProcessException(String.format(msg,
                        getOltChildType(), oltChildView.getSeriennummer(), getOltChildType(), resultAuftragsId));
            }
            else {
                for (HW movedHwOltChild : movedHwOltChildList) {
                    fttxHardwareService.deleteHwOltChildWithCpsTx(movedHwOltChild, true, sessionId);
                }
            }
        }
    }

    protected void handleUpdateOltChild(final VIEW oltChildView, final HW oltChildHw, final Long sessionId)
            throws ResourceProcessException, FindException, StoreException, ValidationException {
        if (!isEqual(oltChildView, oltChildHw)) {
            String msg = "Daten der Resource stimmen nicht mit einer bereits existierenden %s ueberein. " +
                    "Beim Update einer existierenden %s duerfen sich nur die Seriennummer, "
                    + "die Chassisbezeichnung und der Steckplatz unterscheiden.";
            throw new ResourceProcessException(String.format(msg, getOltChildType(), getOltChildType()));
        }

        List<CPSTransaction> activeTransactions = cpsService.findActiveCPSTransactions(null, oltChildHw.getId(),
                CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        if (activeTransactions != null && !activeTransactions.isEmpty()) {
            String msg = "Die existierende %s kann nicht aktualisiert werden, da die Anlage der %s im CPS noch laeuft.";
            throw new ResourceProcessException(String.format(msg, getOltChildType(), getOltChildType()));
        }

        if (StringUtils.isEmpty(oltChildHw.getSerialNo())) {
            if (StringUtils.isNotBlank(oltChildView.getSeriennummer())) {
                updateExistingOltChild(oltChildView, oltChildHw, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, sessionId);
            }
        }
        else {
            if (StringUtils.isEmpty(oltChildView.getSeriennummer())) {
                String msg = "Eine bereits angelegte %s mit gesetzter Seriennummer, " +
                        "darf nicht mit einer leeren Seriennummer modifiziert werden!";
                throw new ResourceProcessException(String.format(msg, getOltChildType()));
            }
            if (!oltChildHw.getSerialNo().equals(oltChildView.getSeriennummer())) {
                Long serviceOrderType = CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE;
                if (cpsService.findSuccessfulCPSTransactions(null, oltChildHw.getId(),
                        CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE).isEmpty()) {
                    serviceOrderType = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE;
                }
                updateExistingOltChild(oltChildView, oltChildHw, serviceOrderType, sessionId);
            }
            else {
                // Wenn sich nur die Chassisbezeichnung bzw. Chassisslot geaendert haben, werden diese Aenderungen
                // gespeicht, aber keine CPS-Transaktion ausgeloest.
                updateOltChildFields(oltChildView, oltChildHw);
            }
        }
    }

    protected void updateExistingOltChild(final VIEW oltChildView, final HW oltChildHw,
            final Long serviceOrderType, final Long sessionId)
            throws StoreException, ValidationException, FindException, ResourceProcessException {

        HW savedOltChildHw = updateOltChildFields(oltChildView, oltChildHw);
        if (isChildHwComplete(savedOltChildHw)) {
            if (cpsService.isCpsTxServiceOrderTypeExecuteable(oltChildHw.getId(), serviceOrderType)) {
                createAndSendCpsTxForOltChild(oltChildHw, serviceOrderType, sessionId);
            }
            else {
                throw new ResourceProcessException(String.format("Der angeforderte ServiceOrderType %d ist nicht " +
                        "ausfuehrbar!", serviceOrderType));
            }
        }
    }

    protected void createAndSendCpsTxForOltChild(final HW oltChildHw,
            final Long serviceOrderType, final Long sessionId) throws FindException, StoreException {

        if (oltChildHw.getSerialNo() == null) {
            // cannot be reached
            return;
        }

        final CPSTransactionResult cpsTxResult =
                cpsService.createCPSTransaction4OltChild(oltChildHw.getId(), serviceOrderType, sessionId);
        if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
            final CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
            cpsService.sendCpsTx2CPSAsyncWithoutNewTx(cpsTx, sessionId);
        }
    }

    protected Optional<ResourceCharacteristic> getResourceCharacteristic(Resource resource, String characteristicName) {
        return resource.getCharacteristic()
                .stream()
                .filter(resourceCharacteristic ->
                        characteristicName.equalsIgnoreCase(resourceCharacteristic.getName()))
                .findFirst();
    }

    protected abstract HW updateOltChildFields(final VIEW oltChildView, final HW oltChildHw) throws StoreException,
            ValidationException;

}
