/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.service.impl.validation;

import java.util.*;
import javax.validation.*;
import org.testng.Assert;

/**
 * Some common validation helpers, useful when unit testing
 */
@SuppressWarnings("unchecked")
public class ValidationTestUtil {

    /**
     * used for debugging purposes for outputting a list of all violations
     */
    public static Map<String, String> allViolations = new HashMap<>();

    public static <T> void assertNotNullViolation(Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("javax.validation.constraints.NotNull", violations, fieldNames);
    }

    public static <T> void assertNotEmptyViolation(Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("org.hibernate.validator.constraints.NotEmpty", violations, fieldNames);
    }

    public static <T> void assertNullViolation(Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("javax.validation.constraints.Null", violations, fieldNames);
    }

    public static <T> void assertSizeViolation(Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("javax.validation.constraints.Size", violations, fieldNames);
    }

    public static <T> void assertKundenwunschterminViolation(
            Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("de.mnet.wbci.validation.constraints.CheckKundenwunschtermin", violations, fieldNames);
    }

    public static <T> void assertKundenwunschterminIgnoringNextDayViolation(
            Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("de.mnet.wbci.validation.constraints.CheckKundenwunschterminIgnoringNextDay", violations, fieldNames);
    }

    public static <T> void assertKundenwunschterminNotInRangeViolation(
            Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("de.mnet.wbci.validation.constraints.CheckKundenwunschterminNotInRange", violations, fieldNames);
    }

    public static <T> void assertPatternViolation(
            Set<ConstraintViolation<T>> violations, String... fieldNames) {
        assertViolation("javax.validation.constraints.Pattern", violations, fieldNames);
    }

    public static <T> void assertViolation(String violationName, Set<ConstraintViolation<T>> violations,
            String... fieldNames) {
        Assert.assertNotNull(violations);

        Map<String, List<String>> violationsByNameAndType = new HashMap<>();
        List<String> violationsNotFoundForFieldName = new ArrayList<>(Arrays.asList(fieldNames));

        for (ConstraintViolation<?> violation : violations) {
            String name = violation.getPropertyPath().toString();
            String type = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
            String errorMsg = violation.getMessage();
            allViolations.put(String.format("%s:%s", type, name), String.format("%s %s", getLastPropertyNameInPath(violation.getPropertyPath()), errorMsg));
        }

        for (String fieldName : fieldNames) {
            for (ConstraintViolation<?> violation : violations) {
                String name = getLastPropertyNameInPath(violation.getPropertyPath());
                String type = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
                List types;
                if (violationsByNameAndType.containsKey(name)) {
                    types = violationsByNameAndType.get(name);
                }
                else {
                    types = new ArrayList<String>();
                    violationsByNameAndType.put(name, types);
                }
                if (!types.contains(type)) {
                    types.add(type);
                }
                if (fieldName.equals(name)) {
                    if (violationName.equals(type)) {
                        violationsNotFoundForFieldName.remove(fieldName);
                    }
                }
            }
        }
        if (!violationsNotFoundForFieldName.isEmpty()) {
            Assert.fail(String.format(
                    "No %s violation found for fieldname(s) %s. The following violations were found: %s",
                    violationName, violationsNotFoundForFieldName, violationsByNameAndType));
        }
    }

    public static String getLastPropertyNameInPath(Path path) {
        Iterator<Path.Node> iterator = path.iterator();
        Path.Node node = null;
        while (iterator.hasNext()) {
            node = iterator.next();
        }
        Assert.assertNotNull(node);
        return node.getName();
    }

    public static String getStringOfLength(int len) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            stringBuilder.append("a");
        }
        return stringBuilder.toString();
    }

    public static Integer getIntegerOfLength(int len) {
        return getLongOfLength(len).intValue();
    }

    public static Long getLongOfLength(int len) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            stringBuilder.append("9");
        }
        return Long.valueOf(stringBuilder.toString());
    }
}
