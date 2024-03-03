package de.mnet.hurrican.webservice.resource.inventory;

import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.messages.AKWarnings;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpec;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.ResourceValidationException;

@Named
@Component
/**
 * Was wird validiert?
 * Jede Resource wird fuer sich validiert. Zur Resource zieht sich Hurrican die Spezifikation und prueft jede
 * Characteristic einzeln:
 * - Characteristics, die keine Spezifikation haben loesen einen Fehler aus
 * - Kardinalitaet der Valueliste
 * - Typ in Characteristic sowie Spezifikation muessen gleich sein
 * - implizite Formate:
 *   - Boolean: nur 'true' oder 'false'
 *   - Long: Zahlenwert
 *   - String: keine Restriktion
 *   - DateTime: '[-]CCYY-MM-DDThh:mm:ss[Z|(+|-)hh:mm]'
 * - Restrictions aus der Spezifikation (Value oder(exklusives ODER) Ranges)
 *
 * Bisher nicht implementiert sind Parentverweise, Parent/Child Konsistenz in einem Request
 *
 */
public class ResourceValidator {

    @Inject
    private ValidatorTools validatorTools;
    @Inject
    private ResourceSpecificationInventory specInventory;
    @Inject
    private List<ResourceRestrictionValidator> restrictionValidators;

    public void validateResource(@Nonnull Resource resource) throws ResourceValidationException {
        Preconditions.checkNotNull(resource);
        Preconditions.checkNotNull(resource.getResourceSpec());
        ResourceSpec resourceSpec = getResourceSpec(resource);
        AKWarnings errors = new AKWarnings();
        errors.addAKWarnings(validateCharacteristics(resource, resourceSpec));
        errors.addAKWarningNotNull(this, validateParent(resource, resourceSpec));
        successOrThrow(errors);
    }

    private void successOrThrow(AKWarnings errors) throws ResourceValidationException {
        if (errors.isNotEmpty()) {
            throw new ResourceValidationException(String.format("Resource Validierung fehlgeschlagen:%n%s",
                    errors.getWarningsAsText()));
        }
    }

    private ResourceSpec getResourceSpec(@Nonnull Resource resource) throws ResourceValidationException {
        ResourceSpec resourceSpec = specInventory.get(resource.getResourceSpec()
                .getId(), resource.getResourceSpec().getInventory());
        if (resourceSpec == null) {
            throw new ResourceValidationException(String.format("Die Spezifikation '%s' im Inventory '%s' ist nicht verfügbar!",
                    resource.getResourceSpec().getId(), resource.getResourceSpec().getInventory()));
        }
        return resourceSpec;
    }

    private AKWarnings validateCharacteristics(@Nonnull Resource resource, @Nonnull ResourceSpec resourceSpec) {
        List<ResourceCharacteristic> resourceCharacteristics = resource.getCharacteristic();
        List<String> characteristicNames = Lists.newArrayList();
        AKWarnings errors = new AKWarnings();
        if (resourceCharacteristics == null || resourceCharacteristics.isEmpty()) {
            return errors;
        }
        for (ResourceCharacteristic resourceCharacteristic : resourceCharacteristics) {
            if (!characteristicNames.contains(resourceCharacteristic.getName())) {
                characteristicNames.add(resourceCharacteristic.getName());
                ResourceSpecCharacteristic resourceSpecCharacteristic = validatorTools.getResourceSpecCharacteristic(resourceSpec,
                        resourceCharacteristic.getName());
                if (resourceSpecCharacteristic != null) {
                    errors.addAKWarnings(validateCardinality(resource, resourceCharacteristic,
                            resourceSpecCharacteristic));
                    errors.addAKWarnings(validateRestrictions(resource, resourceCharacteristic,
                            resourceSpecCharacteristic));
                }
                else {
                    errors.addAKWarning(this, validatorTools.formatError(resource, resourceCharacteristic, null,
                            String.format("Für die Characteristic [name=%s] ist keine Spezifikation verfügbar!",
                                    resourceCharacteristic.getName())
                    ));
                }
            }
            else {
                errors.addAKWarning(this, validatorTools.formatError(resource, resourceCharacteristic, null,
                        String.format("Resource [name=%s] wurde mehrmals angegeben, ist nur einmal möglich!",
                                resourceCharacteristic.getName())
                ));
            }
        }
        return errors;
    }

    private AKWarnings validateRestrictions(@Nonnull Resource resource,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic) {
        AKWarnings errors = new AKWarnings();
        if (!resourceCharacteristic.getValue().isEmpty()) {
            for (String value : resourceCharacteristic.getValue()) {
                errors.addAKWarningNotNull(this, validateImplicitRestriction(resource, resourceSpecCharacteristic,
                        resourceCharacteristic, value));
                if (resourceSpecCharacteristic.getRestrictions() != null) {
                    errors.addAKWarnings(validateRestrictionsValueOrRange(resource, resourceSpecCharacteristic,
                            resourceCharacteristic, value));
                }
            }
        }
        return errors;
    }

    @Nullable
    private String validateImplicitRestriction(@Nonnull Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        final String type = resourceSpecCharacteristic.getType();
        for (ResourceRestrictionValidator restrictionValidator : restrictionValidators) {
            if (restrictionValidator.getSupportedType().equalsIgnoreCase(type)) {
                return restrictionValidator.validateImplicitRestrictions(resource, resourceSpecCharacteristic,
                        resourceCharacteristic, value);
            }
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Der Validator unterstützt den Typen [type=%s] nicht!", type));
    }

    private AKWarnings validateRestrictionsValueOrRange(@Nonnull Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull String value) {
        AKWarnings errors = new AKWarnings();
        if (!resourceSpecCharacteristic.getRestrictions().getValue().isEmpty()) {
            errors.addAKWarningNotNull(this, dispatchValue(resource, resourceSpecCharacteristic, resourceCharacteristic,
                    EnumResourceRestriction.VALUE, value));
        }
        else if (!resourceSpecCharacteristic.getRestrictions().getRange().isEmpty()) {
            errors.addAKWarningNotNull(this, dispatchValue(resource, resourceSpecCharacteristic, resourceCharacteristic,
                    EnumResourceRestriction.RANGE, value));
        }
        return errors;
    }

    @Nullable
    private String dispatchValue(@Nonnull Resource resource,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull EnumResourceRestriction resourceRestriction,
            @Nonnull String value) {
        final String type = resourceSpecCharacteristic.getType();
        for (ResourceRestrictionValidator restrictionValidator : restrictionValidators) {
            if (restrictionValidator.getSupportedType().equalsIgnoreCase(type)) {
                switch (resourceRestriction) {
                    case VALUE:
                        return restrictionValidator.validateValue(resource, resourceSpecCharacteristic,
                                resourceCharacteristic, value);
                    case RANGE:
                        return restrictionValidator.validateRange(resource, resourceSpecCharacteristic,
                                resourceCharacteristic, value);
                    default:
                        break;
                }
            }
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Der Validator unterstützt den Typen [type=%s] nicht!", type));
    }

    private AKWarnings validateCardinality(@Nonnull Resource resource,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic) {
        AKWarnings errors = new AKWarnings();
        errors.addAKWarningNotNull(this, validateCardinalityMax(resource, resourceCharacteristic, resourceSpecCharacteristic));
        errors.addAKWarningNotNull(this, validateCardinalityMin(resource, resourceCharacteristic, resourceSpecCharacteristic));
        return errors;
    }

    @Nullable
    private String validateCardinalityMax(@Nonnull Resource resource,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic) {
        long max = Long.MAX_VALUE;
        if (!resourceSpecCharacteristic.getCardinality().getMax().equalsIgnoreCase("unbound")) {
            max = Long.parseLong(resourceSpecCharacteristic.getCardinality().getMax());
        }
        int size = (resourceCharacteristic.getValue() != null) ? resourceCharacteristic.getValue().size() : 0;
        if (size <= max) {
            return null;
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Maximale Kardinalität [ist=%d, soll=%d] verletzt!", size, max));
    }

    @Nullable
    private String validateCardinalityMin(@Nonnull Resource resource,
            @Nonnull ResourceCharacteristic resourceCharacteristic,
            @Nonnull ResourceSpecCharacteristic resourceSpecCharacteristic) {
        long min = resourceSpecCharacteristic.getCardinality().getMin().longValue();
        int size = (resourceCharacteristic.getValue() != null) ? resourceCharacteristic.getValue().size() : 0;
        if (size >= min) {
            return null;
        }
        return validatorTools.formatError(resource, resourceCharacteristic, resourceSpecCharacteristic,
                String.format("Minimale Kardinalität [ist=%d, soll=%d] verletzt!", size, min));
    }

    @Nullable
    private String validateParent(@Nonnull Resource resource, @Nonnull ResourceSpec resourceSpec) {
        final ResourceSpecId parentSpecId = resourceSpec.getParentSpec();
        final String result;
        if (parentSpecId == null) {
            result = null;
        }
        else if (resource.getParentResource() == null) {
            result = String.format("Die Validierung der Resource[id=%s, inventory=%s, name=%s] ist fehlgeschlagen:" +
                            " Die zugehörige Resource-Spec [id=%s, inventory=%s, name=%s, type=%s] sieht eine Parent-Resource vor. Diese ist jedoch nicht gesetzt!",
                    resource.getId(), resource.getInventory(), resource.getName(), resourceSpec.getId(),
                    resourceSpec.getInventory(), resourceSpec.getName(), resourceSpec.getType()
            );
        }
        else if (!resource.getParentResource().getInventory().equals(parentSpecId.getInventory())) {
            result = String.format("Die Validierung der Resource[id=%s, inventory=%s, name=%s] ist fehlgeschlagen: " +
                            "Das Inventory der Parent-Resource-Id[id=%s, inventory=%s] weicht von dem der Parent-Resourcr-Spec[id=%s, inventory=%s] ab",
                    resource.getId(), resource.getInventory(), resource.getName(), resource.getParentResource().getId(),
                    resource.getParentResource().getInventory(), parentSpecId.getId(), parentSpecId.getInventory()
            );
        }
        else {
            result = null;
        }
        return result;
    }
}
