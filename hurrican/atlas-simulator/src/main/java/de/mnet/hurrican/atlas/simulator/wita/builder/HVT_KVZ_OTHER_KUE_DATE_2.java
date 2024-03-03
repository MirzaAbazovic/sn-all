package de.mnet.hurrican.atlas.simulator.wita.builder;

import static de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.TerminVerschiebung;

@Component("HVT_KVZ_OTHER_KUE_DATE_2")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class HVT_KVZ_OTHER_KUE_DATE_2 extends AbstractHvtKvzTest {

    @Override
    public void configure() {
        receiveOrderREQ();

        waitForIoArchiveRequestEntry();

        conditional(
                sendNotification("K_QEB"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB),

                sendNotification("K_ABM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM),

                // confirm the cancellation date 1 week later as the request date
                variables().add(CONFIRMED_CUSTOMER_DATE,
                        String.format("atlas:asWorkingDayAndNextDayNotHoliday('yyyy-MM-dd', ${%s}, '+7d')",
                                REQUESTED_CUSTOMER_DATE)
                ),

                waitForMnetWitaRequestByExtOrder(TerminVerschiebung.class),
                receiveNotification("K_TV"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.TV),

                sendNotification("K_TV_ABM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM, 2L),

                sendNotification("K_ERLM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM),

                sendNotification("K_ENTM")
        ).when(String.format("${%s}", IS_KUENDIGUNG));

        conditional(
                sendNotification("N_QEB"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB),

                // create new contract id for next messages
                variables().add("contractId", "citrus:randomNumber(10)"),

                // confirm the date 1 week later as the request date
                variables().add(CONFIRMED_CUSTOMER_DATE,
                        String.format("atlas:asWorkingDayAndNextDayNotHoliday('yyyy-MM-dd', ${%s}, '+7d')",
                                REQUESTED_CUSTOMER_DATE)
                ),

                sendNotification("N_ABM"),
                //wait a bit longer before sending the ERLM for the NEUBESTELLUNG,
                // because the TV, which is sent automatically, must be sent before receiving the ERLM
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM),

                sendNotification("N_ERLM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM),

                sendNotification("N_ENTM")
        ).when(String.format("${%s}", IS_BEREITSTELLUNG));
    }
}
