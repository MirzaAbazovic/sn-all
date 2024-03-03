package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AnlagenGeschaeftsfallValid.AnlagenGeschaeftsfallValidator.class)
@Documented
public @interface AnlagenGeschaeftsfallValid {

    /**
     * Flag to indicate whether an anlage of type lageplan is allowed
     */
    boolean lageplanAllowed() default false;

    /**
     * Flag to indicate whether an anlage of type kuendigung is allowed
     */
    boolean kuendigungAllowed() default false;

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * Zulaessige Anzahl von 'Sonstigen'-Anlagen. -1 entspricht keine Begrenzung.
     */
    int numberSonstigeAnlagen() default -1;

    public class AnlagenGeschaeftsfallValidator extends BaseAnlagenValidator implements ConstraintValidator<AnlagenGeschaeftsfallValid, Geschaeftsfall> {

        boolean lageplanAllowed;
        boolean kuendigungAllowed;
        int numberSonstigeAnlagen;

        @Override
        public void initialize(AnlagenGeschaeftsfallValid constraintAnnotation) {
            lageplanAllowed = constraintAnnotation.lageplanAllowed();
            kuendigungAllowed = constraintAnnotation.kuendigungAllowed();
            numberSonstigeAnlagen = constraintAnnotation.numberSonstigeAnlagen();
        }

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            boolean anlagenTypesValid = anlagenTypesValid(geschaeftsfall);
            if (!anlagenTypesValid) {
                ValidationUtils.addConstraintViolation(context, "Es dürfen nur erlaubte Anlagentypen verwendet werden");
            }
            boolean numberSonstigeAnlagenValid = numberSonstigeAnlagenValid(geschaeftsfall, numberSonstigeAnlagen);
            if (!numberSonstigeAnlagenValid) {
                ValidationUtils.addConstraintViolation(context, String.format("Es dürfen maximal %s Anlagen vom Typ 'Sonstige' verwendet werden", numberSonstigeAnlagen));
            }
            boolean fileSizesValid = filesizesValid(geschaeftsfall.getAnlagen(), context);
            return anlagenTypesValid && numberSonstigeAnlagenValid && fileSizesValid;
        }

        private boolean anlagenTypesValid(Geschaeftsfall geschaeftsfall) {
            boolean lageplanFound = false;
            boolean kuendigungFound = false;

            for (Anlage anlage : geschaeftsfall.getAnlagen()) {
                if (anlage.getAnlagentyp() == null) {
                    return false;
                }

                switch (anlage.getAnlagentyp()) {
                    case LAGEPLAN:
                        if (lageplanFound || !lageplanAllowed) {
                            return false;
                        }
                        lageplanFound = true;
                        break;
                    case KUENDIGUNGSSCHREIBEN:
                        if (kuendigungFound || !kuendigungAllowed) {
                            return false;
                        }
                        kuendigungFound = true;
                        break;
                    default:
                }
            }
            return true;
        }

        private boolean numberSonstigeAnlagenValid(Geschaeftsfall geschaeftsfall, int numberSonstigeAnlagen) {
            if (numberSonstigeAnlagen == -1) {
                return true;
            }
            int count = 0;

            for (Anlage anlage : geschaeftsfall.getAnlagen()) {
                if (Anlagentyp.SONSTIGE == anlage.getAnlagentyp()) {
                    count++;
                }
            }
            return count <= numberSonstigeAnlagen;
        }
    }

    /**
     * Defines several <code>@AnlagenGeschaeftsfallValid</code> annotations on the same element
     *
     *
     * @see AnlagenGeschaeftsfallValid
     */
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        AnlagenGeschaeftsfallValid[] value();
    }

}
