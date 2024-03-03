package de.bitconex.adlatus.wholebuy.provision.tmf.service;

import de.bitconex.adlatus.common.infrastructure.exception.OrderInvalidException;
import de.bitconex.adlatus.wholebuy.provision.service.order.OrderProcessingService;
import de.bitconex.adlatus.wholebuy.provision.service.order.OrderProcessingServiceImpl;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionWorkflowEngine;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class OrderProcessingServiceTest {
    private AutoCloseable mocks;
    @Mock
    OrderProvisionWorkflowEngine provisionWorkflowEngine;
    @Mock
    CustomOrderMapper customOrderMapper;
    @Mock
    ResourceOrderService resourceOrderService;
    @Captor
    ArgumentCaptor<Message<OrderProvisionEvents>> messageArgumentCaptor;

    private OrderProcessingService cut;

    static Object[][] dataOrderInbox() {
        final List<Object[]> objs = new ArrayList<>();
        TmfOrderInbox emptyJson = TmfOrderInbox.builder().id("order1").orderId(null).message("{}").build();
        TmfOrderInbox invalidJson = TmfOrderInbox.builder().id("order2").orderId(null).message("dsafadf988ADSFASDF").build();
        TmfOrderInbox validJson = TmfOrderInbox.builder().id("order3").orderId("1234").message("""
            {
                	"id": "1234",
                	"href": "resourceOrder/1234"
            }
            """).build();

        objs.add(new Object[]{emptyJson, null, OrderInvalidException.class});
        objs.add(new Object[]{validJson, "1234", null});
        objs.add(new Object[]{invalidJson, null, OrderInvalidException.class});

        return objs.toArray(new Object[objs.size()][]);
    }

    @BeforeEach
    void setUp() {
        mocks = openMocks(this);
        cut = new OrderProcessingServiceImpl(provisionWorkflowEngine, customOrderMapper, resourceOrderService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @ParameterizedTest
    @MethodSource("dataOrderInbox")
    void processOrder(TmfOrderInbox orderInbox, String expectedResourceOrderId, final Class<? extends Throwable> expectedException) {
        if (expectedException == null) {
            cut.processOrder(orderInbox);
            verify(provisionWorkflowEngine).startProcess(orderInbox.getOrderId());
            verify(provisionWorkflowEngine).sendMessage(eq(orderInbox.getOrderId()), messageArgumentCaptor.capture());
            Message<OrderProvisionEvents> message = messageArgumentCaptor.getValue();
            assertThat(message.getPayload()).isEqualTo(OrderProvisionEvents.ORDER_RECEIVED);
            assertThat(message.getHeaders().get(Variables.RESOURCE_ORDER_ID.getVariableName()))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(expectedResourceOrderId);
        } else {
            assertThatThrownBy(() -> cut.processOrder(orderInbox))
                .isInstanceOf(expectedException);
            verify(provisionWorkflowEngine, never()).startProcess(orderInbox.getOrderId());
        }
        verifyNoMoreInteractions(provisionWorkflowEngine);
    }
}