/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.03.14
 */
package de.mnet.common.tools;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;

/**
 * Helper Utils for normalization of names like for example the company name.
 */
public class NameUtils {

    /**
     * Normalize the assigned Pair to set maximum length after the following procedure: <ol> <li>Join both Strings to
     * one with ' ' as separator char</li> <li>If the length of the joined String fits to the maxLength => return as
     * {@code Pair<>(joinedString, null)}</li> <li>Else the joined String will be split in two parts in consideration of
     * the separation chars ' ' and '-', e.g. for max 8 chars:</li> <ul> <li>"f. word", "word"    =>    "f. word",
     * "word"</li> <li>"word", "new word"    =>    "word new", "word"</li> <li>"first word", "second word"    =>
     * "first", "word sec"</li> <li>"first-word", "second word"    =>    "first-", "word sec"</li> <li>"firstCompletW",
     * "second word"    =>    "firstCom", "pletW se"</li> <li>"first-CompletW", "second word"    =>    "first-",
     * "CompletW"</li> </ul> </ol>
     *
     * @param stringPair a {@link Pair} of Strings
     * @param maxLength  max length of each String in the final result {@link Pair}.
     * @return normalized {@link Pair} of Strings
     */
    public static Pair<String, String> normalizeToLength(Pair<String, String> stringPair, int maxLength) {
        String joinedString = StringTools.join(
                new String[] { stringPair.getFirst(), stringPair.getSecond() }, " ", true);

        if (joinedString.length() <= maxLength) {
            if (StringUtils.isNotEmpty(joinedString)) {
                return new Pair<>(joinedString, null);
            }
            return new Pair<>(null, null);
        }

        int firstSplitPos = getSplitPos(joinedString, maxLength);
        String first = joinedString.substring(0, firstSplitPos).trim();
        String second = joinedString.substring(firstSplitPos).trim();
        if (second.length() > maxLength) {
            second = second.substring(0, maxLength).trim();
        }
        return new Pair<>(first, second);
    }

    /**
     * Wrapper for {@link #normalizeToLength(Pair, int)} *
     */
    public static Pair<String, String> normalizeToLength(String firstString, String secondString, int maxLength) {
        return normalizeToLength(new Pair<>(firstString, secondString), maxLength);
    }

    /**
     * Returns the latest splitting position for the chars ' ' and '-' in consideration of the maximum length, if: - at
     * least on separator char is included - maxLength is not exceed ELSE: - the maxLength will be returned
     */
    private static int getSplitPos(String s, int maxLength) {
        String searchString = s.substring(0, maxLength + 1);
        int lastSeparator = StringUtils.lastIndexOf(searchString, "-") + 1;
        int lastEmptyString = StringUtils.lastIndexOf(searchString, " ");

        if ((lastSeparator <= maxLength && lastEmptyString <= maxLength) &&
                (lastEmptyString > 0 || lastSeparator > 0)) {
            return (lastSeparator > lastEmptyString) ? lastSeparator : lastEmptyString;
        }
        return maxLength;
    }
}
