/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.2014
 */
package de.mnet.hurrican.simulator.helper;

import java.util.regex.*;

/**
 * Helper for translating XPath expressions into a name-space neutral expression.
 */
public class LocaliseXPath {
    public static String localise(String in) {
        Pattern pattern = Pattern.compile("/(\\w+)");
        Matcher matcher = pattern.matcher(in);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(out, String.format("/*[local-name() = '%s']", matcher.group(1)));
        }
        return out.toString();
    }
}
