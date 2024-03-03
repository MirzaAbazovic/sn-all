/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.06.2012 15:37:03
 */
package de.augustakom.hurrican.gui.wholesale;

import java.awt.*;
import java.lang.reflect.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.validation.constraints.*;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;

/**
 * Generischer Dialog, welcher die vorgegebenen Bean-Properties per Reflection aus der übergebenen Bean lädt und
 * anzeigt. Die Beschriftung der Labels kann über die Resource geändert werden.
 */
public class EditDialog extends AbstractServiceOptionDialog {
    private static final Logger LOGGER = Logger.getLogger(EditDialog.class);
    private static final long serialVersionUID = 6543799248923026012L;

    private Object bean = null;
    private final List<String> beanProperties;
    private final Map<String, JComponent> uiProperties = Maps.newHashMap();

    public EditDialog(String resource, Object bean, List<String> propertiesNames) {
        super(resource, false, true);
        this.bean = bean;
        this.beanProperties = propertiesNames;
        createGUI();
        checkMandatory();
    }

    private void checkMandatory() {
        boolean enabled = true;
        for (String propertyName : beanProperties) {
            JComponent comp = uiProperties.get(propertyName);
            Method method = getPropertyMethod(propertyName);
            if (((comp instanceof AKJTextField) || (comp instanceof AKJFormattedTextField))
                    && (method.getAnnotation(NotNull.class) != null)) {
                enabled &= StringUtils.isNotEmpty(((JTextField) comp).getText());
            }
        }
        getButton(CMD_SAVE).setEnabled(enabled);
    }

    @Override
    protected final void createGUI() {
        String labelPrefix = bean.getClass().getSimpleName() + ".";
        setTitle(getSwingFactory().getText(labelPrefix + "title"));
        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkMandatory();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkMandatory();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkMandatory();
            }
        };
        int i = 0;
        for (String propertyName : beanProperties) {
            Object value = getPropertyValue(propertyName);
            AKJLabel labelComp = getSwingFactory().createLabel(labelPrefix + propertyName);
            if (labelComp.getText().isEmpty()) {
                labelComp.setText(propertyName);
            }
            JComponent valueComp;
            if (getPropertyType(propertyName).isEnum()) {
                valueComp = getSwingFactory().createComboBox(labelPrefix + propertyName,
                        new DefaultComboBoxModel(getPropertyType(propertyName).getEnumConstants()));
                if (value != null) {
                    ((AKJComboBox) valueComp).getModel().setSelectedItem(value);
                }
            }
            else if (getPropertyType(propertyName).isAssignableFrom(Boolean.class)
                    || getPropertyType(propertyName).isAssignableFrom(boolean.class)) {
                valueComp = getSwingFactory().createCheckBox(labelPrefix + propertyName, true);
                ((AKJCheckBox) valueComp).setSelected((value != null && (Boolean) value));
            }
            else {
                valueComp = getSwingFactory().createFormattedTextField(labelPrefix + propertyName);
                if (value != null) {
                    ((AKJFormattedTextField) valueComp).setText(value.toString());
                }
                ((AKJFormattedTextField) valueComp).getDocument().addDocumentListener(docListener);
            }
            uiProperties.put(propertyName, valueComp);
            panel.add(labelComp, GBCFactory.createGBC(0, 0, 0, i, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(valueComp, GBCFactory.createGBC(50, 0, 2, i, 2, 1, GridBagConstraints.HORIZONTAL, 10));
            i++;
        }
        // da der Speichernbutton gemanaged ist, muss dieser explizit als
        // executable ueberschrieben werden, da keine Rollen hinterlegt sind
        getButton(CMD_SAVE).setComponentExecutable(true);
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(panel, BorderLayout.CENTER);
    }

    private Object getPropertyValue(String name) {
        try {
            return PropertyUtils.getNestedProperty(bean, name);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return name;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        for (String propertyName : beanProperties) {
            JComponent comp = uiProperties.get(propertyName);
            Class<?> type = getPropertyType(propertyName);
            if (type.equals(String.class)) {
                setPropertyValue(propertyName, ((AKJFormattedTextField) comp).getText());
            }
            else if (type.equals(Integer.class)) {
                setPropertyValue(propertyName, ((AKJFormattedTextField) comp).getValueAsInt(null));
            }
            else if (type.equals(Long.class)) {
                setPropertyValue(propertyName, ((AKJFormattedTextField) comp).getValueAsLong(null));
            }
            else if (type.equals(Float.class)) {
                setPropertyValue(propertyName, ((AKJFormattedTextField) comp).getValueAsFloat(null));
            }
            else if (type.equals(Double.class)) {
                setPropertyValue(propertyName, ((AKJFormattedTextField) comp).getValueAsDouble(null));
            }
            else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                setPropertyValue(propertyName, ((AKJCheckBox) comp).isSelected());
            }
            else if (type.isEnum()) {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                Enum enumValue = Enum.valueOf((Class<? extends Enum>) type,
                        ((AKJComboBox) comp).getSelectedItem().toString());
                setPropertyValue(propertyName, enumValue);
            }
        }
        prepare4Close();
        setValue(bean);
    }

    private Method getPropertyMethod(String propertyName) {
        try {
            return PropertyUtils.getReadMethod(PropertyUtils.getPropertyDescriptor(bean, propertyName));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return null;
    }

    private Class<?> getPropertyType(String propertyName) {
        try {
            return PropertyUtils.getPropertyDescriptor(bean, propertyName).getPropertyType();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return null;
    }

    private void setPropertyValue(String propertyName, Object value) {
        try {
            PropertyUtils.setNestedProperty(bean, propertyName, value);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    public JComponent getUiProperty(String propertyName) {
        return uiProperties.get(propertyName);
    }

}
