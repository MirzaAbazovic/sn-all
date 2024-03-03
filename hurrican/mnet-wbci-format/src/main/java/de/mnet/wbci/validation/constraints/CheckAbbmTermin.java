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
import java.time.format.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.validation.helper.ValidationHelper;

@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckAbbmTermin.WechselterminValidator.class)
@Documented
public @interface CheckAbbmTermin {

    String message() default "Der Termin %s ist für die Abbruchmeldung (ABBM) nicht gültig, da er nicht mit dem vereinbarten Wechseltermin (%s) der Vorabstimmung übereinstimmt";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class WechselterminValidator implements
            ConstraintValidator<CheckAbbmTermin, Abbruchmeldung> {

        protected String defaultMessage;

        @Override
        public void initialize(CheckAbbmTermin constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull Abbruchmeldung value, ConstraintValidatorContext context) {
            /***
             * This will be checked with {@link javax.validation.constraints.NotNull} at the specific getters.
             * See {@link de.mnet.wbci.model.Abbruchmeldung#getWechseltermin()} ,
             * {@link de.mnet.wbci.model.RueckmeldungVorabstimmung#getWbciGeschaeftsfall()},
             * {@link de.mnet.wbci.model.WbciGeschaeftsfall#getWechseltermin()} ()}
             */
            if (value.getWechseltermin() == null
                    || value.getWbciGeschaeftsfall() == null
                    || value.getWbciGeschaeftsfall().getWechseltermin() == null
                    || value.getWbciGeschaeftsfall().getWechseltermin()
                    .isEqual(value.getWechseltermin())) {
                return true;
            }

            ValidationHelper.addConstraintViolation(context,
                    String.format(defaultMessage,
                            value.getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR)),
                            value.getWbciGeschaeftsfall().getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR))
                    )
            );
            return false;
        }
    }

}
