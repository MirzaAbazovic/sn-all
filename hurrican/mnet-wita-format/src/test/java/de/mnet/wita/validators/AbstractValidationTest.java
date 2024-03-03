package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public abstract class AbstractValidationTest<T> {

    private final static Logger LOG = Logger.getLogger(AbstractValidationTest.class);

    protected Validator validator;
    protected Set<ConstraintViolation<T>> violations;

    @BeforeMethod
    public void setupMethod() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        violations = null;
    }

    @AfterMethod
    public void printViolations() {
        LOG.info("Violations found: " + violations);
    }

    protected void checkValidation(T object, boolean valid, Class<?>... groups) {
        violations = validator.validate(object, groups);
        assertEquals(violations.isEmpty(), valid, "Found validations " + violations);
    }

}
