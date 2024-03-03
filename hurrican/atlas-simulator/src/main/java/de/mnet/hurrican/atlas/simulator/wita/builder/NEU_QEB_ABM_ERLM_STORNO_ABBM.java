package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.Storno;

/**
 *
 */
@Component("NEU_QEB_ABM_ERLM_STORNO_ABBM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_ABM_ERLM_STORNO_ABBM extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ("NEU");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        waitForMnetWitaRequestByExtOrder(Storno.class);
        receiveNotification("STORNO");

        waitForIoArchiveEntryByExtOrderNo(MeldungsType.STORNO);

        sendNotification("ABBM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABBM);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);
    }

}
