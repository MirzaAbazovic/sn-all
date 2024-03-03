/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.wita.acceptance.common.actor;

import java.util.*;
import com.consol.citrus.TestActor;
import org.springframework.beans.factory.InitializingBean;

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
