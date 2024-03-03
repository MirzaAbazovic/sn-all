/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2010 18:50:26
 */
package de.augustakom.common.gui.swing.table;

import java.math.*;
import java.sql.*;
import java.text.*;
import java.util.Date;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.WildcardTools;


/**
 * Methoden der Operatoren fuer die Filterung von Daten - EQ, NEQ, ...
 *
 *
 */
public enum FilterOperators {
    EQ {
        @Override
        public boolean comparisonResult(int result) {
            return result == 0;
        }
    },
    LT {
        @Override
        public boolean comparisonResult(int result) {
            return result < 0;
        }
    },
    GT {
        @Override
        public boolean comparisonResult(int result) {
            return result > 0;
        }
    },
    LT_EQ {
        @Override
        public boolean comparisonResult(int result) {
            return result <= 0;
        }
    },
    GT_EQ {
        @Override
        public boolean comparisonResult(int result) {
            return result >= 0;
        }
    },
    NOT_EQ {
        @Override
        public boolean comparisonResult(int result) {
            return result != 0;
        }
    };


    public boolean compare(Object value, Object filterValue) {
        if ((filterValue != null) && (value != null)) {
            // Falls zwei Strings verglichen werden, beachte Wildcards
            if ((value instanceof String) && (filterValue instanceof String)
                    && (WildcardTools.containsWildcard((String) filterValue))) {
                // Bei Wildcards werden nur die Operatoren == und != berücksichtigt.
                boolean test = (WildcardTools.matchIgnoreCase((String) value, (String) filterValue));
                switch (this) {
                    case EQ:
                        return test;
                    case LT:
                        return test;
                    case LT_EQ:
                        return test;
                    case GT:
                        return test;
                    case GT_EQ:
                        return test;
                    case NOT_EQ:
                        return !test;
                    default:
                        break;
                }
            }
            else if ((value instanceof Timestamp) && (filterValue instanceof Timestamp)) {
                return comparisonResult(((Timestamp) value).compareTo((Timestamp) filterValue));
            }
            else if ((value instanceof Date) && (filterValue instanceof Date)) {
                return comparisonResult(DateTools.compareDates((Date) value, (Date) filterValue));
            }
            else if ((value instanceof Number) && (filterValue instanceof Number)) {
                int test;
                if ((value instanceof Integer) && (filterValue instanceof Integer)) {
                    test = ((Integer) value).compareTo((Integer) filterValue);
                }
                else if ((value instanceof BigDecimal) && (filterValue instanceof BigDecimal)) {
                    test = ((BigDecimal) value).compareTo((BigDecimal) filterValue);
                }
                else {
                    test = ((Long) value).compareTo((Long) filterValue);
                }
                return comparisonResult(test);
            }
            else if (((value instanceof Float) && (filterValue instanceof Float))
                    || ((value instanceof Double) && (filterValue instanceof Double))) {
                // Float-Werte werden zur Anzeige in der GUI auf eine Nachkommastelle gerundet,
                // Für den Vergleich werden diese Werte ebenfalls gerundet und dann erst verglichen
                Double val = ((double) ((int) ((Double) value * 10))) / 10;
                Double filterVal = ((double) ((int) ((Double) filterValue * 10))) / 10;
                return comparisonResult(val.compareTo(filterVal));
            }
            else if ((value instanceof Boolean) && (filterValue instanceof Boolean)) {
                return comparisonResult(((Boolean) value).compareTo((Boolean) filterValue));
            }
            else if (value instanceof Enum<?>) {
                return comparisonResult(((Enum<?>) value).toString().compareTo(((Enum<?>) filterValue).toString()));
            }
            else if ((value instanceof String) && (filterValue instanceof String)) {
                Collator myCollator = Collator.getInstance();
                int test = myCollator.compare(value, filterValue);
                return comparisonResult(test);
            }
            else if ((value instanceof Comparable) && (filterValue instanceof Comparable)) {
                return comparisonResult(((Comparable) value).compareTo(filterValue));
            }
            else {
                boolean test = value.equals(filterValue);
                switch (this) {
                    case EQ:
                        return test;
                    case LT:
                        return test;
                    case LT_EQ:
                        return test;
                    case GT:
                        return test;
                    case GT_EQ:
                        return test;
                    case NOT_EQ:
                        return !test;
                    default:
                        break;
                }
            }
        }
        else if ((value == null) && (filterValue == null)) {
            return comparisonResult(0);
        }
        else {
            switch (this) {
                case NOT_EQ:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    protected abstract boolean comparisonResult(int result);
}
