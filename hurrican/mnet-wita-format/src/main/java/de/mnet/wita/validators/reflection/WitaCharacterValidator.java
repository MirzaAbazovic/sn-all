/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2011 11:21:08
 */
package de.mnet.wita.validators.reflection;

import static org.apache.commons.lang.ArrayUtils.*;

import java.util.*;
import javax.validation.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.CharRange;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.tools.WitaReflectionTools;
import de.mnet.wita.tools.WitaReflectionTools.FieldWithPath;

public class WitaCharacterValidator implements ConstraintValidator<WitaCharactersValid, MwfEntity> {

    private static final char[] VALID_CHARS;

    static {
        List<Character> charList = Lists.newArrayList();
        charList.add('\u0009');
        charList.add('\n'); // U+000A
        charList.add('\r'); // U+000D
        charList.addAll(charRange('\u0020', '\u005f'));
        charList.addAll(charRange('\u0061', '\u007b'));
        charList.add('\u007d');
        charList.add('\u00a7');
        charList.addAll(charRange('\u00b0', '\u00b3'));
        charList.addAll(charRange('\u00c0', '\u00d6'));
        charList.addAll(charRange('\u00d8', '\u00f6'));
        charList.addAll(charRange('\u00f8', '\u00ff'));

        VALID_CHARS = toPrimitive(charList.toArray(new Character[charList.size()]));
    }

    private static List<Character> charRange(char start, char end) {
        List<Character> charList = Lists.newArrayList();

        @SuppressWarnings("unchecked")
        Iterator<Character> iterator = new CharRange(start, end).iterator();
        while (iterator.hasNext()) {
            charList.add(iterator.next());
        }

        return charList;
    }

    @Override
    public void initialize(WitaCharactersValid constraintAnnotation) {
        // Nothing to do
    }

    @Override
    public boolean isValid(MwfEntity entity, ConstraintValidatorContext context) {
        Set<String> violations = validate(entity);
        if (violations.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        for (String violation : violations) {
            context.buildConstraintViolationWithTemplate(violation).addConstraintViolation();
        }
        return false;
    }

    /* Method to validate the characters of an field without beanvalidation. */
    public Set<String> validate(MwfEntity entity) {
        Set<String> violations = Sets.newHashSet();
        try {
            Set<FieldWithPath<String>> methods = WitaReflectionTools.filterFields(entity, MwfEntity.class, String.class);
            for (FieldWithPath<String> readMethod : methods) {
                if (!isStringAttributeValid(readMethod.field)) {
                    violations.add("Ungültige Zeichen für " + entity.getClass().getSimpleName() + "."
                            + readMethod.path + ": \"" + readMethod.field + "\"");
                }
            }

            return violations;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to validate char set", e);
        }
    }

    private boolean isStringAttributeValid(Object attribute) {
        if (attribute == null) {
            return true;
        }
        if (!attribute.getClass().isAssignableFrom(String.class)) {
            throw new RuntimeException("Can only validate char set for strings");
        }
        return StringUtils.containsOnly((String) attribute, VALID_CHARS);
    }
}
