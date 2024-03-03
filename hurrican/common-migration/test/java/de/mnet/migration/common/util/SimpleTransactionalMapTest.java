/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2010 11:58:46
 */
package de.mnet.migration.common.util;

import static org.testng.Assert.*;

import java.util.*;
import java.util.concurrent.*;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;


/**
 *
 */
@Test(groups = "unit")
public class SimpleTransactionalMapTest extends MigrationBaseTest {
    private static final Integer KEY1 = Integer.valueOf(1);


    public void testToDebug() throws InterruptedException, ExecutionException {
        final Integer NINE = Integer.valueOf(9);
        final Integer TEN = Integer.valueOf(10);
        final SimpleTransactionalMap<Integer, List<Integer>> map = new SimpleTransactionalMap<Integer, List<Integer>>(
                new SimpleTransactionalMap.Duplicate<List<Integer>>() {
                    @Override
                    public List<Integer> duplicate(List<Integer> value) {
                        return new ArrayList<Integer>(value);
                    }
                }
        );
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // Add
        List<Integer> listForModification = map.getOrCreateForModification(KEY1, new Callable<List<Integer>>() {
            public List<Integer> call() throws Exception {
                return new ArrayList<Integer>();
            }
        });

        Future<List<Integer>> getOrCreate = executor.submit(new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() throws Exception {
                List<Integer> list = map.getOrCreateForModification(KEY1, new Callable<List<Integer>>() {
                    public List<Integer> call() throws Exception {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        list.add(NINE);
                        return list;
                    }
                });
                list.add(TEN);
                map.commit();
                return list;
            }
        });

        Thread.sleep(100);
        assertTrue(!getOrCreate.isDone());

        listForModification.add(1);
        listForModification.add(2);
        listForModification.add(3);
        map.rollback();

        executor.shutdown();
        assertTrue(executor.awaitTermination(100, TimeUnit.MILLISECONDS));

        List<Integer> list = getOrCreate.get();
        assertEquals(list.size(), 2);
        assertTrue(list.contains(NINE));
        assertTrue(list.contains(TEN));
    }


    public void testGetOrCreateForModification() throws InterruptedException, ExecutionException {
        SimpleTransactionalMap<Integer, List<Integer>> map = new SimpleTransactionalMap<Integer, List<Integer>>(
                new SimpleTransactionalMap.Duplicate<List<Integer>>() {
                    @Override
                    public List<Integer> duplicate(List<Integer> value) {
                        return new ArrayList<Integer>(value);
                    }
                }
        );
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Void>> futureList = new ArrayList<Future<Void>>();
        Set<Integer> wanted = new HashSet<Integer>(501);
        for (int i = 0; i < 1000; ++i) {
            futureList.add(executor.submit(new AppenderGetOrCreateForModification(map, i)));
            if (i % 2 == 0) {
                wanted.add(i);
            }
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        for (Future<Void> future : futureList) {
            future.get();
        }

        assertEquals(new HashSet<Integer>(map.getOrCreate(KEY1, null)), wanted);
    }


    private static class AppenderGetOrCreateForModification implements Callable<Void> {
        private final SimpleTransactionalMap<Integer, List<Integer>> map;
        private final int i;

        public AppenderGetOrCreateForModification(SimpleTransactionalMap<Integer, List<Integer>> map, int i) {
            this.map = map;
            this.i = i;
        }

        @Override
        public Void call() throws Exception {
            List<Integer> list = map.getOrCreateForModification(KEY1, new Callable<List<Integer>>() {
                public List<Integer> call() throws Exception {
                    return new ArrayList<Integer>();
                }
            });
            list.add(i);
            if (i % 2 == 0) {
                map.commit();
            }
            else {
                map.rollback();
            }
            return null;
        }
    }


    public void testGetOrCreate() throws InterruptedException, ExecutionException {
        SimpleTransactionalMap<Integer, List<Integer>> map = new SimpleTransactionalMap<Integer, List<Integer>>();
        map.getOrCreate(Integer.valueOf(3), new Callable<List<Integer>>() {
            public List<Integer> call() throws Exception {
                ArrayList<Integer> list = new ArrayList<Integer>();
                list.add(1);
                list.add(2);
                list.add(3);
                return list;
            }
        });
        map.commit();
        Random ramdon = new Random();

        List<Future<Void>> futureList = new ArrayList<Future<Void>>();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1000; ++i) {
            futureList.add(executor.submit(new AppenderGetOrCreate(map, ramdon.nextInt(5))));
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        for (Future<Void> future : futureList) {
            future.get();
        }

        futureList.clear();
        executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1000; ++i) {
            futureList.add(executor.submit(new ValidatorGetOrCreate(map, ramdon.nextInt(5))));
        }
        for (Future<Void> future : futureList) {
            future.get();
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }


    private static class AppenderGetOrCreate implements Callable<Void> {
        private final SimpleTransactionalMap<Integer, List<Integer>> map;
        private final int i;

        public AppenderGetOrCreate(SimpleTransactionalMap<Integer, List<Integer>> map, int i) {
            this.map = map;
            this.i = i;
        }

        @Override
        public Void call() throws Exception {
            map.getOrCreate(Integer.valueOf(i), new Callable<List<Integer>>() {
                public List<Integer> call() throws Exception {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(i);
                    return list;
                }
            });
            map.commit();
            return null;
        }
    }


    private static class ValidatorGetOrCreate implements Callable<Void> {
        private final SimpleTransactionalMap<Integer, List<Integer>> map;
        private final int i;

        public ValidatorGetOrCreate(SimpleTransactionalMap<Integer, List<Integer>> map, int i) {
            this.map = map;
            this.i = i;
        }

        @Override
        public Void call() throws Exception {
            List<Integer> list = map.getOrCreate(Integer.valueOf(i), new Callable<List<Integer>>() {
                public List<Integer> call() throws Exception {
                    throw new RuntimeException("Should not be called");
                }
            });
            if (i == 3) {
                assertEquals(list.size(), 3);
            }
            else {
                assertEquals(list.size(), 1);
            }
            return null;
        }
    }
}
