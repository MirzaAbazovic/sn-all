/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators;

import static de.mnet.wita.validators.ValidationUtils.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.validators.RufnummernBlockValid.RufnummernBlockValidator;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = RufnummernBlockValidator.class)
@Documented
public @interface RufnummernBlockValid {

    String message() default "Der Rufnummernblock ist nicht korrekt gesetzt";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class RufnummernBlockValidator implements
            ConstraintValidator<RufnummernBlockValid, List<RufnummernBlock>> {

        @Override
        public boolean isValid(List<RufnummernBlock> block, ConstraintValidatorContext context) {
            if (block == null) {
                return true;
            }

            if (block.isEmpty() || (block.size() > 5)) {
                return addConstraintViolation(context, "Block muss zwischen 1 und 5 Elemente enthalten");
            }

            List<RufnummernBlock> processedBlocks = new ArrayList<RufnummernBlock>();
            for (RufnummernBlock entry : block) {
                String von = entry.getVon();
                String bis = entry.getBis();
                if (!StringUtils.isNumeric(von) || !StringUtils.isNumeric(bis)) {
                    return addConstraintViolation(context,
                            "Sowohl 'von' als auch 'bis' des Blocks müssen numerisch sein");
                }

                // - die die Nummernblocklänge von 'Rufnummernblock von' und von 'Rufnummernblock bis' müssen jeweils
                // die
                // gleiche Stellenzahl aufweisen
                if (von.length() != bis.length()) {
                    return addConstraintViolation(context, "'Von' und 'bis' des Blocks müssen dieselbe Länge haben");
                }

                // - der Wert von 'Rufnummernblock von' muss kleiner sein als der Wert von 'Rufnummernblock bis'
                Integer vonValue = Integer.valueOf(von);
                Integer bisValue = Integer.valueOf(bis);
                if (vonValue >= bisValue) {
                    return addConstraintViolation(context, "Wert 'von' muss kleiner als 'bis' sein");
                }

                // - alle Ziffern außer der ersten Ziffer müssen bei 'Rufnummernblock von' gleich '0' sein,
                // beispielsweise
                // '20', '300'
                if (!StringUtils.containsOnly(von.substring(1), "0")) {
                    return addConstraintViolation(context,
                            "Bis auf die erste Ziffer müssen alle Ziffern des Feldes 'von' '0' sein");
                }

                // - alle Ziffern außer der ersten Ziffer müssen bei 'Rufnummernblock bis' gleich '9' sein,
                // beispielsweise
                // '29', '499'
                if (!StringUtils.containsOnly(bis.substring(1), "9")) {
                    return addConstraintViolation(context,
                            "Bis auf die erste Ziffer müssen alle Ziffern des Feldes 'bis' '9' sein");
                }

                // - die Schnittmenge der angegebenen Rufnummernblöcke muss die leere Menge sein, die Rufnummernblöcke
                // dürfen sich nicht überschneiden
                //
                for (RufnummernBlock processedEntry : processedBlocks) {
                    Integer processedVonValue = Integer.valueOf(processedEntry.getVon());
                    Integer processedBisValue = Integer.valueOf(processedEntry.getBis());

                    if ((processedVonValue <= bisValue) && (vonValue <= processedBisValue)) {
                        return addConstraintViolation(context, "Blöcke dürfen sich nicht überschneiden");
                    }
                }
                processedBlocks.add(entry);
            }
            return true;
        }

        @Override
        public void initialize(RufnummernBlockValid constraintAnnotation) {
            // nothing to do
        }
    }

}
