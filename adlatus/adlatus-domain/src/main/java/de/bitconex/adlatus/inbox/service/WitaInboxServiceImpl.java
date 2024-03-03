package de.bitconex.adlatus.inbox.service;

import de.bitconex.adlatus.common.persistence.WitaProductInboxRepository;
import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaUtil;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class WitaInboxServiceImpl implements WitaInboxService {

    private final WitaProductInboxRepository inboxRepository;

    public WitaInboxServiceImpl(WitaProductInboxRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    @Override
    @SneakyThrows
    public void save(AnnehmenMeldungRequestType request) {
        String message = MarshallUtil.marshall(request);

        String externalOrderId = WitaUtil.extractExternalOrderId(request.getMeldung().getMeldungstyp());

        inboxRepository.save(
            WitaProductInbox.builder()
                .externalOrderId(externalOrderId)
                .status(WitaProductInbox.Status.ACKNOWLEDGED)
                .message(message)
                .retries(0)
                .build());
    }

    @Override
    @SneakyThrows
    public void save(WitaProductInbox message) {
        inboxRepository.save(message);
    }

    @Override
    public List<WitaProductInbox> findByExternalOrderId(String id) {
        return inboxRepository.findByExternalOrderId(id);
    }

    @Override
    public WitaProductInbox findById(String id) {
        return inboxRepository.findById(id).orElse(null);
    }

}
