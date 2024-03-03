package de.bitconex.adlatus.wholebuy.provision.dto;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class TelecomInterfaceDTO {
    private TelecomInterfaceType type;
    private String majorVersion; // todo: should be removed from here or from type, its redundant
    private String minorVersion;
    private String patchVersion;
}
