/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2010 18:49:48
 */
package de.augustakom.common.gui.swing.table;

import java.util.*;
import org.apache.commons.lang.StringUtils;


/**
 * AND / OR - Relation fuer die Verbindung von Filtern. Kann Kind-Relationen und Kind-Operatoren besitzen, die
 * ausgewertet werden. Besitzt einen Namen, der im Standard-Fall {@code null} ist.
 *
 *
 */
public class FilterRelation {
    private FilterRelations filterRelation;
    private final List<FilterRelation> childRelations = new ArrayList<FilterRelation>();
    private final List<FilterOperator> childOperators = new ArrayList<FilterOperator>();
    private String name;


    public FilterRelation(String name, FilterRelations filterRelation) {
        this.name = name;
        this.filterRelation = filterRelation;
    }

    public FilterRelation(FilterRelations filterRelation) {
        this(null, filterRelation);
    }


    public <T> Collection<T> filter(AKMutableTableModel model, Collection<T> values) {
        Collection<T> filteredValues = new ArrayList<T>();
        if (values == null) {
            return filteredValues;
        }
        Iterator<T> it = values.iterator();
        for (int row = 0; (row < model.getRowCount()) && it.hasNext(); ++row) {
            T value = it.next();
            if (filterSingle(model, row)) {
                filteredValues.add(value);
            }
        }
        return filteredValues;
    }

    private <T> boolean filterSingle(AKMutableTableModel model, int row) {
        boolean result = filterRelation.startValue();
        for (FilterOperator childOperator : childOperators) {
            result = filterRelation.concatenate(result, childOperator.filter(model, row));
            if (filterRelation.canStop(result)) {
                break;
            }
        }
        if (!filterRelation.canStop(result)) {
            for (FilterRelation childRelation : childRelations) {
                result = filterRelation.concatenate(result, childRelation.filterSingle(model, row));
                if (filterRelation.canStop(result)) {
                    break;
                }
            }
        }
        return result;
    }


    public void removeFilter(String name) {
        List<FilterRelation> toRemove = new ArrayList<FilterRelation>(childRelations.size());
        List<FilterRelation> toAdd = new ArrayList<FilterRelation>();
        for (FilterRelation relation : childRelations) {
            if (relation != null) {
                if (StringUtils.equals(relation.getName(), name)) {
                    toRemove.add(relation);
                }
                else {
                    relation.removeFilter(name);
                    if (relation.getChildCount() == 1) {
                        childOperators.addAll(relation.childOperators);
                        toAdd.addAll(relation.childRelations);
                    }
                    if (relation.getChildCount() <= 1) {
                        toRemove.add(relation);
                        // iterator.remove() hier entfernt, da die childRelations weiter oben veraendert sein
                        // koennten und somit eine ConcurrentModificationException geworfen worden waeren
                    }
                }
            }
        }

        for (FilterRelation relation : toRemove) {
            childRelations.remove(relation);
        }
        childRelations.addAll(toAdd);

        for (Iterator<FilterOperator> iterator = childOperators.iterator(); iterator.hasNext(); ) {
            FilterOperator operator = iterator.next();
            if (StringUtils.equals(operator.getName(), name)) {
                iterator.remove();
            }
        }
    }


    public int getChildCount() {
        return childRelations.size() + childOperators.size();
    }

    public void addChild(FilterRelation childRelation) {
        childRelations.add(childRelation);
    }

    public void removeChild(FilterRelation childRelation) {
        childRelations.remove(childRelation);
    }

    public void addChild(FilterOperator childOperator) {
        childOperators.add(childOperator);
    }

    public void removeChild(FilterOperator childOperator) {
        childOperators.remove(childOperator);
    }

    public FilterRelations getRelation() {
        return filterRelation;
    }

    public void setRelation(FilterRelations relation) {
        this.filterRelation = relation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
