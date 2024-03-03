package de.bitconex.adlatus.wholebuy.provision.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDTO {
    String city;
    String zipCode;
    String street;
    String houseNumber;
    Country country;


    public enum Country {
        DE("Deutschland", "de"),
        UNKNOWN("Unknown", "unknown");

        String val;
        String val2;
        Country(String val, String val2) {
            this.val = val;
            this.val2 = val2;
        }

        public static Country fromString(String val) {
            for (Country c : Country.values()) {
                if (c.val.equals(val)) {
                    return c;
                }
            }
            return UNKNOWN;
        }

        public String toString() {
            return val2;
        }
    }
}

