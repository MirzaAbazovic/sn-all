package de.mnet.hurrican.webservice.resource.inventory;

import java.time.*;
import java.time.format.*;
import javax.annotation.*;
import javax.inject.*;
import org.apache.log4j.Logger;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecCharacteristic;

@Named
/**
 * Folgende Werte fuer xsd:dateTime sind gueltig:
 *  2001-10-26T21:32:52,
 *  2001-10-26T21:32:52+02:00,
 *  2001-10-26T19:32:52Z,
 *  2001-10-26T19:32:52+00:00,
 *  -2001-10-26T21:32:52,
 *  2001-10-26T21:32:52.12679.
 */
public class ResourceValidatorDateTime implements ResourceRestrictionValidator {

    private static final Logger LOGGER = Logger.getLogger(ResourceValidatorDateTime.class);

    @Inject
    private ValidatorTools validatorTools;

    @Override
    public String getSupportedType() {
        return "dateTime";
    }

    @Override
    public String validateValue(@Nonnull de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        LocalDateTime dateTimeValue = convertToLong(value);
        if (dateTimeValue == null) {
            return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                    String.format("Die DateTime Validierung für den Wert [value=%s] hat einen " +
                            "Konvertierungsfehler gefangen!", value)
            );
        }
        for (String toCheck : resourceSpecCharacteristic.getRestrictions().getValue()) {
            if (toCheck != null) {
                LocalDateTime dateTimeToCheck = convertToLong(toCheck);
                if (dateTimeToCheck == null) {
                    return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                            String.format("Die DateTime Validierung für den Wert [specValue=%s] hat einen " +
                                    "Konvertierungsfehler gefangen (Spezifikationsfehler)!", toCheck)
                    );
                }
                if (dateTimeToCheck.equals(dateTimeValue)) {
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
        LocalDateTime dateTimeValue = convertToLong(value);
        if (dateTimeValue == null) {
            return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                    String.format("Die DateTime Validierung für den Wert [value=%s] hat einen " +
                            "Konvertierungsfehler gefangen!", value)
            );
        }
        for (ResourceSpecCharacteristic.Restrictions.Range toCheck : resourceSpecCharacteristic.getRestrictions().getRange()) {
            if (toCheck.getFrom() != null && toCheck.getTo() != null) {
                LocalDateTime dateTimeToCheckFrom = convertToLong(toCheck.getFrom());
                LocalDateTime dateTimeToCheckTo = convertToLong(toCheck.getTo());
                if (dateTimeToCheckFrom == null || dateTimeToCheckTo == null) {
                    return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                            String.format("Die DateTime Validierung für die Werte [specFrom=%s, specTo=%s] hat einen " +
                                    "Konvertierungsfehler gefangen (Spezifikationsfehler)!", toCheck.getFrom(), toCheck.getTo())
                    );
                }
                if (dateTimeToCheckFrom.isAfter(dateTimeToCheckTo)) {
                    return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                            String.format("Eine leere DateTime Range [specFrom=%s, specTo=%s] ist nicht erlaubt!",
                                    toCheck.getFrom(), toCheck.getTo())
                    );
                }
                if ((dateTimeValue.isEqual(dateTimeToCheckFrom) || dateTimeValue.isAfter(dateTimeToCheckFrom))
                        && (dateTimeValue.isEqual(dateTimeToCheckTo) || dateTimeValue.isBefore(dateTimeToCheckTo))) {
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
        LocalDateTime dateTime = convertToLong(value);
        if (dateTime != null) {
            return null;
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Der DateTime Wert [value=%s] ist nicht erlaubt. " +
                        "Erlaubtes Format '[-]CCYY-MM-DDThh:mm:ss[Z|(+|-)hh:mm]'!", value)
        );
    }

    private LocalDateTime convertToLong(String value) {
        // it's ugly now because master joda is smarter then java guys
        // need to try several main iso date time formats one by one
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_INSTANT);
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        try {
            // without time zone == local date time
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return null;
    }
}
