/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2015
 */
package de.augustakom.hurrican.model.cc.hardware;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;
import java.util.*;
import javax.validation.*;

import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.ApplicationContextProvider;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Used for validating the DSLAM Type.
 *
 * With the exception of Huawei, the DSLAM Type is optional. If a DSLAM type is set, then it
 * must match one of the DSLAM types specified within the T_REFERENCE table.
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = DslamType.DslamTypeValidator.class)
@Documented
public @interface DslamType {
    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class DslamTypeValidator implements
            ConstraintValidator<DslamType, HWDslam> {

        @Override
        public void initialize(DslamType constraintAnnotation) {
        }

        @Override
        public boolean isValid(HWDslam dslam, ConstraintValidatorContext context) {
            if (dslam.getDslamType() == null && dslam.isHuaweiDSLAM()) {
                return notValid(context, "Das DSLAM Typ fuer ein Huawei DSLAM darf nicht leer sein");
            }
            if (dslam.getDslamType() != null && isInvalidDslamType(dslam.getDslamType())) {
                return notValid(context, String.format("Das DSLAM Typ '%s' ist nicht bekannt", dslam.getDslamType()));
            }
            return true;
        }

        boolean isInvalidDslamType(String dslamType) {
            return !lookupDslamTypes().contains(dslamType);
        }

        boolean notValid(ConstraintValidatorContext context, String errorMsg) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
            return false;
        }

        private List<String> lookupDslamTypes() {
            try {
                ReferenceService referenceService = ApplicationContextProvider.getApplicationContext().getBean(ReferenceService.class);
                List<String> dslamTypes = new ArrayList<>();
                // !!!! IMPORTANT !!!!
                // Ensure that the referenceService call executes in a NEW transaction.
                // Since this validator is called on commit of the current transaction, whilst hibernate is iterating
                // through all beans in its 1st level cache (including this bean), invoking the referenceService inside
                // of the current transaction will result in the 1st level cache (collection) being modified (since the
                // reference types found are added to the 1st level cache)
                // -> resulting in a ConcurrentModificationException
                referenceService.findReferencesByTypeTxNew(Reference.REF_TYPE_HW_DSLAM_TYPE, false).forEach(name -> dslamTypes.add(name.getStrValue()));
                return dslamTypes;
            }
            catch (FindException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
