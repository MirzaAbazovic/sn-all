package de.mnet.wita.validators.meldung;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.validators.BasePresenceValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VorabstimmungsIdSetMeldung.VorabstimmungsIdSetMeldungValidator.class)
@Documented
public @interface VorabstimmungsIdSetMeldung {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    static class VorabstimmungsIdSetMeldungValidator extends BasePresenceValidator implements
            ConstraintValidator<VorabstimmungsIdSetMeldung, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldung, ConstraintValidatorContext context) {
            return checkPresence(context, meldung.getVorabstimmungsId());
        }

        @Override
        public void initialize(VorabstimmungsIdSetMeldung constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die VorabstimmungsId muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die VorabstimmungsId darf nicht gesetzt sein.";
        }
    }

}
