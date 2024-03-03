/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2010 15:00:30
 */
package de.mnet.migration.common.util;

import java.util.*;
import java.util.regex.*;
import org.apache.log4j.Logger;

public class RecursivePropertyResolver {
    private static final Logger LOGGER = Logger.getLogger(RecursivePropertyResolver.class);

    // group 0 is e.g. ${foo}, group 1 foo is e.g. foo - property to be recursively resolved
    private static final Pattern replacePattern = Pattern.compile("(\\$\\{(.*?)\\})");

    public void resolve(Properties props) {
        boolean anythingChanged = true;
        while (anythingChanged) {
            anythingChanged = false;
            Enumeration<?> keys = props.propertyNames();
            while (keys.hasMoreElements()) {
                Object keyObject = keys.nextElement();
                if (!(keyObject instanceof String)) {
                    LOGGER.warn("resolve() - found key " + keyObject
                            + " in property list which is no String, ignoring");
                    continue;
                }
                String key = (String) keyObject;
                String value = props.getProperty(key);
                if (value == null) {
                    LOGGER.warn("resolve() - did not find value for existing key " + key
                            + " - Properties object broken?");
                    continue;
                }
                Matcher matcher = replacePattern.matcher(value);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    // get last match in case of regexp returning "${${sample}"
                    String matchKey = matcher.group(2);
                    int skipChars = matchKey.lastIndexOf("${");
                    if (skipChars >= 0) {
                        matchKey = matchKey.substring(skipChars + 2);
                    }
                    String replacementValue = props.getProperty(matchKey);
                    if (replacementValue != null) {
                        String keyToReplace = matcher.group(1);
                        if (skipChars >= 0) {
                            keyToReplace = keyToReplace.substring(skipChars + 2);
                        }
                        String newValue = value.replace(keyToReplace, replacementValue);
                        if (!value.equals(newValue)) {
                            props.setProperty(key, newValue);
                            anythingChanged = true;
                        }
                        else {
                            LOGGER.warn("resolve() - value " + value
                                    + " for key " + key + " resolves to itself");
                        }
                    }
                }
            }
        }
    }
}
