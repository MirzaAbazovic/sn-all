package de.mnet.hurrican.atlas.simulator.wita.builder;

import static de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;

@Component("HVT_KVZ_ABBM_KVZ_SECOND_KVZ")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class HVT_KVZ_ABBM_KVZ_SECOND_KVZ extends AbstractHvtKvzTest {

    @Override
    public void configure() {
        receiveOrderREQ();

        waitForIoArchiveRequestEntry();

        conditional(
                sendNotification("N_QEB"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB),

                // create new contract id for next messages
                variables().add("contractId", "citrus:randomNumber(10)"),

                sendNotification("N_ABM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM),

                sendNotification("N_ERLM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM),

                sendNotification("N_ENTM")

        ).when(String.format("${%s}", IS_BEREITSTELLUNG));
    }

}
