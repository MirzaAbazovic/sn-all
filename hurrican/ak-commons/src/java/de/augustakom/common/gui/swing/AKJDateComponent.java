/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.time.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;
import de.augustakom.common.tools.lang.DateTools;
import de.mnet.common.tools.DateConverterUtils;


/**
 * Datumskomponente, die ein FormattedTextField und einen Button enthaelt. <br> Ueber den Button wird ein Dialog
 * aufgerufen, ueber den ein Datum ausgewaehlt werden kann.
 *
 *
 */
public class AKJDateComponent extends AKJPanel implements AKColorChangeableComponent, AKManageableComponent,
        ActionListener, AKSwingConstants, FocusListener {
    /**
     * Name fuer ein PropertyChange-Event, wenn das Datum geaendert wurde.
     */
    public static final String PROPERTY_CHANGE_DATE = "AKJDateComponent.date";
    private static final Logger LOGGER = Logger.getLogger(AKJDateComponent.class);
    private static final long serialVersionUID = -593790053376137532L;

    private AKJFormattedTextField tfDate = null;
    private AKJButton btnDate = null;
    private String datePickerTitle = null;
    private String datePickerIcon = null;

    private SimpleDateFormat dateFormat = null;
    private Component dialogParent = null;
    private volatile MouseListener adminML;

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    private Date minDate = null;

    private boolean dateFieldHasFocus = false;

    /**
     * Standardkonstruktor
     */
    public AKJDateComponent() {
        super();
        init();
    }

    /**
     * Konstruktor mit Angabe des darzustellenden Datums.
     *
     * @param date
     */
    public AKJDateComponent(Date date) {
        super();
        init();
        setDate(date);
    }

    /* Initialisiert die Komponente. */
    private void init() {
        createMinDate();
        datePickerTitle = "Datum wählen";
        datePickerIcon = "de/augustakom/common/gui/images/datepicker.gif";

        dateFormat = new SimpleDateFormat(DateTools.PATTERN_DAY_MONTH_YEAR);
        tfDate = new AKJFormattedTextField(dateFormat);
        tfDate.addFocusListener(this);
        btnDate = new AKJButton();
        IconHelper ih = new IconHelper();
        Icon icon = ih.getIcon(datePickerIcon);
        if (icon != null) {
            btnDate.setIcon(icon);
            btnDate.setBorder(BorderFactory.createEtchedBorder());
        }
        else {
            btnDate.setText("...");
        }

        btnDate.setToolTipText("Öffnet einen Dialog, um ein Datum auszuwählen");
        btnDate.setPreferredSize(new Dimension(20, tfDate.getPreferredSize().height));
        btnDate.addActionListener(this);

        setLayout(new GridBagLayout());
        Insets insetsTF = new Insets(0, 0, 0, 0);
        Insets insetsBTN = new Insets(0, 2, 0, 0);
        this.add(tfDate, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, insetsTF));
        this.add(btnDate, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, insetsBTN));
    }

    /* Erzeugt ein Min-Datum */
    private void createMinDate() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 1900);

        minDate = new Date(cal.getTimeInMillis());
    }

    /**
     * Definiert ein Datum, das als minimales Eingabedatum akzeptiert werden soll.
     *
     * @param minDate
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate != null ? new Date(minDate.getTime()) : null;
    }

    /**
     * Uebergibt dem TextField der Date-Komponente ein neues Datums-Format.
     *
     * @param dateFormat
     */
    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
        tfDate.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(this.dateFormat)));
    }

    /**
     * Uebergibt dem TextField der Date-Komponente ein neues DatePattern.
     *
     * @param datePattern z.B. dd.MM.yyyy
     */
    public void setDatePattern(String datePattern) {
        this.dateFormat = new SimpleDateFormat(datePattern);
        setDateFormat(dateFormat);
    }

    /**
     * Setzt das Datum, das in der Komponente dargestellt werden soll. <br> Nachdem das Datum gesetzt wurde, wird ein
     * PropertyChange-Event ausgeloest, ueber das die PropertyChangeListener benachrichtigt werden. Der Name des
     * PropertyChange-Events ist PROPERTY_CHANGE_DATE.
     *
     * @param date
     */
    public void setDate(Date date) {
        Date old = getDate(null);
        tfDate.setValue(date);
        firePropertyChange(PROPERTY_CHANGE_DATE, old, date);
    }

    /**
     * @param date
     * @see AKJDateComponent#setDate(java.util.Date)
     */
    public void setDateTime(LocalDateTime date) {
        final Date javaDate = DateConverterUtils.asDate(date);
        setDate(javaDate);
    }

    /**
     * Gibt das Datum zurueck, dass gerade dargestellt wird oder <code>defaultDate</code>, wenn kein gueltiger
     * Datums-String eingetragen ist.
     *
     * @param defaultDate Datum, das zurueck gegeben werden soll, wenn kein gueltiges Datum eingetragen ist.
     * @return eingetragenes Datum oder <code>defaultDate</code>.
     */
    public Date getDate(Date defaultDate) {
        if (tfDate.hasFocus() || dateFieldHasFocus) {
            convertToDateFormat();
        }
        return tfDate.getValueAsDate(defaultDate);
    }

    /**
     * @param defaultDateTime
     * @return
     * @see AKJDateComponent#getDate(java.util.Date)
     */
    public LocalDateTime getDateTime(LocalDateTime defaultDateTime) {
        final Date defaultDate = DateConverterUtils.asDate(defaultDateTime);
        final Date date = getDate(defaultDate);
        return (date != null) ? DateConverterUtils.asLocalDateTime(date) : defaultDateTime;
    }

    /**
     * Setzt den Tooltip-Text fuer das TextField der Date-Komponente.
     *
     * @see javax.swing.JComponent#setToolTipText(java.lang.String)
     */
    @Override
    public void setToolTipText(String text) {
        tfDate.setToolTipText(text);
    }

    /**
     * @param columns
     */
    public void setColumns(int columns) {
        tfDate.setColumns(columns);
    }

    /**
     * Setzt den Tooltip-Text fuer den Button der Date-Komponente.
     *
     * @param text
     */
    public void setButtonToolTipText(String text) {
        btnDate.setToolTipText(text);
    }

    /**
     * Setzt den Text fuer den Button der Date-Komponente.
     *
     * @param text
     */
    public void setButtonText(String text) {
        btnDate.setText(text);
    }

    /**
     * Versucht das Icon mit der URL <code>iconURL</code> zu laden und uebergibt dieses an den Button.
     *
     * @param iconURL
     */
    public void setButtonIcon(String iconURL) {
        IconHelper helper = new IconHelper();
        ImageIcon icon = helper.getIcon(iconURL);
        if (icon != null) {
            btnDate.setIcon(icon);
            btnDate.setPreferredSize(new Dimension(icon.getIconWidth() + 2, icon.getIconHeight() + 2));
        }
    }

    /**
     * Titel fuer den DatePicker-Dialog
     *
     * @param datePickerTitle
     */
    public void setDatePickerTitle(String datePickerTitle) {
        this.datePickerTitle = datePickerTitle;
    }

    /**
     * Icon-URL fuer den DatePicker-Dialog
     *
     * @param datePickerIcon
     */
    public void setDatePickerIcon(String datePickerIcon) {
        this.datePickerIcon = datePickerIcon;
    }

    /**
     * Setzt das TextField auf <code>editable</code>
     *
     * @param editable
     */
    public void setTextFieldEditable(boolean editable) {
        tfDate.setEditable(editable);
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        boolean x = (isComponentExecutable()) && enabled;
        tfDate.setEnabled(x);
        btnDate.setEnabled(x);
    }

    /**
     * En-/Disable the date picker button.
     * @param enabled boolean
     */
    public void setDatePickerEnabled(boolean enabled) {
        boolean x = (isComponentExecutable()) && enabled;
        btnDate.setEnabled(x);
    }

    public boolean isUsable() {
        return tfDate.isEditable();
    }

    /**
     * Setzt das Datums-Feld auf editable=usable und den Button auf enabled=usable.
     *
     * @param usable
     */
    public void setUsable(boolean usable) {
        boolean x = (isComponentExecutable()) && usable;
        tfDate.setEditable(x);
        btnDate.setEnabled(x);
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean b) {
        boolean x = (isComponentVisible()) && b;
        tfDate.setVisible(x);
        btnDate.setVisible(x);
    }

    /**
     * @see javax.swing.text.JTextComponent#setEditable(boolean)
     */
    public void setEditable(boolean editable) {
        tfDate.setEditable(editable);
    }

    /**
     * Uebergibt der Komponente den Owner fuer den DatePicker-Dialog.
     *
     * @param parent
     */
    public void setParent4Dialog(Component parent) {
        this.dialogParent = parent;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDate) {
            Date toShow = getDate(new Date());
            DatePicker datePicker = new DatePicker(datePickerTitle, datePickerIcon);
            datePicker.setDefaultDate(toShow);
            Component parent = (dialogParent != null) ? dialogParent : this;
            Object selection = DialogHelper.showDialog(parent, datePicker, true, true);
            if (selection instanceof Date) {
                setDate((Date) selection);
            }
        }
    }

    /**
     * Setzt die TextFarbe des TextFields.
     */
    @Override
    public void setForeground(Color foreground) {
        if (tfDate != null) {
            tfDate.setForeground(foreground);
        }
        else {
            super.setForeground(foreground);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public Color getActiveColor() {
        return tfDate.getActiveColor();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public void setActiveColor(Color activeColor) {
        tfDate.setActiveColor(activeColor);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public Color getInactiveColor() {
        return tfDate.getInactiveColor();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public void setInactiveColor(Color inactiveColor) {
        tfDate.setInactiveColor(inactiveColor);
    }

    public void setSelectionColor(Color c) {
        tfDate.setSelectionColor(c);
    }

    public void setSelectedTextColor(Color c) {
        tfDate.setSelectedTextColor(c);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    public boolean isComponentExecutable() {
        return this.executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        tfDate.setEditable(executable);
        btnDate.setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    public boolean isComponentVisible() {
        return this.visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
    }

    /**
     * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
     */
    @Override
    public synchronized void addMouseListener(MouseListener l) {
        adminML = l;
        super.addMouseListener(adminML);

        // Workaround: dem TextField und Button wird ein anderer
        // MouseListener uebergeben, der die Events mit einer
        // anderen Event-Source an <code>l</code> weiterleitet.
        DateCompMouseListener ml = new DateCompMouseListener(this);
        tfDate.addMouseListener(ml);
        btnDate.addMouseListener(ml);
    }

    /**
     * Fuegt der DateComponent den SearchKey-Listener <code>searchKL</code> hinzu.
     *
     * @param searchKL
     */
    public void addSearchKeyListener(AKSearchKeyListener searchKL) {
        DateCompSearchKeyListener dcKL = new DateCompSearchKeyListener(searchKL);
        tfDate.addKeyListener(dcKL);
    }

    /**
     * Fuegt der DateField einen FocusListener hinzu.
     *
     * @param focusListener
     */
    public void addFocusListener(FocusListener focusListener) {
        tfDate.addFocusListener(focusListener);
    }


    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        this.dateFieldHasFocus = true;
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        try {
            convertToDateFormat();
        }
        finally {
            this.dateFieldHasFocus = false;
        }
    }

    /*
     * Ueberprueft, ob der eingegebene Text einem DateFormat entspricht.
     * Ist dies nicht der Fall, wird versucht, den Text entsprechend zu
     * parsen (wenn Eingabe 8-stellig ohne Punkt).
     */
    private void convertToDateFormat() {
        try {
            String text = tfDate.getText();

            // 8-stellige Eingabe ohne Punkte wird geaendert auf dd.MM.yyyy - sofern
            // das Pattern des TextFields damit ueberein stimmt.
            if (DateTools.PATTERN_DAY_MONTH_YEAR.equals(dateFormat.toPattern()) &&
                    (text != null) && (text.length() == 8) && (!text.contains("."))) {
                StringBuilder dateText = new StringBuilder(text.substring(0, 2));
                dateText.append(".");
                dateText.append(text.substring(2, 4));
                dateText.append(".");
                dateText.append(text.substring(4));
                tfDate.setText(dateText.toString());
            }

            tfDate.commitEdit();
            Date date = tfDate.getValueAsDate(null);
            if ((date != null) && DateTools.isBefore(date, minDate)) {
                // Jahreszahl wird auf 20xx geaendert
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(date);

                int year = cal.get(Calendar.YEAR);
                if (year < 100) {
                    year += 2000;
                    cal.set(Calendar.YEAR, year);
                    tfDate.setValue(new Date(cal.getTimeInMillis()));
                }
                else {
                    String minDateStr = DateFormat.getDateInstance().format(minDate);
                    MessageHelper.showMessageDialog(this, String.format(
                                    "Das Datum ist sehr niedrig (<= %s) - bitte pruefen.", minDateStr),
                            "Datum pruefen", JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        }
        catch (Exception ex) {
            // be silent
            LOGGER.debug("convertToDateFormat() - exception", ex);
        }
    }

    /**
     * SearchKeyListener fuer das TextField der Date-Component. <br> Bevor die Suche ausgeloest wird, wird
     * 'focusLost(..)' der DateComponent aufgerufen. Dadurch wird das eingetragene Datum validiert.
     */
    static class DateCompSearchKeyListener extends KeyAdapter {
        private AKSearchKeyListener searchKL = null;

        /**
         * Default-Const.
         */
        public DateCompSearchKeyListener(AKSearchKeyListener searchKL) {
            super();
            this.searchKL = searchKL;
        }

        /**
         * @see de.augustakom.common.gui.swing.AKSearchKeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(KeyEvent e) {
            searchKL.keyPressed(e);
        }
    }

    /**
     * MouseListener fuer das TextField und den Button, um dem AdminMouseListener die DateComponent und nicht das
     * TextField bzw. den Button als AKManageableComponent zu uebergeben.
     */
    class DateCompMouseListener extends MouseAdapter {
        AKJDateComponent dc = null;

        /**
         * Konstruktor mit Angabe der DateComponent, die an den AdminMouseListener uebergeben werden soll.
         */
        public DateCompMouseListener(AKJDateComponent dc) {
            this.dc = dc;
        }

        /**
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            e.setSource(dc);
            adminML.mousePressed(e);
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            e.setSource(dc);
            adminML.mouseReleased(e);
        }
    }
}
