/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2010 18:59:36
 */
package de.augustakom.common.gui.swing.table;


/**
 * Methoden fuer die AND / OR - Relationen zur Verbindung von Filtern.
 *
 *
 */
public enum FilterRelations {
    AND {
        @Override
        public boolean concatenate(boolean value1, boolean value2) {
            return value1 && value2;
        }

        @Override
        public boolean startValue() {
            return true;
        }

        @Override
        public boolean canStop(boolean value) {
            return !value;
        }
    },
    OR {
        @Override
        public boolean concatenate(boolean value1, boolean value2) {
            return value1 || value2;
        }

        @Override
        public boolean startValue() {
            return false;
        }

        @Override
        public boolean canStop(boolean value) {
            return value;
        }
    };

    public abstract boolean concatenate(boolean value1, boolean value2);

    public abstract boolean startValue();

    public abstract boolean canStop(boolean value);
}
