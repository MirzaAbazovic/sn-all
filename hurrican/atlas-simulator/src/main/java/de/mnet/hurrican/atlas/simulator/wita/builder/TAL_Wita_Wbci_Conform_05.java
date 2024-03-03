package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;

/**
 * Testfall dient zum Test, dass der WITA-Vorgang als Klärfall markiert wird, wenn der über WBCI abgestimmte
 * Wechseltermin in der ABM nicht übereinstimmt. Siehe de.mnet.wita.acceptance.WitaWbciConformanceTest.
 */
@Component("TAL_Wita_Wbci_Conform_05")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Wbci_Conform_05 extends AbstractWitaTest {
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
    }

}
