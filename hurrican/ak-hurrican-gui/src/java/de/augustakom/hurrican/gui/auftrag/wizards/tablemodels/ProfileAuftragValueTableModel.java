/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2016
 */
package de.augustakom.hurrican.gui.auftrag.wizards.tablemodels;

import java.util.*;
import javax.swing.table.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.augustakom.hurrican.model.cc.ProfileAuftragValue;
import de.augustakom.hurrican.model.cc.ProfileParameter;
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;

public class ProfileAuftragValueTableModel extends AbstractTableModel {
    private List<ProfileAuftragValue> values;
    private List<ProfileParameterMapper> mappers;
    private boolean isEditable;

    public ProfileAuftragValueTableModel(Set<ProfileAuftragValue> values,
            List<ProfileParameterMapper> mappers, boolean isEditable) {
        super();
        this.mappers = mappers;
        this.isEditable = isEditable;
        setValues(values);
    }

    public final void setValues(Set<ProfileAuftragValue> values) {
        this.values = Lists.newArrayList(values);
        fireTableStructureChanged();
    }

    public List<ProfileAuftragValue> getValues() {
        return ImmutableList.copyOf(values);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int cell) {
        final String columnName = getColumnName(cell);
        return !columnName.contains(ProfileParameter.TDD) && isEditable;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public String getColumnName(int column) {
        ProfileParameterMapper mapper = mappers.get(column);
        return mapper.getParameterGuiName() == null ? mapper.getParameterName() : mapper.getParameterGuiName();
    }

    @Override
    public int getColumnCount() {
        return values.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        return findProfileAuftragValue(col).
                orElse(new ProfileAuftragValue("", "")).getParameterValue();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        findProfileAuftragValue(col).
                orElse(new ProfileAuftragValue("", "")).setParameterValue(value.toString());
    }

    private Optional<ProfileAuftragValue> findProfileAuftragValue(int col) {
        ProfileParameterMapper mapper = mappers.get(col);
        return values.stream().
                filter(val -> val.getParameterName().equals(mapper.getParameterName())).
                findFirst();
    }
}