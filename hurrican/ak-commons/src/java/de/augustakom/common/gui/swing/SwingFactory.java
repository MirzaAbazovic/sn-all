/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.tools.lang.StringTools;

/**
 * Abstrakte SwingFactory. <br> Abhaengig von der uebergebenen Resource wird entweder an eine Implementierung
 * weitergeleitet, die eine XML-Datei auswertet oder an eine, die Property-Dateien auswertet. <br><br> <strong> Im
 * Moment ist lediglich die SwingFactory fuer die XML-Dateien (SwingFactoryXML) implementiert. </strong>
 *
 *
 */
public abstract class SwingFactory {

    /**
     * Default Hintergrundfarbe fuer aktive GUI-Komponenten
     */
    protected static final Color DEFAULT_BG_COLOR_ACTIVE = new Color(255, 255, 215);
    /**
     * Default Hintergrundfarbe fuer inaktive GUI-Komponenten
     */
    protected static final Color DEFAULT_BG_COLOR_INACTIVE = Color.white;
    /**
     * Default Hintergrundfarbe fuer die Text-Selektion
     */
    protected static final Color DEFAULT_SELECTION_COLOR = new Color(0, 0, 100);
    /**
     * Default-Farbe fuer die Text-Selektion
     */
    protected static final Color DEFAULT_SELECTION_TEXT_COLOR = new Color(255, 255, 255);

    /**
     * Wert fuer Parameter 'style' - Verwendung fuer Fett-Schrift.
     */
    protected static final String STYLE_BOLD = "bold";
    /**
     * Wert fuer Parameter 'style' - Verwendung fuer Kursiv-Schrift.
     */
    protected static final String STYLE_ITALIC = "italic";
    protected Set<String> availableFeatures;

    private AdministrationMouseListener adminML = null;

    private static JPopupMenu cutCopyPastePopup = null;

    /**
     * Gibt abhaengig von der uebergebenen Resource eine entsprechende SwingFactory zurueck. <br> Die Resource kann
     * entweder eine XML- oder eine Property-Datei sein. <br> <strong> Im Moment ist nur die SwingFactory fuer
     * XML-Dateien implementiert! </strong>
     *
     * @param resource Resource-Datei, aus der die Konfiguration gelesen werden soll. <br> Bsp. fuer XML-Dateien:
     *                 de/augustakom/MyConfig.xml <br> Bsp. fuer Property-Dateien: de.augustakom.MyConfig
     * @return
     */
    public static SwingFactory getInstance(String resource) {
        if (resource != null) {
            if (resource.endsWith(".xml")) {
                return new SwingFactoryXML(resource);
            }

            throw new IllegalArgumentException("SwingFactory for property files currently not supported!");
        }

        return null;
    }

    /**
     * Gibt abhaengig von der uebergebenen Resource eine entsprechende SwingFactory zurueck. <br> Die Resource kann
     * entweder eine XML- oder eine Property-Datei sein. <br> <strong> Im Moment ist nur die SwingFactory fuer
     * XML-Dateien implementiert! </strong>
     *
     * @param resource          Resource-Datei, aus der die Konfiguration gelesen werden soll. <br> Bsp. fuer
     *                          XML-Dateien: de/augustakom/MyConfig.xml <br> Bsp. fuer Property-Dateien:
     *                          de.augustakom.MyConfig
     * @param availableFeatures Ein Set mit allen im System Features, die online sind. GUI-Komponenten, die erst mit
     *                          einem Feature dazu gekommen sind, werden ausgeblendet, wenn das entsprechende Feature
     *                          nicht online ist. <br>
     * @return
     */
    public static SwingFactory getInstance(String resource, Set<String> availableFeatures) {
        final SwingFactory instance = getInstance(resource);
        if (instance != null) {
            instance.availableFeatures = availableFeatures;
        }
        return instance;
    }

    /**
     * Erzeugt ein neues AKJTextField.
     *
     * @param name Name des TextFields.
     * @return Instanz von AKJTextField.
     */
    public abstract AKJTextField createTextField(String name);

    /**
     * Erzeugt ein neues AKJTextField. Das TextField wird dem Label <code>lbl4TF</code> zugeordnet.
     *
     * @param name   Name des TextFields.
     * @param lbl4TF
     * @return Instanz von AKJTextField.
     */
    public AKJTextField createTextField(String name, AKJLabel lbl4TF) {
        AKJTextField tf = createTextField(name);
        if (lbl4TF != null) {
            lbl4TF.setLabelFor(tf);
        }
        return tf;
    }

    /**
     * Erzeugt ein neues AKJTextField.
     *
     * @param name     Name des TextFields.
     * @param editable Angabe, ob das TextField editierbar sein soll.
     * @return Instanz von AKJTextField.
     */
    public AKJTextField createTextField(String name, boolean editable) {
        AKJTextField tf = createTextField(name);
        tf.setEditable(editable);
        return tf;
    }

    /**
     * Erzeugt ein neues AKJTextField.
     *
     * @param name             Name des TextFields.
     * @param editable         Angabe, ob das TextField editierbar sein soll.
     * @param selectAllOnFocus Angabe, ob der gesamte Inhalt des TextFields selektiert werden soll, wenn die Komponente
     *                         den Focus erhaelt.
     * @return Instanz von AKJTextField.
     */
    public AKJTextField createTextField(String name, boolean editable, boolean selectAllOnFocus) {
        AKJTextField tf = createTextField(name);
        tf.setEditable(editable);
        tf.selectAllOnFocus(selectAllOnFocus);
        return tf;
    }

    /**
     * @param name
     * @param editable
     * @param selectAllOnFocus
     * @param keyListener
     * @return
     */
    public AKJTextField createTextField(String name, boolean editable, boolean selectAllOnFocus,
            KeyListener keyListener) {
        AKJTextField tf = createTextField(name, editable, selectAllOnFocus);
        tf.addKeyListener(keyListener);
        return tf;
    }

    /**
     * Erzeugt ein neues AKJPasswordField.
     *
     * @param name Name des PasswordFields.
     * @return Instanz von AKJPasswordField.
     */
    public abstract AKJPasswordField createPasswordField(String name);

    public AKJPasswordField createPasswordField(String name, boolean selectAllOnFocus) {
        AKJPasswordField tf = createPasswordField(name);
        tf.selectAllOnFocus(selectAllOnFocus);
        return tf;
    }

    /**
     * Erzeugt ein neues TextField vom Typ AKJFormattedTextField. <br> Dem TextField kann entweder ein Format vom Typ
     * 'date', 'number' oder 'mask' zugeordnet werden. <br> Ist mehr als ein Format zugeordnet, wird das erste Format
     * verwendet!
     *
     * @param name Name des TextFields
     * @return Instanz von AKJFormattedTextField.
     */
    public abstract AKJFormattedTextField createFormattedTextField(String name);

    /**
     * @param name
     * @param keyListener
     * @return
     */
    public AKJFormattedTextField createFormattedTextField(String name, KeyListener keyListener) {
        AKJFormattedTextField tf = createFormattedTextField(name);
        tf.addKeyListener(keyListener);
        return tf;
    }

    /**
     * @param name
     * @param editable
     * @return Instanz von AKJFormattedTextField
     */
    public AKJFormattedTextField createFormattedTextField(String name, boolean editable) {
        AKJFormattedTextField tf = createFormattedTextField(name);
        tf.setEditable(editable);
        return tf;
    }

    /**
     * Erzeugt eine Date-Komponente bestehend aus einem TextField und einem Button.
     *
     * @param name Name der Komponente.
     * @return Instanz von AKJDateComponent
     */
    public abstract AKJDateComponent createDateComponent(String name);

    /**
     * @param name
     * @param keyListener KeyListener fuer die DateComponent.
     * @return
     */
    public AKJDateComponent createDateComponent(String name, KeyListener keyListener) {
        AKJDateComponent dc = createDateComponent(name);
        if (keyListener instanceof AKSearchKeyListener) {
            dc.addSearchKeyListener((AKSearchKeyListener) keyListener);
        }
        else {
            dc.addKeyListener(keyListener);
        }
        return dc;
    }

    /**
     * Erzeugt eine Date-Komponente bestehend aus einem TextField und einem Button.
     *
     * @param name   Name der Komponente.
     * @param usable
     * @return Instanz von AKJDateComponent
     */
    public AKJDateComponent createDateComponent(String name, boolean usable) {
        AKJDateComponent dc = createDateComponent(name);
        dc.setUsable(usable);
        return dc;
    }

    /**
     * Erzeugt eine AKJSpinner-Komponente.
     *
     * @param name         Name der Spinner-Komponente
     * @param spinnerModel zu verwendendes Spinner-Model.
     * @return Instanz von AKJSpinner
     */
    public abstract AKJSpinner createSpinner(String name, SpinnerModel spinnerModel);

    /**
     * Erzeugt eine neue AKJTextArea.
     *
     * @param name Name der TextArea.
     * @return Instanz von AKJTextArea.
     */
    public abstract AKJTextArea createTextArea(String name);

    /**
     * Erzeugt eine neue AKJTextArea.
     *
     * @param name     Name der TextArea
     * @param editable Angabe, ob die TextArea editierbar sein soll.
     * @return Instanz von AKJTextArea.
     */
    public AKJTextArea createTextArea(String name, boolean editable) {
        AKJTextArea ta = createTextArea(name);
        ta.setEditable(editable);
        return ta;
    }

    /**
     * @param name
     * @param editable
     * @param wrapStyleWord
     * @param lineWrap
     * @return
     */
    public AKJTextArea createTextArea(String name, boolean editable, boolean wrapStyleWord, boolean lineWrap) {
        AKJTextArea ta = createTextArea(name);
        ta.setEditable(editable);
        ta.setWrapStyleWord(wrapStyleWord);
        ta.setLineWrap(lineWrap);
        return ta;
    }

    /**
     * @param name
     * @param editable
     * @param fontStyle Konstante aus Font (z.B. Font.BOLD)
     * @return Instanz von AKJTextArea
     */
    public AKJTextArea createTextArea(String name, boolean editable, int fontStyle) {
        AKJTextArea ta = createTextArea(name);
        ta.setEditable(editable);
        Font font = new Font(ta.getFont().getName(), fontStyle, ta.getFont().getSize());
        ta.setFont(font);
        return ta;
    }

    /**
     * Erzeugt eine neue AKJTextPane. <br> Der Zugriffsname fuer eine TextPane ist der gleiche Name wie fuer eine
     * TextArea. Bei einer TextPane werden allerdings die Elemente 'columns' und 'rows' ignoriert.
     *
     * @param name Name der TextPane.
     * @return Instanz von AKJTextPane.
     */
    public abstract AKJTextPane createTextPane(String name);

    /**
     * Liest den Text für ein Label aus.
     *
     * @param name name des Labels
     * @return
     */
    public abstract String getLabelText(String name);

    /**
     * Erzeugt ein neues AKJLabel.
     *
     * @param name                Name des neuen Labels.
     * @param horizontalAlignment horizontale Ausrichtung des Labels. (Konstante aus AKJLabel - LEFT, CENTER, RIGHT,
     *                            LEADING oder TRAILING)
     * @return Instanz von AKJLabel.
     */
    public abstract AKJLabel createLabel(String name, int horizontalAlignment);

    /**
     * @param name
     * @param horizontalAlignment
     * @param fontStyle           Angabe des Font-Styles (z.B. Font.BOLD)
     * @return Instanz von AKJLabel
     */
    public AKJLabel createLabel(String name, int horizontalAlignment, int fontStyle) {
        AKJLabel lbl = createLabel(name, horizontalAlignment);
        Font font = new Font(lbl.getFont().getName(), fontStyle, lbl.getFont().getSize());
        lbl.setFont(font);
        return lbl;
    }

    /**
     * Erzeugt ein neues AKJLabel. <br> Die horizontale Ausrichtung des Labels ist LEFT.
     *
     * @param name Name des neuen Labels.
     * @return Instanz von AKJLabel.
     */
    public abstract AKJLabel createLabel(String name);

    /**
     * @param name
     * @param params
     * @return
     * <code>params</code> ersetzt.
     */
    public abstract AKJLabel createLabel(String name, Object[] params);

    /**
     * Erzeugt ein Panel, das eine Linie darstellt. Ueber den Parameter <code>alignment</code> kann definiert werden, ob
     * es sich um eine horizontale oder vertikale Linie handeln soll. <br> Moegliche Werte: <br>
     * SwingConstants.HORIZONTAL <br> SwingConstants.VERTICAL
     *
     * @param alignment SwingConstants.HORIZONTAL oder SwingConstants.VERTICAL
     * @return AKJPanel
     */
    public AKJPanel createLinePanel(int alignment) {
        return createLinePanel(alignment, BorderFactory.createLineBorder(Color.darkGray));
    }

    /**
     * Wie <code>createLinePanel(int)</code>. Allerdings kann hier der Border-Typ angegeben werden.
     *
     * @param alignment SwingConstants.HORIZONTAL oder SwingConstants.VERTICAL
     * @param border    darzustellender Border fuer das Panel.
     * @return AKJPanel
     */
    public AKJPanel createLinePanel(int alignment, Border border) {
        if (alignment == SwingConstants.HORIZONTAL) {
            AKJPanel horizontal = new AKJPanel();
            horizontal.setBorder(border);
            horizontal.setPreferredSize(new Dimension(50, 1));
            return horizontal;
        }
        else if (alignment == SwingConstants.VERTICAL) {
            AKJPanel vertical = new AKJPanel();
            vertical.setBorder(border);
            vertical.setPreferredSize(new Dimension(50, 1));
            return vertical;
        }
        else {
            return null;
        }
    }

    /**
     * Erzeugt eine Instanz von javax.swing.Action. <br> Wichtig: Pro SwingFactory wird jede Action nur
     * <strong>einmal</strong> erzeugt. Der Identifier fuer eine Action ist der uebergebene Name (name). <br><br> Grund:
     * Eine Action wird oft an mehreren Stellen verwendet (z.B. im Menu und in der Toolbar). Wird fuer jede Stelle eine
     * neue Action-Instanz erzeugt, muss auch jedes Item/Button eigens disabled/enabled werden. Bei der Verwendung einer
     * einzelnen Instanz reicht ein einmaliger Aufruf aus, um alle Stellen (MenuItems, Buttons) zu enablen/disablen.
     * <br>
     *
     * @param name Name der Action
     * @return Instanz einer Action.
     */
    public abstract Action createAction(String name);

    /**
     * Erzeugt einen neuen AKJButton.
     *
     * @param name Name des neuen Buttons.
     * @return Instanz von AKJButton.
     */
    public abstract AKJButton createButton(String name);

    /**
     * @param name     Name des neuen Buttons.
     * @param listener Listener fuer den Button
     * @return Instanz von AKJButton.
     * @see SwingFactory#createButton(String)
     */
    public abstract AKJButton createButton(String name, ActionListener listener);

    /**
     * @param name
     * @param listener
     * @param border
     * @return
     */
    public AKJButton createButton(String name, ActionListener listener, Border border) {
        AKJButton b = createButton(name, listener);
        b.setBorder(border);
        return b;
    }

    /**
     * Erzeugt einen neuen AKJRadioButton. <br> Der RadioButton ist standardmaessig nicht selektiert!
     *
     * @param name Name des RadioButtons.
     * @return Instanz von AKJRadioButton.
     */
    public abstract AKJRadioButton createRadioButton(String name);

    /**
     * Der Radio-Button wird zusaetzlich der ButtonGroup <code>buttonGroup</code>
     * zugeordnet.
     */
    public AKJRadioButton createRadioButton(String name, ButtonGroup buttonGroup) {
        AKJRadioButton rb = createRadioButton(name);
        buttonGroup.add(rb);
        return rb;
    }

    /**
     * @param name
     * @param buttonGroup
     * @param selected
     * Der Radio-Button wird zusaetzlich der ButtonGroup <code>buttonGroup</code>
     * zugeordnet.
     */
    public AKJRadioButton createRadioButton(String name, ButtonGroup buttonGroup, boolean selected) {
        AKJRadioButton rb = createRadioButton(name);
        buttonGroup.add(rb);
        rb.setSelected(selected);
        return rb;
    }

    /**
     * @param name     Name des RadioButtons.
     * @param listener ActionListener fuer den RadioButton.
     * @param selected Angabe, ob der RadioButton selektiert sein soll.
     * @return Instanz von AKJRadioButton.
     * @see SwingFactory#createRadioButton(String)
     */
    public abstract AKJRadioButton createRadioButton(String name, ActionListener listener, boolean selected);

    /**
     * @param name
     * @param listener
     * @param selected
     * @param buttonGroup
     * @return
     */
    public AKJRadioButton createRadioButton(String name, ActionListener listener,
            boolean selected, ButtonGroup buttonGroup) {
        AKJRadioButton rb = createRadioButton(name, listener, selected);
        buttonGroup.add(rb);
        return rb;
    }

    /**
     * Erzeugt einen neuen AKJToggleButton. <br> Der ToggleButton ist standardmaessig nicht selektiert!
     *
     * @param name Name des ToggleButtons.
     * @return Instanz von AKJToggleButton.
     */
    public abstract AKJToggleButton createToggleButton(String name);

    /**
     * @param name     Name des ToggleButtons.
     * @param listener ActionListener fuer den ToggleButton.
     * @param selected Angabe, ob der ToggleButton selektiert sein soll.
     * @return Instanz von AKJToggleButton.
     * @see SwingFactory#createToggleButton(String)
     */
    public abstract AKJToggleButton createToggleButton(String name, ActionListener listener, boolean selected);

    /**
     * @param name     Name des ToggleButtons.
     * @param listener ActionListener fuer den ToggleButton.
     * @param selected Angabe, ob der ToggleButton selektiert sein soll.
     * @param bgGroup  ButtonGroup, der der Button zugeordnet werden soll.
     * @return Instanz von AKJToggleButton.
     * @see SwingFactory#createToggleButton(String)
     */
    public AKJToggleButton createToggleButton(String name, ActionListener listener, boolean selected, ButtonGroup bgGroup) {
        AKJToggleButton btn = createToggleButton(name, listener, selected);
        if (bgGroup != null) {
            bgGroup.add(btn);
        }
        return btn;
    }

    /**
     * Erzeugt eine neue AKJCheckBox. <br> Die CheckBox ist standardmaessig nicht selektiert!
     *
     * @param name Name der CheckBox
     * @return Instanz von AKJCheckBox.
     */
    public abstract AKJCheckBox createCheckBox(String name);

    /**
     * @param name
     * @param enabled Angabe, ob die CheckBox enabled oder disabled sein soll.
     * @return
     */
    public AKJCheckBox createCheckBox(String name, boolean enabled) {
        AKJCheckBox cb = createCheckBox(name);
        cb.setEnabled(enabled);
        return cb;
    }

    /**
     * @param name
     * @param listener Listener fuer die CheckBox
     * @param selected Angabe, ob die CheckBox selektiert sein soll.
     * @return Instanz von AKJCheckBox.
     * @see SwingFactory#createCheckBox(String)
     */
    public abstract AKJCheckBox createCheckBox(String name, ActionListener listener, boolean selected);

    /**
     * Erstellt eine neue AKJComboBox. <br>
     *
     * @param name Name der ComboBox.
     * @return Instanz von AKJComboBox.
     */
    public abstract AKJComboBox createComboBox(String name);

    /**
     * @param name
     * @param values Angabe der darzustellenden Werte
     * @return
     *
     */
    public AKJComboBox createComboBox(String name, String[] values) {
        AKJComboBox cb = createComboBox(name);
        ComboBoxModel cbm = new DefaultComboBoxModel(values);
        cb.setModel(cbm);
        return cb;
    }

    /**
     * Erstellt eine neue AKJComboBox. <br>
     *
     * @param name    Name der ComboBox.
     * @param enabled
     * @return Instanz von AKJComboBox.
     */
    public AKJComboBox createComboBox(String name, boolean enabled) {
        AKJComboBox cb = createComboBox(name);
        cb.setEnabled(enabled);
        return cb;
    }

    /**
     * @param name
     * @param renderer Renderer fuer die ComboBox.
     * @return
     */
    public AKJComboBox createComboBox(String name, ListCellRenderer renderer) {
        AKJComboBox cbox = createComboBox(name);
        cbox.setRenderer(renderer);
        return cbox;
    }

    /**
     * @param name
     * @param renderer Renderer fuer die ComboBox.
     * @param enabled
     * @return
     */
    public AKJComboBox createComboBox(String name, ListCellRenderer renderer, boolean enabled) {
        AKJComboBox cbox = createComboBox(name);
        cbox.setRenderer(renderer);
        cbox.setEnabled(enabled);
        return cbox;
    }

    /**
     * Erzeugt eine neue AKJComboBox mit dem angegebenen ComboBoxModel-Objekt. <br> Items, die evtl. in der
     * Resource-Datei fuer die ComboBox konfiguriert sind werden in dieser Methode ignoriert!
     *
     * @param name  Name der ComboBox
     * @param model Model fuer die ComboBox
     * @return Instanz von AKJComboBox.
     */
    public abstract AKJComboBox createComboBox(String name, ComboBoxModel model);

    /**
     * Erzeugt eine neue AKJList.
     *
     * @param name Name der List.
     * @return Instanz von AKJList.
     */
    public abstract AKJList createList(String name);

    /**
     * Erzeugt eine neue AKJList mit dem angegebenen ListModel-Objekt. <br> Items, die evtl. in der Resource-Datei fuer
     * die List konfiguriert sind werden in dieser Methode ignoriert!
     *
     * @param name  Name der List.
     * @param model ListModel, das der List uebergeben werden soll.
     * @return Instanz von AKJList.
     */
    public abstract AKJList createList(String name, ListModel model);

    /**
     * Erstellt eine neue AKJToolBar
     *
     * @param name Name der ToolBar.
     * @return Instanz von AKJToolBar.
     */
    public abstract AKJToolBar createToolBar(String name);

    /**
     * Erstellt eine neue AKJMenuBar.
     *
     * @param name     Name der MenuBar.
     * @param listener ActionListener, der den evtl. vorhandenen MenuItems zugeordnet wird.
     * @return Instanz von AKJMenuBar.
     */
    public abstract AKJMenuBar createMenuBar(String name, ActionListener listener);

    /**
     * Erstellt ein neues AKJMenu.
     *
     * @param name     Name des Menus.
     * @param listener ActionListener, der den evtl. vorhandenen MenuItems zugeordnet wird.
     * @return Instanz von AKJMenu.
     */
    public abstract AKJMenu createMenu(String name, ActionListener listener);

    /**
     * Erstellt ein neues AKJMenuItem. <br>
     * <p/>
     * Ist dem MenuItem eine Action UND ein Text, Tooltip etc. zugeordnet, dann werden die Werte der Action mit den
     * anderen Werten ueberschrieben. <br> Ist nur die Action definiert, dann werden deren Werte fuer das MenuItem
     * verwendet.
     *
     * @param name Name des MenuItems.
     * @return Instanz von AKJMenuItem.
     */
    public abstract AKJMenuItem createMenuItem(String name);

    /**
     * @param name     Name des MenuItems.
     * @param listener ActionListener fuer das MenuItem.
     * @return Instanz von AKJMenuItem.
     * @see SwingFactory#createMenuItem(String)
     */
    public abstract AKJMenuItem createMenuItem(String name, ActionListener listener);

    /**
     * Gibt einen String zurueck.
     *
     * @param name Name des Textes.
     * @return Text, der dem Namen zugeordnet ist.
     */
    public abstract String getText(String name);

    /**
     * Gibt einen String zurueck. <br> Evtl. im String vorhandene Platzhalter - {.} - werden durch die Parameter
     * <code>params</code> ersetzt.
     *
     * @param name   Name des gesuchten Textes
     * @param params
     * @return
     */
    public String getText(String name, Object... params) {
        String text = getText(name);
        return StringTools.formatString(text, params, null);
    }

    /**
     * Die String-Parameter <code>url</code> wird als Dateiname (bzw. URL) interpretiert. Es wird versucht, aus diesem
     * Namen ein Icon zu erstellen.
     *
     * @param url
     * @return
     */
    public abstract ImageIcon getIcon(String url);

    /**
     * Erstellt ein Icon.
     *
     * @param name Name des Icons.
     * @return das definierte Icon oder <code>null</code>, wenn es nicht gefunden wurde.
     */
    public abstract ImageIcon createIcon(String name);

    /**
     * Erstellt eine Farbe.
     *
     * @param name         Name der Farbe.
     * @param defaultColor Default-Farbe. Wird zurueck gegeben, falls keine gueltige Farb-Definition gefunden wurde.
     * @return die definierte Farbe oder <code>defaultColor</code>
     */
    public abstract Color createColor(String name, Color defaultColor);

    /**
     * Erstellt eine NavigationBar. <br> WICHTIG: fuer diese Methode ist kein Konfiguration vorgesehen!
     *
     * @param name      Name fuer die Nav-Bar.
     * @param listener  Listener fuer die Nav-Bar.
     * @param showCount Flag, ob die Anzahl der darstellbaren Objekte angezeigt werden soll.
     * @param showLast  Flag, ob die Buttons 'erster DS' und 'letzer DS' angezeigt werden sollen.
     * @return Instanz von AKJNavigationBar.
     */
    public AKJNavigationBar createNavigationBar(String name, AKNavigationBarListener listener, boolean showCount, boolean showLast) {
        AKJNavigationBar navBar = new AKJNavigationBar(listener, showCount, showLast);
        navBar.getAccessibleContext().setAccessibleName(name);
        navBar.addMouseListener(getAdminMouseListener());
        return navBar;
    }

    /**
     * Erzeugt eine neue Instanz von <code>AKReferenceField</code>
     *
     * @param name Name fuer das Reference-Field
     * @return
     *
     */
    public abstract AKReferenceField createReferenceField(String name);

    /**
     * @param name                 Name fuer das Reference-Field
     * @param refModel             Typ des Referenz-Modells
     * @param idProperty           ID-Property der Referenz
     * @param showProperty         Property-Name fuer die Anzeige
     * @param referenceFindExample Example-Objekt mit den Suchparametern fuer die Ermittlung weiterer Referenzen.
     * @return
     *
     */
    public AKReferenceField createReferenceField(String name,
            Class<?> refModel, String idProperty, String showProperty, Object referenceFindExample) {
        AKReferenceField rf = createReferenceField(name);
        rf.setReferenceModel(refModel);
        rf.setReferenceIdProperty(idProperty);
        rf.setReferenceShowProperty(showProperty);
        rf.setReferenceFindExample(referenceFindExample);
        return rf;
    }

    /**
     * Gibt einen MouseListener zurueck, der einen Administrations-Dialog fuer eine GUI-Komponente oeffnet.
     *
     * @return
     */
    protected AdministrationMouseListener getAdminMouseListener() {
        if (adminML == null) {
            adminML = new AdministrationMouseListener();
        }

        return adminML;
    }

    /**
     * Erzeugt ein Popup-Menu fuer ein Objekt des Typs <code>JTextComponent</code>. Das Popup-Menu enthaelt die Actions
     * 'Cut', 'Copy' und 'Paste'. <br> Wichtig: Das PopupMenu darf nicht veraendert werden, da es statisch(!) erzeugt
     * wurde! Sollen noch andere Actions (ausser Cut, Copy, Paste) in dem PopupMenu angezeigt werden, muessen die Items
     * aus diesem PopupMenu in ein anderes PopupMenu kopiert werden.
     *
     * @param textComp
     * @return JPopupMenu mit Cut-, Copy- und Paste-Funktion
     */
    public JPopupMenu createCCPPopupMenu(JTextComponent textComp) {
        if (textComp != null) {
            if (cutCopyPastePopup == null) {
                cutCopyPastePopup = new JPopupMenu();
                addCCPActions2PopupMenu(textComp, cutCopyPastePopup);
            }
            return cutCopyPastePopup;
        }
        return null;
    }

    /**
     * Konfiguriert das PopupMenu mit Cut/Copy/Paste Actions.
     *
     * @param textComp
     * @param popup
     */
    public static void addCCPActions2PopupMenu(JTextComponent textComp, JPopupMenu popup) {
        List<Action> ccpActions = getCutCopyPasteActions(textComp);
        if ((ccpActions != null) && (!ccpActions.isEmpty())) {
            for (Action action : ccpActions) {
                AKJMenuItem mi = new AKJMenuItem(action);

                String actionName = (String) action.getValue(Action.NAME);
                if (StringUtils.equals(actionName, DefaultEditorKit.cutAction)) {
                    mi.setText("Ausschneiden");
                }
                else if (StringUtils.equals(actionName, DefaultEditorKit.copyAction)) {
                    mi.setText("Kopieren");
                }
                else if (StringUtils.equals(actionName, DefaultEditorKit.pasteAction)) {
                    mi.setText("Einfügen");
                }

                popup.add(mi);
            }
        }
    }

    /**
     * Ermittelt die Standard-Actions 'Cut', 'Copy' und 'Paste' der Text-Komponente und gibt diese in einer List
     * zurueck.
     *
     * @param textComp
     * @return
     */
    public static List<Action> getCutCopyPasteActions(JTextComponent textComp) {
        if (textComp != null) {
            List<Action> cutCopyPasteActions = new ArrayList<Action>();
            Map<Object, Action> actions = new HashMap<Object, Action>();
            Action[] actionsArray = textComp.getActions();
            for (int i = 0; i < actionsArray.length; i++) {
                Action a = actionsArray[i];
                actions.put(a.getValue(Action.NAME), a);
            }

            Action cut = actions.get(DefaultEditorKit.cutAction);
            Action copy = actions.get(DefaultEditorKit.copyAction);
            Action paste = actions.get(DefaultEditorKit.pasteAction);

            if (cut != null) { cutCopyPasteActions.add(cut); }
            if (copy != null) { cutCopyPasteActions.add(copy); }
            if (paste != null) { cutCopyPasteActions.add(paste); }

            return cutCopyPasteActions;
        }
        return null;
    }
}
