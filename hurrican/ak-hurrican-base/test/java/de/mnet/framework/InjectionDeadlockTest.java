package de.mnet.framework;

import static org.testng.Assert.*;

import java.lang.management.*;
import java.util.concurrent.*;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = { BaseTest.SERVICE })
/*
 Spring läuft bei einer ganz bestimmten Konfiguration (Layz-Init, Singletons und einem Mix aus annotierten
 und XML definierten Beans) regelmäßig in einen Deadlock. Der Deadlock entsteht, wenn zur Laufzeit zwei Threads
 (bspw. Scheduler Threads) unterschiedliche Spring Managed Beans zur gleichen Zeit anfordern. Dabei steigt Spring via
 getBean() ein und sperrt sich im Verlauf regelmäßig selber aus. Die angeforderten Beans müssen für einen Thread in
 XML und für den zweiten Thread als Annotation definiert sein. Siehe hierzu einen produktiven Stacktrace in HUR-17025.
 */
public class InjectionDeadlockTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(InjectionDeadlockTest.class);

    private ClassPathXmlApplicationContext applicationContext = null;
    private CyclicBarrier gateStart;
    private CyclicBarrier gateStop;
    private int retries = 200;
    Thread worker1;
    Thread worker2;
    Thread main;

    private int detectDeadlocks() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadBean.findMonitorDeadlockedThreads();
        return (threadIds != null) ? threadIds.length : 0;
    }

    private void runInjectionDeadlockTest(final String contextPath) throws InterruptedException {
        applicationContext = new ClassPathXmlApplicationContext(contextPath);
        gateStart = new CyclicBarrier(2);
        gateStop = new CyclicBarrier(2);
        worker1 = new Thread() {
            public void run() {
                try {
                    gateStart.await();
                    LOGGER.info("Worker1: Executing 'getBean'");
                    Object injectThis = applicationContext.getBean("de.mnet.framework.WitaBsiProtokollService");
                    LOGGER.info(String.format("Worker1: Bean %s request successful", injectThis.toString()));
                    gateStop.await();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    main.interrupt();
                }
            }
        };
        worker2 = new Thread() {
            public void run() {
                try {
                    gateStart.await();
                    LOGGER.info("Worker2: Executing 'getBean'");
                    Object injectThis = applicationContext.getBean("de.mnet.framework.CreateCPSTx4BACommand");
                    LOGGER.info(String.format("Worker2: Bean %s request successful", injectThis.toString()));
                    gateStop.await();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    main.interrupt();
                }
            }
        };
        worker1.start();
        worker2.start();
        worker1.join(10000);
    }

    private void printStacktrace(Thread worker, String caption) {
        if (worker != null) {
            StackTraceElement[] stackTrace = worker.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                LOGGER.info(caption);
                for (StackTraceElement element : stackTrace) {
                    LOGGER.info(element);
                }
            }
        }
    }

    /*
     Syntethischer Test ob mit gegebenem Context der Deadlock auftritt. Die kritischen Codepfade werden bis zu 200 Mal
     wiederholt und nach jedem Zyklus wird ermittelt, ob ein Deadlock aufgetreten ist.
     */
    @Test(enabled = false)
    public void deadlockOccursTest() throws InterruptedException {
        main = Thread.currentThread();
        int deadlocks = detectDeadlocks();
        for (int i = 1; i < retries; i++) {
            runInjectionDeadlockTest("de/mnet/framework/DeadlockOccursContext.xml");
            int currentDeadlocks = detectDeadlocks();
            if (deadlocks < currentDeadlocks) {
                printStacktrace(worker1, "[WORKER1]");
                printStacktrace(worker2, "[WORKER2]");
                LOGGER.info(String.format("Deadlock occured after %d loops.", i));
                return;
            }
            deadlocks = currentDeadlocks;
        }
        fail("Deadlock didn't occur!");
    }

    /*
     Syntethischer Test ob mit gegebenem Context der Deadlock nicht auftritt. Die kritischen Codepfade werden bis zu
     200 Mal wiederholt und nach jedem Zyklus wird ermittelt, ob ein Deadlock aufgetreten ist.
     */
    @Test(enabled = false)
    public void noDeadlockOccursTest() throws InterruptedException {
        main = Thread.currentThread();
        int deadlocks = detectDeadlocks();
        for (int i = 1; i < retries; i++) {
            runInjectionDeadlockTest("de/mnet/framework/NoDeadlockOccursContext.xml");
            int currentDeadlocks = detectDeadlocks();
            if (deadlocks < currentDeadlocks) {
                printStacktrace(worker1, "[WORKER1]");
                printStacktrace(worker2, "[WORKER2]");
                fail(String.format("Deadlock occured [Loop count: %d]!", i));
            }
            deadlocks = currentDeadlocks;
        }
    }
}
