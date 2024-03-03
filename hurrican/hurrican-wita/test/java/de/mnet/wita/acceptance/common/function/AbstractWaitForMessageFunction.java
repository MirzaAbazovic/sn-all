/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2011 11:58:44
 */
package de.mnet.wita.acceptance.common.function;

import java.util.*;
import org.springframework.context.ApplicationContext;

public abstract class AbstractWaitForMessageFunction<ENTITY> extends AbstractAutowiringAcceptanceFunction<Boolean> {

    protected final int expectedNumber;
    protected final Comparator<? super ENTITY> comparator;

    public AbstractWaitForMessageFunction(int expectedNumber, Comparator<? super ENTITY> comparator, ApplicationContext applicationContext) {
        super(applicationContext);

        this.expectedNumber = expectedNumber;
        this.comparator = comparator;
    }

    @Override
    public Boolean apply(Void input) {
        List<ENTITY> entities = getEntities();
        if (entities.size() < expectedNumber) {
            return false;
        }
        Collections.sort(entities, comparator);
        ENTITY achieved = entities.get(expectedNumber - 1);

        return validateMessage(achieved); // check last archived message
    }

    protected Boolean validateMessage(@SuppressWarnings("unused") ENTITY message) {
        return true;
    }

    protected abstract List<ENTITY> getEntities();


}
