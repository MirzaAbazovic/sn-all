package de.mnet.hurrican.webservice.resource.inventory.service;

import java.time.*;
import java.util.List;

import javax.annotation.Nullable;

public class AbstractResourceMapper {

    public static final String COMMAND_INVENTORY = "command";

    @Nullable
    String extractString(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return value.get(index);
    }

    @Nullable
    Long extractLong(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return Long.valueOf(value.get(index));
    }

    @Nullable
    Boolean extractBoolean(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return Boolean.valueOf(value.get(index));
    }

    @Nullable
    LocalDateTime extractDateTime(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return LocalDateTime.parse(value.get(index));
    }

    String encodeDateTime(LocalDateTime d) {
        if (d == null) {
            return null;
        }
        String encoded = d.toString();
        return encoded;
    }
}
