package de.mnet.hurrican.webservice.resource.inventory;

import javax.annotation.*;
import javax.inject.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecCharacteristic;

@Named
public class ResourceValidatorString implements ResourceRestrictionValidator {

    @Inject
    private ValidatorTools validatorTools;

    @Override
    public String getSupportedType() {
        return "string";
    }

    @Override
    public String validateValue(@Nonnull Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        for (String toCheck : resourceSpecCharacteristic.getRestrictions().getValue()) {
            if (toCheck != null && toCheck.equals(value)) {
                return null;
            }
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Der Wert [value=%s] ist nicht erlaubt!", value));
    }

    @Override
    public String validateRange(@Nonnull Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Die String Validierung von Ranges ist nicht unterstützt (Spezifikationsfehler)!"));
    }

    @Override
    public String validateImplicitRestrictions(@Nonnull de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        return null;
    }
}
