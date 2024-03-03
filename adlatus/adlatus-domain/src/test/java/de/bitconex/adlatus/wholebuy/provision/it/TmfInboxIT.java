package de.bitconex.adlatus.wholebuy.provision.it;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Status;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TmfOrderInbox;
import de.bitconex.adlatus.wholebuy.provision.service.order.TmfOrderInboxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class TmfInboxIT extends IntegrationTestBase {

    @Autowired
    TmfOrderInboxService orderInboxService;

    @Test
    void testSaveToInbox() {
        TmfOrderInbox order = TmfOrderInbox.builder()
            .id("12345")
            .orderId("A5678")
            .message("{ \"A\": 1 }")
            .status(Status.fromValue("acknowledged"))
            .build();
        orderInboxService.save(order);

        TmfOrderInbox orderDB = orderInboxService.findById("12345").orElse(null);
        assertThat(orderDB).isNotNull().isInstanceOf(TmfOrderInbox.class);
    }
}
