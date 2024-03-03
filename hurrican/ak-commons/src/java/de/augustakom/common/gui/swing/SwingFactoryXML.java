/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import de.augustakom.common.gui.iface.AKPreventKeyStrokeAwareComponent;
import de.augustakom.common.gui.utils.SwingFactoryUtils;
import de.augustakom.common.tools.lang.StringTools;

/**
 * Hilfsklasse, um Swing-Komponenten zu erstellen. <br> Die Definition der einzelnen Komponenten erfolgt ueber eine
 * XML-Datei. <br> Die Implementierung der SwingFactory greift auf die XML-Datei ueber Dom4j zu und verwendet
 * XPath-Ausdruecke, um die einzelnen Elemente zu lesen. <br> Eine Anleitung fuer XPath kann unter <a
 * href="http://www.w3schools.com/xpath/default.asp">W3-Schools</a> gefunden werden. <br> <br> Eine Instanz der
 * SwingFactoryXML wird ueber <code>SwingFactory#getInstance(String)</code> erstellt.
 *
 *
 */
public class SwingFactoryXML extends SwingFactory {

    /**
     * Definition eines Platzhalter-Strings. (Wird in den XPath-Anweisungen verwendet.)
     */
    protected static final String PLACEHOLDER = "%s";
    /**
     * XPath, um auf die Definition eines bestimmten TextFields zuzugreifen.
     */
    protected static final String XPATH_TEXTFIELD = "//TextField[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten PasswordFields zuzugreifen.
     */
    protected static final String XPATH_PASSWORDFIELD = "//PasswordField[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten FormattedTextFields zuzugreifen.
     */
    protected static final String XPATH_FORMATTED_TEXTFIELD = "//FormattedTextField[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten DateComponent zuzugreifen.
     */
    protected static final String XPATH_DATE_COMPONENT = "//DateComponent[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten ReferenceFields zuzugreifen.
     */
    protected static final String XPATH_DATE_REF_FIELD = "//ReferenceField[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten JSpinner-Komponente zuzugreifen.
     */
    protected static final String XPATH_SPINNER = "//Spinner[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten TextArea bzw. TextPane zuzugreifen.
     */
    protected static final String XPATH_TEXTAREA = "//TextArea[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten Labels zuzugreifen.
     */
    protected static final String XPATH_LABEL = "//Label[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten Buttons zuzugreifen.
     */
    protected static final String XPATH_BUTTON = "//Button[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten RadioButtons zuzugreifen.
     */
    protected static final String XPATH_RADIOBUTTON = "//RadioButton[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten ToggleButtons zuzugreifen.
     */
    protected static final String XPATH_TOGGLEBUTTON = "//ToggleButton[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten Action zuzugreifen.
     */
    protected static final String XPATH_ACTION = "//Action[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten CheckBox zuzugreifen.
     */
    protected static final String XPATH_CHECKBOX = "//CheckBox[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten ComboBox zuzugreifen.
     */
    protected static final String XPATH_COMBOBOX = "//ComboBox[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten List zuzugreifen.
     */
    protected static final String XPATH_LIST = "//List[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten Texts zuzugreifen.
     */
    protected static final String XPATH_TEXT = "//Text[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten Icons zuzugreifen.
     */
    protected static final String XPATH_ICON = "//Icon[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten ToolBar zuzugreifen.
     */
    protected static final String XPATH_TOOLBAR = "//ToolBar[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten MenuBar zuzugreifen.
     */
    protected static final String XPATH_MENUBAR = "//MenuBar[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten Menus zuzugreifen
     */
    protected static final String XPATH_MENU = "//Menu[@name='%s']";
    /**
     * XPath, um auf die Definition eines bestimmten MenuItems zuzugreifen.
     */
    protected static final String XPATH_MENUITEM = "//MenuItem[@name='%s']";
    /**
     * XPath, um auf die Definition einer bestimmten Farbe zuzugreifen.
     */
    protected static final String XPATH_COLOR = "//Color[@name='%s']";
    /**
     * XPath, um auf ein 'text'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_TEXT = "/text";
    /**
     * XPath, um auf ein 'tooltip'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_TOOLTIP = "/tooltip";
    /**
     * XPath, um auf ein 'style'-Element zuzugreifen (Wert z.B. 'bold', 'italic'.
     */
    protected static final String XPATH_ELEMENT_STYLE = "/style";
    /**
     * XPath, um auf ein 'tooltip.selected'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_TOOLTIP_SELECTED = "/tooltip.selected";
    /**
     * XPath, um auf ein 'icon'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ICON = "/icon";
    /**
     * XPath, um auf ein 'mnemonic'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_MNEMONIC = "/mnemonic";
    /**
     * XPath, um auf ein 'accelerator'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ACCELERATOR = "/accelerator";
    /**
     * XPath, um auf ein 'actions'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ACTIONS = "/actions";
    /**
     * XPath, um auf ein 'action'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ACTION = "/action";
    /**
     * XPath, um auf ein 'class'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_CLASS = "/class";
    /**
     * XPath, um auf ein 'items'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ITEMS = "/items";
    /**
     * XPath, um auf ein 'item'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ITEM = "/item";
    /**
     * XPath, um auf ein 'columns'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_COLUMNS = "/columns";
    /**
     * XPath, um auf ein 'rows'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ROWS = "/rows";
    /**
     * XPath, um auf ein 'maxchars'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_MAXCHARS = "/maxchars";
    /**
     * XPath, um auf ein 'format'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_FORMAT = "/format";
    /**
     * XPath, um auf ein 'active'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_COLOR_ACTIVE = "/active";
    /**
     * XPath, um auf ein 'inactive'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_COLOR_INACTIVE = "/inactive";
    /**
     * XPath, um auf ein 'selection'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_COLOR_SELECTION = "/selection";
    /**
     * XPath, um auf ein 'selectiontext'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_COLOR_SELECTION_TEXT = "/selectedtext";
    /**
     * XPath, um auf ein 'forground'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_COLOR_FOREGROUND = "/foreground";
    /**
     * XPath, um auf ein 'red'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_RED = "/red";
    /**
     * XPath, um auf ein 'green'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_GREEN = "/green";
    /**
     * XPath, um auf ein 'blue'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_BLUE = "/blue";
    /**
     * XPath, um auf ein 'id.property'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_ID_PROPERTY = "/id.property";
    /**
     * XPath, um auf ein 'name.property'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_NAME_PROPERTY = "/name.property";
    /**
     * XPath, um auf ein 'tooltip.property'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_TOOLTIP_PROPERTY = "/tooltip.property";
    /**
     * XPath, um auf ein 'reference.class'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_REFERENCE_CLASS = "/reference.class";
    /**
     * XPath, um auf ein 'selection.dialog.class'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_SELECTION_DIALOG_CLASS = "/selection.dialog.class";
    /**
     * XPath, um auf eine Liste mit den zu unterdrueckenden Keys zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_PREVENTED_KEYS = "/prevented-keys";
    /**
     * XPath, um auf ein 'key-stroke'-Element zuzugreifen
     */
    protected static final String XPATH_ELEMENT_KEY_STROKE = "/key-stroke";
    /**
     * XPath, um auf ein 'wordwrap'-Element zuzugreifen
     */
    protected static final String XPATH_TEXTAREA_ELEMENT_WORDWRAP = "/wordwrap";
    /**
     * XPath, um auf ein 'fontstyle'-Element zuzugreifen
     */
    protected static final String XPATH_TEXTAREA_ELEMENT_FONTSTYLE = "/fontstyle";
    /**
     * XPath, um auf ein 'feature'-Element zuzugreifen.
     */
    protected static final String XPATH_ELEMENT_FEATURE = "/feature";
    /**
     * XPath, um auf das 'type'-Attribut eines 'format'-Elements zuzugreifen.
     */
    protected static final String XPATH_ATTRIBUTE_FORMAT_TYPE = "/format/@type";
    /**
     * Definition des 'item'-Attributs 'text'.
     */
    protected static final String ATTRIBUTE_ITEM_TEXT = "text";
    /**
     * Definition des 'item'-Attributs 'value'.
     */
    protected static final String ATTRIBUTE_ITEM_VALUE = "value";
    /**
     * Definition des 'item'-Attributs 'class'.
     */
    protected static final String ATTRIBUTE_ITEM_CLASS = "class";
    /**
     * Name des XML-Elements fuer eine Action.
     */
    protected static final String ELEMENT_ACTION = "action";
    /**
     * Name des XML-Elements fuer ein Menuitem.
     */
    protected static final String ELEMENT_MENUITEM = "item";
    /**
     * Name des XML-Elements fuer ein Sub-Menu.
     */
    protected static final String ELEMENT_SUBMENU = "submenu";
    /**
     * Name des XML-Elements fuer einen Separator.
     */
    protected static final String ELEMENT_SEPARATOR = "separator";
    /**
     * Konstante fuer den Formattyp 'date'
     */
    protected static final String FORMAT_TYPE_DATE = "date";
    /**
     * Konstante fuer den Formattyp 'number'
     */
    protected static final String FORMAT_TYPE_NUMBER = "number";
    /**
     * Konstante fuer den Formattyp 'mask'
     */
    protected static final String FORMAT_TYPE_MASK = "mask";
    /**
     * Konstante fuer den Formattyp 'regexp'
     */
    protected static final String FORMAT_TYPE_REGEXP = "regexp";
    private static final Logger LOGGER = Logger.getLogger(SwingFactoryXML.class);
    /* Dom4j-Dokument */
    private Document document = null;

    /* Map, um Action-Objekte zu speichern. */
    private Map<String, Action> actionInstances = null;

    private IconHelper iconHelper = null;

    /**
     * Erzeugt eine neue XMLSwingFactory.
     *
     * @param xmlResource
     */
    public SwingFactoryXML(String xmlResource) {
        super();
        init(xmlResource);
    }

    /**
     * Initialisiert die XMLSwingFactory.
     *
     * @param xmlResource
     */
    private void init(String xmlResource) {
        actionInstances = new HashMap<>();
        final SAXReader saxReader = new SAXReader();

        final URL url = this.getClass().getClassLoader().getResource(xmlResource);
        if (url != null) {
            try {
                document = saxReader.read(url);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createTextField(String)
     */
    @Override
    public AKJTextField createTextField(String name) {
        AKJTextField tf = new AKJTextField();
        tf.setName(name);
        tf.getAccessibleContext().setAccessibleName(name);
        tf.setActionCommand(name);
        tf.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_TEXTFIELD, PLACEHOLDER, name);

        String text = getValue(xpathBase + XPATH_ELEMENT_TEXT);
        if (StringUtils.isNotBlank(text)) {
            tf.setText(text);
        }
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            tf.setToolTipText(tooltip);
        }
        tf.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        tf.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));
        tf.setSelectionColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION), DEFAULT_SELECTION_COLOR));
        tf.setSelectedTextColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION_TEXT),
                DEFAULT_SELECTION_TEXT_COLOR));
        int columns = getIntValue(xpathBase + XPATH_ELEMENT_COLUMNS);
        if (columns > 0) {
            tf.setColumns(columns);
        }
        tf.setMaxChars(getIntValue(xpathBase + XPATH_ELEMENT_MAXCHARS));

        tf.setComponentPopupMenu(createCCPPopupMenu(tf));
        setPreventedKeys(tf, xpathBase);
        return tf;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createPasswordField(String)
     */
    @Override
    public AKJPasswordField createPasswordField(String name) {
        AKJPasswordField pwField = new AKJPasswordField();
        pwField.setName(name);
        pwField.getAccessibleContext().setAccessibleName(name);
        pwField.setActionCommand(name);
        pwField.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_PASSWORDFIELD, PLACEHOLDER, name);

        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            pwField.setToolTipText(tooltip);
        }
        pwField.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        pwField.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE),
                DEFAULT_BG_COLOR_INACTIVE));
        pwField.setSelectionColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION),
                DEFAULT_SELECTION_COLOR));
        pwField.setSelectedTextColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION_TEXT),
                DEFAULT_SELECTION_TEXT_COLOR));
        int columns = getIntValue(xpathBase + XPATH_ELEMENT_COLUMNS);
        if (columns > 0) {
            pwField.setColumns(columns);
        }
        setPreventedKeys(pwField, xpathBase);

        return pwField;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createFormattedTextField(String)
     */
    @Override
    public AKJFormattedTextField createFormattedTextField(String name) {
        String xpathBase = StringUtils.replace(XPATH_FORMATTED_TEXTFIELD, PLACEHOLDER, name);

        boolean alignmentRight = false;
        Format format = null;
        JFormattedTextField.AbstractFormatter maskFormatter = null;
        String formatType = getValue(xpathBase + XPATH_ATTRIBUTE_FORMAT_TYPE);
        if (StringUtils.equalsIgnoreCase(formatType, FORMAT_TYPE_DATE)) {
            format = new SimpleDateFormat(getValue(xpathBase + XPATH_ELEMENT_FORMAT));
        }
        else if (StringUtils.equalsIgnoreCase(formatType, FORMAT_TYPE_NUMBER)) {
            format = new DecimalFormat(getValue(xpathBase + XPATH_ELEMENT_FORMAT));
            alignmentRight = true;
        }
        else if (StringUtils.equalsIgnoreCase(formatType, FORMAT_TYPE_MASK)) {
            try {
                maskFormatter = new MaskFormatter(getValue(xpathBase + XPATH_ELEMENT_FORMAT));
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                maskFormatter = null;
            }
        }
        else if (StringUtils.equalsIgnoreCase(formatType, FORMAT_TYPE_REGEXP)) {
            try {
                maskFormatter = new RegexpFormatter(getValue(xpathBase + XPATH_ELEMENT_FORMAT));
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                maskFormatter = null;
            }
        }

        AKJFormattedTextField tf;
        if (format != null) {
            tf = new AKJFormattedTextField(format);
        }
        else if (maskFormatter != null) {
            tf = new AKJFormattedTextField(maskFormatter);
        }
        else {
            tf = new AKJFormattedTextField();
        }

        if (alignmentRight) {
            tf.setHorizontalAlignment(AKJFormattedTextField.RIGHT);
        }

        tf.setName(name);
        tf.getAccessibleContext().setAccessibleName(name);
        tf.setActionCommand(name);
        tf.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            tf.setToolTipText(tooltip);
        }
        tf.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        tf.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));
        tf.setSelectionColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION), DEFAULT_SELECTION_COLOR));
        tf.setSelectedTextColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION_TEXT),
                DEFAULT_SELECTION_TEXT_COLOR));
        tf.setMaxChars(getIntValue(xpathBase + XPATH_ELEMENT_MAXCHARS));
        int columns = getIntValue(xpathBase + XPATH_ELEMENT_COLUMNS);
        if (columns > 0) {
            tf.setColumns(columns);
        }

        if ((tf.getText() != null) && !tf.getText().trim().equals("")) {
            try {
                tf.commitEdit();
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                tf.setText("");
            }
        }

        tf.addMouseListener(getAdminMouseListener());
        tf.setComponentPopupMenu(createCCPPopupMenu(tf));
        setPreventedKeys(tf, xpathBase);
        return tf;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createDateComponent(java.lang.String)
     */
    @Override
    public AKJDateComponent createDateComponent(String name) {
        String xpathBase = StringUtils.replace(XPATH_DATE_COMPONENT, PLACEHOLDER, name);

        AKJDateComponent dc = new AKJDateComponent();
        dc.setName(name);
        dc.getAccessibleContext().setAccessibleName(name);
        dc.addMouseListener(getAdminMouseListener());

        SimpleDateFormat format = null;
        String formatType = getValue(xpathBase + XPATH_ATTRIBUTE_FORMAT_TYPE);
        if (StringUtils.equalsIgnoreCase(formatType, FORMAT_TYPE_DATE)) {
            format = new SimpleDateFormat(getValue(xpathBase + XPATH_ELEMENT_FORMAT));
        }
        if (format != null) {
            dc.setDateFormat(format);
        }

        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            dc.setToolTipText(tooltip);
        }

        String btnText = getValue(xpathBase + XPATH_ELEMENT_TEXT + ".button");
        if (StringUtils.isNotEmpty(btnText)) {
            dc.setButtonText(btnText);
        }
        String btnTooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP + ".button");
        if (StringUtils.isNotEmpty(btnTooltip)) {
            dc.setButtonToolTipText(btnTooltip);
        }
        String btnIcon = getValue(xpathBase + XPATH_ELEMENT_ICON + ".button");
        if (StringUtils.isNotEmpty(btnIcon)) {
            dc.setButtonIcon(btnIcon);
        }

        String dlgIcon = getValue(xpathBase + XPATH_ELEMENT_ICON + ".dialog");
        if (StringUtils.isNotEmpty(btnIcon)) {
            dc.setDatePickerIcon(dlgIcon);
        }
        String dlgTitle = getValue(xpathBase + XPATH_ELEMENT_TEXT + ".dialog");
        if (StringUtils.isNotEmpty(dlgTitle)) {
            dc.setDatePickerTitle(dlgTitle);
        }

        dc.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        dc.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));
        dc.setSelectionColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION), DEFAULT_SELECTION_COLOR));
        dc.setSelectedTextColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION_TEXT),
                DEFAULT_SELECTION_TEXT_COLOR));
        int columns = getIntValue(xpathBase + XPATH_ELEMENT_COLUMNS);
        if (columns > 0) {
            dc.setColumns(columns);
        }

        return dc;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createReferenceField(java.lang.String)
     */
    @Override
    public AKReferenceField createReferenceField(String name) {
        String xpathBase = StringUtils.replace(XPATH_DATE_REF_FIELD, PLACEHOLDER, name);

        AKReferenceField rf = new AKReferenceField();
        rf.setName(name);
        rf.getAccessibleContext().setAccessibleName(name);
        rf.addMouseListener(getAdminMouseListener());

        rf.setReferenceIdProperty(getValue(xpathBase + XPATH_ELEMENT_ID_PROPERTY));
        rf.setReferenceShowProperty(getValue(xpathBase + XPATH_ELEMENT_NAME_PROPERTY));
        rf.setReferenceTooltipProperty(getValue(xpathBase + XPATH_ELEMENT_TOOLTIP_PROPERTY));
        try {
            String clazz = getValue(xpathBase + XPATH_ELEMENT_REFERENCE_CLASS);
            if (StringUtils.isNotEmpty(clazz)) {
                rf.setReferenceModel(Class.forName(clazz));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        String selectionDlg = getValue(xpathBase + XPATH_ELEMENT_SELECTION_DIALOG_CLASS);
        if (StringUtils.isNotEmpty(selectionDlg)) {
            rf.setSelectionDialogClass(selectionDlg);
        }

        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            rf.setToolTipText(tooltip);
        }

        String btnText = getValue(xpathBase + XPATH_ELEMENT_TEXT + ".button");
        if (StringUtils.isNotEmpty(btnText)) {
            rf.setButtonText(btnText);
        }
        String btnTooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP + ".button");
        if (StringUtils.isNotEmpty(btnTooltip)) {
            rf.setButtonToolTipText(btnTooltip);
        }
        String btnIcon = getValue(xpathBase + XPATH_ELEMENT_ICON + ".button");
        if (StringUtils.isNotEmpty(btnIcon)) {
            rf.setButtonIcon(btnIcon);
        }

        String dlgIcon = getValue(xpathBase + XPATH_ELEMENT_ICON + ".dialog");
        if (StringUtils.isNotEmpty(dlgIcon)) {
            rf.setDialogIcon(dlgIcon);
        }

        int columns = getIntValue(xpathBase + XPATH_ELEMENT_COLUMNS);
        if (columns > 0) {
            rf.setColumns(columns);
        }

        return rf;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createSpinner(java.lang.String, javax.swing.SpinnerModel)
     */
    @Override
    public AKJSpinner createSpinner(String name, SpinnerModel spinnerModel) {
        AKJSpinner spinner = new AKJSpinner(spinnerModel);
        spinner.setName(name);
        spinner.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_TEXTAREA, PLACEHOLDER, name);
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            spinner.setToolTipText(tooltip);
        }

        spinner.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        spinner.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE),
                DEFAULT_BG_COLOR_INACTIVE));

        return spinner;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createTextArea(String)
     */
    @Override
    public AKJTextArea createTextArea(String name) {
        AKJTextArea ta = new AKJTextArea();
        ta.setName(name);
        ta.addMouseListener(getAdminMouseListener());
        ta.setLineWrap(true);

        String xpathBase = StringUtils.replace(XPATH_TEXTAREA, PLACEHOLDER, name);

        ta.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            ta.setToolTipText(tooltip);
        }
        ta.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        ta.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));
        ta.setSelectionColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION), DEFAULT_SELECTION_COLOR));
        ta.setSelectedTextColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION_TEXT),
                DEFAULT_SELECTION_TEXT_COLOR));

        ta.setMaxChars(getIntValue(xpathBase + XPATH_ELEMENT_MAXCHARS));
        int columns = getIntValue(xpathBase + XPATH_ELEMENT_COLUMNS);
        if (columns > 0) {
            ta.setColumns(columns);
        }

        int rows = getIntValue(xpathBase + XPATH_ELEMENT_ROWS);
        if (rows > 0) {
            ta.setRows(rows);
        }

        String wordwrap = getValue(xpathBase + XPATH_TEXTAREA_ELEMENT_WORDWRAP);
        if (StringUtils.equalsIgnoreCase(wordwrap, "true")) {
            ta.setWrapStyleWord(true);
        }

        String fontstyle = getValue(xpathBase + XPATH_TEXTAREA_ELEMENT_FONTSTYLE);
        if (StringUtils.equalsIgnoreCase(fontstyle, "bold")) {
            ta.setFontStyle(com.lowagie.text.Font.BOLD);
        }
        else if (StringUtils.equalsIgnoreCase(fontstyle, "italic")) {
            ta.setFontStyle(com.lowagie.text.Font.ITALIC);
        }

        ta.setComponentPopupMenu(createCCPPopupMenu(ta));
        setPreventedKeys(ta, xpathBase);
        return ta;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createTextPane(String)
     */
    @Override
    public AKJTextPane createTextPane(String name) {
        AKJTextPane tp = new AKJTextPane();
        tp.setName(name);
        tp.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_TEXTAREA, PLACEHOLDER, name);

        tp.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            tp.setToolTipText(tooltip);
        }
        tp.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        tp.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));
        tp.setSelectionColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION), DEFAULT_SELECTION_COLOR));
        tp.setSelectedTextColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION_TEXT),
                DEFAULT_SELECTION_TEXT_COLOR));
        tp.setMaxChars(getIntValue(xpathBase + XPATH_ELEMENT_MAXCHARS));

        tp.setComponentPopupMenu(createCCPPopupMenu(tp));
        setPreventedKeys(tp, xpathBase);
        return tp;
    }

    @Override
    public String getLabelText(String name) {
        String xpathBase = StringUtils.replace(XPATH_LABEL, PLACEHOLDER, name);
        return getValue(xpathBase + XPATH_ELEMENT_TEXT);
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createLabel(String, int)
     */
    @Override
    public AKJLabel createLabel(String name, int horizontalAlignment) {
        AKJLabel lbl = new AKJLabel();
        lbl.setName(name);
        lbl.getAccessibleContext().setAccessibleName(name);
        lbl.setHorizontalAlignment(horizontalAlignment);
        lbl.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_LABEL, PLACEHOLDER, name);
        lbl.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            lbl.setToolTipText(tooltip);
        }
        lbl.setForeground(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_FOREGROUND), lbl.getForeground()));

        String style = getValue(xpathBase + XPATH_ELEMENT_STYLE);
        if (StringUtils.isNotBlank(style)) {
            int fontStyle = Font.PLAIN;
            if (StringUtils.equalsIgnoreCase(style, STYLE_BOLD)) {
                fontStyle = Font.BOLD;
            }
            else if (StringUtils.equalsIgnoreCase(style, STYLE_ITALIC)) {
                fontStyle = Font.ITALIC;
            }

            Font font = new Font(lbl.getFont().getName(), fontStyle, lbl.getFont().getSize());
            lbl.setFont(font);
        }

        Icon icon = getIcon(getValue(xpathBase + XPATH_ELEMENT_ICON));
        if (icon != null) {
            lbl.setIcon(icon);
        }

        int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
        if (mnemonic > -1) {
            lbl.setDisplayedMnemonic(mnemonic);
        }

        return lbl;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createLabel(String)
     */
    @Override
    public AKJLabel createLabel(String name) {
        return createLabel(name, AKJLabel.LEFT);
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createLabel(java.lang.String, java.lang.Object[])
     */
    @Override
    public AKJLabel createLabel(String name, Object[] params) {
        AKJLabel lbl = createLabel(name, AKJLabel.LEFT);
        String text = StringTools.formatString(lbl.getText(), params, null);
        lbl.setText(text);
        return lbl;
    }

    /**
     * checks if the specific feature in the {@code <feature> ... </feature>} tag is online.
     *
     * @param xpathBase name of the base xpath-Element
     * @return {@code true} - if a the feature is enabled or no feature is mentioned in the xml-base-element. <br/>
     * {@code false} - if the mentioned feature in the xml-base-element is disabled.
     */
    private boolean checkIfFeatureAvailable(String xpathBase) {
        final String feature = getValue(xpathBase + XPATH_ELEMENT_FEATURE);
        return StringUtils.isEmpty(feature) || availableFeatures != null && availableFeatures.contains(feature);
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createAction(String)
     */
    @Override
    public Action createAction(String name) {
        Action saved = actionInstances.get(name);
        if (saved != null) {
            return saved;
        }

        String xpathBase = StringUtils.replace(XPATH_ACTION, PLACEHOLDER, name);
        if (checkIfFeatureAvailable(xpathBase)) {
            String clazz = getValue(xpathBase + XPATH_ELEMENT_CLASS);
            try {
                Class<?> actionClazz = Class.forName(clazz);
                Object obj = actionClazz.newInstance();
                if (obj instanceof Action) {
                    Action action = (Action) obj;

                    action.putValue(Action.ACTION_COMMAND_KEY, name);
                    action.putValue(Action.NAME, getValue(xpathBase + XPATH_ELEMENT_TEXT));
                    action.putValue(Action.SHORT_DESCRIPTION, getValue(xpathBase + XPATH_ELEMENT_TOOLTIP));

                    Icon icon = getIcon(getValue(xpathBase + XPATH_ELEMENT_ICON));
                    if (icon != null) {
                        action.putValue(Action.SMALL_ICON, icon);
                    }

                    int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
                    if (mnemonic > -1) {
                        action.putValue(Action.MNEMONIC_KEY, mnemonic);
                    }

                    KeyStroke keyStroke = getAccelerator(getValue(xpathBase + XPATH_ELEMENT_ACCELERATOR));
                    if (keyStroke != null) {
                        action.putValue(Action.ACCELERATOR_KEY, keyStroke);
                    }

                    actionInstances.put(name, action);
                    return action;
                }
            }
            catch (Exception e) {
                LOGGER.warn("Class not found: " + clazz);
                LOGGER.warn("Action name for class: " + name);
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createButton(String)
     */
    @Override
    public AKJButton createButton(String name) {
        AKJButton btn = new AKJButton();
        btn.setName(name);
        btn.getAccessibleContext().setAccessibleName(name);
        btn.setActionCommand(name);
        btn.setHorizontalAlignment(AKJButton.CENTER);
        btn.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_BUTTON, PLACEHOLDER, name);
        btn.setText(SwingFactoryUtils.convertMultilineTextToHtml(getValue(xpathBase + XPATH_ELEMENT_TEXT)));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            btn.setToolTipText(tooltip);
        }

        Icon icon = getIcon(getValue(xpathBase + XPATH_ELEMENT_ICON));
        if (icon != null) {
            btn.setIcon(icon);
        }

        int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
        if (mnemonic > -1) {
            btn.setMnemonic(mnemonic);
        }

        if (btn.getText().equals("")) {
            btn.setMargin(new Insets(1, 1, 1, 1));
        }

        return btn;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createButton(String, ActionListener)
     */
    @Override
    public AKJButton createButton(String name, ActionListener listener) {
        AKJButton btn = createButton(name);
        if (listener != null) {
            btn.addActionListener(listener);
        }

        return btn;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createRadioButton(String)
     */
    @Override
    public AKJRadioButton createRadioButton(String name) {
        AKJRadioButton btn = new AKJRadioButton();
        btn.setName(name);
        btn.getAccessibleContext().setAccessibleName(name);
        btn.setActionCommand(name);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setSelected(false);
        btn.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_RADIOBUTTON, PLACEHOLDER, name);
        btn.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            btn.setToolTipText(tooltip);
        }

        Icon icon = getIcon(getValue(xpathBase + XPATH_ELEMENT_ICON));
        if (icon != null) {
            btn.setIcon(icon);
        }

        int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
        if (mnemonic > -1) {
            btn.setMnemonic(mnemonic);
        }

        if (btn.getText().equals("")) {
            btn.setMargin(new Insets(1, 1, 1, 1));
        }

        return btn;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createRadioButton(String, ActionListener, boolean)
     */
    @Override
    public AKJRadioButton createRadioButton(String name, ActionListener listener, boolean selected) {
        AKJRadioButton btn = createRadioButton(name);
        btn.setSelected(selected);
        if (listener != null) {
            btn.addActionListener(listener);
        }
        return btn;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createToggleButton(String)
     */
    @Override
    public AKJToggleButton createToggleButton(String name) {
        AKJToggleButton btn = new AKJToggleButton();
        btn.setName(name);
        btn.getAccessibleContext().setAccessibleName(name);
        btn.setActionCommand(name);
        btn.setHorizontalAlignment(AKJButton.CENTER);
        btn.setSelected(false);
        btn.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_TOGGLEBUTTON, PLACEHOLDER, name);
        btn.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            btn.setToolTipText(tooltip);
        }

        String tooltipSelected = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP_SELECTED);
        if (StringUtils.isNotEmpty(tooltipSelected)) {
            btn.setToolTipTextSelected(tooltipSelected);
        }

        Icon icon = getIcon(getValue(xpathBase + XPATH_ELEMENT_ICON));
        if (icon != null) {
            btn.setIcon(icon);
        }

        int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
        if (mnemonic > -1) {
            btn.setMnemonic(mnemonic);
        }

        if (btn.getText().equals("")) {
            btn.setMargin(new Insets(1, 1, 1, 1));
        }

        return btn;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createToggleButton(String, ActionListener, boolean)
     */
    @Override
    public AKJToggleButton createToggleButton(String name, ActionListener listener, boolean selected) {
        AKJToggleButton btn = createToggleButton(name);
        btn.setSelected(selected);
        if (listener != null) {
            btn.addActionListener(listener);
        }
        return btn;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createCheckBox(String)
     */
    @Override
    public AKJCheckBox createCheckBox(String name) {
        AKJCheckBox cb = new AKJCheckBox();
        cb.setName(name);
        cb.getAccessibleContext().setAccessibleName(name);
        cb.setActionCommand(name);
        cb.setSelected(false);
        cb.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_CHECKBOX, PLACEHOLDER, name);
        cb.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            cb.setToolTipText(tooltip);
        }

        int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
        if (mnemonic > -1) {
            cb.setMnemonic(mnemonic);
        }

        return cb;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createCheckBox(String, ActionListener, boolean)
     */
    @Override
    public AKJCheckBox createCheckBox(String name, ActionListener listener, boolean selected) {
        AKJCheckBox cb = createCheckBox(name);
        cb.setSelected(selected);
        if (listener != null) {
            cb.addActionListener(listener);
        }
        return cb;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createComboBox(String)
     */
    @Override
    public AKJComboBox createComboBox(String name) {
        AKJComboBox cb = new AKJComboBox();
        cb.setName(name);
        cb.getAccessibleContext().setAccessibleName(name);
        cb.setActionCommand(name);
        cb.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_COMBOBOX, PLACEHOLDER, name);
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            cb.setToolTipText(tooltip);
        }
        cb.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        cb.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));

        if (document != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(xpathBase);
            sb.append(XPATH_ELEMENT_ITEMS);
            sb.append(XPATH_ELEMENT_ITEM);

            XPath xpathSelector = DocumentHelper.createXPath(sb.toString());
            List<?> items = xpathSelector.selectNodes(document);
            for (Object name2 : items) {
                Element element = (Element) name2;
                String item = element.getText();
                if (StringUtils.isNotEmpty(item)) {
                    cb.addItem(item);
                }
                else {
                    // Attribute 'text', 'value' und 'class' auswerten
                    String text = element.attributeValue(ATTRIBUTE_ITEM_TEXT);
                    String value = element.attributeValue(ATTRIBUTE_ITEM_VALUE);
                    String clazz = element.attributeValue(ATTRIBUTE_ITEM_CLASS);

                    if (StringUtils.isNotBlank(clazz)) {
                        try {
                            cb.addItem(text, ConvertUtils.convert(value, Class.forName(clazz)));
                        }
                        catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                    else {
                        cb.addItem(text, null);
                    }
                }
            }
        }

        return cb;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createComboBox(String, ComboBoxModel)
     */
    @Override
    public AKJComboBox createComboBox(String name, ComboBoxModel model) {
        AKJComboBox cb = new AKJComboBox(model);
        cb.setName(name);
        cb.getAccessibleContext().setAccessibleName(name);
        cb.setActionCommand(name);
        cb.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_COMBOBOX, PLACEHOLDER, name);
        cb.setToolTipText(getValue(xpathBase + XPATH_ELEMENT_TOOLTIP));
        cb.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        cb.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));

        return cb;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createList(String)
     */
    @Override
    public AKJList createList(String name) {
        AKJList list = new AKJList();
        list.setName(name);
        list.getAccessibleContext().setAccessibleName(name);
        list.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_LIST, PLACEHOLDER, name);
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            list.setToolTipText(tooltip);
        }
        list.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        list.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));

        if (document != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(xpathBase);
            sb.append(XPATH_ELEMENT_ITEMS);
            sb.append(XPATH_ELEMENT_ITEM);

            XPath xpathSelector = DocumentHelper.createXPath(sb.toString());
            List<?> items = xpathSelector.selectNodes(document);
            DefaultListModel model = new DefaultListModel();
            for (Object name2 : items) {
                Element element = (Element) name2;
                String item = element.getText();
                if (StringUtils.isNotEmpty(item)) {
                    model.addElement(item);
                }
            }

            list.setModel(model);
        }

        return list;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createList(String, ListModel)
     */
    @Override
    public AKJList createList(String name, ListModel model) {
        AKJList list = new AKJList(model);
        list.setName(name);
        list.getAccessibleContext().setAccessibleName(name);
        list.addMouseListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_LIST, PLACEHOLDER, name);
        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            list.setToolTipText(tooltip);
        }
        list.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        list.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));

        return list;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createToolBar(String)
     */
    @Override
    public AKJToolBar createToolBar(String name) {
        AKJToolBar toolbar = new AKJToolBar();
        toolbar.setName(name);
        toolbar.getAccessibleContext().setAccessibleName(name);

        if (document != null) {
            String xpathBase = StringUtils.replace(XPATH_TOOLBAR, PLACEHOLDER, name);

            // Actions erzeugen und der ToolBar zuordnen
            XPath xpathSubMenus = DocumentHelper.createXPath(xpathBase + XPATH_ELEMENT_ACTIONS);
            List<?> menus = xpathSubMenus.selectNodes(document);
            for (Object name2 : menus) {
                Element element = (Element) name2;
                Iterator<?> elIt = element.elementIterator();
                while (elIt.hasNext()) {
                    Element next = (Element) elIt.next();
                    if (StringUtils.equals(next.getName(), ELEMENT_ACTION)) {
                        Action action = createAction(next.getText());
                        if (action != null) {
                            toolbar.add(action);
                        }
                    }
                    else if (StringUtils.equals(next.getName(), ELEMENT_SEPARATOR)) {
                        if (checkIfFeatureAvailable(next.getUniquePath())) {
                            toolbar.addSeparator();
                        }
                    }
                    else {
                        LOGGER.warn("Unknown element in toolbar definition: " + next.getName());
                    }
                }
            }
        }

        return toolbar;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createMenuBar(String, ActionListener)
     */
    @Override
    public AKJMenuBar createMenuBar(String name, ActionListener listener) {
        AKJMenuBar menuBar = new AKJMenuBar();
        menuBar.setName(name);
        menuBar.getAccessibleContext().setAccessibleName(name);

        if (document != null) {
            String xpathBase = StringUtils.replace(XPATH_MENUBAR, PLACEHOLDER, name);

            // SubMenus erzeugen und der MenuBar zuordnen
            StringBuilder sbMenu = new StringBuilder();
            sbMenu.append(xpathBase);
            sbMenu.append(XPATH_ELEMENT_ITEMS);
            sbMenu.append(XPATH_ELEMENT_ITEM);

            XPath xpathSubMenus = DocumentHelper.createXPath(sbMenu.toString());
            List<?> menus = xpathSubMenus.selectNodes(document);
            for (Object name2 : menus) {
                Element element = (Element) name2;
                String menuName = element.getText();
                AKJMenu subMenu = createMenu(menuName, listener);
                if (subMenu != null) {
                    menuBar.add(subMenu);
                }
            }
        }

        return menuBar;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createMenu(String, ActionListener)
     */
    @Override
    public AKJMenu createMenu(String name, ActionListener listener) {
        AKJMenu menu = new AKJMenu();
        menu.setName(name);
        menu.getAccessibleContext().setAccessibleName(name);
        menu.setActionCommand(name);
        menu.addMouseListener(getAdminMouseListener());
        menu.addMenuKeyListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_MENU, PLACEHOLDER, name);
        if (checkIfFeatureAvailable(xpathBase)) {
            menu.setText(getValue(xpathBase + XPATH_ELEMENT_TEXT));

            String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
            if (StringUtils.isNotEmpty(tooltip)) {
                menu.setToolTipText(tooltip);
            }

            Icon icon = getIcon(getValue(xpathBase + XPATH_ELEMENT_ICON));
            if (icon != null) {
                menu.setIcon(icon);
            }

            int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
            if (mnemonic > -1) {
                menu.setMnemonic(mnemonic);
            }

            // Sub-Menus, MenuItems und Separators erzeugen und dem Menu zuordnen
            if (document != null) {
                XPath xpathSelector = DocumentHelper.createXPath(xpathBase + XPATH_ELEMENT_ITEMS);
                List<?> items = xpathSelector.selectNodes(document);
                for (Object name2 : items) {
                    Element element = (Element) name2;
                    Iterator<?> elIt = element.elementIterator();
                    while (elIt.hasNext()) {
                        Element next = (Element) elIt.next();

                        if (StringUtils.equals(next.getName(), ELEMENT_MENUITEM)) {
                            AKJMenuItem mi = createMenuItem(next.getText(), listener);
                            menu.add(mi);
                        }
                        else if (StringUtils.equals(next.getName(), ELEMENT_SUBMENU)) {
                            String menuName = next.getText();
                            if (!StringUtils.equals(menuName, name)) { // vermeidet Endlosschleife!
                                AKJMenu submenu = createMenu(menuName, listener);
                                if (submenu != null) {
                                    menu.add(submenu);
                                }
                            }
                        }
                        else if (StringUtils.equals(next.getName(), ELEMENT_SEPARATOR)
                                && checkIfFeatureAvailable(next.getUniquePath())) {
                            menu.addSeparator();
                        }
                    }
                }
            }
            return menu;
        }
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createMenuItem(String)
     */
    @Override
    public AKJMenuItem createMenuItem(String name) {
        AKJMenuItem mi = new AKJMenuItem();
        mi.setName(name);
        mi.getAccessibleContext().setAccessibleName(name);
        mi.setActionCommand(name);
        mi.addMenuKeyListener(getAdminMouseListener());

        String xpathBase = StringUtils.replace(XPATH_MENUITEM, PLACEHOLDER, name);

        Action action = createAction(getValue(xpathBase + XPATH_ELEMENT_ACTION));
        if (action != null) {
            mi.setAction(action);
        }

        String text = getValue(xpathBase + XPATH_ELEMENT_TEXT);
        if (StringUtils.isNotEmpty(text)) {
            mi.setText(text);
        }

        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            mi.setToolTipText(tooltip);
        }

        Icon icon = getIcon(getValue(xpathBase + XPATH_ELEMENT_ICON));
        if (icon != null) {
            mi.setIcon(icon);
        }

        int mnemonic = getMnemonic(getValue(xpathBase + XPATH_ELEMENT_MNEMONIC));
        if (mnemonic > -1) {
            mi.setMnemonic(mnemonic);
        }

        KeyStroke keyStroke = getAccelerator(getValue(xpathBase + XPATH_ELEMENT_ACCELERATOR));
        if (keyStroke != null) {
            mi.setAccelerator(keyStroke);
        }

        return mi;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createMenuItem(String, ActionListener)
     */
    @Override
    public AKJMenuItem createMenuItem(String name, ActionListener listener) {
        AKJMenuItem mi = createMenuItem(name);
        if (listener != null) {
            mi.addActionListener(listener);
        }

        return mi;
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#getText(String)
     */
    @Override
    public String getText(String name) {
        String value = getValue(StringUtils.replace(XPATH_TEXT, PLACEHOLDER, name));
        return (value != null) ? value : "";
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createIcon(String)
     */
    @Override
    public ImageIcon createIcon(String name) {
        String url = getValue(StringUtils.replace(XPATH_ICON, PLACEHOLDER, name));
        return getIcon(url);
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#getIcon(String)
     */
    @Override
    public ImageIcon getIcon(String url) {
        if (iconHelper == null) {
            iconHelper = new IconHelper();
        }
        return iconHelper.getIcon(url);
    }

    /**
     * @see de.augustakom.common.gui.swing.SwingFactory#createColor(String, Color)
     */
    @Override
    public Color createColor(String name, Color defaultColor) {
        Color result = null;

        String xpathBase = StringUtils.replace(XPATH_COLOR, PLACEHOLDER, name);
        int red = getIntValue(xpathBase + XPATH_ELEMENT_RED);
        int green = getIntValue(xpathBase + XPATH_ELEMENT_GREEN);
        int blue = getIntValue(xpathBase + XPATH_ELEMENT_BLUE);

        if ((red >= 0) && (red <= 255) && (green >= 0) && (green <= 255) && (blue >= 0) && (blue <= 255)) {
            result = new Color(red, green, blue);
        }

        return (result != null) ? result : defaultColor;
    }

    /**
     * Gibt die KeyCode-Konstante anhand des Strings zurueck.
     *
     * @param keyCode Name der KeyCode-Konstante z.B. VK_L
     * @return int-Wert des Key-Codes oder -1, falls der KeyCode nicht ermittelt werden konnte.
     */
    protected int getMnemonic(String keyCode) {
        if (StringUtils.isNotEmpty(keyCode)) {
            try {
                Class<?> keyClazz = KeyEvent.class;
                Field field = keyClazz.getField(keyCode);
                return field.getInt(null);
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
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
     * Gibt einen String-Wert zurueck, der dem angegebenen XPath entspricht. <br> Konnte fuer den XPath kein Wert
     * gefunden werden, wird ein Leerstring zurueck gegeben.
     *
     * @param xpath XPath, der ausgewertet werden soll
     * @return Value des XPath'
     */
    protected String getValue(String xpath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> Read String-Value for XPath: " + xpath);
        }

        if (document != null) {
            String result = document.valueOf(xpath);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("<<< String value of last XPath: " + result);
            }

            return result;
        }

        LOGGER.warn("XML-Document was null! Could not read value for XPath: " + xpath);
        return "";
    }

    /**
     * Gibt einen int-Wert zurueck, der dem angegebenen XPath entspricht. <br> Ist der XPath ungueltig oder der Wert des
     * XPath' kein int-Wert, wird der Wert -1 zurueck gegeben.
     *
     * @param xpath
     * @return
     */
    protected int getIntValue(String xpath) {
        Number number = getNumberValue(xpath);
        return (number != null) ? number.intValue() : -1;
    }

    /**
     * @param xpath
     * @return
     * @see #getIntValue(String)
     */
    protected double getDoubleValue(String xpath) {
        Number number = getNumberValue(xpath);
        return (number != null) ? number.doubleValue() : -1;
    }

    /**
     * Gibt eine Number zurueck, die dem angegebenen XPath entspricht oder <code>null</code>, falls der XPath ungueltig
     * ist.
     *
     * @param xpath
     * @return
     */
    protected Number getNumberValue(String xpath) {
        Number result = null;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> Read Number-Value for XPath: " + xpath);
        }

        if (document != null) {
            result = document.numberValueOf(xpath);

            // Aufgrund von unterschiedlichen Implementierung in der Bibliothek "jaxen.jar"
            // wird von numberValueOf bei fehlenden Einträgen entweder null oder NaN zurückgeliefert.
            if ((result instanceof Double) && ((Double) result).isNaN()) {
                result = null;
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("<<< Number value of last XPath: " + result);
            }
        }

        return result;
    }

    /* Ermittelt die zu unterdrueckenden Key-Strokes und ordnet diese der Text-Komponente zu. */
    private void setPreventedKeys(AKPreventKeyStrokeAwareComponent textComp, String xpathBase) {
        if ((textComp != null) && StringUtils.isNotBlank(xpathBase)) {
            StringBuilder sb = new StringBuilder();
            sb.append(xpathBase);
            sb.append(XPATH_ELEMENT_PREVENTED_KEYS);
            sb.append(XPATH_ELEMENT_KEY_STROKE);

            XPath xpathSelector = DocumentHelper.createXPath(sb.toString());
            List<?> items = xpathSelector.selectNodes(document);
            for (Object name : items) {
                Element element = (Element) name;
                String item = element.getText();
                if (StringUtils.isNotEmpty(item)) {
                    textComp.preventKeyStroke(item);
                }
            }
        }
    }

    public boolean isXmlDocumentLoaded() {
        return document != null;
    }
}
