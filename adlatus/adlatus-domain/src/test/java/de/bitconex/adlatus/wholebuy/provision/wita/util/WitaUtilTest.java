package de.bitconex.adlatus.wholebuy.provision.wita.util;

import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaUtil;
import de.telekom.wholesale.oss.v15.message.MeldungsattributeQEBType;
import de.telekom.wholesale.oss.v15.message.MeldungstypQEBType;
import de.telekom.wholesale.oss.v15.message.ObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WitaUtilTest {

    private final ObjectFactory objectFactory = new ObjectFactory();

    private MeldungstypQEBType orderConfirmationMessage;

    private String externalOrderId;

    @BeforeEach
    void setup() {
        externalOrderId = UUID.randomUUID().toString();

        orderConfirmationMessage = objectFactory.createMeldungstypQEBType();
        MeldungsattributeQEBType attribute = objectFactory.createMeldungsattributeQEBType();
        attribute.setExterneAuftragsnummer(externalOrderId);
        orderConfirmationMessage.setMeldungsattribute(attribute);
    }

    @Test
    void testExtractExternalOrderId() {
        String extractedExternalOrderId = WitaUtil.extractExternalOrderId(orderConfirmationMessage);

        assertThat(extractedExternalOrderId).isEqualTo(externalOrderId);
    }
}
