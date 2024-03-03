package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.Storno;

/**
 *
 */
@Component("NEU_QEB_STORNO_STORNO")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_STORNO_STORNO extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ("NEU");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        waitForMnetWitaRequestByExtOrder(Storno.class);
        receiveNotification("STORNO");

        waitForIoArchiveEntryByExtOrderNo(MeldungsType.STORNO);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);

        receiveNotification("STORNO2");

        waitForIoArchiveEntryByExtOrderNo(MeldungsType.STORNO, 2L);

        sendNotification("ABBM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABBM);
    }

}
