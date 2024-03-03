package de.bitconex.adlatus.wholebuy.provision.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.adlatus.common.util.json.JsonUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonUtilTest {

    @Test
    void testCreateObjectMapper() {
        ObjectMapper objectMapper = JsonUtil.createObjectMapper();

        assertThat(objectMapper).isNotNull();
    }
}
