package de.bitconex.adlatus.wholebuy.provision.it;

import de.bitconex.adlatus.wholebuy.provision.it.util.FileTestUtil;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.adapter.party.PartyClientService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaMessageHandler;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.action.InitialWitaMessageAction;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.bitconex.tmf.pm.model.Individual;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.ORDER_RECEIVED;
import static de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents.ORDER_SENT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@MockBeans({@MockBean(InitialWitaMessageAction.class)})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QEBProcessingIT extends IntegrationTestBase {
    private static final String BUSINESS_KEY = "qeb12345";
    @Autowired
    SpringStateMachineOrderProvisionWorkflowEngine workflowEngine;

    @Qualifier("QEBHandler")
    @Autowired
    WitaMessageHandler witaMessageHandler;

    @Autowired
    PartyClientService partyClientService;

    @Autowired
    ResourceOrderService resourceOrderService;

    @Autowired
    CustomOrderMapper customOrderMapper;

    @BeforeAll
    void setUp() {
        // todo: Trenutno ovaj matching ne radi bas najbolje, proverit sto??
        this.listenToHub("ResourceOrderAttributeValueChangeEvent");
        ResourceOrder resourceOrder = ResourceOrder.builder()
            .id(BUSINESS_KEY)
            .category("category")
            .state("inProgress")
            .description("description")
            .build();

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder resourceOrderDTO = customOrderMapper.mapToResourceOrderAdl(resourceOrder);
        resourceOrderService.save(resourceOrderDTO);
        workflowEngine.startProcess(BUSINESS_KEY);
        workflowEngine.sendMessage(BUSINESS_KEY,
            ORDER_RECEIVED.withHeaders(Map.of(Variables.RESOURCE_ORDER_ID.getVariableName(), resourceOrder.getId())));
        workflowEngine.sendMessage(BUSINESS_KEY, ORDER_SENT.getMessage());
    }

    @AfterAll
    void tearDown() {
        this.verifyHub("resourceOrderAttributeValueChangeEvent");
        this.destroyHub();
    }

    @Test
    @SneakyThrows
    void testProcessQEBMessage() {

        String message = FileTestUtil.readResourceContent("payloads/wita/v15/qeb/QEB_test_message.xml");


        AnnehmenMeldungRequestType annehenMeldung = null;
        try {
            annehenMeldung = MarshallUtil.unmarshall(message, AnnehmenMeldungRequestType.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        witaMessageHandler.handle(BUSINESS_KEY, annehenMeldung, "qeb_inbox_id");

        List<Individual> individuals = partyClientService.getIndividuals();

        var ro = resourceOrderService.findById(BUSINESS_KEY);

        Optional<Individual> testIndividual = individuals.stream()
            .filter(i -> i.getGivenName().equals("Ali"))
            .findFirst();

        var relatedParty = ro.getRelatedParties().get(0);

        assertThat(relatedParty.getName()).isEqualTo("Ali Geyik");
        assertThat(testIndividual).isPresent();
    }

}
