/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wbci.converter.MeldungsCodeConverter;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Checks if the {@link RueckmeldungVorabstimmung} contains either {@link de.mnet.wbci.model.MeldungsCode#ZWA} or {@link
 * de.mnet.wbci.model.MeldungsCode#NAT}.
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckRuemVaMeldungscodes.MeldungscodesValidator.class)
@Documented
public @interface CheckRuemVaMeldungscodes {

    String message() default "In der Vorabstimmungsantwort (RUEM-VA) muss entweder der Meldungscode ZWA oder NAT angegeben werden. Die Meldungscodes [%s] sind nicht zulässig";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class MeldungscodesValidator implements
            ConstraintValidator<CheckRuemVaMeldungscodes, RueckmeldungVorabstimmung> {

        protected String defaultMessage;

        @Override
        public void initialize(CheckRuemVaMeldungscodes constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull RueckmeldungVorabstimmung ruemVa, ConstraintValidatorContext context) {
            Set<MeldungsCode> meldungsCodes = MeldungsCodeConverter.retrieveMeldungsCodes(ruemVa.getMeldungsCodes());
            boolean containsZWA = meldungsCodes.contains(MeldungsCode.ZWA);
            boolean containsNAT = meldungsCodes.contains(MeldungsCode.NAT);
            if ((containsZWA && containsNAT) || (!containsZWA && !containsNAT)) {
                ValidationHelper.addConstraintViolation(context,
                        String.format(defaultMessage, ruemVa.getMeldungsCodes()));
                return false;
            }
            return true;
        }

    }

}
