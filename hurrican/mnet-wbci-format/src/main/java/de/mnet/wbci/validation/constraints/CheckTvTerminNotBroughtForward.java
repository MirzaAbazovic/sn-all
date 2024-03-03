package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.time.format.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Prueft, ob der in der TV angegebene Wunschtermin VOR dem eigentlich abgestimmten Termin (aus RUEM-VA) liegt.
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckTvTerminNotBroughtForward.TvTerminValidator.class)
@Documented
public @interface CheckTvTerminNotBroughtForward {
    String message() default "Der Termin %s ist für die TerminverschiebungsAnfrage (TV) nicht gültig, da der Termin nicht vor dem vereinbarten Wechseltermin (%s) liegen darf";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};

    public class TvTerminValidator implements
            ConstraintValidator<CheckTvTerminNotBroughtForward, TerminverschiebungsAnfrage> {

        protected String defaultMessage;

        @Override
        public void initialize(CheckTvTerminNotBroughtForward constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull TerminverschiebungsAnfrage value, ConstraintValidatorContext context) {
            if (value.getTvTermin() == null
                    || value.getWbciGeschaeftsfall() == null
                    || value.getWbciGeschaeftsfall().getWechseltermin() == null
                    || !(value.getTvTermin().isBefore(value.getWbciGeschaeftsfall().getWechseltermin()))) {
                return true;
            }

            ValidationHelper.addConstraintViolation(context,
                    String.format(defaultMessage,
                            value.getTvTermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR)),
                            value.getWbciGeschaeftsfall().getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR))
                    )
            );
            return false;
        }
    }
}