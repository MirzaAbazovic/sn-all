package de.mnet.wita.validators.geschaeftsfall;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VorabstimmungsIdSetAuftrag.VorabstimmungsIdSetAuftragValidator.class)
@Documented
public @interface VorabstimmungsIdSetAuftrag {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    static class VorabstimmungsIdSetAuftragValidator extends BasePresenceValidator implements
            ConstraintValidator<VorabstimmungsIdSetAuftrag, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            String vorabstimmungsId = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt().getVorabstimmungsId();
            return checkPresence(context, vorabstimmungsId);
        }

        @Override
        public void initialize(VorabstimmungsIdSetAuftrag constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die VorabstimmungsId muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die VorabstimmungsId darf nicht gesetzt sein.";
        }
    }

}
