package de.bitconex.adlatus.wholebuy.provision.wita.util;

import de.bitconex.adlatus.common.util.xml.MarshallUtil;
import de.bitconex.adlatus.wholebuy.provision.it.util.FileTestUtil;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragRequestType;
import de.telekom.wholesale.oss.v15.envelope.ObjectFactory;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class MarshalUtilTest {

    @Test
    void testMarshalling() throws JAXBException {
        AnnehmenAuftragRequestType object = new ObjectFactory().createAnnehmenAuftragRequestType();
        String content = MarshallUtil.marshall(object);

        assertThat(content).isNotNull();
        assertThat(content.getClass()).isEqualTo(String.class);
    }

    @Test
    void testUnmarshalling() throws IOException, JAXBException {
        String content = FileTestUtil.readResourceContent("payloads/wita/v15/wita_order_template.xml");
        AnnehmenAuftragRequestType object = MarshallUtil.unmarshall(content, AnnehmenAuftragRequestType.class);

        assertThat(object).isNotNull();
        assertThat(object.getClass()).isEqualTo(AnnehmenAuftragRequestType.class);
    }
}
