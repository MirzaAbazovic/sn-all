/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.citrus.actor;

import java.util.*;
import com.consol.citrus.TestActor;
import org.springframework.beans.factory.InitializingBean;

/**
 * Spring bean invoked after Spring application context is setup. Manager enables/disables test actors according to
 * system property values.
 *
 *
 */
public class TestActorManager implements InitializingBean {

    /**
     * Configuration mapping for test actors being post processed
     */
    private Map<TestActor, Boolean> actors = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<TestActor, Boolean> entry : actors.entrySet()) {
            entry.getKey().setDisabled(entry.getValue());
        }
    }

    public void setActors(Map<TestActor, Boolean> actors) {
        this.actors = actors;
    }
}
