/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.gui.swing;

import static org.testng.Assert.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.AKServiceCommandChainTest;

/**
 * Test-Klasse fuer die SwingFactory
 *
 *
 */
@Test(groups = { "unit" })
public class SwingFactoryTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(AKServiceCommandChainTest.class);

    private static final String SWING_FACTORY_TEST_XML = "de/augustakom/common/gui/swing/SwingFactoryTest.xml";

    private SwingFactory sf = null;

    @BeforeMethod(groups = { "unit" })
    protected void setUp() throws Exception {
        sf = SwingFactory.getInstance(SWING_FACTORY_TEST_XML);
        LOGGER.debug("SwingFactory created using '" + SWING_FACTORY_TEST_XML + "'");
    }

    public void testCreateSwingFactory() {
    }

    /**
     * Testet die Methode SwingFactory#createTextField(String)
     */
    public void testCreateTextField() {
        AKJTextField tf = sf.createTextField("test.textfield");

        assertNotNull(tf, "TextField could not be created! TextField-Name: test.textfield");
        assertEquals(tf.getName(), "test.textfield", "Name of TextField is not valid!");
        assertEquals(tf.getText(), "simple text", "Text of TextField is not valid!");
        assertEquals(tf.getToolTipText(), "test.tooltip", "Tooltip of TextField is not valid!");
        assertEquals(tf.getActiveColor(), Color.red, "Active Color of TF is not valid!");
        assertEquals(tf.getInactiveColor(), Color.green, "InActive Color of TF is not valid!");
        assertEquals(tf.getSelectionColor(), Color.blue, "Selection-Color of TF is not valid!");
        assertEquals(tf.getSelectedTextColor(), Color.white, "SelectedText-Color of TF is not valid!");
        assertEquals(tf.getColumns(), 20, "Columns of TF are not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createPasswordField(String)
     */
    public void testCreatePasswordField() {
        AKJPasswordField pw = sf.createPasswordField("test.passwordfield");

        assertNotNull(pw, "PasswordField could not be created!");
        assertEquals(pw.getName(), "test.passwordfield", "Name of PasswordField is not valid!");
        assertEquals(pw.getToolTipText(), "passwordfield.tooltip", "Tooltip of PasswordField is not valid!");
        assertEquals(pw.getColumns(), 20, "Columns of PF are not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createFormattedTextField(String)
     */
    public void testCreateFormattedTextField() {
        AKJFormattedTextField tf = sf.createFormattedTextField("test.formatted.textfield");

        assertNotNull(tf, "FormattedTextField could not be created!");
        assertEquals(tf.getName(), "test.formatted.textfield", "Name of Formatted-TF is not valid!");
        assertEquals(tf.getToolTipText(), "formatted.tooltip", "Tooltip of Formatted-TF is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createLabel(String)
     */
    public void testCreateLabel() {
        AKJLabel label = sf.createLabel("test.label");

        assertNotNull(label, "Label could not be created! Label-Name: test.label");
        assertEquals(label.getText(), "label.text", "Text of Label is not valid!");
        assertEquals(label.getDisplayedMnemonic(), KeyEvent.VK_L, "Displayed Mnemonic of Label is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createButton(String)
     */
    public void testCreateButton() {
        AKJButton btn = sf.createButton("test.button");

        assertNotNull(btn, "Button could not be created! Button-Name: test.button");
        assertEquals(btn.getActionCommand(), "test.button", "ActionCommand of Button is not valid!");
        assertEquals(btn.getText(), "button.text", "Text of Button is not valid!");
        assertEquals(btn.getToolTipText(), "button.tooltip", "Tooltip of Button is not valid!");
        assertEquals(btn.getMnemonic(), KeyEvent.VK_T, "Mnemonic of Button is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createAction(String)
     */
    public void testCreateAction() {
        Action action = sf.createAction("test.action");

        assertNotNull(action, "Action could not be created! Action-Name: test.action");
        assertEquals(action.getValue(Action.ACTION_COMMAND_KEY), "test.action", "Name of Action is not valid!");
        assertEquals(action.getValue(Action.NAME), "action.text", "Text of Action is not valid!");
        assertEquals(action.getValue(Action.SHORT_DESCRIPTION), "action.tooltip", "Tooltip of Action is not valid!");
        assertEquals(action.getValue(Action.MNEMONIC_KEY), Integer.valueOf(KeyEvent.VK_A), "Mnemonic of Action is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createCheckBox(String)
     */
    public void testCreateCheckBox() {
        AKJCheckBox cb = sf.createCheckBox("test.checkbox");

        assertNotNull(cb, "CheckBox could not be created!");
        assertEquals(cb.getName(), "test.checkbox", "Name of CheckBox is not valid!");
        assertEquals(cb.getText(), "checkbox.text", "Text of CheckBox is not valid!");
        assertEquals(cb.getToolTipText(), "checkbox.tooltip", "Tooltip of CheckBox is not valid!");
        assertEquals(cb.getMnemonic(), KeyEvent.VK_C, "Mnemonic of CheckBox is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createRadioButton(String)
     */
    public void testCreateRadioButton() {
        AKJRadioButton rb = sf.createRadioButton("test.radiobutton");

        assertNotNull(rb, "Radio-Button could not be created! Button-Name: test.radiobutton");
        assertEquals(rb.getActionCommand(), "test.radiobutton", "ActionCommand of Radio-Button is not valid!");
        assertEquals(rb.getText(), "radiobutton.text", "Text of Radio-Button is not valid!");
        assertEquals(rb.getToolTipText(), "radiobutton.tooltip", "Tooltip of Radio-Button is not valid!");
        assertEquals(rb.getMnemonic(), KeyEvent.VK_R, "Mnemonic of Radio-Button is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createToggleButton(String)
     */
    public void testCreateToggleButton() {
        AKJToggleButton tb = sf.createToggleButton("test.togglebutton");

        assertNotNull(tb, "Toggle-Button could not be created!");
        assertEquals(tb.getActionCommand(), "test.togglebutton", "Action-Command of Toggle-Button is not valid!");
        assertEquals(tb.getText(), "togglebutton.text", "Text of Toggle-Button is not valid!");
        assertEquals(tb.getToolTipText(), "togglebutton.tooltip", "Tooltip of Toggle-Button is not valid!");
        assertEquals(tb.getMnemonic(), KeyEvent.VK_T, "Mnemonic of Toggle-Button is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createComboBox(String)
     */
    public void testCreateComboBox() {
        AKJComboBox cb = sf.createComboBox("test.combobox");

        assertNotNull(cb, "ComboBox could not be created!");
        assertEquals(cb.getName(), "test.combobox", "Name of ComboBox is not valid!");
        assertEquals(cb.getToolTipText(), "combobox.tooltip", "Tooltip of ComboBox is not valid!");
        assertEquals(cb.getItemCount(), 3, "Item-Count of ComboBox is not valid!");
        assertEquals(cb.getItemAt(0), "item1", "Text of Item 1 is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createList(String)
     */
    public void testCreateList() {
        AKJList list = sf.createList("test.list");

        assertNotNull(list, "List could not be created!");
        assertEquals(list.getName(), "test.list", "Name of List is not valid!");
        assertEquals(list.getToolTipText(), "list.tooltip", "Tooltip of List is not valid!");
        assertEquals(list.getModel().getSize(), 2, "Item-Count of List is not valid!");
        assertEquals(list.getModel().getElementAt(1), "item.2", "Text of Item 2 is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createMenuItem(String)
     */
    public void testCreateMenuItem() {
        AKJMenuItem mi = sf.createMenuItem("test.menuitem");

        assertNotNull(mi, "MenuItem could not be created!");
        assertNotNull(mi.getAction(), "Action of MenuItem is null!");
        assertEquals(mi.getName(), "test.menuitem", "Name of MenuItem is not valid!");
        assertEquals(mi.getText(), "menuitem.text", "Text of MenuItem is not valid!");
        assertEquals(mi.getToolTipText(), "menuitem.tooltip", "Tooltip of MenuItem is not valid!");
        assertEquals(mi.getMnemonic(), KeyEvent.VK_I, "Mnemonic of MenuItem is not valid!");
        assertEquals(mi.getAccelerator(), KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.ALT_MASK), "Accelerator of MenuItem is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createMenu(String, ActionListener)
     */
    public void testCreateMenu() {
        AKJMenu menu = sf.createMenu("test.menu", null);

        assertNotNull(menu, "Menu could not be created!");
        assertEquals(menu.getName(), "test.menu", "Name of Menu is not valid!");
        assertEquals(menu.getActionCommand(), "test.menu", "ActionCommand of Menu is not valid!");
        assertEquals(menu.getText(), "menu.text", "Text of Menu is not valid!");
        assertEquals(menu.getToolTipText(), "menu.tooltip", "Tooltip of Menu is not valid!");
        assertEquals(menu.getMnemonic(), KeyEvent.VK_M, "Mnemonic of Menu is not valid!");
        assertEquals(menu.getItemCount(), 7, "ItemCount of Menu is not valid!");  // inkl. Separator
    }

    /**
     * Testet die Methode SwingFactory#createMenuBar(String, ActionListener)
     */
    public void testCreateMenuBar() {
        AKJMenuBar bar = sf.createMenuBar("test.menubar", null);

        assertNotNull(bar, "MenuBar could not be created!");
        assertEquals(bar.getMenuCount(), 2, "Count of Menus is not valid!");
    }

    /**
     * Testet die Methode SwingFactory#createToolBar(String)
     */
    public void testCreateToolBar() {
        AKJToolBar tb = sf.createToolBar("test.toolbar");

        assertNotNull(tb, "ToolBar could not be created!");
        assertEquals(tb.getName(), "test.toolbar", "Name of toolbar is not valid!");
        assertEquals(tb.getComponentCount(), 3, "Component count of toolbar is not valid!");
        assertNotNull(tb.getComponentAtIndex(0), "Component '0' is null!");
        assertTrue(tb.getComponentAtIndex(1) instanceof JToolBar.Separator, "Component '1' is not of type JToolBar$Separator!");
    }

    /**
     * Testet die Methode SwingFactory#getText(String)
     */
    public void testGetText() {
        String text = sf.getText("test.text");
        assertEquals(text, "simple t\u00f6xt", "Text from SwingFactory is not valid!");
    }
}
