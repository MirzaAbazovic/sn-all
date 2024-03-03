package de.augustakom.hurrican.service.exceptions.helper;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.HandleErrorTestBuilder;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class HandleErrorHelperTest {

    @Test
    public void testHandleErrorToString() throws Exception {
        HandleError handleError = new HandleErrorTestBuilder().buildValid();
        handleError.getError().setTime(null);
        String errorMessage = HandleErrorHelper.handleErrorToString(handleError);

        assertEquals(errorMessage, "ESB-ERROR-Message:\n"
                + "ESB_TrackingId=T123,\n"
                + "Error=[errorCode=123, message=some message, details=some details],\n"
                + "Message=[JMSEndpoint=some endpoint, Payload=some payload, Context=[contextkey=valContext], JMSProperties=[key=val]],\n"
                + "Component=[host=localhost, name=some name, service=svc, operation=op, processId=1, processName=p1],\n"
                + "BusinessKey=[businesskey=1000333]");
    }
}