package de.mnet.hurrican.acceptance.ngn;

import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;

import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Feature;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.acceptance.resource.ResourceInventoryTestVersion;

@Test(groups = BaseTest.ACCEPTANCE)
public class PortierungService_Test extends AbstractHurricanTestBuilder {

    @CitrusTest
    public void migratePortierungskennungRequest() {

        final Feature ngnPortieringWebServiceFeature = new Feature();
        ngnPortieringWebServiceFeature.setName(NGN_PORTIERING_WEB_SERVICE);
        final List<Feature> ngnFeatures = hurrican().getFeatureDAO()
                .queryByExample(ngnPortieringWebServiceFeature, Feature.class);
        final Optional<Feature> enabledNgnWSFeature = ngnFeatures.stream().filter(Feature::getFlag).findAny();

        if (enabledNgnWSFeature.isPresent()) {
            final Long orderNumber = new Long(123L);
            variables().add("billingOrderNumber", orderNumber.toString());

            simulatorUseCase(SimulatorUseCase.PortierungService_01, ResourceInventoryTestVersion.V1);
            resourceInventory().sendMigratePortierungskennungRequest("migratePortierungskennungRequest");
            resourceInventory().receiveMigratePortierungskennungResponse("migratePortierungskennungResponse");

            action(new AbstractTestAction() {
                @Override
                public void doExecute(TestContext testContext) {
                    // do some validations here
                }
            });
        }

    }

    @Test(enabled = false)
    @CitrusTest
    public void migratePortierungskennungRequest_2016565() {
        simulatorUseCase(SimulatorUseCase.PortierungService_01, ResourceInventoryTestVersion.V1);

        final Long orderNumber = new Long(2016565L);
        variables().add("billingOrderNumber", orderNumber.toString());
        variables().add("cpsServiceOrderType", "modifySubscriber");

/*
        // request
        resourceInventory().sendMigratePortierungskennungRequest("migratePortierungskennungRequest");

        // CPS
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceResponseAck");

        // response
        resourceInventory().receiveMigratePortierungskennungResponse("migratePortierungskennungResponse");
*/

        // citrus want to have response immediately therefore need to parallel it
        parallel(
                resourceInventory().sendMigratePortierungskennungRequest("migratePortierungskennungRequest"),

                sequential(
                        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest"),
                        cps().sendCpsAsyncServiceRequestAck("cpsServiceResponseAck")
                )
        );
    }

}
