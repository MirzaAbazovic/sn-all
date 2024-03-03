/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 11.04.2016

 */

package de.mnet.hurrican.acceptance.resourceorder;


import static de.mnet.hurrican.ffm.citrus.VariableNames.*;
import static java.util.Collections.*;

import java.time.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.builder.EkpDataBuilder;
import de.mnet.hurrican.acceptance.builder.StandortDataBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Created by petersde on 11.04.2016.
 */
public abstract class AbstractPortTestBuilder extends AbstractHurricanTestBuilder {

    @Autowired
    private EkpDataBuilder ekpDataBuilder;
    @Autowired
    private StandortDataBuilder standortDataBuilder;

    protected EkpDataBuilder.EkpData ekpData;

    protected StandortDataBuilder.StandortData standortData;

    protected void simulatorUseCase(SimulatorUseCase useCase) {
        simulatorUseCase(useCase, ResourceOrderTestVersion.V1);
    }

    protected void createTestdata() throws Exception {
        ekpData = ekpDataBuilder.build();
        standortData = standortDataBuilder.build();
    }

    protected void addXmlVariables() {
        variables().add(ORDER_ID, "HER_CITRUS_TEST");
        variables().add(EXT_ORDER_ID, "EXT_CITRUS_TEST");
        variables().add(PRODUCT_GROUP, "FTTB 50_BSA");
        variables().add(PRODUCT_NAME, "FttB BSA 50/10");
        variables().add(NOTIFICATION_TYPE, "reservePortResponse");
    }

    protected ReceiveMessageActionDefinition reservePort(String executionDate) throws Exception {
        createTestdata();
        addXmlVariables();
        variables().add(EKP_ID, ekpData.ekpFrameContract.getEkpId());
        variables().add(FRAME_CONTRACT_ID, ekpData.ekpFrameContract.getFrameContractId());
        variables().add(GEO_ID, String.valueOf(standortData.geoId.getId()));
        variables().add(DESIRED_EXECUTION_DATE, executionDate);
        variables().add(LINE_ID, "");
        resourceInventory().sendReservePortResourceOrderManagementServiceRequest("reservePort").fork(true);
        return atlas().receiveReservePortResourceOrderManagementNotificationServiceResponse("reservePortSuccessNotification");
    }

    protected void bauauftragAnFFMVerteilen() {
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                final Auftrag auftrag = hurrican()
                        .getAuftragDAO()
                        .findActiveOrdersByLineIdAndAuftragStatus(context.getVariable(LINE_ID), AuftragStatus.TECHNISCHE_REALISIERUNG)
                        .get(0);

                final Verlauf verlauf = hurrican()
                        .getVerlaufDAO()
                        .findActive(auftrag.getAuftragId() , false)
                        .get(0);

                final VerlaufAbteilung verteiler = hurrican()
                        .getVerlaufAbteilungDAO()
                        .findByVerlaufAndAbtId(verlauf.getId(), Abteilung.DISPO, null);

                final SelectAbteilung4BAModel ffmAbteilung =
                        new SelectAbteilung4BAModel(Abteilung.FFM, Niederlassung.ID_MUENCHEN, null,
                                DateConverterUtils.asDate(LocalDate.parse(context.getVariable("executionDate"))));

                try {
                    hurrican()
                            .getBaService()
                            .dispoVerteilenManuell(verlauf.getId(), verteiler.getId(), singletonList(ffmAbteilung), emptyList(), null);
                }
                catch (StoreException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    protected void auftragAbschliessen() {
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {

                final Auftrag auftrag = hurrican()
                        .getAuftragDAO()
                        .findActiveOrdersByLineIdAndAuftragStatus(context.getVariable(LINE_ID), AuftragStatus.TECHNISCHE_REALISIERUNG)
                        .get(0);

                final Verlauf verlauf = hurrican()
                        .getVerlaufDAO()
                        .findActive(auftrag.getAuftragId() , false)
                        .get(0);

                final VerlaufAbteilung ffm = hurrican()
                        .getVerlaufAbteilungDAO()
                        .findByVerlaufAndAbtId(verlauf.getId(), Abteilung.FFM, null);

                try {

                    hurrican().getBaService().finishVerlauf4FFMWithDeleteOrder(ffm, "hurrican", "", DateConverterUtils.asDate(LocalDate.now()), null,  null,false, null);


                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    final void releasePortKuendigung() throws Exception {
        simulatorUseCase(SimulatorUseCase.Resource_Order_Management);
        //port anlegen
        final ReceiveMessageActionDefinition reservePortSuccess = reservePort(LocalDate.now().toString());

        reservePortSuccess.extractFromPayload("lineId", LINE_ID);

        variables().add(ORDER_ID, "HER_CITRUS_TEST");
        variables().add(RELEASE_DATE, LocalDate.now().toString());
        variables().add(NOTIFICATION_TYPE, "bauauftragChangedResponse");
        variables().add("executionDate", LocalDate.now().toString());

        bauauftragAnFFMVerteilen();

        atlas().receiveCreateOrder("ffmCreateOrder");

        auftragAbschliessen();

        atlas().receiveDeleteOrder("ffmDeleteOrder");

        atlas().receiveResourceOrderManagementNotification("bauauftragChangedResponse");

        //Anschluus in Betreib

        //Kuendigung senden
        variables().add(DESIRED_EXECUTION_DATE, LocalDate.now().plusDays(1).toString());
        resourceInventory().sendReleasePortResourceOrderManagementServiceRequest("releasePortKuendigung");

        //Response bewerten
        variables().add(ORDER_ID, "HER_CITRUS_TEST");
        variables().add(NOTIFICATION_TYPE, "releasePortResponse");

        atlas().receiveResourceOrderManagementNotification("releasePortKuendigungSuccessNotification");
    }

}
