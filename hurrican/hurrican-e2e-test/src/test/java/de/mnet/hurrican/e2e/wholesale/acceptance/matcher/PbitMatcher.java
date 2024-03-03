/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 13:27:25
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.hurrican.wholesale.workflow.Pbit;

/**
 * Matcher fuer {@link Pbit} Objekte
 */
@ObjectsAreNonnullByDefault
public class PbitMatcher extends TypeSafeMatcher<Pbit> {

    private String service;
    private Integer limit;

    public PbitMatcher(String service, Integer limit) {
        this.service = service;
        this.limit = limit;
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("service ").appendValue(service)
                .appendText("; limit ").appendValue(limit);
    }

    @Override
    protected boolean matchesSafely(Pbit item) {
        if (service.equals(item.getService()) && limit.equals(item.getLimit())) {
            return true;
        }
        return false;
    }


}


