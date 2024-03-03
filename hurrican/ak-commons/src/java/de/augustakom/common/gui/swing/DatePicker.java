/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.2004 10:56:41
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;

/**
 * The calendar dialog allows you to select a date, interactively. Although it is automatically managed by the custom
 * date button, you can use it directly if you want. Custom dialog box to enter dates. The <code>DateChooser</code>class
 * presents a calendar and allows the user to visually select a day, month and year so that it is impossible to enter an
 * invalid date.<br><br> Verwendung: <br> <code> DatePicker dp = new DatePicker(this); Date selection =
 * dp.selectDate(new Date()); </code>
 */
public class DatePicker extends AKJAbstractOptionDialog
        implements ItemListener, MouseListener, FocusListener, KeyListener, ActionListener {

    private static final Logger LOGGER = Logger.getLogger(DatePicker.class);

    /**
     * Enumeration for the days of a week containing an index according to e.g. {@link Calendar#SUNDAY}. and a heading
     * to display in the corresponding column
     */
    private enum Day {
        SUNDAY(Calendar.SUNDAY, "Son"),
        MONDAY(Calendar.MONDAY, "Mon"),
        TUESDAY(Calendar.TUESDAY, "Die"),
        WEDNESDAY(Calendar.WEDNESDAY, "Mit"),
        THURSDAY(Calendar.THURSDAY, "Don"),
        FRIDAY(Calendar.FRIDAY, "Fre"),
        SATURDAY(Calendar.SATURDAY, "Sam");

        static final Day FIRST_DAY_OF_WEEK = Day.SUNDAY;
        static final int DAYS_OF_WEEK = Day.values().length;

        /**
         * the day of the week of current {@link Day} enum
         */
        final int dayOfWeek;
        /**
         * the heading to display for the day of the week of current {@link Day} enum
         */
        final String heading;

        Day(int dayOfWeek, String display) {
            this.dayOfWeek = dayOfWeek;
            this.heading = display;
        }

        /**
         * @param dayOfWeek the supplied day (Sunday is 1, {@link Calendar#SUNDAY})
         * @return the {@link Day} corresponding to the supplied {@code dayOfWeek}
         * @throws NoSuchElementException if not corresponds {@link Day} found for supplied {@code dayOfWeek}
         */
        static Day indexOfDayOfWeek(int dayOfWeek) {
            for (Day day : Day.values()) {
                if (day.dayOfWeek == dayOfWeek) {
                    return day;
                }
            }
            throw new NoSuchElementException("No " + Day.class + " for dayOfWeek = " + dayOfWeek + " found");
        }

        /**
         * @param dayOfWeek the dayOfWeek to calculate the offset for (Sunday is 1, {@link Calendar#SUNDAY})
         * @return the zero based column index calculated for {@code dayOfWeek} using {@link #FIRST_DAY_OF_WEEK}
         */
        static int column4DayOfWeek(int dayOfWeek) {
            return ((dayOfWeek - FIRST_DAY_OF_WEEK.dayOfWeek) + DAYS_OF_WEEK) % DAYS_OF_WEEK;
        }
    }

    /**
     * Names of the months.
     */
    private static final String[] MONTHS = new String[] { "Januar", "Februar", "März", "April",
            "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" };

    /**
     * Text color of the days of the weeks, used as column headers in the calendar.
     */
    private static final Color WEEK_DAYS_FOREGROUND = Color.black;

    /**
     * Text color of the days' numbers in the calendar.
     */
    private static final Color DAYS_FOREGROUND = Color.blue;

    /**
     * Background color of the selected day in the calendar.
     */
    private static final Color SELECTED_DAY_FOREGROUND = Color.white;

    /**
     * Text color of the selected day in the calendar.
     */
    private static final Color SELECTED_DAY_BACKGROUND = Color.blue;

    /**
     * Empty border, used when the calendar does not have the focus.
     */
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);

    /**
     * Border used to highlight the selected day when the calendar has the focus.
     */
    private static final Border FOCUSED_BORDER = BorderFactory.createLineBorder(Color.yellow, 1);

    /**
     * First year that can be selected.
     */
    private static final int FIRST_YEAR = 1970;

    /**
     * Last year that can be selected.
     */
    private static final int LAST_YEAR = 2200;

    /**
     * Auxiliary variable to compute dates.
     */
    private GregorianCalendar calendar;

    /**
     * Calendar, as a matrix of labels. The first row represents the first week of the month, the second row, the second
     * week, and so on. Each column represents a day of the week, the first is Sunday, and the last is Saturday. The
     * label's text is the number of the corresponding day.
     */
    private JLabel[][] dayLbls;

    /**
     * Day selection control. It is just a panel that can receive the focus. The actual user interaction is driven by
     * the <code>DateChooser</code> class.
     */
    private FocusablePanel daysGrid;

    /**
     * Month selection control.
     */
    private AKJComboBox cbMonth;

    /**
     * Year selection control.
     */
    private AKJComboBox cbYear;

    /**
     * "Ok" button.
     */
    private AKJButton btnOk;

    /**
     * Selected day.
     */
    private JLabel lblSelectedDay;

    /**
     * Day of the week (0=Sunday) corresponding to the first day of the selected month. Used to calculate the position,
     * in the calendar ({@link #Day}), corresponding to a given day.
     */
    private int offset;

    /**
     * Last day of the selected month.
     */
    private int lastDay;

    /**
     * @param title   Titel fuer den Dialog
     * @param iconURL URL fuer das Icon.
     */
    public DatePicker(String title, String iconURL) {
        super(null, true);
        setTitle(title);
        setIconURL(iconURL);
        createGUI();
    }

    /**
     * Uebergibt dem DatePicker das Datum, das vorausgewaehlt sein soll.
     *
     * @param date
     */
    public void setDefaultDate(Date date) {
        calendar.setTime(date);
        int _day = calendar.get(Calendar.DATE);
        int _month = calendar.get(Calendar.MONTH);
        int _year = calendar.get(Calendar.YEAR);
        cbYear.setSelectedIndex(_year - FIRST_YEAR);
        cbMonth.setSelectedIndex(_month - Calendar.JANUARY);
        setSelected(_day);
    }

    @Override
    protected final void createGUI() {
        calendar = new GregorianCalendar();
        cbMonth = new AKJComboBox(MONTHS);
        cbMonth.addItemListener(this);

        cbYear = new AKJComboBox();
        for (int i = FIRST_YEAR; i <= LAST_YEAR; i++) {
            cbYear.addItem(Integer.toString(i));
        }
        cbYear.addItemListener(this);

        dayLbls = new JLabel[7][Day.DAYS_OF_WEEK];
        for (int dayOfWeek = 1; dayOfWeek <= Day.DAYS_OF_WEEK; dayOfWeek++) {
            int col = Day.column4DayOfWeek(dayOfWeek);
            dayLbls[0][col] = new JLabel(Day.indexOfDayOfWeek(dayOfWeek).heading, JLabel.RIGHT);
            dayLbls[0][col].setForeground(WEEK_DAYS_FOREGROUND);
        }

        for (int i = 1; i < 7; i++) {
            for (int j = 0; j < Day.DAYS_OF_WEEK; j++) {
                dayLbls[i][j] = new JLabel(" ", JLabel.RIGHT);
                dayLbls[i][j].setForeground(DAYS_FOREGROUND);
                dayLbls[i][j].setBackground(SELECTED_DAY_BACKGROUND);
                dayLbls[i][j].setBorder(EMPTY_BORDER);
                dayLbls[i][j].addMouseListener(this);
            }
        }

        btnOk = new AKJButton("Ok");
        btnOk.setToolTipText("Übernimmt das ausgewählte Datum");
        btnOk.addActionListener(this);
        AKJButton btnCancel = new AKJButton("Abbrechen");
        btnCancel.setToolTipText("Bricht die Datumsauswahl ab");
        btnCancel.addActionListener(this);

        AKJPanel monthYearPanel = new AKJPanel(new GridBagLayout());
        monthYearPanel.add(cbMonth, GBCFactory.createGBC(80, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        monthYearPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        monthYearPanel.add(cbYear, GBCFactory.createGBC(20, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        daysGrid = new FocusablePanel(new GridLayout(7, Day.DAYS_OF_WEEK, 5, 0));
        daysGrid.addFocusListener(this);
        daysGrid.addKeyListener(this);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < Day.DAYS_OF_WEEK; j++) {
                daysGrid.add(dayLbls[i][j]);
            }
        }
        daysGrid.setBackground(Color.white);
        daysGrid.setBorder(BorderFactory.createLoweredBevelBorder());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnOk, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(monthYearPanel, BorderLayout.NORTH);
        getChildPanel().add(daysGrid, BorderLayout.CENTER);
        getChildPanel().add(btnPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(200, 180));
    }

    @Override
    @SuppressWarnings("squid:S1186")
    protected void execute(String command) {
    }

    @SuppressWarnings("squid:S1186")
    public void update(Observable o, Object arg) {
    }

    /**
     * Gets the selected day, as an <code>int</code>. Parses the text of the selected label in the calendar to get the
     * day.
     *
     * @return the selected day or -1 if there is no day selected *
     */
    private int getSelectedDay() {
        if (lblSelectedDay == null) {
            return -1;
        }
        try {
            return Integer.parseInt(lblSelectedDay.getText());
        }
        catch (NumberFormatException e) {
            LOGGER.debug("getSelectedDay() - nfe " + e.getMessage());
        }
        return -1;
    }

    /**
     * Sets the selected day. The day is specified as the label control, in the calendar, corresponding to the day to
     * select.
     *
     * @param newDay day to select
     */
    private void setSelected(JLabel newDay) {
        if (lblSelectedDay != null) {
            lblSelectedDay.setForeground(DAYS_FOREGROUND);
            lblSelectedDay.setOpaque(false);
            lblSelectedDay.setBorder(EMPTY_BORDER);
        }
        lblSelectedDay = newDay;
        lblSelectedDay.setForeground(SELECTED_DAY_FOREGROUND);
        lblSelectedDay.setOpaque(true);
        if (daysGrid.hasFocus()) {
            lblSelectedDay.setBorder(FOCUSED_BORDER);
        }
    }

    /**
     * Sets the selected day. The day is specified as the number of the day, in the month, to selected. The function
     * compute the corresponding control to select.
     *
     * @param newDay day to select
     */
    private void setSelected(int newDay) {
        setSelected(dayLbls[(((newDay + offset) - 1) / Day.DAYS_OF_WEEK) + 1][((newDay + offset) - 1) % Day.DAYS_OF_WEEK]);
    }

    /**
     * Updates the calendar. This function updates the calendar panel to reflect the month and year selected. It keeps
     * the same day of the month that was selected, except if it is beyond the last day of the month. In this case, the
     * last day of the month is selected.
     */
    private void update() {
        int iday = getSelectedDay();
        for (int i = 0; i < Day.DAYS_OF_WEEK; i++) {
            dayLbls[1][i].setText(" ");
            dayLbls[5][i].setText(" ");
            dayLbls[6][i].setText(" ");
        }
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, cbMonth.getSelectedIndex() + Calendar.JANUARY);
        calendar.set(Calendar.YEAR, cbYear.getSelectedIndex() + FIRST_YEAR);
        offset = Day.column4DayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        lastDay = calendar.getActualMaximum(Calendar.DATE);
        for (int i = 0; i < lastDay; i++) {
            dayLbls[((i + offset) / Day.DAYS_OF_WEEK) + 1][(i + offset) % Day.DAYS_OF_WEEK].setText(String.valueOf(i + 1));
        }
        if (iday != -1) {
            if (iday > lastDay) {
                iday = lastDay;
            }
            setSelected(iday);
        }
    }

    /**
     * Called when the "Ok" button is pressed. Just sets a flag and hides the dialog box.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOk) {
            prepare4Close();
            calendar.set(Calendar.DATE, getSelectedDay());
            calendar.set(Calendar.MONTH, cbMonth.getSelectedIndex() + Calendar.JANUARY);
            calendar.set(Calendar.YEAR, cbYear.getSelectedIndex() + FIRST_YEAR);
            setValue(calendar.getTime());
        }
        else {
            setValue(CANCEL_OPTION);
        }
    }

    /**
     * Called when the calendar gains the focus. Just re-sets the selected day so that it is redrawn with the border
     * that indicate focus.
     */
    public void focusGained(FocusEvent e) {
        setSelected(lblSelectedDay);
    }

    /**
     * Called when the calendar loses the focus. Just re-sets the selected day so that it is redrawn without the border
     * that indicate focus.
     */
    public void focusLost(FocusEvent e) {
        setSelected(lblSelectedDay);
    }

    /**
     * Called when a new month or year is selected. Updates the calendar to reflect the selection.
     */
    public void itemStateChanged(ItemEvent e) {
        update();
    }

    /**
     * Called when a key is pressed and the calendar has the focus. Handles the arrow keys so that the user can select a
     * day using the keyboard.
     */
    public void keyPressed(KeyEvent e) {
        int iday = getSelectedDay();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (iday > 1) {
                    setSelected(iday - 1);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (iday < lastDay) {
                    setSelected(iday + 1);
                }
                break;
            case KeyEvent.VK_UP:
                if (iday > Day.DAYS_OF_WEEK) {
                    setSelected(iday - Day.DAYS_OF_WEEK);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (iday <= (lastDay - Day.DAYS_OF_WEEK)) {
                    setSelected(iday + Day.DAYS_OF_WEEK);
                }
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("squid:S1186")
    public void keyReleased(KeyEvent e) {
    }

    @SuppressWarnings("squid:S1186")
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Called when the mouse is clicked on a day in the calendar. Selects the clicked day.
     */
    public void mouseClicked(MouseEvent e) {
        JLabel day = (JLabel) e.getSource();
        if (!day.getText().equals(" ")) {
            setSelected(day);
        }
        daysGrid.requestFocus();

        if (e.getClickCount() == 2) {
            ActionEvent ae = new ActionEvent(btnOk, 0, "ok");
            actionPerformed(ae);
        }
    }

    @SuppressWarnings("squid:S1186")
    public void mouseEntered(MouseEvent e) {
    }

    @SuppressWarnings("squid:S1186")
    public void mouseExited(MouseEvent e) {
    }

    @SuppressWarnings("squid:S1186")
    public void mousePressed(MouseEvent e) {
    }

    @SuppressWarnings("squid:S1186")
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Custom panel that can receive the focus. Used to implement the calendar control.
     */
    private static class FocusablePanel extends JPanel {

        /**
         * Constructs a new <code>FocusablePanel</code> with the given layout manager.
         *
         * @param layout layout manager
         */
        public FocusablePanel(LayoutManager layout) {
            super(layout);
        }

        /**
         * Always returns <code>true</code>, since <code>FocusablePanel</code> can receive the focus.
         *
         * @return <code>true</code>
         */
        @Override
        public boolean isFocusable() {
            return true;
        }
    }
}
