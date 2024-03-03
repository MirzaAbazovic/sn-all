package de.mnet.wita.validators;

import static de.mnet.wita.validators.ValidationUtils.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;
import javax.validation.*;
import com.google.common.collect.Lists;

import de.mnet.wita.WitaCdmVersion;

/**
 * Validator Annotation fuer {@link WitaCdmVersion}
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = WitaVersionValid.WitaVersionValidator.class)
@Documented
public @interface WitaVersionValid {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class WitaVersionValidator implements ConstraintValidator<WitaVersionValid, WitaCdmVersion> {

        private static final List<WitaCdmVersion> ALLOWED_CDM_VERSIONS =
                Lists.newArrayList(WitaCdmVersion.V1, WitaCdmVersion.V2);

        private static String ERROR_MSG;

        @Override
        public void initialize(WitaVersionValid constraintAnnotation) {
            String versions = ALLOWED_CDM_VERSIONS.stream()
                    .map(WitaCdmVersion::getVersion)
                    .collect(Collectors.joining(","));

            ERROR_MSG = String.format("Es werden nur die CDM-Versionen '%s' unterstuetzt", versions);
        }

        @Override
        public boolean isValid(WitaCdmVersion witaCdmVersion, ConstraintValidatorContext context) {
            return ALLOWED_CDM_VERSIONS.contains(witaCdmVersion) || addConstraintViolation(context, ERROR_MSG);
        }
    }

}
