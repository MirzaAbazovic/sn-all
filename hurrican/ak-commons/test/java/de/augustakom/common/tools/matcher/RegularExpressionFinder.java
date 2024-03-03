package de.augustakom.common.tools.matcher;

import java.util.regex.*;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher checks if a given {@link Pattern} or its corresponding regular expression {@link String} can be found in a
 * non-null {@link String} (simply checks for null, the type and then casts before executing the pattern matching).
 */
public class RegularExpressionFinder extends TypeSafeMatcher<String> {

    private final Pattern pattern;

    public RegularExpressionFinder(String pattern) {
        this(Pattern.compile(pattern));
    }

    public RegularExpressionFinder(String pattern, int flags) {
        this(Pattern.compile(pattern, flags));
    }

    public RegularExpressionFinder(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("finds regular expression ").appendValue(pattern);
    }

    @Override
    public boolean matchesSafely(String item) {
        return pattern.matcher(item).find();
    }

    @Factory
    public static Matcher<String> findsPattern(Pattern pattern) {
        return new RegularExpressionFinder(pattern);
    }

    @Factory
    public static Matcher<String> findsPattern(String pattern) {
        return new RegularExpressionFinder(pattern);
    }

    @Factory
    public static Matcher<String> findsPattern(String pattern, int flags) {
        return new RegularExpressionFinder(pattern, flags);
    }
}
