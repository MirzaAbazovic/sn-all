/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.2011 14:20:07
 */
package de.mnet.wita.acceptance.common.function;

import java.util.*;
import com.google.common.collect.Ordering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.service.MwfEntityService;

public class WaitForMwfEntityFunction<ENTITY extends MwfEntity> extends AbstractWaitForMessageFunction<ENTITY> {
    @Autowired
    private MwfEntityService mwfEntityService;

    private final ENTITY example;

    public WaitForMwfEntityFunction(ENTITY example, ApplicationContext applicationContext) {
        this(example, 1, applicationContext);
    }

    public WaitForMwfEntityFunction(ENTITY example,
            int expectedNumber, ApplicationContext applicationContext) {
        super(expectedNumber, Ordering.<Long>natural()
                .onResultOf(MwfEntity.GET_ID_FUNCTION), applicationContext);
        this.example = example;
    }

    @Override
    protected List<ENTITY> getEntities() {
        return mwfEntityService.findMwfEntitiesByExample(example);
    }

}


