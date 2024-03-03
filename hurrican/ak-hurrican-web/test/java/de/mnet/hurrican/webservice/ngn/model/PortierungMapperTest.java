package de.mnet.hurrican.webservice.ngn.model;

import static de.mnet.hurrican.ngn.portierungservice.PortierungStatusEnumWeb.*;
import static org.testng.Assert.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.ngn.portierungservice.BillingOrderNumberWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungResponseWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungStatusEntryWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationResponseWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationStatusEnumWeb;

/**
 */
@Test(groups = BaseTest.UNIT)
public class PortierungMapperTest {

    public void testToInternalPortierungRequest() throws Exception {
        PortierungRequestWeb portierungRequest = new PortierungRequestWeb();
        BillingOrderNumberWeb billingOrderNumber = new BillingOrderNumberWeb();
        billingOrderNumber.setBillingOrderNumber(1234L);
        portierungRequest.getBillingOrderNumbers().add(billingOrderNumber);
        PortierungRequest request = PortierungMapper.toInternalPortierungRequest(portierungRequest);
        Assert.assertFalse(request.getBillingOrderNumbers().isEmpty());

        Long orderNumber = request.getBillingOrderNumbers().get(0);
        assertEquals(orderNumber, Long.valueOf(1234L));
    }

    public void testToExternalPortierungResponse() throws Exception {
        PortierungWarning hint = new PortierungWarning("das ist ein hint");
        PortierungResult result = new PortierungResult();
        result.addWarning(1234L, hint);
        result.putStatus(1234L, PortierungStatusEnum.ERROR, "test");

        PortierungResponse response = new PortierungResponse(result);
        PortierungResponseWeb portierungResponseWeb = PortierungMapper.toExternalPortierungResponse(response);

        Assert.assertFalse(portierungResponseWeb.getPortierungStatusEntries().isEmpty());
        PortierungStatusEntryWeb portierungStatusEntryWeb = portierungResponseWeb.getPortierungStatusEntries().get(0);
        Assert.assertEquals(portierungStatusEntryWeb.getBillingOrderNumber().getBillingOrderNumber(), 1234L);
        Assert.assertEquals(portierungStatusEntryWeb.getPortierungStatus().getPortierungStatusEnum(), ERROR);
        Assert.assertEquals(portierungStatusEntryWeb.getPortierungStatus().getHints().size(), 1);
    }

    public void testToInternalValidationRequest() throws Exception {
        ValidationRequestWeb validationRequest = new ValidationRequestWeb();
        BillingOrderNumberWeb billingOrderNumber = new BillingOrderNumberWeb();
        billingOrderNumber.setBillingOrderNumber(1234L);
        validationRequest.setBillingOrderNumber(billingOrderNumber);
        ValidationRequest internalValidationRequest = PortierungMapper.toInternalValidationRequest(validationRequest);

        Assert.assertEquals(internalValidationRequest.getBillingOrderNumber(), 1234L);
    }

    public void testToExternalValidationResponse() throws Exception {
        ValidationStatusEnum statusEnum = ValidationStatusEnum.MIGRATION_POSSIBLE;
        ValidationStatus status = new ValidationStatus(statusEnum, "bla");
        long orderNoOrig = 1234L;
        ValidationResponse validationResponse = new ValidationResponse(orderNoOrig, status);
        ValidationResponseWeb response = PortierungMapper.toExternalValidationResponse(validationResponse);

        Assert.assertEquals(response.getBillingOrderNumber().getBillingOrderNumber(), orderNoOrig);
        Assert.assertEquals(response.getValidationStatus().getMessage(), "bla");
        Assert.assertEquals(response.getValidationStatus().getValidationStatusEnum(), ValidationStatusEnumWeb.MIGRATION_POSSIBLE);
    }
}