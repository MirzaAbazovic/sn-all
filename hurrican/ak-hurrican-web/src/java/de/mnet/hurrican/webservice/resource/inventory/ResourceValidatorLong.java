package de.mnet.hurrican.webservice.resource.inventory;

import javax.annotation.*;
import javax.inject.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecCharacteristic;

@Named
public class ResourceValidatorLong implements ResourceRestrictionValidator {

    @Inject
    private ValidatorTools validatorTools;

    @Override
    public String getSupportedType() {
        return "long";
    }

    @Override
    public String validateValue(@Nonnull de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        Long longValue = convertToLong(value);
        if (longValue == null) {
            return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                    String.format("Die Long Validierung für den Wert [value=%s] hat einen " +
                            "Konvertierungsfehler gefangen!", value)
            );
        }
        for (String toCheck : resourceSpecCharacteristic.getRestrictions().getValue()) {
            if (toCheck != null) {
                Long longToCheck = convertToLong(toCheck);
                if (longToCheck == null) {
                    return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                            String.format("Die Long Validierung für den Wert [specValue=%s] hat einen " +
                                    "Konvertierungsfehler gefangen (Spezifikationsfehler)!", toCheck)
                    );
                }
                if (longValue.longValue() == longToCheck.longValue()) {
                    return null;
                }
            }
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Der Wert [value=%s] ist nicht erlaubt!", value));
    }

    @Override
    public String validateRange(@Nonnull de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        Long longValue = convertToLong(value);
        if (longValue == null) {
            return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                    String.format("Die Long Validierung für den Wert [value=%s] hat einen " +
                            "Konvertierungsfehler gefangen!", value)
            );
        }
        for (ResourceSpecCharacteristic.Restrictions.Range toCheck : resourceSpecCharacteristic.getRestrictions().getRange()) {
            if (toCheck.getFrom() != null && toCheck.getTo() != null) {
                Long longToCheckFrom = convertToLong(toCheck.getFrom());
                Long longToCheckTo = convertToLong(toCheck.getTo());
                if (longToCheckFrom == null || longToCheckTo == null) {
                    return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                            String.format("Die Long Validierung für die Werte [specFrom=%s, specTo=%s] hat einen " +
                                    "Konvertierungsfehler gefangen (Spezifikationsfehler)!", toCheck.getFrom(), toCheck.getTo())
                    );
                }
                if (longToCheckFrom > longToCheckTo) {
                    return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                            String.format("Eine leere Long Range [specFrom=%s, specTo=%s] ist nicht erlaubt!",
                                    toCheck.getFrom(), toCheck.getTo())
                    );
                }
                if (longValue >= longToCheckFrom
                        && longValue <= longToCheckTo) {
                    return null;
                }
            }
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Der Wert [value=%s] ist nicht erlaubt!", value));
    }

    @Override
    public String validateImplicitRestrictions(@Nonnull de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        Long longValue = convertToLong(value);
        if (longValue != null) {
            return null;
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Der Wert [value=%s] ist kein Long Wert.", value));
    }

    private Long convertToLong(String value) {
        try {
            return Long.valueOf(value);
        }
        catch (Exception e) {
            return null;
        }
    }
}
