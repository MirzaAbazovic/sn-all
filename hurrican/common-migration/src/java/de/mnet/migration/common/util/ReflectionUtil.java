/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2010 16:08:00
 */
package de.mnet.migration.common.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;
import javax.persistence.*;
import org.apache.commons.beanutils.BeanUtils;


/**
 *
 */
public class ReflectionUtil {

    public static <T> T cloneBean(T bean, Class<T> clazz) {
        try {
            T newInstance = clazz.newInstance();
            BeanUtils.copyProperties(newInstance, bean);
            return newInstance;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get a list of all fields of a class (all modifiers, not only public) and it's superclasses, up and including the
     * upperBound. Does not get fields with a name containing a Dollar sign ('$').
     */
    public static List<Field> getAllFields(Class<?> upperBound, Class<?> actualClassIn) {
        Class<?> actualClass = actualClassIn;
        List<Field> fields = new ArrayList<Field>();
        while ((actualClass != null) && upperBound.isAssignableFrom(actualClass)) {
            for (Field field : actualClass.getDeclaredFields()) {
                if (!field.getName().contains("$")) {
                    fields.add(field);
                }
            }
            actualClass = actualClass.getSuperclass();
        }
        return fields;
    }


    public static List<Field> filterStaticAndFinal(List<Field> fields) {
        List<Field> filtered = new ArrayList<Field>();
        for (Field field : fields) {
            if (!(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))) {
                filtered.add(field);
            }
        }
        return filtered;
    }


    public static <T extends AccessibleObject> List<T> filterTransient(List<T> list) {
        List<T> filtered = new ArrayList<T>();
        for (T item : list) {
            if (item.getAnnotation(Transient.class) == null) {
                filtered.add(item);
            }
        }
        return filtered;
    }


    /**
     * Creates a set of possible name variations from a name by: <ul> <li>Replacing the first letter with it's uppercase
     * version</li> <li>Adding spaces ' ' where a camelcase name starts with a new word</li> <li>Adding underscores '_'
     * where a camelcase name starts with a new word</li> </ul> In the above definition, a CamelCase name starts with a
     * new word where: <ul> <li>An uppercase letter or a number follows a lowercase letter</li> <li>An uppercase letter
     * that is followed by a lowercase letter follows an uppercase letter</li> <li>A letter follows a number</li> </ul>
     */
    public static Set<String> createPossibleNames(String columnName) {
        Set<String> result = new HashSet<String>();
        String withoutUnderscore = columnName.replaceAll("_", "");
        result.add(columnName);
        result.add(withoutUnderscore);
        if (columnName.length() > 1) {
            result.add(columnName.substring(0, 1).toUpperCase() + columnName.substring(1));
            result.add(withoutUnderscore.substring(0, 1).toUpperCase() + withoutUnderscore.substring(1));
        }
        Pattern pattern1 = Pattern.compile("([a-z])([A-Z0-9])");
        Pattern pattern2 = Pattern.compile("([0-9])([a-zA-Z])");
        Pattern pattern3 = Pattern.compile("([A-Z])([A-Z][a-z])");
        boolean mutated = true;
        while (mutated) {
            mutated = false;
            for (String string : new ArrayList<String>(result)) {
                mutated = handlePattern(result, pattern1, mutated, string);
                mutated = handlePattern(result, pattern2, mutated, string);
                mutated = handlePattern(result, pattern3, mutated, string);
            }
        }
        return result;
    }


    private static boolean handlePattern(Set<String> result, Pattern pattern1, boolean mutatedIn, String string) {
        boolean mutated = mutatedIn;
        Matcher matcher = pattern1.matcher(string);
        while (matcher.find()) {
            mutated = mutated | result.add(string.substring(0, matcher.start()) +
                    matcher.group(1) + "_" + matcher.group(2) +
                    string.substring(matcher.end()));
            mutated = mutated | result.add(string.substring(0, matcher.start()) +
                    matcher.group(1) + " " + matcher.group(2) +
                    string.substring(matcher.end()));
        }
        return mutated;
    }
}
