package de.mnet.hurrican.atlas.simulator.wita.builder;

import java.time.*;
import java.util.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;
import de.mnet.wita.message.MeldungsType;

@Component("NEU_QEB_ABM_BEFORE_HOLIDAY")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_ABM_BEFORE_HOLIDAY extends AbstractWitaTest {

    @Override
    public void configure() {

        receiveOrderREQ()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()),
                        "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        int currentYear = LocalDate.now().getYear();

        // set the ABM date to a friday
        variables().add(
                "requestedCustomerDate",
                DateTools.formatDate(Date.from(LocalDate.of(currentYear + 1, 8, 7).atStartOfDay(ZoneId.systemDefault()).toInstant()), // 8.8. ist Feiertag!
                        DateTools.PATTERN_YEAR_MONTH_DAY));

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);
    }

}
