/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2006 12:32:12
 */
package de.augustakom.common.gui.swing.table;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Implementierung eines TableModels, das die darzustellenden Werte per Reflection ermittelt (siehe
 * AKReflectionTableModel). <br> Dem Modell kann fuer jede Spalte jedoch noch eine Map mit Referenz-Werten uebergeben
 * werden, die statt dem eigentlich Wert aus der Datenliste verwendet werden sollen.
 *
 *
 */
public class AKReferenceAwareTableModel<T> extends AKReflectionTableModel<T> {

    private static final long serialVersionUID = 7855748844059620851L;

    private static final Logger LOGGER = Logger.getLogger(AKReferenceAwareTableModel.class);

    private static final String ERROR_MSG = "<error>";

    private Map<Integer, Reference> referenceMap = null;

    /**
     * @see de.augustakom.common.gui.swing.table.AKReflectionTableModel
     */
    public AKReferenceAwareTableModel(String[] columnNames,
            String[] propertyNames, Class<?>[] classTypes) {
        super(columnNames, propertyNames, classTypes);
        init();
    }

    public AKReferenceAwareTableModel(ReflectionTableMetaData... reflectionTableMetaDatas) {
        super(reflectionTableMetaDatas);
        init();
    }

    /**
     * Initialisiert das TableModel.
     */
    private void init() {
        referenceMap = new HashMap<Integer, Reference>();
    }

    /**
     * Uebergibt eine Referenz-Liste fuer die Spalte <code>column</code>. Fuer eine hier definierte Spalte wird der Wert
     * aus der Ursprungsdatenliste ermittelt und mit diesem Wert eine Uebereinstimmung in der Map
     * <code>references</code> gesucht (als Key). Wird eine Uebereinstimmung gefunden, wird als Wert fuer die Spalte der
     * Wert des Properties <code>dispProperty</code> zurueck geliefert.
     *
     * @param column       Spalte, fuer die die Referenz gedacht ist.
     * @param references   Map mit den References. Als Key wird ein Wert erwartet, der in der Ursprungsliste fuer diese
     *                     Spalte vorhanden ist. Als Value kann ein beliebiges Objekt (herkoemmlich das Modell)
     *                     enthalten sein.
     * @param dispProperty Property-Name des Properties, das fuer die Darstellung gedacht ist.
     *
     */
    public void addReference(int column, Map<?, ?> references, String dispProperty) {
        Reference ref = new Reference();
        ref.setColumn(column);
        ref.setReferences(references);
        ref.setDispProperty(dispProperty);

        referenceMap.put(column, ref);
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKReflectionTableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        Object value = super.getValueAt(row, column);

        Reference ref = referenceMap.get(column);
        if (ref != null) {
            try {
                // darzustellenden Wert aus Reference-Liste ermitteln
                return ref.getReferenceValue(value);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return ERROR_MSG;
            }
        }

        return value;
    }

    /**
     * Hilfsklasse fuer die Reference-Darstellung.
     */
    class Reference {
        private int column = -1;
        private Map<?, ?> references = null;
        private String dispProperty = null;

        /**
         * Ermittelt, ob in der Map ein Eintrag zu dem Key <code>key</code> vorhanden ist. Ist dies der Fall, wird von
         * dem erhaltenen Objekt das Property 'dispProperty' ermittelt und zurueck gegeben.
         *
         * @param key erwarteter Key
         * @return Property-Value oder 'null'
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         *
         */
        public Object getReferenceValue(Object key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            if (references != null) {
                Object value = references.get(key);
                if (value != null) {
                    try {
                        return PropertyUtils.getNestedProperty(value, dispProperty);
                    }
                    catch (NoSuchMethodException e) {
                        if (StringUtils.equals(dispProperty, "toString")) {
                            return value.toString();
                        }
                        throw e;
                    }
                }
            }
            return null;
        }

        /**
         * @return Returns the column.
         */
        public int getColumn() {
            return this.column;
        }

        /**
         * @param column The column to set.
         */
        public void setColumn(int column) {
            this.column = column;
        }

        /**
         * @return Returns the dispProperty.
         */
        public String getDispProperty() {
            return this.dispProperty;
        }

        /**
         * @param dispProperty The dispProperty to set.
         */
        public void setDispProperty(String dispProperty) {
            this.dispProperty = dispProperty;
        }

        /**
         * @return Returns the references.
         */
        public Map<?, ?> getReferences() {
            return this.references;
        }

        /**
         * @param references The references to set.
         */
        public void setReferences(Map<?, ?> references) {
            this.references = references;
        }
    }
}


