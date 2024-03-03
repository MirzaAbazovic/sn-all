package de.bitconex.adlatus.wholebuy.provision.service.scheduling;

import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaMessageHandler;
import de.bitconex.adlatus.common.persistence.WitaProductInboxRepository;
import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaMessageHandlerResolver;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class WitaInboxProcessingServiceImpl implements WitaInboxProcessingService { // todo: split scheduler and service

    private static final long PROCESSING_INTERVAL_IN_MS = 10000;

    private final WitaProductInboxRepository inboxRepository;
    private final WitaMessageHandlerResolver messageHandlerResolver;

    public WitaInboxProcessingServiceImpl(WitaProductInboxRepository inboxRepository, WitaMessageHandlerResolver messageHandlerResolver) {
        this.inboxRepository = inboxRepository;
        this.messageHandlerResolver = messageHandlerResolver;
    }

    @Async
    @Scheduled(fixedRate = PROCESSING_INTERVAL_IN_MS)
    @Override
    @Transactional
    public void check() {
        var productInboxOptional = inboxRepository.findFirstByStatusOrderByRetriesAsc(WitaProductInbox.Status.ACKNOWLEDGED);

        if (productInboxOptional.isEmpty()) {
            log.debug("No new WITA inbox items found");
            return;
        }
        WitaProductInbox productInbox = productInboxOptional.get();

        log.debug("Processing new WITA inbox items.");
        this.process(productInbox);
        log.debug("Finished new inbox items processing");

        productInbox.setStatus(WitaProductInbox.Status.PROCESSED);
        inboxRepository.save(productInbox);
    }

    void process(WitaProductInbox productInbox) { // todo: move this out, its not schedulers job
        log.debug("Processing WITA inbox message: {}", productInbox);
        AnnehmenMeldungRequestType request;
        try {
            request = MarshallUtil.unmarshall(productInbox.getMessage(), AnnehmenMeldungRequestType.class);
        } catch (JAXBException e) {
            log.error("Marshalling exception: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        WitaMessageHandler messageHandler = messageHandlerResolver.getMessageHandler(request.getMeldung().getMeldungstyp());
        messageHandler.handle(productInbox.getExternalOrderId(), request, productInbox.getId());
    }
}
