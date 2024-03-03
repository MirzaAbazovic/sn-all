/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 13:16:24
 */
package de.mnet.wita.model;

import static de.mnet.wita.validators.ValidationUtils.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.model.IoArchiveValid.IoArchiveEntryValidator;

@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = IoArchiveEntryValidator.class)
@Documented
public @interface IoArchiveValid {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class IoArchiveEntryValidator implements ConstraintValidator<IoArchiveValid, IoArchive> {

        @Override
        public boolean isValid(IoArchive entry, ConstraintValidatorContext context) {
            if (entry.getIoType().equals(IOType.IN)) {
                return isValidInMessage(entry, context);
            }

            return true;
        }

        @Override
        public void initialize(IoArchiveValid constraintAnnotation) {
            // nothing to do
        }

        private boolean isValidInMessage(IoArchive entry, ConstraintValidatorContext context) {
            if (entry.getRequestMeldungstyp() == null) {
                return addConstraintViolation(context, "(WITA) Meldungstyp must be set for IN IO archive entries");
            }

            if (entry.getRequestMeldungstyp().endsWith("-PV")) {
                if (StringUtils.isBlank(entry.getWitaVertragsnummer())) {
                    return addConstraintViolation(context,
                            "Vertragsnummer must be set for IO archive entries of -PV messages");
                }
            }
            else {
                if (StringUtils.isBlank(entry.getWitaExtOrderNo())) {
                    return addConstraintViolation(context,
                            "Externe Auftragsnummer must be set for IO archive entries of not -PV messages");
                }
            }
            return true;
        }
    }
}
