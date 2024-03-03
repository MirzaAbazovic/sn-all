/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2010 17:48:14
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import java.lang.reflect.*;
import javax.swing.*;
import org.apache.log4j.Logger;

/**
 * Abstrakte Klasse um Quartz Listener Swing Thread sicher zu machen. Prinzipiell geschieht dies ueber
 * SwingUtilities.invoke().
 *
 *
 */
public abstract class AKAbstractSchedulerListener {
    private static final Logger LOGGER = Logger.getLogger(AKSchedulerListener.class);

    /**
     * <code>callMethod</code> dient als einfache Methodenschnittstelle zum einbinden der Inneren Runnable Klasse
     */
    public void callMethod(Object listener, String name, Class<?>[] paramTypes, Object[] params) {
        if (listener != null) {
            ListenerWorker doWork = new ListenerWorker(listener, name, paramTypes, params);
            SwingUtilities.invokeLater(doWork);
        }
    }

    /**
     * Runnable Klasse, die im Swing Thread Kontext laeuft und die ueber Reflection ermittelte Methode zur Ausfuehrung
     * bringt.
     *
     *
     */
    private static class ListenerWorker implements Runnable {
        private String name = null;
        private Object listener = null;
        private Object[] params = null;
        private Class<?>[] paramTypes = null;

        public ListenerWorker(Object listener, String name, Class<?>[] paramTypes, Object[] params) {
            this.name = name;
            this.params = params;
            this.listener = listener;
            this.paramTypes = paramTypes;
        }

        @Override
        public void run() {
            try {
                Method method = listener.getClass().getMethod(name, paramTypes);
                method.invoke(listener, params);
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }
    }

}
