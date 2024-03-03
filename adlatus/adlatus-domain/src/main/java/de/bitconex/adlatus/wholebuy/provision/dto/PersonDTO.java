package de.bitconex.adlatus.wholebuy.provision.dto;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;

@Data
@Builder
public class PersonDTO {
    private int salutation;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    @Nullable
    private String organizationName;
}
