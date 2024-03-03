package de.mnet.hurrican.atlas.simulator.wita.builder;

import static de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;

@Component("HVT_KVZ_ABBM_HVT")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class HVT_KVZ_ABBM_HVT extends AbstractHvtKvzTest {

    @Override
    public void configure() {
        receiveOrderREQ();

        waitForIoArchiveRequestEntry();

        conditional(
                sendNotification("K_QEB"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB),

                sendNotification("K_ABBM")

        ).when(String.format("${%s}", IS_KUENDIGUNG));
    }

}
