/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2012 15:35:36
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;

import static org.testng.Assert.*;

import java.util.*;
import java.util.concurrent.*;
import com.google.common.collect.Lists;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.HVTStandort;
import de.mnet.hurrican.e2e.common.StandortDataBuilder.StandortData;

@Test(enabled = false)
public class MultiThreadingTest extends BaseWholesaleE2ETest {

    private CountDownLatch latch = new CountDownLatch(1);

    public void parallelReservePortShouldWork() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Future<?>> futures = Lists.newArrayList();
        for (int i = 0; i < 30; i++) {
            futures.add(executor.submit(new CreateStandortAndReservePortTask()));
        }
        executor.shutdown();
        latch.countDown();
        for (Future<?> future : futures) {
            future.get();
        }
    }

    public void parallelReservePortOnSameLocationShouldOnlyYieldTechnicalException() throws Exception {
        int numParallelExecutions = 30;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        StandortData localStandortData = standortDataBuilderProvider.get()
                .rangierungsCount(numParallelExecutions)
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).getStandortData();

        List<Future<?>> futures = Lists.newArrayList();
        for (int i = 0; i < numParallelExecutions; i++) {
            futures.add(executor.submit(new CreateStandortAndReservePortOnSameLocationTask(localStandortData)));
        }
        executor.shutdown();
        latch.countDown();
        for (Future<?> future : futures) {
            try {
                future.get();
            }
            catch (ExecutionException ee) {
                SoapFaultClientException e = (SoapFaultClientException) ee.getCause();
                SoapFault fault = e.getSoapFault();
                SoapFaultDetail detail = fault.getFaultDetail();
                if (detail != null) {
                    Iterator<SoapFaultDetailElement> detailEntries = detail.getDetailEntries();
                    assertFalse(detailEntries.hasNext(), "Functional Fault thrown: "
                            + extractSoapFaultDetail(e).getErrorDescription());
                }
            }
        }

    }

    private abstract class BaseCreateStandortAndReservePortTask implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            latch.await();
            try {
                StandortData localStandortData = getStandortData();

                stateProvider.get()
                        .standortData(localStandortData)
                        .ekpData(ekpDataBuilderProvider.get().getEkpData())
                        .reservePort();
            }
            catch (Exception e) {
                throw e;
            }
            return null;
        }

        protected abstract StandortData getStandortData() throws Exception;

    }

    private class CreateStandortAndReservePortTask extends BaseCreateStandortAndReservePortTask {

        @Override
        protected StandortData getStandortData() throws Exception {
            return standortDataBuilderProvider.get()
                    .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                    .getStandortData();
        }

    }

    private class CreateStandortAndReservePortOnSameLocationTask extends BaseCreateStandortAndReservePortTask {

        private final StandortData sharedStandortData;

        public CreateStandortAndReservePortOnSameLocationTask(StandortData standortData) {
            sharedStandortData = standortData;
        }

        @Override
        protected StandortData getStandortData() throws Exception {
            return sharedStandortData;
        }

    }

}


