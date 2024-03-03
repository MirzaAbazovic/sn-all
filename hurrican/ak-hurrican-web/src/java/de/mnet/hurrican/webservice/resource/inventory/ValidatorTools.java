package de.mnet.hurrican.webservice.resource.inventory;

import java.util.*;
import javax.annotation.*;
import javax.inject.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpec;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecCharacteristic;

@Named
public class ValidatorTools {

    @Nullable
    public ResourceSpecCharacteristic getResourceSpecCharacteristic(@Nonnull ResourceSpec resourceSpec, String name) {
        if (name == null || resourceSpec.getCharacteristic() == null) {
            return null;
        }
        List<ResourceSpecCharacteristic> resourceSpecCharacteristics = resourceSpec.getCharacteristic();
        for (ResourceSpecCharacteristic resourceSpecCharacteristic : resourceSpecCharacteristics) {
            if (name.equals(resourceSpecCharacteristic.getName())) {
                return resourceSpecCharacteristic;
            }
        }
        return null;
    }

    @Nonnull
    public String formatError(@Nonnull de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource resource, @Nonnull ResourceCharacteristic instance,
            @Nullable ResourceSpecCharacteristic meta, @Nonnull String description) {
        return String.format("Die Validierung der Resource[id=%s, inventory=%s, name=%s] für die " +
                        "ResourceCharacteristic[name=%s, values=%s] mit der ResourceSpecCharacteristic[name=%s] " +
                        "ist fehlgeschlagen. Grund: '%s'",
                resource.getId(), resource.getInventory(), resource.getName(),
                instance.getName(), ((instance.getValue() != null) ? instance.getValue().toString() : "null"),
                ((meta != null) ? meta.getName() : "null"),
                description
        );
    }
}
