/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.13
 */
package de.mnet.wbci.acceptance.kft;

import java.time.*;
import com.consol.citrus.TestActor;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueOrnKftBuilder;
import de.mnet.wbci.acceptance.common.role.AbstractTestRole;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.helper.WbciDateUtils;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallBuilder;

/**
 * Role holds special test activities for Kft test runs such as sending messages directly to Atlas ESB without using the
 * hurrican services. Special Kft test requirements should be represented here.
 *
 *
 */
public class KftTestRole extends AbstractTestRole {

    @Autowired
    @Qualifier("hurricanTestActor")
    protected TestActor hurricanTestActor;

    @Autowired
    @Qualifier("hurricanCarrierNegotiationServiceEndpoint")
    private JmsEndpoint hurricanCarrierNegotiationEndpoint;

    /**
     * Send carrier change update message directly to ATLAS ESB.
     *
     * @param payloadTemplate
     * @return
     */
    public SendMessageActionDefinition sendCarrierChangeUpdate(String payloadTemplate) {
        return sendCarrierChange(payloadTemplate, "update");
    }

    /**
     * Send message directly to ATLAS ESB via JMS message sender without using the Hurrican-Services. This means that
     * business logic, marshalling etc., which are done within Hurrican will be skipped and the specified xml template
     * will be placed on the JMS queue.
     */
    private SendMessageActionDefinition sendCarrierChange(String payloadTemplate, String actionPrefix) {
        return (SendMessageActionDefinition) testBuilder.send(hurricanCarrierNegotiationEndpoint)
                .header("SoapAction", String.format("/CarrierNegotiationService/%sCarrierChange", actionPrefix))
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending carrier change %s", actionPrefix));
    }

    /**
     * Creates new WbciGeschaeftsfallKueOrn instance and stores it to the database.
     *
     * @param vorabstimmungsId
     * @param abgebenderEKP
     * @param aufnehmenderEKP
     * @return the persisted GF
     */
    public WbciGeschaeftsfallKueOrn createAndStoreGeschaeftsfallKueOrn(String vorabstimmungsId,
            CarrierCode abgebenderEKP, CarrierCode aufnehmenderEKP) {
        WbciGeschaeftsfallKueOrnKftBuilder kueOrnKftBuilder = new WbciGeschaeftsfallKueOrnKftBuilder(wbciCdmVersion);

        kueOrnKftBuilder.withVorabstimmungsId(vorabstimmungsId);
        kueOrnKftBuilder.withAbgebenderEKP(abgebenderEKP);
        kueOrnKftBuilder.withAufnehmenderEKP(aufnehmenderEKP);

        return createAndStoreGeschaeftsfall(kueOrnKftBuilder);
    }

    /**
     * Creates new WbciGeschaeftsfallKueMrn instance and stores it to the database.
     *
     * @param vorabstimmungsId
     * @param abgebenderEKP
     * @param aufnehmenderEKP
     * @return the persisted GF
     */
    public WbciGeschaeftsfallKueMrn createAndStoreGeschaeftsfallKueMrn(String vorabstimmungsId,
            CarrierCode abgebenderEKP, CarrierCode aufnehmenderEKP) {
        return createAndStoreGeschaeftsfall(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion)
                        .withVorabstimmungsId(vorabstimmungsId)
                        .withAbgebenderEKP(abgebenderEKP)
                        .withAufnehmenderEKP(aufnehmenderEKP)
        );
    }

    @Override
    public <GF extends WbciGeschaeftsfall> GF createAndStoreGeschaeftsfall(
            WbciGeschaeftsfallBuilder<GF> wbciGeschaeftsfallBuilder) {
        GF wbciGeschaeftsfall = super.createAndStoreGeschaeftsfall(wbciGeschaeftsfallBuilder);
        createVariables(wbciGeschaeftsfall);

        return wbciGeschaeftsfall;
    }

    /**
     * Creates mandatory test variables for a manual processed wbci geschaeftsfall instance. Same as {@link
     * de.mnet.wbci.citrus.actions.CreateWbciVorgangTestAction} would do when creating via Hurrican remote service
     * call.
     *
     * @param wbciGeschaeftsfall any kind of {@link de.mnet.wbci.model.WbciGeschaeftsfall}
     */
    public void createVariables(WbciGeschaeftsfall wbciGeschaeftsfall) {
        testBuilder.variable(VariableNames.PRE_AGREEMENT_ID, wbciGeschaeftsfall.getVorabstimmungsId());

        testBuilder.variable(VariableNames.CARRIER_CODE_ABGEBEND, wbciGeschaeftsfall.getAbgebenderEKP()
                .getITUCarrierCode());
        testBuilder.variable(VariableNames.CARRIER_CODE_AUFNEHMEND, wbciGeschaeftsfall.getAufnehmenderEKP()
                .getITUCarrierCode());

        testBuilder.variable(VariableNames.REQUESTED_CUSTOMER_DATE,
                WbciDateUtils.formatToWbciDate(wbciGeschaeftsfall.getKundenwunschtermin().atStartOfDay()));
    }

    public void cleanUpVariables() {
        // add finally block for testdata cleanup to all test cases
        testBuilder.doFinally(
                testBuilder.catchException(
                        testBuilder.variables().add(VariableNames.PRE_AGREEMENT_ID_CLEANUP,
                                "wbci:createVorabstimmungsId('DEU.BACKUP')"),
                        testBuilder.sql(hurricanDataSource)
                                .sqlResource("classpath:database/01_cleanup_vorabstimmungsIds.sql")
                                .ignoreErrors(true)
                )
        );
    }

    public void purgeAllJmsQueues() {
        if (!hurricanTestActor.isDisabled()) {
            purgeJmsQueue(hurricanCarrierNegotiationEndpoint);
        }
    }

}
