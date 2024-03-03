/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm.builder;

import org.springframework.stereotype.Component;

/**
 *
 */
@Component("createOrder")
public class FFM_DEFAULT_CreateOrder extends AbstractFFMTestBuilder {

    @Override
    protected void configure() {
        receiveCreateOrder();
        echo("Received createOrder request!");
    }
}
