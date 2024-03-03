package de.bitconex.adlatus.wholebuy.provision.service.scheduling;

import de.bitconex.adlatus.common.model.WitaProductOutbox;
import de.bitconex.adlatus.common.persistence.WitaProductOutboxRepository;
import de.bitconex.adlatus.wholebuy.provision.adapter.wita.WitaServiceClient;
import de.bitconex.adlatus.wholebuy.provision.dto.constants.Constants;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.SpringStateMachineOrderProvisionWorkflowEngine;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragRequestType;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragResponseType;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WitaOutboxServiceImpl implements WitaOutboxService { // todo: split scheduler and service
    private static final long PROCESSING_INTERVAL_IN_MS = 10000;

    private final WitaProductOutboxRepository outboxRepository;

    private final WitaServiceClient witaServiceClient;

    private final SpringStateMachineOrderProvisionWorkflowEngine stateMachine;

    @Value("${wita.api.basepath.url}:")
    private String witaApiBasePath;


    public WitaOutboxServiceImpl(WitaProductOutboxRepository outboxRepository, WitaServiceClient witaServiceClient, @Lazy SpringStateMachineOrderProvisionWorkflowEngine stateMachine) {
        this.outboxRepository = outboxRepository;
        this.witaServiceClient = witaServiceClient;
        this.stateMachine = stateMachine;
    }

    @Override
    public void save(String orderId, String message) {
        var msg = WitaProductOutbox
            .builder()
            .message(message)
            .status(WitaProductOutbox.Status.CREATED)
            .externalOrderId(orderId)
            .retries(0)
            .build();
        outboxRepository.save(msg);
    }

    @Async
    @Scheduled(fixedRate = PROCESSING_INTERVAL_IN_MS)
    @Override
    @Transactional
    public void check() {
        Optional<WitaProductOutbox> productOutboxes = outboxRepository.findFirstByStatusOrderByRetriesAsc(WitaProductOutbox.Status.CREATED);

        if (productOutboxes.isEmpty()) {
            log.info("No new WITA outbox items found");
            return;
        }

        if (productOutboxes.get().getRetries() >= Constants.MAX_RETRY) {
            log.info("WITA outbox message retries exceeded");
            return;
        }

        WitaProductOutbox witaProductOutbox = productOutboxes.get();

        log.info("Processing new WITA outbox items.");
        this.process(witaProductOutbox);
        log.info("Finished new outbox items processing");


        witaProductOutbox.setStatus(WitaProductOutbox.Status.SENT);
        WitaProductOutbox save = outboxRepository.save(witaProductOutbox);
        log.info("Processing WITA outbox message: {}, {}", save.getExternalOrderId(), save.getStatus());
    }

    @Override
    public void process(WitaProductOutbox productOutbox) {
        log.debug("Processing WITA outbox message: {}", productOutbox);
        AnnehmenAuftragRequestType request;

        try {
            request = MarshallUtil.unmarshall(productOutbox.getMessage(), AnnehmenAuftragRequestType.class);
        } catch (JAXBException exception) {
            log.error(exception.getMessage());
            // TODO: Make custom exception
            throw new RuntimeException(exception);
        }


        AnnehmenAuftragResponseType response = witaServiceClient.sendOrder(request, "".equals(witaApiBasePath) ? null : witaApiBasePath);

        if (isResponseOk(response)) {
            stateMachine.sendMessage(productOutbox.getExternalOrderId(), OrderProvisionEvents.ORDER_SENT.getMessage());
            productOutbox.setStatus(WitaProductOutbox.Status.SENT);
            WitaProductOutbox save = outboxRepository.save(productOutbox);
            log.info("Processing WITA outbox message: {}, {}", save.getExternalOrderId(), save.getStatus());
        } else {
            productOutbox.setRetries(productOutbox.getRetries() + 1);
            if (productOutbox.getRetries() >= Constants.MAX_RETRY) {
                productOutbox.setStatus(WitaProductOutbox.Status.ERROR);
            }
            WitaProductOutbox save = outboxRepository.save(productOutbox);
            log.info("Processing WITA outbox message failed: {}, {}", save.getExternalOrderId(), save.getStatus());
        }
    }

    @Override
    public List<WitaProductOutbox> findByExternalOrderId(String id) {
        return outboxRepository.findByExternalOrderId(id);
    }

    private boolean isResponseOk(AnnehmenAuftragResponseType response) {
        // TODO: Check meldung TEQ response is OK
        return response != null;
    }
}
