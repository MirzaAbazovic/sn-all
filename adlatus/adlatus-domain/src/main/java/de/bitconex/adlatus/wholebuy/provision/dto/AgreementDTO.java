package de.bitconex.adlatus.wholebuy.provision.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AgreementDTO {
    private String agreementNumber;
    private String customerNumber;
}
