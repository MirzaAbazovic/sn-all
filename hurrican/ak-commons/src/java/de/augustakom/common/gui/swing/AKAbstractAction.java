/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2004 08:53:48
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKActionFinishListener;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKParentAware;

/**
 * AK-Implementierung von <code>javax.swing.AbstractAction</code>.
 *
 *
 */
public abstract class AKAbstractAction extends AbstractAction implements AKManageableComponent, AKParentAware {
    private static final Logger LOGGER = Logger.getLogger(AKAbstractAction.class);

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;
    private List<AKActionFinishListener> finishListener = null;

    private String parentClazz = null;

    /**
     * Key, um der Action ein Objekt zu uebergeben, das von ihr verwendet werden soll/kann.
     */
    public static final String OBJECT_4_ACTION = "OBJECT_4_ACTION";

    /**
     * Key, um der Action den Ursprung des Aufrufs zu uebergeben.
     */
    public static final String ACTION_SOURCE = "ACTION_SOURCE";

    /**
     * Key, um der Action ein Objekt zu uebergeben, von dem die Action das eigentliche Modell erhaelt. <br>
     */
    public static final String MODEL_OWNER = "MODEL_OWNER";

    /**
     * Key, um der Action mitzuteilen, ob vor dem Action-Symbol (z.B. MenuItem) ein Separator dargestellt werden soll.
     * <br> Als Value wird ein Objekt vom Typ Boolean erwartet.
     */
    public static final String ADD_SEPARATOR = "ADD_SEPARATOR";

    /**
     * @see javax.swing.AbstractAction()
     */
    public AKAbstractAction() {
        super();
    }

    /**
     * @see javax.swing.AbstractAction(String)
     */
    public AKAbstractAction(String name) {
        super(name);
    }

    /**
     * @see javax.swing.AbstractAction(String, Icon)
     */
    public AKAbstractAction(String name, Icon icon) {
        super(name, icon);
    }

    /**
     * Gibt den Wert von <code>Action.ACCELERATOR_KEY<code> zurueck.
     *
     * @return Returns the acceleratorKey.
     */
    public String getAcceleratorKey() {
        Object value = getValue(Action.ACCELERATOR_KEY);
        return (value != null) ? value.toString() : null;
    }

    /**
     * Versucht, aus dem Wert von <code>acceleratorKey</code> ein KeyStroke-Objekt zu generieren. Dieses wird dann unter
     * dem Key <code>Action.ACCELERATOR_KEY</code> gespeichert.
     *
     * @param acceleratorKey The acceleratorKey to set.
     */
    public void setAcceleratorKey(String acceleratorKey) {
        KeyStroke acc = getAccelerator(acceleratorKey);
        if (acc != null) {
            putValue(Action.ACCELERATOR_KEY, acc);
        }
    }

    /**
     * Gibt den Wert von <code>Action.ACTION_COMMAND_KEY</code> zurueck.
     *
     * @return Returns the actionCommand.
     */
    public String getActionCommand() {
        Object value = getValue(Action.ACTION_COMMAND_KEY);
        return (value instanceof String) ? (String) value : null;
    }

    /**
     * Speichert den Wert von <code>actionCommand</code> unter dem Key <code>Action.ACTION_COMMAND_KEY</code>.
     *
     * @param actionCommand The actionCommand to set.
     */
    public void setActionCommand(String actionCommand) {
        putValue(Action.ACTION_COMMAND_KEY, actionCommand);
    }

    /**
     * Gibt den Wert von <code>Action.SMALL_ICON</code> zurueck.
     *
     * @return Returns the icon.
     */
    public String getIcon() {
        Object value = getValue(Action.SMALL_ICON);
        return (value != null) ? value.toString() : null;
    }

    /**
     * Versucht, aus dem Wert von <code>icon</code> ein Icon zu laden. Dieses wird dann unter dem Key
     * <code>Action.SMALL_ICON</code> gespeichert.
     *
     * @param icon The icon to set.
     */
    public void setIcon(String icon) {
        IconHelper ih = new IconHelper();
        ImageIcon imageIcon = ih.getIcon(icon);
        if (imageIcon != null) {
            putValue(Action.SMALL_ICON, imageIcon);
        }
    }

    /**
     * Gibt den Wert von <code>Action.MNEMONIC_KEY</code> zurueck.
     *
     * @return Returns the mnemonicKey.
     */
    public String getMnemonicKey() {
        Object value = getValue(Action.MNEMONIC_KEY);
        return (value != null) ? value.toString() : null;
    }

    /**
     * Versucht, anhand des Strings eine KeyCode-Konstante (z.B. VK_L) zu ermitteln. Deren Wert wird dann unter dem Key
     * <code>Action.MNEMONIC_KEY</code> gespeichert.
     *
     * @param mnemonicKey The mnemonicKey to set.
     */
    public void setMnemonicKey(String mnemonicKey) {
        int mnemonic = getMnemonic(mnemonicKey);
        if (mnemonic > -1) {
            putValue(Action.MNEMONIC_KEY, Integer.valueOf(mnemonic));
        }
    }

    /**
     * Gibt den Wert von <code>Action.NAME</code> zurueck.
     *
     * @return Returns the name.
     */
    public String getName() {
        Object value = getValue(Action.NAME);
        return (value instanceof String) ? (String) value : null;
    }

    /**
     * Speichert den Wert von <code>name</code> unter dem Key <code>Action.NAME</code>.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        putValue(Action.NAME, name);
    }

    /**
     * Gibt den Wert von <code>Action.SHORT_DESCRIPTION</code> zurueck.
     *
     * @return Returns the tooltip.
     */
    public String getTooltip() {
        Object value = getValue(Action.SHORT_DESCRIPTION);
        return (value instanceof String) ? (String) value : null;
    }

    /**
     * Speichert den Wert von <code>tooltip</code> unter dem Key <code>Action.SHORT_DESCRIPTION</code>.
     *
     * @param tooltip The tooltip to set.
     */
    public void setTooltip(String tooltip) {
        putValue(Action.SHORT_DESCRIPTION, tooltip);
    }

    /**
     * Gibt die KeyCode-Konstante anhand des Strings zurueck.
     *
     * @param value Name der KeyCode-Konstante z.B. VK_L
     * @return int-Wert des Key-Codes oder -1, falls der KeyCode nicht ermittelt werden konnte.
     */
    protected int getMnemonic(String keyCode) {
        if (StringUtils.isNotEmpty(keyCode)) {
            try {
                Class<?> keyClazz = KeyEvent.class;
                Field field = keyClazz.getField(keyCode);
                int mnemonic = field.getInt(null);
                return mnemonic;
            }
            catch (Exception e) {
                LOGGER.warn("getMnemonic() - caught exception", e);
            }
        }

        return -1;
    }

    /**
     * Gibt anhand des Parameters <code>code</code> einen KeyStroke zurueck. <br> Bsp. fuer code: <br> alt shift K <br>
     * control DELETE
     *
     * @param code String-Repraesentation des Key-Strokes
     * @return KeyStroke
     * @see javax.swing.KeyStroke#getKeyStroke(String)
     */
    protected KeyStroke getAccelerator(String code) {
        return KeyStroke.getKeyStroke(code);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
        if (getActionCommand() != null) {
            return getActionCommand();
        }
        else {
            Object name = getValue(NAME);
            return (name != null) ? name.toString() : null;
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    @Override
    public boolean isComponentExecutable() {
        return executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    @Override
    public boolean isComponentVisible() {
        return visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    @Override
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    @Override
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    @Override
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    @Override
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
    }

    /**
     * @see javax.swing.Action#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean b) {
        boolean x = (!isComponentExecutable()) ? false : b;
        super.setEnabled(x);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKParentAware#getParentClassName()
     */
    @Override
    public String getParentClassName() {
        return parentClazz;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKParentAware#setParentClassName(java.lang.String)
     */
    @Override
    public void setParentClassName(String className) {
        this.parentClazz = className;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKParentAware#setParentClass(java.lang.Class)
     */
    @Override
    public void setParentClass(Class<?> parentClazz) {
        this.parentClazz = (parentClazz != null) ? parentClazz.getName() : null;
    }

    /**
     * Informiert alle registrierten AKActionFinishListener ueber das Ende der Action.
     *
     * @param successful Flag, ob die Action erfolgreich durchgefuehrt wurde.
     */
    protected void actionFinished(boolean successful) {
        if (finishListener != null) {
            for (AKActionFinishListener fl : finishListener) {
                fl.actionFinished(successful);
            }
        }
    }

    /**
     * Fuegt der Action einen neuen FinishListener hinzu.
     *
     * @param listener
     */
    public void addFinishListener(AKActionFinishListener listener) {
        if (finishListener == null) {
            finishListener = new ArrayList<AKActionFinishListener>();
        }
        finishListener.add(listener);
    }

    /**
     * Entfernt den FinishListener von der Action.
     *
     * @param listener
     */
    public void removeFinishListener(AKActionFinishListener listener) {
        if (finishListener != null) {
            finishListener.remove(listener);
        }
    }

    /**
     * Gibt eine Liste mit allen registrierten FinishListenern zurueck (oder <code>null</code>).
     *
     * @return Liste mit Objekten des Typs <code>AKActionFinishListener</code> oder <code>null</code>.
     */
    public List<AKActionFinishListener> getFinishListener() {
        return finishListener;
    }

    public <T> T findModelByType(Class<T> clazz) {
        if (getValue(MODEL_OWNER) instanceof AKModelOwner) {
            AKModelOwner mo = (AKModelOwner) getValue(MODEL_OWNER);
            if (mo.getModel() instanceof Object[]) {
                Object[] models = (Object[]) mo.getModel();
                for (int i = 0; i < models.length; i++) {
                    if (clazz.isInstance(models[i])) {
                        @SuppressWarnings("unchecked")
                        T model = (T) models[i];
                        return model;
                    }
                }
            }
        }
        return null;
    }
}
