package de.bitconex.adlatus.wholebuy.provision.it.util;

import de.bitconex.tmf.rcm.model.CharacteristicValueSpecification;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rcm.model.ResourceSpecificationCharacteristic;

import java.util.List;

public class ResourceCatalogTestUtil {


    public static ResourceSpecification createResourceSpecification() {
        return ResourceSpecification.builder()
                .name("L2BSA")
                .resourceSpecCharacteristic(List.of(
                        ResourceSpecificationCharacteristic.builder()
                                .name("upstream")
                                .resourceSpecCharacteristicValue(List.of(
                                        CharacteristicValueSpecification.builder()
                                                .value("100")
                                                .valueType("integer")
                                                .unitOfMeasure("Mbit/s")
                                                .build(),
                                        CharacteristicValueSpecification.builder()
                                                .value("200")
                                                .valueType("integer")
                                                .unitOfMeasure("Mbit/s")
                                                .build()
                                ))
                                .build(),
                        ResourceSpecificationCharacteristic.builder()
                                .name("downstream")
                                .resourceSpecCharacteristicValue(List.of(
                                        CharacteristicValueSpecification.builder()
                                                .value("50")
                                                .valueType("integer")
                                                .unitOfMeasure("Mbit/s")
                                                .build(),
                                        CharacteristicValueSpecification.builder()
                                                .value("100")
                                                .valueType("integer")
                                                .unitOfMeasure("Mbit/s")
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}
