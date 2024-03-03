package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("DEFAULT_TEQ")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class DEFAULT_TEQ extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ(false);
    }
}
