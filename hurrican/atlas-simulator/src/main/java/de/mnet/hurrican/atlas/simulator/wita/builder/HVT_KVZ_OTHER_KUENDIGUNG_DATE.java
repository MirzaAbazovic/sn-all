package de.mnet.hurrican.atlas.simulator.wita.builder;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames.*;

import java.text.*;
import java.time.*;
import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;

@Component("HVT_KVZ_OTHER_KUENDIGUNG_DATE")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class HVT_KVZ_OTHER_KUENDIGUNG_DATE extends AbstractHvtKvzTest {

    @Override
    public void configure() {
        receiveOrderREQ();

        waitForIoArchiveRequestEntry();

        conditional(
                sendNotification("K_QEB"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB),

                // confirm the cancellation date 1 week later as the request date
                variables().add(CONFIRMED_CUSTOMER_DATE,
                        String.format("atlas:asWorkingDayAndNextDayNotHoliday('yyyy-MM-dd', ${%s}, '+7d')",
                                REQUESTED_CUSTOMER_DATE)
                ),

                sendNotification("K_ABM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM),

                sendNotification("K_ERLM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM),

                sendNotification("K_ENTM")
        ).when(String.format("${%s}", IS_KUENDIGUNG));

        conditional(
                sendNotification("N_QEB"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB),

                // create new contract id for next messages
                variables().add("contractId", "citrus:randomNumber(10)"),

                new AbstractTestAction() {
                    @Override
                    public void doExecute(TestContext context) {
                        final String requestedCustomerDate = context.getVariable(REQUESTED_CUSTOMER_DATE);
                        String datePattern = "yyyy-MM-dd";
                        final LocalDate originalKwt =
                                asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17));
                        Date expectedKwt = Date.from(asWorkingDayAndNextDayNotHoliday(originalKwt.plusDays(7)).atStartOfDay(ZoneId.systemDefault()).toInstant());

                        if (!requestedCustomerDate.equals(new SimpleDateFormat(datePattern).format(expectedKwt))) {
                            throw new CitrusRuntimeException(String.format("Failed to validate requested customer date, expected %s but was %s",
                                    expectedKwt, requestedCustomerDate));
                        }
                    }
                },

                sendNotification("N_ABM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM),

                sendNotification("N_ERLM"),
                waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM),

                sendNotification("N_ENTM")
        ).when(String.format("${%s}", IS_BEREITSTELLUNG));
    }

}
