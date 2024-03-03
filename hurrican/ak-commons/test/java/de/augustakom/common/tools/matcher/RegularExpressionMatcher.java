package de.augustakom.common.tools.matcher;

import java.util.regex.*;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher checks if a given {@link Pattern} or its corresponding regular expression {@link String} matches for a
 * non-null {@link String} (simply checks for null, the type and then casts before executing the pattern matching).
 */
public class RegularExpressionMatcher extends TypeSafeMatcher<String> {

    private final Pattern pattern;

    public RegularExpressionMatcher(String pattern) {
        this(Pattern.compile(pattern));
    }

    public RegularExpressionMatcher(String pattern, int flags) {
        this(Pattern.compile(pattern, flags));
    }

    public RegularExpressionMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches regular expression ").appendValue(pattern);
    }

    @Override
    public boolean matchesSafely(String item) {
        return pattern.matcher(item).matches();
    }

    @Factory
    public static Matcher<String> matchesPattern(Pattern pattern) {
        return new RegularExpressionMatcher(pattern);
    }

    @Factory
    public static Matcher<String> matchesPattern(String pattern) {
        return new RegularExpressionMatcher(pattern);
    }

    @Factory
    public static Matcher<String> matchesPattern(String pattern, int flags) {
        return new RegularExpressionMatcher(pattern, flags);
    }
}
